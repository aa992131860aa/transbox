package com.life.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 99213 on 2017/11/2.
 */

public class YunBaJson {

    /**
     * method : method
     * appkey : app-key
     * seckey : secret-key
     * topic : topic
     * msg : message
     * opts : {"time_to_live":"number","qos":"number","apn_json":{"aps":{"alert":"string","badge":"number","sound":"string","content-available":"number"}},"third_party_push":{"notification_title":"你好","notification_content":"云巴推送"}}
     */

    private String method;
    private String appkey;
    private String seckey;
    private String alias;
    private String [] aliases;
    private String msg;
    private OptsBean opts;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getSeckey() {
        return seckey;
    }

    public void setSeckey(String seckey) {
        this.seckey = seckey;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public OptsBean getOpts() {
        return opts;
    }

    public void setOpts(OptsBean opts) {
        this.opts = opts;
    }
    
    

    public String[] getAliases() {
		return aliases;
	}

	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}



	public static class OptsBean {
        /**
         * time_to_live : number
         * qos : number
         * apn_json : {"aps":{"alert":"string","badge":"number","sound":"string","content-available":"number"}}
         * third_party_push : {"notification_title":"你好","notification_content":"云巴推送"}
         */

        private long time_to_live;
        private int qos;
        private ApnJsonBean apn_json;
        private ThirdPartyPushBean third_party_push;

        public long getTime_to_live() {
            return time_to_live;
        }

        public void setTime_to_live(long time_to_live) {
            this.time_to_live = time_to_live;
        }

        public int getQos() {
            return qos;
        }

        public void setQos(int qos) {
            this.qos = qos;
        }

        public ApnJsonBean getApn_json() {
            return apn_json;
        }

        public void setApn_json(ApnJsonBean apn_json) {
            this.apn_json = apn_json;
        }

        public ThirdPartyPushBean getThird_party_push() {
            return third_party_push;
        }

        public void setThird_party_push(ThirdPartyPushBean third_party_push) {
            this.third_party_push = third_party_push;
        }

        public static class ApnJsonBean {
            /**
             * aps : {"alert":"string","badge":"number","sound":"string","content-available":"number"}
             */

            private ApsBean aps;

            public ApsBean getAps() {
                return aps;
            }

            public void setAps(ApsBean aps) {
                this.aps = aps;
            }

            public static class ApsBean {
                /**
                 * alert : string
                 * badge : number
                 * sound : string
                 * content-available : number
                 */

                private String alert;
                private int badge;
                private String sound;
                @SerializedName("content-available")
                private String contentavailable;

                public String getAlert() {
                    return alert;
                }

                public void setAlert(String alert) {
                    this.alert = alert;
                }

                public int getBadge() {
                    return badge;
                }

                public void setBadge(int badge) {
                    this.badge = badge;
                }

                public String getSound() {
                    return sound;
                }

                public void setSound(String sound) {
                    this.sound = sound;
                }

                public String getContentavailable() {
                    return contentavailable;
                }

                public void setContentavailable(String contentavailable) {
                    this.contentavailable = contentavailable;
                }
            }
        }

        public static class ThirdPartyPushBean {
            /**
             * notification_title : 你好
             * notification_content : 云巴推送
             */

            private String notification_title;
            private String notification_content;

            public String getNotification_title() {
                return notification_title;
            }

            public void setNotification_title(String notification_title) {
                this.notification_title = notification_title;
            }

            public String getNotification_content() {
                return notification_content;
            }

            public void setNotification_content(String notification_content) {
                this.notification_content = notification_content;
            }
        }
    }
}
