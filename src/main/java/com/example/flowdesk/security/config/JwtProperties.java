package com.example.flowdesk.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "flow-desk.security.jwt")
public class JwtProperties {

    private String secret;

    private long accessTokenExpirationMinutes;

    private long refreshTokenExpirationDays;
}
