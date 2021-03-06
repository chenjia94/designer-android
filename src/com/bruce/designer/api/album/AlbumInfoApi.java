package com.bruce.designer.api.album;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bruce.designer.api.AbstractApi;
import com.bruce.designer.api.RequestMethodEnum;
import com.bruce.designer.constants.Config;
import com.bruce.designer.model.Album;
import com.bruce.designer.model.json.JsonResultBean;
import com.bruce.designer.util.JsonUtil;

public class AlbumInfoApi extends AbstractApi{
	
	private String REQUESTS_URI=null;
	
	
	private Map<String, String> paramMap = null;
	
	public AlbumInfoApi(int albumId){
		REQUESTS_URI = Config.JINWAN_API_PREFIX+"/album/"+albumId + ".json";
	}
	
	@Override
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	@Override
	public RequestMethodEnum getRequestMethod() {
		return RequestMethodEnum.POST;
	}

	@Override
	public String getRequestUri() {
		return REQUESTS_URI;
	}
	
	@Override
	public JsonResultBean processResponse(String response) {
		JsonResultBean jsonResult = null;
		if(response!=null){
			try {
				JSONObject jsonObject = new JSONObject(response);
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
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jsonResult;
	}
	
}
