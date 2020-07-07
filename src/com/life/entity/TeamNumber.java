package com.life.entity;

import java.util.List;

public class TeamNumber {
    /**
     * tinfos : [{"icon":null,"announcement":null,"updatetime":1562747326680,"muteType":0,"uptinfomode":1,"maxusers":200,"intro":null,"size":3,"createtime":1562652476862,"upcustommode":1,"owner":"18398850872","tname":"03980011-杭州","beinvitemode":1,"joinmode":0,"tid":2610373958,"members":["18398850871","18639805480"],"invitemode":1,"mute":false}]
     * code : 200
     */

    private int code;
    private List<TinfosBean> tinfos;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<TinfosBean> getTinfos() {
        return tinfos;
    }

    public void setTinfos(List<TinfosBean> tinfos) {
        this.tinfos = tinfos;
    }

    public static class TinfosBean {
        /**
         * icon : null
         * announcement : null
         * updatetime : 1562747326680
         * muteType : 0
         * uptinfomode : 1
         * maxusers : 200
         * intro : null
         * size : 3
         * createtime : 1562652476862
         * upcustommode : 1
         * owner : 18398850872
         * tname : 03980011-杭州
         * beinvitemode : 1
         * joinmode : 0
         * tid : 2610373958
         * members : ["18398850871","18639805480"]
         * invitemode : 1
         * mute : false
         */

        private Object icon;
        private Object announcement;
        private long updatetime;
        private int muteType;
        private int uptinfomode;
        private int maxusers;
        private Object intro;
        private int size;
        private long createtime;
        private int upcustommode;
        private String owner;
        private String tname;
        private int beinvitemode;
        private int joinmode;
        private long tid;
        private int invitemode;
        private boolean mute;
        private List<String> members;

        public Object getIcon() {
            return icon;
        }

        public void setIcon(Object icon) {
            this.icon = icon;
        }

        public Object getAnnouncement() {
            return announcement;
        }

        public void setAnnouncement(Object announcement) {
            this.announcement = announcement;
        }

        public long getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(long updatetime) {
            this.updatetime = updatetime;
        }

        public int getMuteType() {
            return muteType;
        }

        public void setMuteType(int muteType) {
            this.muteType = muteType;
        }

        public int getUptinfomode() {
            return uptinfomode;
        }

        public void setUptinfomode(int uptinfomode) {
            this.uptinfomode = uptinfomode;
        }

        public int getMaxusers() {
            return maxusers;
        }

        public void setMaxusers(int maxusers) {
            this.maxusers = maxusers;
        }

        public Object getIntro() {
            return intro;
        }

        public void setIntro(Object intro) {
            this.intro = intro;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getCreatetime() {
            return createtime;
        }

        public void setCreatetime(long createtime) {
            this.createtime = createtime;
        }

        public int getUpcustommode() {
            return upcustommode;
        }

        public void setUpcustommode(int upcustommode) {
            this.upcustommode = upcustommode;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getTname() {
            return tname;
        }

        public void setTname(String tname) {
            this.tname = tname;
        }

        public int getBeinvitemode() {
            return beinvitemode;
        }

        public void setBeinvitemode(int beinvitemode) {
            this.beinvitemode = beinvitemode;
        }

        public int getJoinmode() {
            return joinmode;
        }

        public void setJoinmode(int joinmode) {
            this.joinmode = joinmode;
        }

        public long getTid() {
            return tid;
        }

        public void setTid(long tid) {
            this.tid = tid;
        }

        public int getInvitemode() {
            return invitemode;
        }

        public void setInvitemode(int invitemode) {
            this.invitemode = invitemode;
        }

        public boolean isMute() {
            return mute;
        }

        public void setMute(boolean mute) {
            this.mute = mute;
        }

        public List<String> getMembers() {
            return members;
        }

        public void setMembers(List<String> members) {
            this.members = members;
        }
    }
}
