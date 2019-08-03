package com.yirun.framework.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.ClassUtils;

import com.yirun.framework.core.exception.GeneralException;

/**
 * @Description   : Spring工具类ClassUtils扩展 
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.ClassUtilsExtend.java
 * @Author        : imzhousong@gmail.com 周松
 */
public abstract class ClassUtilsExtend extends ClassUtils{

    /**
     * @Description:是否有注解
     */
    public static boolean hasClassAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return getClassAnnotation(clazz, annotationClass) != null;
    }

    /**
     * @Description:是否有注解
     */
    public static boolean hasFieldAnnotation(Class<?> clazz,
            Class<? extends Annotation> annotationClass, String fieldName) throws Exception {
        return getFieldAnnotation(clazz, annotationClass, fieldName) != null;
    }

    /**
     * @Description:是否有注解
     */
    public static boolean hasMethodAnnotation(Class<?> clazz,
            Class<? extends Annotation> annotationClass, String methodName, Class<?>... paramType) throws Exception {
        return getMethodAnnotation(clazz, annotationClass, methodName, paramType) != null;
    }

    /**
     * @Description:获取类注解
     */
    public static <A extends Annotation> A getClassAnnotation(Class<?> clazz, Class<A> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    /**
     * @Description:获取类成员注解
     */
    public static <A extends Annotation> A getFieldAnnotation(Class<?> clazz,
            Class<A> annotationClass, String fieldName) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field == null) {
                throw new Exception("no such field[" + fieldName + "] in " + clazz.getCanonicalName());
            }
            return field.getAnnotation(annotationClass);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new Exception("access error: field[" + fieldName + "] in " + clazz.getCanonicalName(), e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new Exception("no such field[" + fieldName + "] in " + clazz.getCanonicalName());
        }
    }

    /**
     * @Description:获取类方法上的注解
     */
    public static <A extends Annotation> A getMethodAnnotation(Class<?> clazz,
            Class<A> annotationClass, String methodName, Class<?>... paramType)
            throws Exception {
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramType);
            if (method == null) {
                throw new Exception("access error: method[" + methodName + "] in " + clazz.getCanonicalName());
            }
            return method.getAnnotation(annotationClass);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new Exception("access error: method[" + methodName + "] in " + clazz.getCanonicalName(), e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new Exception("no such method[" + methodName + "] in " + clazz.getCanonicalName(), e);
        }
    }

    /**
     * @Description:实例化类
     */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className) {
		try {
			return (T)newInstance(Class.forName(className));
		} catch (ClassNotFoundException e) {
			throw new GeneralException(e);
		}
	}
	
	/**
     * @Description:实例化类
     */
	public static <T> T newInstance(Class<T> clazz) {
		try {
			return (T) clazz.newInstance();
		} catch (Exception exception) {
			throw new GeneralException(exception);
		}
	}
	
}
