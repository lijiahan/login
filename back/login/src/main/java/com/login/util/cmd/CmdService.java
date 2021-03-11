package com.login.util.cmd;

import com.login.entity.HandlerInfo;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

public class CmdService {
    private String zookeeperPath;
    private String handlerName;
    private ICmdHandler cmdHandler;
    private HandlerInfo handlerInfo;
    private int useNum;

    public CmdService(String path, String name, HandlerInfo handler_info) {
        this.zookeeperPath = path;
        this.handlerName = name;
        this.handlerInfo = handler_info;
        this.useNum = 0;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public HandlerInfo getHandlerInfo() {
        return handlerInfo;
    }

    public void setHandlerInfo(HandlerInfo handlerInfo) {
        this.handlerInfo = handlerInfo;
    }

    public String getZookeeperPath() {
        return zookeeperPath;
    }

    public void setZookeeperPath(String zookeeperPath) {
        this.zookeeperPath = zookeeperPath;
    }


    public void setCmdHandler(ICmdHandler cmdHandler) {
        this.cmdHandler = cmdHandler;
    }

    public int getUseNum() {
        return useNum;
    }

    public void setUseNum(int useNum) {
        this.useNum = useNum;
    }

    public String handler(String req) {
        useNum++;
        return cmdHandler.handler(req);
    }

    public void stop() {

    }
}
