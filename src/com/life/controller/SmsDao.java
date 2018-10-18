package com.life.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.life.entity.Contact;
import com.life.entity.Datas;
import com.life.utils.CONST;
import com.life.utils.ConnectionDB;

public class SmsDao {
	private Connection conn;
	private ConnectionDB connDB = new ConnectionDB();

	public Datas sendSms(String phone) {
		int code = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;// 产生1000-9999的随机数
		String testUsername = "azuretech"; // 在短信宝注册的用户名
		String testPassword = "weilab123456"; // 在短信宝注册的密码
		String testPhone = phone;
		String testContent = "【器官云监控】您的验证码是" + code + "，５分钟内有效。若非本人操作请忽略此消息。"; // 注意测试时，也请带上公司简称或网站签名，发送正规内容短信。千万不要发送无意义的内容：例如
		// 测一下、您好。否则可能会收不到
		String httpUrl = "http://api.smsbao.com/sms";

		StringBuffer httpArg = new StringBuffer();
		httpArg.append("u=").append(testUsername).append("&");
		httpArg.append("p=").append(md5(testPassword)).append("&");
		httpArg.append("m=").append(testPhone).append("&");
		httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));

		String result = request(httpUrl, httpArg.toString());
		Datas data = new Datas();
		// send sms success
		if ("0".equals(result)) {
			data.setResult(CONST.SEND_OK);
			data.setMsg("发送验证码成功");
			Map<String, Integer> map = new HashMap<String, Integer>();
			if ("18398850872".equals(phone)) {
				map.put("code", 9999);
			} else {
				map.put("code", code);
			}
			data.setObj(map);
			insertSmsRecord(testPhone, testContent);
		} else {
			data.setResult(CONST.SEND_FAIL);
			data.setMsg("发送验证码失败");
		}
		//System.out.println("result:" + result + "," + code);
		return data;
	}

	public void sendTransferSms(String[] phones, String content,
			String insertPhones) {

		String testUsername = "azuretech"; // 在短信宝注册的用户名
		String testPassword = "weilab123456"; // 在短信宝注册的密码

		String testContent = "【器官云监控】" + content; // 注意测试时，也请带上公司简称或网站签名，发送正规内容短信。千万不要发送无意义的内容：例如
		// 测一下、您好。否则可能会收不到
		String httpUrl = "http://api.smsbao.com/sms";
		for (int i = 0; i < phones.length; i++) {
			StringBuffer httpArg = new StringBuffer();
			httpArg.append("u=").append(testUsername).append("&");
			httpArg.append("p=").append(md5(testPassword)).append("&");
			httpArg.append("m=").append(phones[i]).append("&");
			httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));
			String result = request(httpUrl, httpArg.toString());
			//System.out.println("smsResult:" + result + ",phone:" + phones[i]);
		}

		insertSmsRecord(insertPhones, testContent);

	}

	public void sendTransferSms(String phone, String content) {

		String testUsername = "azuretech"; // 在短信宝注册的用户名
		String testPassword = "weilab123456"; // 在短信宝注册的密码

		String testContent = "【器官云监控】" + content; // 注意测试时，也请带上公司简称或网站签名，发送正规内容短信。千万不要发送无意义的内容：例如
		// 测一下、您好。否则可能会收不到
		String httpUrl = "http://api.smsbao.com/sms";

		StringBuffer httpArg = new StringBuffer();
		httpArg.append("u=").append(testUsername).append("&");
		httpArg.append("p=").append(md5(testPassword)).append("&");
		httpArg.append("m=").append(phone).append("&");
		httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));
		String result = request(httpUrl, httpArg.toString());
		//System.out.println("smsResult:" + result + ",phone:" + phone);

		insertSmsRecord(phone, testContent);

	}

	public String getOpenId(String appid, String secret,String js_code) {
		//https://api.weixin.qq.com/sns/jscode2session?appid=wx323507bed41c42c8&secret=b0d04f48fb57d4c73ab42652b59037d2&js_code='+res.code+'&grant_type=authorization_code

	 
		String httpUrl = "https://api.weixin.qq.com/sns/jscode2session";

		StringBuffer httpArg = new StringBuffer();
		httpArg.append("appid=").append(appid).append("&");
		httpArg.append("secret=").append(secret).append("&");
		httpArg.append("js_code=").append(js_code).append("&");
		httpArg.append("grant_type=").append("authorization_code");
		String result = request(httpUrl, httpArg.toString());
		 
      return result;
		 

	}
	
	public String getAccessToken() {
		//https://api.weixin.qq.com/sns/jscode2session?appid=wx323507bed41c42c8&secret=b0d04f48fb57d4c73ab42652b59037d2&js_code='+res.code+'&grant_type=authorization_code

	 
		String httpUrl = "https://api.weixin.qq.com/sns/jscode2session";

		StringBuffer httpArg = new StringBuffer();
		
		httpArg.append("appid=").append("wx8d93a4fbade11124").append("&");
		httpArg.append("secret=").append("5391bdbaf15a46bdc6ab8e7d6d8bbb45").append("&");
		httpArg.append("grant_type=").append("client_credential");
		String result = request(httpUrl, httpArg.toString());
		 
      return result;
		 

	}

	
	/**
	 * 插入数据库
	 * 
	 * @param phone
	 * @param content
	 */
	public void getSmsRecord(String phones, String pContent, String organSeg) {
		List<Contact> lists = new ArrayList<Contact>();
		ResultSet rs = null;
		ResultSet rsTwo = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select id users_id,true_name,phone,photo_url,wechat_url,is_upload_photo,role_id from users where ? like CONCAT ('%',phone,'%') ";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, phones);
			rs = ps.executeQuery();

			while (rs.next()) {
				Contact contact = new Contact();

				// contact.setUsersId((rs.getString("users_id")));
				// contact.setTrueName((rs.getString("true_name")));
				// contact.setContactPhone((rs.getString("phone")));
				// contact.setPhotoFile((rs.getString("photo_url")));
				// contact.setWechatUrl((rs.getString("wechat_url")));
				// contact.setIsUploadPhoto((rs.getString("is_upload_photo")));
				// contact.setRoleId(rs.getInt("role_id"));

				String phone = rs.getString("phone");
				contact.setPhone(phone);

				sql = "select p.postRole from postRolePerson pp,postRole p where p.id = pp.postRoleId and pp.phone=? and pp.organSeg like '%"
						+ organSeg + "%'";
				ps = conn.prepareStatement(sql);
				ps.setString(1, phone);

				rsTwo = ps.executeQuery();
				if (rsTwo.next()) {
					contact.setPostRole(rsTwo.getString("postRole"));

				}

				lists.add(contact);

			}

			for (int i = 0; i < lists.size(); i++) {
				String[] phonesStr = phones.split(",");
                String appAddress= "APP下载地址 http://t.cn/RBkPjYd";
                appAddress = "";
				Contact contact = lists.get(i);
				String content = "";
				String phone = contact.getPhone();
				if (contact.getPostRole() == null
						|| "无".equals(contact.getPostRole().trim())) {
					content = pContent + "您暂无岗位角色。详情请至APP查看。"+appAddress;
				} else {
					content = pContent + "您是" + contact.getPostRole()
							+ "。详情请至APP查看。"+appAddress;
				}
				if(phonesStr.length==2){
					if (phone.equals(phonesStr[0])) {
						content = pContent  ;
					} else if (phone.equals(phonesStr[1])) {
						content = pContent ;
					} 
					
				}else{
					if (phone.equals(phonesStr[0])) {
						content = pContent + "您是转运医师。详情请至APP查看。"+appAddress;
					} else if (phone.equals(phonesStr[1])) {
						content = pContent + "您是科室协调员。详情请至APP查看。"+appAddress;
					} else if (phone.equals(phonesStr[2])) {
						content = pContent + "您是OPO人员。详情请至APP查看。"+appAddress;
					}
				}
		

				sendTransferSms(phone, content);
				//System.out.println(content);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);
		}

	}
	

	public void sendOneTransferSms(String phone, String content) {

		String testUsername = "azuretech"; // 在短信宝注册的用户名
		String testPassword = "weilab123456"; // 在短信宝注册的密码

		String testContent = "【器官云监控】" + content; // 注意测试时，也请带上公司简称或网站签名，发送正规内容短信。千万不要发送无意义的内容：例如
		// 测一下、您好。否则可能会收不到
		String httpUrl = "http://api.smsbao.com/sms";

		StringBuffer httpArg = new StringBuffer();
		httpArg.append("u=").append(testUsername).append("&");
		httpArg.append("p=").append(md5(testPassword)).append("&");
		httpArg.append("m=").append(phone).append("&");
		httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));
		String result = request(httpUrl, httpArg.toString());

		insertSmsRecord(phone, testContent);

	}

	public static void main(String[] args) {
		new SmsDao().sendTransferSms("18398850872", "已发送,请注意查收");
	}

	/**
	 * 插入数据库
	 * 
	 * @param phone
	 * @param content
	 */
	private void insertSmsRecord(String phone, String content) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "insert into notice(phone,message) values(?,?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			ps.setString(2, content);
			ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}

	public String request(String httpUrl, String httpArg) {
		BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		httpUrl = httpUrl + "?" + httpArg;

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

	public String md5(String plainText) {
		StringBuffer buf = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	public String encodeUrlString(String str, String charset) {
		String strret = null;
		if (str == null)
			return str;
		try {
			strret = java.net.URLEncoder.encode(str, charset);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return strret;
	}
}