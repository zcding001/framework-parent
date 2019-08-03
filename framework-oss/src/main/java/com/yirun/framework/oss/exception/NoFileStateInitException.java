package com.yirun.framework.oss.exception;

/**
 * @Description : 没有设置文件操作状态异常
 * @Project : framework
 * @Program Name  : com.yirun.framework.oss.exception.NoFileStateInitException
 * @Author : pengwu@hongkun.com.cn 吴鹏
 */
public class NoFileStateInitException extends RuntimeException{

    public NoFileStateInitException(){
        super("请先初始化FileInfo状态！");
    }

    public NoFileStateInitException(String message){
        super(message);
    }

}
