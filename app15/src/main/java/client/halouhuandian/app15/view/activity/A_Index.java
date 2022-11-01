package client.halouhuandian.app15.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import client.halouhuandian.app15.R;
import client.halouhuandian.app15.config.SystemConfig;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaController;
import client.halouhuandian.app15.hardWareConncetion.daa.DaaIntegration;
import client.halouhuandian.app15.hardWareConncetion.daa.mode.DaaDataFormat;
import client.halouhuandian.app15.model.dao.fileSave.LocalLog;
import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.pub.RootCmd;
import client.halouhuandian.app15.pub.util.UtilFilesDirectory;
import client.halouhuandian.app15.view.activity.main.A_Main_Mixiang_12;
import client.halouhuandian.app15.view.activity.main.A_Main_Hello_9;
import client.halouhuandian.app15.view.activity.main.A_Main_Mixiang_9;

import static java.lang.Thread.sleep;


/**
 * Created by guo on 2017/12/2.
 * 引导页
 * 根据dc返回数据 判断用哪种界面
 */

public class A_Index extends BaseActivity {

    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.user)
    TextView user;
    @BindView(R.id.title)
    TextView title;

    //数据接口返回
    private DaaController.DaaControllerListener daaControllerListener = null;
    //dc数据缓存
    private DaaDataFormat daaDataFormat = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_index);
        ButterKnife.bind(this);

        //获取android板内核版本
        String androidCoreString = new RootCmd().execRootCmd("getprop ro.grst.version");
        LocalLog.getInstance().writeLog("初始化 - 内核版本 - " + androidCoreString , A_Index.class);

        if(SystemConfig.getServer(CabInfoSp.getInstance().getServer()) == SystemConfig.serverEnum.hello){
            title.setText("欢迎使用智能换电系统");
            version.setText("版本 : " + CabInfoSp.getInstance().getVersion());
        }else if(SystemConfig.getServer(CabInfoSp.getInstance().getServer()) == SystemConfig.serverEnum.mixiang){
            title.setText("欢迎使用智能换电系统");
            version.setText("版本 : " + CabInfoSp.getInstance().getVersion());
        }

        //daa数据缓存
        DaaController.getInstance().addListener(daaControllerListener = new DaaController.DaaControllerListener() {
            @Override
            public void returnData(DaaDataFormat mDaaDataFormat, DaaIntegration.ReturnDataType returnDataType, int index) {
                daaDataFormat = mDaaDataFormat;
                if (returnDataType.equals(DaaIntegration.ReturnDataType.dcdcInfoByBase)) {

                }
            }
        });

        //选择进入不同的界面
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(5000);
                    int type = -1;
                    if(daaDataFormat == null){
                        //todo::后期更改
                        type = 2;
                    }else{
                        int door_10 = daaDataFormat.getDcdcInfoByBaseFormat(9).getAddress();
                        int door_11 = daaDataFormat.getDcdcInfoByBaseFormat(10).getAddress();
                        int door_12 = daaDataFormat.getDcdcInfoByBaseFormat(11).getAddress();
                        int total = door_10 + door_11 + door_12;
                        if(total == -3){
                            type = 1;
                        }else{
                            type = 2;
                        }
                    }

                    //hello服务器 进入hello界面
                    if(SystemConfig.getServer(CabInfoSp.getInstance().getServer()) == SystemConfig.serverEnum.hello){
                        activity.startActivity(new Intent(activity, A_Main_Hello_9.class));
                        SystemConfig.setMaxBattery(9);
                    }

                    //MiXiang服务器 进入MiXiang界面
                    else{
                        if(type == 1){
                            activity.startActivity(new Intent(activity, A_Main_Mixiang_9.class));
                            SystemConfig.setMaxBattery(9);
                        }else{
                            activity.startActivity(new Intent(activity, A_Main_Mixiang_12.class));
                            SystemConfig.setMaxBattery(12);
                        }
                    }

                    activity.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //删除扩展卡下载信息
        String downLoadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download";
        clear(downLoadPath);
        //删除老旧apk文件
        RootCmd.execRootCmd("pm uninstall -k client.NewElectric.app_6_9_test");
    }

    private void clear(String path) {
        File file = new File(path);
        if(!file.exists()){
            return;
        }
        if (file.exists()) { //指定文件是否存在
            if (file.isFile()) { //该路径名表示的文件是否是一个标准文件
                file.delete(); //删除该文件
            } else if (file.isDirectory()) { //该路径名表示的文件是否是一个目录(文件夹)
                File[] files = file.listFiles(); //列出当前文件夹下的所有文件
                for (File f : files) {
                    clear(f.getPath()); //递归删除
                }
            }
            file.delete(); //删除文件夹(song,art,lyric)
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DaaController.getInstance().deleteListener(daaControllerListener);
        System.gc();
    }
}
