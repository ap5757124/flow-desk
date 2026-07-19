package com.example.flowdesk.security.dto.res;


import lombok.Data;

@Data
public class RefreshTokenRes {

    private String accessToken;

    private String refreshToken;

}
