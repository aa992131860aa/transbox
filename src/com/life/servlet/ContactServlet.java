package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.ContactDao;
import com.life.entity.*;
import com.life.utils.CONST;
import com.wangyi.WangyiHttpUtils;

public class ContactServlet extends HttpServlet {

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
        request.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        Datas datas = new Datas();
        ContactDao contactDao = new ContactDao();
        Gson gson = new Gson();
        if (action != null && !"".equals(action)) {

            if ("search".equals(action)) {


                String phone = request.getParameter("phone");
                String name = request.getParameter("name");
                if (!"".equals(phone) && phone != null && !"".equals(name)
                        && name != null) {
                    List<ContactPerson> contactPersons = contactDao
                            .findFriends(phone, name);
                    if (contactPersons.size() > 0) {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("findFriends ok");
                        datas.setObj(contactPersons);
                    } else {
                        datas.setResult(1);
                        datas.setMsg("好友不存在");
                    }

                } else {
                    datas.setResult(CONST.BAD_PARAM);
                    datas.setMsg("action 参数错误");

                }

            } else if ("refuse".equals(action)) {


                String idStr = request.getParameter("pushId");

                try {
                    if (!"".equals(idStr) && idStr != null) {
                        int id = Integer.parseInt(idStr);
                        int isOK = contactDao.updateContactType(id, "refuse");
                        if (isOK == 1) {
                            datas.setResult(CONST.SEND_OK);
                            datas.setMsg("修改好友请求状态成功");

                        } else {
                            datas.setResult(1);
                            datas.setMsg("修改失败");
                        }

                    } else {
                        datas.setResult(CONST.BAD_PARAM);
                        datas.setMsg("action 参数错误");

                    }
                } catch (Exception e) {
                    datas.setResult(1);
                    datas.setMsg("字符串转换int类型失败");
                }

            } else if ("delete".equals(action)) {


                String phone = request.getParameter("phone");
                String otherPhone = request.getParameter("otherPhone");


                try {
                    if (!"".equals(phone) && phone != null && !"".equals(otherPhone) && otherPhone != null) {
                        int phoneId = contactDao.getUserIdByPhone(phone);
                        int otherPhoneId = contactDao.getUserIdByPhone(otherPhone);

                        if (phoneId != 0 && otherPhoneId != 0) {
                            int isOK = contactDao.deleteContact(phoneId, otherPhoneId);
                            if (isOK == 1) {
                                datas.setResult(CONST.SEND_OK);
                                datas.setMsg("修改好友请求状态成功");
                            } else {
                                datas.setResult(CONST.SEND_FAIL);
                                datas.setMsg("删除好友状态成功");
                            }


                        } else {
                            datas.setResult(CONST.SEND_FAIL);
                            datas.setMsg("没有该手机号");
                        }

                    } else {
                        datas.setResult(CONST.BAD_PARAM);
                        datas.setMsg("action 参数错误");

                    }
                } catch (Exception e) {
                    datas.setResult(1);
                    datas.setMsg("字符串转换int类型失败");
                }

            } else if ("add".equals(action)) {


                String phone = request.getParameter("phone");
                String otherId = request.getParameter("otherId");
                String pushId = request.getParameter("pushId");


                if (!"".equals(pushId)
                        && pushId != null) {
                    try {
                        int otherIdInt = Integer.parseInt(otherId);
                        int pushIdInt = Integer.parseInt(pushId);
                        int phoneId = contactDao.getUserIdByPhone(phone);
                        int id = Integer.parseInt(pushId);
                        int isOK = contactDao.updateContactType(id, "agree");

                        if (isOK == 1) {


                            int isOk = contactDao.insertContact(phoneId,
                                    otherIdInt);

                            if (isOk == 1) {
                                contactDao.insertContact(otherIdInt,
                                        phoneId);
                                datas.setResult(CONST.SEND_OK);
                                datas.setMsg("插入联系人成功");

                            } else {
                                datas.setResult(CONST.SEND_FAIL);
                                datas.setMsg("contact 数据库错误");
                            }

                        }

                    } catch (Exception e) {

                        datas.setResult(CONST.SEND_FAIL);
                        datas.setMsg("otherId 不为数字");
                    }

                } else {

                    datas.setResult(CONST.BAD_PARAM);
                    datas.setMsg("action 参数错误");
                }

            } else if ("getContactList".equals(action)) {


                String phone = request.getParameter("phone");
                String organSeg = request.getParameter("organSeg");
                String hospitalType = request.getParameter("hospitalType");
                if(hospitalType==null){
                    hospitalType="";
                }
                if (!"".equals(phone) && phone != null) {

                    int phoneId = contactDao.getUserIdByPhone(phone);
                    if (phoneId == -1) {
                        datas.setResult(CONST.SEND_FAIL);
                        datas.setMsg("没查到该手机的信息");
                    } else {
                        String hospitalName = contactDao.gainHospName(phone);
                        List<Contact> contacts = contactDao
                                .getContactList(phone, organSeg, hospitalType,hospitalName.split("=")[0]);
                        if (contacts.size() > 0) {
                            datas.setResult(CONST.SEND_OK);
                            datas.setMsg("插入联系人成功");
                            datas.setObj(contacts);
                        } else {
                            datas.setResult(CONST.SEND_FAIL);
                            datas.setMsg("暂无联系人");
                        }
                    }

                } else {

                    datas.setResult(CONST.BAD_PARAM);
                    datas.setMsg("action 参数错误");
                }

            }
            /**
             * 获取医院的科室管理员
             */
            else if ("getContactOpoList".equals(action)) {


                String phone = request.getParameter("phone");
                String organSeg = request.getParameter("organSeg");

                if (!"".equals(phone) && phone != null) {

                    int phoneId = contactDao.getUserIdByPhone(phone);
                    if (phoneId == -1) {
                        datas.setResult(CONST.SEND_FAIL);
                        datas.setMsg("没查到该手机的信息");
                    } else {

                        List<Contact> contacts = contactDao
                                .getContactOpoList(phone, organSeg);
                        if (contacts.size() > 0) {
                            datas.setResult(CONST.SEND_OK);
                            datas.setMsg("插入联系人成功");
                            datas.setObj(contacts);
                        } else {
                            datas.setResult(CONST.SEND_FAIL);
                            datas.setMsg("暂无联系人");
                        }
                    }

                } else {

                    datas.setResult(CONST.BAD_PARAM);
                    datas.setMsg("action 参数错误");
                }

            }
            /**
             * 同一医院的人员
             * 好友的成员
             * 筛选出不是好友的人
             * 添加好友关系
             */
            else if ("updateContact".equals(action)) {
                WangyiHttpUtils wUtils = new WangyiHttpUtils();

                String phone = request.getParameter("phone");

                if (!"".equals(phone) && phone != null) {
                    /**
                     * 建立群组
                     * 或者加入群组
                     */
                    String tid = contactDao.gainTeamTid(phone);
                    String hosp = contactDao.gainHospName(phone);
                    String hospName = hosp.split("=")[0];
                    String hospId = hosp.split("=")[1];
                    String phones = contactDao.gainUsers(phone);
                    if (tid == null) {
                        //加入群组
                        String result = wUtils.createActionTeam(hospName, "18398850872",
                                new Gson().toJson(phones.split(",")), "欢迎加入群组");
                        TeamTid teamTid = new Gson().fromJson(result, TeamTid.class);
                        if (teamTid.getCode() == 200) {
                            //插入群组到数据库
                            contactDao.insertTeamHosp(teamTid.getTid(), hospId, hospName);

                        }
                    } else {
                        //查询群成员
                        TeamNumber teamNumber = wUtils.queryAction(tid);
                        if (teamNumber.getCode() == 200) {
                            if (!teamNumber.getTinfos().get(0).getMembers().toString().contains(phone)) {
                                wUtils.addAction(tid, "18398850872",
                                        new Gson().toJson(phone.split(",")), "欢迎加入群组");
                            }
                        }

                    }


                    List<Integer> usersIds = contactDao.getUserId(phone);
                    List<Integer> contactIds = contactDao.getContactId(phone);
                    int id = contactDao.getUserSelfId(phone);
                    //List<Integer> newContactIds = new ArrayList<Integer>();

                    for (int i = 0; i < usersIds.size(); i++) {
                        int usersId = usersIds.get(i);
                        int flag = 0;
                        for (int j = 0; j < contactIds.size(); j++) {
                            int contactId = contactIds.get(j);
                            if (usersId == contactId) {


                                flag = 1;
                                break;
                            }
                        }
                        if (flag == 0) {


                            contactDao.insertContact(usersId, id);
                            contactDao.insertContact(id, usersId);
                        }
                    }
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("插入联系人成功");

                } else {

                    datas.setResult(CONST.BAD_PARAM);
                    datas.setMsg("action 参数错误");
                }

            } else if ("getDepartments".equals(action)) {

                String deviceId = request.getParameter("deviceId");


                if (!"".equals(deviceId) && deviceId != null) {

                    List<Department> departments = contactDao.getDepartments(deviceId);
                    if (departments.size() > 0) {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("没查到该手机的信息");
                        datas.setObj(departments);
                    } else {


                        datas.setResult(CONST.SEND_FAIL);
                        datas.setMsg("暂无联系人");

                    }

                } else {

                    datas.setResult(CONST.BAD_PARAM);
                    datas.setMsg("action 参数错误");
                }


            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("未知的action");
            }

        } else {
            datas.setResult(CONST.BAD_PARAM);
            datas.setMsg("action 参数错误");

        }
        out.write(gson.toJson(datas));
        out.flush();
        out.close();
    }

}
