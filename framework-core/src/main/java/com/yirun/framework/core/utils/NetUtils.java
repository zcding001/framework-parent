package com.yirun.framework.core.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description   : 网络工具类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.NetUtils.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class NetUtils {

	public static boolean telnetStringPort(String ip, String port, int timeout) {
		if (port == null || !isValidPort(port)) {
			return NetUtils.ping(ip, timeout);
		}
		return NetUtils.telnet(ip, Integer.valueOf(port.trim()).intValue(), timeout);
	}

	public static boolean ping(String ip, int timeout) {
		try {
			return InetAddress.getByName(ip.trim()).isReachable(timeout);
		} catch (UnknownHostException e) {
			// logger.error("UnknownHostException:" + e.getMessage());
			return false;
		} catch (IOException e) {
			// logger.error("IOException:" + e.getMessage());
			return false;
		}
	}

	public static String getLocalIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception uhe) {
			String host = uhe.getMessage();
			if (host != null) {
				int colon = host.indexOf(':');
				if (colon > 0)
					return host.substring(0, colon);
			}
			return host;
		}
	}

	public static boolean telnet(String ip, int port, int timeout) {

		Socket server = null;
		try {
			server = new Socket();
			server.connect(new InetSocketAddress(ip.trim(), port), timeout);
			return true;
		} catch (UnknownHostException e) {
			// logger.error("UnknownHostException:" + e.getMessage());
			return false;
		} catch (IOException e) {
			// logger.error("IOException:" + e.getMessage());
			return false;
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {

				}
			}
		}
	}

	public static boolean isValidPort(String port) {
		if (port != null && port.trim().matches("^[1-9][0-9]{0,3}$|^[1-5][0-9]{0,4}$|^6[0-5]{2}[0-3][0-5]$")) {
			int portInt = Integer.valueOf(port.trim()).intValue();
			if (portInt > 0 && portInt <= 0xFFFF)
				return true;
		}

		return false;
	}

	/**
	 * @description 取得请求真实IP地址
	 * @param request
	 * @return
	 */
	public static String getRequestIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static void main(String[] args) {
		// boolean flag = NetUtils.telnetStringPort("172.16.11.216", "1199",
		// 5000);
		// System.out.println(flag);
		System.out.println(NetUtils.getLocalIp());
	}

}
