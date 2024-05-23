package com.louwei.gptresource.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Data
public class AppConfigService {

    @Value("${app.config.url}")
    private String url;
    @Value("${app.config.apiKey}")
    private String apiKey;
    @Value("${app.config.talk}")
    private String chatTalk;

}
