package com.life.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.life.entity.UploadApp;
import com.life.utils.CONST;
import com.life.utils.ConnectionDB;

public class UploadAppDao {
    private ConnectionDB connDB = new ConnectionDB();

    // 创建数据库连接对象
    private Connection conn = null;

    public UploadApp getUploadTop() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        UploadApp uploadApp = null;
        String sql = "select id,version,url,createTime from pad_upload order by id desc limit 1 ";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                uploadApp = new UploadApp();
                uploadApp.setId(rs.getInt("id"));
                uploadApp.setUrl(rs.getString("url"));
                uploadApp.setVersion(rs.getInt("version"));
                uploadApp.setCreateTime(rs.getTimestamp("createTime"));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return uploadApp;
    }

    public UploadApp getUploadTop(String deviceId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        UploadApp uploadApp = null;
        String type = "release";

        // deviceId 为otqc和没在box里面的设备都是debug模式 ,其余是release模式

        //

        try {

            String sql = " SELECT  b.hosp_id,test FROM box b WHERE b.deviceId = ?";
            // 调用SQL

            ps = conn.prepareStatement(sql);
            ps.setString(1, deviceId);
            rs = ps.executeQuery();
            if (rs.next()) {
                if ("d08f64da-7834-4eeb-93c9-d0b29318a29e".equals(rs
                        .getString("hosp_id"))) {
                    type = "debug";
                }
                if(rs.getInt("test")==1){
                    type = "test";
                }
            } else {
                type = "debug";
            }



            sql = "select id,version,url,createTime from pad_upload where type = ? ";
            conn = connDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, type);
            rs = ps.executeQuery();
            if (rs.next()) {
                uploadApp = new UploadApp();
                uploadApp.setId(rs.getInt("id"));
                uploadApp.setUrl(rs.getString("url"));
                uploadApp.setVersion(rs.getInt("version"));
                uploadApp.setCreateTime(rs.getTimestamp("createTime"));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return uploadApp;
    }

    public UploadApp getUploadAppTop() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        UploadApp uploadApp = null;
        String sql = "select id,version,url,apk_size,update_content from upload order by id desc limit 1 ";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                uploadApp = new UploadApp();
                uploadApp.setId(rs.getInt("id"));
                uploadApp.setUrl(rs.getString("url"));
                uploadApp.setVersion(rs.getInt("version"));
                uploadApp.setApkSize(rs.getInt("apk_size"));
                uploadApp.setUpdateContent(rs.getString("update_content"));
                // uploadApp.setCreateTime(rs.getTimestamp("createTime"));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return uploadApp;
    }
}
