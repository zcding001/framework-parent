package com.yirun.framework.web;

import com.yirun.framework.core.exception.GeneralException;
import com.yirun.framework.core.model.ResponseEntity;
import com.yirun.framework.core.utils.json.JsonUtils;
import com.yirun.framework.core.utils.response.ResponseUtils;
import com.yirun.framework.web.utils.ParamValidator;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

import static com.yirun.framework.core.commons.Constants.ERROR;

/**
 * @Description : 验证controller层返回值的Advice
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.web.ValidateControllerAdvice
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
@ControllerAdvice
public class ExceptionHandlerAdvice {
    /**
     * bean校验未通过异常
     */
    @ExceptionHandler(GeneralException.class)
    public String validExceptionHandler(GeneralException exception, WebRequest request, HttpServletResponse response) {
        //获取所有的异常
        if (isAjaxRequest(request)) {
            //如果验证中存在错误,直接返回
            responseRequestBreak(response,new ResponseEntity(ERROR, exception.getErrorMsg()));
            return null;
        }
        return "/validError";
    }


    /**
     * 验证是否是ajax请求
     * @param webRequest
     * @return
     */
    private  boolean isAjaxRequest(WebRequest webRequest) {
        String requestedWith = webRequest.getHeader("X-Requested-With");
        return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
    }

    /**
     * 中断请求
     * @param response
     * @param responseEntity
     */
    protected void responseRequestBreak(HttpServletResponse response, ResponseEntity responseEntity) {
        String responseStr = JsonUtils.toJson(responseEntity);
        ResponseUtils.responseJson(response, responseStr);
        return;
    }


}