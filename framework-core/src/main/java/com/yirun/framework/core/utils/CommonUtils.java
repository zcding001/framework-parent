package com.yirun.framework.core.utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @Description   : 通用工具类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.CommonUtils.java
 * @Author        : xuhuiliu@hongkun.com.cn 劉旭輝
 */
public class CommonUtils {
	/**
	 *  @Description    : 校验list是否为空
	 *  @Method_Name    : isEmpty
	 *  @param list
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年7月4日 下午5:08:39 
	 *  @Author         : xuhuiliu@hongkun.com.cn 劉旭輝
	 */
	public static boolean isEmpty(List<?> list){
		return list == null || list.isEmpty();
	}
	
	/**
	 *  @Description    : 判断list非空
	 *  @Method_Name    : isNotEmpty
	 *  @param list
	 *  @return         : boolean true：非空， false：空
	 *  @Creation Date  : 2017年7月20日 上午10:47:06 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	public static boolean isNotEmpty(List<?> list){
		return !isEmpty(list);
	}
	/**
	 *  @Description    : Integer类型参数是否大于0
	 *  @Method_Name    : gtZero
	 *  @param param
	 *  @return
	 *  @return         : boolean
	 *  @Creation Date  : 2017年8月10日 下午3:50:52 
	 *  @Author         : xuhuiliu@hongkun.com.cn 劉旭輝
	 */
	public static boolean gtZero(Integer param){
		return param != null && param > 0;
	}

	/**
	 *  @Description    : 输出异常信息和异常堆栈信息
	 *  @Method_Name    : printStackTraceToString
	 *  @param throwable 异常
	 *  @return         : String
	 *  @Creation Date  : 2018年03月29日 下午16:32:52
	 *  @Author         : pengwu@hongkun.com.cn
	 */
	public static String printStackTraceToString(Throwable throwable) {
		StringWriter stringWriter = null;
		PrintWriter printWriter = null;
		try{
			stringWriter = new StringWriter();
			printWriter = new PrintWriter(stringWriter,true);
			throwable.printStackTrace(printWriter);
		}finally {
			if (stringWriter != null){
				try{
					stringWriter.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
			if (printWriter != null){
				printWriter.close();
			}
		}
		return stringWriter.getBuffer().toString();
	}
}
