package com.lvl6.info;


public class ClanGift {

	private int id;
	private String name;
	private int hoursUntilExpiration;

	public ClanGift() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ClanGift(int id, String name, int hoursUntilExpiration) {
		super();
		this.id = id;
		this.name = name;
		this.hoursUntilExpiration = hoursUntilExpiration;
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
	@Override
	public String toString() {
		return "ClanGift [id=" + id + ", name=" + name
				+ ", hoursUntilExpiration=" + hoursUntilExpiration + "]";
	}




}
