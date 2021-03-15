package client.halouhuandian.app15.pub;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import client.halouhuandian.app15.A_Main;
import client.halouhuandian.app15.MyApplication;
import client.halouhuandian.app15.R;


/**
 * Created by apple on 2018/3/20.
 */

public class Updata {

    private Activity activity;
    private String versionUrl;
    private String versionName;
    private String saveName = "app.bin";
    private Handler renturnHandler;
    public static Handler logHandler = null;

    public static Handler PbarHandler = null;
    public static Handler closePbarHandler = null;


    private AlertDialog mAlertDialog;
    // 升级测试参数
    int a = 4;



    public Updata(Activity activity, String versionUrl, String versionName, Handler renturnHandler) {
        this.activity = activity;
        this.versionUrl = versionUrl;
        this.versionName = versionName;
        this.renturnHandler = renturnHandler;
    }

    DownloadManager downloadManager;
    long mTaskId = 0;

    //使用系统下载器下载
    public void downloadAPK() {

        deleteFile();

        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(versionUrl));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载
        // 设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        request.setMimeType(mimeString);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/download/", versionName);
        //request.setDestinationInExternalFilesDir(),也可 以自己制定下载路径
        // 将下载请求加入下载队列
        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        activity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private boolean deleteFile() {
        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + saveName;
        File file = new File(downloadPath);
        String fileName = saveName;
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //检查下载状态
            checkDownloadStatus();
            context.unregisterReceiver(this);
        }
    };

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);
        //筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    System.out.println(">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    System.out.println(">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    System.out.println(">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    System.out.println(">>>下载完成");

                    String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + saveName;
                    File f = new File(downloadPath);

                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("data", f.getName() + "    " + ShowLongFileSzie(f.length()) + "   " + f.length() + "B");
                    message.setData(bundle);
                    renturnHandler.sendMessage(message);

                    break;
                case DownloadManager.STATUS_FAILED:
                    System.out.println(">>>下载失败");
                    Toast.makeText(activity,">>>下载失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public String ShowLongFileSzie(Long length) {
        if (length >= 1048576) {
            return (length / 1048576) + "MB";
        } else if (length >= 1024) {
            return (length / 1024) + "KB";
        } else if (length < 1024) {
            return length + "B";
        } else {
            return "0KB";
        }
    }

}
