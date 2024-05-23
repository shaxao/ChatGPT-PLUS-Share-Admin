package com.louwei.gptresource;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.louwei.gptresource.utils.RequestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;

@SpringBootTest
class GptResourceApplicationTests {
//    @Autowired
//    private AppConfigService appConfigService;
//    @Autowired
//    private RequestUtils requestUtils;
//
//    @Test
//    void contextLoads() throws IOException {
//        //gpt-4-1106-preview   gpt-3.5-turbo-1106
//        //"\"response_format\": {\"type\": \"json_object\"}, "
//        String jsonPar = "{\"model\": \"gpt-3.5-turbo-1106\", " +
//                "\"response_format\": {\"type\": \"json_object\"}, " +
//                "\"messages\": [" +
//                "{\"role\": \"system\", \"content\": \"You are a helpful assistant designed to output JSON.\"}," +
//                "{\"role\": \"user\", \"content\": \"树上 9 只鸟，打掉 1 只，还剩几只？\"}" +
//                "]}";
//        String user = "树上 9 只鸟，打掉 1 只，还剩几只？";
//
//        String body = requestUtils.textRequest("树上 9 只鸟，打掉 1 只，还剩几只？");
//        //{
//        //  "id": "chatcmpl-8MuHMhaMEYGT0xZNzoNBc3crWPIKy",
//        //  "object": "chat.completion",
//        //  "created": 1700470244,
//        //  "model": "gpt-3.5-turbo-1106",
//        //  "choices": [
//        //    {
//        //      "index": 0,
//        //      "message": {
//        //        "role": "assistant",
//        //        "content": "{\"game\":\"英雄联盟\",\"season\":\"S9\",\"champion\":\"冠军\"}"
//        //      },
//        //      "finish_reason": "stop"
//        //    }
//        //  ],
//        //  "usage": {
//        //    "prompt_tokens": 47,
//        //    "completion_tokens": 23,
//        //    "total_tokens": 70
//        //  },
//        //  "system_fingerprint": "fp_eeff13170a"
//        //}
//        System.out.println(body);
//        JSONObject jsonObject = JSONUtil.parseObj(body);
//        //获取message
//        JSONObject firstChoice = jsonObject.getJSONArray("choices").getJSONObject(0);
//        String content = firstChoice.getJSONObject("message").getStr("content");
//        System.out.println("content Value:" + content);
//        }


}
