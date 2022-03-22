package com.klipvr.backend.payload.request;

import javax.validation.constraints.NotBlank;

public class UserName {

    @NotBlank
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
