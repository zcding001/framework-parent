package com.yirun.framework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description   : 防重复提交TOKEN
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.annotation.Token.java
 * @Author        : imzhousong@gmail.com 周松
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {
	
	/**
	 * @Description   : token的操作类别（Operate）
	 * @Project       : framework-core
	 * @Program Name  : com.yirun.framework.core.annotation.Token.java
	 * @Author        : zhichaoding@hongkun.com zc.ding
	 */
	public enum Ope{
		/**
		 * 添加token
		 */
		ADD,
		/**
		 * 验证、刷新token
		 */
		REFRESH,
		/**
		 * 验证、删除token
		 */
		REMOVE
	}

	/**
	 * 操作类型 ，推荐使用1,2<br/>
	 * 		ADD:add <br>
	 * 		REFRESH:validate and refresh<br>
	 * 		REMOVE:validate and remove <br>
	 * @author	 zc.ding
	 * @since 	 2017年5月21日
	 * @return
	 */
	Ope operate() default Ope.REFRESH;
}
