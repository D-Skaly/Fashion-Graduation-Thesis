package com.skaly.fashion_backend.common.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.mail")
public class EmailProperties {
    private String host = "localhost";
    private int port = 587;
    private String username = "";
    private String password = "";
    private String from = "";
    private Properties properties = new Properties();

    @Data
    public static class Properties {
        private boolean auth = true;
        private boolean starttlsEnable = true;
    }
}
