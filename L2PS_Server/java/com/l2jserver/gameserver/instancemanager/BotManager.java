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
package com.l2jserver.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;

/**
 * @author RobíkBobík
 */
public class BotManager
{
	private static final Logger _log = Logger.getLogger(BotManager.class.getName());
	
	private static FastMap<Integer, String[]> _unread;
	// Number of reportes made over each player
	private static FastMap<Integer, FastList<L2PcInstance>> _reportedCount = new FastMap<>();
	// Reporters blocked by time
	protected static FastMap<Integer, Long> _lockedReporters = new FastMap<>();
	// Blocked ips
	protected static Set<String> _lockedIps = new HashSet<>();
	// Blocked accounts
	protected static Set<String> _lockedAccounts = new HashSet<>();
	
	protected BotManager()
	{
		loadUnread();
	}
	
	public static BotManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static boolean reportedIsOnline(L2PcInstance player)
	{
		return L2World.getInstance().getPlayer(player.getObjectId()) != null;
	}
	
	public synchronized void reportBot(L2PcInstance reported, L2PcInstance reporter)
	{
		if (!reportedIsOnline(reported))
		{
			reporter.sendMessage("The player you are reporting is offline.");
			return;
		}
		
		_lockedReporters.put(reporter.getObjectId(), System.currentTimeMillis());
		_lockedIps.add(reporter.getClient().getConnection().getInetAddress().getHostAddress());
		_lockedAccounts.add(reporter.getAccountName());
		
		long date = Calendar.getInstance().getTimeInMillis();
		Connection con = null;
		
		try
		{
			if (!_reportedCount.containsKey(reported))
			{
				FastList<L2PcInstance> p = new FastList<>();
				p.add(reported);
				_reportedCount.put(reporter.getObjectId(), p);
			}
			else
			{
				if (_reportedCount.get(reporter).contains(reported.getObjectId()))
				{
					reporter.sendMessage("You cannot report a player more than 1 time");
					return;
				}
				_reportedCount.get(reporter).add(reported);
			}
			
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO `bot_report`(`reported_name`, `reported_objectId`, `reporter_name`, `reporter_objectId`, `date`) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, reported.getName());
			statement.setInt(2, reported.getObjectId());
			statement.setString(3, reporter.getName());
			statement.setInt(4, reporter.getObjectId());
			statement.setLong(5, date);
			statement.executeUpdate();
			
			ResultSet rs = statement.getGeneratedKeys();
			rs.next();
			final int maxId = rs.getInt(1);
			
			rs.close();
			statement.close();
			_unread.put(maxId, new String[]
			{
				reported.getName(),
				reporter.getName(),
				String.valueOf(date)
			});
		}
		catch (Exception e)
		{
			_log.severe("Could not save reported bot " + reported.getName() + " by " + reporter.getName() + " at " + date + ".");
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_REPORTED_AS_BOT);
		sm.addCharName(reported);
		reporter.sendPacket(sm);
		sm = null;
	}
	
	private void loadUnread()
	{
		_unread = new FastMap<>();
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT `report_id`, `reported_name`, `reporter_name`, `date` FROM `bot_report` WHERE `read` = ?");
			statement.setString(1, "false");
			
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				// Not loading objectIds to increase performance
				// L2World.getInstance().getPlayer(name).getObjectId();
				String[] data = new String[3];
				data[0] = rset.getString("reported_name");
				data[1] = rset.getString("reporter_name");
				data[2] = rset.getString("date");
				
				_unread.put(rset.getInt("report_id"), data);
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.severe("Could not load data from bot_report:\n" + e.getMessage());
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public FastMap<Integer, String[]> getUnread()
	{
		return _unread;
	}
	
	public void markAsRead(int id)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE `bot_report` SET `read` = ? WHERE `report_id` = ?");
			statement.setString(1, "true");
			statement.setInt(2, id);
			statement.execute();
			
			statement.close();
			_unread.remove(id);
			_log.fine("Reported bot marked as read, id was: " + id);
		}
		catch (Exception e)
		{
			_log.severe("Could not mark as read the reported bot: " + id + ":\n" + e.getMessage());
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public int getPlayerReportsCount(L2PcInstance reported)
	{
		int count = 0;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM `bot_report` WHERE `reported_objectId` = ?");
			statement.setInt(1, reported.getObjectId());
			
			ResultSet rset = statement.executeQuery();
			if (rset.next())
			{
				count = rset.getInt(1);
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		return count;
	}
	
	public void savePlayerPunish(L2PcInstance punished)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("UPDATE `bot_reported_punish` SET `time_left` = ? WHERE `charId` = ?");
			statement.setLong(1, punished.getPlayerPunish().getPunishTimeLeft());
			statement.setInt(2, punished.getObjectId());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	public boolean validateBot(L2PcInstance reported, L2PcInstance reporter)
	{
		if ((reported == null) || (reporter == null))
		{
			return false;
		}
		
		// Cannot report while reported is inside peace zone, war zone or olympiad
		if (reported.isInsideZone(ZoneId.PEACE) || reported.isInsideZone(ZoneId.PVP) || reported.isInOlympiadMode())
		{
			reporter.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_REPORT_IN_WARZONE_PEACEZONE_CLANWAR_OLYMPIAD));
			return false;
		}
		
		// Cannot report if reported and reporter are in war
		if ((reported.getClan() != null) && (reporter.getClan() != null))
		{
			if (reported.getClan().isAtWarWith(reporter.getClanId()))
			{
				reporter.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_REPORT_TARGET_IN_CLAN_WAR));
				return false;
			}
		}
		
		// Cannot report if the reported didnt earn exp since he logged in
		if (!reported.getStat().hasEarnedExp())
		{
			reporter.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_REPORT_CHARACTER_WITHOUT_GAINEXP));
			return false;
		}
		
		// Cannot report twice or more a player
		if (_reportedCount.containsKey(reporter))
		{
			for (L2PcInstance p : _reportedCount.get(reporter))
			{
				if (reported == p)
				{
					reporter.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_REPORTED_AS_BOT));
					return false;
				}
			}
		}
		
		return true;
	}
	
	public synchronized boolean validateReport(L2PcInstance reporter)
	{
		if (reporter == null)
		{
			return false;
		}
		
		if (reporter._account == null)
		{
			reporter._account = new L2Account(reporter.getAccountName());
		}
		
		// The player has a 30 mins lock before be able to report anyone again
		if (reporter._account.getReportsPoints() == 0)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_REPORT_IN_S1_MINUTES_S2_REPORT_POINTS_REMAIN_IN_ACCOUNT);
			sm.addNumber(0);
			sm.addNumber(0);
			reporter.sendPacket(sm);
			sm = null;
			return false;
		}
		
		// 30 mins must pass before report again
		else if (_lockedReporters.containsKey(reporter.getObjectId()))
		{
			final long delay = (System.currentTimeMillis() - _lockedReporters.get(reporter.getObjectId()));
			if (delay <= 1800000)
			{
				final int left = (int) (1800000 - delay) / 60000;
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_REPORT_IN_S1_MINUTES_S2_REPORT_POINTS_REMAIN_IN_ACCOUNT);
				sm.addNumber(left);
				sm.addNumber(reporter._account.getReportsPoints());
				reporter.sendPacket(sm);
				sm = null;
				return false;
			}
			ThreadPoolManager.getInstance().executeTask(new ReportClear(reporter));
		}
		// In those 30 mins, the ip which made the first report cannot report again
		else if (_lockedIps.contains(reporter.getClient().getConnection().getInetAddress().getHostAddress()))
		{
			reporter.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_REPORT_ALREDY_REPORTED_FROM_YOUR_CLAN_OR_IP));
			return false;
		}
		// In those 30 mins, the account which made report cannot report again
		else if (_lockedAccounts.contains(reporter.getAccountName()))
		{
			reporter.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_REPORT_ALAREDY_REPORTED_FROM_SAME_ACCOUNT));
			return false;
		}
		// If any clan/ally mate has reported any bot, you cannot report till he releases his lock
		else if (reporter.getClan() != null)
		{
			for (int i : _lockedReporters.keySet())
			{
				// Same clan
				final L2PcInstance p = L2World.getInstance().getPlayer(i);
				if (p == null)
				{
					continue;
				}
				
				if (p.getClanId() == reporter.getClanId())
				{
					reporter.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_REPORT_ALREDY_REPORTED_FROM_YOUR_CLAN_OR_IP));
					return false;
				}
				// Same ally
				else if (reporter.getClan().getAllyId() != 0)
				{
					if (p.getClan().getAllyId() == reporter.getClan().getAllyId())
					{
						reporter.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_REPORT_ALREDY_REPORTED_FROM_YOUR_CLAN_OR_IP));
						return false;
					}
				}
			}
		}
		reporter._account.reducePoints();
		return true;
	}
	
	public void onEnter(L2PcInstance activeChar)
	{
		activeChar.getStat().setFirstExp(activeChar.getExp());
		restorePlayerBotPunishment(activeChar);
		activeChar._account = new L2Account(activeChar.getAccountName());
	}
	
	private void restorePlayerBotPunishment(L2PcInstance activeChar)
	{
		String punish = "";
		long delay = 0;
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT `punish_type`, `time_left` FROM `bot_reported_punish` WHERE `charId` = ?");
			statement.setInt(1, activeChar.getObjectId());
			
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
				punish = rset.getString("punish_type");
				delay = rset.getLong("time_left");
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		if (!punish.isEmpty() && (BotPunish.Punish.valueOf(punish) != null))
		{
			if (delay < 0)
			{
				final BotPunish.Punish p = BotPunish.Punish.valueOf(punish);
				final long left = (-delay / 1000) / 60;
				activeChar.setPunishDueBotting(p, (int) left);
			}
			else
			{
				activeChar.endPunishment();
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static BotManager _instance = new BotManager();
	}
	
	private class ReportClear implements Runnable
	{
		private final L2PcInstance _reporter;
		
		protected ReportClear(L2PcInstance reporter)
		{
			_reporter = reporter;
		}
		
		@Override
		public void run()
		{
			_lockedReporters.remove(_reporter.getObjectId());
			_lockedIps.remove(_reporter.getClient().getConnection().getInetAddress().getHostAddress());
			_lockedAccounts.remove(_reporter.getAccountName());
		}
	}
}
