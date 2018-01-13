package com.nowcoder.service;

import com.nowcoder.controller.LoginController;
import com.nowcoder.util.ToutiaoUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by snow on 2017/12/21.
 */
@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);

    //构造一个带指定Zone对象的配置类
    Configuration cfg = new Configuration(Zone.zone1());
//...其他参数参考类注释

    UploadManager uploadManager = new UploadManager(cfg);
    //...生成上传凭证，然后准备上传
    String accessKey = "fLQ4drg7lbtqYcVBT3yZc0NPHBWtwI781Y5IG6MG";
    String secretKey = "y44w6EvSVDM16GsMDi4WmiC7KKqteKLI8O64ueOz";
    String bucket = "test";


    Auth auth = Auth.create(accessKey, secretKey);
    String upToken = auth.uploadToken(bucket);
    public static String QINIU_DOMAIN = "http://oztl9a0fs.bkt.clouddn.com/";
    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtil.isAllowed(fileExt)) {
                return null;
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + "." + fileExt;
            Response res = uploadManager.put(file.getBytes(), fileName, upToken);
            if (res.isJson() && res.isOK()) {
                return QINIU_DOMAIN + fileName;
            } else {
                logger.error("七牛异常" + res.bodyString());
                return null;
            }
        } catch (QiniuException ex) {
            logger.error("七牛异常：" + ex.getMessage());
            return null;
        }
    }

}
