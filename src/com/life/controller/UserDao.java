package com.life.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.life.entity.HospitalZone;
import com.life.entity.OpoInfo;
import com.life.entity.OpoInfoContact;
import com.life.entity.PdfInfo;
import com.life.entity.Push;
import com.life.entity.Role;
import com.life.entity.RongUser;
import com.life.entity.UserInfo;
import com.life.entity.UserSitePush;
import com.life.entity.Users;
import com.life.utils.CONST;
import com.life.utils.ConnectionDB;
import com.life.utils.HMACSHA256;
import com.mysql.jdbc.Statement;

public class UserDao {
	private ConnectionDB connDB = new ConnectionDB();

	// 创建数据库连接对象
	private Connection conn = null;

	/**
	 * 获取角色
	 */
	public List<PdfInfo> getPdfInfoList(String phone, int page, int pageSize) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<PdfInfo> pdfInfos = new ArrayList<PdfInfo>();

		String sql = "select id,url,phone,organSeg,createTime,pdfSize,organ from pdfInfo where phone=? order by createTime desc limit ?,?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			ps.setInt(2, page * pageSize);
			ps.setInt(3, pageSize);
			rs = ps.executeQuery();
			while (rs.next()) {
				PdfInfo pdfInfo = new PdfInfo();
				pdfInfo.setPdfInfoId(rs.getInt("id"));
				pdfInfo.setUrl(rs.getString("url"));
				pdfInfo.setPhone(rs.getString("phone"));
				pdfInfo.setOrganSeg(rs.getString("organSeg"));
				pdfInfo.setCreateTime(rs.getString("createTime"));
				pdfInfo.setPdfSize(rs.getInt("pdfSize"));
				pdfInfo.setOrgan(rs.getString("organ"));
				pdfInfos.add(pdfInfo);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return pdfInfos;
	}
	/**
	 * 根据手机修改信息
	 */
	public UserInfo checkOpenId(String openId) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int result = 0;
		UserInfo user = null;

		String sql = "select token,phone,true_name,is_upload_photo,photo_url,wechat_url,open_id,role_id,r.roleName,h.`name`  from users u,role r,hospital h  where u.role_id = r.id and u.hospital_id = h.hospitalid and  open_id = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, openId);
			 
			rs = ps.executeQuery();
			if(rs.next()){
				 user = new UserInfo();
				 user.setPhone(rs.getString("phone"));
				 user.setTrueName(rs.getString("true_name"));
				 user.setIsUploadPhoto(rs.getString("is_upload_photo"));
				 user.setPhotoFile(rs.getString("photo_url"));
				 user.setWechatUrl(rs.getString("wechat_url"));
				 user.setOpenId(rs.getString("open_id"));
				 user.setRoleId(rs.getInt("role_id"));
				 user.setRoleName(rs.getString("roleName"));
				 user.setHospitalName(rs.getString("name"));
				 user.setToken(rs.getString("token"));
				 
				 
				 return user;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		 return user;
	}
	

	/**
	 * 插入pdf 1.判断是否存在记录 2.存在就更新,不存在就插入(查询用户id)
	 */
	public int insertPdf(String url, String organSeg, String phone,
			int pdfSize, String organ) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int result = 0;
		// 判断是否重复
		String sql = "select url from pdfInfo where organSeg = ? and phone = ?";

		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, organSeg);
			ps.setString(2, phone);
			rs = ps.executeQuery();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date());
			// 存在
			if (rs.next()) {
				sql = "update pdfInfo set createTime = ?,url = ?,pdfSize=? where phone=? and organSeg = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, createTime);
				ps.setString(2, url);
				ps.setInt(3, pdfSize);
				ps.setString(4, phone);
				ps.setString(5, organSeg);
				ps.executeUpdate();

			}
			// 不存在
			else {

				sql = "insert into pdfInfo(url,organSeg,phone,createTime,pdfSize,organ) values(?,?,?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, url);
				ps.setString(2, organSeg);
				ps.setString(3, phone);
				ps.setString(4, createTime);
				ps.setInt(5, pdfSize);
				ps.setString(6, organ);
				ps.executeUpdate();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return result;
	}

	/**
	 * 获取角色
	 */
	public List<Role> getRoleList() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<Role> roles = new ArrayList<Role>();

		String sql = "select id,roleName from role";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Role role = new Role();
				role.setRoleId(rs.getInt("id"));
				role.setRoleName(rs.getString("roleName"));
				roles.add(role);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return roles;
	}

	/**
	 * 根据手机获取角色信息
	 */
	public Role getRoleInfo(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		Role role = null;

		String sql = "select r.id,r.roleName from users u ,role r where u.role_id = r.id and u.phone=?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			rs = ps.executeQuery();
			while (rs.next()) {
				role = new Role();
				role.setRoleId(rs.getInt("id"));
				role.setRoleName(rs.getString("roleName"));

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return role;
	}

	/**
	 * 根据手机修改信息
	 */
	public int updateByPhone(String phone, int isUpdate) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int result = 0;

		String sql = "update users set is_update = ? where phone = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setInt(1, isUpdate);
			ps.setString(2, phone);
			result = ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return result;
	}

	/**
	 * 根据手机修改信息
	 */
	public int setPushSite(String phone, String type, int status) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "";
		int result = -1;

		if ("temperature".equals(type)) {
			sql = "update userPushSite set temperatureStatus = ? where userId = (select id from users where phone=?)";
		} else if ("open".equals(type)) {
			sql = "update userPushSite set openStatus = ? where userId = (select id from users where phone=?)";
		} else if ("collision".equals(type)) {
			sql = "update userPushSite set collisionStatus = ? where userId = (select id from users where phone=?)";
		}

		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setInt(1, status);
			ps.setString(2, phone);
			result = ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return result;
	}
	
	public UserSitePush getSite(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
	    UserSitePush usp = null;

		String sql = "SELECT temperatureStatus,openStatus,collisionStatus FROM users u,userPushSite ups WHERE u.id = ups.userId AND u.phone=?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			rs = ps.executeQuery();
			if(rs.next()){
				usp = new UserSitePush();
				usp.setCollisionStatus(rs.getInt("collisionStatus"));
				usp.setTemperatureStatus(rs.getInt("temperatureStatus"));
				usp.setOpenStatus(rs.getInt("openStatus"));
				
			}else{
				sql = "INSERT INTO userPushSite(userId,temperatureStatus,openStatus,collisionStatus) VALUES((SELECT id FROM users WHERE phone=?),0,0,0)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, phone);
				ps.executeUpdate();
			}
           

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return usp;
	}
	

	/**
	 * 插入用户(手机)
	 * 
	 * @param phone
	 * @return
	 */
	public int insertUserPhone(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "insert into users(phone,create_time) values(?,?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date());

			ps.setString(2, createTime);

			ps.executeUpdate();

			// 插入推送设置
			sql = "insert into userPushSite(userId)  select id from users where phone=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}

	/**
	 * 获取所以的用户姓名和电话
	 */
	public List<Users> getUsersList() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<Users> userses = new ArrayList<Users>();

		String sql = "select * from users";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				Users users = new Users();
				users.setTrueName(rs.getString("true_name"));
				users.setPhone(rs.getString("phone"));
				userses.add(users);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return userses;
	}
	/**
	 * 获取所以的用户姓名和电话
	 */
	public List<RongUser> getUsersListByPhoneHospital(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<RongUser> userses = new ArrayList<RongUser>();
		
		String sql = "SELECT phone,true_name,is_upload_photo,photo_url,wechat_url from users where hospital_id = (SELECT hospital_id FROM users where phone = ?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			rs = ps.executeQuery();
			while (rs.next()) {
				RongUser users = new RongUser();
				users.setName(rs.getString("true_name"));
				users.setPhone(rs.getString("phone"));
				String flag = rs.getString("is_upload_photo");
				if("0".equals(flag)){
					users.setUrl(rs.getString("wechat_url"));
				}else{
					users.setUrl(rs.getString("photo_url"));
				}
				 
				userses.add(users);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return userses;
	}

	/**
	 * 获取所以的用户姓名和电话
	 */
	public List<RongUser> getUsersListByPhones(String phones) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<RongUser> userses = new ArrayList<RongUser>();

		String sql = "select phone,true_name,is_upload_photo,photo_url,wechat_url from users where ? like CONCAT('%',phone,'%') ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phones);
			rs = ps.executeQuery();
			while (rs.next()) {
				RongUser users = new RongUser();
				users.setName(rs.getString("true_name"));
				users.setPhone(rs.getString("phone"));
				String flag = rs.getString("is_upload_photo");
				if("0".equals(flag)){
					users.setUrl(rs.getString("wechat_url"));
				}else{
					users.setUrl(rs.getString("photo_url"));
				}
				 
				userses.add(users);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return userses;
	}
	/**
	 * 获取所以的用户姓名和电话 pad 同一医院
	 */
	public List<Users> getUsersPadList(String hospitalName) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<Users> userses = new ArrayList<Users>();

		String sql = "select true_name,phone from users u,hospital h where u.hospital_id = h.hospitalid and h.`name` = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, hospitalName);
			rs = ps.executeQuery();
			while (rs.next()) {
				Users users = new Users();
				users.setTrueName(rs.getString("true_name"));
				users.setPhone(rs.getString("phone"));
				userses.add(users);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return userses;
	}

	/**
	 * 获取所以的用户姓名和电话 pad 同一医院
	 */
	public List<OpoInfo> getOpoPadList(String hospitalName) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<OpoInfo> userses = new ArrayList<OpoInfo>();
		ResultSet rsTwo = null;

		String sql = "select o.`name`,o.contactName,o.contactPhone from hospital h,opo_info o where o.hospitalId=h.hospitalid and h.`name` = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, hospitalName);
			rs = ps.executeQuery();
			while (rs.next()) {
				OpoInfo users = new OpoInfo();
				users.setName(rs.getString("name"));
				users.setContactName(rs.getString("contactName"));
				users.setContactPhone(rs.getString("contactPhone"));
				userses.add(users);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return userses;
	}

	/**
	 * 获取所以的用户姓名和电话 pad 同一医院
	 */
	public List<OpoInfo> getOposPadList() {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<OpoInfo> opoInfoes = new ArrayList<OpoInfo>();
		ResultSet rsTwo = null;

		String sql = "select o.id,o.`name`,o.contactName,o.contactPhone from opo_info o ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();
			while (rs.next()) {
				OpoInfo opoInfo = new OpoInfo();
				opoInfo.setName(rs.getString("name") + "OPO");
				opoInfo.setContactName(rs.getString("contactName"));
				opoInfo.setContactPhone(rs.getString("contactPhone"));
				opoInfo.setId(rs.getInt("id"));

				sql = "select contactName,contactPhone from opo_info_contact oic where oic.opoInfoId = ?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, rs.getInt("id"));
				rsTwo = ps.executeQuery();
				List<OpoInfoContact> opoInfoContacts = new ArrayList<OpoInfoContact>();
				while (rsTwo.next()) {
					OpoInfoContact opoInfoContact = new OpoInfoContact();
					opoInfoContact.setContactName(rsTwo
							.getString("contactName"));
					opoInfoContact.setContactPhone(rsTwo
							.getString("contactPhone"));
					opoInfoContacts.add(opoInfoContact);

				}
				opoInfo.setOpoInfoContacts(opoInfoContacts);

				opoInfoes.add(opoInfo);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return opoInfoes;
	}

	/**
	 * 是否认证手机号码
	 * 
	 * @param phone
	 * @return
	 */
	public boolean isExistPhoneConfirm(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select phone from users where phone = ? and hospital_id is not null";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);

			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;
	}

	/**
	 * 是否为绑定的手机号码
	 * 
	 * @param phone
	 * @return
	 */
	public boolean isBindPhoneConfirm(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select bind from users where phone = ? ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);

			rs = ps.executeQuery();

			if (rs.next()) {
				if ("1".equals(rs.getString("bind"))) {
					return true;
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;
	}
	

	public Users getUsersById(String id, String organSeg) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		Users users = null;

		String sql = "select u.id,u.bind,u.is_upload_photo,u.phone,u.photo_url,u.true_name,u.wechat_url,h.name from users u,hospital h where u.hospital_id=h.hospitalid and phone = ?";

		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				users = new Users();
				users.setBind(rs.getString("bind"));
				users.setIsUploadPhoto(rs.getString("is_upload_photo"));
				users.setPhone(rs.getString("phone"));
				users.setPhotoFile(rs.getString("photo_url"));
				users.setTrueName(rs.getString("true_name"));
				users.setUsersId(rs.getString("id"));
				users.setWechatUrl(rs.getString("wechat_url"));
				users.setName(rs.getString("name"));

				sql = "select p.postRole from postRolePerson pp,postRole p where p.id = pp.postRoleId and pp.phone=? and pp.organSeg = ?";
				ps = conn.prepareStatement(sql);

				ps.setString(1, id);
				ps.setString(2, organSeg);
				rs = ps.executeQuery();
				if (rs.next()) {
					users.setPostRole(rs.getString("postRole"));
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return users;
	}

	/**
	 * 是否存在手机号码
	 * 
	 * @param phone
	 * @return
	 */
	public boolean isExistPhone(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select phone from users where phone = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);

			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;
	}

	/**
	 * 微信登录插入用户
	 * 
	 * @param wechatName
	 * @param wechatUrl
	 * @return
	 */
	public int insertUserWechat(String wechatName, String wechatUrl,
			String wechatUuid) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "insert into users(wechat_name,wechat_url,wechat_uuid) values(?,?,?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, wechatName);
			ps.setString(2, wechatUrl);
			ps.setString(3, wechatUuid);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 微信登录插入用户 UUID
	 * 
	 * 
	 * @param UUID
	 * @return
	 */
	public int insertUserWechat(String wechatUuid) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "insert into users(wechat_uuid) values(?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, wechatUuid);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 获取城市 医院名称的集合
	 * 
	 * @return
	 */
	public List<HospitalZone> getHospitalZone() {
		List<HospitalZone> hospitalZones = new ArrayList<HospitalZone>();
		ResultSet rs = null;
		ResultSet rsCity = null;
		PreparedStatement ps = null;
		PreparedStatement psCity = null;
		conn = connDB.getConnection();

		String sql = "select provice from hospital where provice is not null group by city";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String city = rs.getString("provice");
				// List<String> lists = getHospitalNameByCity(city);
				List<String> lists = new ArrayList<String>();
				String sqlCity = "select name from hospital where provice = ?";
				psCity = conn.prepareStatement(sqlCity);
				psCity.setString(1, city);
				rsCity = psCity.executeQuery();
				while (rsCity.next()) {
					lists.add(rsCity.getString("name"));
				}
				HospitalZone hospitalZone = new HospitalZone();
				hospitalZone.setZone(city);
				hospitalZone.setHospitalNames(lists);
				hospitalZones.add(hospitalZone);

			}
			return hospitalZones;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return hospitalZones;
		} finally {
			connDB.closeAll(rs, ps, conn);
			connDB.closeAll(rsCity, psCity, null);
		}
	}

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

	public List<String> getHospitalByProvice(String provice) {
		List<String> lists = new ArrayList<String>();
		ResultSet rs = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "select name from hospital where provice is not null and ? like CONCAT ('%',provice,'%') and name <> 'default'";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, provice);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				String hospital = rs.getString("name");
				lists.add(hospital);

			}
			System.out.println(sql+":"+provice+",size:"+lists.size());
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
	 * 根据城市获取医院名称
	 * 
	 * @param city
	 * @return
	 */
	public List<String> getHospitalNameByCity(String city) {
		List<String> lists = new ArrayList<String>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select name from hospital where city = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, city);
			rs = ps.executeQuery();
			while (rs.next()) {
				lists.add(rs.getString("name"));
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
	 * 是否存在医院,存在就获取医院id
	 * 
	 * @param hospital
	 * @return
	 */
	public String isExistHospital(String hospital) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select hospitalid from hospital where name = ?";

		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, hospital);

			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("hospitalid");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return "";
	}

	/**
	 * 
	 * 判断是否存在绑定 是否存在医院并且该医院是否允许注册,存在就获取医院id bind 1 未后台注册(要重新填写医院) 0 手机注册
	 * 
	 * @param hospital
	 * @return
	 */
	public String isExistHospitalRegister(String hospital, String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		String bind = "0";
		conn = connDB.getConnection();
		String sql = "select bind from users where phone = ? ";

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);

			rs = ps.executeQuery();

			if (rs.next()) {
				bind = rs.getString("bind");
			}

			sql = "select hospitalid from hospital where name = ? ";
			
			if ("0".equals(bind)) {
				sql +=" and register = 1 ";
			}  
			
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, hospital);
			
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("hospitalid");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return "";
	}

	/**
	 * 是否认证微信
	 * 
	 * @param hospital
	 * @return
	 */
	public boolean isExistWechatConfirm(String uuid) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select wechat_uuid from users where wechat_uuid = ? and hospital_id is not null";

		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);

			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;
	}

	/**
	 * 是否存在微信
	 * 
	 * @param hospital
	 * @return
	 */
	public boolean isExistWechat(String uuid) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "select wechat_uuid from users where wechat_uuid = ?";

		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);

			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;
	}

	/**
	 * 插入用户,手机,真实姓名,医院
	 * 
	 * @param phone
	 * @param trueName
	 * @param hospital
	 * @return
	 */
	public int insertUserConfirmByPhone(String phone, String trueName,
			String hospital, String roleId) {
		ResultSet rs = null;
		PreparedStatement ps = null;

		String hospitalId = isExistHospitalRegister(hospital,phone);
		conn = connDB.getConnection();
		// 医院不存在
		if ("".equals(hospitalId)) {
			return -1;
		}
		//System.out.println("hospitalId:" + hospitalId);
		// String photo_url = CONST.URL_PATH+"images/"+phone+".jpg";
		String sql = "insert into users(true_name,hospital_id,phone,role_id,create_time) values(?,?,?,?,?) ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, trueName);
			ps.setString(2, hospitalId);
			ps.setString(3, phone);
			ps.setInt(4, Integer.parseInt(roleId));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date());

			ps.setString(5, createTime);
			//System.out.println("isOK:gg");
			ps.executeUpdate();

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 插入用户,手机,真实姓名,医院
	 * 
	 * @param phone
	 * @param trueName
	 * @param hospital
	 * @return
	 */
	public int updateUserConfirmByPhone(String phone, String trueName,
			String hospital, String roleId) {
		ResultSet rs = null;
		PreparedStatement ps = null;

		String hospitalId = isExistHospitalRegister(hospital,phone);
		conn = connDB.getConnection();
		// 医院不存在
		if ("".equals(hospitalId)) {
			return -1;
		}
		//System.out.println("hospitalId:" + hospitalId);
		// String photo_url = CONST.URL_PATH+"images/"+phone+".jpg";
		String sql = "update users set true_name=?,hospital_id=?,role_id=? where phone = ? ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, trueName);
			ps.setString(2, hospitalId);
			ps.setInt(3, Integer.parseInt(roleId));
			ps.setString(4, phone);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 第一次认证的时候,更新系统消息位置
	 * 
	 * @param phone
	 * @return
	 */
	public int updatePushPostionAndFirst(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "update users set read_push_position = (select max(id) id from push),read_push_position_first = (select max(id) id from push) where phone = ? ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, phone);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 更新系统消息位置
	 * 
	 * @param phone
	 * @return
	 */
	public int updatePushPostion(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "UPDATE push SET status = 1 WHERE push_phone = ? ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, phone);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 根据内容删除系统消息
	 * 
	 * @param phone
	 * @return
	 */
	public int deletePush(int id) {
		ResultSet rs = null;
		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "UPDATE  push SET del = 1 where id = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setInt(1, id);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 插入删除的系统消息的电话
	 * 
	 * @param phone
	 * @return
	 */
	public int insertPushDel(int pushId, String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "insert into pushDel(pushId,phone,createTime) values(?,?,?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date());
			ps.setInt(1, pushId);
			ps.setString(2, phone);
			ps.setString(3, createTime);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 插入用户,手机,真实姓名,医院
	 * 
	 * @param phone
	 * @param trueName
	 * @param hospital
	 * @return
	 */
	public int insertUserConfirmByWechat(String phone, String trueName,
			String hospital, String wechatUuid, String wechatName,
			String wechatUrl, String roleId,String openId) {
		ResultSet rs = null;
		PreparedStatement ps = null;

		String hospitalId = isExistHospitalRegister(hospital,phone);
		conn = connDB.getConnection();
		// 医院不存在
		if ("".equals(hospitalId)) {
			return -1;
		}

		//System.out.println("come in");
		String sql = "insert into users(true_name,hospital_id,phone,wechat_name,wechat_url,wechat_uuid,role_id,create_time,open_id) values(?,?,?,?,?,?,?,?,?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, trueName);
			ps.setString(2, hospitalId);
			ps.setString(3, phone);
			ps.setString(4, wechatName);
			ps.setString(5, wechatUrl);
			ps.setString(6, wechatUuid);
			ps.setInt(7, Integer.parseInt(roleId));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = sdf.format(new Date());
			ps.setString(8, createTime);
			ps.setString(9, openId);
			ps.executeUpdate();

			// 插入推送设置
			sql = "insert into userPushSite(userId)  select id from users where phone=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage());
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 插入用户,手机,真实姓名,医院
	 * 
	 * @param phone
	 * @param trueName
	 * @param hospital
	 * @return
	 */
	public int updateUserConfirmByWechatPhone(String phone, String trueName,
			String hospital, String wechatUuid, String wechatName,
			String wechatUrl, String roleId,String openId) {
		ResultSet rs = null;
		PreparedStatement ps = null;

		String hospitalId = isExistHospitalRegister(hospital,phone);
		conn = connDB.getConnection();
		// 医院不存在
		if ("".equals(hospitalId)) {
			return -1;
		}

		String sql = "update users set true_name=?,hospital_id=?,wechat_uuid=?,wechat_name=?,wechat_url=?,is_wechat=1,role_id=?,is_upload_photo = 0,open_id=? where phone = ?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, trueName);
			ps.setString(2, hospitalId);
			ps.setString(3, wechatUuid);
			ps.setString(4, wechatName);
			ps.setString(5, wechatUrl);
			ps.setInt(6, Integer.parseInt(roleId));
			ps.setString(7, openId);
			ps.setString(8, phone);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	public int deletePhoneNoWechat(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "delete from users where phone = ? and wechat_uuid is null";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, phone);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
	}

	/**
	 * 插入手机图片
	 * 
	 * @param hospital
	 * @return
	 */
	public int updatePhoneUrl(String path, String phone, String flag) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "update users set photo_url=?,is_upload_photo=? where phone = ?";

		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, path);
			ps.setString(2, flag);
			ps.setString(3, phone);
			//System.out.println("path:" + path + ",phone:" + phone + ",flag:" + flag);
			//System.out.println("sql:" + sql);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}

	/**
	 * 修改个人图片
	 * 
	 * @param hospital
	 * @return
	 */
	public int updatePhoneUrlBind(String phone, String bind) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		String sql = "update users set bind=? where phone = ?";

		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, bind);
			ps.setString(2, phone);
			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}

	/**
	 * 根据phone获取id
	 * 
	 * @param phone
	 * @return
	 */
	public int getUserIdByPhone(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int id = 0;

		String sql = "select id from users where phone = ?";

		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, phone);
			rs = ps.executeQuery();
			if (rs.next()) {
				id = rs.getInt("id");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return id;

	}

	public Map<String, String> getUserByPhoneAndContactPhone(String phone,
			String contactPhone) {
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		ResultSet rs = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();
		Map<String, String> userMap = new HashMap<String, String>();
		String sql = "select * from (select u.id,true_name,wechat_name,wechat_url,photo_url,token,is_upload_photo,bind,h.name hospital_name from users u,hospital h where phone = ? and u.hospital_id = h.hospitalid) a,(select users_other_id from contact c where c.users_id = (select id from users u where u.phone = ?)) b ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, contactPhone);
			ps.setString(2, phone);

			rs = ps.executeQuery();
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("usersId", rs.getInt("id") + "");
				map.put("true_name", rs.getString("true_name"));
				map.put("wechat_name", rs.getString("wechat_name"));
				map.put("wechat_url", rs.getString("wechat_url"));
				map.put("photo_url", rs.getString("photo_url"));
				map.put("token", rs.getString("token"));
				map.put("is_upload_photo", rs.getString("is_upload_photo"));
				map.put("bind", rs.getString("bind"));
				map.put("hospital_name", rs.getString("hospital_name"));
				map.put("phone", contactPhone);
				map.put("usersOtherId", rs.getString("users_other_id"));
				maps.add(map);
			}
			String isFriend = "1";
			for (int i = 0; i < maps.size(); i++) {
				String usersId = maps.get(i).get("usersId");
				String usersOtherId = maps.get(i).get("usersOtherId");
				if (usersId.equals(usersOtherId)) {
					isFriend = "0";
					break;
				}

			}
			if (maps.size() >= 1) {
				userMap = maps.get(0);
				userMap.put("isFriend", isFriend);
			}

			return userMap;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println(e.getMessage());
			return userMap;
		} finally {
			connDB.closeAll(rs, ps, conn);

		}
	}

	public int insertLog(String content) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String sql = "insert into log(content,create_time) values(?,?)";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, content);
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
			String time = sdf.format(new Date());
			ps.setString(2, time);

			return ps.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} finally {
			connDB.closeAll(rs, ps, conn);
		}

	}

	public List<Map<String, String>> getUserByPhone(String phone,
			String contactPhone) {
		List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
		ResultSet rs = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "select * from (select u.id,true_name,wechat_name,wechat_url,photo_url,token,is_upload_photo,bind,h.name hospital_name from users u,hospital h where phone = ? and u.hospital_id = h.hospitalid) a,(select users_other_id from contact c where c.users_id = (select id from users u where u.phone = ?)) b ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, contactPhone);
			ps.setString(2, phone);

			rs = ps.executeQuery();
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("usersId", rs.getInt("id") + "");
				map.put("true_name", rs.getString("true_name"));
				map.put("wechat_name", rs.getString("wechat_name"));
				map.put("wechat_url", rs.getString("wechat_url"));
				map.put("photo_url", rs.getString("photo_url"));
				map.put("token", rs.getString("token"));
				map.put("is_upload_photo", rs.getString("is_upload_photo"));
				map.put("bind", rs.getString("bind"));
				map.put("hospital_name", rs.getString("hospital_name"));
				map.put("phone", contactPhone);
				map.put("usersOtherId", rs.getString("users_other_id"));
				maps.add(map);
			}
			return maps;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return maps;
		} finally {
			connDB.closeAll(rs, ps, conn);

		}
	}

	public Map<String, String> getUserByPhone(String phone) {
		Map<String, String> map = new HashMap<String, String>();
		ResultSet rs = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "select u.id,true_name,wechat_name,wechat_url,photo_url,token,is_upload_photo,bind,h.name hospital_name,u.is_update,r.roleName,u.role_id,u.create_time,ups.temperatureStatus,ups.openStatus,ups.collisionStatus from users u,hospital h,role r,userPushSite ups where u.id=ups.userId and u.role_id = r.id and phone = ? and u.hospital_id = h.hospitalid ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			rs = ps.executeQuery();
			while (rs.next()) {
				map.put("usersId", rs.getInt("id") + "");
				map.put("true_name", rs.getString("true_name"));
				map.put("wechat_name", rs.getString("wechat_name"));
				map.put("wechat_url", rs.getString("wechat_url") == null ? ""
						: rs.getString("wechat_url"));
				map.put("photo_url", rs.getString("photo_url") == null ? ""
						: rs.getString("photo_url"));
				map.put("token", rs.getString("token"));
				map.put("is_upload_photo", rs.getString("is_upload_photo"));
				map.put("bind", rs.getString("bind"));
				map.put("hospital_name", rs.getString("hospital_name"));
				map.put("is_update", rs.getInt("is_update") + "");
				map.put("roleName", rs.getString("roleName"));
				map.put("role_id", rs.getString("role_id"));
				map.put("phone", phone);
				map.put("create_time", rs.getString("create_time"));
				map.put("temperatureStatus", rs.getInt("temperatureStatus")
						+ "");
				map.put("openStatus", rs.getInt("openStatus") + "");
				map.put("collisionStatus", rs.getInt("collisionStatus") + "");

			}
			return map;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return map;
		} finally {
			connDB.closeAll(rs, ps, conn);

		}
	}

	public Map<String, String> getUserByWechatUuid(String wechatUuid) {
		Map<String, String> map = new HashMap<String, String>();
		ResultSet rs = null;

		PreparedStatement ps = null;

		conn = connDB.getConnection();

		String sql = "select phone,true_name,wechat_name,wechat_url,u.create_time,photo_url,token,is_upload_photo,bind,h.name hospital_name,u.role_id,r.roleName,u.is_update,ups.temperatureStatus,ups.openStatus,ups.collisionStatus  from users u,hospital h,role r,userPushSite ups where u.role_id = r.id and  wechat_uuid = ? and u.hospital_id = h.hospitalid and u.id=ups.userId ";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, wechatUuid);
			rs = ps.executeQuery();
			while (rs.next()) {
				map.put("phone", rs.getString("phone"));
				map.put("true_name", rs.getString("true_name"));
				map.put("wechat_name", rs.getString("wechat_name"));
				map.put("wechat_url", rs.getString("wechat_url"));
				map.put("photo_url", rs.getString("photo_url"));
				map.put("token", rs.getString("token"));
				map.put("is_upload_photo", rs.getString("is_upload_photo"));
				map.put("bind", rs.getString("bind"));
				map.put("role_id", rs.getString("role_id"));
				map.put("roleName", rs.getString("roleName"));
				map.put("is_update", rs.getString("is_update"));
				map.put("hospital_name", rs.getString("hospital_name"));
				map.put("create_time", rs.getString("create_time"));
				map.put("temperatureStatus", rs.getInt("temperatureStatus")
						+ "");
				map.put("openStatus", rs.getInt("openStatus") + "");
				map.put("collisionStatus", rs.getInt("collisionStatus") + "");

			}
			return map;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return map;
		} finally {
			connDB.closeAll(rs, ps, conn);

		}
	}

	public String getAddressByHospitalName(String hospitalName) {

		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		String address = "";

		String sql = "select address from hospital where name=?";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, hospitalName);
			rs = ps.executeQuery();
			if (rs.next()) {
				address = rs.getString("address");

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return address;

	}

	public Map<String, Object> getUnreadNumByPhone(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		Map<String, Object> map = new HashMap<String, Object>();
		String sql = "select  count(*) count ,content,DATE_FORMAT(create_time,'%m-%d') create_time from (select  p.id,p.content,p.type,p.create_time from push p,users u where (p.other_id = u.id and u.phone = ? and p.id >(select read_push_position from users where phone = ?)) or (p.other_id is null and p.id>(select read_push_position from users where phone = ?)) GROUP BY p.id ORDER BY p.id desc ) c";
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			ps.setString(2, phone);
			ps.setString(3, phone);
			rs = ps.executeQuery();

			if (rs.next()) {
				map.put("count", rs.getInt("count"));
				map.put("content", rs.getString("content"));
				map.put("createTime", rs.getString("create_time"));

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return map;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return map;

	}

	public Push getUserByPushPhone(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		Push push = null;
		try {

			String sql = "select u.true_name,u.photo_url,u.is_upload_photo,u.wechat_url,h.name from users u,hospital h where u.hospital_id = h.hospitalid and phone = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, phone);
			rs = ps.executeQuery();
			if (rs.next()) {
				push = new Push();
				push.setPhone(phone);
				push.setTrueName(rs.getString("true_name"));
				push.setPhotoFile(rs.getString("photo_url"));
				push.setIsUploadPhoto(rs.getString("is_upload_photo"));
				push.setWechatUrl(rs.getString("wechat_url"));
				push.setHospital(rs.getString("name"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return push;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return push;
	}

	// public List<Push> getPushList(String phone, int page, int pageSize) {
	// ResultSet rs = null;
	// ResultSet rsTwo = null;
	// ResultSet rsThree = null;
	// PreparedStatement ps = null;
	// String pushSql = "";
	// int friendFlag = 0;
	// // 判断是否存在该电话的删除信息
	// int count = 0;
	// conn = connDB.getConnection();
	// List<Push> pushes = new ArrayList<Push>();
	// List<Push> pusheExcept = new ArrayList<Push>();
	// List<Push> pusheFriend = new ArrayList<Push>();
	// try {
	// String pushDel = "SELECT count(id) c FROM pushDel WHERE phone =?";
	// ps = conn.prepareStatement(pushDel);
	// ps.setString(1, phone);
	// rsThree = ps.executeQuery();
	// if (rsThree.next()) {
	// count = rsThree.getInt("c");
	// }
	//
	// if (count > 0) {
	//
	// pushSql =
	// "SELECT c.id,c.phone,c.content,c.type,c.other_id,c.transfer_id,c.create_time from (SELECT * from (select DISTINCT(p.id),p.phone,p.content,p.type,p.other_id,p.transfer_id,DATE_FORMAT(p.create_time,'%m月%d日 %H:%i') create_time from push p ,users u where p.other_id = (select id from users where phone= '"
	// + phone
	// +
	// "' ) and p.phone = u.phone and p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "') or (p.other_id is null and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "')) or (p.type = 'transfer' and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "'))   order by p.create_time desc ) a where   a.phone is not null group by a.phone UNION SELECT * from (select DISTINCT(p.id),p.phone,p.content,p.type,p.other_id,p.transfer_id,DATE_FORMAT(p.create_time,'%m月%d日 %H:%i') create_time  from push p ,users u where p.other_id = (select id from users where phone= '"
	// + phone
	// +
	// "' ) and p.phone = u.phone and p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "') or (p.other_id is null and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "')) or (p.type = 'transfer' and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "'))   order by p.create_time desc ) b where  b.transfer_id is not null ) c,(SELECT * from pushDel where phone ='"
	// + phone
	// +
	// "') p where c.id<>p.pushId GROUP BY c.id HAVING count(c.id)>=(SELECT count(id) from pushDel where phone ='"
	// + phone
	// + "') order by  c.create_time desc limit "
	// + page * pageSize + "," + pageSize;
	// } else {
	//
	// pushSql =
	// "SELECT id,phone,content,type,other_id,transfer_id,create_time from (SELECT * from (select DISTINCT(p.id),p.phone,p.content,p.type,p.other_id,p.transfer_id,DATE_FORMAT(p.create_time,'%m月%d日 %H:%i') create_time from push p ,users u where p.other_id = (select id from users where phone= '"
	// + phone
	// +
	// "' ) and p.phone = u.phone and p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "') or (p.other_id is null and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "')) or (p.type = 'transfer' and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "'))   order by p.create_time desc ) a where   a.phone is not null group by a.phone UNION SELECT * from (select DISTINCT(p.id),p.phone,p.content,p.type,p.other_id,p.transfer_id,DATE_FORMAT(p.create_time,'%m月%d日 %H:%i') create_time  from push p ,users u where p.other_id = (select id from users where phone= '"
	// + phone
	// +
	// "' ) and p.phone = u.phone and p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "') or (p.other_id is null and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "')) or (p.type = 'transfer' and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "'))   order by p.create_time desc ) b where  b.transfer_id is not null ) c order by  c.create_time desc limit "
	// + page * pageSize + "," + pageSize;
	// }
	//
	// // group by b.type,b.transfer_id
	//
	// ps = conn.prepareStatement(pushSql);
	//
	// // ps.setInt(1, page * pageSize);
	// // ps.setInt(2, pageSize);
	// // System.out.println("page:" + page + "," + pageSize);
	// rs = ps.executeQuery();
	//
	// while (rs.next()) {
	//
	// Push push = new Push();
	// push.setId(rs.getInt("id"));
	// push.setPushId(rs.getInt("id"));
	// push.setPhone(rs.getString("phone"));
	// push.setContent(rs.getString("content"));
	// push.setType(rs.getString("type"));
	// push.setOtherId(rs.getString("other_id"));
	// push.setCreateTime(rs.getString("create_time"));
	// String transferId = rs.getString("transfer_id");
	// push.setTransferId(transferId);
	//
	// // 是否存在添加好友消息
	// if (transferId == null) {
	// pusheFriend.add(push);
	// }
	//
	// }
	// System.out.println(pushSql);
	// if (count > 0) {
	// pushSql =
	// " SELECT d.id,d.phone,content,type,other_id,transfer_id,d.create_time from (SELECT c.id,c.phone,c.content,c.type,c.other_id,c.transfer_id,c.create_time from (SELECT * from (select DISTINCT(p.id),p.phone,p.content,p.type,p.other_id,p.transfer_id,DATE_FORMAT(p.create_time,'%m月%d日 %H:%i') create_time from push p ,users u where p.other_id = (select id from users where phone= '"
	// + phone
	// +
	// "' ) and p.phone = u.phone and p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "') or (p.other_id is null and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "')) or (p.type = 'transfer' and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "'))   order by p.create_time desc ) a where   a.phone is not null group by a.phone UNION SELECT * from (select DISTINCT(p.id),p.phone,p.content,p.type,p.other_id,p.transfer_id,DATE_FORMAT(p.create_time,'%m月%d日 %H:%i') create_time  from push p ,users u where p.other_id = (select id from users where phone= '"
	// + phone
	// +
	// "' ) and p.phone = u.phone and p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "') or (p.other_id is null and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "')) or (p.type = 'transfer' and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "'))   order by p.create_time desc ) b where  b.transfer_id is not null ) c,(SELECT * from pushDel where phone ='"
	// + phone
	// +
	// "') p where c.id<>p.pushId GROUP BY c.id HAVING count(c.id)>=(SELECT count(id) from pushDel where phone ='"
	// + phone
	// +
	// "') order by  c.create_time desc ) d , transfer_group_temp tgt,transfer t where t.transferNumber = tgt.organSeg and t.transferid = d.transfer_id and  tgt.usersIds like '%"
	// + phone
	// + "%' order by d.create_time desc limit "
	// + page * pageSize + "," + pageSize;
	// } else {
	// pushSql =
	// " SELECT d.id,d.phone,content,type,other_id,transfer_id,d.create_time from (SELECT c.id,c.phone,c.content,c.type,c.other_id,c.transfer_id,c.create_time from (SELECT * from (select DISTINCT(p.id),p.phone,p.content,p.type,p.other_id,p.transfer_id,DATE_FORMAT(p.create_time,'%m月%d日 %H:%i') create_time from push p ,users u where p.other_id = (select id from users where phone= '"
	// + phone
	// +
	// "' ) and p.phone = u.phone and p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "') or (p.other_id is null and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "')) or (p.type = 'transfer' and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "'))   order by p.create_time desc ) a where   a.phone is not null group by a.phone UNION SELECT * from (select DISTINCT(p.id),p.phone,p.content,p.type,p.other_id,p.transfer_id,DATE_FORMAT(p.create_time,'%m月%d日 %H:%i') create_time  from push p ,users u where p.other_id = (select id from users where phone= '"
	// + phone
	// +
	// "' ) and p.phone = u.phone and p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "') or (p.other_id is null and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "')) or (p.type = 'transfer' and  p.id >(select read_push_position_first from users where phone = '"
	// + phone
	// +
	// "'))   order by p.create_time desc ) b where  b.transfer_id is not null ) c order by  c.create_time desc ) d , transfer_group_temp tgt,transfer t where t.transferNumber = tgt.organSeg and t.transferid = d.transfer_id and  tgt.usersIds like '%"
	// + phone
	// + "%' order by d.create_time desc limit "
	// + page * pageSize + "," + pageSize;
	//
	// }
	// ps = conn.prepareStatement(pushSql);
	// rs = ps.executeQuery();
	//
	// while (rs.next()) {
	//
	// Push push = new Push();
	// push.setId(rs.getInt("id"));
	// push.setPushId(rs.getInt("id"));
	// push.setPhone(rs.getString("phone"));
	// push.setContent(rs.getString("content"));
	// push.setType(rs.getString("type"));
	// push.setOtherId(rs.getString("other_id"));
	// push.setCreateTime(rs.getString("create_time"));
	// String transferId = rs.getString("transfer_id");
	// push.setTransferId(transferId);
	// pusheExcept.add(push);
	//
	// }
	//
	// int friendSize = pusheFriend.size();
	// int exceptSize = pusheExcept.size();
	//
	// System.out.println(pushSql);
	// int indexFriend = 0;
	// int indexExcept = 0;
	// int indexTotal = 0;
	//
	// while (friendSize > 0 && exceptSize > 0) {
	// if (pusheExcept.get(indexExcept).getId() > pusheFriend.get(
	// indexFriend).getId()) {
	//
	// pushes.add(pusheExcept.get(indexExcept));
	// indexTotal++;
	// indexExcept++;
	// exceptSize--;
	// if (indexTotal >= pageSize) {
	// return pushes;
	// }
	// } else {
	//
	// pushes.add(pusheFriend.get(indexFriend));
	// indexTotal++;
	// indexFriend++;
	// friendSize--;
	// if (indexTotal >= pageSize) {
	// return pushes;
	// }
	// }
	// }
	// if (indexTotal >= pageSize) {
	// return pushes;
	// }
	//
	// while (friendSize > 0) {
	// pushes.add(pusheFriend.get(indexFriend));
	// indexTotal++;
	// indexFriend++;
	// friendSize--;
	// if (indexTotal >= pageSize) {
	// return pushes;
	// }
	// }
	// while (exceptSize > 0) {
	// pushes.add(pusheExcept.get(indexExcept));
	// indexTotal++;
	// indexExcept++;
	// exceptSize--;
	// if (indexTotal >= pageSize) {
	// return pushes;
	// }
	// }
	//
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return pushes;
	//
	// } finally {
	// connDB.closeAll(rs, ps, conn);
	// }
	// return pushes;
	//
	// }
	public List<Push> getPushList(String phone, int page, int pageSize) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		String pushSql = "";

		// 判断是否存在该电话的删除信息

		conn = connDB.getConnection();
		List<Push> pushes = new ArrayList<Push>();
		//System.out.println(page + "," + pageSize);
		try {
			String pushDel = "SELECT id,phone,content,type,other_id,DATE_FORMAT(create_time,'%m月%d日 %H:%i') AS create_time,transfer_id FROM push WHERE push_phone = ? AND del=0 ORDER BY id DESC limit ?,? ";
			ps = conn.prepareStatement(pushDel);
			ps.setString(1, phone);
			ps.setInt(2, page * pageSize);
			ps.setInt(3, pageSize);
			rs = ps.executeQuery();
			while (rs.next()) {
				Push push = new Push();
				push.setId(rs.getInt("id"));
				push.setPushId(rs.getInt("id"));
				push.setPhone(rs.getString("phone"));
				push.setContent(rs.getString("content"));
				push.setType(rs.getString("type"));
				push.setOtherId(rs.getString("other_id"));
				push.setCreateTime(rs.getString("create_time"));
				String transferId = rs.getString("transfer_id");
				push.setTransferId(transferId);
				pushes.add(push);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return pushes;

	}

	public static void main(String[] args) {
           boolean isOK =  new UserDao().isBindPhoneConfirm("18668176527");
           boolean isOK1 =  new UserDao().isExistPhoneConfirm("18668176527");
           //System.out.println(isOK+":"+isOK1);
	}

	public List<Push> getPushCount(String phone) {
		ResultSet rs = null;

		PreparedStatement ps = null;
		String pushSql = "";
		int friendFlag = 0;
		// 读取系统消息的位置
		int position = 0;
		// 判断是否存在该电话的删除信息
		int count = 0;
		conn = connDB.getConnection();
		List<Push> pushes = new ArrayList<Push>();

		List<Push> pusheFriend = new ArrayList<Push>();

		try {
			String pushDel = "SELECT count(id) AS count FROM push WHERE push_phone = ? AND del=0 AND status=0";
			ps = conn.prepareStatement(pushDel);
			ps.setString(1, phone);
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt("count");
			}

			pushDel = "SELECT content,DATE_FORMAT(create_time,'%m月%d日 %H:%i') AS create_time FROM push WHERE push_phone = ? AND del=0  AND status=0 ORDER BY id DESC limit 1";
			ps = conn.prepareStatement(pushDel);
			ps.setString(1, phone);
			rs = ps.executeQuery();

			while (rs.next()) {
				Push push = new Push();

				push.setContent(rs.getString("content"));
				push.setCreateTime(rs.getString("create_time"));

				push.setCount(count);

				pushes.add(push);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return pushes;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return pushes;

	}

	public List<String> getTransferPersonByPhone(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<String> transferPersons = new ArrayList<String>();

		String pushSql = "select t.trueName from transfer_group_temp tgt,transfer t where tgt.usersIds LIKE '%"
				+ phone
				+ "%'  and t.transferNumber = tgt.organSeg and t.trueName<>'' GROUP BY t.trueName";
		try {

			ps = conn.prepareStatement(pushSql);
			// ps.setString(1, phone);

			rs = ps.executeQuery();

			while (rs.next()) {
				transferPersons.add(rs.getString("trueName"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return transferPersons;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return transferPersons;

	}

	public List<String> getStartAddressByPhone(String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<String> transferPersons = new ArrayList<String>();

		String pushSql = "select t.fromCity from transfer_group_temp tgt,transfer t where tgt.usersIds LIKE '%"
				+ phone
				+ "%'  and t.transferNumber = tgt.organSeg and t.fromCity<> '' GROUP BY t.fromCity;";
		try {

			ps = conn.prepareStatement(pushSql);
			// ps.setString(1, phone);

			rs = ps.executeQuery();

			while (rs.next()) {
				transferPersons.add(rs.getString("fromCity"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return transferPersons;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return transferPersons;

	}

	public List<Push> isDelPush(List<Push> pushInfo, String phone) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<Push> pushNew = new ArrayList<Push>();
		try {

			for (int i = 0; i < pushInfo.size(); i++) {
				String sql = "select id from pushDel where pushId=? and phone=?";

				ps = conn.prepareStatement(sql);
				ps.setInt(1, pushInfo.get(i).getId());
				ps.setString(2, phone);

				if (ps.executeQuery().next()) {

				} else {
					pushNew.add(pushInfo.get(i));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return pushNew;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return pushNew;
	}

	public boolean loginWeb(String hospital, String pwd) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		pwd = HMACSHA256.sha256_HMAC(pwd, HMACSHA256.secret_key);
		try {

			String sql = "select pwd from account a ,hospital h where h.account_id = a.accountid  and h.`name` = ? and a.pwd = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, hospital);
			ps.setString(2, pwd);
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return false;
	}
}
