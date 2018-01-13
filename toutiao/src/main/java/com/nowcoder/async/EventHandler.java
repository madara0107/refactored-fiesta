package com.nowcoder.async;

import java.util.List;

/**
 * Created by snow on 2018/1/5.
 */
public interface EventHandler {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();

}
