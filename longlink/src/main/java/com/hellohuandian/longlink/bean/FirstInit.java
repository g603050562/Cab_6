package com.hellohuandian.longlink.bean;



public class FirstInit {

    String uid ;
    String client_id;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public FirstInit(String uid, String client_id ) {
        this.uid = uid;
        this.client_id = client_id;
    }
}
