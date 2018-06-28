package com.hzu.zao.annotation;

import android.app.Activity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 实例化对象，使用注释的形式
 * Created by  on 2015/10/11.
 */
public class Injector {

    public static void inject(Activity  mActivity) {
        for (Field field : mActivity.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().equals(InjectView.class)) {
                    try {
                        Class<?> fieldType = field.getType();
                        int idValue = InjectView.class.cast(annotation).value();
                        field.setAccessible(true);
                        Object injectedValue = fieldType.cast(mActivity.findViewById(idValue));
                        if (injectedValue == null) {
                            throw new IllegalStateException("findViewById(" + idValue
                                    + ") gave null for " +
                                    field + ", can't inject");
                        }
                        field.set(mActivity, injectedValue);
                        field.setAccessible(false);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
    }
}
