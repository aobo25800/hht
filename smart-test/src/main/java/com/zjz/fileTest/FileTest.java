package com.zjz.fileTest;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.mysql.cj.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author com.com.zjz
 * @date 2021/9/2 11:40
 */
public class FileTest {

    private final static ArrayList<Range<Integer>> RANGES;

    static {
        Range<Integer> range2 = Range.closedOpen(1000, 3000);
        Range<Integer> range3 = Range.closedOpen(3000, 5000);
        Range<Integer> range4 = Range.closedOpen(5000, 9000);
        RANGES = Lists.newArrayList(range2, range3, range4);
        System.out.println(RANGES);
    }

    private final ArrayList<Integer> NAME;

    {
        NAME = new ArrayList<>();
        NAME.add(100);
        NAME.add(200);
        NAME.add(300);
        System.out.println(NAME);
    }
    
    {
        System.out.println("com/zjz");
    }




    public static void main(String[] args) throws IOException {
        new FileTest();
    }

    /**
     * 指定文件路径读取文件内容
     */
    public void ioWriterTest() throws IOException {
        File file = new File("/data/com.zjz/abc.json");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.write("{123:}" + ",\r\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ioReaderTest() {

        StringBuffer sb = new StringBuffer();
        File file = new File("/data/com.zjz/abc.json");
        if (!file.exists()) {
            System.out.println("文件不存在");
        }
        try (FileReader fileReader = new FileReader(file)) {
            int i;
            while ((i = fileReader.read()) != -1) {
                sb.append((char) i);
            }
            System.out.println(sb);
            if (StringUtils.isNullOrEmpty(sb.toString())) {
                return;
            }
            String json = "[" + sb + "]";
            List<XYDMailLogVO> xydMailLogVOS = JSONUtil.toBean(json, new TypeReference<>() {
            }, false);
            System.out.println(xydMailLogVOS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
