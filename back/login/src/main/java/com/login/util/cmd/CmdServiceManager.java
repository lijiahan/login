package com.login.util.cmd;

import com.alibaba.fastjson.JSON;
import com.login.config.ZookeeperClient;
import com.login.entity.HandlerInfo;
import com.login.util.SpringUtil;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CmdServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(CmdServiceManager.class);

    private String zookeeperPath;
    private String serviceName;
    private PathChildrenCache serviceCache;
    private HashMap<String, CmdService> cmdServiceMap;

    public CmdServiceManager(String path, String name) {
        cmdServiceMap = new HashMap<>();
        init(path, name);
    }

    public String handler(String req_cmd) {
        String res = "";
        CmdService cmdService = schedule();
        if(cmdService != null) {
            System.out.println("/service... handler: " + req_cmd);
            res = cmdService.handler(req_cmd);
            System.out.println("/service... res: " + res);
        }
       return res;
    }

    private void init(String path, String name) {
        zookeeperPath = path;
        serviceName = name;
        ZookeeperClient zookeeperClient = (ZookeeperClient) SpringUtil.getBean(ZookeeperClient.class);

        switch (serviceName) {
            default: {
                PathChildrenCacheListener cacheListener = (client, event) -> {
                    System.out.println("/service... name: " + serviceName + "  type: " +  event.getType());
                    if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                        if (null != event.getData()) {
                            String handlerPath = event.getData().getPath();
                            String handlerName = handlerPath.replace(zookeeperPath, "");
                            if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                                //
                                String handlerData = new String(event.getData().getData());
                                HandlerInfo handlerInfo = JSON.parseObject(handlerData, HandlerInfo.class);
                                CmdService nService = new CmdService(handlerPath, handlerName, handlerInfo);
                                CmdServiceConstruct.constructService(nService);
                                //
                                System.out.println("handler path: " + handlerPath + " data: " + handlerInfo);
                                cmdServiceMap.put(handlerName, nService);
                            }
                            else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                                CmdService cmdService = cmdServiceMap.remove(handlerName);
                                cmdService.stop();
                            }
                            else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
                                String handlerData = new String(event.getData().getData());
                                HandlerInfo handlerInfo = JSON.parseObject(handlerData, HandlerInfo.class);
                                System.out.println("handler path: " + handlerPath + " data: " + handlerInfo);
                            }
                        }
                        else {
                            logger.warn("/service... name: " + serviceName + " is null " );
                        }
                    }
                };

                serviceCache = zookeeperClient.watch(zookeeperPath, cacheListener);
            }
        }

    }

    private CmdService schedule() {
        CmdService serviceOpt = null;
        int count = -1;
        for (Map.Entry<String, CmdService> entry : cmdServiceMap.entrySet()) {
            CmdService tmpService = entry.getValue();
            int tmpCount = tmpService.getUseNum();
            if(count == -1 || tmpCount < count) {
                serviceOpt = tmpService;
                count = tmpCount;
            }
        }
        return serviceOpt;
    }

    private void stop() throws Exception{
        serviceCache.close();
    }
}
