package com.bruce.designer.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.bruce.designer.R;
import com.bruce.designer.adapter.AlbumSlidesAdapter;
import com.bruce.designer.adapter.GridAdapter;
import com.bruce.designer.constants.ConstantKey;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.AlbumSlide;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.ApiUtil;
import com.bruce.designer.util.DipUtil;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.cache.ImageLoader;

public class Activity_UserInfo extends BaseActivity {
	
	private View titlebarView;

	private TextView titleView;
	/*设计师头像*/
	private ImageView avatarView;
	/*设计师名称*/
	private TextView designerNameView;

	private AlbumListAdapter listView1Adapter;

	public AlbumSlidesAdapter slideAdapter;
	
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
		
		avatarView = (ImageView) findViewById(R.id.avatar);
		ImageLoader.loadImage("http://img.jinwanr.com.cn/staticFile/avatar/default.jpg", avatarView);
		
		designerNameView = (TextView) findViewById(R.id.txtUsername);
		designerNameView.setText("大树珠宝");
		
//		GridView gridView = (GridView)findViewById(R.id.albumSlideImages);
//		slideAdapter = new AlbumSlidesAdapter(context, null);
//		gridView.setAdapter(slideAdapter);
		
		ListView albumListView = (ListView)findViewById(R.id.designerAlbums);
		listView1Adapter = new AlbumListAdapter(context, null);
		albumListView.setAdapter(listView1Adapter);
		
		//更新数据
		getAlbums(0);
	}
	
	
	class AlbumListAdapter extends BaseAdapter {

		private List<Album> albumList;
		private Context context;
		
		public AlbumListAdapter(Context context, List<Album> albumList) {
			this.context = context;
			this.albumList = albumList;
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
				View albumItemView = LayoutInflater.from(context).inflate(R.layout.item_designer_album_view, null);
				
				/*构造slide数据*/
				GridView gridView = (GridView) albumItemView.findViewById(R.id.albumSlideImages);
				List<AlbumSlide> slideList = buildSlideList(album);
				slideAdapter = new AlbumSlidesAdapter(context, slideList);
				gridView.setAdapter(slideAdapter);

				TextView usernameView = (TextView) albumItemView.findViewById(R.id.txtUsername);
				usernameView.setText(album.getTitle());
				
				TextView pubtimeView = (TextView) albumItemView.findViewById(R.id.txtTime);
				pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
				
				return albumItemView;
			}
			return null;
		}
	}
	
	private List<AlbumSlide> buildSlideList(Album album) {
		List<AlbumSlide> slideList = new ArrayList<AlbumSlide>();
		for(int i=0;i<3;i++){
			AlbumSlide slide = new AlbumSlide();
			slide.setSlideSmallImg(album.getCoverSmallImg());
			slideList.add(slide);
		}
		return slideList;
	}
	
	private void getAlbums(final int albumTailId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				JsonResultBean jsonResult = ApiUtil.getAlbumList(albumTailId);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = albumDataHandler.obtainMessage(0);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	
	private Handler albumDataHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
				case 0:
					Map<String, Object> tab1DataMap = (Map<String, Object>) msg.obj;
					if(tab1DataMap!=null){
						List<Album> albumList = (List<Album>) tab1DataMap.get("albumList");
						if(albumList!=null&&albumList.size()>0){
							listView1Adapter.setAlbumList(albumList);
							listView1Adapter.notifyDataSetChanged();
						}
					}
					break;
				default:
					break;
			}
		}
	};
	
	
}
