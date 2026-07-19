package com.example.flowdesk.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置项。
 *
 * <p>字段值来自 {@code application.yaml} 中的 {@code flow-desk.security.jwt} 节点。</p>
 */
@Data // Lombok 为配置字段生成 getter 和 setter，便于 Spring 写入配置值
@Component // 把该配置对象注册进 Spring 容器，其他 Bean 可以通过构造器注入
@ConfigurationProperties(prefix = "flow-desk.security.jwt") // 将指定前缀下的 YAML 配置绑定到本类字段
public class JwtProperties {

    /** JWT 签名密钥；生产环境应从环境变量或密钥管理服务读取。 */
    private String secret;

    /** Access Token 的有效分钟数。 */
    private long accessTokenExpirationMinutes;

    /** Refresh Token 的有效天数。 */
    private long refreshTokenExpirationDays;
}
