package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.SmsDao;
import com.life.entity.Datas;
import com.life.utils.CONST;

public class SendSmsServlet extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response. setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
	
		String action = request.getParameter("action");

		
		SmsDao smsDao = new SmsDao();
		Datas datas = new Datas();
		Gson gson = new Gson();
		
		if("sendTransfer".equals(action)){
			String content = request.getParameter("content");
			String phones = request.getParameter("phones");
			
			//屏蔽20公里,除了转运人
			if(content.contains("20公里")){
				phones = phones.split(",")[0];
			}
			if(content.contains("检测")){
				
			}else{
			smsDao.sendTransferSms(phones.split(","), content, phones);
			}
			datas.setResult(CONST.SEND_OK);
			
		}
		
		else if("sendListTransfer".equals(action)){
			String content = request.getParameter("content");
			String phones = request.getParameter("phones");
			String organSeg = request.getParameter("organSeg");
			smsDao.getSmsRecord(phones,content,organSeg);
		}
		else{
			
			datas.setResult(CONST.BAD_PARAM);
			datas.setMsg("参数错误");
			
		}
		//System.out.println("sms.do");
	
		String datasJson = gson.toJson(datas);
		out.write(datasJson);
		out.flush();
		out.close();
	}

}
