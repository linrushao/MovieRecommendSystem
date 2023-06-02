package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.form.UserRegisterForm;
import com.linrushao.businessserver.service.UserService;
import com.linrushao.businessserver.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author LRS
 * @Date 2022/9/21 9:14
 * Desc
 */

@RestController
@RequestMapping("/user")
public class UserRegisterController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 注册
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/register")
    public JsonResult<Void> addUser(@RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    @RequestParam("passwordAgain") String passwordAgain) {
        JsonResult<Void> userJsonResult = new JsonResult<>();
        if (userService.checkUserExist(username)) {
            userJsonResult.setState(400);
            userJsonResult.setMessage("用户名已经被注册");
            return userJsonResult;
        }
        if (null == username || username.isEmpty()) {
            userJsonResult.setState(400);
            userJsonResult.setMessage("用户名为空！！");
            return userJsonResult;
        }
        if (null == password || password.isEmpty() || null == passwordAgain || passwordAgain.isEmpty()) {
            userJsonResult.setState(400);
            userJsonResult.setMessage("密码为空！！");
            return userJsonResult;
        }
        if ( !password.equals(passwordAgain) ) {
            userJsonResult.setState(400);
            userJsonResult.setMessage("两次输入的密码不一致！");
            return userJsonResult;
        }
        userService.registerUser(new UserRegisterForm(username, password));
        userJsonResult.setState(200);
        userJsonResult.setMessage("成功注册");
        return userJsonResult;
    }
}

