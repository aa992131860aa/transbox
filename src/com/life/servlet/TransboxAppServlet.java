package com.life.servlet;

import com.google.gson.Gson;
import com.life.controller.BigScreenDao;
import com.life.controller.TransboxAppDao;
import com.life.entity.*;
import com.life.utils.CONST;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class TransboxAppServlet extends HttpServlet {

    /**
     * The doGet method of the servlet. <br>
     * <p>
     * This method is called when a form has its tag value method equals to get.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doPost(request, response);
    }

    /**
     * The doPost method of the servlet. <br>
     * <p>
     * This method is called when a form has its tag value method equals to
     * post.
     *
     * @param request  the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException      if an error occurred
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Gson gson = new Gson();
        Datas datas = new Datas();

        String action = request.getParameter("action");
        TransboxAppDao transboxAppDao = new TransboxAppDao();

        // 验证是否存在箱子并可用
        // 是否是自动转运的箱子
        if ("upload".equals(action)) {


            datas.setResult(CONST.SEND_OK);
            datas.setObj(transboxAppDao.gainTransboxAppUploadInfo());
            datas.setMsg("升级信息获取成功");


        } else {
            datas.setResult(CONST.BAD_PARAM);
            datas.setMsg("transboxApp.do 没有相应的action");
        }
        out.write(gson.toJson(datas));

        out.flush();
        out.close();
    }

}
