package com.dataService.service;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        curatorFramework.sync();

        try {
            if(null == curatorFramework.checkExists().forPath(LOGIN_PATH_SERVICES)) {
                logger.info("create path services .......!!!");
                curatorFramework.create().forPath(LOGIN_PATH_SERVICES);
                System.out.println(curatorFramework.getChildren().forPath("/"));
            }


        }catch (Exception e) {
            logger.error("connect zookeeper fail，please check the log >> {}", e.getMessage(), e);
        }
    }
}
