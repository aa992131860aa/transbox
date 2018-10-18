package com.life.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

public class HttpUtils {
	static Gson gson;

	// 将JSON字符串转换成javabean
	public static <T> T parsr(String json, Class<T> tClass) {
		// 判读字符串是否为空
		if (TextUtils.isEmpty(json)) {
			return null;
		}

		if (gson == null) {
			gson = new Gson();
		}
		return gson.fromJson(json, tClass);
	}

	// 将javabean转换成JSON字符串
	public static String converJavaBeanToJson(Object obj) {
		if (obj == null) {
			return "";
		}
		if (gson == null) {
			gson = new Gson();
		}
		String beanstr = gson.toJson(obj);
		if (!TextUtils.isEmpty(beanstr)) {
			return beanstr;
		}
		return "";
	}

	// 发送JSON字符串 如果成功则返回成功标识。
	public static String doJsonPost(String urlPath, String Json) {
		// HttpClient 6.0被抛弃了
		String result = "";
		BufferedReader reader = null;
		try {
			java.net.URL url = new java.net.URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			// 设置文件类型:
			conn.setRequestProperty("Content-Type",
					"application/json; charset=UTF-8");
			// 设置接收类型否则返回415错误
			// conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
			conn.setRequestProperty("accept", "application/json");
			// 往服务器里面发送数据
			if (Json != null && !TextUtils.isEmpty(Json)) {
				byte[] writebytes = Json.getBytes();
				// 设置文件长度
				conn.setRequestProperty("Content-Length", String
						.valueOf(writebytes.length));
				OutputStream outwritestream = conn.getOutputStream();
				outwritestream.write(Json.getBytes());
				outwritestream.flush();
				outwritestream.close();

			}
			if (conn.getResponseCode() == 200) {
				reader = new BufferedReader(new InputStreamReader(conn
						.getInputStream()));
				result = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static String request(String httpUrl) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();

		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String strRead = reader.readLine();
			if (strRead != null) {
				sbf.append(strRead);
				while ((strRead = reader.readLine()) != null) {
					sbf.append("\n");
					sbf.append(strRead);
				}
			}
			reader.close();
			result = sbf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
