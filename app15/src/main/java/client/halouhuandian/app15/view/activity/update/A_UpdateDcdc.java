package client.halouhuandian.app15.view.activity.update;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateController;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;

public class A_UpdateDcdc extends BaseUpdateActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        title_1.setText("正在进行DCDC升级");

        //判断是全部升级 还是单个升级
        if(door != 0){ //单个升级

            maxTime = 180;
            p_bar_2.setVisibility(View.GONE);
            title_2.setText("正在升级第" + door + "号舱门DCDC，请稍候！");

            UpdateInfoFormat updateInfoFormat = new UpdateInfoFormat(UpdateInfoFormat.UpdateType.updateDcdc , door ,path , type);
            UpdateController.getInstance().init(updateInfoFormat, new UpdateInfoReturnListener() {
                @Override
                public void returnInfo(int door, UpdateTypeInfo type, String info) {
                    updateLog(info);
                    if(type == UpdateTypeInfo.error){
                        showUpdateDialog("dcdc升级失败，程序将在10秒后返回！" , 10);
                        finishUpdate();
                    }else if(type == UpdateTypeInfo.success){
                        showUpdateDialog("dcdc升级成功，程序将在10秒后返回！" , 10);
                        finishUpdate();
                    }
                }

                @Override
                public void returnRate(long current, long total) {
                    updateBar(current , total);
                }
            });

        }else{ //全部升级
            maxTime = 900;
            p_bar_2.setVisibility(View.VISIBLE);
            updateIteration(1);
        }
    }

    private void updateIteration(int door){
        final int mDoor = door;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title_2.setText("正在升级第" + mDoor + "号舱门DCDC，请稍候！");
            }
        });
        updateBar_2(mDoor, SystemConfig.getMaxBattery());
        UpdateInfoFormat updateInfoFormat = new UpdateInfoFormat(UpdateInfoFormat.UpdateType.updateDcdc , mDoor ,path , type);
        UpdateController.getInstance().init(updateInfoFormat, new UpdateInfoReturnListener() {
            @Override
            public void returnInfo(int door, UpdateTypeInfo type, String info) {
                updateLog(info);
                if(type == UpdateTypeInfo.error){
                    showUpdateDialog(mDoor+"号dcdc升级失败，程序将在10秒后返回！" , 10);
                    finishUpdate();
                }else if(type == UpdateTypeInfo.success){
                    if(mDoor < SystemConfig.getMaxBattery()){
                        UpdateController.getInstance().onDestroy();
                        showUpdateDialog(mDoor+"号dcdc升级成功" , 3);
                        updateIteration(mDoor + 1);
                    }else{
                        showUpdateDialog("全部dcdc升级成功，程序将在10秒后返回！" , 10);
                        finishUpdate();
                    }
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
