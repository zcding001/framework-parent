package com.yirun.framework.jms.polling;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.yirun.framework.core.commons.Constants;
import com.yirun.framework.core.model.ResponseEntity;
import com.yirun.framework.core.utils.CommonUtils;
import com.yirun.framework.core.utils.ObjectUtilsExtend;
import com.yirun.framework.core.utils.PropertiesHolder;
import com.yirun.framework.jms.JmsService;
import com.yirun.framework.jms.utils.JmsUtils;

/**
 * @Description   : 将异常未消费的jms消息存储到数据库中
 * @Project       : framework-jms
 * @Program Name  : com.yirun.framework.jms.polling.JdbcRepository.java
 * @Author        : zhichaoding@hongkun.com zc.ding
 */
@Repository("jdbcRepositoryForJmsExceptionRecord")
public class JdbcRepository implements RecoverJmsFailMsgI{

	private Logger logger = LoggerFactory.getLogger(JdbcRepository.class);
	
	/**默认存储异常jms消息的表*/
	private static final String DEFAULT_TABLE_NAME = "jms_fail_msg";
//	private static final String COLUMNS = "id, code, message, message_type, destination_name, destination_type, state, create_time, modify_time";
	private static final String COLUMNS = "id, code, message, create_time";

	@Autowired(required = false)
	private DataSource dataSource;
	@Autowired(required = false)
	private JmsService jmsService;

	@Override
	public Integer addJmsFailMsg(JmsFailMsg jmsFailMsg) {
		Connection connection = null;
		PreparedStatement stmt = null;
		String code = "";
		try {
			code = jmsFailMsg.getCode();
			logger.info("异常JMS异常消息，code:{}, 目的地:{}, 消息类型：{}", code, jmsFailMsg.getDestinationName(), jmsFailMsg.getJmsMessageType().getValue());
			connection = this.getConnection();
			StringBuilder builder = new StringBuilder();
			builder.append("INSERT INTO " + getTableName());
			builder.append("(code, message)");
			builder.append(" VALUES (?, ?)");
			stmt = connection.prepareStatement(builder.toString());
			int i = 1;
			stmt.setString(i++, jmsFailMsg.getCode());
			stmt.setBytes(i++, ObjectUtilsExtend.objectToBytes(jmsFailMsg));
			return stmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("异常JMS异常消息，code:{} 未成功插入到数据库", code);
			throw new RuntimeException(e);
		} finally {
			closeStatement(stmt);
			this.releaseConnection(connection);
		}
	}

	@Override
	public ResponseEntity<?> delJmsFailMsg(List<String> codes) {
		if(CommonUtils.isEmpty(codes)) {
			return new ResponseEntity<>(Constants.ERROR, "为找到有效的数据");
		}
		logger.info("已经恢复的消息列表：{}", codes);
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = this.getConnection();
			StringBuilder builder = new StringBuilder();
			builder.append("DELETE FROM " + getTableName() + " WHERE code IN (?)");
			stmt = connection.prepareStatement(builder.toString());
			StringBuffer sb = new StringBuffer();
			codes.forEach(e -> sb.append(e).append(","));
			stmt.setString(1, sb.toString().substring(0, sb.length() - 1));
			if(stmt.executeUpdate() > 0) {
				return new ResponseEntity<>(Constants.SUCCESS);
			}
			return new ResponseEntity<>(Constants.ERROR, "更新失败");
		} catch (SQLException e) {
			logger.error("异常JMS消息恢复失败，列表：{}", codes);
			throw new RuntimeException(e);
		} finally {
			closeStatement(stmt);
			this.releaseConnection(connection);
		}
	}

	@Override
	public ResponseEntity<?> recoverJmsFailMsg(List<String> codes) {
		Connection connection = null;
		PreparedStatement stmt = null;
		if(CommonUtils.isEmpty(codes)) {
			return new ResponseEntity<>(Constants.ERROR, "为找到有效的数据");
		}
		try {
			connection = this.getConnection();
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT " + COLUMNS);
			builder.append(" FROM " + getTableName());
			builder.append(" WHERE code in (");
			StringBuffer sb = new StringBuffer();
			codes.forEach(e -> sb.append("'" + e.trim() + "'").append(","));
			builder.append(sb.toString().endsWith(",") ? (sb.toString().substring(0, sb.length() - 1)) : sb.toString());
			builder.append(")");
			stmt = connection.prepareStatement(builder.toString());
			ResultSet resultSet = stmt.executeQuery();
			List<JmsFailMsg> list = this.fitJmsFailMsg(resultSet);
			list.forEach(failMsg -> {
				logger.info("重新发送JMS异常消息，code:{}", failMsg.getCode());
				jmsService.sendMsgForPolling(
						JmsUtils.getDestination(failMsg.getDestinationType(), failMsg.getDestinationName()),
						failMsg.getJmsMessageType(), failMsg.getMsg(), failMsg.getDeliveryMode(),
						failMsg.getPriority(), failMsg.getTimeToLive(), 1);
				this.delJmsFailMsg(Arrays.asList(failMsg.getCode()));
			});
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeStatement(stmt);
			this.releaseConnection(connection);
		}
		return new ResponseEntity<>(Constants.SUCCESS);
	}
	
	@Override
	public List<JmsFailMsg> findAllJmsFailMsg() {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = this.getConnection();
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT " + COLUMNS);
			builder.append(" FROM " + getTableName() + " ORDER BY create_time DESC");
			stmt = connection.prepareStatement(builder.toString());
			ResultSet resultSet = stmt.executeQuery();
			return this.fitJmsFailMsg(resultSet);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeStatement(stmt);
			this.releaseConnection(connection);
		}
	}

	private Connection getConnection() {
		try {
			return this.dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 *  @Description    : 释放连接
	 *  @Method_Name    : releaseConnection
	 *  @param con
	 *  @return         : void
	 *  @Creation Date  : 2017年10月31日 上午9:40:53 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	private void releaseConnection(Connection con) {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 *  @Description    : 关闭流
	 *  @Method_Name    : closeStatement
	 *  @param stmt
	 *  @return         : void
	 *  @Creation Date  : 2017年10月31日 上午9:41:10 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	private void closeStatement(Statement stmt) {
		try {
			if (stmt != null && !stmt.isClosed()) {
				stmt.close();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  @Description    : 获得表名
	 *  @Method_Name    : getTableName
	 *  @return         : String
	 *  @Creation Date  : 2017年10月31日 下午5:41:06 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	private String getTableName() {
		String tableName = PropertiesHolder.getProperty(MQ_FIAL_MSG_STORAGE_KEYWORDS);
		if(StringUtils.isNotBlank(tableName)) {
			return tableName;
		}
		return DEFAULT_TABLE_NAME;
	}
	
	/**
	 *  @Description    : 解析检索结果
	 *  @Method_Name    : fitJmsFailMsg
	 *  @param rs
	 *  @throws SQLException
	 *  @return         : List<JmsExceptionRecord>
	 *  @Creation Date  : 2017年10月31日 上午9:41:29 
	 *  @Author         : zhichaoding@hongkun.com zc.ding
	 */
	private List<JmsFailMsg> fitJmsFailMsg(ResultSet rs) throws SQLException {
		List<JmsFailMsg> list = new ArrayList<>();
		while (rs.next()) {
			Blob blob = rs.getBlob("message");
			if(blob != null) {
				JmsFailMsg jmsFailMsg = ObjectUtilsExtend.bytesToObject(blob.getBytes(1, (int)blob.length()));
				if(jmsFailMsg == null) {
					throw new RuntimeException("未找到需要回复的jms消息");
				}
				jmsFailMsg.setId(rs.getInt("id"));
				jmsFailMsg.setCode(rs.getString("code"));
				jmsFailMsg.setCreatTime(rs.getTimestamp("create_time"));
				list.add(jmsFailMsg);
			}
		}
		return list;
	}

	
}
