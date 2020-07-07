package com.life.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.life.entity.BoxHosp;
import com.life.utils.ConnectionDB;


public class QrImageDao {
	ConnectionDB connDB = new ConnectionDB();
	Connection conn;
	/**
	 * 根据箱号获取医院信息
	 */
	public BoxHosp  getBoxHosp(String deviceId){
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		BoxHosp boxHosp = null;

		String sql = "select h.name,b.boxid,b.hosp_id,b.model boxNo from box b,hospital h where h.hospitalid = b.hosp_id and b.deviceId = ?";
		// 调用SQL
		try {
			boxHosp = new BoxHosp();
			ps = conn.prepareStatement(sql);
			ps.setString(1, deviceId);
		
			 rs = ps.executeQuery();
			 while(rs.next()){
				 boxHosp.setBoxId(rs.getString("boxid"));
				 boxHosp.setHospitalName(rs.getString("name"));
				 boxHosp.setHospitalId(rs.getString("hosp_id"));
				 boxHosp.setBoxNo(rs.getString("boxNo"));

			 }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();


		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return boxHosp;
		
	}
}
