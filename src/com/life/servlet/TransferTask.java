package com.life.servlet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import com.google.gson.Gson;
import com.life.controller.TransferDao;
import com.life.entity.AccessToken;
import com.life.socket.ServerThread;
import com.life.utils.CONST;

public class TransferTask extends TimerTask {

	private ServletContext mContext = null; // 设置Context上下文；
	private TransferDao mTransferDao;
	
	
	private int timeCount =0;

	public TransferTask(ServletContext context) {
		this.mContext = context;
		mTransferDao = new TransferDao();
	}

	@Override
	public void run() {
		
		//获取微信公众号的accessToken 
		String accessTokenStr = UrlServlet.getAccessToken();
		CONST.ACCESS_TOKEN = new Gson().fromJson(accessTokenStr,
				AccessToken.class).getAccess_token();
		
 
		//mTransferDao.getTransferSpeed();
		
		System.out.println("执行未完成的任务");
		mTransferDao.getTransferNoFinish();

	
		System.out.println("完成的任务");
		//mTransferDao.getTransferFinish();
		
		System.out.println("没有转运的箱子停止掉");
		mTransferDao.clearDeviceStatus();
		
		System.out.println("++++++++++++++++++"+CONST.DEVICE_ID);
		
 
		mTransferDao.getTransferSpeed();
		
		
		

	}


}