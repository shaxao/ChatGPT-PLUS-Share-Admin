package com.louwei.gptresource.domain;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class PayReturn {
    private Integer code;
    private String msg;
    private String tradeNo;
    private String payUrl;
    private String qrcode;
}
