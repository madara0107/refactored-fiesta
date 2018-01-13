package com.nowcoder.util;

import java.util.Stack;

/**
 * Created by snow on 2018/1/2.
 */
public class RedisKeysUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String DIZ_LIKE = "DISLIKE";
    private static String BIZ_EVENT = "EVENT";

    public static String getLikeKeys(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDislikeKeys(int entityType, int entityId) {
        return DIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENT;
    }
}
