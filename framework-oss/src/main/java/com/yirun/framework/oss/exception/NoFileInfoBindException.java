package com.yirun.framework.oss.exception;

/**
 * @Description : 当前线程没有绑定对象的异常
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.oss.exception.NoFileInfoBindException
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public class NoFileInfoBindException extends RuntimeException {

    public NoFileInfoBindException() {
        super("当前线程没有绑定FileInfo对象!");
    }

    public NoFileInfoBindException(String message) {
        super(message);
    }
}
