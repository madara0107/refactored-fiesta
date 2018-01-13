package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.catalina.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by snow on 2017/12/25.
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/msg/detail", method = {RequestMethod.GET})
    public String messageDetail(Model model, @RequestParam("conversationId") String conversationId) {
        try {
            List<Message> messages = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messageVOs = new ArrayList<ViewObject>();
            for (Message msg : messages) {
                ViewObject messageVO = new ViewObject();
                messageVO.set("message", msg);
                User user = userService.getUserById(msg.getFromId());
                if (user == null) {
                    continue;
                }
                messageVO.set("headUrl", user.getHeadUrl());
                messageVO.set("userName", user.getName());
                messageVOs.add(messageVO);
            }
            model.addAttribute("messages", messageVOs);
            return "letterDetail";
        } catch (Exception e) {
            logger.error("获取详情失败" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = "/msg/list", method = {RequestMethod.GET})
    public String messageDetail(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> messageList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : messageList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                vo.set("targetId", targetId);
                User user = hostHolder.getUser();
                vo.set("user", user);
                vo.set("totalcount", msg.getId());
                vo.set("unreadCount", messageService.getUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
            return "letter";
        } catch (Exception e) {
            logger.error("获取列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping("/msg /addMessage")
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try {
            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(fromId);
            message.setToId(toId);
            message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(message);
            return ToutiaoUtil.getJSONString(0, "添加成功");
        } catch (Exception e) {
            logger.error("添加失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "添加失败");
        }
    }
}
