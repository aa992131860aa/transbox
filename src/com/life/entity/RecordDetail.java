package com.life.entity;

public class RecordDetail {
    private int id;
    private String recordAt;
    private String temperature;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecordAt() {
        return recordAt;
    }

    public void setRecordAt(String recordAt) {
        this.recordAt = recordAt;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
