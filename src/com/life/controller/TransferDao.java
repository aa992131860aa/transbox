package com.life.controller;

import com.life.entity.*;
import com.life.utils.*;
import io.rong.RongCloud;
import io.rong.messages.DizNtfMessage;
import io.rong.messages.InfoNtfMessage;
import io.rong.models.CodeSuccessResult;
import io.rong.models.GroupUserQueryResult;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import io.rong.util.QRCodeUtil;
import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.life.servlet.UrlServlet;
import com.mysql.jdbc.Statement;
import com.wangyi.BaseDao;
import com.wangyi.BaseData;
import com.wangyi.WangyiHttpUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class TransferDao {
    private ConnectionDB connDB = new ConnectionDB();

    // 创建数据库连接对象
    private Connection conn = null;
    String appKey = "n19jmcy5nety9";//
    String appSecret = "SdwTI3aFmYb";//
    RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
    WangyiHttpUtils wUtils = new WangyiHttpUtils();

    /**
     * 提交事物 conn.setAutoCommit(false)); //禁止自动提交事务 conn.commit());
     * conn.rollback());
     */

    public boolean createTransfer() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "insert into  organ_new(segment_num,organ_type,blood_type,organ_sample_type,organ_num,blood_sample_num,organ_sample_type,"
                + "create_time) values(?,?,?,?,?,?,?)";
        // 调用SQL
        try {
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String create_time = sdf.format(new Date());
            rs = ps.executeQuery();
            while (rs.next()) {

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return false;
            }
            return false;

        } finally {
            connDB.closeAll(rs, ps, conn);
        }

        return false;
    }

    public void insertAppLog(String name, String phone, String doAction,
                             String msg, String type) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "insert into app_log(name,phone,do_action,msg,type) values(?,?,?,?,?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, doAction);
            ps.setString(4, msg);
            ps.setString(5, type);

            ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
    }

    public int gainRedTotal(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        int total = 0;
        String sql = "SELECT   sum(price) total FROM  redpacklog WHERE phone = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);


            rs = ps.executeQuery();
            while (rs.next()) {
                total = rs.getInt("total");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return total;
    }
    public int gainRedShow() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        int show = 0;
        int total = 0;
        String sql = "SELECT `show` FROM  red_show";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);



            rs = ps.executeQuery();
            while (rs.next()) {
                show = rs.getInt("show");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return show;
    }
    public void updateTransboxProvince() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "SELECT t.id,p.province,t.province p FROM transfer t,provinces p,cities c where t.fromCity like CONCAT('%',substring_index(c.city,'市', 1),'%') and c.provinceid=p.provinceid\n";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {

                String p = rs.getString("p");
                if (p == null) {
                    String province = rs.getString("province");
                    int id = rs.getInt("id");
                    sql = "update transfer SET  province = ? WHERE  id =?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, province);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }


    /**
     * 获取转运中的转运 判断是否超过24小时 判断是否超过3小时没有上传数据
     */
    public List<TransferJson> getTransferNoFinish() {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        ResultSet rsRecord = null;
        ResultSet rsThree = null;

        PreparedStatement ps = null;
        String sql = "SELECT transferid,transferNumber,getTime,box_id,to_hosp_id,isStart,b.deviceId,t.trueName,t.phone,t.toHospName,t.fromCity,b.model boxNo,tgt.usersIds,t.organ,t.organAddress,t.blood,t.startAt,t.modifyOrganSeg,t.sampleOrgan,t.sampleOrgan1,t.tracfficType,t.tracfficNumber  FROM transfer t,box b,transfer_group_temp tgt WHERE t.box_id = b.boxId AND t.transferNumber = tgt.organSeg AND t.`status` = 'transfering'  ";

        try {
            conn = connDB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransferJson t = new TransferJson();
                t.setTransferid(rs.getString("transferid"));
                t.setOrganSeg(rs.getString("transferNumber"));
                t.setGetTime(rs.getString("getTime"));
                t.setBoxNo(rs.getString("box_id"));
                String boxNo = rs.getString("boxNo");
                String toHospId = rs.getString("to_hosp_id");
                String getTime = rs.getString("getTime");

                // 发送短信给市场人员,
                // otqc的转运不发送,
                /**
                 * 发送邮箱给市场人员, 陈杨、蔡玉雅：cy@lifeperfusor.com,cyy@lifeperfusor.com otqc的转运不发送, 没有箱子数据的不发送
                 * 已发送过得信息不发送
                 */

                if (!"d08f64da-7834-4eeb-93c9-d0b29318a29e".equals(toHospId)) {
                    sql = "SELECT count(*) c FROM transferRecord WHERE transfer_id=?";
                    conn = connDB.getConnection();
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, t.getTransferid());
                    rsThree = ps.executeQuery();
                    if (rsThree.next()) {
                        sql = "SELECT * FROM sms_email WHERE transfer_id=? AND  `type`='start' ";
                        conn = connDB.getConnection();
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, t.getTransferid());
                        rsThree = ps.executeQuery();

                        if (rsThree.next()) {

                        } else {
                            String dataUsageContent = "箱号：" + boxNo + ",转运人:"
                                    + rs.getString("trueName") + ",转运人电话:"
                                    + rs.getString("phone") + ",起始地:"
                                    + rs.getString("fromCity")
                                    + ",目的地医院:" + rs.getString("toHospName")
                                    //tgt.usersIds,t.organ,t.organAddress,t.blood,t.isStart,t.modifyOrganSeg,t.sampleOrgan,t.sampleOrgan1
                                    // ,t.tracfficType,t.tracfficNumber
                                    + ",转运团队人员电话:" + rs.getString("usersIds")
                                    + ",器官类型:" + rs.getString("organ")
                                    + ",器官获取地:" + rs.getString("organAddress")
                                    + ",血型:" + rs.getString("blood")
                                    + ",器官编号（简短）:" + rs.getString("modifyOrganSeg")
                                    + ",样本组织1:" + rs.getString("sampleOrgan")
                                    + ",样本组织2:" + rs.getString("sampleOrgan1")
                                    + ",转运方式:" + rs.getString("tracfficType")
                                    + ",飞机编号/火车编号:" + rs.getString("tracfficNumber")
                                    + ",创建转运的时间:" + rs.getString("startAt")
                                    + ",转运时间:" + rs.getString("getTime");


                            sql = "insert into sms_email(`name`,email,transfer_id,time,type) values(?,?,?,?,?)";
                            conn = connDB.getConnection();
                            ps = conn.prepareStatement(sql);
                            ps.setString(1, dataUsageContent);
                            ps.setString(2, "cy@lifeperfusor.com,cyy@lifeperfusor.com");
                            ps.setString(3, t.getTransferid());

                            SimpleDateFormat sdf = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss");
                            ps.setString(4, sdf.format(new Date()));
                            ps.setString(5, "start");
                            ps.executeUpdate();

                            SmsDao.send("转运创建监控", "转运单号：" + t.getTransferid(),
                                    dataUsageContent,
                                    "cy@lifeperfusor.com,cyy@lifeperfusor.com", "");

                        }

                    }

                }

                /**
                 * 判断是否大于24小时
                 */
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                long oldTime = sdf.parse(getTime).getTime();
                long newTime = new Date().getTime();

                long time24 = 1000 * 60 * 60 * 24;
                long time48 = 1000 * 60 * 60 * 48;
                long time5 = 1000 * 60 * 60 * 5;
                long time3 = 1000 * 60 * 60 * 3;
                long time1 = 1000 * 60 * 60 * 1;


                if (newTime - oldTime > time24
                        && "1".equals(rs.getString("isStart"))) {

                    updateTransferStatus(t.getOrganSeg(), t.getBoxNo());

                    continue;

                }

                if (newTime - oldTime > time48
                        && "0".equals(rs.getString("isStart"))) {
                    CONST.DEVICE_ID += t.getDeviceId() + "@";
                    updateTransferStatus(t.getOrganSeg(), t.getBoxNo());

                    continue;

                }
                /**
                 * 判断是否3小时都接受不到数据
                 */

                // sql =
                // "SELECT recordAt FROM transferRecord WHERE transfer_id = ? ORDER BY recordAt DESC LIMIT 1";
                // conn = connDB.getConnection();
                // ps = conn.prepareStatement(sql);
                // ps.setString(1, t.getTransferid());
                // rsRecord = ps.executeQuery();
                //
                // if (rsRecord.next()) {
                // sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // String recordAt = rsRecord.getString("recordAt");
                // long recordTime = sdf.parse(recordAt).getTime();
                // if (newTime - recordTime > time3) {
                // // CONST.DEVICE_ID += t.getDeviceId() +",";
                // updateTransferStatus(t.getOrganSeg(), t.getBoxNo());
                // }
                //
                // }

            }

            /**
             * 未开始转运，但是开箱后有移动 半小时内
             */
            sql = "SELECT b.deviceId,b.model boxNo,h.`name`  from box b,hospital h where b.hosp_id = h.hospitalid and  b.hosp_id!='d08f64da-7834-4eeb-93c9-d0b29318a29e'";
            conn = connDB.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {
                String deviceId = rs.getString("deviceId");
                String boxNo = rs.getString("boxNo");
                String name = rs.getString("name");
                String time = sdf.format(new Date());
                sql = "SELECT recordAt from transferRecord WHERE remark = ? AND recordAt <'2028-01-01' and transfer_id=1 order by recordAt DESC  limit 1";
                conn = connDB.getConnection();
                ps = conn.prepareStatement(sql);
                ps.setString(1, deviceId);
                rsRecord = ps.executeQuery();

                if (rsRecord.next()) {
                    String recordAt = rsRecord.getString("recordAt");
                    long timeLong = (new Date().getTime() - sdf.parse(recordAt).getTime()) / 1000;


                    if (timeLong < 60 * 30) {
                        sql = "SELECT count(id) c from transferRecord where remark = ? and id >= (SELECT id from (SELECT id,recordAt from transferRecord WHERE remark = ? AND recordAt <'2028-01-01'  and transfer_id=1 and recordAt>? order by recordAt DESC ) a order by recordAt ASC limit 1) and transfer_id=1   GROUP BY longitude ";
                        conn = connDB.getConnection();
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, deviceId);
                        ps.setString(2, deviceId);
                        Date dateOld = new Date(sdf.parse(recordAt).getTime() - 1000 * 30 * 60);

                        ps.setString(3, sdf.format(dateOld));
                        rsRecord = ps.executeQuery();
                        int c = 0;
                        while (rsRecord.next()) {
                            c++;
                        }

                        if (c > 2) {
                            //说明30分钟有位置移动
                            SimpleDateFormat sdfSort = new SimpleDateFormat("yyyy-MM-dd");
                            sql = "SELECT * FROM sms_email WHERE `time` like '%" + sdfSort.format(new Date()) + "%' AND box_no=?";
                            conn = connDB.getConnection();
                            ps = conn.prepareStatement(sql);
                            ps.setString(1, boxNo);
                            rsRecord = ps.executeQuery();
                            if (rsRecord.next()) {

                            } else {
                                String dataUsageContent = "箱号:"
                                        + boxNo + ",医院:"
                                        + name + ",移动时间:"
                                        + time + ",30分钟内移动次数：" + c;

                                sql = "insert into sms_email(`name`,email,box_no,time) values(?,?,?,?)";
                                conn = connDB.getConnection();
                                ps = conn.prepareStatement(sql);
                                ps.setString(1, dataUsageContent);
                                ps.setString(2, "cy@lifeperfusor.com,cyy@lifeperfusor.com");
                                ps.setString(3, boxNo);

                                ps.setString(4, sdf.format(new Date()));

                                ps.executeUpdate();

                                SmsDao.send("未转运监控", "箱号：" + boxNo,
                                        dataUsageContent,
                                        "cy@lifeperfusor.com,cyy@lifeperfusor.com", "");
                            }

                        }

                    }
                }
            }
            /**
             * 树兰的在同一位置持续了2小时,会停止和关闭转运
             */
            // sql =
            // "SELECT transferid,transferNumber,getTime,box_id,to_hosp_id,isStart,b.deviceId,b.model boxNo FROM transfer t,box b WHERE t.box_id = b.boxId AND t.`status` = 'transfering' and t.to_hosp_id=69 ";
            // ps = conn.prepareStatement(sql);
            // rs = ps.executeQuery();
            //
            // String endLatitude = "";
            // String endLongitude = "";
            //
            // while (rs.next()) {
            // sql =
            // "SELECT * FROM (SELECT recordAt,latitude,longitude FROM transferRecord WHERE transfer_id = ? ORDER BY recordAt DESC LIMIT 1) b";
            // ps = conn.prepareStatement(sql);
            // ps.setString(1, rs.getString("transferid"));
            // rsRecord = ps.executeQuery();
            // // 最后一条的经纬度
            // while (rsRecord.next()) {
            // // endTime = rsRecord.getString("recordAt");
            // endLatitude = rsRecord.getString("latitude");
            // endLongitude = rsRecord.getString("longitude");
            // }
            //
            // int lastId = 0;
            // String lastLatitude;
            // String lastLongitude;
            //
            // sql =
            // "SELECT id,latitude,longitude,recordAt FROM transferRecord WHERE transfer_id = ? AND latitude <> ? ORDER BY recordAt DESC LIMIT 1";
            // ps = conn.prepareStatement(sql);
            // ps.setString(1, rs.getString("transferid"));
            // ps.setString(2, endLatitude);
            // rsRecord = ps.executeQuery();
            // if (rsRecord.next() && !"".equals(endLatitude)) {
            // lastId = rsRecord.getInt("id");
            // lastLatitude = rsRecord.getString("latitude");
            // lastLongitude = rsRecord.getString("longitude");
            //
            // // 查询是否只有最后一个点,如果条数大于120*2=240就删除
            // sql =
            // "  SELECT count(*) c FROM transferRecord WHERE transfer_id = ? AND id > ?";
            // ps = conn.prepareStatement(sql);
            // ps.setString(1, rs.getString("transferid"));
            // ps.setInt(2, lastId);

            // + ",transferId:" + rs.getString("transferid"));
            // rsRecord = ps.executeQuery();
            // int lastCount = 0;
            // if (rsRecord.next() && lastId != 0) {
            // lastCount = rsRecord.getInt("c");
            // }
            //
            // + ",transferId:" + rs.getString("transferid")
            // + ",lastId:" + lastId);
            // if (lastCount > 240) {
            // CONST.DEVICE_ID += rs.getString("transferid") + ",";
            //
            // updateTransferStatus(rs.getString("transferNumber"),
            // rs.getString("boxNo"));
            // }
            // }
            // // 都是同一个经纬度
            // else if (!"".equals(endLatitude)) {
            // sql =
            // "SELECT count(*) c FROM transferRecord WHERE transfer_id = ? AND latitude = ?";
            // ps = conn.prepareStatement(sql);
            // ps.setString(1, rs.getString("transferid"));
            // ps.setString(2, endLatitude);
            // rsRecord = ps.executeQuery();
            // int lastCount = 0;
            // if (rsRecord.next()) {
            // lastCount = rsRecord.getInt("c");
            // }
            //
            // + ",transferId:" + rs.getString("transferid")
            // + ",latitude:" + endLatitude);
            // if (lastCount > 240) {
            // CONST.DEVICE_ID += rs.getString("transferid") + ",";
            //
            // updateTransferStatus(rs.getString("transferNumber"), rs
            // .getString("boxNo"));
            // }
            // }
            // }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsRecord, ps, conn);
            connDB.closeAll(rsThree, ps, conn);

        }
        return lists;
    }

    /**
     * 快速操作
     */
    public List<TransferJson> getTransferSpeed() {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        ResultSet rsRecord = null;

        PreparedStatement ps = null;

        /**
         * 移除已经停止转运的关机命令
         */

        long time1 = 1000 * 60 * 60 * 24 * 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String recordLimit = sdf.format(new Date(new Date().getTime() - time1));

        String sql = "SELECT b.deviceId FROM transfer t,box b WHERE t.box_id = b.boxId AND t.`status` = 'done' AND getTime >= ?";

        try {
            conn = connDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, recordLimit);
            rs = ps.executeQuery();
            while (rs.next()) {

                TransferJson t = new TransferJson();
                t.setDeviceId(rs.getString("deviceId"));

                /**
                 * 移除已经转运完成的关机项
                 */
                if (CONST.DEVICE_ID.contains(t.getDeviceId())
                        && !CONST.DEVICE_ID.contains("35039810753280")) {
                    String deviceIds[] = CONST.DEVICE_ID.split("@");
                    String deviceIdTemp = "";
                    for (int i = 0; i < deviceIds.length; i++) {
                        if (t.getDeviceId().equals(deviceIds[i])) {

                        } else {
                            if (!"".equals(deviceIds[i])) {
                                deviceIdTemp += deviceIds[i] + "@";
                            }
                        }
                    }
                    CONST.DEVICE_ID = deviceIdTemp;
                }

            }

            /**
             * 在没有转运的情况下,一个小时关机
             */

            // recordLimit = sdf.format(new Date(new Date().getTime() - time1));
            // sql =
            // "SELECT remark FROM transferRecord WHERE transfer_id =1 and recordAt >= ?  GROUP BY remark";
            // conn = connDB.getConnection();
            // ps = conn.prepareStatement(sql);
            // ps.setString(1, recordLimit);
            // rsRecord = ps.executeQuery();
            // List<String> remarks = new ArrayList<String>();
            // while (rsRecord.next()) {
            // remarks.add(rsRecord.getString("remark"));
            // }
            // for (String remark : remarks) {
            // long hour1 = 1000 * 60 * 60 * 1;
            // String recordHour = sdf.format(new Date(new Date().getTime()
            // - hour1));
            // sql =
            // "SELECT count(id) count FROM transferRecord WHERE transfer_id = 1 AND recordAt > ? AND remark = ? ORDER BY id DESC";
            // conn = connDB.getConnection();
            // ps = conn.prepareStatement(sql);
            // ps.setString(1, recordHour);
            // ps.setString(2, remark);
            // rsRecord = ps.executeQuery();
            // // 3600/30=120个
            // if (rsRecord.next()) {
            // int recordCount = rsRecord.getInt("count");
            // if (recordCount > 120) {
            // // 关机操作
            // CONST.DEVICE_ID += remark;
            // } else {
            // // 移除该关机的deviceId
            // if (CONST.DEVICE_ID.contains(remark)) {
            // String deviceIds[] = CONST.DEVICE_ID.split(",");
            // String deviceIdTemp = "";
            // for (int i = 0; i < deviceIds.length; i++) {
            // if (remark.equals(deviceIds[i])) {
            //
            // } else {
            // if (!"".equals(deviceIds[i])) {
            // deviceIdTemp += deviceIds[i] + ",";
            // }
            // }
            // }
            // CONST.DEVICE_ID = deviceIdTemp;
            // }
            // }
            // }
            // }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsRecord, ps, conn);
        }
        return lists;
    }

    /**
     * 1.处理今天的已转运数据 2.开始的经纬度去重,结束的经纬度去重,全部为同一经纬度的不去重 删除的数据保存在transferRecordDel
     * 3.同一天的箱子保持一条转运
     */
    public List<TransferJson> getTransferFinish() {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        ResultSet rsRecord = null;

        PreparedStatement ps = null;

        long time3 = 1000 * 60 * 60 * 24 * 8;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String recordLimit = sdf.format(new Date(new Date().getTime() - time3));

        String sql = "SELECT transferid,transferNumber,getTime,box_id,to_hosp_id,isStart,b.deviceId,trueName,filterStatus FROM transfer t,box b WHERE t.box_id = b.boxId AND t.`status` = 'done' AND getTime >= ?";

        try {
            conn = connDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, recordLimit);
            rs = ps.executeQuery();
            while (rs.next()) {

                TransferJson t = new TransferJson();
                t.setTransferid(rs.getString("transferid"));
                t.setOrganSeg(rs.getString("transferNumber"));
                t.setGetTime(rs.getString("getTime"));
                t.setBoxNo(rs.getString("box_id"));
                t.setDeviceId(rs.getString("deviceId"));
                t.setTrueName(rs.getString("trueName"));
                t.setFilterStatus(rs.getInt("filterStatus"));

                // /**
                // * 移除已经转运完成的关机项
                // */
                // if (CONST.DEVICE_ID.contains(t.getDeviceId())) {
                // String deviceIds[] = CONST.DEVICE_ID.split(",");
                // String deviceIdTemp = "";
                // for (int i = 0; i < deviceIds.length; i++) {
                // if (t.getDeviceId().equals(deviceIds[i])) {
                //
                // } else {
                // if (!"".equals(deviceIds[i])) {
                // deviceIdTemp += deviceIds[i] + ",";
                // }
                // }
                // }
                // CONST.DEVICE_ID = deviceIdTemp;
                // }

                // 查出第一个经纬度和最后一个经纬度
                String startTime = "";
                String startLatitude = "";
                String startLongitude = "";
                String endTime = "";
                String endLatitude = "";
                String endLongitude = "";
                int startCount = 0;
                int endCount = 0;
                sql = "SELECT * FROM (SELECT recordAt,latitude,longitude FROM transferRecord WHERE transfer_id = ? LIMIT 1) a UNION SELECT * FROM (SELECT recordAt,latitude,longitude FROM transferRecord WHERE transfer_id = ? ORDER BY recordAt DESC LIMIT 1) b";
                ps = conn.prepareStatement(sql);
                ps.setString(1, t.getTransferid());
                ps.setString(2, t.getTransferid());
                rsRecord = ps.executeQuery();
                while (rsRecord.next()) {
                    if ("".equals(startTime)) {
                        startTime = rsRecord.getString("recordAt");
                        startLatitude = rsRecord.getString("latitude");
                        startLongitude = rsRecord.getString("longitude");

                    } else {
                        endTime = rsRecord.getString("recordAt");
                        endLatitude = rsRecord.getString("latitude");
                        endLongitude = rsRecord.getString("longitude");
                    }
                }
                // 只有一条数据的情况下
                if ("".equals(endTime)) {
                    endTime = startTime;
                    endLatitude = startLatitude;
                    endLongitude = startLongitude;
                }

                // 判断是不是全程经纬度只有一个,归纳为测试温度 判断第一个和最后一个点 与所有点是不是都一样
                sql = "SELECT COUNT(latitude) c FROM transferRecord WHERE transfer_id = ? AND latitude=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, t.getTransferid());
                ps.setString(2, startLatitude);

                rsRecord = ps.executeQuery();

                if (rsRecord.next()) {
                    startCount = rsRecord.getInt("c");
                }

                sql = "SELECT COUNT(latitude) c FROM transferRecord WHERE transfer_id = ? AND latitude=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, t.getTransferid());
                ps.setString(2, endLatitude);

                rsRecord = ps.executeQuery();

                if (rsRecord.next()) {
                    endCount = rsRecord.getInt("c");
                }

                // 判断是不是只有一个经纬度,如果不是说明有移动
                sql = "SELECT COUNT(id) FROM transferRecord WHERE transfer_id = ? GROUP BY latitude";
                ps = conn.prepareStatement(sql);
                ps.setString(1, t.getTransferid());
                rsRecord = ps.executeQuery();
                int temp = -1;
                while (rsRecord.next()) {
                    temp++;
                    if (temp == 1) {
                        break;
                    }
                }

                if (temp < 1 && startCount == endCount
                        && t.getFilterStatus() == 0) {
                    // 判断是否有转运人,没有则设置转运为不可以用转运

                    if (t.getTrueName() == null || "".equals(t.getTrueName())) {

                        sql = "UPDATE transfer SET filterStatus = 3 WHERE transferid = ?";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, t.getTransferid());
                        ps.executeUpdate();
                    }
                    continue;
                }

                /**
                 * 去除第一个有效点
                 */

                String firstLatitude = "";
                String firstLongitude = "";
                int firstId = 0;
                String twoLatitude = "";
                String twotLongitude = "";
                int twoId = 0;
                double firstDistance = 0;

                sql = "SELECT id,latitude,longitude,recordAt FROM transferRecord WHERE transfer_id = ? AND latitude <> ? LIMIT 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, t.getTransferid());
                ps.setString(2, startLatitude);
                rsRecord = ps.executeQuery();
                if (rsRecord.next()) {
                    twoId = rsRecord.getInt("id");
                    twoLatitude = rsRecord.getString("latitude");
                    twotLongitude = rsRecord.getString("longitude");

                    firstDistance = LocationUtils.getDistance(twoLatitude,
                            twotLongitude, startLatitude, startLongitude);
                } else {
                    // 如果全程经纬度都是一样,不处理,处理了无法进行实验测试.
                    continue;
                }

                //第一个点离第二个有效点大于0公里,完全移除第一个点
                if (firstDistance > 0) {
                    sql =
                            "INSERT INTO transferRecordDel SELECT * FROM transferRecord WHERE transfer_id = ? AND id < ? AND latitude=? AND  temperature>=10";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, t.getTransferid());
                    ps.setInt(2, twoId);
                    ps.setString(3, startLatitude);
                    int insertResult = ps.executeUpdate();
                    if (insertResult > 0) {
                        sql = "DELETE FROM transferRecord WHERE transfer_id = ? AND id < ? AND latitude=?  AND  temperature>=10";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, t.getTransferid());
                        ps.setInt(2, twoId);
                        ps.setString(3, startLatitude);
                        int delResult = ps.executeUpdate();
                        System.out
                                .println("insertResult:" + insertResult + ",");
                    }
                } else {
                    // 保留第一个有效点的最后一条
                    sql =
                            "SELECT id,latitude,longitude,recordAt FROM transferRecord WHERE transfer_id = ? AND id<? ORDER BY id DESC LIMIT 1";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, t.getTransferid());
                    ps.setInt(2, twoId);
                    rsRecord = ps.executeQuery();
                    if (rsRecord.next()) {
                        firstId = rsRecord.getInt("id");
                        firstLatitude = rsRecord.getString("latitude");
                        firstLongitude = rsRecord.getString("longitude");
                    }
                    if (firstId != 0) {
                        sql =
                                "INSERT INTO transferRecordDel SELECT * FROM transferRecord WHERE transfer_id = ? AND id < ? AND latitude=? AND  temperature>=10";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, t.getTransferid());
                        ps.setInt(2, firstId);
                        ps.setString(3, firstLatitude);
                        int insertResult = ps.executeUpdate();
                        if (insertResult > 0) {
                            sql = "DELETE FROM transferRecord WHERE transfer_id = ? AND id < ? AND latitude=? AND  temperature>=10";
                            ps = conn.prepareStatement(sql);
                            ps.setString(1, t.getTransferid());
                            ps.setInt(2, twoId);
                            ps.setString(3, firstLatitude);
                            int delResult = ps.executeUpdate();

                        }
                    }
                }

                /**
                 * 去除最后一个有效点
                 */
                String lastLatitude = "";
                String lastLongitude = "";
                int lastId = 0;
                sql = "SELECT id,latitude,longitude,recordAt FROM transferRecord WHERE transfer_id = ? AND latitude <> ? ORDER BY recordAt DESC LIMIT 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, t.getTransferid());
                ps.setString(2, endLatitude);
                rsRecord = ps.executeQuery();
                if (rsRecord.next()) {
                    lastId = rsRecord.getInt("id");
                    lastLatitude = rsRecord.getString("latitude");
                    lastLongitude = rsRecord.getString("longitude");

                    // 查询是否只有最后一个点,如果条数大于2就删除
                    sql = "  SELECT count(*) c FROM transferRecord WHERE transfer_id = ? AND id > ?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, t.getTransferid());
                    ps.setInt(2, lastId);
                    rsRecord = ps.executeQuery();
                    int lastCount = 0;
                    if (rsRecord.next()) {
                        lastCount = rsRecord.getInt("c");
                    }

                    if (lastCount > 2) {
                        sql = "INSERT INTO transferRecordDel SELECT * FROM transferRecord WHERE transfer_id = ? AND id > ? AND temperature >=10";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, t.getTransferid());
                        ps.setInt(2, lastId);
                        int insertResult = 1;
                        // int insertResult = ps.executeUpdate();
                        if (insertResult > 0) {
                            sql = "DELETE FROM transferRecord WHERE transfer_id = ? AND id > ? AND temperature>=10";
                            ps = conn.prepareStatement(sql);
                            ps.setString(1, t.getTransferid());
                            ps.setInt(2, lastId);
                            int delResult = 1;
                            // int delResult = ps.executeUpdate();

                        }
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsRecord, ps, conn);
        }
        return lists;
    }
    /**
     * 1.处理今天的已转运数据 2.开始的经纬度去重,结束的经纬度去重,全部为同一经纬度的不去重 删除的数据保存在transferRecordDel
     * 3.同一天的箱子保持一条转运
     */
    public List<TransferJson> cleanTransferLessThanGetTime() {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        ResultSet rsRecord = null;

        PreparedStatement ps = null;

        long time3 = 1000 * 60 * 60 * 24 * 4;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String recordLimit = sdf.format(new Date(new Date().getTime() - time3));

        String sql = "SELECT transferid,transferNumber,getTime,box_id,to_hosp_id,isStart,b.deviceId,trueName,filterStatus,startAt FROM transfer t,box b WHERE t.box_id = b.boxId AND t.`status` = 'done' AND getTime >= ?";

        try {
            conn = connDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, recordLimit);
            rs = ps.executeQuery();
            while (rs.next()) {

                TransferJson t = new TransferJson();
                t.setTransferid(rs.getString("transferid"));
                t.setOrganSeg(rs.getString("transferNumber"));
                t.setGetTime(rs.getString("getTime"));
                t.setBoxNo(rs.getString("box_id"));
                t.setDeviceId(rs.getString("deviceId"));
                t.setTrueName(rs.getString("trueName"));
                t.setFilterStatus(rs.getInt("filterStatus"));
                String startAt = rs.getString("startAt");





                    sql ="INSERT INTO transferRecordDel SELECT * FROM transferRecord WHERE transfer_id = ? AND recordAt<=? ";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, t.getTransferid());
                    ps.setString(2, startAt);

                    int insertResult = ps.executeUpdate();
                    if (insertResult > 0) {
                        sql = "DELETE FROM transferRecord  WHERE transfer_id = ? AND recordAt<=? ";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, t.getTransferid());
                        ps.setString(2, startAt);
                        int delResult = ps.executeUpdate();
                        System.out.println("insertResult:" + insertResult + ",");
                    }



            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsRecord, ps, conn);
        }
        return lists;
    }

    /**
     * 1.处理今天的已转运数据 2. 结束的经纬度去重,全部为同一经纬度的不去重 删除的数据保存在transferRecordDel
     * 3.同一天的箱子保持一条转运
     */
    public void cleanTransferRepeat() {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        ResultSet rsRecord = null;

        PreparedStatement ps = null;

        long timeLast = 1000 * 60 * 60 * 24 * 3;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String recordLimit = sdf.format(new Date(new Date().getTime()
                - timeLast));


        String sql = "SELECT transferid,transferNumber,getTime,box_id,to_hosp_id,isStart,b.deviceId,trueName,filterStatus FROM transfer t,box b WHERE t.box_id = b.boxId  AND getTime >= ? and to_hosp_id!='d08f64da-7834-4eeb-93c9-d0b29318a29e'";
        //System.out.println("recordLimit:" + recordLimit);
        try {
            conn = connDB.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, recordLimit);
            rs = ps.executeQuery();
            while (rs.next()) {

                String toHospId = rs.getString("to_hosp_id");

                TransferJson t = new TransferJson();
                t.setTransferid(rs.getString("transferid"));
                t.setOrganSeg(rs.getString("transferNumber"));
                t.setGetTime(rs.getString("getTime"));
                t.setBoxNo(rs.getString("box_id"));
                t.setDeviceId(rs.getString("deviceId"));
                t.setTrueName(rs.getString("trueName"));
                t.setFilterStatus(rs.getInt("filterStatus"));

                String distanceRecordAt = "";
                sql = "SELECT min(recordAt) id FROM transferRecord WHERE transfer_id =? AND  distance > 1 ";
                ps = conn.prepareStatement(sql);
                ps.setString(1, t.getTransferid());
                rsRecord = ps.executeQuery();
                if (rsRecord.next()) {
                    distanceRecordAt = rsRecord.getString("id");
                }
                //System.out.println("distanceId:" + distanceRecordAt + ',' + t.getTransferid());
                int noRepeatCount = 0;
                // 判断是不是只有一个经纬度,如果不是说明有移动 全程只是一个点的不处理
                sql = "SELECT count(*) c FROM (SELECT COUNT(id) c FROM transferRecord WHERE transfer_id = ? GROUP BY latitude) a";
                ps = conn.prepareStatement(sql);
                ps.setString(1, t.getTransferid());
                rsRecord = ps.executeQuery();
                if (rsRecord.next()) {
                    noRepeatCount = rsRecord.getInt("c");
                }
                // 全程不是一个点,处理删除数据
                if (noRepeatCount > 1) {


                    String endTime = "";
                    String endLatitude = "";
                    String endLongitude = "";
                    // 查出最后一个转运点
                    sql = "SELECT recordAt,latitude,longitude FROM transferRecord WHERE transfer_id = ? ORDER BY recordAt DESC LIMIT 1";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, t.getTransferid());

                    rsRecord = ps.executeQuery();
                    if (rsRecord.next()) {

                        endTime = rsRecord.getString("recordAt");
                        endLatitude = rsRecord.getString("latitude");
                        endLongitude = rsRecord.getString("longitude");

                    }

                    // 查询最后相同的点
                    sql = "SELECT id FROM (SELECT id,longitude FROM transferRecord WHERE transfer_id =  ? ORDER BY recordAt DESC) a WHERE longitude<>? limit 1";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, t.getTransferid());
                    ps.setString(2, endLongitude);
                    rsRecord = ps.executeQuery();
                    int lastNoRepeatId = 0;
                    if (rsRecord.next()) {

                        lastNoRepeatId = rsRecord.getInt("id");

                    }


                    // 判断最后重复的点有多少个,20个以上的才删除,否则不删除
                    sql = "  SELECT count(*) c FROM transferRecord WHERE transfer_id = ? AND id > ?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, t.getTransferid());
                    ps.setInt(2, lastNoRepeatId);
                    rsRecord = ps.executeQuery();
                    int lastRepeatCount = 0;
                    if (rsRecord.next()) {
                        lastRepeatCount = rsRecord.getInt("c");
                    }


                    if (lastRepeatCount > 20) {
                        sql = "INSERT INTO transferRecordDel SELECT * FROM transferRecord WHERE transfer_id = ? AND id > ?";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, t.getTransferid());
                        ps.setInt(2, lastNoRepeatId);
                        int insertResult = ps.executeUpdate();
                        if (insertResult > 0) {
                            sql = "DELETE FROM transferRecord WHERE transfer_id = ? AND id > ?";
                            ps = conn.prepareStatement(sql);
                            ps.setString(1, t.getTransferid());
                            ps.setInt(2, lastNoRepeatId);
                            int delResult = ps.executeUpdate();

                        }
                    }

                    //删除离医院近的点
                    if (!"".equals(distanceRecordAt)) {
                        sql = "INSERT INTO transferRecordDel SELECT * FROM transferRecord WHERE transfer_id = ? AND recordAt < ?";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, t.getTransferid());
                        ps.setString(2, distanceRecordAt);
                        int insertResult = ps.executeUpdate();
                        if (insertResult > 0) {
                            sql = "DELETE FROM transferRecord WHERE transfer_id = ? AND recordAt < ?";
                            ps = conn.prepareStatement(sql);
                            ps.setString(1, t.getTransferid());
                            ps.setString(2, distanceRecordAt);
                            int delResult = ps.executeUpdate();

                        }
                    }

                }

            }
        } catch (Exception e) {
            System.out.println("fantasy:error:cleanTransferRepeat" + e.getMessage());
//            try {
//                sql = "  SELECT num FROM tt  ";
//                ps = conn.prepareStatement(sql);
//
//                rsRecord = ps.executeQuery();
//                List<Integer> list = new ArrayList<>();
//                while (rsRecord.next()) {
//                    int id = rsRecord.getInt("num");
//                    list.add(id);
//                }
//                for(Integer id:list){
//                    sql = "  DELETE  FROM  transferRecordDel WHERE id = ?";
//                    ps = conn.prepareStatement(sql);
//                    ps.setInt(1,id);
//                    ps.executeUpdate();
//                }
//            } catch (Exception eee) {
//                System.out.println("fantasy:error:cleanTransferRepeat" + e.getMessage());
//            }
        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsRecord, ps, conn);
        }
    }


    /**
     * 清除箱子的状态
     *
     * @param
     * @return
     */
    public void clearDeviceStatus() {

        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "SELECT boxid FROM box b where b.transferStatus='transfering'";

        try {

            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {

                String boxid = rs.getString("boxid");
                sql = "SELECT t.id,t.transferNumber,t.`status`,t.box_id FROM transfer t WHERE t.box_id =?  AND t.`status`='transfering'";
                ps = conn.prepareStatement(sql);
                ps.setString(1, boxid);
                rsTwo = ps.executeQuery();

                if (rsTwo.next()) {

                } else {
                    sql = "UPDATE box SET transferStatus='free' WHERE boxid=?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, boxid);
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }

    }

    public List<GroupTidInfo> gainGroupInfoMonthAgo() {

        List<GroupTidInfo> groupTidInfoList = new ArrayList<>();
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date();//获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);//当前时间前去一个月，即一个月前的时间
        calendar.getTime();//获取一年前的时间，或者一个月前的时间
        String lastTime = sdf.format(calendar.getTime());
        System.out.println("time:" + lastTime);
        String sql = "SELECT tid,tgt.createTime startAt,t.id,t.phone from transfer t ,transfer_group_temp tgt WHERE t.transferNumber = tgt.organSeg AND tid != ''  and tgt.createTime < ? AND  tgt.is_group=0";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, lastTime);
            rs = ps.executeQuery();
            while (rs.next()) {
                GroupTidInfo groupTidInfo = new GroupTidInfo();
                groupTidInfo.setStartAt(rs.getString("startAt"));
                groupTidInfo.setTid(rs.getString("tid"));
                groupTidInfo.setTransferId(rs.getInt("id"));
                groupTidInfo.setPhone(rs.getString("phone"));
                groupTidInfoList.add(groupTidInfo);
                //System.out.println("tid:" + rs.getString("tid"));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return groupTidInfoList;
    }

    public boolean isGroup(String tid) {


        ResultSet rs = null;

        PreparedStatement ps = null;
        conn = connDB.getConnection();


        String sql = "SELECT  id FROM transfer_group_temp WHERE tid=? AND  is_group=1";

        try {

            ps = conn.prepareStatement(sql);

            ps.setString(1, tid);


            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);

        }
        return false;

    }

    public void dealGroupHistoryContent(String tid, String content, int transferId) {


        ResultSet rs = null;

        PreparedStatement ps = null;
        conn = connDB.getConnection();


        String sql = "INSERT INTO team_msg(tid,content,transfer_id) VALUES (?,?,?)";

        try {

            ps = conn.prepareStatement(sql);

            ps.setString(1, tid);
            ps.setString(2, content);
            ps.setInt(3, transferId);

            ps.executeUpdate();

            sql = "UPDATE  transfer_group_temp SET is_group=1 WHERE  tid=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, tid);
            ps.executeUpdate();


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);

        }

    }


    /**
     * 获取设备状态
     *
     * @param
     * @return
     */
    public String getModifyOrganSeg(String boxNo) {
        TransferJson t = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String modifyOrganSeg = "";

        String sql = "SELECT count(t.id) c FROM transfer t,box b WHERE t.box_id = b.boxid AND b.model = ?";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, boxNo);
            rs = ps.executeQuery();
            if (rs.next()) {
                int c = rs.getInt("c");
                if (c < 1000) {
                    if (c < 10) {
                        modifyOrganSeg = boxNo + "00" + c;
                    } else if (c < 100) {
                        modifyOrganSeg = boxNo + "0" + c;
                    } else if (c < 1000) {
                        modifyOrganSeg = boxNo + c;
                    }
                } else {
                    c = c % 1000;
                    if (c < 10) {
                        modifyOrganSeg = boxNo + "00" + c;
                    } else if (c < 100) {
                        modifyOrganSeg = boxNo + "0" + c;
                    } else if (c < 1000) {
                        modifyOrganSeg = boxNo + c;
                    }
                }
            } else {
                modifyOrganSeg = boxNo + "000";
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return modifyOrganSeg;

    }

    /**
     * 获取设备状态
     *
     * @param
     * @return
     */
    public boolean getDeviceStatus(String organSeg) {
        TransferJson t = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "SELECT recordAt FROM transferRecord tr WHERE tr.remark  = (SELECT box_id FROM transfer t  where transferNumber =?) ORDER BY recordAt DESC LIMIT 1";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                String recordAt = rs.getString("recordAt");
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                try {
                    long time = (new Date().getTime() - sdf.parse(recordAt)
                            .getTime()) / 1000;
                    if (time < 60 * 60) {
                        return true;
                    }

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return true;
                }

            }
            // modify
            return true;

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return false;

    }

    public String getDeviceByOrganSeg(String organSeg) {

        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String result = "";

        String sql = "SELECT b.deviceId FROM transfer t,box b WHERE t.box_id=b.boxid AND t.transferNumber=?";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("deviceId");
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return result;

    }

    public void gainDistance() {

        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();


        String sql = "select startLong,startLati,endLong,endLati,id from transfer;";

        try {

            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {
                String startLong = rs.getString("startLong");
                String startLati = rs.getString("startLati");
                String endLong = rs.getString("endLong");
                String endLati = rs.getString("endLati");
                double distance = LocationUtils.getDistance(startLong, startLati, endLong, endLati);
                int id = rs.getInt("id");
                sql = "update transfer set distance = ? where id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, distance + "");
                ps.setInt(2, id);
                ps.executeUpdate();

                //System.out.println("id:"+rs.getInt("id")+",distance:"+distance);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }


    }

    public List<TransferJson> getTransfersByPhone(String phone, int page,
                                                  int pageSize) {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> shutDownList = new ArrayList<String>();
        // PreparedStatement psTwo = null;
        ResultSet rsExample = null;
        ResultSet rsTwo = null;
        // 是否是示例
        boolean isExample = false;
        conn = connDB.getConnection();
        String sql = "select t.isGroup,t.status,transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,DATE_FORMAT(getTime,'%Y-%m-%d %H:%i') getTime,modifyOrganSeg,autoTransfer,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,b.model boxNo,t.opoContactName,t.opoContactPhone from transfer t,box b where t.box_id=b.boxId AND t.filterStatus=0 and t.transferNumber in (select organSeg from transfer_group_temp where  usersIds like '%"
                + phone + "%') order by t.`status`, t.createAt desc  limit ?,?";
        try {

            ps = conn.prepareStatement(sql);
            // ps.setString(1, phone);
            ps.setInt(1, page * pageSize);
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransferJson t = new TransferJson();
                String transferid = rs.getString("transferid");
                t.setTransferid(rs.getString("transferid") == null ? "" : rs
                        .getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
                        .getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
                        .getString("openPsd"));

                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
                        : rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ") == null ? "" : rs
                        .getString("organ"));
                t.setOrganNum(rs.getString("organNum") == null ? "" : rs
                        .getString("organNum"));
                t.setBlood(rs.getString("blood") == null ? "" : rs
                        .getString("blood"));
                t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
                        .getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
                        .getString("sampleOrgan"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
                        : rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName") == null ? "" : rs
                        .getString("opoName"));
                t.setContactName(rs.getString("contactName") == null ? "" : rs
                        .getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone") == null ? ""
                        : rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone") == null ? "" : rs
                        .getString("phone"));
                t.setTrueName(rs.getString("trueName") == null ? "" : rs
                        .getString("trueName"));
                t.setGetTime(rs.getString("getTime") == null ? "" : rs
                        .getString("getTime"));
                t.setIsStart(rs.getString("isStart") == null ? "" : rs
                        .getString("isStart"));
                t.setDistance(rs.getString("distance") == null ? "" : rs
                        .getString("distance"));
                t.setStartLong(rs.getString("startLong") == null ? "" : rs
                        .getString("startLong"));
                t.setStartLati(rs.getString("startLati") == null ? "" : rs
                        .getString("startLati"));
                t.setEndLong(rs.getString("endLong") == null ? "" : rs
                        .getString("endLong"));
                t.setEndLati(rs.getString("endLati") == null ? "" : rs
                        .getString("endLati"));

                String fromCityStr = rs.getString("fromCity");

                if (fromCityStr.contains("/")) {
                    String temp[] = fromCityStr.split("/");
                    if (temp.length >= 3) {
                        fromCityStr = temp[1] + temp[2];
                    }
                }

                t.setFromCity(fromCityStr);

                t.setToHosp(rs.getString("toHosp") == null ? "" : rs
                        .getString("toHosp"));
                t.setBoxNo(rs.getString("boxNo") == null ? "" : rs
                        .getString("boxNo"));
                t.setStatus(rs.getString("status") == null ? "" : rs
                        .getString("status"));
                t.setIsGroup(rs.getString("isGroup") == null ? "" : rs
                        .getString("isGroup"));
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
                        : rs.getString("modifyOrganSeg"));
                String distance = rs.getString("distance") == null ? "" : rs
                        .getString("distance");
                t.setOpoContactName(rs.getString("opoContactName") == null ? ""
                        : rs.getString("opoContactName"));
                t
                        .setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
                                : rs.getString("opoContactPhone"));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String getTime = rs.getString("getTime");

                // 判断是否大于24小时
                long oldTime = 0;
                try {
                    oldTime = sdf.parse(getTime).getTime();
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                long newTime = new Date().getTime();

                long time24 = 1000 * 60 * 60 * 24;
                if (newTime - oldTime > time24 && !"done".equals(t.getStatus())
                        && "1".equals(rs.getString("isStart"))) {
                    shutDownList.add(t.getOrganSeg() + ":" + t.getBoxNo());
                    t.setStatus("done");
                }

                sql = "select distance,recordAt from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' and tr.distance >0 order by tr.recordAt desc limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferid);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    t.setPause(1);
                    // 修改转运
                    String nowDistance = rsTwo.getString("distance");
                    double dis = Double.parseDouble(distance)
                            - Double.parseDouble(nowDistance);
                    if (dis < 0) {
                        dis = 0.0;
                    }
                    t.setNowDistance(dis + "");

                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        long time = Math.abs(sdf.parse(
                                rsTwo.getString("recordAt")).getTime()
                                - sdf.parse(t.getGetTime()).getTime());
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "0" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "0" + t2;
                            } else {
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "0" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + ":" + secode);
                    } catch (Exception e) {
                        t.setTime("00:00");
                    }
                    t.setDeviceStatus(1);
                } else {
                    t.setNowDistance("0.0");
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    try {
                        long time = Math.abs(sdf.parse(sdf.format(new Date()))
                                .getTime()
                                - sdf.parse(t.getGetTime()).getTime());
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "0" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "0" + t2;
                            } else {
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "0" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + ":" + secode);
                        t.setDeviceStatus(0);

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        t.setTime("00:00");
                    }

                }

                sql = "SELECT DATE_FORMAT(create_time,'%Y-%m-%d %H:%i') create_time,type,content FROM push p where p.transfer_id = ? ORDER BY p.create_time DESC limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferid);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String time = rsTwo.getString("create_time");
                    String type = rsTwo.getString("type");
                    String content = rsTwo.getString("content");
                    String exce = "";

                    // content = "【单号:3652】发生第2次开箱,请及时处理";
                    // type = "open";

                    if ("collision".equals(type)) {
                        exce = time
                                + " "
                                + content.substring(content.indexOf("发生") + 2,
                                content.indexOf("发生") + 7);
                    } else if ("temperature".equals(type)) {
                        exce = time + " 温度异常";
                    } else if ("open".equals(type)) {

                        exce = time
                                + " "
                                + content.substring(content.indexOf("发生") + 2,
                                content.indexOf("发生") + 7);
                    }
                    t.setPushException(exce);
                }
                if (!"LP20180103131749".equals(t.getOrganSeg())) {
                    lists.add(t);
                }

            }
            // 示例转运
            if (!isExample && page == 0) {
                isExample = true;

                sql = "select t.isGroup,t.status,transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,modifyOrganSeg,autoTransfer,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,b.model boxNo,t.opoContactName,t.opoContactPhone from transfer t,box b where t.box_id=b.boxId and t.transferNumber ='LP20180103131749' ";

                ps = conn.prepareStatement(sql);

                rsExample = ps.executeQuery();
                while (rsExample.next()) {
                    TransferJson t = new TransferJson();
                    // transferid = rsExample.getString("transferid");
                    t.setTransferid(rsExample.getString("transferid"));
                    t.setOrganSeg(rsExample.getString("organSeg"));
                    t.setOpenPsd(rsExample.getString("openPsd"));
                    t.setFromCity(rsExample.getString("fromCity"));
                    t.setToHospName(rsExample.getString("toHospName"));
                    t.setTracfficType(rsExample.getString("tracfficType"));
                    t.setToHospName(rsExample.getString("toHospName"));
                    t.setTracfficType(rsExample.getString("tracfficType"));
                    t.setTracfficNumber(rsExample.getString("tracfficNumber"));
                    t.setOrgan(rsExample.getString("organ"));
                    t.setOrganNum(rsExample.getString("organNum"));
                    t.setBlood(rsExample.getString("blood"));
                    t.setBloodNum(rsExample.getString("bloodNum"));
                    t.setSampleOrgan(rsExample.getString("sampleOrgan"));
                    t.setSampleOrganNum(rsExample.getString("sampleOrganNum"));
                    t.setOpoName(rsExample.getString("opoName"));
                    t.setContactName(rsExample.getString("contactName"));
                    t.setContactPhone(rsExample.getString("contactPhone"));
                    t.setPhone(rsExample.getString("phone"));
                    t.setTrueName(rsExample.getString("trueName"));
                    t.setGetTime(rsExample.getString("getTime"));
                    t.setIsStart(rsExample.getString("isStart"));
                    t.setDistance(rsExample.getString("distance"));
                    t.setStartLong(rsExample.getString("startLong"));
                    t.setStartLati(rsExample.getString("startLati"));
                    t.setEndLong(rsExample.getString("endLong"));
                    t.setEndLati(rsExample.getString("endLati"));
                    t.setToHosp(rsExample.getString("toHosp"));
                    t.setBoxNo(rsExample.getString("boxNo"));
                    t.setStatus(rsExample.getString("status"));
                    t.setIsGroup(rsExample.getString("isGroup"));
                    t.setAutoTransfer(rsExample.getInt("autoTransfer"));
                    t.setModifyOrganSeg(rsExample.getString("modifyOrganSeg"));
                    // distance = rsExample.getString("distance");
                    t.setOpoContactName(rsExample.getString("opoContactName"));
                    t
                            .setOpoContactPhone(rsExample
                                    .getString("opoContactPhone"));

                    // sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    // getTime = rsExample.getString("getTime");
                    t.setNowDistance("19.01");
                    t.setTime("01:15");
                    lists.add(t);
                }

            }
            for (int i = 0; i < shutDownList.size(); i++) {


                updateTransferStatus(shutDownList.get(i).split(":")[0],
                        shutDownList.get(i).split(":")[1]);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
            connDB.closeAll(rsExample, ps, conn);
        }
        return lists;
    }

    public List<TransferJson> getTransfersByPhoneNew(String phone, int page,
                                                     int pageSize, int check, String transferType) {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        List<String> shutDownList = new ArrayList<String>();
        // PreparedStatement psTwo = null;
        ResultSet rsExample = null;
        ResultSet rsTwo = null;
        ResultSet rsRecord = null;
        // 是否是示例
        boolean isExample = false;
        conn = connDB.getConnection();
        String sql = "select t.isGroup,t.status,transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrgan1,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,DATE_FORMAT(getTime,'%Y-%m-%d %H:%i') getTime,modifyOrganSeg,autoTransfer,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,b.model boxNo,t.opoContactName,t.opoContactPhone,t.temperature,t.weather,t.check from transfer t,box b where t.box_id=b.boxId ";
        if (check > 0 && check != 2) {
            sql += " AND t.`check` = " + check;
        }
        if (check > 0 && check == 2) {
            sql += " AND ( t.`check` = " + check + " OR t.`check`=4) ";
        }
        if ("transferList".equals(transferType)) {
            sql += " AND t.filterStatus=0 and t.transferNumber in (select organSeg from transfer_group_temp where  usersIds like '%"
                    + phone + "%') order by t.`status`, t.getTime desc  limit ?,?";
        } else {
            sql += " AND t.filterStatus=0 and t.phone=" + phone + " order by t.`status`, t.getTime desc  limit ?,?";

        }


        try {

            ps = conn.prepareStatement(sql);
            // ps.setString(1, phone);

            ps.setInt(1, page * pageSize);
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransferJson t = new TransferJson();
                String transferid = rs.getString("transferid");
                t.setTransferid(rs.getString("transferid") == null ? "" : rs
                        .getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
                        .getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
                        .getString("openPsd"));

                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
                        : rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ") == null ? "" : rs
                        .getString("organ"));
                t.setOrganNum(rs.getString("organNum") == null ? "" : rs
                        .getString("organNum"));
                t.setBlood(rs.getString("blood") == null ? "" : rs
                        .getString("blood"));
                t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
                        .getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
                        .getString("sampleOrgan"));
                t.setSampleOrgan1(rs.getString("sampleOrgan1") == null ? ""
                        : rs.getString("sampleOrgan1"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
                        : rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName") == null ? "" : rs
                        .getString("opoName"));
                t.setContactName(rs.getString("contactName") == null ? "" : rs
                        .getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone") == null ? ""
                        : rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone") == null ? "" : rs
                        .getString("phone"));
                t.setTrueName(rs.getString("trueName") == null ? "" : rs
                        .getString("trueName"));
                t.setGetTime(rs.getString("getTime") == null ? "" : rs
                        .getString("getTime"));
                t.setIsStart(rs.getString("isStart") == null ? "" : rs
                        .getString("isStart"));
                t.setDistance(rs.getString("distance") == null ? "" : rs
                        .getString("distance"));
                t.setStartLong(rs.getString("startLong") == null ? "" : rs
                        .getString("startLong"));
                t.setStartLati(rs.getString("startLati") == null ? "" : rs
                        .getString("startLati"));
                t.setEndLong(rs.getString("endLong") == null ? "" : rs
                        .getString("endLong"));
                t.setEndLati(rs.getString("endLati") == null ? "" : rs
                        .getString("endLati"));
                t.setTemperature(rs.getString("temperature") == null ? "" : rs
                        .getString("temperature"));
                t.setWeather(rs.getString("weather") == null ? "" : rs
                        .getString("weather"));
                t.setCheck(rs.getInt("check"));

                String fromCityStr = rs.getString("fromCity");

                if (fromCityStr.contains("/")) {
                    String temp[] = fromCityStr.split("/");
                    if (temp.length >= 3) {
                        fromCityStr = temp[1] + temp[2];
                    }
                }

                t.setFromCity(fromCityStr);

                t.setToHosp(rs.getString("toHosp") == null ? "" : rs
                        .getString("toHosp"));
                t.setBoxNo(rs.getString("boxNo") == null ? "" : rs
                        .getString("boxNo"));
                t.setStatus(rs.getString("status") == null ? "" : rs
                        .getString("status"));
                t.setIsGroup(rs.getString("isGroup") == null ? "" : rs
                        .getString("isGroup"));
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
                        : rs.getString("modifyOrganSeg"));
                String distance = rs.getString("distance") == null ? "" : rs
                        .getString("distance");
                t.setOpoContactName(rs.getString("opoContactName") == null ? ""
                        : rs.getString("opoContactName"));
                t.setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
                        : rs.getString("opoContactPhone"));
                if (fromCityStr.contains("省")) {
                    fromCityStr = fromCityStr.split("省")[1];

                }
                if (fromCityStr.contains("市")) {
                    fromCityStr = fromCityStr.split("市")[0];
                }
                if (fromCityStr.length() > 5) {
                    t.setGroupName(t.getModifyOrganSeg() + "-"
                            + fromCityStr.substring(0, 4));
                } else {
                    t.setGroupName(t.getModifyOrganSeg() + "-" + fromCityStr);
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String getTime = rs.getString("getTime");

                // 判断是否大于24小时
                long oldTime = 0;
                try {
                    oldTime = sdf.parse(getTime).getTime();
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                long newTime = new Date().getTime();

                long time24 = 1000 * 60 * 60 * 24;
                if (newTime - oldTime > time24 && !"done".equals(t.getStatus())
                        && "1".equals(rs.getString("isStart"))) {
                    shutDownList.add(t.getOrganSeg() + ":" + t.getBoxNo());
                    t.setStatus("done");
                }
                String currentCity = "";
                String currentTemperature = "";
                sql = "select distance,recordAt,currentCity,temperature currentTemperature from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' and tr.distance >0 order by tr.id desc limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferid);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    currentCity = rsTwo.getString("currentCity");
                    currentTemperature = rsTwo.getString("currentTemperature");
                    t.setPause(1);
                    // 修改转运
                    String nowDistance = rsTwo.getString("distance");
                    double dis = Double.parseDouble(distance)
                            - Double.parseDouble(nowDistance);
                    dis = Double.parseDouble(nowDistance);
                    // if (dis < 0) {
                    // dis = 0.0;
                    // }
                    dis = Math.abs(dis);
                    t.setNowDistance(dis + "");

                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        long time = Math.abs(sdf.parse(
                                rsTwo.getString("recordAt")).getTime()
                                - sdf.parse(t.getGetTime()).getTime());
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "" + t2;
                            } else {
                                if (t2 >= 24) {
                                    t2 = 23;
                                }
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + "." + secode);
                    } catch (Exception e) {
                        t.setTime("0.0");
                    }
                    t.setDeviceStatus(1);
                } else {
                    t.setNowDistance(distance);
                    currentTemperature = "0";
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    try {
                        long time = Math.abs(sdf.parse(sdf.format(new Date()))
                                .getTime()
                                - sdf.parse(t.getGetTime()).getTime());
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "" + t2;
                            } else {
                                if (t2 >= 24) {
                                    t2 = 23;
                                }
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + "." + secode);
                        t.setDeviceStatus(0);
                        t.setTime("0.0");
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                        t.setTime("0.0");
                    }

                }
                t.setCurrentTemperature(currentTemperature);

                // 获取平均温度,湿度
                sql = "SELECT IF(AVG(temperature),AVG(temperature),0) avgT,IF(AVG(humidity),AVG(humidity),0) avgH  FROM transferRecord WHERE transfer_id = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferid);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String avgT = ((int) rsTwo.getDouble("avgT")) + "";
                    String avgH = ((int) rsTwo.getDouble("avgH")) + "";
                    t.setAvgT(avgT);
                    t.setAvgH(avgH);

                }
                // 获取碰撞总是
                sql = "SELECT IF(SUM(collision),SUM(collision),0) collisionTotal  FROM transferRecord WHERE transfer_id = ? AND  collision=1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferid);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String collisionTotal = rsTwo.getInt("collisionTotal") + "";

                    t.setCollisionTotal(collisionTotal);

                }

                // 获取群组tid
                sql = "SELECT tgt.tid,tgt.phone,tgt.usersIds  FROM transfer_group_temp tgt,transfer t WHERE t.transferNumber = tgt.organSeg AND t.transferid=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferid);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String tid = rsTwo.getString("tid");
                    String nowPhone = rsTwo.getString("phone");
                    if (!phone.equals(nowPhone)) {
                        t.setFromCityShow("无查看权限");
                    }
                    t.setTid(tid == null ? "" : tid);
                    t.setPhones(rsTwo.getString("usersIds"));

                }

                sql = "SELECT DATE_FORMAT(create_time,'%m.%d %H:%i') create_time,type,content FROM push p where p.transfer_id = ? ORDER BY p.create_time DESC limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferid);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String time = rsTwo.getString("create_time");
                    String type = rsTwo.getString("type");
                    String content = rsTwo.getString("content");
                    String exce = "";

                    // content = "【单号:3652】发生第2次开箱,请及时处理";
                    // type = "open";

                    if ("collision".equals(type)) {
                        exce = content.substring(content.indexOf("发生") + 2,
                                content.indexOf("发生") + 7);
                    } else if ("temperature".equals(type)) {
                        exce = "温度异常";
                    } else if ("open".equals(type)) {

                        exce = content.substring(content.indexOf("发生") + 2,
                                content.indexOf("发生") + 7);
                    }

                    t.setPushException(exce);
                    if (!"".equals(exce)) {
                        t.setPushExceptionTime(time);
                    }
                }
                if ("".equals(currentCity)) {
                    currentCity = t.getToHosp();
                }
                t.setCurrentCity(currentCity);
                // 在转运中的曲线
                // transfering
                sql = "SELECT tr.temperature tem FROM transfer t ,transferRecord tr WHERE t.transferid = tr.transfer_id AND t.transferNumber = ? AND t.status='transfering'";
                ps = conn.prepareStatement(sql);
                ps.setString(1, t.getOrganSeg());
                rsRecord = ps.executeQuery();
                List<String> temperatureList = new ArrayList<String>();
                List<String> temperatureSortList = new ArrayList<String>();
                while (rsRecord.next()) {
                    temperatureList.add(rsRecord.getString("tem"));
                }
                if (temperatureList.size() < 6) {
                    temperatureSortList.addAll(temperatureList);
                } else {
                    int base = temperatureList.size() / 5;
                    for (int i = 0; i < temperatureList.size(); i++) {
                        if (i % base == 0) {
                            temperatureSortList.add(temperatureList.get(i));
                            if (temperatureSortList.size() > 5) {
                                break;
                            }
                        }
                    }

                }
                t.setTemperatureList(temperatureSortList);
                DecimalFormat df = new DecimalFormat("#0.00");
                String str = df.format(Double.parseDouble(t.getDistance()));
                t.setDistance(str);
                str = df.format(Double.parseDouble(t.getNowDistance()));
                t.setNowDistance(str);

                if (!"LP20180103131749".equals(t.getOrganSeg())) {
                    lists.add(t);
                }

            }

            // 示例转运
            if (!isExample && page == 0) {
                isExample = true;

                sql = "select t.isGroup,t.status,transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,modifyOrganSeg,autoTransfer,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,b.model boxNo,t.opoContactName,t.opoContactPhone,t.check from transfer t,box b where t.box_id=b.boxId and t.transferNumber ='LP20180103131749' ";

                ps = conn.prepareStatement(sql);

                rsExample = ps.executeQuery();
                while (rsExample.next()) {
                    TransferJson t = new TransferJson();
                    // transferid = rsExample.getString("transferid");
                    t.setTransferid(rsExample.getString("transferid"));
                    t.setOrganSeg(rsExample.getString("organSeg"));
                    t.setOpenPsd(rsExample.getString("openPsd"));
                    t.setFromCity(rsExample.getString("fromCity"));
                    t.setToHospName(rsExample.getString("toHospName"));
                    t.setTracfficType(rsExample.getString("tracfficType"));
                    t.setToHospName(rsExample.getString("toHospName"));
                    t.setTracfficType(rsExample.getString("tracfficType"));
                    t.setTracfficNumber(rsExample.getString("tracfficNumber"));
                    t.setOrgan(rsExample.getString("organ"));
                    t.setOrganNum(rsExample.getString("organNum"));
                    t.setBlood(rsExample.getString("blood"));
                    t.setBloodNum(rsExample.getString("bloodNum"));
                    t.setSampleOrgan(rsExample.getString("sampleOrgan"));
                    t.setSampleOrganNum(rsExample.getString("sampleOrganNum"));
                    t.setOpoName(rsExample.getString("opoName"));
                    t.setContactName(rsExample.getString("contactName"));
                    t.setContactPhone(rsExample.getString("contactPhone"));
                    t.setPhone(rsExample.getString("phone"));
                    t.setTrueName(rsExample.getString("trueName"));
                    t.setGetTime(rsExample.getString("getTime"));
                    t.setIsStart(rsExample.getString("isStart"));
                    t.setDistance(rsExample.getString("distance"));
                    t.setStartLong(rsExample.getString("startLong"));
                    t.setStartLati(rsExample.getString("startLati"));
                    t.setEndLong(rsExample.getString("endLong"));
                    t.setEndLati(rsExample.getString("endLati"));
                    t.setToHosp(rsExample.getString("toHosp"));
                    t.setBoxNo(rsExample.getString("boxNo"));
                    t.setStatus(rsExample.getString("status"));
                    t.setIsGroup(rsExample.getString("isGroup"));
                    t.setAutoTransfer(rsExample.getInt("autoTransfer"));
                    t.setModifyOrganSeg(rsExample.getString("modifyOrganSeg"));
                    t.setCurrentCity(rsExample.getString("toHosp"));
                    // distance = rsExample.getString("distance");
                    t.setOpoContactName(rsExample.getString("opoContactName"));
                    t
                            .setOpoContactPhone(rsExample
                                    .getString("opoContactPhone"));

                    // sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    // getTime = rsExample.getString("getTime");
                    t.setNowDistance("19.01");
                    t.setTime("1.15");
                    t.setAvgH(60 + "");
                    t.setAvgT(2 + "");
                    t.setCollisionTotal(3 + "");
                    t.setGroupName("杭州-99999001");
                    t.setCheck(rsExample.getInt("check"));
                    if (lists.size() == 0 && check == -1) {
                        lists.add(t);
                    }

                }

            }
            for (int i = 0; i < shutDownList.size(); i++) {


                updateTransferStatus(shutDownList.get(i).split(":")[0],
                        shutDownList.get(i).split(":")[1]);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
            connDB.closeAll(rsExample, ps, conn);
            connDB.closeAll(rsRecord, ps, conn);

        }
        return lists;
    }

    public List<TransferJson> getSearchTransfersHistory(String phone,
                                                        String condition, int page, int pageSize) {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select  transferid,transferNumber organSeg,boxPin openPsd,fromCity,modifyOrganSeg,autoTransfer,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,t.status,t.opoContactName,t.opoContactPhone,t.temperature,t.weather,t.check,b.model boxNo  from transfer t,box b where t.box_id=b.boxId AND t.filterStatus=0 and t.`status`='done' AND CONCAT(trueName,contactName,organ,fromCity,toHosp,blood,sampleOrgan,tracfficType,tracfficNumber,opoName,toHospName,b.model)  like '%"
                + condition
                + "%'  and t.transferNumber in (select organSeg from transfer_group_temp where  usersIds like '%"
                + phone
                + "%') order by t.`status`, t.createAt desc   limit ?,?";
        if (condition.equals("未审核") || condition.equals("审核中") || condition.equals("已审核")) {
            int check = 3;
            if (condition.equals("未审核")) {
                check = 3;
            }
            if (condition.equals("审核中")) {
                check = 1;
            }
            if (condition.equals("已审核")) {
                check = 2;
            }
            sql = "select  transferid,transferNumber organSeg,boxPin openPsd,fromCity,modifyOrganSeg,autoTransfer,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,t.status,t.opoContactName,t.opoContactPhone,t.temperature,t.weather,t.check,b.model boxNo  from transfer t,box b where t.box_id=b.boxId AND t.filterStatus=0 and t.`status`='done' AND  t.`check`=  "
                    + check
                    + "  and t.transferNumber in (select organSeg from transfer_group_temp where  usersIds like '%"
                    + phone
                    + "%') order by t.`status`, t.createAt desc   limit ?,?";
        }


        try {

            ps = conn.prepareStatement(sql);
            // ps.setString(1, phone);
            ps.setInt(1, page * pageSize);
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransferJson t = new TransferJson();
                String transferid = rs.getString("transferid");
                t.setTransferid(rs.getString("transferid") == null ? "" : rs
                        .getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
                        .getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
                        .getString("openPsd"));
                t.setFromCity(rs.getString("fromCity") == null ? "" : rs
                        .getString("fromCity"));
                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
                        : rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ") == null ? "" : rs
                        .getString("organ"));
                t.setOrganNum(rs.getString("organNum") == null ? "" : rs
                        .getString("organNum"));
                t.setBlood(rs.getString("blood") == null ? "" : rs
                        .getString("blood"));
                t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
                        .getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
                        .getString("sampleOrgan"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
                        : rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName") == null ? "" : rs
                        .getString("opoName"));
                t.setContactName(rs.getString("contactName") == null ? "" : rs
                        .getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone") == null ? ""
                        : rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone") == null ? "" : rs
                        .getString("phone"));
                t.setTrueName(rs.getString("trueName") == null ? "" : rs
                        .getString("trueName"));
                t.setGetTime(rs.getString("getTime") == null ? "" : rs
                        .getString("getTime"));
                t.setIsStart(rs.getString("isStart") == null ? "" : rs
                        .getString("isStart"));
                t.setDistance(rs.getString("distance") == null ? "" : rs
                        .getString("distance"));
                t.setStartLong(rs.getString("startLong") == null ? "" : rs
                        .getString("startLong"));
                t.setStartLati(rs.getString("startLati") == null ? "" : rs
                        .getString("startLati"));
                t.setEndLong(rs.getString("endLong") == null ? "" : rs
                        .getString("endLong"));
                t.setEndLati(rs.getString("endLati") == null ? "" : rs
                        .getString("endLati"));
                t.setToHosp(rs.getString("toHosp") == null ? "" : rs
                        .getString("toHosp"));
                // t.setBoxNo(rs.getString("boxNo")==null?"":rs.getString("boxNo"));
                t.setStatus(rs.getString("status") == null ? "" : rs
                        .getString("status"));
                // t.setIsGroup(rs.getString("isGroup")==null?"":rs.getString("isGroup"));
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
                        : rs.getString("modifyOrganSeg"));
                String distance = rs.getString("distance") == null ? "" : rs
                        .getString("distance");
                t.setOpoContactName(rs.getString("opoContactName") == null ? ""
                        : rs.getString("opoContactName"));
                t
                        .setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
                                : rs.getString("opoContactPhone"));
                t.setTemperature(rs.getString("temperature") == null ? "" : rs
                        .getString("temperature"));
                t.setToHosp(rs.getString("weather") == null ? "" : rs
                        .getString("weather"));
                t.setBoxNo(rs.getString("boxNo") == null ? "" : rs
                        .getString("boxNo"));
                t.setCheck(rs.getInt("check"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String currentCity = "";
                String currentTemperature;
                sql = "select distance,recordAt,temperature currentTemperature,currentCity from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' and tr.distance >0 order by tr.recordAt desc limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferid);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    // 修改转运
                    String nowDistance = rsTwo.getString("distance");
                    currentCity = rsTwo.getString("currentCity");
                    currentTemperature = rsTwo.getString("currentTemperature");
                    double dis = Double.parseDouble(distance)
                            - Double.parseDouble(nowDistance);
                    if (dis < 0) {
                        dis = 0.0;
                    }
                    t.setNowDistance(dis + "");

                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        long time = Math.abs(sdf.parse(
                                rsTwo.getString("recordAt")).getTime()
                                - sdf.parse(t.getGetTime()).getTime());


                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "" + t2;
                            } else {
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + "." + secode);
                    } catch (Exception e) {
                        t.setTime("0.0");
                    }
                    t.setDeviceStatus(1);
                } else {
                    currentCity = t.getFromCity().split("市")[0];
                    currentTemperature = "0";
                    // currentTemperature =
                    // rsTwo.getString("currentTemperature");
                    t.setNowDistance(distance);
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    try {
                        long time = Math.abs(sdf.parse(sdf.format(new Date()))
                                .getTime()
                                - sdf.parse(t.getGetTime()).getTime());
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "" + t2;
                            } else {
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + "." + secode);
                        t.setDeviceStatus(0);
                        // 设置为0
                        t.setTime("0.0");
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        t.setTime("0.0");
                    }

                }
                t.setCurrentCity(currentCity);
                t.setCurrentTemperature(currentTemperature);
                DecimalFormat df = new DecimalFormat("#0.00");
                try {
                    String str = df.format(Double.parseDouble(t.getDistance()));
                    t.setDistance(str);
                } catch (Exception e) {
                    t.setDistance("0");
                }
                lists.add(t);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return lists;

    }

    public List<TransferJson> getTransfersHistoryByPhone(String phone,
                                                         String condition, int page, int pageSize) {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        ResultSet rsTwo = null;
        conn = connDB.getConnection();
        // b.model boxNo from transfer t,box b where t.box_id = b.boxid
        String sql = "select DISTINCT(transferid),transferNumber organSeg,boxPin openPsd,modifyOrganSeg,autoTransfer,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp,b.model boxNo,t.status,t.opoContactName,t.opoContactPhone from box b, transfer t,users u,transfer_group_temp tg where t.box_id = b.boxid  AND t.filterStatus=0 and tg.usersIds like '%"
                + phone
                + "%' and tg.organSeg = t.transferNumber  and t.`status`='done' "
                + condition + " order by  t.getTime desc limit ?,?";

        try {

            ps = conn.prepareStatement(sql);
            // ps.setString(1, phone);
            ps.setInt(1, page * pageSize);
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {

                TransferJson t = new TransferJson();
                String transferid = rs.getString("transferid");
                t.setTransferid(rs.getString("transferid") == null ? "" : rs
                        .getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
                        .getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
                        .getString("openPsd"));
                t.setFromCity(rs.getString("fromCity") == null ? "" : rs
                        .getString("fromCity"));
                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
                        : rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ") == null ? "" : rs
                        .getString("organ"));
                t.setOrganNum(rs.getString("organNum") == null ? "" : rs
                        .getString("organNum"));
                t.setBlood(rs.getString("blood") == null ? "" : rs
                        .getString("blood"));
                t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
                        .getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
                        .getString("sampleOrgan"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
                        : rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName") == null ? "" : rs
                        .getString("opoName"));
                t.setContactName(rs.getString("contactName") == null ? "" : rs
                        .getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone") == null ? ""
                        : rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone") == null ? "" : rs
                        .getString("phone"));
                t.setTrueName(rs.getString("trueName") == null ? "" : rs
                        .getString("trueName"));
                t.setGetTime(rs.getString("getTime") == null ? "" : rs
                        .getString("getTime"));
                t.setIsStart(rs.getString("isStart") == null ? "" : rs
                        .getString("isStart"));
                t.setDistance(rs.getString("distance") == null ? "" : rs
                        .getString("distance"));
                t.setStartLong(rs.getString("startLong") == null ? "" : rs
                        .getString("startLong"));
                t.setStartLati(rs.getString("startLati") == null ? "" : rs
                        .getString("startLati"));
                t.setEndLong(rs.getString("endLong") == null ? "" : rs
                        .getString("endLong"));
                t.setEndLati(rs.getString("endLati") == null ? "" : rs
                        .getString("endLati"));
                t.setToHosp(rs.getString("toHosp") == null ? "" : rs
                        .getString("toHosp"));
                t.setBoxNo(rs.getString("boxNo") == null ? "" : rs
                        .getString("boxNo"));
                t.setStatus(rs.getString("status") == null ? "" : rs
                        .getString("status"));
                // t.setIsGroup(rs.getString("isGroup")==null?"":rs.getString("isGroup"));
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
                        : rs.getString("modifyOrganSeg"));
                String distance = rs.getString("distance") == null ? "" : rs
                        .getString("distance");
                t.setOpoContactName(rs.getString("opoContactName") == null ? ""
                        : rs.getString("opoContactName"));
                t
                        .setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
                                : rs.getString("opoContactPhone"));

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                sql = "select distance,recordAt from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' and tr.distance >0 order by tr.recordAt desc limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferid);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    // 修改转运
                    String nowDistance = rsTwo.getString("distance");
                    double dis = Double.parseDouble(distance)
                            - Double.parseDouble(nowDistance);
                    if (dis < 0) {
                        dis = 0.0;
                    }
                    t.setNowDistance(dis + "");

                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        long time = Math.abs(sdf.parse(
                                rsTwo.getString("recordAt")).getTime()
                                - sdf.parse(t.getGetTime()).getTime());
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "0" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "0" + t2;
                            } else {
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "0" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + ":" + secode);
                    } catch (Exception e) {
                        t.setTime("00:00");
                    }
                    t.setDeviceStatus(1);
                } else {
                    t.setNowDistance(distance);
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    try {
                        long time = Math.abs(sdf.parse(sdf.format(new Date()))
                                .getTime()
                                - sdf.parse(t.getGetTime()).getTime());
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "0" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "0" + t2;
                            } else {
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "0" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + ":" + secode);
                        t.setDeviceStatus(0);

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        t.setTime("00:00");
                    }

                }

                lists.add(t);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return lists;

    }

    /**
     * @param
     * @param
     * @param page
     * @param pageSize
     * @return
     */
    public List<TransferJson> getTransfersHistoryByorganSeg(String deviceId,
                                                            String organSeg, int page, int pageSize) {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        ResultSet rsTwo = null;
        conn = connDB.getConnection();
        // b.model boxNo from transfer t,box b where t.box_id = b.boxid
        // ransferNumber='20170905'
        String sql = "select transferid,transferNumber organSeg,boxPin openPsd,modifyOrganSeg,autoTransfer,fromCity,toHospName,tracfficType,tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,trueName,getTime,t.isStart,t.distance,t.startLong,t.startLati,t.endLong,t.endLati,t.toHosp  from transfer t,box b where t.box_id=b.boxid and b.deviceId = ? AND t.filterStatus=0  and t.`status`='done' "
                + (organSeg == null ? "" : "and t.transferNumber like '%" + organSeg + "%' ") + " order by getTime desc limit ?,?";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, deviceId);
            ps.setInt(2, page * pageSize);
            ps.setInt(3, pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransferJson t = new TransferJson();
                t.setTransferid(rs.getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd"));
                t.setFromCity(rs.getString("fromCity"));
                t.setToHospName(rs.getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType"));
                t.setToHospName(rs.getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ"));
                t.setOrganNum(rs.getString("organNum"));
                t.setBlood(rs.getString("blood"));
                t.setBloodNum(rs.getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName"));
                t.setContactName(rs.getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone"));
                t.setTrueName(rs.getString("trueName"));
                t.setGetTime(rs.getString("getTime"));
                t.setIsStart(rs.getString("isStart"));
                t.setDistance(rs.getString("distance"));
                t.setStartLong(rs.getString("startLong"));
                t.setStartLati(rs.getString("startLati"));
                t.setEndLong(rs.getString("endLong"));
                t.setEndLati(rs.getString("endLati"));
                t.setToHosp(rs.getString("toHosp"));
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));

                String distance = rs.getString("distance");
                sql = "select distance from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' order by tr.recordAt desc limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, rs.getString("transferid"));
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String nowDistance = rsTwo.getString("distance");
                    double dis = Double.parseDouble(distance)
                            - Double.parseDouble(nowDistance);
                    if (dis < 0) {
                        dis = 0.0;
                    }
                    t.setNowDistance(dis + "");

                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");
                    try {
                        long time = sdf.parse(rsTwo.getString("recordAt"))
                                .getTime()
                                - sdf.parse(t.getGetTime()).getTime();
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "0" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "0" + t2;
                            } else {
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "0" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + ":" + secode);
                    } catch (Exception e) {
                        t.setTime("00:00");
                    }
                } else {
                    t.setNowDistance("0");
                    t.setTime("00:00");
                }

                lists.add(t);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return lists;

    }

    public TransferJson getTransferByOrganSeg(String organSeg) {
        TransferJson t = null;
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,modifyOrganSeg,autoTransfer,"
                + "tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,opoContactName,opoContactPhone,t.phone,"
                + "trueName,getTime,t.isStart,t.startLong,t.startLati,t.endLong,t.endLati,t.distance,t.toHosp,b.model boxNo,t.isGroup from transfer t,box b where t.box_id = b.boxid and transferNumber = ?";
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            while (rs.next()) {
                t = new TransferJson();
                t.setTransferid(rs.getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd"));
                t.setFromCity(rs.getString("fromCity"));
                t.setToHospName(rs.getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType"));
                t.setToHospName(rs.getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ"));
                t.setOrganNum(rs.getString("organNum"));
                t.setBlood(rs.getString("blood"));
                t.setBloodNum(rs.getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName"));
                t.setContactName(rs.getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone"));
                t.setTrueName(rs.getString("trueName"));
                t.setGetTime(rs.getString("getTime"));
                t.setIsStart(rs.getString("isStart"));
                t.setStartLong(rs.getString("startLong"));
                t.setStartLati(rs.getString("startLati"));
                t.setEndLong(rs.getString("endLong"));
                t.setEndLati(rs.getString("endLati"));
                t.setDistance(rs.getString("distance"));
                t.setToHosp(rs.getString("toHosp"));
                t.setBoxNo(rs.getString("boxNo"));
                t.setIsGroup(rs.getString("isGroup"));
                t.setOpoContactName(rs.getString("opoContactName"));
                t.setOpoContactPhone(rs.getString("opoContactPhone"));
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));
                String distance = rs.getString("distance");
                sql = "select distance from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' order by tr.recordAt desc limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, rs.getString("transferid"));
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String nowDistance = rsTwo.getString("distance");
                    double dis = Double.parseDouble(distance)
                            - Double.parseDouble(nowDistance);
                    if (dis < 0) {
                        dis = 0.0;
                    }
                    t.setNowDistance(dis + "");

                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");
                    try {
                        long time = sdf.parse(rsTwo.getString("recordAt"))
                                .getTime()
                                - sdf.parse(t.getGetTime()).getTime();
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "0" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "0" + t2;
                            } else {
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "0" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + ":" + secode);
                    } catch (Exception e) {
                        t.setTime("00:00");
                    }
                } else {
                    t.setNowDistance("0");
                    t.setTime("00:00");
                }

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return t;

    }

    public TransferJson getTransferByTransferId(String transferId) {
        TransferJson t = null;
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,modifyOrganSeg,autoTransfer,"
                + "tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,t.phone,"
                + "trueName,getTime,t.isStart,t.startLong,t.startLati,t.endLong,t.endLati,t.distance,t.toHosp,b.model boxNo,t.`status`,tgt.tid from transfer t,box b,transfer_group_temp tgt where t.box_id = b.boxid and transferid = ? and tgt.organSeg = t.transferNumber";
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, transferId);
            rs = ps.executeQuery();
            while (rs.next()) {
                t = new TransferJson();


                t.setTransferid(rs.getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd"));
                t.setFromCity(rs.getString("fromCity"));
                t.setToHospName(rs.getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType"));
                t.setToHospName(rs.getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ"));
                t.setOrganNum(rs.getString("organNum"));
                t.setBlood(rs.getString("blood"));
                t.setBloodNum(rs.getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName"));
                t.setContactName(rs.getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone"));
                t.setTrueName(rs.getString("trueName"));
                t.setGetTime(rs.getString("getTime"));
                t.setIsStart(rs.getString("isStart"));
                t.setStartLong(rs.getString("startLong"));
                t.setStartLati(rs.getString("startLati"));
                t.setEndLong(rs.getString("endLong"));
                t.setEndLati(rs.getString("endLati"));
                t.setDistance(rs.getString("distance"));
                t.setToHosp(rs.getString("toHosp"));
                t.setBoxNo(rs.getString("boxNo"));
                t.setStatus(rs.getString("status"));
                String distance = rs.getString("distance");
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));
                t.setTid(rs.getString("tid") == null ? "" : rs.getString("tid"));
                sql = "select distance from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0' order by tr.recordAt desc limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, transferId);
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String nowDistance = rsTwo.getString("distance");
                    double dis = Double.parseDouble(distance)
                            - Double.parseDouble(nowDistance);
                    if (dis < 0) {
                        dis = 0.0;
                    }
                    t.setNowDistance(dis + "");

                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm");
                    try {
                        long time = sdf.parse(rsTwo.getString("recordAt"))
                                .getTime()
                                - sdf.parse(t.getGetTime()).getTime();
                        String secode = "";
                        String minus = "";
                        if (time / 1000 > 3600) {
                            long t1 = time / 1000 % 3600;
                            t1 = t1 / 60;
                            if (t1 < 10) {
                                secode = "0" + t1;
                            } else {
                                secode = t1 + "";
                            }
                        } else {
                            long t2 = time / 1000 / 60;
                            if (t2 < 10) {
                                secode = "0" + t2;
                            } else {
                                secode = t2 + "";
                            }
                        }
                        if ((time / 1000 / 60 / 60) < 10) {
                            minus = "0" + (time / 1000 / 60 / 60);
                        } else {
                            minus = "" + (time / 1000 / 60 / 60);
                        }
                        t.setTime(minus + ":" + secode);
                    } catch (Exception e) {
                        t.setTime("00:00");
                    }

                } else {
                    t.setNowDistance("0");
                    t.setTime("00:00");
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return t;

    }

    /**
     * 根据设备的标识获取转运信息
     *
     * @param
     * @return
     */
    public boolean transferDown(String organSeg) {
        TransferJson t = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select id from transfer where transferNumber = ? and `status` = 'done'";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            while (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return false;

    }

    /**
     * 获取暂停的转运
     *
     * @param
     * @return
     */
    public boolean getTransferStatus(String organSeg) {
        TransferJson t = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select isStart from transfer where transferNumber = ? and isStart=0 ";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            while (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return false;

    }

    public void insertDevice(String deviceId) {
        TransferJson t = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "INSERT INTO  power_device(device_id) VALUES(?)";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, deviceId);
            ps.executeUpdate();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }


    public boolean isPowerDevice(String deviceId) {

        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        SimpleDateFormat sdfSort = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String oldTime = sdf.format(new Date(new Date().getTime() - 24 * 60 * 60 * 1000));
        String sql = "SELECT * FROM power_device WHERE create_time LIKE '%" + sdfSort.format(new Date()) + "%' AND device_id=?";

        try {
            //System.out.println("时间：" + oldTime);
            ps = conn.prepareStatement(sql);
            ps.setString(1, deviceId);
            rs = ps.executeQuery();
            if (rs.next()) {
                //System.out.println("power_device表已保存有");
                return false;
            } else {
                sql = "SELECT transferStatus from box WHERE deviceId = ? ";
                ps = conn.prepareStatement(sql);
                ps.setString(1, deviceId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    if ("transfering".equals(rs.getString("transferStatus"))) {
                        return false;
                    }
                }
                sql = "SELECT count(id) c  FROM transferRecord WHERE recordAt >= ? AND  remark = ? AND transfer_id <> '1' ";
                ps = conn.prepareStatement(sql);
                ps.setString(1, oldTime);
                ps.setString(2, deviceId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    if (rs.getInt("c") == 0) {


                        sql = "SELECT count(id) c  FROM transferRecord WHERE recordAt >= ? AND  remark = ? ";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, oldTime);
                        ps.setString(2, deviceId);
                        rsTwo = ps.executeQuery();
                        if (rsTwo.next()) {

                            if (rsTwo.getInt("c") > (120 * 23)) {
                                System.out.println("transferRecord表没有转运，数量：" + rsTwo.getInt("c") + "," + deviceId);
                                return true;
                            }
                        }

                    } else {
                        //System.out.println("transferRecord表在24小时已经有转运，数量：" + rs.getInt("c"));
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);

        }
        return false;
    }

    public boolean overTransferTime(String organSeg) {
        TransferJson t = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select getTime from transfer where transferNumber = ?";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String getTime = rs.getString("getTime");

                long oldTime = sdf.parse(getTime).getTime();
                long newTime = new Date().getTime();
                // modify *10去掉
                long time = 1000 * 60 * 60 * 24;
                if (newTime - oldTime > time && !getTime.contains("2010")) {
                    return true;
                } else {
                    return false;
                }

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return false;
    }

    /**
     * 根据设备的标识获取转运信息
     *
     * @param deviceId
     * @return
     */
    public TransferJson getTransferByDeviceId(String deviceId) {
        TransferJson t = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,tracfficType,modifyOrganSeg,autoTransfer,modifyOrganSeg,"
                + "tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,opoContactName,opoContactPhone,t.phone,"
                + "trueName,getTime,t.isStart,t.startLong,t.startLati,t.endLong,t.endLati,t.distance,t.toHosp,b.model boxNo from transfer t,box b where t.box_id = b.boxid and b.deviceId = ? and t.status='transfering' ";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, deviceId);
            rs = ps.executeQuery();
            while (rs.next()) {
                t = new TransferJson();
                t.setTransferid(rs.getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd"));
                t.setFromCity(rs.getString("fromCity"));
                t.setToHospName(rs.getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType"));
                t.setToHospName(rs.getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ"));
                t.setOrganNum(rs.getString("organNum"));
                t.setBlood(rs.getString("blood"));
                t.setBloodNum(rs.getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName"));
                t.setContactName(rs.getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone"));
                t.setTrueName(rs.getString("trueName"));
                t.setGetTime(rs.getString("getTime"));
                t.setIsStart(rs.getString("isStart"));
                t.setStartLong(rs.getString("startLong"));
                t.setStartLati(rs.getString("startLati"));
                t.setEndLong(rs.getString("endLong"));
                t.setEndLati(rs.getString("endLati"));
                t.setDistance(rs.getString("distance"));
                t.setToHosp(rs.getString("toHosp"));
                t.setBoxNo(rs.getString("boxNo"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));
                t.setOpoContactName(rs.getString("opoContactName"));
                t.setOpoContactPhone(rs.getString("opoContactPhone"));
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg"));
            }
            if (t != null) {
                String organSeg = t.getOrganSeg();
                sql = "SELECT usersIds from transfer_group_temp where organSeg = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, organSeg);
                rs = ps.executeQuery();
                if (rs.next()) {
                    t.setPhones(rs.getString("usersIds"));
                }
            } else {
                // 改为空闲状态
                sql = "UPDATE box SET transferStatus = 'free' WHERE deviceId=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, deviceId);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return t;

    }

    public List<TransferJson> getTransfersByOrganSegTid(String tid) {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        ResultSet rsTwo = null;
        String sql = "select transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,toHosp,tracfficType,modifyOrganSeg,autoTransfer,"
                + "tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,opoContactName,opoContactPhone,t.phone,"
                + "trueName,getTime,t.isStart,t.startLong,t.startLati,t.endLong,t.endLati,t.distance,b.model boxNo,t.isGroup from transfer t,box b,transfer_group_temp tgt where t.box_id = b.boxid  AND t.filterStatus=0 and tgt.organSeg = t.transferNumber and tgt.tid = ?";
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, tid);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransferJson t = new TransferJson();
                t.setTransferid(rs.getString("transferid") == null ? "" : rs
                        .getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
                        .getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
                        .getString("openPsd"));
                t.setFromCity(rs.getString("fromCity") == null ? "" : rs
                        .getString("fromCity"));
                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setToHosp(rs.getString("toHosp") == null ? "" : rs
                        .getString("toHosp"));

                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
                        : rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ") == null ? "" : rs
                        .getString("organ"));
                t.setOrganNum(rs.getString("organNum") == null ? "" : rs
                        .getString("organNum"));
                t.setBlood(rs.getString("blood") == null ? "" : rs
                        .getString("blood"));
                t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
                        .getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
                        .getString("sampleOrgan"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
                        : rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName") == null ? "" : rs
                        .getString("opoName"));
                t.setContactName(rs.getString("contactName") == null ? "" : rs
                        .getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone") == null ? ""
                        : rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone") == null ? "" : rs
                        .getString("phone"));
                t.setTrueName(rs.getString("trueName") == null ? "" : rs
                        .getString("trueName"));
                t.setGetTime(rs.getString("getTime") == null ? "" : rs
                        .getString("getTime"));
                t.setIsStart(rs.getString("isStart") == null ? "" : rs
                        .getString("isStart"));
                t.setStartLong(rs.getString("startLong") == null ? "" : rs
                        .getString("startLong"));
                t.setStartLati(rs.getString("startLati") == null ? "" : rs
                        .getString("startLati"));
                t.setEndLong(rs.getString("endLong") == null ? "" : rs
                        .getString("endLong"));
                t.setEndLati(rs.getString("endLati") == null ? "" : rs
                        .getString("endLati"));
                t.setDistance(rs.getString("distance") == null ? "" : rs
                        .getString("distance"));
                t.setBoxNo(rs.getString("boxNo") == null ? "" : rs
                        .getString("boxNo"));
                t.setIsGroup(rs.getString("isGroup") == null ? "" : rs
                        .getString("isGroup"));
                t.setOpoContactName(rs.getString("opoContactName") == null ? ""
                        : rs.getString("opoContactName"));
                t
                        .setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
                                : rs.getString("opoContactPhone"));
                String distance = rs.getString("distance") == null ? "" : rs
                        .getString("distance");
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
                        : rs.getString("modifyOrganSeg"));

                sql = "select distance,recordAt from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0'  order by tr.recordAt desc limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, rs.getString("transferid"));
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String nowDistance = rsTwo.getString("distance");
                    double dis = Double.parseDouble(distance)
                            - Double.parseDouble(nowDistance);
                    if (dis < 0) {
                        dis = 0.0;
                    }
                    t.setNowDistance(dis + "");

                } else {
                    t.setNowDistance("0");
                }

                lists.add(t);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return lists;

    }

    public List<TransferJson> getTransfersByOrganSeg(String organSeg) {
        List<TransferJson> lists = new ArrayList<TransferJson>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        ResultSet rsTwo = null;
        String sql = "select tgt.usersIds phones, transferid,transferNumber organSeg,boxPin openPsd,fromCity,toHospName,toHosp,tracfficType,modifyOrganSeg,autoTransfer,"
                + "tracfficNumber,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,opoName,contactName,contactPhone,opoContactName,opoContactPhone,t.phone,"
                + "trueName,getTime,t.isStart,t.startLong,t.startLati,t.endLong,t.endLati,t.distance,b.model boxNo,t.isGroup,tgt.tid from transfer t,box b,transfer_group_temp tgt where t.box_id = b.boxid  AND t.filterStatus=0 and transferNumber = ? AND transferNumber=tgt.organSeg ";
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            while (rs.next()) {
                TransferJson t = new TransferJson();
                t.setTransferid(rs.getString("transferid") == null ? "" : rs
                        .getString("transferid"));
                t.setOrganSeg(rs.getString("organSeg") == null ? "" : rs
                        .getString("organSeg"));
                t.setOpenPsd(rs.getString("openPsd") == null ? "" : rs
                        .getString("openPsd"));
                t.setFromCity(rs.getString("fromCity") == null ? "" : rs
                        .getString("fromCity"));
                t.setToHospName(rs.getString("toHospName") == null ? "" : rs
                        .getString("toHospName"));
                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setToHosp(rs.getString("toHosp") == null ? "" : rs
                        .getString("toHosp"));

                t.setTracfficType(rs.getString("tracfficType") == null ? ""
                        : rs.getString("tracfficType"));
                t.setTracfficNumber(rs.getString("tracfficNumber") == null ? ""
                        : rs.getString("tracfficNumber"));
                t.setOrgan(rs.getString("organ") == null ? "" : rs
                        .getString("organ"));
                t.setOrganNum(rs.getString("organNum") == null ? "" : rs
                        .getString("organNum"));
                t.setBlood(rs.getString("blood") == null ? "" : rs
                        .getString("blood"));
                t.setBloodNum(rs.getString("bloodNum") == null ? "" : rs
                        .getString("bloodNum"));
                t.setSampleOrgan(rs.getString("sampleOrgan") == null ? "" : rs
                        .getString("sampleOrgan"));
                t.setSampleOrganNum(rs.getString("sampleOrganNum") == null ? ""
                        : rs.getString("sampleOrganNum"));
                t.setOpoName(rs.getString("opoName") == null ? "" : rs
                        .getString("opoName"));
                t.setContactName(rs.getString("contactName") == null ? "" : rs
                        .getString("contactName"));
                t.setContactPhone(rs.getString("contactPhone") == null ? ""
                        : rs.getString("contactPhone"));
                t.setPhone(rs.getString("phone") == null ? "" : rs
                        .getString("phone"));
                t.setTrueName(rs.getString("trueName") == null ? "" : rs
                        .getString("trueName"));
                t.setGetTime(rs.getString("getTime") == null ? "" : rs
                        .getString("getTime"));
                t.setIsStart(rs.getString("isStart") == null ? "" : rs
                        .getString("isStart"));
                t.setStartLong(rs.getString("startLong") == null ? "" : rs
                        .getString("startLong"));
                t.setStartLati(rs.getString("startLati") == null ? "" : rs
                        .getString("startLati"));
                t.setEndLong(rs.getString("endLong") == null ? "" : rs
                        .getString("endLong"));
                t.setEndLati(rs.getString("endLati") == null ? "" : rs
                        .getString("endLati"));
                t.setDistance(rs.getString("distance") == null ? "" : rs
                        .getString("distance"));
                t.setBoxNo(rs.getString("boxNo") == null ? "" : rs
                        .getString("boxNo"));
                t.setIsGroup(rs.getString("isGroup") == null ? "" : rs
                        .getString("isGroup"));
                t.setOpoContactName(rs.getString("opoContactName") == null ? ""
                        : rs.getString("opoContactName"));
                t
                        .setOpoContactPhone(rs.getString("opoContactPhone") == null ? ""
                                : rs.getString("opoContactPhone"));
                String distance = rs.getString("distance") == null ? "" : rs
                        .getString("distance");
                t.setAutoTransfer(rs.getInt("autoTransfer"));
                t.setModifyOrganSeg(rs.getString("modifyOrganSeg") == null ? ""
                        : rs.getString("modifyOrganSeg"));
                t.setPhones(rs.getString("phones") == null ? "" : rs
                        .getString("phones"));
                t.setTid(rs.getString("tid") == null ? "" : rs
                        .getString("tid"));
                sql = "select distance,recordAt from transferRecord tr where tr.transfer_id = ? and tr.longitude <> '0.0'  order by tr.recordAt desc limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, rs.getString("transferid"));
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    String nowDistance = rsTwo.getString("distance");
                    double dis = Double.parseDouble(distance)
                            - Double.parseDouble(nowDistance);
                    if (dis < 0) {
                        dis = 0.0;
                    }
                    t.setNowDistance(dis + "");

                } else {
                    t.setNowDistance("0");
                }

                lists.add(t);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return lists;

    }

    public int updateStartByOrganSeg(String organSeg, String isStart,
                                     String type) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        int isOK = -1;
        conn = connDB.getConnection();
        String sql = "update transfer set isStart = ?,getTime=? where transferNumber = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, isStart);
            ps.setString(2, new SimpleDateFormat("yyyy-MM-dd HH:mm")
                    .format(new Date()));
            ps.setString(3, organSeg);

            isOK = ps.executeUpdate();

            // 查询群组名,分割整理
            String groupSqlQuery = "select groupName from transfer_group_temp where organSeg = ? ";
            ps = conn.prepareStatement(groupSqlQuery);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            String groupName = "";
            String newGroupName = "";
            if (rs.next()) {
                groupName = rs.getString("groupName");
            }
            if (groupName.contains("-")) {
                String str = "转运中-";
                if ("pause".equals(type)) {
                    str = "转运中-";
                } else {
                    str = "转运中-";
                }
                newGroupName = str
                        + groupName.substring(
                        groupName.split("-")[0].length() + 1, groupName
                                .length());
            }

            // 修改群组名
            String updateGroupSql = "update transfer_group_temp set groupName = ? where organSeg = ?";
            ps = conn.prepareStatement(updateGroupSql);
            ps.setString(1, newGroupName);
            ps.setString(2, organSeg);
            ps.executeUpdate();

            return isOK;

        } catch (SQLException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
            isOK = -1;
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return isOK;
    }

    public String gainToHospId(String organSeg)
            throws UnsupportedEncodingException {
        ResultSet rs = null;
        PreparedStatement ps = null;
        int isOK = -1;
        String transerId = "";
        String toHospId = "";
        ResultSet rsTwo = null;
        conn = connDB.getConnection();
        String sql = "SELECT to_hosp_id,id,trueName,phone,fromCity,toHospName,getTime FROM transfer where transferNumber = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);

            ps.setString(1, organSeg);

            rs = ps.executeQuery();
            if (rs.next()) {
                transerId = rs.getString("id");
                toHospId = rs.getString("to_hosp_id");

                if (!"d08f64da-7834-4eeb-93c9-d0b29318a29e".equals(toHospId)) {

                    sql = "SELECT * FROM sms_email WHERE transfer_id=?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, transerId);
                    rsTwo = ps.executeQuery();

                    if (rsTwo.next()) {

                    } else {
                        String dataUsageContent = "红包已提交审核。转运信息为，转运人:"
                                + rs.getString("trueName") + ",转运人电话:"
                                + rs.getString("phone") + ",起始地:"
                                + rs.getString("fromCity") + ",目的地医院:"
                                + rs.getString("toHospName") + ",转运时间:"
                                + rs.getString("getTime");

                        sql = "insert into sms_email(`name`,email,transfer_id,time) values(?,?,?,?)";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, dataUsageContent);
                        ps.setString(2, "cy@lifeperfusor.com,cyy@lifeperfusor.com");
                        ps.setString(3, transerId);
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy-MM-DD HH:mm:ss");
                        ps.setString(4, sdf.format(new Date()));

                        ps.executeUpdate();

                        SmsDao.send("转运红包审核", "转运单号：" + transerId,
                                dataUsageContent,
                                "cy@lifeperfusor.com,cyy@lifeperfusor.com", "");

                    }
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
            isOK = -1;
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return "";
    }

    public int updateCheck(String organSeg, int check) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        int isOK = -1;
        conn = connDB.getConnection();
        String sql = "update transfer set `check` = ? where transferNumber = ?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setInt(1, check);
            ps.setString(2, organSeg);

            isOK = ps.executeUpdate();

            return isOK;

        } catch (SQLException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
            isOK = -1;
            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);

        }
        return isOK;
    }

    public static void main(String[] args) {
        //new TransferDao().updateTransferStatus("LPA20190921160811","99999");
        new TransferDao().getTransferNoFinish();
    }

    public int updateTransferStatus(String organSeg, String boxNo) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        int isOK = 0;
        int isTwo = 0;
        int isThree = 0;
        conn = connDB.getConnection();

        // 调用SQL
        try {
            // 修改转运状态
            String sql = "update transfer set `status` = 'done'  where transferNumber = ?";
            //conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            isOK = ps.executeUpdate();


            // 查询群组名,分割整理
            String groupSqlQuery = "select groupName from transfer_group_temp where organSeg = ? ";
            ps = conn.prepareStatement(groupSqlQuery);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            String groupName = "";
            String newGroupName = "";
            if (rs.next()) {
                groupName = rs.getString("groupName");
            }
            if (groupName.contains("-")) {

                newGroupName = "已转运-"
                        + groupName.substring(
                        groupName.split("-")[0].length() + 1, groupName
                                .length());
            }

            // 修改群组名
            String updateGroupSql = "update transfer_group_temp set groupName = ? where organSeg = ?";
            ps = conn.prepareStatement(updateGroupSql);
            ps.setString(1, newGroupName);
            ps.setString(2, organSeg);
            isTwo = ps.executeUpdate();


            String updateBoxSql = "update box set transferStatus = 'free' where model = ?";

            ps = conn.prepareStatement(updateBoxSql);
            ps.setString(1, boxNo);
            isThree = ps.executeUpdate();


            //保存详情记录到excel
            sql = "SELECT  id,trueName,toHospName,transferNumber,to_hosp_id FROM  transfer WHERE transferNumber = ? ";
            ps = conn.prepareStatement(sql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            int transferId = 0;
            String trueName = "";
            String toHospName = "";
            String transferNumber = "";
            String toHospId = "";
            if (rs.next()) {
                transferId = rs.getInt("id");
                trueName = rs.getString("trueName");
                toHospName = rs.getString("toHospName");
                transferNumber = rs.getString("transferNumber");
                toHospId = rs.getString("to_hosp_id");
            }
            if (!"d08f64da-7834-4eeb-93c9-d0b29318a29e".equals(toHospId)) {
                sql = "SELECT  * FROM  transferRecord WHERE  transfer_id=?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, transferId);
                rs = ps.executeQuery();
                List<TransferRecord> recordList = new ArrayList<>();
                while (rs.next()) {
                    TransferRecord t = new TransferRecord();
                    t.setTransferRecordId(rs.getInt("id"));
                    t.setAvgTemperature(rs.getString("avgTemperature"));
                    t.setCreateAt(rs.getString("createAt"));
                    t.setCurrentCity(rs.getString("currentCity"));
                    t.setDbStatus(rs.getString("dbStatus"));
                    t.setTrueTemperature(rs.getString("trueTemperature"));
                    t.setDistance(rs.getString("distance"));
                    t.setDuration(rs.getString("duration"));
                    t.setExpendPower(rs.getString("expendPower"));
//				t.setFlow1(rs.getString("flow1"));
//				t.setFlow2(rs.getString("flow2"));
                    t.setHumidity(rs.getString("humidity"));
                    t.setLatitude(rs.getString("latitude"));
                    t.setLongitude(rs.getString("longitude"));
                    t.setModifyAt(rs.getString("modifyAt"));
                    t.setPower(rs.getString("power"));
//				t.setPress1(rs.getString("press1"));
//				t.setPress2(rs.getString("press2"));
                    t.setRecordAt(rs.getString("recordAt"));
                    t.setRemark(rs.getString("remark"));

                    DecimalFormat df = new DecimalFormat("######0"); // 四色五入转换成整数
                    String temperature = rs.getString("temperature").substring(0,
                            rs.getString("temperature").indexOf(".", 0) + 2);
                    t.setTemperature(temperature);
                    t.setTransfer_id(rs.getString("transfer_id"));
                    t.setType(rs.getString("type"));
//				t.setPupple(rs.getString("pupple"));
//				t.setStopping_power1(rs.getString("stopping_power1"));
//				t.setStopping_power2(rs.getString("stopping_power2"));
                    t.setCollision(rs.getString("collision"));
                    t.setOpen(rs.getString("open"));
                    t.setOther(rs.getString("other"));
                    recordList.add(t);
                }
                if (recordList != null && recordList.size() > 0) {
                    //获取数据

                    String content[][] = new String[recordList.size() + 1][];

                    //excel标题
                    String[] title = {"id", "avgTemperature", "createAt", "currentCity", "dbStatus",
                            "trueTemperature", "distance", "duration", "expendPower", "humidity",
                            "latitude", "longitude", "modifyAt", "power", "recordAt", "remark"
                            , "temperature", "transfer_id", "type", "collision", "open", "other"
                    };
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    //excel文件名


                    //sheet名
                    String sheetName = "转运详情记录";

                    for (int i = 0; i < recordList.size(); i++) {
                        content[i] = new String[title.length];


                        content[i][0] = recordList.get(i).getTransfer_id();
                        content[i][1] = recordList.get(i).getAvgTemperature();
                        content[i][2] = recordList.get(i).getCreateAt();
                        content[i][3] = recordList.get(i).getCurrentCity();
                        content[i][4] = recordList.get(i).getDbStatus();
                        content[i][5] = recordList.get(i).getTrueTemperature();
                        content[i][6] = recordList.get(i).getDistance();
                        content[i][7] = recordList.get(i).getDuration();
                        content[i][8] = recordList.get(i).getExpendPower();
                        content[i][9] = recordList.get(i).getHumidity();
                        content[i][10] = recordList.get(i).getLatitude();
                        content[i][11] = recordList.get(i).getLongitude();
                        content[i][12] = recordList.get(i).getModifyAt();
                        content[i][13] = recordList.get(i).getPower();
                        content[i][14] = recordList.get(i).getRecordAt();
                        content[i][15] = recordList.get(i).getRemark();
                        content[i][16] = recordList.get(i).getTemperature();
                        content[i][17] = recordList.get(i).getTransfer_id();
                        content[i][18] = recordList.get(i).getType();
                        content[i][19] = recordList.get(i).getCollision();
                        content[i][20] = recordList.get(i).getOpen();
                        content[i][21] = recordList.get(i).getOther();
                    }


                    //创建HSSFWorkbook
                    HSSFWorkbook workbook = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
                    String savePath = "/usr/share/tomcat7/webapps/transbox/download/" + transferNumber + ".xls";
                    //String upload = httpServlet.getServletContext().getRealPath(File.separator + "images" + File.separator);
                    //响应到客户端
                    try {
                        FileOutputStream fos = new FileOutputStream(new File(savePath));
                        workbook.write(fos);
                        workbook.close();
                        fos.close();
                        System.out.println("生成excel文档成功");
                        String dataUsageContent = "箱号：" + boxNo + ",转运人:"
                                + trueName + ",目的地医院:" + toHospName;


                        sql = "insert into sms_email(`name`,email,transfer_id,time,`type`) values(?,?,?,?,?)";
                        //conn = connDB.getConnection();
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, dataUsageContent);
                        ps.setString(2, "cy@lifeperfusor.com");
                        ps.setString(3, transferId + "");
                        SimpleDateFormat sdfTemp = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss");
                        ps.setString(4, sdfTemp.format(new Date()));
                        ps.setString(5, "stop");
                        ps.executeUpdate();

                        SmsDao.send("转运原始数据", "转运单号：" + transferId,
                                dataUsageContent,
                                "cy@lifeperfusor.com", savePath);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

//            if (isOK == 0 ) {
//                conn.rollback();
//                isOK = 0;
//            } else {
//                conn.commit();
//            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

            CONST.ERROR = e.getMessage();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return isOK;
    }

    // public static void main(String[] args) {
    // String groupName = "转远在-胜多负少";
    // if (groupName.contains("-")) {
    //
    // String newGroupName = groupName.substring(groupName.split("-")[0]
    // .length() + 1, groupName.length());
    //
    // }
    //
    // }

    public List<Map<String, String>> getTransfers() {

        List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select t.transferid t_transferid,t.transferNumber t_transferNumber,t.organCount t_organCount,t.boxPin t_boxPin, t.fromCity t_fromCity,t.toHospName t_toHospName,t.tracfficType t_tracfficType,t.deviceType t_deviceType,DATE_FORMAT(t.getOrganAt,'%Y-%m-%d') t_getOrganAt,DATE_FORMAT(t.startAt,'%Y-%m-%d') t_startAt,DATE_FORMAT(t.endAt,'%Y-%m-%d') t_endAt,t.`status` t_status,t.createAt t_createAt,b.boxid b_boxid,b.deviceId b_deviceId,b.qrcode b_qrcode,b.model b_model,b.transferStatus b_transferStatus,b.`status` b_status,o.organid o_organid,o.segNumber o_segNumber,o.type o_type,o.bloodType o_bloodType,o.bloodSampleCount o_bloodSampleCount,o.organizationSampleType o_organizationSampleType,o.organizationSampleCount o_organizationSampleCount,h.hospitalid h_hospitalid,h.`name` h_name,h.district h_district,h.address h_address,h.grade h_grade,h.remark h_remark,h.`status` h_status,h.account_id h_account_id,tp.transferPersonid tp_transferPersonid,tp.`name` tp_name,tp.phone tp_phone,tp.organType tp_organType,op.opoid op_opoid,op.`name` op_name,op.district op_district,op.address op_address,op.grade op_grade,op.contactPerson op_contactPerson,op.contactPhone op_contactPhone,op.remark op_remark from transfer t,organ o,box b,hospital h,transferPerson tp,opo op where t.dbStatus = 'N'  and b.boxid = t.box_id and h.hospitalid = t.to_hosp_id AND t.filterStatus=0  and o.organid = t.organ_id and tp.transferPersonid = t.transferPerson_id and op.opoid = t.opo_id and t.status = 'transfering'  ORDER BY t.createAt ";

        try {

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, String> maps = new HashMap<String, String>();
                maps.put("t_transferid", rs.getString("t_transferid"));
                maps.put("t_transferNumber", rs.getString("t_transferNumber"));
                maps.put("t_organCount", rs.getString("t_organCount"));
                maps.put("t_boxPin", rs.getString("t_boxPin"));
                maps.put("t_fromCity", rs.getString("t_fromCity"));
                maps.put("t_toHospName", rs.getString("t_toHospName"));
                maps.put("t_tracfficType", rs.getString("t_tracfficType"));
                maps.put("t_deviceType", rs.getString("t_deviceType"));
                maps.put("t_getOrganAt", rs.getString("t_getOrganAt"));
                maps.put("t_startAt", rs.getString("t_startAt"));
                maps.put("t_endAt", rs.getString("t_endAt"));
                maps.put("t_status", rs.getString("t_status"));
                maps.put("t_createAt", rs.getString("t_createAt"));
                maps.put("b_boxid", rs.getString("b_boxid"));
                maps.put("b_deviceId", rs.getString("b_deviceId"));
                maps.put("b_qrcode", rs.getString("b_qrcode"));
                maps.put("b_model", rs.getString("b_model"));
                maps.put("b_transferStatus", rs.getString("b_transferStatus"));
                maps.put("b_status", rs.getString("b_status"));
                maps.put("o_organid", rs.getString("o_organid"));
                maps.put("o_segNumber", rs.getString("o_segNumber"));
                maps.put("o_type", rs.getString("o_type"));
                maps.put("o_bloodType", rs.getString("o_bloodType"));
                maps.put("o_bloodSampleCount", rs
                        .getString("o_bloodSampleCount"));
                maps.put("o_organizationSampleType", rs
                        .getString("o_organizationSampleType"));
                maps.put("o_organizationSampleCount", rs
                        .getString("o_organizationSampleCount"));
                maps.put("h_hospitalid", rs.getString("h_hospitalid"));
                maps.put("h_name", rs.getString("h_name"));
                maps.put("h_district", rs.getString("h_district"));
                maps.put("h_address", rs.getString("h_address"));
                maps.put("h_grade", rs.getString("h_grade"));
                maps.put("h_remark", rs.getString("h_remark"));
                maps.put("h_status", rs.getString("h_status"));
                maps.put("h_account_id", rs.getString("h_account_id"));
                maps.put("tp_transferPersonid", rs
                        .getString("tp_transferPersonid"));
                maps.put("tp_name", rs.getString("tp_name"));
                maps.put("tp_phone", rs.getString("tp_phone"));
                maps.put("tp_organType", rs.getString("tp_organType"));
                maps.put("op_opoid", rs.getString("op_opoid"));
                maps.put("op_name", rs.getString("op_name"));
                maps.put("op_district", rs.getString("op_district"));
                maps.put("op_address", rs.getString("op_address"));
                maps.put("op_grade", rs.getString("op_grade"));
                maps.put("op_contactPerson", rs.getString("op_contactPerson"));
                maps.put("op_contactPhone", rs.getString("op_contactPhone"));
                maps.put("op_remark", rs.getString("op_remark"));
                lists.add(maps);

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
     * 获取opo信息
     *
     * @return
     */
    public List<OpoInfo> getOpoList() {
        List<OpoInfo> lists = new ArrayList<OpoInfo>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select name from opo";
        try {

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                OpoInfo opo = new OpoInfo();
                opo.setName(rs.getString("name"));
                lists.add(opo);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return lists;
        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return lists;
    }

    /**
     * 查询是否存在群组 不存在就创建本医院群组,创建群组后发送默认消息 存在就判断当前人员是否存在群组中 存在就发送默认消息
     * 不存在就加入群组,发默认消息
     *
     * @param phone
     * @param hospitalName
     */
    public void createHospitalGroup(String phone, String hospitalName,
                                    String trueName) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        try {
            // 查询是否存在群组
            GroupUserQueryResult groupQueryUserResult = rongCloud.group
                    .queryUser(hospitalName);
            System.out
                    .println("queryUser:  " + groupQueryUserResult.toString());
            RongUserJson rongUserJson = new Gson().fromJson(
                    groupQueryUserResult.toString(), RongUserJson.class);

            if (rongUserJson.getUsers().size() > 0) {
                // 判断当前人员是否存在群组中
                if (groupQueryUserResult.toString().contains(phone)) {
                    // 发送默认消息

                } else {
                    // 加入群组
                    String[] groupJoinUserId = {phone};
                    CodeSuccessResult groupJoinResult = rongCloud.group.join(
                            groupJoinUserId, hospitalName, "*" + hospitalName
                                    + "*");


                    // 发送默认消息

                }

            } else {
                String sql = "SELECT phone FROM users u,hospital h WHERE u.hospital_id = h.hospitalid AND h.`name`=?";

                ps = conn.prepareStatement(sql);
                ps.setString(1, hospitalName);
                rs = ps.executeQuery();
                List<String> lists = new ArrayList<String>();
                while (rs.next()) {
                    lists.add(rs.getString("phone"));

                }
                String phones[] = new String[lists.size()];
                for (int i = 0; i < lists.size(); i++) {
                    phones[i] = lists.get(i);
                }

                // 创建本医院群组
                rongCloud.group.create(phones, hospitalName, "--"
                        + hospitalName + "--");
                // 发送默认消息

            }
            // 发送默认消息
            // 发送群组消息方法（以一个用户身份向群组发送消息，单条消息最大 128k.每秒钟最多发送 20 条消息，每次最多向 3
            // 个群组发送，如：一次向 3 个群组发送消息，示为 3 条消息。）
            String[] messagePublishGroupToGroupId = {hospitalName};

            rongCloud.message.publishGroup(phone, messagePublishGroupToGroupId,
                    new InfoNtfMessage(trueName + "已进入群组", ""), trueName
                            + "已进入群组", "", 0, 0, 1);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }

    /**
     * 注意该方法没有关闭结束
     *
     * @param phone
     * @throws ClientProtocolException
     * @throws IOException
     */
    public void dealNoToken(String phone) throws ClientProtocolException,
            IOException {

        ResultSet rs = null;
        PreparedStatement ps = null;
        // conn = connDB.getConnection();
        String sql = "SELECT true_name,phone,is_upload_photo,wechat_url,photo_url FROM users WHERE phone = ? AND is_token != 1";
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            if (rs.next()) {
                String true_name = rs.getString("true_name");
                String is_upload_photo = rs.getString("is_upload_photo");
                String wechat_url = rs.getString("wechat_url");
                String photo_url = rs.getString("photo_url");
                String photoUrl = "";
                if ("0".equals(is_upload_photo)) {
                    photoUrl = wechat_url;
                } else {
                    photoUrl = photo_url;
                }
                BaseData baseData = null;
                try {
                    baseData = wUtils.createAction(true_name, phone,
                            photoUrl);
                } catch (Exception e1) {
                    baseData = new BaseData();
                }
                // 创建token失败,插入数据库
                if (baseData.getCode() == 200) {
                    sql = "UPDATE users SET token =?,is_token=1 WHERE phone=?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, baseData.getInfo().getToken());
                    ps.setString(2, phone);
                    ps.executeUpdate();
                    // baseDao.updateToken(phone,
                    // baseData.getInfo().getToken());
                    // token = baseData.getInfo().getToken();

                }
                // 重新更新token,插入数据库
                else {
                    BaseData refreshData = new BaseData();
                    try {
                        refreshData = wUtils.refreshTokenAction(phone);
                    } catch (Exception e1) {

                    }
                    if (refreshData.getCode() == 200) {
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, baseData.getInfo().getToken());
                        ps.setString(2, phone);
                        ps.executeUpdate();
                        // baseDao.updateToken(phone,
                        // refreshData.getInfo().getToken());
                        // token = refreshData.getInfo().getToken();
                    } else {
                        // 获取失败

                    }
                    System.out
                            .println("refreshToken:" + refreshData.toString());
                }

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("dealNoToken:" + e.getMessage());
        }
        // finally {
        // connDB.closeAll(rs, ps, conn);
        // }

    }

    /**
     * 方法完整结束
     *
     * @param phone
     * @throws ClientProtocolException
     * @throws IOException
     */
    public void dealNoTokenAll(String phone) throws ClientProtocolException,
            IOException {

        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "SELECT true_name,phone,is_upload_photo,wechat_url,photo_url FROM users WHERE phone = ? AND is_token != 1";
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            if (rs.next()) {
                String true_name = rs.getString("true_name");
                String is_upload_photo = rs.getString("is_upload_photo");
                String wechat_url = rs.getString("wechat_url");
                String photo_url = rs.getString("photo_url");
                String photoUrl = "";
                if ("0".equals(is_upload_photo)) {
                    photoUrl = wechat_url;
                } else {
                    photoUrl = photo_url;
                }
                BaseData baseData = null;
                try {
                    baseData = wUtils.createAction(true_name, phone,
                            photoUrl);
                } catch (Exception e1) {
                    baseData = new BaseData();
                }
                // 创建token失败,插入数据库
                if (baseData.getCode() == 200) {
                    sql = "UPDATE users SET token =?,is_token=1 WHERE phone=?";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, baseData.getInfo().getToken());
                    ps.setString(2, phone);
                    ps.executeUpdate();
                    // baseDao.updateToken(phone,
                    // baseData.getInfo().getToken());
                    // token = baseData.getInfo().getToken();

                }
                // 重新更新token,插入数据库
                else {
                    BaseData refreshData = new BaseData();
                    try {
                        refreshData = wUtils.refreshTokenAction(phone);
                    } catch (Exception e1) {

                    }
                    if (refreshData.getCode() == 200) {
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, baseData.getInfo().getToken());
                        ps.setString(2, phone);
                        ps.executeUpdate();
                        // baseDao.updateToken(phone,
                        // refreshData.getInfo().getToken());
                        // token = refreshData.getInfo().getToken();
                    } else {
                        // 获取失败

                    }
                    System.out
                            .println("refreshToken:" + refreshData.toString());
                }

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }

    /**
     * @param organSeg       器官段号
     * @param getTime        获取时间
     * @param openPsd        开箱密码
     * @param organ          器官
     * @param organNum       器官数量
     * @param blood          血液
     * @param bloodNum       血液数量
     * @param sampleOrgan    样本组织
     * @param sampleOrganNum 样本组织数量
     * @param fromCity       开始城市
     * @param toHospName     结束城市
     * @param tracfficType   转运方式
     * @param tracfficNumber 航班
     * @param opoName        opo名称
     * @param contactName    联系人名称
     * @param contactPhone   联系人电话
     * @param phone          本人电话
     * @param trueName       本人姓名18
     * @return
     * @throws SQLException
     */
    public String insertTransferTransfer(String organSeg, String getTime,
                                         String openPsd, String organ, String organNum, String blood,
                                         String bloodNum, String sampleOrgan, String sampleOrgan1,
                                         String sampleOrganNum, String fromCity, String toHospName,
                                         String tracfficType, String tracfficNumber, String opoName,
                                         String contactName, String contactPhone, String phone,
                                         String trueName, String groupName, String usersIds,
                                         String distance, String startLong, String startLati,
                                         String endLong, String endLati, String toHosp, String boxNo,
                                         String isStart, String opoContactName, String opoContactPhone,
                                         String autoTransfer, String modifyOrganSeg, String organAddress,
                                         String organTime, String temperature, String weather)
            throws SQLException {
        String transferSql = "insert into transfer(transferNumber,getTime,boxPin,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,fromCity,toHospName,tracfficType,tracfficNumber,opoName,contactName,contactPhone,phone,trueName,distance,startLong,startLati,endLong,endLati,toHosp,box_id,isStart,opoContactName,opoContactPhone,to_hosp_id,autoTransfer,modifyOrganSeg,organAddress,organStart,sampleOrgan1,temperature,weather) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        ResultSet rs = null;
        PreparedStatement ps = null;
        long transferId = 0L;
        int usersId = 0;
        int insertTransferUsers = 0;
        int transferIdUpdate = 0;
        int rongGroup = -1;
        int updateGroup = 0;
        int insertGroup = 0;
        String to_hosp_id = "";
        String returnValue = "";
        String boxId = "";
        int updateBoxStatus = 0;
        BoxDao boxDao = new BoxDao();
        this.conn = this.connDB.getConnection();

        try {

            /**
             * 判断箱子是否可用 获取箱子的id 更改箱子的状态为transfering
             */

            if (!boxDao.getBoxNoStatus(boxNo) && !"99999".equals(boxNo)) {
                CONST.ERROR = "箱子已被使用,不可创建";
                return "";
            }

            boxId = boxDao.getBoxId(boxNo);
            to_hosp_id = boxDao.getHospId(boxNo);
            if ("1".equals(isStart) || "0".equals(isStart)) {
                updateBoxStatus = boxDao.updateBoxStatus(boxNo, "transfering");
                if (updateBoxStatus == 0 && !"99999".equals(boxNo)) {
                    CONST.ERROR = "修改箱子状态失败";
                    return "";
                }
            }

            this.conn.setAutoCommit(false);
            ps = this.conn.prepareStatement(transferSql, 1);
            ps.setString(1, organSeg);
            ps.setString(2, getTime);
            ps.setString(3, openPsd);
            ps.setString(4, organ);
            ps.setString(5, organNum);
            ps.setString(6, blood);
            ps.setString(7, bloodNum);
            ps.setString(8, sampleOrgan);
            ps.setString(9, sampleOrganNum);
            ps.setString(10, fromCity);
            ps.setString(11, toHospName);
            ps.setString(12, tracfficType);
            ps.setString(13, tracfficNumber);
            ps.setString(14, opoName);
            ps.setString(15, contactName);
            ps.setString(16, contactPhone);
            ps.setString(17, phone);
            ps.setString(18, trueName);
            ps.setString(19, distance);
            ps.setString(20, startLong);
            ps.setString(21, startLati);
            ps.setString(22, endLong);
            ps.setString(23, endLati);
            ps.setString(24, toHosp);
            ps.setString(25, boxId);

            if (isStart != null && !"".equals(isStart)) {
                ps.setString(26, isStart);
            } else {
                ps.setString(26, "0");
            }
            ps.setString(27, opoContactName);
            ps.setString(28, opoContactPhone);
            ps.setString(29, to_hosp_id);
            if ("1".equals(autoTransfer)) {
                ps.setInt(30, 1);
            } else {
                ps.setInt(30, 0);
            }
            ps.setString(31, modifyOrganSeg);
            ps.setString(32, organAddress);
            ps.setString(33, organTime);
            ps.setString(34, sampleOrgan1);
            ps.setString(35, temperature);
            ps.setString(36, weather);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            Object e = null;
            if (rs.next()) {
                e = rs.getObject(1);
            }

            transferId = Long.parseLong(e.toString());
            String transferUpdate = "update transfer set transferId = ? where id = ?";
            ps = this.conn.prepareStatement(transferUpdate);
            ps.setString(1, e.toString());
            ps.setLong(2, transferId);
            transferIdUpdate = ps.executeUpdate();
            String users = "select id from users where phone = ?";
            ps = this.conn.prepareStatement(users);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            if (rs.next()) {
                usersId = rs.getInt("id");
            }

            String transferUser = "insert into transfer_users(users_id,transfer_id) values(?,?)";
            ps = this.conn.prepareStatement(transferUser);
            ps.setInt(1, usersId);
            ps.setLong(2, transferId);
            insertTransferUsers = ps.executeUpdate();

            // 查询创建的群组是否超过30个/人 总共能建50个 多余解散
            String sql = "SELECT count(*) c  from transfer_group_temp WHERE tid != '' AND phone=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            int groupNum = 0;
            if (rs.next()) {
                groupNum = rs.getInt("c");
            }
            if (groupNum > 30) {
                sql = "SELECT tid  from transfer_group_temp WHERE tid != '' AND phone=? order by id ASC limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, phone);
                rs = ps.executeQuery();
                String delGroupTid = "";
                if (rs.next()) {
                    delGroupTid = rs.getString("tid");
                }
                String result = "";
                TeamTid teamTid = null;

                try {
                    result = wUtils.removeAction(delGroupTid, phone);
                    teamTid = new Gson().fromJson(result, TeamTid.class);
                } catch (Exception e111) {

                }
                if (teamTid == null) {
                    teamTid = new TeamTid();
                }

                if (teamTid.getCode() == 200) {
                    sql = "UPDATE transfer_group_temp SET tid='' WHERE tid = ?";
                    ps = this.conn.prepareStatement(sql);
                    ps.setString(1, delGroupTid);
                    ps.executeUpdate();
                }

            }

            // 判断是否存在organSeg的群组
            String isOrganSegsql = "select usersIds from transfer_group_temp where organSeg = ?";
            ps = this.conn.prepareStatement(isOrganSegsql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();

            if (!usersIds.contains(",") && usersIds.length() < 15) {

                usersIds = usersIds + "," + opoContactPhone;
            }

            String[] usersIdsArray = usersIds.split(",");
            // usersIds 1 3 6 gruopid 1 groupName 测试1
            // 创建
            // CodeSuccessResult groupCreateResult = rongCloud.group.create(
            // usersIdsArray, organSeg, groupName);
            // String result = groupCreateResult.toString();

            for (int i = 0; i < usersIdsArray.length; i++) {
                try {
                    dealNoToken(usersIdsArray[i]);
                } catch (Exception e2) {

                }
            }

            String result = "";
            TeamTid teamTid = null;
            try {

                result = wUtils.createActionTeam(groupName, phone,
                        new Gson().toJson(usersIdsArray), "欢迎加入群组");
                teamTid = new Gson().fromJson(result, TeamTid.class);
                //发送消息
                wUtils.sendMsg(phone, teamTid.getTid(), "1", "");
            } catch (Exception e333) {

            }
            if (teamTid == null) {
                teamTid = new TeamTid();
            }

            //if (teamTid.getCode() == 200) {

            // 存在 更新
            if (rs.next()) {
                String updateGroupSql = "update transfer_group_temp set usersIds = ?,groupName = ?,phone = ?,tid=? where organSeg = ?";
                ps = conn.prepareStatement(updateGroupSql);
                ps.setString(1, usersIds);
                ps.setString(2, groupName);
                ps.setString(3, phone);

                ps.setString(4, teamTid.getTid());
                ps.setString(5, organSeg);
                if (usersIds.length() > 8) {
                    updateGroup = ps.executeUpdate();
                }

            }
            // 不存在 插入
            else {
                String insertGroupSql = "insert into transfer_group_temp(organSeg,usersIds,groupName,phone,tid) values(?,?,?,?,?)";
                ps = conn.prepareStatement(insertGroupSql);
                ps.setString(1, organSeg);
                ps.setString(2, usersIds);
                ps.setString(3, groupName);
                if (!"".equals(phone)) {
                    ps.setString(4, phone);
                } else {
                    ps.setString(4, "18388888888");
                }
                ps.setString(5, teamTid.getTid());
                if (usersIds.length() > 8) {
                    insertGroup = updateGroup = ps.executeUpdate();
                }

            }

//            } else {
//
//                rongGroup = 0;
//
//            }

            // 插入转运推送设置

            // for (int i = 0; i < usersIds.split(",").length; i++) {
            // String pushSiteSql =
            // "insert into transferPushSite(organSeg,phone) values(?,?)";
            // ps = conn.prepareStatement(pushSiteSql);
            // ps.setString(1, organSeg);
            // ps.setString(2, usersIds.split(",")[i]);
            // ps.executeUpdate();
            // }


//            if (transferId != 0L && transferIdUpdate != 0 && rongGroup != 0
//                    && (updateGroup != 0 || insertGroup != 0)) {
            if (transferId != 0L && transferIdUpdate != 0) {
                this.conn.commit();
                returnValue = organSeg;

                for (int i = 0; i < usersIdsArray.length; i++) {

                    String openId = "";
                    String true_name = "";
                    sql = "SELECT weixin_openid wechat_uuid,true_name FROM users WHERE phone=? AND weixin_openid<>''";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, usersIdsArray[i]);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        openId = rs.getString("wechat_uuid");
                        true_name = rs.getString("true_name");
                    }

                    if (!"".equals(openId)) {
                        String url = UrlServlet.mUrl + CONST.ACCESS_TOKEN;

                        String json = TemplateData.New().setTouser(openId)
                                .setTemplate_id(UrlServlet.mModeSuccess)
                                .setTopcolor("#743A3A").setUrl(url)
                                .setMiniprogram(
                                        new Miniprogram(
                                                UrlServlet.mWxXiaoAppId,
                                                "pages/index/index"))
                                .add("first", "转运创建成功", "#050505")
                                .add("keyword1", true_name, "#1d4499")
                                // .add("keyword2", "树兰(杭州)医院", "#1d4499")
                                .add(
                                        "keyword3",
                                        toHosp + "，" + weather + "，当前温度："
                                                + temperature + "℃", "#1d4499")
                                .add("keyword4", distance + "km", "#1d4499")
                                .add("remark", "请各位配合密切关注转运情况", "#1d4499")
                                .build();

                        // HttpUtils.doJsonPost(url, json);

                    }
                }

            } else {
                this.conn.rollback();
                updateBoxStatus = boxDao.updateBoxStatus(boxNo, "free");
                CONST.ERROR = "服务器错误.transferId:" + transferId
                        + ",transferIdUpdate:" + transferIdUpdate
                        + ",rongGroup:" + rongGroup + ",updateGroup:"
                        + updateGroup + ",insertGroup:" + insertGroup;
                System.out.println("服务器错误：" + CONST.ERROR);
                // :transferId:" + transferId + ",usersId:"
                // + usersId + ",usersId:" + usersId + ",usersId:"
                // + transferIdUpdate + ",transferIdUpdate:"
                // + transferIdUpdate + ",rongGroup:" + rongGroup
                // + ",updateGroup:" + updateGroup + ",insertGroup:"
                // + insertGroup;
            }
        } catch (Exception var35) {
            var35.printStackTrace();
            this.conn.rollback();
            updateBoxStatus = boxDao.updateBoxStatus(boxNo, "free");
            System.out.println("服务器错误22：" + var35.getMessage());
        } finally {
            this.connDB.closeAll(rs, ps, this.conn);
        }

        return returnValue;
    }

    /**
     * @param organSeg       器官段号
     * @param getTime        获取时间
     * @param openPsd        开箱密码
     * @param organ          器官
     * @param organNum       器官数量
     * @param blood          血液
     * @param bloodNum       血液数量
     * @param sampleOrgan    样本组织
     * @param sampleOrganNum 样本组织数量
     * @param fromCity       开始城市
     * @param toHospName     结束城市
     * @param tracfficType   转运方式
     * @param tracfficNumber 航班
     * @param opoName        opo名称
     * @param contactName    联系人名称
     * @param contactPhone   联系人电话
     * @param phone          本人电话
     * @param trueName       本人姓名18
     * @return
     * @throws SQLException
     */
    public String insertTransfer(String organSeg, String getTime,
                                 String openPsd, String organ, String organNum, String blood,
                                 String bloodNum, String sampleOrgan, String sampleOrganNum,
                                 String fromCity, String toHospName, String tracfficType,
                                 String tracfficNumber, String opoName, String contactName,
                                 String contactPhone, String phone, String trueName,
                                 String groupName, String usersIds, String distance,
                                 String startLong, String startLati, String endLong, String endLati,
                                 String toHosp, String boxNo, String isStart, String opoContactName,
                                 String opoContactPhone, String autoTransfer, String modifyOrganSeg,
                                 String organAddress, String organTime) throws SQLException {
        String transferSql = "insert into transfer(transferNumber,getTime,boxPin,organ,organNum,blood,bloodNum,sampleOrgan,sampleOrganNum,fromCity,toHospName,tracfficType,tracfficNumber,opoName,contactName,contactPhone,phone,trueName,distance,startLong,startLati,endLong,endLati,toHosp,box_id,isStart,opoContactName,opoContactPhone,to_hosp_id,autoTransfer,modifyOrganSeg,organAddress,organStart) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        ResultSet rs = null;
        PreparedStatement ps = null;
        long transferId = 0L;
        int usersId = 0;
        int insertTransferUsers = 0;
        int transferIdUpdate = 0;
        int rongGroup = -1;
        int updateGroup = 0;
        int insertGroup = 0;
        String to_hosp_id = "";
        String returnValue = "";
        String boxId = "";
        int updateBoxStatus = 0;
        BoxDao boxDao = new BoxDao();
        this.conn = this.connDB.getConnection();

        try {

            /**
             * 判断箱子是否可用 获取箱子的id 更改箱子的状态为transfering
             */

            if (!boxDao.getBoxNoStatus(boxNo) && !"99999".equals(boxNo)) {
                CONST.ERROR = "箱子已被使用,不可创建";
                return "";
            }

            boxId = boxDao.getBoxId(boxNo);
            to_hosp_id = boxDao.getHospId(boxNo);
            if ("1".equals(isStart) || "0".equals(isStart)) {
                updateBoxStatus = boxDao.updateBoxStatus(boxNo, "transfering");
                if (updateBoxStatus == 0) {
                    CONST.ERROR = "修改箱子状态失败";
                    return "";
                }
            }

            this.conn.setAutoCommit(false);
            ps = this.conn.prepareStatement(transferSql, 1);
            ps.setString(1, organSeg);
            ps.setString(2, getTime);
            ps.setString(3, openPsd);
            ps.setString(4, organ);
            ps.setString(5, organNum);
            ps.setString(6, blood);
            ps.setString(7, bloodNum);
            ps.setString(8, sampleOrgan);
            ps.setString(9, sampleOrganNum);
            ps.setString(10, fromCity);
            ps.setString(11, toHospName);
            ps.setString(12, tracfficType);
            ps.setString(13, tracfficNumber);
            ps.setString(14, opoName);
            ps.setString(15, contactName);
            ps.setString(16, contactPhone);
            ps.setString(17, phone);
            ps.setString(18, trueName);
            ps.setString(19, distance);
            ps.setString(20, startLong);
            ps.setString(21, startLati);
            ps.setString(22, endLong);
            ps.setString(23, endLati);
            ps.setString(24, toHosp);
            ps.setString(25, boxId);

            if (isStart != null && !"".equals(isStart)) {
                ps.setString(26, isStart);
            } else {
                ps.setString(26, "0");
            }
            ps.setString(27, opoContactName);
            ps.setString(28, opoContactPhone);
            ps.setString(29, to_hosp_id);
            if ("1".equals(autoTransfer)) {
                ps.setInt(30, 1);
            } else {
                ps.setInt(30, 0);
            }
            ps.setString(31, modifyOrganSeg);
            ps.setString(32, organAddress);
            ps.setString(33, organTime);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            Object e = null;
            if (rs.next()) {
                e = rs.getObject(1);
            }

            transferId = Long.parseLong(e.toString());
            String transferUpdate = "update transfer set transferId = ? where id = ?";
            ps = this.conn.prepareStatement(transferUpdate);
            ps.setString(1, e.toString());
            ps.setLong(2, transferId);
            transferIdUpdate = ps.executeUpdate();
            String users = "select id from users where phone = ?";
            ps = this.conn.prepareStatement(users);
            ps.setString(1, phone);
            rs = ps.executeQuery();
            if (rs.next()) {
                usersId = rs.getInt("id");
            }

            String transferUser = "insert into transfer_users(users_id,transfer_id) values(?,?)";
            ps = this.conn.prepareStatement(transferUser);
            ps.setInt(1, usersId);
            ps.setLong(2, transferId);
            insertTransferUsers = ps.executeUpdate();
            // 判断是否存在organSeg的群组
            String isOrganSegsql = "select usersIds from transfer_group_temp where organSeg = ?";
            ps = this.conn.prepareStatement(isOrganSegsql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            String[] usersIdsArray = usersIds.split(",");
            // usersIds 1 3 6 gruopid 1 groupName 测试1
            // 创建
            CodeSuccessResult groupCreateResult = rongCloud.group.create(
                    usersIdsArray, organSeg, groupName);
            // String result = groupCreateResult.toString();
            TeamTid teamTid = null;
            try {
                String result = wUtils.createActionTeam(groupName, phone,
                        new Gson().toJson(usersIdsArray), "欢迎加入群组");
                teamTid = new Gson().fromJson(result, TeamTid.class);
            } catch (Exception e22) {

            }
            if (teamTid == null) {
                teamTid = new TeamTid();
            }
            if (teamTid.getCode() == 200) {
                // 存在 更新
                if (rs.next()) {
                    String updateGroupSql = "update transfer_group_temp set usersIds = ?,groupName = ?,phone = ? where organSeg = ?";
                    ps = conn.prepareStatement(updateGroupSql);
                    ps.setString(1, usersIds);
                    ps.setString(2, groupName);
                    ps.setString(3, phone);
                    ps.setString(4, organSeg);
                    if (usersIds.length() > 8) {
                        updateGroup = ps.executeUpdate();
                    }

                }
                // 不存在 插入
                else {
                    String insertGroupSql = "insert into transfer_group_temp(organSeg,usersIds,groupName,phone) values(?,?,?,?)";
                    ps = conn.prepareStatement(insertGroupSql);
                    ps.setString(1, organSeg);
                    ps.setString(2, usersIds);
                    ps.setString(3, groupName);
                    if (!"".equals(phone)) {
                        ps.setString(4, phone);
                    } else {
                        ps.setString(4, "18388888888");
                    }
                    if (usersIds.length() > 8) {
                        insertGroup = updateGroup = ps.executeUpdate();
                    }
                }

            } else {

                rongGroup = 0;

            }

            // 插入转运推送设置

            // for (int i = 0; i < usersIds.split(",").length; i++) {
            // String pushSiteSql =
            // "insert into transferPushSite(organSeg,phone) values(?,?)";
            // ps = conn.prepareStatement(pushSiteSql);
            // ps.setString(1, organSeg);
            // ps.setString(2, usersIds.split(",")[i]);
            // ps.executeUpdate();
            // }


            if (transferId != 0L && transferIdUpdate != 0 && rongGroup != 0
                    && (updateGroup != 0 || insertGroup != 0)) {
                this.conn.commit();
                returnValue = organSeg;
            } else {
                this.conn.rollback();
                updateBoxStatus = boxDao.updateBoxStatus(boxNo, "free");
                CONST.ERROR = "数据库错误:transferId:" + transferId + ",usersId:"
                        + usersId + ",usersId:" + usersId + ",usersId:"
                        + transferIdUpdate + ",transferIdUpdate:"
                        + transferIdUpdate + ",rongGroup:" + rongGroup
                        + ",updateGroup:" + updateGroup + ",insertGroup:"
                        + insertGroup;
            }
        } catch (Exception var35) {
            var35.printStackTrace();
            this.conn.rollback();
            updateBoxStatus = boxDao.updateBoxStatus(boxNo, "free");
        } finally {
            this.connDB.closeAll(rs, ps, this.conn);
        }

        return returnValue;
    }

    /**
     * 根据手机修改信息
     */
    public TransferPushSite getTransferPushSite(String phone, String organSeg) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        TransferPushSite transferPushSite = null;
        String sql = "";

        // 查询是否存在
        try {
            sql = "select temperatureStatus,openStatus,collisionStatus from  transferPushSite   where organSeg = ? and phone = ?";

            // 调用SQL

            ps = conn.prepareStatement(sql);

            ps.setString(1, organSeg);
            ps.setString(2, phone);
            rs = ps.executeQuery();

            if (rs.next()) {
                transferPushSite = new TransferPushSite();
                transferPushSite.setTemperatureStatus(rs
                        .getInt("temperatureStatus"));
                transferPushSite.setOpenStatus(rs.getInt("openStatus"));
                transferPushSite.setCollisionStatus(rs
                        .getInt("collisionStatus"));
            } else {
                sql = "insert into transferPushSite(organSeg,phone) values(?,?)";
                ps = conn.prepareStatement(sql);

                ps.setString(1, organSeg);
                ps.setString(2, phone);
                ps.executeUpdate();

                transferPushSite = new TransferPushSite();
                transferPushSite.setTemperatureStatus(0);
                transferPushSite.setOpenStatus(0);
                transferPushSite.setCollisionStatus(0);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return transferPushSite;
    }

    /**
     * 根据手机修改信息
     */
    public int setTransferPushSite(String phone, String organSeg, String type,
                                   int status) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "";
        int result = -1;
        // 查询是否存在
        try {

            if ("temperature".equals(type)) {
                sql = "update transferPushSite set temperatureStatus = ? where organSeg = ? and phone = ?";
            } else if ("open".equals(type)) {
                sql = "update transferPushSite set openStatus = ? where organSeg = ? and phone = ? ";
            } else if ("collision".equals(type)) {
                sql = "update transferPushSite set collisionStatus = ? where organSeg = ? and phone = ?";
            }

            // 调用SQL

            ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ps.setString(2, organSeg);
            ps.setString(3, phone);
            result = ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return result;
    }

    public int updateTransfer(String organSeg, String getTime, String openPsd,
                              String organ, String organNum, String blood, String bloodNum,
                              String sampleOrgan, String sampleOrganNum, String fromCity,
                              String toHospName, String tracfficType, String tracfficNumber,
                              String opoName, String contactName, String contactPhone,
                              String phone, String trueName, String groupName, String usersIds,
                              String distance, String startLong, String startLati,
                              String endLong, String endLati, String toHosp, String isStart,
                              String opoContactName, String opoContactPhone,
                              String modifyOrganSeg, String autoTransfer, String historyModify,
                              String status) throws SQLException {

        String transferSql = "update  transfer set getTime=?,boxPin=?,organ=?,organNum=?,blood=?,bloodNum=?,sampleOrgan=?,sampleOrganNum=?,fromCity=?,toHospName=?,tracfficType=?,tracfficNumber=?,opoName=?,contactName=?,contactPhone=?,phone=?,trueName=?,distance=?,startLong=?,startLati=?,endLong=?,endLati=?,toHosp=?,isStart=?,opoContactName=?,opoContactPhone=?,getTime = ?,modifyOrganSeg=?,autoTransfer=?,historyModify=? where transferNumber=?";
        ResultSet rs = null;
        PreparedStatement ps = null;

        int updateBoxStatus = 0;

        this.conn = this.connDB.getConnection();

        try {

            this.conn.setAutoCommit(false);

            // 查询出opo人员,判断是否修改了人员
            String opoSql = "select usersIds,groupName from transfer_group_temp where organSeg= ? ";
            ps = conn.prepareStatement(opoSql);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {
                String usersIdsTemp = rs.getString("usersIds");
                groupName = rs.getString("groupName");
                /**
                 * 修改了opo人员 1.删除以前在融云opo人员 2.加入融云 3.拼接usersIds 插入数据库
                 */
                try {
                    if (usersIdsTemp.contains(",")
                            && usersIdsTemp.split(",").length >= 3) {
                        String temp[] = usersIdsTemp.split(",");
                        if (!opoContactPhone.equals(temp[2])) {

                            rongCloud.group.quit(temp[2].split(" "), organSeg);

                            rongCloud.group.join(opoContactPhone.split(" "),
                                    organSeg, groupName);

                            String tempIds = "";
                            for (int i = 0; i < temp.length; i++) {
                                if (i == 2) {
                                    tempIds += opoContactPhone;
                                } else {
                                    tempIds += temp[i];
                                }

                                if (i != (temp.length - 1)) {
                                    tempIds += ",";
                                }
                            }
                            usersIds = tempIds;


                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            ps = this.conn.prepareStatement(transferSql);

            ps.setString(1, getTime);
            ps.setString(2, openPsd);
            ps.setString(3, organ);
            ps.setString(4, organNum);
            ps.setString(5, blood);
            ps.setString(6, bloodNum);
            ps.setString(7, sampleOrgan);
            ps.setString(8, sampleOrganNum);
            ps.setString(9, fromCity);
            ps.setString(10, toHospName);
            ps.setString(11, tracfficType);
            ps.setString(12, tracfficNumber);
            ps.setString(13, opoName);
            ps.setString(14, contactName);
            ps.setString(15, contactPhone);
            ps.setString(16, phone);
            ps.setString(17, trueName);
            ps.setString(18, distance);
            ps.setString(19, startLong);
            ps.setString(20, startLati);
            ps.setString(21, endLong);
            ps.setString(22, endLati);
            ps.setString(23, toHosp);

            if ("1".equals(isStart)) {
                isStart = "1";
            } else {
                isStart = "0";
            }
            ps.setString(24, isStart);

            ps.setString(25, opoContactName);
            ps.setString(26, opoContactPhone);
            // SimpleDateFormat sdf = new
            // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ps.setString(27, getTime);
            if (modifyOrganSeg != null && !"".equals(modifyOrganSeg)) {
                ps.setString(28, modifyOrganSeg);
            } else {
                ps.setString(28, "");
            }

            if ("1".equals(autoTransfer)) {
                ps.setInt(29, 0);
            } else {
                ps.setInt(29, 0);
            }

            ps.setString(30, historyModify);
            ps.setString(31, organSeg);

            updateBoxStatus = ps.executeUpdate();


            // 更新修改的群组
            if (!"modify".equals(historyModify)) {
                transferSql = "UPDATE transfer_group_temp SET usersIds = ?,groupName=? WHERE organSeg = ? ";
                ps = this.conn.prepareStatement(transferSql);
                ps.setString(1, usersIds);

                if (modifyOrganSeg != null && !"".equals(modifyOrganSeg)) {
                    fromCity = modifyOrganSeg;
                }
                if ("1".equals(isStart)) {

                    ps.setString(2, "转运中-" + fromCity + "-" + organ);
                } else {
                    ps.setString(2, "待转运-" + fromCity + "-" + organ);
                }

                ps.setString(3, organSeg);
                if (usersIds.length() > 8) {
                    ps.executeUpdate();
                }
            } else if (usersIds != null && !"".equals(usersIds))
            // 预览修改界面
            {
                transferSql = "UPDATE transfer_group_temp SET usersIds = ?  WHERE organSeg = ? ";
                ps = this.conn.prepareStatement(transferSql);
                ps.setString(1, usersIds);

                ps.setString(2, organSeg);
                if (usersIds.length() > 8) {
                    ps.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException var35) {
            var35.printStackTrace();
            this.conn.rollback();
            CONST.ERROR = var35.getMessage();


        } finally {
            this.connDB.closeAll(rs, ps, this.conn);
        }

        return updateBoxStatus;
    }

    public String deleteTransfer(String organSeg, String phone) {

        ResultSet rs = null;
        PreparedStatement ps = null;

        String returnValue = "";
        int delTransferInt = 0;
        int delTransferUsersInt = 0;
        int delTransferGroupTempInt = 0;
        this.conn = this.connDB.getConnection();

        try {
            this.conn.setAutoCommit(false);

            // 修改箱子状态
            String updateBoxStatus = "update box set transferStatus = 'free' where boxid = ( select box_id from transfer where transferNumber = ?)";
            ps = this.conn.prepareStatement(updateBoxStatus);
            ps.setString(1, organSeg);
            ps.executeUpdate();

            String delTransfer = "delete from transfer where transferNumber=?";
            ps = this.conn.prepareStatement(delTransfer);

            ps.setString(1, organSeg);
            delTransferInt = ps.executeUpdate();

            String delTransferUsers = "delete from transfer_users where transfer_id = (select id from transfer where transferNumber='"
                    + organSeg + "')";
            ps = this.conn.prepareStatement(delTransferUsers);
            // ps.setString(1, organSeg);
            delTransferUsersInt = ps.executeUpdate();


            String delTransferGroupTemp = "delete from transfer_group_temp where organSeg = ?";
            ps = this.conn.prepareStatement(delTransferGroupTemp);
            ps.setString(1, organSeg);
            delTransferGroupTempInt = ps.executeUpdate();


            String sqlTransfer = "select id from transfer where transferNumber=?";
            ps = this.conn.prepareStatement(sqlTransfer);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {

            } else {
                delTransferInt = 1;
            }

            String sqlTransferUsers = "select id from transfer_users where transfer_id = (select id from transfer where transferNumber=?)";
            ps = this.conn.prepareStatement(sqlTransferUsers);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {

            } else {
                delTransferUsersInt = 1;
            }

            String sqlTransferGroupTemp = "select id  from transfer_group_temp where organSeg = ?";
            ps = this.conn.prepareStatement(sqlTransferGroupTemp);
            ps.setString(1, organSeg);
            rs = ps.executeQuery();
            if (rs.next()) {

            } else {
                delTransferGroupTempInt = 1;
            }

            if (delTransferInt == 1 && delTransferGroupTempInt == 1) {
                CodeSuccessResult groupDismissResult = rongCloud.group.dismiss(
                        phone + "", organSeg);
                if (groupDismissResult.toString().contains("200")) {
                    conn.commit();
                    returnValue = "ok";
                }

            } else {
                conn.rollback();

            }

        } catch (Exception var35) {
            var35.printStackTrace();
            try {
                this.conn.rollback();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            this.connDB.closeAll(rs, ps, this.conn);
        }

        return returnValue;
    }

}
