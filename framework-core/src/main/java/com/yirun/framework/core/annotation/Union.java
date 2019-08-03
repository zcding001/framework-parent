package com.yirun.framework.core.annotation;

import java.lang.annotation.*;

/**
 * @Description :处理VO与实体类字段之间的对应关系
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.web.annotation.DumpTo
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */

@Documented
@Target({ElementType.FIELD})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Union {
    /**
     * 绑定的目标类型
     * @return
     */
    String[] bind() default {};

    /**
     * 是否要在注入时修改属性名称
     * @return
     */
    String reNameTo() default "";

    /**
     * 合并的时候指定的唯一的key
     * 如果为空默取bind的一个值
     * 或者取本字段的名称
     * @return
     */
    String mergeKey() default "";

    /**
     * 在聚合过程中，指定该属性是否可以被覆盖（防止源对象中的属性不为空，但是VO对象又不想被改变值）
     * 默认为true，可以被改变
     * @return
     */
    boolean changeAble() default true;

    /**
     * 判断该属性有值的时候是否可以被改变，默认为true
     * @return
     */
    boolean changeAbleIfHasValue() default true;

    /**
     * 是否用于去其他服务限制查询
     * @return
     */
    boolean forLimitQuery() default false;

}
