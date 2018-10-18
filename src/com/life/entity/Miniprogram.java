package com.life.entity;

public class Miniprogram {
	private String appid;
	private String pagepath;
	
	public Miniprogram(){
		
	}

	public Miniprogram(String appid, String pagepath) {
		 
		this.appid = appid;
		this.pagepath = pagepath;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getPagepath() {
		return pagepath;
	}

	public void setPagepath(String pagepath) {
		this.pagepath = pagepath;
	}

}
