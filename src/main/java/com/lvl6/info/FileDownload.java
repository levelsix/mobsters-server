package com.lvl6.info;

import java.io.Serializable;

public class FileDownload implements Serializable {
	
	private static final long serialVersionUID = 4796237251890111519L;
	
	private int id;
	private String fileName;
	private int priority;
	private boolean downloadOnlyOverWifi;
	
	public FileDownload(int id, String fileName, int priority, boolean downloadOnlyOverWifi) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.priority = priority;
		this.downloadOnlyOverWifi = downloadOnlyOverWifi;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public boolean isDownloadOnlyOverWifi() {
		return downloadOnlyOverWifi;
	}
	public void setDownloadOnlyOverWifi(boolean downloadOnlyOverWifi) {
		this.downloadOnlyOverWifi = downloadOnlyOverWifi;
	}

	
	
	@Override
	public String toString() {
		return "FileDownload [id=" + id + ", fileName=" + fileName
				+ ", priority=" + priority + ", downloadOnlyOverWifi="
				+ downloadOnlyOverWifi + "]";
	}
	
	

	

}