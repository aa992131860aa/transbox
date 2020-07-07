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

public class BigScreenDao {
    private ConnectionDB connDB = new ConnectionDB();

    // 创建数据库连接对象
    private Connection conn = null;

    /**
     * 获取四个转运箱的状态
     *
     * @param account 医院登录的状态
     * @return
     */
    public List<ScreenBox> gainScreenBoxList(String account) {
        // 电量 温度 湿度 当前位置  箱子状态（充电中、使用中、空闲中）
        // 考虑没有数据的情况
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<ScreenBox> screenBoxList = new ArrayList<>();

        String sql = "SELECT b.deviceId FROM box b,account a,hospital h WHERE b.hosp_id = h.hospitalid  AND h.account_id = a.accountid  and a.username=? limit 4";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            rs = ps.executeQuery();
            while (rs.next()) {
                String deviceId = rs.getString("deviceId");
                sql = "SELECT  tr.currentCity,tr.power,tr.humidity,tr.temperature,b.model boxNo,tr.recordAt,IF(tr.transfer_id=1,'空闲','使用中') `status` FROM transferRecord tr,box b WHERE tr.remark = b.deviceId AND tr.currentCity <>'' AND  tr.remark =? ORDER by tr.id DESC LIMIT 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, deviceId);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    ScreenBox screenBox = new ScreenBox();
                    screenBox.setBoxNo(rsTwo.getString("boxNo"));
                    screenBox.setCurrentCity(rsTwo.getString("currentCity"));
                    screenBox.setHumidity(rsTwo.getDouble("humidity"));
                    screenBox.setPower(rsTwo.getInt("power"));
                    screenBox.setRecordAt(rsTwo.getString("recordAt"));
                    screenBox.setStatus(rsTwo.getString("status"));
                    screenBox.setTemperature(rsTwo.getDouble("temperature"));
                    screenBoxList.add(screenBox);
                }
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        int size = 4 - screenBoxList.size();
        for (int i = 0; i < size; i++) {
            screenBoxList.add(new ScreenBox());
        }
        return screenBoxList;
    }

    /**
     * 获取转运温度数量的信息
     *
     * @param account 医院登录的状态
     * @return
     */
    public List<ScreenInfoCount> gainTemperatureList(String account) {
        // 电量 温度 湿度 当前位置  箱子状态（充电中、使用中、空闲中）
        // 考虑没有数据的情况
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<ScreenInfoCount> screenInfoCountList = new ArrayList<>();

        String sql =
                "SELECT count(avgTemperature) temperatureCount,'0℃以下' temperatureType FROM ( SELECT AVG(temperature) avgTemperature FROM transferRecord tr ," +
                        " (SELECT t.id  FROM transfer t,hospital h,account ac,box b" +
                        " WHERE 1=1 AND b.hosp_id=h.hospitalid AND t.filterStatus=0 AND ac.accountid=h.account_id  AND h.hospitalid=t.to_hosp_id" +
                        " AND t.`status`='done' AND ac.username=? ) a" +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature < 0 " +
                        " UNION" +
                        " SELECT count(avgTemperature) temperatureCount,'0℃-4℃' temperatureType from( SELECT AVG(temperature) avgTemperature FROM transferRecord tr ," +
                        " (SELECT t.id  FROM transfer t,hospital h,account ac,box b" +
                        " WHERE 1=1 and b.hosp_id=h.hospitalid and ac.accountid=h.account_id AND t.filterStatus=0 and h.hospitalid=t.to_hosp_id" +
                        "  and t.`status`='done' AND ac.username=? ) a" +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature >= 0 and avgTemperature<4" +
                        " UNION" +
                        " SELECT count(avgTemperature) temperatureCount,'4℃-10℃' temperatureType from( SELECT AVG(temperature) avgTemperature FROM transferRecord tr ," +
                        " (SELECT t.id  FROM transfer t,hospital h,account ac,box b" +
                        " WHERE 1=1 and b.hosp_id=h.hospitalid and ac.accountid=h.account_id AND t.filterStatus=0  and h.hospitalid=t.to_hosp_id" +
                        " and t.`status`='done' AND ac.username=? ) a" +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature >= 4 and avgTemperature<10" +
                        " UNION" +
                        " SELECT count(avgTemperature) temperatureCount,'10℃-15℃' temperatureType from( SELECT AVG(temperature) avgTemperature FROM transferRecord tr ," +
                        " (SELECT t.id  FROM transfer t,hospital h,account ac,box b" +
                        " WHERE 1=1 and b.hosp_id=h.hospitalid and ac.accountid = h.account_id AND t.filterStatus=0 and h.hospitalid=t.to_hosp_id" +
                        " and t.`status`='done' AND ac.username=? ) a" +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature >= 10 and avgTemperature<15" +
                        " UNION" +
                        " SELECT count(avgTemperature) temperatureCount,'15℃以上' temperatureType from( SELECT AVG(temperature) avgTemperature FROM transferRecord tr ," +
                        " (SELECT t.id  FROM transfer t,hospital h,account ac,box b" +
                        " WHERE 1=1 and b.hosp_id=h.hospitalid AND t.filterStatus=0  and ac.accountid= account_id and h.hospitalid=t.to_hosp_id" +
                        " and t.`status`='done' AND ac.username=? ) a" +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature >= 15";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            ps.setString(2, account);
            ps.setString(3, account);
            ps.setString(4, account);
            ps.setString(5, account);

            rs = ps.executeQuery();
            while (rs.next()) {
                int temperatureCount = rs.getInt("temperatureCount");
                String temperatureType = rs.getString("temperatureType");
                ScreenInfoCount screenInfoCount = new ScreenInfoCount();
                screenInfoCount.setCount(temperatureCount);
                screenInfoCount.setType(temperatureType);
                screenInfoCountList.add(screenInfoCount);
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }

        return screenInfoCountList;
    }

    /**
     * 获取转运湿度数量的信息
     *
     * @param account 医院登录的状态
     * @return
     */
    public List<ScreenInfoCount> gainHumidityList(String account) {
        // 电量 温度 湿度 当前位置  箱子状态（充电中、使用中、空闲中）
        // 考虑没有数据的情况
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<ScreenInfoCount> screenInfoCountList = new ArrayList<>();

        String sql =
                " SELECT count(avgTemperature) temperatureCount,'60%以下' temperatureType from( SELECT AVG(humidity) avgTemperature FROM transferRecord tr , " +
                        " (SELECT t.id  FROM transfer t, hospital h,account ac,box b " +
                        " WHERE 1=1 and b.hosp_id=h.hospitalid  and ac.accountid=h.account_id  and h.hospitalid=t.to_hosp_id AND t.filterStatus=0 " +
                        " and t.`status`='done'  AND ac.username=? ) a " +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature < 60  " +
                        " UNION " +

                        " SELECT count(avgTemperature) temperatureCount,'60%-80%' temperatureType from( SELECT AVG(humidity) avgTemperature FROM transferRecord tr , " +
                        " (SELECT t.id  FROM transfer t,hospital h,account ac,box b " +
                        " WHERE 1=1 and b.hosp_id=h.hospitalid  and ac.accountid=h.account_id and h.hospitalid=t.to_hosp_id AND t.filterStatus=0 " +
                        "  and t.`status`='done'  AND ac.username=? ) a " +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature >= 60 and avgTemperature<80 " +

                        " UNION " +

                        " SELECT count(avgTemperature) temperatureCount,'80%-90%' temperatureType from( SELECT AVG(humidity) avgTemperature FROM transferRecord tr , " +
                        " (SELECT t.id  FROM transfer t,hospital h,account ac,box b " +
                        " WHERE 1=1 and b.hosp_id=h.hospitalid  and ac.accountid=h.account_id  and h.hospitalid=t.to_hosp_id AND t.filterStatus=0 " +
                        " and t.`status`='done'  AND ac.username=? ) a " +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature >= 80 and avgTemperature<90 " +


                        " UNION " +

                        " SELECT count(avgTemperature) temperatureCount,'90%-95%' temperatureType from( SELECT AVG(humidity) avgTemperature FROM transferRecord tr , " +
                        " (SELECT t.id  FROM transfer t,hospital h,account ac,box b " +
                        " WHERE 1=1 and b.hosp_id=h.hospitalid   and ac.accountid=h.account_id  and h.hospitalid=t.to_hosp_id AND t.filterStatus=0 " +
                        " and t.`status`='done'  AND ac.username=? ) a " +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature >= 90 and avgTemperature<95 " +

                        " UNION " +

                        " SELECT count(avgTemperature) temperatureCount,'95%以上' temperatureType from( SELECT AVG(humidity) avgTemperature FROM transferRecord tr , " +
                        " (SELECT t.id  FROM transfer t,hospital h,account ac,box b " +
                        " WHERE 1=1 and b.hosp_id=h.hospitalid  and ac.accountid=h.account_id  and h.hospitalid=t.to_hosp_id AND t.filterStatus=0 " +
                        " and t.`status`='done'  AND ac.username=? ) a " +
                        " WHERE a.id = tr.transfer_id   GROUP BY a.id ) b  WHERE avgTemperature >= 95";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            ps.setString(2, account);
            ps.setString(3, account);
            ps.setString(4, account);
            ps.setString(5, account);

            rs = ps.executeQuery();
            while (rs.next()) {
                int temperatureCount = rs.getInt("temperatureCount");
                String temperatureType = rs.getString("temperatureType");
                ScreenInfoCount screenInfoCount = new ScreenInfoCount();
                screenInfoCount.setCount(temperatureCount);
                screenInfoCount.setType(temperatureType);
                screenInfoCountList.add(screenInfoCount);
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }

        return screenInfoCountList;
    }

    /**
     * 获取转运温总数量
     *
     * @param account 医院登录的状态
     * @return
     */
    public ScreenTotal gainTransferTotal(String account) {
        // 电量 温度 湿度 当前位置  箱子状态（充电中、使用中、空闲中）
        // 考虑没有数据的情况
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        ScreenTotal screenTotal = new ScreenTotal();
        int count = 0;

        String sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE a.accountid = h.account_id AND h.hospitalid = b.hosp_id AND b.hosp_id = t.to_hosp_id AND t.`status` = 'done' AND t.filterStatus=0    and a.username=?";


        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, account);


            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                screenTotal.setAllTotal(count);

            }

            //心脏
            ScreenInfoCountTwo screenInfoCountTwoHeart = new ScreenInfoCountTwo();
            screenInfoCountTwoHeart.setType("心脏");

            sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE  a.accountid = h.account_id and b.hosp_id = t.to_hosp_id  AND h.hospitalid = b.hosp_id AND  t.`status` = 'transfering' AND t.filterStatus=0 AND t.organ='心脏'  and a.username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                screenInfoCountTwoHeart.setCurrentCount(count);
            }

            sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE  a.accountid = h.account_id and b.hosp_id=t.to_hosp_id  AND h.hospitalid = b.hosp_id AND  t.`status` = 'done' AND t.filterStatus=0 AND t.organ='心脏'  and a.username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                screenInfoCountTwoHeart.setTotalCount(count);
            }


            //肝脏
            ScreenInfoCountTwo screenInfoCountTwoLiver = new ScreenInfoCountTwo();
            screenInfoCountTwoLiver.setType("肝脏");
            //String sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE a.accountid = h.account_id AND h.hospitalid = b.hosp_id AND b.hosp_id = t.to_hosp_id AND t.`status` = 'done' AND t.filterStatus=0    and a.username=?";

            sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE  a.accountid = h.account_id and b.hosp_id=t.to_hosp_id AND h.hospitalid = b.hosp_id AND  t.`status` = 'transfering' AND t.filterStatus=0 AND t.organ='肝脏'  and a.username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                screenInfoCountTwoLiver.setCurrentCount(count);
            }

            sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE  a.accountid = h.account_id and b.hosp_id=t.to_hosp_id  AND h.hospitalid = b.hosp_id AND  t.`status` = 'done' AND t.filterStatus=0 AND t.organ='肝脏'  and a.username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                screenInfoCountTwoLiver.setTotalCount(count);
            }


            //肾脏
            ScreenInfoCountTwo screenInfoCountTwoKidney = new ScreenInfoCountTwo();
            screenInfoCountTwoKidney.setType("肾脏");

            sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE  a.accountid = h.account_id and b.hosp_id=t.to_hosp_id  AND h.hospitalid = b.hosp_id AND  t.`status` = 'transfering' AND t.filterStatus=0 AND t.organ='肾脏'  and a.username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                screenInfoCountTwoKidney.setCurrentCount(count);
            }

            sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE  a.accountid = h.account_id and b.hosp_id=t.to_hosp_id  AND h.hospitalid = b.hosp_id AND   t.`status` = 'done' AND t.filterStatus=0 AND t.organ='肾脏'  and a.username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                screenInfoCountTwoKidney.setTotalCount(count);
            }


            //肺
            ScreenInfoCountTwo screenInfoCountTwoLung = new ScreenInfoCountTwo();
            screenInfoCountTwoLung.setType("肺");

            sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE  a.accountid = h.account_id and b.hosp_id=t.to_hosp_id  AND h.hospitalid = b.hosp_id AND  t.`status` = 'transfering' AND t.filterStatus=0 AND t.organ='肺'  and a.username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                screenInfoCountTwoLung.setCurrentCount(count);
            }

            sql = "SELECT count(distinct(t.id)) count FROM account a,hospital h,transfer t,box b     WHERE  a.accountid = h.account_id and b.hosp_id=t.to_hosp_id  AND h.hospitalid = b.hosp_id AND  t.`status` = 'done' AND t.filterStatus=0 AND t.organ='肺'  and a.username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                screenInfoCountTwoLung.setTotalCount(count);
            }


            List<ScreenInfoCountTwo> countTwoList = new ArrayList<>();
            countTwoList.add(screenInfoCountTwoHeart);
            countTwoList.add(screenInfoCountTwoLiver);
            countTwoList.add(screenInfoCountTwoKidney);
            countTwoList.add(screenInfoCountTwoLung);

            screenTotal.setScreenInfoCountList(countTwoList);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }

        return screenTotal;
    }


    /**
     * 获取四个转运箱的状态
     *
     * @param account 医院登录的状态
     * @return
     */
    public List<ScreenOpenAndCollision> gainScreenOpenAndCollision(String account) {

        ResultSet rs = null;

        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<ScreenOpenAndCollision> screenOpenAndCollisionArrayList = new ArrayList<>();

        String sql = " select '0' as 'open','0' as 'collision','12-01' as 'time' from pad_upload where not exists( SELECT MAX(tr.open) open FROM transferRecord tr ," +
                " (SELECT distinct(t.id) ,t.getTime FROM transfer t,hospital h,account ac,box b" +
                " WHERE 1=1 and b.hosp_id=h.hospitalid  and ac.accountid=h.account_id and t.to_hosp_id=h.hospitalid" +
                " and t.`status`='done' and ac.username=?) a" +
                " WHERE a.id = tr.transfer_id   GROUP BY a.id)" +

                " union" +

                " SELECT MAX(tr.open) open,MAX(tr.collision)   collision , DATE_FORMAT(getTime,'%Y-%m-%d %H:%i')  time FROM transferRecord tr ," +
                " (SELECT distinct(t.id) ,t.getTime FROM transfer t,hospital h,account ac,box b" +
                " WHERE 1=1 and b.hosp_id=h.hospitalid  and ac.accountid=h.account_id  and t.to_hosp_id=h.hospitalid and t.`status`='done' and ac.username=? )a " +
                " WHERE a.id = tr.transfer_id   GROUP BY a.getTime DESC  limit 110";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, account);
            ps.setString(2, account);
            rs = ps.executeQuery();
            while (rs.next()) {
                int open = rs.getInt("open");
                int collision = rs.getInt("collision");
                String time = rs.getString("time");
                ScreenOpenAndCollision screenOpenAndCollision = new ScreenOpenAndCollision();
                screenOpenAndCollision.setOpen(open);
                screenOpenAndCollision.setCollision(collision);
                screenOpenAndCollision.setTime(time);
                screenOpenAndCollisionArrayList.add(screenOpenAndCollision);
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);

        }
        Collections.reverse(screenOpenAndCollisionArrayList);
        return screenOpenAndCollisionArrayList;
    }

}
