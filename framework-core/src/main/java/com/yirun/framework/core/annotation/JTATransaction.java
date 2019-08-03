package com.yirun.framework.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description   : JTA事务处理
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.annotation.JTATransaction.java
 * @Author        : imzhousong@gmail.com 周松
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JTATransaction {
}
