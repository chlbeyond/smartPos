package com.rainbow.smartpos.place;

public class Bean {
	public static final int SINGLE_TYPE = 0; // view类型0
	public static final int SET_TYPE = 1; // view类型1
	private int type;

	public Bean(int type) {
		super();
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
