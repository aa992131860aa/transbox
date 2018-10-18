package com.life.servlet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.life.socket.ServerThread;
import com.life.socket.SocketThread;

public class ContextListener implements ServletContextListener {

	Timer mTimer;
	// socket server 线程
	private SocketThread socketThread;

	// 参数为空的构造方法
	public ContextListener() {

	}

	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
		if (mTimer != null) {
			mTimer.cancel();
		}
		// if (null != socketThread && !socketThread.isInterrupted()) {
		// socketThread.closeSocketServer();
		// socketThread.interrupt();
		// }
		// System.out.println("定时器已经销毁");
		event.getServletContext().log("定时器已经销毁");
	}

	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		// 设置一个定时器；20分钟每次

		mTimer = new Timer(true);
		int time1 = (int) (20 * 60 * 1000);
		event.getServletContext().log("定时器已经启动");

		// 定时器定时到指定时间，执行相应的方法或类；
		mTimer.schedule(new TransferTask(event.getServletContext()),
				10 * 60 * 1000, time1);
		event.getServletContext().log("加入时间调度表");
		System.out.println("socketThread:" + socketThread);
		// if(null==socketThread)
		// {
		//			
		// //新建线程类
		// socketThread=new SocketThread(null);
		// //启动线程
		// socketThread.start();
		// }
		socket20008();
	}

	private void socket20008() {
		// 服务端在20006端口监听客户端请求的TCP连接
		new Thread() {
			public void run() {
				try {
					// 服务端在20006端口监听客户端请求的TCP连接
					ServerSocket server = new ServerSocket(20008);
					Socket client = null;
					boolean f = true;
					while (f) {
						// 等待客户端的连接，如果没有获取连接
						client = server.accept();
						System.out.println("与客户端连接成功！");
						// 为每个客户端连接开启一个线程
						new Thread(new ServerThread(client)).start();
					}
					server.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();

	}

}
