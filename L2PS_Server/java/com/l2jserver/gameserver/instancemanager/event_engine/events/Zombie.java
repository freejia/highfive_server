package com.l2jserver.gameserver.instancemanager.event_engine.events;

import com.l2jserver.gameserver.instancemanager.event_engine.AbstractEvent;
import com.l2jserver.gameserver.instancemanager.event_engine.Configuration;
import com.l2jserver.gameserver.instancemanager.event_engine.io.Out;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventPlayer;
import com.l2jserver.gameserver.instancemanager.event_engine.model.PLoc;
import com.l2jserver.gameserver.instancemanager.event_engine.model.SingleEventStatus;

public class Zombie extends AbstractEvent
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
						divideIntoTeams(1);
						teleportToRandom();
						preparePlayers();
						forceSitAll();
						setStatus(EventState.FIGHT);
						schedule(10000);
						break;
					
					case FIGHT:
						forceStandAll();
						transform(getRandomPlayer());
						setStatus(EventState.END);
						
						clock.start();
						
						break;
					
					case END:
						setStatus(EventState.INACTIVE);
						clock.stop();
						
						for (EventPlayer p : players)
						{
							p.untransform();
						}
						
						if (getPlayersWithStatus(0).size() != 1)
						{
							msgToAll("Tie!");
							announce("The match has ended as a tie!");
						}
						
						else
						{
							EventPlayer winner = getWinner();
							giveReward(winner);
							announce("Congratulation! " + winner.getName() + " won the event!");
						}
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
		INACTIVE
	}
	
	public EventState eventState;
	
	private final Core task;
	
	@SuppressWarnings("synthetic-access")
	public Zombie(Integer containerId)
	{
		super(containerId);
		eventId = 9;
		createNewTeam(1, "All", Configuration.getInstance().getColor(getId(), "All"), Configuration.getInstance().getPosition(getId(), "All", 1));
		task = new Core();
		clock = new EventClock(Configuration.getInstance().getInt(getId(), "matchTime"));
	}
	
	@Override
	public boolean canAttack(EventPlayer player, EventPlayer target)
	{
		if ((player.getStatus() == 1) && (target.getStatus() == 0))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public void endEvent()
	{
		setStatus(EventState.END);
		clock.stop();
	}
	
	@Override
	public String getScorebar()
	{
		return "Humans: " + getPlayersWithStatus(0).size() + "  Time: " + clock.getTimeInString();
	}
	
	public EventPlayer getWinner()
	{
		return getPlayersWithStatus(0).head().getNext().getValue();
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
	public void onHit(EventPlayer actor, EventPlayer target)
	{
		if (eventState == EventState.END)
		{
			if ((actor.getStatus() == 1) && (target.getStatus() == 0))
			{
				transform(target);
				for (EventPlayer player : getPlayersWithStatus(0))
				{
					player.increaseScore();
				}
			}
			
			if (getPlayersWithStatus(0).size() == 1)
			{
				schedule(1);
			}
		}
		
	}
	
	@Override
	public void onLogout(EventPlayer player)
	{
		
		if ((player.getStatus() == 1) && (getPlayersWithStatus(1).size() == 1))
		{
			super.onLogout(player);
			transform(getRandomPlayer());
		}
		else
		{
			super.onLogout(player);
		}
		
	}
	
	@Override
	public boolean onUseItem(EventPlayer player, Integer item)
	{
		return false;
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
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.eventengine.Event#showHtml(com.l2jserver.gameserver .model.actor.instance.Integer, int)
	 */
	
	@Override
	public void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	public void teleportToRandom()
	{
		for (EventPlayer player : players)
		{
			int[] loc = Configuration.getInstance().getPosition(getId(), "All", 0);
			player.teleport(new PLoc(loc[0], loc[1], loc[2]), 0, true, 9101);
		}
	}
	
	public void transform(EventPlayer player)
	{
		player.setStatus(1);
		player.setNameColor(255, 0, 0);
		player.transform(303);
		player.unequipItemInSlot(10);
		player.unequipItemInSlot(16);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.eventengine.AbstractEvent#createStatus()
	 */
	@Override
	public void createStatus()
	{
		status = new SingleEventStatus(containerId);
	}
}
