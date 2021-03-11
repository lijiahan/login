package com.dataService.service;

import com.alibaba.fastjson.JSON;
import com.dataService.entity.HandlerInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;

@Service("LoginService")
public class LoginServiceImp implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImp.class);

    private final static String ROOT_PATH_SERVICES = "/services";
    private final static String LOGIN_SERVICE = "/login";
    private final static String LOGIN_SERVICE_NAME = "/service_1";


    @Autowired
    private CuratorFramework curatorFramework;

    public LoginServiceImp() {
        System.out.println("LoginService.....");
    }

    @Override
    public void afterPropertiesSet()  {
        curatorFramework = curatorFramework.usingNamespace("services-namespace");
        String pathService = ROOT_PATH_SERVICES + LOGIN_SERVICE;
        try {
            if(null == curatorFramework.checkExists().forPath(pathService)) {
                logger.info("create path services .......!!!");
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(pathService, LOGIN_SERVICE.getBytes());
            }
            constructService();
        }catch (Exception e) {
            logger.error("connect zookeeper fail，please check the log >> {}", e.getMessage());
        }
    }

    private void constructService() throws Exception {
        String pathService = ROOT_PATH_SERVICES + LOGIN_SERVICE;
        InetAddress ip4 = Inet4Address.getLocalHost();
        String local_ip = ip4.getHostAddress();
        HandlerInfo handlerInfo = new HandlerInfo();
        handlerInfo.setName(LOGIN_SERVICE_NAME);
        handlerInfo.setUrl(local_ip + ":9020" + pathService);


        curatorFramework.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(pathService + LOGIN_SERVICE_NAME, JSON.toJSONBytes(handlerInfo));
    }
}
