package com.lvl6.info;

import java.io.Serializable;

public class Board implements Serializable {
	
	private static final long serialVersionUID = -4012142356784810208L;
	
	private int id;
	private int width;
	private int height;
	private String orbElements;
	
	public Board( int id, int width, int height, String orbElements )
	{
		super();
		this.id = id;
		this.width = width;
		this.height = height;
		this.orbElements = orbElements;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth( int width )
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight( int height )
	{
		this.height = height;
	}

	public String getOrbElements()
	{
		return orbElements;
	}

	public void setOrbElements( String orbElements )
	{
		this.orbElements = orbElements;
	}

	@Override
	public String toString()
	{
		return "Board [id="
			+ id
			+ ", width="
			+ width
			+ ", height="
			+ height
			+ ", orbElements="
			+ orbElements
			+ "]";
	}

}
