package com.hellohuandian.longlink.bean;


public class UserBean {
    String type;
    String phone;
    String vcode;

    public UserBean(String type, String phone, String vcode) {
        this.type = type;
        this.phone = phone;
        this.vcode = vcode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    @Override
    public String toString() {
        return "UserBean{" + "type='" + type + '\'' + ", phone='" + phone + '\'' + ", vcode='" + vcode + '\'' + '}';
    }
}
