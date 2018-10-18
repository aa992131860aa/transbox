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

import com.life.entity.Blood;
import com.life.entity.BoxUse;
import com.life.entity.OpoInfo;
import com.life.entity.TransferRecord;
import com.life.utils.ConnectionDB;
import com.mysql.jdbc.Statement;

public class BoxDao {
	private ConnectionDB connDB = new ConnectionDB();

	// 创建数据库连接对象
	private Connection conn = null;

	public void testSql() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "SELECT COUNT(id) FROM transferRecord WHERE transfer_id = ? GROUP BY latitude";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, "2064");
			rs = ps.executeQuery();
			int temp = -1;
			while(rs.next()){
				temp++;
				if(temp==1){
					break;
				}
				//System.out.println(temp);
				
				
				
			}
			//System.out.println("row:"+temp);
		} catch (Exception e) {

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	public static void main(String[] args) {
		BoxDao boxDao = new BoxDao();
//      插入树兰未录入的数据
//		1662 机场       1755 火车站
		List<TransferRecord> tr = boxDao.test("2584", "2018-08-23","1662");
		for (int i = 0; i < tr.size(); i++) {
			boxDao.insertTest(tr.get(i));
			System.out.println("start:" + i + "," + tr.size());
		}
 

	}

	public boolean getBoxNoStatus(String boxNo) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "SELECT t.transferNumber organSeg FROM transfer t ,box b WHERE t.box_id = b.boxid AND b.model = ? AND t.`status`='transfering'";
	 
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, boxNo);
			rs = ps.executeQuery();
			if (rs.next()) {
				String organSeg = rs.getString("organSeg");
				if (organSeg.contains("AP")) {
					return true;
				}
			}else{
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

	public String  autoTransferOrganSeg(String boxNo) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "SELECT t.transferNumber organSeg FROM transfer t ,box b WHERE t.box_id = b.boxid AND b.model = ? AND t.`status`='transfering'";
	 
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, boxNo);
			rs = ps.executeQuery();
			if (rs.next()) {
				String organSeg = rs.getString("organSeg");
				if (organSeg.contains("AP")) {
					 //停止转运
					return organSeg;
				}
			} 

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		 return null;
	}
	
	/**
	 * 收集箱子信息
	 */
	public int setBoxIMEI(String device) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "insert ignore into device_temp(device,createTime) values(?,?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, device);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ps.setString(2, sdf.format(new Date()));
			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return 0;

	}

	public String getBoxId(String boxNo) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select boxid from box where model = ? ";
		//System.out.println("boxNo:" + boxNo);
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, boxNo);
			rs = ps.executeQuery();
			if (rs.next()) {
				String boxid = rs.getString("boxid");
				return boxid;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return "";
	}
	
	public String getBoxNo(String deviceId) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select model from box where deviceId = ? ";
		//System.out.println("boxNo:" + boxNo);
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, deviceId);
			rs = ps.executeQuery();
			if (rs.next()) {
				String boxid = rs.getString("model");
				return boxid;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return "";
	}

	public List<BoxUse> getBoxUses(String hospital) {
		ResultSet rs = null;
		ResultSet rsUse = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<BoxUse> boxUses = new ArrayList<BoxUse>();

		String sql = "select b.id id,model boxNo,transferStatus from box b,hospital h where b.hosp_id = h.hospitalid and h.`name` = ?  and b.model<>'99999' order by b.model+0";

		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, hospital);
			rs = ps.executeQuery();
			while (rs.next()) {
				BoxUse boxUse = new BoxUse();
				boxUse.setBoxId(rs.getInt("id"));
				boxUse.setBoxNo(rs.getString("boxNo"));
				boxUse.setTransferStatus(rs.getString("transferStatus"));
				if ("free".equals(boxUse.getTransferStatus())) {
					boxUse.setStatus("");
					// boxUse.setBoxNo(rs.getString("boxNo"));
				} else {
					boxUse.setStatus("使用中");
					// boxUse.setBoxNo(rs.getString("boxNo")+" 使用中");
				}
				
				
				sql = "SELECT t.transferNumber organSeg FROM transfer t ,box b WHERE t.box_id = b.boxid AND b.model = ? AND t.`status`='transfering'";
				ps = conn.prepareStatement(sql);
				ps.setString(1, boxUse.getBoxNo());
				rsUse = ps.executeQuery();
				if (rsUse.next()) {
					String organSeg = rsUse.getString("organSeg");
					if (organSeg.contains("AP")) {
						boxUse.setStatus("自动转运中");
						boxUse.setTransferStatus("free");
					}
				} 
				
		

				boxUses.add(boxUse);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsUse, ps, conn);
		}
		return boxUses;
	}

	public List<TransferRecord> test(String transfer_id, String time,String method) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		List<TransferRecord> transferRecords = new ArrayList<TransferRecord>();

		// String sql =
		// "select b.id id,model boxNo,transferStatus from box b,hospital h where b.hosp_id = h.hospitalid and h.`name` = ?  and b.model<>'99999' order by b.model+0";

		// 调用SQL
		try {

			String sql = "select tr.id,tr.transferRecordid,tr.transfer_id,tr.type,tr.currentCity,tr.distance,tr.duration,tr.remark,tr.longitude,tr.latitude,tr.temperature,tr.avgTemperature,tr.power,tr.expendPower,tr.humidity,tr.recordAt,tr.dbStatus,tr.createAt,tr.modifyAt,tr.press1,tr.press2,tr.flow1,tr.flow2,tr.pupple,tr.collision,tr.open from transferRecord tr,transfer t where tr.transfer_id = t.id  and t.id = ?     ";
			// 调用SQL

			ps = conn.prepareStatement(sql);
			ps.setString(1, method);

			rs = ps.executeQuery();
			while (rs.next()) {
				TransferRecord t = new TransferRecord();
				t.setTransfer_id(transfer_id);
				// t.setTransferRecordId(1756);
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
				String recordAt = rs.getString("recordAt");
				String re = time + " " + recordAt.split(" ")[1];
				t.setRecordAt(re);

				t.setRemark(rs.getString("remark"));

				t.setTemperature(rs.getString("temperature"));

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

			for (int i = 0; i < transferRecords.size(); i++) {
				// System.out.println(transferRecords.get(i).getRecordAt()+","+transferRecords.get(i).getTransfer_id());
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return transferRecords;
	}

	public String insertTest(TransferRecord t) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "insert into transferRecord"
				+ "(transfer_id,createAt,currentCity,dbStatus,distance,duration,expendPower,flow1,flow2,humidity,latitude,longitude,"
				+ "modifyAt,power,press1,press2,recordAt,remark,temperature,type,pupple,collision,open) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, t.getTransfer_id());
			ps.setString(2, t.getCreateAt());

			ps.setString(3, t.getCurrentCity());
			ps.setString(4, t.getDbStatus());

			ps.setString(5, t.getDistance());
			ps.setString(6, t.getDuration());
			ps.setString(7, t.getExpendPower());
			ps.setString(8, t.getFlow1());
			ps.setString(9, t.getFlow2());
			ps.setString(10, t.getHumidity());
			ps.setString(11, t.getLatitude());
			ps.setString(12, t.getLongitude());
			ps.setString(13, t.getModifyAt());
			ps.setString(14, t.getPower());
			ps.setString(15, t.getPress1());
			ps.setString(16, t.getPress2());

			ps.setString(17, t.getRecordAt());

			ps.setString(18, t.getRemark());

			ps.setString(19, t.getTemperature());

			ps.setString(20, t.getType());
			ps.setString(21, t.getPupple());
			ps.setString(22, t.getCollision());
			ps.setString(23, t.getOpen());
			ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage());

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return "";
	}

	public String getHospId(String boxNo) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select hosp_id from box where model = ? ";
		//System.out.println("boxNo:" + boxNo);
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, boxNo);
			rs = ps.executeQuery();
			if (rs.next()) {
				String hosp_id = rs.getString("hosp_id");
				return hosp_id;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return "";
	}

	public int updateBoxStatus(String boxNo, String transferStatus) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int update = 0;
		String sql = "update box set transferStatus = ?  where model = ? ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, transferStatus);
			ps.setString(2, boxNo);
			update = ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return update;
	}

	// public static void main(String[] args) {
	// ConnectionDB connDB = new ConnectionDB();
	//
	// // 创建数据库连接对象
	// Connection conn = null;
	// ResultSet rs = null;
	// PreparedStatement ps = null;
	// conn = connDB.getConnection();
	// List<OpoInfo> opoInfos = new ArrayList<OpoInfo>();
	//		 
	// // 调用SQL
	// try {
	// String sql = "select id,contactName,contactPhone from opo_info";
	//	
	//
	// ps = conn.prepareStatement(sql);
	// rs = ps.executeQuery();
	// while(rs.next()){
	// OpoInfo opoInfo = new OpoInfo();
	// opoInfo.setId(rs.getInt("id"));
	// opoInfo.setContactName(rs.getString("contactName"));
	// opoInfo.setContactPhone(rs.getString("contactPhone"));
	// opoInfos.add(opoInfo);
	// }
	// for(int i=0;i<opoInfos.size()/2;i++){
	// sql =
	// "insert into opo_info_contact(opoInfoId,contactName,contactPhone) values(?,?,?)";
	// ps = conn.prepareStatement(sql);
	// ps.setInt(1, opoInfos.get(i).getId());
	// ps.setString(2, opoInfos.get(i).getContactName());
	// ps.setString(3, opoInfos.get(i).getContactPhone());
	// ps.executeUpdate();
	// }
	//			
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	//
	// } finally {
	// connDB.closeAll(rs, ps, conn);
	// }
	// }
}
