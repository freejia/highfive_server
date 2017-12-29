/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.instancemanager.rank_system.rankpvpsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javolution.util.FastMap;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.ThreadPoolManager;

/**
 * @author Masterio
 */
public class TopTable
{
	private static TopTable _instance = null;
	
	private boolean _isUpdating = false;
	
	/** <position, Field> contains top list */
	private FastMap<Integer, TopField> _topKillsTable = new FastMap<>();
	/** <position, PvpStats> contains top list */
	private FastMap<Integer, TopField> _topGatherersTable = new FastMap<>();
	
	private TopTable()
	{
		if (RankPvpSystemConfig.COMMUNITY_BOARD_TOP_LIST_ENABLED)
		{
			load();
			ThreadPoolManager.getInstance().scheduleGeneral(new TopTableSchedule(), RankPvpSystemConfig.TOP_TABLE_UPDATE_INTERVAL);
		}
	}
	
	public static TopTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new TopTable();
		}
		
		return _instance;
	}
	
	private void load()
	{
		Calendar c = Calendar.getInstance();
		long startTime = c.getTimeInMillis();
		
		updateTopTable();
		
		c = Calendar.getInstance();
		long endTime = c.getTimeInMillis();
		
		System.out.println(" - TopTable loaded " + getTopKillsTable().size() + " TopKillers, " + getTopGatherersTable().size() + " TopGatherers in " + (endTime - startTime) + " ms.");
	}
	
	public void updateTopTable()
	{
		
		if (RankPvpSystemConfig.COMMUNITY_BOARD_TOP_LIST_ENABLED)
		{
			// lock table:
			setUpdating(true);
			
			// clear tables:
			getTopKillsTable().clear();
			getTopGatherersTable().clear();
			
			// get minimum allowed time:
			long sysTime = Calendar.getInstance().getTimeInMillis() - RankPvpSystemConfig.COMMUNITY_BOARD_TOP_LIST_IGNORE_TIME_LIMIT;
			
			// load Tables:
			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				
				// get top killers:
				statement = con.prepareStatement("SELECT killer_id, char_name, level, base_class, sum(kills_legal) as col5, max(kill_time) as col6 FROM rank_pvp_system JOIN characters ON characters.charId = rank_pvp_system.killer_id GROUP BY killer_id HAVING col5 > 0 AND col6 >= ? ORDER BY col5 DESC LIMIT 500");
				
				statement.setLong(1, sysTime);
				
				rset = statement.executeQuery();
				
				while (rset.next())
				{
					
					TopField tf = new TopField();
					tf.setCharacterId(rset.getInt("killer_id"));
					tf.setCharacterName(rset.getString("char_name"));
					tf.setCharacterLevel(rset.getInt("level"));
					tf.setCharacterBaseClassId(rset.getInt("base_class"));
					tf.setCharacterPoints(rset.getLong("col5"));
					
					// get killer pvp stats:
					getTopKillsTable().put(getTopKillsTable().size() + 1, tf);
					
				}
				
				rset.close();
				statement.close();
				
				// get top RP gatherers:
				statement = con.prepareStatement("SELECT killer_id, char_name, level, base_class, sum(rank_points) as col5, max(kill_time) as col6 FROM rank_pvp_system JOIN characters ON characters.charId = rank_pvp_system.killer_id GROUP BY killer_id HAVING col5 > 0 AND col6 >= ? ORDER BY col5 DESC LIMIT 500");
				
				statement.setLong(1, sysTime);
				
				rset = statement.executeQuery();
				
				while (rset.next())
				{
					
					TopField tf = new TopField();
					tf.setCharacterId(rset.getInt("killer_id"));
					tf.setCharacterName(rset.getString("char_name"));
					tf.setCharacterLevel(rset.getInt("level"));
					tf.setCharacterBaseClassId(rset.getInt("base_class"));
					tf.setCharacterPoints(rset.getLong("col5"));
					
					// get killer pvp stats:
					getTopGatherersTable().put(getTopGatherersTable().size() + 1, tf);
					
				}
				
				rset.close();
				statement.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if (con != null)
					{
						con.close();
						con = null;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			// unlock table:
			setUpdating(false);
		}
	}
	
	private static class TopTableSchedule implements Runnable
	{
		public TopTableSchedule()
		{
			
		}
		
		@Override
		public void run()
		{
			if (!TopTable.getInstance().isUpdating())
			{
				TopTable.getInstance().updateTopTable();
				ThreadPoolManager.getInstance().scheduleGeneral(new TopTableSchedule(), RankPvpSystemConfig.TOP_TABLE_UPDATE_INTERVAL);
			}
			else
			{
				ThreadPoolManager.getInstance().scheduleGeneral(new TopTableSchedule(), 30000);
			}
		}
	}
	
	/**
	 * @return the _topKillsTable
	 */
	public FastMap<Integer, TopField> getTopKillsTable()
	{
		return _topKillsTable;
	}
	
	/**
	 * @param _topKillsTable the _topKillsTable to set
	 */
	public void setTopKillsTable(FastMap<Integer, TopField> _topKillsTable)
	{
		this._topKillsTable = _topKillsTable;
	}
	
	/**
	 * @return the _topGatherersTable
	 */
	public FastMap<Integer, TopField> getTopGatherersTable()
	{
		return _topGatherersTable;
	}
	
	/**
	 * @param _topGatherersTable the _topGatherersTable to set
	 */
	public void setTopGatherersTable(FastMap<Integer, TopField> _topGatherersTable)
	{
		this._topGatherersTable = _topGatherersTable;
	}
	
	/**
	 * @return the _isUpdating
	 */
	public boolean isUpdating()
	{
		return _isUpdating;
	}
	
	/**
	 * @param _isUpdating the _isUpdating to set
	 */
	public void setUpdating(boolean _isUpdating)
	{
		this._isUpdating = _isUpdating;
	}
	
}