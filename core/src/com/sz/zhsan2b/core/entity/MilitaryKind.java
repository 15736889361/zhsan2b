package com.sz.zhsan2b.core.entity;

public class MilitaryKind {
	
	public MilitaryKind() {
	}

	public MilitaryKind(long id) {
		this.id = id;
	}

	private long id;
	//test
	private int defaultMoveWeight = 5;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getDefaultMoveWeight() {
		return defaultMoveWeight;
	}
	
	

}
