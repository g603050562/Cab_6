package client.halouhuandian.app15.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // 数据库文件名
    public static final String DB_NAME = "hello_client.db";
    // 数据库表名
    public static final String TABLE_NAME = "exchange";
    // 数据库版本号
    public static final int DB_VERSION = 1;

    public static final String NUMBER = "number";
    public static final String UID = "uid";
    public static final String EXTIME = "extime";
    public static final String IN_BATTERY = "in_battery";
    public static final String IN_DOOR = "in_door";
    public static final String IN_ELECTRIC = "in_electric";
    public static final String OUT_BATTERY = "out_battery";
    public static final String OUT_DOOR = "out_door";
    public static final String OUT_ELECTRIC = "out_electric";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 当数据库文件创建时，执行初始化操作，并且只执行一次
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建表
        String sql = "create table " +
                TABLE_NAME +
                "(_id integer primary key autoincrement, " +
                NUMBER + " varchar, " +
                UID + " varchar, " +
                EXTIME + " varchar, " +
                IN_BATTERY + " varchar, " +
                IN_DOOR + " varchar, " +
                IN_ELECTRIC + " varchar, " +
                OUT_BATTERY + " varchar, " +
                OUT_DOOR + " varchar, " +
                OUT_ELECTRIC + " varchar"
                + ")";

        db.execSQL(sql);
    }

    // 当数据库版本更新执行该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}