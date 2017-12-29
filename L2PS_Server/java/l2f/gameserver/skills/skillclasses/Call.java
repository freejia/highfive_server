package l2f.gameserver.skills.skillclasses;

import l2f.gameserver.cache.Msg;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Zone;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.templates.InstantZone;
import l2f.gameserver.templates.StatsSet;
import l2f.gameserver.utils.Location;

import java.util.List;

import static l2f.gameserver.model.Zone.ZoneType.no_restart;
import static l2f.gameserver.model.Zone.ZoneType.no_summon;

public class Call extends Skill
{
	final boolean _party;

	public Call(StatsSet set)
	{
		super(set);
		_party = set.getBool("party", false);
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if (activeChar.isPlayer())
		{
			if (_party && ((Player) activeChar).getParty() == null)
				
				
				return false;

			SystemMessage2 msg = canSummonHere((Player)activeChar);
			if (msg != null)
			{
				activeChar.sendPacket(msg);
				return false;
			}

			// This check is only for a single target
			if (!_party)
			{
				if (activeChar == target)
					return false;

				msg = canBeSummoned(target);
				if (msg != null)
				{
					activeChar.sendPacket(msg);
					return false;
				}
			}
		}

		return super.checkCondition(activeChar, target, forceUse, dontMove, first);
	}

	@Override
	public void useSkill(Creature activeChar, List<Creature> targets)
	{
		if (!activeChar.isPlayer())
			return;

		SystemMessage2 msg = canSummonHere((Player)activeChar);
		if (msg != null)
		{
			activeChar.sendPacket(msg);
			return;
		}

		if (_party)
		{
			if (((Player) activeChar).getParty() != null)
				for (Player target : ((Player) activeChar).getParty().getPartyMembers())
					if (!target.equals(activeChar) && canBeSummoned(target) == null && !target.isTerritoryFlagEquipped())
					{
						target.stopMove();
						target.teleToLocation(Location.findPointToStay(activeChar, 100, 150), activeChar.getGeoIndex());
						getEffects(activeChar, target, getActivateRate() > 0, false);
					}

			if (isSSPossible())
				activeChar.unChargeShots(isMagic());
			return;
		}

		for (Creature target : targets)
			if (target != null)
			{
				if (canBeSummoned(target) != null)
					continue;

				((Player) target).summonCharacterRequest(activeChar, Location.findAroundPosition(activeChar, 100, 150), getId() == 1403 || getId() == 1404 ? 1 : 0);

				getEffects(activeChar, target, getActivateRate() > 0, false);
			}

		if (isSSPossible())
			activeChar.unChargeShots(isMagic());
	}

	/**
	 * Can a summons at the moment to use the call
	 */
	public static SystemMessage2 canSummonHere(Player activeChar)
	{
		if (activeChar.isAlikeDead() || activeChar.isInOlympiadMode() || activeChar.isInObserverMode() || activeChar.isFlying() || activeChar.isFestivalParticipant() || activeChar.isInFightClub())
			return new SystemMessage2(SystemMsg.NOTHING_HAPPENED);

		if (activeChar.getPlayer().isJailed())
		{
			activeChar.getPlayer().sendMessage("You cannot escape from Jail!");
			return new SystemMessage2(SystemMsg.NOTHING_HAPPENED);
		}

		// "You can not call the characters to / from the free PvP"
		// "In the zone of sediment"
		// "At the Olympiad Stadium"
		// "In the area of certain raid bosses and epic bosses"
		
		if (activeChar.isInZoneBattle() || activeChar.isInZone(Zone.ZoneType.SIEGE) || activeChar.isInZone(no_restart) || activeChar.isInZone(no_summon) || activeChar.isInBoat() || activeChar.getReflection() != ReflectionManager.DEFAULT)
			return new SystemMessage2(SystemMsg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);

		//if(activeChar.isInCombat())
		//return Msg.YOU_CANNOT_SUMMON_DURING_COMBAT;

		if (activeChar.isInStoreMode() || activeChar.isProcessingRequest())
			return new SystemMessage2(SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);

		return null;
	}
	
	 public static SystemMessage canBeSummoned(Creature player, Creature target)
	  {
	    if ((target == null) || (!target.isPlayer()) || (target.getPlayer().isTerritoryFlagEquipped()) || (target.isFlying()) || (target.isInObserverMode()) || (target.getPlayer().isFestivalParticipant()) || (!target.getPlayer().getPlayerAccess().UseTeleport)) {
	      return Msg.INVALID_TARGET;
	    }
	    if (target.isInOlympiadMode()) {
	      return Msg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD;
	    }
	    if ((target.isInZoneBattle()) || (target.isInZone(Zone.ZoneType.SIEGE)) || (target.isInZone(Zone.ZoneType.no_restart)) || (target.isInZone(Zone.ZoneType.no_summon)) || (target.getReflection() != ReflectionManager.DEFAULT) || (target.isInBoat())) {
	      return Msg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING;
	    }

	    if (target.isAlikeDead()) {
	      return new SystemMessage(1844).addString(target.getName());
	    }

		if (target.getPlayer().isInFightClub())
			return Msg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING;

		  if(target.getPlayer().isJailed())
		  {
			  target.getPlayer().sendMessage("You cannot escape from Jail!");
			  return Msg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING;
		  }

	    if ((target.getPvpFlag() != 0) || (target.isInCombat())) {
	      return new SystemMessage(1843).addString(target.getName());
	    }
	    Player pTarget = (Player)target;

	    if ((pTarget.getPrivateStoreType() != 0) || (pTarget.isProcessingRequest())) {
	      return new SystemMessage(1898).addString(target.getName());
	    }
	    if (player.isPlayer())
	    {
	      Player caster = (Player)player;

	      if (caster.getReflection() != ReflectionManager.DEFAULT)
	      {
	        InstantZone iz = player.getReflection().getInstancedZone();
	        if ((iz != null) && (
	          (pTarget.getLevel() < iz.getMinLevel()) || (pTarget.getLevel() > iz.getMaxLevel()))) {
	          return Msg.INVALID_TARGET;
	        }
	      }
	    }
	    return null;
	  }


	/**
	 * Whether the goal is to answer the call
	 */
	public static SystemMessage2 canBeSummoned(Creature target)
	{
		if (target == null || !target.isPlayer() || target.getPlayer().isTerritoryFlagEquipped() || target.isFlying() || target.isInObserverMode() || target.getPlayer().isFestivalParticipant() || !target.getPlayer().getPlayerAccess().UseTeleport)
			return new SystemMessage2(SystemMsg.INVALID_TARGET);

		if (target.isInOlympiadMode())
			return new SystemMessage2(SystemMsg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD);

		if (target.isInZoneBattle() || target.isInZone(Zone.ZoneType.SIEGE) || target.isInZone(no_restart) || target.isInZone(no_summon) || target.getReflection() != ReflectionManager.DEFAULT || target.isInBoat())
			return new SystemMessage2(SystemMsg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING);

		// You can not summon the dead characters
		if (target.isAlikeDead())
			return new SystemMessage2(SystemMsg.S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED).addString(target.getName());

		// You can not call the characters that are in the PvP mode or Combat Mode
		if (target.getPvpFlag() != 0 || target.isInCombat())
			return new SystemMessage2(SystemMsg.S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED).addString(target.getName());

		Player pTarget = (Player) target;

		// You can not call selling characters
		if (pTarget.getPrivateStoreType() != Player.STORE_PRIVATE_NONE || pTarget.isProcessingRequest())
			return new SystemMessage2(SystemMsg.C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED).addString(target.getName());

		return null;
	}
}