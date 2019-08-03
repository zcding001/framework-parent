package com.yirun.framework.core.utils;

import com.yirun.framework.core.commons.Constants;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;


/**
 * @Described	: HttpSession工具类
 * @project		: com.sirding.core.utils.HttpSessionUtil
 * @author 		: zc.ding
 * @date 		: 2016年12月29日
 */
public class HttpSessionUtil {
	
	private HttpSessionUtil(){}
	/**
	 * Spring3.x通过{@linkcom.yirun.finance.user.support.security.interceptor.AddResponseInterceptor}将response添加request中
	 * Spring4.x以上支持 ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
	 * @Described			: 获得HttpServletResponse  3.x不支持
	 * @author				: zc.ding
	 * @date 				: 2016年12月29日
	 * @return
	 */
	public static HttpServletResponse getResponse(){
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//		return (HttpServletResponse) getRequest().getAttribute("ATTR_RESPONSE");
	}
	
	/**
	 * @Described			: 获得HttpServletRequest
	 * @author				: zc.ding
	 * @date 				: 2016年12月29日
	 * @return
	 */
	public static HttpServletRequest getRequest(){
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	/**
	 * 添加属性到HttpServletRequest中
	 * @author	 zc.ding
	 * @since 	 2017年5月21日
	 * @param name
	 * @param obj
	 */
	public static void addAttrToRequest(String name, Object obj){
		getRequest().setAttribute(name, obj);
	}
	
	/**
	 * 从HttpServletRequest中删除name属性
	 * @author	 zc.ding
	 * @since 	 2017年5月21日
	 * @param name
	 */
	public static void removeAttrFromRequest(String name){
		getRequest().removeAttribute(name);
	}
	
	/**
	 * 从HttpServletRequest中获得name属性值
	 * @author	 zc.ding
	 * @since 	 2017年5月21日
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttrFromRequest(String name) {
		return (T)getRequest().getParameter(name);
	}

	/**
	 * 添加属性到HttpSession中
	 * @author	 zc.ding
	 * @since 	 2017年5月21日
	 * @param name
	 * @param obj
	 */
	public static void addAttrToSession(String name, Serializable obj){
		getSession().setAttribute(name, obj);
	}
	
	/**
	 * 从HttpSession中删除name属性
	 * @author	 zc.ding
	 * @since 	 2017年5月21日
	 * @param name
	 */
	public static void removeAttrFromSession(String name){
		getSession().removeAttribute(name);
	}
	
	/**
	 * 从HttpSession中获得name属性值
	 * @author	 zc.ding
	 * @since 	 2017年5月21日
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttrFromSession(String name) {
		return (T)getSession().getAttribute(name);
	}
	
	
	/**
	 * @Described	: 获得请求的session
	 * @author		: zc.ding
	 * @date 		: 2016年11月18日
	 * @return		: HttpSession
	 * @return
	 */
	public static HttpSession getSession(){
		return getRequest().getSession();
	}

    /**
    *  获得请求的session
    *  @Method_Name             ：getSession
    *  @param flag
    *  @return javax.servlet.http.HttpSession
    *  @Creation Date           ：2018/5/17
    *  @Author                  ：zhichaoding@hongkun.com.cn
    */
    public static HttpSession getSession(boolean flag){
        return getRequest().getSession(flag);
    }
	
	/**
	 * @Described	: 保存登录用户信息到seesion中
	 * @author		: zc.ding
	 * @date 		: 2016年11月18日
	 * @return		: void
	 * @param appUser
	 */
	public static void addLoginUser(Serializable user){
		getSession().setAttribute(Constants.CURRENT_USER, user);
	}
	
	/**
	 * @Described			: 获得当前登录用户的信息
	 * @author				: zc.ding
	 * @date 				: 2016年12月27日
	 * @return
	 */
	public static Object getLoginUser(){
		return getSession().getAttribute(Constants.CURRENT_USER);
	}
}
