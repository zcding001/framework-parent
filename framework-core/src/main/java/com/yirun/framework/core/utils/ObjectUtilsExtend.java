package com.yirun.framework.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ObjectUtils;

import com.yirun.framework.core.exception.GeneralException;

/**
 * @Description : Spring工具类ObjectUtils扩展
 * @Project : framework-core
 * @Program Name : com.yirun.framework.core.utils.ObjectUtilsExtend.java
 * @Author : imzhousong@gmail.com 周松
 */
public abstract class ObjectUtilsExtend extends ObjectUtils {

  public static boolean contains(Object[] array, Object elment) {
    if (isEmpty(array) || elment == null) {
      return false;
    }
    for (Object perElment : array) {
      if (perElment.equals(elment)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @Description:把对象转换成字节数组
   */
  public static byte[] objectToBytes(Object obj) {
    try {
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      ObjectOutputStream objectStream =
          new ObjectOutputStream(new BufferedOutputStream(byteStream));
      objectStream.flush();
      objectStream.writeObject(obj);
      objectStream.flush();
      byte[] bytes = byteStream.toByteArray();
      objectStream.close();
      return bytes;
    } catch (Exception exception) {
      throw new GeneralException(exception);
    }
  }

  /**
   * @Description:把字节数组转换成对象
   */
  public static <T> T bytesToObject(byte[] bytes) {
    try {
      ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
      ObjectInputStream objectStream = new ObjectInputStream(new BufferedInputStream(byteStream));
      @SuppressWarnings("unchecked")
      T object = (T) objectStream.readObject();
      objectStream.close();
      return object;
    } catch (Exception exception) {
      throw new GeneralException(exception);
    }
  }

  /**
   * @Description:判断两个对象是否相等
   */
  public static boolean compare(Object first, Object second) {
    if ((first == second) || (first != null && second != null && first.equals(second))) {
      return true;
    }
    // 字符串情况
    if ((first instanceof String && second == null)
        || (second instanceof String && first == null)) {
      if ("".equals(first) || "".equals(second)) {
        return true;
      }
    }

    return false;
  }

  /**
   * @Description:判断两个对象指定的属性是否相等
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static boolean compare(Object first, Object second, List<String> properties) {
    try {
      for (String property : properties) {
        Method firstMethod = first.getClass()
            .getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
        Method secondMethod = second.getClass()
            .getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
        Object firstPropertyVal = firstMethod.invoke(first);
        Object secondPropertyVal = secondMethod.invoke(second);
        boolean isEqualed = true;
        if (firstPropertyVal instanceof Map || secondPropertyVal instanceof Map) {
          isEqualed = compare(new HashMap((Map<?, ?>) firstPropertyVal),
              new HashMap((Map<?, ?>) secondPropertyVal));
        } else if (firstPropertyVal instanceof List || secondPropertyVal instanceof List) {
          isEqualed = compare(new ArrayList((List<?>) firstPropertyVal),
              new ArrayList((List<?>) secondPropertyVal));
        } else if (firstPropertyVal instanceof Set || secondPropertyVal instanceof Set) {
          isEqualed = compare(new HashSet((Set<?>) firstPropertyVal),
              new HashSet((Set<?>) secondPropertyVal));
        } else {
          isEqualed = compare(firstPropertyVal, secondPropertyVal);
        }
        if (!isEqualed) {
          return isEqualed;
        }
      }
    } catch (Exception e) {
      throw new GeneralException(e);
    }
    return true;
  }

  public static void copyProperties(Object dest, Object origin, List<String> properties) {
    try {
      Field[] fields = origin.getClass().getDeclaredFields();
      for (String property : properties) {
        for (int i = 0; i < fields.length; i++) {
          if (property.equals(fields[i].getName())) {
            Method getter = origin.getClass()
                .getMethod("get" + property.substring(0, 1).toUpperCase() + property.substring(1));
            Method setter = dest.getClass().getMethod(
                "set" + property.substring(0, 1).toUpperCase() + property.substring(1),
                fields[i].getType());
            setter.invoke(dest, getter.invoke(origin));
            break;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new GeneralException(e);
    }
  }

  public static boolean isNull(Object object) {
    return object == null;
  }

  public static boolean notNull(Object object) {
    return object != null;
  }
}
