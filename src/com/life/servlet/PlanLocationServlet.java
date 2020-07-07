package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.BoxDao;
import com.life.controller.PlanLocationDao;
import com.life.entity.BoxUse;
import com.life.entity.Datas;
import com.life.utils.CONST;

public class PlanLocationServlet extends HttpServlet {

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
		response.addHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();

		Gson gson = new Gson();
		Datas datas = new Datas();

		String action = request.getParameter("action");
		PlanLocationDao planLocationDao = new PlanLocationDao();

		// 获取飞机的经纬度
		if ("gainLatLng".equals(action)) {
		 
				List<String> list= planLocationDao.getLngLats();
				if(list.size()>0){
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("箱子可用");
					datas.setObj(list);
				}else{
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("box.do start 箱子不可用");
				}
				
			
		}else if("insertAddress".equals(action)){
			String lnglat = request.getParameter("lnglat");
			String address = request.getParameter("address");
			String longitude =lnglat.split(",")[0];
			String latitude = lnglat.split(",")[1];
			boolean isInsert = planLocationDao.insertAddress(Double.parseDouble(latitude), Double.parseDouble(longitude), address);
			if(isInsert){
				datas.setResult(CONST.SEND_OK);
				datas.setMsg("插入成功");
				 
			}else{
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("插入失败");
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

}
