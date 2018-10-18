package com.life.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.life.entity.Record;
import com.life.entity.TransferRecord;
import com.life.utils.ConnectionDB;

public class TransferRecordDao {

	// 创建数据库连接对象
	private Connection conn = null;
	private ConnectionDB connDB = new ConnectionDB();

	public List<TransferRecord> getTransferRecordsByOrganSegSample(
			String organSeg) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<TransferRecord> transferRecords = new ArrayList<TransferRecord>();
		double distance = 0;
		try {
			String sql = "SELECT distance FROM transfer where transferNumber = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			rs = ps.executeQuery();
			if (rs.next()) {
				distance = Double.parseDouble(rs.getString("distance"));
			}

			sql = "select DATE_FORMAT(tr.recordAt,'%m-%d %H:%i') recordAt,tr.collision,tr.open,tr.longitude,tr.latitude from transferRecord tr,transfer t where tr.transfer_id = t.id and t.transferNumber = ? and longitude <> '' and longitude <> '0.0' ORDER BY recordAt   ";
			// 调用SQL

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);

			rs = ps.executeQuery();
			while (rs.next()) {
				TransferRecord t = new TransferRecord();
				t.setRecordAt(rs.getString("recordAt"));
				t.setCollision((int) (Double.parseDouble(rs
						.getString("collision")))
						+ "");
				t
						.setOpen((int) (Double
								.parseDouble(rs.getString("open")))
								+ "");

				t.setLongitude(rs.getString("longitude"));
				t.setLatitude(rs.getString("latitude"));

				transferRecords.add(t);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return transferRecords;
	}

	/**
	 * 获取转运记录
	 * 
	 * @param phone
	 * @return
	 */
	public List<TransferRecord> getTestLongitude() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<TransferRecord> transferRecords = new ArrayList<TransferRecord>();

		try {
			String sql = "SELECT longitude,latitude FROM transferRecordT";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				TransferRecord tr = new TransferRecord();
				tr.setLongitude(rs.getString("longitude"));
				tr.setLatitude(rs.getString("latitude"));
				transferRecords.add(tr);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return transferRecords;
	}

	/**
	 * 获取转运记录
	 * 
	 * @param phone
	 * @return
	 */
	public List<TransferRecord> getTransferRecordsByOrganSeg(String organSeg,
			String type) {
		ResultSet rs = null;

		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<TransferRecord> transferRecords = new ArrayList<TransferRecord>();

		try {

			// String sql =
			// "SELECT distance FROM transfer where transferNumber = ?";
			// ps = conn.prepareStatement(sql);
			//
			// ps.setString(1, organSeg);
			// rs = ps.executeQuery();
			// if (rs.next()) {
			// distance = Double.parseDouble(rs.getString("distance"));
			// }

			// '%m-%d %H:%i'

			String sql = "select tr.id,tr.transferRecordid,tr.transfer_id,tr.type,tr.currentCity,tr.distance,tr.duration,tr.remark,tr.longitude,tr.latitude,tr.temperature,tr.avgTemperature,tr.power,tr.expendPower,tr.humidity,DATE_FORMAT(tr.recordAt,'%H:%i') recordAt,tr.recordAt recordAtAll,tr.dbStatus,tr.createAt,tr.modifyAt,tr.press1,tr.press2,tr.flow1,tr.flow2,tr.pupple,tr.collision,tr.open from transferRecord tr,transfer t where tr.transfer_id = t.id  and t.transferNumber = ? and tr.humidity <> '0.0'  and  longitude <> '0.0' "
					+ type + "    ORDER BY recordAtAll   ";
			// 调用SQL

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			// ps.setString(2, longitude);

			rs = ps.executeQuery();
			while (rs.next()) {
				TransferRecord t = new TransferRecord();
				t.setTransferRecordId(rs.getInt("id"));
				t.setAvgTemperature(rs.getString("avgTemperature"));
				t.setCreateAt(rs.getString("createAt"));
				t.setCurrentCity(rs.getString("currentCity"));
				t.setDbStatus(rs.getString("dbStatus"));

				t.setDistance((int) (Double.parseDouble(rs
						.getString("distance")))
						+ "");
				t.setDuration(rs.getString("duration"));
				t.setExpendPower(rs.getString("expendPower"));
				t.setFlow1(rs.getString("flow1"));
				t.setFlow2(rs.getString("flow2"));
				t.setHumidity(rs.getString("humidity"));
				t.setLatitude(rs.getString("latitude"));
				t.setLongitude(rs.getString("longitude"));
				t.setModifyAt(rs.getString("modifyAt"));
				t.setPower(rs.getString("power"));
				t.setPress1(rs.getString("press1"));
				t.setPress2(rs.getString("press2"));
				t.setRecordAt(rs.getString("recordAt"));
				t.setRemark(rs.getString("remark"));
				DecimalFormat df = new DecimalFormat("######0"); // 四色五入转换成整数
				String temperature = rs.getString("temperature").substring(0,
						rs.getString("temperature").indexOf(".", 0) + 2);
				t.setTemperature(temperature);
				t.setTransfer_id(rs.getString("transfer_id"));
				t.setType(rs.getString("type"));
				t.setPupple(rs.getString("pupple"));
				t.setCollision((int) (Double.parseDouble(rs
						.getString("collision")))
						+ "");
				t
						.setOpen((int) (Double
								.parseDouble(rs.getString("open")))
								+ "");
				transferRecords.add(t);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return transferRecords;
	}

	/**
	 * 获取转运记录
	 * 
	 * @param phone
	 * @return
	 */
	public List<TransferRecord> getTransferRecordsByOrganSegTen(String organSeg) {
		ResultSet rs = null;

		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<TransferRecord> transferRecords = new ArrayList<TransferRecord>();
		int count = 0;

		try {
			String sql = "select count(tr.id) count from transferRecord tr,transfer t where tr.transfer_id = t.id  and t.transferNumber = ? and tr.humidity <> '0.0'  and  longitude <> '0.0'     ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			// ps.setString(2, longitude);

			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt("count");
				//System.out.println("count:" + count);
			}

			// '%m-%d %H:%i'
			sql = "select tr.id,tr.transferRecordid,tr.transfer_id,tr.distance,tr.temperature,tr.power,tr.humidity,DATE_FORMAT(tr.recordAt,'%H:%i') recordAt,tr.recordAt recordAtAll,tr.press1,tr.press2,tr.flow1,tr.flow2,tr.pupple,tr.collision,tr.open from transferRecord tr,transfer t where tr.transfer_id = t.id  and t.transferNumber = ? and tr.humidity <> '0.0'  and  longitude <> '0.0'  "
					+ (count < 200 ? " " : "and tr.id%10=0")
					+ "   ORDER BY recordAtAll   ";
			// 调用SQL

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			// ps.setString(2, longitude);

			rs = ps.executeQuery();
			while (rs.next()) {
				TransferRecord t = new TransferRecord();
				t.setTransferRecordId(rs.getInt("id"));

				t.setDistance((int) (Double.parseDouble(rs
						.getString("distance")))
						+ "");

				t.setFlow1(rs.getString("flow1"));
				t.setFlow2(rs.getString("flow2"));
				t.setHumidity(rs.getString("humidity"));
				t.setPower(rs.getString("power"));
				t.setPress1(rs.getString("press1"));
				t.setPress2(rs.getString("press2"));
				t.setRecordAt(rs.getString("recordAt"));

				DecimalFormat df = new DecimalFormat("######0"); // 四色五入转换成整数
				String temperature = rs.getString("temperature").substring(0,
						rs.getString("temperature").indexOf(".", 0) + 2);
				t.setTemperature(temperature);
				t.setTransfer_id(rs.getString("transfer_id"));

				t.setPupple(rs.getString("pupple"));
				t.setCollision((int) (Double.parseDouble(rs
						.getString("collision")))
						+ "");
				t
						.setOpen((int) (Double
								.parseDouble(rs.getString("open")))
								+ "");
				transferRecords.add(t);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return transferRecords;
	}

	public static void main(String[] args) {
		String b = "2.0";
		//System.out.println((int) Double.parseDouble(b));
	}

	public String insertPowerTemp(String time, String level, String deviceId) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "insert into powerTemp(time,level,deviceId) values(?,?,?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, time);
			ps.setString(2, level);
			ps.setString(3, deviceId);
			ps.executeUpdate();

			return time;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage());

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return "";
	}

	public synchronized int insertRecord(String temperature, String humidity,
			String collision, String longitude, String latitude, String city,
			String power, String expendPower, String transferId,
			String recordAt, String distance, String duration, String open) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int flag = 0;

		String sql = "insert into transferRecord(temperature,humidity,collision,longitude,latitude,currentCity,power,expendPower,transfer_id,recordAt,distance,duration,open) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		// 调用SQL
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql, 1);
			ps.setString(1, temperature);
			ps.setString(2, humidity);
			ps.setString(3, collision);
			ps.setString(4, longitude);
			ps.setString(5, latitude);
			ps.setString(6, city);
			ps.setString(7, power);
			ps.setString(8, expendPower);
			ps.setString(9, transferId);
			ps.setString(10, recordAt);
			ps.setString(11, distance);
			ps.setString(12, duration);
			ps.setString(13, open);

			int insertSql = ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			Object e = null;
			if (rs.next()) {
				e = rs.getObject(1);
			}

			long transferRecordId = Long.parseLong(e.toString());
			String transferUpdate = "update transferRecord set transferRecordid = ? where id = ?";
			ps = this.conn.prepareStatement(transferUpdate);
			ps.setString(1, e.toString());
			ps.setLong(2, transferRecordId);
			int updateId = ps.executeUpdate();

			if (insertSql == 1) {
				conn.commit();
				flag = insertSql;
			} else {
				conn.rollback();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return flag;
	}

	public int getUserPushSite(String pPhone, String pType) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int status = 0;

		String sql = "select "
				+ pType
				+ " from users u,userPushSite ups where phone = ? and u.id=ups.userId";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, pPhone);
			rs = ps.executeQuery();
			if (rs.next()) {
				status = rs.getInt(pType);
			}
			return status;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage());

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return status;
	}

	public String getAllDevice() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<String> list = new ArrayList<String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long time48 = 1000 * 60 * 60 * 48;
		Date time = new Date(new Date().getTime() - time48);
		String timeStr = sdf.format(time);
		String sql = "SELECT remark FROM transferRecord WHERE recordAt > ? GROUP BY remark";
		String result ="";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, timeStr);
			rs = ps.executeQuery();
			while (rs.next()) {
              result += rs.getString("remark");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage());

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return result;
	}

	public int getTransferPushSite(String pPhone, String pOrganSeg, String pType) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int status = 0;

		String sql = "select " + pType
				+ " from transferPushSite where phone = ? and organSeg = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, pPhone);
			ps.setString(2, pOrganSeg);
			rs = ps.executeQuery();
			if (rs.next()) {
				status = rs.getInt(pType);
			}
			return status;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage());

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return status;
	}

	public List<Integer> insertRecords(List<Record> recordList, String pType) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		List<Integer> lists = new ArrayList<Integer>();

		String sql = "insert into transferBoxRecord(temperature,humidity,collision,longitude,latitude,currentCity,power,expendPower,transfer_id,recordAt,distance,duration,open,remark,voltage,trueTemperature,other) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		// 调用SQL
		try {

			// ps = conn.prepareStatement(sql);
			// // conn.setAutoCommit(false);
			// for (int i = 0; i < recordList.size(); i++) {
			// Record record = recordList.get(i);
			// ps.setString(1, record.getTemperature());
			// ps.setString(2, record.getHumidity());
			// ps.setString(3, (int) Double.parseDouble(record.getCollision())
			// + "");
			// ps.setString(4, record.getLongitude());
			// ps.setString(5, record.getLatitude());
			// ps.setString(6, record.getCurrentCity());
			// ps.setString(7, record.getPower());
			// ps.setString(8, record.getExpendPower());
			// ps.setString(9, record.getTransferId());
			// ps.setString(10, record.getRecordAt());
			// ps.setString(11, record.getDistance());
			// ps.setString(12, record.getDuration());
			// ps.setString(13, (int) Double.parseDouble(record.getOpen())
			// + "");
			// ps.setString(14, record.getRemark());
			// ps.setString(15, record.getVoltage());
			// ps.setString(16, record.getTrueTemperature());
			// ps.setString(17, record.getOther());
			// ps.addBatch();
			// lists.add(record.getId());
			//
			// }
			//
			// ps.executeBatch();

			// if ("start".equals(pType)) {
			sql = "insert into transferRecord(temperature,humidity,collision,longitude,latitude,currentCity,power,expendPower,transfer_id,recordAt,distance,duration,open,remark,voltage,trueTemperature,other) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);
			// conn.setAutoCommit(false);
			for (int i = 0; i < recordList.size(); i++) {
				Record record = recordList.get(i);
				ps.setString(1, record.getTemperature());
				ps.setString(2, record.getHumidity());
				ps.setString(3, (int) Double.parseDouble(record.getCollision())
						+ "");
				ps.setString(4, record.getLongitude());
				ps.setString(5, record.getLatitude());
				ps.setString(6, record.getCurrentCity());
				ps.setString(7, record.getPower());
				ps.setString(8, record.getExpendPower());
				if ("".equals(record.getTransferId())) {
					record.setTransferId("1");
				}
				//System.out.println("getTransferId:" + record.getTransferId());
				ps.setString(9, record.getTransferId());
				ps.setString(10, record.getRecordAt());
				ps.setString(11, record.getDistance());
				ps.setString(12, record.getDuration());
				ps.setString(13, (int) Double.parseDouble(record.getOpen())
						+ "");
				ps.setString(14, record.getRemark());
				ps.setString(15, record.getVoltage());
				ps.setString(16, record.getTrueTemperature());
				ps.setString(17, record.getOther());
				ps.addBatch();
				lists.add(record.getId());

			}

			ps.executeBatch();
			// }

			// conn.commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage());
			lists = new ArrayList<Integer>();

		} finally {
			try {
				ps.clearBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			connDB.closeAll(rs, ps, conn);
		}
		return lists;
	}

}
