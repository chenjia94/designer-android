package com.bruce.designer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static String md5Encrypt(String input)
			throws NoSuchAlgorithmException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return byte2hex(md.digest(input.toString().getBytes("utf-8")));
		} catch (Exception e) {
			throw new java.lang.RuntimeException("sign error!");
		}
	}
	
	
	private static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0").append(stmp);
			else
				hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}

//	private static String getString(byte[] b) {
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < b.length; i++) {
//			sb.append(b[i]);
//		}
//		return sb.toString();
//	}

}