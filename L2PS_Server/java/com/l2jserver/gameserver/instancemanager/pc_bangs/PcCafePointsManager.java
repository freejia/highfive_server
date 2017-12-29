package com.l2jserver.gameserver.instancemanager.pc_bangs;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Object.InstanceType;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExPCCafePointInfo;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.util.Rnd;

public class PcCafePointsManager
{
	private static PcCafePointsManager _instance;
	
	public static PcCafePointsManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new PcCafePointsManager();
		}
		return _instance;
	}
	
	public PcCafePointsManager()
	{
	}
	
	public void givePcCafePoint(final L2PcInstance player, final long givedexp)
	{
		if (!Config.PC_BANG_ENABLED)
		{
			return;
		}
		
		if (player.isInsideZone(ZoneId.PEACE) || player.isInsideZone(ZoneId.PVP) || player.isInsideZone(ZoneId.SIEGE) || (player.isOnlineInt() == 0) || player.isInJail())
		{
			return;
		}
		
		if (player.getPcBangPoints() >= Config.MAX_PC_BANG_POINTS)
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_MAXMIMUM_ACCUMULATION_ALLOWED_OF_PC_CAFE_POINTS_HAS_BEEN_EXCEEDED);
			player.sendPacket(sm);
			return;
		}
		int _points = (int) (givedexp * 0.0001 * Config.PC_BANG_POINT_RATE);
		if ((player.getActiveClass() == ClassId.archmage.getId()) || (player.getActiveClass() == ClassId.soultaker.getId()) || (player.getActiveClass() == ClassId.stormScreamer.getId()) || (player.getActiveClass() == ClassId.mysticMuse.getId()))
		{
			_points /= Config.MAGE_LEES_THAN_FIGHTER;
		}
		L2Object targetmob = player.getTarget();
		L2Character target = (L2Character) targetmob;
		if ((target instanceof L2GrandBossInstance) || (target instanceof L2RaidBossInstance))
		{
			if ((target.getInstanceType() == InstanceType.L2GrandBossInstance) || target.isRaid())
			{
				_points = (int) (givedexp * 0.0001 * Config.PC_BANG_POINT_RATE_BOSS);
			}
		}
		if (target instanceof L2MonsterInstance)
		{
			if (target.isEasyChampion())
			{
				_points = (int) (givedexp * 0.0001 * Config.PC_BANG_POINT_RATE_EASY_CHAMPION);
			}
		}
		if (target instanceof L2MonsterInstance)
		{
			if (target.isHardChampion())
			{
				_points = (int) (givedexp * 0.0001 * Config.PC_BANG_POINT_RATE_HARD_CHAMIPON);
			}
		}
		if (Config.RANDOM_PC_BANG_POINT)
		{
			_points = Rnd.get(_points / 2, _points);
		}
		
		boolean doublepoint = false;
		SystemMessage sm = null;
		if (_points > 0)
		{
			if (Config.ENABLE_DOUBLE_PC_BANG_POINTS && (Rnd.get(100) < Config.DOUBLE_PC_BANG_POINTS_CHANCE))
			{
				_points *= 2;
				sm = SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT_DOUBLE);
				doublepoint = true;
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_PC_CAFE_POINTS);
			}
			if ((player.getPcBangPoints() + _points) > Config.MAX_PC_BANG_POINTS)
			{
				_points = Config.MAX_PC_BANG_POINTS - player.getPcBangPoints();
			}
			sm.addNumber(_points);
			player.sendPacket(sm);
			player.setPcBangPoints(player.getPcBangPoints() + _points);
			player.sendPacket(new ExPCCafePointInfo(player.getPcBangPoints(), _points, true, doublepoint, 1));
		}
	}
}