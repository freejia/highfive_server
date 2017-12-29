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

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Masterio
 */
public class IVoicedCommandHandlerPvpInfo implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"pvpinfo"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		L2PcInstance playerTarget = null;
		
		if (activeChar == null)
		{
			return false;
		}
		
		if ((activeChar.getTarget() != null) && (activeChar.getTarget() instanceof L2PcInstance))
		{
			playerTarget = (L2PcInstance) activeChar.getTarget();
		}
		else
		{
			playerTarget = activeChar;
			activeChar.sendMessage("PvP Status executed on self!");
		}
		
		RankPvpSystemPlayerInfo playerInfo = new RankPvpSystemPlayerInfo();
		
		if (playerTarget != null)
		{
			playerInfo.sendPlayerResponse(activeChar, playerTarget);
		}
		else
		{
			playerInfo.sendPlayerResponse(activeChar, activeChar);
		}
		
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
	
}