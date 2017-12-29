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
package com.l2jserver.gameserver.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;

import com.l2jserver.L2DatabaseFactory;

public class NevitAdventTable
{
	private static Logger _log = Logger.getLogger(NevitAdventTable.class.getName());
	private static String SQL_RESTORE = "SELECT charId,advent_time,advent_points FROM character_advent_bonus";
	private static String SQL_REPLACE = "REPLACE INTO character_advent_bonus (charId,advent_time,advent_points) VALUES (?,?,?)";
	
	private static class HuntingState
	{
		private int adventTime;
		private int adventPoints;
		
		public HuntingState(int adventTime, int adventPoints)
		{
			super();
			this.adventTime = adventTime;
			this.adventPoints = adventPoints;
		}
		
		public int getAdventTime()
		{
			return adventTime;
		}
		
		public int getAdventPoints()
		{
			return adventPoints;
		}
		
		public void setAdventTime(int adventTime)
		{
			this.adventTime = adventTime;
		}
		
		public void setAdventPoints(int adventPoints)
		{
			this.adventPoints = adventPoints;
		}
	}
	
	private final FastMap<Integer, HuntingState> hunting = new FastMap<>();
	
	private NevitAdventTable()
	{
		load();
	}
	
	private void load()
	{
		reload();
	}
	
	public void reload()
	{
		hunting.clear();
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SQL_RESTORE);
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				int charId = rset.getInt("charId");
				int atime = rset.getInt("advent_time");
				int apoints = rset.getInt("advent_points");
				hunting.put(charId, new HuntingState(atime, apoints));
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Could not restore Hunting Bonus", e);
		}
	}
	
	public int getAdventTime(int charId)
	{
		HuntingState rec = hunting.get(charId);
		if (rec != null)
		{
			return rec.getAdventTime();
		}
		return -1;
	}
	
	public int getAdventPoints(int charId)
	{
		HuntingState rec = hunting.get(charId);
		if (rec != null)
		{
			return rec.getAdventPoints();
		}
		return 0;
	}
	
	public void setAdventTime(int charId, int time, boolean store)
	{
		HuntingState rec = hunting.get(charId);
		if (rec != null)
		{
			rec.setAdventTime(time);
		}
		else
		{
			rec = new HuntingState(time, 0);
			hunting.put(charId, rec);
		}
		if (store)
		{
			store(rec, charId);
		}
	}
	
	public void setAdventPoints(int charId, int value, boolean store)
	{
		HuntingState rec = hunting.get(charId);
		if (rec != null)
		{
			rec.setAdventPoints(value);
		}
		else
		{
			rec = new HuntingState(0, value);
			hunting.put(charId, rec);
		}
		if (store)
		{
			store(rec, charId);
		}
	}
	
	public static NevitAdventTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private void store(HuntingState rec, int charId)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SQL_REPLACE);
			storeExecute(rec, charId, statement);
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Could not update Hunting for player: " + charId, e);
		}
	}
	
	private void storeExecute(HuntingState rec, int charId, PreparedStatement statement) throws SQLException
	{
		statement.setInt(1, charId);
		statement.setInt(2, rec.getAdventTime());
		statement.setInt(3, rec.getAdventPoints());
		statement.executeUpdate();
	}
	
	public void execRecTask()
	{
		for (HuntingState rec : hunting.values())
		{
			rec.setAdventTime(0);
		}
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SQL_REPLACE);
			for (Entry<Integer, HuntingState> e : hunting.entrySet())
			{
				HuntingState rec = e.getValue();
				int charId = e.getKey();
				storeExecute(rec, charId, statement);
				statement.clearParameters();
			}
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Could not update Hunting task for players", e);
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final NevitAdventTable _instance = new NevitAdventTable();
	}
}
