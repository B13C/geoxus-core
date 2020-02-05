package com.geoxus.core.common.listener;

import com.geoxus.core.common.util.GXAsyncEventBusCenterUtils;
import com.google.common.eventbus.EventBus;

public class GXAsyncBaseListener {
    protected static final EventBus ASYNC_EVENT_BUS;

    static {
        ASYNC_EVENT_BUS = GXAsyncEventBusCenterUtils.getInstance();
    }

    public GXAsyncBaseListener() {
        ASYNC_EVENT_BUS.register(this);
    }
}
