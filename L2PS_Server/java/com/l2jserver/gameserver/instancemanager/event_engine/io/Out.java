package com.l2jserver.gameserver.instancemanager.event_engine.io;

import java.sql.Connection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javolution.util.FastList;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.handler.AdminCommandHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.handler.ISkillHandler;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.handler.SkillHandler;
import com.l2jserver.gameserver.handler.VoicedCommandHandler;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.event_engine.Configuration;
import com.l2jserver.gameserver.instancemanager.event_engine.ManagerNpc;
import com.l2jserver.gameserver.instancemanager.event_engine.container.EventContainer;
import com.l2jserver.gameserver.instancemanager.event_engine.container.PlayerContainer;
import com.l2jserver.gameserver.instancemanager.event_engine.function.Vote;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventPlayer;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.effects.AbnormalEffect;
import com.l2jserver.gameserver.model.items.type.L2EtcItemType;
import com.l2jserver.gameserver.model.skills.L2Skill;
import com.l2jserver.gameserver.model.skills.L2SkillType;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.util.Broadcast;
import com.l2jserver.util.Rnd;

public class Out
{
	protected static class BombHandler implements ISkillHandler
	{
		private final L2SkillType[] SKILL_IDS =
		{
			L2SkillType.BOMB
		};
		
		@Override
		public L2SkillType[] getSkillIds()
		{
			return SKILL_IDS;
		}
		
		@Override
		public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
		{
			if (!(activeChar instanceof L2PcInstance))
			{
				return;
			}
			
			PlayerContainer.getInstance().getPlayer(activeChar.getObjectId()).getEvent().dropBomb(PlayerContainer.getInstance().getPlayer(((L2PcInstance) activeChar).getObjectId()));
			
		}
	}
	
	protected static class CaptureHandler implements ISkillHandler
	{
		private final L2SkillType[] SKILL_IDS =
		{
			L2SkillType.CAPTURE
		};
		
		@Override
		public L2SkillType[] getSkillIds()
		{
			return SKILL_IDS;
		}
		
		@Override
		public void useSkill(L2Character activeChar, L2Skill skill, L2Object[] targets)
		{
			if (!(activeChar instanceof L2PcInstance))
			{
				return;
			}
			
			if (!(targets[0] instanceof L2NpcInstance))
			{
				return;
			}
			
			L2PcInstance player = (L2PcInstance) activeChar;
			L2NpcInstance target = (L2NpcInstance) targets[0];
			PlayerContainer.getInstance().getPlayer(activeChar.getObjectId()).getEvent().useCapture(PlayerContainer.getInstance().getPlayer(player.getObjectId()), target.getObjectId());
		}
	}
	
	protected static class ReloadHandler implements IAdminCommandHandler
	{
		private final String[] ADMIN_COMMANDS =
		{
			"admin_reload_event_Configuration"
		};
		
		@Override
		public String[] getAdminCommandList()
		{
			return ADMIN_COMMANDS;
		}
		
		@Override
		public boolean useAdminCommand(String command, L2PcInstance activeChar)
		{
			if (command.startsWith("admin_reload_event_Configuration"))
			{
				Configuration.getInstance().load();
			}
			
			return true;
		}
	}
	
	protected static class KickHandler implements IAdminCommandHandler
	{
		private final String[] ADMIN_COMMANDS =
		{
			"admin_eventkick"
		};
		
		@Override
		public String[] getAdminCommandList()
		{
			return ADMIN_COMMANDS;
		}
		
		@Override
		public boolean useAdminCommand(String command, L2PcInstance activeChar)
		{
			if (command.startsWith("admin_eventkick "))
			{
				EventPlayer p = PlayerContainer.getInstance().getPlayerByName(command.substring(16));
				if (p != null)
				{
					p.getEvent().onLogout(p);
				}
			}
			
			return true;
		}
	}
	
	protected static class CreateEventHandler implements IAdminCommandHandler
	{
		private final String[] ADMIN_COMMANDS =
		{
			"admin_createevent"
		};
		
		@Override
		public String[] getAdminCommandList()
		{
			return ADMIN_COMMANDS;
		}
		
		@Override
		public boolean useAdminCommand(String command, L2PcInstance activeChar)
		{
			if (command.startsWith("admin_createevent "))
			{
				EventContainer.getInstance().createEvent(Integer.parseInt(command.substring(18)));
			}
			
			return true;
		}
	}
	
	protected static class VoicedHandler implements IVoicedCommandHandler
	{
		private static final String[] _voicedCommands =
		{
			"event",
			"popup"
		};
		
		@Override
		public String[] getVoicedCommandList()
		{
			return _voicedCommands;
		}
		
		@Override
		public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
		{
			if (command.equalsIgnoreCase("event"))
			{
				ManagerNpc.getInstance().showMain(activeChar.getObjectId());
			}
			if (command.equalsIgnoreCase("popup"))
			{
				if (Configuration.getInstance().getBoolean(0, "voteEnabled"))
				{
					Vote.getInstance().switchPopup(activeChar.getObjectId());
				}
			}
			return true;
		}
	}
	
	public static void broadcastCreatureSay(String message)
	{
		Broadcast.toAllOnlinePlayers(new CreatureSay(0, 18, "", message));
	}
	
	public static void closeConnection(Connection con)
	{
		L2DatabaseFactory.close(con);
	}
	
	public static void createInstance(int id)
	{
		InstanceManager.getInstance().createInstance(id);
	}
	
	public static void createParty2(FastList<EventPlayer> players)
	{
		L2Party party = null;
		party = new L2Party(players.get(0).getOwner(), 1);
		
		for (EventPlayer player : players.subList(1, players.size()))
		{
			player.joinParty(party);
		}
	}
	
	public static int getClassIndex(int player)
	{
		return getPlayerById(player).getClassIndex();
	}
	
	public static Connection getConnection()
	{
		try
		{
			return L2DatabaseFactory.getInstance().getConnection();
		}
		catch (Exception e)
		{
			System.out.println("getconnection error");
			return null;
		}
	}
	
	public static L2PcInstance getPlayerById(int id)
	{
		return L2World.getInstance().getPlayer(id);
	}
	
	public static String getSkillName(int skill)
	{
		return SkillTable.getInstance().getInfo(skill, 1).getName();
	}
	
	public static void html(Integer player, String html)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage(0);
		msg.setHtml(html);
		getPlayerById(player).sendPacket(msg);
	}
	
	public static boolean isPotion(int item)
	{
		if (ItemTable.getInstance().getTemplate(item).getItemType() == L2EtcItemType.POTION)
		{
			return true;
		}
		return false;
	}
	
	public static boolean isRestrictedSkill(int skill)
	{
		if (SkillTable.getInstance().getInfo(skill, 1).getSkillType() == L2SkillType.RESURRECT)
		{
			return true;
		}
		
		if (SkillTable.getInstance().getInfo(skill, 1).getSkillType() == L2SkillType.RECALL)
		{
			return true;
		}
		
		if (SkillTable.getInstance().getInfo(skill, 1).getSkillType() == L2SkillType.SUMMON_FRIEND)
		{
			return true;
		}
		
		if (SkillTable.getInstance().getInfo(skill, 1).getSkillType() == L2SkillType.FAKE_DEATH)
		{
			return true;
		}
		
		return false;
	}
	
	public static boolean isScroll(int item)
	{
		if (ItemTable.getInstance().getTemplate(item).getItemType() == L2EtcItemType.SCROLL)
		{
			return true;
		}
		return false;
		
	}
	
	public static int random(int max)
	{
		return Rnd.get(max);
	}
	
	public static void registerHandlers()
	{
		SkillHandler.getInstance().registerHandler(new BombHandler());
		SkillHandler.getInstance().registerHandler(new CaptureHandler());
		
		AdminCommandHandler.getInstance().registerHandler(new ReloadHandler());
		AdminCommandHandler.getInstance().registerHandler(new KickHandler());
		AdminCommandHandler.getInstance().registerHandler(new CreateEventHandler());
		VoicedCommandHandler.getInstance().registerHandler(new VoicedHandler());
	}
	
	public static void sendMessage(int player, String message)
	{
		getPlayerById(player).sendMessage(message);
	}
	
	public static void setPvPInstance(int id)
	{
		InstanceManager.getInstance().getInstance(id).setPvPInstance(true);
	}
	
	public static void startFlameEffect(Integer npc)
	{
		((L2Npc) L2World.getInstance().findObject(npc)).startAbnormalEffect(AbnormalEffect.FLAME);
	}
	
	public static void tpmPurge()
	{
		ThreadPoolManager.getInstance().purge();
	}
	
	public static ScheduledFuture<?> tpmScheduleGeneral(Runnable task, int time)
	{
		return ThreadPoolManager.getInstance().scheduleGeneral(task, time);
	}
	
	public static void tpmScheduleGeneralAtFixedRate(Runnable task, int first, int delay)
	{
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(task, first, delay);
	}
	
	public static Collection<Integer> getEveryPlayer()
	{
		List<Integer> l = new LinkedList<>();
		for (Integer p : L2World.getInstance().getAllPlayers().keys())
		{
			l.add(p);
		}
		return l;
	}
}
