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

    //??????app?????????????????????
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

    //???????????????????????????
    private void doStartApplicationWithPackageName(String packagename) {

        // ?????????????????????APP?????????????????????Activities???services???versioncode???name??????
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // ?????????????????????CATEGORY_LAUNCHER???????????????Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // ??????getPackageManager()???queryIntentActivities????????????
        List<ResolveInfo> resolveinfoList = getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = ??????packname
            String packageName = resolveinfo.activityInfo.packageName;
            // ??????????????????????????????APP???LAUNCHER???Activity[???????????????packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // ??????ComponentName??????1:packagename??????2:MainActivity??????
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