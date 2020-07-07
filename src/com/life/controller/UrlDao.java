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

public class UrlDao {
	private ConnectionDB connDB = new ConnectionDB();

	// 创建数据库连接对象
	private Connection conn = null;

 
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
	/**
	 * 
	 * @param fromUserName
	 * @return
	 */
	public int updateOrInsertWechatInfo(String fromUserName,String toUserName,String msgType,String content,String unionId,String jsonInfo){
		
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int update = 0;
		String sql = "SELECT id FROM wechat_info WHERE from_user_name=?";
		
		// 调用SQL
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, fromUserName);
			rs = ps.executeQuery();
			//存在用户信息,更新
			if(rs.next()){
				sql = "UPDATE wechat_info SET to_user_name=?,msg_type=?,content=?,union_id=?,json_info=? WHERE from_user_name=?";
				ps=conn.prepareStatement(sql);
				ps.setString(1, toUserName);
				ps.setString(2, msgType);
				ps.setString(3, content);
				ps.setString(4, unionId);
				ps.setString(5, jsonInfo);
				ps.setString(6, fromUserName);
				ps.executeUpdate();
				
			}
			//插入
			else{
				sql = "INSERT INTO wechat_info(to_user_name,msg_type,content,union_id,json_info,from_user_name) VALUES(?,?,?,?,?,?)";
				ps=conn.prepareStatement(sql);
				ps.setString(1, toUserName);
				ps.setString(2, msgType);
				ps.setString(3, content);
				ps.setString(4, unionId);
				ps.setString(5, jsonInfo);
				ps.setString(6, fromUserName);
				ps.executeUpdate();
			}
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return update;
	}
	public boolean isExistWechatInfo(String fromUserName) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "SELECT id FROM wechat_info WHERE from_user_name=? AND union_id<>''";
		boolean isTrue =false;
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, fromUserName);
			rs = ps.executeQuery();
			if(rs.next()){
				isTrue = true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return isTrue;
	}
	
	public String getOTQCOpenIdByPhone(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String openId="";
		String sql = "SELECT weixin_openid union_id FROM users WHERE phone=? AND weixin_openid<>''";
		boolean isTrue =false;
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, phone);
			rs = ps.executeQuery();
			if(rs.next()){
				openId = rs.getString("union_id");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return openId;
	}
	
	public int updateUnionIdByPhone(String phone,String unionId) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int update = 0;
		String sql = "update users set union_id = ?  where phone = ? ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, unionId);
			ps.setString(2, phone);
			update = ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return update;
	}
	 
}
