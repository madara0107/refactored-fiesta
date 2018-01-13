package com.nowcoder.service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by snow on 2017/12/25.
 */
@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    public int addMessage(Message message){
        return messageDAO.addMessage(message);
    }

    public List<Message> getConversationDetail(String id, int offset, int limit){
        return messageDAO.getConversationDetail(id, offset, limit);
    }

    public List<Message> getConversationList(int id, int offset, int limit){
        return messageDAO.getConversationList(id, offset, limit);
    }

    public int getUnreadCount(int userId, String conversationId){
        return messageDAO.getUnreadCount(userId, conversationId);
    }


}
