package com.bruce.designer.api;

import java.util.Map;
import java.util.TreeMap;

import com.bruce.designer.util.HttpClientUtils;
import com.bruce.designer.util.LogUtil;
import com.bruce.designer.util.MD5;

public class API {

	/* 加密 5000字 上限*/
	private static final int MD5_STRING_LIMIT = 5000;

	/**
	 * 请求
	 * @param url
	 * @param paramMap
	 */
	public static String httpGet(String url) {
		return httpGet(url, null);
	}
	
	/**
	 * 请求
	 * @param url
	 * @param paramMap
	 */
	public static String httpGet(String url, TreeMap<String, String> paramMap) {
		// 构造基本参数（appId, secret或ticket）
		addBasicParam(paramMap);
		try {
			return HttpClientUtils.httpGet(url, paramMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 构造mcs请求的基本参数
	 * @param param
	 */
	private static void addBasicParam(TreeMap<String, String> param) {
		if(param==null){
			param = new TreeMap<String, String>();
		}
		// if (!manualTicket) {
		// ticket = LoginManager.INSTANCE.getTicket();
		// }
		// final String secretKey = choose(this.secretKey,
		// LoginManager.INSTANCE.getSecretKey(), ApiMapper.mcpSecretKey);

		String appId = "a";
		String secretKey = "b";// choose(this.secretKey,
								// LoginManager.INSTANCE.getSecretKey(),
								// ApiMapper.mcpSecretKey);
		param.put("app_id", appId);
		param.put("v", "1.0");
		param.put("call_id", Long.toString(System.currentTimeMillis()));
//		String ticket = "";
//		if (ticket != null) {
//			param.put("t", ticket);
//		}
		//TODO param.put("client_info", getClientInfo());//clientInfo

		final StringBuilder sb = new StringBuilder("");
		for (Map.Entry<String, String> entry : param.entrySet()) {
			sb.append(entry.getKey()).append('=').append(entry.getValue());
		}
		
		String value = limitedString(sb.toString(), MD5_STRING_LIMIT)+secretKey;
		final String sig = MD5.toMD5(value);
		LogUtil.d("======value: "+value);
		LogUtil.d("======sig: "+sig);
		param.put("sig", sig);
	}

	
	
	/**
	 * 限制加密字符串长度
	 * @param input
	 * @param maxLength
	 * @return
	 */
	private static String limitedString(String input, int maxLength) {
		if (input == null) {
			return "";
		}
		if (input.length() > maxLength) {
			return input.substring(0, maxLength);
		}
		return input;
	}

}
