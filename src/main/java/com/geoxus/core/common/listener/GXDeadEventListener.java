package com.geoxus.core.common.listener;

import cn.hutool.core.util.StrUtil;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@SuppressWarnings("unused")
public class GXDeadEventListener {
    boolean notDelivered = false;

    @Subscribe
    public void listen(DeadEvent event) {
        notDelivered = true;
        log.info(StrUtil.format("事件{}没有对应的监听器对其进行处理", event.getClass().getName()));
    }

    public boolean isNotDelivered() {
        return notDelivered;
    }
}