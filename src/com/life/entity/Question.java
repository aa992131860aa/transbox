package com.life.entity;

public class Question {

    private int id;
    private String department;//'所在科室名称',
    private String name;//'姓名',
    private int app_sel1;// '您最近使用A胖胖的频率？',
    private int app_sel2;// '是否知道如何通过手机新建转运？',
    private int app_sel3;// '是否知道如何在app中查看转运报告？',
    private int app_sel4;// '是否知道如何在app中查看当前转运情况',
    private int app_sel5;// '您认为通过手机新建转运是否方便？',
    private int app_sel6;// ' 你认为目前app是否满足了转运过程的所有要求？',
    private String app_suggest;//'对于app的建议',
    private int device_sel1;// '您最近使用设备的频率？',
    private int device_sel2;// '是否知道如何解锁设备？',
    private int device_sel3;// '是否知道如何通过手机新建转运？',
    private int device_sel4;// '是否知道开始转运时的注意事项（如勿遮挡红外传感器）？',
    private int device_sel5;// '您认为设备在转运时相较其他厂家设备是否便捷？',
    private int device_sel6;// '您认为设备目前是否满足转运过程的所有要求？',
    private String device_suggest; // '对于转运装置的建议',

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getApp_sel1() {
        return app_sel1;
    }

    public void setApp_sel1(int app_sel1) {
        this.app_sel1 = app_sel1;
    }

    public int getApp_sel2() {
        return app_sel2;
    }

    public void setApp_sel2(int app_sel2) {
        this.app_sel2 = app_sel2;
    }

    public int getApp_sel3() {
        return app_sel3;
    }

    public void setApp_sel3(int app_sel3) {
        this.app_sel3 = app_sel3;
    }

    public int getApp_sel4() {
        return app_sel4;
    }

    public void setApp_sel4(int app_sel4) {
        this.app_sel4 = app_sel4;
    }

    public int getApp_sel5() {
        return app_sel5;
    }

    public void setApp_sel5(int app_sel5) {
        this.app_sel5 = app_sel5;
    }

    public int getApp_sel6() {
        return app_sel6;
    }

    public void setApp_sel6(int app_sel6) {
        this.app_sel6 = app_sel6;
    }

    public String getApp_suggest() {
        return app_suggest;
    }

    public void setApp_suggest(String app_suggest) {
        this.app_suggest = app_suggest;
    }

    public int getDevice_sel1() {
        return device_sel1;
    }

    public void setDevice_sel1(int device_sel1) {
        this.device_sel1 = device_sel1;
    }

    public int getDevice_sel2() {
        return device_sel2;
    }

    public void setDevice_sel2(int device_sel2) {
        this.device_sel2 = device_sel2;
    }

    public int getDevice_sel3() {
        return device_sel3;
    }

    public void setDevice_sel3(int device_sel3) {
        this.device_sel3 = device_sel3;
    }

    public int getDevice_sel4() {
        return device_sel4;
    }

    public void setDevice_sel4(int device_sel4) {
        this.device_sel4 = device_sel4;
    }

    public int getDevice_sel5() {
        return device_sel5;
    }

    public void setDevice_sel5(int device_sel5) {
        this.device_sel5 = device_sel5;
    }

    public int getDevice_sel6() {
        return device_sel6;
    }

    public void setDevice_sel6(int device_sel6) {
        this.device_sel6 = device_sel6;
    }

    public String getDevice_suggest() {
        return device_suggest;
    }

    public void setDevice_suggest(String device_suggest) {
        this.device_suggest = device_suggest;
    }
}
