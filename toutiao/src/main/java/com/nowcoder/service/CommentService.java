package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by snow on 2017/12/24.
 */
@Service
public class CommentService {

    @Autowired
    CommentDAO commentDAO;

    public List<Comment> getCommentByEntity(int entityId, int entityType) {
        return commentDAO.selectByEntity(entityId, entityType);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }
}
