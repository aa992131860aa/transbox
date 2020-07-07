package com.station;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.life.entity.ContactPerson;
import com.life.entity.UploadApp;
import com.life.utils.CONST;
import com.life.utils.ConnectionDB;
import com.life.utils.HMACSHA256;

public class MainDao {
    // 创建数据库连接对象
    private Connection conn = null;
    private ConnectionDB connDB = new ConnectionDB();

    /**
     * @return
     * @throws UnsupportedEncodingException
     */

    public List<BoxInfo> gainBoxInfo(String userName, String status, String boxNos) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<BoxInfo> boxInfoList = new ArrayList<BoxInfo>();
        List<String> boxIdList = new ArrayList<String>();
        List<Integer> transferIdList = new ArrayList<Integer>();

        // 获取四个箱子
        String sql = "SELECT b.boxid FROM box b,account a,hospital h WHERE b.hosp_id = h.hospitalid AND h.account_id = a.accountid  AND  a.username=? AND b.model in (" + boxNos + ")";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            rs = ps.executeQuery();

            while (rs.next()) {
                boxIdList.add(rs.getString("boxid"));
            }

            // 获取每个箱子,最新的转运信息
            for (String boxId : boxIdList) {

                sql = "SELECT id FROM transfer WHERE box_id = ? ORDER BY getTime DESC limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, boxId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    transferIdList.add(rs.getInt("id"));
                }

            }

            // 根据转运编号获取转运的详细信息
            for (int transferId : transferIdList) {
                sql = "SELECT power,tr.temperature,tr.humidity,currentCity,recordAt,b.model boxNo,IF(b.transferStatus='free','空闲','使用中') `status`,t.startLong,t.startLati,t.endLong,t.endLati,b.box_rfic   FROM transferRecord tr,box b,transfer t WHERE t.id = ? and  t.box_id = b.boxid AND tr.transfer_id = t.transferid order by tr.id DESC  LIMIT 1";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, transferId);

                rs = ps.executeQuery();
                if (rs.next()) {
                    BoxInfo boxInfo = new BoxInfo();
                    boxInfo.setBoxNo(rs.getString("boxNo"));
                    boxInfo.setCurrentCity(rs.getString("currentCity"));
                    boxInfo.setPower(rs.getInt("power"));
                    boxInfo.setRecordAt(rs.getString("recordAt"));
                    boxInfo.setStatus(rs.getString("status"));
                    boxInfo.setTemperature(rs.getString("temperature"));
                    boxInfo.setHumidity(rs.getString("humidity"));
                    boxInfo.setStartLong(rs.getDouble("startLong"));
                    boxInfo.setStartLati(rs.getDouble("startLati"));
                    boxInfo.setEndLong(rs.getDouble("endLong"));
                    boxInfo.setEndLati(rs.getDouble("endLati"));
                    boxInfo.setTransferId(transferId + "");
                    boxInfo.setBoxRfic(rs.getString("box_rfic"));
                    if ("transfering".equals(status)) {
                        if (boxInfo.getStatus().equals("使用中")) {
                            boxInfoList.add(boxInfo);
                        }
                    } else {
                        boxInfoList.add(boxInfo);
                    }

                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return boxInfoList;
    }

    /**
     * @return
     * @throws UnsupportedEncodingException
     */

    public List<BoxInfo> gainBoxInfoAll(String userName) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<BoxInfo> boxInfoList = new ArrayList<BoxInfo>();
        List<String> boxIdList = new ArrayList<String>();
        List<Integer> transferIdList = new ArrayList<Integer>();
        List<String> deviceIdList = new ArrayList<>();

        // 获取四个箱子
        String sql = "SELECT b.boxid,b.deviceId FROM box b,account a,hospital h WHERE b.hosp_id = h.hospitalid AND h.account_id = a.accountid  AND  a.username=? AND b.box_rfic IS NOT NULL AND  b.box_rfic <>''";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            rs = ps.executeQuery();

            while (rs.next()) {
                boxIdList.add(rs.getString("boxid"));
                deviceIdList.add(rs.getString("deviceId"));
            }

            // 获取每个箱子,最新的转运信息
            for (String boxId : boxIdList) {

                sql = "SELECT id FROM transfer WHERE box_id = ? ORDER BY getTime DESC limit 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, boxId);
                rs = ps.executeQuery();
                if (rs.next()) {
                    transferIdList.add(rs.getInt("id"));
                } else {

                }

            }

            // 根据转运编号获取转运的详细信息

            for (int i = transferIdList.size() - 1; i >= 0; i--) {
                int transferId = transferIdList.get(i);

                sql = "SELECT power,tr.temperature,tr.humidity,currentCity,recordAt,b.model boxNo,IF(b.transferStatus='free','空闲','使用中') `status`,t.startLong,t.startLati,t.endLong,t.endLati,b.box_rfic,b.deviceId   FROM transferRecord tr,box b,transfer t WHERE t.id = ? and  t.box_id = b.boxid AND tr.transfer_id = t.transferid order by tr.id DESC  LIMIT 1";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, transferId);

                rs = ps.executeQuery();
                if (rs.next()) {


                    BoxInfo boxInfo = new BoxInfo();
                    boxInfo.setBoxNo(rs.getString("boxNo"));
                    boxInfo.setCurrentCity(rs.getString("currentCity"));
                    boxInfo.setPower(rs.getInt("power"));
                    boxInfo.setRecordAt(rs.getString("recordAt"));
                    boxInfo.setStatus(rs.getString("status"));
                    boxInfo.setTemperature(rs.getString("temperature"));
                    boxInfo.setHumidity(rs.getString("humidity"));
                    boxInfo.setStartLong(rs.getDouble("startLong"));
                    boxInfo.setStartLati(rs.getDouble("startLati"));
                    boxInfo.setEndLong(rs.getDouble("endLong"));
                    boxInfo.setEndLati(rs.getDouble("endLati"));
                    boxInfo.setTransferId(transferId + "");
                    boxInfo.setBoxRfic(rs.getString("box_rfic"));

                    boxInfoList.add(boxInfo);
                    int dIndex = -1;

                    String deviceId = rs.getString("deviceId");
                    for (int d = 0; d < deviceIdList.size(); d++) {
                        if (deviceId.equals(deviceIdList.get(d))) {
                            dIndex = d;
                            break;
                        }
                    }

                    if (dIndex > 0) {
                        deviceIdList.remove(dIndex);
                    }


                }
            }
            //获取未转运的箱子信息
            for (String deviceId : deviceIdList) {

                sql = "SELECT power,tr.temperature,tr.humidity,currentCity,recordAt,b.model boxNo,IF(b.transferStatus='free','空闲','使用中') `status`,b.box_rfic   FROM transferRecord tr,box b WHERE  tr.remark = b.deviceId AND tr.remark = ? order by tr.id DESC  LIMIT 1";
                ps = conn.prepareStatement(sql);
                ps.setString(1, deviceId);

                rs = ps.executeQuery();
                if (rs.next()) {
                    BoxInfo boxInfo = new BoxInfo();
                    boxInfo.setBoxNo(rs.getString("boxNo"));
                    boxInfo.setCurrentCity(rs.getString("currentCity"));
                    boxInfo.setPower(rs.getInt("power"));
                    boxInfo.setRecordAt(rs.getString("recordAt"));
                    boxInfo.setStatus(rs.getString("status"));
                    boxInfo.setTemperature(rs.getString("temperature"));
                    boxInfo.setHumidity(rs.getString("humidity"));
//                    boxInfo.setStartLong(rs.getDouble("startLong"));
//                    boxInfo.setStartLati(rs.getDouble("startLati"));
//                    boxInfo.setEndLong(rs.getDouble("endLong"));
//                    boxInfo.setEndLati(rs.getDouble("endLati"));
                    //boxInfo.setTransferId(transferId + "");
                    boxInfo.setBoxRfic(rs.getString("box_rfic"));

                    boxInfoList.add(boxInfo);


                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return boxInfoList;
    }

    public void updatePower(String power, String rfid) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql ;
        Box box = null;
        int sqlId1 = -1;
        int sqlId2 = -1;
        try {
            // 获取四个箱子


            sql = "(SELECT id FROM (SELECT tr.id  FROM transferRecord tr,box b WHERE  tr.remark = b.deviceId AND b.box_rfic = ? order by tr.id DESC  LIMIT 1) e)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, rfid);
            rs = ps.executeQuery();
            if (rs.next()) {
               sqlId1 = rs.getInt("id");
            }

            sql = "(SELECT id FROM (SELECT tr.id FROM transferRecord tr ,(SELECT t.id FROM transfer t,box b WHERE t.box_id=b.boxid AND b.box_rfic=? order by t.id DESC limit 1) a where tr.transfer_id = a.id ORDER BY tr.id DESC limit 1) e)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, rfid);
            rs = ps.executeQuery();
            if (rs.next()) {
                sqlId2 = rs.getInt("id");
            }

             sql = "UPDATE transferRecord SET power = ? WHERE id = ?";
            // 调用SQL


            ps = conn.prepareStatement(sql);
            ps.setString(1, power);
            ps.setInt(2,sqlId1);
            int result1 = ps.executeUpdate();

            sql = "UPDATE transferRecord SET power = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, power);
            ps.setInt(2,sqlId2);
            int result2 = ps.executeUpdate();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }

    public Box gainBoxLocation(String boxNo) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        Box box = null;

        // 获取四个箱子
        String sql = "SELECT tr.longitude,tr.latitude,tr.recordAt,tr.transfer_id from transferRecord tr,box b WHERE b.model = ? AND   tr.transfer_id<>1 AND tr.remark = b.deviceId ORDER BY tr.recordAt DESC LIMIT 1";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, boxNo);
            rs = ps.executeQuery();

            if (rs.next()) {
                String transfer_id = rs.getString("transfer_id");
                if ("1".equals(transfer_id)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long time = new Date().getTime() / 1000;
                    String recordAt = rs.getString("recordAt");
                    double longitude = rs.getDouble("longitude");
                    double latitude = rs.getDouble("latitude");
                    long past = sdf.parse(recordAt).getTime() / 1000;
                    if (time - past < 60 * 60 * 15) {
                        box = new Box();
                        box.setBoxNo(boxNo);
                        box.setLongitude(longitude);
                        box.setLatitude(latitude);
                    }

                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return box;
    }

    public List<Video> gainVideoList() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Video> recordList = new ArrayList<Video>();

        // 获取四个箱子
        String sql = "SELECT name,url FROM video";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();

            while (rs.next()) {
                Video video = new Video();
                video.setName(rs.getString("name"));
                video.setUrl(rs.getString("url"));

                recordList.add(video);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return recordList;
    }

    public UploadApp gainStationUpdate() {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        UploadApp uploadApp = null;
        String sql = "select id,version,url,apk_size,update_content from station_upload order by id desc limit 1 ";
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

    public List<String> gainBoxNo(String userName, String boxNos) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<String> recordList = new ArrayList<String>();

        // 获取四个箱子
        String sql = "SELECT b.model boxNo FROM box b,account a,hospital h WHERE b.hosp_id = h.hospitalid AND h.account_id = a.accountid  AND  a.username=?  limit 0,4";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            rs = ps.executeQuery();

            while (rs.next()) {


                recordList.add(rs.getString("boxNo"));

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return recordList;
    }

    /**
     * @return
     * @throws UnsupportedEncodingException
     */
    public List<Record> gainRecordList1(String transferId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Record> recordList = new ArrayList<Record>();

        // 获取四个箱子
        String sql = "SELECT longitude,latitude,recordAt FROM transferRecord WHERE transfer_id = ? ORDER BY recordAt ASC";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, transferId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Record record = new Record();
                record.setLatitude(rs.getDouble("latitude"));
                record.setLongitude(rs.getDouble("longitude"));
                record.setRecordAt(rs.getString("recordAt"));

                recordList.add(record);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return recordList;
    }

    /**
     * @return
     * @throws UnsupportedEncodingException
     */
    public List<Record> gainRecordList(String transferId) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Record> recordList = new ArrayList<Record>();

        // 获取四个箱子
        String sql = "SELECT longitude,latitude,recordAt,temperature,humidity FROM transferRecord WHERE transfer_id = ? ORDER BY recordAt ASC";

        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, transferId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Record record = new Record();
                record.setLatitude(rs.getDouble("latitude"));
                record.setLongitude(rs.getDouble("longitude"));
                String recordAt[] = rs.getString("recordAt").split(" ")[1].split(":");
                record.setRecordAt(recordAt[0] + ":" + recordAt[1]);
                record.setTemperature(rs.getDouble("temperature"));
                record.setHumidity(rs.getDouble("humidity"));

                recordList.add(record);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return recordList;
    }

    public List<Transfer> searchTransferList(String userName, String condition,
                                             String boxNo, int page, int pageSize) {
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        List<Transfer> transferList = new ArrayList<Transfer>();
        String hospitalId = "";
        String sqlCondition = "";
        int time = 0;
        // 获取医院的id
        String sql = "SELECT h.hospitalid FROM hospital h ,account a WHERE  h.account_id=a.accountid AND a.username=? ";

        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            rs = ps.executeQuery();

            while (rs.next()) {
                hospitalId = rs.getString("hospitalid");
            }
            if (boxNo != null && !"".equals(boxNo)) {
                sqlCondition = "AND t.box_id=(SELECT boxid FROM box WHERE model='"
                        + boxNo + "') ";
            }

            // 获取转运的信息
            sql = "SELECT t.id,b.model,t.organ,t.trueName,t.fromCity,t.getTime FROM transfer t,box b WHERE t.to_hosp_id=? AND t.filterStatus=0 "
                    + sqlCondition
                    + "AND t.box_id = b.boxid AND CONCAT(t.organ,t.trueName,t.fromCity,t.getTime) LIKE '%"
                    + condition + "%' ORDER BY t.getTime DESC LIMIT ?,?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, hospitalId);
            ps.setInt(2, page * pageSize);
            ps.setInt(3, pageSize);
            rs = ps.executeQuery();
            while (rs.next()) {
                Transfer t = new Transfer();
                t.setId(rs.getInt("id"));
                t.setBoxNo(rs.getString("model"));
                t.setOrgan(rs.getString("organ"));
                t.setTrueName(rs.getString("trueName"));
                t.setFromCity(rs.getString("fromCity"));
                t.setGetTime(rs.getString("getTime"));

                // 获取转运时长
                sql = "SELECT  TIMESTAMPDIFF(MINUTE,MIN(recordAt),MAX(recordAt)) time,AVG(temperature) avgT,AVG(humidity) avgH FROM transferRecord WHERE transfer_id=? ";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, t.getId());
                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    time = Math.abs(rsTwo.getInt("time"));
                    t.setAvgH(rsTwo.getInt("avgH"));
                    t.setAvgT(rsTwo.getInt("avgT"));
                }
                int hour = (int) Math.floor(time / 60);
                int minute = (time % 60) * 10 / 60;

                t.setSpendTime(hour + minute / 10);
                transferList.add(t);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }
        return transferList;
    }

    public boolean login(String hospital, String pwd) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        pwd = HMACSHA256.sha256_HMAC(pwd, HMACSHA256.secret_key);
        try {

            String sql = "select pwd from account a ,hospital h where h.account_id = a.accountid  and a.`username` = ? and a.pwd = ? ";
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

    public static void main(String[] args) throws UnsupportedEncodingException {

    }

}
