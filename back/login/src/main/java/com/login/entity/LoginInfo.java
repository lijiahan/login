package com.login.entity;

public class LoginInfo {
    private String name;
    private String password;

    public LoginInfo() {

    }

    public LoginInfo(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passWord) {
        this.password = passWord;
    }

    public String getName() {
        return name;
    }

    public void setName(String userName) {
        this.name = userName;
    }

    @Override
    public String toString() {
        return "User{" +
                " userName='" + name + '\'' +
                ", passWord='" + password + '\'' +
                '}';
    }
}
