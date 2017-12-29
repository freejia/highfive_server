/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General private License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General private License for more
 * details.
 *
 * You should have received a copy of the GNU General private License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.instancemanager.event_engine.events;

import com.l2jserver.gameserver.instancemanager.event_engine.AbstractEvent;
import com.l2jserver.gameserver.instancemanager.event_engine.Configuration;
import com.l2jserver.gameserver.instancemanager.event_engine.io.Out;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventPlayer;
import com.l2jserver.gameserver.instancemanager.event_engine.model.SingleEventStatus;

public class DM extends AbstractEvent
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
						teleportToTeamPos();
						preparePlayers();
						forceSitAll();
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
						EventPlayer winner = getPlayerWithMaxScore();
						giveReward(winner);
						setStatus(EventState.INACTIVE);
						announce("Congratulation! " + winner.getName() + " won the event with " + winner.getScore() + " kills!");
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
	public DM(Integer containerId)
	{
		super(containerId);
		eventId = 1;
		createNewTeam(1, "All", Configuration.getInstance().getColor(getId(), "All"), Configuration.getInstance().getPosition(getId(), "All", 1));
		task = new Core();
		clock = new EventClock(Configuration.getInstance().getInt(getId(), "matchTime"));
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
		return "Max: " + getPlayerWithMaxScore().getScore() + "  Time: " + clock.getTimeInString() + "";
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
	public void onKill(EventPlayer victim, EventPlayer killer)
	{
		super.onKill(victim, killer);
		killer.increaseScore();
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
	
	@Override
	public void start()
	{
		setStatus(EventState.START);
		schedule(1);
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
