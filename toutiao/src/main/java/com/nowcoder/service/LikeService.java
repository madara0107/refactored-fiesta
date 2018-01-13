package com.nowcoder.service;

import com.nowcoder.model.EntityType;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeysUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by snow on 2018/1/2.
 */
@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 获取用户对某类型的喜欢状态，喜欢为1， 不喜欢为-1， 否则为0
     * @param userId
     * @param entityType  可以表示资讯或评论
     * @param entityId    可以表示咨询或评论的id
     * @return
     */
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKeys = RedisKeysUtil.getLikeKeys(entityType, entityId);
        if (jedisAdapter.sismember(likeKeys, String.valueOf(userId))){
            return 1;
        }

        String dislikeKeys = RedisKeysUtil.getDislikeKeys(entityType, entityId);
        return jedisAdapter.sismember(dislikeKeys, String.valueOf(userId)) ? -1 : 0;
    }

    /**
     * 执行对某种类型（资讯或评论）的喜欢操作,将用户id添加进喜欢的集合中，从不喜欢的集合中删除该用户id。
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public long like(int userId, int entityType, int entityId) {
        String likeKeys = RedisKeysUtil.getLikeKeys(entityType, entityId);
        jedisAdapter.sadd(likeKeys,String.valueOf(userId));

        String dislikeKeys = RedisKeysUtil.getDislikeKeys(entityType, entityId);
        jedisAdapter.srem(dislikeKeys, String.valueOf(userId));

        return jedisAdapter.scard(likeKeys);
    }

    /**
     * 取消某个用户对某种类型（资讯或评论）的喜欢操作,将用户id添加进不喜欢的集合中，从喜欢的集合中删除该用户id。
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public long dislike(int userId, int entityType, int entityId) {
        String dislikeKeys = RedisKeysUtil.getDislikeKeys(entityType, entityId);
        jedisAdapter.sadd(dislikeKeys, String.valueOf(userId));

        String likeKeys = RedisKeysUtil.getLikeKeys(entityType, entityId);
        jedisAdapter.srem(likeKeys,String.valueOf(userId));

        return jedisAdapter.scard(likeKeys);
    }
}
