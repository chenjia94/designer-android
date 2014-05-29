package com.bruce.designer.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.bruce.designer.constants.Config;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.Comment;
import com.bruce.designer.model.json.JsonResultBean;
import com.google.gson.reflect.TypeToken;

public class ApiUtil {
	
	/**
	 * 主屏的产品列表
	 * @param albumTailId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JsonResultBean getAlbumList(int albumTailId){
		
		String albumListUrl = Config.JINWAN_API_ALBUMS + "?albumsTailId="+albumTailId;
		LogUtil.d("=====albumListUrl======"+albumListUrl);
		String albumListJsonResult = null;
		JsonResultBean jsonResult = null;
		try {
			albumListJsonResult = HttpClientUtils.httpGet(albumListUrl);
			JSONObject jsonObject = new JSONObject(albumListJsonResult);
			int result = jsonObject.getInt("result");
			if(result==1){//成功响应
				JSONObject jsonData = jsonObject.getJSONObject("data");
				int resTailId = jsonData.getInt("albumTailId");
				String albumListStr = jsonData.getString("albumList");
				List<Album> albumList = JsonUtil.gson.fromJson(albumListStr, new TypeToken<List<Album>>(){}.getType());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("albumTailId", resTailId);
				map.put("albumList", albumList);
				jsonResult = new JsonResultBean(result, map, 0, null);
			}else{//错误响应
				int errorcode = jsonObject.getInt("errorcode");
				String message = jsonObject.getString("message");
				jsonResult = new JsonResultBean(result, null, errorcode, message);
			}
			return jsonResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtil.d("=====albumListJsonResult======"+albumListJsonResult);
		return null;
	}
	
	
	/**
	 * 获取产品详情
	 * @param albumId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JsonResultBean getAlbumInfo(int albumId){
		
		String albumInfoUrl = Config.JINWAN_API_PREFIX+"/album/"+albumId + ".json";
		
		LogUtil.d("=====albumInfoUrl======"+albumInfoUrl);
		String albumInfoJsonResult = null;
		JsonResultBean jsonResult = null;
		try {
			albumInfoJsonResult = HttpClientUtils.httpGet(albumInfoUrl);
			JSONObject jsonObject = new JSONObject(albumInfoJsonResult);
			int result = jsonObject.getInt("result");
			if(result==1){//成功响应
				JSONObject jsonData = jsonObject.getJSONObject("data");
				String albumInfoStr = jsonData.getString("albumInfo");
				Album albumInfo = JsonUtil.gson.fromJson(albumInfoStr, Album.class);
				if(albumInfo!=null){
					jsonResult = new JsonResultBean(result, albumInfo, 0, null);
				}
			}else{//错误响应
				int errorcode = jsonObject.getInt("errorcode");
				String message = jsonObject.getString("message");
				jsonResult = new JsonResultBean(result, null, errorcode, message);
			}
			return jsonResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtil.d("=====albumInfoUrl======"+albumInfoJsonResult);
		return null;
	}
	
	/**
	 * 获取产品的评论列表
	 * @param albumId
	 * @param commentsTailId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JsonResultBean getAlbumComments(int albumId, int commentsTailId){
		String albumCommentsUrl = Config.JINWAN_API_PREFIX+"/moreComments.json?albumId="+albumId+"&commentsTailId="+commentsTailId;
		
		LogUtil.d("=====albumCommentsUrl======"+albumCommentsUrl);
		String albumCommentsJsonResult = null;
		JsonResultBean jsonResult = null;
		try {
			albumCommentsJsonResult = HttpClientUtils.httpGet(albumCommentsUrl);
			JSONObject jsonObject = new JSONObject(albumCommentsJsonResult);
			int result = jsonObject.getInt("result");
			if(result==1){//成功响应
				JSONObject jsonData = jsonObject.getJSONObject("data");
				int resTailId = jsonData.getInt("tailId");
				String commentListStr = jsonData.getString("commentList");
				List<Comment> commentList = JsonUtil.gson.fromJson(commentListStr, new TypeToken<List<Comment>>(){}.getType());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("commentTailId", resTailId);
				map.put("commentList", commentList);
				jsonResult = new JsonResultBean(result, map, 0, null);
			}else{//错误响应
				int errorcode = jsonObject.getInt("errorcode");
				String message = jsonObject.getString("message");
				jsonResult = new JsonResultBean(result, null, errorcode, message);
			}
			return jsonResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtil.d("=====albumCommentsJsonResult======"+albumCommentsJsonResult);
		return null;
	}
	
	
	
	
//	@SuppressWarnings("unchecked")
//	public static Map<String, Object> getAlbumList(int albumTailId){
//		
//		String albumListUrl = Config.JINWAN_API_ALBUMS + "?albumsTailId="+albumTailId;
//		LogUtil.d("=====albumListUrl======"+albumListUrl);
//		String albumListJsonResult = null;
//		try {
//			albumListJsonResult = HttpClientUtils.httpGet(albumListUrl);
//			AlbumsResultWrapper albumsResult = JsonUtil.gson.fromJson(albumListJsonResult, AlbumsResultWrapper.class);
//			if(albumsResult!=null&&albumsResult.getResult()==1){
//				Map<String, Object> dataMap = (Map<String, Object>) albumsResult.getData();
//				return dataMap;
//			}else{//请求数据错误
//				LogUtil.d("=======responseBean error=======");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		LogUtil.d("=====albumListJsonResult======"+albumListJsonResult);
//		return null;
//	}

}
