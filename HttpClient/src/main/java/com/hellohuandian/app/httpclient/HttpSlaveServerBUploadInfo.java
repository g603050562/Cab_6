package com.hellohuandian.app.httpclient;

import com.hellohuandian.pubfunction.Unit.Md5;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 离线换电 上传换电信息
 */
public class HttpSlaveServerBUploadInfo extends Thread {

    private String number, extime, in_electric, in_door, in_battery, out_battery, out_door, out_electric;
    private int dbm;
    private IFHttpSlaveServerBUploadInfoListener ifHttpSlaveServerBUploadInfoListener;

    /**
     * @param number                               电柜ID（例：04531）
     * @param extime                               换电时间
     * @param in_battery                           插入电池的 电池ID
     * @param in_door                              插入电池的 电池舱门
     * @param in_electric                          插入电池的 电池电量
     * @param out_battery                          弹出电池的 电池ID
     * @param out_door                             弹出电池的 电池舱门
     * @param out_electric                         弹出电池的 电池电量
     * @param dbm                                  当前电柜信号强度
     * @param ifHttpSlaveServerBUploadInfoListener 接口
     */
    public HttpSlaveServerBUploadInfo(String number, String extime, String in_battery, String in_door, String in_electric, String out_battery, String out_door, String out_electric, int dbm, IFHttpSlaveServerBUploadInfoListener ifHttpSlaveServerBUploadInfoListener) {
        this.number = number;
        this.extime = extime;
        this.in_electric = in_electric;
        this.in_door = in_door;
        this.in_battery = in_battery;
        this.out_battery = out_battery;
        this.out_door = out_door;
        this.out_electric = out_electric;
        this.dbm = dbm;
        this.ifHttpSlaveServerBUploadInfoListener = ifHttpSlaveServerBUploadInfoListener;
    }

    @Override
    public void run() {
        super.run();

        if (dbm > 50 || dbm < -125) {
            System.out.println("网络：  网络：JSON：HttpSlaveServerBUploadInfo   DBM超时");
            returnMessgae("-1", "网络：JSON：HttpSlaveServerBUploadInfo   DBM超时", "", extime);
            return;
        }

//        String path = "https://offapc.vshanji.com/Receive/offdata.html";
        String path = "https://offapc.halouhuandian.com/Receive/offdata.html";
        HttpPost httpPost = new HttpPost(path);
        httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);

        Header header1 = new Header() {
            @Override
            public String getName() {
                return "aptk";
            }

            @Override
            public String getValue() {
                return new Md5().getDateToken();
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        };
        httpPost.addHeader(header1);

        String dataMillis = System.currentTimeMillis() + "";
        String dataString = dataMillis.substring(0, dataMillis.length() - 3);

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("number", number));
        list.add(new BasicNameValuePair("extime", extime));
        list.add(new BasicNameValuePair("in_battery", in_battery));
        list.add(new BasicNameValuePair("in_door", in_door));
        list.add(new BasicNameValuePair("in_electric", in_electric));
        list.add(new BasicNameValuePair("out_battery", out_battery));
        list.add(new BasicNameValuePair("out_door", out_door));
        list.add(new BasicNameValuePair("out_electric", out_electric));
        list.add(new BasicNameValuePair("extoken", Md5.stringToMD5(dataString + "#offapc.halouhuandian.com#" + dataString) + "-" + dataString));

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = null;
                try {

                    jsonObject = new JSONObject(result);
                    System.out.println("网络：   " + jsonObject);

                    String code = jsonObject.getString("status");
                    String messageString = jsonObject.getString("msg");
                    String data = "";
                    if (code.equals("0")) {
                        data = "";
                    } else {
                        data = "";
//                            data = jsonObject.getString("data");
                    }
                    returnMessgae(code, messageString, data, extime);

                } catch (JSONException e) {
                    System.out.println("网络：   JSON：HttpSlaveServerBUploadInfo" + e.toString());
                    returnMessgae("-1", "JSON：HttpSlaveServerBUploadInfo" + e.toString(), "",   extime);
                }

            } else {
                System.out.println("网络：   JSON：HttpSlaveServerBUploadInfo" + httpResponse.getStatusLine().getStatusCode());
                returnMessgae("-1", "JSON：HttpSlaveServerBUploadInfo" + httpResponse.getStatusLine().getStatusCode(), "",   extime);
            }
        } catch (Exception e) {
            System.out.println("网络：   JSON：HttpSlaveServerBUploadInfo" + e.toString());
            returnMessgae("-1", "JSON：HttpSlaveServerBUploadInfo" + e.toString(), "",   extime);
        }
    }

    private void returnMessgae(String code, String str, String data, String extime) {
        ifHttpSlaveServerBUploadInfoListener.onHttpSlaveServerBUploadInfoResult(code, str, data, extime, in_door, out_door);
    }

}
