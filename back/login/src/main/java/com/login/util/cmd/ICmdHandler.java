package com.login.util.cmd;

@FunctionalInterface
public interface ICmdHandler {
    public String handler(String req_cmd);;
}
