package com.bruce.designer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bruce.designer.R;

public class Activity_UserInfo extends BaseActivity {
	
	private View titlebarView;

	private TextView titleView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("设计师");
	}

	
}
