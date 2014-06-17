package com.bruce.designer.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bruce.designer.AppManager;
import com.bruce.designer.R;
import com.bruce.designer.util.UiUtil;

public class Activity_Main extends BaseActivity {
	
	private long lastQuitTime = 0;
	
	private FragmentManager fragmentManager;
	
	private Fragment[] mFragments;
	
	private FragmentTransaction fragmentTransaction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fragmentManager = getFragmentManager();
		
		mFragments = new Fragment[2];
		mFragments[0] = fragmentManager.findFragmentById(R.id.fragment_main);
        mFragments[1] = fragmentManager.findFragmentById(R.id.fragment_designer);  
        
        showFragment(0);
		
        ImageButton btnTabMain = (ImageButton) findViewById(R.id.btnTabMain);
        btnTabMain.setOnClickListener(tabOnclickListener);
        ImageButton profileBtn = (ImageButton) findViewById(R.id.btnTabProfile);
		profileBtn.setOnClickListener(tabOnclickListener);
		
	}

	/**
	 * 显示fragment
	 * @param index
	 */
	private void showFragment(int index) {
		fragmentTransaction = fragmentManager.beginTransaction().hide(mFragments[0]).hide(mFragments[1]);  
        fragmentTransaction.show(mFragments[index]).commit();
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = false;
		if (keyCode == KeyEvent.KEYCODE_BACK) {// 退出
			long currentTime = System.currentTimeMillis();
			if (lastQuitTime <= 0 || currentTime - lastQuitTime > 2000) {
				lastQuitTime = System.currentTimeMillis();
				UiUtil.showShortToast(context, "再次点击后即可退出应用");
			} else {
				AppManager.getInstance().exitApp(context);
			}
		} else {
			flag = super.onKeyUp(keyCode, event);
		}
		return flag;
	}

	
	private OnClickListener tabOnclickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			switch(view.getId()){
				case R.id.btnTabMain:{
					showFragment(0);
					break;
				}
				case R.id.btnTabProfile:{
					showFragment(1);
					break;
				}default:{
					break;
				}
			}
		}
	};
	
}
