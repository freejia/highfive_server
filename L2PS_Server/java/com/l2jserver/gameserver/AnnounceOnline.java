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
package com.l2jserver.gameserver;

import com.l2jserver.Config;
import com.l2jserver.gameserver.Announcements;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.L2World;

/**
 * @author RobíkBobík - L2PS Team
 */
public class AnnounceOnline
{
	static int _fakeOnlinePlayer = Config.FAKE_PLAYERS;
	
	protected static void StartAnnounce()
	{
		int OnlinePlayers = L2World.getInstance().getAllPlayersCount();
		
		if (OnlinePlayers >= 1)
		{
			if (Config.CRITICAL_ONLINE_ANNOUNCE)
			{
				Announcements.getInstance().announceToAll(OnlinePlayers + _fakeOnlinePlayer + " players are online. Have a nice day.", true);
			}
			else
			{
				Announcements.getInstance().announceToAll(OnlinePlayers + _fakeOnlinePlayer + " players are online. Have a nice day.");
			}
		}
	}
	
	public static void getInstance()
	{
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				StartAnnounce();
			}
		}, 0, Config.ANNOUNCE_ONLINE_PLAYERS_DELAY * 1000);
	}
}