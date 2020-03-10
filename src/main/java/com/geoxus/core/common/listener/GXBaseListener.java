package com.geoxus.core.common.listener;

import com.geoxus.core.common.event.GXBaseEvent;
import org.springframework.context.ApplicationListener;

public abstract class GXBaseListener<E extends GXBaseEvent<T>, T> implements ApplicationListener<E> {
}
