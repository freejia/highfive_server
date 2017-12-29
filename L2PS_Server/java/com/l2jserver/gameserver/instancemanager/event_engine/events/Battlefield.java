package com.l2jserver.gameserver.instancemanager.event_engine.events;

import javolution.util.FastList;

import com.l2jserver.gameserver.instancemanager.event_engine.AbstractEvent;
import com.l2jserver.gameserver.instancemanager.event_engine.Configuration;
import com.l2jserver.gameserver.instancemanager.event_engine.container.NpcContainer;
import com.l2jserver.gameserver.instancemanager.event_engine.io.Out;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventNpc;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventPlayer;
import com.l2jserver.gameserver.instancemanager.event_engine.model.TeamEventStatus;

public class Battlefield extends AbstractEvent
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
						giveSkill();
						spawnBases();
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
						unspawnBases();
						removeSkill();
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
		TELEPORT,
		INACTIVE
	}
	
	public EventState eventState;
	
	private final Core task;
	
	public int winnerTeam;
	
	private final FastList<EventNpc> bases;
	
	@SuppressWarnings(
	{
		"synthetic-access"
	})
	public Battlefield(int containerId)
	{
		super(containerId);
		eventId = 14;
		createNewTeam(1, "Blue", Configuration.getInstance().getColor(getId(), "Blue"), Configuration.getInstance().getPosition(getId(), "Blue", 1));
		createNewTeam(2, "Red", Configuration.getInstance().getColor(getId(), "Red"), Configuration.getInstance().getPosition(getId(), "Red", 1));
		bases = new FastList<>();
		task = new Core();
		winnerTeam = 0;
		clock = new EventClock(Configuration.getInstance().getInt(getId(), "matchTime"));
	}
	
	@Override
	public void clockTick()
	{
		for (EventNpc base : bases)
		{
			if (base.getTeam() != 0)
			{
				teams.get(base.getTeam()).increaseScore(1);
			}
		}
	}
	
	@Override
	public void endEvent()
	{
		winnerTeam = players.head().getNext().getValue().getMainTeam();
		
		setStatus(EventState.END);
		schedule(1);
		
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
	
	public void giveSkill()
	{
		for (EventPlayer player : getPlayerList())
		{
			player.addSkill(Configuration.getInstance().getInt(getId(), "captureSkillId"), 1);
		}
	}
	
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
	
	public void removeSkill()
	{
		for (EventPlayer player : getPlayerList())
		{
			player.removeSkill(Configuration.getInstance().getInt(getId(), "captureSkillId"), 1);
		}
	}
	
	@Override
	protected void reset()
	{
		super.reset();
		bases.clear();
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
	
	public void spawnBases()
	{
		for (int i = 1; i <= Configuration.getInstance().getInt(getId(), "numOfBases"); i++)
		{
			int[] pos = Configuration.getInstance().getPosition(getId(), "Base", i);
			EventNpc npci = NpcContainer.getInstance().createNpc(pos[0], pos[1], pos[2], Configuration.getInstance().getInt(getId(), "baseNpcId"), instanceId);
			bases.add(npci);
			npci.setTitle("- Neutral -");
		}
	}
	
	@Override
	public void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	public void unspawnBases()
	{
		for (EventNpc base : bases)
		{
			base.unspawn();
		}
	}
	
	@Override
	public void useCapture(EventPlayer player, Integer base)
	{
		EventNpc baseinfo = NpcContainer.getInstance().getNpc(base);
		if (baseinfo == null)
		{
			return;
		}
		
		if (!bases.contains(baseinfo))
		{
			return;
		}
		
		for (EventNpc b : bases)
		{
			if (b.equals(baseinfo))
			{
				if (b.getTeam() == player.getMainTeam())
				{
					return;
				}
				
				b.setTeam(player.getMainTeam());
				b.setTitle("- " + teams.get(player.getMainTeam()).getName() + " -");
				
				for (EventPlayer p : getPlayerList())
				{
					p.sendAbstractNpcInfo(b);
				}
				
				announce(getPlayerList(), "The " + teams.get(player.getMainTeam()).getName() + " team captured a base!");
				player.increaseScore();
				break;
			}
		}
		
	}
	
	@Override
	public void createStatus()
	{
		status = new TeamEventStatus(containerId);
		
	}
	
}
