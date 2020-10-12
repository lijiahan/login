package com.login.controller;

import com.login.entity.JsonResult;
import com.login.entity.LoginInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/User")
@CrossOrigin(allowCredentials="true",allowedHeaders="*",maxAge = 3600)
public class UserController {
    @PostMapping(value = "/Login")
    public JsonResult loginUser(@RequestBody LoginInfo user, HttpSession session) {
        System.out.println("start login......." + user.toString());
        if (StringUtils.isEmpty(user.getName()) || StringUtils.isEmpty(user.getPassword())) {
            return new JsonResult(-1, "用户名或密码不能为空");
        }

        return  new JsonResult(0, "登录成功");
    }
}
