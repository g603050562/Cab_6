package client.halouhuandian.app15.pub;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class CreateFile {

    private Activity context;
    public static String SD_CARD = Environment.getExternalStorageDirectory() + "/";
    public static String SELFDIR = SD_CARD + "HelloElectricity/";
    public static String Image_DIR = SELFDIR + "Log/";

    public CreateFile(Activity context) {
        this.context = context;
        init();

    }

    private void init() {

        if (ExistSDCard()) {
            File sd = Environment.getExternalStorageDirectory();
            String filePath = SELFDIR;
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
                System.out.println("在SD上创建HelloElectricity文件夹!");
            } else {
                System.out.println("SD上HelloElectricity文件夹以创建!");
            }

            String musicFilePath = Image_DIR;
            File musicfile = new File(musicFilePath);
            if (!musicfile.exists()) {
                musicfile.mkdirs();
                System.out.println("在HelloElectricity文件夹里创建HTML文件夹!");
            } else {
                System.out.println("文件夹HTML在HelloElectricity里已创建!");
            }

            File dir1 = new File(Image_DIR + "TEXT.HTML");
            if (!dir1.exists()) {
                try {
                    //在指定的文件夹中创建文件
                    dir1.createNewFile();
                    System.out.println("测试文件创建成功！！");
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            } else {
                System.out.println("测试文件已经被创建！");
            }

            creatLoglFile();

        } else {
            String filePath = "/data/data/HelloElectricity/";
            File file = new File(filePath);
            if (file.exists()) {
                System.out.println("项目目录文件夹以创建!");
            } else {
                file.mkdirs();
                System.out.println("在项目目录上创建文件夹!");
            }
        }
    }

    private boolean ExistSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            System.out.println("存在SD卡!");
            return true;
        } else
            System.out.println("不存在SD卡!");
        return false;
    }


    private void creatLoglFile() {
        for (int i = 0; i < 9; i++) {
            int count = i + 1;
            File mFile = new File(Image_DIR + "Log_" + count + ".txt");
            //判断文件是否存在，存在就删除
            if (mFile.exists()) {

            } else {
                try {
                    //创建文件
                    mFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
