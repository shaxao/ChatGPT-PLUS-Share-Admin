package com.louwei.gptresource.utils;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.louwei.gptresource.domain.ShareTokenValue;
import com.louwei.gptresource.service.AppConfigService;
import com.plexpt.chatgpt.util.Proxys;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 请求工具类
 */
@Component
public class RequestUtils {
    @Autowired
    private AppConfigService appConfigService;


    public String textRequest(String user) throws IOException {
        //"\"response_format\": {\"type\": \"json_object\"}, " +   启用json模式  You are a helpful assistant designed to output JSON
        String json = "{\"model\": \"gpt-3.5-turbo-1106\", " +
                "\"messages\": [" +
                "{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}," +
                "{\"role\": \"user\", \"content\": \"" + user + "\"}" +
                "]}";
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + appConfigService.getApiKey());
        //本地代理
        //Proxy proxy = Proxys.http("127.0.0.1", 33210);
        HttpResponse response = HttpRequest.post(appConfigService.getUrl() + appConfigService.getChatTalk())
                .headerMap(headers, false)
                .body(json)
                //.setProxy(proxy)
                .execute();
        return response.body();
    }

    /**
     * 生成shareToken请求工具方法
     * @param shareTokenValue
     * @return
     */
    public String refreshShareToken(ShareTokenValue shareTokenValue) {
        // 设置请求头
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(shareTokenValue.getUrl());
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        // 设置请求参数
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("unique_name", shareTokenValue.getUniqueName()));
        params.add(new BasicNameValuePair("access_token", shareTokenValue.getAccessToken()));
        params.add(new BasicNameValuePair("site_limit", shareTokenValue.getSiteLimit()));
        params.add(new BasicNameValuePair("expires_in", shareTokenValue.getExpireTime()));
        params.add(new BasicNameValuePair("show_conversations", shareTokenValue.getShowConversations()));
        params.add(new BasicNameValuePair("show_userinfo", shareTokenValue.getShowUserInfo()));
        String shareToken = "";
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            // 发送请求并获取响应
            org.apache.http.HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            // 读取响应内容
            if (httpEntity != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()))) {
                    StringBuilder responseText = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        responseText.append(line);
                    }

                    // 输出响应内容  {"expire_at":1705741487,"show_conversations":false,"show_userinfo":false,"site_limit":"https://chat.ttzi.com","token_key":"fk-ioAdsx-ms_gI6ZIPW7svbcu0Vf_XTPqKwgdiwaHmlqI","unique_name":"mhchat"}
                    JSONObject jsonObject = new JSONObject(responseText);
                    shareToken = (String) jsonObject.get("token_key");
                    System.out.println("ShareToken:" + shareToken);
                    return shareToken;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return shareToken;
    }

    /**
     * 生成或者刷新token
     * @return
     */
    public StringBuilder getToken(String url,String username,String password){
        // 设置请求头
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        // String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik1UaEVOVUpHTkVNMVFURTRNMEZCTWpkQ05UZzVNRFUxUlRVd1FVSkRNRU13UmtGRVFrRXpSZyJ9.eyJodHRwczovL2FwaS5vcGVuYWkuY29tL3Byb2ZpbGUiOnsiZW1haWwiOiJqZ3B4ZGJkZXJqODZAb3V0bG9vay5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZX0sImh0dHBzOi8vYXBpLm9wZW5haS5jb20vYXV0aCI6eyJwb2lkIjoib3JnLVAweW5aUmFyUllwTGh2NlhiTHFiYlc1RiIsInVzZXJfaWQiOiJ1c2VyLUtJUFZsa054V2VJY2xaWEduaXJyWkhlRyJ9LCJpc3MiOiJodHRwczovL2F1dGgwLm9wZW5haS5jb20vIiwic3ViIjoiYXV0aDB8NjU1YjI5NGYwZjgyZDQ0Nzg5MTcyOGE0IiwiYXVkIjpbImh0dHBzOi8vYXBpLm9wZW5haS5jb20vdjEiLCJodHRwczovL29wZW5haS5vcGVuYWkuYXV0aDBhcHAuY29tL3VzZXJpbmZvIl0sImlhdCI6MTcwNTQ4OTczOSwiZXhwIjoxNzA2MzUzNzM5LCJhenAiOiJUZEpJY2JlMTZXb1RIdE45NW55eXdoNUU0eU9vNkl0RyIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgbW9kZWwucmVhZCBtb2RlbC5yZXF1ZXN0IG9yZ2FuaXphdGlvbi5yZWFkIG9yZ2FuaXphdGlvbi53cml0ZSBvZmZsaW5lX2FjY2VzcyJ9.xUiigsLdYFWgP13Gb6Odk9rIYh7gF7OV7h_P6O5IIWEe25FTsvNaSSSI6ODbjUQDTfam1BM6Wb5sR35blWJjvl87ZOomMUJaUwv1HFbCrpQgHWBJH7ga42hue6HV1-zKywKxoGmDHio9hlvaix_w5HGwUGK10KmlGH-F7a7VZaRrTnFMwCbPJt3FozLr8ubYnUAKc3uS6cYa9fa1e9P80dTTmacmJ2IjRcvj4yTog12OQLnLPkwKaF0pTr8NLJ9hRJiE1IhfP26tQwH6W2_IuRuQ9KhUotafmTV8hQ2exGJl1wlb4b2kL-TqmyWWP1BCyaEMlTDYSifgOEF2Bn69uA";
        // 设置请求参数
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        StringBuilder responseText = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));

            // 发送请求并获取响应
            org.apache.http.HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            // 读取响应内容
            if (httpEntity != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()))) {
                    responseText = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        responseText.append(line);
                    }
                    return responseText;
//                    // 输出响应内容  {"expire_at":1705741487,"show_conversations":false,"show_userinfo":false,"site_limit":"https://chat.ttzi.com","token_key":"fk-ioAdsx-ms_gI6ZIPW7svbcu0Vf_XTPqKwgdiwaHmlqI","unique_name":"mhchat"}
//                    JSONObject jsonObject = new JSONObject(responseText);
//                    String accessToken = jsonObject.getStr("access_token");
//                    String sessionToken = jsonObject.getStr("session_token");
//                    System.out.println("responseText:" + responseText.toString());
////                    String tokenKey = (String) jsonObject.get("token_key");
//                    System.out.println("accessToken:" + accessToken);
//                    System.out.println("sessionToken:" + sessionToken);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseText;
    }

    public static String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0];
        }
        return ipAddress;
    }


    public static void main(String[] args) {

        RequestUtils requestUtils = new RequestUtils();
        //gpt-4-1106-preview   gpt-3.5-turbo-1106
        //"\"response_format\": {\"type\": \"json_object\"}, "
//        String jsonPar = "{\"model\": \"gpt-3.5-turbo-1106\", " +
//                "\"response_format\": {\"type\": \"json_object\"}, " +
//                "\"messages\": [" +
//                "{\"role\": \"system\", \"content\": \"You are a helpful assistant designed to output JSON.\"}," +
//                "{\"role\": \"user\", \"content\": \"树上 9 只鸟，打掉 1 只，还剩几只？\"}" +
//                "]}";
        String user = "树上 9 只鸟，打掉 1 只，还剩几只？";

        String body = null;
        try {
            body = requestUtils.textRequest(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //{
        //  "id": "chatcmpl-8MuHMhaMEYGT0xZNzoNBc3crWPIKy",
        //  "object": "chat.completion",
        //  "created": 1700470244,
        //  "model": "gpt-3.5-turbo-1106",
        //  "choices": [
        //    {
        //      "index": 0,
        //      "message": {
        //        "role": "assistant",
        //        "content": "{\"game\":\"英雄联盟\",\"season\":\"S9\",\"champion\":\"冠军\"}"
        //      },
        //      "finish_reason": "stop"
        //    }
        //  ],
        //  "usage": {
        //    "prompt_tokens": 47,
        //    "completion_tokens": 23,
        //    "total_tokens": 70
        //  },
        //  "system_fingerprint": "fp_eeff13170a"
        //}
        System.out.println(body);
        JSONObject jsonObject = JSONUtil.parseObj(body);
        //获取message
        JSONObject firstChoice = jsonObject.getJSONArray("choices").getJSONObject(0);
        String content = firstChoice.getJSONObject("message").getStr("content");
        System.out.println("content Value:" + content);
        }
    }



