package com.lvl6.info;

import java.io.Serializable;

public class BoardProperty implements Serializable {
	
	private static final long serialVersionUID = 5685688105654346112L;
	
	private int id;
	private int boardId;
	private String name;
	private int posX;
	private int posY;
	private String element;
	private int value;
	
	public BoardProperty(
		int id,
		int boardId,
		String name,
		int posX,
		int posY,
		String element,
		int value )
	{
		super();
		this.id = id;
		this.boardId = boardId;
		this.name = name;
		this.posX = posX;
		this.posY = posY;
		this.element = element;
		this.value = value;
	}

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public int getBoardId()
	{
		return boardId;
	}

	public void setBoardId( int boardId )
	{
		this.boardId = boardId;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public int getPosX()
	{
		return posX;
	}

	public void setPosX( int posX )
	{
		this.posX = posX;
	}

	public int getPosY()
	{
		return posY;
	}

	public void setPosY( int posY )
	{
		this.posY = posY;
	}

	public String getElement()
	{
		return element;
	}

	public void setElement( String element )
	{
		this.element = element;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue( int value )
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "BoardProperty [id="
			+ id
			+ ", boardId="
			+ boardId
			+ ", name="
			+ name
			+ ", posX="
			+ posX
			+ ", posY="
			+ posY
			+ ", element="
			+ element
			+ ", value="
			+ value
			+ "]";
	}

}
