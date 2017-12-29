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
package com.l2jserver.gameserver.instancemanager.rank_system.rankpvpsystem;

import java.util.Calendar;

import javolution.util.FastList;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.actor.instance.L2ServitorInstance;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

/**
 * @author Masterio
 */
public class RankPvpSystem
{
	// private static final Logger _log = Logger.getLogger(RankPvpSystem.class.getName());
	
	private L2PcInstance killer = null;
	private L2PcInstance victim = null;
	
	public RankPvpSystem(L2Character killer, L2PcInstance victim)
	{
		// killer can be a Player, Pet or Summon:
		if ((killer != null) && (killer instanceof L2PcInstance))
		{
			setKiller((L2PcInstance) killer);
		}
		else if ((killer != null) && (killer instanceof L2PetInstance) && (((L2PetInstance) killer).getOwner() != null))
		{
			setKiller(((L2PetInstance) killer).getOwner());
		}
		else if ((killer != null) && (killer instanceof L2ServitorInstance) && (((L2ServitorInstance) killer).getOwner() != null))
		{
			setKiller(((L2ServitorInstance) killer).getOwner());
		}
		else
		{
			return;
		}
		
		setVictim(victim);
	}
	
	/**
	 * Executed when kill player (from victim side)
	 */
	public void doPvp()
	{
		
		if (checkBasicConditions(killer, victim))
		{
			
			// set pvp times:
			Calendar c = Calendar.getInstance();
			long systemTime = c.getTimeInMillis(); // date & time
			
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.HOUR, 0);
			long systemDay = c.getTimeInMillis(); // date
			
			long protectionTime = (1000 * 60 * RankPvpSystemConfig.PROTECTION_TIME_RESET);
			
			// get killer - victim pvp:
			Pvp pvp = PvpTable.getInstance().getPvp(killer.getObjectId(), victim.getObjectId(), systemDay);
			
			// check time protection:
			boolean timeProtectionOn = false;
			String nextRewardTime = "";
			if (RankPvpSystemConfig.PROTECTION_TIME_RESET > 0)
			{
				if ((pvp.getKillTime() + protectionTime) > (systemTime))
				{
					timeProtectionOn = true;
					nextRewardTime = calculateTimeToString(systemTime, pvp.getKillTime());
				}
			}
			
			// update pvp:
			increasePvp(pvp, systemTime, systemDay, timeProtectionOn);
			
			// get Killer and Victim pvp stats:
			PvpStats killerPvpStats = PvpTable.getInstance().getPvpStats(killer.getObjectId(), systemDay);
			PvpStats victimPvpStats = PvpTable.getInstance().getPvpStats(victim.getObjectId(), systemDay);
			
			// update killer Alt+T info.
			if (RankPvpSystemConfig.LEGAL_COUNTER_ALTT_ENABLED)
			{
				killer.setPvpKills(killerPvpStats.getTotalKillsLegal());
				killer.sendPacket(new UserInfo(killer));
				killer.broadcastUserInfo();
			}
			
			// start message separator:
			killer.sendMessage("----------------------------------------------------------------");
			victim.sendMessage("----------------------------------------------------------------");
			
			// execute PvP reward script if enabled:
			if (checkItemRewardConditions(pvp, timeProtectionOn, nextRewardTime))
			{
				addItemRewardForKiller();
			}
			
			// execute rank points script if enabled:
			if (checkRankPointsConditions(pvp, timeProtectionOn, nextRewardTime))
			{
				addRankPointsForKiller(pvp, killerPvpStats, victimPvpStats);
			}
			
			// update nick and title colors:
			updateNickAndTitleColor(killer, killerPvpStats);
			
			// show message:
			shoutPvpMessage(pvp);
			
			// end message separator:
			killer.sendMessage("----------------------------------------------------------------");
			victim.sendMessage("----------------------------------------------------------------");
			
			if (RankPvpSystemConfig.DEATH_MANAGER_DETAILS_ENABLED)
			{
				victim._rankPvpSystemDeathMgr = new RankPvpSystemDeathMgr(killer, victim);
			}
			
			if (RankPvpSystemConfig.PVP_INFO_COMMAND_ON_DEATH_ENABLED)
			{
				if (!RankPvpSystemZoneChecker.isInDMRestrictedZone(killer))
				{
					RankPvpSystemPlayerInfo playerInfo = new RankPvpSystemPlayerInfo();
					playerInfo.sendPlayerResponse(victim, killer);
				}
			}
		}
	}
	
	/**
	 * Check all conditions and increase or not PvP <br>
	 * (rank points are increased in addRankPointsForKiller() method)
	 * @param pvp
	 * @param systemTime
	 * @param systemDay
	 * @param timeProtectionOn
	 */
	private void increasePvp(Pvp pvp, long systemTime, long systemDay, boolean timeProtectionOn)
	{
		
		// add normal kills, checking is outside this method:
		pvp.increaseKills();
		
		if (pvp.getKillDay() == systemDay)
		{
			pvp.increaseKillsToday();
		}
		else
		{
			pvp.setKillsToday(1);
		}
		
		if ((victim.getClan() != null) && (killer.getClan() != null) && killer.getClan().isAtWarWith(victim.getClanId()))
		{
			pvp.increaseWarKills();
		}
		
		// shout combo kill, if legal kill protection is disabled:
		if (RankPvpSystemConfig.COMBO_KILL_ENABLED && !RankPvpSystemConfig.COMBO_KILL_PROTECTION_WITH_LEGAL_KILL_ENABLED)
		{
			shoutComboKill(systemTime);
		}
		
		if (checkLegalKillConditions(pvp))
		{
			
			// add legal kills:
			if (checkIsLegalKill(killer, victim))
			{
				
				if (!timeProtectionOn)
				{
					
					pvp.increaseKillsLegal();
					
					if ((victim.getClan() != null) && (killer.getClan() != null) && killer.getClan().isAtWarWith(victim.getClanId()))
					{
						pvp.increaseWarKillsLegal();
					}
					
					if (pvp.getKillDay() == systemDay)
					{
						pvp.increaseKillsLegalToday();
					}
					else
					{
						pvp.setKillsLegalToday(1);
					}
					
					// shout combo kill, if legal kill protection is enabled:
					if (RankPvpSystemConfig.COMBO_KILL_ENABLED && RankPvpSystemConfig.COMBO_KILL_PROTECTION_WITH_LEGAL_KILL_ENABLED)
					{
						shoutComboKill(systemTime);
					}
					
					// if protection is OFF set the current kill time.
					pvp.setKillTime(systemTime);
				}
				
			}
			
		}
		
		pvp.setKillDay(systemDay);
		
	}
	
	/**
	 * Shout current kills, kills_today, etc.
	 * @param pvp
	 */
	private void shoutPvpMessage(Pvp pvp)
	{
		
		if (RankPvpSystemConfig.TOTAL_KILLS_IN_SHOUT_ENABLED)
		{
			if (pvp.getKills() > 1)
			{
				String timeStr1 = " times";
				if (pvp.getKillsToday() == 1)
				{
					timeStr1 = "st time";
				}
				String msgVictim1 = killer.getName() + " killed you " + pvp.getKills() + " times";
				String msgVictim2 = killer.getName() + " killed you " + pvp.getKills() + " times (" + pvp.getKillsToday() + "" + timeStr1 + " today)";
				String msgKiller1 = "You have killed " + victim.getName() + " " + pvp.getKills() + " times";
				String msgKiller2 = "You have killed " + victim.getName() + " " + pvp.getKills() + " times (" + pvp.getKillsToday() + "" + timeStr1 + " today)";
				
				if (RankPvpSystemConfig.PROTECTION_TIME_RESET == 0)
				{
					victim.sendMessage(msgVictim1);
					killer.sendMessage(msgKiller1);
				}
				else
				{
					victim.sendMessage(msgVictim2);
					killer.sendMessage(msgKiller2);
				}
			}
			else
			{
				victim.sendMessage("This is the first time you have been killed by " + killer.getName());
				killer.sendMessage("You have killed " + victim.getName() + " for the first time");
			}
		}
		else
		{
			if (pvp.getKillsLegal() > 1)
			{
				String timeStr1 = " times";
				if (pvp.getKillsLegalToday() == 1)
				{
					timeStr1 = "st time";
				}
				String msgVictim1 = killer.getName() + " killed you " + pvp.getKillsLegal() + " times legally";
				String msgVictim2 = killer.getName() + " killed you " + pvp.getKillsLegal() + " times (" + pvp.getKillsLegalToday() + "" + timeStr1 + " today) legally";
				String msgKiller1 = "You have killed " + victim.getName() + " " + pvp.getKillsLegal() + " times legally";
				String msgKiller2 = "You have killed " + victim.getName() + " " + pvp.getKillsLegal() + " times (" + pvp.getKillsLegalToday() + "" + timeStr1 + " today) legally";
				
				if (RankPvpSystemConfig.PROTECTION_TIME_RESET == 0)
				{
					victim.sendMessage(msgVictim1);
					killer.sendMessage(msgKiller1);
				}
				else
				{
					victim.sendMessage(msgVictim2);
					killer.sendMessage(msgKiller2);
				}
			}
			else
			{
				victim.sendMessage("This is the first time you have been killed by " + killer.getName() + " legally.");
				killer.sendMessage("You have killed " + victim.getName() + " for the first time legally.");
			}
		}
		
	}
	
	private void addRankPointsForKiller(Pvp pvp, PvpStats killerPvpStats, PvpStats victimPvpStats)
	{
		
		int[] points_table = getPointsForKill(pvp, killerPvpStats, victimPvpStats, getKiller(), getVictim());
		
		// increase rank points:
		pvp.increaseRankPointsBy(points_table[0]);
		pvp.increaseRankPointsTodayBy(points_table[0]);
		// required update this object for increasePvp() (only in this method):
		killerPvpStats.addTotalRankPoints(points_table[0]);
		killerPvpStats.addTotalRankPointsToday(points_table[0]);
		
		// add rank reward into killer inventory:
		if (RankPvpSystemConfig.RANK_REWARD_ENABLED)
		{
			killer.addItem("Rank Reward", victimPvpStats.getRank().getRewardId(), victimPvpStats.getRank().getRewardAmount(), killer, true);
		}
		
		// shout rank informations:
		if (RankPvpSystemConfig.RANK_SHOUT_INFO_ON_KILL_ENABLED)
		{
			String killerRankName = killerPvpStats.getRank().getName();
			
			killer.sendMessage("You have obtained " + points_table[0] + " Rank Points for kill " + victim.getName()/* +" ("+victimPvpStats.getRank().getName()+")" */);
			if (RankPvpSystemConfig.RANK_SHOUT_BONUS_INFO_ON_KILL_ENABLED && RankPvpSystemConfig.RANK_SHOUT_INFO_ON_KILL_ENABLED)
			{
				showBonusDataPointsForKiller(points_table);
			}
			killer.sendMessage("Your Rank Points: " + killerPvpStats.getTotalRankPoints() + " (" + killerPvpStats.getTotalRankPointsToday() + " today)"/* +", current Rank: "+killerRankName */);
			victim.sendMessage("You have been killed by " + killer.getName() + " (" + killerRankName + ")");
		}
	}
	
	private void showBonusDataPointsForKiller(int[] points_table)
	{
		
		// show bonus points data for killer:
		String war = "";
		String area = "";
		String combo = "";
		
		if (points_table[1] > 0)
		{
			war = "war: " + points_table[1] + ", ";
		}
		
		if (points_table[2] > 0)
		{
			area = "area: " + points_table[2] + ", ";
		}
		
		if (points_table[3] > 0)
		{
			combo = "combo: " + points_table[3] + ", ";
		}
		
		if ((points_table[1] > 0) || (points_table[2] > 0) || (points_table[3] > 0))
		{
			String msg = war + area + combo;
			msg = msg.substring(0, msg.length() - 2);
			
			killer.sendMessage("Bonus RP (" + msg + ")");
		}
		
	}
	
	private void updateNickAndTitleColor(L2PcInstance killer, PvpStats killerPvpStats)
	{
		
		if (RankPvpSystemConfig.NICK_COLOR_ENABLED && (killer.getAppearance().getNameColor() != killerPvpStats.getRank().getNickColor()))
		{
			killer.getAppearance().setNameColor(killerPvpStats.getRank().getNickColor());
			killer.sendPacket(new UserInfo(killer));
			killer.broadcastUserInfo();
		}
		
		if (RankPvpSystemConfig.TITLE_COLOR_ENABLED && (killer.getAppearance().getTitleColor() != killerPvpStats.getRank().getTitleColor()))
		{
			killer.getAppearance().setTitleColor(killerPvpStats.getRank().getTitleColor());
			killer.broadcastTitleInfo();
		}
	}
	
	/**
	 * Adds the item reward.
	 */
	private void addItemRewardForKiller()
	{
		if (killer != null)
		{
			killer.addItem("PvP Reward", RankPvpSystemConfig.PVP_REWARD_ID, RankPvpSystemConfig.PVP_REWARD_AMOUNT, killer, true);
		}
	}
	
	public static final String calculateTimeToString(long sys_time, long kill_time)
	{
		long TimeToRewardInMilli = ((kill_time + (1000 * 60 * RankPvpSystemConfig.PROTECTION_TIME_RESET)) - sys_time);
		long TimeToRewardHours = TimeToRewardInMilli / (60 * 60 * 1000);
		long TimeToRewardMinutes = (TimeToRewardInMilli % (60 * 60 * 1000)) / (60 * 1000);
		long TimeToRewardSeconds = (TimeToRewardInMilli % (60 * 1000)) / (1000);
		
		String H = Long.toString(TimeToRewardHours);
		String M = Long.toString(TimeToRewardMinutes);
		String S = Long.toString(TimeToRewardSeconds);
		if (TimeToRewardHours <= 9)
		{
			H = "0" + H;
		}
		if (TimeToRewardMinutes <= 9)
		{
			M = "0" + M;
		}
		if (TimeToRewardSeconds <= 9)
		{
			S = "0" + S;
		}
		
		return H + ":" + M + ":" + S;
	}
	
	/**
	 * Calculate Rank points table for kill victim <br>
	 * [0] - Sum of Rank Points. <br>
	 * [1] - Bonus points for War.<br>
	 * [2] - Bonus points for Area.<br>
	 * [3] - Bonus points for Combo.
	 * @param pvp
	 * @param killerPvpStats
	 * @param victimPvpStats
	 * @param killer
	 * @param victim
	 * @return Rank points table for kill victim <br>
	 *         [0] - Sum of Rank Points. <br>
	 *         [1] - Bonus points for War.<br>
	 *         [2] - Bonus points for Area.<br>
	 *         [3] - Bonus points for Combo.
	 */
	public int[] getPointsForKill(Pvp pvp, PvpStats killerPvpStats, PvpStats victimPvpStats, L2PcInstance killer, L2PcInstance victim)
	{
		
		int points = 0;
		int points_war = 0;
		int points_bonus_zone = 0;
		int points_combo = 0;
		
		// add basic points:
		if (RankPvpSystemConfig.RANK_POINTS_DOWN_COUNT_ENABLED)
		{
			int i = 1;
			for (FastList.Node<Integer> n = RankPvpSystemConfig.RANK_POINTS_DOWN_AMOUNTS.head(), end = RankPvpSystemConfig.RANK_POINTS_DOWN_AMOUNTS.tail(); (n = n.getNext()) != end;)
			{
				if (pvp.getKillsLegalToday() == i)
				{
					points = n.getValue();
					break;
				}
				i++;
			}
		}
		else
		{
			points = victimPvpStats.getRank().getPointsForKill();
		}
		
		// cut points if enabled:
		if (RankPvpSystemConfig.RANK_POINTS_CUT_ENABLED && (killerPvpStats.getRank().getPointsForKill() < points))
		{
			points = killerPvpStats.getRank().getPointsForKill();
		}
		
		// add war points, if Killer's clan and Victim's clan at war:
		if (RankPvpSystemConfig.WAR_KILLS_ENABLED && (points > 0) && (RankPvpSystemConfig.WAR_RANK_POINTS_RATIO > 1) && (killer.getClan() != null) && (victim.getClan() != null) && killer.getClan().isAtWarWith(victim.getClanId()))
		{
			points_war = (int) Math.floor((points * RankPvpSystemConfig.WAR_RANK_POINTS_RATIO) - points);
		}
		
		// add bonus zone points, if Killer is inside bonus zone:
		if (points > 0)
		{
			double zone_ratio_killer = RankPvpSystemZoneChecker.getZoneBonusRatio(killer);
			if (zone_ratio_killer > 1)
			{
				points_bonus_zone = (int) Math.floor((points * zone_ratio_killer) - points);
			}
		}
		
		// add combo points:
		if (RankPvpSystemConfig.COMBO_KILL_RANK_POINTS_RATIO_ENABLED)
		{
			double combo_ratio = killer._rankPvpSystemComboKill.getComboKillRankPointsRatio();
			if (combo_ratio > 1)
			{
				points_combo = (int) Math.floor((points * combo_ratio) - points);
			}
		}
		
		points = points + points_war + points_bonus_zone + points_combo;
		
		int[] points_table = new int[4];
		points_table[0] = points;
		points_table[1] = points_war;
		points_table[2] = points_bonus_zone;
		points_table[3] = points_combo;
		
		return points_table;
	}
	
	/**
	 * Method used for Combo Kill System.
	 * @param killTime
	 */
	private void shoutComboKill(long killTime)
	{
		
		// create new combo instance if not exists or reset old combo if kill reseter enabled:
		if (killer._rankPvpSystemComboKill == null)
		{
			killer._rankPvpSystemComboKill = new RankPvpSystemComboKill();
		}
		else if ((RankPvpSystemConfig.COMBO_KILL_RESETER > 0) && ((killTime - killer._rankPvpSystemComboKill.getLastKillTime()) > (RankPvpSystemConfig.COMBO_KILL_RESETER * 1000)))
		{
			killer._rankPvpSystemComboKill = new RankPvpSystemComboKill();
		}
		
		// rise combo level and shout message:
		if (killer._rankPvpSystemComboKill.addVictim(victim.getObjectId(), killTime))
		{
			killer._rankPvpSystemComboKill.shoutComboKill(killer, victim);
		}
		
	}
	
	/**
	 * Return TRUE if at event or at olympiad.
	 * @param player
	 * @return
	 */
	private boolean checkPlayerEventStatus(L2PcInstance player)
	{
		
		if (player.isInOlympiadMode() || player.isOlympiadStart())
		{
			return true;
		}
		
		if (player.getEventStatus() != null)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Check Basic conditions for RPS, it's mean check if can I add +1 into kills and kills_today.<br>
	 * Basic mean: if killer is: in olympiad, in event, in restricted zone, etc.
	 * @param killer
	 * @param victim
	 * @return TRUE if conditions are correct.
	 */
	private boolean checkBasicConditions(L2PcInstance killer, L2PcInstance victim)
	{
		
		if ((killer == null) || (victim == null))
		{
			return false;
		}
		
		if (killer.isDead() || killer.isAlikeDead())
		{
			return false;
		}
		
		if (checkPlayerEventStatus(killer))
		{
			return false;
		}
		
		// check if killer is in allowed zone & not in restricted zone:
		if (!RankPvpSystemZoneChecker.isInPvpAllowedZone(killer) || RankPvpSystemZoneChecker.isInPvpRestrictedZone(killer))
		{
			if (RankPvpSystemConfig.PVP_REWARD_ENABLED && RankPvpSystemConfig.RANKS_ENABLED)
			{
				killer.sendMessage("You can't get Reward and Rank Points in restricted zone");
				return false;
			}
			
			if (RankPvpSystemConfig.PVP_REWARD_ENABLED)
			{
				killer.sendMessage("You can't get Reward in restricted zone");
				return false;
			}
			
			if (RankPvpSystemConfig.RANKS_ENABLED)
			{
				killer.sendMessage("You can't rise Rank Points in restricted zone");
				return false;
			}
			
			return false;
		}
		
		if (!antiFarmCheck(killer, victim))
		{
			return false;
		}
		
		return true;
	}
	
	private boolean checkLegalKillConditions(Pvp pvp)
	{
		// 1: check total legal kills:
		if ((pvp.getKillsLegal() > RankPvpSystemConfig.LEGAL_KILL_PROTECTION) && (RankPvpSystemConfig.LEGAL_KILL_PROTECTION > 0))
		{
			killer.sendMessage("Legal Kill is not possible for kill this player!");
			return false;
		}
		
		// 2: check total legal kills today:
		if ((pvp.getKillsLegalToday() > RankPvpSystemConfig.DAILY_LEGAL_KILL_PROTECTION) && (RankPvpSystemConfig.DAILY_LEGAL_KILL_PROTECTION > 0))
		{
			killer.sendMessage("Legal Kill is not possible for kill this player today!");
			return false;
		}
		
		return true;
	}
	
	private boolean checkItemRewardConditions(Pvp pvp, boolean timeProtectionOn, String nextRewardTime)
	{
		
		if (!RankPvpSystemConfig.PVP_REWARD_ENABLED)
		{
			return false;
		}
		
		if ((RankPvpSystemConfig.PVP_REWARD_MIN_LVL > victim.getLevel()) || (RankPvpSystemConfig.PVP_REWARD_MIN_LVL > killer.getLevel()))
		{
			killer.sendMessage("You or your target have not required level!");
			return false;
		}
		
		// if PK mode is disabled:
		if (!RankPvpSystemConfig.PVP_REWARD_FOR_INNOCENT_KILL_ENABLED && (killer.getKarma() > 0) && (victim.getPvpFlag() == 0) && (victim.getKarma() == 0))
		{
			killer.sendMessage("You can't earn Reward for kill innocent players!");
			return false;
		}
		
		// if reward for PK kill is disabled:
		if (!RankPvpSystemConfig.PVP_REWARD_FOR_PK_KILLER_ENABLED && (victim.getKarma() > 0))
		{
			killer.sendMessage("No Reward for kill player with Karma!");
			return false;
		}
		
		// 1: check total legal kills:
		if ((pvp.getKillsLegal() > RankPvpSystemConfig.LEGAL_KILL_PROTECTION) && (RankPvpSystemConfig.LEGAL_KILL_PROTECTION > 0))
		{
			if (!RankPvpSystemConfig.RANKS_ENABLED)
			{
				killer.sendMessage("Reward has been awarded for kill this player!");
			}
			return false;
		}
		
		// 2: check total legal kills today:
		if ((pvp.getKillsLegalToday() > RankPvpSystemConfig.DAILY_LEGAL_KILL_PROTECTION) && (RankPvpSystemConfig.DAILY_LEGAL_KILL_PROTECTION > 0))
		{
			if (!RankPvpSystemConfig.RANKS_ENABLED)
			{
				killer.sendMessage("Reward has been awarded for kill this player today!");
			}
			return false;
		}
		
		// 3: check time protection:
		if (timeProtectionOn)
		{
			if (!RankPvpSystemConfig.RANKS_ENABLED)
			{
				killer.sendMessage("Reward protection is on for " + nextRewardTime);
			}
			return false;
		}
		
		return true;
	}
	
	private boolean checkRankPointsConditions(Pvp pvp, boolean timeProtectionOn, String nextRewardTime)
	{
		
		if (!RankPvpSystemConfig.RANKS_ENABLED)
		{
			return false;
		}
		
		if ((RankPvpSystemConfig.RANK_POINTS_MIN_LVL > victim.getLevel()) || (RankPvpSystemConfig.RANK_POINTS_MIN_LVL > killer.getLevel()))
		{
			killer.sendMessage("You or your target have not required level!");
			return false;
		}
		
		// if PK mode is disabled:
		if ((!RankPvpSystemConfig.RANK_REWARD_FOR_INNOCENT_KILL_ENABLED) && (killer.getKarma() > 0) && (victim.getPvpFlag() == 0) && (victim.getKarma() == 0))
		{
			killer.sendMessage("You can't earn Rank Points on innocent players!");
			return false;
		}
		
		// if points for PK kill is disabled:
		if ((!RankPvpSystemConfig.RANK_REWARD_FOR_PK_KILLER_ENABLED) && (victim.getKarma() > 0))
		{
			killer.sendMessage("No Rank Points for kill player with Karma!");
			return false;
		}
		
		// 1: check total legal kills:
		if ((pvp.getKillsLegal() > RankPvpSystemConfig.LEGAL_KILL_PROTECTION) && (RankPvpSystemConfig.LEGAL_KILL_PROTECTION > 0))
		{
			if (!RankPvpSystemConfig.PVP_REWARD_ENABLED)
			{
				killer.sendMessage("Rank Points has been awarded for kill this player!");
			}
			else
			{
				killer.sendMessage("Reward & Rank Points has been awarded for kill this player!");
			}
			return false;
		}
		
		// 2: check total legal kills today:
		if ((pvp.getKillsLegalToday() > RankPvpSystemConfig.DAILY_LEGAL_KILL_PROTECTION) && (RankPvpSystemConfig.DAILY_LEGAL_KILL_PROTECTION > 0))
		{
			if (!RankPvpSystemConfig.PVP_REWARD_ENABLED)
			{
				killer.sendMessage("Rank Points has been awarded for kill this player today!");
			}
			else
			{
				killer.sendMessage("Reward & Rank Points has been awarded for kill this player today!");
			}
			return false;
		}
		
		// 3: check time protection:
		if (timeProtectionOn)
		{
			if (!RankPvpSystemConfig.PVP_REWARD_ENABLED)
			{
				killer.sendMessage("Rank Points protection is on for " + nextRewardTime);
			}
			else
			{
				killer.sendMessage("Reward & Rank Points protection is on for " + nextRewardTime);
			}
			return false;
		}
		
		return true;
	}
	
	/**
	 * Return True if it's Legal Kill (without farm check).
	 * @param killer
	 * @param victim
	 * @return
	 */
	private boolean checkIsLegalKill(L2PcInstance killer, L2PcInstance victim)
	{
		if ((RankPvpSystemConfig.LEGAL_KILL_MIN_LVL > victim.getLevel()) || (RankPvpSystemConfig.LEGAL_KILL_MIN_LVL > killer.getLevel()))
		{
			return false;
		}
		
		if ((!RankPvpSystemConfig.LEGAL_KILL_FOR_INNOCENT_KILL_ENABLED && (killer.getKarma() > 0)))
		{
			return false;
		}
		
		if (!RankPvpSystemConfig.LEGAL_KILL_FOR_PK_KILLER_ENABLED && (killer.getKarma() == 0) && (victim.getKarma() > 0))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * If returns TRUE is OK (no farming detected).<BR>
	 * Checking: Party, Clan/Ally, IP, self-kill.
	 * @param player1
	 * @param player2
	 * @return
	 */
	public static final boolean antiFarmCheck(L2PcInstance player1, L2PcInstance player2)
	{
		
		if ((player1 == null) || (player2 == null))
		{
			return true;
		}
		
		if (player1.equals(player2))
		{
			return false;
		}
		
		// Anti FARM Clan - Ally
		if ((RankPvpSystemConfig.ANTI_FARM_CLAN_ALLY_ENABLED && ((player1.getClan() != null) && (player2.getClan() != null)) && ((player1.getClan().getClanId() > 0) && (player2.getClan().getClanId() > 0) && (player1.getClan().getClanId() == player2.getClan().getClanId()))) || ((player1.getAllyId() > 0) && (player2.getAllyId() > 0) && (player1.getAllyId() == player2.getAllyId())))
		{
			player1.sendMessage("PvP Farm is not allowed!");
			// _log.warning("PVP POINT FARM ATTEMPT, " + player1.getName() + " and " + player2.getName() +". SAME CLAN or ALLY.");
			return false;
		}
		
		// Anti FARM Party
		if (RankPvpSystemConfig.ANTI_FARM_PARTY_ENABLED && (player1.getParty() != null) && (player2.getParty() != null) && player1.getParty().equals(player2.getParty()))
		{
			player1.sendMessage("PvP Farm is not allowed!");
			// _log.warning("PVP POINT FARM ATTEMPT, " + player1.getName() + " and " + player2.getName() +". SAME PARTY.");
			return false;
		}
		
		// Anti FARM same IP
		if (RankPvpSystemConfig.ANTI_FARM_IP_ENABLED)
		{
			if ((player1.getClient() != null) && (player2.getClient() != null))
			{
				String ip1 = player1.getClient().getConnection().getInetAddress().getHostAddress();
				String ip2 = player2.getClient().getConnection().getInetAddress().getHostAddress();
				
				if (ip1.equals(ip2))
				{
					player1.sendMessage("PvP Farm is not allowed!");
					// _log.warning("PVP POINT FARM ATTEMPT: " + player1.getName() + " and " + player2.getName() +". SAME IP.");
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * @return the killer
	 */
	public L2PcInstance getKiller()
	{
		return killer;
	}
	
	/**
	 * @param killer the killer to set
	 */
	public void setKiller(L2PcInstance killer)
	{
		this.killer = killer;
	}
	
	/**
	 * @return the victim
	 */
	public L2PcInstance getVictim()
	{
		return victim;
	}
	
	/**
	 * @param victim the victim to set
	 */
	public void setVictim(L2PcInstance victim)
	{
		this.victim = victim;
	}
	
}