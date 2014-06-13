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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.designer.AppManager;
import com.bruce.designer.R;
import com.bruce.designer.adapter.GridAdapter;
import com.bruce.designer.adapter.ViewPagerAdapter;
import com.bruce.designer.constants.ConstantKey;
import com.bruce.designer.db.AlbumDB;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.ApiUtil;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UiUtil;
import com.bruce.designer.util.cache.ImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class Activity_Main extends BaseActivity {
	
	private static final int HANDLER_FLAG_ERROR = 0;
	private static final int HANDLER_FLAG_TAB1 = 1;
	private static final int HANDLER_FLAG_TAB2 = 2;
	
	private long lastQuitTime = 0;
	
	private int currentTab = 1;
	
	/*tab1中最新一条的albumId*/
	private int tab1AlbumHeadId = 0;
	/*tab1中最老一条的albumId*/
	private int tab1AlbumTailId = 0;
	/*tab1中最后刷新时间*/
	private int tab1LastRefreshTime = 0;
	
	/*tab2中最新一条的albumId*/
	private int tab2AlbumHeadId = 0;
	/*tab2中最老一条的albumId*/
	private int tab2AlbumTailId = 0;
	/*tab2中最后刷新时间*/
	private int tab2LastRefreshTime = 0;
	
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
	
	private ImageButton btnRefresh;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tab1View = findViewById(R.id.tab_recommend);
		tab2View = findViewById(R.id.tab_latest);
		tab3View = findViewById(R.id.tab_myfollow);
		
		//响应事件
		tab1View.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				currentTab = 1;
				viewPager.setCurrentItem(0);
				UiUtil.showShortToast(context, ""+currentTab);
			}
		});
		tab2View.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				currentTab = 2;
				viewPager.setCurrentItem(1);
				UiUtil.showShortToast(context, ""+currentTab);
			}
		});
		tab3View.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				currentTab = 3;
				viewPager.setCurrentItem(2);
				UiUtil.showShortToast(context, ""+currentTab);
			}
		});
		
		
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
		
		//tab1请求db数据
		List<Album> albumList= AlbumDB.queryAll(context);
		LogUtil.d("====albumList==="+albumList);
		if(albumList!=null&&albumList.size()>0){//展示db中数据
			listView1Adapter.setAlbumList(albumList);
			listView1Adapter.notifyDataSetChanged();
			tab1AlbumHeadId = albumList.get(0).getId();
			tab1AlbumTailId = albumList.get(albumList.size()-1).getId();
		}
		
		if(albumList==null||albumList.size()==0){//需要加载网络数据
			//tab1请求网络数据
			getAlbums(tab1AlbumHeadId, HANDLER_FLAG_TAB1);
		}
		
		pullToRefreshView2 = (PullToRefreshListView) tabContentView2.findViewById(R.id.pull_refresh_list);
		pullToRefreshView2.setMode(Mode.BOTH);
		pullToRefreshView2.setOnRefreshListener(tab2RefreshListener);
		listView2 = pullToRefreshView2.getRefreshableView();
		listView2Adapter = new AlbumListAdapter(context, null, 0);
		listView2.setAdapter(listView2Adapter);
		//刷新
//		pullToRefreshView2.setRefreshing(true);
		
		//tab2请求db数据
		
		//tab2请求网络数据
		getAlbums(tab2AlbumHeadId, HANDLER_FLAG_TAB2);
		
		pagerViews.add(tabContentView1);
		pagerViews.add(tabContentView2);
		
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(pagerViews, this);
		viewPager.setAdapter(viewPagerAdapter);
		
		//刷新按钮及点击事件
		btnRefresh = (ImageButton) findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch(currentTab){
					case 1:
						pullToRefreshView1.setRefreshing(true);
						break;
					case 2:
						pullToRefreshView2.setRefreshing(true);
						break;
				}
			}	
		});
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
					albumItemView = LayoutInflater.from(context).inflate(R.layout.item_album_info_view, null);
					
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
						intent.putExtra(ConstantKey.BUNDLE_USER_INFO_ID, album.getUserId());
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
			//tab1请求新数据
			getAlbums(tab1AlbumHeadId, HANDLER_FLAG_TAB1);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(getApplicationContext(), "上拉获取更多", Toast.LENGTH_LONG).show();
			//tab1请求历史数据
			getAlbums(tab1AlbumTailId, HANDLER_FLAG_TAB1);
		}
	};
	
	OnRefreshListener2<ListView> tab2RefreshListener = new OnRefreshListener2<ListView>() {
		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(getApplicationContext(), "下拉刷新", Toast.LENGTH_LONG).show();
			//tab2请求数据
			getAlbums(tab2AlbumHeadId, HANDLER_FLAG_TAB2);
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			Toast.makeText(getApplicationContext(), "上拉获取更多", Toast.LENGTH_LONG).show();
			//tab1请求历史数据
			getAlbums(tab2AlbumTailId, HANDLER_FLAG_TAB2);
		}
	};
	
	
	private void getAlbums(final int albumTailId, final int tabIndex) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				JsonResultBean jsonResult = ApiUtil.getAlbumList(0, albumTailId);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = tabDataHandler.obtainMessage(tabIndex);
					message.obj = jsonResult.getData();
					message.sendToTarget(); 
				}else{//发送失败消息
					tabDataHandler.obtainMessage(HANDLER_FLAG_ERROR).sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	private Handler tabDataHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
				case HANDLER_FLAG_TAB1:
					Map<String, Object> tab1DataMap = (Map<String, Object>) msg.obj;
					if(tab1DataMap!=null){
						List<Album> albumList = (List<Album>) tab1DataMap.get("albumList");
						if(albumList!=null&&albumList.size()>0){
							
							//save to db
							AlbumDB.save(context, albumList);
							
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
				case HANDLER_FLAG_TAB2:
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
