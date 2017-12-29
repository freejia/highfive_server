/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.l2jserver.gameserver.instancemanager.rank_system.rankpvpsystem;

import com.l2jserver.gameserver.communitybbs.Managers.BaseBBSManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ShowBoard;

public class RankPvpSystemBBSManager extends BaseBBSManager
{
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("_bbsrps:") && RankPvpSystemConfig.COMMUNITY_BOARD_TOP_LIST_ENABLED)
		{
			int page = 0;
			try
			{
				page = Integer.parseInt(command.split(":", 2)[1].trim());
			}
			catch (Exception e)
			{
				e.printStackTrace();
				page = 0;
			}
			
			separateAndSend(RankPvpSystemBBSHtm.prepareHtmResponse(activeChar, page), activeChar);
		}
		else
		{
			ShowBoard sb = null;
			if (command.startsWith("_bbsrps:") && !RankPvpSystemConfig.COMMUNITY_BOARD_TOP_LIST_ENABLED)
			{
				sb = new ShowBoard("<html><body><br><br><center>Community Board Top List is disabled in config file</center><br><br></body></html>", "101");
			}
			else
			{
				sb = new ShowBoard("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", "101");
			}
			
			activeChar.sendPacket(sb);
			sb = null;
			activeChar.sendPacket(new ShowBoard(null, "102"));
			activeChar.sendPacket(new ShowBoard(null, "103"));
		}
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		//
	}
	
	private static RankPvpSystemBBSManager _instance = new RankPvpSystemBBSManager();
	
	/**
	 * @return
	 */
	public static RankPvpSystemBBSManager getInstance()
	{
		return _instance;
	}
}
