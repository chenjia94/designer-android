package com.bruce.designer.activity;

import com.bruce.designer.AppManager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

public class BaseActivity extends Activity {

	protected Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		AppManager.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getInstance().finishActivity(this);
	}

}
