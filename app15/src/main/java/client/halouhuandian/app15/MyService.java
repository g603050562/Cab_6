package client.halouhuandian.app15;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.text.TextUtils;


import java.util.List;


/**
 * Created by apple on 2017/9/12.
 */


public class MyService extends Service {

    private Context context;
    private Thread dateThread;

    private int isStopCount = 0;
    private int isStopMaxCount = 5;

    private SharedPreferences sharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("protectThread - step - onCreate");
        context = this;
        sharedPreferences = getSharedPreferences("CabInfo", Activity.MODE_PRIVATE);

        dateThread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    while (true) {
                        String is_thread_protection = sharedPreferences.getString("thread_protection_type", "1");
                        System.out.println("protectThread - type - " + is_thread_protection + " - isTop - " + isRunningForeground(context));
                        if (is_thread_protection.equals("1")) {
                            if (isRunningForeground(context) == false) {
                                isStopCount++;
                                if (isStopCount > isStopMaxCount) {
                                    doStartApplicationWithPackageName("client.NewElectric.app15");
                                    isStopMaxCount = 0;
                                }
                            } else {
                                isStopCount = 0;
                            }
                        }
                        sleep(2000);
                    }
                } catch (InterruptedException e) {
                    System.out.println("protectThread - error -" + e.toString());
                }
            }
        };
        dateThread.start();
    }

    //判断app是否在前台运行
    private boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String currentPackageName = cn.getPackageName();
            if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(getPackageName())) {
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    //通过包名打开主程序
    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("protectThread - step - onStartCommand");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("protectThread - step - onDestroy");
    }
}