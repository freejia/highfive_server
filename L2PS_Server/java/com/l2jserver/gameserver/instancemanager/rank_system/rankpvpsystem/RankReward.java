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
 * @author Masterio
 */
public class RankReward
{
	private int _rewardId = 0;
	private int _itemId = 0;
	private int _itemAmount = 0;
	private int _minRankPoints = 0;
	
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
	 * @return the _itemId
	 */
	public int getItemId()
	{
		return _itemId;
	}
	
	/**
	 * @param _itemId the _itemId to set
	 */
	public void setItemId(int _itemId)
	{
		this._itemId = _itemId;
	}
	
	/**
	 * @return the _itemAmount
	 */
	public int getItemAmount()
	{
		return _itemAmount;
	}
	
	/**
	 * @param _itemAmount the _itemAmount to set
	 */
	public void setItemAmount(int _itemAmount)
	{
		this._itemAmount = _itemAmount;
	}
	
	/**
	 * @return the _minRankPoints
	 */
	public int getMinRankPoints()
	{
		return _minRankPoints;
	}
	
	/**
	 * @param _minRankPoints the _minRankPoints to set
	 */
	public void setMinRankPoints(int _minRankPoints)
	{
		this._minRankPoints = _minRankPoints;
	}
	
}
