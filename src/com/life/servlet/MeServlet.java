package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.MeDao;
import com.life.entity.Datas;
import com.life.utils.CONST;

public class MeServlet extends HttpServlet {

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
		response.setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();

		Gson gson = new Gson();
		Datas datas = new Datas();
	    MeDao meDao = new MeDao();
	    String action = request.getParameter("action");
	    if("feedback".equals(action)){
	    	String content = request.getParameter("content");
	    	String phone = request.getParameter("phone");
	    	if(content!=null&&!"".equals(content)&&phone!=null&&!"".equals(phone)){
	    		int result = meDao.insertFeedback(phone, content);
	    		if(result==1){
	    			datas.setResult(CONST.SEND_OK);
			    	datas.setMsg("feedback 数据库成功");
	    		}else{
	    			datas.setResult(CONST.SEND_FAIL);
			    	datas.setMsg("feedback 数据库错误");
	    		}
	    	}else{
	    		datas.setResult(CONST.BAD_PARAM);
		    	datas.setMsg("feedback 参数错误");
	    	}
	    	
	    }else{
	    	datas.setResult(CONST.BAD_PARAM);
	    	datas.setMsg("没有me.do action");
	    }
		out.print(gson.toJson(datas));
		out.flush();
		out.close();
	}

}
