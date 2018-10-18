package com.life.entity;

import java.util.List;

public class RongUserJson {

    /**
     * code : 200
     * users : [{"id":"18668176527"},{"id":"18767103122"},{"id":"13073682115"},{"id":"15757194975"},{"id":"18398850872"},{"id":"18917995795"},{"id":"18257194696"},{"id":"13732278960"},{"id":"18329102298"},{"id":"15336568476"},{"id":"13486160973"},{"id":"13735532673"},{"id":"15355481205"},{"id":"18639805480"}]
     */

    private int code;
    private List<UsersBean> users;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<UsersBean> getUsers() {
        return users;
    }

    public void setUsers(List<UsersBean> users) {
        this.users = users;
    }

    public static class UsersBean {
        /**
         * id : 18668176527
         */

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
