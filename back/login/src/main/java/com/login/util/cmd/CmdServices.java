package com.login.util.cmd;

import com.login.config.ZookeeperClient;
import com.login.entity.CmdMessage;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CmdServices implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(CmdServices.class);
    private final static String ROOT_PATH_SERVICES = "/services";
    private final static String NAMESPACE = "services-namespace";
    private ConcurrentHashMap<String, CmdServiceManager> servicesMap = new ConcurrentHashMap<>();
    private PathChildrenCache servicesCache;

    @Autowired
    private ZookeeperClient zookeeperClient;

    public CmdServices() {

    }

    public void init() {
        //
        if(!zookeeperClient.isExistNode(ROOT_PATH_SERVICES)) {
            zookeeperClient.createNode(CreateMode.PERSISTENT, ROOT_PATH_SERVICES);
        }
        //
        PathChildrenCacheListener cacheListener = (client, event) -> {
            if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                if (null != event.getData()) {
                    String servicePath = event.getData().getPath();
                    String serviceName = servicePath.replace(ROOT_PATH_SERVICES, "");
                    System.out.println("/services... servicePath: " +  servicePath + " serviceName: " + serviceName);
                    if (null == servicesMap.get(serviceName)) {
                        servicesMap.put(serviceName, new CmdServiceManager(servicePath, serviceName));
                    }
                }
                else {
                    logger.warn("/services ...... data is null");
                }
            }
        };
        servicesCache = zookeeperClient.watch(ROOT_PATH_SERVICES, cacheListener);
    }

    public String dispath(CmdMessage cmdMsg) {
       return servicesMap.get(cmdMsg.getCmd()).handler(cmdMsg.getData());
    }

    @Override
    public void afterPropertiesSet()  {
        try {
            zookeeperClient.usingNameSpace(NAMESPACE);
            init();
        }catch (Exception e) {
            logger.error("connect zookeeper fail，please check the log >> {}", e.getMessage(), e);
        }
    }

    @PreDestroy
    public void destory() throws Exception {
        logger.info("destory CmdServices .......!!!");
        servicesCache.close();
    }
}
