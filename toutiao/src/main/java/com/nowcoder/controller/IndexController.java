package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by snow on 2017/12/16.
 */
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);



    @RequestMapping("/profile/{groupId}/{userId}")
    @ResponseBody
    public String profile(@PathVariable("groupId") int groupId,
                          @PathVariable("userId") int userId,
                          @RequestParam("key") String key,
                          @RequestParam("type") String type) {
        return String.format("groupID:%d userId:%d key:%s type:%s", groupId, userId, key, type);
    }

    @RequestMapping("/ftl")
    public String freeMarker(Model model) {
        List<String> colors = Arrays.asList(new String[]{"red", "green", "yellow", "blue"});
        model.addAttribute("colors", colors);

        Map<String, String> maps = new HashMap<String, String>();
        for (int i = 0; i < 5; i++) {
            maps.put(String.valueOf(i), String.valueOf(i * i));
        }
        model.addAttribute("maps", maps);
        String title = "首页";
        model.addAttribute("title", title);
        return "test";
    }

    @RequestMapping("/request")
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            sb.append(headerNames.nextElement() + "<br>");
        }

        for (Cookie cookie : request.getCookies()) {
            sb.append("CookieName:");
            sb.append(cookie.getName() + "<br>");
            sb.append("cookieValue:");
            sb.append(cookie.getValue() + "<br>");
        }
        sb.append("getMethod:" + request.getMethod() + "<br>");
        sb.append("getPathInfo:" + request.getPathInfo() + "<br>");
        sb.append("getQueryString:" + request.getQueryString() + "<br>");
        sb.append("getRequestURI:" + request.getRequestURI() + "<br>");
        return sb.toString();
    }

    @RequestMapping("/response")
    @ResponseBody
    public String response(@CookieValue(value = "userId", defaultValue = "0") int userId,
                           @RequestParam(value = "key", defaultValue = "key") String key,
                           @RequestParam(value = "value", defaultValue = "value") String value,
                           HttpServletResponse response) {
        response.addCookie(new Cookie(key, value));
        response.addHeader(key, value);

        return "userId from:" + userId;
    }

    @RequestMapping("/redirect/{code}")
    public RedirectView redirectView(@PathVariable("code") int code){
        RedirectView red = new RedirectView("/", true);
        if (code == 301) {
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }

    @RequestMapping("/setting")
    @ResponseBody
    public String setting(){
        return "setting ok";
    }

}
