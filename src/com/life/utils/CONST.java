package com.life.utils;

import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class CONST {
    //public final static String URL_PATH = "http://116.62.28.28:8080/transbox/";
    public final static String URL_PATH = "https://www.lifeperfusor.com/transbox/";
    public final static int WORD_SIZE = 12;
    public final static int SEND_OK = 0;
    public final static int SEND_FAIL = 1;
    public final static int BAD_PARAM = 2;
    public final static int NO_MORE = 3;
    public final static int NO_REGISTER = 4;
    public final static int NO_DATA = 5;
    public static String RONG_SEND_SEG = "";
    public static double DISTANCE = 123;
    public static double HEIGHT = 0;
    public static String ERROR = "";
    public final static String YUN_BA_APPKEY = "59f95a6ccd3e3d932f093eb9";
    public final static String YUN_BA_SECKEY = "sec-0rWAc2PXXMQ8CrDlMeT65JZgkC5apbxYQcWmVjwrqx6b028h";
    public  static int SEND_PUSH = 0;
    public final static int NO_SEND_PUSH = 1;
    public final static Map<String, IoSession> ALL_IO_SESSION = new HashMap<>();

    public final static String DOCTOR = "2";
    public final static String NURSE = "5";

    public final static String WECHAT_APP_ID = "wx323507bed41c42c8";
    public final static String WECHAT_SECRET = "b0d04f48fb57d4c73ab42652b59037d2";

    public static String ACCESS_TOKEN = "";

    //设备的id
    public static String DEVICE_ID = "";


    //网易云即时通讯的设置
    public static String WANGYI_APP_KEY = "8229955108537dc0443326c4ee0ce17a";
    public static String WANGYI_APP_SCRET = "b4e6f506d6a0";
    public final static String WANGYI_NONCE = "otqc";
    public final static String CREATE_ACTION = "https://api.netease.im/nimserver/user/create.action";
    public final static String ADD_ACTION = "https://api.netease.im/nimserver/team/add.action";
    public final static String CREATE_ACTION_TEAM = "https://api.netease.im/nimserver/team/create.action";
    public final static String REMOVE_ACTION = "https://api.netease.im/nimserver/team/remove.action";
    public final static String KICK_ACTION = "https://api.netease.im/nimserver/team/kick.action";
    public final static String SEND_MSG = "https://api.netease.im/nimserver/msg/sendMsg.action";
    public final static String BLOCK_ACTION = "https://api.netease.im/nimserver/user/block.action";
    public final static String UN_BLOCK_ACTION = "https://api.netease.im/nimserver/user/unblock.action";
    public final static String REFRESH_TOKEN_ACTION = "https://api.netease.im/nimserver/user/refreshToken.action";

    public final static String SIM_APP_ID = "102420130542";
    public final static String SIM_APP_SCREAT = "6e677a1f45b5e34aed4ca5d0be667b20";


    public static void main(String[] args) {
        List<String> phonePush = new ArrayList<String>();
        phonePush.add("111");
        phonePush.add("333");
        String[] p = (String[]) phonePush.toArray();

    }
}
