package com.yirun.framework.core.utils.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * @Description   : 签名工具类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.crypto.SignatureUtil.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class SignatureUtil {
	
	private static final char last2byte = (char) Integer.parseInt("00000011", 2);

	private static final char last4byte = (char) Integer.parseInt("00001111", 2);

	private static final char last6byte = (char) Integer.parseInt("00111111", 2);

	private static final char lead6byte = (char) Integer.parseInt("11111100", 2);

	private static final char lead4byte = (char) Integer.parseInt("11110000", 2);

	private static final char lead2byte = (char) Integer.parseInt("11000000", 2);

	private static final char[] encodeTable = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	public static String getSecrectKey(String domain, String secretKey) {
		int ex = secretKey.length();
		byte[] ipadArray = new byte[64];
		byte[] opadArray = new byte[64];
		byte[] keyArray = new byte[64];
		SHA1Utils sha1 = new SHA1Utils();
		if (secretKey.length() > 64) {
			byte[] temp = sha1.getDigestOfBytes(secretKey.getBytes());
			ex = temp.length;
			for (int i = 0; i < ex; i++) {
				keyArray[i] = temp[i];
			}
		} else {
			byte[] temp = secretKey.getBytes();
			for (int i = 0; i < temp.length; i++) {
				keyArray[i] = temp[i];
			}
		}
		for (int i = ex; i < 64; i++) {
			keyArray[i] = 0;
		}
		for (int j = 0; j < 64; j++) {
			ipadArray[j] = (byte) (keyArray[j] ^ 0x36);
			opadArray[j] = (byte) (keyArray[j] ^ 0x5C);
		}
		byte[] tempResult = sha1.getDigestOfBytes(join(ipadArray, domain.getBytes()));
		return encode(tempResult);
	}

	public static String sign(String domain, String secretKey, Map<String, String> params) {
		String data = domain + "?" + sort(params);
		return HmacSHA1(data, secretKey);
	}

	public static String HmacSHA1(String data, String key) {
		byte[] byteHMAC = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
			mac.init(spec);
			byteHMAC = mac.doFinal(data.getBytes());
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException ignore) {
		}
		String oauth = Base64.encodeBase64String(byteHMAC);
		return oauth;
	}

	public static String sign(String dataStr, String secretKey) {
		return sign(dataStr.getBytes(), secretKey);
	}

	public static String sign(byte[] data, String secretKey) {
		byte[] ipadArray = new byte[64];
		byte[] opadArray = new byte[64];
		byte[] keyArray = new byte[64];
		int ex = secretKey.length();
		SHA1Utils sha1 = new SHA1Utils();
		if (secretKey.length() > 64) {
			byte[] temp = sha1.getDigestOfBytes(secretKey.getBytes());
			ex = temp.length;
			for (int i = 0; i < ex; i++) {
				keyArray[i] = temp[i];
			}
		} else {
			byte[] temp = secretKey.getBytes();
			for (int i = 0; i < temp.length; i++) {
				keyArray[i] = temp[i];
			}
		}
		for (int i = ex; i < 64; i++) {
			keyArray[i] = 0;
		}
		for (int j = 0; j < 64; j++) {
			ipadArray[j] = (byte) (keyArray[j] ^ 0x36);
			opadArray[j] = (byte) (keyArray[j] ^ 0x5C);
		}
		byte[] tempResult = sha1.getDigestOfBytes(join(ipadArray, data));
		return encode(tempResult);
	}

	private static byte[] join(byte[] b1, byte[] b2) {
		int length = b1.length + b2.length;
		byte[] newer = new byte[length];
		for (int i = 0; i < b1.length; i++) {
			newer[i] = b1[i];
		}
		for (int i = 0; i < b2.length; i++) {
			newer[i + b1.length] = b2[i];
		}
		return newer;
	}

	private static String sort(Map<String, String> params) {
		Map<String, String> result = new TreeMap<String, String>();

		Object[] unsort_key = params.keySet().toArray();
		Arrays.sort(unsort_key);
		for (Object obj : unsort_key) {
			result.put(obj.toString(), params.get(obj));
		}
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = result.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			sb.append(key + "=" + result.get(key) + "&");
		}

		return sb.toString();
	}

	private static String encode(byte[] from) {
		StringBuffer to = new StringBuffer((int) (from.length * 1.34) + 3);
		int num = 0;
		char currentByte = 0;
		for (int i = 0; i < from.length; i++) {
			num = num % 8;
			while (num < 8) {
				switch (num) {
				case 0:
					currentByte = (char) (from[i] & lead6byte);
					currentByte = (char) (currentByte >>> 2);
					break;
				case 2:
					currentByte = (char) (from[i] & last6byte);
					break;
				case 4:
					currentByte = (char) (from[i] & last4byte);
					currentByte = (char) (currentByte << 2);
					if ((i + 1) < from.length) {
						currentByte |= (from[i + 1] & lead2byte) >>> 6;
					}
					break;
				case 6:
					currentByte = (char) (from[i] & last2byte);
					currentByte = (char) (currentByte << 4);
					if ((i + 1) < from.length) {
						currentByte |= (from[i + 1] & lead4byte) >>> 4;
					}
					break;
				}
				to.append(encodeTable[currentByte]);
				num += 6;
			}
		}
		if (to.length() % 4 != 0) {
			for (int i = 4 - to.length() % 4; i > 0; i--) {
				to.append("=");
			}
		}
		return to.toString();
	}

	public static void main(String[] args) throws Exception {
		String key = "{SHA}fEqNCco3Yq9h5ZUglD3CZJT4lBs=";
		String str = "中华人民共和国ssssssssssssssssssssssssssssssssssssssssssssssssssssaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaassss";
		String res = sign(str, key);
		System.out.println(res);

	}

}
