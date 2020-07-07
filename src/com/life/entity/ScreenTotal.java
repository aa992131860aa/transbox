package com.life.entity;

import java.util.List;

public class ScreenTotal {
    private int allTotal;
    private List<ScreenInfoCountTwo> screenInfoCountList;

    public int getAllTotal() {
        return allTotal;
    }

    public void setAllTotal(int allTotal) {
        this.allTotal = allTotal;
    }

    public List<ScreenInfoCountTwo> getScreenInfoCountList() {
        return screenInfoCountList;
    }

    public void setScreenInfoCountList(List<ScreenInfoCountTwo> screenInfoCountList) {
        this.screenInfoCountList = screenInfoCountList;
    }
}
