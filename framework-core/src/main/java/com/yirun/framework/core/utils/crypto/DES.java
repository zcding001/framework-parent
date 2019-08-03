package com.yirun.framework.core.utils.crypto;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.yirun.framework.core.exception.GeneralException;

/**
 * @Description   : Des工具类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.crypto.DES.java
 * @Author        : imzhousong@gmail.com 周松
 */
public abstract class DES {

	private final static String DES = "DES";

	/**
	 * 将字符串原文加密成16进制的字符串密文
	 * 
	 * @param src 字符串原文
	 * @param key 字符串密匙， 8 位长度
	 * @return 16进制的字符串密文
	 */
	public final static String encryptToHex(String src, String key) {
		try {
			return byteToHex(encrypt(src.getBytes(), key.getBytes()));
		} catch (Exception exception) {
			throw new GeneralException(exception);
		}
	}

	/**
	 * 将字符串原文加密成Base64编码的字符串密文
	 * 
	 * @param src 字符串原文
	 * @param key 字符串密匙， 8 位长度
	 * @return Base64编码的字符串密文
	 */
	public final static String encryptToBase64(String src, String key) {
		try {
			byte[] desBytes = encrypt(src.getBytes(), key.getBytes());
			byte[] base64Bytes = Base64.encodeBase64(desBytes);
			return new String(base64Bytes);
		} catch (Exception exception) {
			throw new GeneralException(exception);
		}
	}
	
	/**
	 * 将字符串原文加密成具有Base64编码的URL（UTF-8）形式
	 * URL使用{#link URLEncoder}编码
	 * 
	 * @param src 字符串原文
	 * @param key 字符串密匙， 8 位长度
	 * @return 具有Base64编码的URL（UTF-8）
	 */
	public final static String encryptToBase64Url(String src, String key) {
		try {
			byte[] desBytes = encrypt(src.getBytes(), key.getBytes());
			byte[] base64Bytes = Base64.encodeBase64(desBytes);
			return URLEncoder.encode(new String(base64Bytes), "UTF-8");
		} catch (Exception exception) {
			throw new GeneralException(exception);
		}
	}
	
	/**
	 * 将字节数组原文加密成字节数组密文
	 * 
	 * @param src 字节数组原文
	 * @param key 字节数组密匙，长度为64
	 * @return 字节数组密文
	 */
	public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(DES);
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);

	}
	
	/**
	 * 将16进制的字符串密文解密成字符串原文
	 * 
	 * @param src 16进制的字符串密文
	 * @param key 字符串密匙， 8 位长度
	 * @return 字符串原文
	 */
	public final static String decryptFromHex(String src, String key) {
		try {
			return new String(decrypt(hexToByte(src.getBytes()), key.getBytes()));
		} catch (Exception exception) {
			throw new GeneralException(exception);
		}
	}
	
	/**
	 * 将Base64编码的字符串密文解密成字符串原文
	 * 
	 * @param src Base64编码的字符串密文
	 * @param key 字符串密匙， 8 位长度
	 * @return 字符串原文
	 */
	public final static String decryptFromBase64(String src, String key) {
		try {
			byte[] base64Bytes = Base64.decodeBase64(src.getBytes());
			byte[] desBytes = decrypt(base64Bytes, key.getBytes());
			return new String(desBytes);
		} catch (Exception exception) {
			throw new GeneralException(exception);
		}
	}

	/**
	 * 将字节数组密文解密密成字节数组原文
	 * 
	 * @param src 字节数组密文
	 * @param key 字节数组密匙，长度为64
	 * @return 字节数组原文
	 */
	public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(DES);
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		return cipher.doFinal(src);

	}

	/**
	 * 将二进制字节数组转换成16进制字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteToHex(byte[] bytes) {
		StringBuffer hex = new StringBuffer();
		for (int n = 0; n < bytes.length; n++) {
			String perByteHex = (Integer.toHexString(bytes[n] & 0XFF));
			if (perByteHex.length() == 1)
				hex.append("0" + perByteHex);
			else
				hex.append(perByteHex);
		}
		return hex.toString().toUpperCase();
	}

	/**
	 * 将16进制的字符串对应的字节数组转换成二进制数组
	 * 
	 * @param b
	 * @return
	 */
	public static byte[] hexToByte(byte[] hexBytes) {
		if ((hexBytes.length % 2) != 0) {
			throw new GeneralException();
		}
		byte[] bytes = new byte[hexBytes.length / 2];
		for (int n = 0; n < hexBytes.length; n += 2) {
			String item = new String(hexBytes, n, 2);
			bytes[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return bytes;
	}
	
	public static void testHex() {
		String text = "的算法速度发沙发大斯蒂芬萨菲";
		System.out.println(" 将 " +text + "加密成des 16进制");
		System.out.println("原文：" + text + " 原文长度：" + text.length());
		String hex = encryptToHex(text, "12346578");
		System.out.println("密文：" + hex + "  密文长度：" + hex.length());
		String posttext = decryptFromHex(hex, "12346578");
		System.out.println(posttext);
		System.out.println(" text == posttext : " + text.equals(posttext));
	}
	
	public static void testBase64() throws Exception {
		String text = "的算法速度发沙发大斯蒂芬萨菲";
		System.out.println(" 将 " +text + "加密成des base64进制");
		System.out.println("原文：" + text + " 原文长度：" + text.length());
		String base64 = encryptToBase64(text, "12346578");
		System.out.println("密文：" + base64 + "  密文长度：" + base64.length());
		String posttext = decryptFromBase64(base64, "12346578");
		System.out.println(posttext);
		System.out.println(" text == posttext : " + text.equals(posttext));
	}
	
	public static void testURL() throws Exception {
		String text = "orgCode=0692&currentOperator=aa&orgName=bb";
		System.out.println(" 将 " +text + "加密成des url");
		System.out.println("原文：" + text + " 原文长度：" + text.length());
		String url = encryptToBase64Url(text, "58851111");
		System.out.println("密文：" + url + "  密文长度：" + url.length());
		String posttext = decryptFromBase64(URLDecoder.decode(url, "UTF-8"), "58851111");
		System.out.println(posttext);
		System.out.println(" text == posttext : " + text.equals(posttext));
	}
	
	public static void main(String[] args) throws Exception {
		testHex();
		testBase64();
		testURL();
	}
}
