package com.hellohuandian.moviesupload;

import android.os.Environment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/3/10 0010.
 */
public class MoviesAppFile {

    public static String SD_CARD = Environment.getExternalStorageDirectory() + "/";;
    public static String SELFDIR = SD_CARD + "GuoApp/";


    public void creatFile () {

        if(ExistSDCard()){
            File sd = Environment.getExternalStorageDirectory();
            String filePath = SELFDIR;
            File file = new File(filePath);
            if(!file.exists()){
                file.mkdirs();
            }else{

            }
        }else {
            String filePath = "/data/data/GuoApp/";
            File file = new File(filePath);
            if(file.exists()){
                System.out.println("项目目录文件夹以创建!");
            }else{
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

    public int moviesCount(){
        int count = 0;

        File file = new File(SELFDIR);
        if(!file.exists()){
            System.out.println("路径不存在");
        }else{
            File[] files = file.listFiles();
            count = files.length;
        }
        return  count;
    }

    public void deleteFile(String name) {
        File file = new File(SELFDIR+name);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    public List<String> getAllFileName(){
        List<String>  fileList = new ArrayList<>();
        File file = new File(SELFDIR);
        if(!file.exists()){
            System.out.println("路径不存在");
        }else{
            File[] files = file.listFiles();
            for(File f:files){
                fileList.add(f.getName());
            }

            for (int i = 0; i < fileList.size() - 1; i++) {
                for (int j = 1; j < fileList.size() - i; j++) {
                    String a;
                    if (compare_date(fileList.get(j-1),fileList.get(j)) > 0) { // 比较两个整数的大小
                        a = fileList.get(j - 1);
                        fileList.set((j - 1), fileList.get(j));
                        fileList.set(j, a);//交换数据
                    }
                }
            }

            for (String s : fileList) {
                System.out.println(s);//输出arraylist的数据
            }

        }
        return fileList;
    }

    public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

}
