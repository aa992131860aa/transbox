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

import com.life.entity.*;
import com.life.utils.ConnectionDB;
import com.mysql.jdbc.Statement;

public class ToolsDao {
    private ConnectionDB connDB = new ConnectionDB();

    // 创建数据库连接对象
    private Connection conn = null;

    public List<Tools> gainTools() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Tools> toolsList = new ArrayList<Tools>();
        String sql = "SELECT name,url FROM tools ORDER BY id ASC";
        boolean isTrue = false;
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);


            rs = ps.executeQuery();
            while (rs.next()) {
                Tools tools = new Tools();
                tools.setName(rs.getString("name"));
                tools.setUrl(rs.getString("url"));
                toolsList.add(tools);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return toolsList;
    }

    public List<String> gainNewsDate() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<String> toolsList = new ArrayList<String>();
        String sql = "SELECT substr(create_time,1,4) date FROM news GROUP BY SUBSTR(create_time,1,4) ORDER BY create_time DESC";
        boolean isTrue = false;
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);


            rs = ps.executeQuery();
            while (rs.next()) {
                toolsList.add(rs.getString("date"));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return toolsList;
    }

    public List<News> gainNewsDate(String date) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<News> toolsList = new ArrayList<News>();
        String sql = "SELECT * FROM news WHERE 1=1 AND create_time LIKE CONCAT('%','" + date + "','%') ORDER BY create_time DESC";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);


            rs = ps.executeQuery();
            while (rs.next()) {
                News news = new News();
                news.setId(rs.getInt("id"));
                news.setBrief(rs.getString("brief"));
                //news.setContent(rs.getString("content"));
                news.setCreateTime(rs.getString("create_time").split(" ")[0]);
                news.setDel(rs.getInt("del"));
                news.setNetType(rs.getString("net_type"));
                news.setTitle(rs.getString("title"));
                news.setTitleImg(rs.getString("title_img"));
                toolsList.add(news);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return toolsList;
    }

    public News gainNewsDateById(int id) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        News news = null;
        String sql = "SELECT * FROM news WHERE id=?";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                news = new News();
                news.setId(rs.getInt("id"));
                news.setBrief(rs.getString("brief"));
                news.setContent(rs.getString("content"));
                news.setCreateTime(rs.getString("create_time").split(" ")[0]);
                news.setDel(rs.getInt("del"));
                news.setNetType(rs.getString("net_type"));
                news.setTitle(rs.getString("title"));
                news.setTitleImg(rs.getString("title_img"));

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return news;
    }

    public int joinUs(String firstName, String lastName, String email, String nation, String title, String institute, String wishes) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Tools> toolsList = new ArrayList<Tools>();
        String sql = "INSERT INTO  join_us(first_name,last_name,email,nation,title,institute,wishes) VALUES (?,?,?,?,?,?,?)";
        int isOk = 0;
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, nation);
            ps.setString(5, title);
            ps.setString(6, institute);
            ps.setString(7, wishes);


            isOk = ps.executeUpdate();


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return isOk;
    }

    public int animalReport(String name, String phone, String company, String question, String questionUrl, String type) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Tools> toolsList = new ArrayList<Tools>();
        String sql = "INSERT INTO  join_us(name,phone,company,question,question_url,type) VALUES (?,?,?,?,?,?)";
        int isOk = 0;
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, company);
            ps.setString(4, question);
            ps.setString(5, questionUrl);
            ps.setString(6, type);


            isOk = ps.executeUpdate();


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return isOk;
    }

}
