package com.life.entity;

import java.util.List;

public class OpoInfo {
	private int id;
	private int opoInfoId;
	private String name;
	private String contactName;
	private String contactPhone;
	private List<OpoInfoContact> opoInfoContacts;

   
   
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOpoInfoId() {
		return opoInfoId;
	}

	public void setOpoInfoId(int opoInfoId) {
		this.opoInfoId = opoInfoId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public List<OpoInfoContact> getOpoInfoContacts() {
		return opoInfoContacts;
	}

	public void setOpoInfoContacts(List<OpoInfoContact> opoInfoContacts) {
		this.opoInfoContacts = opoInfoContacts;
	}



}
