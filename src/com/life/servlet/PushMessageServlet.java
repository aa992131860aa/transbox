package com.life.servlet;

import com.life.controller.UserDao;
import io.rong.util.HttpUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.PushMessageDao;
import com.life.controller.RongDao;
import com.life.entity.Datas;
import com.life.entity.YunBaJson;
import com.life.push.PushExample;
import com.life.utils.CONST;

public class PushMessageServlet extends HttpServlet {

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
		doPost(request, response);
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
		response.addHeader("Access-Control-Allow-Origin", "*");

		// String push_content = request.getParameter("push_content");
		// if (push_content != null) {
		// //保存到数据库
		// String sql =
		// "insert into push(content,create_time,phone,type,other_id) values(?,?)";
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// String create_time = sdf.format(new Date());
		// Object [] params = {push_content,create_time};
		// new ConnectionDB().executeUpdate(sql, params);
		// //发送推送消息
		// PushExample.sendPush(push_content);
		// }
		String action = request.getParameter("action");
		Datas datas = new Datas();
		PushMessageDao pushMessageDao = new PushMessageDao();
		Gson gson = new Gson();
		if (action != null && !"".equals(action)) {

			if ("add".equals(action)) {


				String phone = request.getParameter("phone");
				String content = request.getParameter("content");
				String otherId = request.getParameter("otherId");
				String type = request.getParameter("type");
				String targetPhone = request.getParameter("targetPhone");

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String createTime = sdf.format(new Date());
				if (!"".equals(phone) && phone != null && !"".equals(content)
						&& content != null && !"".equals(otherId)
						&& otherId != null && !"".equals(type) && type != null
						&& !"".equals(targetPhone) && targetPhone != null) {
					int isOk = pushMessageDao.insertPush(content, createTime,
							phone, type, otherId,targetPhone);
					if (isOk == 1) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("插入推送内容成功");
						//modify
						//PushExample.sendPush(content, targetPhone);
						
						String json = HttpUtil.sendPushJson(targetPhone, "系统消息", content);
						HttpUtil.sendJson(com.life.utils.URL.YUN_BA, json);
						
					} else {
						datas.setResult(CONST.SEND_FAIL);
						datas.setMsg("数据库错误");
					}

				} else {
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("action 参数错误");

				}
				
			
				
				

			}
			
			else if ("sendPushTransfer".equals(action)) {
				String organSeg = request.getParameter("organSeg");
				String type = request.getParameter("type");
				if (!"".equals(organSeg) && null != organSeg) {
					String phones = new RongDao().getPhonesByGroupId(organSeg);
					if ("delete".equals(type)) {
						PushExample.sendPush("delete:" + organSeg, phones.split(","));
					} else if ("exitGroup".equals(type)) {
						PushExample.sendPush("exitGroup:" + organSeg, phones.split(","));
					} else {
						PushExample.sendPush("transfer:" + organSeg, phones.split(","));
					}

				}
			}else if ("sendPushDelConversation".equals(action)) {
				String phone = request.getParameter("phone");
				String type = request.getParameter("type");
				if (!"".equals(phone) && null != phone) {
					 
				
						PushExample.sendPushCustom(type +":" + phone, phone);
					

				}
			}
			else if ("clearUnreadPushMessageNum".equals(action)) {
				String user_info_id = request.getParameter("user_info_id");
			    pushMessageDao.clearUnreadPushMessageNum(user_info_id);
			    new UserDao().updatePushPostion(user_info_id);
		
			}

			else {
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("未知的action");
			}

		} else {
			datas.setResult(CONST.BAD_PARAM);
			datas.setMsg("action 参数错误");

		}
		out.write(gson.toJson(datas));
		out.flush();
		out.close();
	}
	
}
