package com.yirun.framework.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.yirun.framework.core.support.jta.JTAInterceptor;
import com.yirun.framework.core.utils.PropertiesHolder;

/**
 * @Description : 全局异常处理
 * @Project : framework-core
 * @Program Name :
 *          com.yirun.framework.core.exception.GlobalExceptionHandler.java
 * @Author : imzhousong@gmail.com 周松
 */
public class GlobalExceptionHandler extends SimpleMappingExceptionResolver {

	private static final Logger logger = Logger.getLogger(JTAInterceptor.class);

	private MessageSource messageResource;

	@Override
	public ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
										   Exception ex) {
		String viewName = determineViewName(ex, request);
		String reqType = request.getHeader("X-Requested-With");
		String errorMsg = getExceptionMessage(ex, request);
		request.setAttribute("errorMsg", errorMsg);
		if ("XMLHttpRequest".equals(reqType)) {
			if (ex instanceof BaseException) {
				BaseException base = (BaseException) ex;
				base.setErrorMsg(errorMsg);
			} else {
				BaseException base = new GeneralException(ex.getMessage());
				base.setErrorMsg(errorMsg);
				throw base;
			}
			return null;
		} else {
			if (viewName != null) {
				return super.doResolveException(request, response, handler, ex);
			} else {
				return new ModelAndView("errors/error");
			}
		}

	}

	public String getExceptionMessage(Exception ex, HttpServletRequest request) {
		String message = "";
		if (ex instanceof BaseException) {
			BaseException be = (BaseException) ex;
			String code = be.getCode();
			if (StringUtils.isNotEmpty(code)) {
				message = messageResource.getMessage(code, be.getValues(), request.getLocale());
			}
			if (StringUtils.isEmpty(message)) {
				message = ex.getMessage();
			}
		} else {
			String isAllException = PropertiesHolder.getProperty("is.print.all.exception", "true");
			if (!"true".equals(isAllException.trim())) {
				message = "系统正在维护!";
			} else {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
				try {
					message = sw.toString();
					pw.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}

			logger.error(ex.getMessage(), ex);
		}

		return message;
	}

	public void setMessageResource(ReloadableResourceBundleMessageSource messageResource) {
		this.messageResource = messageResource;
	}

}
