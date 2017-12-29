package com.l2jserver.gameserver.instancemanager.event_engine.events;

import javolution.util.FastList;

import com.l2jserver.gameserver.instancemanager.event_engine.AbstractEvent;
import com.l2jserver.gameserver.instancemanager.event_engine.Configuration;
import com.l2jserver.gameserver.instancemanager.event_engine.container.NpcContainer;
import com.l2jserver.gameserver.instancemanager.event_engine.io.Out;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventNpc;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventPlayer;
import com.l2jserver.gameserver.instancemanager.event_engine.model.TeamEventStatus;

public class Bomb extends AbstractEvent
{
	public static boolean enabled = true;
	
	private class BombTask implements Runnable
	{
		private final EventPlayer owner;
		private final EventNpc bomb;
		private FastList<EventPlayer> victims;
		
		private BombTask(EventPlayer p)
		{
			owner = p;
			bomb = NpcContainer.getInstance().createNpc(owner.getOwnerLoc().getX(), owner.getOwnerLoc().getY(), owner.getOwnerLoc().getZ(), Configuration.getInstance().getInt(getId(), "bombNpcId"), instanceId);
			bomb.setTitle(owner.getMainTeam() == 1 ? "Blue" : "Red");
			bomb.broadcastStatusUpdate();
			
			for (EventPlayer pl : getPlayerList())
			{
				pl.sendAbstractNpcInfo(bomb);
			}
			
			Out.tpmScheduleGeneral(this, 3000);
		}
		
		@Override
		public void run()
		{
			
			victims = new FastList<>();
			
			for (EventPlayer victim : getPlayerList())
			{
				if ((owner.getMainTeam() != victim.getMainTeam()) && (Math.sqrt(victim.getPlanDistanceSq(bomb.getNpc())) <= Configuration.getInstance().getInt(getId(), "bombRadius")))
				{
					victim.doDie();
					owner.increaseScore();
					
					victims.add(victim);
					
					if (victim.getMainTeam() == 1)
					{
						teams.get(2).increaseScore();
					}
					if (victim.getMainTeam() == 2)
					{
						teams.get(1).increaseScore();
					}
				}
			}
			if (victims.size() != 0)
			{
				bomb.showBombEffect(victims);
				victims.clear();
			}
			
			bomb.unspawn();
			
		}
	}
	
	private class Core implements Runnable
	{
		
		@SuppressWarnings(
		{
			"synthetic-access"
		})
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
						giveSkill();
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
						
						removeSkill();
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
	
	private enum EventState
	{
		START,
		FIGHT,
		END,
		TELEPORT,
		INACTIVE
	}
	
	private EventState eventState;
	
	private final Core task;
	
	@SuppressWarnings("synthetic-access")
	public Bomb(Integer containerId)
	{
		super(containerId);
		eventId = 12;
		createNewTeam(1, "Blue", Configuration.getInstance().getColor(getId(), "Blue"), Configuration.getInstance().getPosition(getId(), "Blue", 1));
		createNewTeam(2, "Red", Configuration.getInstance().getColor(getId(), "Red"), Configuration.getInstance().getPosition(getId(), "Red", 1));
		task = new Core();
		winnerTeam = 0;
		clock = new EventClock(Configuration.getInstance().getInt(getId(), "matchTime"));
	}
	
	@Override
	public boolean canAttack(EventPlayer player, EventPlayer target)
	{
		return false;
	}
	
	@SuppressWarnings("synthetic-access")
	@Override
	public void dropBomb(EventPlayer player)
	{
		new BombTask(player);
	}
	
	@Override
	public void endEvent()
	{
		winnerTeam = players.head().getNext().getValue().getMainTeam();
		
		setStatus(EventState.END);
		clock.stop();
		
	}
	
	@Override
	public String getScorebar()
	{
		return "" + teams.get(1).getName() + ": " + teams.get(1).getScore() + "  " + teams.get(2).getName() + ": " + teams.get(2).getScore() + "  Time: " + clock.getTimeInString();
	}
	
	@Override
	public int getWinnerTeam()
	{
		if (teams.get(1).getScore() > teams.get(2).getScore())
		{
			return 1;
		}
		if (teams.get(2).getScore() > teams.get(1).getScore())
		{
			return 2;
		}
		if (teams.get(1).getScore() == teams.get(2).getScore())
		{
			if (rnd.nextInt(1) == 1)
			{
				return 1;
			}
			return 2;
		}
		
		return 1;
	}
	
	private void giveSkill()
	{
		for (EventPlayer player : getPlayerList())
		{
			player.addSkill(Configuration.getInstance().getInt(getId(), "bombSkillId"), 1);
		}
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
		addToResurrector(victim);
	}
	
	@Override
	public void onLogout(EventPlayer player)
	{
		player.removeSkill(Configuration.getInstance().getInt(getId(), "bombSkillId"), 1);
	}
	
	@Override
	public boolean onUseMagic(EventPlayer player, Integer skill)
	{
		if (Integer.valueOf(skill) == Configuration.getInstance().getInt(getId(), "bombSkillId"))
		{
			return true;
		}
		
		return false;
		
	}
	
	private void removeSkill()
	{
		for (EventPlayer player : getPlayerList())
		{
			player.removeSkill(Configuration.getInstance().getInt(getId(), "bombSkillId"), 1);
		}
	}
	
	@Override
	public void schedule(int time)
	{
		Out.tpmScheduleGeneral(task, time);
	}
	
	private void setStatus(EventState s)
	{
		eventState = s;
	}
	
	@Override
	public void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	@Override
	public void createStatus()
	{
		status = new TeamEventStatus(containerId);
		
	}
}
