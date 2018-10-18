package com.life.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.gson.Gson;
import com.life.controller.UserDao;
import com.life.entity.Datas;
import com.life.utils.CONST;

public class UploadFileServlet extends HttpServlet {

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		

		// 创建文件项目工厂对象
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// 设置文件上传路径
		String upload = this.getServletContext().getRealPath(File.separator+"images"+File.separator);
		// 获取系统默认的临时文件保存路径，该路径为Tomcat根目录下的temp文件夹
		String temp = System.getProperty("java.io.tmpdir");
		// 设置缓冲区大小为 5M
		factory.setSizeThreshold(1024 * 1024 * 5);
		// 设置临时文件夹为temp
		factory.setRepository(new File(temp));
		// 用工厂实例化上传组件,ServletFileUpload 用来解析文件上传请求
		ServletFileUpload servletFileUpload = new ServletFileUpload(factory);

		// 解析结果放在List中
		try {
			List<FileItem> list = servletFileUpload.parseRequest(request);
            String photoName = "";
            String phone = "";
            String flag = "";
			for (FileItem item : list) {
				String name = item.getFieldName();
				InputStream is = item.getInputStream();

				 if (name.contains("photoFile")) {
					//System.out.println("file");
					try {
//						System.out.println("photoFile:"+item.getName());
//						System.out.println("photoFileStr:"+item.getString());
//						System.out.println("is:"+is);
//						if(item.getName()==null){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
							String date =sdf.format(new Date());
							photoName = date+".jpg";
							inputStream2File(is, upload + File.separator + photoName);
//						}else{
//							inputStream2File(is, upload + "\\" + item.getName());
//							photoName = item.getName();
//						}
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(name.contains("phone")){
					phone = item.getString();
					//System.out.println("name:"+name+",item:"+item.getString());
				}else if(name.contains("flag")){
					flag = item.getString();
				}
			}
			
			
			Datas datas = new Datas();
			UserDao userDao = new UserDao();
			if(!"".equals(phone)&&!"".equals(flag)){
				int updateOk = userDao.updatePhoneUrl(CONST.URL_PATH+"images"+File.separator+photoName,phone,flag);
				if(updateOk==1){
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("上传成功");
					Map<String,String> map = new HashMap<String, String>();
					map.put("photoUrl", CONST.URL_PATH+"images"+File.separator+photoName);
					datas.setObj(map);
				}else{
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("数据库错误");
				}
				
			}else{
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("参数错误");
			}
			
			Gson gson = new Gson();
			

			out.write(gson.toJson(datas));
		} catch (FileUploadException e) {
			e.printStackTrace();
			Datas datas = new Datas();
			datas.setResult(CONST.SEND_FAIL);
			datas.setMsg("上传文件失败,"+e.getMessage());
          	Gson gson = new Gson();
			out.write(gson.toJson(datas));
		}

		out.flush();
		out.close();
	}

	// 流转化成字符串
	public static String inputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}

	// 流转化成文件
	public static void inputStream2File(InputStream is, String savePath)
			throws Exception {
		//System.out.println("文件保存路径为:" + savePath);
		File file = new File(savePath);
		InputStream inputSteam = is;
		BufferedInputStream fis = new BufferedInputStream(inputSteam);
		FileOutputStream fos = new FileOutputStream(file);
		int f;
		while ((f = fis.read()) != -1) {
			fos.write(f);
		}
		fos.flush();
		fos.close();
		fis.close();
		inputSteam.close();

	}

}