package com.life.servlet;

import io.rong.util.HttpUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.life.controller.PushMessageDao;
import com.life.controller.RongDao;
import com.life.controller.TransferDao;
import com.life.controller.TransferRecordDao;
import com.life.entity.Datas;
import com.life.entity.DatasRecord;
import com.life.entity.PathInfo;
import com.life.entity.Record;
import com.life.entity.RecordDatas;
import com.life.entity.TransferJson;
import com.life.entity.TransferRecord;
import com.life.push.PushExample;
import com.life.utils.CONST;

public class TransferRecordServlet extends HttpServlet {

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
		Gson gson = new Gson();
		Datas datas = new Datas();
		DatasRecord datasRecord = new DatasRecord();
		RecordDatas recordDatas = new RecordDatas();
		String action = request.getParameter("action");
		TransferRecordDao transferRecordDao = new TransferRecordDao();
		if ("powerTemp".equals(action)) {
			String time = request.getParameter("time");
			String level = request.getParameter("level");
			String deviceId = request.getParameter("deviceId");
			String transferRecords = transferRecordDao.insertPowerTemp(time,
					level, deviceId);
			if (!"".equals(transferRecords)) {
				datas.setResult(CONST.SEND_OK);
				datas.setMsg(transferRecords);

			} else {
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("转运记录暂无信息");
			}
		}

		else if ("transferRecord".equals(action)) {

			String organSeg = request.getParameter("organSeg");
			if (organSeg != null && !"".equals(organSeg)) {

				List<TransferRecord> transferRecords = transferRecordDao
						.getTransferRecordsByOrganSeg(organSeg, "");

				if (transferRecords.size() > 0) {

					datasRecord.setResult(CONST.SEND_OK);
					datasRecord.setMsg("获取转运记录成功");
					datasRecord.setObj(transferRecords);
					datasRecord.setOpen(transferRecordDao
							.getTransferRecordsByOrganSeg(organSeg,
									"and open=1"));
					datasRecord.setCollision(transferRecordDao
							.getTransferRecordsByOrganSeg(organSeg,
									"and collision=1"));

					List<TransferRecord> collisionAndOpenRecords = transferRecordDao
							.getTransferRecordsByOrganSeg(organSeg,
									" and (open =1 or collision=1) ");

					int open = 0;
					int collision = 0;
					SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
					SimpleDateFormat sdfAll = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					List<PathInfo> pathInfos = new ArrayList<PathInfo>();
					try {
						for (int i = 0; i < collisionAndOpenRecords.size(); i++) {

							if ("1".equals(collisionAndOpenRecords.get(i)
									.getOpen())) {
								open++;
								PathInfo pathInfo = new PathInfo();
								String recordAt = sdf.format(sdfAll
										.parse(collisionAndOpenRecords.get(i)
												.getCreateAt()));
								pathInfo.setMonth(recordAt.split(" ")[0]);
								pathInfo.setTime(collisionAndOpenRecords.get(i)
										.getRecordAt());
								pathInfo.setContent("设备发生第" + open + "次开箱");
								pathInfo.setLongitude(collisionAndOpenRecords
										.get(i).getLongitude());
								pathInfo.setLatitude(collisionAndOpenRecords
										.get(i).getLatitude());
								pathInfo.setLnglat(collisionAndOpenRecords.get(
										i).getLongitude()
										+ ","
										+ collisionAndOpenRecords.get(i)
												.getLatitude());
								pathInfo.setOrderId(i);
								pathInfos.add(pathInfo);

							}
							if ("1".equals(collisionAndOpenRecords.get(i)
									.getCollision())) {
								collision++;
								PathInfo pathInfo = new PathInfo();
								String recordAt = sdf.format(sdfAll
										.parse(collisionAndOpenRecords.get(i)
												.getCreateAt()));
								pathInfo.setMonth(recordAt.split(" ")[0]);
								pathInfo.setTime(recordAt.split(" ")[1]);
								pathInfo
										.setContent("设备发生第" + collision + "次碰撞");
								pathInfo.setLongitude(collisionAndOpenRecords
										.get(i).getLongitude());
								pathInfo.setLatitude(collisionAndOpenRecords
										.get(i).getLatitude());
								pathInfo.setLnglat(collisionAndOpenRecords.get(
										i).getLongitude()
										+ ","
										+ collisionAndOpenRecords.get(i)
												.getLatitude());
								pathInfo.setOrderId(i);
								pathInfos.add(pathInfo);

							}

						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					datasRecord.setInfo(pathInfos);
				} else {
					datasRecord.setResult(CONST.SEND_FAIL);
					datasRecord.setMsg("转运记录暂无信息");
				}

			} else {
				datasRecord.setResult(CONST.BAD_PARAM);
				datasRecord
						.setMsg("transferRecord.do  transferRecord params error");
			}

		} else if ("transferRecordTen".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			if (organSeg != null && !"".equals(organSeg)) {

				List<TransferRecord> transferRecords = transferRecordDao
						.getTransferRecordsByOrganSegTen(organSeg);
				if (transferRecords.size() > 0) {
					datasRecord.setResult(CONST.SEND_OK);
					datasRecord.setMsg("获取转运记录成功");
					datasRecord.setObj(transferRecords);
					datasRecord.setOpen(transferRecordDao
							.getTransferRecordsByOrganSeg(organSeg,
									"and open=1"));
					datasRecord.setCollision(transferRecordDao
							.getTransferRecordsByOrganSeg(organSeg,
									"and collision=1"));

					List<TransferRecord> collisionAndOpenRecords = transferRecordDao
							.getTransferRecordsByOrganSeg(organSeg,
									" and (open =1 or collision=1) ");

					int open = 0;
					int collision = 0;
					SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
					SimpleDateFormat sdfAll = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					List<PathInfo> pathInfos = new ArrayList<PathInfo>();
					try {
						for (int i = 0; i < collisionAndOpenRecords.size(); i++) {

							if ("1".equals(collisionAndOpenRecords.get(i)
									.getOpen())) {
								open++;
								PathInfo pathInfo = new PathInfo();
								String recordAt = sdf.format(sdfAll
										.parse(collisionAndOpenRecords.get(i)
												.getCreateAt()));
								pathInfo.setMonth(recordAt.split(" ")[0]);
								pathInfo.setTime(collisionAndOpenRecords.get(i)
										.getRecordAt());
								pathInfo.setContent("设备发生第" + open + "次开箱");
								pathInfo.setLongitude(collisionAndOpenRecords
										.get(i).getLongitude());
								pathInfo.setLatitude(collisionAndOpenRecords
										.get(i).getLatitude());
								pathInfo.setLnglat(collisionAndOpenRecords.get(
										i).getLongitude()
										+ ","
										+ collisionAndOpenRecords.get(i)
												.getLatitude());
								pathInfos.add(pathInfo);

							}
							if ("1".equals(collisionAndOpenRecords.get(i)
									.getCollision())) {
								collision++;
								PathInfo pathInfo = new PathInfo();
								String recordAt = sdf.format(sdfAll
										.parse(collisionAndOpenRecords.get(i)
												.getCreateAt()));
								pathInfo.setMonth(recordAt.split(" ")[0]);
								pathInfo.setTime(recordAt.split(" ")[1]);
								pathInfo
										.setContent("设备发生第" + collision + "次碰撞");
								pathInfo.setLongitude(collisionAndOpenRecords
										.get(i).getLongitude());
								pathInfo.setLatitude(collisionAndOpenRecords
										.get(i).getLatitude());
								pathInfo.setLnglat(collisionAndOpenRecords.get(
										i).getLongitude()
										+ ","
										+ collisionAndOpenRecords.get(i)
												.getLatitude());
								pathInfos.add(pathInfo);

							}

						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					datasRecord.setInfo(pathInfos);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("转运记录暂无信息");
				}

			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("transferRecord.do  transferRecord params error");
			}

		} else if ("transferTest".equals(action)) {

			List<TransferRecord> transferRecords = transferRecordDao
					.getTestLongitude();
			if (transferRecords.size() > 0) {
				datas.setResult(CONST.SEND_OK);
				datas.setMsg("获取转运记录成功");
				datas.setObj(transferRecords);
			} else {
				datas.setResult(CONST.SEND_FAIL);
				datas.setMsg("转运记录暂无信息");
			}

		} else if ("transferRecordSample".equals(action)) {
			String organSeg = request.getParameter("organSeg");
			if (organSeg != null && !"".equals(organSeg)) {

				List<TransferRecord> transferRecords = transferRecordDao
						.getTransferRecordsByOrganSegSample(organSeg);
				if (transferRecords.size() > 0) {
					datas.setResult(CONST.SEND_OK);
					datas.setMsg("获取转运记录成功");
					datas.setObj(transferRecords);
				} else {
					datas.setResult(CONST.SEND_FAIL);
					datas.setMsg("转运记录暂无信息");
				}

			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("transferRecord.do  transferRecord params error");
			}

		}
		/**
		 * 记录pad上传的数据
		 */
		else if ("record".equals(action)) {

			String temperature = request.getParameter("temperature");
			String humidity = request.getParameter("humidity");
			String collision = request.getParameter("collision");
			String longitude = request.getParameter("longitude");
			String latitude = request.getParameter("latitude");
			String city = request.getParameter("city");
			String power = request.getParameter("power");
			String expendPower = request.getParameter("expendPower");
			String transferId = request.getParameter("transferId");
			String recordAt = request.getParameter("recordAt");
			String distance = request.getParameter("distance");
			String duration = request.getParameter("duration");
			String open = request.getParameter("open");
			String index = request.getParameter("index");

			// System.out.println("temperature:" + temperature);
			// System.out.println("humidity:" + humidity);
			// System.out.println("collision:" + collision);
			// System.out.println("longitude:" + longitude);
			// System.out.println("latitude:" + latitude);
			// System.out.println("city" + city);
			// System.out.println("power:" + power);
			// System.out.println("expendPower:" + expendPower);
			// System.out.println("transferId:" + transferId);
			// System.out.println("recordAt:" + recordAt);
			// System.out.println("distance:" + distance);
			// System.out.println("duration:" + duration);
			// System.out.println("open:" + open);
			// System.out.println("+++++++++++++++++++++++++++++++");

			if (temperature != null && !"".equals(temperature)
					&& humidity != null && !"".equals(humidity)
					&& collision != null && !"".equals(collision)
					&& longitude != null && !"".equals(longitude)
					&& latitude != null && !"".equals(latitude) && city != null
					&& !"".equals(city) && power != null && !"".equals(power)
					&& expendPower != null && !"".equals(expendPower)
					&& transferId != null && !"".equals(transferId)
					&& recordAt != null && !"".equals(recordAt)
					&& distance != null && !"".equals(distance)
					&& duration != null && !"".equals(duration) && open != null
					&& !"".equals(open)) {

				try {

					int flag = transferRecordDao.insertRecord(temperature,
							humidity, collision, longitude, latitude, city,
							power, expendPower, transferId, recordAt, distance,
							duration, open);

					if (flag == 1) {
						datas.setResult(CONST.SEND_OK);
						datas.setMsg(index);
						datas.setObj("this");
					} else {

						datas.setResult(CONST.BAD_PARAM);
						datas.setMsg(CONST.ERROR);

					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					datas.setResult(CONST.BAD_PARAM);
					datas.setMsg("我也不知道为什么:" + e.getMessage());
				}

			} else {
				datas.setResult(CONST.BAD_PARAM);
				datas.setMsg("参数错误");
			}

		} else if ("records".equals(action)) {
			String records = request.getParameter("records");

			// type 未开始转运 noStart 开始 start
			String type = request.getParameter("type");
			String organSeg = request.getParameter("organSeg");

			// Json的解析类对象
			JsonParser parser = new JsonParser();
			// 将JSON的String 转成一个JsonArray对象
			JsonArray jsonArray = parser.parse(records).getAsJsonArray();
			//System.out.println(records);

			ArrayList<Record> recordList = new ArrayList<Record>();

			// 加强for循环遍历JsonArray
			for (JsonElement user : jsonArray) {
				// 使用GSON，直接转成Bean对象
				Record userBean = gson.fromJson(user, Record.class);
				recordList.add(userBean);
			}

			List<Integer> lists = transferRecordDao.insertRecords(recordList,
					type);
			boolean isStop = new TransferDao().transferDown(organSeg);

			// 转运信息
			TransferJson transferJson = new TransferDao()
					.getTransferByOrganSeg(organSeg);

			if (lists.size() > 0) {
				recordDatas.setResult(CONST.SEND_OK);
				recordDatas.setObj(lists);
				recordDatas.setInfo(transferJson);

			} else {
				recordDatas.setResult(CONST.SEND_FAIL);
				recordDatas.setObj(lists);
			}
			// 变量的赋值变化 todo
			// 距离目的地多远停止转运(km)
			double END_DISTANCE = 0.2;
			// 20公里发送短信(km)
			double END_DISTANCE_20 = 20;
			// 停止转运后,多少时间关机 60*60s
			long END_TIME = 60 * 60;
			
			// 温度的异常时间
			int EXCEPTION_TIME = 1000 * 60 * 20;
			// 串口发送的间隔时间 ms
			int SERIAL_PERIOD = 500;
			// 上传的值
			int UPLOAD_NUM_VALUE = 15;
			// 串口循环的时间
			int SERIAL_NUM = 1;

			// 30s 30000ms 间隔时间
			long SERIAL_TIME = 30000;
			// 每页的页数
			int PAGE_SIZE = 20;
			// 电量
			int POWER = 13;
			
			//自动开始的时间
			long START_TIME = 60 * 15;
            //自动停止转运
			boolean isStart = false;
			boolean isStopRepeat = true;
			boolean isClose = true;
			boolean isPlaneShow = true;
			boolean isTemperature = true;
			boolean isOpen = true;
			boolean isHour24 = true;
			//设备编号 可设置多个设备(所有的设备)
			String device = "";
			//device = transferRecordDao.getAllDevice();
			//是否设置自动类型
			boolean isSite = true;
			
			//一键转运开关
			boolean isTransfer = false;
			
			   boolean isOver24Hour  = new TransferDao().overTransferTime(organSeg);
	            if(isOver24Hour){
	            	 String deviceId = new TransferDao().getDeviceByOrganSeg(organSeg);
	            	 if(!"".equals(deviceId)){
	            		 CONST.DEVICE_ID += deviceId + "@";
	            	 }
	            	
	            }
		 
//			CONST.DEVICE_ID+="35390485024627@";
//			CONST.DEVICE_ID+="35527325159290@";
			//0.2,20.0,3600,1200000,500,10,1,30000,20,13,900,false,true,true,true,true,true,true,,true,3532086430233635467180597450,
            String values = END_DISTANCE +","+END_DISTANCE_20+","+END_TIME+","+EXCEPTION_TIME+","+SERIAL_PERIOD
            +","+UPLOAD_NUM_VALUE+","+SERIAL_NUM+","+SERIAL_TIME+","+PAGE_SIZE+","+POWER+","+START_TIME
            +","+isStart+","+isStopRepeat+","+isClose+","+isPlaneShow+","+isTemperature+","+isOpen
            +","+isHour24+","+device+","+isSite +","+CONST.DEVICE_ID+","+isTransfer;
          //自动关机
            
            //是否暂停
            boolean isPause = new TransferDao().getTransferStatus(organSeg);
         
			recordDatas.setMsg(isStop + "="
					+ isOver24Hour+"="+values+"="+isPause);
			 

		}
		// 处理异常
		else if ("recordException".equals(action)) {
			String temperatureException = request
					.getParameter("temperatureException");
			String temperature = request.getParameter("temperature");
			String openException = request.getParameter("openException");
			String open = request.getParameter("open");
			String collisionException = request
					.getParameter("collisionException");
			String collision = request.getParameter("collision");
			String transferId = request.getParameter("transferId");
			String organSeg = request.getParameter("organSeg");
			String modifyOrganSeg = request.getParameter("modifyOrganSeg");
			String powerException = request.getParameter("powerException");
			String power = request.getParameter("power");
			String powerType = request.getParameter("powerType");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date());

			String phones[] = new RongDao().getPhonesByGroupId(organSeg).split(
					",");
			
			 

			// 温度异常
			if ("true".equals(temperatureException)) {
				String phoneArr[] = getPushPhones(phones, organSeg,
						"temperatureStatus", transferRecordDao);
				// String .format("%.1f",temperature)
				String organSegTemp = organSeg;
				if(modifyOrganSeg!=null&&!"".equals(modifyOrganSeg)){
					organSegTemp = modifyOrganSeg;
				}
				String content = "【器官段号:" + organSegTemp + "】温度为" + temperature
						+ "℃,高于正常的0℃-10℃,请及时处理";
				// modify
				// PushExample.sendPushOrganSeg(content, new
				// RongDao().getPhonesByGroupId(organSeg).split(","));
				//对电话号码去重
				String phoneArrRepeat []= repeatPhones(phoneArr);
				new PushMessageDao().insertExceptionPush(content, createTime,
						"temperature", transferId, phoneArrRepeat);

				String json = HttpUtil.sendPushJson(phoneArr, "温度异常", content);
				HttpUtil.sendJson(com.life.utils.URL.YUN_BA, json);
			}

			// 开箱异常
			if ("true".equals(openException)) {
				String organSegTemp = organSeg;
				if(modifyOrganSeg!=null&&!"".equals(modifyOrganSeg)){
					organSegTemp = modifyOrganSeg;
				}
				String content = "【器官段号:" + organSegTemp + "】发生第"
						+ ((int) Double.parseDouble(open)) + "次开箱";
				// modify
				// PushExample.sendPushOrganSeg(content, new
				// RongDao().getPhonesByGroupId(organSeg).split(","));
				String phoneArr[] = getPushPhones(phones, organSeg,
						"openStatus", transferRecordDao);
				
				//对电话号码去重
				String phoneArrRepeat []= repeatPhones(phoneArr);

				new PushMessageDao().insertExceptionPush(content, createTime,
						"open", transferId, phoneArrRepeat);

				String json = HttpUtil.sendPushJson(phoneArr, "开箱提醒", content);
				HttpUtil.sendJson(com.life.utils.URL.YUN_BA, json);
			}

			// 碰撞异常
			if ("true".equals(collisionException)) {
				String organSegTemp = organSeg;
				if(modifyOrganSeg!=null&&!"".equals(modifyOrganSeg)){
					organSegTemp = modifyOrganSeg;
				}
				String content = "【器官段号:" + organSegTemp + "】发生第"
						+ ((int) Double.parseDouble(collision)) + "次碰撞异常,请及时处理";
				// modify
				// PushExample.sendPushOrganSeg(content, new
				// RongDao().getPhonesByGroupId(organSeg).split(","));
				String phoneArr[] = getPushPhones(phones, organSeg,
						"collisionStatus", transferRecordDao);
				//对电话号码去重
				String phoneArrRepeat []= repeatPhones(phoneArr);
				new PushMessageDao().insertExceptionPush(content, createTime,
						"collision", transferId, phoneArrRepeat);

				String json = HttpUtil.sendPushJson(phoneArr, "碰撞异常", content);
				HttpUtil.sendJson(com.life.utils.URL.YUN_BA, json);
			}

			// 电量提醒
			if ("true".equals(powerException)) {
				String organSegTemp = organSeg;
				if(modifyOrganSeg!=null&&!"".equals(modifyOrganSeg)){
					organSegTemp = modifyOrganSeg;
				}
				String content = "";
				String contentUrl = "";
				if ("start".equals(powerType)) {
					content = "【器官段号:" + organSegTemp + "】器官转运已开始，当前设备电量为" + power
							+ "，请注意保持充足电量。";
					contentUrl = "【器官段号:" + organSegTemp + "】器官转运已开始，当前设备电量为"
							+ power + "%，请注意保持电量充足。";
				} else if ("end".equals(powerType)) {
					content = "【器官段号:" + organSegTemp + "】器官转运已停止，当前设备电量为" + power
							+ "，请及时充电，保证下次转运正常运行。";
					contentUrl = "【器官段号:" + organSegTemp + "】器官转运已停止，当前设备电量为"
							+ power + "%，请及时充电，保证下次转运正常运行。";
				} else if ("exception".equals(powerType)) {
					content = "【器官段号:" + organSegTemp + "】电量不足，当前设备电量为" + power
							+ "，请及时充电，保证本次转运正常运行。";
					contentUrl = "【器官段号:" + organSegTemp + "】电量不足，当前设备电量为" + power
							+ "%，请及时充电，保证本次转运正常运行。";
				}
				
				if (!"".equals(content)) {
					//对电话号码去重
					String phoneArrRepeat []= repeatPhones(phones);
					new PushMessageDao().insertExceptionPush(contentUrl,
							createTime, "power", transferId, phoneArrRepeat);
					String json = HttpUtil.sendPushJson(new RongDao()
							.getPhonesByGroupId(organSeg).split(","), "电量提醒",
							content);

					HttpUtil.sendJson(com.life.utils.URL.YUN_BA, json);
				}
			}

		}

		else {
			datas.setResult(CONST.BAD_PARAM);
			datas.setMsg("transferRecord.do  no action");
		}
		// 发送记录的信息
		if ("records".equals(action)) {

			out.print(gson.toJson(recordDatas));
		} else if ("transferRecord".equals(action)
				|| "transferRecordTen".equals(action)) {
			out.print(gson.toJson(datasRecord));
		} else {
			out.print(gson.toJson(datas));
		}
		// System.out.println(gson.toJson(datasRecord));
		out.flush();
		out.close();
	}
	private String [] repeatPhones(String phones []){
		String phoneTemp = "";
		List<String> lists = new ArrayList();
		for(int i=0;i<phones.length;i++){
			if(!phoneTemp.contains(phones[i])){
				lists.add(phones[i]);
			}
			phoneTemp += phones[i];
		}
		String [] returnPhones = new String[lists.size()];
		for(int i=0;i<lists.size();i++){
			returnPhones[i] = lists.get(i);
		}
		return returnPhones;
	}

	public static void main(String[] args) {
		double d = 3.1415926;

		String result = String.format("%.1f", d);
		//System.out.println(result);
	}

	private String[] getPushPhones(String[] pPhones, String pOrganSeg,
			String pStatus, TransferRecordDao pTransferRecordDao) {
		List<String> phonePush = new ArrayList<String>();
		for (int i = 0; i < pPhones.length; i++) {
			int temperatureStatus = pTransferRecordDao.getUserPushSite(
					pPhones[i], pStatus);
			if (temperatureStatus == CONST.NO_SEND_PUSH) {
				continue;
			} else {
				int transferTemperatureStatus = pTransferRecordDao
						.getTransferPushSite(pPhones[i], pOrganSeg, pStatus);
				if (transferTemperatureStatus == CONST.NO_SEND_PUSH) {
					continue;
				} else {
					phonePush.add(pPhones[i]);
				}
			}
		}
		String phoneArr[] = new String[phonePush.size()];
		for (int i = 0; i < phonePush.size(); i++) {
			phoneArr[i] = phonePush.get(i);
			//System.out.println("phonePush:" + phonePush.get(i));
		}
		return phoneArr;
	}
}
