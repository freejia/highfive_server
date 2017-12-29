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
public class TopField
{
	private int _characterId = 0;
	private String _characterName = null;
	private int _characterLevel = 0;
	private int _characterBaseClassId = 0;
	private long _characterPoints = 0;
	
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
	 * @return the _characterPoints
	 */
	public long getCharacterPoints()
	{
		return _characterPoints;
	}
	
	/**
	 * @param _characterPoints the _characterPoints to set
	 */
	public void setCharacterPoints(long _characterPoints)
	{
		this._characterPoints = _characterPoints;
	}
	
	/**
	 * @return the _characterName
	 */
	public String getCharacterName()
	{
		return _characterName;
	}
	
	/**
	 * @param _characterName the _characterName to set
	 */
	public void setCharacterName(String _characterName)
	{
		this._characterName = _characterName;
	}
	
	/**
	 * @return the _characterLevel
	 */
	public int getCharacterLevel()
	{
		return _characterLevel;
	}
	
	/**
	 * @param _characterLevel the _characterLevel to set
	 */
	public void setCharacterLevel(int _characterLevel)
	{
		this._characterLevel = _characterLevel;
	}
	
	/**
	 * @return the _characterBaseClassId
	 */
	public int getCharacterBaseClassId()
	{
		return _characterBaseClassId;
	}
	
	/**
	 * @param _characterBaseClassId the _characterBaseClassId to set
	 */
	public void setCharacterBaseClassId(int _characterBaseClassId)
	{
		this._characterBaseClassId = _characterBaseClassId;
	}
	
}
