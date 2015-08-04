package com.moxm.frameworks.samples.http;import android.util.Log;import org.apache.http.NameValuePair;import java.io.BufferedReader;import java.io.DataOutputStream;import java.io.IOException;import java.io.InputStreamReader;import java.io.OutputStream;import java.io.UnsupportedEncodingException;import java.net.HttpURLConnection;import java.net.MalformedURLException;import java.net.URL;import java.security.SecureRandom;import java.security.cert.CertificateException;import java.security.cert.X509Certificate;import java.util.List;import javax.net.ssl.HostnameVerifier;import javax.net.ssl.HttpsURLConnection;import javax.net.ssl.SSLContext;import javax.net.ssl.SSLSession;import javax.net.ssl.TrustManager;import javax.net.ssl.X509TrustManager;/** * Depiction: Http请求工具 * <p> * Modify:  * <p> * Author: Lynn * <p> * Create Date：2012-6-27 下午5:34:24 * <p> * @version 1.0 * @since 1.0 */public final class HttpExecute {	private static final String TAG = "HttpUtil";		private HttpExecute() {	}		/**	 * Post请求	 * 	 * @param url 请求地址	 * @param postParameters 请求参数	 * @return 结果String	 */	public static String post(String url, List<NameValuePair> postParameters) throws Exception {		HttpURLConnection conn = null;		StringBuffer resultData = null;		BufferedReader buffered = null;		try {			if (url.startsWith("https")) {				trustAllHosts();				HttpsURLConnection conns = (HttpsURLConnection) new URL(url).openConnection();				conns.setHostnameVerifier(DO_NOT_VERIFY);				conn = conns;			} else {				conn = (HttpURLConnection) new URL(url).openConnection();			}			conn.setRequestMethod("POST");			conn.setRequestProperty("User-Agent", "Android");			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");			conn.setConnectTimeout(20 * 1000);			conn.setReadTimeout(5 * 60 * 1000);			conn.setDoOutput(true);			conn.setDoInput(true);						OutputStream outStream = conn.getOutputStream();			outStream.write(buildParams(postParameters).getBytes("UTF8"));			outStream.flush();			outStream.close();						resultData = new StringBuffer();			int code = conn.getResponseCode();			if (code == 200) {				buffered = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));				String readData = null;				while ((readData = buffered.readLine()) != null) {					resultData.append(readData);				}			}		} catch (MalformedURLException e) {			throw new IllegalStateException("URL不正确", e);		} catch (UnsupportedEncodingException e) {			throw new IllegalStateException("远程连接出错", e);		} catch (IOException e) {			throw new IllegalStateException("请求返回参数异常", e);		} finally {			try {				if (buffered != null) {					buffered.close();				}			} catch (IOException e) {				Log.e(TAG, e.toString());			}			if (conn != null) {				conn.disconnect();				conn = null;			}		}		return resultData.toString();	}		/**	 * 上传文件的Post请求 	 * @param url 请求地址	 * @param postParameters 请求参数	 * @param file 图片对象	 * @return 结果String	 */	public static String post(String url, List<NameValuePair> postParameters, ImageFile file) throws Exception {		return post(url, postParameters, new ImageFile[]{ file });	}	public static String post(String url, List<NameValuePair> postParameters, ImageFile[] files) throws Exception {		HttpURLConnection conn = null;		StringBuffer resultData = null;		BufferedReader buffered = null;		String CHARSET = "UTF-8";		try {			if (url.startsWith("https")) {				trustAllHosts();				HttpsURLConnection conns = (HttpsURLConnection) new URL(url).openConnection();				conns.setHostnameVerifier(DO_NOT_VERIFY);				conn = conns;			} else {				conn = (HttpURLConnection) new URL(url).openConnection();			}			conn.setRequestMethod("POST");			conn.setRequestProperty("User-Agent", "Android");			conn.setRequestProperty("Charsert", CHARSET);			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");			conn.setConnectTimeout(60 * 1000);			conn.setReadTimeout(5 * 60 * 1000);			conn.setDoOutput(true);			conn.setDoInput(true);						String BOUNDARY = "--------------";			String MULTIPART_FORM_DATA = "multipart/form-data";			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY);						StringBuilder sb = new StringBuilder();			if(postParameters != null){				for(NameValuePair params : postParameters){					sb.append("--");					sb.append(BOUNDARY);					sb.append("\r\n");					sb.append("Content-Disposition: form-data; name=\"" + params.getName() + "\"\r\n");					sb.append("Content-Type: text/plain;charset=" + CHARSET + "\r\n");					sb.append("Content-Transfer-Encoding: 8bit" + "\r\n\r\n");					sb.append(params.getValue());					sb.append("\r\n");				}			}			DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());			outStream.write(sb.toString().getBytes("UTF-8"));			if(files != null){				for(ImageFile file : files){					byte[] content = file.getData();					StringBuilder split = new StringBuilder();					split.append("--");					split.append(BOUNDARY);					split.append("\r\n");					split.append("Content-Disposition: form-data;name=\""+ file.getParamname() +"\";filename=\"" + file.getName() + "\"\r\n");					split.append("Content-Type: " + file.getMIME() + "; charset=UTF-8\r\n");					split.append("Content-Transfer-Encoding: binary" + "\r\n\r\n");					outStream.write(split.toString().getBytes("UTF-8"));					outStream.write(content, 0, content.length);					outStream.write("\r\n".getBytes());				}			}			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();			outStream.write(end_data);			outStream.flush();			outStream.close();						resultData = new StringBuffer();			int code = conn.getResponseCode();			if (code == 200) {				buffered = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));				String readData = null;				while ((readData = buffered.readLine()) != null) {					resultData.append(readData);				}			}		} catch (MalformedURLException e) {			throw new IllegalStateException("URL不正确", e);		} catch (UnsupportedEncodingException e) {			throw new IllegalStateException("远程连接出错", e);		} catch (IOException e) {			throw new IllegalStateException("请求返回参数异常", e);		} finally {			try {				if (buffered != null) {					buffered.close();				}			} catch (IOException e) {				Log.e(TAG, e.toString());			}			if (conn != null) {				conn.disconnect();				conn = null;			}		}		return resultData.toString();	}		/**	 * Get请求	 * 	 * @param url 请求地址	 * @param postParameters 请求参数	 * @return 结果String	 */	public static String get(String url, List<NameValuePair> postParameters) {		HttpURLConnection conn = null;		StringBuffer resultData = null;		BufferedReader buffered = null;		try {			if (!url.endsWith("?") && postParameters != null) {				url += "?";			}			url = url + buildParams(postParameters);			if (url.startsWith("https")) {				trustAllHosts();				HttpsURLConnection conns = (HttpsURLConnection) new URL(url).openConnection();				conns.setHostnameVerifier(DO_NOT_VERIFY);				conn = conns;			} else {				conn = (HttpURLConnection) new URL(url).openConnection();			}			conn.setRequestMethod("GET");			conn.setRequestProperty("User-Agent", "Android");			conn.setConnectTimeout(20 * 1000);			conn.setReadTimeout(5 * 60 * 1000);			conn.setDoOutput(true);			conn.setDoInput(true);						resultData = new StringBuffer();			int code = conn.getResponseCode();			if (code == 200) {				buffered = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));				String readData = null;				while ((readData = buffered.readLine()) != null) {					resultData.append(readData);				}			}		} catch (MalformedURLException e1) {			Log.e(TAG, e1.toString());		} catch (UnsupportedEncodingException e2) {			Log.e(TAG, e2.toString());		} catch (IOException e3) {			Log.e(TAG, e3.toString());		} finally {			try {				if (buffered != null) {					buffered.close();				}			} catch (IOException e) {				Log.e(TAG, e.toString());			}			if (conn != null) {				conn.disconnect();				conn = null;			}		}		return resultData.toString();	}		//private method	private static String buildParams(List<NameValuePair> postParameters) {		if (postParameters == null || postParameters.size() == 0) {			return "";		}		StringBuilder sb = new StringBuilder();		if(postParameters != null) {			for(NameValuePair params : postParameters){				sb.append(params.getName());				sb.append("=");				sb.append(params.getValue());				sb.append("&");			}		}		return sb.toString().substring(0, sb.toString().length() - 1);	}		private static void trustAllHosts() {		TrustManager[] trustAllCerts = new TrustManager[] {			new X509TrustManager() {								@Override				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {				}								@Override				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {				}								@Override				public X509Certificate[] getAcceptedIssuers() {					return new X509Certificate[] {};				}			}		};				try {			SSLContext sc = SSLContext.getInstance("TLS");			sc.init(null, trustAllCerts, new SecureRandom());			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());		} catch (Exception e) {			Log.e(TAG, e.toString());		}	}		private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {		                                                    public boolean verify(String hostname, SSLSession session) {			                                                    return true;		                                                    }	                                                    };}