package com.l2jserver.gameserver.instancemanager.event_engine.model;

public class PLoc
{
	private final int _x;
	private final int _y;
	private final int _z;
	private int _heading;
	
	public PLoc(int x, int y, int z)
	{
		_x = x;
		_y = y;
		_z = z;
	}
	
	public PLoc(int x, int y, int z, int heading)
	{
		_x = x;
		_y = y;
		_z = z;
		_heading = heading;
	}
	
	public int getHeading()
	{
		return _heading;
	}
	
	public int getX()
	{
		return _x;
	}
	
	public int getY()
	{
		return _y;
	}
	
	public int getZ()
	{
		return _z;
	}
}
