package client.halouhuandian.app15.view.customUi.view;

import android.content.Context;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ResizableView extends LinearLayout {

    private int height = 0;
    private int width = 0;

    public ResizableView(Context context) {
        super(context);
        init();
    }

    public ResizableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){

    }

}
