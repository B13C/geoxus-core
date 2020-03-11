package com.geoxus.core.common.event;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

public abstract class GXBaseEvent<T> extends ApplicationEvent implements ResolvableTypeProvider {
    @GXFieldCommentAnnotation(zh = "附加参数")
    protected transient Dict param;

    @GXFieldCommentAnnotation(zh = "场景值,用于区分同一个事件的不同使用场景")
    protected transient Object scene;

    public GXBaseEvent(T source, Dict param) {
        this(source, param, "");
    }

    public GXBaseEvent(T source, Dict param, Object scene) {
        super(source);
        this.param = param;
        this.scene = scene;
    }

    public Dict getParam() {
        return param;
    }

    public Object getScene() {
        return scene;
    }

    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(getSource()));
    }
}
