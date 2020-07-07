package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.BoxDao;
import com.life.controller.RongDao;
import com.life.controller.TransferDao;
import com.life.controller.UserDao;
import com.life.entity.BoxUse;
import com.life.entity.Datas;
import com.life.entity.Question;
import com.life.utils.CONST;
import com.life.utils.CommandUtil;

public class BoxServlet extends HttpServlet {

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
        BoxDao boxDao = new BoxDao();

        // 验证是否存在箱子并可用
        // 是否是自动转运的箱子
        if ("start".equals(action)) {
            String boxNo = request.getParameter("boxNo");
            if (boxNo != null && !"".equals(boxNo)) {
                boolean isOK = boxDao.getBoxNoStatus(boxNo);
                if (isOK) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("箱子可用");
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("box.do start 箱子不可用");
                }

            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("box.do start 参数错误");
            }

        } else if ("pdfTest".equals(action)) {
            try {
                String result = CommandUtil.run("wkhtmltopdf --disable-smart-shrinking    --encoding utf-8  --page-width 750px --page-height 1010px --margin-left 0px --margin-right 0px --margin-top 0px --margin-bottom 0px   http://www.lifeperfusor.com/transboxWeb/pdf/69/树兰（杭州）医院/2019-01-01=2019-04-01/2019年第一季度移植器官转运质量报告-肝脏/index   /root/lala.pdf");
                System.out.println("result:" + result);
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("result:" + result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("boxInfo".equals(action)) {
            String boxNo = request.getParameter("boxNo");
            if (boxNo != null && !"".equals(boxNo)) {
                boolean isOK = boxDao.getBoxNoStatus(boxNo);
                if (isOK || "99999".equals(boxNo)) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("箱子可用");

                    Map<String, String> mapInfo = new RongDao().gainHospitalInfo(boxNo);

                    String department = mapInfo.get("office");
                    if ("心脏科".equals(department)) {
                        department = "心脏";
                    } else if ("眼科科".equals(department)) {
                        department = "眼角膜";
                    } else if ("肝脏科".equals(department)) {
                        department = "肝脏";
                    } else if ("肺科".equals(department)) {
                        department = "肺";
                    } else if ("肾脏科".equals(department)) {
                        department = "肾脏";
                    } else if ("胰脏科".equals(department)) {
                        department = "胰脏";
                    } else {
                        department = "肝脏";
                    }
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("type", department);
                    map.put("modifyOrganSeg", new TransferDao()
                            .getModifyOrganSeg(boxNo));
                    map.put("hospitalName", mapInfo.get("name"));
                    map.put("longitude", mapInfo.get("longitude"));
                    map.put("latitude", mapInfo.get("latitude"));
                    datas.setObj(map);

                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("box.do start 箱子不可用");
                }

            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("box.do start 参数错误");
            }

        } else if ("delTestRecord".equals(action)) {
            CONST.DISTANCE = 123;
            CONST.HEIGHT = 0;
            boxDao.delTestRecord();
        } else if ("insertZeroDistance".equals(action)) {
            boxDao.insertZeroDistance();
        } else if ("insertZeroHeight".equals(action)) {
            boxDao.insertZeroHeight();
        } else if ("insertMinusDistance".equals(action)) {
            boxDao.insertMinusDistance();
        } else if ("insertMinusHeight".equals(action)) {
            boxDao.insertMinusHeight();
        } else if ("insertPlusHeight".equals(action)) {
            boxDao.insertPlusHeight();
        } else if ("insertHeight".equals(action)) {
            boxDao.insertHeight();
        } else if ("insertQuestion".equals(action)) {
            Question question = new Question();
//            private int id;
//            private String department;//'所在科室名称',
//            private String name;//'姓名',
//            private int app_sel1;// '您最近使用A胖胖的频率？',
//            private int app_sel2;// '是否知道如何通过手机新建转运？',
//            private int app_sel3;// '是否知道如何在app中查看转运报告？',
//            private int app_sel4;// '是否知道如何在app中查看当前转运情况',
//            private int app_sel5;// '您认为通过手机新建转运是否方便？',
//            private int app_sel6;// ' 你认为目前app是否满足了转运过程的所有要求？',
//            private String app_suggest;//'对于app的建议',
//            private int device_sel1;// '您最近使用设备的频率？',
//            private int device_sel2;// '是否知道如何解锁设备？',
//            private int device_sel3;// '是否知道如何通过手机新建转运？',
//            private int device_sel4;// '是否知道开始转运时的注意事项（如勿遮挡红外传感器）？',
//            private int device_sel5;// '您认为设备在转运时相较其他厂家设备是否便捷？',
//            private int device_sel6;// '您认为设备目前是否满足转运过程的所有要求？',
//            private String device_suggest; // '对于转运装置的建议',
            //http://localhost:8080/transbox/box.do?action=insertQuestion&department=%E5%8C%BB%E7%94%9F&question=gg&name=gg2&app_sel1=1&app_sel2=2&app_sel3=2&app_sel4=2&app_sel5=3&app_sel6=0&app_suggest=asdf%E5%A4%9A%E5%B0%91&device_sel1=1&device_sel2=1&device_sel3=1&device_sel4=2&device_sel5=1&device_sel6=2&device_suggest=%E5%A4%9A%E5%B0%91%E7%9A%84
            String department = request.getParameter("department");
            String name = request.getParameter("name");
            int app_sel1 = Integer.parseInt(request.getParameter("app_sel1"));
            int app_sel2 = Integer.parseInt(request.getParameter("app_sel2"));
            int app_sel3 = Integer.parseInt(request.getParameter("app_sel3"));
            int app_sel4 = Integer.parseInt(request.getParameter("app_sel4"));
            int app_sel5 = Integer.parseInt(request.getParameter("app_sel5"));
            int app_sel6 = Integer.parseInt(request.getParameter("app_sel6"));
            String app_suggest = request.getParameter("app_suggest");
            int device_sel1 = Integer.parseInt(request.getParameter("device_sel1"));
            int device_sel2 = Integer.parseInt(request.getParameter("device_sel2"));
            int device_sel3 = Integer.parseInt(request.getParameter("device_sel3"));
            int device_sel4 = Integer.parseInt(request.getParameter("device_sel4"));
            int device_sel5 = Integer.parseInt(request.getParameter("device_sel5"));
            int device_sel6 = Integer.parseInt(request.getParameter("device_sel6"));
            String device_suggest = request.getParameter("device_suggest");
            question.setDepartment(department);
            question.setName(name);
            question.setApp_sel1(app_sel1);
            question.setApp_sel2(app_sel2);
            question.setApp_sel3(app_sel3);
            question.setApp_sel4(app_sel4);
            question.setApp_sel5(app_sel5);
            question.setApp_sel6(app_sel6);
            question.setApp_suggest(app_suggest);
            question.setDevice_sel1(device_sel1);
            question.setDevice_sel2(device_sel2);
            question.setDevice_sel3(device_sel3);
            question.setDevice_sel4(device_sel4);
            question.setDevice_sel5(device_sel5);
            question.setDevice_sel6(device_sel6);

            question.setDevice_suggest(device_suggest);
           int result =  boxDao.insertQuestion(question);
            if (result == 1) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("插入成功");

            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("插入数据错误");
            }
        }
        // 收集箱子信息
        else if ("device".equals(action)) {

            String device = request.getParameter("device");
            String iccid = request.getParameter("iccid");
            if (device != null && !"".equals(device)) {
                int result = boxDao.setBoxIMEI(device);

                if (result == 1) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("box.do device ok");
                    datas.setObj(device);
                } else {
                    datas.setResult(CONST.BAD_PARAM);
                    datas.setMsg("box.do device 数据库问题错误");
                }
            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("box.do device 参数错误");
            }

        }
        // 获取保修的条目
        else if ("gainGuaranteeList".equals(action)) {


            List<String> list = boxDao.gainGuaranteeList();

            if (list.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("获取成功");
                datas.setObj(list);
            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("暂无数据");
            }


        }

        // 获取箱子信息
        else if ("boxUse".equals(action)) {

            String hospital = request.getParameter("hospital");
            if (hospital != null && !"".equals(hospital)) {

                List<BoxUse> boxUses = boxDao.getBoxUses(hospital);
                if (boxUses.size() > 0) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("box.do device ok");
                    datas.setObj(boxUses);
                } else {
                    datas.setResult(CONST.BAD_PARAM);
                    datas.setMsg("box.do device 数据库问题错误");
                }
            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("box.do device 参数错误");
            }

        } else if ("hospital".equals(action)) {

            List<Map<String, String>> map = boxDao.gainHospitalList();
            datas.setObj(map);
        } else if ("insertGuarantee".equals(action)) {
            String boxNo = request.getParameter("boxNo");
            String phone = request.getParameter("phone");
            String content = request.getParameter("content");
            if (content.contains("%")) {
                content = URLDecoder.decode(content, "utf-8");
            }

            new UserDao().insertGuarantee(boxNo, phone, content, "");
            datas.setResult(CONST.SEND_OK);
        } else if ("updateHospital".equals(action)) {

            String name = request.getParameter("name");
            String longitude = request.getParameter("longitude");
            String latitude = request.getParameter("latitude");
            boxDao.updateHospital(name, longitude, latitude);
        } else {
            datas.setResult(CONST.BAD_PARAM);
            datas.setMsg("box.do 没有相应的action");
        }
        out.write(gson.toJson(datas));

        out.flush();
        out.close();
    }

}
