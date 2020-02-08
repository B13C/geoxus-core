package com.geoxus.core.common.util;

import com.geoxus.core.common.exception.GXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class GXUnsafeUtils {
    private static final Logger log = LoggerFactory.getLogger(GXUnsafeUtils.class);

    private static final Unsafe unsafe;

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

    private GXUnsafeUtils() {
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }
}