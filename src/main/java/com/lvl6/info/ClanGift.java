package com.lvl6.info;


public class ClanGift {

	private int id;
	private String name;
	private int hoursUntilExpiration;
	private String imageName;
	private String quality;

	public ClanGift() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ClanGift(int id, String name, int hoursUntilExpiration, String imageName,
			String quality) {
		super();
		this.id = id;
		this.name = name;
		this.hoursUntilExpiration = hoursUntilExpiration;
		this.imageName = imageName;
		this.quality = quality;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getHoursUntilExpiration() {
		return hoursUntilExpiration;
	}
	public void setHoursUntilExpiration(int hoursUntilExpiration) {
		this.hoursUntilExpiration = hoursUntilExpiration;
	}

	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getQuality() {
		return quality;
	}
	public void setQuality(String quality) {
		this.quality = quality;
	}
	@Override
	public String toString() {
		return "ClanGift [id=" + id + ", name=" + name
				+ ", hoursUntilExpiration=" + hoursUntilExpiration
				+ ", imageName=" + imageName + ", quality=" + quality + "]";
	}




}
