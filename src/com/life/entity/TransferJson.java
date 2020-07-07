package com.life.entity;

import java.util.ArrayList;
import java.util.List;

public class TransferJson {
	private String transferid= "";
	private String organSeg= "";
	private String openPsd= "";
	private String fromCity= "";
	private String toHospName= "";
	private String tracfficType= "";
	private String tracfficNumber= "";
	private String organ= "";
	private String organNum= "";
	private String blood= "";
	private String bloodNum= "";
	private String sampleOrgan= "";
	private String sampleOrgan1= "";
	private String sampleOrganNum= "";
	private String opoName= "";
	private String contactName= "";
	private String contactPhone= "";
	private String phone= "";
	private String trueName= "";
	private String getTime= "";
	private String isStart= "";
	private String startLong= "";
	private String startLati= "";
	private String endLong= "";
	private String endLati= "";
	private String distance= "";
	private String toHosp= "";
	private String boxNo= "";
	private String status= "";
	private String nowDistance= "";
	private String time= "";
	private String isGroup= "";
	private String opoContactName= "";
	private String opoContactPhone= "";
	private String phones="";
	private String deviceId = "";
	private int autoTransfer = 0;
	private String modifyOrganSeg = "";
	private int deviceStatus = 0;
	private int filterStatus ;
	private String currentCity="";
	private String quality= "优良";
	private String pushException= "";
	private String pushExceptionTime= "";
	private String temperature="";
	private String weather="";
	private int check = 0;
	private List<String> temperatureList= new ArrayList<String>();
	//0 未开始  1为暂停
	private int pause;
	private String currentTemperature="";
	private String avgT="0";
	private String avgH="0";
	private String collisionTotal="0";
	private String tid="";
	private String groupName="";
	
	//无查看权限
	private String fromCityShow="";
	
	
	
	
	
	public String getFromCityShow() {
		return fromCityShow;
	}
	public void setFromCityShow(String fromCityShow) {
		this.fromCityShow = fromCityShow;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getAvgT() {
		return avgT;
	}
	public void setAvgT(String avgT) {
		this.avgT = avgT;
	}
	public String getAvgH() {
		return avgH;
	}
	public void setAvgH(String avgH) {
		this.avgH = avgH;
	}
	public String getCollisionTotal() {
		return collisionTotal;
	}
	public void setCollisionTotal(String collisionTotal) {
		this.collisionTotal = collisionTotal;
	}
	public String getCurrentTemperature() {
		return currentTemperature;
	}
	public void setCurrentTemperature(String currentTemperature) {
		this.currentTemperature = currentTemperature;
	}
	public int getCheck() {
		return check;
	}
	public void setCheck(int check) {
		this.check = check;
	}
	public String getPushExceptionTime() {
		return pushExceptionTime;
	}
	public void setPushExceptionTime(String pushExceptionTime) {
		this.pushExceptionTime = pushExceptionTime;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getSampleOrgan1() {
		return sampleOrgan1;
	}
	public void setSampleOrgan1(String sampleOrgan1) {
		this.sampleOrgan1 = sampleOrgan1;
	}
	public List<String> getTemperatureList() {
		return temperatureList;
	}
	public void setTemperatureList(List<String> temperatureList) {
		this.temperatureList = temperatureList;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	public String getCurrentCity() {
		return currentCity;
	}
	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}
	public String getTransferid() {
		return transferid;
	}
	public void setTransferid(String transferid) {
		this.transferid = transferid;
	}
	public String getOrganSeg() {
		return organSeg;
	}
	public void setOrganSeg(String organSeg) {
		this.organSeg = organSeg;
	}
	public String getOpenPsd() {
		return openPsd;
	}
	public void setOpenPsd(String openPsd) {
		this.openPsd = openPsd;
	}
	public String getFromCity() {
		return fromCity;
	}
	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}
	public String getToHospName() {
		return toHospName;
	}
	public void setToHospName(String toHospName) {
		this.toHospName = toHospName;
	}
	public String getTracfficType() {
		return tracfficType;
	}
	public void setTracfficType(String tracfficType) {
		this.tracfficType = tracfficType;
	}
	public String getTracfficNumber() {
		return tracfficNumber;
	}
	public void setTracfficNumber(String tracfficNumber) {
		this.tracfficNumber = tracfficNumber;
	}
	public String getOrgan() {
		return organ;
	}
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	public String getOrganNum() {
		return organNum;
	}
	public void setOrganNum(String organNum) {
		this.organNum = organNum;
	}
	public String getBlood() {
		return blood;
	}
	public void setBlood(String blood) {
		this.blood = blood;
	}
	public String getBloodNum() {
		return bloodNum;
	}
	public void setBloodNum(String bloodNum) {
		this.bloodNum = bloodNum;
	}
	public String getSampleOrgan() {
		return sampleOrgan;
	}
	public void setSampleOrgan(String sampleOrgan) {
		this.sampleOrgan = sampleOrgan;
	}
	public String getSampleOrganNum() {
		return sampleOrganNum;
	}
	public void setSampleOrganNum(String sampleOrganNum) {
		this.sampleOrganNum = sampleOrganNum;
	}
	public String getOpoName() {
		return opoName;
	}
	public void setOpoName(String opoName) {
		this.opoName = opoName;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getTrueName() {
		return trueName;
	}
	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
	public String getGetTime() {
		return getTime;
	}
	public void setGetTime(String getTime) {
		this.getTime = getTime;
	}
	public String getIsStart() {
		return isStart;
	}
	public void setIsStart(String isStart) {
		this.isStart = isStart;
	}
	public String getStartLong() {
		return startLong;
	}
	public void setStartLong(String startLong) {
		this.startLong = startLong;
	}
	public String getStartLati() {
		return startLati;
	}
	public void setStartLati(String startLati) {
		this.startLati = startLati;
	}
	public String getEndLong() {
		return endLong;
	}
	public void setEndLong(String endLong) {
		this.endLong = endLong;
	}
	public String getEndLati() {
		return endLati;
	}
	public void setEndLati(String endLati) {
		this.endLati = endLati;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getToHosp() {
		return toHosp;
	}
	public void setToHosp(String toHosp) {
		this.toHosp = toHosp;
	}
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNowDistance() {
		return nowDistance;
	}
	public void setNowDistance(String nowDistance) {
		this.nowDistance = nowDistance;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIsGroup() {
		return isGroup;
	}
	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}
	public String getOpoContactName() {
		return opoContactName;
	}
	public void setOpoContactName(String opoContactName) {
		this.opoContactName = opoContactName;
	}
	public String getOpoContactPhone() {
		return opoContactPhone;
	}
	public void setOpoContactPhone(String opoContactPhone) {
		this.opoContactPhone = opoContactPhone;
	}
	public String getPushException() {
		return pushException;
	}
	public void setPushException(String pushException) {
		this.pushException = pushException;
	}
	public String getPhones() {
		return phones;
	}
	public void setPhones(String phones) {
		this.phones = phones;
	}
	public int getAutoTransfer() {
		return autoTransfer;
	}
	public void setAutoTransfer(int autoTransfer) {
		this.autoTransfer = autoTransfer;
	}
	public String getModifyOrganSeg() {
		return modifyOrganSeg;
	}
	public void setModifyOrganSeg(String modifyOrganSeg) {
		this.modifyOrganSeg = modifyOrganSeg;
	}
	public int getDeviceStatus() {
		return deviceStatus;
	}
	public void setDeviceStatus(int deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public int getFilterStatus() {
		return filterStatus;
	}
	public void setFilterStatus(int filterStatus) {
		this.filterStatus = filterStatus;
	}
	public int getPause() {
		return pause;
	}
	public void setPause(int pause) {
		this.pause = pause;
	}

     
	
	

}
