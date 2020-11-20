package com.login.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicesManager implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ServicesManager.class);
    private final static String ROOT_PATH_SERVICES = "/services";
    private Map<String, Map<String, String>> servicesMap;

    @Autowired
    private CuratorFramework curatorFramework;

    private PathChildrenCache servicesCache;

    private void constructServices() throws Exception {
        servicesMap = new HashMap<>();
        List<String> servicesName = curatorFramework.getChildren().forPath(ROOT_PATH_SERVICES);
        for(String service : servicesName) {
            Map<String, String> handlersMap = new HashMap<>();
            servicesMap.put(service, handlersMap);
            String servicePath = ROOT_PATH_SERVICES + "/" + service;
            List<String> handlersPoint = curatorFramework.getChildren().forPath(servicePath);
            for(String handler : handlersPoint) {
                String pointPath = servicePath + "/" + handler;
                String handlerAddress = new String(curatorFramework.getData().forPath(pointPath));
                logger.info("create service .......{}!!!", handler, handlerAddress);
                System.out.println(handler + "......" + handlerAddress);
                handlersMap.put(handler, handlerAddress);
            }
        }

        servicesCache = new PathChildrenCache(curatorFramework, ROOT_PATH_SERVICES, true);
        servicesCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        servicesCache.getListenable().addListener((client, event) -> {
            System.out.println("event type: " +  event.getType());
            if(null != event.getData()) {
                System.out.println("path: " + event.getData().getPath() + " data: " + event.getData().getData());
            }
//            if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
//                String oldPath = event.getData().getPath();
//                logger.info("success to release lock for path:{}", oldPath);
//            }
        });
    }

    @Override
    public void afterPropertiesSet()  {
        curatorFramework = curatorFramework.usingNamespace("services-namespace");
        curatorFramework.sync();

        try {
            if(null == curatorFramework.checkExists().forPath(ROOT_PATH_SERVICES)) {
                logger.info("create path services .......!!!");
                curatorFramework.create().forPath(ROOT_PATH_SERVICES);
                System.out.println(curatorFramework.getChildren().forPath("/"));
            }

            constructServices();

        }catch (Exception e) {
            logger.error("connect zookeeper fail，please check the log >> {}", e.getMessage(), e);
        }
    }

    @PreDestroy
    public void destory() throws Exception {
        logger.info("destory servicesManage .......!!!");
        servicesCache.close();
        servicesMap.clear();
    }
}
