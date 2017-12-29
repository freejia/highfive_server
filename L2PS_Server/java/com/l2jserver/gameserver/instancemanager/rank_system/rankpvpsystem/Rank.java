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

/**
 * All that data stored in global table: FastMap&ltInteger, Rank&gt RANKS in RankPvpSystemConfig class.
 * @author Masterio
 */
public class Rank
{
	private int _id = 0;
	private String _name = null; // rank name
	private long _minPoints = 0; // for get this rank
	private int _pointsForKill = 0; // for kill player with this rank
	
	private int _rewardId = 0; // reward for kill this player
	private int _rewardAmount = 0; // reward amount
	private int _nickColor = -1; // no color
	private int _titleColor = -1; // no color // TODO add support for no colors.
	
	/**
	 * @return the _id
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @param _id the _id to set
	 */
	public void setId(int _id)
	{
		this._id = _id;
	}
	
	/**
	 * @return the _name
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * @param _name the _name to set
	 */
	public void setName(String _name)
	{
		this._name = _name;
	}
	
	/**
	 * Store information about minimum Rank Points for obtain this Rank.
	 * @return the _minPoints
	 */
	public long getMinPoints()
	{
		return _minPoints;
	}
	
	/**
	 * @param _minPoints the _minPoints to set
	 */
	public void setMinPoints(long _minPoints)
	{
		this._minPoints = _minPoints;
	}
	
	/**
	 * @return the _pointsForKill
	 */
	public int getPointsForKill()
	{
		return _pointsForKill;
	}
	
	/**
	 * @param _pointsForKill the _pointsForKill to set
	 */
	public void setPointsForKill(int _pointsForKill)
	{
		this._pointsForKill = _pointsForKill;
	}
	
	/**
	 * @return the _rewardId
	 */
	public int getRewardId()
	{
		return _rewardId;
	}
	
	/**
	 * @param _rewardId the _rewardId to set
	 */
	public void setRewardId(int _rewardId)
	{
		this._rewardId = _rewardId;
	}
	
	/**
	 * @return the _rewardAmount
	 */
	public int getRewardAmount()
	{
		return _rewardAmount;
	}
	
	/**
	 * @param _rewardAmount the _rewardAmount to set
	 */
	public void setRewardAmount(int _rewardAmount)
	{
		this._rewardAmount = _rewardAmount;
	}
	
	/**
	 * @return the _nickColor
	 */
	public int getNickColor()
	{
		return _nickColor;
	}
	
	/**
	 * @param _nickColor the _nickColor to set
	 */
	public void setNickColor(int _nickColor)
	{
		this._nickColor = _nickColor;
	}
	
	/**
	 * @return the _titleColor
	 */
	public int getTitleColor()
	{
		return _titleColor;
	}
	
	/**
	 * @param _titleColor the _titleColor to set
	 */
	public void setTitleColor(int _titleColor)
	{
		this._titleColor = _titleColor;
	}
	
}
