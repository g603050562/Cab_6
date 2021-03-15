package client.halouhuandian.app15.sp;

import android.app.Activity;
import android.content.SharedPreferences;

public class ForbiddenSp {

    private Activity activity;
    private SharedPreferences sharedPreferences;

    public ForbiddenSp(Activity activity) {
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("Forbidden", Activity.MODE_PRIVATE);
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
}
