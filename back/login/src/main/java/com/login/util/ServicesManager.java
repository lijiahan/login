package com.login.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
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

    private Map<String, PathChildrenCache> servicesCacheMap;

    private void constructServices() throws Exception {
        servicesMap = new HashMap<>();
        servicesCacheMap = new HashMap<>();

        List<String> servicesName = curatorFramework.getChildren().forPath(ROOT_PATH_SERVICES);
        for(String service : servicesName) {
            Map<String, String> handlersMap = new HashMap<>();
            servicesMap.put(service, handlersMap);
            String servicePath = ROOT_PATH_SERVICES + "/" + service;
            List<String> handlersPoint = curatorFramework.getChildren().forPath(servicePath);
            for(String handler : handlersPoint) {
                String pointPath = servicePath + "/" + handler;
                String handlerAddress = new String(curatorFramework.getData().forPath(pointPath));
                logger.info("create service .......{}!!!{}", handler, handlerAddress);
//                System.out.println(handler + "......" + handlerAddress);
                handlersMap.put(handler, handlerAddress);
            }
        }

        PathChildrenCache serviceCache = new PathChildrenCache(curatorFramework, ROOT_PATH_SERVICES, true);
        serviceCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        serviceCache.getListenable().addListener((client, event) -> {
            System.out.println("event type: " +  event.getType());
            if(null != event.getData()) {
                System.out.println("path: " + event.getData().getPath() + " data: " + new String(event.getData().getData()));
            }
            if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                String servicePath = event.getData().getPath();
                PathChildrenCache handlersCache = new PathChildrenCache(curatorFramework, servicePath, true);
                handlersCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
                handlersCache.getListenable().addListener((client1, event1) -> {
                    System.out.println("handler event type: " +  event1.getType());
                    if(null != event1.getData()) {
                        System.out.println("handler path: " + event1.getData().getPath() + " data: " + new String(event1.getData().getData()));
                    }
                });
                servicesCacheMap.put(servicePath, handlersCache);
            }
        });
        servicesCacheMap.put(ROOT_PATH_SERVICES, serviceCache);
    }

    @Override
    public void afterPropertiesSet()  {
        curatorFramework = curatorFramework.usingNamespace("services-namespace");
        //curatorFramework.sync();

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
        for (Map.Entry<String, PathChildrenCache> entry : servicesCacheMap.entrySet()) {
            System.out.println("close ..... " + entry.getKey() );
            entry.getValue().close();
        }
        servicesMap.clear();
    }
}
