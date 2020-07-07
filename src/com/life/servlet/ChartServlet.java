package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.entity.Datas;
import com.life.utils.CONST;

public class ChartServlet extends HttpServlet {

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
        String url = "http://116.62.28.28:8888/WebReport/ReportServer?formlet=power_line.frm&organSeg=";
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String action = request.getParameter("action");
		Gson gson = new Gson();
		Datas datas = new Datas();
		if("powerColumn".equals(action)){
			String organSeg = request.getParameter("organSeg");
			if(organSeg!=null&&!"".equals(organSeg)){
				 datas.setResult(CONST.SEND_OK);
				 datas.setMsg(url+organSeg);
			}else{
				 datas.setResult(CONST.BAD_PARAM);
			     datas.setMsg("chart.do powerColumn 参数错误");
			}
		}else if("powerLine".equals(action)){
			
		}else {
		     datas.setResult(CONST.BAD_PARAM);
		     datas.setMsg("chart.do action 不存在");
		}
		out.print(gson.toJson(datas));
		
		out.flush();
		out.close();
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
       doGet(request, response);
	}

}
