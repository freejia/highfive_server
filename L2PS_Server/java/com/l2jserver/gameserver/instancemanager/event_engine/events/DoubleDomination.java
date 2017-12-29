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

import javolution.util.FastMap;

import com.l2jserver.gameserver.instancemanager.event_engine.AbstractEvent;
import com.l2jserver.gameserver.instancemanager.event_engine.Configuration;
import com.l2jserver.gameserver.instancemanager.event_engine.container.NpcContainer;
import com.l2jserver.gameserver.instancemanager.event_engine.io.Out;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventNpc;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventPlayer;
import com.l2jserver.gameserver.instancemanager.event_engine.model.TeamEventStatus;

public class DoubleDomination extends AbstractEvent
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
						
						giveReward(getPlayersOfTeam(winnerTeam));
						unSpawnZones();
						setStatus(EventState.INACTIVE);
						announce("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " points!");
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
	
	private final FastMap<EventNpc, Integer> zones;
	
	private int time;
	
	private int holder;
	
	@SuppressWarnings(
	{
		"synthetic-access"
	})
	public DoubleDomination(Integer containerId)
	{
		super(containerId);
		eventId = 3;
		createNewTeam(1, "Blue", Configuration.getInstance().getColor(getId(), "Blue"), Configuration.getInstance().getPosition(getId(), "Blue", 1));
		createNewTeam(2, "Red", Configuration.getInstance().getColor(getId(), "Red"), Configuration.getInstance().getPosition(getId(), "Red", 1));
		task = new Core();
		zones = new FastMap<>();
		winnerTeam = 0;
		time = 0;
		holder = 0;
		clock = new EventClock(Configuration.getInstance().getInt(getId(), "matchTime"));
	}
	
	@Override
	public void clockTick()
	{
		int team1 = 0;
		int team2 = 0;
		
		for (EventNpc zone : zones.keySet())
		{
			for (EventPlayer player : getPlayerList())
			{
				switch (player.getMainTeam())
				{
					case 1:
						if (Math.sqrt(player.getPlanDistanceSq(zone.getNpc())) <= Configuration.getInstance().getInt(getId(), "zoneRadius"))
						{
							team1++;
						}
						break;
					
					case 2:
						if (Math.sqrt(player.getPlanDistanceSq(zone.getNpc())) <= Configuration.getInstance().getInt(getId(), "zoneRadius"))
						{
							team2++;
						}
						break;
				}
			}
			
			if (team1 > team2)
			{
				zones.getEntry(zone).setValue(1);
			}
			
			if (team2 > team1)
			{
				zones.getEntry(zone).setValue(2);
			}
			
			if (team1 == team2)
			{
				zones.getEntry(zone).setValue(0);
			}
			
			team1 = 0;
			team2 = 0;
		}
		
		if (zones.containsValue(1) && (!zones.containsValue(0) && !zones.containsValue(2)))
		{
			if (holder != 1)
			{
				announce(getPlayerList(), "The " + teams.get(1).getName() + " team captured both zones. Score in 10sec!");
				holder = 1;
				time = 0;
			}
			
			if (time == (Configuration.getInstance().getInt(getId(), "timeToScore") - 1))
			{
				for (EventPlayer player : getPlayersOfTeam(1))
				{
					player.increaseScore();
				}
				teams.get(1).increaseScore();
				teleportToTeamPos();
				time = 0;
				announce(getPlayerList(), "The " + teams.get(1).getName() + " team scored!");
				holder = 0;
			}
			else
			{
				time++;
			}
			
		}
		else if (zones.containsValue(2) && (!zones.containsValue(0) && !zones.containsValue(1)))
		{
			if (holder != 2)
			{
				announce(getPlayerList(), "The " + teams.get(2).getName() + " team captured both zones. Score in 10sec!");
				holder = 1;
				time = 0;
			}
			
			if (time == (Configuration.getInstance().getInt(getId(), "timeToScore") - 1))
			{
				for (EventPlayer player : getPlayersOfTeam(2))
				{
					player.increaseScore();
				}
				teams.get(2).increaseScore();
				teleportToTeamPos();
				time = 0;
				announce(getPlayerList(), "The " + teams.get(2).getName() + " team scored!");
				holder = 0;
			}
			else
			{
				time++;
			}
		}
		else
		{
			if (holder != 0)
			{
				announce(getPlayerList(), "Canceled!");
			}
			
			holder = 0;
			time = 0;
		}
		
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
		addToResurrector(victim);
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
		int[] z1pos = Configuration.getInstance().getPosition(getId(), "Zone", 1);
		int[] z2pos = Configuration.getInstance().getPosition(getId(), "Zone", 2);
		zones.put(NpcContainer.getInstance().createNpc(z1pos[0], z1pos[1], z1pos[2], Configuration.getInstance().getInt(getId(), "zoneNpcId"), instanceId), 0);
		zones.put(NpcContainer.getInstance().createNpc(z2pos[0], z2pos[1], z2pos[2], Configuration.getInstance().getInt(getId(), "zoneNpcId"), instanceId), 0);
		setStatus(EventState.START);
		schedule(1);
	}
	
	public void unSpawnZones()
	{
		for (EventNpc s : zones.keySet())
		{
			s.unspawn();
			zones.remove(s);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.eventengine.AbstractEvent#createStatus()
	 */
	@Override
	public void createStatus()
	{
		status = new TeamEventStatus(containerId);
	}
	
}
