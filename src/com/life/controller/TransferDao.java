package com.life.controller;

import io.rong.RongCloud;
import io.rong.messages.DizNtfMessage;
import io.rong.messages.InfoNtfMessage;
import io.rong.models.CodeSuccessResult;
import io.rong.models.GroupUserQueryResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.life.entity.OpoInfo;
import com.life.entity.RongUserJson;
import com.life.entity.TransferJson;
import com.life.entity.TransferPushSite;
import com.life.utils.CONST;
import com.life.utils.ConnectionDB;
import com.life.utils.LocationUtils;
import com.mysql.jdbc.Statement;

public class TransferDao {
	private ConnectionDB connDB = new ConnectionDB();

	// 创建数据库连接对象
	private Connection conn = null;
	String appKey = "n19jmcy5nety9";// 
	String appSecret = "SdwTI3aFmYb";// 
	RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);

	/**
	 * 提交事物 conn.setAutoCommit(false)); //禁止自动提交事务 conn.commit());
	 * conn.rollback());
	 */

	public boolean createTransfer() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "insert into  organ_new(segment_num,organ_type,blood_type,organ_sample_type,organ_num,blood_sample_num,organ_sample_type,"
				+ "create_time) values(?,?,?,?,?,?,?)";
		// 调用SQL
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String create_time = sdf.format(new Date());
			rs = ps.executeQuery();
			while (rs.next()) {
				//System.out.println(rs.getTime("create_time"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return false;
			}
			return false;

		}finally{
			connDB.closeAll(rs, ps, conn);
		}
		
		return false;
	}

	/**
	 * 获取转运中的转运 判断是否超过24小时 判断是否超过3小时没有上传数据
	 * 
	 */
	public List<TransferJson> getTransferNoFinish() {
		List<TransferJson> lists = new ArrayList<TransferJson>();
		ResultSet rs = null;
		ResultSet rsRecord = null;
		 
		PreparedStatement ps = null;
		String sql = "SELECT transferid,transferNumber,getTime,box_id,to_hosp_id,isStart,b.deviceId FROM transfer t,box b WHERE t.box_id = b.boxId AND t.`status` = 'transfering' ";

		try {
			conn = connDB.getConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				TransferJson t = new TransferJson();
				t.setTransferid(rs.getString("transferid"));
				t.setOrganSeg(rs.getString("transferNumber"));
				t.setGetTime(rs.getString("getTime"));
				t.setBoxNo(rs.getString("box_id"));

				String toHospId = rs.getString("to_hosp_id");
				String getTime = rs.getString("getTime");

				/**
				 * 判断是否大于24小时
				 */
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				long oldTime = sdf.parse(getTime).getTime();
				long newTime = new Date().getTime();

				long time24 = 1000 * 60 * 60 * 24;
				long time48 = 1000 * 60 * 60 * 48;
				long time5 = 1000 * 60 * 60 * 5;
				long time3 = 1000 * 60 * 60 * 3;
				long time1 = 1000 * 60 * 60 * 1;
				//System.out.println("new:"+new Date()+",getTime:"+getTime);
				System.out.println("time:"+(newTime - oldTime > time24)+",newTime"+newTime+",oldTime:"+oldTime+","+rs.getString("isStart"));
				if (newTime - oldTime > time24
						&& "1".equals(rs.getString("isStart"))) {
					
					updateTransferStatus(t.getOrganSeg(), t.getBoxNo());

					continue;

				}
				
				if (newTime - oldTime > time48
						&& "0".equals(rs.getString("isStart"))) {
					CONST.DEVICE_ID += t.getDeviceId() + "@";
					updateTransferStatus(t.getOrganSeg(), t.getBoxNo());

					continue;

				}
				/**
				 * 判断是否3小时都接受不到数据
				 */

//				sql = "SELECT recordAt FROM transferRecord WHERE transfer_id = ? ORDER BY recordAt DESC LIMIT 1";
//				conn = connDB.getConnection();
//				ps = conn.prepareStatement(sql);
//				ps.setString(1, t.getTransferid());
//				rsRecord = ps.executeQuery();
//
//				if (rsRecord.next()) {
//					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					String recordAt = rsRecord.getString("recordAt");
//					long recordTime = sdf.parse(recordAt).getTime();
//					if (newTime - recordTime > time3) {
//						// CONST.DEVICE_ID += t.getDeviceId() +",";
//						updateTransferStatus(t.getOrganSeg(), t.getBoxNo());
//					}
//
//				}

			}

			/**
			 * 树兰的在同一位置持续了2小时,会停止和关闭转运 
			 */
//			sql = "SELECT transferid,transferNumber,getTime,box_id,to_hosp_id,isStart,b.deviceId,b.model boxNo FROM transfer t,box b WHERE t.box_id = b.boxId AND t.`status` = 'transfering' and t.to_hosp_id=69 ";
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//
//			String endLatitude = "";
//			String endLongitude = "";
//
//			while (rs.next()) {
//				sql = "SELECT * FROM (SELECT recordAt,latitude,longitude FROM transferRecord WHERE transfer_id = ? ORDER BY recordAt DESC LIMIT 1) b";
//				ps = conn.prepareStatement(sql);
//				ps.setString(1, rs.getString("transferid"));
//				rsRecord = ps.executeQuery();
//				// 最后一条的经纬度
//				while (rsRecord.next()) {
//					// endTime = rsRecord.getString("recordAt");
//					endLatitude = rsRecord.getString("latitude");
//					endLongitude = rsRecord.getString("longitude");
//				}
//
//				int lastId = 0;
//				String lastLatitude;
//				String lastLongitude;
//
//				sql = "SELECT id,latitude,longitude,recordAt FROM transferRecord WHERE transfer_id = ? AND latitude <> ? ORDER BY recordAt DESC LIMIT 1";
//				ps = conn.prepareStatement(sql);
//				ps.setString(1, rs.getString("transferid"));
//				ps.setString(2, endLatitude);
//				rsRecord = ps.executeQuery();
//				if (rsRecord.next() && !"".equals(endLatitude)) {
//					lastId = rsRecord.getInt("id");
//					lastLatitude = rsRecord.getString("latitude");
//					lastLongitude = rsRecord.getString("longitude");
//
//					// 查询是否只有最后一个点,如果条数大于120*2=240就删除
//					sql = "  SELECT count(*) c FROM transferRecord WHERE transfer_id = ? AND id > ?";
//					ps = conn.prepareStatement(sql);
//					ps.setString(1, rs.getString("transferid"));
//					ps.setInt(2, lastId);
//					System.out.println("树兰检测2小时停止的转运(>6000停止):" + lastId
//							+ ",transferId:" + rs.getString("transferid"));
//					rsRecord = ps.executeQuery();
//					int lastCount = 0;
//					if (rsRecord.next() && lastId != 0) {
//						lastCount = rsRecord.getInt("c");
//					}
//					System.out.println("树兰检测2小时停止的转运(>600停止):" + lastCount
//							+ ",transferId:" + rs.getString("transferid")
//							+ ",lastId:" + lastId);
//					if (lastCount > 240) {
//						CONST.DEVICE_ID += rs.getString("transferid") + ",";
//
//						updateTransferStatus(rs.getString("transferNumber"), rs.getString("boxNo"));
//					}
//				}
//				// 都是同一个经纬度
//				else if (!"".equals(endLatitude)) {
//					sql = "SELECT count(*) c FROM transferRecord WHERE transfer_id = ? AND latitude = ?";
//					ps = conn.prepareStatement(sql);
//					ps.setString(1, rs.getString("transferid"));
//					ps.setString(2, endLatitude);
//					rsRecord = ps.executeQuery();
//					int lastCount = 0;
//					if (rsRecord.next()) {
//						lastCount = rsRecord.getInt("c");
//					}
//					System.out.println("树兰检测2小时停止的转运(>600停止):" + lastCount
//							+ ",transferId:" + rs.getString("transferid")
//							+ ",latitude:" + endLatitude);
//					if (lastCount > 240) {
//						CONST.DEVICE_ID += rs.getString("transferid") + ",";
//
//						updateTransferStatus(rs.getString("transferNumber"), rs
//								.getString("boxNo"));
//					}
//				}
//			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsRecord, ps, conn);
		 
		}
		return lists;
	}

	/**
	 *快速操作
	 * 
	 */
	public List<TransferJson> getTransferSpeed() {
		List<TransferJson> lists = new ArrayList<TransferJson>();
		ResultSet rs = null;
		ResultSet rsRecord = null;

		PreparedStatement ps = null;

		/**
		 * 移除已经停止转运的关机命令
		 */

		long time1 = 1000 * 60 * 60 * 24 * 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String recordLimit = sdf.format(new Date(new Date().getTime() - time1));
		// System.out.println("time:"+recordLimit);
		String sql = "SELECT b.deviceId FROM transfer t,box b WHERE t.box_id = b.boxId AND t.`status` = 'done' AND getTime >= ?";

		try {
			conn = connDB.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, recordLimit);
			rs = ps.executeQuery();
			while (rs.next()) {

				TransferJson t = new TransferJson();
				t.setDeviceId(rs.getString("deviceId"));

				/**
				 * 移除已经转运完成的关机项
				 */
				if (CONST.DEVICE_ID.contains(t.getDeviceId())&&!CONST.DEVICE_ID.contains("35039810753280")) {
					String deviceIds[] = CONST.DEVICE_ID.split("@");
					String deviceIdTemp = "";
					for (int i = 0; i < deviceIds.length; i++) {
						if (t.getDeviceId().equals(deviceIds[i])) {

						} else {
							if (!"".equals(deviceIds[i])) {
								deviceIdTemp += deviceIds[i] + "@";
							}
						}
					}
					CONST.DEVICE_ID = deviceIdTemp;
				}

			}

			
			/**
			 * 在没有转运的情况下,一个小时关机
			 */

//			recordLimit = sdf.format(new Date(new Date().getTime() - time1));
//			sql = "SELECT remark FROM transferRecord WHERE transfer_id =1 and recordAt >= ?  GROUP BY remark";
//			conn = connDB.getConnection();
//			ps = conn.prepareStatement(sql);
//			ps.setString(1, recordLimit);
//			rsRecord = ps.executeQuery();
//			List<String> remarks = new ArrayList<String>();
//			while (rsRecord.next()) {
//				remarks.add(rsRecord.getString("remark"));
//			}
//			for (String remark : remarks) {
//				long hour1 = 1000 * 60 * 60 * 1;
//				String recordHour = sdf.format(new Date(new Date().getTime()
//						- hour1));
//				sql = "SELECT count(id) count FROM transferRecord WHERE transfer_id = 1 AND recordAt > ? AND remark = ? ORDER BY id DESC";
//				conn = connDB.getConnection();
//				ps = conn.prepareStatement(sql);
//				ps.setString(1, recordHour);
//				ps.setString(2, remark);
//				rsRecord = ps.executeQuery();
//				// 3600/30=120个
//				if (rsRecord.next()) {
//					int recordCount = rsRecord.getInt("count");
//					if (recordCount > 120) {
//						// 关机操作
//						CONST.DEVICE_ID += remark;
//					} else {
//						// 移除该关机的deviceId
//						if (CONST.DEVICE_ID.contains(remark)) {
//							String deviceIds[] = CONST.DEVICE_ID.split(",");
//							String deviceIdTemp = "";
//							for (int i = 0; i < deviceIds.length; i++) {
//								if (remark.equals(deviceIds[i])) {
//
//								} else {
//									if (!"".equals(deviceIds[i])) {
//										deviceIdTemp += deviceIds[i] + ",";
//									}
//								}
//							}
//							CONST.DEVICE_ID = deviceIdTemp;
//						}
//					}
//				}
//			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsRecord, ps, conn);
		}
		return lists;
	}

	/**
	 * 1.处理今天的已转运数据 2.开始的经纬度去重,结束的经纬度去重,全部为同一经纬度的不去重 删除的数据保存在transferRecordDel
	 * 3.同一天的箱子保持一条转运
	 * 
	 */
	public List<TransferJson> getTransferFinish() {
		List<TransferJson> lists = new ArrayList<TransferJson>();
		ResultSet rs = null;
		ResultSet rsRecord = null;

		PreparedStatement ps = null;

		long time3 = 1000 * 60 * 60 * 24 * 8;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String recordLimit = sdf.format(new Date(new Date().getTime() - time3));
		// System.out.println("time:"+recordLimit);
		String sql = "SELECT transferid,transferNumber,getTime,box_id,to_hosp_id,isStart,b.deviceId,trueName,filterStatus FROM transfer t,box b WHERE t.box_id = b.boxId AND t.`status` = 'done' AND getTime >= ?";

		try {
			conn = connDB.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, recordLimit);
			rs = ps.executeQuery();
			while (rs.next()) {

				TransferJson t = new TransferJson();
				t.setTransferid(rs.getString("transferid"));
				t.setOrganSeg(rs.getString("transferNumber"));
				t.setGetTime(rs.getString("getTime"));
				t.setBoxNo(rs.getString("box_id"));
				t.setDeviceId(rs.getString("deviceId"));
				t.setTrueName(rs.getString("trueName"));
				t.setFilterStatus(rs.getInt("filterStatus"));

				// /**
				// * 移除已经转运完成的关机项
				// */
				// if (CONST.DEVICE_ID.contains(t.getDeviceId())) {
				// String deviceIds[] = CONST.DEVICE_ID.split(",");
				// String deviceIdTemp = "";
				// for (int i = 0; i < deviceIds.length; i++) {
				// if (t.getDeviceId().equals(deviceIds[i])) {
				//
				// } else {
				// if (!"".equals(deviceIds[i])) {
				// deviceIdTemp += deviceIds[i] + ",";
				// }
				// }
				// }
				// CONST.DEVICE_ID = deviceIdTemp;
				// }

				// 查出第一个经纬度和最后一个经纬度
				String startTime = "";
				String startLatitude = "";
				String startLongitude = "";
				String endTime = "";
				String endLatitude = "";
				String endLongitude = "";
				int startCount = 0;
				int endCount = 0;
				sql = "SELECT * FROM (SELECT recordAt,latitude,longitude FROM transferRecord WHERE transfer_id = ? LIMIT 1) a UNION SELECT * FROM (SELECT recordAt,latitude,longitude FROM transferRecord WHERE transfer_id = ? ORDER BY recordAt DESC LIMIT 1) b";
				ps = conn.prepareStatement(sql);
				ps.setString(1, t.getTransferid());
				ps.setString(2, t.getTransferid());
				rsRecord = ps.executeQuery();
				while (rsRecord.next()) {
					if ("".equals(startTime)) {
						startTime = rsRecord.getString("recordAt");
						startLatitude = rsRecord.getString("latitude");
						startLongitude = rsRecord.getString("longitude");

					} else {
						endTime = rsRecord.getString("recordAt");
						endLatitude = rsRecord.getString("latitude");
						endLongitude = rsRecord.getString("longitude");
					}
				}
				// 只有一条数据的情况下
				if ("".equals(endTime)) {
					endTime = startTime;
					endLatitude = startLatitude;
					endLongitude = startLongitude;
				}
				 

				// 判断是不是全程经纬度只有一个,归纳为测试温度 判断第一个和最后一个点 与所有点是不是都一样
				sql = "SELECT COUNT(latitude) c FROM transferRecord WHERE transfer_id = ? AND latitude=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, t.getTransferid());
				ps.setString(2, startLatitude);

				rsRecord = ps.executeQuery();

				if (rsRecord.next()) {
					startCount = rsRecord.getInt("c");
				}
				
				sql = "SELECT COUNT(latitude) c FROM transferRecord WHERE transfer_id = ? AND latitude=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, t.getTransferid());
				ps.setString(2, endLatitude);

				rsRecord = ps.executeQuery();

				if (rsRecord.next()) {
					endCount = rsRecord.getInt("c");
				}
				// 判断是不是只有一个经纬度,如果不是说明有移动
				sql = "SELECT COUNT(id) FROM transferRecord WHERE transfer_id = ? GROUP BY latitude";
				ps = conn.prepareStatement(sql);
				ps.setString(1, t.getTransferid());
				rsRecord = ps.executeQuery();
				int temp = -1;
				while (rsRecord.next()) {
					temp++;
					if (temp == 1) {
						break;
					}
				}
				// System.out.println("yes3:"+t.getTransferid()+",count:"+startCount+","+endCount);
				if (temp < 1 && startCount == endCount
						&& t.getFilterStatus() == 0) {
					// 判断是否有转运人,没有则设置转运为不可以用转运
					// System.out.println("yes1:"+t.getTransferid()+","+t.getTrueName());
					if (t.getTrueName() == null || "".equals(t.getTrueName())) {
						// System.out.println("yes2:"+t.getTransferid());
						sql = "UPDATE transfer SET filterStatus = 3 WHERE transferid = ?";
						ps = conn.prepareStatement(sql);
						ps.setString(1, t.getTransferid());
						ps.executeUpdate();
					}
					continue;
				}

				/**
				 * 去除第一个有效点
				 */

				String firstLatitude = "";
				String firstLongitude = "";
				int firstId = 0;
				String twoLatitude = "";
				String twotLongitude = "";
				int twoId = 0;
				double firstDistance = 0;

				sql = "SELECT id,latitude,longitude,recordAt FROM transferRecord WHERE transfer_id = ? AND latitude <> ? LIMIT 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, t.getTransferid());
				ps.setString(2, startLatitude);
				rsRecord = ps.executeQuery();
				if (rsRecord.next()) {
					twoId = rsRecord.getInt("id");
					twoLatitude = rsRecord.getString("latitude");
					twotLongitude = rsRecord.getString("longitude");

					firstDistance = LocationUtils.getDistance(twoLatitude,
							twotLongitude, startLatitude, startLongitude);
				} else {
					// 如果全程经纬度都是一样,不处理,处理了无法进行实验测试.
					continue;
				}

				// 第一个点离第二个有效点大于0公里,完全移除第一个点
//				if (firstDistance > 0) {
//					sql = "INSERT INTO transferRecordDel SELECT * FROM transferRecord WHERE transfer_id = ? AND id < ? AND latitude=? ";
//					ps = conn.prepareStatement(sql);
//					ps.setString(1, t.getTransferid());
//					ps.setInt(2, twoId);
//					ps.setString(3, startLatitude);
//					int insertResult = ps.executeUpdate();
//					if (insertResult > 0) {
//						sql = "DELETE FROM transferRecord WHERE transfer_id = ? AND id < ? AND latitude=? ";
//						ps = conn.prepareStatement(sql);
//						ps.setString(1, t.getTransferid());
//						ps.setInt(2, twoId);
//						ps.setString(3, startLatitude);
//						int delResult = ps.executeUpdate();
//						System.out
//								.println("insertResult:" + insertResult + ",");
//					}
//				} else {
//					// 保留第一个有效点的最后一条
//					sql = "SELECT id,latitude,longitude,recordAt FROM transferRecord WHERE transfer_id = ? AND id<? ORDER BY id DESC LIMIT 1";
//					ps = conn.prepareStatement(sql);
//					ps.setString(1, t.getTransferid());
//					ps.setInt(2, twoId);
//					rsRecord = ps.executeQuery();
//					if (rsRecord.next()) {
//						firstId = rsRecord.getInt("id");
//						firstLatitude = rsRecord.getString("latitude");
//						firstLongitude = rsRecord.getString("longitude");
//					}
//					if (firstId != 0) {
//						sql = "INSERT INTO transferRecordDel SELECT * FROM transferRecord WHERE transfer_id = ? AND id < ? AND latitude=?";
//						ps = conn.prepareStatement(sql);
//						ps.setString(1, t.getTransferid());
//						ps.setInt(2, firstId);
//						ps.setString(3, firstLatitude);
//						int insertResult = ps.executeUpdate();
//						if (insertResult > 0) {
//							sql = "DELETE FROM transferRecord WHERE transfer_id = ? AND id < ? AND latitude=?";
//							ps = conn.prepareStatement(sql);
//							ps.setString(1, t.getTransferid());
//							ps.setInt(2, twoId);
//							ps.setString(3, firstLatitude);
//							int delResult = ps.executeUpdate();
//							//System.out.println("delResult:" + delResult + ",");
//						}
//					}
//				}

				/**
				 * 去除最后一个有效点
				 */
				String lastLatitude = "";
				String lastLongitude = "";
				int lastId = 0;
				sql = "SELECT id,latitude,longitude,recordAt FROM transferRecord WHERE transfer_id = ? AND latitude <> ? ORDER BY recordAt DESC LIMIT 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, t.getTransferid());
				ps.setString(2, endLatitude);
				rsRecord = ps.executeQuery();
				if (rsRecord.next()) {
					lastId = rsRecord.getInt("id");
					lastLatitude = rsRecord.getString("latitude");
					lastLongitude = rsRecord.getString("longitude");

					// 查询是否只有最后一个点,如果条数大于2就删除
					sql = "  SELECT count(*) c FROM transferRecord WHERE transfer_id = ? AND id > ?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, t.getTransferid());
					ps.setInt(2, lastId);
					rsRecord = ps.executeQuery();
					int lastCount = 0;
					if (rsRecord.next()) {
						lastCount = rsRecord.getInt("c");
					}

					if (lastCount > 2) {
						sql = "INSERT INTO transferRecordDel SELECT * FROM transferRecord WHERE transfer_id = ? AND id > ?";
						ps = conn.prepareStatement(sql);
						ps.setString(1, t.getTransferid());
						ps.setInt(2, lastId);
						int insertResult = 1;
						//int insertResult = ps.executeUpdate();
						if (insertResult > 0) {
							sql = "DELETE FROM transferRecord WHERE transfer_id = ? AND id > ?";
							ps = conn.prepareStatement(sql);
							ps.setString(1, t.getTransferid());
							ps.setInt(2, lastId);
							int delResult = 1;
							//int delResult = ps.executeUpdate();
							System.out.println("delResult:" + delResult + "," + lastId + ",transfer_id:" + t.getTransferid());
						}
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsRecord, ps, conn);
		}
		return lists;
	}
	
	

	/**
	 * 清除箱子的状态
	 * 
	 * @param deviceId
	 * @return
	 */
	public void clearDeviceStatus() {

		ResultSet rs = null;
		ResultSet rsTwo = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "SELECT boxid FROM box b where b.transferStatus='transfering'";
		// System.out.println(sql);
		try {

			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();
			while (rs.next()) {

				String boxid = rs.getString("boxid");
				sql = "SELECT t.id,t.transferNumber,t.`status`,t.box_id FROM transfer t WHERE t.box_id =?  AND t.`status`='transfering'";
				ps = conn.prepareStatement(sql);
				ps.setString(1, boxid);
				rsTwo = ps.executeQuery();

				if (rsTwo.next()) {

				} else {
					sql = "UPDATE box SET transferStatus='free' WHERE boxid=?";
					ps = conn.prepareStatement(sql);
					ps.setString(1, boxid);
					ps.executeUpdate();
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);
		}

	}

	/**
	 * 获取设备状态
	 * 
	 * @param deviceId
	 * @return
	 */
	public String getModifyOrganSeg(String boxNo) {
		TransferJson t = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String modifyOrganSeg = "";

		String sql = "SELECT count(t.id) c FROM transfer t,box b WHERE t.box_id = b.boxid AND b.model = ?";
		// System.out.println(sql);
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, boxNo);
			rs = ps.executeQuery();
			if (rs.next()) {
				int c = rs.getInt("c");
				if (c < 1000) {
					if (c < 10) {
						modifyOrganSeg = boxNo + "00" + c;
					} else if (c < 100) {
						modifyOrganSeg = boxNo + "0" + c;
					} else if (c < 1000) {
						modifyOrganSeg = boxNo + c;
					}
				} else {
					c = c % 1000;
					if (c < 10) {
						modifyOrganSeg = boxNo + "00" + c;
					} else if (c < 100) {
						modifyOrganSeg = boxNo + "0" + c;
					} else if (c < 1000) {
						modifyOrganSeg = boxNo + c;
					}
				}
			} else {
				modifyOrganSeg = boxNo + "000";
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return modifyOrganSeg;

	}

	/**
	 * 获取设备状态
	 * 
	 * @param deviceId
	 * @return
	 */
	public boolean getDeviceStatus(String organSeg) {
		TransferJson t = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "SELECT recordAt FROM transferRecord tr WHERE tr.remark  = (SELECT box_id FROM transfer t  where transferNumber =?) ORDER BY recordAt DESC LIMIT 1";
		// System.out.println(sql);
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			if (rs.next()) {
				String recordAt = rs.getString("recordAt");
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				try {
					long time = (new Date().getTime() - sdf.parse(recordAt)
							.getTime()) / 1000;
					if (time < 60 * 60) {
						return true;
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return true;
				}

			}
			// modify
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;

	}
	
	public String getDeviceByOrganSeg(String organSeg) {
		 
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String result = "";

		String sql = "SELECT b.deviceId FROM transfer t,box b WHERE t.box_id=b.boxid AND t.transferNumber=?";
		// System.out.println(sql);
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			if (rs.next()) {
               result = rs.getString("deviceId");
			}
		 
			 

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return result;

	}


	public List<TransferJson> getTransfersByPhone(String phone, int page,
			int pageSize) {
		List<TransferJson> lists = new ArrayList<TransferJson>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		List<String> shutDownList = new ArrayList<String>();
		// PreparedStatement psTwo = null;
		ResultSet rsExample = null;
		ResultSet rsTwo = null;
		// 是否是示例
		boolean isExample = false;
		conn = connDB.getConnection();
		String sql = "select t.isGroup,t.status,transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,DATE_FORMAT(getTime,'%Y-%m-%d %H:%i') getTime,modifyOrganSeg,autoTransfer,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,b.model boxNo,t.opoContactName,t.opoContactPhone from transfer t,box b where t.box_id=b.boxId AND t.filterStatus=0 and t.transferNumber in (select organSeg from transfer_group_temp where  usersIds like '%"
				+ phone + "%') order by t.`status`, t.createAt desc  limit ?,?";
		try {

			ps = conn.prepareStatement(sql);
			// ps.setString(1, phone);
			ps.setInt(1, page * pageSize);
			ps.setInt(2, pageSize);
			rs = ps.executeQuery();
			while (rs.next()) {
				TransferJson t = new TransferJson();
				String transferid = rs.getString("transferid");
				t.setTransferid(rs.getString("transferid") == null ? "" : rs
						.getString("transferid"));
				t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
						.getString("organSeg"));
				t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
						.getString("openPsd"));

				t.setToHospName(rs.getString("toHospName") == null ? "" : rs
						.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType") == null ? ""
						: rs.getString("tracfficType"));
				t.setToHospName(rs.getString("toHospName") == null ? "" : rs
						.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType") == null ? ""
						: rs.getString("tracfficType"));
				t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
						: rs.getString("tracfficNumber"));
				t.setOrgan(rs.getString("organ") == null ? "" : rs
						.getString("organ"));
				t.setOrganNum(rs.getString("organNum") == null ? "" : rs
						.getString("organNum"));
				t.setBlood(rs.getString("blood") == null ? "" : rs
						.getString("blood"));
				t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
						.getString("bloodNum"));
				t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
						.getString("sampleOrgan"));
				t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
						: rs.getString("sampleOrganNum"));
				t.setOpoName(rs.getString("opoName") == null ? "" : rs
						.getString("opoName"));
				t.setContactName(rs.getString("contactName") == null ? "" : rs
						.getString("contactName"));
				t.setContactPhone(rs.getString("contactPhone") == null ? ""
						: rs.getString("contactPhone"));
				t.setPhone(rs.getString("phone") == null ? "" : rs
						.getString("phone"));
				t.setTrueName(rs.getString("trueName") == null ? "" : rs
						.getString("trueName"));
				t.setGetTime(rs.getString("getTime") == null ? "" : rs
						.getString("getTime"));
				t.setIsStart(rs.getString("isStart") == null ? "" : rs
						.getString("isStart"));
				t.setDistance(rs.getString("distance") == null ? "" : rs
						.getString("distance"));
				t.setStartLong(rs.getString("startLong") == null ? "" : rs
						.getString("startLong"));
				t.setStartLati(rs.getString("startLati") == null ? "" : rs
						.getString("startLati"));
				t.setEndLong(rs.getString("endLong") == null ? "" : rs
						.getString("endLong"));
				t.setEndLati(rs.getString("endLati") == null ? "" : rs
						.getString("endLati"));

				String fromCityStr = rs.getString("fromCity");

				if (fromCityStr.contains("/")) {
					String temp[] = fromCityStr.split("/");
					if (temp.length >= 3) {
						fromCityStr = temp[1] + temp[2];
					}
				}

				t.setFromCity(fromCityStr);

				t.setToHosp(rs.getString("toHosp") == null ? "" : rs
						.getString("toHosp"));
				t.setBoxNo(rs.getString("boxNo") == null ? "" : rs
						.getString("boxNo"));
				t.setStatus(rs.getString("status") == null ? "" : rs
						.getString("status"));
				t.setIsGroup(rs.getString("isGroup") == null ? "" : rs
						.getString("isGroup"));
				t.setAutoTransfer(rs.getInt("autoTransfer"));
				t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
						: rs.getString("modifyOrganSeg"));
				String distance = rs.getString("distance") == null ? "" : rs
						.getString("distance");
				t.setOpoContactName(rs.getString("opoContactName") == null ? ""
						: rs.getString("opoContactName"));
				t
						.setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
								: rs.getString("opoContactPhone"));

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String getTime = rs.getString("getTime");

				// 判断是否大于24小时
				long oldTime = 0;
				try {
					oldTime = sdf.parse(getTime).getTime();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				long newTime = new Date().getTime();

				long time24 = 1000 * 60 * 60 * 24;
				if (newTime - oldTime > time24 && !"done".equals(t.getStatus())
						&& "1".equals(rs.getString("isStart"))) {
					shutDownList.add(t.getOrganSeg() + ":" + t.getBoxNo());
					t.setStatus("done");
				}

				sql = "select distance,recordAt from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' and tr.distance >0 order by tr.recordAt desc limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, transferid);
				rsTwo = ps.executeQuery();
				if (rsTwo.next()) {
					t.setPause(1);
					// 修改转运
					String nowDistance = rsTwo.getString("distance");
					double dis = Double.parseDouble(distance)
							- Double.parseDouble(nowDistance);
					if (dis < 0) {
						dis = 0.0;
					}
					t.setNowDistance(dis + "");

					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					try {
						long time = Math.abs(sdf.parse(
								rsTwo.getString("recordAt")).getTime()
								- sdf.parse(t.getGetTime()).getTime());
						String secode = "";
						String minus = "";
						if (time / 1000 > 3600) {
							long t1 = time / 1000 % 3600;
							t1 = t1 / 60;
							if (t1 < 10) {
								secode = "0" + t1;
							} else {
								secode = t1 + "";
							}
						} else {
							long t2 = time / 1000 / 60;
							if (t2 < 10) {
								secode = "0" + t2;
							} else {
								secode = t2 + "";
							}
						}
						if ((time / 1000 / 60 / 60) < 10) {
							minus = "0" + (time / 1000 / 60 / 60);
						} else {
							minus = "" + (time / 1000 / 60 / 60);
						}
						t.setTime(minus + ":" + secode);
					} catch (Exception e) {
						t.setTime("00:00");
					}
					t.setDeviceStatus(1);
				} else {
					t.setNowDistance("0.0");
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					// System.out.println(t.getGetTime());
					try {
						long time = Math.abs(sdf.parse(sdf.format(new Date()))
								.getTime()
								- sdf.parse(t.getGetTime()).getTime());
						String secode = "";
						String minus = "";
						if (time / 1000 > 3600) {
							long t1 = time / 1000 % 3600;
							t1 = t1 / 60;
							if (t1 < 10) {
								secode = "0" + t1;
							} else {
								secode = t1 + "";
							}
						} else {
							long t2 = time / 1000 / 60;
							if (t2 < 10) {
								secode = "0" + t2;
							} else {
								secode = t2 + "";
							}
						}
						if ((time / 1000 / 60 / 60) < 10) {
							minus = "0" + (time / 1000 / 60 / 60);
						} else {
							minus = "" + (time / 1000 / 60 / 60);
						}
						t.setTime(minus + ":" + secode);
						t.setDeviceStatus(0);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						t.setTime("00:00");
					}

				}

				sql = "SELECT DATE_FORMAT(create_time,'%Y-%m-%d %H:%i') create_time,type,content FROM push p where p.transfer_id = ? ORDER BY p.create_time DESC limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, transferid);
				rsTwo = ps.executeQuery();
				if (rsTwo.next()) {
					String time = rsTwo.getString("create_time");
					String type = rsTwo.getString("type");
					String content = rsTwo.getString("content");
					String exce = "";

					// content = "【单号:3652】发生第2次开箱,请及时处理";
					// type = "open";

					if ("collision".equals(type)) {
						exce = time
								+ " "
								+ content.substring(content.indexOf("发生") + 2,
										content.indexOf("发生") + 7);
					} else if ("temperature".equals(type)) {
						exce = time + " 温度异常";
					} else if ("open".equals(type)) {

						exce = time
								+ " "
								+ content.substring(content.indexOf("发生") + 2,
										content.indexOf("发生") + 7);
					}
					t.setPushException(exce);
				}
				if (!"LP20180103131749".equals(t.getOrganSeg())) {
					lists.add(t);
				}

			}
			// 示例转运
			if (!isExample && page == 0) {
				isExample = true;

				sql = "select t.isGroup,t.status,transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,modifyOrganSeg,autoTransfer,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,b.model boxNo,t.opoContactName,t.opoContactPhone from transfer t,box b where t.box_id=b.boxId and t.transferNumber ='LP20180103131749' ";

				ps = conn.prepareStatement(sql);

				rsExample = ps.executeQuery();
				while (rsExample.next()) {
					TransferJson t = new TransferJson();
					// transferid = rsExample.getString("transferid");
					t.setTransferid(rsExample.getString("transferid"));
					t.setOrganSeg(rsExample.getString("organSeg"));
					t.setOpenPsd(rsExample.getString("openPsd"));
					t.setFromCity(rsExample.getString("fromCity"));
					t.setToHospName(rsExample.getString("toHospName"));
					t.setTracfficType(rsExample.getString("tracfficType"));
					t.setToHospName(rsExample.getString("toHospName"));
					t.setTracfficType(rsExample.getString("tracfficType"));
					t.setTracfficNumber(rsExample.getString("tracfficNumber"));
					t.setOrgan(rsExample.getString("organ"));
					t.setOrganNum(rsExample.getString("organNum"));
					t.setBlood(rsExample.getString("blood"));
					t.setBloodNum(rsExample.getString("bloodNum"));
					t.setSampleOrgan(rsExample.getString("sampleOrgan"));
					t.setSampleOrganNum(rsExample.getString("sampleOrganNum"));
					t.setOpoName(rsExample.getString("opoName"));
					t.setContactName(rsExample.getString("contactName"));
					t.setContactPhone(rsExample.getString("contactPhone"));
					t.setPhone(rsExample.getString("phone"));
					t.setTrueName(rsExample.getString("trueName"));
					t.setGetTime(rsExample.getString("getTime"));
					t.setIsStart(rsExample.getString("isStart"));
					t.setDistance(rsExample.getString("distance"));
					t.setStartLong(rsExample.getString("startLong"));
					t.setStartLati(rsExample.getString("startLati"));
					t.setEndLong(rsExample.getString("endLong"));
					t.setEndLati(rsExample.getString("endLati"));
					t.setToHosp(rsExample.getString("toHosp"));
					t.setBoxNo(rsExample.getString("boxNo"));
					t.setStatus(rsExample.getString("status"));
					t.setIsGroup(rsExample.getString("isGroup"));
					t.setAutoTransfer(rsExample.getInt("autoTransfer"));
					t.setModifyOrganSeg(rsExample.getString("modifyOrganSeg"));
					// distance = rsExample.getString("distance");
					t.setOpoContactName(rsExample.getString("opoContactName"));
					t
							.setOpoContactPhone(rsExample
									.getString("opoContactPhone"));

					// sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					// getTime = rsExample.getString("getTime");
					t.setNowDistance("19.01");
					t.setTime("01:15");
					lists.add(t);
				}

			}
			for (int i = 0; i < shutDownList.size(); i++) {
				// System.out.println(shutDownList.get(i).split(":")[0] + ":"\+
				// shutDownList.get(i).split(":")[1]);

				updateTransferStatus(shutDownList.get(i).split(":")[0],
						shutDownList.get(i).split(":")[1]);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return lists;
	}

	public List<TransferJson> getSearchTransfersHistory(String phone,
			String condition, int page, int pageSize) {
		List<TransferJson> lists = new ArrayList<TransferJson>();
		ResultSet rs = null;
		ResultSet rsTwo = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select transferid,transferNumber organSeg,boxPin openPsd,fromCity,modifyOrganSeg,autoTransfer,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,t.status,t.opoContactName,t.opoContactPhone from transfer t,transfer_group_temp tg where t.transferNumber = tg.organSeg AND t.filterStatus=0 and  tg.usersIds like '%"
				+ phone
				+ "%' and CONCAT(trueName,contactName,organ,fromCity,toHosp,blood,sampleOrgan,tracfficType,tracfficNumber,opoName,toHospName) like '%"
				+ condition
				+ "%' and t.`status`='done'  order by  t.getTime desc  limit ?,?";
		// System.out.println("sql:" + sql);
		try {

			ps = conn.prepareStatement(sql);
			// ps.setString(1, phone);
			ps.setInt(1, page * pageSize);
			ps.setInt(2, pageSize);
			rs = ps.executeQuery();
			while (rs.next()) {
				TransferJson t = new TransferJson();
				String transferid = rs.getString("transferid");
				t.setTransferid(rs.getString("transferid") == null ? "" : rs
						.getString("transferid"));
				t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
						.getString("organSeg"));
				t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
						.getString("openPsd"));
				t.setFromCity(rs.getString("fromCity") == null ? "" : rs
						.getString("fromCity"));
				t.setToHospName(rs.getString("toHospName") == null ? "" : rs
						.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType") == null ? ""
						: rs.getString("tracfficType"));
				t.setToHospName(rs.getString("toHospName") == null ? "" : rs
						.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType") == null ? ""
						: rs.getString("tracfficType"));
				t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
						: rs.getString("tracfficNumber"));
				t.setOrgan(rs.getString("organ") == null ? "" : rs
						.getString("organ"));
				t.setOrganNum(rs.getString("organNum") == null ? "" : rs
						.getString("organNum"));
				t.setBlood(rs.getString("blood") == null ? "" : rs
						.getString("blood"));
				t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
						.getString("bloodNum"));
				t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
						.getString("sampleOrgan"));
				t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
						: rs.getString("sampleOrganNum"));
				t.setOpoName(rs.getString("opoName") == null ? "" : rs
						.getString("opoName"));
				t.setContactName(rs.getString("contactName") == null ? "" : rs
						.getString("contactName"));
				t.setContactPhone(rs.getString("contactPhone") == null ? ""
						: rs.getString("contactPhone"));
				t.setPhone(rs.getString("phone") == null ? "" : rs
						.getString("phone"));
				t.setTrueName(rs.getString("trueName") == null ? "" : rs
						.getString("trueName"));
				t.setGetTime(rs.getString("getTime") == null ? "" : rs
						.getString("getTime"));
				t.setIsStart(rs.getString("isStart") == null ? "" : rs
						.getString("isStart"));
				t.setDistance(rs.getString("distance") == null ? "" : rs
						.getString("distance"));
				t.setStartLong(rs.getString("startLong") == null ? "" : rs
						.getString("startLong"));
				t.setStartLati(rs.getString("startLati") == null ? "" : rs
						.getString("startLati"));
				t.setEndLong(rs.getString("endLong") == null ? "" : rs
						.getString("endLong"));
				t.setEndLati(rs.getString("endLati") == null ? "" : rs
						.getString("endLati"));
				t.setToHosp(rs.getString("toHosp") == null ? "" : rs
						.getString("toHosp"));
				// t.setBoxNo(rs.getString("boxNo")==null?"":rs.getString("boxNo"));
				t.setStatus(rs.getString("status") == null ? "" : rs
						.getString("status"));
				// t.setIsGroup(rs.getString("isGroup")==null?"":rs.getString("isGroup"));
				t.setAutoTransfer(rs.getInt("autoTransfer"));
				t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
						: rs.getString("modifyOrganSeg"));
				String distance = rs.getString("distance") == null ? "" : rs
						.getString("distance");
				t.setOpoContactName(rs.getString("opoContactName") == null ? ""
						: rs.getString("opoContactName"));
				t
						.setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
								: rs.getString("opoContactPhone"));

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

				sql = "select distance,recordAt from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' and tr.distance >0 order by tr.recordAt desc limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, transferid);
				rsTwo = ps.executeQuery();
				if (rsTwo.next()) {
					// 修改转运
					String nowDistance = rsTwo.getString("distance");
					double dis = Double.parseDouble(distance)
							- Double.parseDouble(nowDistance);
					if (dis < 0) {
						dis = 0.0;
					}
					t.setNowDistance(dis + "");

					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					try {
						long time = Math.abs(sdf.parse(
								rsTwo.getString("recordAt")).getTime()
								- sdf.parse(t.getGetTime()).getTime());
						String secode = "";
						String minus = "";
						if (time / 1000 > 3600) {
							long t1 = time / 1000 % 3600;
							t1 = t1 / 60;
							if (t1 < 10) {
								secode = "0" + t1;
							} else {
								secode = t1 + "";
							}
						} else {
							long t2 = time / 1000 / 60;
							if (t2 < 10) {
								secode = "0" + t2;
							} else {
								secode = t2 + "";
							}
						}
						if ((time / 1000 / 60 / 60) < 10) {
							minus = "0" + (time / 1000 / 60 / 60);
						} else {
							minus = "" + (time / 1000 / 60 / 60);
						}
						t.setTime(minus + ":" + secode);
					} catch (Exception e) {
						t.setTime("00:00");
					}
					t.setDeviceStatus(1);
				} else {
					t.setNowDistance(distance);
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					// System.out.println(t.getGetTime());
					try {
						long time = Math.abs(sdf.parse(sdf.format(new Date()))
								.getTime()
								- sdf.parse(t.getGetTime()).getTime());
						String secode = "";
						String minus = "";
						if (time / 1000 > 3600) {
							long t1 = time / 1000 % 3600;
							t1 = t1 / 60;
							if (t1 < 10) {
								secode = "0" + t1;
							} else {
								secode = t1 + "";
							}
						} else {
							long t2 = time / 1000 / 60;
							if (t2 < 10) {
								secode = "0" + t2;
							} else {
								secode = t2 + "";
							}
						}
						if ((time / 1000 / 60 / 60) < 10) {
							minus = "0" + (time / 1000 / 60 / 60);
						} else {
							minus = "" + (time / 1000 / 60 / 60);
						}
						t.setTime(minus + ":" + secode);
						t.setDeviceStatus(0);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						t.setTime("00:00");
					}

				}

				lists.add(t);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);
		}
		return lists;

	}

	public List<TransferJson> getTransfersHistoryByPhone(String phone,
			String condition, int page, int pageSize) {
		List<TransferJson> lists = new ArrayList<TransferJson>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		ResultSet rsTwo = null;
		conn = connDB.getConnection();
		// b.model boxNo from transfer t,box b where t.box_id = b.boxid
		String sql = "select DISTINCT(transferid),transferNumber organSeg,boxPin openPsd,modifyOrganSeg,autoTransfer,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,b.model boxNo,t.status,t.opoContactName,t.opoContactPhone from box b, transfer t,users u,transfer_group_temp tg where t.box_id = b.boxid  AND t.filterStatus=0 and tg.usersIds like '%"
				+ phone
				+ "%' and tg.organSeg = t.transferNumber  and t.`status`='done' "
				+ condition + " order by  t.getTime desc limit ?,?";
		//System.out.println("sql:" + sql);
		try {

			ps = conn.prepareStatement(sql);
			// ps.setString(1, phone);
			ps.setInt(1, page * pageSize);
			ps.setInt(2, pageSize);
			rs = ps.executeQuery();
			while (rs.next()) {

				TransferJson t = new TransferJson();
				String transferid = rs.getString("transferid");
				t.setTransferid(rs.getString("transferid") == null ? "" : rs
						.getString("transferid"));
				t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
						.getString("organSeg"));
				t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
						.getString("openPsd"));
				t.setFromCity(rs.getString("fromCity") == null ? "" : rs
						.getString("fromCity"));
				t.setToHospName(rs.getString("toHospName") == null ? "" : rs
						.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType") == null ? ""
						: rs.getString("tracfficType"));
				t.setToHospName(rs.getString("toHospName") == null ? "" : rs
						.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType") == null ? ""
						: rs.getString("tracfficType"));
				t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
						: rs.getString("tracfficNumber"));
				t.setOrgan(rs.getString("organ") == null ? "" : rs
						.getString("organ"));
				t.setOrganNum(rs.getString("organNum") == null ? "" : rs
						.getString("organNum"));
				t.setBlood(rs.getString("blood") == null ? "" : rs
						.getString("blood"));
				t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
						.getString("bloodNum"));
				t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
						.getString("sampleOrgan"));
				t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
						: rs.getString("sampleOrganNum"));
				t.setOpoName(rs.getString("opoName") == null ? "" : rs
						.getString("opoName"));
				t.setContactName(rs.getString("contactName") == null ? "" : rs
						.getString("contactName"));
				t.setContactPhone(rs.getString("contactPhone") == null ? ""
						: rs.getString("contactPhone"));
				t.setPhone(rs.getString("phone") == null ? "" : rs
						.getString("phone"));
				t.setTrueName(rs.getString("trueName") == null ? "" : rs
						.getString("trueName"));
				t.setGetTime(rs.getString("getTime") == null ? "" : rs
						.getString("getTime"));
				t.setIsStart(rs.getString("isStart") == null ? "" : rs
						.getString("isStart"));
				t.setDistance(rs.getString("distance") == null ? "" : rs
						.getString("distance"));
				t.setStartLong(rs.getString("startLong") == null ? "" : rs
						.getString("startLong"));
				t.setStartLati(rs.getString("startLati") == null ? "" : rs
						.getString("startLati"));
				t.setEndLong(rs.getString("endLong") == null ? "" : rs
						.getString("endLong"));
				t.setEndLati(rs.getString("endLati") == null ? "" : rs
						.getString("endLati"));
				t.setToHosp(rs.getString("toHosp") == null ? "" : rs
						.getString("toHosp"));
				t.setBoxNo(rs.getString("boxNo") == null ? "" : rs
						.getString("boxNo"));
				t.setStatus(rs.getString("status") == null ? "" : rs
						.getString("status"));
				// t.setIsGroup(rs.getString("isGroup")==null?"":rs.getString("isGroup"));
				t.setAutoTransfer(rs.getInt("autoTransfer"));
				t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
						: rs.getString("modifyOrganSeg"));
				String distance = rs.getString("distance") == null ? "" : rs
						.getString("distance");
				t.setOpoContactName(rs.getString("opoContactName") == null ? ""
						: rs.getString("opoContactName"));
				t
						.setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
								: rs.getString("opoContactPhone"));

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

				sql = "select distance,recordAt from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' and tr.distance >0 order by tr.recordAt desc limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, transferid);
				rsTwo = ps.executeQuery();
				if (rsTwo.next()) {
					// 修改转运
					String nowDistance = rsTwo.getString("distance");
					double dis = Double.parseDouble(distance)
							- Double.parseDouble(nowDistance);
					if (dis < 0) {
						dis = 0.0;
					}
					t.setNowDistance(dis + "");

					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					try {
						long time = Math.abs(sdf.parse(
								rsTwo.getString("recordAt")).getTime()
								- sdf.parse(t.getGetTime()).getTime());
						String secode = "";
						String minus = "";
						if (time / 1000 > 3600) {
							long t1 = time / 1000 % 3600;
							t1 = t1 / 60;
							if (t1 < 10) {
								secode = "0" + t1;
							} else {
								secode = t1 + "";
							}
						} else {
							long t2 = time / 1000 / 60;
							if (t2 < 10) {
								secode = "0" + t2;
							} else {
								secode = t2 + "";
							}
						}
						if ((time / 1000 / 60 / 60) < 10) {
							minus = "0" + (time / 1000 / 60 / 60);
						} else {
							minus = "" + (time / 1000 / 60 / 60);
						}
						t.setTime(minus + ":" + secode);
					} catch (Exception e) {
						t.setTime("00:00");
					}
					t.setDeviceStatus(1);
				} else {
					t.setNowDistance(distance);
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					// System.out.println(t.getGetTime());
					try {
						long time = Math.abs(sdf.parse(sdf.format(new Date()))
								.getTime()
								- sdf.parse(t.getGetTime()).getTime());
						String secode = "";
						String minus = "";
						if (time / 1000 > 3600) {
							long t1 = time / 1000 % 3600;
							t1 = t1 / 60;
							if (t1 < 10) {
								secode = "0" + t1;
							} else {
								secode = t1 + "";
							}
						} else {
							long t2 = time / 1000 / 60;
							if (t2 < 10) {
								secode = "0" + t2;
							} else {
								secode = t2 + "";
							}
						}
						if ((time / 1000 / 60 / 60) < 10) {
							minus = "0" + (time / 1000 / 60 / 60);
						} else {
							minus = "" + (time / 1000 / 60 / 60);
						}
						t.setTime(minus + ":" + secode);
						t.setDeviceStatus(0);

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						t.setTime("00:00");
					}

				}

				lists.add(t);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);
		}
		return lists;

	}

	/**
	 * 
	 * @param phone
	 * @param condition
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List<TransferJson> getTransfersHistoryByorganSeg(String deviceId,
			String organSeg, int page, int pageSize) {
		List<TransferJson> lists = new ArrayList<TransferJson>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		ResultSet rsTwo = null;
		conn = connDB.getConnection();
		// b.model boxNo from transfer t,box b where t.box_id = b.boxid
		// ransferNumber='20170905'
		String sql = "select transferid,transferNumber organSeg,boxPin openPsd,modifyOrganSeg,autoTransfer,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp  from transfer t,box b where t.box_id=b.boxid and b.deviceId = ? AND t.filterStatus=0  and t.`status`='done' "
				+ (organSeg == null ? "" : "and t.transferNumber='" + organSeg
						+ "' ") + " order by getTime desc limit ?,?";
		//System.out.println("sql:" + sql);
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, deviceId);
			ps.setInt(2, page * pageSize);
			ps.setInt(3, pageSize);
			rs = ps.executeQuery();
			while (rs.next()) {
				TransferJson t = new TransferJson();
				t.setTransferid(rs.getString("transferid"));
				t.setOrganSeg(rs.getString("organSeg"));
				t.setOpenPsd(rs.getString("openPsd"));
				t.setFromCity(rs.getString("fromCity"));
				t.setToHospName(rs.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType"));
				t.setToHospName(rs.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType"));
				t.setTracfficNumber(rs.getString("tracfficNumber"));
				t.setOrgan(rs.getString("organ"));
				t.setOrganNum(rs.getString("organNum"));
				t.setBlood(rs.getString("blood"));
				t.setBloodNum(rs.getString("bloodNum"));
				t.setSampleOrgan(rs.getString("sampleOrgan"));
				t.setSampleOrganNum(rs.getString("sampleOrganNum"));
				t.setOpoName(rs.getString("opoName"));
				t.setContactName(rs.getString("contactName"));
				t.setContactPhone(rs.getString("contactPhone"));
				t.setPhone(rs.getString("phone"));
				t.setTrueName(rs.getString("trueName"));
				t.setGetTime(rs.getString("getTime"));
				t.setIsStart(rs.getString("isStart"));
				t.setDistance(rs.getString("distance"));
				t.setStartLong(rs.getString("startLong"));
				t.setStartLati(rs.getString("startLati"));
				t.setEndLong(rs.getString("endLong"));
				t.setEndLati(rs.getString("endLati"));
				t.setToHosp(rs.getString("toHosp"));
				t.setAutoTransfer(rs.getInt("autoTransfer"));
				t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));

				String distance = rs.getString("distance");
				sql = "select distance from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' order by tr.recordAt desc limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, rs.getString("transferid"));
				rsTwo = ps.executeQuery();
				if (rsTwo.next()) {
					String nowDistance = rsTwo.getString("distance");
					double dis = Double.parseDouble(distance)
							- Double.parseDouble(nowDistance);
					if (dis < 0) {
						dis = 0.0;
					}
					t.setNowDistance(dis + "");

					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					try {
						long time = sdf.parse(rsTwo.getString("recordAt"))
								.getTime()
								- sdf.parse(t.getGetTime()).getTime();
						String secode = "";
						String minus = "";
						if (time / 1000 > 3600) {
							long t1 = time / 1000 % 3600;
							t1 = t1 / 60;
							if (t1 < 10) {
								secode = "0" + t1;
							} else {
								secode = t1 + "";
							}
						} else {
							long t2 = time / 1000 / 60;
							if (t2 < 10) {
								secode = "0" + t2;
							} else {
								secode = t2 + "";
							}
						}
						if ((time / 1000 / 60 / 60) < 10) {
							minus = "0" + (time / 1000 / 60 / 60);
						} else {
							minus = "" + (time / 1000 / 60 / 60);
						}
						t.setTime(minus + ":" + secode);
					} catch (Exception e) {
						t.setTime("00:00");
					}
				} else {
					t.setNowDistance("0");
					t.setTime("00:00");
				}

				lists.add(t);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);
		}
		return lists;

	}

	public TransferJson getTransferByOrganSeg(String organSeg) {
		TransferJson t = null;
		ResultSet rs = null;
		ResultSet rsTwo = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,modifyOrganSeg,autoTransfer,"
				+ "tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,opoContactName,opoContactPhone,t.phone,"
				+ "trueName,getTime,t.isStart,t.startLong,t.startLati,t.endLong,t.endLati,t.distance,t.toHosp,b.model boxNo,t.isGroup from transfer t,box b where t.box_id = b.boxid and transferNumber = ?";
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			while (rs.next()) {
				t = new TransferJson();
				t.setTransferid(rs.getString("transferid"));
				t.setOrganSeg(rs.getString("organSeg"));
				t.setOpenPsd(rs.getString("openPsd"));
				t.setFromCity(rs.getString("fromCity"));
				t.setToHospName(rs.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType"));
				t.setToHospName(rs.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType"));
				t.setTracfficNumber(rs.getString("tracfficNumber"));
				t.setOrgan(rs.getString("organ"));
				t.setOrganNum(rs.getString("organNum"));
				t.setBlood(rs.getString("blood"));
				t.setBloodNum(rs.getString("bloodNum"));
				t.setSampleOrgan(rs.getString("sampleOrgan"));
				t.setSampleOrganNum(rs.getString("sampleOrganNum"));
				t.setOpoName(rs.getString("opoName"));
				t.setContactName(rs.getString("contactName"));
				t.setContactPhone(rs.getString("contactPhone"));
				t.setPhone(rs.getString("phone"));
				t.setTrueName(rs.getString("trueName"));
				t.setGetTime(rs.getString("getTime"));
				t.setIsStart(rs.getString("isStart"));
				t.setStartLong(rs.getString("startLong"));
				t.setStartLati(rs.getString("startLati"));
				t.setEndLong(rs.getString("endLong"));
				t.setEndLati(rs.getString("endLati"));
				t.setDistance(rs.getString("distance"));
				t.setToHosp(rs.getString("toHosp"));
				t.setBoxNo(rs.getString("boxNo"));
				t.setIsGroup(rs.getString("isGroup"));
				t.setOpoContactName(rs.getString("opoContactName"));
				t.setOpoContactPhone(rs.getString("opoContactPhone"));
				t.setAutoTransfer(rs.getInt("autoTransfer"));
				t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));
				String distance = rs.getString("distance");
				sql = "select distance from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' order by tr.recordAt desc limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, rs.getString("transferid"));
				rsTwo = ps.executeQuery();
				if (rsTwo.next()) {
					String nowDistance = rsTwo.getString("distance");
					double dis = Double.parseDouble(distance)
							- Double.parseDouble(nowDistance);
					if (dis < 0) {
						dis = 0.0;
					}
					t.setNowDistance(dis + "");

					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					try {
						long time = sdf.parse(rsTwo.getString("recordAt"))
								.getTime()
								- sdf.parse(t.getGetTime()).getTime();
						String secode = "";
						String minus = "";
						if (time / 1000 > 3600) {
							long t1 = time / 1000 % 3600;
							t1 = t1 / 60;
							if (t1 < 10) {
								secode = "0" + t1;
							} else {
								secode = t1 + "";
							}
						} else {
							long t2 = time / 1000 / 60;
							if (t2 < 10) {
								secode = "0" + t2;
							} else {
								secode = t2 + "";
							}
						}
						if ((time / 1000 / 60 / 60) < 10) {
							minus = "0" + (time / 1000 / 60 / 60);
						} else {
							minus = "" + (time / 1000 / 60 / 60);
						}
						t.setTime(minus + ":" + secode);
					} catch (Exception e) {
						t.setTime("00:00");
					}
				} else {
					t.setNowDistance("0");
					t.setTime("00:00");
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);
		}
		return t;

	}

	public TransferJson getTransferByTransferId(String transferId) {
		TransferJson t = null;
		ResultSet rs = null;
		ResultSet rsTwo = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,modifyOrganSeg,autoTransfer,"
				+ "tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,"
				+ "trueName,getTime,t.isStart,t.startLong,t.startLati,t.endLong,t.endLati,t.distance,t.toHosp,b.model boxNo,t.`status` from transfer t,box b where t.box_id = b.boxid and transferid = ?";
		try {
			//System.out.println("sql:" + sql + "," + transferId);
			ps = conn.prepareStatement(sql);
			ps.setString(1, transferId);
			rs = ps.executeQuery();
			while (rs.next()) {
				t = new TransferJson();
				t.setTransferid(rs.getString("transferid"));
				t.setOrganSeg(rs.getString("organSeg"));
				t.setOpenPsd(rs.getString("openPsd"));
				t.setFromCity(rs.getString("fromCity"));
				t.setToHospName(rs.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType"));
				t.setToHospName(rs.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType"));
				t.setTracfficNumber(rs.getString("tracfficNumber"));
				t.setOrgan(rs.getString("organ"));
				t.setOrganNum(rs.getString("organNum"));
				t.setBlood(rs.getString("blood"));
				t.setBloodNum(rs.getString("bloodNum"));
				t.setSampleOrgan(rs.getString("sampleOrgan"));
				t.setSampleOrganNum(rs.getString("sampleOrganNum"));
				t.setOpoName(rs.getString("opoName"));
				t.setContactName(rs.getString("contactName"));
				t.setContactPhone(rs.getString("contactPhone"));
				t.setPhone(rs.getString("phone"));
				t.setTrueName(rs.getString("trueName"));
				t.setGetTime(rs.getString("getTime"));
				t.setIsStart(rs.getString("isStart"));
				t.setStartLong(rs.getString("startLong"));
				t.setStartLati(rs.getString("startLati"));
				t.setEndLong(rs.getString("endLong"));
				t.setEndLati(rs.getString("endLati"));
				t.setDistance(rs.getString("distance"));
				t.setToHosp(rs.getString("toHosp"));
				t.setBoxNo(rs.getString("boxNo"));
				t.setStatus(rs.getString("status"));
				String distance = rs.getString("distance");
				t.setAutoTransfer(rs.getInt("autoTransfer"));
				t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));
				sql = "select distance from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' order by tr.recordAt desc limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, transferId);
				rsTwo = ps.executeQuery();
				if (rsTwo.next()) {
					String nowDistance = rsTwo.getString("distance");
					double dis = Double.parseDouble(distance)
							- Double.parseDouble(nowDistance);
					if (dis < 0) {
						dis = 0.0;
					}
					t.setNowDistance(dis + "");

					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					try {
						long time = sdf.parse(rsTwo.getString("recordAt"))
								.getTime()
								- sdf.parse(t.getGetTime()).getTime();
						String secode = "";
						String minus = "";
						if (time / 1000 > 3600) {
							long t1 = time / 1000 % 3600;
							t1 = t1 / 60;
							if (t1 < 10) {
								secode = "0" + t1;
							} else {
								secode = t1 + "";
							}
						} else {
							long t2 = time / 1000 / 60;
							if (t2 < 10) {
								secode = "0" + t2;
							} else {
								secode = t2 + "";
							}
						}
						if ((time / 1000 / 60 / 60) < 10) {
							minus = "0" + (time / 1000 / 60 / 60);
						} else {
							minus = "" + (time / 1000 / 60 / 60);
						}
						t.setTime(minus + ":" + secode);
					} catch (Exception e) {
						t.setTime("00:00");
					}

				} else {
					t.setNowDistance("0");
					t.setTime("00:00");
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);
		}
		return t;

	}

	/**
	 * 根据设备的标识获取转运信息
	 * 
	 * @param deviceId
	 * @return
	 */
	public boolean transferDown(String organSeg) {
		TransferJson t = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select id from transfer where transferNumber = ? and `status` = 'done'";
		// System.out.println(sql);
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			while (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;

	}

	/**
	 * 获取暂停的转运
	 * 
	 * @param deviceId
	 * @return
	 */
	public boolean getTransferStatus(String organSeg) {
		TransferJson t = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select isStart from transfer where transferNumber = ? and isStart=0 ";
		// System.out.println(sql);
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			while (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;

	}

	public boolean overTransferTime(String organSeg) {
		TransferJson t = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select getTime from transfer where transferNumber = ?";
		// System.out.println(sql);
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			if (rs.next()) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String getTime = rs.getString("getTime");

				long oldTime = sdf.parse(getTime).getTime();
				long newTime = new Date().getTime();
				// modify *10去掉
				long time = 1000 * 60 * 60 * 24;
				if (newTime - oldTime > time && !getTime.contains("2010")) {
					return true;
				} else {
					return false;
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;
	}

	/**
	 * 根据设备的标识获取转运信息
	 * 
	 * @param deviceId
	 * @return
	 */
	public TransferJson getTransferByDeviceId(String deviceId) {
		TransferJson t = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,modifyOrganSeg,autoTransfer,modifyOrganSeg,"
				+ "tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,opoContactName,opoContactPhone,t.phone,"
				+ "trueName,getTime,t.isStart,t.startLong,t.startLati,t.endLong,t.endLati,t.distance,t.toHosp,b.model boxNo from transfer t,box b where t.box_id = b.boxid and b.deviceId = ? and t.status='transfering' ";
		//System.out.println(sql + ",deviceId:" + deviceId);
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, deviceId);
			rs = ps.executeQuery();
			while (rs.next()) {
				t = new TransferJson();
				t.setTransferid(rs.getString("transferid"));
				t.setOrganSeg(rs.getString("organSeg"));
				t.setOpenPsd(rs.getString("openPsd"));
				t.setFromCity(rs.getString("fromCity"));
				t.setToHospName(rs.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType"));
				t.setToHospName(rs.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType"));
				t.setTracfficNumber(rs.getString("tracfficNumber"));
				t.setOrgan(rs.getString("organ"));
				t.setOrganNum(rs.getString("organNum"));
				t.setBlood(rs.getString("blood"));
				t.setBloodNum(rs.getString("bloodNum"));
				t.setSampleOrgan(rs.getString("sampleOrgan"));
				t.setSampleOrganNum(rs.getString("sampleOrganNum"));
				t.setOpoName(rs.getString("opoName"));
				t.setContactName(rs.getString("contactName"));
				t.setContactPhone(rs.getString("contactPhone"));
				t.setPhone(rs.getString("phone"));
				t.setTrueName(rs.getString("trueName"));
				t.setGetTime(rs.getString("getTime"));
				t.setIsStart(rs.getString("isStart"));
				t.setStartLong(rs.getString("startLong"));
				t.setStartLati(rs.getString("startLati"));
				t.setEndLong(rs.getString("endLong"));
				t.setEndLati(rs.getString("endLati"));
				t.setDistance(rs.getString("distance"));
				t.setToHosp(rs.getString("toHosp"));
				t.setBoxNo(rs.getString("boxNo"));
				t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));
				t.setOpoContactName(rs.getString("opoContactName"));
				t.setOpoContactPhone(rs.getString("opoContactPhone"));
				t.setAutoTransfer(rs.getInt("autoTransfer"));
				t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));
			}
			if (t != null) {
				String organSeg = t.getOrganSeg();
				sql = "SELECT usersIds from transfer_group_temp where organSeg = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, organSeg);
				rs = ps.executeQuery();
				if (rs.next()) {
					t.setPhones(rs.getString("usersIds"));
				}
			} else {
				// 改为空闲状态
				sql = "UPDATE box SET transferStatus = 'free' WHERE deviceId=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, deviceId);
				ps.executeUpdate();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return t;

	}

	public List<TransferJson> getTransfersByOrganSeg(String organSeg) {
		List<TransferJson> lists = new ArrayList<TransferJson>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		ResultSet rsTwo = null;
		String sql = "select transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,toHosp,tracfficType,modifyOrganSeg,autoTransfer,"
				+ "tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,opoContactName,opoContactPhone,t.phone,"
				+ "trueName,getTime,t.isStart,t.startLong,t.startLati,t.endLong,t.endLati,t.distance,b.model boxNo,t.isGroup from transfer t,box b where t.box_id = b.boxid  AND t.filterStatus=0 and transferNumber = ?";
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			while (rs.next()) {
				TransferJson t = new TransferJson();
				t.setTransferid(rs.getString("transferid") == null ? "" : rs
						.getString("transferid"));
				t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
						.getString("organSeg"));
				t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
						.getString("openPsd"));
				t.setFromCity(rs.getString("fromCity") == null ? "" : rs
						.getString("fromCity"));
				t.setToHospName(rs.getString("toHospName") == null ? "" : rs
						.getString("toHospName"));
				t.setTracfficType(rs.getString("tracfficType") == null ? ""
						: rs.getString("tracfficType"));
				t.setToHosp(rs.getString("toHosp") == null ? "" : rs
						.getString("toHosp"));
				System.out.println(rs.getString("toHosp"));
				t.setTracfficType(rs.getString("tracfficType") == null ? ""
						: rs.getString("tracfficType"));
				t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
						: rs.getString("tracfficNumber"));
				t.setOrgan(rs.getString("organ") == null ? "" : rs
						.getString("organ"));
				t.setOrganNum(rs.getString("organNum") == null ? "" : rs
						.getString("organNum"));
				t.setBlood(rs.getString("blood") == null ? "" : rs
						.getString("blood"));
				t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
						.getString("bloodNum"));
				t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
						.getString("sampleOrgan"));
				t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
						: rs.getString("sampleOrganNum"));
				t.setOpoName(rs.getString("opoName") == null ? "" : rs
						.getString("opoName"));
				t.setContactName(rs.getString("contactName") == null ? "" : rs
						.getString("contactName"));
				t.setContactPhone(rs.getString("contactPhone") == null ? ""
						: rs.getString("contactPhone"));
				t.setPhone(rs.getString("phone") == null ? "" : rs
						.getString("phone"));
				t.setTrueName(rs.getString("trueName") == null ? "" : rs
						.getString("trueName"));
				t.setGetTime(rs.getString("getTime") == null ? "" : rs
						.getString("getTime"));
				t.setIsStart(rs.getString("isStart") == null ? "" : rs
						.getString("isStart"));
				t.setStartLong(rs.getString("startLong") == null ? "" : rs
						.getString("startLong"));
				t.setStartLati(rs.getString("startLati") == null ? "" : rs
						.getString("startLati"));
				t.setEndLong(rs.getString("endLong") == null ? "" : rs
						.getString("endLong"));
				t.setEndLati(rs.getString("endLati") == null ? "" : rs
						.getString("endLati"));
				t.setDistance(rs.getString("distance") == null ? "" : rs
						.getString("distance"));
				t.setBoxNo(rs.getString("boxNo") == null ? "" : rs
						.getString("boxNo"));
				t.setIsGroup(rs.getString("isGroup") == null ? "" : rs
						.getString("isGroup"));
				t.setOpoContactName(rs.getString("opoContactName") == null ? ""
						: rs.getString("opoContactName"));
				t
						.setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
								: rs.getString("opoContactPhone"));
				String distance = rs.getString("distance") == null ? "" : rs
						.getString("distance");
				t.setAutoTransfer(rs.getInt("autoTransfer"));
				t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
						: rs.getString("modifyOrganSeg"));

				sql = "select distance,recordAt from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0'  order by tr.recordAt desc limit 1";
				ps = conn.prepareStatement(sql);
				ps.setString(1, rs.getString("transferid"));
				rsTwo = ps.executeQuery();
				if (rsTwo.next()) {
					String nowDistance = rsTwo.getString("distance");
					double dis = Double.parseDouble(distance)
							- Double.parseDouble(nowDistance);
					if (dis < 0) {
						dis = 0.0;
					}
					t.setNowDistance(dis + "");

				} else {
					t.setNowDistance("0");
				}

				lists.add(t);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);
		}
		return lists;

	}

	public int updateStartByOrganSeg(String organSeg, String isStart,
			String type) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		int isOK = -1;
		conn = connDB.getConnection();
		String sql = "update transfer set isStart = ?,getTime=? where transferNumber = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, isStart);
			ps.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm")
					.format(new Date()));
			ps.setString(3, organSeg);
			System.out.println(sql + isStart + organSeg );
			isOK = ps.executeUpdate();

			// 查询群组名,分割整理
			String groupSqlQuery = "select groupName from transfer_group_temp where organSeg = ? ";
			ps = conn.prepareStatement(groupSqlQuery);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			String groupName = "";
			String newGroupName = "";
			if (rs.next()) {
				groupName = rs.getString("groupName");
			}
			if (groupName.contains("-")) {
				String str = "转运中-";
				if ("pause".equals(type)) {
					str = "转运中-";
				} else {
					str = "转运中-";
				}
				newGroupName = str
						+ groupName.substring(
								groupName.split("-")[0].length() + 1, groupName
										.length());
			}

			// 修改群组名
			String updateGroupSql = "update transfer_group_temp set groupName = ? where organSeg = ?";
			ps = conn.prepareStatement(updateGroupSql);
			ps.setString(1, newGroupName);
			ps.setString(2, organSeg);
			ps.executeUpdate();

			return isOK;

		} catch (SQLException e) {
			// TODO Auto-generated catch blockd
			e.printStackTrace();
			isOK = -1;
			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return isOK;
	}

	public int updateTransferStatus(String organSeg, String boxNo) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		int isOK = 0;
		int isTwo = 0;
		int isThree = 0;
		conn = connDB.getConnection();

		// 调用SQL
		try {
			// 修改转运状态
			String sql = "update transfer set `status` = 'done'  where transferNumber = ?";
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			isOK = ps.executeUpdate();
			//System.out.println(isOK);

			// 查询群组名,分割整理
			String groupSqlQuery = "select groupName from transfer_group_temp where organSeg = ? ";
			ps = conn.prepareStatement(groupSqlQuery);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			String groupName = "";
			String newGroupName = "";
			if (rs.next()) {
				groupName = rs.getString("groupName");
			}
			if (groupName.contains("-")) {

				newGroupName = "已转运-"
						+ groupName.substring(
								groupName.split("-")[0].length() + 1, groupName
										.length());
			}

			// 修改群组名
			String updateGroupSql = "update transfer_group_temp set groupName = ? where organSeg = ?";
			ps = conn.prepareStatement(updateGroupSql);
			ps.setString(1, newGroupName);
			ps.setString(2, organSeg);
			isTwo = ps.executeUpdate();
			//System.out.println(isTwo);

			String updateBoxSql = "update box set transferStatus = 'free' where model = ?";
			// System.out.println("updateBox:" + updateBoxSql + ",boxNo:" +
			// boxNo);
			ps = conn.prepareStatement(updateBoxSql);
			ps.setString(1, boxNo);
			isThree = ps.executeUpdate();

			//System.out.println(isThree);

			if (isOK == 0 || isTwo == 0) {
				conn.rollback();
				isOK = 0;
			} else {
				conn.commit();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			CONST.ERROR = e.getMessage();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return isOK;
	}

	public static void main(String[] args) {
		String groupName = "转远在-胜多负少";
		if (groupName.contains("-")) {

			String newGroupName = groupName.substring(groupName.split("-")[0]
					.length() + 1, groupName.length());
			// System.out.println(newGroupName);
		}

	}

	public List<Map<String, String>> getTransfers() {

		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select t.transferid t_transferid,t.transferNumber t_transferNumber,t.organCount t_organCount,t.boxPin t_boxPin, t.fromCity t_fromCity,t.toHospName t_toHospName,t.tracfficType t_tracfficType,t.deviceType t_deviceType,DATE_FORMAT(t.getOrganAt,'%Y-%m-%d') t_getOrganAt,DATE_FORMAT(t.startAt,'%Y-%m-%d') t_startAt,DATE_FORMAT(t.endAt,'%Y-%m-%d') t_endAt,t.`status` t_status,t.createAt t_createAt,b.boxid b_boxid,b.deviceId b_deviceId,b.qrcode b_qrcode,b.model b_model,b.transferStatus b_transferStatus,b.`status` b_status,o.organid o_organid,o.segNumber o_segNumber,o.type o_type,o.bloodType o_bloodType,o.bloodSampleCount o_bloodSampleCount,o.organizationSampleType o_organizationSampleType,o.organizationSampleCount o_organizationSampleCount,h.hospitalid h_hospitalid,h.`name` h_name,h.district h_district,h.address h_address,h.grade h_grade,h.remark h_remark,h.`status` h_status,h.account_id h_account_id,tp.transferPersonid tp_transferPersonid,tp.`name` tp_name,tp.phone tp_phone,tp.organType tp_organType,op.opoid op_opoid,op.`name` op_name,op.district op_district,op.address op_address,op.grade op_grade,op.contactPerson op_contactPerson,op.contactPhone op_contactPhone,op.remark op_remark from transfer t,organ o,box b,hospital h,transferPerson tp,opo op where t.dbStatus = 'N'  and b.boxid = t.box_id and h.hospitalid = t.to_hosp_id AND t.filterStatus=0  and o.organid = t.organ_id and tp.transferPersonid = t.transferPerson_id and op.opoid = t.opo_id and t.status = 'transfering'  ORDER BY t.createAt ";

		try {

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Map<String, String> maps = new HashMap<String, String>();
				maps.put("t_transferid", rs.getString("t_transferid"));
				maps.put("t_transferNumber", rs.getString("t_transferNumber"));
				maps.put("t_organCount", rs.getString("t_organCount"));
				maps.put("t_boxPin", rs.getString("t_boxPin"));
				maps.put("t_fromCity", rs.getString("t_fromCity"));
				maps.put("t_toHospName", rs.getString("t_toHospName"));
				maps.put("t_tracfficType", rs.getString("t_tracfficType"));
				maps.put("t_deviceType", rs.getString("t_deviceType"));
				maps.put("t_getOrganAt", rs.getString("t_getOrganAt"));
				maps.put("t_startAt", rs.getString("t_startAt"));
				maps.put("t_endAt", rs.getString("t_endAt"));
				maps.put("t_status", rs.getString("t_status"));
				maps.put("t_createAt", rs.getString("t_createAt"));
				maps.put("b_boxid", rs.getString("b_boxid"));
				maps.put("b_deviceId", rs.getString("b_deviceId"));
				maps.put("b_qrcode", rs.getString("b_qrcode"));
				maps.put("b_model", rs.getString("b_model"));
				maps.put("b_transferStatus", rs.getString("b_transferStatus"));
				maps.put("b_status", rs.getString("b_status"));
				maps.put("o_organid", rs.getString("o_organid"));
				maps.put("o_segNumber", rs.getString("o_segNumber"));
				maps.put("o_type", rs.getString("o_type"));
				maps.put("o_bloodType", rs.getString("o_bloodType"));
				maps.put("o_bloodSampleCount", rs
						.getString("o_bloodSampleCount"));
				maps.put("o_organizationSampleType", rs
						.getString("o_organizationSampleType"));
				maps.put("o_organizationSampleCount", rs
						.getString("o_organizationSampleCount"));
				maps.put("h_hospitalid", rs.getString("h_hospitalid"));
				maps.put("h_name", rs.getString("h_name"));
				maps.put("h_district", rs.getString("h_district"));
				maps.put("h_address", rs.getString("h_address"));
				maps.put("h_grade", rs.getString("h_grade"));
				maps.put("h_remark", rs.getString("h_remark"));
				maps.put("h_status", rs.getString("h_status"));
				maps.put("h_account_id", rs.getString("h_account_id"));
				maps.put("tp_transferPersonid", rs
						.getString("tp_transferPersonid"));
				maps.put("tp_name", rs.getString("tp_name"));
				maps.put("tp_phone", rs.getString("tp_phone"));
				maps.put("tp_organType", rs.getString("tp_organType"));
				maps.put("op_opoid", rs.getString("op_opoid"));
				maps.put("op_name", rs.getString("op_name"));
				maps.put("op_district", rs.getString("op_district"));
				maps.put("op_address", rs.getString("op_address"));
				maps.put("op_grade", rs.getString("op_grade"));
				maps.put("op_contactPerson", rs.getString("op_contactPerson"));
				maps.put("op_contactPhone", rs.getString("op_contactPhone"));
				maps.put("op_remark", rs.getString("op_remark"));
				lists.add(maps);

			}
			return lists;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return lists;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}

	/**
	 * 获取opo信息
	 * 
	 * @return
	 */
	public List<OpoInfo> getOpoList() {
		List<OpoInfo> lists = new ArrayList<OpoInfo>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "select name from opo";
		try {

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				OpoInfo opo = new OpoInfo();
				opo.setName(rs.getString("name"));
				lists.add(opo);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return lists;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return lists;
	}

	/**
	 * 查询是否存在群组 不存在就创建本医院群组,创建群组后发送默认消息 存在就判断当前人员是否存在群组中 存在就发送默认消息
	 * 不存在就加入群组,发默认消息
	 * 
	 * @param phone
	 * @param hospitalName
	 */
	public void createHospitalGroup(String phone, String hospitalName,
			String trueName) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		try {
			// 查询是否存在群组
			GroupUserQueryResult groupQueryUserResult = rongCloud.group
					.queryUser(hospitalName);
			System.out
					.println("queryUser:  " + groupQueryUserResult.toString());
			RongUserJson rongUserJson = new Gson().fromJson(
					groupQueryUserResult.toString(), RongUserJson.class);

			if (rongUserJson.getUsers().size() > 0) {
				// 判断当前人员是否存在群组中
				if (groupQueryUserResult.toString().contains(phone)) {
					// 发送默认消息

				} else {
					// 加入群组
					String[] groupJoinUserId = { phone };
					CodeSuccessResult groupJoinResult = rongCloud.group.join(
							groupJoinUserId, hospitalName, "*" + hospitalName
									+ "*");
					//System.out.println("join:  " + groupJoinResult.toString());

					// 发送默认消息

				}

			} else {
				String sql = "SELECT phone FROM users u,hospital h WHERE u.hospital_id = h.hospitalid AND h.`name`=?";

				ps = conn.prepareStatement(sql);
				ps.setString(1, hospitalName);
				rs = ps.executeQuery();
				List<String> lists = new ArrayList<String>();
				while (rs.next()) {
					lists.add(rs.getString("phone"));

				}
				String phones[] = new String[lists.size()];
				for (int i = 0; i < lists.size(); i++) {
					phones[i] = lists.get(i);
				}

				// 创建本医院群组
				rongCloud.group.create(phones, hospitalName, "--"
						+ hospitalName + "--");
				// 发送默认消息

			}
			// 发送默认消息
			// 发送群组消息方法（以一个用户身份向群组发送消息，单条消息最大 128k.每秒钟最多发送 20 条消息，每次最多向 3
			// 个群组发送，如：一次向 3 个群组发送消息，示为 3 条消息。）
			String[] messagePublishGroupToGroupId = { hospitalName };

			rongCloud.message.publishGroup(phone, messagePublishGroupToGroupId,
					new InfoNtfMessage(trueName + "已进入群组", ""), trueName
							+ "已进入群组", "", 0, 0, 1);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}

	/**
	 * 
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
	 *            本人姓名18
	 * @return
	 * @throws SQLException
	 */
	public String insertTransfer(String organSeg, String getTime,
			String openPsd, String organ, String organNum, String blood,
			String bloodNum, String sampleOrgan, String sampleOrganNum,
			String fromCity, String toHospName, String tracfficType,
			String tracfficNumber, String opoName, String contactName,
			String contactPhone, String phone, String trueName,
			String groupName, String usersIds, String distance,
			String startLong, String startLati, String endLong, String endLati,
			String toHosp, String boxNo, String isStart, String opoContactName,
			String opoContactPhone, String autoTransfer, String modifyOrganSeg,String organAddress,String organTime)
			throws SQLException {
		String transferSql = "insert into transfer(transferNumber,getTime,boxPin,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,fromCity,toHospName,tracfficType,tracfficNumber,opoName,contactName,contactPhone,phone,trueName,distance,startLong,startLati,endLong,endLati,toHosp,box_id,isStart,opoContactName,opoContactPhone,to_hosp_id,autoTransfer,modifyOrganSeg,organAddress,organStart) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		ResultSet rs = null;
		PreparedStatement ps = null;
		long transferId = 0L;
		int usersId = 0;
		int insertTransferUsers = 0;
		int transferIdUpdate = 0;
		int rongGroup = -1;
		int updateGroup = 0;
		int insertGroup = 0;
		String to_hosp_id = "";
		String returnValue = "";
		String boxId = "";
		int updateBoxStatus = 0;
		BoxDao boxDao = new BoxDao();
		this.conn = this.connDB.getConnection();

		try {

			/**
			 * 判断箱子是否可用 获取箱子的id 更改箱子的状态为transfering
			 */

			if (!boxDao.getBoxNoStatus(boxNo) && !"99999".equals(boxNo)) {
				CONST.ERROR = "箱子已被使用,不可创建";
				return "";
			}

			boxId = boxDao.getBoxId(boxNo);
			to_hosp_id = boxDao.getHospId(boxNo);
			if ("1".equals(isStart) || "0".equals(isStart)) {
				updateBoxStatus = boxDao.updateBoxStatus(boxNo, "transfering");
				if (updateBoxStatus == 0) {
					CONST.ERROR = "修改箱子状态失败";
					return "";
				}
			}

			this.conn.setAutoCommit(false);
			ps = this.conn.prepareStatement(transferSql, 1);
			ps.setString(1, organSeg);
			ps.setString(2, getTime);
			ps.setString(3, openPsd);
			ps.setString(4, organ);
			ps.setString(5, organNum);
			ps.setString(6, blood);
			ps.setString(7, bloodNum);
			ps.setString(8, sampleOrgan);
			ps.setString(9, sampleOrganNum);
			ps.setString(10, fromCity);
			ps.setString(11, toHospName);
			ps.setString(12, tracfficType);
			ps.setString(13, tracfficNumber);
			ps.setString(14, opoName);
			ps.setString(15, contactName);
			ps.setString(16, contactPhone);
			ps.setString(17, phone);
			ps.setString(18, trueName);
			ps.setString(19, distance);
			ps.setString(20, startLong);
			ps.setString(21, startLati);
			ps.setString(22, endLong);
			ps.setString(23, endLati);
			ps.setString(24, toHosp);
			ps.setString(25, boxId);

			if (isStart != null && !"".equals(isStart)) {
				ps.setString(26, isStart);
			} else {
				ps.setString(26, "0");
			}
			ps.setString(27, opoContactName);
			ps.setString(28, opoContactPhone);
			ps.setString(29, to_hosp_id);
			if ("1".equals(autoTransfer)) {
				ps.setInt(30, 1);
			} else {
				ps.setInt(30, 0);
			}
			ps.setString(31, modifyOrganSeg);
			ps.setString(32, organAddress);
			ps.setString(33, organTime);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			Object e = null;
			if (rs.next()) {
				e = rs.getObject(1);
			}

			transferId = Long.parseLong(e.toString());
			String transferUpdate = "update transfer set transferId = ? where id = ?";
			ps = this.conn.prepareStatement(transferUpdate);
			ps.setString(1, e.toString());
			ps.setLong(2, transferId);
			transferIdUpdate = ps.executeUpdate();
			String users = "select id from users where phone = ?";
			ps = this.conn.prepareStatement(users);
			ps.setString(1, phone);
			rs = ps.executeQuery();
			if (rs.next()) {
				usersId = rs.getInt("id");
			}

			String transferUser = "insert into transfer_users(users_id,transfer_id) values(?,?)";
			ps = this.conn.prepareStatement(transferUser);
			ps.setInt(1, usersId);
			ps.setLong(2, transferId);
			insertTransferUsers = ps.executeUpdate();
			// 判断是否存在organSeg的群组
			String isOrganSegsql = "select usersIds from transfer_group_temp where organSeg = ?";
			ps = this.conn.prepareStatement(isOrganSegsql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			String[] usersIdsArray = usersIds.split(",");
			// usersIds 1 3 6 gruopid 1 groupName 测试1
			// 创建
			CodeSuccessResult groupCreateResult = rongCloud.group.create(
					usersIdsArray, organSeg, groupName);
			String result = groupCreateResult.toString();
			if (result.contains("200")) {
				// 存在 更新
				if (rs.next()) {
					String updateGroupSql = "update transfer_group_temp set usersIds = ?,groupName = ?,phone = ? where organSeg = ?";
					ps = conn.prepareStatement(updateGroupSql);
					ps.setString(1, usersIds);
					ps.setString(2, groupName);
					ps.setString(3, phone);
					ps.setString(4, organSeg);
					updateGroup = ps.executeUpdate();

				}
				// 不存在 插入
				else {
					String insertGroupSql = "insert into transfer_group_temp(organSeg,usersIds,groupName,phone) values(?,?,?,?)";
					ps = conn.prepareStatement(insertGroupSql);
					ps.setString(1, organSeg);
					ps.setString(2, usersIds);
					ps.setString(3, groupName);
					if (!"".equals(phone)) {
						ps.setString(4, phone);
					} else {
						ps.setString(4, "18388888888");
					}
					insertGroup = updateGroup = ps.executeUpdate();
				}

			} else {
				// System.out.println("result:" + result);
				rongGroup = 0;

			}

			// 插入转运推送设置

			// for (int i = 0; i < usersIds.split(",").length; i++) {
			// String pushSiteSql =
			// "insert into transferPushSite(organSeg,phone) values(?,?)";
			// ps = conn.prepareStatement(pushSiteSql);
			// ps.setString(1, organSeg);
			// ps.setString(2, usersIds.split(",")[i]);
			// ps.executeUpdate();
			// }

			// System.out.println("transferId:" + transferId + "," + "usersId:"
			// + usersId + "," + "insertTransferUsers:"
			// + insertTransferUsers + "," + "transferIdUpdate:"
			// + transferIdUpdate + "," + "rongGroup:" + rongGroup + ","
			// + "updateGroup:" + updateGroup + "," + "insertGroup:"
			// + insertGroup);
			if (transferId != 0L && transferIdUpdate != 0 && rongGroup != 0
					&& (updateGroup != 0 || insertGroup != 0)) {
				this.conn.commit();
				returnValue = organSeg;
			} else {
				this.conn.rollback();
				updateBoxStatus = boxDao.updateBoxStatus(boxNo, "free");
				CONST.ERROR = "数据库错误:transferId:" + transferId + ",usersId:"
						+ usersId + ",usersId:" + usersId + ",usersId:"
						+ transferIdUpdate + ",transferIdUpdate:"
						+ transferIdUpdate + ",rongGroup:" + rongGroup
						+ ",updateGroup:" + updateGroup + ",insertGroup:"
						+ insertGroup;
			}
		} catch (Exception var35) {
			var35.printStackTrace();
			this.conn.rollback();
			updateBoxStatus = boxDao.updateBoxStatus(boxNo, "free");
		} finally {
			this.connDB.closeAll(rs, ps, this.conn);
		}

		return returnValue;
	}

	/**
	 * 根据手机修改信息
	 */
	public TransferPushSite getTransferPushSite(String phone, String organSeg) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		TransferPushSite transferPushSite = null;
		String sql = "";

		// 查询是否存在
		try {
			sql = "select temperatureStatus,openStatus,collisionStatus from  transferPushSite   where organSeg = ? and phone = ?";

			// 调用SQL

			ps = conn.prepareStatement(sql);

			ps.setString(1, organSeg);
			ps.setString(2, phone);
			rs = ps.executeQuery();

			if (rs.next()) {
				transferPushSite = new TransferPushSite();
				transferPushSite.setTemperatureStatus(rs
						.getInt("temperatureStatus"));
				transferPushSite.setOpenStatus(rs.getInt("openStatus"));
				transferPushSite.setCollisionStatus(rs
						.getInt("collisionStatus"));
			} else {
				sql = "insert into transferPushSite(organSeg,phone) values(?,?)";
				ps = conn.prepareStatement(sql);

				ps.setString(1, organSeg);
				ps.setString(2, phone);
				ps.executeUpdate();
				//System.out.println(sql + ",organSeg:" + organSeg + ",phone:" + phone);
				transferPushSite = new TransferPushSite();
				transferPushSite.setTemperatureStatus(0);
				transferPushSite.setOpenStatus(0);
				transferPushSite.setCollisionStatus(0);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return transferPushSite;
	}

	/**
	 * 根据手机修改信息
	 */
	public int setTransferPushSite(String phone, String organSeg, String type,
			int status) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "";
		int result = -1;
		// 查询是否存在
		try {

			if ("temperature".equals(type)) {
				sql = "update transferPushSite set temperatureStatus = ? where organSeg = ? and phone = ?";
			} else if ("open".equals(type)) {
				sql = "update transferPushSite set openStatus = ? where organSeg = ? and phone = ? ";
			} else if ("collision".equals(type)) {
				sql = "update transferPushSite set collisionStatus = ? where organSeg = ? and phone = ?";
			}

			// 调用SQL

			ps = conn.prepareStatement(sql);
			ps.setInt(1, status);
			ps.setString(2, organSeg);
			ps.setString(3, phone);
			result = ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage());

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return result;
	}

	public int updateTransfer(String organSeg, String getTime, String openPsd,
			String organ, String organNum, String blood, String bloodNum,
			String sampleOrgan, String sampleOrganNum, String fromCity,
			String toHospName, String tracfficType, String tracfficNumber,
			String opoName, String contactName, String contactPhone,
			String phone, String trueName, String groupName, String usersIds,
			String distance, String startLong, String startLati,
			String endLong, String endLati, String toHosp, String isStart,
			String opoContactName, String opoContactPhone,
			String modifyOrganSeg, String autoTransfer, String historyModify,
			String status) throws SQLException {

		String transferSql = "update  transfer set getTime=?,boxPin=?,organ=?,organNum=?,blood=?,bloodNum=?,sampleOrgan=?,sampleOrganNum=?,fromCity=?,toHospName=?,tracfficType=?,tracfficNumber=?,opoName=?,contactName=?,contactPhone=?,phone=?,trueName=?,distance=?,startLong=?,startLati=?,endLong=?,endLati=?,toHosp=?,isStart=?,opoContactName=?,opoContactPhone=?,getTime = ?,modifyOrganSeg=?,autoTransfer=?,historyModify=? where transferNumber=?";
		ResultSet rs = null;
		PreparedStatement ps = null;

		int updateBoxStatus = 0;

		this.conn = this.connDB.getConnection();

		try {

			this.conn.setAutoCommit(false);

			// 查询出opo人员,判断是否修改了人员
			String opoSql = "select usersIds,groupName from transfer_group_temp where organSeg= ? ";
			ps = conn.prepareStatement(opoSql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			if (rs.next()) {
				String usersIdsTemp = rs.getString("usersIds");
				groupName = rs.getString("groupName");
				/**
				 * 修改了opo人员 1.删除以前在融云opo人员 2.加入融云 3.拼接usersIds 插入数据库
				 */
				try {
					if (usersIdsTemp.contains(",")
							&& usersIdsTemp.split(",").length >= 3) {
						String temp[] = usersIdsTemp.split(",");
						if (!opoContactPhone.equals(temp[2])) {

							rongCloud.group.quit(temp[2].split(" "), organSeg);

							rongCloud.group.join(opoContactPhone.split(" "),
									organSeg, groupName);

							String tempIds = "";
							for (int i = 0; i < temp.length; i++) {
								if (i == 2) {
									tempIds += opoContactPhone;
								} else {
									tempIds += temp[i];
								}

								if (i != (temp.length - 1)) {
									tempIds += ",";
								}
							}
							usersIds = tempIds;
							//System.out.println("usersIds:" + usersIds + ",tempIds:" + usersIdsTemp);

						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			ps = this.conn.prepareStatement(transferSql);

			ps.setString(1, getTime);
			ps.setString(2, openPsd);
			ps.setString(3, organ);
			ps.setString(4, organNum);
			ps.setString(5, blood);
			ps.setString(6, bloodNum);
			ps.setString(7, sampleOrgan);
			ps.setString(8, sampleOrganNum);
			ps.setString(9, fromCity);
			ps.setString(10, toHospName);
			ps.setString(11, tracfficType);
			ps.setString(12, tracfficNumber);
			ps.setString(13, opoName);
			ps.setString(14, contactName);
			ps.setString(15, contactPhone);
			ps.setString(16, phone);
			ps.setString(17, trueName);
			ps.setString(18, distance);
			ps.setString(19, startLong);
			ps.setString(20, startLati);
			ps.setString(21, endLong);
			ps.setString(22, endLati);
			ps.setString(23, toHosp);

			if ("1".equals(isStart)) {
				isStart = "1";
			} else {
				isStart = "0";
			}
			ps.setString(24, isStart);

			ps.setString(25, opoContactName);
			ps.setString(26, opoContactPhone);
			// SimpleDateFormat sdf = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ps.setString(27, getTime);
			if (modifyOrganSeg != null && !"".equals(modifyOrganSeg)) {
				ps.setString(28, modifyOrganSeg);
			} else {
				ps.setString(28, "");
			}

			if ("1".equals(autoTransfer)) {
				ps.setInt(29, 0);
			} else {
				ps.setInt(29, 0);
			}

			ps.setString(30, historyModify);
			ps.setString(31, organSeg);

			updateBoxStatus = ps.executeUpdate();
			//System.out.println("updateBoxStatus:" + updateBoxStatus);

			// 更新修改的群组
			if (!"modify".equals(historyModify)) {
				transferSql = "UPDATE transfer_group_temp SET usersIds = ?,groupName=? WHERE organSeg = ? ";
				ps = this.conn.prepareStatement(transferSql);
				ps.setString(1, usersIds);

				if (modifyOrganSeg != null && !"".equals(modifyOrganSeg)) {
					fromCity = modifyOrganSeg;
				}
				if ("1".equals(isStart)) {

					ps.setString(2, "转运中-" + fromCity + "-" + organ);
				} else {
					ps.setString(2, "待转运-" + fromCity + "-" + organ);
				}

				ps.setString(3, organSeg);
				ps.executeUpdate();
			} else if (usersIds != null && !"".equals(usersIds))
			// 预览修改界面
			{
				transferSql = "UPDATE transfer_group_temp SET usersIds = ?  WHERE organSeg = ? ";
				ps = this.conn.prepareStatement(transferSql);
				ps.setString(1, usersIds);

				ps.setString(2, organSeg);
				ps.executeUpdate();
			}
			conn.commit();
		} catch (SQLException var35) {
			var35.printStackTrace();
			this.conn.rollback();
			CONST.ERROR = var35.getMessage();
			//System.out.println("error:" + CONST.ERROR);

		} finally {
			this.connDB.closeAll(rs, ps, this.conn);
		}

		return updateBoxStatus;
	}

	public String deleteTransfer(String organSeg, String phone) {

		ResultSet rs = null;
		PreparedStatement ps = null;

		String returnValue = "";
		int delTransferInt = 0;
		int delTransferUsersInt = 0;
		int delTransferGroupTempInt = 0;
		this.conn = this.connDB.getConnection();

		try {
			this.conn.setAutoCommit(false);

			// 修改箱子状态
			String updateBoxStatus = "update box set transferStatus = 'free' where boxid = ( select box_id from transfer where transferNumber = ?)";
			ps = this.conn.prepareStatement(updateBoxStatus);
			ps.setString(1, organSeg);
			ps.executeUpdate();

			String delTransfer = "delete from transfer where transferNumber=?";
			ps = this.conn.prepareStatement(delTransfer);
			//System.out.println("+++++++++");
			ps.setString(1, organSeg);
			delTransferInt = ps.executeUpdate();

			String delTransferUsers = "delete from transfer_users where transfer_id = (select id from transfer where transferNumber='"
					+ organSeg + "')";
			ps = this.conn.prepareStatement(delTransferUsers);
			// ps.setString(1, organSeg);
			delTransferUsersInt = ps.executeUpdate();
			//System.out.println("delTransferUsers:" + delTransferUsers);

			String delTransferGroupTemp = "delete from transfer_group_temp where organSeg = ?";
			ps = this.conn.prepareStatement(delTransferGroupTemp);
			ps.setString(1, organSeg);
			delTransferGroupTempInt = ps.executeUpdate();
			//System.out.println("delTransferGroupTemp:" + delTransferGroupTemp);

			//System.out.println("delTransferInt:" + delTransferInt + ",:delTransferUsersInt" + delTransferUsersInt + ",:delTransferGroupTempInt" + delTransferGroupTempInt);
			//System.out.println("+++++++++");

			String sqlTransfer = "select id from transfer where transferNumber=?";
			ps = this.conn.prepareStatement(sqlTransfer);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			if (rs.next()) {

			} else {
				delTransferInt = 1;
			}

			String sqlTransferUsers = "select id from transfer_users where transfer_id = (select id from transfer where transferNumber=?)";
			ps = this.conn.prepareStatement(sqlTransferUsers);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			if (rs.next()) {

			} else {
				delTransferUsersInt = 1;
			}

			String sqlTransferGroupTemp = "select id  from transfer_group_temp where organSeg = ?";
			ps = this.conn.prepareStatement(sqlTransferGroupTemp);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			if (rs.next()) {

			} else {
				delTransferGroupTempInt = 1;
			}

			if (delTransferInt == 1 && delTransferGroupTempInt == 1) {
				CodeSuccessResult groupDismissResult = rongCloud.group.dismiss(
						phone + "", organSeg);
				if (groupDismissResult.toString().contains("200")) {
					conn.commit();
					returnValue = "ok";
				}

			} else {
				conn.rollback();

			}

		} catch (Exception var35) {
			var35.printStackTrace();
			try {
				this.conn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			this.connDB.closeAll(rs, ps, this.conn);
		}

		return returnValue;
	}

}
