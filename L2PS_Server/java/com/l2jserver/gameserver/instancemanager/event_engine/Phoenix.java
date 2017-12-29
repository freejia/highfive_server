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
package com.l2jserver.gameserver.instancemanager.event_engine;

import com.l2jserver.gameserver.instancemanager.event_engine.function.Buffer;
import com.l2jserver.gameserver.instancemanager.event_engine.function.Scheduler;
import com.l2jserver.gameserver.instancemanager.event_engine.function.Vote;
import com.l2jserver.gameserver.instancemanager.event_engine.io.Out;

public class Phoenix
{
	public static void phoenixStart()
	{
		Configuration.getInstance();
		
		if (Configuration.getInstance().getBoolean(0, "voteEnabled"))
		{
			Vote.getInstance();
		}
		if (Configuration.getInstance().getBoolean(0, "schedulerEnabled"))
		{
			Scheduler.getInstance();
		}
		if (Configuration.getInstance().getBoolean(0, "eventBufferEnabled"))
		{
			Buffer.getInstance();
		}
		
		Out.registerHandlers();
	}
}
