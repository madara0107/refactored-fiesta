package com.nowcoder.model;

import oracle.jrockit.jfr.StringConstantPool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by snow on 2017/12/18.
 */
public class ViewObject {
    private Map<String, Object> objs = new HashMap<String, Object>();

    public Object get(String key) {
        return objs.get(key);
    }

    public void set(String key, Object value) {
        objs.put(key, value);
    }
}
