package com.life.servlet;

import io.rong.util.MessageUtil;
import io.rong.util.XmlUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.DocumentException;

import com.google.gson.Gson;
import com.life.controller.BoxDao;
import com.life.controller.UrlDao;
import com.life.encrypt.AES;
import com.life.encrypt.WxPKCS7Encoder;
import com.life.entity.AccessToken;
import com.life.entity.BoxUse;
import com.life.entity.Datas;
import com.life.entity.MenuButton;
import com.life.entity.Miniprogram;
import com.life.entity.TemplateData;
import com.life.entity.WxInfo;
 
import com.life.utils.CONST;
import com.life.utils.CheckSignatureUtil;
import com.life.utils.HttpUtils;

public class UrlServlet extends HttpServlet {

	/**
	 * {{first.DATA}} 转运人员：{{keyword1.DATA}} 目的地医院：{{keyword2.DATA}}
	 * 当地天气：{{keyword3.DATA}} 直线距离：{{keyword4.DATA}} {{remark.DATA}}
	 */

	private String mModeSuccess = "dfnd-qco_pCuUMqRnaMkszBVjU2lv-1VmWYckSbF5jA";
	private String mUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
	private String mWxXiaoAppId = "wx323507bed41c42c8";
	private static final String token = "transbox";
	

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if ("event".equals(action)) {
			//接收微信用来校验信息的内置规定参数
			String signature = request.getParameter("signature");
			String timestamp = request.getParameter("timestamp");
			String nonce = request.getParameter("nonce");
			String echostr = request.getParameter("echostr");
			System.out.println("signature:" + signature);
			System.out.println("timestamp:" + timestamp);
			System.out.println("nonce:" + nonce);
			System.out.println("echostr:" + echostr);
			PrintWriter out = response.getWriter();
			//按微信指定的规则进行校验并做出响应
			if(CheckSignatureUtil.checkSignature(signature, timestamp, nonce)){
				out.print(echostr);
				System.out.println("echostr:"+echostr);
			} 
		} else {
			doPost(request, response);
		}
		System.out.println("action:"+action);

	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		Gson gson = new Gson();
		Datas datas = new Datas();

		String action = request.getParameter("action");
		UrlDao urlDao = new UrlDao();
	

		/**
		 * 获取微信的access_token
		 */
		if ("getAccessToken".equals(action)) {
			String phone = request.getParameter("phone");
			String openId = urlDao.getOTQCOpenIdByPhone(phone);
		 
			if(!"".equals(openId)){
				String url = mUrl + CONST.ACCESS_TOKEN;
			 
				String json = TemplateData.New()
				 .setTouser(openId)
				 .setTemplate_id(mModeSuccess)
				 .setTopcolor("#743A3A").setUrl(url)
				 .setMiniprogram(new Miniprogram(mWxXiaoAppId,"index"))
				 .add("first", "转运创建成功", "#050505")
				 .add("keyword1", "陈杨", "#1d4499")
				 //.add("keyword2", "树兰(杭州)医院", "#1d4499")
				 .add("keyword3", "杭州市，雷阵雨，最高温度36度，最低温度28度", "#1d4499")
				 .add("keyword4", "500km", "#1d4499")
				 .add("remark", "请各位配合密切关注转运情况", "#1d4499").build();
				
				String result = HttpUtils.doJsonPost(url, json);
			
				datas.setMsg(result);
			}
	

		
		} else if ("encrypt".equals(action)) {
			// WechatOpenIdRes wechatInfo = getWehatInfoByCode(code);
			String encryptedData = request.getParameter("encryptedData");
			String sessionKey = request.getParameter("sessionKey");
			String iv = request.getParameter("iv");
			String phone = request.getParameter("phone");
			System.out.println("encryptedData:" + encryptedData);

			boolean isNew = true;
			try {
				AES aes = new AES();
				byte[] resultByte = aes.decrypt(Base64
						.decodeBase64(encryptedData), Base64
						.decodeBase64(sessionKey), Base64.decodeBase64(iv));
				if (null != resultByte && resultByte.length > 0) {
					String userInfo = new String(WxPKCS7Encoder
							.decode(resultByte));
					System.out.println(userInfo);
					WxInfo wxInfo = new Gson().fromJson(userInfo, WxInfo.class);
					if (wxInfo.getUnionId() != null
							&& !"".equals(wxInfo.getUnionId())) {
						urlDao.updateUnionIdByPhone(phone, wxInfo.getUnionId());
					}
					// if(wxInfo != null) {
					// logger.debug("xxxxxunionid===="+wxInfo.getUnionId());
					// }
				}
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("event".equals(action)) {

			 
			String message = "success";
			try {
				//把微信返回的xml信息转义成map
				Map<String, String> map = XmlUtil.xmlToMap(request);
				String fromUserName = map.get("FromUserName");//消息来源用户标识
				String toUserName = map.get("ToUserName");//消息目的用户标识
				String msgType = map.get("MsgType");//消息类型
				String content = map.get("Content");//消息内容
				
				String eventType = map.get("Event");
				if(MessageUtil.MSGTYPE_EVENT.equals(msgType)){//如果为事件类型
					if(MessageUtil.MESSAGE_SUBSCIBE.equals(eventType)){//处理订阅事件
						boolean isTrue = urlDao.isExistWechatInfo(fromUserName);
						if(!isTrue){
							//根据微信公众号的openId获取unionId
							String userInfoResult = getOTQCUnionId(fromUserName);
							System.out.println("userInfoResult:"+userInfoResult);
							WeixinInfo  weixinInfo = new Gson().fromJson(userInfoResult, WeixinInfo.class);
							urlDao.updateOrInsertWechatInfo(fromUserName, toUserName, msgType, content, weixinInfo.getUnionid(), userInfoResult);
						}
						
						//自定义菜单
						MenuButton menuButton = new MenuButton();
						List<MenuButton.ButtonBean> buttonList = new ArrayList<MenuButton.ButtonBean>();
						MenuButton.ButtonBean button = new MenuButton.ButtonBean();
						button.setAppid(mWxXiaoAppId);
						button.setName("新建转运");
						button.setPagepath("pages/index/index");
						button.setType("miniprogram");
						button.setUrl("https://www.lifeperfusor.com");
						buttonList.add(button);
						
						button = new MenuButton.ButtonBean();
						 
						button.setName("莱普晟医疗");
						button.setType("view");
						button.setUrl("https://www.lifeperfusor.com");
						buttonList.add(button);
						
						menuButton.setButton(buttonList);
						
						String menuUrl="https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+CONST.ACCESS_TOKEN;
						HttpUtils.doJsonPost(menuUrl, new Gson().toJson(menuButton));
						
						message = MessageUtil.subscribeForText(toUserName, fromUserName);
					}else if(MessageUtil.MESSAGE_UNSUBSCIBE.equals(eventType)){//处理取消订阅事件
						message = MessageUtil.unsubscribe(toUserName, fromUserName);
					}
				}
				System.out.println("mesage:"+message+",event:"+eventType);
			
			} catch (DocumentException e) {
				e.printStackTrace();
				System.out.println("error:"+e.getMessage());
			}finally{
				out.println(message);
				System.out.println("mesage:"+message);
				if(out!=null){
					out.close();
				}
			
			}
		}

		else {
			datas.setResult(CONST.BAD_PARAM);
			datas.setMsg("box.do 没有相应的action");
		}
		out.write(gson.toJson(datas));

		out.flush();
		out.close();
	}


	public static  String getAccessToken() {
		// https://api.weixin.qq.com/sns/jscode2session?appid=wx323507bed41c42c8&secret=b0d04f48fb57d4c73ab42652b59037d2&js_code='+res.code+'&grant_type=authorization_code

		String httpUrl = "https://api.weixin.qq.com/cgi-bin/token";

		StringBuffer httpArg = new StringBuffer();

		httpArg.append("appid=").append("wx8d93a4fbade11124").append("&");
		httpArg.append("secret=").append("5391bdbaf15a46bdc6ab8e7d6d8bbb45")
				.append("&");
		httpArg.append("grant_type=").append("client_credential");
		String result = HttpUtils.request(httpUrl + "?" + httpArg.toString());

		return result;

	}
	
	public String getOTQCUnionId(String openId){
		//	https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
		String httpUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+CONST.ACCESS_TOKEN+"&openid="+openId+"&lang=zh_CN";

	
		String result = HttpUtils.request(httpUrl);

		return result;
	}

}
