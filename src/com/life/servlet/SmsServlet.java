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

public class SmsServlet extends HttpServlet {

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
		response.addHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		String phone = request.getParameter("phone");
		SmsDao smsDao = new SmsDao();
		Datas datas = null;
		if(phone!=null){
			datas = smsDao.sendSms(phone);
		}else{
			datas = new Datas();
			datas.setResult(CONST.BAD_PARAM);
			datas.setMsg("参数错误");
			
		}

		Gson gson = new Gson();
		String datasJson = gson.toJson(datas);
		out.write(datasJson);
		out.flush();
		out.close();
	}
	public static void main(String[] args) {
	
	for(int i=0;i<90;i++){
		int num= 	(int) (Math.random()*33);

	}
	
	}

}
