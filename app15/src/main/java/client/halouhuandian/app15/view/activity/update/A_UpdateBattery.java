package client.halouhuandian.app15.view.activity.update;

import android.os.Bundle;
import android.support.annotation.Nullable;

import client.halouhuandian.app15.service.logic.logicUpdate.UpdateController;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;
import client.halouhuandian.app15.service.logic.logicUpdate.battery.BatteryUpdateController;

public class A_UpdateBattery extends BaseUpdateActivity {


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        maxTime = 600;
        title_1.setText("正在进行电池升级");
        title_2.setText("正在升级第" + door + "号舱门电池，请稍候！");

        UpdateInfoFormat updateInfoFormat = new UpdateInfoFormat(UpdateInfoFormat.UpdateType.updateBattery , door , path , type);
        UpdateController.getInstance().init(updateInfoFormat, new UpdateInfoReturnListener() {
            @Override
            public void returnInfo(int door, UpdateTypeInfo type, String info) {
                updateLog(info);
                if(type == UpdateTypeInfo.error){
                    showUpdateDialog("电池升级失败，程序将在10秒后返回！" , 10);
                    System.out.println("batteryUpdate - error - " + info);
                    finishUpdate();
                }else if(type == UpdateTypeInfo.success){
                    showUpdateDialog("电池升级成功，程序将在10秒后返回！" , 10);
                    System.out.println("batteryUpdate - success - " + info);
                    finishUpdate();
                }else{
                    System.out.println("batteryUpdate - step - " + info);
                }
            }

            @Override
            public void returnRate(long current, long total) {
                updateBar(current , total);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpdateController.getInstance().onDestroy();
    }
}
