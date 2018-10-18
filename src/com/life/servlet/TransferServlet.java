package com.life.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.life.controller.RongDao;
import com.life.controller.TransferDao;
import com.life.entity.Datas;
import com.life.entity.OpoInfo;
import com.life.entity.TransferJson;
import com.life.entity.TransferPushSite;
import com.life.utils.CONST;

public class TransferServlet extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
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

		}
		
		else if("closePower".equals(action)){
			String deviceId = request.getParameter("deviceId");
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
			}else{
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("移除失败");
			}
		}
		else if ("updateStart".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			String isStart = request.getParameter("isStart");
			String type = request.getParameter("type");
			if (organSeg != null && !"".equals(organSeg) && isStart != null
					&& !"".equals(isStart)) {
				//判断是否打开设备
				boolean deviceStatus = transferDao.getDeviceStatus(organSeg);
				//modify
				deviceStatus  = true;
				if(deviceStatus){
					
				int isOK = transferDao.updateStartByOrganSeg(organSeg, isStart,type);
				if (isOK != -1) {				
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("更改状态成功");
				}else{
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("设备未开启");
				}
				 
					
				
				}else{
					
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("设备未开启");
					}
				}
			 else {
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("参数错误");
			}

		}

		else if ("shutDownTransfer".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			String boxNo = request.getParameter("boxNo");
			//System.out.println("boxNo:" + boxNo + ",organSeg:" + organSeg);

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

		}

		else if ("getTransferList".equals(action)) {
			String phone = request.getParameter("phone");
			String page = request.getParameter("page");
			String pageSize = request.getParameter("pageSize");
			// System.out.println("getTransferList,phone: " + phone);
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

					datas.setMsg((new Date().getTime() - 10 * 60 * 1000*0) + "");
					datas.setObj(lists);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg((new Date().getTime() - 10 * 60 * 1000*0) + "");
				}
			} else {
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg((new Date().getTime() - 10 * 60 * 1000*0) + "");
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

		}

		else if ("getTransferHistory".equals(action)) {
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
			
			if(integrity!=null&&!"".equals(integrity)){
			       if("待完善".equals(integrity)){
			    	   condition += " and t.autoTransfer = 1";
			       }else if("已完善".equals(integrity)){
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
					
					if(page==0){
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("没有数据");
						datas.setObj(lists);
					}else{
						datas.setResult(CONST.NO_MORE);
						datas.setMsg("没有更多" );
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
					if(page==0){
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("没有数据");
						datas.setObj(lists);
					}else{
						datas.setResult(CONST.NO_MORE);
						datas.setMsg("没有更多" );
					}
				}
			} else {
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("参数错误");
			}
		}

		else if ("getPadTransferHistory".equals(action)) {
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
					if(page==0){
						datas.setResult(CONST.SEND_OK);
						datas.setMsg("没有数据");
						datas.setObj(lists);
					}else{
						datas.setResult(CONST.NO_MORE);
						datas.setMsg("没有更多" );
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
		}

		else if ("create".equals(action)) {
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
			
			// System.out.println("organSeg:" + organSeg);
			// System.out.println("getTime:" + getTime);
			// System.out.println("openPsd:" + openPsd);
			// System.out.println("organ:" + organ);
			// System.out.println("organNum:" + organNum);
			// System.out.println("blood:" + blood);
			// System.out.println("bloodNum:" + bloodNum);
			// System.out.println("sampleOrgan:" + sampleOrgan);
			// System.out.println("sampleOrganNum:" + sampleOrganNum);
			// System.out.println("fromCity:" + fromCity);
			// System.out.println("toHospName:" + toHospName);
			// System.out.println("tracfficType:" + tracfficType);
			// System.out.println("tracfficNumber:" + tracfficNumber);
			// System.out.println("opoName:" + opoName);
			// System.out.println("contactName:" + contactName);
			// System.out.println("contactPhone:" + contactPhone);
			// System.out.println("contactPhone:" + phone);
			// System.out.println("trueName:" + trueName);
			// System.out.println("groupName:" + groupName);
			// System.out.println("usersIds:" + usersIds);
			// System.out.println("distance:" + distance);
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
				// System.out.println("organSeg:" + organSeg);

				try {
					
					//
					//如果手动创建,就停止掉自动转运的转运
					String autoTransferOrganSeg =new BoxDao().autoTransferOrganSeg(boxNo);
					if(autoTransferOrganSeg!=null){
					 transferDao.updateTransferStatus(autoTransferOrganSeg,boxNo);
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
								opoContactName, opoContactPhone,autoTransfer,modifyOrganSeg,organAddress,organTime);
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
		}
		else if("hospitalGroup".equals(action)){
			String trueName = request.getParameter("trueName");
			String phone = request.getParameter("phone");
			String hospitalName = request.getParameter("hospitalName");
			transferDao.createHospitalGroup(phone, hospitalName,trueName);
			datas.setResult(CONST.SEND_OK);
			datas.setMsg("发送成功");
			
		}

		else if ("updateTransfer".equals(action)) {
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
			String opoContactName = request.getParameter("opoContactName");
			String opoContactPhone = request.getParameter("opoContactPhone");
			String isStart = request.getParameter("isStart");
			String modifyOrganSeg = request.getParameter("modifyOrganSeg");
			String autoTransfer = request.getParameter("autoTransfer");
			String historyModify = request.getParameter("historyModify");
			 
			String status = request.getParameter("status");
			 

//			System.out.println("organSeg:" + organSeg);
//			System.out.println("getTime:" + getTime);
//			System.out.println("openPsd:" + openPsd);
//			System.out.println("organ:" + organ);
//			System.out.println("organNum:" + organNum);
//			System.out.println("blood:" + blood);
//			System.out.println("bloodNum:" + bloodNum);
//			System.out.println("sampleOrgan:" + sampleOrgan);
//			System.out.println("sampleOrganNum:" + sampleOrganNum);
//			System.out.println("fromCity:" + fromCity);
//			System.out.println("toHospName:" + toHospName);
//			System.out.println("tracfficType:" + tracfficType);
//			System.out.println("tracfficNumber:" + tracfficNumber);
//			System.out.println("opoName:" + opoName);
//			System.out.println("contactName:" + contactName);
//			System.out.println("contactPhone:" + contactPhone);
//			System.out.println("phone:" + phone);
//			System.out.println("trueName:" + trueName);
//			System.out.println("distance:" + distance);
//			System.out.println("startLong:" + startLong);
//			System.out.println("startLati:" + startLati);
//			System.out.println("endLong:" + endLong);
//			System.out.println("endLati:" + endLati);
//			System.out.println("autoTransfer:" + autoTransfer);
//			System.out.println("modifyOrganSeg:" + modifyOrganSeg);
			if (organSeg != null && !"".equals(organSeg) && getTime != null
					&& !"".equals(getTime) && organ != null
					&& !"".equals(organ) && organNum != null
					&& !"".equals(organNum)
					
//					&& blood != null
//					&& !"".equals(blood) && bloodNum != null
//					&& !"".equals(bloodNum) && sampleOrgan != null
//					&& !"".equals(sampleOrgan) && sampleOrganNum != null
//					&& !"".equals(sampleOrganNum) 
					
					&& fromCity != null&& !"".equals(fromCity) && toHospName != null
					&& !"".equals(toHospName) && tracfficType != null
					&& !"".equals(tracfficType) && opoName != null
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
							toHosp,isStart, opoContactName, opoContactPhone,modifyOrganSeg,autoTransfer,historyModify,status);
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
				// System.out.println("参数错误");
			}
		}

		else if ("organRepeat".equals(action)) {
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
				//System.out.println(department);
				if("心脏科".equals(department)){
					department = "心脏";
				}else if("眼科科".equals(department)){
					department = "眼角膜";
				}else if("肝脏科".equals(department)){
					department = "肝脏";
				}else if("肺科".equals(department)){
					department = "肺";
				}else if("肾脏科".equals(department)){
					department = "肾脏";
				}else if("胰脏科".equals(department)){
					department = "胰脏";
				}else {
					department = "肝脏";
				}
				HashMap< String, String> map = new HashMap<String, String>();
				map.put("type", department);
				//datas.setObj(map);
			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");
			}
		}
		else if ("organRepeatType".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			String modifyOrganSeg = request.getParameter("modifyOrganSeg");
			modifyOrganSeg = null;
			String boxNo = request.getParameter("boxNo");
			if (organSeg != null && !"".equals(organSeg)) {
				boolean isTransfer = new RongDao()
						.isTransferByOrganSeg(organSeg);
				boolean isTransferModify = false;
				
				if(modifyOrganSeg!=null&&!"".equals(modifyOrganSeg)){
					isTransferModify = new RongDao().isTransferByModifyOrganSeg(modifyOrganSeg);
				}
				
				if (!isTransfer&&!isTransferModify) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("不存在器官段号");
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("存在器官段号");
				}
				String department = new RongDao().gainDepartment(boxNo);
				//System.out.println(department);
				if("心脏科".equals(department)){
					department = "心脏";
				}else if("眼科科".equals(department)){
					department = "眼角膜";
				}else if("肝脏科".equals(department)){
					department = "肝脏";
				}else if("肺科".equals(department)){
					department = "肺";
				}else if("肾脏科".equals(department)){
					department = "肾脏";
				}else if("胰脏科".equals(department)){
					department = "胰脏";
				}else {
					department = "肝脏";
				}
				HashMap< String, String> map = new HashMap<String, String>();
				map.put("type", department);
				map.put("modifyOrganSeg",  transferDao.getModifyOrganSeg(boxNo));
				datas.setObj(map);
			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");
			}
		}
		else {
			datas.setResult(CONST.SEND_FAIL);
			datas.setMsg("没有相应的action");
		}
		out.write(gson.toJson(datas));
		out.flush();
		out.close();
	}

}
