package com.l2jserver.gameserver.instancemanager.event_engine.events;

import com.l2jserver.gameserver.instancemanager.event_engine.AbstractEvent;
import com.l2jserver.gameserver.instancemanager.event_engine.Configuration;
import com.l2jserver.gameserver.instancemanager.event_engine.container.NpcContainer;
import com.l2jserver.gameserver.instancemanager.event_engine.io.Out;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventNpc;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventPlayer;
import com.l2jserver.gameserver.instancemanager.event_engine.model.TeamEventStatus;

public class CTF extends AbstractEvent
{
	public static boolean enabled = true;
	
	private class Core implements Runnable
	{
		
		@Override
		public void run()
		{
			try
			{
				switch (eventState)
				{
					case START:
						divideIntoTeams(2);
						teleportToTeamPos();
						preparePlayers();
						createPartyOfTeam(1);
						createPartyOfTeam(2);
						forceSitAll();
						spawnFlagsAndHolders();
						setStatus(EventState.FIGHT);
						schedule(10000);
						break;
					
					case FIGHT:
						forceStandAll();
						setStatus(EventState.END);
						
						clock.start();
						
						break;
					
					case END:
						clock.stop();
						if (winnerTeam == 0)
						{
							winnerTeam = getWinnerTeam();
						}
						unspawnFlagsAndHolders();
						if (playerWithRedFlag != null)
						{
							unequipFlag(playerWithRedFlag);
						}
						if (playerWithBlueFlag != null)
						{
							unequipFlag(playerWithBlueFlag);
						}
						giveReward(getPlayersOfTeam(winnerTeam));
						setStatus(EventState.INACTIVE);
						announce("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " kills!");
						eventEnded();
						break;
					default:
						break;
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				announce("Error! Event ended.");
				eventEnded();
			}
		}
	}
	
	public enum EventState
	{
		START,
		FIGHT,
		END,
		TELEPORT,
		INACTIVE
	}
	
	public EventState eventState;
	
	public Core task;
	
	public EventNpc redFlagNpc;
	public EventNpc blueFlagNpc;
	public EventNpc redHolderNpc;
	public EventNpc blueHolderNpc;
	public int redFlagStatus;
	public int blueFlagStatus;
	public EventPlayer playerWithRedFlag;
	public EventPlayer playerWithBlueFlag;
	
	@SuppressWarnings("synthetic-access")
	public CTF(Integer containerId)
	{
		super(containerId);
		eventId = 10;
		createNewTeam(1, "Blue", Configuration.getInstance().getColor(getId(), "Blue"), Configuration.getInstance().getPosition(getId(), "Blue", 1));
		createNewTeam(2, "Red", Configuration.getInstance().getColor(getId(), "Red"), Configuration.getInstance().getPosition(getId(), "Red", 1));
		task = new Core();
		winnerTeam = 0;
		playerWithRedFlag = null;
		playerWithBlueFlag = null;
		blueFlagStatus = 0;
		redFlagStatus = 0;
		clock = new EventClock(Configuration.getInstance().getInt(getId(), "matchTime"));
		
	}
	
	@Override
	public void endEvent()
	{
		winnerTeam = players.head().getNext().getValue().getMainTeam();
		
		setStatus(EventState.END);
		clock.stop();
		
	}
	
	private void equipFlag(EventPlayer player, int flag)
	{
		player.unequipWeapon();
		player.equipNewItem(6718);
		
		switch (flag)
		{
			case 1:
				playerWithBlueFlag = player;
				announce(getPlayerList(), player.getName() + " took the Blue flag!");
				blueFlagNpc.unspawn();
				break;
			case 2:
				playerWithRedFlag = player;
				announce(getPlayerList(), player.getName() + " took the Red flag!");
				redFlagNpc.unspawn();
				break;
			default:
				break;
		}
		player.broadcastUserInfo();
	}
	
	@Override
	public String getScorebar()
	{
		return "" + teams.get(1).getName() + ": " + teams.get(1).getScore() + "  " + teams.get(2).getName() + ": " + teams.get(2).getScore() + "  Time: " + clock.getTimeInString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.eventengine.AbstractEvent#onClockZero()
	 */
	@Override
	public void onClockZero()
	{
		setStatus(EventState.END);
		schedule(1);
	}
	
	@Override
	public void onDie(EventPlayer victim, EventPlayer killer)
	{
		super.onDie(victim, killer);
		if ((playerWithRedFlag != null) && playerWithRedFlag.equals(victim))
		{
			announce(getPlayerList(), victim.getName() + " dropped the Red flag!");
			redFlagStatus = 2;
			unequipFlag(victim);
			redFlagNpc = NpcContainer.getInstance().createNpc(victim.getOwnerLoc().getX(), victim.getOwnerLoc().getY(), victim.getOwnerLoc().getZ(), Configuration.getInstance().getInt(getId(), "redFlagId"), instanceId);
		}
		if ((playerWithBlueFlag != null) && playerWithBlueFlag.equals(victim))
		{
			announce(getPlayerList(), victim.getName() + " dropped the Blue flag!");
			blueFlagStatus = 2;
			unequipFlag(victim);
			blueFlagNpc = NpcContainer.getInstance().createNpc(victim.getOwnerLoc().getX(), victim.getOwnerLoc().getY(), victim.getOwnerLoc().getZ(), Configuration.getInstance().getInt(getId(), "blueFlagId"), instanceId);
		}
		
		addToResurrector(victim);
	}
	
	@Override
	public void onKill(EventPlayer victim, EventPlayer killer)
	{
		super.onKill(victim, killer);
		
	}
	
	@Override
	public void onLogout(EventPlayer player)
	{
		super.onLogout(player);
		
		if (playerWithRedFlag.equals(player))
		{
			announce(getPlayerList(), player.getName() + " dropped the Red flag!");
			redFlagStatus = 2;
			unequipFlag(player);
			redFlagNpc = NpcContainer.getInstance().createNpc(player.getOwnerLoc().getX(), player.getOwnerLoc().getY(), player.getOwnerLoc().getZ(), Configuration.getInstance().getInt(getId(), "redFlagId"), instanceId);
		}
		if (playerWithBlueFlag.equals(player))
		{
			announce(getPlayerList(), player.getName() + " dropped the Blue flag!");
			blueFlagStatus = 2;
			unequipFlag(player);
			blueFlagNpc = NpcContainer.getInstance().createNpc(player.getOwnerLoc().getX(), player.getOwnerLoc().getY(), player.getOwnerLoc().getZ(), Configuration.getInstance().getInt(getId(), "blueFlagId"), instanceId);
		}
		
	}
	
	@Override
	public boolean onTalkNpc(Integer npcId, EventPlayer player)
	{
		EventNpc npc = NpcContainer.getInstance().getNpc(npcId);
		
		if (npc == null)
		{
			return false;
		}
		
		if (!(npc.equals(blueFlagNpc) || npc.equals(blueHolderNpc) || npc.equals(redFlagNpc) || npc.equals(redHolderNpc)))
		{
			return false;
		}
		// Blue holder
		if (npc.equals(blueHolderNpc))
		{
			if (player.equals(playerWithRedFlag) && (blueFlagStatus == 0))
			{
				announce(getPlayerList(), "The Blue team scored!");
				teams.get(player.getMainTeam()).increaseScore();
				player.increaseScore();
				returnFlag(2);
			}
		}
		
		// Red holder
		if (npc.equals(redHolderNpc))
		{
			if (player.equals(playerWithBlueFlag) && (redFlagStatus == 0))
			{
				announce(getPlayerList(), "The Red team scored!");
				teams.get(player.getMainTeam()).increaseScore();
				player.increaseScore();
				returnFlag(1);
			}
		}
		
		// Blue flag
		if (npc.equals(blueFlagNpc))
		{
			if (blueFlagStatus == 2)
			{
				// blue player
				if (player.getMainTeam() == 1)
				{
					returnFlag(1);
				}
				
				// red player
				if (player.getMainTeam() == 2)
				{
					equipFlag(player, 1);
				}
			}
			if (blueFlagStatus == 0)
			{
				if (player.getMainTeam() == 2)
				{
					equipFlag(player, 1);
					blueFlagNpc.unspawn();
					blueFlagStatus = 1;
				}
			}
			
		}
		
		// Red flag
		if (npc.equals(redFlagNpc))
		{
			if (redFlagStatus == 2)
			{
				// red player
				if (player.getMainTeam() == 2)
				{
					returnFlag(2);
				}
				
				// blue player
				if (player.getMainTeam() == 1)
				{
					equipFlag(player, 2);
				}
			}
			if (redFlagStatus == 0)
			{
				if (player.getMainTeam() == 1)
				{
					equipFlag(player, 2);
					redFlagNpc.unspawn();
					redFlagStatus = 1;
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean onUseItem(EventPlayer player, Integer item)
	{
		if (((playerWithRedFlag != null) && playerWithRedFlag.equals(player)) || ((playerWithBlueFlag != null) && playerWithBlueFlag.equals(player)))
		{
			return false;
		}
		
		return true;
	}
	
	private void returnFlag(int flag)
	{
		int[] pos;
		switch (flag)
		{
			case 1:
				if (playerWithBlueFlag != null)
				{
					unequipFlag(playerWithBlueFlag);
				}
				if (blueFlagStatus == 2)
				{
					blueFlagNpc.unspawn();
				}
				
				pos = Configuration.getInstance().getPosition(getId(), "BlueFlag", 1);
				blueFlagNpc = NpcContainer.getInstance().createNpc(pos[0], pos[1], pos[2], Configuration.getInstance().getInt(getId(), "blueFlagId"), instanceId);
				blueFlagStatus = 0;
				announce(getPlayerList(), "The Blue flag returned!");
				break;
			
			case 2:
				if (playerWithRedFlag != null)
				{
					unequipFlag(playerWithRedFlag);
				}
				if (redFlagStatus == 2)
				{
					redFlagNpc.unspawn();
				}
				
				pos = Configuration.getInstance().getPosition(getId(), "RedFlag", 1);
				redFlagNpc = NpcContainer.getInstance().createNpc(pos[0], pos[1], pos[2], Configuration.getInstance().getInt(getId(), "redFlagId"), instanceId);
				redFlagStatus = 0;
				announce(getPlayerList(), "The Red flag returned!");
				break;
		}
	}
	
	@Override
	public void schedule(int time)
	{
		Out.tpmScheduleGeneral(task, time);
	}
	
	public void setStatus(EventState s)
	{
		eventState = s;
	}
	
	public void spawnFlagsAndHolders()
	{
		int[] pos = Configuration.getInstance().getPosition(getId(), "BlueFlag", 1);
		blueFlagNpc = NpcContainer.getInstance().createNpc(pos[0], pos[1], pos[2], Configuration.getInstance().getInt(getId(), "blueFlagId"), instanceId);
		blueHolderNpc = NpcContainer.getInstance().createNpc(pos[0], pos[1], pos[2], Configuration.getInstance().getInt(getId(), "blueFlagHolderId"), instanceId);
		
		pos = Configuration.getInstance().getPosition(getId(), "RedFlag", 1);
		redFlagNpc = NpcContainer.getInstance().createNpc(pos[0], pos[1], pos[2], Configuration.getInstance().getInt(getId(), "redFlagId"), instanceId);
		redHolderNpc = NpcContainer.getInstance().createNpc(pos[0], pos[1], pos[2], Configuration.getInstance().getInt(getId(), "redFlagHolderId"), instanceId);
	}
	
	@Override
	public void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	public void unequipFlag(EventPlayer player)
	{
		player.unequipAndRemove(6718);
		
		if (player.equals(playerWithRedFlag))
		{
			playerWithRedFlag = null;
		}
		if (player.equals(playerWithBlueFlag))
		{
			playerWithBlueFlag = null;
		}
	}
	
	public void unspawnFlagsAndHolders()
	{
		blueFlagNpc.unspawn();
		blueHolderNpc.unspawn();
		redFlagNpc.unspawn();
		redHolderNpc.unspawn();
	}
	
	@Override
	public void createStatus()
	{
		status = new TeamEventStatus(containerId);
	}
}
