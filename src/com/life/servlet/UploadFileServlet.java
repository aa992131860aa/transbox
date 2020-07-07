package com.life.servlet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wangyi.WangyiHttpUtils;
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
        response.addHeader("Access-Control-Allow-Origin", "*");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        // 创建文件项目工厂对象
        DiskFileItemFactory factory = new DiskFileItemFactory();
        try {
            // 设置文件上传路径
            String upload = this.getServletContext().getRealPath(File.separator + "images" + File.separator);
            // 获取系统默认的临时文件保存路径，该路径为Tomcat根目录下的temp文件夹
            String temp = System.getProperty("java.io.tmpdir");
            // 设置缓冲区大小为 5M
            factory.setSizeThreshold(1024 * 1024 * 5);
            // 设置临时文件夹为temp
            factory.setRepository(new File(temp));
            // 用工厂实例化上传组件,ServletFileUpload 用来解析文件上传请求
            ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
            List<FileItem> list = servletFileUpload.parseRequest(request);

            String action = "";
            String boxNo = "";
            for (FileItem item : list) {
                if ("action".contains(item.getFieldName())) {
                    action = item.getString();

                }
                if ("boxNo".contains(item.getFieldName())) {
                    boxNo = item.getString();

                }
            }

            if ("guarantee".equals(action)) {


                // 解析结果放在List中


                String photoName = "";
                String phone = "";
                String hospitalName = "";

                String content = "";
                int iIndex = 0;
                List<String> urlList = new ArrayList<String>();
                for (FileItem item : list) {
                    String name = item.getFieldName();
                    InputStream is = item.getInputStream();

                    if (name.contains("photoFile")) {

                        try {

//							if(item.getName()==null){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            String date = sdf.format(new Date());
                            photoName = boxNo + "_" + date + iIndex + ".jpg";
                            inputStream2File(is, upload + File.separator + photoName);
                            urlList.add(boxNo + "_" + date + iIndex);
                            iIndex++;
//							}else{
//								inputStream2File(is, upload + "\\" + item.getName());
//								photoName = item.getName();
//							}


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (name.contains("phone")) {
                        phone = item.getString();

                    } else if (name.contains("hospitalName")) {
                        hospitalName = item.getString();
                    } else if (name.contains("content")) {
                        content = item.getString();
                        if (content.contains("%")) {
                            content = URLDecoder.decode(content, "utf-8");
                        }
                    }
                }


                Datas datas = new Datas();
                UserDao userDao = new UserDao();
                String hospitalId = userDao.gainHospitalId(hospitalName);
                long guaranteeId = userDao.insertGuarantee(boxNo, phone, content, hospitalId);
                if (guaranteeId > 0) {
                    for (int i = 0; i < urlList.size(); i++) {
                        userDao.insertGuaranteeFiles((int) guaranteeId, urlList.get(i), CONST.URL_PATH + "images" + File.separator + urlList.get(i) + ".jpg");
                    }


                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("上传成功");
                    Map<String, String> map = new HashMap<String, String>();
//						map.put("photoUrl", CONST.URL_PATH+"images"+File.separator+photoName);
//						datas.setObj(map);
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("数据库错误");
                }


                Gson gson = new Gson();


                out.write(gson.toJson(datas));


            } else {
//			// 创建文件项目工厂对象
//			DiskFileItemFactory factory = new DiskFileItemFactory();
//
//			// 设置文件上传路径
//			String upload = this.getServletContext().getRealPath(File.separator+"images"+File.separator);
//			// 获取系统默认的临时文件保存路径，该路径为Tomcat根目录下的temp文件夹
//			String temp = System.getProperty("java.io.tmpdir");
//			// 设置缓冲区大小为 5M
//			factory.setSizeThreshold(1024 * 1024 * 5);
//			// 设置临时文件夹为temp
//			factory.setRepository(new File(temp));
//			// 用工厂实例化上传组件,ServletFileUpload 用来解析文件上传请求
//			ServletFileUpload servletFileUpload = new ServletFileUpload(factory);

                // 解析结果放在List中

//				List<FileItem> list = servletFileUpload.parseRequest(request);
                String photoName = "";
                String phone = "";
                String flag = "";
                for (FileItem item : list) {
                    String name = item.getFieldName();
                    InputStream is = item.getInputStream();

                    if (name.contains("photoFile")) {

                        try {

//							if(item.getName()==null){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                            String date = sdf.format(new Date());
                            photoName = date + ".jpg";
                            inputStream2File(is, upload + File.separator + photoName);
//							}else{
//								inputStream2File(is, upload + "\\" + item.getName());
//								photoName = item.getName();
//							}


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (name.contains("phone")) {
                        phone = item.getString();

                    } else if (name.contains("flag")) {
                        flag = item.getString();
                    }
                }


                Datas datas = new Datas();
                UserDao userDao = new UserDao();
                if (!"".equals(phone) && !"".equals(flag)) {
                    String pathTemp = CONST.URL_PATH + "images" + File.separator + photoName;
                    int updateOk = userDao.updatePhoneUrl(pathTemp, phone, flag);

                    if (updateOk == 1) {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("上传成功");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("photoUrl", CONST.URL_PATH + "images" + File.separator + photoName);
                        datas.setObj(map);
                    } else {
                        datas.setResult(CONST.SEND_FAIL);
                        datas.setMsg("数据库错误");
                    }


                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("参数错误");
                }

                Gson gson = new Gson();


                out.write(gson.toJson(datas));


            }
        } catch (FileUploadException e) {
            e.printStackTrace();
            Datas datas = new Datas();
            datas.setResult(CONST.SEND_FAIL);
            datas.setMsg("上传文件失败," + e.getMessage());
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