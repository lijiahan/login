package com.login.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class ServicesManager implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ServicesManager.class);
    private final static String ROOT_PATH_SERVICES = "/services";
    private Map<String, ArrayList<IHandler>> servicesMap;

    @Autowired
    private CuratorFramework curatorFramework;

    @Autowired
    private RestTemplate restTemplate;

    private Map<String, PathChildrenCache> servicesCacheMap;


    private String getServiceFromSPath(String path) {
        String [] strArray = path.split("/|\\\\");
        if (strArray.length > 1) {
            return strArray[1];
        }
        return "";
    }

    private String getHandlerFromHPath(String path) {
        String [] strArray = path.split("/|\\\\");
        if (strArray.length > 3) {
            return strArray[3];
        }
        return "";
    }

    private IHandler constructHandler(String serviceName, String hPath, String hAddress) {
        IHandler handler = null;
        switch (serviceName) {
            default:
                handler = new CommonHandler();
                handler.build(getHandlerFromHPath(hPath), hAddress);
        }
        return handler;
    }

    private void releaseHandler(String hPath) {
        ArrayList<IHandler> handlersList = servicesMap.get(getServiceFromSPath(hPath));
        String hName = getHandlerFromHPath(hPath);
        Iterator<IHandler> iterator = handlersList.iterator();
        while(iterator.hasNext()) {
            IHandler handler = iterator.next();
            if (handler.release(hName)) {
                System.out.println("release ....... : " + hPath + "....." + hName);
                iterator.remove();
                break;
            }
        }
    }

    private void constructServices() throws Exception {
        servicesMap = new HashMap<>();
        servicesCacheMap = new HashMap<>();

        PathChildrenCache serviceCache = new PathChildrenCache(curatorFramework, ROOT_PATH_SERVICES, true);
        serviceCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        serviceCache.getListenable().addListener((client, event) -> {
            System.out.println("/services... type: " +  event.getType());
            if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                if (null != event.getData()) {
                    String servicePath = event.getData().getPath();
                    PathChildrenCache handlersCache = new PathChildrenCache(curatorFramework, servicePath, true);
                    handlersCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
                    handlersCache.getListenable().addListener((client_hd, event_hd) -> {
                        System.out.println("/services/handler event type: " +  event_hd.getType());
                        if(null != event_hd.getData()) {
                            String handlerPath = event_hd.getData().getPath();
                            String handlerAddress = new String(event_hd.getData().getData());
                            System.out.println("handler path: " + handlerPath + " data: " + handlerAddress);
                            if (event_hd.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                                String serviceName = getServiceFromSPath(handlerPath);
                                ArrayList<IHandler> handlersList = servicesMap.get(serviceName);
                                handlersList.add(constructHandler(serviceName, handlerPath, handlerAddress));
                            }
                            else if (event_hd.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                                releaseHandler(handlerPath);
                            }
                        }
                    });
                    servicesCacheMap.put(servicePath, handlersCache);
                    String serviceName = getServiceFromSPath(servicePath);
                    if (null == servicesMap.get(serviceName)) {
                        servicesMap.put(serviceName, new ArrayList<>());
                    }
                }
                else {
                    logger.warn("/services ...... data is null");
                }
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
