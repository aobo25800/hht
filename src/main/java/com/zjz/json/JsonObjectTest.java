package com.zjz.json;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * @author com.zjz
 * @date 2021/8/27 13:56
 */
public class JsonObjectTest {

    public static void main(String[] args) {
        // XML 转json
//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<Message>\n" +
//                "<Head>\n" +
//                "<ReqJnlNo>202108271000000863</ReqJnlNo>\n" +
//                "<ResJnlNo>0092024052600001053</ResJnlNo>\n" +
//                "<ReqDate>20210828</ReqDate>\n" +
//                "<ResponseType>S</ResponseType>\n" +
//                "<ResTime>20210828012151</ResTime>\n" +
//                "<ResCode>000000</ResCode>\n" +
//                "<ResMsg>交易成功</ResMsg>\n" +
//                "</Head>\n" +
//                "<Body>\n" +
//                "<stat>S</stat>\n" +
//                "</Body>\n" +
//                "</Message>";
//
//        JSONObject jsonObject = XML.toJSONObject(xml);
//        System.out.println(jsonObject);
//        JSONObject head = jsonObject.getJSONObject("Message");
//        System.out.println(head);
//        System.out.println(head.containsKey("Body"));

        String json = "{\"msg\":\"成功\",\"code\":1000}";

        JSONObject jsonObject = JSONUtil.parseObj(json);
        String msg = (String) jsonObject.get("msg");
        System.out.println(msg);
    }
}
