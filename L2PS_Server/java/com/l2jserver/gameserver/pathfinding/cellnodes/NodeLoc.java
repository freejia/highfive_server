/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.pathfinding.cellnodes;

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.pathfinding.AbstractNodeLoc;

/**
 * @author -Nemesiss-
 */
public class NodeLoc extends AbstractNodeLoc
{
	private int _x;
	private int _y;
	private short _geoHeightAndNSWE;
	
	public NodeLoc(int x, int y, short z)
	{
		_x = x;
		_y = y;
		_geoHeightAndNSWE = GeoData.getInstance().getHeightAndNSWE(x, y, z);
	}
	
	public void set(int x, int y, short z)
	{
		_x = x;
		_y = y;
		_geoHeightAndNSWE = GeoData.getInstance().getHeightAndNSWE(x, y, z);
	}
	
	public short getNSWE()
	{
		return (short) (_geoHeightAndNSWE & 0x0f);
	}
	
	@Override
	public int getX()
	{
		return (_x << 4) + L2World.MAP_MIN_X;
	}
	
	@Override
	public int getY()
	{
		return (_y << 4) + L2World.MAP_MIN_Y;
	}
	
	@Override
	public short getZ()
	{
		short height = (short) (_geoHeightAndNSWE & 0x0fff0);
		return (short) (height >> 1);
	}
	
	@Override
	public void setZ(short z)
	{
		//
	}
	
	@Override
	public int getNodeX()
	{
		return _x;
	}
	
	@Override
	public int getNodeY()
	{
		return _y;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + _x;
		result = (prime * result) + _y;
		result = (prime * result) + _geoHeightAndNSWE;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof NodeLoc))
		{
			return false;
		}
		final NodeLoc other = (NodeLoc) obj;
		if (_x != other._x)
		{
			return false;
		}
		if (_y != other._y)
		{
			return false;
		}
		if (_geoHeightAndNSWE != other._geoHeightAndNSWE)
		{
			return false;
		}
		return true;
	}
}
