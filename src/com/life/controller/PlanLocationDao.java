package com.life.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.life.entity.BoxUse;
import com.life.entity.OpoInfo;
import com.life.utils.ConnectionDB;
import com.mysql.jdbc.Statement;

public class PlanLocationDao {
	private ConnectionDB connDB = new ConnectionDB();

	// 创建数据库连接对象
	private Connection conn = null;

	public List<String> getLngLats() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "SELECT latitude,longitude FROM plan_location";
		List<String> list = new ArrayList<String>();

		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(rs.getDouble("longitude") + ","
						+ rs.getDouble("latitude"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return list;
	}

	public boolean insertAddress(double latitude, double longitude,
			String address) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
         boolean isUpdate = false;
		String sql = "UPDATE plan_location SET address = ? WHERE latitude=? AND longitude=?";

		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, address);
			ps.setDouble(2, latitude);
			ps.setDouble(3, longitude);
			int result =  ps.executeUpdate();
            if(result>0){
            	isUpdate = true;
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return isUpdate;
	}

}
