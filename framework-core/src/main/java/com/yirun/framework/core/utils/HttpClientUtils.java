package com.yirun.framework.core.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description : 基于HttpClient实现服务端模拟请求http、https请求
 * @Project : finance-user-model
 * @Program Name : com.yirun.finance.user.utils.HttpClientUtil.java
 * @Author : zhichaoding@hongkun.com zc.ding
 */
public class HttpClientUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
	private static final String ENCODING = "UTF-8";

	/**
	 * @Description : 模拟http的post请求
	 * @Method_Name : httpPost
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据：String | Map<String, String>
	 * @return : String
	 * @Creation Date : 2017年5月31日 上午10:39:39
	 * @Author : zhichaoding@hongkun.com zc.ding
	 */
	public static String httpPost(String url, Object data) {
		HttpClient httpClient = HttpClients.createDefault();
		return simulateRequest(url, data, httpClient, "POST");
	}

	/**
	 * @Description : 模拟https的Post请求，空的证书链
	 * @Method_Name : httpsPost
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据 String|Map<String, String>
	 * @throws Exception
	 * @return : String
	 * @Creation Date : 2017年5月31日 上午10:40:27
	 * @Author : zhichaoding@hongkun.com zc.ding
	 */
	public static String httpsPost(String url, Object data) throws Exception {
		HttpClient httpClient = buildHttpClientByNullKeyStore();
		return simulateRequest(url, data, httpClient, "POST");
	}

	/**
	 * @Description : 通过Httpclient模拟https请求，需要加载请求认证证书
	 * @Method_Name : httpsPost
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据 String | Map<String, String>
	 * @param keyStorePath
	 *            证书库文件路径
	 * @param keyStorePwd
	 *            证书密码
	 * @param trustStorePath
	 *            信任证书库路径，可能同keyStorePath是一个
	 * @param trustStorePwd
	 *            信任证书库密码
	 * @return
	 * @return : String
	 * @Creation Date : 2017年5月31日 上午10:35:49
	 * @Author : zhichaoding@hongkun.com zc.ding
	 */
	public static String httpsPost(String url, Object data, String keyStorePath, String keyStorePwd,
			String trustStorePath, String trustStorePwd) {
		try {
			HttpClient httpClient = buildHttpClient(keyStorePath, keyStorePwd, trustStorePath, trustStorePwd);
			return simulateRequest(url, data, httpClient, "POST");
		} catch (Exception e) {
			logger.error("proxy http error!", e);
		}
		return null;
	}

	/**
	 * @Description : 模拟http的get请求
	 * @Method_Name : httpGet
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据 String|Map<String, String>
	 * @return : String
	 * @Creation Date : 2017年5月31日 上午10:41:36
	 * @Author : zhichaoding@hongkun.com zc.ding
	 */
	public static String httpGet(String url, Object data) {
		HttpClient httpClient = HttpClients.createDefault();
		return simulateRequest(url, data, httpClient, "GET");
	}

	/**
	 * @Description : 模拟https的get请求，空的证书链
	 * @Method_Name : httpGet
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求数据 String|Map<String, String>
	 * @return : String
	 * @throws Exception
	 * @Creation Date : 2017年5月31日 上午10:41:36
	 * @Author : zhichaoding@hongkun.com zc.ding
	 */
	public static String httpsGet(String url, Object data) throws Exception {
		HttpClient httpClient = buildHttpClientByNullKeyStore();
		return simulateRequest(url, data, httpClient, "GET");
	}

	/**
	 * @Description : 模拟请求
	 * @Method_Name : simulateRequest
	 * @param url
	 * @param data
	 * @param httpClient
	 * @param type
	 * @return
	 * @return : String
	 * @Creation Date : 2017年5月31日 下午3:05:15
	 * @Author : zhichaoding@hongkun.com zc.ding
	 */
	@SuppressWarnings("unchecked")
	private static String simulateRequest(String url, Object data, HttpClient httpClient, String type) {
		StringBuilder content = new StringBuilder();
		HttpRequestBase http = null;
		try {

			logger.info("请求的url: {}, data: {}, type: {}",new Object[]{url,data,type});
			if ("post".equalsIgnoreCase(type)) {
				http = new HttpPost(url);
				if (data instanceof String) {
					((HttpPost) http).setEntity(new StringEntity((String) data, ENCODING));
				} else if (data instanceof Map) {
					List<NameValuePair> list = new ArrayList<>();
					Map<String, String> map = ((Map<String, String>) data);
					for (String key : map.keySet()) {
						list.add(new BasicNameValuePair(key, map.get(key)));
					}
					((HttpPost) http).setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));
				}
			} else if ("get".equalsIgnoreCase(type)) {
				// 添加GET请求时处理MAP与String
				URI uri = null;
				if (data instanceof String) {
					uri = new URIBuilder(url + "?" + (String) data).build();
				} else if (data instanceof Map) {
					String param = toHttpGetParams((Map<String, String>) data);
					uri = new URIBuilder(url + "?" + param).build();
				}
				if (data == null) {
					uri = new URIBuilder(url).build();
				}
				http = new HttpGet(uri);
			}
			// 设置请求的连接配置
			http.setConfig(getRequestConfig(10000, true));
			// 执行请求并获得请求的响应
			HttpResponse response = httpClient.execute(http);
			HttpEntity respEntity = response.getEntity();
			int httpStatus = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == httpStatus && respEntity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(respEntity.getContent(), ENCODING));
				String line;
				while ((line = reader.readLine()) != null) {
					content.append(line);
				}
				logger.info("相应的结果: " + content);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return "error";
		} finally {
			if (http != null) {
				http.releaseConnection();
			}
		}
		return content.toString();
	}

	/**
	 * @Description : 这里只是其中的一种场景,也就是把参数用&符号进行连接且进行URL编码 根据实际情况拼接参数
	 * @Method_Name : toHttpGetParams;
	 * @param param
	 * @return
	 * @throws Exception
	 * @return : String;
	 * @Creation Date : 2017年6月16日 下午1:45:18;
	 * @Author : yanbinghuang@hongkun.com.cn 黄艳兵;
	 */
	private static String toHttpGetParams(Map<String, String> param) throws Exception {
		String res = "";
		if (param == null) {
			return res;
		}
		for (Map.Entry<String, String> entry : param.entrySet()) {
			res += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
		}
		return "".equals(res) ? "" : StringUtils.chop(res);
	}

	/**
	 * @Description : 构建没有证书的httpsClient客户端
	 * @Method_Name : buildHttpClientByNullKeyStore
	 * @return
	 * @throws Exception
	 * @return : HttpClient
	 * @Creation Date : 2017年5月31日 上午10:07:05
	 * @Author : zhichaoding@hongkun.com zc.ding
	 */
	static HttpClient buildHttpClientByNullKeyStore() throws Exception {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		};
		sslContext.init(null, new TrustManager[] { tm }, null);

		sslContext.getSupportedSSLParameters().setProtocols(new String[] { "TLSv1" });
		// (X509HostnameVerifier)
		// SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
		// 不验证证书中的主机ip是否和keystore中的主机ip一致
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext,
				(X509HostnameVerifier) SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		RegistryBuilder<ConnectionSocketFactory> rb = RegistryBuilder.create();
		rb.register("https", csf);
		org.apache.http.config.Registry<ConnectionSocketFactory> reg = rb.build();
		PoolingHttpClientConnectionManager pccm = new PoolingHttpClientConnectionManager(reg);
		HttpClientBuilder build = HttpClientBuilder.create();
		build.setConnectionManager(pccm);
		build.setDefaultRequestConfig(getRequestConfig(10000, true));
		return build.build();
	}

	/**
	 * @Description : 创建httpClient绑定证书，用于执行https请求
	 * @Method_Name : buildHttpClient
	 * @param keyStorePath
	 * @param keyStorePwd
	 * @param trustStorePath
	 * @param trustStorePwd
	 * @return
	 * @throws Exception
	 * @return : HttpClient
	 * @Creation Date : 2017年5月31日 上午10:12:15
	 * @Author : zhichaoding@hongkun.com zc.ding
	 */
	public static HttpClient buildHttpClient(String keyStorePath, String keyStorePwd, String trustStorePath,
			String trustStorePwd) throws Exception {
		// 加载证书库
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		InputStream in = new FileInputStream(keyStorePath);
		keyStore.load(in, keyStorePwd.toCharArray());
		in.close();
		// 加载信任库
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		in = new FileInputStream(trustStorePath);
		trustStore.load(in, trustStorePwd.toCharArray());
		in.close();
		/*
		 * 服务端tomcat 节点中clientAuth="false"时使用如下方式进行访问 SSLContext sslContext =
		 * SSLContexts.custom().useTLS().loadTrustMaterial(keyStore, new
		 * TrustStrategy() { public boolean isTrusted(X509Certificate[] chain,
		 * String authType) throws CertificateException {//信任所有 return true; }
		 * }).build();
		 */
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keyStore);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, keyStorePwd.toCharArray());

		SSLContext sslContext = SSLContext.getInstance("TLS");
		// 双向认证，服务端tomcat
		// 节点中clientAuth="true"，如果为false那么可以使用sslContext.init(null,
		// tmf.getTrustManagers(), null);进行配置
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		// (X509HostnameVerifier)
		// SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
		// 不验证证书中的主机ip是否和keystore中的主机ip一致
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext,
				(X509HostnameVerifier) SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		RegistryBuilder<ConnectionSocketFactory> rb = RegistryBuilder.create();
		rb.register("https", csf);
		org.apache.http.config.Registry<ConnectionSocketFactory> reg = rb.build();
		PoolingHttpClientConnectionManager pccm = new PoolingHttpClientConnectionManager(reg);
		// 是否配置连接池进行管理
		// HttpClient httpClient =
		// HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpClient httpClient = HttpClients.custom().setConnectionManager(pccm).build();
		return httpClient;
	}

	/**
	 * 设置请求的参数
	 * 
	 * @param time
	 * @param flag
	 * @return
	 */
	private static RequestConfig getRequestConfig(int time, boolean flag) {
		Builder build = RequestConfig.custom();
		build.setConnectTimeout(time);
		build.setSocketTimeout(time);
		build.setConnectionRequestTimeout(time);
		build.setExpectContinueEnabled(flag);
		build.setCookieSpec(CookieSpecs.IGNORE_COOKIES);
		return build.build();
	}

	/**
	 * 设置请求头部信息
	 * 
	 * @param httpPost
	 */
	void setHeadInfo(HttpPost httpPost) {
		httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate");
		httpPost.setHeader("Accept-Language", "en-US,en;q=0.5");
		httpPost.setHeader("Cache-Control", "max-age=0");
		httpPost.setHeader("Connection", "keep-alive");
		// httpPost.setHeader("Content-Type",
		// "application/x-www-form-urlencoded");
		httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:28.0) Gecko/20100101 Firefox/28.0");
	}

}
