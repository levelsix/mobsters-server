package com.lvl6.datastructures;

public class ZSetMember {

	protected String key;
	protected Long score;
	protected Integer rank;

	public ZSetMember() {
	}

	public ZSetMember(String key, Long score, Integer rank) {
		super();
		this.key = key;
		this.score = score;
		this.rank = rank;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		return "ZSetMember [key=" + key + ", score=" + score + ", rank=" + rank
				+ "]";
	}
}