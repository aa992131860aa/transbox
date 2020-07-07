package com.life.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import com.google.gson.Gson;
import com.life.controller.SmsDao;
import com.life.controller.TransferDao;
import com.life.entity.AccessToken;
import com.life.entity.GroupTidInfo;
import com.life.entity.TeamContent;
import com.life.entity.TeamNumber;
import com.life.socket.ServerThread;
import com.life.utils.CONST;
import com.wangyi.BaseDao;
import com.wangyi.WangyiAppInfo;
import com.wangyi.WangyiHttpUtils;

public class TransferTask extends TimerTask {

    private ServletContext mContext = null; // 设置Context上下文；
    private TransferDao mTransferDao;

    private int timeCount = 0;

    public TransferTask(ServletContext context) {
        this.mContext = context;
        mTransferDao = new TransferDao();
    }

    public static void main(String[] args) throws ParseException {
        String phone = "11111";

    }

    @Override
    public void run() {

        // 获取微信公众号的accessToken
        String accessTokenStr = UrlServlet.getAccessToken();
        CONST.ACCESS_TOKEN = new Gson().fromJson(accessTokenStr,
                AccessToken.class).getAccess_token();
        //System.out.println("ACCESS_TOKEN:" + CONST.ACCESS_TOKEN);
        //获取网易云的app_key和app_secret
//		WangyiAppInfo wangyiAppInfo = new BaseDao().gainWangyiAppInfo();
//		if(wangyiAppInfo!=null){
//			CONST.WANGYI_APP_KEY = wangyiAppInfo.getAppKey();
//			CONST.WANGYI_APP_SCRET = wangyiAppInfo.getAppSecret();
//		}

//		System.out.println("WANGYI_APP_KEY:"+CONST.ACCESS_TOKEN);
        System.out.println("执行循环任务");

        // mTransferDao.getTransferSpeed();

        //System.out.println("执行未完成的任务");
        mTransferDao.getTransferNoFinish();

        //System.out.println("完成的任务");
        //mTransferDao.getTransferFinish();
        //去除转运时间超出起始时间的点
        mTransferDao.cleanTransferLessThanGetTime();

        //去掉重复点
        mTransferDao.cleanTransferRepeat();

        //System.out.println("没有转运的箱子停止掉");
        mTransferDao.clearDeviceStatus();


        //更新transfer表的省份字段
        mTransferDao.updateTransboxProvince();


        //删除超过一个月的转运群组、并保存聊天信息
        try {
            List<GroupTidInfo> groupTidInfoList = mTransferDao.gainGroupInfoMonthAgo();
            WangyiHttpUtils wUtils = new WangyiHttpUtils();
            for (GroupTidInfo groupTidInfo : groupTidInfoList) {

                String result = wUtils.queryTeamMsg(groupTidInfo.getTid(), groupTidInfo.getPhone(), groupTidInfo.getStartAt());
                TeamContent teamContent = new Gson().fromJson(result, TeamContent.class);
                if (teamContent.getCode() == 200 && teamContent.getSize() < 100) {
                    //插入team_msg  更改transfer_group_team群组状态1
                    mTransferDao.dealGroupHistoryContent(groupTidInfo.getTid(), result, groupTidInfo.getTransferId());
                    wUtils.removeAction(groupTidInfo.getTid(), groupTidInfo.getPhone());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("解散群组错误：" + e.getMessage());
        }


        // 每天02:00更新
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        if (sdf.format(new Date()).contains("03")) {
            System.out.println("更新SIM卡信息" + sdf.format(new Date()));
            new SmsDao().dealSms();
        }
        System.out.println("++++++++++++++++++" + CONST.DEVICE_ID);

        mTransferDao.getTransferSpeed();


    }

}