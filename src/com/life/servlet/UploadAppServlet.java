package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.UploadAppDao;
import com.life.entity.Datas;
import com.life.entity.UploadApp;
import com.life.utils.CONST;

public class UploadAppServlet extends HttpServlet {

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
		Gson gson = new Gson();
		Datas datas = new Datas();
		UploadAppDao uploadAppDao = new UploadAppDao();
		String action = request.getParameter("action");
		if ("pad".equals(action)) {
             UploadApp uploadApp = uploadAppDao.getUploadTop();
             if(uploadApp!=null){
            	 datas.setResult(CONST.SEND_OK);
                 datas.setMsg("获取正确");
                 datas.setObj(uploadApp);
             }else{
            	 datas.setResult(CONST.BAD_PARAM);
                 datas.setMsg("数据库错误:"+CONST.ERROR);
             }
		}
		else if ("padNew".equals(action)) {
			String deviceId = request.getParameter("deviceId");
            UploadApp uploadApp = uploadAppDao.getUploadTop(deviceId);
            if(uploadApp!=null){
           	 datas.setResult(CONST.SEND_OK);
                datas.setMsg("获取正确");
                datas.setObj(uploadApp);
            }else{
           	 datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("数据库错误:"+CONST.ERROR);
            }
		}
		else if ("app".equals(action)) {
            UploadApp uploadApp = uploadAppDao.getUploadAppTop();
            if(uploadApp!=null){
           	 datas.setResult(CONST.SEND_OK);
                datas.setMsg("获取正确");
                datas.setObj(uploadApp);
            }else{
           	 datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("数据库错误:"+CONST.ERROR);
            }
		}
		else {
             datas.setResult(CONST.BAD_PARAM);
             datas.setMsg("uploadApp.do  pad 没有相应的action");
		}

		out.write(gson.toJson(datas));
		out.flush();
		out.close();
	}

}
