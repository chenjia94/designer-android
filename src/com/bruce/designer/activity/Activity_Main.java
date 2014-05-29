package com.bruce.designer.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.designer.AppManager;
import com.bruce.designer.R;
import com.bruce.designer.adapter.GridAdapter;
import com.bruce.designer.adapter.ViewPagerAdapter;
import com.bruce.designer.constants.ConstantKey;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.ApiUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UiUtil;
import com.bruce.designer.util.cache.ImageLoader;
import com.bruce.designer.view.RoundImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class Activity_Main extends BaseActivity {

	private long lastQuitTime = 0;
	private int albumTailId = 0;
	
	private ViewPager viewPager;
	
	private ListView listView1;
	private AlbumListAdapter listView1Adapter;
	private ListView listView2;
	private AlbumListAdapter listView2Adapter;
	
	/*Tabs*/
	private View tab1View;
	private View tab2View;
	private View tab3View;
	
	/*下拉控件*/
	private PullToRefreshListView pullToRefreshView1;
	private PullToRefreshListView pullToRefreshView2;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tab1View = findViewById(R.id.tab_recommend);
		tab2View = findViewById(R.id.tab_latest);
		tab3View = findViewById(R.id.tab_myfollow);
		
		View[] tabViews = new View[]{tab1View, tab2View, tab3View};
		for(int i=0;i<tabViews.length;i++){
			final int index = i;
			View clickedTab = tabViews[i];
			if(clickedTab!=null){
				clickedTab.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						viewPager.setCurrentItem(index);
					}
				});
			}
		}
		
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		List<View> pagerViews = new ArrayList<View>();
		// 初始化tab页的view
		View tabContentView1 = inflater.inflate(R.layout.albums_listview, null);
		View tabContentView2 = inflater.inflate(R.layout.albums_listview, null);
		
		pullToRefreshView1 = (PullToRefreshListView) tabContentView1.findViewById(R.id.pull_refresh_list);
		pullToRefreshView1.setMode(Mode.BOTH);
		pullToRefreshView1.setOnRefreshListener(tab1RefreshListener);
		listView1 = pullToRefreshView1.getRefreshableView();
		listView1Adapter = new AlbumListAdapter(context, null, 0);
		listView1.setAdapter(listView1Adapter);
		getAlbums(0, 1);
		
		pullToRefreshView2 = (PullToRefreshListView) tabContentView2.findViewById(R.id.pull_refresh_list);
		pullToRefreshView2.setMode(Mode.BOTH);
		pullToRefreshView2.setOnRefreshListener(tab2RefreshListener);
		listView2 = pullToRefreshView2.getRefreshableView();
		listView2Adapter = new AlbumListAdapter(context, null, 0);
		listView2.setAdapter(listView2Adapter);
		pullToRefreshView2.setRefreshing(true);
		getAlbums(0, 2);
		
		pagerViews.add(tabContentView1);
		pagerViews.add(tabContentView2);
		
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(pagerViews, this);
		viewPager.setAdapter(viewPagerAdapter);
	}
	
	class AlbumListAdapter extends BaseAdapter {

		private List<Album> albumList;
		private Context context;
		private int style;
		
		public AlbumListAdapter(Context context, List<Album> albumList, int style) {
			this.context = context;
			this.albumList = albumList;
			this.style = style;
		}
		
		public void setAlbumList(List<Album> albumList) {
			this.albumList = albumList;
		}

		public List<Album> getAlbumList() {
			return albumList;
		}

		@Override
		public int getCount() {
			if (albumList != null) {
				return albumList.size();
			}
			return 0;
		}

		@Override
		public Album getItem(int position) {
			if (albumList != null) {
				return albumList.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//TODO 暂未使用convertView
			if(getItem(position)!=null){
				final Album album = getItem(position);
				View albumItemView = null;
				
				if(style==1){//grid mode
					albumItemView = LayoutInflater.from(context).inflate(R.layout.item_album_view2, null);
					
					GridView gridView = (GridView) albumItemView.findViewById(R.id.grid);
					gridView.setAdapter(new GridAdapter(context));
					
				}else{//mainImg mode
					albumItemView = LayoutInflater.from(context).inflate(R.layout.item_album_view, null);
					ImageView coverView = (ImageView) albumItemView.findViewById(R.id.cover_img);
					ImageLoader.loadImage(album.getCoverMediumImg(), coverView);
				}
				
				View designerView = (View) albumItemView.findViewById(R.id.designerContainer);
				designerView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(context, Activity_UserInfo.class);
						context.startActivity(intent);
					}
				});
				
				
				ImageView avatarView = (ImageView) albumItemView.findViewById(R.id.avatar);
				ImageLoader.loadImage("http://img.jinwanr.com.cn/staticFile/avatar/100/100009.jpg", avatarView);
				
				TextView usernameView = (TextView) albumItemView.findViewById(R.id.txtUsername);
				usernameView.setText("大树珠宝");
				
				TextView pubtimeView = (TextView) albumItemView.findViewById(R.id.txtTime);
				pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
				
				TextView titleView = (TextView) albumItemView.findViewById(R.id.txtSticker);
				titleView.setText(album.getTitle());
				TextView contentView = (TextView) albumItemView.findViewById(R.id.txtContent);
				contentView.setText(album.getRemark());
				
				TextView commentView = (TextView) albumItemView.findViewById(R.id.txtComment);
				if(album.getCommentCount()>0){
					commentView.setText("查看全部"+album.getCommentCount()+"条评论");
				}else{
					commentView.setVisibility(View.GONE); 
				}
				
//				final int albumId = album.getId();
				
				albumItemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(context, Activity_AlbumInfo.class);
						intent.putExtra(ConstantKey.BUNDLE_ALBUM_INFO, album);
						context.startActivity(intent);
					}
				});
				return albumItemView;
			}
			return null;
		}
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
	


	OnRefreshListener2<ListView> tab1RefreshListener = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(getApplicationContext(), "下拉刷新", Toast.LENGTH_LONG).show();
			//tab1请求数据
			getAlbums(0, 1);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(getApplicationContext(), "上拉获取更多", Toast.LENGTH_LONG).show();
			//TODO 启动线程获取数据
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					tabDataHandler.obtainMessage(1).sendToTarget();
				}
			});
			thread.start();
		}
	};
	
	OnRefreshListener2<ListView> tab2RefreshListener = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(getApplicationContext(), "下拉刷新", Toast.LENGTH_LONG).show();
			//tab2请求数据
			getAlbums(0, 2);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(getApplicationContext(), "上拉获取更多", Toast.LENGTH_LONG).show();
			//TODO 启动线程获取数据
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					tabDataHandler.obtainMessage(2).sendToTarget();
				}
			});
			thread.start();
		}
	};
	
	
	private void getAlbums(final int albumTailId, final int tabIndex) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				JsonResultBean jsonResult = ApiUtil.getAlbumList(albumTailId);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = tabDataHandler.obtainMessage(tabIndex);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}else{//发送失败消息
					tabDataHandler.obtainMessage(0).sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	
	private Handler tabDataHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
				case 1:
					Map<String, Object> tab1DataMap = (Map<String, Object>) msg.obj;
					if(tab1DataMap!=null){
						List<Album> albumList = (List<Album>) tab1DataMap.get("albumList");
						if(albumList!=null&&albumList.size()>0){
							
							List<Album> oldAlbumList = listView1Adapter.getAlbumList();
							if(oldAlbumList==null){
								oldAlbumList = new ArrayList<Album>();
							}
							oldAlbumList.addAll(0, albumList);
							listView1Adapter.setAlbumList(oldAlbumList);
							
							listView1Adapter.notifyDataSetChanged();
						}
					}
					pullToRefreshView1.onRefreshComplete();
					break;
				case 2:
					Map<String, Object> tab2DataMap = (Map<String, Object>) msg.obj;
					if(tab2DataMap!=null){
						List<Album> albumList = (List<Album>) tab2DataMap.get("albumList");
						if(albumList!=null&&albumList.size()>0){
							List<Album> oldAlbumList = listView2Adapter.getAlbumList();
							if(oldAlbumList==null){
								oldAlbumList = new ArrayList<Album>();
							}
							oldAlbumList.addAll(0, albumList);
							listView2Adapter.setAlbumList(oldAlbumList);
							listView2Adapter.notifyDataSetChanged();
						}
					}
					pullToRefreshView2.onRefreshComplete();
					break;
				default:
					break;
			}
		};
	};
}
