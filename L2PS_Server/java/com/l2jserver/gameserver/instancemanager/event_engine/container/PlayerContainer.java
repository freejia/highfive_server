package com.l2jserver.gameserver.instancemanager.event_engine.container;

import java.util.Collection;

import javolution.util.FastMap;

import com.l2jserver.gameserver.instancemanager.event_engine.io.Out;
import com.l2jserver.gameserver.instancemanager.event_engine.model.EventPlayer;

public class PlayerContainer
{
	private static class SingletonHolder
	{
		protected static final PlayerContainer _instance = new PlayerContainer();
	}
	
	public static final PlayerContainer getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private final FastMap<Integer, EventPlayer> players;
	
	public PlayerContainer()
	{
		players = new FastMap<Integer, EventPlayer>().shared();
	}
	
	public void clearPlayers()
	{
		players.clear();
	}
	
	public EventPlayer createInfo(Integer player)
	{
		EventPlayer pi = new EventPlayer(Out.getPlayerById(player));
		players.put(player, pi);
		return pi;
	}
	
	public void deleteInfo(EventPlayer player)
	{
		players.remove(player.getPlayersId());
	}
	
	public void deleteInfo(int player)
	{
		players.remove(player);
	}
	
	public void deleteInfo(Integer player)
	{
		players.remove(player.intValue());
	}
	
	public EventPlayer getPlayer(int id)
	{
		return players.get(id);
	}
	
	public EventPlayer getPlayer(Integer id)
	{
		return players.get(id.intValue());
	}
	
	public EventPlayer getPlayerByName(String name)
	{
		for (EventPlayer player : players.values())
		{
			if (name.equals(player.getName()))
			{
				return player;
			}
		}
		
		return null;
	}
	
	public Collection<EventPlayer> getPlayers()
	{
		return players.values();
	}
}
