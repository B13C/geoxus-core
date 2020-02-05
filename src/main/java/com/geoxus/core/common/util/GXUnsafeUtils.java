package com.geoxus.core.common.util;

import com.geoxus.core.common.exception.GXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class GXUnsafeUtils {
    private static final Logger log = LoggerFactory.getLogger(GXHttpContextUtils.class);

    private static final Unsafe unsafe;

    private GXUnsafeUtils() {
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    static {
        Field f;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage(), e);
            throw new GXException(e.getMessage());
        }

        f.setAccessible(true);

        try {
            unsafe = (Unsafe) f.get(null);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
            throw new GXException(e.getMessage());
        }
    }
}