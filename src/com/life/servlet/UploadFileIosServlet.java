package com.life.servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.life.utils.CONST;
import com.lowagie.text.pdf.codec.Base64.InputStream;

public class UploadFileIosServlet extends HttpServlet {

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
		response.setCharacterEncoding("utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		request.setCharacterEncoding("UTF-8");
		String phone = request.getParameter("phone");
		String flag = request.getParameter("flag");
		String photoFile= request.getParameter("photoFile");

		PrintWriter out = response.getWriter();
		
		ServletInputStream is =request.getInputStream();
	

		FileOutputStream os = new FileOutputStream("e://QQ1.png");
		int read =0;
		byte [] bytes = new byte[1024];
		while((read =is.read())!=-1){
			os.write(read);
		}
		os.close();
		is.close();
		out.flush();
		out.close();
	}
	   static byte[] getImageBinary() {  
		   String path = CONST.URL_PATH+"images/start.png";
		   path = "D:\\GoogleDownload\\logo1.png";

	       File f = new File(path); 
	       BufferedImage bi;  
	       try {  
	           bi = ImageIO.read(f);  
	           ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	           ImageIO.write(bi, "png", baos);  //经测试转换的图片是格式这里就什么格式，否则会失真  
	           byte[] bytes = baos.toByteArray();  
	 

	           return bytes;
	       } catch (IOException e) {  
	           e.printStackTrace();  
	       }  
	       return null;  
	   }
	   /** 
	    * 将二进制转换为图片 
	    *  
	    * @param base64String 
	    */  
	   static void base64StringToImage(byte[] bytes1) {  
	       try {  
	          // byte[] bytes1 = decoder.decodeBuffer(base64String);  
	 
	           ByteArrayInputStream bais = new ByteArrayInputStream(bytes1);  
	           BufferedImage bi1 = ImageIO.read(bais);  
	           File w2 = new File("e://QQ.png");// 可以是jpg,png,gif格式  
	           ImageIO.write(bi1, "jpg", w2);// 不管输出什么格式图片，此处不需改动  
	       } catch (IOException e) {  
	           e.printStackTrace();  
	       }  
	   }  
}
