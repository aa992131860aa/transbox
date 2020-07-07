package com.life.entity;

public class AccessToken {
    //{"access_token":"12_dWFM0i7uFwz-HRP34DG5pvfGBCgK5BkkKbrNlRqDG8mOucSy1sPCXjS8eO2c9MhXHHfWoabcgLsWCOtNeTZWd9mRNuBPF1dVh58lWjTnamrHGRwVf4d7D5ntOZYDDVbADANNF","expires_in":7200}
    private String access_token;
    private String expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String accessToken) {
        access_token = accessToken;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expiresIn) {
        expires_in = expiresIn;
    }

}
