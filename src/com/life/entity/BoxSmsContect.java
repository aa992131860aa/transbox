package com.life.entity;

 

public class BoxSmsContect {
	//{"message":"OK","detail":"normally invoke","data":{"iccid":"89860618050008541476","status":"DEACTIVATED_NAME"},"code":"0","success":true}
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
	    	private String iccid;
	    	private String status;
			public String getIccid() {
				return iccid;
			}
			public void setIccid(String iccid) {
				this.iccid = iccid;
			}
			public String getStatus() {
				return status;
			}
			public void setStatus(String status) {
				this.status = status;
			}
	    	
	    }
	    
}
