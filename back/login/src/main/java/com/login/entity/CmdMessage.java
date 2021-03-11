package com.login.entity;

public class CmdMessage {
    private String uid;
    private String cmd;
    private String data;

    public CmdMessage() {

    }

    public CmdMessage(String uid, String cmd, String data) {
        this.uid = uid;
        this.cmd = cmd;
        this.data = data;
    }

    public String getCmd() {
        return cmd;
    }

    public String getData() {
        return data;
    }

    public String getUid() {
        return uid;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
