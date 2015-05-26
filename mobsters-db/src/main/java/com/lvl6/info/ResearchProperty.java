package com.lvl6.info;

import java.io.Serializable;

public class ResearchProperty implements Serializable {

	private static final long serialVersionUID = -8718270944886994869L;

	private int id;
	private String name;
	private float value;
	private int researchId;

	public ResearchProperty(int id, String name, float value, int researchId) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
		this.researchId = researchId;
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

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public int getResearchId() {
		return researchId;
	}

	public void setResearchId(int researchId) {
		this.researchId = researchId;
	}

	@Override
	public String toString() {
		return "ResearchProperty [id=" + id + ", name=" + name + ", value="
				+ value + ", researchId=" + researchId + "]";
	}

}
