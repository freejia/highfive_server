package l2f.gameserver.handler.admincommands.impl;

import l2f.gameserver.handler.admincommands.IAdminCommandHandler;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObject;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

@SuppressWarnings("unused")
public class AdminRes implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_res
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().Res)
			return false;

		if (fullString.startsWith("admin_res "))
			handleRes(activeChar, wordList[1]);
		if (fullString.equals("admin_res"))
			handleRes(activeChar);

		return true;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}

	private void handleRes(Player activeChar)
	{
		handleRes(activeChar, null);
	}

	private void handleRes(Player activeChar, String player)
	{
		GameObject obj = activeChar.getTarget();
		if (player != null)
		{
			Player plyr = World.getPlayer(player);
			if (plyr != null)
				obj = plyr;
			else
				try
			{
					int radius = Math.max(Integer.parseInt(player), 100);
					for (Creature character : activeChar.getAroundCharacters(radius, radius))
						handleRes(character);
					activeChar.sendMessage("Resurrected within " + radius + " unit radius.");
					return;
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Enter valid player name or radius");
				return;
			}
		}

		if (obj == null)
			obj = activeChar;

		if (obj instanceof Creature)
			handleRes((Creature) obj);
		else
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
	}

	private void handleRes(Creature target)
	{
		if (!target.isDead())
			return;

		if (target.isPlayable())
		{
			if (target.isPlayer())
				((Player)target).doRevive(100.);
			else
				((Playable)target).doRevive();
		}
		else if (target.isNpc())
			((NpcInstance)target).stopDecay();

		target.setCurrentHpMp(target.getMaxHp(), target.getMaxMp(), true);
		target.setCurrentCp(target.getMaxCp());
	}
}