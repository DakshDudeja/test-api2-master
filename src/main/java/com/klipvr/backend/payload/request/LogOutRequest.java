package com.klipvr.backend.payload.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public class LogOutRequest {
    public Long getId() {
        return id;
    }

    public LogOutRequest(Long id, String token) {
        this.id = id;
        this.token = token;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Valid
    @NotNull(message = "Device info cannot be null")
    private Long id;

    @Valid
    @NotNull(message = "Existing Token needs to be passed")
    private String token;
}
