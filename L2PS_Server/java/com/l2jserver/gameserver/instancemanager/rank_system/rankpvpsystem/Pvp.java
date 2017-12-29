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

import java.util.Calendar;

/**
 * Class contains informations about victim. <br>
 * Example: How many times Killer killed Victim? <br>
 * Answer: getKills().
 * @author Masterio
 */
public class Pvp
{
	private int _killerObjId = 0;
	
	private int _victimObjId = 0;
	private int _kills = 0;
	private int _killsToday = 0;
	private int _killsLegal = 0;
	private int _killsLegalToday = 0;
	private long _rankPoints = 0;
	private long _rankPointsToday = 0;
	private int _warKills = 0;
	private int _warKillsLegal = 0;
	
	private long _killTime = 0; // store date and time, used in anti-farm options.
	private long _killDay = 0; // store only date without time.
	
	/** 0 - object not changed, 1 - object updated, 2 - object inserted. */
	private byte _dbStatus = 2;
	
	public void increaseKills()
	{
		_kills++;
		onUpdate();
	}
	
	public void increaseKillsToday()
	{
		_killsToday++;
		onUpdate();
	}
	
	public void increaseKillsLegal()
	{
		_killsLegal++;
		onUpdate();
	}
	
	public void increaseKillsLegalToday()
	{
		_killsLegalToday++;
		onUpdate();
	}
	
	public void increaseRankPointsBy(long rankPoints)
	{
		_rankPoints += rankPoints;
		onUpdate();
	}
	
	public void increaseRankPointsTodayBy(long rankPoints)
	{
		_rankPointsToday += rankPoints;
		onUpdate();
	}
	
	public void increaseWarKills()
	{
		_warKills++;
		onUpdate();
	}
	
	public void increaseWarKillsLegal()
	{
		_warKillsLegal++;
		onUpdate();
	}
	
	/**
	 * @return the _killerObjId
	 */
	public int getKillerObjId()
	{
		return _killerObjId;
	}
	
	/**
	 * @param _killerObjId the _killerObjId to set
	 */
	public void setKillerObjId(int _killerObjId)
	{
		this._killerObjId = _killerObjId;
		onUpdate();
	}
	
	/**
	 * @return the _victimObjId
	 */
	public int getVictimObjId()
	{
		return _victimObjId;
	}
	
	/**
	 * @param _victimObjId the _victimObjId to set
	 */
	public void setVictimObjId(int _victimObjId)
	{
		this._victimObjId = _victimObjId;
		onUpdate();
	}
	
	/**
	 * @return the _kills
	 */
	public int getKills()
	{
		return _kills;
	}
	
	/**
	 * @param _kills the _kills to set
	 */
	public void setKills(int _kills)
	{
		this._kills = _kills;
		onUpdate();
	}
	
	/**
	 * @return the _killsToday
	 */
	public int getKillsToday()
	{
		if (!checkToday())
		{
			return 0;
		}
		return _killsToday;
	}
	
	/**
	 * @param _killsToday the _killsToday to set
	 */
	public void setKillsToday(int _killsToday)
	{
		this._killsToday = _killsToday;
		onUpdate();
	}
	
	/**
	 * @return the _killsLegal
	 */
	public int getKillsLegal()
	{
		return _killsLegal;
	}
	
	/**
	 * @param _killsLegal the _killsLegal to set
	 */
	public void setKillsLegal(int _killsLegal)
	{
		this._killsLegal = _killsLegal;
		onUpdate();
	}
	
	/**
	 * @return the _killsLegalToday
	 */
	public int getKillsLegalToday()
	{
		if (!checkToday())
		{
			return 0;
		}
		return _killsLegalToday;
	}
	
	/**
	 * @param _killsLegalToday the _killsLegalToday to set
	 */
	public void setKillsLegalToday(int _killsLegalToday)
	{
		this._killsLegalToday = _killsLegalToday;
		onUpdate();
	}
	
	/**
	 * @return the _rankPoints
	 */
	public long getRankPoints()
	{
		return _rankPoints;
	}
	
	/**
	 * @param _rankPoints the _rankPoints to set
	 */
	public void setRankPoints(long _rankPoints)
	{
		this._rankPoints = _rankPoints;
		onUpdate();
	}
	
	/**
	 * @return the _rankPintsToday
	 */
	public long getRankPointsToday()
	{
		if (!checkToday())
		{
			return 0;
		}
		return _rankPointsToday;
	}
	
	/**
	 * @param _rankPointsToday the _rankPintsToday to set
	 */
	public void setRankPointsToday(long _rankPointsToday)
	{
		this._rankPointsToday = _rankPointsToday;
		onUpdate();
	}
	
	/**
	 * @return the _warKills
	 */
	public int getWarKills()
	{
		return _warKills;
	}
	
	/**
	 * @param _warKills the _warKills to set
	 */
	public void setWarKills(int _warKills)
	{
		this._warKills = _warKills;
		onUpdate();
	}
	
	/**
	 * @return the _warKillsLegal
	 */
	public int getWarKillsLegal()
	{
		return _warKillsLegal;
	}
	
	/**
	 * @param _warKillsLegal the _warKillsLegal to set
	 */
	public void setWarKillsLegal(int _warKillsLegal)
	{
		this._warKillsLegal = _warKillsLegal;
		onUpdate();
	}
	
	/**
	 * @return the _killTime
	 */
	public long getKillTime()
	{
		return _killTime;
	}
	
	/**
	 * @param _killTime the _killTime to set
	 */
	public void setKillTime(long _killTime)
	{
		this._killTime = _killTime;
		onUpdate();
	}
	
	/**
	 * @return the _killDay
	 */
	public long getKillDay()
	{
		return _killDay;
	}
	
	/**
	 * @param _killDay the _killDay to set
	 */
	public void setKillDay(long _killDay)
	{
		this._killDay = _killDay;
		onUpdate();
	}
	
	/**
	 * @return the _dbStatus
	 */
	public byte getDbStatus()
	{
		return _dbStatus;
	}
	
	/**
	 * @param _dbStatus the _dbStatus to set
	 */
	public void setDbStatus(byte _dbStatus)
	{
		this._dbStatus = _dbStatus;
	}
	
	private void onUpdate()
	{
		if (_dbStatus == 0)
		{
			_dbStatus = 1;
		}
	}
	
	/**
	 * If Kill Day == System Day
	 * @return
	 */
	private boolean checkToday()
	{
		
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);
		long systemDay = c.getTimeInMillis(); // date
		
		if (getKillDay() != systemDay)
		{
			return false;
		}
		
		return true;
	}
	
}
