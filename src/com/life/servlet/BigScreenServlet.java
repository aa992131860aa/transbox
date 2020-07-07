package com.life.servlet;

import com.google.gson.Gson;
import com.life.controller.*;
import com.life.entity.*;
import com.life.utils.CONST;
import com.life.utils.CommandUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BigScreenServlet extends HttpServlet {

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
        BigScreenDao bigScreenDao = new BigScreenDao();

        // 验证是否存在箱子并可用
        // 是否是自动转运的箱子
        if ("boxInfo".equals(action)) {
            String account = request.getParameter("account");

            List<ScreenBox> screenBoxList = bigScreenDao.gainScreenBoxList(account);
            if (screenBoxList != null && screenBoxList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(screenBoxList);
                datas.setMsg("箱子信息获取成功");
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("暂无箱子的信息");
            }


        } else if ("temperature".equals(action)) {
            String account = request.getParameter("account");
            List<ScreenInfoCount> screenInfoCountList = bigScreenDao.gainTemperatureList(account);
            if (screenInfoCountList != null && screenInfoCountList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(screenInfoCountList);
                datas.setMsg("温度数量获取成功");
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("暂无温度数量的信息");
            }

        }
        else if ("humidity".equals(action)) {
            String account = request.getParameter("account");
            List<ScreenInfoCount> screenInfoCountList = bigScreenDao.gainHumidityList(account);
            if (screenInfoCountList != null && screenInfoCountList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(screenInfoCountList);
                datas.setMsg("温度数量获取成功");
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("暂无温度数量的信息");
            }

        }else if("transferTotal".equals(action)){
            String account = request.getParameter("account");
            ScreenTotal screenTotal = bigScreenDao.gainTransferTotal(account);
            datas.setResult(CONST.SEND_OK);
            datas.setObj(screenTotal);
            datas.setMsg("转运数量获取成功");
        }
        else if("openAndCollision".equals(action)){
            String account = request.getParameter("account");
            List<ScreenOpenAndCollision> openAndCollisions = bigScreenDao.gainScreenOpenAndCollision(account);
            datas.setResult(CONST.SEND_OK);
            datas.setObj(openAndCollisions);
            datas.setMsg("转运开箱和碰撞获取成功");
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
