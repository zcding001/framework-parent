package com.yirun.framework.core.utils.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description   : MD5工具类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.crypto.MD5.java
 * @Author        : imzhousong@gmail.com 周松
 */
public abstract class MD5 {
	
	private final static String DM5 = "MD5";
	
	/**
	 *  @Description    : MD5加密
	 *  @Method_Name    : encrypt
	 *  @param data		待加密数据(未加密)
	 *  @return         : String
	 *  @Author         : imzhousong@gmail.com 周松
	 */
	public static String encrypt(String data) {
		try {
			MessageDigest alg = MessageDigest.getInstance(DM5);
			alg.update(data.getBytes());
			byte[] digesta = alg.digest();
			return byte2hex(digesta);
		} catch (NoSuchAlgorithmException NsEx) {
			return null;
		}
	}
    
    /**
     *  @Description    : MD5加密
     *  @Method_Name    : encrypt
     *  @param data		待加密数据(未加密)
     *  @return         : String
     *  @Author         : imzhousong@gmail.com 周松
     */
	public static String encrypt(byte[] data) {
		try {
			MessageDigest alg = MessageDigest.getInstance(DM5);
			alg.update(data);
			byte[] digesta = alg.digest();
			return byte2hex(digesta);
		} catch (NoSuchAlgorithmException NsEx) {
			return null;
		}
	}
    
	private static String byte2hex(byte[] data) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < data.length; n++) {
			stmp = (java.lang.Integer.toHexString(data[n] & 0XFF));
			if (stmp.length() == 1) {
				hs.append("0");
				hs.append(stmp);
			} else {
				hs.append(stmp);
			}
		}
		return hs.toString();
	}
}