package com.life.controller;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.life.entity.Contact;
import com.life.entity.ContactPerson;
import com.life.entity.Department;
import com.life.utils.CommUtils;
import com.life.utils.ConnectionDB;

public class ContactDao {
    // 创建数据库连接对象
    private Connection conn = null;
    private ConnectionDB connDB = new ConnectionDB();

    /**
     * 插入用户(手机)
     *
     * @param phone 本人的电话
     * @param name  查询的名字
     * @return
     * @throws UnsupportedEncodingException
     */
    public List<ContactPerson> findFriends(String phone, String name)
            throws UnsupportedEncodingException {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<ContactPerson> contactPersons = new ArrayList<ContactPerson>();
        String sql = "select distinct(u.phone),u.id ,u.true_name,u.phone,u.photo_url,u.wechat_url,u.is_upload_photo,h.name,if(c.id is null,1,0) is_friend,u.bind from users u LEFT JOIN contact c on u.id in (select users_other_id from contact where users_id = (select id from users where phone = ?)),hospital h where u.true_name like '%"
                + name
                + "%' and u.phone <> ? and h.hospitalid=u.hospital_id order by is_friend";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ps.setString(2, phone);
            rs = ps.executeQuery();
            while (rs.next()) {
                ContactPerson contactPerson = new ContactPerson();
                contactPerson.setOther_id(rs.getInt("id"));
                contactPerson.setTrue_name(rs.getString("true_name"));
                contactPerson.setPhone(rs.getString("phone"));
                contactPerson.setPhoto_url(rs.getString("photo_url"));
                contactPerson.setWechat_url(rs.getString("wechat_url"));
                contactPerson.setIs_upload_photo(rs
                        .getString("is_upload_photo"));
                contactPerson.setName(rs.getString("name"));
                contactPerson.setIs_friend(rs.getInt("is_friend"));
                contactPerson.setBind(rs.getString("bind"));
                contactPersons.add(contactPerson);

            }

            return contactPersons;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("findFriends=" + e.getMessage() + "=参数phone:" + phone + ",name:" + name);
            return contactPersons;
        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }


    public int updateContactType(int id, String type) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "update push set type = ? where id = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, type);
            ps.setInt(2, id);

            return ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("updateContactType=" + e.getMessage() + "=参数id:" + id + ",type:" + type);
            return -1;
        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }


    /**
     * 根据phone查询id
     *
     * @param phone 本人的电话
     * @return
     */
    public int getUserIdByPhone(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select id from users where phone = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("updateContactType=" + e.getMessage() + "=参数phone:" + phone);
            return -1;
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return -1;

    }

    /**
     * 根据phone查询id
     *
     * @param
     * @return
     */
    public List<Department> getDepartments(String deviceId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Department> departments = new ArrayList<Department>();
        String sql = "SELECT u.true_name name,u.phone from box b,hospital h,users u  where b.hosp_id = h.hospitalid and h.hospitalid = u.hospital_id and b.deviceId=? and u.role_id=3 order by  u.id desc";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, deviceId);

            rs = ps.executeQuery();
            while (rs.next()) {
                Department department = new Department();
                department.setName(rs.getString("name"));
                department.setPhone(rs.getString("phone"));
                departments.add(department);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("getDepartments=" + e.getMessage() + "=参数deviceId:" + deviceId);
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return departments;

    }

    /**
     * 根据phone查询id
     *
     * @param phone 本人的电话
     * @return
     */
    public int getUserSelfId(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        int usersId = -1;
        String sql = "select id from users where phone = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);

            rs = ps.executeQuery();
            if (rs.next()) {
                usersId = rs.getInt("id");

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("getUserSelfId=" + e.getMessage() + "=参数phone:" + phone);
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return usersId;

    }

    /**
     * 根据phone查询id
     *
     * @param phone 本人的电话
     * @return
     */
    public List<Integer> getUserId(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Integer> usersIds = new ArrayList<Integer>();
        String sql = "select id from users where hospital_id = (select hospital_id from users where phone = ?) and phone <> ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ps.setString(2, phone);
            rs = ps.executeQuery();
            while (rs.next()) {
                usersIds.add(rs.getInt("id"));

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("getUserId=" + e.getMessage() + "=参数phone:" + phone);
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return usersIds;

    }

    public String gainTeamTid(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String tid = null;
        String sql = "SELECT tid from team_hosp where hosp_id = (select hospital_id from users where phone=?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);

            rs = ps.executeQuery();
            while (rs.next()) {
                tid = rs.getString("tid");

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("gainTeamTid=" + e.getMessage() + "=参数phone:" + phone);
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return tid;

    }

    public void insertTeamHosp(String tid, String hospId, String hospName) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "INSERT  INTO  team_hosp(tid,hosp_id,group_name) VALUES (?,?,?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, tid);
            ps.setString(2, hospId);
            ps.setString(3, hospName);
            ps.executeUpdate();


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("insertTeamHosp=" + e.getMessage() + "=参数tid:" + tid + ",hospId:" + hospId + ",hospName:" + hospName);
        } finally {
            connDB.closeAll(rs, ps, conn);
        }


    }

    public String gainHospName(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String hospitalName = "";
        String sql = "SELECT name,hospitalid from hospital where hospitalid = (select hospital_id from users where phone=?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);

            rs = ps.executeQuery();
            if (rs.next()) {
                hospitalName = rs.getString("name");
                hospitalName += "=" + rs.getString("hospitalid");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("gainHospName=" + e.getMessage() + "=参数phone:" + phone);
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return hospitalName;

    }

    public String gainUsers(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String phones = "";
        String sql = "SELECT phone from users where hospital_id = (select hospital_id from users where phone=?) and is_token=1";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);

            rs = ps.executeQuery();
            while (rs.next()) {
                phones += rs.getString("phone") + ",";

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("gainUsers=" + e.getMessage() + "=参数phone:" + phone);
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return phones;

    }

    /**
     * 根据phone查询contact id
     *
     * @param phone 本人的电话
     * @return
     */
    public List<Integer> getContactId(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Integer> usersIds = new ArrayList<Integer>();
        String sql = "select users_other_id id  from contact where users_id = (select id from users where phone = ?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);

            rs = ps.executeQuery();
            while (rs.next()) {
                usersIds.add(rs.getInt("id"));

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("getContactId=" + e.getMessage() + "=参数phone:" + phone);
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return usersIds;

    }


    /**
     * 根据phone查询id
     *
     * @param
     * @return
     * @throws SQLException
     */
    public int deleteContact(int usersId, int usersOtherId) throws SQLException {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        int id1 = 0;
        int id2 = 0;
        // 调用SQL
        try {
            conn.setAutoCommit(false);
            String sql = "delete from contact where  users_id = ? and users_other_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, usersId);
            ps.setInt(2, usersOtherId);
            id1 = ps.executeUpdate();

            String sql2 = "delete from contact where  users_id = ? and users_other_id = ?";
            ps = conn.prepareStatement(sql2);
            ps.setInt(1, usersOtherId);
            ps.setInt(2, usersId);
            id2 = ps.executeUpdate();

            if (id1 >= 1 && id2 >= 1) {
                conn.commit();
                return 1;
            } else {
                conn.rollback();
                return 0;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            CommUtils.insertTransboxErrorFile("deleteContact=" + e.getMessage() + "=参数usersId:" + usersId + ",usersOtherId:" + usersOtherId);
            e.printStackTrace();
            conn.rollback();
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return 0;

    }

    /**
     * 根据phone查询id
     * <p>
     * 本人的电话
     *
     * @return
     */
    public int insertContact(int phoneId, int otherId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "insert into contact(users_id,users_other_id) values(?,?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setInt(1, phoneId);
            ps.setInt(2, otherId);

            return ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("insertContact=" + e.getMessage() + "=参数phoneId:" + phoneId + ",otherId:" + otherId);

            return 0;
        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }

    /**
     * 是否已经同意
     * <p>
     * 本人的电话
     *
     * @return
     */
    public boolean getContactFriend(int phoneId, int otherId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select id from contact where users_id = ? and users_other_id = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setInt(1, phoneId);
            ps.setInt(2, otherId);

            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("getContactFriend=" + e.getMessage() + "=参数phoneId:" + phoneId + ",otherId:" + otherId);

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return false;
    }

    /**
     * 获取联系人信息
     *
     * @return
     */
    public List<Contact> getContactList(String phone, String organSeg, String hospitalType, String hospitalName) {
        List<Contact> lists = new ArrayList<Contact>();
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = " select u.id users_id,true_name,phone,photo_url,wechat_url,is_upload_photo,role_id,h.name from users u, hospital h where u.hospital_id = h.hospitalid and u.id in (select c.users_other_id from users u,contact c where u.id = c.users_id and  u.phone = ?)";
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            while (rs.next()) {
                Contact contact = new Contact();
                contact.setUsersId((rs.getString("users_id")));
                contact.setTrueName((rs.getString("true_name")));
                contact.setContactPhone((rs.getString("phone")));
                contact.setPhotoFile((rs.getString("photo_url")));
                contact.setWechatUrl((rs.getString("wechat_url")));
                contact.setIsUploadPhoto((rs.getString("is_upload_photo")));
                contact.setRoleId(rs.getInt("role_id"));
                contact.setHospitalName(rs.getString("name"));
                if ("1".equals(contact.getIsUploadPhoto())) {
                    contact.setIsUploadPhoto("0");
                    contact.setWechatUrl((rs.getString("photo_url")));
                }

                sql = "select p.postRole from postRolePerson pp,postRole p where p.id = pp.postRoleId and pp.phone=? and pp.organSeg = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, rs.getString("phone"));
                ps.setString(2, organSeg);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    contact.setPostRole(rsTwo.getString("postRole"));

                }
                if (hospitalType.equals("hospital")) {
                    if (hospitalName.equals(contact.getHospitalName())) {
                        lists.add(contact);
                    }
                } else if (hospitalType.equals("noHospital")) {
                    if (!hospitalName.equals(contact.getHospitalName())) {
                        lists.add(contact);
                    }
                } else {
                    lists.add(contact);
                }


            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("getContactFriend=" + e.getMessage() + "=参数phone:" + phone + ",organSeg:" + organSeg);

            return lists;
        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return lists;
    }

    /**
     * 获取科室协调员联系人信息
     *
     * @return
     */
    public List<Contact> getContactOpoList(String phone, String organSeg) {
        List<Contact> lists = new ArrayList<Contact>();
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = " select id users_id,true_name,phone,photo_url,wechat_url,is_upload_photo,role_id from users where role_id = 3 and  id in (select c.users_other_id from users u,contact c where u.id = c.users_id and  u.phone = ?)";
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            while (rs.next()) {
                Contact contact = new Contact();
                contact.setUsersId((rs.getString("users_id")));
                contact.setTrueName((rs.getString("true_name")));
                contact.setContactPhone((rs.getString("phone")));
                contact.setPhotoFile((rs.getString("photo_url")));
                contact.setWechatUrl((rs.getString("wechat_url")));
                contact.setIsUploadPhoto((rs.getString("is_upload_photo")));
                contact.setRoleId(rs.getInt("role_id"));


                sql = "select p.postRole from postRolePerson pp,postRole p where p.id = pp.postRoleId and pp.phone=? and pp.organSeg = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, rs.getString("phone"));
                ps.setString(2, organSeg);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    contact.setPostRole(rsTwo.getString("postRole"));

                }


                lists.add(contact);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CommUtils.insertTransboxErrorFile("getContactOpoList=" + e.getMessage() + "=参数phone:" + phone + ",organSeg:" + organSeg);
            return lists;
        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return lists;
    }

}
