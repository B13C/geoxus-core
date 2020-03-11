package com.geoxus.core.common.listener;

import com.geoxus.core.common.event.GXBaseEvent;
import org.springframework.context.ApplicationListener;

public interface GXBaseListener<E extends GXBaseEvent<?>> extends ApplicationListener<E> {
    /**
     * 监听器  需要标记上@EventListener注解
     *
     * @param event 事件类型
     */
    void listen(E event);

    /**
     * 默认实现
     *
     * @param event 事件类型
     */
    default void onApplicationEvent(E event) {
    }
}
