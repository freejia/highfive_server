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
import javolution.util.FastMap;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.ZoneId;

/**
 * Special zone checker for High Five Chronicle UNSTABLE.
 * @author Masterio
 */
public class RankPvpSystemZoneChecker
{
	/**
	 * Returns true if character is in allowed zone.
	 * @param activeChar
	 * @return
	 */
	public static final boolean isInPvpAllowedZone(L2PcInstance activeChar)
	{
		if (RankPvpSystemConfig.ALLOWED_ZONES_IDS.size() == 0)
		{
			return true;
		}
		for (FastList.Node<Integer> n = RankPvpSystemConfig.ALLOWED_ZONES_IDS.head(), end = RankPvpSystemConfig.ALLOWED_ZONES_IDS.tail(); (n = n.getNext()) != end;)
		{
			ZoneId zone = RankPvpSystemZoneChecker.getZoneId(n.getValue());
			if ((zone != null) && activeChar.isInsideZone(zone))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if character is in restricted zone.
	 * @param activeChar
	 * @return
	 */
	public static final boolean isInPvpRestrictedZone(L2PcInstance activeChar)
	{
		for (FastList.Node<Integer> n = RankPvpSystemConfig.RESTRICTED_ZONES_IDS.head(), end = RankPvpSystemConfig.RESTRICTED_ZONES_IDS.tail(); (n = n.getNext()) != end;)
		{
			ZoneId zone = RankPvpSystemZoneChecker.getZoneId(n.getValue());
			
			if ((zone != null) && activeChar.isInsideZone(zone))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if character is in restricted zone for death manager.
	 * @param activeChar
	 * @return
	 */
	public static final boolean isInDMRestrictedZone(L2PcInstance activeChar)
	{
		for (FastList.Node<Integer> n = RankPvpSystemConfig.DEATH_MANAGER_RESTRICTED_ZONES_IDS.head(), end = RankPvpSystemConfig.DEATH_MANAGER_RESTRICTED_ZONES_IDS.tail(); (n = n.getNext()) != end;)
		{
			ZoneId zone = RankPvpSystemZoneChecker.getZoneId(n.getValue());
			
			if ((zone != null) && activeChar.isInsideZone(zone))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns 1.0 if character is not in Bonus Ratio zone, otherwise returns ratio from configuration file.
	 * @param player
	 * @return
	 */
	public static final double getZoneBonusRatio(L2PcInstance player)
	{
		for (FastMap.Entry<Integer, Double> e = RankPvpSystemConfig.RANK_POINTS_BONUS_ZONES_IDS.head(), end = RankPvpSystemConfig.RANK_POINTS_BONUS_ZONES_IDS.tail(); (e = e.getNext()) != end;)
		{
			ZoneId zone = RankPvpSystemZoneChecker.getZoneId(e.getKey());
			
			if ((zone != null) && player.isInsideZone(zone))
			{
				return e.getValue();
			}
		}
		return 1.0;
	}
	
	public static final ZoneId getZoneId(int zoneId)
	{
		ZoneId zone = null;
		
		switch (zoneId)
		{
			case 0:
				return ZoneId.PVP;
			case 1:
				return ZoneId.PEACE;
			case 2:
				return ZoneId.SIEGE;
			case 3:
				return ZoneId.MOTHER_TREE;
			case 4:
				return ZoneId.CLAN_HALL;
			case 5:
				return ZoneId.LANDING;
			case 6:
				return ZoneId.NO_LANDING;
			case 7:
				return ZoneId.WATER;
			case 8:
				return ZoneId.JAIL;
			case 9:
				return ZoneId.MONSTER_TRACK;
			case 10:
				return ZoneId.CASTLE;
			case 11:
				return ZoneId.SWAMP;
			case 12:
				return ZoneId.NO_SUMMON_FRIEND;
			case 13:
				return ZoneId.FORT;
			case 14:
				return ZoneId.NO_STORE;
			case 15:
				return ZoneId.TOWN;
			case 16:
				return ZoneId.SCRIPT;
			case 17:
				return ZoneId.HQ;
			case 18:
				return ZoneId.DANGER_AREA;
			case 19:
				return ZoneId.ALTERED;
			case 20:
				return ZoneId.NO_BOOKMARK;
			case 21:
				return ZoneId.NO_ITEM_DROP;
			case 22:
				return ZoneId.NO_RESTART;
		}
		
		return zone;
	}
}
