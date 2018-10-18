package com.life.entity;

import java.util.List;

public class WorkloadPersonInfo {
     
	private String time;
	private String trueName;
	private String photoUrl;
	private String roleName;
	private String phone;
	private List<Workload> workloads;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTrueName() {
		return trueName;
	}
	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public List<Workload> getWorkloads() {
		return workloads;
	}
	public void setWorkloads(List<Workload> workloads) {
		this.workloads = workloads;
	}
	
	
	
}
