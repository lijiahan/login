package com.login.service;

import com.login.entity.CmdMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DispatchService {
    @Autowired
    private SecurityManager securityManager;

    public String dispatch(CmdMsg cmdMsg) {

        return "";
    }
}
