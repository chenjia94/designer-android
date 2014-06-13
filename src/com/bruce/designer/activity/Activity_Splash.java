package com.bruce.designer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.bruce.designer.R;
import com.bruce.designer.util.UiUtil;

public class Activity_Splash extends BaseActivity{

	private Context context;

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			Intent intent = new Intent(context, Activity_Login.class);
			startActivity(intent);
			finish();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = Activity_Splash.this;
		setContentView(R.layout.activity_splash);
		
		//启动线程
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//TODO check version for update
				try {
					Thread.sleep(2000);
					handler.obtainMessage().sendToTarget();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		
//		ImageButton wbLoginBtnView = (ImageButton) findViewById(R.id.wbLoginButton);
//		wbLoginBtnView.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(context, Activity_Main.class);
//				startActivity(intent);
//				finish();				
//			}
//		});
	}
	
	
	/**
	 * 禁止使用退出键
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean flag = false;
		return flag;
	}


}
