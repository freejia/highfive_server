package com.l2jserver.gameserver.communitybbs;

import java.util.StringTokenizer;

import com.l2jserver.Config;
import com.l2jserver.gameserver.communitybbs.Managers.BuffBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.ClanBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.ClassBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.ContactManager;
import com.l2jserver.gameserver.communitybbs.Managers.EnchantBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.PostBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.RegionBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.ServerInfoManager;
import com.l2jserver.gameserver.communitybbs.Managers.ServiceBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.StateBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.TeleportBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.TopBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.TopicBBSManager;
import com.l2jserver.gameserver.datatables.MultiSell;
import com.l2jserver.gameserver.instancemanager.rank_system.rankpvpsystem.RankPvpSystemBBSManager;
import com.l2jserver.gameserver.instancemanager.rank_system.rankpvpsystem.RankPvpSystemConfig;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import com.l2jserver.gameserver.network.serverpackets.ShowBoard;

public class CommunityBoard
{
	protected CommunityBoard()
	{
	}
	
	public static CommunityBoard getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void handleCommands(L2GameClient client, String command)
	{
		L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.inObserverMode())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isAlikeDead())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInSiege())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInsideZone(ZoneId.PVP))
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInCombat())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isDead())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isCastingNow())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isAttackingNow())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInJail())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isFlying())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInDuel())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		
		switch (Config.COMMUNITY_TYPE)
		{
			default:
			case 0:
				activeChar.sendPacket(SystemMessageId.CB_OFFLINE);
				break;
			case 2:
				if (command.startsWith("_bbsclan"))
				{
					ClanBBSManager.getInstance().parsecmd(command, activeChar);
				}
				// second table in cb (server info)
				else if (command.startsWith("_bbsgetfav"))
				{
					ServerInfoManager.getInstance().parsecmd(command, activeChar);
				}
				// third table in cb (contact info)
				else if (command.startsWith("_bbslink"))
				{
					ContactManager.getInstance().parsecmd(command, activeChar);
				}
				else if (command.startsWith("_bbsmemo"))
				{
					TopicBBSManager.getInstance().parsecmd(command, activeChar);
				}
				else if (command.startsWith("_bbstopics"))
				{
					TopicBBSManager.getInstance().parsecmd(command, activeChar);
				}
				else if (command.startsWith("_bbsposts"))
				{
					PostBBSManager.getInstance().parsecmd(command, activeChar);
				}
				else if (command.startsWith("_bbstop"))
				{
					TopBBSManager.getInstance().parsecmd(command, activeChar);
				}
				else if (command.startsWith("_bbshome"))
				{
					TopBBSManager.getInstance().parsecmd(command, activeChar);
				}
				else if (command.startsWith("_bbsloc"))
				{
					RegionBBSManager.getInstance().parsecmd(command, activeChar);
				}
				else if (command.startsWith("_bbsstat;"))
				{
					if (Config.ALLOW_COMMUNITY_STATS)
					{
						StateBBSManager.getInstance().parsecmd(command, activeChar);
					}
					else
					{
						activeChar.sendMessage("You cant see stats!");
						return;
					}
				}
				else if (command.startsWith("_bbsteleport;"))
				{
					if (Config.ALLOW_COMMUNITY_TELEPORT)
					{
						TeleportBBSManager.getInstance().parsecmd(command, activeChar);
					}
					else
					{
						activeChar.sendMessage("You cant use this service!");
						return;
					}
				}
				else if (command.startsWith("_bbs_buff"))
				{
					if (Config.ALLOW_COMMUNITY_BUFF)
					{
						BuffBBSManager.getInstance().parsecmd(command, activeChar);
					}
					else
					{
						activeChar.sendMessage("You cant use this service!");
						return;
					}
				}
				else if (command.startsWith("_bbsservice"))
				{
					if (Config.ALLOW_COMMUNITY_SERVICES)
					{
						ServiceBBSManager.getInstance().parsecmd(command, activeChar);
					}
					else
					{
						activeChar.sendMessage("You cant use this service!");
						return;
					}
				}
				else if (command.startsWith("_bbsechant"))
				{
					if (Config.ALLOW_COMMUNITY_ENCHANT)
					{
						EnchantBBSManager.getInstance().parsecmd(command, activeChar);
					}
					else
					{
						activeChar.sendMessage("You cant use this service!");
						return;
					}
				}
				else if (command.startsWith("_bbsclass"))
				{
					if (Config.ALLOW_COMMUNITY_CLASS)
					{
						ClassBBSManager.getInstance().parsecmd(command, activeChar);
					}
					else
					{
						activeChar.sendMessage("You cant use this service!");
						return;
					}
				}
				else if (command.startsWith("_bbsmultisell;"))
				{
					if (Config.ALLOW_COMMUNITY_MULTISELL)
					{
						if (activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isInSiege() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isInJail() || activeChar.isFlying() || (activeChar.getKarma() > 0) || activeChar.isInDuel())
						{
							activeChar.sendMessage("You cant use this service!");
							return;
						}
						StringTokenizer st = new StringTokenizer(command, ";");
						st.nextToken();
						TopBBSManager.getInstance().parsecmd("_bbstop;" + st.nextToken(), activeChar);
						int multisell = Integer.parseInt(st.nextToken());
						MultiSell.getInstance().separateAndSend(multisell, activeChar, null, false);
					}
					else
					{
						activeChar.sendMessage("You cant use this service");
						return;
					}
				}
				else if (command.startsWith("_bbsAugment;add"))
				{
					if (Config.ALLOW_COMMUNITY_MULTISELL)
					{
						TopBBSManager.getInstance().parsecmd(command, activeChar);
						activeChar.sendPacket(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED);
						activeChar.sendPacket(new ExShowVariationMakeWindow());
						activeChar.cancelActiveTrade();
						TopBBSManager.getInstance().parsecmd(command, activeChar);
						return;
					}
					activeChar.sendMessage("You cant use this service!");
					return;
				}
				else if (command.startsWith("_bbsAugment;remove"))
				{
					if (Config.ALLOW_COMMUNITY_MULTISELL)
					{
						TopBBSManager.getInstance().parsecmd(command, activeChar);
						activeChar.sendPacket(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
						activeChar.sendPacket(new ExShowVariationCancelWindow());
						activeChar.cancelActiveTrade();
						TopBBSManager.getInstance().parsecmd(command, activeChar);
						return;
					}
					activeChar.sendMessage("You cant use this service!");
					return;
				}
				else if (command.startsWith("bbs_add_fav"))
				{
					activeChar.sendMessage("Contact l2jps developers for missing text");
				}
				else if (command.startsWith("_bbsrps") && RankPvpSystemConfig.RANK_PVP_SYSTEM_ENABLED && RankPvpSystemConfig.COMMUNITY_BOARD_TOP_LIST_ENABLED)
				{ // to Rank PvP System by Masterio
					RankPvpSystemBBSManager.getInstance().parsecmd(command, activeChar);
				}
				else
				{
					ShowBoard sb = new ShowBoard("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", "101");
					activeChar.sendPacket(sb);
					activeChar.sendPacket(new ShowBoard(null, "102"));
					activeChar.sendPacket(new ShowBoard(null, "103"));
				}
				break;
		}
	}
	
	public void handleWriteCommands(L2GameClient client, String url, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		switch (Config.COMMUNITY_TYPE)
		{
			case 2:
				if (url.equals("Topic"))
				{
					TopicBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
				}
				else if (url.equals("Post"))
				{
					PostBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
				}
				else if (url.equals("Region"))
				{
					RegionBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
				}
				else if (url.equals("Notice"))
				{
					ClanBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
				}
				else
				{
					ShowBoard sb = new ShowBoard("<html><body><br><br><center>the command: " + url + " is not implemented yet</center><br><br></body></html>", "101");
					activeChar.sendPacket(sb);
					activeChar.sendPacket(new ShowBoard(null, "102"));
					activeChar.sendPacket(new ShowBoard(null, "103"));
				}
				break;
			default:
			case 0:
				ShowBoard sb = new ShowBoard("<html><body><br><br><center>The Community board is currently disabled</center><br><br></body></html>", "101");
				activeChar.sendPacket(sb);
				activeChar.sendPacket(new ShowBoard(null, "102"));
				activeChar.sendPacket(new ShowBoard(null, "103"));
				break;
		}
	}
	
	private static class SingletonHolder
	{
		protected static final CommunityBoard _instance = new CommunityBoard();
	}
}