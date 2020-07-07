package com.station;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.TransferDao;
import com.life.controller.UserDao;
import com.life.entity.Datas;
import com.life.entity.UploadApp;
import com.life.utils.CONST;

public class MainServlet extends HttpServlet {

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
        MainDao mainDao = new MainDao();

        // 获取箱子的信息
        if ("gainBoxInfo".equals(action)) {

            String userName = request.getParameter("userName");
            String boxNos = request.getParameter("boxNos");
            List<BoxInfo> boxInfoList = mainDao.gainBoxInfo(userName, "free", boxNos);
            if (boxInfoList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(boxInfoList);
                datas.setMsg("已获取信息");
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无箱子信息");
            }

        } else if ("gainBoxNo".equals(action)) {
            String userName = request.getParameter("userName");
            String boxNos = request.getParameter("boxNos");
            List<String> boxInfoList = mainDao.gainBoxNo(userName, boxNos);
            if (boxInfoList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(boxInfoList);
                datas.setMsg("已获取信息");
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无箱子信息");
            }

        }
        // 获取所有箱子的信息
        else if ("gainBoxInfoAll".equals(action)) {
            String userName = request.getParameter("userName");
            String boxNos = request.getParameter("boxNos");
            List<BoxInfo> boxInfoList = mainDao.gainBoxInfoAll(userName);
            if (boxInfoList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(boxInfoList);
                datas.setMsg("已获取信息");
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无箱子信息");
            }

        } else if ("gainBoxLocation".equals(action)) {

            String boxNos = request.getParameter("boxNos");
            String boxNosStr[] = boxNos.split(",");
            List<Box> boxList = new ArrayList<>();
            for (int i = 0; i < boxNosStr.length; i++) {
                Box box = mainDao.gainBoxLocation(boxNosStr[i]);
                if (box != null) {
                    boxList.add(box);
                }

            }
            if (boxList.size() > 0) {

                datas.setResult(CONST.SEND_OK);
                datas.setObj(boxList);
                datas.setMsg("已获取信息");
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无信息");
            }
        } else if ("updatePower".equals(action)) {
            String power = request.getParameter("power");
            String rfid = request.getParameter("rfid");
            mainDao.updatePower(power, rfid);

        }
        // 获取转运中的箱子
        else if ("gainTransferingBoxInfo".equals(action)) {

            String userName = request.getParameter("userName");
            String boxNos = request.getParameter("boxNos");
            List<BoxInfo> boxInfoList = mainDao.gainBoxInfo(userName,
                    "transfering", boxNos);
            if (boxInfoList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(boxInfoList);
                datas.setMsg("已获取信息");
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无箱子信息");
            }

        }
        // 获取转运信息
        else if ("gainRecord".equals(action)) {

            // String boxNo = request.getParameter("boxNo");
            String transferId = request.getParameter("transferId");

            List<Record> recordList = mainDao.gainRecordList(transferId);
            if (recordList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("已获取信息");
                datas.setObj(recordList);
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无箱子信息");
            }

        }
        // 获取转运信息
        else if ("gainVideo".equals(action)) {
            // String boxNo = request.getParameter("boxNo");

            List<Video> recordList = mainDao.gainVideoList();
            if (recordList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("已获取信息");
                datas.setObj(recordList);
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无视频信息");
            }

        }
        //获取更新版本
        else if ("stationUpdate".equals(action)) {
            UploadApp uploadApp = mainDao.gainStationUpdate();
            if (uploadApp != null) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("获取正确");
                datas.setObj(uploadApp);
            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("数据库错误:" + CONST.ERROR);
            }
        }
        // 获取转运历史
        else if ("serachHistory".equals(action)) {

            String boxNo = request.getParameter("boxNo");
            String condition = request.getParameter("condition");
            String userName = request.getParameter("userName");
            String pageStr = request.getParameter("page");
            String pageSizeStr = request.getParameter("pageSize");
            int page = Integer.parseInt(pageStr);
            int pageSize = Integer.parseInt(pageSizeStr);

            List<Transfer> transferList = mainDao.searchTransferList(userName,
                    condition, boxNo, page, pageSize);
            if (transferList.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setObj(transferList);
                datas.setMsg("已获取信息");
            } else {
                datas.setResult(CONST.NO_DATA);
                datas.setMsg("暂无转运信息");
            }

        }
        // 登录
        else if ("login".equals(action)) {

            String pwd = request.getParameter("pwd");
            String hospital = request.getParameter("hospital");

            boolean isTrue = mainDao.login(hospital, pwd);
            if (isTrue) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("登录成功");
                // datas.setObj(userses);
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("登录失败");
            }

        } else {
            datas.setResult(CONST.BAD_PARAM);
            datas.setMsg("main.do 没有相应的action");

        }
        out.write(gson.toJson(datas));

        out.flush();
        out.close();
    }

}
