package com.life.servlet;


import io.rong.util.HttpUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.life.controller.SmsDao;
import com.life.entity.Datas;
import com.life.utils.CONST;
import com.life.utils.URL;
import com.show.api.ShowApiRequest;

public class WeatherSerlvet extends HttpServlet {

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

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String action = request.getParameter("action");
		String weatherArea = request.getParameter("weatherArea");
		Gson gson = new Gson();
		Datas datas = new Datas();
         if("weather".equals(action)){
//        	 final String res = new ShowApiRequest("http://route.showapi.com/9-2",
//     				URL.YIYUAN_APPID, URL.YIYUAN_SECRET).addTextPara("area",
//     				weatherArea).addTextPara("needMoreDay", "0").addTextPara(
//     				"needIndex", "0").addTextPara("needHourData", "0").addTextPara(
//     				"need3HourForcast", "0").addTextPara("needAlarm", "0").post();
        	 out.print(getWeather(weatherArea,""));
         }
         else if("weatherHour".equals(action)){
        	 out.print(getWeather(weatherArea,"hour"));
         }
         else if("distance".equals(action)){
        	 String fromHospitalAddress = request.getParameter("fromHospitalAddress");
        	 String toHospitalAddress = request.getParameter("toHospitalAddress");
        	 
         }
         else{
        	 datas.setResult(CONST.SEND_FAIL);
        	 datas.setMsg("参数错误");
        	 out.print(gson.toJson(datas));
         }
		
		
		out.flush();
		out.close();
	}
	public String  getWeather(String area,String type) {
	    String host = "https://ali-weather.showapi.com";
	    String path = "/area-to-weather";
	    if("hour".equals(type)){
	    	path = "/hour24";
	    }

	    String method = "GET";
	    String appcode = "eaf3191467744512810610f412e0d77b";
	    String result="";
	    Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
	    Map<String, String> querys = new HashMap<String, String>();
	    querys.put("area", area);

	    querys.put("need3HourForcast", "0");
	    querys.put("needAlarm", "0");
	    querys.put("needHourData", "0");
	    querys.put("needIndex", "0");
	    querys.put("needMoreDay", "0");
         

	    try {
	    	/**
	    	* 重要提示如下:
	    	* HttpUtils请从
	    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
	    	* 下载
	    	*
	    	* 相应的依赖请参照
	    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
	    	*/
	    	HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
	    	result = EntityUtils.toString(response.getEntity());

	    	//获取response的body

	    	
	    	return result;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return result;
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

		doPost(request, response);
	}

}
