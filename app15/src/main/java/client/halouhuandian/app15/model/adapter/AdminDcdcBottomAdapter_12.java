package client.halouhuandian.app15.model.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import client.halouhuandian.app15.R;
import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.acdc.AcdcInfoByStateFormat;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.acdc.AcdcInfoByWarningFormat;
import client.halouhuandian.app15.hardWareConncetion.environmentPlate.EnvironmentDataFormat;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;

/**
 * A_Admin的适配器
 */
public class AdminDcdcBottomAdapter_12 extends BaseAdapter {

    private Context context;
    private AcdcInfoByStateFormat[] acdcStateBeans;
    private AcdcInfoByWarningFormat[] acdcWarningBeans;
    private int resources_1;
    private int resources_2;

    private EnvironmentDataFormat environmentDataFormat;

    public AdminDcdcBottomAdapter_12(Context context , AcdcInfoByStateFormat[] acdcStateBeans, AcdcInfoByWarningFormat[] acdcWarningBeans , EnvironmentDataFormat environmentDataFormat) {

        this.context = context;
        this.acdcStateBeans = new AcdcInfoByStateFormat[SystemConfig.getMaxAcdc()];
        this.acdcWarningBeans = new AcdcInfoByWarningFormat[SystemConfig.getMaxAcdc()];

        setData(acdcStateBeans,acdcWarningBeans);
        this.environmentDataFormat = environmentDataFormat;

        resources_1 = R.layout.activity_admin_1080p_dcdc_grid_item_2_12;
        resources_2 = R.layout.activity_admin_1080p_dcdc_grid_item_3_12;
    }

    public void setData(AcdcInfoByStateFormat[] acdcStateBeans, AcdcInfoByWarningFormat[] acdcWarningBeans){
        for(int i = 0 ; i < SystemConfig.getMaxAcdc() ; i++){
            this.acdcStateBeans[i] = acdcStateBeans[i];
            this.acdcWarningBeans[i] = acdcWarningBeans[i];
        }
    }

    @Override
    public int getCount() {
        return acdcStateBeans.length + 1;
    }

    @Override
    public Object getItem(int i) {
        return acdcStateBeans[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setEnvironmentDataFormat(EnvironmentDataFormat environmentDataFormat) {
        this.environmentDataFormat = environmentDataFormat;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if (null == convertView) {
            if(i == 1 || i == 0 || i == 2){
                convertView = View.inflate(context, resources_1, null);
            }else if(i == 3){
                convertView = View.inflate(context, resources_2, null);
            }

            viewHolder = new ViewHolder();

            viewHolder.t_1  = convertView.findViewById(R.id.t_1);
            viewHolder.t_2  = convertView.findViewById(R.id.t_2);
            viewHolder.t_3  = convertView.findViewById(R.id.t_3);
            viewHolder.t_4  = convertView.findViewById(R.id.t_4);
            viewHolder.t_5  = convertView.findViewById(R.id.t_5);
            viewHolder.t_6  = convertView.findViewById(R.id.t_6);
            viewHolder.t_7  = convertView.findViewById(R.id.t_7);
            viewHolder.t_8  = convertView.findViewById(R.id.t_8);
            viewHolder.t_9  = convertView.findViewById(R.id.t_9);
            viewHolder.t_10  = convertView.findViewById(R.id.t_10);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(i == 1 || i == 0 || i == 2){
            //舱门号
            viewHolder.t_1.setText("ACDC - 0" + (i + 1));
            //模块状态
            viewHolder.t_2.setText(acdcWarningBeans[i].getAcdcIsOpen());
            //输入电压
            viewHolder.t_3.setText(acdcStateBeans[i].getAcdcInputVoltage()+"V");
            //输出电压
            viewHolder.t_4.setText(acdcStateBeans[i].getAcdcOutPutVoltage()+"V");
            //告警码
            viewHolder.t_5.setText(acdcWarningBeans[i].getErrorStateInSide());
            //最大输出
            viewHolder.t_6.setText(acdcStateBeans[i].getAcdcOutputPower()+"W");
            //输出电流
            viewHolder.t_7.setText(acdcStateBeans[i].getAcdcOutPutElectric()+"A");
            //acdc版本
            String HV = acdcStateBeans[i].getAcdcHardWareVersion();
            String SV = acdcStateBeans[i].getAcdcSoftWareVersion();
            viewHolder.t_8.setText("H" + HV + " S" + SV);
            //剩余总功率
            viewHolder.t_9.setText(acdcStateBeans[i].getAcdcSurplusPower() + "KW");
            //是否休眠
            viewHolder.t_10.setText(acdcStateBeans[i].getAcdcIsSleep());
        }
        else if(i == 3){
            //舱门号
            viewHolder.t_1.setText("柜内设备");
            //风扇1状态
            double fan_1 = environmentDataFormat.getFanStatus_1();
            String fan_1_str = "";
            if(fan_1 == 1){
                fan_1_str = "关闭";
            }else if(fan_1 == 0){
                fan_1_str = "打开";
            }
            viewHolder.t_2.setText(fan_1_str);
            //风扇2状态
            double fan_2 = environmentDataFormat.getFanStatus_2();
            String fan_2_str = "";
            if(fan_2 == 1){
                fan_2_str = "关闭";
            }else if(fan_2 == 0){
                fan_2_str = "打开";
            }
            viewHolder.t_3.setText(fan_2_str);
            //风扇模式
            int fanState = CabInfoSp.getInstance().getFanActivityMode();
            if(fanState == 1){
                viewHolder.t_4.setText("自动");
            }else{
                viewHolder.t_4.setText("手动");
            }
            //内部温度
            viewHolder.t_5.setText(environmentDataFormat.getTemperature_1()+"C");
            //环境温度
            viewHolder.t_6.setText(environmentDataFormat.getTemperature_3()+"C");
            //烟感系数
            viewHolder.t_7.setText(environmentDataFormat.getSmoke()+"");
            //内部水位
            viewHolder.t_8.setText(environmentDataFormat.getWater_1()+"");
            //外部水位
            viewHolder.t_9.setText(environmentDataFormat.getWater_2()+"");
            //环境板版本
            viewHolder.t_10.setText("S-"+environmentDataFormat.getVersion()+"H-"+environmentDataFormat.getFunctionState());
        }
        return convertView;
    }

    private class ViewHolder {
        TextView a_1;
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
    }
}
