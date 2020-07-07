package com.life.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.imageio.ImageIO;


import com.life.utils.CONST;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class Test {
//	static BASE64Encoder encoder = new sun.misc.BASE64Encoder();  
//    static BASE64Decoder decoder = new sun.misc.BASE64Decoder();  
   public static void main(String[] args) throws FileNotFoundException, DocumentException {
//	 Random random = new Random();
//
//	byte[] bytes = getImageBinary();
//	for(int i=0;i<bytes.length;i++){
//		System.out.print(bytes[i]);
//	}
//	
//
//	base64StringToImage(getImageBinary());
	   // 1.新建document对象
	            Document document = new Document();
	    
	            // 2.建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中。
	            // 创建 PdfWriter 对象 第一个参数是对文档对象的引用，第二个参数是文件的实际名称，在该名称中还会给出其输出路径。
	            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:/save/test.pdf"));
	    
	            // 3.打开文档
	            document.open();
	           
	            // 4.添加一个内容段落
	            document.add(new Paragraph("Hello World!"));
	    
	            // 5.关闭文档
	            document.close();
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
 
          // return encoder.encodeBuffer(bytes).trim();  
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
