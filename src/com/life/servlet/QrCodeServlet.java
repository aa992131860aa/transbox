package com.life.servlet;

import io.rong.util.QRCodeUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.BoxDao;
import com.life.controller.QrImageDao;
import com.life.entity.BoxHosp;
import com.life.entity.Datas;
import com.life.utils.CONST;
import com.life.utils.URL;

public class QrCodeServlet extends HttpServlet {

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
		QrImageDao qrImageDao = new QrImageDao();
		
		

		// 验证是否存在箱子并可用
		if ("boxHosp".equals(action)) {
			
			String deviceId = request.getParameter("deviceId");
			BoxHosp boxHosp = qrImageDao.getBoxHosp(deviceId);
	
			
			if(boxHosp!=null){
				
				String upload = getServletContext().getRealPath("/");
				String url = "http://www.lifeperfusor.com/transbox/transportHtml/create/index.html#create?deviceId="+deviceId;
				url = "boxNo:"+new BoxDao().getBoxNo(deviceId);
				String path = upload+"images"+File.separator+deviceId+".png";
				
				
				String imgPath = upload+"images"+File.separator+"flag.png";
		    	QRCodeUtil.QRCodeCreate(url,path, 2, null);
		    	String qrImages = CONST.URL_PATH +"images"+File.separator+deviceId+".png";
		    	boxHosp.setQrImages(qrImages);
		    	datas.setResult(CONST.SEND_OK);
				datas.setMsg("ok");
				datas.setObj(boxHosp);
		    	
			}else{
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("qrCode.do 数据库错误");
			}
			
		}
		else {
			datas.setResult(CONST.BAD_PARAM);
			datas.setMsg("qrCode.do 没有相应的action");
		}
		out.write(gson.toJson(datas));
		
		
		
		out.flush();
		out.close();
	}

}
