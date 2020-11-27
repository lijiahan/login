package com.dataService.service;

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

    private final static String LOGIN_PATH_SERVICES = "/services/login";


    @Autowired
    private CuratorFramework curatorFramework;

    public LoginServiceImp() {
        System.out.println("LoginService.....");
    }

    @Override
    public void afterPropertiesSet()  {
        curatorFramework = curatorFramework.usingNamespace("services-namespace");

        try {
            if(null == curatorFramework.checkExists().forPath(LOGIN_PATH_SERVICES)) {
                logger.info("create path services .......!!!");
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(LOGIN_PATH_SERVICES, "111".getBytes());
            }

            InetAddress ip4 = Inet4Address.getLocalHost();
            String local_ip = ip4.getHostAddress();
            String handler_address = local_ip + ":9020" + LOGIN_PATH_SERVICES;

            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(LOGIN_PATH_SERVICES + "/" + String.valueOf(System.currentTimeMillis()), handler_address.getBytes());


            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(LOGIN_PATH_SERVICES + "/" + String.valueOf(System.currentTimeMillis()), handler_address.getBytes());

        }catch (Exception e) {
            logger.error("connect zookeeper fail，please check the log >> {}", e.getMessage());
        }
    }
}
