package com.wangyi;

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

public class BaseServlet extends HttpServlet {

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
        BaseDao baseDao = new BaseDao();
        WangyiHttpUtils wUtils = new WangyiHttpUtils();

        // 验证是否存在箱子并可用
        // 是否是自动转运的箱子
        if ("gainToken".equals(action)) {

            String userId = request.getParameter("userId");
            String userName = request.getParameter("userName");
            String photoUrl = request.getParameter("photoUrl");
            String isToken = request.getParameter("isToken");
            String token = "";
            String tokenOld = baseDao.isExistToken(userId);
            if ("".equals(tokenOld) || "0".equals(isToken)) {
                BaseData baseData = new BaseData();
                try {
                     baseData = wUtils.createAction(userName, userId,
                            photoUrl );
                }catch (Exception e1){

                }
                System.out.println("gainToken:create:1" + baseData.toString());
                // 创建token失败,插入数据库
                if (baseData.getCode() == 200) {
                    baseDao.updateToken(userId, baseData.getInfo().getToken());
                    token = baseData.getInfo().getToken();

                }
                // 重新更新token,插入数据库
                else {
                    BaseData refreshData = new BaseData();
                    try {
                        refreshData = wUtils.refreshTokenAction(userId );
                    } catch (Exception e1) {

                    }
                    if (refreshData.getCode() == 200) {
                        baseDao.updateToken(userId, refreshData.getInfo()
                                .getToken());
                        token = refreshData.getInfo().getToken();
                    } else {
                        // 获取失败

                    }
                    System.out.println("gainToken:refresh:" + refreshData.toString());
                }
            } else {
                token = tokenOld;
            }

            if ("".equals(token)) {
                datas.setResult(CONST.SEND_FAIL);
                datas.setObj(token);
            } else {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(token);
            }

        } else if ("block".equals(action)) {

            String phone = request.getParameter("phone");
            String needkick = request.getParameter("needkick");
            BaseData baseData = wUtils.blockAction(phone, needkick);
            datas.setObj(baseData.toString());
        } else if ("unblock".equals(action)) {

            String phone = request.getParameter("phone");

            BaseData baseData = wUtils.unBlockAction(phone);
            datas.setObj(baseData.toString());
        } else if ("appInfo".equals(action)) {
            String phone = request.getParameter("phone");
            if (phone == null || "".equals(phone)) {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("参数错误，没有电话号码");
            } else {
                WangyiAppInfo wangyiAppInfo = baseDao.gainWangyiAppInfo(phone);
                if (wangyiAppInfo == null) {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("获取信息失败");

                } else {
                    datas.setResult(CONST.SEND_OK);
                    datas.setObj(wangyiAppInfo);
                }
            }

        } else {
            datas.setResult(CONST.BAD_PARAM);

            String name = request.getParameter("userName");

            datas.setMsg("base.do 没有相应的action");

        }
        out.write(gson.toJson(datas));

        out.flush();
        out.close();
    }

}
