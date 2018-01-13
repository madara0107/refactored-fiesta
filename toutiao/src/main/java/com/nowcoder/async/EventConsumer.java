package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.controller.IndexController;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeysUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by snow on 2018/1/6.
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    JedisAdapter jedisAdapter;

    Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();


    @Autowired
    ApplicationContext applicationContext;

    public EventConsumer() {
        super();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);

        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }

                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeysUtil.getEventQueueKey();
                    List<String> messages = jedisAdapter.brpop(0, key);
                    for (String message : messages) {
                        if (message.equals(key)) {
                            continue;
                        }

                        EventModel model = JSONObject.parseObject(message,EventModel.class);


                        if (!config.containsKey(model.getType())) {
                            logger.error("不支持的类型");
                            continue;
                        }

                        for(EventHandler handler : config.get(model.getType())){
                            handler.doHandle(model);
                        }
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
