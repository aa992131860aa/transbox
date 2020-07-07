package com.life.entity;

public class OneSmsDetail {
//	字段名	字段类型	字段含义	可以为空	参数举例
//	iccid	string	查询的iccid	否	898606182111132823
//	imsi	string	所查询的imsi	电信卡为空，联通、移动卡非空	400282378292
//	type	string	卡类型	SINGLE：单卡，POOL：流量池卡	SINGLE
//	status	string	状态，测试中：testing、库存：inventory、待激活：pending-activation、已激活：activation、已停卡：deactivation、已销卡：retired	否	activation
//	msisdn	string	对应的手机号码	否	10641211212123
//	startDate	string	激活时间	是	2017-06-30 23:59:59
//	expireDate	string	过期时间	是	2017-09-30 23:59:59
//	lastSyncDate	string	套餐用量同步时间	是	2017-09-30 23:59:59
//	memo	string	卡备注	是	卡备注信息
//	orgName	string	卡所属机构名称	是	卡所属机构名称
//	imeiStatus	string	机卡绑定状态，NOT_SET：未开启，NOT_BIND：待绑定，NORMAL：正常，SPLIT：机卡分离	否	NOT_BIND
//	speedLimit	double	网络限速值，单位：Kbps，4G上限为150Mbps(153600Kbps)	否	64
//	remark	string	卡片订单解释	是	根据 xxxxxxxxx 订单号出库
//	iratePlanName	string	卡当前套餐名称	是	标准月套餐-30M
//	ratePlanId	int	卡当前套餐ID	是	123
//	dataUsage	double	卡本月用量, 单位M	否	23.4
//	totalDataVolume	double	卡套餐大小, 单位M	否	1024
//	usedDataVolume	double	卡套餐用量，如果未激活无此字段，如果激活则为当前套餐用量，如果已经过期停卡则为最后一个套餐的用量	否	23.4
//	realName	string	卡实名信息	是	姚妍
//	realNameCertifystatus	string	卡实名审核状态，审核通过：pass、未提交：not-submit、审核不通过：not-pass、待审核：not-audit	是	pass
//	realnameRequired	boolean	卡实名需求，需要实名：true、不需要实名：false	是	true
//	carrier	string	运营商，unicom：中国联通，cmcc：中国移动，chinanet：中国电信	否	unicom
//	functions	string array	卡功能列表	是	["UNLIMITED_VOLUME"]
//	deviceStatus	string	卡在运营商的状态, 可测试: TEST_READY_NAME, 库存：INVENTORY_NAME，可激活：ACTIVATION_READY_NAME， 已激活：ACTIVATED_NAME， 已停卡：DEACTIVATED_NAME，已销卡：RETIRED_NAME, 已清除：PURGED_NAME	否	ACTIVATED_NAME
//	openDate	date	出库时间	否	2018-03-06 11:24:29
//	activeDuration	int	激活宽限期（天）	是	12
//	cardPoolId	int	流量池id，type为POOL的才有此字段	是	12
//	testingExpireDate	date	测试期结束时间, 无测试期的卡此字段无效	否	2018-03-02 12:12:12
//	ratePlanEffetiveDate	date	卡套餐生效时间，如果未激活无此字段，如果激活则为当前套餐，如果已经过期停卡则为最后一个套餐	是	false
//	ratePlanExpirationDate	date	套餐过期时间，如果未激活无此字段，如果激活则为当前套餐，如果已经过期停卡则为最后一个套餐	是	false
	
    /**
     * message : OK
     * detail : normally invoke
     * data : {"iccid":"89860618050008541476","imsi":"460067015054147","msisdn":"861064657154147","carrier":"unicom","type":"SINGLE","status":"activation","deviceStatus":"ACTIVATED_NAME","openDate":"2018-09-07 15:46:24","startDate":"2018-10-30 10:56:05","expireDate":"2019-01-26 23:59:59","lastSyncDate":"2018-11-27 00:09:28","activeDuration":89,"realnameRequired":false,"realNameCertifystatus":"not-submit","testingExpireDate":"2018-09-07 15:46:24","ratePlanId":44,"iratePlanName":"500.0M/月","usedDataVolume":0,"totalDataVolume":500,"ratePlanEffetiveDate":"2018-11-27 00:00:00","ratePlanExpirationDate":"2018-12-26 23:59:59","dataUsage":0,"orgName":"杭州莱普晟医疗科技有限公司","imeiStatus":"NOT_SET","speedLimit":153600}
     * code : 0
     * success : true
     */

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
        /**
         * iccid : 89860618050008541476
         * imsi : 460067015054147
         * msisdn : 861064657154147
         * carrier : unicom
         * type : SINGLE
         * status : activation
         * deviceStatus : ACTIVATED_NAME
         * openDate : 2018-09-07 15:46:24
         * startDate : 2018-10-30 10:56:05
         * expireDate : 2019-01-26 23:59:59
         * lastSyncDate : 2018-11-27 00:09:28
         * activeDuration : 89
         * realnameRequired : false
         * realNameCertifystatus : not-submit
         * testingExpireDate : 2018-09-07 15:46:24
         * ratePlanId : 44
         * iratePlanName : 500.0M/月
         * usedDataVolume : 0.0
         * totalDataVolume : 500.0
         * ratePlanEffetiveDate : 2018-11-27 00:00:00
         * ratePlanExpirationDate : 2018-12-26 23:59:59
         * dataUsage : 0.0
         * orgName : 杭州莱普晟医疗科技有限公司
         * imeiStatus : NOT_SET
         * speedLimit : 153600.0
         */

        private String iccid;
        private String imsi;
        private String msisdn;
        private String carrier;
        private String type;
        private String status;
        private String deviceStatus;
        private String openDate;
        private String startDate;
        private String expireDate;
        private String lastSyncDate;
        private int activeDuration;
        private boolean realnameRequired;
        private String realNameCertifystatus;
        private String testingExpireDate;
        private int ratePlanId;
        private String iratePlanName;
        private double usedDataVolume;
        private double totalDataVolume;
        private String ratePlanEffetiveDate;
        private String ratePlanExpirationDate;
        private double dataUsage;
        private String orgName;
        private String imeiStatus;
        private double speedLimit;

        public String getIccid() {
            return iccid;
        }

        public void setIccid(String iccid) {
            this.iccid = iccid;
        }

        public String getImsi() {
            return imsi;
        }

        public void setImsi(String imsi) {
            this.imsi = imsi;
        }

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDeviceStatus() {
            return deviceStatus;
        }

        public void setDeviceStatus(String deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public String getOpenDate() {
            return openDate;
        }

        public void setOpenDate(String openDate) {
            this.openDate = openDate;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(String expireDate) {
            this.expireDate = expireDate;
        }

        public String getLastSyncDate() {
            return lastSyncDate;
        }

        public void setLastSyncDate(String lastSyncDate) {
            this.lastSyncDate = lastSyncDate;
        }

        public int getActiveDuration() {
            return activeDuration;
        }

        public void setActiveDuration(int activeDuration) {
            this.activeDuration = activeDuration;
        }

        public boolean isRealnameRequired() {
            return realnameRequired;
        }

        public void setRealnameRequired(boolean realnameRequired) {
            this.realnameRequired = realnameRequired;
        }

        public String getRealNameCertifystatus() {
            return realNameCertifystatus;
        }

        public void setRealNameCertifystatus(String realNameCertifystatus) {
            this.realNameCertifystatus = realNameCertifystatus;
        }

        public String getTestingExpireDate() {
            return testingExpireDate;
        }

        public void setTestingExpireDate(String testingExpireDate) {
            this.testingExpireDate = testingExpireDate;
        }

        public int getRatePlanId() {
            return ratePlanId;
        }

        public void setRatePlanId(int ratePlanId) {
            this.ratePlanId = ratePlanId;
        }

        public String getIratePlanName() {
            return iratePlanName;
        }

        public void setIratePlanName(String iratePlanName) {
            this.iratePlanName = iratePlanName;
        }

        public double getUsedDataVolume() {
            return usedDataVolume;
        }

        public void setUsedDataVolume(double usedDataVolume) {
            this.usedDataVolume = usedDataVolume;
        }

        public double getTotalDataVolume() {
            return totalDataVolume;
        }

        public void setTotalDataVolume(double totalDataVolume) {
            this.totalDataVolume = totalDataVolume;
        }

        public String getRatePlanEffetiveDate() {
            return ratePlanEffetiveDate;
        }

        public void setRatePlanEffetiveDate(String ratePlanEffetiveDate) {
            this.ratePlanEffetiveDate = ratePlanEffetiveDate;
        }

        public String getRatePlanExpirationDate() {
            return ratePlanExpirationDate;
        }

        public void setRatePlanExpirationDate(String ratePlanExpirationDate) {
            this.ratePlanExpirationDate = ratePlanExpirationDate;
        }

        public double getDataUsage() {
            return dataUsage;
        }

        public void setDataUsage(double dataUsage) {
            this.dataUsage = dataUsage;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getImeiStatus() {
            return imeiStatus;
        }

        public void setImeiStatus(String imeiStatus) {
            this.imeiStatus = imeiStatus;
        }

        public double getSpeedLimit() {
            return speedLimit;
        }

        public void setSpeedLimit(double speedLimit) {
            this.speedLimit = speedLimit;
        }

		@Override
		public String toString() {
			return "DataBean [activeDuration=" + activeDuration + ", carrier="
					+ carrier + ", dataUsage=" + dataUsage + ", deviceStatus="
					+ deviceStatus + ", expireDate=" + expireDate + ", iccid="
					+ iccid + ", imeiStatus=" + imeiStatus + ", imsi=" + imsi
					+ ", iratePlanName=" + iratePlanName + ", lastSyncDate="
					+ lastSyncDate + ", msisdn=" + msisdn + ", openDate="
					+ openDate + ", orgName=" + orgName
					+ ", ratePlanEffetiveDate=" + ratePlanEffetiveDate
					+ ", ratePlanExpirationDate=" + ratePlanExpirationDate
					+ ", ratePlanId=" + ratePlanId + ", realNameCertifystatus="
					+ realNameCertifystatus + ", realnameRequired="
					+ realnameRequired + ", speedLimit=" + speedLimit
					+ ", startDate=" + startDate + ", status=" + status
					+ ", testingExpireDate=" + testingExpireDate
					+ ", totalDataVolume=" + totalDataVolume + ", type=" + type
					+ ", usedDataVolume=" + usedDataVolume + "]";
		}
        
    }

	@Override
	public String toString() {
		return "OneSmsDetail [code=" + code + ", data=" + data + ", detail="
				+ detail + ", message=" + message + ", success=" + success
				+ "]";
	}
    
}
