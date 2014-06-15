package com.bruce.designer.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpClientUtils {

	/**
	 * http get请求
	 * 
	 * @param url
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public static String httpGet(String url, Map<String, String> paramMap)
			throws Exception {
		StringBuilder sb = null;
		if (paramMap != null && paramMap.size() > 0) {
			sb = new StringBuilder();
			for (Entry<String, String> entry : paramMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				sb.append("&");
				sb.append(key + "=" + value);
			}
		}
		if (sb != null && !"".equals(sb.toString())) {
			url = url + sb.replace(0, 1, "?");
		}
		LogUtil.v("========url======" + url);
		HttpGet httpGet = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpGet);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			// 请求成功
			String string = EntityUtils.toString(response.getEntity());
			return string;
		}
		return null;
	}

	/**
	 * httpPost 请求
	 * 
	 * @param url
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public static String httpPost(String url, Map<String, String> paramMap)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		HttpClient client = new DefaultHttpClient();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (paramMap != null && paramMap.size() > 0) {
			for (Entry<String, String> set : paramMap.entrySet()) {
				String key = set.getKey();
				String value = paramMap.get(set.getKey());
				params.add(new BasicNameValuePair(key, value));
			}
		}

		httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		HttpResponse response = client.execute(httpPost);

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			// 请求成功
			String string = EntityUtils.toString(response.getEntity());
			return string;
		}
		return null;
	}

	/**
	 * httpPost
	 * 
	 * @param url
	 * @param dataMap
	 * @return
	 * @throws Exception
	 */
	public static String httpPostJson(String url, String jsonStr)
			throws Exception {

		HttpPost httpPost = new HttpPost(url);
		HttpClient client = new DefaultHttpClient();
		httpPost.addHeader("Content-Type", "application/json");
		StringEntity s = new StringEntity(jsonStr, "UTF-8");
		httpPost.setEntity(s);
		HttpResponse response = client.execute(httpPost);

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String string = EntityUtils.toString(response.getEntity());
			return string;
		}
		LogUtil.d("----------" + response.getStatusLine().getStatusCode());

		return null;
	}

	/**
	 * 获取bitmap
	 * 
	 * @param imageUrl
	 * @return
	 */
	public static Bitmap getNetBitmap(String imageUrl) {
		try {
			URL imgURL = new URL(imageUrl);
			URLConnection conn = imgURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			// 下载图片
			Bitmap bmp = BitmapFactory.decodeStream(bis);
			// 关闭Stream
			bis.close();
			is.close();
			imgURL = null;
			return bmp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
