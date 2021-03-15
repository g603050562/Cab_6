package com.hellohuandian.longlink.bean;


public class UserToken {
    String type;
    String client_id;

    public UserToken(String type, String client_id) {
        this.type = type;
        this.client_id = client_id;
    }

    @Override
    public String toString() {
        return "UserToken{" + "type='" + type + '\'' + ", client_id='" + client_id + '\'' + '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }
}
