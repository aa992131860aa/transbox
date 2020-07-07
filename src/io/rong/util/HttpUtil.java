package io.rong.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;
import com.life.entity.YunBaJson;
import com.life.utils.CONST;

public class HttpUtil {

    private static final String APPKEY = "RC-App-Key";
    private static final String NONCE = "RC-Nonce";
    private static final String TIMESTAMP = "RC-Timestamp";
    private static final String SIGNATURE = "RC-Signature";

    private static SSLContext sslCtx = null;

    static {

        try {
            sslCtx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslCtx.init(null, new TrustManager[]{tm}, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

            public boolean verify(String arg0, SSLSession arg1) {
                // TODO Auto-generated method stub
                return true;
            }

        });

        HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx.getSocketFactory());

    }

    // 设置body体
    public static void setBodyParameter(StringBuilder sb, HttpURLConnection conn) throws IOException {
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(sb.toString());
        out.flush();
        out.close();
    }

    public static HttpURLConnection CreateGetHttpConnection(String uri) throws MalformedURLException, IOException {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(30000);
        conn.setRequestMethod("GET");
        return conn;
    }

    public static void setBodyParameter(String str, HttpURLConnection conn) throws IOException {
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.write(str.getBytes("utf-8"));
        out.flush();
        out.close();
    }

    public static HttpURLConnection CreatePostHttpConnection(HostType hostType, String appKey, String appSecret, String uri,
                                                             String contentType) throws MalformedURLException, IOException, ProtocolException {
        String nonce = String.valueOf(Math.random() * 1000000);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        StringBuilder toSign = new StringBuilder(appSecret).append(nonce).append(timestamp);
        String sign = CodeUtil.hexSHA1(toSign.toString());
        uri = hostType.getStrType() + uri;
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        conn.setRequestProperty(APPKEY, appKey);
        conn.setRequestProperty(NONCE, nonce);
        conn.setRequestProperty(TIMESTAMP, timestamp);
        conn.setRequestProperty(SIGNATURE, sign);
        conn.setRequestProperty("Content-Type", contentType);

        return conn;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    public static String returnResult(HttpURLConnection conn) throws Exception, IOException {
        InputStream input = null;
        if (conn.getResponseCode() == 200) {
            input = conn.getInputStream();
        } else {
            input = conn.getErrorStream();
        }
        String result = new String(readInputStream(input), "UTF-8");
        return result;
    }

    public static String sendPushJson(String phone, String title, String content) {

        YunBaJson yunBaJson = new YunBaJson();
        yunBaJson.setMethod("publish_to_alias");
        yunBaJson.setAppkey(CONST.YUN_BA_APPKEY);
        yunBaJson.setSeckey(CONST.YUN_BA_SECKEY);
        yunBaJson.setAlias(phone);
        yunBaJson.setMsg(content);

        YunBaJson.OptsBean optsBean = new YunBaJson.OptsBean();
        optsBean.setQos(1);
        optsBean.setTime_to_live(36000);

        YunBaJson.OptsBean.ApnJsonBean apnJsonBean = new YunBaJson.OptsBean.ApnJsonBean();

        YunBaJson.OptsBean.ApnJsonBean.ApsBean apsBean = new YunBaJson.OptsBean.ApnJsonBean.ApsBean();
        apsBean.setAlert(title);
        apsBean.setBadge(0);
        apsBean.setSound("bingbong.aiff");

        apnJsonBean.setAps(apsBean);

        YunBaJson.OptsBean.ThirdPartyPushBean thirdPartyPushBean = new YunBaJson.OptsBean.ThirdPartyPushBean();
        thirdPartyPushBean.setNotification_title(title);
        thirdPartyPushBean.setNotification_content(content);

        optsBean.setThird_party_push(thirdPartyPushBean);
        optsBean.setApn_json(apnJsonBean);

        yunBaJson.setOpts(optsBean);
        String json = new Gson().toJson(yunBaJson);
        return json;

    }

    public static String sendPushJson(String[] phones, String title, String content) {

        YunBaJson yunBaJson = new YunBaJson();
        yunBaJson.setMethod("publish_to_alias_batch");
        yunBaJson.setAppkey(CONST.YUN_BA_APPKEY);
        yunBaJson.setSeckey(CONST.YUN_BA_SECKEY);
        yunBaJson.setAliases(phones);
        yunBaJson.setMsg(content);

        YunBaJson.OptsBean optsBean = new YunBaJson.OptsBean();
        optsBean.setQos(1);
        optsBean.setTime_to_live(36000);

        YunBaJson.OptsBean.ApnJsonBean apnJsonBean = new YunBaJson.OptsBean.ApnJsonBean();

        YunBaJson.OptsBean.ApnJsonBean.ApsBean apsBean = new YunBaJson.OptsBean.ApnJsonBean.ApsBean();
        apsBean.setAlert(title);
        apsBean.setBadge(0);
        apsBean.setSound("bingbong.aiff");

        apnJsonBean.setAps(apsBean);

        YunBaJson.OptsBean.ThirdPartyPushBean thirdPartyPushBean = new YunBaJson.OptsBean.ThirdPartyPushBean();
        thirdPartyPushBean.setNotification_title(title);
        thirdPartyPushBean.setNotification_content(content);

        optsBean.setThird_party_push(thirdPartyPushBean);
        optsBean.setApn_json(apnJsonBean);

        yunBaJson.setOpts(optsBean);
        String json = new Gson().toJson(yunBaJson);
        return json;

    }


    public static void sendJson(String pUrl, String json) {

        try {
            // 将JSON进行UTF-8编码,以便传输中文
            //String data = URLEncoder.encode(json, "GBK");
            String data = json;
            URL url = new URL(pUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");// 提交模式
            // 是否允许输入输出
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // 设置请求头里面的数据，以下设置用于解决http请求code415的问题
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "UTF-8");

            // 链接地址
            conn.connect();
            OutputStreamWriter writer = new OutputStreamWriter(conn
                    .getOutputStream(), "UTF-8");
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            // 发送参数
            bufferedWriter.write(data);

            // 清理当前编辑器的左右缓冲区，并使缓冲区数据写入基础流
            bufferedWriter.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String lines = reader.readLine();// 读取请求结果
            System.out.println("lines:" + lines);
            // JSONObject js=JSONObject.fromObject(lines);
            reader.close();
        } catch (IOException i) {

            i.printStackTrace();
        }
    }
}
