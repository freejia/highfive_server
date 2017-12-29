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

import javolution.util.FastList;
import javolution.util.FastMap.Entry;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.util.Broadcast;

/**
 * @author Masterio
 */
public class RankPvpSystemComboKill
{
	
	private FastList<Integer> _victims = new FastList<>();
	
	private int _comboLevel = 0; // _comboLevel contains real combo size, do not use _victims.size() !
	
	private long _lastKillTime = 0;
	
	/**
	 * Add victim information to combo list (_victims).
	 * @param victimId
	 * @param killTime
	 * @return
	 */
	public boolean addVictim(int victimId, long killTime)
	{
		
		if (!RankPvpSystemConfig.COMBO_KILL_PROTECTION_NO_REPEAT_ENABLED)
		{
			setComboLevel(getComboLevel() + 1);
			setLastKillTime(killTime);
			
			return true;
		}
		else if (RankPvpSystemConfig.COMBO_KILL_PROTECTION_NO_REPEAT_ENABLED)
		{
			if (!getVictims().contains(victimId))
			{
				getVictims().add(victimId);
				setComboLevel(getComboLevel() + 1);
				setLastKillTime(killTime);
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Shout in LOCAL or GLOBAL area information about killer combo.
	 * @param killer
	 * @param victim
	 */
	public void shoutComboKill(L2PcInstance killer, L2PcInstance victim)
	{
		
		String msg = null;
		
		CreatureSay cs;
		
		if (!RankPvpSystemConfig.COMBO_KILL_ALT_MESSAGES_ENABLED)
		{
			if (RankPvpSystemConfig.COMBO_KILL_LOCAL_AREA_MESSAGES.containsKey(getComboLevel()))
			{
				msg = RankPvpSystemConfig.COMBO_KILL_LOCAL_AREA_MESSAGES.get(getComboLevel());
				msg = msg.replace("%killer%", killer.getName());
				msg = msg.replace("%victim%", victim.getName());
				msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));
				
				cs = new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", msg);
				
				Broadcast.toSelfAndKnownPlayers(killer, cs);
				
			}
			else if (RankPvpSystemConfig.COMBO_KILL_GLOBAL_AREA_MESSAGES.containsKey(getComboLevel()))
			{
				msg = RankPvpSystemConfig.COMBO_KILL_GLOBAL_AREA_MESSAGES.get(getComboLevel());
				msg = msg.replace("%killer%", killer.getName());
				msg = msg.replace("%victim%", victim.getName());
				msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));
				
				cs = new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", msg);
				
				Broadcast.toAllOnlinePlayers(cs);
				
			}
			else
			{
				// global have higher priority than local.
				
				if ((RankPvpSystemConfig.COMBO_KILL_GLOBAL_AREA_MESSAGES.tail().getPrevious().getKey() != null) && (getComboLevel() > RankPvpSystemConfig.COMBO_KILL_GLOBAL_AREA_MESSAGES.tail().getPrevious().getKey()))
				{ // if combo size greater than global max key.
					msg = RankPvpSystemConfig.COMBO_KILL_GLOBAL_AREA_MESSAGES.tail().getPrevious().getValue();
					msg = msg.replace("%killer%", killer.getName());
					msg = msg.replace("%victim%", victim.getName());
					msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));
					
					cs = new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", msg);
					
					Broadcast.toAllOnlinePlayers(cs);
					
				}
				else if ((RankPvpSystemConfig.COMBO_KILL_LOCAL_AREA_MESSAGES.tail().getPrevious().getKey() != null) && (getComboLevel() > RankPvpSystemConfig.COMBO_KILL_LOCAL_AREA_MESSAGES.tail().getPrevious().getKey()))
				{// if combo size greater than local max key.
					msg = RankPvpSystemConfig.COMBO_KILL_LOCAL_AREA_MESSAGES.tail().getPrevious().getValue();
					msg = msg.replace("%killer%", killer.getName());
					msg = msg.replace("%victim%", victim.getName());
					msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));
					
					cs = new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", msg);
					
					Broadcast.toSelfAndKnownPlayers(killer, cs);
					
				}
			}
		}
		else
		{
			
			if (getComboLevel() > 1)
			{
				if ((RankPvpSystemConfig.COMBO_KILL_ALT_GLOBAL_MESSAGE_LVL > 0) && (getComboLevel() >= RankPvpSystemConfig.COMBO_KILL_ALT_GLOBAL_MESSAGE_LVL))
				{
					msg = RankPvpSystemConfig.COMBO_KILL_ALT_MESSAGE;
					msg = msg.replace("%killer%", killer.getName());
					msg = msg.replace("%victim%", victim.getName());
					msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));
					
					cs = new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", msg);
					
					Broadcast.toAllOnlinePlayers(cs);
					
				}
				else
				{
					msg = RankPvpSystemConfig.COMBO_KILL_ALT_MESSAGE;
					msg = msg.replace("%killer%", killer.getName());
					msg = msg.replace("%victim%", victim.getName());
					msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));
					
					cs = new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", msg);
					
					Broadcast.toSelfAndKnownPlayers(killer, cs);
					
				}
			}
		}
	}
	
	/**
	 * Shout in chat window information about defeated killer, <br>
	 * who have combo level >= COMBO_KILL_DEFEAT_MESSAGE_MIN_LVL.
	 * @param killer - defeated player (this).
	 */
	public void shoutDefeatMessage(L2PcInstance killer)
	{
		if (RankPvpSystemConfig.COMBO_KILL_DEFEAT_MESSAGE_ENABLED)
		{
			if (getComboLevel() >= RankPvpSystemConfig.COMBO_KILL_DEFEAT_MESSAGE_MIN_LVL)
			{
				String msg = RankPvpSystemConfig.COMBO_KILL_DEFEAT_MESSAGE;
				msg = msg.replace("%killer%", killer.getName());
				msg = msg.replace("%combo_level%", Integer.toString(getComboLevel()));
				
				CreatureSay cs = new CreatureSay(0, Say2.CRITICAL_ANNOUNCE, "", msg);
				
				Broadcast.toKnownPlayers(killer, cs);
			}
		}
	}
	
	/**
	 * Get Rank Points ratio for combo size.
	 * @return
	 */
	public double getComboKillRankPointsRatio()
	{
		
		if (getComboLevel() > 0)
		{
			
			// checking if combo size is in combo rank points ratio table:
			Entry<Integer, Double> entry = RankPvpSystemConfig.COMBO_KILL_RANK_POINTS_RATIO.getEntry(getComboLevel());
			
			if (entry != null)
			{
				return entry.getValue();
			}
			
			// if not, then check the last element of table. Reason: combo size can be greater than max table value, then killer should get max ratio:
			Entry<Integer, Double> tail = RankPvpSystemConfig.COMBO_KILL_RANK_POINTS_RATIO.tail().getPrevious();
			
			if ((tail != null) && (tail.getKey() < getComboLevel()))
			{
				return tail.getValue();
			}
			
		}
		
		return 1.0;
	}
	
	/**
	 * @return the _victims
	 */
	public FastList<Integer> getVictims()
	{
		return _victims;
	}
	
	/**
	 * @param _victims the _victims to set
	 */
	public void setVictims(FastList<Integer> _victims)
	{
		this._victims = _victims;
	}
	
	/**
	 * @return the _victimsSize
	 */
	public int getComboLevel()
	{
		return _comboLevel;
	}
	
	/**
	 * @param comboLevel the _comboLevel to set
	 */
	public void setComboLevel(int comboLevel)
	{
		this._comboLevel = comboLevel;
	}
	
	/**
	 * @return the _lastKillTime
	 */
	public long getLastKillTime()
	{
		return _lastKillTime;
	}
	
	/**
	 * @param _lastKillTime the _lastKillTime to set
	 */
	public void setLastKillTime(long _lastKillTime)
	{
		this._lastKillTime = _lastKillTime;
	}
	
}
