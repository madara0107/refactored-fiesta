package com.nowcoder;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.dao.MessageDAO;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.*;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ToutiaoApplicationTests {

    @Autowired
    UserDAO userDAO;

    @Autowired
    NewsDAO newsDAO;

    @Autowired
    UserService userService;

    @Autowired
    NewsService newsService;

    @Autowired
    CommentDAO commentDAO;

    @Autowired
    MessageDAO messageDAO;

    @Autowired
    HostHolder hostHolder;


    @Test
    public void contextLoads() {
        Random random = new Random();
        for (int i = 0; i < 10; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i+1));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);
        }

    }

    @Test
    public void testNews() {
        for (int i = 0; i < 10; i++) {
            News news = new News();
            news.setUserId(i + 1);
            news.setLikeCount(i + 1);
            news.setCommentCount(i + 1);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", new Random().nextInt(1000)));
            news.setCreatedDate(new Date());
            news.setLink(String.format("www.nowcoder.com/%d", i+1));
            news.setTitle("title" + String.valueOf(i+1));
            newsDAO.addNews(news);

            for(int j=0; j<3; j++) {
                Comment comment = new Comment();
                comment.setCreatedDate(new Date());
                comment.setContent("comment" + String.valueOf(i));
                comment.setUserId(i + 1);
                comment.setEntityId(news.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);
                comment.setStatus(0);
                commentDAO.addComment(comment);
            }
        }

    }

    @Test
    public void testAddNews() {
        List<News> newsList = newsService.getLastestNews(0, 0, 10);

        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            vos.add(vo);
        }
    }

    @Test
    public void testConversation() {
        List<Message> messageList = messageDAO.getConversationList(hostHolder.getUser().getId(), 0, 10);
        for (Message msg : messageList) {
            System.out.println(msg.getFromId());
        }

    }

    @Test
    public void addNews(){
        for (int i = 10; i < 15; i++) {
            News news = new News();
            news.setUserId(i + 1);
            news.setLikeCount(1);
            news.setCommentCount(3);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", new Random().nextInt(1000)));
            news.setCreatedDate(new Date());
            news.setLink(String.format("www.nowcoder.com/%d", i+1));
            news.setTitle("title" + String.valueOf(i+1));
            newsDAO.addNews(news);

            for(int j=0; j<3; j++) {
                Comment comment = new Comment();
                comment.setCreatedDate(new Date());
                comment.setContent("this is comment" + String.valueOf(i));
                comment.setUserId(i + 1);
                comment.setEntityId(news.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);
                comment.setStatus(0);
                commentDAO.addComment(comment);
            }
        }
    }

    @Test
    public void addUsers(){
        Random random = new Random();
        for (int i = 12; i < 15; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("https://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i+1));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);
        }
    }
}
