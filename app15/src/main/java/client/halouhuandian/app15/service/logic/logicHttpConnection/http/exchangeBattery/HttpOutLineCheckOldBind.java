package client.halouhuandian.app15.service.logic.logicHttpConnection.http.exchangeBattery;

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

import java.util.ArrayList;
import java.util.List;

import client.halouhuandian.app15.pub.util.UtilMd5;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.HttpUrlMap;

/**
 * 离线换电
 * 检测插入的电池 从没有写入UID32 到 离线换电绑定电池的 过度接口
 */
public class HttpOutLineCheckOldBind extends Thread {

    private String in_door;
    private String battery;
    private String number;
    private int dbm = 0;
    private IFHttpOutLineCheckOldBindListener ifHttpOutLineCheckOldBindListener;


    /**
     * @param in_door                           插入的舱门电池
     * @param battery                           插入的电池ID
     * @param dbm                               柜体的DBM信号值 信号差的直接返回-1 不用等超时了
     * @param ifHttpOutLineCheckOldBindListener 接口
     */
    public HttpOutLineCheckOldBind(String number, String in_door, String battery, int dbm, IFHttpOutLineCheckOldBindListener ifHttpOutLineCheckOldBindListener) {
        this.number = number;
        this.in_door = in_door;
        this.battery = battery;
        this.dbm = dbm;
        this.ifHttpOutLineCheckOldBindListener = ifHttpOutLineCheckOldBindListener;
    }

    @Override
    public void run() {
        super.run();

        System.out.println("okhttp - HttpOutLineCheckOldBind   电池 - " + battery);

        if (dbm > 50 || dbm < -125) {
            System.out.println("okhttp - JSON：HttpOutLineCheckOldBind   DBM超时");
            returnMessgae("-1", "网络：JSON：HttpOutLineCheckOldBind   DBM超时", "");
            return;
        }

        String path = HttpUrlMap.GetUID;
        HttpPost httpPost = new HttpPost(path);
        httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
        httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);

        Header header1 = new Header() {
            @Override
            public String getName() {
                return "aptk";
            }

            @Override
            public String getValue() {
                return new UtilMd5().getDateToken();
            }

            @Override
            public HeaderElement[] getElements() throws ParseException {
                return new HeaderElement[0];
            }
        };
        httpPost.addHeader(header1);

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("battery", battery));
        list.add(new BasicNameValuePair("number", number));
        list.add(new BasicNameValuePair("door", in_door));

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
                    System.out.println("okhttp - success - HttpOutLineCheckOldBind - " + jsonObject.toString());
                    String messageString = jsonObject.getString("msg");
                    String status = jsonObject.getString("status");
                    returnMessgae(status, messageString, jsonObject + "");
                } catch (JSONException e) {
                    returnMessgae("-1", "网络：JSON：HttpOutLineCheckOldBind" + e.toString(), "");
                    System.out.println("okhttp - error - HttpOutLineCheckOldBind - " + e.toString());
                }

            } else {
                returnMessgae("-1", "网络：JSON：HttpOutLineCheckOldBind" + httpResponse.getStatusLine().getStatusCode(), "");
                System.out.println("okhttp - error - HttpOutLineCheckOldBind - " + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            returnMessgae("-1", "网络：JSON：HttpOutLineCheckOldBind" + e.toString(), "");
            System.out.println("okhttp - error - HttpOutLineCheckOldBind - " + e.toString());
        }
    }

    private void returnMessgae(String code, String str, String data) {
        ifHttpOutLineCheckOldBindListener.onHttpOutLineCheckOldBindResult(in_door, code, str, data);
    }

}
