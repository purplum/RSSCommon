package com.sf.beans;

public class CommonRSSItem {
	
	private String title = "";
	private String description = "";
	private String pubdate = "";
	private String link = "";
	private String picLink = "";
	private String originContent = "";
	
	public String toString() {
		return "[Title] "+getTitle()+"\n"+
				"[Description] "+getDescription()+"\n";
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPubdate() {
		return pubdate;
	}
	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPicLink() {
		return picLink;
	}
	public void setPicLink(String picLink) {
		this.picLink = picLink;
	}

	public String getOriginContent() {
		return originContent;
	}

	public void setOriginContent(String originContent) {
		this.originContent = originContent;
	}

}
