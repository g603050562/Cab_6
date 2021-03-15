package client.halouhuandian.app15.bean;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/9/15
 * Description:
 */
public class ExchangeStep1ResultModel {

    /**
     * number : 460041060118952
     * phone : 5692
     * in_battery : MKKKKGH11AMM0032
     * in_door : 8
     * in_electric : 95
     * out_battery : MDFFFHG21AMM0931
     * out_door : 3
     * out_electric : 100
     * hello : 3
     * uid : 304678
     * trade_id : 41113496
     * cost : 本次换电扣币：3个
     * remark : 交易成功,请开3号仓门.
     */

    private String number;
    private String phone;
    private String in_battery;
    private int in_door;
    private int in_electric;
    private String out_battery;
    private String out_door;
    private String out_electric;
    private int hello;
    private String uid;
    private String trade_id;
    private String cost;
    private String remark;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIn_battery() {
        return in_battery;
    }

    public void setIn_battery(String in_battery) {
        this.in_battery = in_battery;
    }

    public int getIn_door() {
        return in_door;
    }

    public void setIn_door(int in_door) {
        this.in_door = in_door;
    }

    public int getIn_electric() {
        return in_electric;
    }

    public void setIn_electric(int in_electric) {
        this.in_electric = in_electric;
    }

    public String getOut_battery() {
        return out_battery;
    }

    public void setOut_battery(String out_battery) {
        this.out_battery = out_battery;
    }

    public String getOut_door() {
        return out_door;
    }

    public void setOut_door(String out_door) {
        this.out_door = out_door;
    }

    public String getOut_electric() {
        return out_electric;
    }

    public void setOut_electric(String out_electric) {
        this.out_electric = out_electric;
    }

    public int getHello() {
        return hello;
    }

    public void setHello(int hello) {
        this.hello = hello;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
