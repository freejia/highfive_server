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
import java.sql.Statement;
import java.util.Calendar;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.ThreadPoolManager;

/**
 * @author Masterio
 */
public class PvpTable
{
	private static PvpTable _instance = null;
	
	/** <pvp_id, PvP> contains killer pvp's data (victim, kills on victim, last kill time, etc.) */
	private FastMap<Integer, Pvp> _pvpTable = new FastMap<>();
	
	private PvpTable()
	{
		Calendar c = Calendar.getInstance();
		long startTime = c.getTimeInMillis();
		
		if (RankPvpSystemConfig.DATABASE_CLEANER_ENABLED)
		{
			cleanPvpTable();
		}
		
		load();
		
		c = Calendar.getInstance();
		long endTime = c.getTimeInMillis();
		System.out.println(" - PvpTable loaded " + (this.getPvpTable().size()) + " objects in " + (endTime - startTime) + " ms.");
		
		ThreadPoolManager.getInstance().scheduleGeneral(new PvpTableSchedule(), RankPvpSystemConfig.PVP_TABLE_UPDATE_INTERVAL);
	}
	
	public static PvpTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new PvpTable();
		}
		
		return _instance;
	}
	
	/**
	 * Get Pvp object, if not found returns new Pvp object for killer - victim.
	 * @param killerId
	 * @param victimId
	 * @return
	 */
	public Pvp getPvp(int killerId, int victimId)
	{
		
		// getPvp:
		for (FastMap.Entry<Integer, Pvp> e = getPvpTable().head(), end = getPvpTable().tail(); (e = e.getNext()) != end;)
		{
			if ((e.getValue().getKillerObjId() == killerId) && (e.getValue().getVictimObjId() == victimId))
			{
				return e.getValue();
			}
		}
		
		// create and getPvp:
		Pvp pvp = new Pvp();
		pvp.setKillerObjId(killerId);
		pvp.setVictimObjId(victimId);
		pvp.setDbStatus((byte) 2);
		
		int pvpId = getPvpTable().size() + 1;
		getPvpTable().put(pvpId, pvp);
		
		return getPvpTable().get(pvpId);
		
	}
	
	/**
	 * Get Pvp object, if not found returns new Pvp object for killer - victim, and reset daily fields if required.
	 * @param killerId
	 * @param victimId
	 * @param systemDay
	 * @return
	 */
	public Pvp getPvp(int killerId, int victimId, long systemDay)
	{
		
		// getPvp:
		for (FastMap.Entry<Integer, Pvp> e = getPvpTable().head(), end = getPvpTable().tail(); (e = e.getNext()) != end;)
		{
			if ((e.getValue().getKillerObjId() == killerId) && (e.getValue().getVictimObjId() == victimId))
			{
				// check daily fields, set 0 if kill day is other than system day:
				if (e.getValue().getKillDay() != systemDay)
				{
					e.getValue().setKillsToday(0);
					e.getValue().setKillsLegalToday(0);
					e.getValue().setRankPointsToday(0);
				}
				return e.getValue();
			}
		}
		
		// create and getPvp:
		Pvp pvp = new Pvp();
		pvp.setKillerObjId(killerId);
		pvp.setVictimObjId(victimId);
		pvp.setDbStatus((byte) 2);
		
		int pvpId = getPvpTable().size() + 1;
		getPvpTable().put(pvpId, pvp);
		
		return getPvpTable().get(pvpId);
		
	}
	
	/**
	 * Returns PvP statistics like total kills, total legal kills, etc. for character id.
	 * @param characterId
	 * @return
	 */
	public PvpStats getPvpStats(int characterId)
	{
		
		// get system day for update daily fields:
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);
		long systemDay = c.getTimeInMillis(); // current system day
		
		PvpStats pvpStats = new PvpStats();
		long totalRankPoints = 0; // because addTotalRankPoints updates on every add// now will be faster.
		
		// getPvpStats:
		for (FastMap.Entry<Integer, Pvp> e = getPvpTable().head(), end = getPvpTable().tail(); (e = e.getNext()) != end;)
		{
			if (e.getValue().getKillerObjId() == characterId)
			{
				pvpStats.addTotalKills(e.getValue().getKills());
				pvpStats.addTotalKillsLegal(e.getValue().getKillsLegal());
				totalRankPoints += e.getValue().getRankPoints();
				pvpStats.addTotalWarKills(e.getValue().getWarKills());
				pvpStats.addTotalWarKillsLegal(e.getValue().getWarKillsLegal());
				
				// add if is today:
				if (e.getValue().getKillDay() == systemDay)
				{
					pvpStats.addTotalKillsToday(e.getValue().getKillsToday());
					pvpStats.addTotalKillsLegalToday(e.getValue().getKillsLegalToday());
					pvpStats.addTotalRankPointsToday(e.getValue().getRankPointsToday());
				}
			}
		}
		pvpStats.setTotalRankPoints(totalRankPoints); // set rank points and update Rank.
		
		return pvpStats;
	}
	
	/**
	 * Returns PvP statistics like total kills, total legal kills, etc. for character id, daily fields are not ignored<br>
	 * if kill day = system day.
	 * @param characterId
	 * @param systemDay
	 * @return
	 */
	public PvpStats getPvpStats(int characterId, long systemDay)
	{
		
		PvpStats pvpStats = new PvpStats();
		long totalRankPoints = 0; // because addTotalRankPoints updates on every add// now will be faster.
		
		// getPvpStats:
		for (FastMap.Entry<Integer, Pvp> e = getPvpTable().head(), end = getPvpTable().tail(); (e = e.getNext()) != end;)
		{
			if (e.getValue().getKillerObjId() == characterId)
			{
				pvpStats.addTotalKills(e.getValue().getKills());
				pvpStats.addTotalKillsLegal(e.getValue().getKillsLegal());
				totalRankPoints += e.getValue().getRankPoints();
				pvpStats.addTotalWarKills(e.getValue().getWarKills());
				pvpStats.addTotalWarKillsLegal(e.getValue().getWarKillsLegal());
				
				// add if is today:
				if (e.getValue().getKillDay() == systemDay)
				{
					pvpStats.addTotalKillsToday(e.getValue().getKillsToday());
					pvpStats.addTotalKillsLegalToday(e.getValue().getKillsLegalToday());
					pvpStats.addTotalRankPointsToday(e.getValue().getRankPointsToday());
				}
			}
		}
		pvpStats.setTotalRankPoints(totalRankPoints); // set rank points and update Rank.
		
		return pvpStats;
	}
	
	public FastList<Integer> getKillersList()
	{
		FastList<Integer> list = new FastList<>();
		
		for (FastMap.Entry<Integer, Pvp> e = getPvpTable().head(), end = getPvpTable().tail(); (e = e.getNext()) != end;)
		{
			if (!list.contains(e.getValue().getKillerObjId()))
			{
				list.add(e.getValue().getKillerObjId());
			}
		}
		
		return list;
	}
	
	/**
	 * @return the _PvpTable
	 */
	public FastMap<Integer, Pvp> getPvpTable()
	{
		return _pvpTable;
	}
	
	/**
	 * @param _pvpTable the _PvpTable to set
	 */
	public void setPvpTable(FastMap<Integer, Pvp> _pvpTable)
	{
		this._pvpTable = _pvpTable;
	}
	
	private void load()
	{
		
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM rank_pvp_system");
			
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				Pvp pvp = new Pvp();
				
				pvp.setKillerObjId(rset.getInt("killer_id"));
				pvp.setVictimObjId(rset.getInt("victim_id"));
				pvp.setKills(rset.getInt("kills"));
				pvp.setKillsToday(rset.getInt("kills_today"));
				pvp.setKillsLegal(rset.getInt("kills_legal"));
				pvp.setKillsLegalToday(rset.getInt("kills_today_legal"));
				pvp.setRankPoints(rset.getLong("rank_points"));
				pvp.setRankPointsToday(rset.getLong("rank_points_today"));
				pvp.setWarKillsLegal(rset.getInt("war_kills_legal"));
				pvp.setWarKills(rset.getInt("war_kills"));
				pvp.setKillTime(rset.getLong("kill_time"));
				pvp.setKillDay(rset.getLong("kill_day"));
				
				pvp.setDbStatus((byte) 0); // load as updated.
				
				_pvpTable.put(getPvpTable().size() + 1, pvp);
				
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
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void updateDB()
	{
		
		Connection con = null;
		Statement statement = null;
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.createStatement();
			
			// search new or updated fields in PvpTable:
			for (FastMap.Entry<Integer, Pvp> e = getPvpTable().head(), end = getPvpTable().tail(); (e = e.getNext()) != end;)
			{
				
				if (e.getValue().getDbStatus() == 1)
				{
					statement.addBatch("UPDATE rank_pvp_system SET kills=" + e.getValue().getKills() + ", kills_today=" + e.getValue().getKillsToday() + ", kills_legal=" + e.getValue().getKillsLegal() + ", kills_today_legal=" + e.getValue().getKillsLegalToday() + ", rank_points=" + e.getValue().getRankPoints() + ", rank_points_today=" + e.getValue().getRankPointsToday() + ", war_kills_legal=" + e.getValue().getWarKillsLegal() + ", war_kills=" + e.getValue().getWarKills() + ", kill_time=" + e.getValue().getKillTime() + ", kill_day=" + e.getValue().getKillDay() + " WHERE killer_id=" + e.getValue().getKillerObjId() + " AND victim_id=" + e.getValue().getVictimObjId());
					e.getValue().setDbStatus((byte) 0);
				}
				else if (e.getValue().getDbStatus() == 2)
				{
					statement.addBatch("INSERT INTO rank_pvp_system (killer_id, victim_id, kills, kills_today, kills_legal, kills_today_legal, rank_points, rank_points_today, war_kills_legal, war_kills, kill_time, kill_day) VALUES (" + e.getValue().getKillerObjId() + ", " + e.getValue().getVictimObjId() + ", " + e.getValue().getKills() + ", " + e.getValue().getKillsToday() + ", " + e.getValue().getKillsLegal() + ", " + e.getValue().getKillsLegalToday() + ", " + e.getValue().getRankPoints() + ", " + e.getValue().getRankPointsToday() + ", " + e.getValue().getWarKillsLegal() + ", " + e.getValue().getWarKills() + ", " + e.getValue().getKillTime() + ", " + e.getValue().getKillDay() + ")");
					e.getValue().setDbStatus((byte) 0);
				}
				
			}
			
			statement.executeBatch();
			
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
	}
	
	/**
	 * Remove permanently not active players!
	 */
	private static void cleanPvpTable()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM rank_pvp_system WHERE (SELECT (lastAccess) FROM characters WHERE charId = killer_id) < ?");
			
			// calculate ignore time:
			Calendar c = Calendar.getInstance();
			long ignoreTime = c.getTimeInMillis() - RankPvpSystemConfig.DATABASE_CLEANER_REPEAT_TIME;
			
			statement.setLong(1, ignoreTime);
			
			statement.execute();
			
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
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		System.out.println(" - Cleaned Pvp Table with players who are inactive for longer than " + Math.round((double) RankPvpSystemConfig.DATABASE_CLEANER_REPEAT_TIME / (double) 86400000) + " day(s).");
		
	}
	
	private static class PvpTableSchedule implements Runnable
	{
		public PvpTableSchedule()
		{
			
		}
		
		@Override
		public void run()
		{
			if (!TopTable.getInstance().isUpdating())
			{
				PvpTable.getInstance().updateDB();
				ThreadPoolManager.getInstance().scheduleGeneral(new PvpTableSchedule(), RankPvpSystemConfig.PVP_TABLE_UPDATE_INTERVAL);
			}
			else
			{
				ThreadPoolManager.getInstance().scheduleGeneral(new PvpTableSchedule(), 30000);
			}
		}
	}
}