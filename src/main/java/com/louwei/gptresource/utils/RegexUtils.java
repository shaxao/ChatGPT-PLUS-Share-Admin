package com.louwei.gptresource.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    // 正则表达式：手机号
    private static final String MOBILE_REGEX = "^1[3-9]\\d{9}$";

    // 正则表达式：QQ邮箱账号
    private static final String QQ_EMAIL_REGEX = "^[1-9]\\d{4,11}@qq\\.com$";

    public static void main(String[] args) {
        String input1 = "13800138000"; // 示例手机号
        String input2 = "123456789@qq.com"; // 示例QQ邮箱账号

        System.out.println("Is " + input1 + " a mobile number? " + isMobileNumber(input1));
        System.out.println("Is " + input2 + " a QQ email? " + isQQEmail(input2));
    }

    // 方法：判断是否为手机号
    private static boolean isMobileNumber(String input) {
        Pattern pattern = Pattern.compile(MOBILE_REGEX);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    // 方法：判断是否为QQ邮箱账号
    private static boolean isQQEmail(String input) {
        Pattern pattern = Pattern.compile(QQ_EMAIL_REGEX);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
