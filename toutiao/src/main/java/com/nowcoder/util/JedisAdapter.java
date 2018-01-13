package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import com.nowcoder.aspect.LogAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

/**
 * Created by snow on 2017/12/29.
 */
@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);


    public static void print(int index, Object object) {
        System.out.println(String.format("%d %s", index, object.toString()));
    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "snow");
        jedis.setex("tt", 10, "vv");

        jedis.set("pv", "10");
        jedis.incr("pv");
        print(2, jedis.get("pv"));

        //List
        String listName = "listA";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, String.format("a%d", i));
        }
        print(3, jedis.lrange(listName, 0, 12));
        print(4, jedis.lindex(listName, 4));
        print(5, jedis.llen(listName));
        print(6, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a2", "before"));
        print(7, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a2", "after"));
        print(8, jedis.lrange(listName, 0, 12));
        print(9, jedis.lpop(listName));
        print(8, jedis.lrange(listName, 0, 12));

        //Hash
        String userKey = "user";
        jedis.hset(userKey, "age", "12");
        print(9, jedis.hget(userKey, "age"));
        print(10, jedis.hexists(userKey, "age"));
        print(11, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "age");
        print(12, jedis.hgetAll(userKey));
        jedis.hset(userKey, "age", "12");
        jedis.hincrBy(userKey, "age", 13);
        print(13, jedis.hgetAll(userKey));
        jedis.hsetnx(userKey, "age", "11");
        print(14, jedis.hgetAll(userKey));

        jedis.hincrByFloat(userKey, "age", 1.23);
        print(15, jedis.hgetAll(userKey));

        jedis.hset(userKey, "name", "jojo");
        print(15, jedis.hkeys(userKey));
        print(16, jedis.hvals(userKey));

        print(17, jedis.hlen(userKey));

        jedis.hset(userKey, "school name", "jj");
        print(18, jedis.hmget(userKey, "school name", "age"));

        String rank = "zRank";
        jedis.zadd(rank, 100, "jojo");
        jedis.zadd(rank, 90, "saber");
        jedis.zadd(rank, 80, "caster");
        jedis.zadd(rank, 10, "lancer");
        print(19, jedis.zrange(rank, 0, 10));
        print(20, jedis.zscore(rank, "saber"));
        print(21, jedis.zrank(rank, "jojo"));
        print(22, jedis.zrevrank(rank, "jojo"));

        print(23, jedis.zcount(rank, 10, 80));
        print(24, jedis.zcard(rank));

        String midTest = "mid";
        String finTest = "fin";
        String sumTest = "sum";
        jedis.zadd(midTest, 80, "li");
        jedis.zadd(midTest, 23, "ming");
        jedis.zadd(midTest, 12, "chen");

        jedis.zadd(finTest, 10, "li");
        jedis.zadd(finTest, 10, "ming");
        jedis.zadd(finTest, 10, "chen");

        print(25, jedis.zinterstore(sumTest, midTest, finTest));
        print(26, jedis.zscore(sumTest, "li"));

        for (Tuple tuple : jedis.zrangeByScoreWithScores(rank, 0, 100)) {
            System.out.println(tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis jd = pool.getResource();
            System.out.println("a" + i);
            jd.close();
        }

        String setKey = "set";

        jedis.sadd(setKey, "11", "12");
        print(27, jedis.sismember(setKey, "12"));


    }

    private Jedis jedis = null;
    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool();
    }

    private Jedis getJedis() {
        return pool.getResource();
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("获取jedis失败");
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("获取jedis失败");
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("获取jedis失败");
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("获取jedis失败");
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setex(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.setex(key, 10, value);
        } catch (Exception e) {
            logger.error("获取jedis失败");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("设置异常：" + e.getMessage());
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("设置异常：" + e.getMessage());
            return null;
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setObject(String key, Object obj) {
        set(key, JSON.toJSONString(obj));
    }

    public <T> T getObject(String key, Class<T> clazz){
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value,clazz);
        }
        return null;
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout,key);
        } catch (Exception e) {
            logger.error("获取异常：" + e.getMessage());
            return null;
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long lpush(String key, String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("获取异常：" + e.getMessage());
            return 0;
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
