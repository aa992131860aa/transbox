package com.life.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.life.entity.BoxSms;
import com.life.entity.BoxSmsContect;
import com.life.entity.Contact;
import com.life.entity.Datas;
import com.life.entity.OneSmsDetail;
import com.life.entity.SmsAccount;
import com.life.utils.CONST;
import com.life.utils.ConnectionDB;
import com.sun.mail.util.MailSSLSocketFactory;

public class SmsDao {
    private Connection conn;
    private ConnectionDB connDB = new ConnectionDB();

    private static String account = "cy@lifeperfusor.com";// 登录账户
    private static String password = "dYZaoTHjSjWCWD7s";// 登录密码
    private static String host = "smtp.exmail.qq.com";// 服务器地址
    private static String port = "465";// 端口
    private static String protocol = "smtp";// 协议

    // 初始化参数
    public static Session initProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", protocol);
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        // 使用smtp身份验证
        properties.put("mail.smtp.auth", "true");
        // 使用SSL,企业邮箱必需 start
        // 开启安全协议
        MailSSLSocketFactory mailSSLSocketFactory = null;
        try {
            mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        properties.put("mail.smtp.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);
        properties.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.socketFactory.port", port);
        Session session = Session.getDefaultInstance(properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(account, password);
                    }
                });
        // 使用SSL,企业邮箱必需 end
        // TODO 显示debug信息 正式环境注释掉
        session.setDebug(true);
        return session;
    }

    public Datas sendSms(String phone) {
        int code = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;// 产生1000-9999的随机数
        String testUsername = "azuretech"; // 在短信宝注册的用户名
        String testPassword = "weilab123456"; // 在短信宝注册的密码
        String testPhone = phone;
        String testContent = "【器官云监控】您的验证码是" + code + "，５分钟内有效。若非本人操作请忽略此消息。"; // 注意测试时，也请带上公司简称或网站签名，发送正规内容短信。千万不要发送无意义的内容：例如
        // 测一下、您好。否则可能会收不到
        String httpUrl = "http://api.smsbao.com/sms";

        StringBuffer httpArg = new StringBuffer();
        httpArg.append("u=").append(testUsername).append("&");
        httpArg.append("p=").append(md5(testPassword)).append("&");
        httpArg.append("m=").append(testPhone).append("&");
        httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));

        String result = request(httpUrl, httpArg.toString());
        Datas data = new Datas();
        // send sms success
        if ("0".equals(result)) {

            int c = gainNotice(phone);
            if (c < 4) {
                data.setResult(CONST.SEND_OK);
                data.setMsg("发送验证码成功");
                Map<String, Integer> map = new HashMap<String, Integer>();
                if ("18398850872".equals(phone)) {
                    map.put("code", 9999);
                } else {
                    map.put("code", code);
                }
                data.setObj(map);

                insertSmsRecord(testPhone, testContent);
            } else {
                data.setResult(CONST.SEND_FAIL);
                data.setMsg("验证码次数发送过多,请使用微信登录");
            }


        } else {
            data.setResult(CONST.SEND_FAIL);
            data.setMsg("发送验证码失败");
        }

        return data;
    }

    public void sendTransferSms(String[] phones, String content,
                                String insertPhones) {

        String testUsername = "azuretech"; // 在短信宝注册的用户名
        String testPassword = "weilab123456"; // 在短信宝注册的密码

        String testContent = "【器官云监控】" + content; // 注意测试时，也请带上公司简称或网站签名，发送正规内容短信。千万不要发送无意义的内容：例如
        // 测一下、您好。否则可能会收不到
        String httpUrl = "http://api.smsbao.com/sms";
        for (int i = 0; i < phones.length; i++) {
            StringBuffer httpArg = new StringBuffer();
            httpArg.append("u=").append(testUsername).append("&");
            httpArg.append("p=").append(md5(testPassword)).append("&");
            httpArg.append("m=").append(phones[i]).append("&");
            httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));
            String result = request(httpUrl, httpArg.toString());


        }

        insertSmsRecord(insertPhones, testContent);

    }

    public void sendTransferSms(String phone, String content) {

        String testUsername = "azuretech"; // 在短信宝注册的用户名
        String testPassword = "weilab123456"; // 在短信宝注册的密码

        String testContent = "【器官云监控】" + content; // 注意测试时，也请带上公司简称或网站签名，发送正规内容短信。千万不要发送无意义的内容：例如
        // 测一下、您好。否则可能会收不到
        String httpUrl = "http://api.smsbao.com/sms";

        StringBuffer httpArg = new StringBuffer();
        httpArg.append("u=").append(testUsername).append("&");
        httpArg.append("p=").append(md5(testPassword)).append("&");
        httpArg.append("m=").append(phone).append("&");
        httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));
        String result = request(httpUrl, httpArg.toString());


        insertSmsRecord(phone, testContent);

    }

    public String getOpenId(String appid, String secret, String js_code) {
        // https://api.weixin.qq.com/sns/jscode2session?appid=wx323507bed41c42c8&secret=b0d04f48fb57d4c73ab42652b59037d2&js_code='+res.code+'&grant_type=authorization_code

        String httpUrl = "https://api.weixin.qq.com/sns/jscode2session";

        StringBuffer httpArg = new StringBuffer();
        httpArg.append("appid=").append(appid).append("&");
        httpArg.append("secret=").append(secret).append("&");
        httpArg.append("js_code=").append(js_code).append("&");
        httpArg.append("grant_type=").append("authorization_code");
        String result = request(httpUrl, httpArg.toString());

        return result;

    }

    public String getAccessToken() {
        // https://api.weixin.qq.com/sns/jscode2session?appid=wx323507bed41c42c8&secret=b0d04f48fb57d4c73ab42652b59037d2&js_code='+res.code+'&grant_type=authorization_code

        String httpUrl = "https://api.weixin.qq.com/sns/jscode2session";

        StringBuffer httpArg = new StringBuffer();

        httpArg.append("appid=").append("wx8d93a4fbade11124").append("&");
        httpArg.append("secret=").append("5391bdbaf15a46bdc6ab8e7d6d8bbb45")
                .append("&");
        httpArg.append("grant_type=").append("client_credential");
        String result = request(httpUrl, httpArg.toString());

        return result;

    }

    /**
     * 插入数据库
     *
     * @param phone
     * @param content
     */
    public void getSmsRecord(String phones, String pContent, String organSeg) {
        List<Contact> lists = new ArrayList<Contact>();
        ResultSet rs = null;
        ResultSet rsTwo = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "select id users_id,true_name,phone,photo_url,wechat_url,is_upload_photo,role_id from users where ? like CONCAT ('%',phone,'%') ";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, phones);
            rs = ps.executeQuery();

            while (rs.next()) {
                Contact contact = new Contact();

                // contact.setUsersId((rs.getString("users_id")));
                // contact.setTrueName((rs.getString("true_name")));
                // contact.setContactPhone((rs.getString("phone")));
                // contact.setPhotoFile((rs.getString("photo_url")));
                // contact.setWechatUrl((rs.getString("wechat_url")));
                // contact.setIsUploadPhoto((rs.getString("is_upload_photo")));
                // contact.setRoleId(rs.getInt("role_id"));

                String phone = rs.getString("phone");
                contact.setPhone(phone);

                sql = "select p.postRole from postRolePerson pp,postRole p where p.id = pp.postRoleId and pp.phone=? and pp.organSeg like '%"
                        + organSeg + "%'";
                conn = connDB.getConnection();
                ps = conn.prepareStatement(sql);
                ps.setString(1, phone);

                rsTwo = ps.executeQuery();
                if (rsTwo.next()) {
                    contact.setPostRole(rsTwo.getString("postRole"));

                }

                lists.add(contact);

            }

            for (int i = 0; i < lists.size(); i++) {
                String[] phonesStr = phones.split(",");
                String appAddress = "APP下载地址 http://t.cn/RBkPjYd";
                appAddress = "";
                Contact contact = lists.get(i);
                String content = "";
                String phone = contact.getPhone();
                if (contact.getPostRole() == null
                        || "无".equals(contact.getPostRole().trim())) {
                    content = pContent + "您暂无岗位角色。详情请至APP查看。" + appAddress;
                } else {
                    content = pContent + "您是" + contact.getPostRole()
                            + "。详情请至APP查看。" + appAddress;
                }
                try {
                    if (phonesStr.length == 2) {
                        if (phone.equals(phonesStr[0])) {
                            content = pContent;
                        } else if (phone.equals(phonesStr[1])) {
                            content = pContent;
                        }

                    } else {
                        if (phone.equals(phonesStr[0])) {
                            content = pContent + "您是转运医师。详情请至APP查看。"
                                    + appAddress;
                        } else if (phone.equals(phonesStr[1])) {
                            content = pContent + "您是科室协调员。详情请至APP查看。"
                                    + appAddress;
                        } else if (phone.equals(phonesStr[2])) {
                            content = pContent + "您是OPO人员。详情请至APP查看。"
                                    + appAddress;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sendTransferSms(phone, content);

            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
            connDB.closeAll(rsTwo, ps, conn);
        }

    }

    public void sendOneTransferSms(String phone, String content) {

        String testUsername = "azuretech"; // 在短信宝注册的用户名
        String testPassword = "weilab123456"; // 在短信宝注册的密码

        String testContent = "【器官云监控】" + content; // 注意测试时，也请带上公司简称或网站签名，发送正规内容短信。千万不要发送无意义的内容：例如
        // 测一下、您好。否则可能会收不到
        String httpUrl = "http://api.smsbao.com/sms";

        StringBuffer httpArg = new StringBuffer();
        httpArg.append("u=").append(testUsername).append("&");
        httpArg.append("p=").append(md5(testPassword)).append("&");
        httpArg.append("m=").append(phone).append("&");
        httpArg.append("c=").append(encodeUrlString(testContent, "UTF-8"));
        String result = request(httpUrl, httpArg.toString());

        insertSmsRecord(phone, testContent);

    }

    public void insert(String name, String email, String time, String type) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "INSERT INTO sms_email(name,email,time,type) VALUES(?,?,?,?)";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, time);
            ps.setString(4, type);
            ps.executeUpdate();

        } catch (Exception e) {

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
    }

    public boolean isExist(String time) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        String sql = "SELECT * FROM sms_email WHERE time = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, time);

            ps.executeUpdate();

        } catch (Exception e) {

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return false;
    }

    public void dealSms() {
        SmsDao smsDao = new SmsDao();
        List<BoxSms> smsDaoList = smsDao.gainBoxIccidList();
        List<String> iccidList = new ArrayList<String>();
        try {
            for (String iccid : iccidList) {
                String json = smsDao.oneSmsDetail(iccid);
                OneSmsDetail oneSmsDetail = new Gson().fromJson(json,
                        OneSmsDetail.class);
                String contectJson = smsDao.contectStatus(iccid);
                BoxSmsContect boxSmsContect = new Gson().fromJson(contectJson,
                        BoxSmsContect.class);
                // smsDao.insertOneSmsDetail(oneSmsDetail.getData(), "",
                // boxSmsContect.getData().getStatus());
            }

            for (BoxSms boxSms : smsDaoList) {


                String json = smsDao.oneSmsDetail(boxSms.getIccid());
                OneSmsDetail oneSmsDetail = new Gson().fromJson(json,
                        OneSmsDetail.class);

                String phone = oneSmsDetail.getData().getMsisdn();
                String status = oneSmsDetail.getData().getStatus();
                double usedDataVolume = oneSmsDetail.getData()
                        .getUsedDataVolume();
                double totalDataVolume = oneSmsDetail.getData()
                        .getTotalDataVolume();
                String realNameCertifystatus = oneSmsDetail.getData()
                        .getRealNameCertifystatus();
                String carrier = oneSmsDetail.getData().getCarrier();
                String iratePlanName = oneSmsDetail.getData()
                        .getIratePlanName();
                String ratePlanEffetiveDate = oneSmsDetail.getData()
                        .getRatePlanEffetiveDate();
                String expireDate = oneSmsDetail.getData().getExpireDate();

                String contectJson = smsDao.contectStatus(boxSms.getIccid());
                BoxSmsContect boxSmsContect = new Gson().fromJson(contectJson,
                        BoxSmsContect.class);
                String contectStatus = boxSmsContect.getData().getStatus();
                smsDao.insertOneSmsDetail(oneSmsDetail.getData(), boxSms
                        .getBoxNo(), boxSmsContect.getData().getStatus());


                // 发送邮箱
                // "卢双堂"<shuangtang.lu@lifeperfusor.com>;"吴明达"<mingda.wu@lifeperfusor.com>;童晓煜734425424@qq.com;cy.wu@lifeperfusor.com陈阳;
                // 734425424@qq.com　童晓煜目前这个邮箱
                // send("陈杨", "警告", "test content", "992131860@qq.com", "");

                // 流量使用达到480 dataUsage
                // 自动续费
                // 余额 allPrice
                // 状态 status

                double dataUsage = oneSmsDetail.getData().getDataUsage();
                if (dataUsage > 480) {
                    String dataUsageContent = "ICCID：" + boxSms.getIccid()
                            + ",当前使用流量：" + dataUsage + ",总计流量："
                            + totalDataVolume + "。请根据iccid及时添加流量包。";
                    send(
                            "SIM卡监控",
                            "流量不足",
                            dataUsageContent,
                            "992131860@qq.com,734425424@qq.com,shuangtang.lu@lifeperfusor.com,mingda.wu@lifeperfusor.com,cy.wu@lifeperfusor.com",
                            "");
                }

                if (!"activation".equals(status)) {
                    // 状态，测试中：testing、库存：inventory、待激活：pending-activation、已激活：activation、已停卡：deactivation、已销卡：retired
                    String statusStr = "";

                    if ("testing".equals(status)) {
                        statusStr = "测试中";

                    }
                    if ("inventory".equals(status)) {
                        statusStr = "待激活";

                    }
                    if ("pending-activation".equals(status)) {
                        statusStr = "待激活";

                    }
                    if ("activation".equals(status)) {
                        statusStr = "已激活";

                    }
                    if ("deactivation".equals(status)) {
                        statusStr = "已停卡";

                    }
                    if ("retired".equals(status)) {
                        statusStr = "已销卡";

                    }
                    String statusStrContent = "ICCID：" + boxSms.getIccid()
                            + ",当前SIM状态已经变更为：" + statusStr + "。请根据iccid及时处理。";
                    send(
                            "SIM卡监控",
                            "状态变更",
                            statusStrContent,
                            "992131860@qq.com,734425424@qq.com,shuangtang.lu@lifeperfusor.com,mingda.wu@lifeperfusor.com,cy.wu@lifeperfusor.com",
                            "");
                }
            }

            double allPrice = new Gson().fromJson(smsDao.gainAccount(),
                    SmsAccount.class).getData().getBalance();
            if (allPrice < 100) {
                String allPriceContent = "当前账号余额为：" + allPrice
                        + "。请及时充值，避免无法自动续费SIM导致停机。";
                send(
                        "SIM卡监控",
                        "余额不足",
                        allPriceContent,
                        "992131860@qq.com,734425424@qq.com,shuangtang.lu@lifeperfusor.com,mingda.wu@lifeperfusor.com,cy.wu@lifeperfusor.com",
                        "");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // @param sender 发件人别名
    // @param subject 邮件主题
    // @param content 邮件内容
    // @param receiverList 接收者列表,多个接收者之间用","隔开
    // @param fileSrc 附件地址
    public static void send(String sender, String subject, String content,
                            String receiverList, String fileSrc) {
        try {
            Session session = initProperties();
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(account, sender, "utf-8"));// 发件人,可以设置发件人的别名
            // 收件人,多人接收
            InternetAddress[] internetAddressTo = new InternetAddress()
                    .parse(receiverList);
            mimeMessage.setRecipients(Message.RecipientType.TO,
                    internetAddressTo);
            // 主题

            mimeMessage.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
            // 时间
            mimeMessage.setSentDate(new Date());
            // 容器类 附件
            MimeMultipart mimeMultipart = new MimeMultipart();
            // 可以包装文本,图片,附件
            MimeBodyPart bodyPart = new MimeBodyPart();
            // 设置内容
            bodyPart.setContent(content, "text/html; charset=UTF-8");
            mimeMultipart.addBodyPart(bodyPart);
            if (!"".equals(fileSrc)) {
                // 添加图片&附件
                bodyPart = new MimeBodyPart();
                bodyPart.attachFile(fileSrc);
                mimeMultipart.addBodyPart(bodyPart);
            }

            mimeMessage.setContent(mimeMultipart);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertOneSmsDetail(OneSmsDetail.DataBean data, String boxNo,
                                    String connect_staus) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "SELECT id FROM sms_detail WHERE iccid=?";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, data.getIccid());

            rs = ps.executeQuery();
            if (rs.next()) {
                // 包含以前的信息,更新
                sql = "UPDATE sms_detail SET box_no=?,iccid=?,phone=?,status=?,"
                        + "data_usage=?,total_data_volume=?,carrier=?,irate_plan_name=?,"
                        + "rate_plan_expiration_date=?,expire_date=?,connect_status=? WHERE iccid=?";
                conn = connDB.getConnection();
                ps = conn.prepareStatement(sql);
                ps.setString(1, boxNo);
                ps.setString(2, data.getIccid());
                ps.setString(3, data.getMsisdn());
                ps.setString(4, data.getStatus());
                ps.setDouble(5, data.getDataUsage());
                ps.setDouble(6, data.getTotalDataVolume());
                ps.setString(7, data.getCarrier());
                ps.setString(8, data.getIratePlanName());
                ps.setString(9, data.getRatePlanExpirationDate());
                ps.setString(10, data.getExpireDate());
                ps.setString(11, connect_staus);
                ps.setString(12, data.getIccid());
                ps.executeUpdate();
            } else {
                // 不包含以前的信息,插入
                sql = "INSERT INTO sms_detail(box_no,iccid,phone,status,data_usage,total_data_volume,carrier,irate_plan_name,rate_plan_expiration_date,expire_date,connect_status) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
                conn = connDB.getConnection();
                ps = conn.prepareStatement(sql);
                ps.setString(1, boxNo);
                ps.setString(2, data.getIccid());
                ps.setString(3, data.getMsisdn());
                ps.setString(4, data.getStatus());
                ps.setDouble(5, data.getDataUsage());
                ps.setDouble(6, data.getTotalDataVolume());
                ps.setString(7, data.getCarrier());
                ps.setString(8, data.getIratePlanName());
                ps.setString(9, data.getRatePlanExpirationDate());
                ps.setString(10, data.getExpireDate());
                ps.setString(11, connect_staus);

                ps.executeUpdate();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }

    /**
     * 插入数据库
     *
     * @param phone
     * @param content
     */
    private void insertSmsRecord(String phone, String content) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "insert into notice(phone,message) values(?,?)";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ps.setString(2, content);
            ps.executeUpdate();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }

    }

    /**
     * 插入数据库
     *
     * @param phone
     * @param content
     */
    private int gainNotice(String phone) {
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();
        int c = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdf.format(new Date());
        String sql = "SELECT count(*) c FROM notice where phone =? AND createAt like '%"
                + time + "%'";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, phone);

            rs = ps.executeQuery();
            while (rs.next()) {
                c = rs.getInt("c");

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return c;
    }

    public String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = reader.readLine();
            if (strRead != null) {
                sbf.append(strRead);
                while ((strRead = reader.readLine()) != null) {
                    sbf.append("\n");
                    sbf.append(strRead);
                }
            }
            is.close();
            connection.disconnect();
            reader.close();

            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String md5(String plainText) {
        StringBuffer buf = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    public String encodeUrlString(String str, String charset) {
        String strret = null;
        if (str == null)
            return str;
        try {
            strret = java.net.URLEncoder.encode(str, charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return strret;
    }

    public static String calcSign(Map<String, String> params, String secret) {
        params = new TreeMap<String, String>(params);
        StringBuilder stringBuilder = new StringBuilder();
        int size = params.size();
        int index = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            index++;
            stringBuilder.append(entry.getKey()).append("=").append(
                    entry.getValue());
            if (index < size) {
                stringBuilder.append("&");
            }
        }
        stringBuilder.append(secret);
        return DigestUtils.sha256Hex(stringBuilder.toString());
    }

    public static String smsPost(String uri, Map<String, String> params)
            throws Exception {
        HttpPost httpPost = new HttpPost(uri);
        // RequestConfig requestConfig = RequestConfig.DEFAULT;
        // httpPost.setConfig(requestConfig);
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            paramList.add(new BasicNameValuePair(entry.getKey(), entry
                    .getValue()));
        }
        UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(paramList,
                "utf-8");
        httpPost.setEntity(paramEntity);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httpPost);
        HttpEntity resultEntity = response.getEntity();
        String result = EntityUtils.toString(resultEntity);
        EntityUtils.consume(resultEntity);
        response.close();
        httpclient.close();
        return result;
    }

    public String oneSmsDetail(String iccid) throws Exception {
        // app secret
        String secret = CONST.SIM_APP_SCREAT;

        // api url
        String apiUrl = "https://api.simboss.com/2.0/device/detail";

        // 参数
        Map<String, String> params = new TreeMap<String, String>();

        // 系统参数

        params.put("appid", CONST.SIM_APP_ID);

        params.put("timestamp", new Date().getTime() + "");

        // 应用参数

        params.put("iccid", iccid);

        // 签名
        String sign = calcSign(params, secret);

        params.put("sign", sign);

        // 提交请求
        String json = smsPost(apiUrl, params);
        return json;
    }

    public String contectStatus(String iccid) throws Exception {
        // app secret
        String secret = CONST.SIM_APP_SCREAT;

        // api url
        String apiUrl = "https://api.simboss.com/2.0/device/gprsStatus";

        // 参数
        Map<String, String> params = new TreeMap<String, String>();

        // 系统参数

        params.put("appid", CONST.SIM_APP_ID);

        params.put("timestamp", new Date().getTime() + "");

        // 应用参数

        params.put("iccid", iccid);

        // 签名
        String sign = calcSign(params, secret);

        params.put("sign", sign);

        // 提交请求
        String json = smsPost(apiUrl, params);
        return json;
    }

    public String gainAccount() throws Exception {
        // app secret
        String secret = CONST.SIM_APP_SCREAT;

        // api url
        String apiUrl = "https://api.simboss.com/2.0/user/dashboard/get";

        // 参数
        Map<String, String> params = new TreeMap<String, String>();

        // 系统参数

        params.put("appid", CONST.SIM_APP_ID);

        params.put("timestamp", new Date().getTime() + "");

        // 应用参数

        // params.put("iccid", iccid);

        // 签名
        String sign = calcSign(params, secret);

        params.put("sign", sign);

        // 提交请求
        String json = smsPost(apiUrl, params);

        return json;
    }

    public static void main(String[] args) throws Exception {
        new SmsDao().dealSms();
    }

    public List<BoxSms> gainBoxIccidList() {
        List<BoxSms> boxSmsList = new ArrayList<BoxSms>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        conn = connDB.getConnection();

        String sql = "SELECT model boxNo,iccid FROM box WHERE iccid IS NOT NULL";
        // 调用SQL
        try {

            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();

            while (rs.next()) {
                BoxSms boxSms = new BoxSms();
                boxSms.setBoxNo(rs.getString("boxNo"));
                boxSms.setIccid(rs.getString("iccid"));
                boxSmsList.add(boxSms);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            connDB.closeAll(rs, ps, conn);
        }
        return boxSmsList;
    }
}