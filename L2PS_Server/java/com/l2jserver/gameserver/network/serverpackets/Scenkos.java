package com.l2jserver.gameserver.network.serverpackets;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;

/*
 * Created by RobikBobik L2PS Team
 * + Clean by Millu L2PS Team
 * + Clean from debug Andrey L2PS Team
 */
public final class Scenkos
{
	private static Logger _log = Logger.getLogger(Scenkos.class.getName());
	
	public static void toPlayersTargettingMyself(L2Character character, L2GameServerPacket mov)
	{
		Collection<L2PcInstance> plrs = character.getKnownList().getKnownPlayers().values();
		{
			for (L2PcInstance player : plrs)
			{
				if (player.getTarget() != character)
				{
					continue;
				}
				
				player.sendPacket(mov);
			}
		}
	}
	
	public static void toKnownPlayers(L2Character character, L2GameServerPacket mov)
	{
		Collection<L2PcInstance> plrs = character.getKnownList().getKnownPlayers().values();
		{
			for (L2PcInstance player : plrs)
			{
				if (player == null)
				{
					continue;
				}
				try
				{
					player.sendPacket(mov);
					if ((mov instanceof CharInfo) && (character instanceof L2PcInstance))
					{
						int relation = ((L2PcInstance) character).getRelation(player);
						Integer oldrelation = character.getKnownList().getKnownRelations().get(player.getObjectId());
						if ((oldrelation != null) && (oldrelation != relation))
						{
							player.sendPacket(new RelationChanged((L2PcInstance) character, relation, character.isAutoAttackable(player)));
							if (((L2PcInstance) character).getSummon() != null)
							{
								player.sendPacket(new RelationChanged(((L2PcInstance) character).getSummon(), relation, character.isAutoAttackable(player)));
							}
						}
					}
				}
				catch (NullPointerException e)
				{
					_log.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}
	
	public static void toKnownPlayersInRadius(L2Character character, L2GameServerPacket mov, int radius)
	{
		if (radius < 0)
		{
			radius = 1500;
		}
		
		Collection<L2PcInstance> plrs = character.getKnownList().getKnownPlayers().values();
		{
			for (L2PcInstance player : plrs)
			{
				if (character.isInsideRadius(player, radius, false, false))
				{
					player.sendPacket(mov);
				}
			}
		}
	}
	
	public static void toSelfAndKnownPlayers(L2Character character, L2GameServerPacket mov)
	{
		if (character instanceof L2PcInstance)
		{
			character.sendPacket(mov);
		}
		
		toKnownPlayers(character, mov);
	}
	
	public static void toSelfAndKnownPlayersInRadius(L2Character character, L2GameServerPacket mov, long radiusSq)
	{
		if (radiusSq < 0)
		{
			radiusSq = 360000;
		}
		
		if (character instanceof L2PcInstance)
		{
			character.sendPacket(mov);
		}
		
		Collection<L2PcInstance> plrs = character.getKnownList().getKnownPlayers().values();
		{
			for (L2PcInstance player : plrs)
			{
				if ((player != null) && (character.getDistanceSq(player) <= radiusSq))
				{
					player.sendPacket(mov);
				}
			}
		}
	}
	
	public static void toAllOnlinePlayers(L2GameServerPacket mov)
	{
		
		L2PcInstance[] pls = L2World.getInstance().getAllPlayersArray();
		{
			for (L2PcInstance onlinePlayer : pls)
			{
				if ((onlinePlayer != null) && onlinePlayer.isOnline())
				{
					onlinePlayer.sendPacket(mov);
				}
			}
		}
	}
	
	public static void announceToOnlinePlayers(String text)
	{
		CreatureSay cs = new CreatureSay(0, Say2.ANNOUNCEMENT, "", text);
		toAllOnlinePlayers(cs);
	}
	
	public static void toPlayersInInstance(L2GameServerPacket mov, int instanceId)
	{
		L2PcInstance[] pls = L2World.getInstance().getAllPlayersArray();
		{
			for (L2PcInstance onlinePlayer : pls)
			{
				if ((onlinePlayer != null) && onlinePlayer.isOnline() && (onlinePlayer.getInstanceId() == instanceId))
				{
					onlinePlayer.sendPacket(mov);
				}
			}
		}
	}
}
