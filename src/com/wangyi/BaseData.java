package com.wangyi;

public class BaseData {

	// {
	// "code":200,
	// "info":{"token":"xx","accid":"xx","name":"xx"}
	// }
	private int code;
	private Info info=new Info();
	

	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}


	public Info getInfo() {
		return info;
	}


	public void setInfo(Info info) {
		this.info = info;
	}


	@Override
	public String toString() {
		return "BaseData [code=" + code + ", info=" + info + "]";
	}


	public class Info {
		private String token = "";
		private String accid = "";
		private String name = "";
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public String getAccid() {
			return accid;
		}
		public void setAccid(String accid) {
			this.accid = accid;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return "Info [accid=" + accid + ", name=" + name + ", token="
					+ token + "]";
		}
		
		
	}
}
