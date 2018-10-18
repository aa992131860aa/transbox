package com.life.entity;

public class WxInfo {
	  /**
     * openId : ocAk-5eS5CcIYvTOaMW-Z46ApsJc
     * nickName : fantasy
     * gender : 1
     * language : zh_CN
     * city : Kwong
     * province : Sichuan
     * country : China
     * avatarUrl : https://wx.qlogo.cn/mmopen/vi_32/deccb3jDLJQ7XyPt4iabr5u4xdKacu4BW5Oc9wa1YBwITfLKq7GwpyBrNFRJl9af05L5TXVhxaRMSia8yibibnMia2g/132
     * unionId : omniiv1dgQeVBTV8RNYbwS7O0-Is
     * watermark : {"timestamp":1534752987,"appid":"wx323507bed41c42c8"}
     */

    private String openId;
    private String nickName;
    private int gender;
    private String language;
    private String city;
    private String province;
    private String country;
    private String avatarUrl;
    private String unionId;
    private WatermarkBean watermark;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public WatermarkBean getWatermark() {
        return watermark;
    }

    public void setWatermark(WatermarkBean watermark) {
        this.watermark = watermark;
    }

    public static class WatermarkBean {
        /**
         * timestamp : 1534752987
         * appid : wx323507bed41c42c8
         */

        private int timestamp;
        private String appid;

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }
    }
}
