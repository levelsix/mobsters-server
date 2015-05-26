package com.lvl6.info;

import java.io.Serializable;


public class TangoGift implements Serializable {

	private static final long serialVersionUID = 6211500589020082904L;

	private int id;
	private String name;
	private int hoursUntilExpiration;
	private String imageName;

	public TangoGift() {
		super();
	}
	public TangoGift(int id, String name, int hoursUntilExpiration, String imageName)
	{
		super();
		this.id = id;
		this.name = name;
		this.hoursUntilExpiration = hoursUntilExpiration;
		this.imageName = imageName;
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
	@Override
	public String toString() {
		return "TangoGift [id=" + id + ", name=" + name
				+ ", hoursUntilExpiration=" + hoursUntilExpiration
				+ ", imageName=" + imageName + "]";
	}
}
