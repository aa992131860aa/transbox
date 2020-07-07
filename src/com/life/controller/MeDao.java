package com.life.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.life.utils.CommUtils;
import com.life.utils.ConnectionDB;

public class MeDao {
	private ConnectionDB connDB = new ConnectionDB();

	// 创建数据库连接对象
	private Connection conn = null;
	public int insertFeedback(String phone,String content) {
		ResultSet rs = null;
		int result = 0;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = "insert into feedback(phone,content,createTime) values(?,?,?)";
		
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			ps.setString(2, content);
			ps.setString(3, sdf.format(new Date()));
			result = ps.executeUpdate();
			


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CommUtils.insertTransboxErrorFile("insertFeedback="+e.getMessage()+"=参数phone:"+phone+",content:"+content);

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return result;
	}
}
