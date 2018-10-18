package com.life.entity;

public class WorkloadNurse {
	
    private String circuitNurse = "巡回护士";
    private int circuitNurseCount = 0;
    private String washNurse = "洗手护士";
    private int washNurseCount = 0;
   
    private String time;
	public String getCircuitNurse() {
		return circuitNurse;
	}
	public void setCircuitNurse(String circuitNurse) {
		this.circuitNurse = circuitNurse;
	}
	public String getWashNurse() {
		return washNurse;
	}
	public void setWashNurse(String washNurse) {
		this.washNurse = washNurse;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getCircuitNurseCount() {
		return circuitNurseCount;
	}
	public void setCircuitNurseCount(int circuitNurseCount) {
		this.circuitNurseCount = circuitNurseCount;
	}
	public int getWashNurseCount() {
		return washNurseCount;
	}
	public void setWashNurseCount(int washNurseCount) {
		this.washNurseCount = washNurseCount;
	}

    
}
