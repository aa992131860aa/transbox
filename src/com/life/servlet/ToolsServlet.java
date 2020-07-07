package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.*;
import com.life.entity.BoxUse;
import com.life.entity.Datas;
import com.life.entity.News;
import com.life.entity.Tools;
import com.life.utils.CONST;

public class ToolsServlet extends HttpServlet {

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
        ToolsDao toolsDao = new ToolsDao();

        // 验证是否存在箱子并可用
        // 是否是自动转运的箱子
        if ("list".equals(action)) {
            List<Tools> toolsList = toolsDao.gainTools();
            if (toolsList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("医疗小工具数据");
                datas.setObj(toolsList);
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("没有数据");
            }

        } else if ("pdf1".equals(action)) {

            Process process = Runtime.getRuntime().exec("wkhtmltopdf 'https://www.lifeperfusor.com/transbox/about/ff.html'  '/root/bb.pdf'");
            System.out.println("process1:" + process);
        } else if ("pdf2".equals(action)) {

            Process process = Runtime.getRuntime().exec("wkhtmltopdf  https://www.lifeperfusor.com/transbox/about/ff.html    /root/bb.pdf ");
            System.out.println("process2:" + process);
        } else if ("joinUs".equals(action)) {
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String nation = request.getParameter("nation");
            String institute = request.getParameter("institute");
            String title = request.getParameter("title");
            String wishes = request.getParameter("wishes");
            int isOk = toolsDao.joinUs(firstName, lastName, email, nation, institute, title, wishes);
            if (isOk > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("加入成功");
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("插入失败");
            }
        } else if ("animal".equals(action)) {

            String name = request.getParameter("name");
            String phone = request.getParameter("phone");
            String company = request.getParameter("company");
            String question = request.getParameter("question");
            String questionUrl = request.getParameter("questionUrl");
            String type = request.getParameter("type");

            if ("animal".equals(type)) {
                //给小动物设备的客服人员发送邮件
                SmsDao.send("反馈", "反馈问题：",
                        name + "反馈了问题，请查收。问题为：" + question,
                        "cy@lifeperfusor.com,rong.gu@lifeperfusor.com", "");
            } else if ("OTQC".equals(type)) {
                //给OTQC的客服人员发送邮件
                //给小动物设备的客服人员发送邮件
                SmsDao.send("反馈", "反馈问题：",
                        name + "反馈了问题，请查收。问题为：" + question,
                        "cy@lifeperfusor.com", "");
            }

            int isOk = toolsDao.animalReport(name, phone, company, question, questionUrl, type);
            if (isOk > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("加入成功");
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("插入失败");
            }

        } else if ("newsList".equals(action)) {
            String date = request.getParameter("date");
            List<News> newsList = toolsDao.gainNewsDate(date);
            if (newsList != null && newsList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(newsList);
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无数据");
            }

        } else if ("newsById".equals(action)) {
            String id = request.getParameter("id");
            News news = toolsDao.gainNewsDateById(Integer.parseInt(id));
            if (news != null) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(news);
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无数据");
            }

        } else if ("newsDate".equals(action)) {
            datas.setResult(CONST.SEND_OK);
            datas.setObj(toolsDao.gainNewsDate());
        } else {
            datas.setResult(CONST.BAD_PARAM);
            datas.setMsg("tools.do 没有相应的action");
        }
        out.write(gson.toJson(datas));

        out.flush();
        out.close();
    }

}
