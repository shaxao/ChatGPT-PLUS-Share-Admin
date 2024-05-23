//package com.louwei.gptresource;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.json.JSON;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.junit.jupiter.api.Test;
//import org.junit.platform.engine.discovery.PackageSelector;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.crypto.interfaces.PBEKey;
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//public class TokenTest {
//    @Value("${pandora.config.file}")
//    private String configFile;
//
//    @Test
//    void shareTokenTest(){
//        // 设置请求的URL
//        String url = "https://one.ttzi.top/mhchat3464/api/token/register";
//
//        // 设置请求头
//        HttpClient httpClient = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost(url);
//        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//
//        String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik1UaEVOVUpHTkVNMVFURTRNMEZCTWpkQ05UZzVNRFUxUlRVd1FVSkRNRU13UmtGRVFrRXpSZyJ9.eyJodHRwczovL2FwaS5vcGVuYWkuY29tL3Byb2ZpbGUiOnsiZW1haWwiOiJqZ3B4ZGJkZXJqODZAb3V0bG9vay5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZX0sImh0dHBzOi8vYXBpLm9wZW5haS5jb20vYXV0aCI6eyJwb2lkIjoib3JnLVAweW5aUmFyUllwTGh2NlhiTHFiYlc1RiIsInVzZXJfaWQiOiJ1c2VyLUtJUFZsa054V2VJY2xaWEduaXJyWkhlRyJ9LCJpc3MiOiJodHRwczovL2F1dGgwLm9wZW5haS5jb20vIiwic3ViIjoiYXV0aDB8NjU1YjI5NGYwZjgyZDQ0Nzg5MTcyOGE0IiwiYXVkIjpbImh0dHBzOi8vYXBpLm9wZW5haS5jb20vdjEiLCJodHRwczovL29wZW5haS5vcGVuYWkuYXV0aDBhcHAuY29tL3VzZXJpbmZvIl0sImlhdCI6MTcwNTQ4OTczOSwiZXhwIjoxNzA2MzUzNzM5LCJhenAiOiJUZEpJY2JlMTZXb1RIdE45NW55eXdoNUU0eU9vNkl0RyIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgbW9kZWwucmVhZCBtb2RlbC5yZXF1ZXN0IG9yZ2FuaXphdGlvbi5yZWFkIG9yZ2FuaXphdGlvbi53cml0ZSBvZmZsaW5lX2FjY2VzcyJ9.xUiigsLdYFWgP13Gb6Odk9rIYh7gF7OV7h_P6O5IIWEe25FTsvNaSSSI6ODbjUQDTfam1BM6Wb5sR35blWJjvl87ZOomMUJaUwv1HFbCrpQgHWBJH7ga42hue6HV1-zKywKxoGmDHio9hlvaix_w5HGwUGK10KmlGH-F7a7VZaRrTnFMwCbPJt3FozLr8ubYnUAKc3uS6cYa9fa1e9P80dTTmacmJ2IjRcvj4yTog12OQLnLPkwKaF0pTr8NLJ9hRJiE1IhfP26tQwH6W2_IuRuQ9KhUotafmTV8hQ2exGJl1wlb4b2kL-TqmyWWP1BCyaEMlTDYSifgOEF2Bn69uA";
//        // 设置请求参数
//        List<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("unique_name", "mhchat"));
//        params.add(new BasicNameValuePair("access_token", accessToken));
//        params.add(new BasicNameValuePair("site_limit", "https://one.ttzi.top"));
//        params.add(new BasicNameValuePair("expires_in", "86400"));
//        params.add(new BasicNameValuePair("show_conversations", "false"));
//        params.add(new BasicNameValuePair("show_userinfo", "false"));
//
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(params));
//
//            // 发送请求并获取响应
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            HttpEntity httpEntity = httpResponse.getEntity();
//
//            // 读取响应内容
//            if (httpEntity != null) {
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()))) {
//                    StringBuilder responseText = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        responseText.append(line);
//                    }
//
//                    // 输出响应内容  {"expire_at":1705741487,"show_conversations":false,"show_userinfo":false,"site_limit":"https://chat.ttzi.com","token_key":"fk-ioAdsx-ms_gI6ZIPW7svbcu0Vf_XTPqKwgdiwaHmlqI","unique_name":"mhchat"}
//                    JSONObject jsonObject = new JSONObject(responseText);
//                    String tokenKey = (String) jsonObject.get("token_key");
//                    System.out.println("ShareToken:" + tokenKey);
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    void tokenTest(){
//        // 设置请求的URL
//        String url = "https://one.ttzi.top/mhchat3464/api/auth/login";
//
//        // 设置请求头
//        HttpClient httpClient = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost(url);
//        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//
//       // String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik1UaEVOVUpHTkVNMVFURTRNMEZCTWpkQ05UZzVNRFUxUlRVd1FVSkRNRU13UmtGRVFrRXpSZyJ9.eyJodHRwczovL2FwaS5vcGVuYWkuY29tL3Byb2ZpbGUiOnsiZW1haWwiOiJqZ3B4ZGJkZXJqODZAb3V0bG9vay5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZX0sImh0dHBzOi8vYXBpLm9wZW5haS5jb20vYXV0aCI6eyJwb2lkIjoib3JnLVAweW5aUmFyUllwTGh2NlhiTHFiYlc1RiIsInVzZXJfaWQiOiJ1c2VyLUtJUFZsa054V2VJY2xaWEduaXJyWkhlRyJ9LCJpc3MiOiJodHRwczovL2F1dGgwLm9wZW5haS5jb20vIiwic3ViIjoiYXV0aDB8NjU1YjI5NGYwZjgyZDQ0Nzg5MTcyOGE0IiwiYXVkIjpbImh0dHBzOi8vYXBpLm9wZW5haS5jb20vdjEiLCJodHRwczovL29wZW5haS5vcGVuYWkuYXV0aDBhcHAuY29tL3VzZXJpbmZvIl0sImlhdCI6MTcwNTQ4OTczOSwiZXhwIjoxNzA2MzUzNzM5LCJhenAiOiJUZEpJY2JlMTZXb1RIdE45NW55eXdoNUU0eU9vNkl0RyIsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgbW9kZWwucmVhZCBtb2RlbC5yZXF1ZXN0IG9yZ2FuaXphdGlvbi5yZWFkIG9yZ2FuaXphdGlvbi53cml0ZSBvZmZsaW5lX2FjY2VzcyJ9.xUiigsLdYFWgP13Gb6Odk9rIYh7gF7OV7h_P6O5IIWEe25FTsvNaSSSI6ODbjUQDTfam1BM6Wb5sR35blWJjvl87ZOomMUJaUwv1HFbCrpQgHWBJH7ga42hue6HV1-zKywKxoGmDHio9hlvaix_w5HGwUGK10KmlGH-F7a7VZaRrTnFMwCbPJt3FozLr8ubYnUAKc3uS6cYa9fa1e9P80dTTmacmJ2IjRcvj4yTog12OQLnLPkwKaF0pTr8NLJ9hRJiE1IhfP26tQwH6W2_IuRuQ9KhUotafmTV8hQ2exGJl1wlb4b2kL-TqmyWWP1BCyaEMlTDYSifgOEF2Bn69uA";
//        // 设置请求参数
//        List<NameValuePair> params = new ArrayList<>();
//        params.add(new BasicNameValuePair("username", "mfdlfnpfn@proton.me"));
//        params.add(new BasicNameValuePair("password", "hdhuihfoeuhvdnov"));
//        StringBuilder responseText = null;
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(params));
//
//            // 发送请求并获取响应
//            HttpResponse httpResponse = httpClient.execute(httpPost);
//            HttpEntity httpEntity = httpResponse.getEntity();
//
//            // 读取响应内容
//            if (httpEntity != null) {
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent()))) {
//                    responseText = new StringBuilder();
//                    String line;
//
//                    while ((line = reader.readLine()) != null) {
//                        responseText.append(line);
//                    }
//                    System.out.println(responseText);
//                    // 输出响应内容  {"expire_at":1705741487,"show_conversations":false,"show_userinfo":false,"site_limit":"https://chat.ttzi.com","token_key":"fk-ioAdsx-ms_gI6ZIPW7svbcu0Vf_XTPqKwgdiwaHmlqI","unique_name":"mhchat"}
//
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        JSONObject jsonObject = new JSONObject(responseText);
//        String accessToken = jsonObject.getStr("access_token");
//        String sessionToken = jsonObject.getStr("session_token");
//        System.out.println("responseText:" + responseText.toString());
////                    String tokenKey = (String) jsonObject.get("token_key");
//        System.out.println("accessToken:" + accessToken);
//        System.out.println("sessionToken:" + sessionToken);
//    }
//
//    @Test
//    void fileTest(){
//        File file = new File(configFile);
//        JSONObject jsonObject = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
////{"access_token":"this is access_token","password":"asdsafsafass","share_token":"","share":true,"show_user_info":false,"plus":false,"token":"this is session_token","username":"muhuohuo2@proton.me"}
////        String name = String.valueOf(jsonObject.get("test3"));
////        System.out.println(name);
//        //JSONObject object = (JSONObject) jsonObject.get("test3");
//        //jsonObject.remove("test3");
//        //object.put("access_token","new access_token");
//        //FileUtil.writeString(jsonObject.toStringPretty(),configFile,StandardCharsets.UTF_8);
//        Map<String,Object> dataForName = new HashMap<>();
//        dataForName.put("token","this is session_token");
//        dataForName.put("access_token","this is access_token");
//        dataForName.put("share_token", "");
//        dataForName.put("username","muhuohuo2@proton.me");
//        dataForName.put("userPassword","asdsafsafass");
//        dataForName.put("share",true);
//        dataForName.put("show_user_info",false);
//        dataForName.put("plus",false);
//        JSONObject dataJsonObect = new JSONObject();
//        dataJsonObect.putAll(dataForName);
//        jsonObject.putOnce("test3",dataJsonObect);
//        FileUtil.writeString(jsonObject.toStringPretty(),configFile,StandardCharsets.UTF_8);
//        System.out.println("文件已成功更新！");
//        System.out.println(jsonObject);
//    }
//}
