package com.dataService.controller;

import com.dataService.entity.JsonResult;
import com.dataService.entity.LoginInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/services")
@CrossOrigin(allowCredentials="true",allowedHeaders="*",maxAge = 3600)
public class UserController {
    @RequestMapping(value="/login", method = RequestMethod.GET)
    public JsonResult login() {
        return new JsonResult(0, "登录成功");
    }
}