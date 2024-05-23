package com.louwei.gptresource.test;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
//import java.util.HashMap;
//import java.util.Base64;
//import com.google.gson.GsonBuilder;
//import javax.activation.DataHandler;
//import javax.activation.FileDataSource;
//import java.net.URL;
//import java.net.URLEncoder;
//import javax.activation.DataHandler;
//import javax.activation.URLDataSource;

public class SampleMail {
    protected static String genMessageID(String mailFrom) {
        // message-id 必须符合 first-part@last-part
        String[] mailInfo = mailFrom.split("@");
        String domain = mailFrom;
        int index = mailInfo.length - 1;
        if (index >= 0) {
            domain = mailInfo[index];
        }
        UUID uuid = UUID.randomUUID();
        StringBuffer messageId = new StringBuffer();
        messageId.append('<').append(uuid.toString()).append('@').append(domain).append('>');
        return messageId.toString();
    }

    public static void main(String[] args) {
        // 配置发送邮件的环境属性
        final Properties props = new Properties();

        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtpdm.aliyun.com");
        //设置端口：
        //props.put("mail.smtp.port", "80");//或"25", 如果使用ssl，则去掉使用80或25端口的配置，进行如下配置：
        //加密方式：
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.port", "465");

        props.put("mail.smtp.from", "mhchat@ttzi.top");    //mailfrom 参数
        props.put("mail.user", "mhchat@ttzi.top");// 发件人的账号（在控制台创建的发信地址）
        props.put("mail.password", "T1e2s3t4P5a6s");// 发信地址的smtp密码（在控制台选择发信地址进行设置）
        props.setProperty("mail.smtp.ssl.enable", "true");  //请配合465端口使用
        System.setProperty("mail.mime.splitlongparameters", "false");//用于解决附件名过长导致的显示异常
        //props.put("mail.smtp.connectiontimeout", 1000);

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };

        //使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        //mailSession.setDebug(true);//开启debug模式


        final String messageIDValue = genMessageID(props.getProperty("mail.user"));
        //创建邮件消息
        MimeMessage message = new MimeMessage(mailSession) {
            @Override
            protected void updateMessageID() throws MessagingException {
                //设置自定义Message-ID值
                setHeader("Message-ID", messageIDValue);//创建Message-ID
            }
        };

        try {
            // 设置发件人邮件地址和名称。填写控制台配置的发信地址。和上面的mail.user保持一致。名称用户可以自定义填写。
            InternetAddress from = new InternetAddress("mhchat@ttzi.top", "木火科技");//from 参数,可实现代发，注意：代发容易被收信方拒信或进入垃圾箱。
            message.setFrom(from);

            //可选。设置回信地址
//            Address[] a = new Address[1];
//            a[0] = new InternetAddress("415240147@qq.com");
//            message.setReplyTo(a);

            // 设置收件人邮件地址
            InternetAddress to = new InternetAddress("415240147@qq.com");
            message.setRecipient(MimeMessage.RecipientType.TO, to);
            //如果同时发给多人，才将上面两行替换为如下（因为部分收信系统的一些限制，尽量每次投递给一个人；同时我们限制单次允许发送的人数，具体参考规格清单）：
            //InternetAddress[] adds = new InternetAddress[2];
            //adds[0] = new InternetAddress("收信地址");
            //adds[1] = new InternetAddress("收信地址");
            //message.setRecipients(Message.RecipientType.TO, adds);

            message.setSentDate(new Date()); //设置时间
//            String ccUser = "抄送地址";
//            // 设置多个抄送地址
//            if (null != ccUser && !ccUser.isEmpty()) {
//                @SuppressWarnings("static-access")
//                InternetAddress[] internetAddressCC = new InternetAddress().parse(ccUser);
//                message.setRecipients(Message.RecipientType.CC, internetAddressCC);
//            }
//            String bccUser = "密送地址";
//            // 设置多个密送地址
//            if (null != bccUser && !bccUser.isEmpty()) {
//                @SuppressWarnings("static-access")
//                InternetAddress[] internetAddressBCC = new InternetAddress().parse(bccUser);
//                message.setRecipients(Message.RecipientType.BCC, internetAddressBCC);
//            }
            //设置邮件标题
            message.setSubject("【木火科技】验证码通知");
            message.setContent("安全验证码\"+code+\"，有效期十分钟!", "text/plain;charset=UTF-8");//html超文本；// "text/plain;charset=UTF-8" //纯文本。

//            //若需要开启邮件跟踪服务，请使用以下代码设置跟踪链接头。前置条件和约束见文档"如何开启数据跟踪功能？"
//            String tagName = "Test";
//            HashMap<String, String> trace = new HashMap<>();
//            //这里为字符串"1"
//            trace.put("OpenTrace", "1");      //打开邮件跟踪
//            trace.put("LinkTrace", "1");     //点击邮件里的URL跟踪
//            trace.put("TagName", tagName);   //控制台创建的标签tagname
//            String jsonTrace = new GsonBuilder().setPrettyPrinting().create().toJson(trace);
//            //System.out.println(jsonTrace);
//            String base64Trace = new String(Base64.getEncoder().encode(jsonTrace.getBytes()));
//            //设置跟踪链接头
//            message.addHeader("X-AliDM-Trace", base64Trace);
//            //邮件eml原文中的示例值：X-AliDM-Trace: eyJUYWdOYW1lIjoiVGVzdCIsIk9wZW5UcmFjZSI6IjEiLCJMaW5rVHJhY2UiOiIxIn0=

//发送附件和内容：
//            BodyPart messageBodyPart = new MimeBodyPart();
//            //messageBodyPart.setText("消息<br>Text");//设置邮件的内容，文本
//            messageBodyPart.setContent("测试<br> 内容", "text/html;charset=UTF-8");// 纯文本："text/plain;charset=UTF-8" //设置邮件的内容
//            //创建多重消息
//            Multipart multipart = new MimeMultipart();
//            //设置文本消息部分
//            multipart.addBodyPart(messageBodyPart);

//            //附件部分
//            //发送附件，总的邮件大小不超过15M，创建消息部分。
            //发送本地附件
//            String[] fileList = new String[2];
//            fileList[0] = "C:\\Users\\test1.txt";
//            fileList[1] = "C:\\Users\\test2.txt";
//            for (int index = 0; index < fileList.length; index++) {
//                MimeBodyPart mimeBodyPart = new MimeBodyPart();
//                //            //设置要发送附件的文件路径
//                FileDataSource filesdata = new FileDataSource(fileList[index]);
//                mimeBodyPart.setDataHandler(new DataHandler(filesdata));
//                //处理附件名称中文（附带文件路径）乱码问题
//                mimeBodyPart.setFileName(MimeUtility.encodeWord("自定义附件名.xlsx"));
//                mimeBodyPart.addHeader("Content-Transfer-Encoding", "base64");
//                multipart.addBodyPart(mimeBodyPart);
//            }


            //发送URL附件
//            String[] fileList = new String[2];
//            fileList[0] = "https://example.oss-cn-shanghai.aliyuncs.com/xxxxxxxxxxx1.png";
//            fileList[1] = "https://example.oss-cn-shanghai.aliyuncs.com/xxxxxxxxxxx2.png";
//            for  (int index = 0; index < fileList.length; index++) {
//                String encode = URLEncoder.encode(fileList[index], "UTF-8");
//                MimeBodyPart mimeBodyPart = new MimeBodyPart();
//                mimeBodyPart.setDataHandler(new DataHandler(new URLDataSource(new URL(encode.replace("%3A",":").replace("%2F","/")))));
//                mimeBodyPart.setFileName(MimeUtility.encodeText("自定义附件名.xlsx"));
//                multipart.addBodyPart(mimeBodyPart);
//            }


//            //发送含有附件的完整消息
//            message.setContent(multipart);
//            // 发送附件代码，结束

            // 发送邮件
            Transport.send(message);

        } catch (MessagingException e) {
            String err = e.getMessage();
            // 在这里处理message内容， 格式是固定的
            System.out.println(err);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } //catch (MalformedURLException e) {
        //    e.printStackTrace();
        //}
    }
}
