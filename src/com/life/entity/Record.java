package com.life.entity;

import java.io.Serializable;

public class Record implements Serializable{
    /**
     * transfer_id : 501
     * temperature : 27.1
     * power : 100
     * humidity : 58.0
     * collision : 0.0
     * open : 0
     * duration : 0
     * currentCity : 杭州市
     * expendPower : 0
     */
    private int id;
    private String transfer_id;
    private String temperature;
    private String power;
    private String humidity;
    private String collision;
    private String open;
    private String duration;
    private String currentCity;
    private String expendPower;
    private String recordAt;
    private String distance;
    private String longitude;
    private String latitude;
    private String transferId;
    private String remark;//(箱子编号)
    
    private String voltage;
    private String trueTemperature;
    private String other;
    private String boxTemperature;
    private String altitude;
    
    

    public String getAltitude() {
		return altitude;
	}

	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	public String getBoxTemperature() {
		return boxTemperature;
	}

	public void setBoxTemperature(String boxTemperature) {
		this.boxTemperature = boxTemperature;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(String transfer_id) {
        this.transfer_id = transfer_id;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getExpendPower() {
        return expendPower;
    }

    public void setExpendPower(String expendPower) {
        this.expendPower = expendPower;
    }

	public String getRecordAt() {
		return recordAt;
	}

	public void setRecordAt(String recordAt) {
		this.recordAt = recordAt;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
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

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}
	

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	

	public String getVoltage() {
		return voltage;
	}

	public void setVoltage(String voltage) {
		this.voltage = voltage;
	}

	public String getTrueTemperature() {
		return trueTemperature;
	}

	public void setTrueTemperature(String trueTemperature) {
		this.trueTemperature = trueTemperature;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	@Override
	public String toString() {
		return "Record [altitude=" + altitude + ", boxTemperature="
				+ boxTemperature + ", collision=" + collision
				+ ", currentCity=" + currentCity + ", distance=" + distance
				+ ", duration=" + duration + ", expendPower=" + expendPower
				+ ", humidity=" + humidity + ", id=" + id + ", latitude="
				+ latitude + ", longitude=" + longitude + ", open=" + open
				+ ", other=" + other + ", power=" + power + ", recordAt="
				+ recordAt + ", remark=" + remark + ", temperature="
				+ temperature + ", transferId=" + transferId + ", transfer_id="
				+ transfer_id + ", trueTemperature=" + trueTemperature
				+ ", voltage=" + voltage + "]";
	}

 
    
	




}
