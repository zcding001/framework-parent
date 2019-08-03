package com.yirun.framework.core.support.jta;

import java.util.Vector;

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.ibatis.session.SqlSession;

/**
 * @Description   : JTA上下文事务环境
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.support.jta.JTAContext.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class JTAContext {

	public static final ThreadLocal<JTAModel> TRANSACTION_LOCAL = new ThreadLocal<JTAModel>();

	public static final ThreadLocal<Vector<SqlSession>> CONNECTION_HOLDER = new ThreadLocal<Vector<SqlSession>>();

	public static final ThreadLocal<Session> JMS_SESSION_LOCAL = new ThreadLocal<Session>();

	public static final ThreadLocal<Connection> JMS_CONNECTION_LOCAL = new ThreadLocal<Connection>();

}
