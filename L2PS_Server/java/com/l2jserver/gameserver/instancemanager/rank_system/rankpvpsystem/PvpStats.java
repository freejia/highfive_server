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

import javolution.util.FastMap;

/**
 * Class contains Character PvP statistics like sum of kills, sum of rank points, etc.
 * @author Masterio
 */
public class PvpStats
{
	private int _characterId = 0;
	
	private int _totalKills = 0;
	private int _totalKillsToday = 0;
	private int _totalKillsLegal = 0;
	private int _totalKillsLegalToday = 0;
	private long _totalRankPoints = 0;
	private long _totalRankPointsToday = 0;
	private int _totalWarKills = 0;
	private int _totalWarKillsLegal = 0;
	
	private Rank _rank = new Rank();
	
	public void addTotalKills(int kills)
	{
		_totalKills += kills;
	}
	
	public void addTotalKillsToday(int killsToday)
	{
		_totalKillsToday += killsToday;
	}
	
	public void addTotalKillsLegal(int killsLegal)
	{
		_totalKillsLegal += killsLegal;
	}
	
	public void addTotalKillsLegalToday(int killsLegalToday)
	{
		_totalKillsLegalToday += killsLegalToday;
	}
	
	/**
	 * Add Rank Points to Total Rank Points and update Rank.
	 * @param rankPoints
	 */
	public void addTotalRankPoints(long rankPoints)
	{
		_totalRankPoints += rankPoints;
		onUpdateRankPoints();
	}
	
	public void addTotalRankPointsToday(long rankPointsToday)
	{
		_totalRankPointsToday += rankPointsToday;
	}
	
	public void addTotalWarKills(int warKills)
	{
		_totalWarKills += warKills;
	}
	
	public void addTotalWarKillsLegal(int warKillsLegal)
	{
		_totalWarKillsLegal += warKillsLegal;
	}
	
	/**
	 * @return the _characterId
	 */
	public int getCharacterId()
	{
		return _characterId;
	}
	
	/**
	 * @param _characterId the _characterId to set
	 */
	public void setCharacterId(int _characterId)
	{
		this._characterId = _characterId;
	}
	
	/**
	 * @return the _totalKills
	 */
	public int getTotalKills()
	{
		return _totalKills;
	}
	
	/**
	 * @param _totalKills the _totalKills to set
	 */
	public void setTotalKills(int _totalKills)
	{
		this._totalKills = _totalKills;
	}
	
	/**
	 * @return the _totalKillsToday
	 */
	public int getTotalKillsToday()
	{
		return _totalKillsToday;
	}
	
	/**
	 * @param _totalKillsToday the _totalKillsToday to set
	 */
	public void setTotalKillsToday(int _totalKillsToday)
	{
		this._totalKillsToday = _totalKillsToday;
	}
	
	/**
	 * @return the _totalKillsLegal
	 */
	public int getTotalKillsLegal()
	{
		return _totalKillsLegal;
	}
	
	/**
	 * @param _totalKillsLegal the _totalKillsLegal to set
	 */
	public void setTotalKillsLegal(int _totalKillsLegal)
	{
		this._totalKillsLegal = _totalKillsLegal;
	}
	
	/**
	 * @return the _totalKillsLegalToday
	 */
	public int getTotalKillsLegalToday()
	{
		return _totalKillsLegalToday;
	}
	
	/**
	 * @param _totalKillsLegalToday the _totalKillsLegalToday to set
	 */
	public void setTotalKillsLegalToday(int _totalKillsLegalToday)
	{
		this._totalKillsLegalToday = _totalKillsLegalToday;
	}
	
	/**
	 * @return the _totalRankPoints
	 */
	public long getTotalRankPoints()
	{
		return _totalRankPoints;
	}
	
	/**
	 * Set Total Rank Points and update Rank.
	 * @param _totalRankPoints the _totalRankPoints to set
	 */
	public void setTotalRankPoints(long _totalRankPoints)
	{
		this._totalRankPoints = _totalRankPoints;
		onUpdateRankPoints();
	}
	
	/**
	 * @return the _totalRankPointsToday
	 */
	public long getTotalRankPointsToday()
	{
		return _totalRankPointsToday;
	}
	
	/**
	 * @param _totalRankPointsToday the _totalRankPointsToday to set
	 */
	public void setTotalRankPointsToday(long _totalRankPointsToday)
	{
		this._totalRankPointsToday = _totalRankPointsToday;
	}
	
	/**
	 * @return the _totalWarKills
	 */
	public int getTotalWarKills()
	{
		return _totalWarKills;
	}
	
	/**
	 * @param _totalWarKills the _totalWarKills to set
	 */
	public void setTotalWarKills(int _totalWarKills)
	{
		this._totalWarKills = _totalWarKills;
	}
	
	/**
	 * @return the _totalWarKillsLegal
	 */
	public int getTotalWarKillsLegal()
	{
		return _totalWarKillsLegal;
	}
	
	/**
	 * @param _totalWarKillsLegal the _totalWarKillsLegal to set
	 */
	public void setTotalWarKillsLegal(int _totalWarKillsLegal)
	{
		this._totalWarKillsLegal = _totalWarKillsLegal;
	}
	
	/**
	 * @return the _rank
	 */
	public Rank getRank()
	{
		return _rank;
	}
	
	/**
	 * @param _rank the _rank to set
	 */
	public void setRank(Rank _rank)
	{
		this._rank = _rank;
	}
	
	/**
	 * Update current Rank values for this character,<br>
	 * should be executed always when total rank points was updated.
	 */
	public void onUpdateRankPoints()
	{
		
		for (FastMap.Entry<Integer, Rank> e = RankPvpSystemConfig.RANKS.head(), end = RankPvpSystemConfig.RANKS.tail(); (e = e.getNext()) != end;)
		{
			// set up rank for current rank points:
			if (e.getValue().getMinPoints() <= getTotalRankPoints())
			{
				setRank(e.getValue());
				return;
			}
		}
		
		// if not set any rank, set minimum rank:
		if (RankPvpSystemConfig.RANKS.tail() != null)
		{
			setRank(RankPvpSystemConfig.RANKS.tail().getValue());
		}
	}
	
}
