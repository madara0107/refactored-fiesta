package com.nowcoder.async;


import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeysUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by snow on 2018/1/6.
 */
@Service
public class EventProducer {



    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel model){
        try {
            String json = JSONObject.toJSONString(model);
            String key = RedisKeysUtil.getEventQueueKey();
            jedisAdapter.lpush(key,json);
            return true;
        } catch (Exception e) {

            return false;
        }
    }
}
