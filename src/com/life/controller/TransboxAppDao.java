package com.life.controller;

import com.life.entity.*;
import com.life.utils.ConnectionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransboxAppDao {
    private ConnectionDB connDB = new ConnectionDB();

    // 创建数据库连接对象
    private Connection conn = null;

    /**
     * 获取箱子的下载apk地址
     *
     * @return
     */
    public TransboxAppUpload gainTransboxAppUploadInfo() {
        // 电量 温度 湿度 当前位置  箱子状态（充电中、使用中、空闲中）
        // 考虑没有数据的情况
        ResultSet rs = null;

        PreparedStatement ps = null;
        conn = connDB.getConnection();
        TransboxAppUpload transboxAppUpload = new TransboxAppUpload();

        String sql = "SELECT  version,url FROM transbox_app_upload";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {
                String url = rs.getString("url");
                int version = rs.getInt("version");

                transboxAppUpload.setUrl(url);
                transboxAppUpload.setVersion(version);

            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);

        }

        return transboxAppUpload;
    }

}
