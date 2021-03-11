package com.login.entity;

public class HandlerInfo {
    private String name;
    private String url;

    public HandlerInfo() {

    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "HandlerInfo{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
