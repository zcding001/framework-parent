package com.yirun.framework.core.utils.crypto;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;

/**
 * @Description   : 非对称加密，解密工具类
 * @Project       : framework-core
 * @Program Name  : com.yirun.framework.core.utils.crypto.RSAUtils.java
 * @Author        : imzhousong@gmail.com 周松
 */
public class RSAUtils {
	/**
	 * 数字签名
	 * 密钥算法
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 数字签名
	 * 签名/验证算法
	 */
	public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	/**
	 * RSA密钥长度 默认1024位，密钥长度必须是64的倍数， 范围在512至65536位之间。
	 */
	private static final int KEY_SIZE = 1024;
	 
	/**
	 * @description  产生密钥对
	 * @return
	 * String [0] = RSAPublicKey 公钥
	 * String [1] = RSAPrivateKey 私钥
	 * @throws NoSuchAlgorithmException 
	 */
	public static String [] genSecretKeyPair() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(KEY_SIZE);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		return new String[]{Base64.encodeBase64String(publicKey.getEncoded()), Base64.encodeBase64String(privateKey.getEncoded())};
	}
	 
	/**
	 * 公钥验证数据
	 * @param data 待校验数据
	 * @param publicKey 公钥
	 * @param sign 数字签名
	 * @return boolean 校验成功返回true 失败返回false
	 * @throws Exception
	 */
	public static boolean verify(byte[] data, String publicKey, byte[] sign)
				throws Exception {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);
		return signature.verify(sign);
	}
	 
	/**
	 * 私钥签名数据
	 * @param data 待签名数据
	 * @param privateKey 私钥
	 * @return byte[] 数字签名
	 * @throws Exception
	 */
	public static byte[] sign(byte[] data, String privateKey) throws Exception {
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);
		return signature.sign();
	}
	 
	/**
	 * 公钥加密
	 * @param data 待加密数据
	 * @param publicKey 公钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String  publicKey)
			throws Exception {
		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicKeyObj = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKeyObj);
		// 加密时超过117字节就报错。为此采用分段加密的办法来加密
		byte [] bufRes = new byte[]{};
		for (int i = 0; i < data.length; i += 100) {
			byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i,i + 100));
			bufRes = ArrayUtils.addAll(bufRes, doFinal);
		}
		return Base64.encodeBase64(bufRes);
	}

	/**
	 * 私钥加密
	 * @param data 待加密数据
	 * @param privateKey 私钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
			throws Exception {
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateKeyObj = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKeyObj);
		byte [] bufRes = new byte[]{};
		for (int i = 0; i < data.length; i += 100) {
			byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i,i + 100));
			bufRes = ArrayUtils.addAll(bufRes, doFinal);
		}
		 return  Base64.encodeBase64(bufRes);
	}
	
	/**
	 * 公钥解密
	 * @param data 待解密数据
	 * @param key 公钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String publicKey)
			throws Exception {
		data = Base64.decodeBase64(data);
		
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicKeyObj = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKeyObj);
		byte [] bufRes = new byte[]{};
		for (int i = 0; i < data.length; i += 128) {
			byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i,
					i + 128));
			bufRes = ArrayUtils.addAll(bufRes, doFinal);
		}
		return  bufRes;
	}
	
	/**
	 * 私钥解密
	 * @param data 待解密数据
	 * @param key 私钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String privateKey)
			throws Exception {
		data = Base64.decodeBase64(data);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateKeyObj = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKeyObj);
		byte [] bufRes = new byte[]{};
		for (int i = 0; i < data.length; i += 128) {
			byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i,
					i + 128));
			bufRes = ArrayUtils.addAll(bufRes, doFinal);
		}
		return bufRes;
	}
	
	public static void main(String[] args) throws Exception {
		String [] ary = genSecretKeyPair();
		String publicKey = ary[0];
		String privateKey = ary[1];
		System.out.println(publicKey);
		System.out.println(privateKey);
		String str = "{\"accordLawBid\":1,\"approvedAgencyCode\":\"\",\"approvedAgencyName\":\"\",\"areaCode\":\"1001,110000,110101,110100\",\"attachmentModels\":[],\"bsCode\":\"0618-124123123a\",\"bsContent\":\"#$%^&*(我我我\",\"bsContentEn\":\"111111111111111A\",\"bsCreateDate\":\"2012-12-21 10:11:36\",\"bsName\":\"123123a\",\"bsNameEn\":\"123123a\",\"chargeUnit\":\"123123a\",\"contentScope\":\"#$%^&*(我我我\",\"contentScopeEn\":\"111111111111111A\",\"contractEstimateDollar\":0,\"contractEstimatePrice\":0,\"creatorBiddingNumber\":\"1100090911\",\"entrustAuthorizeExplain\":\"123123a123123a123123a我4%^&*(\",\"entrustDate\":\"\",\"entrustMoney\":1,\"fundNature\":1,\"fundProvider\":\".\",\"fundSource\":1,\"industryCode\":\"14\",\"inviteObjects\":[],\"inviteReason\":\"\",\"loanCode\":\"\",\"loanCountries\":[],\"otherRegulatoryAgencies\":[],\"pfUniqueId\":\"\",\"planExplain\":\"\",\"platformCode\":\"\",\"platformId\":0,\"prequalification\":2,\"prjAddress\":\"\",\"prjCode\":\"2012122110113600131\",\"prjLegal\":\"\",\"prjName\":\"123123a\",\"prjNameEn\":\"123123a\",\"prjNature\":0,\"prjPropertiesCode\":\"\",\"prjScale\":\"\",\"proxyAgencyBiddingNumber\":\"1100090911\",\"recordNo\":\"\",\"registrationStatus\":0,\"regulatoryAgencyId\":0,\"responsiblePerson\":\"\",\"shield\":1,\"tenderCategory\":1,\"tenderForm\":1,\"tenderMode\":1,\"tenders\":[{\"biddingNumber\":\"15368994\"}],\"totalInvestment\":1,\"totalInvestmentDollar\":1,\"tpCode\":\"2012122110113600131\",\"tpCreateDate\":\"2012-12-21 10:11:36\",\"tpName\":\"123123a\",\"tpNameEn\":\"123123a\",\"tpOperator\":\"\",\"tpRemark\":\"\",\"twoStageBid\":2,\"typeCode\":0,\"workflowCode\":\"\"}";
		
		byte [] buf = encryptByPublicKey(Base64.encodeBase64(str.getBytes()), publicKey);
		
		System.out.println(new String(buf));
		
		byte [] res = decryptByPrivateKey(buf, privateKey);
		
		System.out.println(new String(Base64.decodeBase64(res)));
	}
}

