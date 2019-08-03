package com.yirun.framework.jms.handler;

import com.yirun.framework.jms.JmsProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description : 指定key的MessageHandler
 * @Project : framework-parent
 * @Program Name  : com.yirun.framework.jms.handler.KeyAssignedJmsMessageHandler
 * @Author : zhongpingtang@yiruntz.com 唐忠平
 */
public abstract class KeyAssignedJmsMessageHandler extends AbstractJmsMessageHandler{


    /**
     * 找到当前环境中有哪些处理器
     */
    private Map<String,JmsProcessor> cachedProcessors = new ConcurrentHashMap(10);

    @Autowired(required = false)
    private List<JmsProcessor> autowiredProcessors;


    @Override
    public synchronized void handlerMessage(Message message) {
        JmsProcessor jmsProcessor=findProcessorByKey(message);
        if (jmsProcessor!=null) {
            try {
                jmsProcessor.processKeyedMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据PROCESSKEY找到处理器
     * @param message
     * @return
     */
    private JmsProcessor findProcessorByKey(Message message) {

        Object key = tryToFindKey(message);

        if (key==null) {
          return null;
        }

        if (cachedProcessors.isEmpty()) {
            synchronized (this.getClass()) {
                if (cachedProcessors.isEmpty()) {
                    initCachedProcessors();
                }
            }
        }
        return cachedProcessors.get(key);
    }

    /**
     * 规定一定要制定返回key的规则
     * @param message
     * @return
     */
    protected  Object tryToFindKey(Message message){
        return null;
    }

    /**
     * 初始化缓存处理器
     */
    private void initCachedProcessors() {
        if (autowiredProcessors!=null&&autowiredProcessors.size()>0) {
            autowiredProcessors.stream().forEach((e)-> cachedProcessors.put(e.getClass().getSimpleName(), e));
        }
    }



}
