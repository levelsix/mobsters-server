package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MiniEvent implements Serializable {

	private static final long serialVersionUID = -2474490230347808486L;

	private int id;
	private Date startTime;
	private Date endTime;
	private String name;
	private String desc;
	private String img;

	public MiniEvent() {
		super();
	}

	public MiniEvent(int id, Date startTime, Date endTime, String name,
			String desc, String img) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
		this.desc = desc;
		this.img = img;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	@Override
	public String toString() {
		return "MiniEvent [id=" + id + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", name=" + name + ", desc=" + desc
				+ ", img=" + img + "]";
	}

}
