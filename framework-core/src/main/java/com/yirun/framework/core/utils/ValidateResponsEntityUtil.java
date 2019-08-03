package com.yirun.framework.core.utils;

import com.yirun.framework.core.commons.Constants;
import com.yirun.framework.core.exception.BusinessException;
import com.yirun.framework.core.model.ResponseEntity;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * 执行步骤Util
 */
public class ValidateResponsEntityUtil {

    /**
     * 日志类别
     */
    private final Logger logger;

    /**
     * 构造器必须制定日志
     *
     * @param logger
     */
    public ValidateResponsEntityUtil(Logger logger) {
        this.logger = logger;
    }

    /**
     * 验证业务逻辑的步骤
     *
     * @param result           方法调用结果
     * @param errorLogMsg    错误日志信息
     * @param throwException 需要抛出的异常
     * @return
     */
    public Object validate(Object result, String errorLogMsg, RuntimeException throwException) {

        //判断是否是错误的结果
        if (result != null && (result instanceof ResponseEntity)) {
            if (((ResponseEntity) result).getResStatus() == Constants.ERROR) {
                //抛出异常
                logger.error(errorLogMsg);
                throw throwException;
            }
        }
        return result;
    }

    /**
     * 验证业务逻辑的步骤
     *
     * @param result           方法调用结果
     * @param errorLogMsg    错误日志信息
     * @return
     */
    public Object validate(Object result, String errorLogMsg, String exceptionMsg) {

        //判断是否是错误的结果
        if (result != null && (result instanceof ResponseEntity)) {
            if (((ResponseEntity) result).getResStatus() == Constants.ERROR) {
                //抛出异常
                logger.error(errorLogMsg);
                throw new BusinessException(exceptionMsg);
            }
        }
        return result;
    }
    /**
     * 验证业务逻辑的步骤
     *
     * @param result           方法调用结果
     * @param errorLogMsg    错误日志信息
     * @return
     */
    public Object validate(Object result, String errorLogMsg) {
        //判断是否是错误的结果
        if (result != null && (result instanceof ResponseEntity)) {
            if (((ResponseEntity) result).getResStatus() == Constants.ERROR) {
                //抛出异常
                logger.error(errorLogMsg);
                throw new BusinessException(errorLogMsg);
            }
        }
        return result;
    }

    /**
     * 只抛异常，不记录日志
     *
     * @param result           方法调用结果
     * @return
     */
    public Object validate(Object result) {
        //判断是否是错误的结果
        if (result != null && (result instanceof ResponseEntity)) {
            if (((ResponseEntity) result).getResStatus() == Constants.ERROR) {
                throw new BusinessException((String)((ResponseEntity) result).getResMsg());
            }
        }
        return result;
    }


}
