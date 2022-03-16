#include <stdio.h>

// Define global system variables
typedef struct {
  short abort;                 // System abort flag. Forces exit back to main loop for reset.
  short state;                 // Tracks the current state of Grbl.
  short suspend;               // System suspend bitflag variable that manages holds, cancels, and safety door.
  short soft_limit;            // Tracks soft limit errors for the state machine. (boolean)

  int position[3];      // Real-time machine (aka home) position vector in steps.
                                 // NOTE: This may need to be a volatile variable, if problems arise.

  int probe_position[3]; // Last probe position in machine coordinates and steps.
  short probe_succeeded;        // Tracks if last probing cycle was successful.
  short homing_axis_lock;       // Locks axes when limits engage. Used as an axis motion mask in the stepper ISR.
} system_t;
extern system_t sys;


typedef struct {
  // Axis settings
  float steps_per_mm[3];
  float max_rate[3];
  float acceleration[3];
  float max_travel[3];

  // Remaining Grbl settings
  short pulse_microseconds;
  short step_invert_mask;
  short dir_invert_mask;
  short stepper_idle_lock_time; // If max value 255, steppers do not disable.
  short status_report_mask; // Mask to indicate desired report data.
  float junction_deviation;
  float arc_tolerance;

  short flags;  // Contains default boolean settings

  short homing_dir_mask;
  float homing_feed_rate;
  float homing_seek_rate;
  short homing_debounce_delay;
  float homing_pulloff;
} settings_t;
extern settings_t settings;

typedef struct {
  // Fields used by the bresenham algorithm for tracing the line
  // NOTE: Used by stepper algorithm to execute the block correctly. Do not alter these values.
  short direction_bits;    // The direction bit set for this block (refers to *_DIRECTION_BIT in config.h)
  int steps[3];    // Step count along each axis
  int step_event_count; // The maximum step axis count and number of steps required to complete this block.

  // Fields used by the motion planner to manage acceleration
  float entry_speed_sqr;         // The current planned entry speed at block junction in (mm/min)^2
  float max_entry_speed_sqr;     // Maximum allowable entry speed based on the minimum of junction limit and
                                 //   neighboring nominal speeds with overrides in (mm/min)^2
  float max_junction_speed_sqr;  // Junction entry speed limit based on direction vectors in (mm/min)^2
  float nominal_speed_sqr;       // Axis-limit adjusted nominal speed for this block in (mm/min)^2
  float acceleration;            // Axis-limit adjusted line acceleration in (mm/min^2)
  float millimeters;             // The remaining distance for this block to be executed in (mm)
  // uint8_t max_override;       // Maximum override value based on axis speed limits

  #ifdef USE_LINE_NUMBERS
    int32_t line_number;
  #endif
} plan_block_t;


#define MINIMUM_FEED_RATE 1.0 // (mm/min)
#define MINIMUM_JUNCTION_SPEED 0.0
#define SOME_LARGE_VALUE 1.0E+38
static short BLOCK_BUFFER_SIZE = 18;
static plan_block_t block_buffer[18];  // A ring buffer for motion instructions
static short block_buffer_tail;     // Index of the block to process now
static short block_buffer_head;     // Index of the next block to be pushed
static short next_buffer_head;      // Index of the next buffer head
static short block_buffer_planned;  // Index of the optimally planned block


// Define planner variables
typedef struct {
  int position[3];          // The planner position of the tool in absolute steps. Kept separate
                                     // from g-code position for movements requiring multiple line motions,
                                     // i.e. arcs, canned cycles, and backlash compensation.
  float previous_unit_vec[3];   // Unit vector of previous path line segment
  float previous_nominal_speed_sqr;  // Nominal speed of previous path line segment
} planner_t;
static planner_t pl;


// Returns the index of the next block in the ring buffer. Also called by stepper segment buffer.
short plan_next_block_index(short block_index)
{
  block_index++;
  if (block_index == BLOCK_BUFFER_SIZE) { block_index = 0; }
  return(block_index);
}


// Returns the index of the previous block in the ring buffer
static short plan_prev_block_index(short block_index)
{
  if (block_index == 0) { block_index = BLOCK_BUFFER_SIZE; }
  block_index--;
  return(block_index);
}

static void planner_recalculate(void)
{
  // Initialize block index to the last block in the planner buffer.
  short block_index = plan_prev_block_index(block_buffer_head);

  // Bail. Can't do anything with one only one plan-able block.

  float entry_speed_sqr;
  plan_block_t *next;
  plan_block_t *current = &block_buffer[block_index];

  if (block_index == block_buffer_planned) { return; }

  // Reverse Pass: Coarsely maximize all possible deceleration curves back-planning from the last
  // block in buffer. Cease planning when the last optimal planned or tail pointer is reached.
  // NOTE: Forward pass will later refine and correct the reverse pass to create an optimal plan.


  // Calculate maximum entry speed for last block in buffer, where the exit speed is always zero.
  current->entry_speed_sqr = min( current->max_entry_speed_sqr, 2*current->acceleration*current->millimeters);

  block_index = plan_prev_block_index(block_index);
  if (block_index == block_buffer_planned) { // Only two plannable blocks in buffer. Reverse pass complete.
    // Check if the first block is the tail. If so, notify stepper to update its current parameters.
    if (block_index == block_buffer_tail) { st_update_plan_block_parameters(); }
  } else { // Three or more plan-able blocks
    while (block_index != block_buffer_planned) {
      next = current;
      current = &block_buffer[block_index];
      block_index = plan_prev_block_index(block_index);

      // Check if next block is the tail block(=planned block). If so, update current stepper parameters.
      if (block_index == block_buffer_tail) { st_update_plan_block_parameters(); }

      // Compute maximum entry speed decelerating over the current block from its exit speed.
      if (current->entry_speed_sqr != current->max_entry_speed_sqr) {
        entry_speed_sqr = next->entry_speed_sqr + 2*current->acceleration*current->millimeters;
        if (entry_speed_sqr < current->max_entry_speed_sqr) {
          current->entry_speed_sqr = entry_speed_sqr;
        } else {
          current->entry_speed_sqr = current->max_entry_speed_sqr;
        }
      }
    }
  }

  // Forward Pass: Forward plan the acceleration curve from the planned pointer onward.
  // Also scans for optimal plan breakpoints and appropriately updates the planned pointer.
  next = &block_buffer[block_buffer_planned]; // Begin at buffer planned pointer
  block_index = plan_next_block_index(block_buffer_planned);
  while (block_index != block_buffer_head) {
    current = next;
    next = &block_buffer[block_index];

    // Any acceleration detected in the forward pass automatically moves the optimal planned
    // pointer forward, since everything before this is all optimal. In other words, nothing
    // can improve the plan from the buffer tail to the planned pointer by logic.
    if (current->entry_speed_sqr < next->entry_speed_sqr) {
      entry_speed_sqr = current->entry_speed_sqr + 2*current->acceleration*current->millimeters;
      // If true, current block is full-acceleration and we can move the planned pointer forward.
      if (entry_speed_sqr < next->entry_speed_sqr) {
        next->entry_speed_sqr = entry_speed_sqr; // Always <= max_entry_speed_sqr. Backward pass sets this.
        block_buffer_planned = block_index; // Set optimal plan pointer.
      }
    }

    // Any block set at its maximum entry speed also creates an optimal plan up to this
    // point in the buffer. When the plan is bracketed by either the beginning of the
    // buffer and a maximum entry speed or two maximum entry speeds, every block in between
    // cannot logically be further improved. Hence, we don't have to recompute them anymore.
    if (next->entry_speed_sqr == next->max_entry_speed_sqr) { block_buffer_planned = block_index; }
    block_index = plan_next_block_index( block_index );
  }
}


void plan_reset(void)
{
  memset(&pl, 0, sizeof(planner_t)); // Clear planner struct
  block_buffer_tail = 0;
  block_buffer_head = 0; // Empty = tail
  next_buffer_head = 1; // plan_next_block_index(block_buffer_head)
  block_buffer_planned = 0; // = block_buffer_tail;
}


void plan_discard_current_block(void)
{
  if (block_buffer_head != block_buffer_tail) { // Discard non-empty buffer.
    short block_index = plan_next_block_index( block_buffer_tail );
    // Push block_buffer_planned pointer, if encountered.
    if (block_buffer_tail == block_buffer_planned) { block_buffer_planned = block_index; }
    block_buffer_tail = block_index;
  }
}


plan_block_t *plan_get_current_block(void)
{
  if (block_buffer_head == block_buffer_tail) { return(NULL); } // Buffer empty
  return(&block_buffer[block_buffer_tail]);
}


float plan_get_exec_block_exit_speed(void)
{
  short block_index = plan_next_block_index(block_buffer_tail);
  if (block_index == block_buffer_head) { return( 0.0 ); }
  return( sqrt( block_buffer[block_index].entry_speed_sqr ) );
}


// Returns the availability status of the block ring buffer. True, if full.
short plan_check_full_buffer(void)
{
  if (block_buffer_tail == next_buffer_head) { return(1); }
  return(0);
}


/* Add a new linear movement to the buffer. target[N_AXIS] is the signed, absolute target position
   in millimeters. Feed rate specifies the speed of the motion. If feed rate is inverted, the feed
   rate is taken to mean "frequency" and would complete the operation in 1/feed_rate minutes.
   All position data passed to the planner must be in terms of machine position to keep the planner
   independent of any coordinate system changes and offsets, which are handled by the g-code parser.
   NOTE: Assumes buffer is available. Buffer checks are handled at a higher level by motion_control.
   In other words, the buffer head is never equal to the buffer tail.  Also the feed rate input value
   is used in three ways: as a normal feed rate if invert_feed_rate is false, as inverse time if
   invert_feed_rate is true, or as seek/rapids rate if the feed_rate value is negative (and
   invert_feed_rate always false). */
#ifdef USE_LINE_NUMBERS
  void plan_buffer_line(float *target, float feed_rate, uint8_t invert_feed_rate, int32_t line_number)
#else
  void plan_buffer_line(float *target, float feed_rate, short invert_feed_rate)
#endif
{

  int target_steps[3];
  float unit_vec[3], delta_mm;
  short idx;
  float inverse_unit_vec_value;
  float inverse_millimeters;  // Inverse millimeters to remove multiple float divides
  float junction_cos_theta;
  float sin_theta_d2;

  // Prepare and initialize new block
  plan_block_t *block = &block_buffer[block_buffer_head];
  block->step_event_count = 0;
  block->millimeters = 0;
  block->direction_bits = 0;
  block->acceleration = 1.0E+38; // Scaled down to maximum acceleration later
  #ifdef USE_LINE_NUMBERS
    block->line_number = line_number;
  #endif

  // Compute and store initial move distance data.
  // TODO: After this for-loop, we don't touch the stepper algorithm data. Might be a good idea
  // to try to keep these types of things completely separate from the planner for portability.

  #ifdef COREXY
    target_steps[A_MOTOR] = (target[A_MOTOR]*settings.steps_per_mm[A_MOTOR])>0?(int32_t)(target[A_MOTOR]*settings.steps_per_mm[A_MOTOR]+0.5):(int32_t)(target[A_MOTOR]*settings.steps_per_mm[A_MOTOR]-0.5);
    target_steps[B_MOTOR] = (target[B_MOTOR]*settings.steps_per_mm[B_MOTOR])>0?(int32_t)(target[B_MOTOR]*settings.steps_per_mm[B_MOTOR]+0.5):(int32_t)(target[B_MOTOR]*settings.steps_per_mm[B_MOTOR]-0.5);
    block->steps[A_MOTOR] = labs((target_steps[X_AXIS]-pl.position[X_AXIS]) + (target_steps[Y_AXIS]-pl.position[Y_AXIS]));
    block->steps[B_MOTOR] = labs((target_steps[X_AXIS]-pl.position[X_AXIS]) - (target_steps[Y_AXIS]-pl.position[Y_AXIS]));
  #endif

  for (idx=0; idx<3; idx++) {
    // Calculate target position in absolute steps, number of steps for each axis, and determine max step events.
    // Also, compute individual axes distance for move and prep unit vector calculations.
    // NOTE: Computes true distance from converted step values.
    #ifdef COREXY
      if ( !(idx == A_MOTOR) && !(idx == B_MOTOR) ) {
        target_steps[idx] = (target[idx]*settings.steps_per_mm[idx])>0?(int32_t)(target[idx]*settings.steps_per_mm[idx]+0.5):(int32_t)(target[idx]*settings.steps_per_mm[idx]-0.5);
        block->steps[idx] = labs(target_steps[idx]-pl.position[idx]);
      }
      block->step_event_count = max(block->step_event_count, block->steps[idx]);
      if (idx == A_MOTOR) {
        delta_mm = ((target_steps[X_AXIS]-pl.position[X_AXIS]) + (target_steps[Y_AXIS]-pl.position[Y_AXIS]))/settings.steps_per_mm[idx];
      } else if (idx == B_MOTOR) {
        delta_mm = ((target_steps[X_AXIS]-pl.position[X_AXIS]) - (target_steps[Y_AXIS]-pl.position[Y_AXIS]))/settings.steps_per_mm[idx];
      } else {
        delta_mm = (target_steps[idx] - pl.position[idx])/settings.steps_per_mm[idx];
      }
    #else
      target_steps[idx] = (target[idx]*settings.steps_per_mm[idx])>0?(int)(target[idx]*settings.steps_per_mm[idx]+0.5):(int)(target[idx]*settings.steps_per_mm[idx]-0.5);
      block->steps[idx] = labs(target_steps[idx]-pl.position[idx]);
      block->step_event_count = max(block->step_event_count, block->steps[idx]);
      delta_mm = (target_steps[idx] - pl.position[idx])/settings.steps_per_mm[idx];
    #endif
    unit_vec[idx] = delta_mm; // Store unit vector numerator. Denominator computed later.

    // Set direction bits. Bit enabled always means direction is negative.
    if (delta_mm < 0 ) { block->direction_bits |= get_direction_pin_mask(idx); }

    // Incrementally compute total move distance by Euclidean norm. First add square of each term.
    block->millimeters += delta_mm*delta_mm;
  }
  block->millimeters = sqrt(block->millimeters); // Complete millimeters calculation with sqrt()

  // Bail if this is a zero-length block. Highly unlikely to occur.
  if (block->step_event_count == 0) { return; }

  // Adjust feed_rate value to mm/min depending on type of rate input (normal, inverse time, or rapids)
  // TODO: Need to distinguish a rapids vs feed move for overrides. Some flag of some sort.
  if (feed_rate < 0) { feed_rate = 1.0E+38; } // Scaled down to absolute max/rapids rate later
  else if (invert_feed_rate) { feed_rate *= block->millimeters; }
  if (feed_rate < MINIMUM_FEED_RATE) { feed_rate = MINIMUM_FEED_RATE; } // Prevents step generation round-off condition.

  // Calculate the unit vector of the line move and the block maximum feed rate and acceleration scaled
  // down such that no individual axes maximum values are exceeded with respect to the line direction.
  // NOTE: This calculation assumes all axes are orthogonal (Cartesian) and works with ABC-axes,
  // if they are also orthogonal/independent. Operates on the absolute value of the unit vector.
  inverse_millimeters = 1.0/block->millimeters;  // Inverse millimeters to remove multiple float divides
  junction_cos_theta = 0;
  for (idx=0; idx<3; idx++) {
    if (unit_vec[idx] != 0) {  // Avoid divide by zero.
      unit_vec[idx] *= inverse_millimeters;  // Complete unit vector calculation
      inverse_unit_vec_value = fabs(1.0/unit_vec[idx]); // Inverse to remove multiple float divides.

      // Check and limit feed rate against max individual axis velocities and accelerations
      feed_rate = min(feed_rate,settings.max_rate[idx]*inverse_unit_vec_value);
      block->acceleration = min(block->acceleration,settings.acceleration[idx]*inverse_unit_vec_value);

      // Incrementally compute cosine of angle between previous and current path. Cos(theta) of the junction
      // between the current move and the previous move is simply the dot product of the two unit vectors,
      // where prev_unit_vec is negative. Used later to compute maximum junction speed.
      junction_cos_theta -= pl.previous_unit_vec[idx] * unit_vec[idx];
    }
  }

  // TODO: Need to check this method handling zero junction speeds when starting from rest.
  if (block_buffer_head == block_buffer_tail) {

    // Initialize block entry speed as zero. Assume it will be starting from rest. Planner will correct this later.
    block->entry_speed_sqr = 0.0;
    block->max_junction_speed_sqr = 0.0; // Starting from rest. Enforce start from zero velocity.

  } else {

    // NOTE: Computed without any expensive trig, sin() or acos(), by trig half angle identity of cos(theta).
    if (junction_cos_theta > 0.999999) {
      //  For a 0 degree acute junction, just set minimum junction speed.
      block->max_junction_speed_sqr = MINIMUM_JUNCTION_SPEED*MINIMUM_JUNCTION_SPEED;
    } else {
      junction_cos_theta = max(junction_cos_theta,-0.999999); // Check for numerical round-off to avoid divide by zero.
      sin_theta_d2 = sqrt(0.5*(1.0-junction_cos_theta)); // Trig half angle identity. Always positive.

      // TODO: Technically, the acceleration used in calculation needs to be limited by the minimum of the
      // two junctions. However, this shouldn't be a significant problem except in extreme circumstances.
      block->max_junction_speed_sqr = max( MINIMUM_JUNCTION_SPEED*MINIMUM_JUNCTION_SPEED,
                                   (block->acceleration * settings.junction_deviation * sin_theta_d2)/(1.0-sin_theta_d2) );

    }
  }

  // Store block nominal speed
  block->nominal_speed_sqr = feed_rate*feed_rate; // (mm/min). Always > 0

  // Compute the junction maximum entry based on the minimum of the junction speed and neighboring nominal speeds.
  block->max_entry_speed_sqr = min(block->max_junction_speed_sqr,
                                   min(block->nominal_speed_sqr,pl.previous_nominal_speed_sqr));

  // Update previous path unit_vector and nominal speed (squared)
  memcpy(pl.previous_unit_vec, unit_vec, sizeof(unit_vec)); // pl.previous_unit_vec[] = unit_vec[]
  pl.previous_nominal_speed_sqr = block->nominal_speed_sqr;

  // Update planner position
  memcpy(pl.position, target_steps, sizeof(target_steps)); // pl.position[] = target_steps[]

  // New block is all set. Update buffer head and next buffer head indices.
  block_buffer_head = next_buffer_head;
  next_buffer_head = plan_next_block_index(block_buffer_head);

  // Finish up by recalculating the plan with the new block.
  planner_recalculate();
}


// Reset the planner position vectors. Called by the system abort/initialization routine.
void plan_sync_position()
{
  // TODO: For motor configurations not in the same coordinate frame as the machine position,
  // this function needs to be updated to accomodate the difference.
  short idx;
  for (idx=0; idx<3; idx++) {
    #ifdef COREXY
     if (idx==X_AXIS) {
        pl.position[X_AXIS] = system_convert_corexy_to_x_axis_steps(sys.position);
      } else if (idx==Y_AXIS) {
        pl.position[Y_AXIS] = system_convert_corexy_to_y_axis_steps(sys.position);
      } else {
        pl.position[idx] = sys.position[idx];
      }
    #else
      pl.position[idx] = sys.position[idx];
    #endif
  }
}


// Returns the number of active blocks are in the planner buffer.
short plan_get_block_buffer_count(void)
{
  if (block_buffer_head >= block_buffer_tail) { return(block_buffer_head-block_buffer_tail); }
  return(BLOCK_BUFFER_SIZE - (block_buffer_tail-block_buffer_head));
}


// Re-initialize buffer plan with a partially completed block, assumed to exist at the buffer tail.
// Called after a steppers have come to a complete stop for a feed hold and the cycle is stopped.
void plan_cycle_reinitialize(void)
{
  // Re-plan from a complete stop. Reset planner entry speeds and buffer planned pointer.
  st_update_plan_block_parameters();
  block_buffer_planned = block_buffer_tail;
  planner_recalculate();
}

void main() {
    printf("hello word!!!");
}