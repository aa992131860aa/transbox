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
import com.life.entity.BoxUse;
import com.life.entity.Datas;
import com.life.utils.CONST;

public class BoxServlet extends HttpServlet {

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

		String action = request.getParameter("action");
		BoxDao boxDao = new BoxDao();

		// 验证是否存在箱子并可用
		//是否是自动转运的箱子
		if ("start".equals(action)) {
			String boxNo = request.getParameter("boxNo");
			if (boxNo != null && !"".equals(boxNo)) {
				boolean isOK = boxDao.getBoxNoStatus(boxNo);
				if(isOK){
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("箱子可用");
				}else{
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("box.do start 箱子不可用");
				}
				
			}
			else{
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("box.do start 参数错误");
			}
			
		}
		//收集箱子信息
		else if ("device".equals(action)) {
			
			String device = request.getParameter("device");
			if(device!=null&&!"".equals(device)){
				int result = boxDao.setBoxIMEI(device);
				if(result==1){
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("box.do device ok");
					datas.setObj(device);
				}else{
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("box.do device 数据库问题错误");
				}
			}else{
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("box.do device 参数错误");
			}
			

		}
		//获取箱子信息
		else if ("boxUse".equals(action)) {
			
			String hospital = request.getParameter("hospital");
			if(hospital!=null&&!"".equals(hospital)){
				
				List<BoxUse> boxUses  = boxDao.getBoxUses(hospital);
				if(boxUses.size()>0){
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("box.do device ok");
					datas.setObj(boxUses);
				}else{
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("box.do device 数据库问题错误");
				}
			}else{
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("box.do device 参数错误");
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
