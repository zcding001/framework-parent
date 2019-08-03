package com.yirun.framework.core.utils.response;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @Description   : 响应工具类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.response.ResponseUtils.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class ResponseUtils {
	
	private static Logger logger = Logger.getLogger(ResponseUtils.class);

	/**
	 *  @Description    : JSON 格式响应
	 *  @Method_Name    : responseScript
	 *  @param response
	 *  @param res		响应字符串
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public static void responseJson(HttpServletResponse response, String res) {
		try {
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(res);
			out.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 *  @Description    : SCRIPT 格式响应
	 *  @Method_Name    : responseScript
	 *  @param response
	 *  @param res		响应字符串
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public static void responseScript(HttpServletResponse response, String res) {
		try {
			response.setContentType("text/javascript;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(res);
			out.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 *  @Description    : text/html 格式响应
	 *  @Method_Name    : responseHtml
	 *  @param response
	 *  @param res		响应字符串
	 *  @return         : void
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public static void responseHtml(HttpServletResponse response, String res) {
		try {
			response.setContentType("text/html;charset=utf-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(res);
			out.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
