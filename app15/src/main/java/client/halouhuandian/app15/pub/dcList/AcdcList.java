package client.halouhuandian.app15.pub.dcList;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.ArrayList;

/**
 * Author:      Lee Yeung
 * Create Date: 2020/10/26
 * Description:
 */
public class AcdcList{

    /**
     * status : 1
     * msg : 请求成功！
     * data : [{"id":"602","bata":"1","project":"31","type":"","name":"ACDC升级程序","version":"2","bhv":"0","vdesc":"ACDC升级程序1","default":"0","sorts":"100","volt":"0","url":"https://img01.halouhuandian.com/hello/adm/2020/20200826104319_55316.bin","md5str":"001cd16624d47b10486e28612412f9c8","fname":"TC096K3000M1S_SV002HV001_20200729.bin","is_kf_show":"1","is_dw_show":"1","mjson":"[\"\"]","citys":"","cabids":"","open":"-1","create_uid":"207","create_time":"2020-08-26 10:43:20","update_time":"2020-10-26 10:19:10","update_uid":"1","status":"1","is_del":"0"},{"id":"516","bata":"1","project":"31","type":"","name":"TC096K3000M1S_SV205HV001_20200715","version":"205","bhv":"0","vdesc":"TC096K3000M1S_SV205HV001_20200715","default":"0","sorts":"100","volt":"0","url":"https://img01.halouhuandian.com/hello/adm/2020/20200715190025_83244.bin","md5str":"","fname":"TC096K3000M1S_SV205HV001_20200715.bin","is_kf_show":"1","is_dw_show":"1","mjson":"[\"\"]","citys":"","cabids":"","open":"1","create_uid":"207","create_time":"2020-07-15 19:00:27","update_time":"2020-09-24 16:58:25","update_uid":"0","status":"1","is_del":"0"}]
     */

    private int status;
    private String msg;
    private ArrayList<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<DataBean> data) {
        this.data = data;
    }

    public static class DataBean  extends DcList {
        /**
         * id : 602
         * bata : 1
         * project : 31
         * type :
         * name : ACDC升级程序
         * version : 2
         * bhv : 0
         * vdesc : ACDC升级程序1
         * default : 0
         * sorts : 100
         * volt : 0
         * url : https://img01.halouhuandian.com/hello/adm/2020/20200826104319_55316.bin
         * md5str : 001cd16624d47b10486e28612412f9c8
         * fname : TC096K3000M1S_SV002HV001_20200729.bin
         * is_kf_show : 1
         * is_dw_show : 1
         * mjson : [""]
         * citys :
         * cabids :
         * open : -1
         * create_uid : 207
         * create_time : 2020-08-26 10:43:20
         * update_time : 2020-10-26 10:19:10
         * update_uid : 1
         * status : 1
         * is_del : 0
         */

        private String id;
        private String bata;
        private String project;
        private String type;
        private String name;
        private String version;
        private String bhv;
        private String vdesc;
        @SerializedName("default")
        private String defaultX;
        private String sorts;
        private String volt;
        private String url;
        private String md5str;
        private String fname;
        private String is_kf_show;
        private String is_dw_show;
        private String mjson;
        private String citys;
        private String cabids;
        private String open;
        private String create_uid;
        private String create_time;
        private String update_time;
        private String update_uid;
        private String status;
        private String is_del;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBata() {
            return bata;
        }

        public void setBata(String bata) {
            this.bata = bata;
        }

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getBhv() {
            return bhv;
        }

        public void setBhv(String bhv) {
            this.bhv = bhv;
        }

        public String getVdesc() {
            return vdesc;
        }

        public void setVdesc(String vdesc) {
            this.vdesc = vdesc;
        }

        public String getDefaultX() {
            return defaultX;
        }

        public void setDefaultX(String defaultX) {
            this.defaultX = defaultX;
        }

        public String getSorts() {
            return sorts;
        }

        public void setSorts(String sorts) {
            this.sorts = sorts;
        }

        public String getVolt() {
            return volt;
        }

        public void setVolt(String volt) {
            this.volt = volt;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMd5str() {
            return md5str;
        }

        public void setMd5str(String md5str) {
            this.md5str = md5str;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getIs_kf_show() {
            return is_kf_show;
        }

        public void setIs_kf_show(String is_kf_show) {
            this.is_kf_show = is_kf_show;
        }

        public String getIs_dw_show() {
            return is_dw_show;
        }

        public void setIs_dw_show(String is_dw_show) {
            this.is_dw_show = is_dw_show;
        }

        public String getMjson() {
            return mjson;
        }

        public void setMjson(String mjson) {
            this.mjson = mjson;
        }

        public String getCitys() {
            return citys;
        }

        public void setCitys(String citys) {
            this.citys = citys;
        }

        public String getCabids() {
            return cabids;
        }

        public void setCabids(String cabids) {
            this.cabids = cabids;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }

        public String getCreate_uid() {
            return create_uid;
        }

        public void setCreate_uid(String create_uid) {
            this.create_uid = create_uid;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public String getUpdate_uid() {
            return update_uid;
        }

        public void setUpdate_uid(String update_uid) {
            this.update_uid = update_uid;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIs_del() {
            return is_del;
        }

        public void setIs_del(String is_del) {
            this.is_del = is_del;
        }
    }
}
