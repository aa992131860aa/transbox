package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.life.controller.BoxDao;
import com.life.controller.OpoDao;
import com.life.controller.RongDao;
import com.life.controller.TransferDao;
import com.life.controller.UserDao;
import com.life.entity.Datas;
import com.life.entity.OpoInfo;
import com.life.entity.TransferJson;
import com.life.entity.TransferPushSite;
import com.life.entity.UserInfo;
import com.life.utils.CONST;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class TransferServlet extends HttpServlet {
    IoAcceptor acceptor = null;

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


//		response.setCharacterEncoding("UTF-8");
//		response.setHeader("content-type", "text/html;charset=UTF-8");
//		response.setContentType("application/json;charset=utf-8");
//		response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");
        TransferDao transferDao = new TransferDao();
        Gson gson = new Gson();
        Datas datas = new Datas();
        if ("getTransferPushSite".equals(action)) {

            String phone = request.getParameter("phone");
            String organSeg = request.getParameter("organSeg");
            TransferPushSite transferPushSite = transferDao
                    .getTransferPushSite(phone, organSeg);
            if (transferPushSite != null) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("获取转运中的信息");
                datas.setObj(transferPushSite);
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("数据库错误");
            }

        } else if ("gainRed".equals(action)) {
            String phone = request.getParameter("phone");
            int show = transferDao.gainRedShow();
            if (show == 1) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("获取红包功能");
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("红包功能关闭");
            }

        } else if ("gainRedTotal".equals(action)) {
            String phone = request.getParameter("phone");
            int total = transferDao.gainRedTotal(phone);
            Map<String, String> map = new HashMap<>();
            map.put("total", total + "");
            datas.setResult(CONST.SEND_OK);
            datas.setMsg("获取红包总计");
            datas.setObj(map);
        } else if ("getRedList".equals(action)) {
            String phone = request.getParameter("phone");
            String page = request.getParameter("page");
            String pageSize = request.getParameter("pageSize");
            String check = request.getParameter("check");

            if (phone != null && !"".equals(phone)) {
                List<TransferJson> lists = transferDao.getTransfersByPhoneNew(
                        phone, Integer.parseInt(page), Integer
                                .parseInt(pageSize), Integer.parseInt(check), "redList");
                if (lists.size() > 0) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {
                    datas.setResult(CONST.NO_MORE);
                    datas.setMsg("没有转运数据信息," + CONST.ERROR);
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        } else if ("appLog".equals(action)) {
            String phone = request.getParameter("phone");
            String name = request.getParameter("name");
            String doAction = request.getParameter("doAction");
            String msg = request.getParameter("msg");
            String type = request.getParameter("type");
            transferDao.insertAppLog(name, phone, doAction, msg, type);

        } else if ("setTransferPushSite".equals(action)) {

            String phone = request.getParameter("phone");
            String organSeg = request.getParameter("organSeg");
            String type = request.getParameter("type");
            String status = request.getParameter("status");

            int isOk = transferDao.setTransferPushSite(phone, organSeg, type,
                    Integer.parseInt(status));
            if (isOk >= 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("获取转运中的信息");
                datas.setObj("ok");
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("数据库错误");
            }

        } else if ("transfering".equals(action)) {

            List<Map<String, String>> lists = transferDao.getTransfers();
            if (lists.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("获取转运中的信息");
                datas.setObj(lists);
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("数据库错误");
            }

        } else if ("getOpoList".equals(action)) {

            List<OpoInfo> lists = transferDao.getOpoList();
            if (lists.size() > 0) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("获取转运中的信息");
                datas.setObj(lists);
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("没有Opo信息");
            }

        } else if ("closePower".equals(action)) {

            String deviceId = request.getParameter("deviceId");

            //保存到数据库
            transferDao.insertDevice(deviceId);
            // 移除该关机的deviceId
            if (CONST.DEVICE_ID.contains(deviceId)) {
                String deviceIds[] = CONST.DEVICE_ID.split("@");
                String deviceIdTemp = "";
                for (int i = 0; i < deviceIds.length; i++) {
                    if (deviceId.equals(deviceIds[i])) {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("移除成功");
                    } else {
                        if (!"".equals(deviceIds[i])) {
                            deviceIdTemp += deviceIds[i] + "@";
                        }
                    }
                }
                CONST.DEVICE_ID = deviceIdTemp;
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("移除失败");
            }
        } else if ("updateStart".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            String isStart = request.getParameter("isStart");
            String type = request.getParameter("type");
            if (organSeg != null && !"".equals(organSeg) && isStart != null
                    && !"".equals(isStart)) {
                //判断是否打开设备
                boolean deviceStatus = transferDao.getDeviceStatus(organSeg);
                //modify
                deviceStatus = true;
                if (deviceStatus) {

                    int isOK = transferDao.updateStartByOrganSeg(organSeg, isStart, type);
                    if (isOK != -1) {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("更改状态成功");
                    } else {
                        datas.setResult(CONST.SEND_FAIL);
                        datas.setMsg("设备未开启");
                    }


                } else {

                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("设备未开启");
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        } else if ("shutDownTransfer".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            String boxNo = request.getParameter("boxNo");
            System.out.println("shutDownTransfer come:" + CONST.ALL_IO_SESSION.size() + ",sendPush:" + CONST.SEND_PUSH);
            //发送推送给停止的转运
//            for (String key : CONST.ALL_IO_SESSION.keySet()) {
//
//                if (key.equals(boxNo)) {
//                    datas.setMsg("shutDownTransfer");
//                    datas.setObj(boxNo);
//                    CONST.ALL_IO_SESSION.get(key).write(new Gson().toJson(datas));
//                    System.out.println("发送shutdown" + "key:" + key);
//                    break;
//                }
//            }

            if (organSeg != null && !"".equals(organSeg) && boxNo != null
                    && !"".equals(boxNo)) {
                int isOK = transferDao.updateTransferStatus(organSeg, boxNo);
                if (isOK != 0) {

                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("更改状态成功");


                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("没有转运数据信息," + CONST.ERROR);
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        } else if ("isGroup".equals(action)) {
            String tid = request.getParameter("tid");
            boolean isOK = transferDao.isGroup(tid);
            if (isOK) {
                datas.setResult(CONST.SEND_OK);
                datas.setMsg("此群已解散");

            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("此群没有解散");
            }
        } else if ("getTransferListNew".equals(action)) {
            String phone = request.getParameter("phone");
            String page = request.getParameter("page");
            String pageSize = request.getParameter("pageSize");

            if (phone != null && !"".equals(phone)) {
                List<TransferJson> lists = transferDao.getTransfersByPhoneNew(
                        phone, Integer.parseInt(page), Integer
                                .parseInt(pageSize), -1, "transferList");
                if (lists.size() > 0) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {
                    datas.setResult(CONST.NO_MORE);
                    datas.setMsg("没有转运数据信息," + CONST.ERROR);
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        } else if ("updateCheck".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            String check = request.getParameter("check");
            try {
                int isOK = transferDao.updateCheck(organSeg, Integer.parseInt(check));


                if (isOK != 0) {

                    /**
                     * 发送邮箱给市场人员, 陈杨、蔡玉雅：cy@lifeperfusor.com,cyy@lifeperfusor.com
                     * otqc的转运不发送,
                     */
                    String toHospId = transferDao.gainToHospId(organSeg);


                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("更改状态成功");

                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("更改状态失败," + CONST.ERROR);
                }
            } catch (Exception e) {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("更改状态失败");
            }

        } else if ("getTransferList".equals(action)) {
            String phone = request.getParameter("phone");
            String page = request.getParameter("page");
            String pageSize = request.getParameter("pageSize");

            if (phone != null && !"".equals(phone)) {
                List<TransferJson> lists = transferDao.getTransfersByPhone(
                        phone, Integer.parseInt(page), Integer
                                .parseInt(pageSize));
                if (lists.size() > 0) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("没有转运数据信息," + CONST.ERROR);
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        } else if ("getTransferTid".equals(action)) {
            String tid = request.getParameter("tid");
            if (tid != null && !"".equals(tid)) {
                List<TransferJson> lists = transferDao
                        .getTransfersByOrganSegTid(tid);
                if (lists.size() > 0) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("没有转运数据信息," + CONST.ERROR);
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        } else if ("getTransfer".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            if (organSeg != null && !"".equals(organSeg)) {
                List<TransferJson> lists = transferDao
                        .getTransfersByOrganSeg(organSeg);
                if (lists.size() > 0) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("没有转运数据信息," + CONST.ERROR);
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        } else if ("getTransferByOrganSeg".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            if (organSeg != null && !"".equals(organSeg)) {
                TransferJson lists = transferDao
                        .getTransferByOrganSeg(organSeg);
                if (lists != null) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("没有转运数据信息," + CONST.ERROR);
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        } else if ("getTransferByTransferId".equals(action)) {
            String transferId = request.getParameter("transferId");
            if (transferId != null && !"".equals(transferId)) {
                TransferJson lists = transferDao
                        .getTransferByTransferId(transferId);
                if (lists != null) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("没有转运数据信息," + CONST.ERROR);
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        }
        /**
         * 根据设备id获取转运信息
         */
        else if ("getTransferByDeviceId".equals(action)) {
            String deviceId = request.getParameter("deviceId");
            if (deviceId != null && !"".equals(deviceId)) {
                TransferJson lists = transferDao
                        .getTransferByDeviceId(deviceId);
                if (lists != null) {
                    datas.setResult(CONST.SEND_OK);

                    datas.setMsg((new Date().getTime() - 10 * 60 * 1000 * 0) + "");
                    datas.setObj(lists);
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg((new Date().getTime() - 10 * 60 * 1000 * 0) + "");
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg((new Date().getTime() - 10 * 60 * 1000 * 0) + "");
            }

        } else if ("transferDown".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            if (organSeg != null && !"".equals(organSeg)) {
                boolean lists = transferDao.transferDown(organSeg);
                if (lists) {

                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("" + transferDao.overTransferTime(organSeg));
                    datas.setObj(lists);
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("" + transferDao.overTransferTime(organSeg));
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }

        } else if ("getTransferHistory".equals(action)) {
            String phone = request.getParameter("phone");
            String startTime = request.getParameter("startTime");
            String endTime = request.getParameter("endTime");
            String organ = request.getParameter("organ");
            String transferPerson = request.getParameter("transferPerson");
            String startAddress = request.getParameter("startAddress");
            String integrity = request.getParameter("integrity");
            String pageStr = request.getParameter("page");
            String pageSizeStr = request.getParameter("pageSize");
            String condition = "";

            if (integrity != null && !"".equals(integrity)) {
                if ("待完善".equals(integrity)) {
                    condition += " and t.autoTransfer = 1";
                } else if ("已完善".equals(integrity)) {
                    condition += " and t.autoTransfer = 0";
                }
            }

            if (startTime != null && !"".equals(startTime)) {
                condition += " and t.getTime >= '" + startTime + "'";
            }
            if (endTime != null && !"".equals(endTime)) {
                condition += " and t.getTime <= '" + endTime + "'";
            }
            if (organ != null && !"".equals(organ)) {
                condition += " and t.organ ='" + organ + "'";
            }
            if (transferPerson != null && !"".equals(transferPerson)) {
                condition += " and t.trueName ='" + transferPerson + "'";
            }
            // else {
            // condition += " and t.phone ='" + phone + "'";
            // }
            if (startAddress != null && !"".equals(startAddress)) {
                condition += " and t.fromCity ='" + startAddress + "'";
            }
            if (phone != null && !"".equals(phone)) {
                int page = Integer.parseInt(pageStr);
                int pageSize = Integer.parseInt(pageSizeStr);
                List<TransferJson> lists = transferDao
                        .getTransfersHistoryByPhone(phone, condition, page,
                                pageSize);
                if (lists.size() > 0) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {

                    if (page == 0) {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("没有数据");
                        datas.setObj(lists);
                    } else {
                        datas.setResult(CONST.NO_MORE);
                        datas.setMsg("没有更多");
                    }

                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }
        } else if ("getSearchTransferHistory".equals(action)) {
            String phone = request.getParameter("phone");
            String condition = request.getParameter("condition");
            String pageStr = request.getParameter("page");
            String pageSizeStr = request.getParameter("pageSize");

            if (phone != null && !"".equals(phone)) {
                int page = Integer.parseInt(pageStr);
                int pageSize = Integer.parseInt(pageSizeStr);
                List<TransferJson> lists = transferDao
                        .getSearchTransfersHistory(phone, condition, page,
                                pageSize);
                if (lists.size() > 0) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {
                    if (page == 0) {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("没有数据");
                        datas.setObj(lists);
                    } else {
                        datas.setResult(CONST.NO_MORE);
                        datas.setMsg("没有更多");
                    }
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }
        } else if ("getPadTransferHistory".equals(action)) {
            String deviceId = request.getParameter("deviceId");
            String organSeg = request.getParameter("organSeg");
            String pageStr = request.getParameter("page");
            String pageSizeStr = request.getParameter("pageSize");

            if (deviceId != null && !"".equals(deviceId)) {
                int page = Integer.parseInt(pageStr);
                int pageSize = Integer.parseInt(pageSizeStr);
                List<TransferJson> lists = transferDao
                        .getTransfersHistoryByorganSeg(deviceId, organSeg,
                                page, pageSize);
                if (lists.size() > 0) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("获取转运中的信息");
                    datas.setObj(lists);
                } else {
                    if (page == 0) {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("没有数据");
                        datas.setObj(lists);
                    } else {
                        datas.setResult(CONST.NO_MORE);
                        datas.setMsg("没有更多");
                    }
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }
        } else if ("deleteTransfer".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            String phone = request.getParameter("phone");
            if (organSeg != null && !"".equals(organSeg) && phone != null
                    && !"".equals(phone)) {
                String returnValue = transferDao
                        .deleteTransfer(organSeg, phone);
                if (!"".equals(returnValue)) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("删除成功");

                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("没有转运数据信息," + CONST.ERROR);
                }
            } else {
                datas.setResult(CONST.SEND_FAIL);
                datas.setMsg("参数错误");
            }
        } else if ("createTransfer".equals(action) || "create".equals(action)) {
            /**
             * @param organSeg
             *            器官段号
             * @param getTime
             *            获取时间
             * @param openPsd
             *            开箱密码
             * @param organ
             *            器官
             * @param organNum
             *            器官数量
             * @param blood
             *            血液
             * @param bloodNum
             *            血液数量
             * @param sampleOrgan
             *            样本组织
             * @param sampleOrganNum
             *            样本组织数量
             * @param fromCity
             *            开始城市
             * @param toHospName
             *            结束城市
             * @param tracfficType
             *            转运方式
             * @param tracfficNumber
             *            航班
             * @param opoName
             *            opo名称
             * @param contactName
             *            联系人名称
             * @param contactPhone
             *            联系人电话
             * @param phone
             *            本人电话
             * @param trueName
             *            本人姓名 18
             */
            String organSeg = request.getParameter("organSeg");
            String getTime = request.getParameter("getTime");
//			String openPsd = request.getParameter("openPsd");
//			String organ = request.getParameter("organ");
//			String organNum = request.getParameter("organNum");
            String blood = request.getParameter("blood");
            String bloodNum = request.getParameter("bloodNum");
            String sampleOrgan = request.getParameter("sampleOrgan");
            String sampleOrgan1 = request.getParameter("sampleOrgan1");
            String sampleOrganNum = request.getParameter("sampleOrganNum");
            String fromCity = request.getParameter("fromCity");
            //医院名称
            String toHospName = request.getParameter("toHospName");
            String tracfficType = request.getParameter("tracfficType");
            String tracfficNumber = request.getParameter("tracfficNumber");

            String opoName = toHospName;
            //String opoName = request.getParameter("opoName");
//			String contactName = request.getParameter("contactName");
//			String contactPhone = request.getParameter("contactPhone");
            String phone = request.getParameter("phone");
            String trueName = request.getParameter("trueName");
            String groupName = request.getParameter("groupName");
            String usersIds = request.getParameter("usersIds");
            String distance = request.getParameter("distance");
            String startLong = request.getParameter("startLong");
            String startLati = request.getParameter("startLati");
            String endLong = request.getParameter("endLong");
            String endLati = request.getParameter("endLati");
            String toHosp = request.getParameter("toHosp");
            String boxNo = request.getParameter("boxNo");
            String isStart = request.getParameter("isStart");
//			String opoContactName = request.getParameter("opoContactName");
//			String opoContactPhone = request.getParameter("opoContactPhone");
            //String autoTransfer = request.getParameter("autoTransfer");
            String modifyOrganSeg = request.getParameter("modifyOrganSeg");
            String organAddress = request.getParameter("organAddress");
            String temperature = request.getParameter("temperature");
            String weather = request.getParameter("weather");
            //String organTime = request.getParameter("organTime");


//			if (organSeg != null && !"".equals(organSeg) && getTime != null
//					&& !"".equals(getTime) && organ != null
//					&& !"".equals(organ) && organNum != null
//					&& !"".equals(organNum) && blood != null
//					&& !"".equals(blood) && bloodNum != null
//					&& !"".equals(bloodNum) && sampleOrgan != null
//					&& !"".equals(sampleOrgan) && sampleOrganNum != null
//					&& !"".equals(sampleOrganNum) && fromCity != null
//					&& !"".equals(fromCity) && toHospName != null
//					&& !"".equals(toHospName) && tracfficType != null
//					&& !"".equals(tracfficType) && opoName != null
//					&& !"".equals(opoName) && contactName != null
//					&& !"".equals(contactName) && contactPhone != null
//					&& !"".equals(contactPhone) && phone != null
//					&& !"".equals(phone) && trueName != null
//					&& !"".equals(trueName) && groupName != null
//					&& !"".equals(groupName) && usersIds != null
//					&& !"".equals(usersIds) && distance != null
//					&& !"".equals(distance) && startLong != null
//					&& !"".equals(startLong) && startLati != null
//					&& !"".equals(startLati) && endLong != null
//					&& !"".equals(endLong) && endLati != null
//					&& !"".equals(endLati)) {


            try {

                //
                //如果手动创建,就停止掉自动转运的转运
                String autoTransferOrganSeg = new BoxDao().autoTransferOrganSeg(boxNo);
                if (autoTransferOrganSeg != null) {
                    transferDao.updateTransferStatus(autoTransferOrganSeg, boxNo);
                }
                //根据箱子获取肝脏类型
                String organ = new BoxDao().getOrgan(boxNo);

                //获取opo人员和电话
                OpoInfo opoInfo = new OpoDao().getOpo(toHospName);
                String opoContactName = "";
                String opoContactPhone = "";
                if (opoInfo != null && opoInfo.getOpoInfoContacts() != null && opoInfo.getOpoInfoContacts().size() > 0) {
                    opoContactName = opoInfo.getOpoInfoContacts().get(0).getContactName();
                    opoContactPhone = opoInfo.getOpoInfoContacts().get(0).getContactPhone();
                }

                //获取科室协调员的名称和电话

                UserInfo userInfo = new UserDao().getContact(phone);
                String contactName = "";
                String contactPhone = "";
                if (userInfo == null) {
                    contactName = opoContactName;
                    contactPhone = opoContactPhone;
                } else {
                    contactName = userInfo.getTrueName();
                    contactPhone = userInfo.getPhone();
                }


                //南总医院自动加群 谭、唐、刘仁东 、刘军、龚，范丽兰
                //谭晓宇:13926457513,唐姗姗:13078800005,刘仁东:19901727607,刘军:13570231363,龚安安:15920126447，范丽兰：‭13580355978‬
                try {

                    if (toHospName.contains("中国人民解放军南部战区总医院")) {
                        System.out.println("usersIds1:" + usersIds);
                        if (!usersIds.endsWith(",")) {
                            usersIds += ",";
                        }

                        if (!usersIds.contains(contactPhone)) {
                            usersIds += contactPhone + ",";
                        }

                        if (!usersIds.contains("13926457513")) {
                            usersIds += "13926457513" + ",";
                        }
                        if (!usersIds.contains("13078800005")) {
                            usersIds += "13078800005" + ",";
                        }
                        if (!usersIds.contains("19901727607")) {
                            usersIds += "19901727607" + ",";
                        }
                        if (!usersIds.contains("13570231363")) {
                            usersIds += "13570231363" + ",";
                        }
                        if (!usersIds.contains("15920126447")) {
                            usersIds += "15920126447" + ",";
                        }
                        if (!usersIds.contains("13580355978")) {
                            usersIds += "13580355978" + ",";
                        }

                        System.out.println("usersIds:" + usersIds);

                    }

                } catch (Exception e) {

                }


                boolean isTransfer = new RongDao()
                        .isTransferByOrganSeg(organSeg);
                if (!isTransfer) {
                    String isOK = transferDao.insertTransferTransfer(organSeg,
                            getTime, "", organ, "1", blood,
                            bloodNum, sampleOrgan, sampleOrgan1, sampleOrganNum,
                            fromCity, toHospName, tracfficType,
                            tracfficNumber, opoName, contactName,
                            contactPhone, phone, trueName, groupName,
                            usersIds, distance, startLong, startLati,
                            endLong, endLati, toHosp, boxNo, isStart,
                            opoContactName, opoContactPhone, "0", modifyOrganSeg, organAddress, "", temperature, weather);
                    if ("".equals(isOK)) {
                        datas.setResult(CONST.BAD_PARAM);
                        datas.setMsg(CONST.ERROR);
                    } else {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("新建成功");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("organSeg", isOK);
                    }
                } else {
                    datas.setResult(CONST.SEND_FAIL);

                    datas.setMsg("已存在器官段号,请修改器官段号");
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("数据库错误:" + e.getMessage());
            }

            //发送推送给停止的转运
//            for (String key : CONST.ALL_IO_SESSION.keySet()) {
//                  Datas datasTemp = new Datas();
//                if (key.equals(boxNo)) {
//                    datasTemp.setResult(CONST.SEND_OK);
//                    datasTemp.setMsg("shutDownTransfer");
//                    datasTemp.setObj(boxNo);
//                    CONST.ALL_IO_SESSION.get(key).write(new Gson().toJson(datasTemp));
//                    System.out.println("发送shutdown" + "key:" + key);
//                    break;
//                }
//            }

//			} else {
//				datas.setResult(CONST.BAD_PARAM);
//				datas.setMsg("参数错误");
//			}
        } else if ("create1".equals(action)) {
            /**
             * @param organSeg
             *            器官段号
             * @param getTime
             *            获取时间
             * @param openPsd
             *            开箱密码
             * @param organ
             *            器官
             * @param organNum
             *            器官数量
             * @param blood
             *            血液
             * @param bloodNum
             *            血液数量
             * @param sampleOrgan
             *            样本组织
             * @param sampleOrganNum
             *            样本组织数量
             * @param fromCity
             *            开始城市
             * @param toHospName
             *            结束城市
             * @param tracfficType
             *            转运方式
             * @param tracfficNumber
             *            航班
             * @param opoName
             *            opo名称
             * @param contactName
             *            联系人名称
             * @param contactPhone
             *            联系人电话
             * @param phone
             *            本人电话
             * @param trueName
             *            本人姓名 18
             */
            String organSeg = request.getParameter("organSeg");
            String getTime = request.getParameter("getTime");
            String openPsd = request.getParameter("openPsd");
            String organ = request.getParameter("organ");
            String organNum = request.getParameter("organNum");
            String blood = request.getParameter("blood");
            String bloodNum = request.getParameter("bloodNum");
            String sampleOrgan = request.getParameter("sampleOrgan");
            String sampleOrganNum = request.getParameter("sampleOrganNum");
            String fromCity = request.getParameter("fromCity");
            String toHospName = request.getParameter("toHospName");
            String tracfficType = request.getParameter("tracfficType");
            String tracfficNumber = request.getParameter("tracfficNumber");
            String opoName = request.getParameter("opoName");
            String contactName = request.getParameter("contactName");
            String contactPhone = request.getParameter("contactPhone");
            String phone = request.getParameter("phone");
            String trueName = request.getParameter("trueName");
            String groupName = request.getParameter("groupName");
            String usersIds = request.getParameter("usersIds");
            String distance = request.getParameter("distance");
            String startLong = request.getParameter("startLong");
            String startLati = request.getParameter("startLati");
            String endLong = request.getParameter("endLong");
            String endLati = request.getParameter("endLati");
            String toHosp = request.getParameter("toHosp");
            String boxNo = request.getParameter("boxNo");
            String isStart = request.getParameter("isStart");
            String opoContactName = request.getParameter("opoContactName");
            String opoContactPhone = request.getParameter("opoContactPhone");
            String autoTransfer = request.getParameter("autoTransfer");
            String modifyOrganSeg = request.getParameter("modifyOrganSeg");
            String organAddress = request.getParameter("organAddress");
            String organTime = request.getParameter("organTime");


//			if (organSeg != null && !"".equals(organSeg) && getTime != null
//					&& !"".equals(getTime) && organ != null
//					&& !"".equals(organ) && organNum != null
//					&& !"".equals(organNum) && blood != null
//					&& !"".equals(blood) && bloodNum != null
//					&& !"".equals(bloodNum) && sampleOrgan != null
//					&& !"".equals(sampleOrgan) && sampleOrganNum != null
//					&& !"".equals(sampleOrganNum) && fromCity != null
//					&& !"".equals(fromCity) && toHospName != null
//					&& !"".equals(toHospName) && tracfficType != null
//					&& !"".equals(tracfficType) && opoName != null
//					&& !"".equals(opoName) && contactName != null
//					&& !"".equals(contactName) && contactPhone != null
//					&& !"".equals(contactPhone) && phone != null
//					&& !"".equals(phone) && trueName != null
//					&& !"".equals(trueName) && groupName != null
//					&& !"".equals(groupName) && usersIds != null
//					&& !"".equals(usersIds) && distance != null
//					&& !"".equals(distance) && startLong != null
//					&& !"".equals(startLong) && startLati != null
//					&& !"".equals(startLati) && endLong != null
//					&& !"".equals(endLong) && endLati != null
//					&& !"".equals(endLati)) {


            try {

                //
                //如果手动创建,就停止掉自动转运的转运
                String autoTransferOrganSeg = new BoxDao().autoTransferOrganSeg(boxNo);
                if (autoTransferOrganSeg != null) {
                    transferDao.updateTransferStatus(autoTransferOrganSeg, boxNo);
                }


                boolean isTransfer = new RongDao()
                        .isTransferByOrganSeg(organSeg);
                if (!isTransfer) {
                    String isOK = transferDao.insertTransfer(organSeg,
                            getTime, openPsd, organ, organNum, blood,
                            bloodNum, sampleOrgan, sampleOrganNum,
                            fromCity, toHospName, tracfficType,
                            tracfficNumber, opoName, contactName,
                            contactPhone, phone, trueName, groupName,
                            usersIds, distance, startLong, startLati,
                            endLong, endLati, toHosp, boxNo, isStart,
                            opoContactName, opoContactPhone, autoTransfer, modifyOrganSeg, organAddress, organTime);
                    if ("".equals(isOK)) {
                        datas.setResult(CONST.BAD_PARAM);
                        datas.setMsg(CONST.ERROR);
                    } else {
                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("新建成功");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("organSeg", isOK);
                    }
                } else {
                    datas.setResult(CONST.SEND_FAIL);

                    datas.setMsg("已存在器官段号");
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("我也不知道为什么:" + e.getMessage());
            }

//			} else {
//				datas.setResult(CONST.BAD_PARAM);
//				datas.setMsg("参数错误");
//			}
        } else if ("hospitalGroup".equals(action)) {
//            String trueName = request.getParameter("trueName");
//            String phone = request.getParameter("phone");
//            String hospitalName = request.getParameter("hospitalName");
//            transferDao.createHospitalGroup(phone, hospitalName, trueName);
            datas.setResult(CONST.SEND_OK);
            datas.setMsg("发送成功");

        } else if ("updateTransfer".equals(action)) {
            /**
             * @param organSeg
             *            器官段号
             * @param getTime
             *            获取时间
             * @param openPsd
             *            开箱密码
             * @param organ
             *            器官
             * @param organNum
             *            器官数量
             * @param blood
             *            血液
             * @param bloodNum
             *            血液数量
             * @param sampleOrgan
             *            样本组织
             * @param sampleOrganNum
             *            样本组织数量
             * @param fromCity
             *            开始城市
             * @param toHospName
             *            结束城市
             * @param tracfficType
             *            转运方式
             * @param tracfficNumber
             *            航班
             * @param opoName
             *            opo名称
             * @param contactName
             *            联系人名称
             * @param contactPhone
             *            联系人电话
             * @param phone
             *            本人电话
             * @param trueName
             *            本人姓名 18
             */
            String organSeg = request.getParameter("organSeg");
            String getTime = request.getParameter("getTime");
            String openPsd = request.getParameter("openPsd");
            String organ = request.getParameter("organ");
            String organNum = request.getParameter("organNum");
            String blood = request.getParameter("blood");
            String bloodNum = request.getParameter("bloodNum");
            String sampleOrgan = request.getParameter("sampleOrgan");
            String sampleOrganNum = request.getParameter("sampleOrganNum");
            String fromCity = request.getParameter("fromCity");
            String toHospName = request.getParameter("toHospName");
            String tracfficType = request.getParameter("tracfficType");
            String tracfficNumber = request.getParameter("tracfficNumber");
            //String opoName = request.getParameter("opoName");
            String contactName = request.getParameter("contactName");
            String contactPhone = request.getParameter("contactPhone");
            String phone = request.getParameter("phone");
            String trueName = request.getParameter("trueName");
            String groupName = request.getParameter("groupName");
            String usersIds = request.getParameter("usersIds");
            String distance = request.getParameter("distance");
            String startLong = request.getParameter("startLong");
            String startLati = request.getParameter("startLati");
            String endLong = request.getParameter("endLong");
            String endLati = request.getParameter("endLati");
            String toHosp = request.getParameter("toHosp");
//            String opoContactName = request.getParameter("opoContactName");
//            String opoContactPhone = request.getParameter("opoContactPhone");
            String isStart = request.getParameter("isStart");
            String modifyOrganSeg = request.getParameter("modifyOrganSeg");
            String autoTransfer = request.getParameter("autoTransfer");
            String historyModify = request.getParameter("historyModify");
            String opoName = toHospName;
            String status = request.getParameter("status");
            //根据箱子获取肝脏类型
            //String organ = new BoxDao().getOrgan(boxNo);

            //获取opo人员和电话
            OpoInfo opoInfo = new OpoDao().getOpo(toHospName);
            String opoContactName = "";
            String opoContactPhone = "";
            if (opoInfo != null && opoInfo.getOpoInfoContacts() != null && opoInfo.getOpoInfoContacts().size() > 0) {
                opoContactName = opoInfo.getOpoInfoContacts().get(0).getContactName();
                opoContactPhone = opoInfo.getOpoInfoContacts().get(0).getContactPhone();
            }

            //获取科室协调员的名称和电话

//            UserInfo userInfo = new UserDao().getContact(phone);
//            String contactName = "";
//            String contactPhone = "";
//            if (userInfo == null) {
//                contactName = opoContactName;
//                contactPhone = opoContactPhone;
//            } else {
//                contactName = userInfo.getTrueName();
//                contactPhone = userInfo.getPhone();
//            }


            if (organSeg != null && !"".equals(organSeg)
                    && getTime != null
                    && !"".equals(getTime) && organ != null
                    && !"".equals(organ) && organNum != null
                    && !"".equals(organNum)

//					&& blood != null
//					&& !"".equals(blood) && bloodNum != null
//					&& !"".equals(bloodNum) && sampleOrgan != null
//					&& !"".equals(sampleOrgan) && sampleOrganNum != null
//					&& !"".equals(sampleOrganNum) 

                    && fromCity != null && !"".equals(fromCity) && toHospName != null
                    && !"".equals(toHospName) && opoName != null
                    && !"".equals(opoName) && contactName != null
                    && !"".equals(contactName) && contactPhone != null
                    && !"".equals(contactPhone) && phone != null
                    && !"".equals(phone) && trueName != null
                    && !"".equals(trueName) && distance != null
                    && !"".equals(distance) && startLong != null
                    && !"".equals(startLong) && startLati != null
                    && !"".equals(startLati) && endLong != null
                    && !"".equals(endLong) && endLati != null
                    && !"".equals(endLati)
                    ) {

                try {

                    int updateTransfer = transferDao.updateTransfer(organSeg,
                            getTime, openPsd, organ, organNum, blood, bloodNum,
                            sampleOrgan, sampleOrganNum, fromCity, toHospName,
                            tracfficType, tracfficNumber, opoName, contactName,
                            contactPhone, phone, trueName, groupName, usersIds,
                            distance, startLong, startLati, endLong, endLati,
                            toHosp, isStart, opoContactName, opoContactPhone, modifyOrganSeg, autoTransfer, historyModify, status);
                    if (updateTransfer == 1) {

                        datas.setResult(CONST.SEND_OK);
                        datas.setMsg("修改成功成功");
                    } else {
                        datas.setResult(CONST.SEND_FAIL);
                        datas.setMsg(CONST.ERROR);

                    }

                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    datas.setResult(CONST.BAD_PARAM);
                    datas.setMsg("我也不知道为什么:" + e.getMessage());
                }

            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("参数错误");

            }
        } else if ("organRepeat".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            String boxNo = request.getParameter("boxNo");
            if (organSeg != null && !"".equals(organSeg)) {
                boolean isTransfer = new RongDao()
                        .isTransferByOrganSeg(organSeg);

                if (!isTransfer) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("不存在器官段号");
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("存在器官段号");
                }
                String department = new RongDao().gainDepartment(boxNo);

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
                //datas.setObj(map);
            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("参数错误");
            }
        } else if ("organRepeatType".equals(action)) {
            String organSeg = request.getParameter("organSeg");
            String modifyOrganSeg = request.getParameter("modifyOrganSeg");
            modifyOrganSeg = null;
            String boxNo = request.getParameter("boxNo");
            if (organSeg != null && !"".equals(organSeg)) {
                boolean isTransfer = new RongDao()
                        .isTransferByOrganSeg(organSeg);
                boolean isTransferModify = false;

                if (modifyOrganSeg != null && !"".equals(modifyOrganSeg)) {
                    isTransferModify = new RongDao().isTransferByModifyOrganSeg(modifyOrganSeg);
                }

                if (!isTransfer && !isTransferModify) {
                    datas.setResult(CONST.SEND_OK);
                    datas.setMsg("不存在器官段号");
                } else {
                    datas.setResult(CONST.SEND_FAIL);
                    datas.setMsg("存在器官段号");
                }
                String department = new RongDao().gainDepartment(boxNo);

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
                map.put("modifyOrganSeg", transferDao.getModifyOrganSeg(boxNo));
                datas.setObj(map);
            } else {
                datas.setResult(CONST.BAD_PARAM);
                datas.setMsg("参数错误");
            }
        } else if ("startSocket6666s".equals(action)) {
            if (acceptor == null) {
                startSocket6666();
            }

        } else if ("stopSocket6666s".equals(action)) {
            acceptor.unbind(new InetSocketAddress(6666));
        } else {
            datas.setResult(CONST.SEND_FAIL);
            datas.setMsg("没有相应的action");
        }
        out.write(gson.toJson(datas));
        out.flush();
        out.close();
    }

    /**
     * 开启长连接
     */
    private void startSocket6666() {
        // 继承IoService，服务器端接收器
        acceptor = new NioSocketAcceptor();

        // 添加过滤器
        acceptor.getFilterChain().addLast("logf", new LoggingFilter()); // 日志过滤器
        acceptor.getFilterChain().addLast("objType", new ProtocolCodecFilter(new ObjectSerializationCodecFactory())); // 只能传输序列化后的对象

        // 回调
        acceptor.setHandler(new IoHandlerAdapter() {
            // session创建时回调
            public void sessionCreated(IoSession session) throws Exception {
                super.sessionCreated(session);

                System.out.println("sessionCreated ");
            }

            // session打开时回调
            public void sessionOpened(IoSession session) throws Exception {
                super.sessionOpened(session);

                System.out.println("sessionOpened ");
            }

            // 消息接收时回调
            public void messageReceived(IoSession session, Object message) throws Exception {
                super.messageReceived(session, message);


                String msg = message.toString();
                session.write("服务端给返回的消息：" + msg); // 给客户端返回
                //System.out.println("服务端接收消息： " + msg);

                //先移除存在的，在添加
                if (msg.length() == 5) {
                    for (String key : CONST.ALL_IO_SESSION.keySet()) {
                        if (key.equals(msg)) {
                            CONST.ALL_IO_SESSION.get(key).closeNow();
                            CONST.ALL_IO_SESSION.remove(key);
                        }
                    }
                    CONST.ALL_IO_SESSION.put(msg, session);
                    CONST.SEND_PUSH = 10;
                }

                System.out.println("当前个数:" + CONST.ALL_IO_SESSION.size() + ":msg:" + msg);
            }

            // 消息发送时回调
            public void messageSent(final IoSession session, Object message) throws Exception {
                super.messageSent(session, message);
                //session.write(message);
                System.out.println("messageSent " + message);
            }

            // session关闭时回调
            public void sessionClosed(IoSession session) throws Exception {
                super.sessionClosed(session);
                System.out.println("sessionClosed ");
            }
        });

        // 进行一些初始化配置
        acceptor.getSessionConfig().setReadBufferSize(2048); // 设置读缓存区大小
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10); // 如果10秒钟没有任何读写，就设置成空闲状态。 BOTH_IDLE（读和写）

        // 设置监听端口号，开始监听了
        try {
            acceptor.bind(new InetSocketAddress(6666));

            System.out.println("启动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
