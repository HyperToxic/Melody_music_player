package com.guohow.melody.utils;

public class Mp3Info {
	long id, size;

	String title, url, art, duration;

	// public Mp3Info(long id, long duration, long size, String art, String
	// title,
	// String url) {
	// this.id = id;
	// this.duration = duration;
	// this.size = size;
	// this.title = title;
	// this.art = art;
	// this.url = url;
	//
	// }

	public void setId(long id) {
		this.id = id;
	}

	public void setDuration(String dur) {
		this.duration = dur;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public void setArt(String art) {

		this.art = art;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUrl(String url) {
		this.url = url;
		;
	}

	public long getId() {
		return this.id;
	}

	public String getDuration() {
		return this.duration;
	}

	public long getSize() {
		return this.size;
	}

	public String getArt() {

		return this.art;
	}

	public String getTitle() {
		return this.title;
	}

	public String getUrl() {
		return this.url;

	}

}
