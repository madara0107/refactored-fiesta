package com.nowcoder.util;

import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Map;
import java.util.Properties;

/**
 * Created by snow on 2018/1/7.
 */
@Service
public class MailSender implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private JavaMailSenderImpl mailSender;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    public boolean sendWithHTMLTemplate(String to, String subject,
                                        String template, Map<String, Object> model) {
        try {
            String nick = MimeUtility.encodeText("头条资讯");
            InternetAddress from = new InternetAddress(nick + "<1537665423@qq.com>");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            Template tmp = freeMarkerConfigurer.getConfiguration().getTemplate(template);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(tmp, model);

            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject(subject);
            mailSender.send(mimeMessage);
            return true;

        } catch (Exception e) {
            logger.error("发送异常" + e.getMessage());
            return false;
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();

        mailSender.setUsername("1537665423@qq.com");
        mailSender.setPassword("fhykbttkiatqhbjc");
        mailSender.setHost("smtp.qq.com");

        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);


    }
}
