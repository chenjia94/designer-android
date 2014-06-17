package com.bruce.designer.api;

import java.util.Map;

import com.bruce.designer.model.json.JsonResultBean;

public interface IApi {

	abstract Map<String, String> getParamMap();

	abstract RequestMethodEnum getRequestMethod();
	
	abstract String getRequestUri();
	
	/**
	 * 抽象方法，子类需要对响应做处理
	 * @param response
	 * @return 
	 */
	abstract JsonResultBean processResponse(String response);
}
