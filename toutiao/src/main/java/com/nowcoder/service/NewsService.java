package com.nowcoder.service;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Created by snow on 2017/12/18.
 */
@Service
public class NewsService {
    @Autowired
    NewsDAO newsDAO;

    public List<News> getLastestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public News getNewsById(int newsId){
        return newsDAO.getNewsById(newsId);
    }


    public String saveImage(MultipartFile file) throws IOException {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!ToutiaoUtil.isAllowed(fileExt)) {
            return null;
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + fileExt;
        Files.copy(file.getInputStream(),new File(ToutiaoUtil.IMAGE_DIR+fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;

    }

    public int updateCommentCount(int id, int commentCount){
        return newsDAO.updateCommentCount(id, commentCount);
    }

    public int updateLikeCount(int id, int likeCount){
        return newsDAO.updateLikeCount(id, likeCount);
    }

    public void addNews(News news){
        newsDAO.addNews(news);
    }
}
