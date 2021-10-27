package com.zjz.task;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * @author com.zjz
 * @date 2021/10/9 9:50
 */
@Component
public class XxlJobTask {

    private Logger logger = LoggerFactory.getLogger(XxlJobTask.class);

    @XxlJob("zjzTest")
    public ReturnT zjzTest(String name) {
        logger.info("task is run!");
        System.out.println("执行了");
        System.out.println("name is: " + name);
        return ReturnT.SUCCESS;
    }
}
