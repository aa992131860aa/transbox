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
import com.life.entity.TransferJson;
import com.life.entity.Users;
import com.life.entity.Workload;
import com.life.entity.WorkloadDoctor;
import com.life.entity.WorkloadNurse;
import com.life.entity.WorkloadPersonInfo;
import com.life.utils.ConnectionDB;
import com.mysql.jdbc.Statement;

public class WorkloadDao {
	private ConnectionDB connDB = new ConnectionDB();

	// 创建数据库连接对象
	private Connection conn = null;

	public static void main(String[] args) {
		new WorkloadDao().getWorkload("18398850872", "2017-10");
	}
	/**
	 * 根据医院获取个人信息
	 */
	
	
		public List<Users> getUsersByHospital(String hospital) {
		TransferJson t = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<Users> users = new ArrayList<Users>();
		 
		String sql = "SELECT  u.phone,u.true_name,u.photo_url,u.wechat_url,u.is_upload_photo,r.roleName FROM users u,hospital h,role r WHERE u.role_id = r.id AND u.hospital_id = h.hospitalid AND h.name=?";
		 
		try {

			ps = conn.prepareStatement(sql);
            ps.setString(1, hospital);
			rs = ps.executeQuery();
			while (rs.next()) {
				Users user = new Users();
				user.setPhone(rs.getString("phone"));
				user.setTrueName(rs.getString("true_name"));
				user.setPhotoFile(rs.getString("photo_url"));
				user.setWechatUrl(rs.getString("wechat_url"));
				user.setIsUploadPhoto(rs.getString("is_upload_photo"));
				user.setPostRole(rs.getString("roleName"));
				users.add(user);
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
	 * 根据科室协调员获取转运相关人员的信息
	 */
	
	public List<Users> getUsersByContactPhone(String phone) {
		TransferJson t = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<Users> users = new ArrayList<Users>();
		 
		String sql = "SELECT  u.phone,u.true_name,u.photo_url,u.wechat_url,u.is_upload_photo,r.roleName FROM transfer t,transfer_group_temp tgt,users u,role r WHERE u.role_id = r.id AND t.contactPhone=? AND t.transferNumber = tgt.organSeg AND tgt.usersIds like CONCAT('%',u.phone,'%') GROUP BY u.phone";
		 
		try {

			ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
			rs = ps.executeQuery();
			while (rs.next()) {
				Users user = new Users();
				user.setPhone(rs.getString("phone"));
				user.setTrueName(rs.getString("true_name"));
				user.setPhotoFile(rs.getString("photo_url"));
				user.setWechatUrl(rs.getString("wechat_url"));
				user.setIsUploadPhoto(rs.getString("is_upload_photo"));
				user.setPostRole(rs.getString("roleName"));
				users.add(user);
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
	 * 获取科室协调员所在的工作量统计
	 * 
	 * @param phone
	 * @param time
	 * @return
	 */
	public List<WorkloadPersonInfo> getWorkload(String contactPhone, String time) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<WorkloadPersonInfo> workloadPersonInfos = new ArrayList<WorkloadPersonInfo>();

		String sql = "SELECT time,postRole,trueName,count,u.wechat_url,u.photo_url,u.is_upload_photo,r.roleName,u.phone,postRoleId FROM (SELECT DATE_FORMAT(t.getTime,'%Y-%m') AS time,'转运医师' AS postRole,t.trueName,COUNT(t.trueName) AS count,0 AS postRoleId FROM transfer AS t WHERE DATE_FORMAT(t.getTime,'%Y-%m') = ? AND t.filterStatus=0 AND t.contactPhone= ? GROUP BY t.trueName,postRole UNION SELECT DATE_FORMAT(t.getTime,'%Y-%m') AS time, postRole,u.true_name,COUNT(postRole) AS count,pr.id AS postRoleId FROM transfer AS t,postRole AS pr,postRolePerson AS prp,users u WHERE u.phone=prp.phone AND DATE_FORMAT(t.getTime,'%Y-%m') = ? AND t.transferNumber = prp.organSeg AND pr.id = prp.postRoleId AND t.filterStatus=0 AND t.contactPhone =? AND postRole<> '无' AND pr.id = prp.postRoleId   GROUP BY u.true_name,postRole) AS a  , users u ,role r  WHERE u.true_name = a.trueName AND u.role_id = r.id ORDER BY a.trueName,a.postRoleId";

		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, time);
			ps.setString(2, contactPhone);
			ps.setString(3, time);
			ps.setString(4, contactPhone);
			rs = ps.executeQuery();
			String lastTrueName = null;
			WorkloadPersonInfo lastWorkloadPersonInfo = new WorkloadPersonInfo();
			List<Workload> workloads = new ArrayList<Workload>();
			while (rs.next()) {

				String postRole = rs.getString("postRole");
				String trueName = rs.getString("trueName");
				String postRoleId = rs.getString("postRoleId");
				lastTrueName = trueName;
				System.out
						.println(trueName + "," + postRole + "," + postRoleId);
				int count = rs.getInt("count");
				String wechat_url = rs.getString("wechat_url");
				String photo_url = rs.getString("photo_url");
				String is_upload_photo = rs.getString("is_upload_photo");
				String roleName = rs.getString("roleName");
				String phone = rs.getString("phone");

				WorkloadPersonInfo workloadPersonInfo = new WorkloadPersonInfo();
				workloadPersonInfo.setPhone(phone);
				workloadPersonInfo.setRoleName(roleName);
				workloadPersonInfo.setTrueName(trueName);
				workloadPersonInfo.setTime(time);

				if ("0".equals(is_upload_photo)) {
					workloadPersonInfo.setPhotoUrl(wechat_url);
				} else {
					workloadPersonInfo.setPhotoUrl(photo_url);
				}

				// 判断是否重复
				if (!trueName.equals(lastWorkloadPersonInfo.getTrueName())) {

					if (lastWorkloadPersonInfo.getTrueName() != null) {
						lastWorkloadPersonInfo.setWorkloads(workloads);
						workloadPersonInfos.add(lastWorkloadPersonInfo);
						workloads = new ArrayList<Workload>();
					}

					lastWorkloadPersonInfo = workloadPersonInfo;
				}

				Workload workload = new Workload();
				workload.setPostRole(postRole);
				workload.setCount(count);
				workloads.add(workload);

			}

			if (lastTrueName != null) {
				lastWorkloadPersonInfo.setWorkloads(workloads);
				workloadPersonInfos.add(lastWorkloadPersonInfo);
			}

			for (int i = 0; i < workloadPersonInfos.size(); i++) {

				String phone = workloadPersonInfos.get(i).getPhone();
				// 无岗位
				int ownTotal = 0;
				int totalMonth = 0;// 当月的总数
				int totalAll = 0;// 全部的总数

				sql = "select count(t.id) total from transfer t,transfer_group_temp tgt where DATE_FORMAT(t.getTime,'%Y-%m') = ? and t.transferNumber = tgt.organSeg AND t.filterStatus=0 and    tgt.usersIds like '%"
						+ phone + "%'";
				ps = conn.prepareStatement(sql);
				ps.setString(1, time);
				rs = ps.executeQuery();
				if (rs.next()) {
					totalMonth = rs.getInt("total");
				}

				sql = "select count(t.id) total from transfer t,transfer_group_temp tgt where   t.transferNumber = tgt.organSeg AND t.filterStatus=0 and    tgt.usersIds like '%"
						+ phone + "%'";
				ps = conn.prepareStatement(sql);

				rs = ps.executeQuery();
				if (rs.next()) {
					totalAll = rs.getInt("total");
				}

				int workloadSize = workloadPersonInfos.get(i).getWorkloads()
						.size();
				for (int j = 0; j < workloadSize; j++) {

					ownTotal += workloadPersonInfos.get(i).getWorkloads()
							.get(j).getCount();
					workloadPersonInfos.get(i).getWorkloads().get(j)
							.setTotalAll(totalAll);
					workloadPersonInfos.get(i).getWorkloads().get(j)
							.setTotalMonth(totalMonth);

					// 最后一项
					if (j == (workloadSize - 1)) {
						int trueTotal = totalMonth - ownTotal;
						if (trueTotal > 0) {
							Workload workload = new Workload();
							workload.setPostRole("暂无岗位");
							workload.setCount(trueTotal);
							workload.setTotalAll(totalAll);
							workload.setTotalMonth(totalMonth);
							workloadPersonInfos.get(i).getWorkloads().add(
									workload);
						}
					}
				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return workloadPersonInfos;
	}

	/**
	 * 获取同一医院所在的工作量统计
	 * 
	 * @param time
	 * @return
	 */
	public List<WorkloadPersonInfo> getWorkloadHospital(String hospital,
			String time) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<WorkloadPersonInfo> workloadPersonInfos = new ArrayList<WorkloadPersonInfo>();

		String sql = "SELECT time,postRole,trueName,count,u.wechat_url,u.photo_url,u.is_upload_photo,r.roleName,u.phone,postRoleId FROM (SELECT DATE_FORMAT(t.getTime,'%Y-%m') AS time,'转运医师' AS postRole,t.trueName,COUNT(t.trueName) AS count,0 AS postRoleId FROM transfer AS t,hospital AS h WHERE t.to_hosp_id = h.hospitalid AND DATE_FORMAT(t.getTime,'%Y-%m') = ? AND t.filterStatus=0 AND h.`name`= ? GROUP BY t.trueName,postRole UNION SELECT DATE_FORMAT(t.getTime,'%Y-%m') AS time, postRole,u.true_name,COUNT(postRole) AS count,pr.id AS postRoleId FROM transfer AS t,postRole AS pr,postRolePerson AS prp,users u,hospital h WHERE t.to_hosp_id = h.hospitalid  AND t.filterStatus=0 AND u.phone=prp.phone AND DATE_FORMAT(t.getTime,'%Y-%m') = ? AND t.transferNumber = prp.organSeg AND pr.id = prp.postRoleId AND h.`name` =? AND postRole<> '无' AND pr.id = prp.postRoleId   GROUP BY u.true_name,postRole) AS a  , users u ,role r  WHERE u.true_name = a.trueName AND u.role_id = r.id ORDER BY a.trueName,a.postRoleId";

		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);

			ps.setString(1, time);
			ps.setString(2, hospital);
			ps.setString(3, time);
			ps.setString(4, hospital);
			rs = ps.executeQuery();
			String lastTrueName = null;
			WorkloadPersonInfo lastWorkloadPersonInfo = new WorkloadPersonInfo();
			List<Workload> workloads = new ArrayList<Workload>();
			while (rs.next()) {

				String postRole = rs.getString("postRole");
				String trueName = rs.getString("trueName");
				String postRoleId = rs.getString("postRoleId");
				lastTrueName = trueName;
				System.out
						.println(trueName + "," + postRole + "," + postRoleId);
				int count = rs.getInt("count");
				String wechat_url = rs.getString("wechat_url");
				String photo_url = rs.getString("photo_url");
				String is_upload_photo = rs.getString("is_upload_photo");
				String roleName = rs.getString("roleName");
				String phone = rs.getString("phone");

				WorkloadPersonInfo workloadPersonInfo = new WorkloadPersonInfo();
				workloadPersonInfo.setPhone(phone);
				workloadPersonInfo.setRoleName(roleName);
				workloadPersonInfo.setTrueName(trueName);
				workloadPersonInfo.setTime(time);

				if ("0".equals(is_upload_photo)) {
					workloadPersonInfo.setPhotoUrl(wechat_url);
				} else {
					workloadPersonInfo.setPhotoUrl(photo_url);
				}

				// 判断是否重复
				if (!trueName.equals(lastWorkloadPersonInfo.getTrueName())) {

					if (lastWorkloadPersonInfo.getTrueName() != null) {
						lastWorkloadPersonInfo.setWorkloads(workloads);
						workloadPersonInfos.add(lastWorkloadPersonInfo);
						workloads = new ArrayList<Workload>();
					}

					lastWorkloadPersonInfo = workloadPersonInfo;
				}

				Workload workload = new Workload();
				workload.setPostRole(postRole);
				workload.setCount(count);
				workloads.add(workload);

			}

			if (lastTrueName != null) {
				lastWorkloadPersonInfo.setWorkloads(workloads);
				workloadPersonInfos.add(lastWorkloadPersonInfo);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return workloadPersonInfos;
	}

	/**
	 * 获取护士的统计量
	 * 
	 * @param phone
	 * @param time
	 * @return
	 */
	public List<Workload> getWorkloadNurse(String phone, String time) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		List<Workload> workloads = new ArrayList<Workload>();

		String sql = "SELECT DATE_FORMAT(t.getTime,'%Y-%m') AS time, postRole,COUNT(postRole) AS count FROM transfer AS t,postRole AS pr,postRolePerson AS prp,users AS u WHERE u.phone = prp.phone AND DATE_FORMAT(t.getTime,'%Y-%m') = ? AND t.transferNumber = prp.organSeg AND pr.id = prp.postRoleId AND prp.phone =? AND (pr.postRole = '巡回护士' OR pr.postRole ='洗手护士') AND postRole<> '无' GROUP BY u.true_name,postRole";

		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, time);
			ps.setString(2, phone);
			rs = ps.executeQuery();

			while (rs.next()) {
				String postRole = rs.getString("postRole");
				int count = rs.getInt("count");
				Workload workload = new Workload();

				workload.setPostRole(postRole);
				workload.setCount(count);
				workload.setTime(time);

				workloads.add(workload);
			}

			String postRoles = "";

			if (workloads.size() == 0) {

				Workload workload = new Workload();

				workload.setPostRole("洗手护士");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("巡回护士");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);
			} else {
				for (int i = 0; i < workloads.size(); i++) {
					postRoles += workloads.get(i).getPostRole();
				}
				if (!postRoles.contains("洗手护士")) {
					Workload workload = new Workload();

					workload.setPostRole("洗手护士");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("巡回护士")) {
					Workload workload = new Workload();
					workload.setPostRole("巡回护士");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return workloads;
	}

	public int getAllWorkLoadNum(String phone) {
		TransferJson t = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();
		int total = 0;
		String sql = "select count(t.id) total from transfer t,transfer_group_temp tgt where t.transferNumber = tgt.organSeg AND t.filterStatus=0 and    tgt.usersIds like '%"
				+ phone + "%'";
		// System.out.println(sql);
		try {

			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("total");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return total;

	}

	/**
	 * 获取医师的统计量
	 * 
	 * @param phone
	 * @param time
	 * @return
	 */
	public List<Workload> getWorkloadDoctor(String phone, String time) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		conn = connDB.getConnection();

		List<Workload> workloads = new ArrayList<Workload>();
		// AND pr.postRole <> '巡回护士' AND pr.postRole <>'洗手护士'
		String sql = "SELECT DATE_FORMAT(t.getTime,'%Y-%m') AS time,'转运医师' AS postRole,COUNT(t.phone) AS count FROM transfer AS t WHERE DATE_FORMAT(t.getTime,'%Y-%m') = ? AND t.phone = ? GROUP BY t.phone UNION SELECT DATE_FORMAT(t.getTime,'%Y-%m') AS time, postRole,COUNT(postRole) AS count FROM transfer AS t,postRole AS pr,postRolePerson AS prp,users AS u WHERE u.phone=prp.phone AND DATE_FORMAT(t.getTime,'%Y-%m') = ? AND t.transferNumber = prp.organSeg AND pr.id = prp.postRoleId AND prp.phone =?  AND postRole<> '无' AND t.filterStatus=0  GROUP BY u.true_name,postRole";
		// 调用SQL
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, time);
			ps.setString(2, phone);
			ps.setString(3, time);
			ps.setString(4, phone);
			rs = ps.executeQuery();

			while (rs.next()) {
				Workload workload = new Workload();
				String postRole = rs.getString("postRole");
				int count = rs.getInt("count");

				workload.setPostRole(postRole);
				workload.setCount(count);
				workload.setTime(time);

				workloads.add(workload);

			}

			String postRoles = "";
			if (workloads.size() == 0) {

				Workload workload = new Workload();

				workload.setPostRole("转运医师");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("修肝医师");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("接肝医师");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("主刀医师");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("麻醉医师");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("一助");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("二助");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("三助");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("巡回护士");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);

				workload = new Workload();
				workload.setPostRole("洗手护士");
				workload.setCount(0);
				workload.setTime(time);
				workloads.add(workload);
			} else {
				for (int i = 0; i < workloads.size(); i++) {
					postRoles += workloads.get(i).getPostRole();
				}
				if (!postRoles.contains("转运医师")) {
					Workload workload = new Workload();

					workload.setPostRole("转运医师");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("修肝医师")) {
					Workload workload = new Workload();
					workload.setPostRole("修肝医师");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("接肝医师")) {
					Workload workload = new Workload();
					workload.setPostRole("接肝医师");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("主刀医师")) {
					Workload workload = new Workload();
					workload.setPostRole("主刀医师");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("麻醉医师")) {
					Workload workload = new Workload();
					workload.setPostRole("麻醉医师");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("一助")) {
					Workload workload = new Workload();
					workload.setPostRole("一助");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("二助")) {
					Workload workload = new Workload();
					workload.setPostRole("二助");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("三助")) {
					Workload workload = new Workload();
					workload.setPostRole("三助");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("巡回护士")) {
					Workload workload = new Workload();
					workload.setPostRole("巡回护士");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
				if (!postRoles.contains("洗手护士")) {
					Workload workload = new Workload();
					workload.setPostRole("洗手护士");
					workload.setCount(0);
					workload.setTime(time);
					workloads.add(workload);
				}
			}
			// 无岗位
			int ownTotal = 0;
			int total = 0;// 当月的总数
			int totalAll = 0;// 全部的总数

			sql = "select count(t.id) total from transfer t,transfer_group_temp tgt where DATE_FORMAT(t.getTime,'%Y-%m') = ? and t.transferNumber = tgt.organSeg AND t.filterStatus=0 and    tgt.usersIds like '%"
					+ phone + "%'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, time);
			rs = ps.executeQuery();
			if (rs.next()) {
				total = rs.getInt("total");
			}

			sql = "select count(t.id) total from transfer t,transfer_group_temp tgt where   t.transferNumber = tgt.organSeg AND t.filterStatus=0 and    tgt.usersIds like '%"
					+ phone + "%'";
			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();
			if (rs.next()) {
				totalAll = rs.getInt("total");
			}

			for (int i = 0; i < workloads.size(); i++) {
				ownTotal += workloads.get(i).getCount();
				workloads.get(i).setTotalAll(totalAll);
				workloads.get(i).setTotalMonth(total);
			}

			int trueTotal = (total - ownTotal) > 0 ? (total - ownTotal) : 0;
			Workload workload = new Workload();
			workload.setPostRole("暂无岗位");
			workload.setCount(trueTotal);
			workload.setTotalAll(totalAll);
			workload.setTotalMonth(total);
			workload.setTime(time);
			workloads.add(workload);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			connDB.closeAll(rs, ps, conn);
		}
		return workloads;
	}

}
