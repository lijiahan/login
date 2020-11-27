package com.login.util;

public interface IHandler {
    public void build(String hName, String hAddress);
    public String handler(String req);
    public boolean release(String hName);
}
