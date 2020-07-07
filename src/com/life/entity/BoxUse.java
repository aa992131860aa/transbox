package com.life.entity;

public class BoxUse { 
	private int boxId;
	private String boxNo;
	private String transferStatus;
	private String status;
	public static BoxUse a = new BoxUse();
	public int getBoxId() {
		return boxId;
	}
	public void setBoxId(int boxId) {
		this.boxId = boxId;
	}
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public String getTransferStatus() {
		return transferStatus;
	}
	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public static BoxUse getA() {
		return a;
	}
	public static void setA(BoxUse a) {
		BoxUse.a = a;
	}
	public static void main(String[] args) {

	}
	@Override
	public String toString() {
		return "BoxUse [boxId=" + boxId + ", boxNo=" + boxNo + ", status="
				+ status + ", transferStatus=" + transferStatus + "]";
	}

}
