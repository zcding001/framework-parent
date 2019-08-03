package com.yirun.framework.jms.listener;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.yirun.framework.jms.handler.DefaultJmsMessageHandler;
import com.yirun.framework.jms.model.RequestJmsModel;

public class TestJmsMessageHandler extends DefaultJmsMessageHandler {
	
	private final Logger logger = Logger.getLogger(TestJmsMessageHandler.class);

	/**
	 * 设置并发数 4
	 */
//	private final static int CONCURRENT_NUM = 4;

	/**
	 * 接口调用异常后尝试重新调用的延时 (ms)
	 */
	public final static int DELAY_TIME = 5 * 1000 * 60;
	
	/**
	 * 接口调用超时时间设置（接口在指定时间没有返回会抛异常）
	 */
	public final static int FUTURE_DELAY_TIME = 60;
	
	/**
	 * 业务执行线程池
	 */
//	private static ExecutorService executeService = Executors.newFixedThreadPool(CONCURRENT_NUM);

	/**
	 * 调度任务线程池
	 */
//	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

//	private static ReentrantLock lock = new ReentrantLock();
	
	public synchronized void handlerMessage(ObjectMessage objectMessage) throws JMSException {
		String messageId = objectMessage.getJMSMessageID();
		Serializable obj = objectMessage.getObject();
		try {
			logger.info("消息:【" + messageId + "】加入处理队列!");
			if (obj instanceof RequestJmsModel) {
				RequestJmsModel jmsModel = (RequestJmsModel) obj;
				System.out.println(jmsModel.getRequestJson());
			} else {
				logger.error("非法数据==messageId:" + messageId);
				return;
			}
		} catch (Exception e) {
			logger.error(ToStringBuilder.reflectionToString(objectMessage));
			logger.error(e.getMessage(), e);
		}
	}
	
//	/**
//	 * @description 执行任务
//	 */
//	public void runTask() {
//		System.out.println("runTask...");
//		
//		scheduler.scheduleAtFixedRate(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					lock.lock();
//					ConcurrentLinkedQueue<Future<String>> tempQueue = new ConcurrentLinkedQueue<Future<String>>();
//					logger.debug("执行任务:" + DateUtils.format(new Date(), DateUtils.DATE_HH_MM_SS));
//					for (Iterator<Map.Entry<String, ConcurrentLinkedQueue<BidObjectMessage>>> it = curMap.entrySet().iterator(); it.hasNext();) {
//						Map.Entry<String, ConcurrentLinkedQueue<BidObjectMessage>> entry = it.next();
//						ConcurrentLinkedQueue<BidObjectMessage> queue = entry.getValue();
//						String keyProjectId = entry.getKey();
//						if (queue.isEmpty()) { // 如果队列为空就清除
//							curMap.remove(keyProjectId);
//						}
//						BidObjectMessage objMsg = queue.peek();
//						Future<String> future = null;
//						BpsToPspTask task = new BpsToPspTask(bpsToPspService, messageNotifyService, monitorJmsModelService, objMsg, curMap);
//						if (objMsg.getCallCount() > 0) {
//							// 需要延时
//							long curTime = System.currentTimeMillis();
//							if (curTime - objMsg.getStartTime() > DELAY_TIME) {
//								future = executeService.submit(task);
//							} else {
//								continue;
//							}
//						} else if (objMsg.getCallCount() == 0) {
//							future = executeService.submit(task);
//						}
//						if (future != null) {
//							tempQueue.offer(future);
//						}
//					}
//					while (!tempQueue.isEmpty()) {
//						Future<String> future = tempQueue.poll();
//						if (future != null) {
//							String resMsg = future.get(FUTURE_DELAY_TIME, TimeUnit.SECONDS); // 阻塞
//							logger.info(resMsg);
//						}
//					}
//				} catch (Exception e) {
//					logger.info(e.getMessage(), e);
//				} finally {
//					lock.unlock();
//					logger.debug("任务执行结束:" + DateUtils.format(new Date(), DateUtils.DATE_HH_MM_SS));
//				}
//			}
//		}, 1, 2, TimeUnit.SECONDS);
//	}
}
