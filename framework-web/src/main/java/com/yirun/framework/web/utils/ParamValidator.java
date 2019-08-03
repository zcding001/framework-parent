package com.yirun.framework.web.utils;

import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description : 统一处理参数验证类
 * @Project : finance
 * @Program Name  : com.yirun.finance.webutils.ParamValidator
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public class ParamValidator {
    /**
     * 返回参数中的错误的提示信息
     * @param bindingResult
     * @return
     */
    public static List getParamsErroMessages(BindingResult bindingResult) {
       return  bindingResult.getAllErrors().stream().map((error) -> error.getDefaultMessage()).collect(Collectors.toList());
    }
}
