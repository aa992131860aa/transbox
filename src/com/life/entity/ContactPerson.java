package com.life.entity;

public class ContactPerson {
	private int other_id;
	private String true_name;
	private String phone;
	private String photo_url;
	private String wechat_url;
	private String is_upload_photo;
	private String name;
	private int is_friend;
	private String bind;
    
	public int getOther_id() {
		return other_id;
	}
	public void setOther_id(int otherId) {
		other_id = otherId;
	}
	public String getTrue_name() {
		return true_name;
	}
	public void setTrue_name(String trueName) {
		true_name = trueName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhoto_url() {
		return photo_url;
	}
	public void setPhoto_url(String photoUrl) {
		photo_url = photoUrl;
	}
	public String getWechat_url() {
		return wechat_url;
	}
	public void setWechat_url(String wechatUrl) {
		wechat_url = wechatUrl;
	}
	public String getIs_upload_photo() {
		return is_upload_photo;
	}
	public void setIs_upload_photo(String isUploadPhoto) {
		is_upload_photo = isUploadPhoto;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIs_friend() {
		return is_friend;
	}
	public void setIs_friend(int isFriend) {
		is_friend = isFriend;
	}
	public String getBind() {
		return bind;
	}
	public void setBind(String bind) {
		this.bind = bind;
	}
	
    
}
