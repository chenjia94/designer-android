package com.bruce.designer.activity;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bruce.designer.R;
import com.bruce.designer.adapter.GridAdapter;
import com.bruce.designer.constants.ConstantKey;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.Comment;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.ApiUtil;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.TimeUtil;
import com.bruce.designer.util.UiUtil;
import com.bruce.designer.util.cache.ImageLoader;

public class Activity_AlbumInfo extends BaseActivity {
	private View titlebarView;
	private TextView titleView;
	
	private ImageView avatarView;
	private TextView designerNameView;
	private TextView pubtimeView;
	private TextView albumTitleView;
	private TextView albumContentView;
	private ImageView coverView;
	
	private ListView commentListView;
	private AlbumCommentsAdapter commentsAdapter;
	
	private Handler handler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch(msg.what){
				case 1:
					Album album = (Album) msg.obj;
					if(album!=null){
						ImageLoader.loadImage("http://img.jinwanr.com.cn/staticFile/avatar/default.jpg", avatarView);
						ImageLoader.loadImage(album.getCoverLargeImg(), coverView);
						designerNameView.setText("大树珠宝");
						pubtimeView.setText(TimeUtil.displayTime(album.getCreateTime()));
						albumTitleView.setText(album.getTitle());
						albumContentView.setText(album.getRemark());
					}
					break;
				case 2:
					Map<String, Object> dataMap = (Map<String, Object>) msg.obj;
					if(dataMap!=null){
						List<Comment> commentList = (List<Comment>) dataMap.get("commentList");
						commentsAdapter.setCommentList(commentList);
						commentsAdapter.notifyDataSetChanged();
						//解决scrollview与list的冲突
						UiUtil.setListViewHeightBasedOnChildren(commentListView);
					}
					break;
				default:
					break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_info);
		
		//init view
		titlebarView = findViewById(R.id.titlebar_return);
		titlebarView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleView = (TextView) findViewById(R.id.titlebar_title);
		titleView.setText("作品集");
		
		avatarView = (ImageView) findViewById(R.id.avatar);
		coverView = (ImageView) findViewById(R.id.cover_img);
		designerNameView = (TextView) findViewById(R.id.txtUsername);
		pubtimeView = (TextView) findViewById(R.id.txtTime);
		albumTitleView = (TextView) findViewById(R.id.txtSticker);
		albumContentView = (TextView) findViewById(R.id.txtContent);
		
		commentListView =(ListView) findViewById(R.id.commentList);
		commentsAdapter = new AlbumCommentsAdapter(context, null);
		commentListView.setAdapter(commentsAdapter);
		 
		
		Intent intent = getIntent();
		Album album = (Album) intent.getSerializableExtra(ConstantKey.BUNDLE_ALBUM_INFO);
		//读取上个activity传入的albumId值 
		if(album!=null&&album.getId()!=null){
			//UI线程展示
			Message message = handler.obtainMessage(1);
			message.obj = album;
			message.sendToTarget();
			//获取评论列表
			getAlbumComments(album.getId(), 0);
		}
	}
	

	private void getAlbumInfo(final int albumId) {
		//启动线程获取数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				JsonResultBean jsonResult = ApiUtil.getAlbumInfo(albumId);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(1);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}else{//发送失败消息
					handler.obtainMessage(0).sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	private void getAlbumComments(final int albumId, final int commentsTailId) {
		//启动线程获取评论数据
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Message message;
				JsonResultBean jsonResult = ApiUtil.getAlbumComments(albumId, commentsTailId);
				if(jsonResult!=null&&jsonResult.getResult()==1){
					message = handler.obtainMessage(2);
					message.obj = jsonResult.getData();
					message.sendToTarget();
				}else{//发送失败消息
					handler.obtainMessage(0).sendToTarget();
				}
			}
		});
		thread.start();
	}
	
	class AlbumCommentsAdapter extends BaseAdapter {

		private List<Comment> commentList;
		private Context context;
		
		public AlbumCommentsAdapter(Context context, List<Comment> commentList) {
			this.context = context;
			this.commentList = commentList;
		}
		
		public List<Comment> getCommentList() {
			return commentList;
		}

		public void setCommentList(List<Comment> commentList) {
			this.commentList = commentList;
		}

		@Override
		public int getCount() {
			if (commentList != null) {
				return commentList.size();
			}
			return 0;
		}

		@Override
		public Comment getItem(int position) {
			if (commentList != null) {
				return commentList.get(position);
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
				final Comment comment = getItem(position);
				View commentItemView = LayoutInflater.from(context).inflate(R.layout.comment_item_view, null);
				
				ImageView avatarView = (ImageView) commentItemView.findViewById(R.id.avatar);
				ImageLoader.loadImage("http://img.jinwanr.com.cn/staticFile/avatar/100/100009.jpg", avatarView);
				
				TextView commentUsernameView = (TextView) commentItemView.findViewById(R.id.commentUserame);
				commentUsernameView.setText(comment.getNickname());
				
				TextView commentContentView = (TextView) commentItemView.findViewById(R.id.commentContent);
				commentContentView.setText(comment.getComment());
				
				TextView commentTimeView = (TextView) commentItemView.findViewById(R.id.commentTime);
				commentTimeView.setText(TimeUtil.displayTime(comment.getCreateTime()));
				
				return commentItemView;
			}
			return null;
		}
	}
}
