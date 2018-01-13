package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.async.handler.LoginExceptionHandler;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.catalina.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by snow on 2017/12/18.
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = "/reg/", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "rember",defaultValue = "0") int rememberme,
                           HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.register(username, password);

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0){
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "注册成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        } catch (Exception e) {
            logger.error("注册异常:" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "注册异常");
        }
    }

    @RequestMapping(path = "/login/", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rember",defaultValue = "0") int rememberme,
                        HttpServletResponse response) {
        try {
            Map<String, Object> map = userService.login(username, password);

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                if (rememberme > 0){
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                cookie.setPath("/");
                response.addCookie(cookie);

                EventModel mod = new EventModel(EventType.LOGIN);
                mod.setExt("to", "wjhr123456789@163.com").setActorId((int)map.get("userId")).setExt("username", "牛客");
                System.out.println(mod.getExt("to"));
                eventProducer.fireEvent(mod);

                return ToutiaoUtil.getJSONString(0, "登录成功");
            } else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        } catch (Exception e) {
            logger.error("登录异常:" + e.getMessage());
            return ToutiaoUtil.getJSONString(1,"登录异常");
        }
    }

    @RequestMapping(path = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }
}
