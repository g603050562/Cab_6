package client.halouhuandian.app15;

import android.app.Activity;
import android.app.Application;
import android.os.Build;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import client.halouhuandian.app15.serial_port.IFCanBusResultListener;
import client.halouhuandian.app15.serial_port.IFSerialPortResultListener;
import client.halouhuandian.app15.serial_port.SerialAndCanPortUtils;


/**
 * Created by hasee on 2017/3/23.
 * 主要功能就是 完全退出APP
 */

public class MyApplication extends Application implements IFCanBusResultListener, IFSerialPortResultListener {

    /**
     * 连接域名和 版本信息
     */
    public static String cab_version = BuildConfig.VERSION_NAME;

    /**
     * 单例模式中获取唯一的MyApplication实例
     * 完全退出程序时 需要
     */

    public static List<Activity> activitys = new LinkedList<Activity>();
    private static MyApplication instance;
    public static String device_model = "";
    public static String version_release = "";

    public static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activitys != null && activitys.size() > 0) {
            if (!activitys.contains(activity)) {
                activitys.add(activity);
            }
        } else {
            activitys.add(activity);
        }
    }

    public void deletActivity(Activity activity) {

    }

    // 遍历所有Activity并finish
    public void exit() {
        System.out.println("Activity：ALL onDestory");
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
        }
        System.exit(0);
    }


    /**
     * 485串口 和 canbus 初始化
     */

    public static SerialAndCanPortUtils serialAndCanPortUtils = null;

    public interface IFResultAppLinstener {
        void onCanResultApp(byte[] canData);

        void onSerialResultApp(byte[] serData);
    }

    public static List<IFResultAppLinstener> IFResultAppLinsteners = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        System.out.println("MyApplication：初始化");
        device_model = Build.MODEL; // 设备型号 。
        version_release = Build.VERSION.RELEASE; // 设备的系统版本 。

        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setAppVersion(cab_version + "");
        strategy.setDeviceID(CabinetUtil.optCabinetNumber(getApplicationContext()));
        strategy.setAppPackageName(PackageUtil.getAppPackageName(getApplicationContext()));
        CrashReport.initCrashReport(getApplicationContext(), "1876fec5ec", false, strategy);
    }


    // 添加Listener到容器中
    public void initSerialAndCanPortUtils() {
        if (serialAndCanPortUtils == null) {
            serialAndCanPortUtils = new SerialAndCanPortUtils();
            serialAndCanPortUtils.openCanPort(this);
            serialAndCanPortUtils.openSerialPort(this);
        }
    }

    // 添加Listener到容器中
    public void addListener(IFResultAppLinstener IFResultAppLinstener) {
        try {
            if (IFResultAppLinsteners != null && IFResultAppLinsteners.size() > 0) {
                if (!IFResultAppLinsteners.contains(IFResultAppLinstener)) {
                    IFResultAppLinsteners.add(IFResultAppLinstener);
                }
            } else {
                IFResultAppLinsteners.add(IFResultAppLinstener);
            }

            System.out.println("IFResultAppLinsteners   " + IFResultAppLinsteners.size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 删除Listener到容器中
    public void deletListener(IFResultAppLinstener IFResultAppLinstener) {
        try {
            IFResultAppLinsteners.remove(IFResultAppLinstener);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCanBusResult(byte[] canData) {
        if (IFResultAppLinsteners != null) {
            try {
                for (int i = 0; i < IFResultAppLinsteners.size(); i++) {
                    IFResultAppLinsteners.get(i).onCanResultApp(canData);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onSerialPortResult(byte[] serData) {
        if (IFResultAppLinsteners != null) {
            for (int i = 0; i < IFResultAppLinsteners.size(); i++) {
                IFResultAppLinsteners.get(i).onSerialResultApp(serData);
            }
        }
    }
}

