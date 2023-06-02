package com.linrushao.businessserver.controller;

import com.linrushao.businessserver.entity.User;
import com.linrushao.businessserver.entity.form.UserLoginForm;
import com.linrushao.businessserver.service.UserService;
import com.linrushao.businessserver.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * @Author LRS
 * @Date 2022/9/26 17:19
 * Desc 用户登录层控制层
 */
@RestController
@RequestMapping("/user")
public class UserLoginControler extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping("/login")
    public JsonResult<User> login(@RequestParam("username") String username,
                                  @RequestParam("password") String password,
                                  HttpSession session) {
        // 调用业务对象的方法执行登录，并获取返回值
        User data = userService.loginUser(new UserLoginForm(username, password));
        if (null == data) {
            JsonResult<User> userJsonResult = new JsonResult<>();
            userJsonResult.setState(500);
            userJsonResult.setMessage("用户不存在或密码错误");
            // 将以上返回值和状态码OK封装到响应结果中并返回
            return userJsonResult;
        }
        if (null == username || username.isEmpty() || null == password || password.isEmpty()) {
            JsonResult<User> userJsonResult = new JsonResult<>();
            userJsonResult.setState(400);
            userJsonResult.setMessage("用户名或密码为空");
            return userJsonResult;
        }
        //登录成功后，将uid和username存入到HttpSession中
        session.setAttribute("uid", data.getUid());
        session.setAttribute("username", data.getUsername());
//         System.out.println("Session中的uid=" + getUidFromSession(session));
//         System.out.println("Session中的username=" + getUsernameFromSession(session));
        // 将以上返回值和状态码OK封装到响应结果中并返回
        return new JsonResult<>(OK, data);
    }

    @GetMapping("/queryUser")
    public JsonResult<User> queryUserByUid(HttpSession session) {
        Integer uid = getUidFromSession(session);

        User user = userService.findByUID(uid);

        //将用户名、id 进行回传
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setUid(user.getUid());
        newUser.setFirst(user.isFirst());
        return new JsonResult<>(OK, newUser);
    }

    //处理用户退出登录的请求
    @GetMapping("/exit")
    public JsonResult<Void> exitUserLoginStatus(HttpSession session){
        session.removeAttribute("username");
        session.removeAttribute("uid");
        return new JsonResult<>(OK);
    }

    //冷启动问题
    @RequestMapping( "/prefgenres")
    @ResponseBody
    public JsonResult<Void> addPrefGenres(HttpSession session, @RequestParam("genres[]") String[] genres ) {
        Object username = session.getAttribute("username");
        User user = userService.findByUsername(String.valueOf(username));
        ArrayList<String> genresList = Arraytransformed(genres);
//        System.out.println(Arrays.toString(genres));
//        user.getPrefGenres().addAll(Arrays.asList(genres.split(",")));
        user.getPrefGenres().addAll(genresList);
        user.setFirst(false);
        if(!userService.updateUser(user)){
            JsonResult<Void> jsonResult = new JsonResult<>();
            jsonResult.setState(400);
            return jsonResult;
        }
        JsonResult<Void> jsonResult = new JsonResult<>();
        jsonResult.setState(200);
        return jsonResult;
    }

    /**
     * 将前端传输过来的类别数组进行转换，转为列表返回
     * @param genres
     * @return
     */
    private ArrayList<String> Arraytransformed(String[] genres) {

        ArrayList<String> genresList = new ArrayList<>();
        for(String genre : genres){
            genresList.add(genre);
        }

        return genresList;
    }


}
