package com.life.entity;

import java.sql.Date;
import java.sql.Time;

public class Organ {
    private int id;
    private String segmentNum;
    private String bloodType;
    private String organSampleType;
    private String organType;
    private String organCount;
    private String bloodCount;
    private String organSampleCount;
    private Date createDate;
    private Time createTime;
    private String modifyTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSegmentNum() {
		return segmentNum;
	}
	public void setSegmentNum(String segmentNum) {
		this.segmentNum = segmentNum;
	}
    
	public String getBloodType() {
		return bloodType;
	}
	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}
	public String getOrganSampleType() {
		return organSampleType;
	}
	public void setOrganSampleType(String organSampleType) {
		this.organSampleType = organSampleType;
	}
	public String getOrganType() {
		return organType;
	}
	public void setOrganType(String organType) {
		this.organType = organType;
	}
	public String getOrganCount() {
		return organCount;
	}
	public void setOrganCount(String organCount) {
		this.organCount = organCount;
	}
	public String getBloodCount() {
		return bloodCount;
	}
	public void setBloodCount(String bloodCount) {
		this.bloodCount = bloodCount;
	}
	public String getOrganSampleCount() {
		return organSampleCount;
	}
	public void setOrganSampleCount(String organSampleCount) {
		this.organSampleCount = organSampleCount;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Time getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Time createTime) {
		this.createTime = createTime;
	}
	public String getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
    
    
    
    
}
