package com.life.utils;

public class WechatOpen {
	//{"session_key":"8pJxtUas09Bo2ISqhTzu1Q==","openid":"ocAk-5eS5CcIYvTOaMW-Z46ApsJc"}
	
	//{"errcode":40163,"errmsg":"code been used, hints: [ req_id: 9pcdCA00232269 ]"}
	
	private String session_key;
	private String openid;
	public String getSession_key() {
		return session_key;
	}
	public void setSession_key(String sessionKey) {
		session_key = sessionKey;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	
}
