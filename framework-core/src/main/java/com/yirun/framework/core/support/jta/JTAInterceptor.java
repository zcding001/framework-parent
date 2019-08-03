package com.yirun.framework.core.support.jta;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.yirun.framework.core.annotation.JTATransaction;
import com.yirun.framework.core.exception.JmsException;

/**
 * @Description   : JTA事务支持
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.support.jta.JTAInterceptor.java
 * @Author        : imzhousong@gmail.com 周松
 */
@Aspect
@Component
@Order(0)
public class JTAInterceptor {

	private static final Logger logger = Logger.getLogger(JTAInterceptor.class);

	@Around(value = "execution(* *com.yirun..service..impl.*ServiceImpl.* (..))", argNames = "pjp")
	public Object doException(ProceedingJoinPoint pjp) throws Throwable {
		Object obj = null;
		Method method = null;
		JTAModel model = null;
		JTATransaction jtaTran = null;
		try {
			Signature sign = pjp.getSignature();
			if (sign instanceof MethodSignature) {
				MethodSignature methodSign = (MethodSignature) sign;
				method = methodSign.getMethod();
				jtaTran = method.getAnnotation(JTATransaction.class);
				if (jtaTran != null) {
					if (JTAContext.TRANSACTION_LOCAL.get() == null) { // 只控制最外层
						JTAContext.TRANSACTION_LOCAL.set(new JTAModel(System.currentTimeMillis(), method));
						JTAContext.CONNECTION_HOLDER.set(new Vector<SqlSession>());
					}
				}
			}

			obj = pjp.proceed();

			model = JTAContext.TRANSACTION_LOCAL.get();
			if (jtaTran != null) {
				if (method != null && model != null && method.hashCode() == model.getMethod().hashCode()) {
					commitAllTransaction();
				}
			}

		} catch (Throwable e) {
			if (jtaTran != null) {
				rollbackAll();
			}
			throw e;
		} finally {
			model = JTAContext.TRANSACTION_LOCAL.get();
			if (jtaTran != null && method != null && model != null && method.hashCode() == model.getMethod().hashCode()) {
				removeCurThreadLocal(model);
			}

		}
		return obj;
	}

	/**
	 * @param model
	 * @return : void
	 * @Description : 清空线程绑定
	 * @Method_Name : removeCurThreadLocal
	 * @Author : imzhousong@gmail.com 周松
	 */
	private void removeCurThreadLocal(JTAModel model) {
		try {
			JTAContext.TRANSACTION_LOCAL.remove();
			JTAContext.CONNECTION_HOLDER.remove();
			JTAContext.JMS_SESSION_LOCAL.remove();
			JTAContext.JMS_CONNECTION_LOCAL.remove();
			logger.debug("grobal trancation cost time :" + (System.currentTimeMillis() - model.getStartTime()) + "ms");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * @return : void
	 * @Description : 提交事务
	 * @Method_Name : commitAllTransaction
	 * @Author : imzhousong@gmail.com 周松
	 */
	private void commitAllTransaction() {

		// [1]提交数据库事务
		Vector<SqlSession> vectorList = JTAContext.CONNECTION_HOLDER.get();
		if (vectorList != null && vectorList.size() > 0) {
			if (executeTest(vectorList)) {
				for (SqlSession sqlSession : vectorList) {
					setAutoCommit(sqlSession.getConnection(), true);
					commit(sqlSession);
				}
			} else {
				for (SqlSession sqlSession : vectorList) {
					rollback(sqlSession);
				}
			}
		}

		// [2]提交JMS事务
		Session session = JTAContext.JMS_SESSION_LOCAL.get();
		javax.jms.Connection jmsCon = JTAContext.JMS_CONNECTION_LOCAL.get();
		if (session != null) {
			commitIfNecessary(session);
		}
		if (jmsCon != null) {
			closeJmsConnection(jmsCon);
		}

	}

	private void commitIfNecessary(Session session) {
		Assert.notNull(session, "Session must not be null");
		try {
			session.commit();
		} catch (Exception ex) {
			throw new JmsException(ex);
		} finally {
			try {
				session.close();
			} catch (JMSException e) {
				throw new JmsException(e);
			}
		}
	}

	private void rollbackIfNecessary(Session session) {
		Assert.notNull(session, "Session must not be null");
		try {
			session.rollback();
		} catch (Exception ex) {
			throw new JmsException(ex);
		} finally {
			try {
				session.close();
			} catch (JMSException e) {
				throw new JmsException(e);
			}
		}
	}

	private void closeJmsConnection(javax.jms.Connection connection) {
		Assert.notNull(connection, "jms connection must not be null");
		try {
			connection.close();
		} catch (JMSException e) {
			throw new JmsException(e);
		}
	}

	/**
	 * @return : void
	 * @Description : 回滚所有操作
	 * @Method_Name : rollbackAll
	 * @Author : imzhousong@gmail.com 周松
	 */
	private void rollbackAll() {
		// 【1】回滚数据库
		Vector<SqlSession> vectorList = JTAContext.CONNECTION_HOLDER.get();
		if (vectorList != null && vectorList.size() > 0) {
			for (SqlSession sqlSession : vectorList) {
				rollback(sqlSession);
			}
		}

		// 【2】JMS回滚
		Session session = JTAContext.JMS_SESSION_LOCAL.get();
		javax.jms.Connection jmsCon = JTAContext.JMS_CONNECTION_LOCAL.get();
		if (session != null) {
			rollbackIfNecessary(session);
		}
		if (jmsCon != null) {
			closeJmsConnection(jmsCon);
		}

	}

	/**
	 * @param sqlSession
	 * @return : void
	 * @Description : 提交
	 * @Method_Name : commit
	 * @Author : imzhousong@gmail.com 周松
	 */
	private void commit(SqlSession sqlSession) {
		try {
			sqlSession.commit(true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		} finally {
			sqlSession.close();
		}
	}

	/**
	 * @param sqlSession
	 * @return : void
	 * @Description : 回滚
	 * @Method_Name : rollback
	 * @Author : imzhousong@gmail.com 周松
	 */
	private void rollback(SqlSession sqlSession) {
		try {
			sqlSession.rollback(true);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			sqlSession.close();
		}
	}

	/**
	 * @param vectorList
	 * @return : boolean(true 表示成功 | false 失败回滚)
	 * @Description : 测试连接是否可用
	 * @Method_Name : executeTest
	 * @Creation Date  : 2016年6月28日 下午2:05:43
	 * @Author : imzhousong@gmail.com 周松
	 */
	private boolean executeTest(Vector<SqlSession> vectorList) {
		if (vectorList == null) {
			return false;
		}
		for (SqlSession session : vectorList) {
			if (!checkConnection(session)) {
				return false; // 只要一个连接不成功所有就失败
			}
		}
		return true;
	}

	private boolean checkConnection(SqlSession session) {
		try {
			Connection con = session.getConnection();
			Statement stmt = con.createStatement();
			stmt.executeQuery("select 'x' ");
			stmt.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}

	private void setAutoCommit(Connection con, boolean autoCommit) {
		try {
			if (con != null)
				con.setAutoCommit(autoCommit);
		} catch (Exception e) {
		}
	}
}
