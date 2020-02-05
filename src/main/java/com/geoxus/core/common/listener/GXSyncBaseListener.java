package com.geoxus.core.common.listener;

import com.geoxus.core.common.util.GXSyncEventBusCenterUtils;
import com.google.common.eventbus.EventBus;

public class GXSyncBaseListener {
    protected static final EventBus EVENT_BUS;

    static {
        EVENT_BUS = GXSyncEventBusCenterUtils.getInstance();
    }

    public GXSyncBaseListener() {
        EVENT_BUS.register(this);
    }
}
