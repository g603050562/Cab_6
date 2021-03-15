package client.halouhuandian.app15;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;


public class A_Index extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        System.out.println("Activity：A_Index onCreate");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(A_Index.this, A_Main.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
