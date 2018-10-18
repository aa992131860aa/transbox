package com.life.entity;

import java.util.List;

public class HospitalZone {
   private String zone;
   private List<String> hospitalNames;
public String getZone() {
	return zone;
}
public void setZone(String zone) {
	this.zone = zone;
}
public List<String> getHospitalNames() {
	return hospitalNames;
}
public void setHospitalNames(List<String> hospitalNames) {
	this.hospitalNames = hospitalNames;
}
   
}
