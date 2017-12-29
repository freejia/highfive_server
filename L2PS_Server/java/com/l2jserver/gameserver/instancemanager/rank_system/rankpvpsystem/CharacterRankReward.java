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
public class CharacterRankReward extends RankReward
{
	private int _characterId = 0;
	
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
	
}
