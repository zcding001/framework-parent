package com.yirun.framework.core.utils.mail;


import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * @Description   : 邮件发送工具类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.mail.MailUtil.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class MailUtil {

	private static Logger logger = LoggerFactory.getLogger(MailUtil.class);
	
	private final static String contentType = "text/html;charset=UTF-8";

	private String userName = "";

	private String password = "";

	private String host = "";

	private int port = 25;

	/**
	 * Message.RecipientType.TO(发送)/CC(抄送)/BCC(密送)
	 */
	private Map<Message.RecipientType, String[]> toAddrMap = new HashMap<Message.RecipientType, String[]>();

	public Map<Message.RecipientType, String[]> getToAddrMap() {
		return toAddrMap;
	}

	public void setToAddrMap(Map<Message.RecipientType, String[]> toAddrMap) {
		this.toAddrMap = toAddrMap;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public MailUtil() {
	}

	public MailUtil(String userName, String password, String host) {
		this.userName = userName;
		this.password = password;
		this.host = host;
	}

	public MailUtil(String userName, String password, String host, int port) {
		this.userName = userName;
		this.password = password;
		this.host = host;
		this.port = port;
	}

	/**
	 *  @Description    : 初始邮件发送
	 *  @Method_Name    : initMailSend
	 *  @param userName	用户名
	 *  @param password	密码
	 *  @param fromAddr	邮件发送者（如:xxx@163.com）
	 *  @param host		邮件服务器地址 (如:smtp.163.com)
	 *  @param port		端口25 Gmail不是这个端口
	 *  @return         : Session
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	private Session initMailSend(String userName, String password, String fromAddr, String host, int port) throws GeneralSecurityException {
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", String.valueOf(port));
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		props.put("mail.smtp.localhost", host);
		props.put("mail.debug", "true");
		props.put("mail.smtp.from", fromAddr);

		MailSSLSocketFactory sslSF = new MailSSLSocketFactory();
		sslSF.setTrustAllHosts(true);
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.socketFactory", sslSF);
		props.put("mail.transport.protocol", "smtp");
		MailAuthenticator authenticator = new MailAuthenticator(userName, password);
		return Session.getDefaultInstance(props, authenticator);
	}

	public static void main(String[] args) {
		MailUtil mailUtil = new MailUtil();
		Map<Message.RecipientType, String[]> toAddrMap = new HashMap<Message.RecipientType, String[]>();
		String[] a = new String[1];
		a[0] = "pengwu@hongkun.com.cn";
		toAddrMap.put(Message.RecipientType.TO, a);
		mailUtil.sendMail("cxjservice@hongkunjinfu.com",toAddrMap,"测试","测试");
	}
	/**
	 *  @Description    : 带附件的邮件(收邮件人全是发送，不包括抄送/密送)
	 *  @Method_Name    : sendMail
	 *  @param fromAddr	发件人
	 *  @param toAddr	收邮件人
	 *  @param subject	主题
	 *  @param context	内容
	 *  @param att		附件
	 *  @return         : boolean
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public boolean sendMail(String fromAddr, String[] toAddr, String subject, String context, Attachment att) {
		toAddrMap.put(Message.RecipientType.TO, toAddr);
		return sendMail(fromAddr, toAddrMap, subject, context, att);
	}

	/**
	 *  @Description    : 不带附件的邮件(收邮件人全是发送，不包括抄送/密送)
	 *  @Method_Name    : sendMail
	 *  @param fromAddr	发件人
	 *  @param toAddr	收邮件人
	 *  @param subject	主题
	 *  @param context	内容
	 *  @return         : boolean
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public boolean sendMail(String fromAddr, String[] toAddr, String subject, String context) {
		toAddrMap.put(Message.RecipientType.TO, toAddr);
		return sendMail(fromAddr, toAddrMap, subject, context, null);
	}

	/**
	 *  @Description    : 不带附件的邮件
	 *  @Method_Name    : sendMail
	 *  @param fromAddr	发件人
	 *  @param toAddrMap 收邮件人
	 *  @param subject	主题
	 *  @param context	内容
	 *  @return         : boolean
	 *  @Creation Date  : 2016年7月6日 下午2:43:22 
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public boolean sendMail(String fromAddr, Map<Message.RecipientType, String[]> toAddrMap, String subject,
			String context) {
		return sendMail(fromAddr, toAddrMap, subject, context, null);
	}

	/**
	 *  @Description    : 邮件发送
	 *  @Method_Name    : sendMail
	 *  @param fromAddr		发邮人
	 *  @param toAddrMap	收件人
	 *  @param subject		主题
	 *  @param context		内容
	 *  @param attachment	附件
	 *  @return         : boolean
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public boolean sendMail(String fromAddr, Map<Message.RecipientType, String[]> toAddrMap, String subject,
			String context, Attachment attachment) {
		try {
			Session session = initMailSend(userName, password, fromAddr, host, port);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(fromAddr)); // 设置发件人
			msg.setSubject(subject); // 设置主题
			msg.setSentDate(new Date()); // 设置发送日期

			/**
			 * 收件人检查
			 */
			if (toAddrMap == null || toAddrMap.size() == 0) {
				throw new RuntimeException("收件人列表为空!");
			}
			// 设置收件人
			for (Iterator<Map.Entry<Message.RecipientType, String[]>> it = toAddrMap.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry<Message.RecipientType, String[]> entry = it.next();
				msg.addRecipients(entry.getKey(), getEmailAddress(entry.getValue()));
			}
			MimeMultipart mm = new MimeMultipart("related");
			// 新建一个存放信件内容的BodyPart对象
			BodyPart mdp = new MimeBodyPart();
			// 设置内容
			mdp.setContent(context, contentType);
			// 添加内容
			mm.addBodyPart(mdp);
			/** ********************添加附件************************** */
			if (attachment != null && attachment.getMapAttachMent().size() != 0) {
				Map<String, byte[]> att = attachment.getMapAttachMent();
				for (Iterator<Map.Entry<String, byte[]>> it = att.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, byte[]> entry = it.next();
					mdp = new MimeBodyPart();
					mdp.setFileName(new String(entry.getKey().getBytes("UTF-8"), "ISO8859-1"));
					DataHandler dh = new DataHandler(new ByteArrayDataSource(entry.getValue(),
							"application/octet-stream"));
					mdp.setDataHandler(dh);
					// 将含有附件的BodyPart加入到MimeMultipart对象中
					mm.addBodyPart(mdp);
				}
			}
			// 把mm作为消息对象的内容
			msg.setContent(mm);
			msg.saveChanges();
			Transport.send(msg);
//			javax.mail.Transport transport = session.getTransport("smtps");
//			transport.connect();
//			transport.sendMessage(msg, msg.getAllRecipients());
//			transport.close();
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 *  @Description    : 取得Email Address
	 *  @Method_Name    : getEmailAddress
	 *  @param toAddr
	 *  @throws AddressException
	 *  @return         : InternetAddress[]
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	private InternetAddress[] getEmailAddress(String[] toAddr) throws AddressException {
		InternetAddress[] tos = new InternetAddress[toAddr.length];
		int i = 0;
		for (String addr : toAddr) {
			tos[i++] = new InternetAddress(addr);
		}
		return tos;
	}

	/**
	 * Authenticator
	 */
	static class PopupAuthenticator extends Authenticator {
		private String user;
		private String pass;

		public PopupAuthenticator(String user, String pass) {
			this.user = user;
			this.pass = pass;
		}

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(user, pass);
		}
	}
}
