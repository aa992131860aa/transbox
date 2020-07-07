package com.wangyi;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.life.entity.ContactPerson;
import com.life.utils.ConnectionDB;

public class BaseDao {
    // 创建数据库连接对象
    private Connection conn = null;
    private ConnectionDB connDB = new ConnectionDB();

    /**
     * 更新token
     *
     * @param phone 本人的电话
     * @param name  网易云的token
     * @return
     * @throws UnsupportedEncodingException
     */
    public int updateToken(String phone, String token)
            throws UnsupportedEncodingException {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "UPDATE users SET token =?,is_token=1 WHERE phone=?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, token);
            ps.setString(2, phone);
            return ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return -1;
    }

    public WangyiAppInfo gainWangyiAppInfo(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        WangyiAppInfo wangyiAppInfo = null;

        String sql = "SELECT app_key,app_secret FROM wang_yi_info w,hospital h,users u,account a WHERE h.account_id= a.accountid AND u.hospital_id = h.hospitalid AND w.hospital_id = h.hospitalid AND u.phone = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            if (rs.next()) {
                wangyiAppInfo = new WangyiAppInfo();
                wangyiAppInfo.setAppKey(rs.getString("app_key"));
                wangyiAppInfo.setAppSecret(rs.getString("app_secret"));
            } else {
                sql = "SELECT app_key,app_secret FROM wang_yi_info WHERE hospital_id=-1";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    wangyiAppInfo = new WangyiAppInfo();
                    wangyiAppInfo.setAppKey(rs.getString("app_key"));
                    wangyiAppInfo.setAppSecret(rs.getString("app_secret"));
                }
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return wangyiAppInfo;
    }
    public WangyiAppInfo gainWangyiAppInfo(String phone,ResultSet rs,PreparedStatement ps,Connection conn) {

        conn = connDB.getConnection();
        WangyiAppInfo wangyiAppInfo = null;

        String sql = "SELECT app_key,app_secret FROM wang_yi_info w,hospital h,users u,account a WHERE h.account_id= a.accountid AND u.hospital_id = h.hospitalid AND w.hospital_id = h.hospitalid AND u.phone = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            if (rs.next()) {
                wangyiAppInfo = new WangyiAppInfo();
                wangyiAppInfo.setAppKey(rs.getString("app_key"));
                wangyiAppInfo.setAppSecret(rs.getString("app_secret"));
            } else {
                sql = "SELECT app_key,app_secret FROM wang_yi_info WHERE hospital_id=-1";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                if (rs.next()) {
                    wangyiAppInfo = new WangyiAppInfo();
                    wangyiAppInfo.setAppKey(rs.getString("app_key"));
                    wangyiAppInfo.setAppSecret(rs.getString("app_secret"));
                }
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return wangyiAppInfo;
    }
    public static void main(String[] args) {

    }

    public String isExistToken(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "SELECT token FROM users   WHERE phone=? AND is_token=1";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);

            ps.setString(1, phone);
            rs = ps.executeQuery();
            if (rs.next()) {
                String token = rs.getString("token");
                if (token.length() > 50) {
                    return "";
                } else {
                    return token;
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return "";
    }
}
