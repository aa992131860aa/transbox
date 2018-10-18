package com.life.utils;

import java.util.ArrayList;
import java.util.List;

public class CONST {
   //public final static String URL_PATH = "http://116.62.28.28:8080/transbox/";
   public final static String URL_PATH = "http://www.lifeperfusor.com/transbox/";
   public final static int WORD_SIZE =12;
   public final static int SEND_OK =0;
   public final static int SEND_FAIL =1;
   public final static int BAD_PARAM =2;
   public final static int NO_MORE =3;
   public final static int NO_REGISTER =4;
   public static String ERROR="";
   public final static String YUN_BA_APPKEY = "59f95a6ccd3e3d932f093eb9";
   public final static String YUN_BA_SECKEY = "sec-0rWAc2PXXMQ8CrDlMeT65JZgkC5apbxYQcWmVjwrqx6b028h";
   public final static int SEND_PUSH =0;
   public final static int NO_SEND_PUSH =1;
   
   public final static String DOCTOR = "2";
   public final static String NURSE = "5";
   
   public final static String WECHAT_APP_ID = "wx323507bed41c42c8";
   public final static String WECHAT_SECRET = "b0d04f48fb57d4c73ab42652b59037d2";
   
   public static String ACCESS_TOKEN = "";
   
   //设备的id
   public static String DEVICE_ID= "";
   
   public static void main(String[] args) {
		List<String> phonePush = new ArrayList<String>();
		phonePush.add("111");
		phonePush.add("333");
		String [] p = (String[]) phonePush.toArray();
		System.out.println(p[0]+p[1]);
}
}
