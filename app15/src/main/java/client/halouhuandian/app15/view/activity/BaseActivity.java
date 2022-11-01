package client.halouhuandian.app15.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import client.halouhuandian.app15.model.dao.sharedPreferences.CabInfoSp;
import client.halouhuandian.app15.model.dao.sharedPreferences.ForbiddenSp;

public class BaseActivity extends Activity {

    //this
    protected Activity activity;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
