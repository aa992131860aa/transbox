package com.life.entity;

import com.life.entity.OneSmsDetail.DataBean;

public class SmsAccount {
	//{"message":"OK","detail":"normally invoke","data":{"userAccount":"13735532673","company":"杭州莱普晟医疗科技有限公司","mobile":"13735532673","name":"杭州莱普晟医疗科技有限公司","registerTime":"2017-10-30 10:34:18","balance":2451.35,"cardSummary":{"cmcc":0,"unicom":73,"telcom":0}},"code":"0","success":true}

	  private String message;
	    private String detail;
	    private DataBean data;
	    private String code;
	    private boolean success;

	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }

	    public String getDetail() {
	        return detail;
	    }

	    public void setDetail(String detail) {
	        this.detail = detail;
	    }

	    public DataBean getData() {
	        return data;
	    }

	    public void setData(DataBean data) {
	        this.data = data;
	    }

	    public String getCode() {
	        return code;
	    }

	    public void setCode(String code) {
	        this.code = code;
	    }

	    public boolean isSuccess() {
	        return success;
	    }

	    public void setSuccess(boolean success) {
	        this.success = success;
	    }

	    public static class DataBean {
	 
	    	//"userAccount":"13735532673","company":"杭州莱普晟医疗科技有限公司","mobile":"13735532673","name":"杭州莱普晟医疗科技有限公司","registerTime":"2017-10-30 10:34:18","balance":2451.35,"cardSummary":{"cmcc":0,"unicom":73,"telcom":0
	        private String userAccount;
	        private String company;
	        private String mobile;
	        private String name;
	        private String registerTime;
	        private double balance;
			public String getUserAccount() {
				return userAccount;
			}
			public void setUserAccount(String userAccount) {
				this.userAccount = userAccount;
			}
			public String getCompany() {
				return company;
			}
			public void setCompany(String company) {
				this.company = company;
			}
			public String getMobile() {
				return mobile;
			}
			public void setMobile(String mobile) {
				this.mobile = mobile;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getRegisterTime() {
				return registerTime;
			}
			public void setRegisterTime(String registerTime) {
				this.registerTime = registerTime;
			}
			public double getBalance() {
				return balance;
			}
			public void setBalance(double balance) {
				this.balance = balance;
			}
	        
	        
	    }

	
}
