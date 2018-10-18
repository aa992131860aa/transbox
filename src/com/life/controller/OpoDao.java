package com.life.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.life.entity.OpoInfo;
import com.life.entity.OpoInfoContact;
import com.life.entity.PostRole;
import com.life.utils.ConnectionDB;
import com.mysql.jdbc.Statement;

public class OpoDao {
	private ConnectionDB connDB = new ConnectionDB();

	// 创建数据库连接对象
	private Connection conn = null;


	public List<String> getProvices() {
		List<String> lists = new ArrayList<String>();
		ResultSet rs = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "select provice from hospital where provice is not null and provice <> '' group by provice";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String city = rs.getString("provice");
				lists.add(city);

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
	public List<PostRole> getPostRoles() {
		List<PostRole> postRoles = new ArrayList<PostRole>();
		ResultSet rs = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "select id,postRole from postRole";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				PostRole postRole = new PostRole();
		
				postRole.setPostRoleId(rs.getInt("id"));
				postRole.setPostRole(rs.getString("postRole"));
				postRoles.add(postRole);

			}
			return postRoles;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return postRoles;
		} finally {
			connDB.closeAll(rs, ps, conn);

		}
	}
	
	public void dealPostRole(String organSeg,String phone,int postRoleId) {
		
		ResultSet rs = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "select id from postRolePerson where organSeg=? and phone=? ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			ps.setString(2, phone);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
             //存在为修改
				sql = "update postRolePerson set postRoleId=? where organSeg=? and phone=?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, postRoleId);
				ps.setString(2, organSeg);
				ps.setString(3, phone);
				ps.executeUpdate();

			}else{
			 //不存在为插入
				sql = "insert into postRolePerson(organSeg,phone,postRoleId) values(?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, organSeg);
				ps.setString(2, phone);
				ps.setInt(3, postRoleId);
				ps.executeUpdate();
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		
		} finally {
			connDB.closeAll(rs, ps, conn);

		}
	}
	
	public OpoInfo getOpo(String hospital) {
		OpoInfo opoInfo = null;
		ResultSet rs = null;
		ResultSet rsTwo = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "select o.id,o.`name` from hospital h,opo_info o where h.hospitalid = o.hospitalId  AND h.`name` = ? ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, hospital);
			rs = ps.executeQuery();
			if (rs.next()) {
		        opoInfo = new OpoInfo();
		        opoInfo.setOpoInfoId(rs.getInt("id"));
		        opoInfo.setName(rs.getString("name")+"OPO");
		        
		        sql = "select contactName,contactPhone from opo_info_contact oic where oic.opoInfoId = ?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, rs.getInt("id"));
				rsTwo = ps.executeQuery();
				List<OpoInfoContact> opoInfoContacts = new ArrayList<OpoInfoContact>();
				
				while(rsTwo.next()){
				   OpoInfoContact opoInfoContact = new OpoInfoContact();
				   opoInfoContact.setContactName(rsTwo.getString("contactName"));
				   opoInfoContact.setContactPhone(rsTwo.getString("contactPhone"));
				   opoInfoContacts.add(opoInfoContact);
				
				}
				opoInfo.setOpoInfoContacts(opoInfoContacts);
			}
			return opoInfo;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return opoInfo;
		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);

		}
	}
    
	/**
	 * 获取opo信息
	 * 根据opo获取改opo人员
	 * @param provice
	 * @return
	 */
	public List<OpoInfo> getHospitalByProvice(String provice) {
		List<OpoInfo> lists = new ArrayList<OpoInfo>();
		ResultSet rs = null;
		ResultSet rsTwo = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "select b.id,b.name from hospital h,opo_info b where h.hospitalId=b.hospitalId and h.provice is not null and ? like CONCAT ('%',h.provice,'%')";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, provice);
			rs = ps.executeQuery();
			while (rs.next()) {
				OpoInfo opoInfo = new OpoInfo();
				int id = rs.getInt("id");
				String name = rs.getString("name");	
				opoInfo.setId(id);
				opoInfo.setOpoInfoId(id);
				opoInfo.setName(name+"OPO");
		         
				sql = "select contactName,contactPhone from opo_info_contact oic where oic.opoInfoId = ?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, id);
				rsTwo = ps.executeQuery();
				List<OpoInfoContact> opoInfoContacts = new ArrayList<OpoInfoContact>();
				
				while(rsTwo.next()){
				   OpoInfoContact opoInfoContact = new OpoInfoContact();
				   opoInfoContact.setContactName(rsTwo.getString("contactName"));
				   opoInfoContact.setContactPhone(rsTwo.getString("contactPhone"));
				   opoInfoContacts.add(opoInfoContact);
				
				}
				opoInfo.setOpoInfoContacts(opoInfoContacts);
				
				lists.add(opoInfo);

			}
			return lists;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return lists;
		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsTwo, ps, conn);

		}
	}
	
	public static void main(String[] args) {
      
	}
}
