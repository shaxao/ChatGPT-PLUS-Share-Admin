package com.louwei.gptresource.utils;

import java.math.BigDecimal;

public class DanweiUtils {

    /**
     * 将分转换为元。
     *
     * @param amountInCents 金额，以分为单位
     * @return 金额，以元为单位，保留两位小数
     */
    public static BigDecimal centsToYuan(Integer amountInCents) {
        if (amountInCents == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        return new BigDecimal(amountInCents).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 将元转换为分。
     *
     * @param amountInYuan 金额，以元为单位
     * @return 金额，以分为单位，为整数
     */
    public static Integer yuanToCents(BigDecimal amountInYuan) {
        if (amountInYuan == null) {
            throw new IllegalArgumentException("金额不能为空");
        }
        return amountInYuan.multiply(new BigDecimal(100)).intValue();
    }

    public static void main(String[] args) {
        BigDecimal bigDecimal = centsToYuan(5689);
        System.out.println(bigDecimal);
    }
}
