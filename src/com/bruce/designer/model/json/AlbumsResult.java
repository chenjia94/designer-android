package com.bruce.designer.model.json;

import java.util.List;

import com.bruce.designer.model.Album;

public class AlbumsResult {

	private int albmTailId;
	private List<Album> albumList;
	
	
	public int getAlbmTailId() {
		return albmTailId;
	}
	public void setAlbmTailId(int albmTailId) {
		this.albmTailId = albmTailId;
	}
	public List<Album> getAlbumList() {
		return albumList;
	}
	public void setAlbumList(List<Album> albumList) {
		this.albumList = albumList;
	}
	
	
	
	
}
