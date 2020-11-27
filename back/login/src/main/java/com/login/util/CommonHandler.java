package com.login.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class CommonHandler implements IHandler {

    private String handlerName;
    private String handlerURl;

    @Autowired
    private RestTemplate restTemplate;

    public void build(String hName, String hAddress) {
        this.handlerName = hName;
        this.handlerURl =  "http://" + hAddress;;
    }

    public String handler(String req) {
        return restTemplate.getForObject(handlerURl, String.class);
    }

    public boolean release(String hName) {
        if (this.handlerName.equals(hName)) {
            return true;
        }

        return false;
    }
}
