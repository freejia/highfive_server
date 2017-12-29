/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.L2CharPosition;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.position.PcPosition;
import com.l2jserver.gameserver.model.skills.L2Skill;
import com.l2jserver.gameserver.model.skills.L2SkillType;
import com.l2jserver.gameserver.model.skills.targets.L2TargetType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;

public final class RequestMagicSkillUse extends L2GameClientPacket
{
	private static final String _C__39_REQUESTMAGICSKILLUSE = "[C] 39 RequestMagicSkillUse";
	
	private int _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	protected void readImpl()
	{
		_magicId = readD(); // Identifier of the used skill
		_ctrlPressed = readD() != 0; // True if it's a ForceAttack : Ctrl pressed
		_shiftPressed = readC() != 0; // True if Shift pressed
	}
	
	@Override
	protected void runImpl()
	{
		// Get the current L2PcInstance of the player
		final L2PcInstance activeChar = getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// Get the level of the used skill
		L2Skill skill = activeChar.getKnownSkill(_magicId);
		if (skill == null)
		{
			// Player doesn't know this skill, maybe it's the display Id.
			skill = activeChar.getCustomSkill(_magicId);
			if (skill == null)
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				_log.warning("Skill Id " + _magicId + " not found in player!");
				return;
			}
		}
		
		// Avoid Use of Skills in AirShip.
		if (activeChar.isPlayable() && activeChar.isInAirShip())
		{
			activeChar.sendPacket(SystemMessageId.ACTION_PROHIBITED_WHILE_MOUNTED_OR_ON_AN_AIRSHIP);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if ((activeChar.isTransformed() || activeChar.isInStance()) && !activeChar.containsAllowedTransformSkill(skill.getId()))
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isDebug())
		{
			_log.fine("Skill:" + skill.getName() + " level:" + skill.getLevel() + " passive:" + skill.isPassive());
			_log.fine("Range:" + skill.getCastRange() + " targettype:" + skill.getTargetType() + " power:" + skill.getPower());
			_log.fine("Reusedelay:" + skill.getReuseDelay() + " hittime:" + skill.getHitTime());
		}
		
		// If Alternate rule Karma punishment is set to true, forbid skill Return to player with Karma
		if ((skill.getSkillType() == L2SkillType.RECALL) && !Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && (activeChar.getKarma() > 0))
		{
			return;
		}
		
		// players mounted on pets cannot use any toggle skills
		if (skill.isToggle() && activeChar.isMounted())
		{
			return;
		}
		
		activeChar.useMagic(skill, _ctrlPressed, _shiftPressed);
		
		// Stop if use self-buff (except if on AirShip or Boat).
		if (((skill.getSkillType() == L2SkillType.BUFF) && (skill.getTargetType() == L2TargetType.SELF)) && (!activeChar.isInAirShip() || !activeChar.isInBoat()))
		{
			final PcPosition charPos = activeChar.getPosition();
			final L2CharPosition stopPos = new L2CharPosition(charPos.getX(), charPos.getY(), charPos.getZ(), charPos.getHeading());
			activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, stopPos);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__39_REQUESTMAGICSKILLUSE;
	}
}
