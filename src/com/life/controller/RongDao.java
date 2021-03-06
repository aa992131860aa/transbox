package com.life.controller;

import io.rong.models.GroupUserQueryResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.life.entity.Contact;
import com.life.entity.Group;
import com.life.entity.HospitalZone;
import com.life.entity.Users;
import com.life.utils.CONST;
import com.life.utils.ConnectionDB;

public class RongDao {
    // 创建数据库连接对象
    private Connection conn = null;
    private ConnectionDB connDB = new ConnectionDB();

    /**
     * 创建群组
     *
     * @param phone
     * @return
     */
    public int insertUserPhone1(String userId, String userName, String token,
                                String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "update users set user_info_id = ?,user_info_name=?,token=? where phone = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setString(2, userName);
            ps.setString(3, token);
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
     * 插入用户(手机)
     *
     * @param phone
     * @return
     */
    public int insertUserPhone(String userId, String userName, String token,
                               String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "update users set user_info_id = ?,user_info_name=?,token=? where phone = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            ps.setString(2, userName);
            ps.setString(3, token);
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
     * 插入用户(手机)
     *
     * @param
     * @return
     */
    public String getTokenByUserId(String userId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "select token from users where user_info_id = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("token");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return null;

    }

    /**
     * 删除transfer_group_temp
     */
    public int deleteTransferGroup(String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "delete from transfer_group_temp  where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);

            return ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return 0;
    }

    /**
     * 更新transfer_group_temp
     */
    public int updateTransferGroup(String organSeg, String usersIds,
                                   String groupName, String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "update transfer_group_temp set usersIds = ?,groupName = ?,phone = ? where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, usersIds);
            ps.setString(2, groupName);
            ps.setString(3, phone);
            ps.setString(4, organSeg);
            if (usersIds.length() > 8) {
                return ps.executeUpdate();
            } else {
                return 1;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return 0;
    }

    public List<Users> gainTeamInfo(String phones) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Users> usersList = new ArrayList<>();
        String sql = "SELECT u.true_name,u.phone,u.wechat_url,u.photo_url,u.is_upload_photo,h.`name`  from users u,hospital h WHERE u.hospital_id = h.hospitalid AND   ? LIKE CONCAT('%',phone,'%')";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phones);
            rs = ps.executeQuery();

            while (rs.next()) {
                Users users = new Users();
                String is_upload_photo = rs.getString("is_upload_photo");
                String photo_url = rs.getString("photo_url");
                String wechat_url = rs.getString("wechat_url");
                if ("0".equals(is_upload_photo)) {
                    users.setPhotoFile(wechat_url);
                } else {
                    users.setPhotoFile(photo_url);
                }


                users.setPhone(rs.getString("phone"));
                users.setTrueName(rs.getString("true_name"));
                users.setName(rs.getString("name"));
                usersList.add(users);
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return usersList;
    }

    public Users gainTidUsersIds(String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        Users users = null;
        String sql = "SELECT tgt.usersIds,tgt.tid,t.trueName FROM transfer t ,transfer_group_temp tgt WHERE t.transferNumber = tgt.organSeg AND t.transferNumber=?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();

            if (rs.next()) {
                users = new Users();
                users.setUsersId(rs.getString("usersIds"));
                users.setTid(rs.getString("tid"));
                users.setName(rs.getString("trueName"));
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return users;
    }

    /**
     * 更新transfer_group_temp
     */
    public int updateTransferGroup(String organSeg, String usersIds) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "update transfer_group_temp set usersIds = ? where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, usersIds);
            ps.setString(2, organSeg);

            if (usersIds.length() > 8) {
                return ps.executeUpdate();
            } else {
                return 1;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return 0;
    }

    /**
     * 插入transfer_group_temp
     */
    public int insertTransferGroup(String organSeg, String usersIds,
                                   String groupName) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "insert into transfer_group_temp(organSeg,usersIds,groupName) values(?,?,?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            ps.setString(2, usersIds);
            ps.setString(3, groupName);
            return ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return 0;
    }

    /**
     * 插入transfer_group_temp
     */
    public int insertTransferGroup(String organSeg, String usersIds,
                                   String groupName, String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "insert into transfer_group_temp(organSeg,usersIds,groupName,phone) values(?,?,?,?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            ps.setString(2, usersIds);
            ps.setString(3, groupName);
            ps.setString(4, phone);
            return ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return 0;
    }

    /**
     * 插入transfer_group_temp
     */
    public int insertTransferGroup(String organSeg, String usersIds) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "insert into transfer_group_temp(organSeg,usersIds) values(?,?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            ps.setString(2, usersIds);
            return ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return 0;
    }

    /**
     * 插入rong_group
     *
     */
    // public int insertRongGroup(String groupId,String rongId){
    // ResultSet rs = null;
    // PreparedStatement ps = null;
    // conn = connDB.getConnection();
    //
    // String sql = "insert into rong_group(users_id,group_id) values(?,?)";
    // // 调用SQL
    // try {
    //
    // ps = conn.prepareStatement(sql);
    // ps.setString(1, organSeg);
    // ps.setString(2, usersIds);
    // return ps.executeUpdate();
    //
    //
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    //
    // } finally {
    // connDB.closeAll(rs, ps, conn);
    // }
    // return 0;
    // }

    /**
     * 是否存在
     */
    public boolean isTransferGroupByOrganSeg(String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        boolean isTrue = false;

        String sql = "select usersIds from transfer_group_temp where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
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

    /**
     * 是否存在
     */
    public String GroupPhonesByOrganSeg(String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String phones = null;

        String sql = "select usersIds from transfer_group_temp where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                phones = rs.getString("usersIds");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return phones;
    }

    /**
     * 是否存在器官账号
     */
    public boolean isTransferByOrganSeg(String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        boolean isTrue = false;

        String sql = "select id from transfer where transferNumber = ?";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
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

    /**
     * 是否存在器官账号
     */
    public boolean isTransferByModifyOrganSeg(String modifyOrganSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        boolean isTrue = false;

        String sql = "select id from transfer where modifyOrganSeg = ?";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, modifyOrganSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
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

    /**
     * 获取科室
     */
    public String gainDepartment(String boxNo) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "select office from box where model = ?";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, boxNo);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("office");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return "";
    }

    /**
     * 获取科室
     */
    public Map<String, String> gainHospitalInfo(String boxNo) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        Map<String, String> map = new HashMap<String, String>();

        String sql = " SELECT office,name,longitude,latitude FROM box b,hospital h   WHERE b.model = ? AND b.hosp_id = h.hospitalid";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, boxNo);
            rs = ps.executeQuery();
            if (rs.next()) {
                map.put("office", rs.getString("office"));
                map.put("name", rs.getString("name"));
                map.put("longitude", rs.getString("longitude"));
                map.put("latitude", rs.getString("latitude"));

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return map;
    }

    /**
     * 群组中是否存在id
     */
    public boolean isGroupByPhoneAndOrganSeg(String phone, String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        boolean isTrue = false;

        String sql = "select id FROM transfer_group_temp t  where  t.usersIds like CONCAT ('%',(select id from users u where  u.phone=? ),'%') and t.organSeg = ? ";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ps.setString(2, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
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

    /**
     * 改变群组状态
     */
    public boolean chanceGroupStatus(String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        boolean isTrue = false;

        String sql = "update transfer set isGroup = 1 where transferNumber = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);

            ps.setString(1, organSeg);
            ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return isTrue;
    }

    public static void main(String[] args) {

    }

    public String getUsersIdsByTid(String organSeg) {

        ResultSet rs = null;

        PreparedStatement ps = null;

        conn = connDB.getConnection();
        String usersIds = "";

        String sql = "select tid,phone from transfer_group_temp where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                usersIds = rs.getString("tid") + "," + rs.getString("phone");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block

        } finally {
            connDB.closeAll(rs, ps, conn);

        }
        return usersIds;
    }

    public String getUsersIdsByOrganSeg(String organSeg) {

        ResultSet rs = null;

        PreparedStatement ps = null;

        conn = connDB.getConnection();
        String usersIds = "";

        String sql = "select usersIds from transfer_group_temp where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                usersIds = rs.getString("usersIds");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block

        } finally {
            connDB.closeAll(rs, ps, conn);

        }
        return usersIds;
    }

    public String getGroupNameByOrganSeg(String organSeg) {

        ResultSet rs = null;

        PreparedStatement ps = null;

        conn = connDB.getConnection();
        String groupName = "";

        String sql = "select groupName from transfer_group_temp where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                groupName = rs.getString("groupName");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block

        } finally {
            connDB.closeAll(rs, ps, conn);

        }
        return groupName;
    }

    public List<Group> getGroupByPhone(String phone, int page, int pageSize) {
        List<Group> groups = new ArrayList<Group>();
        ResultSet rs = null;

        PreparedStatement ps = null;

        conn = connDB.getConnection();
        // CONCAT ('%',(select id from users u where u.phone=? ),'%')

        String sql = "select t.id,t.organSeg,t.usersIds,t.groupName,t.phone,ts.status,ts.getTime createTime,t.tid,ts.toHosp,ts.modifyOrganSeg,ts.organ  from transfer_group_temp t,transfer ts where t.organSeg = ts.transferNumber and usersIds like '%"
                + phone + "%' and t.tid != '' AND  t.is_group = 0 order by ts.getTime desc limit ?,?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setInt(1, page * pageSize);
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {

                Group group = new Group();
                group.setGroupId(rs.getInt("id"));
                group.setUsersIds(rs.getString("usersIds"));

                group.setPhone(rs.getString("phone"));

                String organSeg = rs.getString("organSeg");
                String status = rs.getString("status");
                String groupName = rs.getString("groupName");
                String createTime = rs.getString("createTime");
                String tid = rs.getString("tid") == null ? "" : rs
                        .getString("tid");

//				if (groupName.contains("已转运")) {
//					group.setGroupName(groupName);
//				} else {
//					if ("done".equals(status)) {
//
//						if (groupName.contains("-")) {
//
//							groupName = "已转运-"
//									+ groupName
//											.substring(groupName.split("-")[0]
//													.length() + 1, groupName
//													.length());
//							sql = "update transfer_group_temp set groupName = ? where organSeg = ?";
//							ps = conn.prepareStatement(sql);
//							ps.setString(1, groupName);
//							ps.setString(2, organSeg);
//							ps.executeUpdate();
//							group.setGroupName(groupName);
//
//						} else {
//							group.setGroupName(groupName);
//						}
//
//					} else {
//						group.setGroupName(groupName);
//					}
//				}
                group.setGroupName(rs.getString("modifyOrganSeg") + "-" + rs.getString("toHosp").split("市")[0]);
                group.setOrganSeg(organSeg);
                group.setCreateTime(createTime);
                group.setOrgan(rs.getString("organ"));

                group.setTid(tid);

                // group.setGroupName(groupName);
                groups.add(group);

            }
            // 加入医院的群组
            // sql =
            // "select u.phone,h.name from users u,hospital h WHERE u.hospital_id = h.hospitalid AND u.phone=? ";
            // ps = conn.prepareStatement(sql);
            // ps.setString(1, phone);
            // rs = ps.executeQuery();
            // if(rs.next()){
            // Group group = new Group();
            // group.setOrganSeg(organSeg)
            // groups.add(group)
            // }

        } catch (SQLException e) {
            // TODO Auto-generated catch block

        } finally {
            connDB.closeAll(rs, ps, conn);

        }
        return groups;
    }

    public List<Contact> getGroupByOrganSeg(String organSeg) {
        List<Contact> contacts = new ArrayList<Contact>();
        ResultSet rs = null;

        PreparedStatement ps = null;

        conn = connDB.getConnection();
        // CONCAT ('%',(select id from users u where u.phone=? ),'%')

        String sql = "select u.id,u.true_name,u.phone,u.photo_url,u.wechat_url,u.is_upload_photo from users u where  (select usersIds from transfer_group_temp tgt where tgt.organSeg = ? ) like CONCAT ('%',u.phone,'%')";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            while (rs.next()) {
                Contact contact = new Contact();
                contact.setUsersId((rs.getString("id")));
                contact.setTrueName((rs.getString("true_name")));
                contact.setContactPhone((rs.getString("phone")));
                contact.setPhotoFile((rs.getString("photo_url")));
                contact.setWechatUrl((rs.getString("wechat_url")));
                contact.setIsUploadPhoto((rs.getString("is_upload_photo")));
                contacts.add(contact);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            connDB.closeAll(rs, ps, conn);

        }
        return contacts;
    }

    /**
     * 获取群组联系人的id
     */
    public String getTransferGroupByOrganSeg(String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String usersIds = "";

        String sql = "select usersIds,phone,groupName from transfer_group_temp where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                usersIds = rs.getString("usersIds");
                usersIds += "=" + rs.getString("phone");
                usersIds += "=" + rs.getString("groupName");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return usersIds;
    }

    /**
     * 根据phone获取token
     *
     * @param phone
     * @return
     */
    public String getTokenByPhone(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "select token from users where phone = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("token");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return null;

    }

    /**
     * 根据群组名判断是否置顶
     *
     * @param
     * @return
     */
    public String getTopByGroupId(String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "select groupName from transfer_group_temp tgt where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("groupName");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return null;

    }

    /**
     * 根据群组名获取当前的人员电话
     *
     * @param
     * @return
     */
    public String getPhonesByGroupId(String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "select usersIds from transfer_group_temp tgt where organSeg = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("usersIds");
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
     * 插入用户(手机)
     *
     * @param
     * @return
     */
    public boolean isExistToken(String userId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "select user_info_id from users where user_info_id = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, userId);
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
