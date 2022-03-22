package com.klipvr.backend.models;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="userinfo" ,
        uniqueConstraints ={
@UniqueConstraint(columnNames = "username")
})
public class UserInfo
{
    public UserInfo()
    {
    }
    private Long level;
    private Long task_done;
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public UserInfo(Long level, Long task_done, String username, Long id, Long coins, Long avatar)
    {
        this.level = level;
        this.task_done = task_done;
        this.username = username;
        this.id = id;
        this.coins = coins;
        this.avatar = avatar;
    }
    @NotBlank
    private String username;
    public Long getId() {
        return id;
    }
    public Long getTask_done() {
        return task_done;
    }
    public void setTask_done(Long task_done) {
        this.task_done = task_done;
    }
    public void setId(Long id)
    {
        this.id = id;
    }
    public Long getCoins() {
        return coins;
    }
    public void setCoins(Long coins) {
        this.coins = coins;
    }
    public Long getAvatar() {
        return avatar;
    }
    public void setAvatar(Long avatar) {
        this.avatar = avatar;
    }
    @Id
    private Long id;
    private Long coins;
    private Long avatar;
    public Long getLevel() {
        return level;
    }
    public void setLevel(Long level) {
        this.level = level;
    }
}
