package com.life.entity;

public class TransferRecord {
    private int transferRecordId;
    private String transferRecordid = "";
    private String transfer_id = "";
    private String type = "";
    private String currentCity = "";
    private String distance = "";
    private String duration = "";
    private String remark = "";
    private String longitude = "";
    private String latitude = "";
    private String temperature = "";
    private String avgTemperature = "";
    private String power = "";
    private String expendPower = "";
    private String humidity = "";
    private String recordAt = "";
    private String dbStatus = "";
    private String createAt = "";
    private String modifyAt = "";
    private String press1 = "0";
    private String press2 = "0";
    private String flow1 = "0";
    private String flow2 = "0";
    private String pupple = "0";
    private String collision = "";
    private String open = "";
    private String stopping_power1 = "0";
    private String stopping_power2 = "0";
    private String trueTemperature = "";
    private String other = "";

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getTrueTemperature() {
        return trueTemperature;
    }

    public void setTrueTemperature(String trueTemperature) {
        this.trueTemperature = trueTemperature;
    }

    public int getTransferRecordId() {
        return transferRecordId;
    }

    public void setTransferRecordId(int transferRecordId) {
        this.transferRecordId = transferRecordId;
    }

    public String getTransferRecordid() {
        return transferRecordid;
    }

    public void setTransferRecordid(String transferRecordid) {
        this.transferRecordid = transferRecordid;
    }

    public String getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(String transferId) {
        transfer_id = transferId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(String avgTemperature) {
        this.avgTemperature = avgTemperature;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getExpendPower() {
        return expendPower;
    }

    public void setExpendPower(String expendPower) {
        this.expendPower = expendPower;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getRecordAt() {
        return recordAt;
    }

    public void setRecordAt(String recordAt) {
        this.recordAt = recordAt;
    }

    public String getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(String dbStatus) {
        this.dbStatus = dbStatus;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(String modifyAt) {
        this.modifyAt = modifyAt;
    }

    public String getPress1() {
        return press1;
    }

    public void setPress1(String press1) {
        this.press1 = press1;
    }

    public String getPress2() {
        return press2;
    }

    public void setPress2(String press2) {
        this.press2 = press2;
    }

    public String getFlow1() {
        return flow1;
    }

    public void setFlow1(String flow1) {
        this.flow1 = flow1;
    }

    public String getFlow2() {
        return flow2;
    }

    public void setFlow2(String flow2) {
        this.flow2 = flow2;
    }

    public String getPupple() {
        return pupple;
    }

    public void setPupple(String pupple) {
        this.pupple = pupple;
    }

    public String getCollision() {
        return collision;
    }

    public void setCollision(String collision) {
        this.collision = collision;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getStopping_power1() {
        return stopping_power1;
    }

    public void setStopping_power1(String stoppingPower1) {
        stopping_power1 = stoppingPower1;
    }

    public String getStopping_power2() {
        return stopping_power2;
    }

    public void setStopping_power2(String stoppingPower2) {
        stopping_power2 = stoppingPower2;
    }

    @Override
    public String toString() {
        return "TransferRecord{" +
                "transferRecordId:" + transferRecordId +
                ", transferRecordid:'" + transferRecordid + '\'' +
                ", transfer_id:'" + transfer_id + '\'' +
                ", type:'" + type + '\'' +
                ", currentCity:'" + currentCity + '\'' +
                ", distance:'" + distance + '\'' +
                ", duration:'" + duration + '\'' +
                ", remark:'" + remark + '\'' +
                ", longitude:'" + longitude + '\'' +
                ", latitude:'" + latitude + '\'' +
                ", temperature:'" + temperature + '\'' +
                ", avgTemperature:'" + avgTemperature + '\'' +
                ", power:'" + power + '\'' +
                ", expendPower:'" + expendPower + '\'' +
                ", humidity:'" + humidity + '\'' +
                ", recordAt:'" + recordAt + '\'' +
                ", dbStatus:'" + dbStatus + '\'' +
                ", createAt:'" + createAt + '\'' +
                ", modifyAt:'" + modifyAt + '\'' +
                ", press1:'" + press1 + '\'' +
                ", press2:'" + press2 + '\'' +
                ", flow1:'" + flow1 + '\'' +
                ", flow2:'" + flow2 + '\'' +
                ", pupple:'" + pupple + '\'' +
                ", collision:'" + collision + '\'' +
                ", open:'" + open + '\'' +
                ", stopping_power1:'" + stopping_power1 + '\'' +
                ", stopping_power2:'" + stopping_power2 + '\'' +
                '}';
    }
}
