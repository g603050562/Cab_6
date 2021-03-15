package client.halouhuandian.app15;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hellohuandian.pubfunction.Unit.LogUtil;

import static android.content.Context.ALARM_SERVICE;


//开机自启动广播接受
public class AutoStartBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {     // boot
//            Intent intent2 = new Intent(context, A_Index.class);
//            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent2.putExtra("type","door");
//            context.startActivity(intent2);
            PackageUtil.launcher(context);
        }
    }

}