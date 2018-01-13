package com.nowcoder.controller;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.ToutiaoUtil;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import org.apache.catalina.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by snow on 2017/12/21.
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/news/{newsId}", method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
        try {
            if (hostHolder.getUser() == null) {
                return "redirect:/?pop=1";
            }

            News news = newsService.getNewsById(newsId);
            int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;

            if (news != null) {
                List<Comment> comments = commentService.getCommentByEntity(news.getId(), EntityType.ENTITY_NEWS);
                List<ViewObject> commentVOs = new ArrayList<ViewObject>();
                for (Comment comment : comments) {
                    ViewObject commentVO = new ViewObject();
                    commentVO.set("comment", comment);
                    commentVO.set("user", userService.getUserById(news.getUserId()));
                    commentVOs.add(commentVO);
                }
                if (hostHolder.getUser() != null){
                    model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
                }else {
                    model.addAttribute("like", 0);
                }
                model.addAttribute("comments", commentVOs);
            }


            model.addAttribute("owner", userService.getUserById(news.getUserId()));
            model.addAttribute("news", news);
        } catch (Exception e) {
            logger.error("获取详情失败" + e.getMessage());
        }

        return "detail";
    }

    @RequestMapping(path = "/addComment", method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setCreatedDate(new Date());
            comment.setUserId(hostHolder.getUser().getId());
            comment.setStatus(0);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setContent(content);
            commentService.addComment(comment);

            int count = commentService.getCommentCount(newsId, EntityType.ENTITY_NEWS);
            newsService.updateCommentCount(comment.getEntityId(), count);
            return ToutiaoUtil.getJSONString(0, "添加成功");
        } catch (Exception e) {
            logger.error("添加失败" + e.getMessage());
        }

        return "redirect:/news/" + String.valueOf(newsId);
    }

    @RequestMapping(path = "/image/", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String name,
                         HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(ToutiaoUtil.IMAGE_DIR + name), response.getOutputStream());

        } catch (Exception e) {
            logger.error("读取图片异常：" + name + e.getMessage());
        }

    }

    @RequestMapping(path = "/uploadImage/", method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            //String fileUrl = newsService.saveImage(file);
            String fileUrl = qiniuService.saveImage(file);
            if (fileUrl == null) {
                return ToutiaoUtil.getJSONString(1, "上传失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("上传异常：" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传异常");
        }

    }

    @RequestMapping(path = "/user/addNews/", method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("image") String image,
                              @RequestParam("title") String title,
                              @RequestParam("link") String link) {

        try {
            News news = new News();
            news.setCreatedDate(new Date());
            news.setImage(image);
            news.setTitle(title);
            news.setLink(link);
            if (hostHolder.getUser() != null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                news.setUserId(10);
            }
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0,"添加成功");
        } catch (Exception e) {
            logger.error("添加异常"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"添加失败");
        }
    }

}
