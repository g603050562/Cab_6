package client.halouhuandian.app15.model.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import client.halouhuandian.app15.R;
import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByBaseFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByStateFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.dcdc.DcdcInfoByWarningFormat;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.service.logic.logicOpenDoor.LogicOpenDoor;

/**
 * A_Admin的适配器
 */
public class AdminDcdcTopAdapter_12 extends BaseAdapter {

    public interface AdminTopAdapterListener{
        void openDoor(int door);
    }

    private Context context;
    private DcdcInfoByBaseFormat[] dcdcBaseBeans;
    private DcdcInfoByStateFormat[] dcdcStateBeans;
    private DcdcInfoByWarningFormat[] dcdcWarningBeans;
    private AdminTopAdapterListener adminTopAdapterListener;
    private int resources;


    public AdminDcdcTopAdapter_12(Context context , DcdcInfoByBaseFormat[] dcdcBaseBeans, DcdcInfoByStateFormat[] dcdcStateBeans, DcdcInfoByWarningFormat[] dcdcWarningBeans , AdminTopAdapterListener adminTopAdapterListener) {

        this.context = context;
        this.dcdcBaseBeans = new DcdcInfoByBaseFormat[SystemConfig.getMaxBattery()];
        this.dcdcStateBeans = new DcdcInfoByStateFormat[SystemConfig.getMaxBattery()];
        this.dcdcWarningBeans = new DcdcInfoByWarningFormat[SystemConfig.getMaxBattery()];

        setData(dcdcBaseBeans , dcdcStateBeans , dcdcWarningBeans);

        this.adminTopAdapterListener = adminTopAdapterListener;
        resources = R.layout.activity_admin_1080p_dcdc_grid_item_1_12;
    }

    public void setData(DcdcInfoByBaseFormat[] dcdcBaseBeans, DcdcInfoByStateFormat[] dcdcStateBeans, DcdcInfoByWarningFormat[] dcdcWarningBeans){
        for(int i = 0 ; i < SystemConfig.getMaxBattery() ; i++){
            this.dcdcBaseBeans[i] = dcdcBaseBeans[i];
            this.dcdcStateBeans[i] = dcdcStateBeans[i];
            this.dcdcWarningBeans[i] = dcdcWarningBeans[i];
        }
    }

    @Override
    public int getCount() {
        return dcdcBaseBeans.length;
    }

    @Override
    public Object getItem(int i) {
        return dcdcBaseBeans[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = View.inflate(context, resources, null);
            viewHolder = new ViewHolder();
            viewHolder.t_1 = (TextView) convertView.findViewById(R.id.t_1);
            viewHolder.t_2 = (TextView) convertView.findViewById(R.id.t_2);
            viewHolder.t_3 = (TextView) convertView.findViewById(R.id.t_3);
            viewHolder.t_4 = (TextView) convertView.findViewById(R.id.t_4);
            viewHolder.t_5 = (TextView) convertView.findViewById(R.id.t_5);
            viewHolder.t_6 = (TextView) convertView.findViewById(R.id.t_6);
            viewHolder.t_7 = (TextView) convertView.findViewById(R.id.t_7);
            viewHolder.t_8 = (TextView) convertView.findViewById(R.id.t_8);
            viewHolder.t_9 = (TextView) convertView.findViewById(R.id.t_9);
            viewHolder.t_10 = (TextView) convertView.findViewById(R.id.t_10);
            viewHolder.t_11 = (TextView) convertView.findViewById(R.id.t_11);
            viewHolder.t_12 = (TextView) convertView.findViewById(R.id.t_12);
            viewHolder.t_13 = (TextView) convertView.findViewById(R.id.t_13);
            viewHolder.t_14 = (TextView) convertView.findViewById(R.id.t_14);
            viewHolder.t_15 = (TextView) convertView.findViewById(R.id.t_15);
            viewHolder.t_16 = (TextView) convertView.findViewById(R.id.t_16);
            viewHolder.t_17 = (TextView) convertView.findViewById(R.id.t_17);
            viewHolder.t_18 = (TextView) convertView.findViewById(R.id.t_18);
            viewHolder.t_19 = (TextView) convertView.findViewById(R.id.t_19);
            viewHolder.t_20 = (TextView) convertView.findViewById(R.id.t_20);
            viewHolder.t_21 = (TextView) convertView.findViewById(R.id.t_21);
            viewHolder.t_22 = (TextView) convertView.findViewById(R.id.t_22);
            viewHolder.t_23 = (TextView) convertView.findViewById(R.id.t_23);
            viewHolder.t_24 = (TextView) convertView.findViewById(R.id.t_24);
            viewHolder.t_25 = (TextView) convertView.findViewById(R.id.t_25);
            viewHolder.t_26 = (TextView) convertView.findViewById(R.id.t_26);
            viewHolder.t_27 = (TextView) convertView.findViewById(R.id.t_27);
            viewHolder.t_28 = (TextView) convertView.findViewById(R.id.t_28);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //舱门号
        if (i + 1 < 10) {
            viewHolder.t_1.setText("0" + (i + 1));
        } else {
            viewHolder.t_1.setText((i + 1) + "");
        }
        //BID
        viewHolder.t_2.setText(dcdcBaseBeans[i].getBID());
        //UID
        viewHolder.t_3.setText(dcdcBaseBeans[i].getUID());

        //状态
        viewHolder.t_4.setText(dcdcStateBeans[i].getDcdcState());

        //电压
        viewHolder.t_5.setText(dcdcBaseBeans[i].getBatteryVoltage() +"V");
        //电量
        viewHolder.t_8.setText(dcdcBaseBeans[i].getBatteryRelativeSurplus() + "%");
        //电流
        viewHolder.t_11.setText(dcdcBaseBeans[i].getBatteryElectric() + "A");
        //芯温度
        viewHolder.t_14.setText(dcdcBaseBeans[i].getTemperatureSensorByInner()+"");

        //微动_1
        String innerStr = "";
        if(dcdcBaseBeans[i].getInchingByInner() == 0){
            innerStr = "未触发";
            viewHolder.t_6.setTextColor(0xfff06b00);
        }else if(dcdcBaseBeans[i].getInchingByInner() == 1){
            innerStr = "触发";
            viewHolder.t_6.setTextColor(0xff008000);
        }else if(dcdcBaseBeans[i].getInchingByInner() == -1){
            innerStr = "未检测";
            viewHolder.t_6.setTextColor(0xffcccccc);
        }
        viewHolder.t_6.setText(innerStr);
        //微动_3
        String outerCloseStr = "";
        if(dcdcBaseBeans[i].getInchingByOuterClose() == 0){
            outerCloseStr = "未触发";
            viewHolder.t_9.setTextColor(0xfff06b00);
        }else if(dcdcBaseBeans[i].getInchingByOuterClose() == 1){
            outerCloseStr = "触发";
            viewHolder.t_9.setTextColor(0xff008000);
        }else if(dcdcBaseBeans[i].getInchingByOuterClose() == -1){
            outerCloseStr = "未检测";
            viewHolder.t_9.setTextColor(0xffcccccc);
        }
        viewHolder.t_9.setText(outerCloseStr);
        //微动_2
        String outerOpenStr = "";
        if(dcdcBaseBeans[i].getInchingByOuterOpen() == 0){
            outerOpenStr = "未触发";
            viewHolder.t_12.setTextColor(0xfff06b00);
        }else if(dcdcBaseBeans[i].getInchingByOuterOpen() == 1){
            outerOpenStr = "触发";
            viewHolder.t_12.setTextColor(0xff008000);
        }else if(dcdcBaseBeans[i].getInchingByOuterOpen() == -1){
            outerOpenStr = "未检测";
            viewHolder.t_12.setTextColor(0xffcccccc);
        }
        viewHolder.t_12.setText(outerOpenStr);
        //壳温度
        viewHolder.t_15.setText(dcdcBaseBeans[i].getTemperatureSensorByOuter()+"/"+dcdcBaseBeans[i].getTemperatureSensorByOuter2());

        //DCDC内部预警
        viewHolder.t_7.setText(dcdcWarningBeans[i].getErrorStateInSide()+"");
        //DCDC外部预警
        viewHolder.t_10.setText(dcdcWarningBeans[i].getErrorStateOutSide()+"");
        //BMS预警
        viewHolder.t_13.setText(dcdcWarningBeans[i].getErrorStateBMS()+"");
        //循环次数
        viewHolder.t_16.setText(dcdcBaseBeans[i].getLoops()+"");

        //模块儿电压
        viewHolder.t_17.setText(dcdcStateBeans[i].getDcdcVoltage() + "V");
        //模块儿电流
        viewHolder.t_20.setText(dcdcStateBeans[i].getDcdcElectric() + "A");

        //单体最低
        viewHolder.t_18.setText(dcdcBaseBeans[i].getItemMin() +"mV");
        //单体最高
        viewHolder.t_21.setText(dcdcBaseBeans[i].getItemMax() + "mV");

        //需求功率
        viewHolder.t_19.setText(dcdcBaseBeans[i].getRequirePower() + "W");
        //采样电压
        viewHolder.t_22.setText(dcdcBaseBeans[i].getSamplingVoltage() + "V");

        //实际soc
        viewHolder.t_23.setText(dcdcBaseBeans[i].getBatteryRealRelativeSurplus() + "%");
        //终止原因
        viewHolder.t_24.setText(dcdcStateBeans[i].getDcdcStopInfo()+"");

        //dcdc版本
        viewHolder.t_25.setText("HV - "+dcdcStateBeans[i].getDcdcHardWareVersion() + "   SV - "+dcdcStateBeans[i].getDcdcSoftwareVersion());
        //电池版本
        String batteryVersion = dcdcBaseBeans[i].getBatteryVersion();
        String batteryHV = batteryVersion.substring(0,2);
        String batterySV = batteryVersion.substring(2,4);
        viewHolder.t_26.setText("HV - " + Integer.parseInt(batteryHV,16) + "   SV - "+Integer.parseInt(batterySV,16));

        //开关舱门
        viewHolder.t_27.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogicOpenDoor.getInstance().putterPush(i+1 , CabInfoSp.getInstance().getPutterActivityTime() , "电柜后台点击开舱门");
            }
        });
        //开关舱门
        viewHolder.t_28.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogicOpenDoor.getInstance().putterPull(i+1 , CabInfoSp.getInstance().getPutterActivityTime() , "电柜后台点击开舱门");
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView t_1;
        TextView t_2;
        TextView t_3;
        TextView t_4;
        TextView t_5;
        TextView t_6;
        TextView t_7;
        TextView t_8;
        TextView t_9;
        TextView t_10;
        TextView t_11;
        TextView t_12;
        TextView t_13;
        TextView t_14;
        TextView t_15;
        TextView t_16;
        TextView t_17;
        TextView t_18;
        TextView t_19;
        TextView t_20;
        TextView t_21;
        TextView t_22;
        TextView t_23;
        TextView t_24;
        TextView t_25;
        TextView t_26;
        TextView t_27;
        TextView t_28;
    }
}
