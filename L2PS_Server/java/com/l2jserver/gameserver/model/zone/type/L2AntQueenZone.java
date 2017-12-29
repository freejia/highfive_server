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
 * this program. If not, see <http://L2J.EternityWorld.ru/>.
 */
package com.l2jserver.gameserver.model.zone.type;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * Author: RobikBobik L2PS Team
 */
public class L2AntQueenZone extends L2ZoneType
{
	public L2AntQueenZone(final int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(final L2Character character)
	{
		if ((character.isPlayer()) && (character.getLevel() >= Config.MAX_LEVEL_FOR_AQ_ZONE) && (!character.isGM()))
		{
			character.setInsideZone(ZoneId.ALTERED, true);
			_log.info("Player " + character.getName() + " is trying to get access to Ant Queen zone with hight level than " + Config.MAX_LEVEL_FOR_AQ_ZONE + ".");
			character.teleToLocation(47591, 185820, -3486);
			character.sendPacket(new ExShowScreenMessage("You cannot to enter, if you have " + Config.MAX_LEVEL_FOR_AQ_ZONE + " level or higher", 5000));
		}
		else if (character.isServitor() && (character.getLevel() >= Config.MAX_LEVEL_FOR_AQ_ZONE) && (!character.isGM()))
		{
			character.setInsideZone(ZoneId.ALTERED, true);
			_log.info("Player " + character.getName() + " is trying to get access to Ant Queen zone with hight level summon than " + Config.MAX_LEVEL_FOR_AQ_ZONE + ".");
			character.teleToLocation(47591, 185820, -3486);
			character.sendPacket(new ExShowScreenMessage("You cannot to enter, if you have " + Config.MAX_LEVEL_FOR_AQ_ZONE + " level or higher", 5000));
		}
		else if (character.isPet() && (character.getLevel() >= Config.MAX_LEVEL_FOR_AQ_ZONE) && (!character.isGM()))
		{
			character.setInsideZone(ZoneId.ALTERED, true);
			_log.info("Player " + character.getName() + " is trying to get access to Ant Queen zone with hight level summon than " + Config.MAX_LEVEL_FOR_AQ_ZONE + ".");
			character.teleToLocation(47591, 185820, -3486);
			character.sendPacket(new ExShowScreenMessage("You cannot to enter, if you have " + Config.MAX_LEVEL_FOR_AQ_ZONE + " level or higher", 5000));
		}
	}
	
	@Override
	protected void onExit(final L2Character character)
	{
		if (character.isPlayer() && (character.getLevel() >= Config.MAX_LEVEL_FOR_AQ_ZONE) && (!character.isGM()))
		{
			character.setInsideZone(ZoneId.ALTERED, false);
			_log.info("Player " + character.getName() + " is leaving Ant Queen zone.");
		}
	}
	
	@Override
	public void onDieInside(final L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(final L2Character character)
	{
	}
}
