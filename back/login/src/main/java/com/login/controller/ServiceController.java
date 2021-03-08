package com.login.controller;

import com.login.entity.CmdMsg;
import com.login.service.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(allowCredentials="true",allowedHeaders="*",maxAge = 3600)
public class ServiceController {
    @Autowired
    private DispatchService dispatchService;
    @PostMapping(value = "/service")
    public String service(@RequestBody CmdMsg cmd) {
        return dispatchService.dispatch(cmd);
    }
}
