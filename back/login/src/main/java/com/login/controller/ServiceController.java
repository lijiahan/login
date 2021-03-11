package com.login.controller;

import com.login.entity.CmdMessage;
import com.login.service.DispatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(allowCredentials="true",allowedHeaders="*",maxAge = 3600)
@Api(value = "用户服务",description = "用户的基本操作")
public class ServiceController {
    @Autowired
    private DispatchService dispatchService;
    @PostMapping(value = "/service")
    public String service(@RequestBody CmdMessage cmd) {
        return dispatchService.dispatch(cmd);
    }
    @ApiOperation(value = "服务",notes = "服务")
    @GetMapping(value = "/services")
    public String service() {
        CmdMessage cmd = new CmdMessage("0", "/login", "login");
        return dispatchService.dispatch(cmd);
    }
}
