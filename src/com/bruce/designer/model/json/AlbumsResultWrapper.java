package com.bruce.designer.model.json;

import java.util.Map;

public class AlbumsResultWrapper extends BaseJsonResult{

	
	private Map<String, Object> data;

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	
}
