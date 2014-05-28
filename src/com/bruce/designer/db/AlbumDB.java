package com.bruce.designer.db;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bruce.designer.model.Album;

public class AlbumDB {

	public static int deleteAll() {
		SQLiteDatabase db = DBHelper.getDB();
		int result = db.delete("notice_info", null, null);
		return result;
	}

	public static List<Album> queryAll() {
		// 读取SQLite里面的数据
		SQLiteDatabase db = DBHelper.getDB();

		// 这里是把SQLite里面的数据进行排序，依据ID由大到小排序，这样可以保证ListView展示在最上面的一条 数据是最新的一条
		Cursor cursor = db.query("tb_album", null, null, null, null, null, "id DESC");

		List<Album> albumList = new ArrayList<Album>();
		while (cursor.moveToNext()) {
			Album album = new Album();
//			System.out.println("<<<<<<<<<" + data.toString());
			albumList.add(album);
		}
		return albumList;
	}
}