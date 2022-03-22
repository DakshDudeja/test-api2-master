package com.klipvr.backend.payload.request;

import javax.validation.constraints.NotBlank;

public class AddFriend {
    @NotBlank
    private String username;
    private Long id;

    public AddFriend() {
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
