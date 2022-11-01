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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import client.halouhuandian.app15.pub.util.UtilMd5;
import client.halouhuandian.app15.service.logic.logicHttpConnection.http.HttpUrlMap;

/**
 * 离线换电
 * 检测插入的电池有UID 后台判断这块儿电池用户的 使用情况
 */
public class HttpOutLineCheckUserBalance extends Thread {

    private String number, uid32, in_battery, in_electric, in_door;
    private int dbm = 0;
    private IFHttpOutLineCheckUserBalanceListener ifHttpOutLineCheckUserBalanceListener;

    /**
     * @param number                                电柜ID（例：04531）
     * @param uid32                                 插入电池的UID32
     * @param in_battery                            插入的电池ID
     * @param in_electric                           插入的电池电量
     * @param in_door                               插入的电池舱门号
     * @param dbm                                   柜体的DBM信号值 信号差的直接返回-1 不用等超时了
     * @param ifHttpOutLineCheckUserBalanceListener 接口
     */
    public HttpOutLineCheckUserBalance(String number, String uid32, String in_battery, String in_electric, String in_door, int dbm, IFHttpOutLineCheckUserBalanceListener ifHttpOutLineCheckUserBalanceListener) {
        this.number = number;
        this.uid32 = uid32;
        this.in_battery = in_battery;
        this.in_electric = in_electric;
        this.in_door = in_door;
        this.dbm = dbm;
        this.ifHttpOutLineCheckUserBalanceListener = ifHttpOutLineCheckUserBalanceListener;
    }

    @Override
    public void run() {
        super.run();
        if (dbm > 50 || dbm < -125) {
            returnMessgae("-1", "网络：JSON：HttpOutLineCheckUserBalance   DBM超时", "");
            return;
        }

        String path = HttpUrlMap.GetUserInfo;
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
        list.add(new BasicNameValuePair("number", number));
        list.add(new BasicNameValuePair("uid32", uid32));
        list.add(new BasicNameValuePair("in_battery", in_battery));
        list.add(new BasicNameValuePair("in_electric", in_electric));
        list.add(new BasicNameValuePair("in_door", in_door));

        try {
            HttpEntity entity = new UrlEncodedFormEntity(list, "utf-8");
            httpPost.setEntity(entity);
            HttpClient client = new DefaultHttpClient();
            HttpResponse httpResponse = client.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                JSONObject jsonObject = new JSONObject(result);
                String messageString = jsonObject.getString("msg");
                String status = jsonObject.getString("status");
                returnMessgae(status, messageString, jsonObject + "");
                System.out.println("okHttp - Success - data - " + jsonObject.toString());
            } else {
                returnMessgae("-1", "网络：JSON：HttpOutLineCheckUserBalance" + httpResponse.getStatusLine().getStatusCode(), "");
                System.out.println("okHttp - Error - JSON：HttpOutLineCheckUserBalance" + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            returnMessgae("-1", "网络：JSON：HttpOutLineCheckUserBalance" + e.toString(), "");
            System.out.println("okHttp - Error - JSON：HttpOutLineCheckUserBalance" + e.toString());
        }
    }

    private void returnMessgae(String code, String str, String data) {
        ifHttpOutLineCheckUserBalanceListener.onHttpOutLineCheckUserBalanceResult(uid32, in_door, code, str, data);
    }

}
