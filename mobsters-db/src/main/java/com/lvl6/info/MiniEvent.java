package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MiniEvent implements Serializable {

	private static final long serialVersionUID = 5373914858026496963L;

	private int id;
	private Date startTime;
	private Date endTime;
	private String name;
	private String desc;
	private String img;
	private String icon;

	public MiniEvent() {
		super();
	}

	public MiniEvent(int id, Date startTime, Date endTime, String name,
			String desc, String img, String icon) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
		this.desc = desc;
		this.img = img;
		this.icon = icon;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "MiniEvent [id=" + id + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", name=" + name + ", desc=" + desc
				+ ", img=" + img + ", icon=" + icon + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MiniEvent other = (MiniEvent) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
