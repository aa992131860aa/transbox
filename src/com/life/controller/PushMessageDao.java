package com.life.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.life.entity.ContactPerson;

import com.life.utils.ConnectionDB;

public class PushMessageDao {
	// 创建数据库连接对象
	private Connection conn = null;
	private ConnectionDB connDB = new ConnectionDB();

	/**
	 * 插入推送信息
	 * 
	 * @param phone 本人的电话
	 * @param content 推送的内容
	 * @return type  add(添加好友) system(系统消息) finish(添加好友完成)
	 */
	public int insertPush(String content,String time,String phone,String type,String otherId,String pushPhone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
        
		String sql = "insert into push(content,create_time,phone,type,other_id,push_phone) values(?,?,?,?,?,?)";
		// 调用SQL
		try {
       
			ps = conn.prepareStatement(sql);
			ps.setString(1, content);
			ps.setString(2, time);
			ps.setString(3, phone);
			ps.setString(4, type);
			ps.setString(5, otherId);
			ps.setString(6, pushPhone);
		  
		    return  ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}
	public static void main(String[] args) {
		String [] phones = {"asdf","sdf"};
		new PushMessageDao().insertExceptionPush("1", "2", "3", "4", phones);
	}
	/**
	 * 插入推送信息
	 * 
	 * @param
	 * @param content 推送的内容
	 * @return type  add(添加好友) system(系统消息) finish(添加好友完成)
	 */
	public int insertExceptionPush(String content,String time,String type,String transferId,String [] phoneArr) {
		
		 if(phoneArr.length==0){
        	 return 0;
         }
		 
		 
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
        
		String sql = "insert into push(content,create_time,type,transfer_id,push_phone) values";
		for(int i=0;i<phoneArr.length;i++){
			sql += "('"+content+"','"+time+"','"+type+"','"+transferId+"','"+phoneArr[i]+"')";
			if(i!=phoneArr.length-1){
			sql += ",";
			}
		}

		
		// 调用SQL
		try {
       
			ps = conn.prepareStatement(sql);
//			ps.setString(1, content);
//			ps.setString(2, time);
//			ps.setString(3, type);
//			ps.setString(4, transferId);
		  
		    return  ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}
	
	/**
	 * 清除推送消息
	 * 
	 * @param user_info_id  
	 */
	public int clearUnreadPushMessageNum(String user_info_id) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
        
		String sql = "update users set read_push_position = (select max(id) from push ) where phone = ?";
		// 调用SQL
		try {

       
			ps = conn.prepareStatement(sql);
			ps.setString(1, user_info_id);
		  
		    return  ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}
}
