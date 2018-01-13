package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.ibatis.ognl.IntHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by snow on 2017/12/18.
 */
@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;
    HostHolder hostHolder;

    public User getUserById(int id) {
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String name, String password) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (name == "") {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (password == "") {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(name);
        if (user != null) {
            map.put("msgname", "用户名已存在");
            return map;
        }

        user = new User();
        user.setName(name);
        String head = String.format("https://image.nowcoder.com/head/%dm.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setSalt(UUID.randomUUID().toString().replace("-", "").substring(0, 5));
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, Object> login(String name, String password) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (name == "") {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (password == "") {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(name);
        if (user == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }

        if (!ToutiaoUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msgpwd", "密码错误");
            return map;
        }

        map.put("userId", user.getId());

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public String addLoginTicket(int id) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(id);
        loginTicket.setStatus(0);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24 * 5);
        loginTicket.setExpired(date);
        loginTicket.setTicket(UUID.randomUUID().toString().replace("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

}
