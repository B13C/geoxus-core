package com.geoxus.core.common.event;

import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import org.springframework.context.ApplicationEvent;

public abstract class GXBaseEvent<T> extends ApplicationEvent {
    @GXFieldCommentAnnotation(zh = "附加参数")
    protected transient T param;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public GXBaseEvent(Object source, T param) {
        super(source);
        this.param = param;
    }

    public T getParam() {
        return param;
    }
}
