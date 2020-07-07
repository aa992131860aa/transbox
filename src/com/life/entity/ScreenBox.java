package com.life.entity;

public class ScreenBox {
    // 电量 温度 湿度 当前位置  箱子状态（充电中、使用中、空闲中）
    private int power;
    private double temperature;
    private double humidity;
    private String currentCity="";
    private String boxNo="00000";
    private String status="";
    private String recordAt="";


    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
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

    public String getRecordAt() {
        return recordAt;
    }

    public void setRecordAt(String recordAt) {
        this.recordAt = recordAt;
    }
}
