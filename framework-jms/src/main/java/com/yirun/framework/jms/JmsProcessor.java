package com.yirun.framework.jms;

import javax.jms.Message;

/**
 * @Description :消息处理器接口
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.jms.JmsProcessor
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public interface JmsProcessor {

    /**
     * 处理核心逻辑
     * @param message
     * @return
     */
    Object processKeyedMessage(Message message);

}
