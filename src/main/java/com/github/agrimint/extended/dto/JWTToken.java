package com.github.agrimint.extended.dto;

public class JWTToken {

    private String idToken;

    private String accessToken;

    private String refreshToken;

    public JWTToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public JWTToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    String getIdToken() {
        return idToken;
    }

    void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
