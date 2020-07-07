package com.wangyi;

import com.life.entity.SendMsg;
import com.life.entity.TeamContent;
import com.life.entity.TeamNumber;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.life.utils.CONST;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WangyiHttpUtils {

    public BaseData blockAction(String accid, String needkick)
            throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(CONST.BLOCK_ACTION);

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        nvps.add(new BasicNameValuePair("accid", accid));
        nvps.add(new BasicNameValuePair("needkick", needkick));


        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        return new Gson().fromJson(result, BaseData.class);
    }

    public BaseData unBlockAction(String accid)
            throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(CONST.UN_BLOCK_ACTION);

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        nvps.add(new BasicNameValuePair("accid", accid));


        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        return new Gson().fromJson(result, BaseData.class);
    }

    public String kickAction(String tid, String owner, String members) throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(CONST.KICK_ACTION);

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        nvps.add(new BasicNameValuePair("tid", tid));
        nvps.add(new BasicNameValuePair("owner", owner));
        nvps.add(new BasicNameValuePair("members", members));


        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        return result;
    }

    /**
     * 发送文本消息
     *
     * @param accid   用户
     * @param tid     群id
     * @param type    1
     * @param content ""
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String sendMsg(String accid, String tid, String type, String content) throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(CONST.SEND_MSG);

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        nvps.add(new BasicNameValuePair("from", accid));
        nvps.add(new BasicNameValuePair("ope", "" + type));
        nvps.add(new BasicNameValuePair("to", "" + tid));
        nvps.add(new BasicNameValuePair("type", "0"));
        nvps.add(new BasicNameValuePair("body", new Gson().toJson(new SendMsg((content == "" ? "本次转运已开始，器官已成功放入转运箱，请各位配合并密切关注转运情况，确保器官高质转到，谢谢！" : content)))));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);
        //System.out.println("sendMsg:key:" + wangyiAppInfo.getAppKey() + ",secret:" + wangyiAppInfo.getAppSecret());

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("result:" + result);
        return result;
    }

    /**
     * 解散群组
     *
     * @param tid
     * @param owner
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    //'tid=1513535&owner=zhangsan' 'https://api.netease.im/nimserver/team/remove.action'
    public String removeAction(String tid, String owner) throws ClientProtocolException, IOException {

        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(CONST.REMOVE_ACTION);

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        nvps.add(new BasicNameValuePair("tid", tid));
        nvps.add(new BasicNameValuePair("owner", owner));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);
        //System.out.println("removeAction:key:" + wangyiAppInfo.getAppKey() + ",secret:" + wangyiAppInfo.getAppSecret());

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("removeAction:" + result);
        return result;
    }

    /**
     * 创建群组
     *
     * @param tname
     * @param owner
     * @param members
     * @param msg
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String createActionTeam(String tname, String owner, String members,
                                   String msg) throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(CONST.CREATE_ACTION_TEAM);

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        nvps.add(new BasicNameValuePair("tname", tname));
        nvps.add(new BasicNameValuePair("owner", owner));
        nvps.add(new BasicNameValuePair("members", members));
        nvps.add(new BasicNameValuePair("msg", msg));
        nvps.add(new BasicNameValuePair("magree", "0"));
        nvps.add(new BasicNameValuePair("joinmode", "0"));
        nvps.add(new BasicNameValuePair("beinvitemode", "1"));
        nvps.add(new BasicNameValuePair("invitemode", "1"));
        nvps.add(new BasicNameValuePair("uptinfomode", "1"));
        nvps.add(new BasicNameValuePair("upcustommode", "1"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);
        //System.out.println("createActionTeam:key:" + wangyiAppInfo.getAppKey() + ",secret:" + wangyiAppInfo.getAppSecret());

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("createActionTeam:" + result+","+tname+':'+owner+":"+members+":"+msg);
        return result;
    }

    /**
     * 创建个人信息
     *
     * @param name
     * @param accid
     * @param icon
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public BaseData createAction(String name, String accid, String icon)
            throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(CONST.CREATE_ACTION);

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        nvps.add(new BasicNameValuePair("accid", accid));
        nvps.add(new BasicNameValuePair("name", name));
        nvps.add(new BasicNameValuePair("icon", icon));
        System.out.println("accid:" + accid);
        System.out.println("name:" + name);
        System.out.println("icon:" + icon);
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);
        //System.out.println("createAction:key:" + wangyiAppInfo.getAppKey() + ",secret:" + wangyiAppInfo.getAppSecret());

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("createAction:" + result);
        return new Gson().fromJson(result, BaseData.class);
    }

    /**
     * 添加成员到群
     *
     * @param tid
     * @param owner
     * @param members
     * @param msg
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public BaseData addAction(String tid, String owner, String members, String msg)
            throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(CONST.ADD_ACTION);

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        nvps.add(new BasicNameValuePair("tid", tid));
        nvps.add(new BasicNameValuePair("owner", owner));
        nvps.add(new BasicNameValuePair("members", members));
        nvps.add(new BasicNameValuePair("magree", "0"));
        nvps.add(new BasicNameValuePair("msg", msg));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        //System.out.println("addAction:key:" + wangyiAppInfo.getAppKey() + ",secret:" + wangyiAppInfo.getAppSecret());
        System.out.println("addAction:" + result);
        return new Gson().fromJson(result, BaseData.class);
    }

    /**
     * 刷新token
     */
    public BaseData refreshTokenAction(String accid)
            throws ClientProtocolException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(CONST.REFRESH_TOKEN_ACTION);

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        nvps.add(new BasicNameValuePair("accid", accid));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        System.out.println("refreshTokenAction:" + result);
        return new Gson().fromJson(result, BaseData.class);
    }

    /**
     * 获取历史消息
     *
     * @param tid

     *               查询存储在网易云通信服务器中的群聊天历史消息，只能查询在保存时间范围内的消息
     *                跟据时间段查询群消息，每次最多返回100条；
     *                不提供分页支持，第三方需要跟据时间段来查询。
     *                <p>
     *                <p>
     *                <p>
     *                参数	类型	必须	说明
     *                tid	String	是	群id
     *                accid	String	是	查询用户对应的accid.
     *                begintime	String	是	开始时间，ms
     *                endtime	String	是	截止时间，ms
     *                limit	int	是	本次查询的消息条数上限(最多100条),小于等于0，或者大于100，会提示参数错误
     *                reverse	int	否	1按时间正序排列，2按时间降序排列。其它返回参数414错误。默认是按降序排列
     *                type	String	否	查询指定的多个消息类型，类型之间用","分割，不设置该参数则查询全部类型消息格式示例： 0,1,2,3
     *                类型支持： 1:图片，2:语音，3:视频，4:地理位置，5:通知，6:文件，10:提示，11:Robot，100:自定义
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String queryTeamMsg(String tid, String accid, String startAt)
            throws IOException, ParseException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost("https://api.netease.im/nimserver/history/queryTeamMsg.action");

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 20ab0f37e5a95aa4063a57afafa55f45
        long nowDate = new Date().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long lastDate = sdf.parse(startAt).getTime();
        System.out.println(" nowDate:" + nowDate);//1443599639999
        System.out.println("lastDate:" + lastDate);//1443599639999
        nvps.add(new BasicNameValuePair("tid", tid));
        nvps.add(new BasicNameValuePair("accid", accid));
        nvps.add(new BasicNameValuePair("begintime", "" + lastDate));
        nvps.add(new BasicNameValuePair("endtime", "" + nowDate));
        nvps.add(new BasicNameValuePair("limit", "100"));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        //System.out.println("addAction:key:" + wangyiAppInfo.getAppKey() + ",secret:" + wangyiAppInfo.getAppSecret());
        System.out.println("queryTeamMsg:" + result);
        TeamContent teamContent = new Gson().fromJson(result, TeamContent.class);
//        if (teamContent.getCode() == 200) {
//            if (teamContent.getSize() < 100) {
//                //存入数据库、解散群组
//
//            }
//        }

        return result;
    }

    /**
     * 查询群组成员
     *
     * @param tid
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public TeamNumber queryAction(String tid)
            throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost("https://api.netease.im/nimserver/team/query.action");

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        String members[] = new String[1];
        members[0] = tid;
        nvps.add(new BasicNameValuePair("tids", new Gson().toJson(members)));
        nvps.add(new BasicNameValuePair("ope", "1"));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        //System.out.println("addAction:key:" + wangyiAppInfo.getAppKey() + ",secret:" + wangyiAppInfo.getAppSecret());
        System.out.println("result:" + result);


        return new Gson().fromJson(result, TeamNumber.class);
    }

    /**
     * 获取用户名片信息
     *
     * @param accid
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getUinfos(String accid)
            throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost("https://api.netease.im/nimserver/user/getUinfos.action");

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        String members[] = new String[1];
        members[0] = accid;
        nvps.add(new BasicNameValuePair("accids", new Gson().toJson(members)));


        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        //System.out.println("addAction:key:" + wangyiAppInfo.getAppKey() + ",secret:" + wangyiAppInfo.getAppSecret());
        System.out.println("getUinfos:" + result);


        return result;
    }

    public String updateUinfo(String accid, String icon)
            throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost("https://api.netease.im/nimserver/user/updateUinfo.action");

        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
                CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type",
                "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("accid", accid));
        nvps.add(new BasicNameValuePair("icon", icon));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        // 打印执行结果
        String result = EntityUtils.toString(response.getEntity(), "utf-8");
        //System.out.println("addAction:key:" + wangyiAppInfo.getAppKey() + ",secret:" + wangyiAppInfo.getAppSecret());
        System.out.println("setUinfos:" + result);


        return result;
    }

    public static void main(String[] args) throws ClientProtocolException,
            IOException {
        // DefaultHttpClient httpClient = new DefaultHttpClient();
        //
        // HttpPost httpPost = new HttpPost(CONST.CREATE_ACTION);
        //
        // String curTime = String.valueOf((new Date()).getTime() / 1000L);
        // String checkSum = CheckSumBuilder.getCheckSum(CONST.WANGYI_APP_SCRET,
        // CONST.WANGYI_NONCE, curTime);// 参考 计算CheckSum的java代码
        //
        // // 设置请求的header
        // httpPost.addHeader("AppKey", CONST.WANGYI_APP_KEY);
        // httpPost.addHeader("Nonce", CONST.WANGYI_NONCE);
        // httpPost.addHeader("CurTime", curTime);
        // httpPost.addHeader("CheckSum", checkSum);
        // httpPost.addHeader("Content-Type",
        // "application/x-www-form-urlencoded;charset=utf-8");
        //
        // // 设置请求的参数
        // List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // // 20ab0f37e5a95aa4063a57afafa55f45
        // nvps.add(new BasicNameValuePair("accid", "helloworld"));
        // nvps.add(new BasicNameValuePair("name", "cy"));
        //
        // httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        //
        // // 执行请求
        // HttpResponse response = httpClient.execute(httpPost);
        //
        // // 打印执行结果
        //
        // "utf-8"));
        WangyiHttpUtils wUtils = new WangyiHttpUtils();
        //wUtils.removeAction("2643785802","18398850874");
        //wUtils.queryTeamMsg("2610373958", "18398850872");
        //wUtils.sendMsg("18398850872", "2610373958", "1", "");
        //wUtils.queryAction("2610373958");
//        wUtils.getUinfos("18639805480");
//        wUtils.getUinfos("18398850872");
        //wUtils.setUinfos("18639805480","http://www.lifeperfusor.com/transbox/images/20180130150727.jpg");
//        String members[] = new String[2];
//        members[0] = "18398850872";
//        members[1] = "18639805480";
//        List<String> list = new ArrayList<String>();
//        list.add("18398850872");
//        list.add("18639805480");
//		wUtils.createActionTeam("测试", "18398850872",
//				new Gson().toJson(members), "欢迎加入群组");
//        WangyiAppInfo wangyiAppInfo = new WangyiAppInfo();
//        wangyiAppInfo.setAppKey("8229955108537dc0443326c4ee0ce17a");
//        wangyiAppInfo.setAppSecret("b4e6f506d6a0");
//        wUtils.sendMsg(wangyiAppInfo);
//        BaseData baseData = new BaseData();
//        System.out.println("baseData:" + baseData.getInfo().getToken());
    }
}
