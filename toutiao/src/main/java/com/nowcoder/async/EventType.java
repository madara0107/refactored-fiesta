package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by snow on 2018/1/5.
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(4);

    private int value;
    EventType(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }


}
