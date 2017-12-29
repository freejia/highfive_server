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

/**
 * @author Masterio
 */
public class RankRewardTable
{
	
	private static RankRewardTable _instance = null;
	
	private FastMap<Integer, RankReward> _rankRewardTable = new FastMap<>();
	
	private RankRewardTable()
	{
		Calendar c = Calendar.getInstance();
		long startTime = c.getTimeInMillis();
		
		load();
		
		c = Calendar.getInstance();
		long endTime = c.getTimeInMillis();
		
		System.out.println(" - RankRewardTable loaded " + (this.getRankRewardTable().size()) + " objects in " + (endTime - startTime) + " ms.");
	}
	
	public static RankRewardTable getInstance()
	{
		if (_instance == null)
		{
			_instance = new RankRewardTable();
		}
		
		return _instance;
	}
	
	/**
	 * @return the _rewardTable
	 */
	public FastMap<Integer, RankReward> getRankRewardTable()
	{
		return _rankRewardTable;
	}
	
	/**
	 * @param _rankRewardTable the _rewardTable to set
	 */
	public void setRankRewardTable(FastMap<Integer, RankReward> _rankRewardTable)
	{
		this._rankRewardTable = _rankRewardTable;
	}
	
	public FastMap<Integer, RankReward> getRankRewardByRankPoints(int rankPoints)
	{
		FastMap<Integer, RankReward> rankRewards = new FastMap<>();
		
		for (FastMap.Entry<Integer, RankReward> e = getRankRewardTable().head(), end = getRankRewardTable().tail(); (e = e.getNext()) != end;)
		{
			if (e.getValue().getMinRankPoints() <= rankPoints)
			{
				rankRewards.put(e.getKey(), e.getValue());
			}
		}
		
		return rankRewards;
	}
	
	private void load()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM rank_pvp_system_rank_rewards");
			
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				RankReward rr = new RankReward();
				
				rr.setRewardId(rset.getInt("reward_id"));
				rr.setItemId(rset.getInt("item_id"));
				rr.setItemAmount(rset.getInt("item_amount"));
				rr.setMinRankPoints(rset.getInt("min_rank_points"));
				
				_rankRewardTable.put(rset.getInt("reward_id"), rr);
				
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
}
