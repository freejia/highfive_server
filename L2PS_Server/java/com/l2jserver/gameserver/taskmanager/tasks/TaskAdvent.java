/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.taskmanager.tasks;

import java.util.logging.Logger;

import com.l2jserver.gameserver.datatables.NevitAdventTable;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.taskmanager.Task;
import com.l2jserver.gameserver.taskmanager.TaskManager;
import com.l2jserver.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.l2jserver.gameserver.taskmanager.TaskTypes;

/**
 * Author: RobikBobik(L2Brick)
 */
public class TaskAdvent extends Task
{
	private static final Logger _log = Logger.getLogger(TaskAdvent.class.getName());
	private static final String NAME = "huntingbonus";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		NevitAdventTable.getInstance().execRecTask();
		L2PcInstance[] onlinePlayers = L2World.getInstance().getAllPlayersArray();
		for (L2PcInstance player : onlinePlayers)
		{
			if ((player != null) && player.isOnline())
			{
				player.pauseAdventTask();
				player.startAdventTask();
			}
		}
		_log.config("Hunting Bonus System reseted");
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
	}
}