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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.l2jserver.util.L2Properties;
import com.l2jserver.util.StringUtil;

/**
 * @author Masterio
 */
public final class RankPvpSystemConfig
{
	private static final Logger _log = Logger.getLogger(RankPvpSystemConfig.class.getName());
	
	// --------------------------------------------------
	// L2J Variable Definitions
	// --------------------------------------------------
	
	// Rank PvP System:
	public static boolean RANK_PVP_SYSTEM_ENABLED;
	public static int LEGAL_KILL_MIN_LVL;
	public static boolean LEGAL_COUNTER_ALTT_ENABLED;
	public static boolean LEGAL_KILL_FOR_PK_KILLER_ENABLED;
	public static boolean LEGAL_KILL_FOR_INNOCENT_KILL_ENABLED;
	public static int PROTECTION_TIME_RESET;
	public static int LEGAL_KILL_PROTECTION;
	public static int DAILY_LEGAL_KILL_PROTECTION;
	
	// PvP Reward:
	public static boolean PVP_REWARD_ENABLED;
	public static int PVP_REWARD_ID;
	public static int PVP_REWARD_AMOUNT;
	public static int PVP_REWARD_MIN_LVL;
	public static boolean PVP_REWARD_FOR_PK_KILLER_ENABLED;
	public static boolean PVP_REWARD_FOR_INNOCENT_KILL_ENABLED;
	
	// Ranks:
	/** FastMap &lt;rankId, Rank&gt; - store all Ranks as Rank objects. */
	public static FastMap<Integer, Rank> RANKS = new FastMap<>();
	
	public static boolean RANKS_ENABLED;
	public static int RANK_POINTS_MIN_LVL;
	public static boolean RANK_POINTS_CUT_ENABLED;
	public static boolean RANK_REWARD_ENABLED;
	
	public static boolean RANK_POINTS_DOWN_COUNT_ENABLED;
	public static FastList<Integer> RANK_POINTS_DOWN_AMOUNTS = new FastList<>();
	
	public static boolean RANK_SHOUT_INFO_ON_KILL_ENABLED;
	public static boolean RANK_SHOUT_BONUS_INFO_ON_KILL_ENABLED;
	public static boolean RANK_REWARD_FOR_PK_KILLER_ENABLED;
	public static boolean RANK_REWARD_FOR_INNOCENT_KILL_ENABLED;
	
	public static boolean RANK_POINTS_REWARD_ENABLED;
	
	// War Kills:
	public static boolean WAR_KILLS_ENABLED;
	public static double WAR_RANK_POINTS_RATIO;
	
	// Combo Kill:
	public static boolean COMBO_KILL_ENABLED;
	public static boolean COMBO_KILL_PROTECTION_WITH_LEGAL_KILL_ENABLED;
	public static boolean COMBO_KILL_PROTECTION_NO_REPEAT_ENABLED;
	
	public static FastMap<Integer, String> COMBO_KILL_LOCAL_AREA_MESSAGES = new FastMap<>();
	public static FastMap<Integer, String> COMBO_KILL_GLOBAL_AREA_MESSAGES = new FastMap<>();
	
	public static boolean COMBO_KILL_ALT_MESSAGES_ENABLED;
	public static String COMBO_KILL_ALT_MESSAGE;
	public static int COMBO_KILL_ALT_GLOBAL_MESSAGE_LVL;
	
	public static boolean COMBO_KILL_DEFEAT_MESSAGE_ENABLED;
	public static int COMBO_KILL_DEFEAT_MESSAGE_MIN_LVL;
	public static String COMBO_KILL_DEFEAT_MESSAGE;
	
	public static int COMBO_KILL_RESETER;
	public static boolean COMBO_KILL_RANK_POINTS_RATIO_ENABLED;
	public static FastMap<Integer, Double> COMBO_KILL_RANK_POINTS_RATIO = new FastMap<>();
	
	// public static boolean COMBO_KILL_ON_EVENTS_ENABLED;
	
	// Title & Nick Color:
	public static boolean NICK_COLOR_ENABLED;
	public static boolean TITLE_COLOR_ENABLED;
	
	// Zones:
	public static FastList<Integer> ALLOWED_ZONES_IDS = new FastList<>();
	public static FastList<Integer> RESTRICTED_ZONES_IDS = new FastList<>();
	public static FastList<Integer> DEATH_MANAGER_RESTRICTED_ZONES_IDS = new FastList<>();
	public static FastMap<Integer, Double> RANK_POINTS_BONUS_ZONES_IDS = new FastMap<>();
	
	// pvpinfo command, pvp status window, death manager:
	public static boolean PVP_INFO_COMMAND_ENABLED;
	public static boolean PVP_INFO_USER_COMMAND_ENABLED;
	public static int PVP_INFO_USER_COMMAND_ID;
	
	public static boolean PVP_INFO_COMMAND_ON_DEATH_ENABLED;
	public static boolean DEATH_MANAGER_DETAILS_ENABLED;
	public static boolean DEATH_MANAGER_SHOW_ITEMS_ENABLED;
	
	public static boolean TOTAL_KILLS_IN_SHOUT_ENABLED;
	public static boolean TOTAL_KILLS_IN_PVPINFO_ENABLED;
	public static boolean TOTAL_KILLS_ON_ME_IN_PVPINFO_ENABLED;
	public static boolean SHOW_PLAYER_LEVEL_IN_PVPINFO_ENABLED;
	
	// Anti-Farm:
	public static boolean ANTI_FARM_CLAN_ALLY_ENABLED;
	public static boolean ANTI_FARM_PARTY_ENABLED;
	public static boolean ANTI_FARM_IP_ENABLED;
	
	// Community Board:
	public static boolean COMMUNITY_BOARD_TOP_LIST_ENABLED;
	public static long COMMUNITY_BOARD_TOP_LIST_IGNORE_TIME_LIMIT;
	
	// Database:
	public static long PVP_TABLE_UPDATE_INTERVAL;
	public static long TOP_TABLE_UPDATE_INTERVAL;
	
	public static boolean DATABASE_CLEANER_ENABLED;
	public static long DATABASE_CLEANER_REPEAT_TIME;
	
	// Image:
	public static int IMAGE_PREFIX;
	
	// Button style:
	public static String BUTTON_UP;
	public static String BUTTON_DOWN;
	public static String BUTTON_W;
	public static String BUTTON_H;
	
	/**
	 * This class initializes all global variables for configuration.<br>
	 * If the key doesn't appear in properties file, a default value is set by this class.
	 */
	public static void load()
	{
		try
		{
			File cc = new File(RANK_PVP_SYSTEM_CONFIG_FILE);
			InputStream is = new FileInputStream(cc);
			L2Properties ccSettings = new L2Properties();
			ccSettings.load(is);
			
			RANK_PVP_SYSTEM_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankPvpSystemEnabled", "false"));
			LEGAL_COUNTER_ALTT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("LegalCounterAltTEnabled", "false"));
			PVP_REWARD_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpRewardEnabled", "false"));
			
			DATABASE_CLEANER_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("DatabaseCleanerEnabled", "false"));
			DATABASE_CLEANER_REPEAT_TIME = Integer.parseInt(ccSettings.getProperty("DatabaseCleanerRepeatTime", "0"));
			if (DATABASE_CLEANER_REPEAT_TIME <= 0)
			{
				DATABASE_CLEANER_ENABLED = false;
			}
			else
			{
				DATABASE_CLEANER_REPEAT_TIME *= 86400000;
			}
			
			PVP_REWARD_ID = Integer.parseInt(ccSettings.getProperty("PvpRewardId", "6392"));
			PVP_REWARD_AMOUNT = Integer.parseInt(ccSettings.getProperty("PvpRewardAmmount", "1"));
			PVP_REWARD_MIN_LVL = Integer.parseInt(ccSettings.getProperty("PvpRewardMinLvl", "76"));
			PVP_REWARD_FOR_PK_KILLER_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpRewardForPkKillerEnabled", "true"));
			PVP_REWARD_FOR_INNOCENT_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpRewardForInnocentKillEnabled", "false"));
			RANKS_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RanksEnabled", "false"));
			RANK_POINTS_MIN_LVL = Integer.parseInt(ccSettings.getProperty("RankPointsMinLvl", "76"));
			RANK_POINTS_CUT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankPointsCutEnabled", "true"));
			
			RANK_REWARD_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankRewardEnabled", "false"));
			
			// set rank's:
			String id1[] = ccSettings.getProperty("RankNames", "").split(",");
			String id2[] = ccSettings.getProperty("RankMinPoints", "").split(",");
			String id3[] = ccSettings.getProperty("RankPointsForKill", "").split(",");
			String id4[] = ccSettings.getProperty("RankRewardIds", "").split(",");
			String id5[] = ccSettings.getProperty("RankRewardAmounts", "").split(",");
			String id6[] = ccSettings.getProperty("NickColors", "").split(",");
			String id7[] = ccSettings.getProperty("TitleColors", "").split(",");
			
			if (RANK_PVP_SYSTEM_ENABLED || RANK_REWARD_ENABLED || RANKS_ENABLED)
			{
				if ((id1.length != id2.length) || (id1.length != id3.length) || (id1.length != id4.length) || (id1.length != id5.length) || (id1.length != id6.length) || (id1.length != id7.length))
				{
					_log.info("ERROR: Rank PvP System Config: Arrays sizes should be the same!");
					
					_log.info("RANK_NAMES          :" + id1.length);
					_log.info("RANK_MIN_POINTS     :" + id2.length);
					_log.info("RANK_POINTS_FOR_KILL:" + id3.length);
					_log.info("RANK_REWARD_IDS     :" + id4.length);
					_log.info("RANK_REWARD_AMOUNTS :" + id5.length);
					_log.info("RANK_NICK_COLORS    :" + id6.length);
					_log.info("RANK_TITLE_COLORS   :" + id7.length);
					
				}
				else
				{
					for (int i = 0; i < id1.length; i++)
					{
						Rank rank = new Rank();
						
						rank.setId(id1.length - i);
						rank.setName(id1[i]);
						rank.setMinPoints(Long.parseLong(id2[i]));
						rank.setPointsForKill(Integer.parseInt(id3[i]));
						rank.setRewardId(Integer.parseInt(id4[i]));
						rank.setRewardAmount(Integer.parseInt(id5[i]));
						rank.setNickColor(Integer.decode("0x" + id6[i]));
						rank.setTitleColor(Integer.decode("0x" + id7[i]));
						
						RANKS.put(id1.length - i, rank);
					}
				}
			}
			
			NICK_COLOR_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("NickColorEnabled", "false"));
			TITLE_COLOR_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("TitleColorEnabled", "false"));
			
			RANK_POINTS_DOWN_COUNT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankPointsDownCountEnabled", "false"));
			RANK_POINTS_DOWN_AMOUNTS = new FastList<>();
			for (String id : ccSettings.getProperty("RankPointsDownAmounts", "").split(","))
			{
				RANK_POINTS_DOWN_AMOUNTS.add(Integer.parseInt(id));
			}
			
			RANK_SHOUT_INFO_ON_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankShoutInfoOnKillEnabled", "false"));
			RANK_SHOUT_BONUS_INFO_ON_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankShoutBonusInfoOnKillEnabled", "false"));
			RANK_REWARD_FOR_PK_KILLER_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankRewardForPkKillerEnabled", "false"));
			RANK_REWARD_FOR_INNOCENT_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankRewardForInnocentKillEnabled", "false"));
			WAR_KILLS_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("WarKillsEnabled", "false"));
			if (WAR_KILLS_ENABLED)
			{
				WAR_RANK_POINTS_RATIO = Double.parseDouble(ccSettings.getProperty("WarRankPointsRatio", "1.0"));
			}
			else
			{
				WAR_RANK_POINTS_RATIO = 1.0;
			}
			
			COMBO_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillEnabled", "false"));
			COMBO_KILL_PROTECTION_WITH_LEGAL_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillProtectionWithLegalKillEnabled", "false"));
			COMBO_KILL_PROTECTION_NO_REPEAT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillProtectionNoRepeatEnabled", "false"));
			
			String propertyValue = ccSettings.getProperty("ComboKillLocalAreaMessages");
			if ((propertyValue != null) && (propertyValue.length() > 0))
			{
				
				String[] propertySplit = propertyValue.split(";");
				if (propertySplit.length > 0)
				{
					for (String skill : propertySplit)
					{
						
						String[] skillSplit = skill.split(",");
						if (skillSplit.length != 2)
						{
							// _log.warning(StringUtil.concat("[RankPvpRankPointsBonusArea]: invalid config property -> RankPvpRankPointsBonusArea \"", skill, "\""));
						}
						else
						{
							try
							{
								COMBO_KILL_LOCAL_AREA_MESSAGES.put(Integer.parseInt(skillSplit[0]), skillSplit[1]);
							}
							catch (NumberFormatException nfe)
							{
								if (!skill.isEmpty())
								{
									_log.warning(StringUtil.concat("[ComboKillLocalAreaMessages]: invalid config property -> \"", skillSplit[0], "\"", skillSplit[1]));
								}
							}
						}
					}
				}
			}
			
			propertyValue = ccSettings.getProperty("ComboKillGlobalAreaMessages", "");
			if ((propertyValue != null) && (propertyValue.length() > 0))
			{
				
				String[] propertySplit = ccSettings.getProperty("ComboKillGlobalAreaMessages").split(";");
				if (propertySplit.length > 0)
				{
					for (String skill : propertySplit)
					{
						
						String[] skillSplit = skill.split(",");
						if (skillSplit.length != 2)
						{
							// _log.warning(StringUtil.concat("[RankPvpRankPointsBonusArea]: invalid config property -> RankPvpRankPointsBonusArea \"", skill, "\""));
						}
						else
						{
							try
							{
								COMBO_KILL_GLOBAL_AREA_MESSAGES.put(Integer.parseInt(skillSplit[0]), skillSplit[1]);
							}
							catch (NumberFormatException nfe)
							{
								if (!skill.isEmpty())
								{
									_log.warning(StringUtil.concat("[ComboKillGlobalAreaMessages]: invalid config property -> \"", skillSplit[0], "\"", skillSplit[1]));
								}
							}
						}
					}
				}
			}
			
			COMBO_KILL_ALT_MESSAGES_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillAltMessagesEnabled", "false"));
			COMBO_KILL_ALT_MESSAGE = ccSettings.getProperty("ComboKillAltMessage", "%killer% have %combo_level% Combo kills!");
			COMBO_KILL_ALT_GLOBAL_MESSAGE_LVL = Integer.parseInt(ccSettings.getProperty("ComboKillAltGlobalMessageMinLvl", "0"));
			
			COMBO_KILL_DEFEAT_MESSAGE_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillDefeatMessageEnabled", "true"));
			COMBO_KILL_DEFEAT_MESSAGE_MIN_LVL = Integer.parseInt(ccSettings.getProperty("ComboKillDefeatMessageMinComboLvl", "0"));
			COMBO_KILL_DEFEAT_MESSAGE = ccSettings.getProperty("ComboKillDefeatMessage", "%killer% is defeated with %combo_level% combo lvl!!!");
			
			COMBO_KILL_RESETER = Integer.parseInt(ccSettings.getProperty("ComboKillReseter", "0"));
			COMBO_KILL_RANK_POINTS_RATIO_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillRankPointsRatioEnabled", "false"));
			
			propertyValue = ccSettings.getProperty("ComboKillRankPointsRatio", "");
			if ((propertyValue != null) && (propertyValue.length() > 0))
			{
				
				String[] propertySplit = ccSettings.getProperty("ComboKillRankPointsRatio").split(";");
				if (propertySplit.length > 0)
				{
					for (String skill : propertySplit)
					{
						
						String[] skillSplit = skill.split(",");
						if (skillSplit.length != 2)
						{
							// _log.warning(StringUtil.concat("[RankPvpRankPointsBonusArea]: invalid config property -> RankPvpRankPointsBonusArea \"", skill, "\""));
						}
						else
						{
							try
							{
								COMBO_KILL_RANK_POINTS_RATIO.put(Integer.parseInt(skillSplit[0]), Double.parseDouble(skillSplit[1]));
							}
							catch (NumberFormatException nfe)
							{
								if (!skill.isEmpty())
								{
									_log.warning(StringUtil.concat("[ComboKillRankPointsRatio]: invalid config property -> \"", skillSplit[0], "\"", skillSplit[1]));
								}
							}
						}
					}
				}
			}
			
			// COMBO_KILL_ON_EVENTS_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ComboKillOnEventsEnabled", "false"));
			
			// additional security for combo kill system:
			if ((COMBO_KILL_LOCAL_AREA_MESSAGES.size() == 0) && (COMBO_KILL_GLOBAL_AREA_MESSAGES.size() == 0))
			{
				COMBO_KILL_ENABLED = false;
			}
			
			int i = 0;
			String tempStr = ccSettings.getProperty("AllowedZonesIds");
			if ((tempStr != null) && (tempStr.length() > 0))
			{
				for (String rZoneId : tempStr.split(","))
				{
					try
					{
						ALLOWED_ZONES_IDS.add(i, Integer.parseInt(rZoneId));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					i++;
				}
			}
			
			i = 0;
			tempStr = ccSettings.getProperty("RestrictedZonesIds");
			if ((tempStr != null) && (tempStr.length() > 0))
			{
				for (String rZoneId : tempStr.split(","))
				{
					try
					{
						RESTRICTED_ZONES_IDS.add(i, Integer.parseInt(rZoneId));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					i++;
				}
			}
			LEGAL_KILL_MIN_LVL = Integer.parseInt(ccSettings.getProperty("LegalKillMinLvl", "1"));
			LEGAL_KILL_FOR_PK_KILLER_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("LegalKillForPkKillerEnabled", "true"));
			LEGAL_KILL_FOR_INNOCENT_KILL_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("LegalKillForInnocentKillerEnabled", "false"));
			PROTECTION_TIME_RESET = Integer.parseInt(ccSettings.getProperty("ProtectionTimeReset", "0"));
			
			LEGAL_KILL_PROTECTION = Integer.parseInt(ccSettings.getProperty("LegalKillProtection", "0"));
			DAILY_LEGAL_KILL_PROTECTION = Integer.parseInt(ccSettings.getProperty("DailyLegalKillProtection", "0"));
			
			PVP_INFO_COMMAND_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpInfoCommandEnabled", "true"));
			PVP_INFO_USER_COMMAND_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpInfoUserCommandEnabled", "false"));
			PVP_INFO_USER_COMMAND_ID = Integer.parseInt(ccSettings.getProperty("PvpInfoUserCommandId", "114"));
			
			PVP_INFO_COMMAND_ON_DEATH_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("PvpInfoCommandShowOnDeathEnabled", "true"));
			DEATH_MANAGER_DETAILS_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("DeathManagerDetailsEnabled", "true"));
			DEATH_MANAGER_SHOW_ITEMS_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("DeathManagerShowItemsEnabled", "true"));
			
			i = 0;
			tempStr = ccSettings.getProperty("DeathManagerRestrictedZonesIds");
			if ((tempStr != null) && (tempStr.length() > 0))
			{
				for (String rZoneId : tempStr.split(","))
				{
					try
					{
						DEATH_MANAGER_RESTRICTED_ZONES_IDS.add(i, Integer.parseInt(rZoneId));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					i++;
				}
			}
			
			propertyValue = ccSettings.getProperty("RankPointsBonusZonesIds", "");
			if ((propertyValue != null) && (propertyValue.length() > 0))
			{
				
				String[] propertySplit = ccSettings.getProperty("RankPointsBonusZonesIds", "").split(";");
				if (propertySplit.length > 0)
				{
					for (String skill : propertySplit)
					{
						
						String[] skillSplit = skill.split(",");
						if (skillSplit.length != 2)
						{
							// _log.warning(StringUtil.concat("[RankPvpRankPointsBonusArea]: invalid config property -> RankPvpRankPointsBonusArea \"", skill, "\""));
						}
						else
						{
							try
							{
								
								RANK_POINTS_BONUS_ZONES_IDS.put(Integer.parseInt(skillSplit[0]), Double.parseDouble(skillSplit[1]));
							}
							catch (NumberFormatException nfe)
							{
								if (!skill.isEmpty())
								{
									_log.warning(StringUtil.concat("[RankPvpRankPointsBonusArea]: invalid config property -> \"", skillSplit[0], "\"", skillSplit[1]));
								}
							}
						}
					}
				}
			}
			
			TOTAL_KILLS_IN_SHOUT_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("TotalKillsInShoutEnabled", "true"));
			TOTAL_KILLS_IN_PVPINFO_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("TotalKillsInPvpInfoEnabled", "true"));
			TOTAL_KILLS_ON_ME_IN_PVPINFO_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("TotalKillsOnMeInPvpInfoEnabled", "true"));
			SHOW_PLAYER_LEVEL_IN_PVPINFO_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("ShowPlayerLevelInPvpInfoEnabled", "true"));
			
			RANK_POINTS_REWARD_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("RankPointsRewardEnabled", "true"));
			
			ANTI_FARM_CLAN_ALLY_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("AntiFarmClanAllyEnabled", "true"));
			ANTI_FARM_PARTY_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("AntiFarmPartyEnabled", "true"));
			ANTI_FARM_IP_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("AntiFarmIpEnabled", "true"));
			
			PVP_TABLE_UPDATE_INTERVAL = (Integer.parseInt(ccSettings.getProperty("PvpTableUpdateInterval", "1")) * 60000);
			if (PVP_TABLE_UPDATE_INTERVAL < 1)
			{
				PVP_TABLE_UPDATE_INTERVAL = 60000;
			}
			
			TOP_TABLE_UPDATE_INTERVAL = (Integer.parseInt(ccSettings.getProperty("TopTableUpdateInterval", "60")) * 60000);
			if (TOP_TABLE_UPDATE_INTERVAL < 10)
			{
				TOP_TABLE_UPDATE_INTERVAL = 3600000;
			}
			
			COMMUNITY_BOARD_TOP_LIST_ENABLED = Boolean.parseBoolean(ccSettings.getProperty("CommunityBoardTopListEnabled", "true"));
			COMMUNITY_BOARD_TOP_LIST_IGNORE_TIME_LIMIT = Integer.parseInt(ccSettings.getProperty("CommunityBoardTopListIgnoreTimeLimit", "0"));
			if (COMMUNITY_BOARD_TOP_LIST_IGNORE_TIME_LIMIT > 0)
			{
				COMMUNITY_BOARD_TOP_LIST_IGNORE_TIME_LIMIT *= 86400000;
			}
			
			IMAGE_PREFIX = Integer.parseInt(ccSettings.getProperty("ImagePrefix", "1"));
			
			// Buttons style:
			BUTTON_UP = ccSettings.getProperty("ButtonFore", "L2UI_ch3.BigButton3_over");
			BUTTON_DOWN = ccSettings.getProperty("ButtonBack", "L2UI_ch3.BigButton3");
			BUTTON_W = ccSettings.getProperty("ButtonWidth", "134");
			BUTTON_H = ccSettings.getProperty("ButtonHeight", "21");
			
			_log.warning(" - Rank Pvp System Config initialization complete.");
		}
		catch (Exception e)
		{
			_log.warning("Config: " + e.getMessage());
			throw new Error("Failed to Load " + RANK_PVP_SYSTEM_CONFIG_FILE + " File.");
		}
		
	}
	
	public static final String RANK_PVP_SYSTEM_CONFIG_FILE = "./config/InGame/RankPvpSystemConfig.l2ps";
	
}
