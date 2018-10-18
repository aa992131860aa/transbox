package com.life.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.life.utils.ConnectionDB;
import com.mysql.jdbc.Statement;

public class OrganDao {
	private ConnectionDB connDB = new ConnectionDB();

	// 创建数据库连接对象
	private Connection conn = null;

	public void insert(String content) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "insert into repeat_num(content) values(?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, content);
		
			
			ps.executeUpdate();
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		connDB.closeAll(rs, ps, conn);
	}

	public static void main(String[] args) {
		for(int i=10000;i<=99999;i++){
		new OrganDao().insert(i+"");
		}
	}
}
