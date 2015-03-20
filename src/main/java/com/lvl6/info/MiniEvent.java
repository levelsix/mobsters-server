package com.lvl6.info;

import java.io.Serializable;
import java.util.Date;

public class MiniEvent implements Serializable {

	private static final long serialVersionUID = 7951935352524271812L;

	private int id;
	private Date startTime;
	private Date endTime;

	public MiniEvent() {
		super();
	}

	public MiniEvent(int id, Date startTime, Date endTime) {
		super();
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
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

	@Override
	public String toString() {
		return "MiniEvent [id=" + id + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}

}
