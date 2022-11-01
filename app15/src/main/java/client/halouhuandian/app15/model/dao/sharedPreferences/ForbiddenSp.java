package client.halouhuandian.app15.model.dao.sharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用于保存电柜的禁用参数
 */

public class ForbiddenSp {

    private static volatile ForbiddenSp forbiddenSp;
    private ForbiddenSp(){};
    public static ForbiddenSp getInstance(){
        if(forbiddenSp == null){
            synchronized (ForbiddenSp.class){
                if(forbiddenSp == null){
                    forbiddenSp = new ForbiddenSp();
                }
            }
        }
        return  forbiddenSp;
    }

    private Context context;
    private SharedPreferences sharedPreferences;

    public void init(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("Forbidden", Activity.MODE_PRIVATE);
    }

    /**
     * 设置目标舱门禁用状态
     *
     * @param index 舱门下标（从0开始）
     * @param type  设置舱门状态     1 - 正常     -1 - 停用不推出推杆      -2 - 停用推出推杆
     */
    public void setTargetForbidden(int index, int type) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("forbiddenType_" + index, type);
        editor.commit();
    }

    /**
     * 获取目标舱门禁用状态
     *
     * @param index 舱门下标（从0开始）
     */
    public int getTargetForbidden(int index) {
        int forbiddenType = sharedPreferences.getInt("forbiddenType_"+index, 1);
        return forbiddenType;
    }


    /**
     * 设置目标dcdc禁用状态
     *
     * @param index 舱门下标（从0开始）
     * @param type  设置舱门状态     1 - 正常     -1 - 禁用
     */
    public void setDcdcForbidden(int index, int type) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("dcdcForbiddenType_" + index, type);
        editor.commit();
    }

    /**
     * 获取目标dcdc禁用状态
     *
     * @param index 舱门下标（从0开始）
     */
    public int getDcdcForbidden(int index) {
        int forbiddenType = sharedPreferences.getInt("dcdcForbiddenType_"+index, 1);
        return forbiddenType;
    }
}
