package client.halouhuandian.app15.view.activity.update;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import client.halouhuandian.app15.service.logic.logicUpdate.UpdateController;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoFormat;
import client.halouhuandian.app15.service.logic.logicUpdate.UpdateInfoReturnListener;

public class A_UpdateEnvironment extends BaseUpdateActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        title_1.setText("正在进行环境板升级");

        maxTime = 180;
        p_bar_2.setVisibility(View.GONE);
        title_2.setText("正在升级环境板，请稍候！");

        UpdateInfoFormat updateInfoFormat = new UpdateInfoFormat(UpdateInfoFormat.UpdateType.updateEnvironment , door ,path , type);
        UpdateController.getInstance().init(updateInfoFormat, new UpdateInfoReturnListener() {
            @Override
            public void returnInfo(int door, UpdateTypeInfo type, String info) {
                updateLog(info);
                System.out.println("update - info - " + info);
                if(type == UpdateTypeInfo.error){
                    showUpdateDialog("环境板升级失败，程序将在10秒后返回！" , 10);
                    finishUpdate();
                }else if(type == UpdateTypeInfo.success){
                    showUpdateDialog("环境板升级成功，程序将在10秒后返回！" , 10);
                    finishUpdate();
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
