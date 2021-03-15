package client.halouhuandian.app15.log;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.hellohuandian.moviesupload.MoviesCreateFile;
import com.hellohuandian.pubfunction.Unit.LogUtil;
import com.hellohuandian.pubfunction.Unit.PubFunction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LocalLog {

    private Context context;
    private String cabID;

    public LocalLog(Context context, String cabID) {
        this.context = context;
        this.cabID = cabID;
    }

    public void writeLog(String string) {
        LogUtil.I("sd卡数量：" + PubFunction.getExtSDCardPathList().size());
        if (PubFunction.getExtSDCardPathList().size() < 1) {
            return;
        }

        File file = getOutputTxtFile();
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String writeData = df.format(new Date()) + ":   " + cabID + "   " + string;
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(writeData);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
            LogUtil.I("Clog - 写入日志 - " + writeData);
        } catch (FileNotFoundException e) {
            LogUtil.I("Clog - error - " + e.toString());
            LogUtil.I("Clog - error - 文件不存在");
            e.printStackTrace();
        } catch (IOException e) {
            LogUtil.I("Clog - error - " + e.toString());
            e.printStackTrace();
        }

    }


    private File getOutputTxtFile() {

        String outSideSd = getExternalFileDir(context);
        File mediaStorageDir = new File(outSideSd, "MyCameraApp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                LogUtil.I("Clog - error - 创建目录MyCameraApp失败");
                return null;
            }
        }

        String timeStamp_1 = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String timeStamp_2 = new SimpleDateFormat("HH").format(new Date());

        String path_1 = mediaStorageDir.getPath() + File.separator + timeStamp_1;
        String path_2 = path_1 + File.separator + timeStamp_2;

        File file_1 = new File(path_1);
        if (!file_1.exists()) {
            //创建文件夹
            file_1.mkdirs();
            LogUtil.I("Clog - 创建目录一成功");
        } else {
        }
        File file_2 = new File(path_2);
        if (!file_2.exists()) {
            //创建文件夹
            file_2.mkdirs();
            LogUtil.I("Clog - 创建目录二成功");
        } else {
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHH").format(new Date());
        File mediaFile = new File(file_2 + File.separator + "Log_" + timeStamp + ".txt");

        return mediaFile;
    }

    /**
     * 获取外置SD卡存储文件的绝对路径
     * Android 4.4以后
     *
     * @param context
     */
    public String getExternalFileDir(Context context) {
        StringBuilder sb = new StringBuilder();
        File file = context.getExternalCacheDir();     //此句代码一定要，在内部存储空间创建对应的data目录，但不存储文件
        if (file.exists()) {
            sb.append(getTFSDCardPath().toString()).append("/Android/data/").append(context.getPackageName()).append("/cache").append(File.separator).toString();
        } else {
            sb.append(getTFSDCardPath().toString()).append("/Android/data/").append(context.getPackageName()).append("/cache").append(File.separator).toString();
        }
        return sb.toString();
    }

    public String getTFSDCardPath() {
        List<String> list = getExtSDCardPathList();
        if (list.size() > 1) {
            return list.get(1);
        } else {
            return list.get(0);
        }
    }

    /**
     * 获取外置SD卡路径
     */
    public List<String> getExtSDCardPathList() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        //首先判断一下外置SD卡的状态，处于挂载状态才能获取的到
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED) && extFile.exists() && extFile.isDirectory() && extFile.canWrite()) {
            //外置SD卡的路径
            paths.add(extFile.getAbsolutePath());
        }
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile
                        .getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                //扩展存储卡即TF卡或者SD卡路径
                paths.add(mountPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

}
