package com.hellohuandian.pubfunction.ProgressDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.hellohuandian.pubfunction.R;

public class ProgressDialog {
	private Dialog progressDialog;
	private AnimationDrawable animationDrawable;
	private Context context;
	
	public ProgressDialog(Context context) {
		this.context = context;
		init();
	}
	
	private void init(){

		if(context == null){
			return;
		}

		progressDialog = new Dialog(context, R.style.progress_dialog);
		progressDialog.setContentView(R.layout.dialog);
		progressDialog.setCancelable(true);
		progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
		msg.setText("请稍候...");
		ImageView bar = progressDialog.findViewById(R.id.loadingPrigressBar);
		bar.setImageResource(R.drawable.progress_drawable_white);
		animationDrawable = (AnimationDrawable)bar.getDrawable();
	}

	public void show(){
		if(progressDialog == null){
			init();
		}
		ImageView bar = progressDialog.findViewById(R.id.loadingPrigressBar);
		bar.setImageResource(R.drawable.progress_drawable_white);
		animationDrawable = (AnimationDrawable)bar.getDrawable();
		animationDrawable.start();
		progressDialog.show();
	}
	
	public void dismiss(){
		if(progressDialog!=null){
			animationDrawable.stop();
			progressDialog.dismiss();
		}
	}
	
	public void destory(){
		progressDialog = null;
		animationDrawable = null;
	}
}
