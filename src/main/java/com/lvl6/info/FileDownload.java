package com.lvl6.info;

import java.io.Serializable;

public class FileDownload implements Serializable {
	
	private static final long serialVersionUID = 4796237251890111519L;
	
	private int id;
	private String fileName;
	private int priority;
	private boolean downloadOnlyOverWifi;
	private boolean useIphone6Prefix;
	
	public FileDownload(int id, String fileName, int priority, 
			boolean downloadOnlyOverWifi, boolean useIphone6Prefix) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.priority = priority;
		this.downloadOnlyOverWifi = downloadOnlyOverWifi;
		this.useIphone6Prefix = useIphone6Prefix;
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
	
	public boolean isUseIphone6Prefix() {
		return useIphone6Prefix;
	}

	public void setUseIphone6Prefix(boolean useIphone6Prefix) {
		this.useIphone6Prefix = useIphone6Prefix;
	}

	@Override
	public String toString() {
		return "FileDownload [id=" + id + ", fileName=" + fileName
				+ ", priority=" + priority + ", downloadOnlyOverWifi="
				+ downloadOnlyOverWifi + ", useIphone6Prefix="
				+ useIphone6Prefix + "]";
	}

	

	

}
