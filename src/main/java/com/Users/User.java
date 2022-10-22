package com.Users;

import java.io.Serializable;

public class User implements Serializable {

    private String userName;
    private String nickName;
    private String password;
    private Status status;

    public User(String userName, String nickName, String password) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
    }

    public User() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return userName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

