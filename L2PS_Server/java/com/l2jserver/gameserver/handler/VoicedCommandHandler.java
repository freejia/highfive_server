/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import com.l2jserver.gameserver.instancemanager.rank_system.rankpvpsystem.IVoicedCommandHandlerPvpInfo;
import com.l2jserver.gameserver.instancemanager.rank_system.rankpvpsystem.RankPvpSystemConfig;

/**
 * @author UnAfraid
 */
public class VoicedCommandHandler implements IHandler<IVoicedCommandHandler, String>
{
	private final Map<String, IVoicedCommandHandler> _datatable;
	
	protected VoicedCommandHandler()
	{
		_datatable = new HashMap<>();
		if (RankPvpSystemConfig.PVP_INFO_COMMAND_ENABLED && RankPvpSystemConfig.RANK_PVP_SYSTEM_ENABLED && !RankPvpSystemConfig.PVP_INFO_USER_COMMAND_ENABLED)
		{
			registerHandler(new IVoicedCommandHandlerPvpInfo());
		}
	}
	
	@Override
	public void registerHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		for (String id : ids)
		{
			_datatable.put(id, handler);
		}
	}
	
	@Override
	public synchronized void removeHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		for (String id : ids)
		{
			_datatable.remove(id);
		}
	}
	
	@Override
	public IVoicedCommandHandler getHandler(String voicedCommand)
	{
		String command = voicedCommand;
		if (voicedCommand.contains(" "))
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}
		return _datatable.get(command);
	}
	
	@Override
	public int size()
	{
		return _datatable.size();
	}
	
	public static VoicedCommandHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final VoicedCommandHandler _instance = new VoicedCommandHandler();
	}
}
