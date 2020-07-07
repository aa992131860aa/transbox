package com.life.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.life.entity.Miniprogram;
import com.life.entity.TemplateData;
import com.life.servlet.UrlServlet;

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
	public static void main(String[] args) {
		
		String url = UrlServlet.mUrl + CONST.ACCESS_TOKEN;

		String json = TemplateData.New().setTouser("onGLcwGb9L9v9KG1af44emRvsuJg")
				.setTemplate_id(UrlServlet.mModeSuccess)
				.setTopcolor("#743A3A").setUrl(url)
				.setMiniprogram(
						new Miniprogram(
								UrlServlet.mWxXiaoAppId,
								"pages/index/index"))
				.add("first", "转运创建成功", "#050505")
				.add("keyword1", "陈杨", "#1d4499")
				// .add("keyword2", "树兰(杭州)医院", "#1d4499")
				.add(
						"keyword3",
						"医院℃", "#1d4499")
				.add("keyword4",  "km", "#1d4499")
				.add("remark", "请各位配合密切关注转运情况", "#1d4499")
				.build();

		HttpUtils.doJsonPost(url, json);
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
//			conn.setRequestProperty("Content-Type",
//					"application/json; charset=UTF-8");
			conn.setRequestProperty("Content-Type",
			"UTF-8");
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
			is.close();
			connection.disconnect();
			reader.close();
			result = sbf.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	  // HTTP POST请求
    public static void sendPost(String url,String urlParameters) throws Exception {
    	  final String USER_AGENT = "Mozilla/5.0";
       // String url = "https://selfsolve.apple.com/wcResults.do";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //添加请求头
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        //发送Post请求
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果


    }
}
