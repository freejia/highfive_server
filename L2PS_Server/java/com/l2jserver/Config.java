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
package com.l2jserver;

import info.tak11.subnet.Subnet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.gameserver.engines.DocumentParser;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import com.l2jserver.gameserver.util.FloodProtectorConfig;
import com.l2jserver.util.L2Properties;
import com.l2jserver.util.StringUtil;

public final class Config
{
	private static final Logger _log = Logger.getLogger(Config.class.getName());
	
	// --------------------------------------------------
	// Constants
	// --------------------------------------------------
	public static final String EOL = System.getProperty("line.separator");
	
	// Bosses/
	public static final String ANTHARAS_CONFIG = "./config/Bosses/Antharas.l2ps";
	public static final String BAIUM_CONFIG = "./config/Bosses/Baium.l2ps";
	public static final String BELETH_CONFIG = "./config/Bosses/Beleth.l2ps";
	public static final String CORE_CONFIG = "./config/Bosses/Core.l2ps";
	public static final String DESTRUCTION_BOSSES_CONFIG = "./config/Bosses/DestructionBosses.l2ps";
	public static final String EKIMUS_CONFIG = "./config/Bosses/Ekimus.l2ps";
	public static final String FREYA_EASY_CONFIG = "./config/Bosses/FreyaEasy.l2ps";
	public static final String FREYA_HARDCORE_CONFIG = "./config/Bosses/FreyaHardCore.l2ps";
	public static final String FRINTEZZA_CONFIG = "./config/Bosses/Frintezza.l2ps";
	public static final String ORFEN_CONFIG = "./config/Bosses/Orfen.l2ps";
	public static final String QUEEN_ANT_CONFIG = "./config/Bosses/QueenAnt.l2ps";
	public static final String SAILREN_CONFIG = "./config/Bosses/Sailren.l2ps";
	public static final String TIAT_CONFIG = "./config/Bosses/Tiat.l2ps";
	public static final String VALAKAS_CONFIG = "./config/Bosses/Valakas.l2ps";
	public static final String ZAKEN_CONFIG = "./config/Bosses/Zaken.l2ps";
	// Events/
	public static final String CH_SIEGE_CONFIG = "./config/Events/ConquerableHallSiege.l2ps";
	public static final String FORTSIEGE_CONFIG = "./config/Events/FortSiege.l2ps";
	public static final String EASY_CHAMPION_CONFIG = "./config/Events/EasyChampions.l2ps";
	public static final String HARD_CHAMPION_CONFIG = "./config/Events/HardChampions.l2ps";
	public static final String L2PSMODS_CONFIG = "./config/Events/CustomMods.l2ps";
	public static final String OLYMPIAD_CONFIG = "./config/Events/Olympiad.l2ps";
	public static final String PC_BANG_CONFIG = "./config/Events/PcBangs.l2ps";
	public static final String SIEGE_CONFIG = "./config/Events/Siege.l2ps";
	public static final String TVT_CONFIG = "./config/Events/TeamVsTeam-Classis.l2ps";
	public static final String TW_CONFIG = "./config/Events/TerritoryWar.l2ps";
	public static final String TOWNWAR_CONFIG = "./config/Events/TownWar.l2ps";
	public static final String WEDDING_CONFIG = "./config/Events/WeddingSystem.l2ps";
	public static final String EVENT_MODS = "./config/Events/EventMods.l2ps";
	// Engine Events/
	public static final String EVENT_ENGINE_CONFIG = "./config/EventsEngine/OnOff_Engine.l2ps";
	// HexId
	public static final String HEXID_FILE = "./config/Hexid/hexid.txt";
	// Chat Filter
	public static final String CHAT_FILTER_FILE = "./config/ChatFilter/chatfilter.txt";
	// InGame
	public static final String CLASS_BALANCE_CONFIG = "./config/InGame/ClassBalance.l2ps";
	public static final String PREMIUM_BUFFER_CONFIG = "./config/InGame/PremiumBuffer.l2ps";
	public static final String NORMAL_BUFFER_CONFIG = "./config/InGame/NormalBuffer.l2ps";
	public static final String ADMIN_CONFIG = "./config/InGame/Administrator.l2ps";
	public static final String FEATURE_CONFIG = "./config/InGame/Feature.l2ps";
	public static final String GENERAL_CONFIG = "./config/InGame/General.l2ps";
	public static final String GEODATA_CONFIG = "./config/InGame/Geodata.l2ps";
	public static final String CHARACTER_CONFIG = "./config/InGame/Character.l2ps";
	public static final String NPC_CONFIG = "./config/InGame/NPC.l2ps";
	public static final String OFFLINE_SHOP_CONFIG = "./config/InGame/OfflineShop.l2ps";
	public static final String PVP_CONFIG = "./config/InGame/PVP.l2ps";
	public static final String RATES_CONFIG = "./config/InGame/Rates.l2ps";
	public static final String FAKEPCS_CONFIG = "./config/InGame/FakePcs.l2ps";
	// Premium Accounts
	public static final String PREMIUM_SYSTEM_CONFIG = "./config/PremiumAccounts/PremiumSystemOptions.l2ps";
	// Server
	public static final String AUTO_RESTART = "./config/Server/AutoRestartGameServer.properties";
	public static final String COMMUNITY_PVP_CONFIG = "./config/Server/CommunityPvP.l2ps";
	public static final String COMMUNITY_CONFIG = "./config/Server/CommunityServer.l2ps";
	public static final String FLOOD_PROTECTOR_CONFIG = "./config/Server/FloodProtector.l2ps";
	public static final String ID_FACTORY_CONFIG = "./config/Server/IdFactory.l2ps";
	public static final String SECURITY_CONFIG = "./config/Server/Security.l2ps";
	public static final String SERVER_CONFIG = "./config/Server/Server.l2ps";
	public static final String VOTE_EVENT_NPC = "./config/Server/VoteEventNpc.l2ps";
	public static final String THREAD_CONFIG = "./config/Server/ThreadSettings.l2ps";
	public static final String CLIENT_PACKETS_CONFIG = "./config/Server/ClientPackets.l2ps";
	public static final String OPTIMIZATION_CONFIG = "./config/Server/Optimization.l2ps";
	public static final String ANTI_BOT_CONFIG = "./config/Server/AntiBotting.l2ps";
	// Others
	public static final String LOGIN_CONFIG = "./config/LoginServer.l2ps";
	public static final String IP_CONFIG = "./config/ipconfig.xml";
	public static final String TELNET_CONFIG = "./config/Telnet.l2ps";
	public static final String MMO_CONFIG = "./config/MMO.l2ps";
	public static final String EMAIL_CONFIG = "./config/Email.l2ps";
	
	public static boolean ENABLE_FAKE_PCS;
	public static boolean OLY_VISUAL_RESTRICTION;
	
	// TODO:
	public static int CHANGE_STATUS;
	public static int CHANCE_SPAWN;
	public static int RESPAWN_TIME;
	public static boolean ENABLE_EVENT_ACHIEVEMENT;
	
	// DONE
	public static int MAX_LEVEL_FOR_AQ_ZONE;
	public static int GET_TIME_TO_FILL;
	public static int GET_KILLS;
	public static int START_CAPTCHA;
	public static int TW_TOWN_ID;
	public static String TW_TOWN_NAME;
	public static boolean TW_ALL_TOWNS;
	public static boolean TW_AUTO_EVENT;
	public static String[] TW_INTERVAL;
	public static int TW_TIME_BEFORE_START;
	public static int TW_RUNNING_TIME;
	public static int TW_ITEM_ID;
	public static int TW_ITEM_AMOUNT;
	public static boolean TW_GIVE_PVP_AND_PK_POINTS;
	public static boolean TW_ALLOW_KARMA;
	public static boolean TW_DISABLE_GK;
	public static boolean TW_LOSE_BUFFS_ON_DEATH;
	public static int MIN_ZAKEN_DAY_PLAYERS;
	public static int MAX_ZAKEN_DAY_PLAYERS;
	public static int MIN_ZAKEN_NIGHT_PLAYERS;
	public static int MAX_ZAKEN_NIGHT_PLAYERS;
	public static int MIN_PLAYER_TO_FE;
	public static int MAX_PLAYER_TO_FE;
	public static int MIN_LEVEL_TO_FE;
	public static int MIN_PLAYERS_TO_HARD;
	public static int MAX_PLAYERS_TO_HARD;
	public static int MIN_PLAYERS_TO_EASY;
	public static int MAX_PLAYERS_TO_EASY;
	public static int MIN_PLAYER_LEVEL_TO_HARD;
	public static int MIN_PLAYER_LEVEL_TO_EASY;
	public static int EROSION_ATTACK_MIN_PLAYERS;
	public static int EROSION_ATTACK_MAX_PLAYERS;
	public static int EROSION_DEFENCE_MIN_PLAYERS;
	public static int EROSION_DEFENCE_MAX_PLAYERS;
	public static int HEART_ATTACK_MIN_PLAYERS;
	public static int HEART_ATTACK_MAX_PLAYERS;
	public static int HEART_DEFENCE_MIN_PLAYERS;
	public static int HEART_DEFENCE_MAX_PLAYERS;
	public static int SOI_EKIMUS_KILL_COUNT;
	public static int MIN_TIAT_PLAYERS;
	public static int MAX_TIAT_PLAYERS;
	public static int SOD_TIAT_KILL_COUNT;
	public static long SOD_STAGE_2_LENGTH;
	public static int SAILREN_SPAWN_INTERVAL;
	public static int SAILREN_SPAWN_RANDOM;
	public static int ANTHARAS_WAIT_TIME;
	public static int ANTHARAS_SPAWN_INTERVAL;
	public static int ANTHARAS_SPAWN_RANDOM;
	public static int VALAKAS_WAIT_TIME;
	public static int VALAKAS_SPAWN_INTERVAL;
	public static int VALAKAS_SPAWN_RANDOM;
	public static int BAIUM_SPAWN_INTERVAL;
	public static int BAIUM_SPAWN_RANDOM;
	public static int CORE_SPAWN_INTERVAL;
	public static int CORE_SPAWN_RANDOM;
	public static int ORFEN_SPAWN_INTERVAL;
	public static int ORFEN_SPAWN_RANDOM;
	public static int QUEEN_ANT_SPAWN_INTERVAL;
	public static int QUEEN_ANT_SPAWN_RANDOM;
	public static int BELETH_MIN_PLAYERS;
	public static int BELETH_SPAWN_INTERVAL;
	public static int BELETH_SPAWN_RANDOM;
	public static boolean DISABLED_FOR_NON_VIP;
	public static int MAGE_LEES_THAN_FIGHTER;
	public static boolean SPAWN_CHAR;
	public static int SPAWN_X;
	public static int SPAWN_Y;
	public static int SPAWN_Z;
	public static boolean CHAR_TITLE;
	public static String ADD_CHAR_TITLE;
	public static boolean ANNOUNCE_HERO_LOGIN;
	public static boolean ANNOUNCE_CASTLE_LORDS;
	public static boolean ANNOUNCE_GM_LOGIN;
	
	public static int ANNOUNCE_ONLINE_PLAYERS_DELAY;
	public static int FAKE_PLAYERS;
	public static boolean ALLOW_ANNOUNCE_ONLINE_PLAYERS;
	public static boolean CRITICAL_ONLINE_ANNOUNCE;
	public static boolean AUTO_RESTART_ENABLE;
	public static int AUTO_RESTART_TIME;
	public static String[] AUTO_RESTART_INTERVAL;
	
	public static boolean ANNOUNCE_BOSS_SPAWN;
	public static boolean ANNOUNCE_BOSS_SPAWN_CRIT;
	public static String ANNOUNCE_BOSS_MSG;
	
	// PREMIUM BUFFER
	public static boolean PremiumNpcBuffer_SmartWindow;
	public static boolean PremiumNpcBuffer_VIP;
	public static boolean PremiumVIP_ONLY;
	public static boolean PremiumNpcBuffer_EnableBuff;
	public static boolean PremiumNpcBuffer_EnableScheme;
	public static boolean PremiumNpcBuffer_EnableHeal;
	public static boolean PremiumNpcBuffer_EnableBuffs;
	public static boolean PremiumNpcBuffer_EnableResist;
	public static boolean PremiumNpcBuffer_EnableSong;
	public static boolean PremiumNpcBuffer_EnableDance;
	public static boolean PremiumNpcBuffer_EnableChant;
	public static boolean PremiumNpcBuffer_EnableOther;
	public static boolean PremiumNpcBuffer_EnableSpecial;
	public static boolean PremiumNpcBuffer_EnableCubic;
	public static boolean PremiumNpcBuffer_EnableCancel;
	public static boolean PremiumNpcBuffer_EnableBuffSet;
	public static boolean PremiumNpcBuffer_EnableBuffPK;
	public static boolean PremiumNpcBuffer_EnableFreeBuffs;
	public static boolean PremiumNpcBuffer_EnableTimeOut;
	public static int PremiumNpcBuffer_TimeOutTime;
	public static int PremiumNpcBuffer_MinLevel;
	public static int PremiumNpcBuffer_PriceCancel;
	public static int PremiumNpcBuffer_PriceHeal;
	public static int PremiumNpcBuffer_PriceBuffs;
	public static int PremiumNpcBuffer_PriceResist;
	public static int PremiumNpcBuffer_PriceSong;
	public static int PremiumNpcBuffer_PriceDance;
	public static int PremiumNpcBuffer_PriceChant;
	public static int PremiumNpcBuffer_PriceOther;
	public static int PremiumNpcBuffer_PriceSpecial;
	public static int PremiumNpcBuffer_PriceCubic;
	public static int PremiumNpcBuffer_PriceSet;
	public static int PremiumNpcBuffer_PriceScheme;
	public static int PremiumNpcBuffer_MaxScheme;
	public static int PremiumNpcBuffer_consumableID;
	public static int PremiumBuffMaxAmount;
	public static int PremiumDanceMaxAmout;
	// PREMIUM END
	// NORMAL BUFFER
	public static boolean NormalNpcBuffer_SmartWindow;
	public static boolean NormalNpcBuffer_VIP;
	public static boolean NormalVIP_ONLY;
	public static boolean NormalNpcBuffer_EnableBuff;
	public static boolean NormalNpcBuffer_EnableScheme;
	public static boolean NormalNpcBuffer_EnableHeal;
	public static boolean NormalNpcBuffer_EnableBuffs;
	public static boolean NormalNpcBuffer_EnableResist;
	public static boolean NormalNpcBuffer_EnableSong;
	public static boolean NormalNpcBuffer_EnableDance;
	public static boolean NormalNpcBuffer_EnableChant;
	public static boolean NormalNpcBuffer_EnableOther;
	public static boolean NormalNpcBuffer_EnableSpecial;
	public static boolean NormalNpcBuffer_EnableCubic;
	public static boolean NormalNpcBuffer_EnableCancel;
	public static boolean NormalNpcBuffer_EnableBuffSet;
	public static boolean NormalNpcBuffer_EnableBuffPK;
	public static boolean NormalNpcBuffer_EnableFreeBuffs;
	public static boolean NormalNpcBuffer_EnableTimeOut;
	public static int NormalNpcBuffer_TimeOutTime;
	public static int NormalNpcBuffer_MinLevel;
	public static int NormalNpcBuffer_PriceCancel;
	public static int NormalNpcBuffer_PriceHeal;
	public static int NormalNpcBuffer_PriceBuffs;
	public static int NormalNpcBuffer_PriceResist;
	public static int NormalNpcBuffer_PriceSong;
	public static int NormalNpcBuffer_PriceDance;
	public static int NormalNpcBuffer_PriceChant;
	public static int NormalNpcBuffer_PriceOther;
	public static int NormalNpcBuffer_PriceSpecial;
	public static int NormalNpcBuffer_PriceCubic;
	public static int NormalNpcBuffer_PriceSet;
	public static int NormalNpcBuffer_PriceScheme;
	public static int NormalNpcBuffer_MaxScheme;
	public static int NormalNpcBuffer_consumableID;
	public static int NormalBuffMaxAmount;
	public static int NormalDanceMaxAmout;
	// NORMAL END
	
	public static boolean ENABLE_ELPY;
	public static int EVENT_INTERVAL_ELPIES;
	public static int EVENT_TIME_ELPIES;
	public static int EVENT_NUMBER_OF_SPAWNED_ELPIES;
	public static boolean ENABLE_RABBITS;
	public static int EVENT_INTERVAL_RABBITS;
	public static int EVENT_TIME_RABBITS;
	public static int EVENT_NUMBER_OF_SPAWNED_CHESTS;
	public static boolean ENABLE_RACE;
	public static int EVENT_INTERVAL_RACE;
	public static int EVENT_REG_TIME_RACE;
	public static int EVENT_RUNNING_TIME_RACE;
	public static boolean ALLOW_COMMUNITY_MULTISELL;
	public static boolean ALLOW_COMMUNITY_CLASS;
	public static boolean ALLOW_COMMUNITY_ENCHANT;
	public static boolean ALLOW_COMMUNITY_BUFF;
	public static boolean ALLOW_COMMUNITY_TELEPORT;
	public static boolean ALLOW_COMMUNITY_STATS;
	public static String ALLOW_CLASS_MASTERSCB;
	public static String CLASS_MASTERS_PRICECB;
	public static int[] CLASS_MASTERS_PRICE_LISTCB = new int[4];
	public static int CLASS_MASTERS_PRICE_ITEMCB;
	public static ArrayList<Integer> ALLOW_CLASS_MASTERS_LISTCB = new ArrayList<>();
	public static int ENCHANT_ITEM;
	public static boolean BUFF_PEACE_ZONE;
	public static boolean BUFF_COST;
	public static boolean ALLOW_COMMUNITY_SERVICES;
	public static int DelevelItemId;
	public static int DelevelItemCount;
	public static int NoblItemId;
	public static int NoblItemCount;
	public static int GenderItemId;
	public static int GenderItemCount;
	public static int HeroItemId;
	public static int HeroItemCount;
	public static int RecoveryPKItemId;
	public static int RecoveryPKItemCount;
	public static int RecoveryVitalityItemId;
	public static int RecoveryVitalityItemCount;
	public static int SPItemId;
	public static int SPItemCount;
	public static String VOTE_LINK_HOPZONE;
	public static String VOTE_LINK_TOPZONE;
	public static int VOTE_REWARD_ID1;
	public static int VOTE_REWARD_ID2;
	public static int VOTE_REWARD_ID3;
	public static int VOTE_REWARD_ID4;
	public static int VOTE_REWARD_AMOUNT1;
	public static int VOTE_REWARD_AMOUNT2;
	public static int VOTE_REWARD_AMOUNT3;
	public static int VOTE_REWARD_AMOUNT4;
	public static int SECS_TO_VOTE;
	public static int EXTRA_REW_VOTE_AM;
	//
	public static boolean ENABLE_EVENT_ENGINE;
	public static boolean PC_BANG_ENABLED;
	public static int MAX_PC_BANG_POINTS;
	public static boolean ENABLE_DOUBLE_PC_BANG_POINTS;
	public static int DOUBLE_PC_BANG_POINTS_CHANCE;
	public static double PC_BANG_POINT_RATE;
	public static double PC_BANG_POINT_RATE_BOSS;
	public static double PC_BANG_POINT_RATE_EASY_CHAMPION;
	public static double PC_BANG_POINT_RATE_HARD_CHAMIPON;
	public static boolean RANDOM_PC_BANG_POINT;
	public static boolean USE_PREMIUMSERVICE;
	public static float PREMIUM_RATE_XP;
	public static float PREMIUM_RATE_SP;
	public static Map<Integer, Float> PREMIUM_RATE_DROP_ITEMS_ID;
	public static float PREMIUM_RATE_DROP_SPOIL;
	public static float PREMIUM_RATE_DROP_ITEMS;
	public static float PREMIUM_RATE_DROP_ITEMS_BY_RAID;
	// DOKONCENO
	public static boolean ALT_GAME_DELEVEL;
	public static boolean DECREASE_SKILL_LEVEL;
	public static double ALT_WEIGHT_LIMIT;
	public static int RUN_SPD_BOOST;
	public static int DEATH_PENALTY_CHANCE;
	public static double RESPAWN_RESTORE_CP;
	public static double RESPAWN_RESTORE_HP;
	public static double RESPAWN_RESTORE_MP;
	public static boolean ALT_GAME_TIREDNESS;
	public static boolean ENABLE_MODIFY_SKILL_DURATION;
	public static Map<Integer, Integer> SKILL_DURATION_LIST;
	public static boolean ENABLE_MODIFY_SKILL_REUSE;
	public static Map<Integer, Integer> SKILL_REUSE_LIST;
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean AUTO_LEARN_FS_SKILLS;
	public static boolean AUTO_LOOT_HERBS;
	public static byte BUFFS_MAX_AMOUNT;
	public static byte TRIGGERED_BUFFS_MAX_AMOUNT;
	public static byte DANCES_MAX_AMOUNT;
	public static boolean DANCE_CANCEL_BUFF;
	public static boolean DANCE_CONSUME_ADDITIONAL_MP;
	public static boolean ALT_STORE_DANCES;
	public static boolean AUTO_LEARN_DIVINE_INSPIRATION;
	public static boolean ALT_GAME_CANCEL_BOW;
	public static boolean ALT_GAME_CANCEL_CAST;
	public static boolean EFFECT_CANCELING;
	public static boolean ALT_GAME_MAGICFAILURES;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static boolean STORE_SKILL_COOLTIME;
	public static boolean SUBCLASS_STORE_SKILL_COOLTIME;
	public static boolean SUMMON_STORE_SKILL_COOLTIME;
	public static boolean ALT_GAME_SHIELD_BLOCKS;
	public static int ALT_PERFECT_SHLD_BLOCK;
	public static boolean ALLOW_CLASS_MASTERS;
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	public static boolean ALLOW_ENTIRE_TREE;
	public static boolean ALTERNATE_CLASS_MASTER;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean ALT_GAME_SKILL_LEARN;
	public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
	public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
	public static boolean ALLOW_TRANSFORM_WITHOUT_QUEST;
	public static int FEE_DELETE_TRANSFER_SKILLS;
	public static int FEE_DELETE_SUBCLASS_SKILLS;
	public static boolean RESTORE_SERVITOR_ON_RECONNECT;
	public static boolean RESTORE_PET_ON_RECONNECT;
	public static double MAX_BONUS_EXP;
	public static double MAX_BONUS_SP;
	public static int MAX_RUN_SPEED;
	public static int MAX_PCRIT_RATE;
	public static int MAX_MCRIT_RATE;
	public static int MAX_PATK_SPEED;
	public static int MAX_MATK_SPEED;
	public static int MAX_EVASION;
	public static int MIN_ABNORMAL_STATE_SUCCESS_RATE;
	public static int MAX_ABNORMAL_STATE_SUCCESS_RATE;
	public static byte MAX_SUBCLASS;
	public static byte BASE_SUBCLASS_LEVEL;
	public static byte MAX_SUBCLASS_LEVEL;
	public static int MAX_PVTSTORESELL_SLOTS_DWARF;
	public static int MAX_PVTSTORESELL_SLOTS_OTHER;
	public static int MAX_PVTSTOREBUY_SLOTS_DWARF;
	public static int MAX_PVTSTOREBUY_SLOTS_OTHER;
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_GM;
	public static int INVENTORY_MAXIMUM_QUEST_ITEMS;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int ALT_FREIGHT_SLOTS;
	public static int ALT_FREIGHT_PRICE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
	public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
	public static int MAX_PERSONAL_FAME_POINTS;
	public static int FORTRESS_ZONE_FAME_TASK_FREQUENCY;
	public static int FORTRESS_ZONE_FAME_AQUIRE_POINTS;
	public static int CASTLE_ZONE_FAME_TASK_FREQUENCY;
	public static int CASTLE_ZONE_FAME_AQUIRE_POINTS;
	public static boolean FAME_FOR_DEAD_PLAYERS;
	public static boolean IS_CRAFTING_ENABLED;
	public static boolean CRAFT_MASTERWORK;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	public static boolean ALT_GAME_CREATION;
	public static double ALT_GAME_CREATION_SPEED;
	public static double ALT_GAME_CREATION_XP_RATE;
	public static double ALT_GAME_CREATION_RARE_XPSP_RATE;
	public static double ALT_GAME_CREATION_SP_RATE;
	public static boolean ALT_BLACKSMITH_USE_RECIPES;
	public static int ALT_CLAN_LEADER_DATE_CHANGE;
	public static String ALT_CLAN_LEADER_HOUR_CHANGE;
	public static boolean ALT_CLAN_LEADER_INSTANT_ACTIVATION;
	public static int ALT_CLAN_JOIN_DAYS;
	public static int ALT_CLAN_CREATE_DAYS;
	public static int ALT_CLAN_DISSOLVE_DAYS;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
	public static int ALT_CLAN_MEMBERS_FOR_WAR;
	public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	public static boolean REMOVE_CASTLE_CIRCLETS;
	public static int ALT_PARTY_RANGE;
	public static int ALT_PARTY_RANGE2;
	public static boolean ALT_LEAVE_PARTY_LEADER;
	public static boolean INITIAL_EQUIPMENT_EVENT;
	public static long STARTING_ADENA;
	public static byte STARTING_LEVEL;
	public static int STARTING_SP;
	public static long MAX_ADENA;
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_RAIDS;
	public static int LOOT_RAIDS_PRIVILEGE_INTERVAL;
	public static int LOOT_RAIDS_PRIVILEGE_CC_SIZE;
	public static int UNSTUCK_INTERVAL;
	public static int TELEPORT_WATCHDOG_TIMEOUT;
	public static int PLAYER_SPAWN_PROTECTION;
	public static List<Integer> SPAWN_PROTECTION_ALLOWED_ITEMS;
	public static int PLAYER_TELEPORT_PROTECTION;
	public static boolean RANDOM_RESPAWN_IN_TOWN_ENABLED;
	public static boolean OFFSET_ON_TELEPORT_ENABLED;
	public static int MAX_OFFSET_ON_TELEPORT;
	public static boolean RESTORE_PLAYER_INSTANCE;
	public static boolean ALLOW_SUMMON_TO_INSTANCE;
	public static int EJECT_DEAD_PLAYER_TIME;
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	public static boolean ALT_GAME_FREE_TELEPORT;
	public static int DELETE_DAYS;
	public static float ALT_GAME_EXPONENT_XP;
	public static float ALT_GAME_EXPONENT_SP;
	public static String PARTY_XP_CUTOFF_METHOD;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static int[][] PARTY_XP_CUTOFF_GAPS;
	public static int[] PARTY_XP_CUTOFF_GAP_PERCENTS;
	public static boolean DISABLE_TUTORIAL;
	public static boolean EXPERTISE_PENALTY;
	public static boolean STORE_RECIPE_SHOPLIST;
	public static boolean STORE_UI_SETTINGS;
	public static String[] FORBIDDEN_NAMES;
	public static boolean SILENCE_MODE_EXCLUDE;
	public static boolean ALT_VALIDATE_TRIGGER_SKILLS;
	
	// --------------------------------------------------
	// ClanHall Settings
	// --------------------------------------------------
	public static long CH_TELE_FEE_RATIO;
	public static int CH_TELE1_FEE;
	public static int CH_TELE2_FEE;
	public static long CH_ITEM_FEE_RATIO;
	public static int CH_ITEM1_FEE;
	public static int CH_ITEM2_FEE;
	public static int CH_ITEM3_FEE;
	public static long CH_MPREG_FEE_RATIO;
	public static int CH_MPREG1_FEE;
	public static int CH_MPREG2_FEE;
	public static int CH_MPREG3_FEE;
	public static int CH_MPREG4_FEE;
	public static int CH_MPREG5_FEE;
	public static long CH_HPREG_FEE_RATIO;
	public static int CH_HPREG1_FEE;
	public static int CH_HPREG2_FEE;
	public static int CH_HPREG3_FEE;
	public static int CH_HPREG4_FEE;
	public static int CH_HPREG5_FEE;
	public static int CH_HPREG6_FEE;
	public static int CH_HPREG7_FEE;
	public static int CH_HPREG8_FEE;
	public static int CH_HPREG9_FEE;
	public static int CH_HPREG10_FEE;
	public static int CH_HPREG11_FEE;
	public static int CH_HPREG12_FEE;
	public static int CH_HPREG13_FEE;
	public static long CH_EXPREG_FEE_RATIO;
	public static int CH_EXPREG1_FEE;
	public static int CH_EXPREG2_FEE;
	public static int CH_EXPREG3_FEE;
	public static int CH_EXPREG4_FEE;
	public static int CH_EXPREG5_FEE;
	public static int CH_EXPREG6_FEE;
	public static int CH_EXPREG7_FEE;
	public static long CH_SUPPORT_FEE_RATIO;
	public static int CH_SUPPORT1_FEE;
	public static int CH_SUPPORT2_FEE;
	public static int CH_SUPPORT3_FEE;
	public static int CH_SUPPORT4_FEE;
	public static int CH_SUPPORT5_FEE;
	public static int CH_SUPPORT6_FEE;
	public static int CH_SUPPORT7_FEE;
	public static int CH_SUPPORT8_FEE;
	public static long CH_CURTAIN_FEE_RATIO;
	public static int CH_CURTAIN1_FEE;
	public static int CH_CURTAIN2_FEE;
	public static long CH_FRONT_FEE_RATIO;
	public static int CH_FRONT1_FEE;
	public static int CH_FRONT2_FEE;
	public static boolean CH_BUFF_FREE;
	// --------------------------------------------------
	// Castle Settings
	// --------------------------------------------------
	public static long CS_TELE_FEE_RATIO;
	public static int CS_TELE1_FEE;
	public static int CS_TELE2_FEE;
	public static long CS_MPREG_FEE_RATIO;
	public static int CS_MPREG1_FEE;
	public static int CS_MPREG2_FEE;
	public static long CS_HPREG_FEE_RATIO;
	public static int CS_HPREG1_FEE;
	public static int CS_HPREG2_FEE;
	public static long CS_EXPREG_FEE_RATIO;
	public static int CS_EXPREG1_FEE;
	public static int CS_EXPREG2_FEE;
	public static long CS_SUPPORT_FEE_RATIO;
	public static int CS_SUPPORT1_FEE;
	public static int CS_SUPPORT2_FEE;
	public static List<String> CL_SET_SIEGE_TIME_LIST;
	public static List<Integer> SIEGE_HOUR_LIST_MORNING;
	public static List<Integer> SIEGE_HOUR_LIST_AFTERNOON;
	public static int OUTER_DOOR_UPGRADE_PRICE2;
	public static int OUTER_DOOR_UPGRADE_PRICE3;
	public static int OUTER_DOOR_UPGRADE_PRICE5;
	public static int INNER_DOOR_UPGRADE_PRICE2;
	public static int INNER_DOOR_UPGRADE_PRICE3;
	public static int INNER_DOOR_UPGRADE_PRICE5;
	public static int WALL_UPGRADE_PRICE2;
	public static int WALL_UPGRADE_PRICE3;
	public static int WALL_UPGRADE_PRICE5;
	public static int TRAP_UPGRADE_PRICE1;
	public static int TRAP_UPGRADE_PRICE2;
	public static int TRAP_UPGRADE_PRICE3;
	public static int TRAP_UPGRADE_PRICE4;
	
	// --------------------------------------------------
	// Fortress Settings
	// --------------------------------------------------
	public static long FS_TELE_FEE_RATIO;
	public static int FS_TELE1_FEE;
	public static int FS_TELE2_FEE;
	public static long FS_MPREG_FEE_RATIO;
	public static int FS_MPREG1_FEE;
	public static int FS_MPREG2_FEE;
	public static long FS_HPREG_FEE_RATIO;
	public static int FS_HPREG1_FEE;
	public static int FS_HPREG2_FEE;
	public static long FS_EXPREG_FEE_RATIO;
	public static int FS_EXPREG1_FEE;
	public static int FS_EXPREG2_FEE;
	public static long FS_SUPPORT_FEE_RATIO;
	public static int FS_SUPPORT1_FEE;
	public static int FS_SUPPORT2_FEE;
	public static int FS_BLOOD_OATH_COUNT;
	public static int FS_UPDATE_FRQ;
	public static int FS_MAX_SUPPLY_LEVEL;
	public static int FS_FEE_FOR_CASTLE;
	public static int FS_MAX_OWN_TIME;
	// --------------------------------------------------
	// Feature Settings
	// --------------------------------------------------
	public static int TAKE_FORT_POINTS;
	public static int LOOSE_FORT_POINTS;
	public static int TAKE_CASTLE_POINTS;
	public static int LOOSE_CASTLE_POINTS;
	public static int CASTLE_DEFENDED_POINTS;
	public static int FESTIVAL_WIN_POINTS;
	public static int HERO_POINTS;
	public static int ROYAL_GUARD_COST;
	public static int KNIGHT_UNIT_COST;
	public static int KNIGHT_REINFORCE_COST;
	public static int BALLISTA_POINTS;
	public static int BLOODALLIANCE_POINTS;
	public static int BLOODOATH_POINTS;
	public static int KNIGHTSEPAULETTE_POINTS;
	public static int REPUTATION_SCORE_PER_KILL;
	public static int JOIN_ACADEMY_MIN_REP_SCORE;
	public static int JOIN_ACADEMY_MAX_REP_SCORE;
	public static int RAID_RANKING_1ST;
	public static int RAID_RANKING_2ND;
	public static int RAID_RANKING_3RD;
	public static int RAID_RANKING_4TH;
	public static int RAID_RANKING_5TH;
	public static int RAID_RANKING_6TH;
	public static int RAID_RANKING_7TH;
	public static int RAID_RANKING_8TH;
	public static int RAID_RANKING_9TH;
	public static int RAID_RANKING_10TH;
	public static int RAID_RANKING_UP_TO_50TH;
	public static int RAID_RANKING_UP_TO_100TH;
	public static int CLAN_LEVEL_6_COST;
	public static int CLAN_LEVEL_7_COST;
	public static int CLAN_LEVEL_8_COST;
	public static int CLAN_LEVEL_9_COST;
	public static int CLAN_LEVEL_10_COST;
	public static int CLAN_LEVEL_11_COST;
	public static int CLAN_LEVEL_6_REQUIREMENT;
	public static int CLAN_LEVEL_7_REQUIREMENT;
	public static int CLAN_LEVEL_8_REQUIREMENT;
	public static int CLAN_LEVEL_9_REQUIREMENT;
	public static int CLAN_LEVEL_10_REQUIREMENT;
	public static int CLAN_LEVEL_11_REQUIREMENT;
	public static boolean ALLOW_WYVERN_DURING_SIEGE;
	// --------------------------------------------------
	// General Settings
	// --------------------------------------------------
	public static boolean EVERYBODY_HAS_ADMIN_RIGHTS;
	public static boolean SERVER_LIST_BRACKET;
	public static int SERVER_LIST_TYPE;
	public static int SERVER_LIST_AGE;
	public static boolean SERVER_GMONLY;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_SILENCE;
	public static boolean GM_STARTUP_AUTO_LIST;
	public static boolean GM_STARTUP_DIET_MODE;
	public static String GM_ADMIN_MENU_STYLE;
	public static boolean GM_ITEM_RESTRICTION;
	public static boolean GM_SKILL_RESTRICTION;
	public static boolean GM_TRADE_RESTRICTED_ITEMS;
	public static boolean GM_RESTART_FIGHTING;
	public static boolean GM_ANNOUNCER_NAME;
	public static boolean GM_CRITANNOUNCER_NAME;
	public static boolean GM_GIVE_SPECIAL_SKILLS;
	public static boolean GM_GIVE_SPECIAL_AURA_SKILLS;
	public static boolean BYPASS_VALIDATION;
	public static boolean GAMEGUARD_ENFORCE;
	public static boolean GAMEGUARD_PROHIBITACTION;
	public static boolean LOG_CHAT;
	public static boolean LOG_AUTO_ANNOUNCEMENTS;
	public static boolean LOG_ITEMS;
	public static boolean LOG_ITEMS_SMALL_LOG;
	public static boolean LOG_ITEM_ENCHANTS;
	public static boolean LOG_SKILL_ENCHANTS;
	public static boolean GMAUDIT;
	public static boolean LOG_GAME_DAMAGE;
	public static int LOG_GAME_DAMAGE_THRESHOLD;
	public static boolean SKILL_CHECK_ENABLE;
	public static boolean SKILL_CHECK_REMOVE;
	public static boolean SKILL_CHECK_GM;
	public static boolean DEBUG;
	public static boolean PACKET_HANDLER_DEBUG;
	public static boolean DEVELOPER;
	public static boolean ACCEPT_GEOEDITOR_CONN;
	public static boolean ALT_DEV_NO_HANDLERS;
	public static boolean ALT_DEV_NO_QUESTS;
	public static boolean ALT_DEV_NO_SPAWNS;
	public static int THREAD_P_EFFECTS;
	public static int THREAD_P_GENERAL;
	public static int GENERAL_PACKET_THREAD_CORE_SIZE;
	public static int IO_PACKET_THREAD_CORE_SIZE;
	public static int GENERAL_THREAD_CORE_SIZE;
	public static int AI_MAX_THREAD;
	public static int CLIENT_PACKET_QUEUE_SIZE;
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE;
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND;
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL;
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND;
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN;
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN;
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN;
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN;
	public static boolean DEADLOCK_DETECTOR;
	public static int DEADLOCK_CHECK_INTERVAL;
	public static boolean RESTART_ON_DEADLOCK;
	public static boolean ALLOW_DISCARDITEM;
	public static int AUTODESTROY_ITEM_AFTER;
	public static int HERB_AUTO_DESTROY_TIME;
	public static List<Integer> LIST_PROTECTED_ITEMS;
	public static boolean DATABASE_CLEAN_UP;
	public static long CONNECTION_CLOSE_TIME;
	public static int CHAR_STORE_INTERVAL;
	public static boolean LAZY_ITEMS_UPDATE;
	public static boolean UPDATE_ITEMS_ON_CHAR_STORE;
	public static boolean DESTROY_DROPPED_PLAYER_ITEM;
	public static boolean DESTROY_EQUIPABLE_PLAYER_ITEM;
	public static boolean SAVE_DROPPED_ITEM;
	public static boolean EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD;
	public static int SAVE_DROPPED_ITEM_INTERVAL;
	public static boolean CLEAR_DROPPED_ITEM_TABLE;
	public static boolean AUTODELETE_INVALID_QUEST_DATA;
	public static boolean PRECISE_DROP_CALCULATION;
	public static boolean MULTIPLE_ITEM_DROP;
	public static boolean FORCE_INVENTORY_UPDATE;
	public static boolean LAZY_CACHE;
	public static boolean CACHE_CHAR_NAMES;
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static int MIN_MONSTER_ANIMATION;
	public static int MAX_MONSTER_ANIMATION;
	public static int COORD_SYNCHRONIZE;
	public static boolean ENABLE_FALLING_DAMAGE;
	public static boolean GRIDS_ALWAYS_ON;
	public static int GRID_NEIGHBOR_TURNON_TIME;
	public static int GRID_NEIGHBOR_TURNOFF_TIME;
	public static int WORLD_X_MIN;
	public static int WORLD_X_MAX;
	public static int WORLD_Y_MIN;
	public static int WORLD_Y_MAX;
	public static int GEODATA;
	public static File GEODATA_DIR;
	public static File PATHNODE_DIR;
	public static boolean GEODATA_CELLFINDING;
	public static String PATHFIND_BUFFERS;
	public static float LOW_WEIGHT;
	public static float MEDIUM_WEIGHT;
	public static float HIGH_WEIGHT;
	public static boolean ADVANCED_DIAGONAL_STRATEGY;
	public static float DIAGONAL_WEIGHT;
	public static int MAX_POSTFILTER_PASSES;
	public static boolean DEBUG_PATH;
	public static boolean FORCE_GEODATA;
	public static boolean MOVE_BASED_KNOWNLIST;
	public static long KNOWNLIST_UPDATE_INTERVAL;
	public static int PEACE_ZONE_MODE;
	public static String DEFAULT_GLOBAL_CHAT;
	public static String DEFAULT_TRADE_CHAT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean WAREHOUSE_CACHE;
	public static int WAREHOUSE_CACHE_TIME;
	public static boolean ALLOW_REFUND;
	public static boolean ALLOW_MAIL;
	public static boolean ALLOW_ATTACHMENTS;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_RACE;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_RENTPET;
	public static boolean ALLOWFISHING;
	public static boolean ALLOW_BOAT;
	public static int BOAT_BROADCAST_RADIUS;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_MANOR;
	public static boolean ALLOW_PET_WALKERS;
	public static boolean SERVER_NEWS;
	public static int COMMUNITY_TYPE;
	public static boolean BBS_SHOW_PLAYERLIST;
	public static String BBS_DEFAULT;
	public static boolean SHOW_LEVEL_COMMUNITYBOARD;
	public static boolean SHOW_STATUS_COMMUNITYBOARD;
	public static int NAME_PAGE_SIZE_COMMUNITYBOARD;
	public static int NAME_PER_ROW_COMMUNITYBOARD;
	public static boolean USE_SAY_FILTER;
	public static String CHAT_FILTER_CHARS;
	public static int[] BAN_CHAT_CHANNELS;
	public static int ALT_OLY_START_TIME;
	public static int ALT_OLY_MIN;
	public static long ALT_OLY_CPERIOD;
	public static long ALT_OLY_BATTLE;
	public static long ALT_OLY_WPERIOD;
	public static long ALT_OLY_VPERIOD;
	public static int ALT_OLY_START_POINTS;
	public static int ALT_OLY_WEEKLY_POINTS;
	public static int ALT_OLY_CLASSED;
	public static int ALT_OLY_NONCLASSED;
	public static int ALT_OLY_TEAMS;
	public static int ALT_OLY_REG_DISPLAY;
	public static int[][] ALT_OLY_CLASSED_REWARD;
	public static int[][] ALT_OLY_NONCLASSED_REWARD;
	public static int[][] ALT_OLY_TEAM_REWARD;
	public static int ALT_OLY_COMP_RITEM;
	public static int ALT_OLY_MIN_MATCHES;
	public static int ALT_OLY_GP_PER_POINT;
	public static int ALT_OLY_HERO_POINTS;
	public static int ALT_OLY_RANK1_POINTS;
	public static int ALT_OLY_RANK2_POINTS;
	public static int ALT_OLY_RANK3_POINTS;
	public static int ALT_OLY_RANK4_POINTS;
	public static int ALT_OLY_RANK5_POINTS;
	public static int ALT_OLY_MAX_POINTS;
	public static int ALT_OLY_DIVIDER_CLASSED;
	public static int ALT_OLY_DIVIDER_NON_CLASSED;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_NON_CLASSED;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_CLASSED;
	public static int ALT_OLY_MAX_WEEKLY_MATCHES_TEAM;
	public static boolean ALT_OLY_LOG_FIGHTS;
	public static boolean ALT_OLY_SHOW_MONTHLY_WINNERS;
	public static boolean ALT_OLY_ANNOUNCE_GAMES;
	public static List<Integer> LIST_OLY_RESTRICTED_ITEMS;
	public static int ALT_OLY_ENCHANT_LIMIT;
	public static int ALT_OLY_WAIT_TIME;
	public static int ALT_MANOR_REFRESH_TIME;
	public static int ALT_MANOR_REFRESH_MIN;
	public static int ALT_MANOR_APPROVE_TIME;
	public static int ALT_MANOR_APPROVE_MIN;
	public static int ALT_MANOR_MAINTENANCE_PERIOD;
	public static boolean ALT_MANOR_SAVE_ALL_ACTIONS;
	public static int ALT_MANOR_SAVE_PERIOD_RATE;
	public static long ALT_LOTTERY_PRIZE;
	public static long ALT_LOTTERY_TICKET_PRICE;
	public static float ALT_LOTTERY_5_NUMBER_RATE;
	public static float ALT_LOTTERY_4_NUMBER_RATE;
	public static float ALT_LOTTERY_3_NUMBER_RATE;
	public static long ALT_LOTTERY_2_AND_1_NUMBER_PRIZE;
	public static boolean ALT_ITEM_AUCTION_ENABLED;
	public static int ALT_ITEM_AUCTION_EXPIRED_AFTER;
	public static long ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
	public static int FS_TIME_ATTACK;
	public static int FS_TIME_COOLDOWN;
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_WARMUP;
	public static int FS_PARTY_MEMBER_COUNT;
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static float RIFT_BOSS_ROOM_TIME_MUTIPLY;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static int DEFAULT_PUNISH;
	public static int DEFAULT_PUNISH_PARAM;
	public static boolean ONLY_GM_ITEMS_FREE;
	public static boolean JAIL_IS_PVP;
	public static boolean JAIL_DISABLE_CHAT;
	public static boolean JAIL_DISABLE_TRANSACTION;
	public static boolean CUSTOM_SPAWNLIST_TABLE;
	public static boolean SAVE_GMSPAWN_ON_CUSTOM;
	public static boolean CUSTOM_NPC_TABLE;
	public static boolean CUSTOM_NPC_SKILLS_TABLE;
	public static boolean CUSTOM_TELEPORT_TABLE;
	public static boolean CUSTOM_DROPLIST_TABLE;
	public static boolean CUSTOM_MERCHANT_TABLES;
	public static boolean CUSTOM_NPCBUFFER_TABLES;
	public static boolean CUSTOM_SKILLS_LOAD;
	public static boolean CUSTOM_ITEMS_LOAD;
	public static boolean CUSTOM_MULTISELL_LOAD;
	public static int ALT_BIRTHDAY_GIFT;
	public static String ALT_BIRTHDAY_MAIL_SUBJECT;
	public static String ALT_BIRTHDAY_MAIL_TEXT;
	public static boolean ENABLE_BLOCK_CHECKER_EVENT;
	public static int MIN_BLOCK_CHECKER_TEAM_MEMBERS;
	public static boolean HBCE_FAIR_PLAY;
	public static boolean HELLBOUND_WITHOUT_QUEST;
	public static int PLAYER_MOVEMENT_BLOCK_TIME;
	public static boolean CLEAR_CREST_CACHE;
	public static int NORMAL_ENCHANT_COST_MULTIPLIER;
	public static int SAFE_ENCHANT_COST_MULTIPLIER;
	// --------------------------------------------------
	// FloodProtector Settings
	// --------------------------------------------------
	public static FloodProtectorConfig FLOOD_PROTECTOR_USE_ITEM;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ROLL_DICE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_FIREWORK;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_PET_SUMMON;
	public static FloodProtectorConfig FLOOD_PROTECTOR_HERO_VOICE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_GLOBAL_CHAT;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SUBCLASS;
	public static FloodProtectorConfig FLOOD_PROTECTOR_DROP_ITEM;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SERVER_BYPASS;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MULTISELL;
	public static FloodProtectorConfig FLOOD_PROTECTOR_TRANSACTION;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MANUFACTURE;
	public static FloodProtectorConfig FLOOD_PROTECTOR_MANOR;
	public static FloodProtectorConfig FLOOD_PROTECTOR_SENDMAIL;
	public static FloodProtectorConfig FLOOD_PROTECTOR_CHARACTER_SELECT;
	public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_AUCTION;
	// --------------------------------------------------
	// Mods Settings
	// --------------------------------------------------
	public static boolean HARD_CHAMPION_ENABLE;
	public static boolean HARD_CHAMPION_PASSIVE;
	public static int HARD_CHAMPION_FREQUENCY;
	public static String HARD_CHAMP_TITLE;
	public static int HARD_CHAMP_MIN_LVL;
	public static int HARD_CHAMP_MAX_LVL;
	public static int HARD_CHAMPION_HP;
	public static int HARD_CHAMPION_REWARDS;
	public static float HARD_CHAMPION_ADENAS_REWARDS;
	public static float HARD_CHAMPION_HP_REGEN;
	public static float HARD_CHAMPION_ATK;
	public static float HARD_CHAMPION_SPD_ATK;
	public static int HARD_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE;
	public static int HARD_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE;
	public static int HARD_CHAMPION_REWARD_ID;
	public static int HARD_CHAMPION_REWARD_QTY;
	public static boolean HARD_CHAMPION_ENABLE_VITALITY;
	public static boolean HARD_CHAMPION_ENABLE_IN_INSTANCES;
	//
	public static boolean EASY_CHAMPION_ENABLE;
	public static boolean EASY_CHAMPION_PASSIVE;
	public static int EASY_CHAMPION_FREQUENCY;
	public static String EASY_CHAMP_TITLE;
	public static int EASY_CHAMP_MIN_LVL;
	public static int EASY_CHAMP_MAX_LVL;
	public static int EASY_CHAMPION_HP;
	public static int EASY_CHAMPION_REWARDS;
	public static float EASY_CHAMPION_ADENAS_REWARDS;
	public static float EASY_CHAMPION_HP_REGEN;
	public static float EASY_CHAMPION_ATK;
	public static float EASY_CHAMPION_SPD_ATK;
	public static int EASY_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE;
	public static int EASY_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE;
	public static int EASY_CHAMPION_REWARD_ID;
	public static int EASY_CHAMPION_REWARD_QTY;
	public static boolean EASY_CHAMPION_ENABLE_VITALITY;
	public static boolean EASY_CHAMPION_ENABLE_IN_INSTANCES;
	//
	public static boolean TVT_EVENT_ENABLED;
	public static boolean TVT_EVENT_IN_INSTANCE;
	public static String TVT_EVENT_INSTANCE_FILE;
	public static String[] TVT_EVENT_INTERVAL;
	public static int TVT_EVENT_PARTICIPATION_TIME;
	public static int TVT_EVENT_RUNNING_TIME;
	public static int TVT_EVENT_PARTICIPATION_NPC_ID;
	public static int[] TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] TVT_EVENT_PARTICIPATION_FEE = new int[2];
	public static int TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static int TVT_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
	public static String TVT_EVENT_TEAM_1_NAME;
	public static int[] TVT_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String TVT_EVENT_TEAM_2_NAME;
	public static int[] TVT_EVENT_TEAM_2_COORDINATES = new int[3];
	public static List<int[]> TVT_EVENT_REWARDS;
	public static boolean TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean TVT_EVENT_SCROLL_ALLOWED;
	public static boolean TVT_EVENT_POTIONS_ALLOWED;
	public static boolean TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> TVT_DOORS_IDS_TO_OPEN;
	public static List<Integer> TVT_DOORS_IDS_TO_CLOSE;
	public static boolean TVT_REWARD_TEAM_TIE;
	public static byte TVT_EVENT_MIN_LVL;
	public static byte TVT_EVENT_MAX_LVL;
	public static int TVT_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> TVT_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> TVT_EVENT_MAGE_BUFFS;
	public static int TVT_EVENT_MAX_PARTICIPANTS_PER_IP;
	public static boolean TVT_ALLOW_VOICED_COMMAND;
	public static boolean ALLOW_WEDDING;
	public static int WEDDING_PRICE;
	public static boolean WEDDING_PUNISH_INFIDELITY;
	public static boolean WEDDING_TELEPORT;
	public static int WEDDING_TELEPORT_PRICE;
	public static int WEDDING_TELEPORT_DURATION;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	public static int WEDDING_DIVORCE_COSTS;
	public static boolean HELLBOUND_STATUS;
	public static boolean BANKING_SYSTEM_ENABLED;
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int BANKING_SYSTEM_ADENA;
	public static boolean ENABLE_WAREHOUSESORTING_CLAN;
	public static boolean ENABLE_WAREHOUSESORTING_PRIVATE;
	public static boolean OFFLINE_TRADE_ENABLE;
	public static boolean OFFLINE_CRAFT_ENABLE;
	public static boolean OFFLINE_MODE_IN_PEACE_ZONE;
	public static boolean OFFLINE_MODE_NO_DAMAGE;
	public static boolean RESTORE_OFFLINERS;
	public static int OFFLINE_MAX_DAYS;
	public static boolean OFFLINE_DISCONNECT_FINISHED;
	public static boolean OFFLINE_SET_NAME_COLOR;
	public static int OFFLINE_NAME_COLOR;
	public static boolean OFFLINE_FAME;
	public static boolean ENABLE_MANA_POTIONS_SUPPORT;
	public static boolean DISPLAY_SERVER_TIME;
	public static boolean WELCOME_MESSAGE_ENABLED;
	public static String WELCOME_MESSAGE_TEXT;
	public static int WELCOME_MESSAGE_TIME;
	public static boolean ANTIFEED_ENABLE;
	public static boolean ANTIFEED_DUALBOX;
	public static boolean ANTIFEED_DISCONNECTED_AS_DUALBOX;
	public static int ANTIFEED_INTERVAL;
	public static boolean ANNOUNCE_PK_PVP;
	public static boolean ANNOUNCE_PK_PVP_NORMAL_MESSAGE;
	public static String ANNOUNCE_PK_MSG;
	public static String ANNOUNCE_PVP_MSG;
	public static boolean CHAT_ADMIN;
	public static boolean MULTILANG_ENABLE;
	public static List<String> MULTILANG_ALLOWED = new ArrayList<>();
	public static String MULTILANG_DEFAULT;
	public static boolean MULTILANG_VOICED_ALLOW;
	public static boolean MULTILANG_SM_ENABLE;
	public static List<String> MULTILANG_SM_ALLOWED = new ArrayList<>();
	public static boolean MULTILANG_NS_ENABLE;
	public static List<String> MULTILANG_NS_ALLOWED = new ArrayList<>();
	public static boolean L2WALKER_PROTECTION;
	public static int DUALBOX_CHECK_MAX_PLAYERS_PER_IP;
	public static int DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP;
	public static int DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP;
	public static Map<Integer, Integer> DUALBOX_CHECK_WHITELIST;
	public static boolean ALLOW_CHANGE_PASSWORD;
	// --------------------------------------------------
	// NPC Settings
	// --------------------------------------------------
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
	public static boolean ALT_ATTACKABLE_NPCS;
	public static boolean ALT_GAME_VIEWNPC;
	public static int MAX_DRIFT_RANGE;
	public static boolean DEEPBLUE_DROP_RULES;
	public static boolean DEEPBLUE_DROP_RULES_RAID;
	public static boolean SHOW_NPC_LVL;
	public static boolean SHOW_CREST_WITHOUT_QUEST;
	public static boolean ENABLE_RANDOM_ENCHANT_EFFECT;
	public static int MIN_NPC_LVL_DMG_PENALTY;
	public static Map<Integer, Float> NPC_DMG_PENALTY;
	public static Map<Integer, Float> NPC_CRIT_DMG_PENALTY;
	public static Map<Integer, Float> NPC_SKILL_DMG_PENALTY;
	public static int MIN_NPC_LVL_MAGIC_PENALTY;
	public static Map<Integer, Float> NPC_SKILL_CHANCE_PENALTY;
	public static int DECAY_TIME_TASK;
	public static int NPC_DECAY_TIME;
	public static int RAID_BOSS_DECAY_TIME;
	public static int SPOILED_DECAY_TIME;
	public static int MAX_SWEEPER_TIME;
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	public static boolean ALLOW_WYVERN_UPGRADER;
	public static List<Integer> LIST_PET_RENT_NPC;
	public static double RAID_HP_REGEN_MULTIPLIER;
	public static double RAID_MP_REGEN_MULTIPLIER;
	public static double RAID_PDEFENCE_MULTIPLIER;
	public static double RAID_MDEFENCE_MULTIPLIER;
	public static double RAID_PATTACK_MULTIPLIER;
	public static double RAID_MATTACK_MULTIPLIER;
	public static double RAID_MINION_RESPAWN_TIMER;
	public static Map<Integer, Integer> MINIONS_RESPAWN_TIME;
	public static float RAID_MIN_RESPAWN_MULTIPLIER;
	public static float RAID_MAX_RESPAWN_MULTIPLIER;
	public static boolean RAID_DISABLE_CURSE;
	public static int RAID_CHAOS_TIME;
	public static int GRAND_CHAOS_TIME;
	public static int MINION_CHAOS_TIME;
	public static int INVENTORY_MAXIMUM_PET;
	public static double PET_HP_REGEN_MULTIPLIER;
	public static double PET_MP_REGEN_MULTIPLIER;
	public static List<Integer> NON_TALKING_NPCS;
	
	// --------------------------------------------------
	// PvP Settings
	// --------------------------------------------------
	public static int KARMA_MIN_KARMA;
	public static int KARMA_MAX_KARMA;
	public static int KARMA_XP_DIVIDER;
	public static int KARMA_LOST_BASE;
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_AWARD_PK_KILL;
	public static int KARMA_PK_LIMIT;
	public static String KARMA_NONDROPPABLE_PET_ITEMS;
	public static String KARMA_NONDROPPABLE_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_PET_ITEMS;
	public static int[] KARMA_LIST_NONDROPPABLE_ITEMS;
	
	public static boolean CLASS_BALANCE;
	public static float DAGGER_VS_HEAVY;
	public static float DAGGER_VS_LIGHT;
	public static float DAGGER_VS_ROBE;
	public static float DAGGER_VS_HEAVY_AUTOATTACK;
	public static float DAGGER_VS_LIGHT_AUTOATTACK;
	public static float DAGGER_VS_ROBE_AUTOATTACK;
	public static float ARCHER_VS_HEAVY;
	public static float ARCHER_VS_LIGHT;
	public static float ARCHER_VS_ROBE;
	public static float ARCHER_VS_HEAVY_AUTOATTACK;
	public static float ARCHER_VS_LIGHT_AUTOATTACK;
	public static float ARCHER_VS_ROBE_AUTOATTACK;
	public static float BLUNT_VS_HEAVY;
	public static float BLUNT_VS_LIGHT;
	public static float BLUNT_VS_ROBE;
	public static float BLUNT_VS_HEAVY_AUTOATTACK;
	public static float BLUNT_VS_LIGHT_AUTOATTACK;
	public static float BLUNT_VS_ROBE_AUTOATTACK;
	public static float FIST_VS_HEAVY;
	public static float FIST_VS_LIGHT;
	public static float FIST_VS_ROBE;
	public static float FIST_VS_HEAVY_AUTOATTACK;
	public static float FIST_VS_LIGHT_AUTOATTACK;
	public static float FIST_VS_ROBE_AUTOATTACK;
	public static float DUAL_VS_HEAVY;
	public static float DUAL_VS_LIGHT;
	public static float DUAL_VS_ROBE;
	public static float DUAL_VS_HEAVY_AUTOATTACK;
	public static float DUAL_VS_LIGHT_AUTOATTACK;
	public static float DUAL_VS_ROBE_AUTOATTACK;
	public static float SWORD_VS_HEAVY;
	public static float SWORD_VS_LIGHT;
	public static float SWORD_VS_ROBE;
	public static float SWORD_VS_HEAVY_AUTOATTACK;
	public static float SWORD_VS_LIGHT_AUTOATTACK;
	public static float SWORD_VS_ROBE_AUTOATTACK;
	public static float POLE_VS_HEAVY;
	public static float POLE_VS_LIGHT;
	public static float POLE_VS_ROBE;
	public static float POLE_VS_HEAVY_AUTOATTACK;
	public static float POLE_VS_LIGHT_AUTOATTACK;
	public static float POLE_VS_ROBE_AUTOATTACK;
	public static float BIGBLUNT_VS_HEAVY;
	public static float BIGBLUNT_VS_LIGHT;
	public static float BIGBLUNT_VS_ROBE;
	public static float BIGBLUNT_VS_HEAVY_AUTOATTACK;
	public static float BIGBLUNT_VS_LIGHT_AUTOATTACK;
	public static float BIGBLUNT_VS_ROBE_AUTOATTACK;
	public static float BIGSWORD_VS_HEAVY;
	public static float BIGSWORD_VS_LIGHT;
	public static float BIGSWORD_VS_ROBE;
	public static float BIGSWORD_VS_HEAVY_AUTOATTACK;
	public static float BIGSWORD_VS_LIGHT_AUTOATTACK;
	public static float BIGSWORD_VS_ROBE_AUTOATTACK;
	public static float RAPIER_VS_HEAVY;
	public static float RAPIER_VS_LIGHT;
	public static float RAPIER_VS_ROBE;
	public static float RAPIER_VS_HEAVY_AUTOATTACK;
	public static float RAPIER_VS_LIGHT_AUTOATTACK;
	public static float RAPIER_VS_ROBE_AUTOATTACK;
	public static float ANCIENTSWORD_VS_HEAVY;
	public static float ANCIENTSWORD_VS_LIGHT;
	public static float ANCIENTSWORD_VS_ROBE;
	public static float ANCIENTSWORD_VS_HEAVY_AUTOATTACK;
	public static float ANCIENTSWORD_VS_LIGHT_AUTOATTACK;
	public static float ANCIENTSWORD_VS_ROBE_AUTOATTACK;
	public static float CROSSBOW_VS_HEAVY;
	public static float CROSSBOW_VS_LIGHT;
	public static float CROSSBOW_VS_ROBE;
	public static float CROSSBOW_VS_HEAVY_AUTOATTACK;
	public static float CROSSBOW_VS_LIGHT_AUTOATTACK;
	public static float CROSSBOW_VS_ROBE_AUTOATTACK;
	public static float DUALDAGGER_VS_HEAVY;
	public static float DUALDAGGER_VS_LIGHT;
	public static float DUALDAGGER_VS_ROBE;
	public static float DUALDAGGER_VS_HEAVY_AUTOATTACK;
	public static float DUALDAGGER_VS_LIGHT_AUTOATTACK;
	public static float DUALDAGGER_VS_ROBE_AUTOATTACK;
	public static float MAGE_VS_HEAVY;
	public static float MAGE_VS_LIGHT;
	public static float MAGE_VS_ROBE;
	
	public static boolean ENABLE_CUSTOM_PERIOD;
	public static int[] ALT_OLY_END_DATE;
	public static int[] ALT_OLY_END_HOUR = new int[3];
	
	// --------------------------------------------------
	// Rate Settings
	// --------------------------------------------------
	public static float RATE_XP;
	public static float RATE_SP;
	public static float RATE_PARTY_XP;
	public static float RATE_PARTY_SP;
	public static float RATE_CONSUMABLE_COST;
	public static float RATE_HB_TRUST_INCREASE;
	public static float RATE_HB_TRUST_DECREASE;
	public static float RATE_EXTRACTABLE;
	public static float RATE_DROP_ITEMS;
	public static float RATE_DROP_ITEMS_BY_RAID;
	public static float RATE_DROP_SPOIL;
	public static int RATE_DROP_MANOR;
	public static float RATE_QUEST_DROP;
	public static float RATE_QUEST_REWARD;
	public static float RATE_QUEST_REWARD_XP;
	public static float RATE_QUEST_REWARD_SP;
	public static float RATE_QUEST_REWARD_ADENA;
	public static boolean RATE_QUEST_REWARD_USE_MULTIPLIERS;
	public static float RATE_QUEST_REWARD_POTION;
	public static float RATE_QUEST_REWARD_SCROLL;
	public static float RATE_QUEST_REWARD_RECIPE;
	public static float RATE_QUEST_REWARD_MATERIAL;
	public static Map<Integer, Float> RATE_DROP_ITEMS_ID;
	public static float RATE_KARMA_EXP_LOST;
	public static float RATE_SIEGE_GUARDS_PRICE;
	public static float RATE_DROP_COMMON_HERBS;
	public static float RATE_DROP_HP_HERBS;
	public static float RATE_DROP_MP_HERBS;
	public static float RATE_DROP_SPECIAL_HERBS;
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	public static float PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static float SINEATER_XP_RATE;
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	public static double[] PLAYER_XP_PERCENT_LOST;
	
	// --------------------------------------------------
	// Seven Signs Settings
	// --------------------------------------------------
	public static boolean ALT_GAME_CASTLE_DAWN;
	public static boolean ALT_GAME_CASTLE_DUSK;
	public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
	public static int ALT_FESTIVAL_MIN_PLAYER;
	public static int ALT_MAXIMUM_PLAYER_CONTRIB;
	public static long ALT_FESTIVAL_MANAGER_START;
	public static long ALT_FESTIVAL_LENGTH;
	public static long ALT_FESTIVAL_CYCLE_LENGTH;
	public static long ALT_FESTIVAL_FIRST_SPAWN;
	public static long ALT_FESTIVAL_FIRST_SWARM;
	public static long ALT_FESTIVAL_SECOND_SPAWN;
	public static long ALT_FESTIVAL_SECOND_SWARM;
	public static long ALT_FESTIVAL_CHEST_SPAWN;
	public static double ALT_SIEGE_DAWN_GATES_PDEF_MULT;
	public static double ALT_SIEGE_DUSK_GATES_PDEF_MULT;
	public static double ALT_SIEGE_DAWN_GATES_MDEF_MULT;
	public static double ALT_SIEGE_DUSK_GATES_MDEF_MULT;
	public static boolean ALT_STRICT_SEVENSIGNS;
	public static boolean ALT_SEVENSIGNS_LAZY_UPDATE;
	public static int SSQ_DAWN_TICKET_QUANTITY;
	public static int SSQ_DAWN_TICKET_PRICE;
	public static int SSQ_DAWN_TICKET_BUNDLE;
	public static int SSQ_MANORS_AGREEMENT_ID;
	public static int SSQ_JOIN_DAWN_ADENA_FEE;
	
	// --------------------------------------------------
	// Server Settings
	// --------------------------------------------------
	public static int PORT_GAME;
	public static int PORT_LOGIN;
	public static String LOGIN_BIND_ADDRESS;
	public static int LOGIN_TRY_BEFORE_BAN;
	public static int LOGIN_BLOCK_AFTER_BAN;
	public static String GAMESERVER_HOSTNAME;
	public static String DATABASE_DRIVER;
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;
	public static int DATABASE_MAX_CONNECTIONS;
	public static int DATABASE_MAX_IDLE_TIME;
	public static int MAXIMUM_ONLINE_USERS;
	public static String CNAME_TEMPLATE;
	public static String PET_NAME_TEMPLATE;
	public static String CLAN_NAME_TEMPLATE;
	public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
	public static File DATAPACK_ROOT;
	public static boolean ACCEPT_ALTERNATE_ID;
	public static int REQUEST_ID;
	public static boolean RESERVE_HOST_ON_LOGIN = false;
	public static List<Integer> PROTOCOL_LIST;
	public static boolean LOG_LOGIN_CONTROLLER;
	public static boolean LOGIN_SERVER_SCHEDULE_RESTART;
	public static long LOGIN_SERVER_SCHEDULE_RESTART_TIME;
	
	// --------------------------------------------------
	// CommunityServer Settings
	// --------------------------------------------------
	public static boolean ENABLE_COMMUNITY_BOARD;
	public static String COMMUNITY_SERVER_ADDRESS;
	public static int COMMUNITY_SERVER_PORT;
	public static byte[] COMMUNITY_SERVER_HEX_ID;
	public static int COMMUNITY_SERVER_SQL_DP_ID;
	
	// --------------------------------------------------
	// MMO Settings
	// --------------------------------------------------
	public static int MMO_SELECTOR_SLEEP_TIME;
	public static int MMO_MAX_SEND_PER_PASS;
	public static int MMO_MAX_READ_PER_PASS;
	public static int MMO_HELPER_BUFFER_COUNT;
	public static boolean MMO_TCP_NODELAY;
	
	// --------------------------------------------------
	// Vitality Settings
	// --------------------------------------------------
	public static boolean ENABLE_VITALITY;
	public static boolean RECOVER_VITALITY_ON_RECONNECT;
	public static boolean ENABLE_DROP_VITALITY_HERBS;
	public static float RATE_VITALITY_LEVEL_1;
	public static float RATE_VITALITY_LEVEL_2;
	public static float RATE_VITALITY_LEVEL_3;
	public static float RATE_VITALITY_LEVEL_4;
	public static float RATE_DROP_VITALITY_HERBS;
	public static float RATE_RECOVERY_VITALITY_PEACE_ZONE;
	public static float RATE_VITALITY_LOST;
	public static float RATE_VITALITY_GAIN;
	public static float RATE_RECOVERY_ON_RECONNECT;
	public static int STARTING_VITALITY_POINTS;
	
	// --------------------------------------------------
	// No classification assigned to the following yet
	// --------------------------------------------------
	public static int MAX_ITEM_IN_PACKET;
	public static boolean CHECK_KNOWN;
	public static int GAME_SERVER_LOGIN_PORT;
	public static String GAME_SERVER_LOGIN_HOST;
	public static List<String> GAME_SERVER_SUBNETS;
	public static List<String> GAME_SERVER_HOSTS;
	public static int PVP_NORMAL_TIME;
	public static int PVP_PVP_TIME;
	
	public static enum IdFactoryType
	{
		Compaction,
		BitSet,
		Stack
	}
	
	public static IdFactoryType IDFACTORY_TYPE;
	public static boolean BAD_ID_CHECKING;
	
	public static double ENCHANT_CHANCE;
	public static int MAX_ENCHANT_LEVEL;
	public static double ENCHANT_CHANCE_ELEMENT_STONE;
	public static double ENCHANT_CHANCE_ELEMENT_CRYSTAL;
	public static double ENCHANT_CHANCE_ELEMENT_JEWEL;
	public static double ENCHANT_CHANCE_ELEMENT_ENERGY;
	public static int ENCHANT_SAFE_MAX;
	public static int ENCHANT_SAFE_MAX_FULL;
	public static int[] ENCHANT_BLACKLIST;
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	public static int AUGMENTATION_ACC_SKILL_CHANCE;
	public static boolean RETAIL_LIKE_AUGMENTATION;
	public static int[] RETAIL_LIKE_AUGMENTATION_NG_CHANCE;
	public static int[] RETAIL_LIKE_AUGMENTATION_MID_CHANCE;
	public static int[] RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE;
	public static int[] RETAIL_LIKE_AUGMENTATION_TOP_CHANCE;
	public static boolean RETAIL_LIKE_AUGMENTATION_ACCESSORY;
	public static int[] AUGMENTATION_BLACKLIST;
	public static boolean ALT_ALLOW_AUGMENT_PVP_ITEMS;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	public static boolean IS_TELNET_ENABLED;
	public static boolean SHOW_LICENCE;
	public static boolean ACCEPT_NEW_GAMESERVER;
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	public static boolean AUTO_CREATE_ACCOUNTS;
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	
	// chatfilter
	public static ArrayList<String> FILTER_LIST;
	
	// Security
	public static boolean SECOND_AUTH_ENABLED;
	public static int SECOND_AUTH_MAX_ATTEMPTS;
	public static long SECOND_AUTH_BAN_TIME;
	public static String SECOND_AUTH_REC_LINK;
	
	// Email
	public static String EMAIL_SERVERINFO_NAME;
	public static String EMAIL_SERVERINFO_ADDRESS;
	public static boolean EMAIL_SYS_ENABLED;
	public static String EMAIL_SYS_HOST;
	public static int EMAIL_SYS_PORT;
	public static boolean EMAIL_SYS_SMTP_AUTH;
	public static String EMAIL_SYS_FACTORY;
	public static boolean EMAIL_SYS_FACTORY_CALLBACK;
	public static String EMAIL_SYS_USERNAME;
	public static String EMAIL_SYS_PASSWORD;
	public static String EMAIL_SYS_ADDRESS;
	public static String EMAIL_SYS_SELECTQUERY;
	public static String EMAIL_SYS_DBFIELD;
	
	// Conquerable Halls Settings
	public static int CHS_CLAN_MINLEVEL;
	public static int CHS_MAX_ATTACKERS;
	public static int CHS_MAX_FLAGS_PER_CLAN;
	public static boolean CHS_ENABLE_FAME;
	public static int CHS_FAME_AMOUNT;
	public static int CHS_FAME_FREQUENCY;
	
	public static void load()
	{
		if (Server.serverMode == Server.MODE_GAMESERVER)
		{
			FLOOD_PROTECTOR_USE_ITEM = new FloodProtectorConfig("UseItemFloodProtector");
			FLOOD_PROTECTOR_ROLL_DICE = new FloodProtectorConfig("RollDiceFloodProtector");
			FLOOD_PROTECTOR_FIREWORK = new FloodProtectorConfig("FireworkFloodProtector");
			FLOOD_PROTECTOR_ITEM_PET_SUMMON = new FloodProtectorConfig("ItemPetSummonFloodProtector");
			FLOOD_PROTECTOR_HERO_VOICE = new FloodProtectorConfig("HeroVoiceFloodProtector");
			FLOOD_PROTECTOR_GLOBAL_CHAT = new FloodProtectorConfig("GlobalChatFloodProtector");
			FLOOD_PROTECTOR_SUBCLASS = new FloodProtectorConfig("SubclassFloodProtector");
			FLOOD_PROTECTOR_DROP_ITEM = new FloodProtectorConfig("DropItemFloodProtector");
			FLOOD_PROTECTOR_SERVER_BYPASS = new FloodProtectorConfig("ServerBypassFloodProtector");
			FLOOD_PROTECTOR_MULTISELL = new FloodProtectorConfig("MultiSellFloodProtector");
			FLOOD_PROTECTOR_TRANSACTION = new FloodProtectorConfig("TransactionFloodProtector");
			FLOOD_PROTECTOR_MANUFACTURE = new FloodProtectorConfig("ManufactureFloodProtector");
			FLOOD_PROTECTOR_MANOR = new FloodProtectorConfig("ManorFloodProtector");
			FLOOD_PROTECTOR_SENDMAIL = new FloodProtectorConfig("SendMailFloodProtector");
			FLOOD_PROTECTOR_CHARACTER_SELECT = new FloodProtectorConfig("CharacterSelectFloodProtector");
			FLOOD_PROTECTOR_ITEM_AUCTION = new FloodProtectorConfig("ItemAuctionFloodProtector");
			
			L2Properties serverSettings = new L2Properties();
			final File server = new File(SERVER_CONFIG);
			try (InputStream is = new FileInputStream(server))
			{
				serverSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Server settings!", e);
			}
			
			GAMESERVER_HOSTNAME = serverSettings.getProperty("GameserverHostname", "*");
			PORT_GAME = Integer.parseInt(serverSettings.getProperty("GameserverPort", "7777"));
			
			GAME_SERVER_LOGIN_PORT = Integer.parseInt(serverSettings.getProperty("LoginPort", "9014"));
			GAME_SERVER_LOGIN_HOST = serverSettings.getProperty("LoginHost", "127.0.0.1");
			
			REQUEST_ID = Integer.parseInt(serverSettings.getProperty("RequestServerID", "0"));
			ACCEPT_ALTERNATE_ID = Boolean.parseBoolean(serverSettings.getProperty("AcceptAlternateID", "True"));
			
			DATABASE_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
			DATABASE_URL = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l2jgs");
			DATABASE_LOGIN = serverSettings.getProperty("Login", "root");
			DATABASE_PASSWORD = serverSettings.getProperty("Password", "");
			DATABASE_MAX_CONNECTIONS = Integer.parseInt(serverSettings.getProperty("MaximumDbConnections", "10"));
			DATABASE_MAX_IDLE_TIME = Integer.parseInt(serverSettings.getProperty("MaximumDbIdleTime", "0"));
			
			try
			{
				DATAPACK_ROOT = new File(serverSettings.getProperty("DatapackRoot", ".").replaceAll("\\\\", "/")).getCanonicalFile();
			}
			catch (IOException e)
			{
				_log.log(Level.WARNING, "Error setting datapack root!", e);
				DATAPACK_ROOT = new File(".");
			}
			
			CNAME_TEMPLATE = serverSettings.getProperty("CnameTemplate", ".*");
			PET_NAME_TEMPLATE = serverSettings.getProperty("PetNameTemplate", ".*");
			CLAN_NAME_TEMPLATE = serverSettings.getProperty("ClanNameTemplate", ".*");
			
			MAX_CHARACTERS_NUMBER_PER_ACCOUNT = Integer.parseInt(serverSettings.getProperty("CharMaxNumber", "7"));
			MAXIMUM_ONLINE_USERS = Integer.parseInt(serverSettings.getProperty("MaximumOnlineUsers", "100"));
			
			String[] protocols = serverSettings.getProperty("AllowedProtocolRevisions", "267;268;271;273").split(";");
			PROTOCOL_LIST = new ArrayList<>(protocols.length);
			for (String protocol : protocols)
			{
				try
				{
					PROTOCOL_LIST.add(Integer.parseInt(protocol.trim()));
				}
				catch (NumberFormatException e)
				{
					_log.log(Level.WARNING, "Wrong config protocol version: " + protocol + ". Skipped.");
				}
			}
			
			// Hosts and Subnets
			IPConfigData ipcd = new IPConfigData();
			GAME_SERVER_SUBNETS = ipcd.getSubnets();
			GAME_SERVER_HOSTS = ipcd.getHosts();
			
			// Load Auto restart L2Properties file (if exists)
			final File auto_restart = new File(AUTO_RESTART);
			try (InputStream is = new FileInputStream(auto_restart))
			{
				L2Properties autores = new L2Properties();
				autores.load(is);
				AUTO_RESTART_ENABLE = Boolean.parseBoolean(autores.getProperty("EnableRestartSystem", "false"));
				AUTO_RESTART_TIME = Integer.parseInt(autores.getProperty("RestartSeconds", "360"));
				AUTO_RESTART_INTERVAL = autores.getProperty("RestartInterval", "00:00").split(",");
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + AUTO_RESTART + " File.");
			}
			
			// Load PVP Community L2Properties file (if exists)
			final File pvpcom = new File(COMMUNITY_PVP_CONFIG);
			try (InputStream is = new FileInputStream(pvpcom))
			{
				L2Properties pvp_com = new L2Properties();
				pvp_com.load(is);
				ALLOW_CLASS_MASTERSCB = pvp_com.getProperty("AllowClassMastersCB", "0");
				if ((ALLOW_CLASS_MASTERSCB.length() != 0) && !ALLOW_CLASS_MASTERSCB.equals("0"))
				{
					for (final String id : ALLOW_CLASS_MASTERSCB.split(","))
					{
						ALLOW_CLASS_MASTERS_LISTCB.add(Integer.parseInt(id));
					}
				}
				CLASS_MASTERS_PRICECB = pvp_com.getProperty("ClassMastersPriceCB", "0,0,0");
				if (CLASS_MASTERS_PRICECB.length() >= 5)
				{
					int level = 0;
					for (final String id : CLASS_MASTERS_PRICECB.split(","))
					{
						CLASS_MASTERS_PRICE_LISTCB[level] = Integer.parseInt(id);
						level++;
					}
				}
				CLASS_MASTERS_PRICE_ITEMCB = Integer.parseInt(pvp_com.getProperty("ClassMastersPriceItemCB", "57"));
				
				ALLOW_COMMUNITY_MULTISELL = Boolean.parseBoolean(pvp_com.getProperty("AllowCommunityMultisell", "false"));
				ALLOW_COMMUNITY_STATS = Boolean.parseBoolean(pvp_com.getProperty("AllowCommunityStats", "false"));
				ALLOW_COMMUNITY_TELEPORT = Boolean.parseBoolean(pvp_com.getProperty("AllowCommunityTeleport", "false"));
				ALLOW_COMMUNITY_BUFF = Boolean.parseBoolean(pvp_com.getProperty("AllowCommunityBuff", "false"));
				ALLOW_COMMUNITY_ENCHANT = Boolean.parseBoolean(pvp_com.getProperty("AllowCommunityEnchant", "false"));
				ALLOW_COMMUNITY_CLASS = Boolean.parseBoolean(pvp_com.getProperty("AllowCommunityClass", "false"));
				ENCHANT_ITEM = Integer.parseInt(pvp_com.getProperty("EnchantItem", "4037"));
				BUFF_COST = Boolean.parseBoolean(pvp_com.getProperty("BuffCost", "true"));
				ALLOW_COMMUNITY_SERVICES = Boolean.parseBoolean(pvp_com.getProperty("AllowCommunityServices", "false"));
				DelevelItemId = Integer.parseInt(pvp_com.getProperty("DelevelItemId", "4037"));
				DelevelItemCount = Integer.parseInt(pvp_com.getProperty("DelevelItemCount", "10"));
				NoblItemId = Integer.parseInt(pvp_com.getProperty("NoblItemId", "4037"));
				NoblItemCount = Integer.parseInt(pvp_com.getProperty("NoblItemCount", "50"));
				GenderItemId = Integer.parseInt(pvp_com.getProperty("GenderItemId", "4037"));
				GenderItemCount = Integer.parseInt(pvp_com.getProperty("GenderItemCount", "30"));
				HeroItemId = Integer.parseInt(pvp_com.getProperty("HeroItemId", "4037"));
				HeroItemCount = Integer.parseInt(pvp_com.getProperty("HeroItemCount", "100"));
				RecoveryPKItemId = Integer.parseInt(pvp_com.getProperty("RecoveryPKItemId", "4037"));
				RecoveryPKItemCount = Integer.parseInt(pvp_com.getProperty("RecoveryPKItemCount", "10"));
				RecoveryVitalityItemId = Integer.parseInt(pvp_com.getProperty("RecoveryVitalityItemId", "4037"));
				RecoveryVitalityItemCount = Integer.parseInt(pvp_com.getProperty("RecoveryVitalityItemCount", "10"));
				SPItemId = Integer.parseInt(pvp_com.getProperty("SPItemId", "4037"));
				SPItemCount = Integer.parseInt(pvp_com.getProperty("SPItemCount", "10"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + COMMUNITY_PVP_CONFIG + " File.");
			}
			
			// Load fake mods L2Properties file (if exists)
			final File fakemo = new File(FAKEPCS_CONFIG);
			try (InputStream is = new FileInputStream(fakemo))
			{
				L2Properties fakemo_mods = new L2Properties();
				fakemo_mods.load(is);
				ENABLE_FAKE_PCS = Boolean.parseBoolean(fakemo_mods.getProperty("EnableFakePcs", "False"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + FAKEPCS_CONFIG + " File.");
			}
			
			// Load event mods L2Properties file (if exists)
			final File eventmods = new File(EVENT_MODS);
			try (InputStream is = new FileInputStream(eventmods))
			{
				L2Properties event_mods = new L2Properties();
				event_mods.load(is);
				ENABLE_ELPY = Boolean.parseBoolean(event_mods.getProperty("EnableElpyEvent", "False"));
				EVENT_INTERVAL_ELPIES = Integer.parseInt(event_mods.getProperty("EventIntervalElpies", "180"));
				EVENT_TIME_ELPIES = Integer.parseInt(event_mods.getProperty("EventTimeElpies", "2"));
				EVENT_NUMBER_OF_SPAWNED_ELPIES = Integer.parseInt(event_mods.getProperty("EventNumberOfSpawnedElpies", "100"));
				ENABLE_RABBITS = Boolean.parseBoolean(event_mods.getProperty("EnableRabbitsEvent", "False"));
				EVENT_INTERVAL_RABBITS = Integer.parseInt(event_mods.getProperty("EventIntervalRabbits", "240"));
				EVENT_TIME_RABBITS = Integer.parseInt(event_mods.getProperty("EventTimeRabbits", "10"));
				EVENT_NUMBER_OF_SPAWNED_CHESTS = Integer.parseInt(event_mods.getProperty("EventNumberOfSpawnedChest", "100"));
				ENABLE_RACE = Boolean.parseBoolean(event_mods.getProperty("EnableRaceEvent", "False"));
				EVENT_INTERVAL_RACE = Integer.parseInt(event_mods.getProperty("EventIntervalRace", "300"));
				EVENT_REG_TIME_RACE = Integer.parseInt(event_mods.getProperty("EventRegTimeRace", "5"));
				EVENT_RUNNING_TIME_RACE = Integer.parseInt(event_mods.getProperty("EventRunningTimeRace", "10"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + EVENT_MODS + " File.");
			}
			
			// Load buffer L2Properties file (if exists)
			final File normalnpcbuffer = new File(NORMAL_BUFFER_CONFIG);
			try (InputStream is = new FileInputStream(normalnpcbuffer))
			{
				L2Properties normal_npc_buffer = new L2Properties();
				normal_npc_buffer.load(is);
				NormalNpcBuffer_SmartWindow = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableSmartWindow", "True"));
				NormalVIP_ONLY = Boolean.parseBoolean(normal_npc_buffer.getProperty("OnlyVip", "False"));
				NormalNpcBuffer_VIP = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableVIP", "False"));
				NormalNpcBuffer_EnableBuff = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableBuffSection", "True"));
				NormalNpcBuffer_EnableScheme = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableScheme", "True"));
				NormalNpcBuffer_EnableHeal = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableHeal", "True"));
				NormalNpcBuffer_EnableBuffs = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableBuffs", "True"));
				NormalNpcBuffer_EnableResist = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableResist", "True"));
				NormalNpcBuffer_EnableSong = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableSongs", "True"));
				NormalNpcBuffer_EnableDance = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableDances", "True"));
				NormalNpcBuffer_EnableChant = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableChants", "True"));
				NormalNpcBuffer_EnableOther = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableOther", "True"));
				NormalNpcBuffer_EnableSpecial = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableSpecial", "True"));
				NormalNpcBuffer_EnableCubic = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableCubic", "True"));
				NormalNpcBuffer_EnableCancel = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableRemoveBuffs", "True"));
				NormalNpcBuffer_EnableBuffSet = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableBuffSet", "True"));
				NormalNpcBuffer_EnableBuffPK = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableBuffForPK", "False"));
				NormalNpcBuffer_EnableFreeBuffs = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableFreeBuffs", "True"));
				NormalNpcBuffer_EnableTimeOut = Boolean.parseBoolean(normal_npc_buffer.getProperty("EnableTimeOut", "True"));
				NormalNpcBuffer_TimeOutTime = Integer.parseInt(normal_npc_buffer.getProperty("TimeoutTime", "10"));
				NormalNpcBuffer_MinLevel = Integer.parseInt(normal_npc_buffer.getProperty("MinimumLevel", "20"));
				NormalNpcBuffer_PriceCancel = Integer.parseInt(normal_npc_buffer.getProperty("RemoveBuffsPrice", "100000"));
				NormalNpcBuffer_PriceHeal = Integer.parseInt(normal_npc_buffer.getProperty("HealPrice", "100000"));
				NormalNpcBuffer_PriceBuffs = Integer.parseInt(normal_npc_buffer.getProperty("BuffsPrice", "100000"));
				NormalNpcBuffer_PriceResist = Integer.parseInt(normal_npc_buffer.getProperty("ResistPrice", "100000"));
				NormalNpcBuffer_PriceSong = Integer.parseInt(normal_npc_buffer.getProperty("SongPrice", "100000"));
				NormalNpcBuffer_PriceDance = Integer.parseInt(normal_npc_buffer.getProperty("DancePrice", "100000"));
				NormalNpcBuffer_PriceChant = Integer.parseInt(normal_npc_buffer.getProperty("ChantsPrice", "100000"));
				NormalNpcBuffer_PriceOther = Integer.parseInt(normal_npc_buffer.getProperty("OtherPrice", "100000"));
				NormalNpcBuffer_PriceSpecial = Integer.parseInt(normal_npc_buffer.getProperty("SpecialPrice", "100000"));
				NormalNpcBuffer_PriceCubic = Integer.parseInt(normal_npc_buffer.getProperty("CubicPrice", "100000"));
				NormalNpcBuffer_PriceSet = Integer.parseInt(normal_npc_buffer.getProperty("SetPrice", "10000000"));
				NormalNpcBuffer_PriceScheme = Integer.parseInt(normal_npc_buffer.getProperty("SchemePrice", "10000000"));
				NormalNpcBuffer_MaxScheme = Integer.parseInt(normal_npc_buffer.getProperty("MaxScheme", "4"));
				NormalNpcBuffer_consumableID = Integer.parseInt(normal_npc_buffer.getProperty("ConsumableID", "57"));
				NormalBuffMaxAmount = Integer.parseInt(normal_npc_buffer.getProperty("MaxBuffAmount", "24"));
				NormalDanceMaxAmout = Integer.parseInt(normal_npc_buffer.getProperty("MaxDanceAmount", "6"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + NORMAL_BUFFER_CONFIG + " File.");
			}
			
			// Load buffer L2Properties file (if exists)
			final File premiumnpcbuffer = new File(PREMIUM_BUFFER_CONFIG);
			try (InputStream is = new FileInputStream(premiumnpcbuffer))
			{
				L2Properties premium_npc_buffer = new L2Properties();
				premium_npc_buffer.load(is);
				PremiumNpcBuffer_SmartWindow = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableSmartWindow", "True"));
				PremiumVIP_ONLY = Boolean.parseBoolean(premium_npc_buffer.getProperty("OnlyVip", "False"));
				PremiumNpcBuffer_VIP = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableVIP", "False"));
				PremiumNpcBuffer_EnableBuff = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableBuffSection", "True"));
				PremiumNpcBuffer_EnableScheme = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableScheme", "True"));
				PremiumNpcBuffer_EnableHeal = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableHeal", "True"));
				PremiumNpcBuffer_EnableBuffs = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableBuffs", "True"));
				PremiumNpcBuffer_EnableResist = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableResist", "True"));
				PremiumNpcBuffer_EnableSong = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableSongs", "True"));
				PremiumNpcBuffer_EnableDance = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableDances", "True"));
				PremiumNpcBuffer_EnableChant = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableChants", "True"));
				PremiumNpcBuffer_EnableOther = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableOther", "True"));
				PremiumNpcBuffer_EnableSpecial = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableSpecial", "True"));
				PremiumNpcBuffer_EnableCubic = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableCubic", "True"));
				PremiumNpcBuffer_EnableCancel = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableRemoveBuffs", "True"));
				PremiumNpcBuffer_EnableBuffSet = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableBuffSet", "True"));
				PremiumNpcBuffer_EnableBuffPK = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableBuffForPK", "False"));
				PremiumNpcBuffer_EnableFreeBuffs = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableFreeBuffs", "True"));
				PremiumNpcBuffer_EnableTimeOut = Boolean.parseBoolean(premium_npc_buffer.getProperty("EnableTimeOut", "True"));
				PremiumNpcBuffer_TimeOutTime = Integer.parseInt(premium_npc_buffer.getProperty("TimeoutTime", "10"));
				PremiumNpcBuffer_MinLevel = Integer.parseInt(premium_npc_buffer.getProperty("MinimumLevel", "20"));
				PremiumNpcBuffer_PriceCancel = Integer.parseInt(premium_npc_buffer.getProperty("RemoveBuffsPrice", "100000"));
				PremiumNpcBuffer_PriceHeal = Integer.parseInt(premium_npc_buffer.getProperty("HealPrice", "100000"));
				PremiumNpcBuffer_PriceBuffs = Integer.parseInt(premium_npc_buffer.getProperty("BuffsPrice", "100000"));
				PremiumNpcBuffer_PriceResist = Integer.parseInt(premium_npc_buffer.getProperty("ResistPrice", "100000"));
				PremiumNpcBuffer_PriceSong = Integer.parseInt(premium_npc_buffer.getProperty("SongPrice", "100000"));
				PremiumNpcBuffer_PriceDance = Integer.parseInt(premium_npc_buffer.getProperty("DancePrice", "100000"));
				PremiumNpcBuffer_PriceChant = Integer.parseInt(premium_npc_buffer.getProperty("ChantsPrice", "100000"));
				PremiumNpcBuffer_PriceOther = Integer.parseInt(premium_npc_buffer.getProperty("OtherPrice", "100000"));
				PremiumNpcBuffer_PriceSpecial = Integer.parseInt(premium_npc_buffer.getProperty("SpecialPrice", "100000"));
				PremiumNpcBuffer_PriceCubic = Integer.parseInt(premium_npc_buffer.getProperty("CubicPrice", "100000"));
				PremiumNpcBuffer_PriceSet = Integer.parseInt(premium_npc_buffer.getProperty("SetPrice", "10000000"));
				PremiumNpcBuffer_PriceScheme = Integer.parseInt(premium_npc_buffer.getProperty("SchemePrice", "10000000"));
				PremiumNpcBuffer_MaxScheme = Integer.parseInt(premium_npc_buffer.getProperty("MaxScheme", "4"));
				PremiumNpcBuffer_consumableID = Integer.parseInt(premium_npc_buffer.getProperty("ConsumableID", "57"));
				PremiumBuffMaxAmount = Integer.parseInt(premium_npc_buffer.getProperty("MaxBuffAmount", "24"));
				PremiumDanceMaxAmout = Integer.parseInt(premium_npc_buffer.getProperty("MaxDanceAmount", "6"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + PREMIUM_BUFFER_CONFIG + " File.");
			}
			
			// Load class b L2Properties file (if exists)
			final File classb = new File(CLASS_BALANCE_CONFIG);
			try (InputStream is = new FileInputStream(classb))
			{
				L2Properties classbalance = new L2Properties();
				classbalance.load(is);
				CLASS_BALANCE = Boolean.parseBoolean(classbalance.getProperty("ClassBalance", "False"));
				DAGGER_VS_HEAVY = Float.parseFloat(classbalance.getProperty("DaggerVsHeavy", "1.00"));
				DAGGER_VS_LIGHT = Float.parseFloat(classbalance.getProperty("DaggerVsLight", "1.00"));
				DAGGER_VS_ROBE = Float.parseFloat(classbalance.getProperty("DaggerVsRobe", "1.00"));
				DAGGER_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("DaggerVsHeavyAutoattack", "1.00"));
				DAGGER_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("DaggerVsLightAutoattack", "1.00"));
				DAGGER_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("DaggerVsRobeAutoattack", "1.00"));
				ARCHER_VS_HEAVY = Float.parseFloat(classbalance.getProperty("ArcherVsHeavy", "1.00"));
				ARCHER_VS_LIGHT = Float.parseFloat(classbalance.getProperty("ArcherVsLight", "1.00"));
				ARCHER_VS_ROBE = Float.parseFloat(classbalance.getProperty("ArcherVsRobe", "1.00"));
				ARCHER_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("ArcherVsHeavyAutoattack", "1.00"));
				ARCHER_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("ArcherVsLightAutoattack", "1.00"));
				ARCHER_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("ArcherVsRobeAutoattack", "1.00"));
				BLUNT_VS_HEAVY = Float.parseFloat(classbalance.getProperty("BluntVsHeavy", "1.00"));
				BLUNT_VS_LIGHT = Float.parseFloat(classbalance.getProperty("BluntVsLight", "1.00"));
				BLUNT_VS_ROBE = Float.parseFloat(classbalance.getProperty("BluntVsRobe", "1.00"));
				BLUNT_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("BluntVsHeavyAutoattack", "1.00"));
				BLUNT_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("BluntVsLightAutoattack", "1.00"));
				BLUNT_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("BluntVsRobeAutoattack", "1.00"));
				FIST_VS_HEAVY = Float.parseFloat(classbalance.getProperty("FistVsHeavy", "1.00"));
				FIST_VS_LIGHT = Float.parseFloat(classbalance.getProperty("FistVsLight", "1.00"));
				FIST_VS_ROBE = Float.parseFloat(classbalance.getProperty("FistVsRobe", "1.00"));
				FIST_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("FistVsHeavyAutoattack", "1.00"));
				FIST_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("FistVsLightAutoattack", "1.00"));
				FIST_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("FistVsRobeAutoattack", "1.00"));
				DUAL_VS_HEAVY = Float.parseFloat(classbalance.getProperty("DualVsHeavy", "1.00"));
				DUAL_VS_LIGHT = Float.parseFloat(classbalance.getProperty("DualVsLight", "1.00"));
				DUAL_VS_ROBE = Float.parseFloat(classbalance.getProperty("DualVsRobe", "1.00"));
				DUAL_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("DualVsHeavyAutoattack", "1.00"));
				DUAL_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("DualVsLightAutoattack", "1.00"));
				DUAL_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("DualVsRobeAutoattack", "1.00"));
				SWORD_VS_HEAVY = Float.parseFloat(classbalance.getProperty("SwordVsHeavy", "1.00"));
				SWORD_VS_LIGHT = Float.parseFloat(classbalance.getProperty("SwordVsLight", "1.00"));
				SWORD_VS_ROBE = Float.parseFloat(classbalance.getProperty("SwordVsRobe", "1.00"));
				SWORD_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("SwordVsHeavyAutoattack", "1.00"));
				SWORD_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("SwordVsLightAutoattack", "1.00"));
				SWORD_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("SwordVsRobeAutoattack", "1.00"));
				POLE_VS_HEAVY = Float.parseFloat(classbalance.getProperty("PoleVsHeavy", "1.00"));
				POLE_VS_LIGHT = Float.parseFloat(classbalance.getProperty("PoleVsLight", "1.00"));
				POLE_VS_ROBE = Float.parseFloat(classbalance.getProperty("PoleVsRobe", "1.00"));
				POLE_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("PoleVsHeavyAutoattack", "1.00"));
				POLE_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("PoleVsLightAutoattack", "1.00"));
				POLE_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("PoleVsRobeAutoattack", "1.00"));
				BIGBLUNT_VS_HEAVY = Float.parseFloat(classbalance.getProperty("BigBluntVsHeavy", "1.00"));
				BIGBLUNT_VS_LIGHT = Float.parseFloat(classbalance.getProperty("BigBluntVsLight", "1.00"));
				BIGBLUNT_VS_ROBE = Float.parseFloat(classbalance.getProperty("BigBluntVsRobe", "1.00"));
				BIGBLUNT_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("BigBluntVsHeavyAutoattack", "1.00"));
				BIGBLUNT_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("BigBluntVsLightAutoattack", "1.00"));
				BIGBLUNT_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("BigBluntVsRobeAutoattack", "1.00"));
				BIGSWORD_VS_HEAVY = Float.parseFloat(classbalance.getProperty("BigSwordVsHeavy", "1.00"));
				BIGSWORD_VS_LIGHT = Float.parseFloat(classbalance.getProperty("BigSwordVsLight", "1.00"));
				BIGSWORD_VS_ROBE = Float.parseFloat(classbalance.getProperty("BigSwordVsRobe", "1.00"));
				BIGSWORD_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("BigSwordVsHeavyAutoattack", "1.00"));
				BIGSWORD_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("BigSwordVsLightAutoattack", "1.00"));
				BIGSWORD_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("BigSwordVsRobeAutoattack", "1.00"));
				RAPIER_VS_HEAVY = Float.parseFloat(classbalance.getProperty("RapierVsHeavy", "1.00"));
				RAPIER_VS_LIGHT = Float.parseFloat(classbalance.getProperty("RapierVsLight", "1.00"));
				RAPIER_VS_ROBE = Float.parseFloat(classbalance.getProperty("RapierVsRobe", "1.00"));
				RAPIER_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("RapierVsHeavyAutoattack", "1.00"));
				RAPIER_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("RapierVsLightAutoattack", "1.00"));
				RAPIER_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("RapierVsRobeAutoattack", "1.00"));
				ANCIENTSWORD_VS_HEAVY = Float.parseFloat(classbalance.getProperty("AncientSwordVsHeavy", "1.00"));
				ANCIENTSWORD_VS_LIGHT = Float.parseFloat(classbalance.getProperty("AncientSwordVsLight", "1.00"));
				ANCIENTSWORD_VS_ROBE = Float.parseFloat(classbalance.getProperty("AncientSwordVsRobe", "1.00"));
				ANCIENTSWORD_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("AncientSwordVsHeavyAutoattack", "1.00"));
				ANCIENTSWORD_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("AncientSwordVsLightAutoattack", "1.00"));
				ANCIENTSWORD_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("AncientSwordVsRobeAutoattack", "1.00"));
				CROSSBOW_VS_HEAVY = Float.parseFloat(classbalance.getProperty("CrossbowVsHeavy", "1.00"));
				CROSSBOW_VS_LIGHT = Float.parseFloat(classbalance.getProperty("CrossbowVsLight", "1.00"));
				CROSSBOW_VS_ROBE = Float.parseFloat(classbalance.getProperty("CrossbowVsRobe", "1.00"));
				CROSSBOW_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("CrossbowVsHeavyAutoattack", "1.00"));
				CROSSBOW_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("CrossbowVsLightAutoattack", "1.00"));
				CROSSBOW_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("CrossbowVsRobeAutoattack", "1.00"));
				DUALDAGGER_VS_HEAVY = Float.parseFloat(classbalance.getProperty("DualDaggerVsHeavy", "1.00"));
				DUALDAGGER_VS_LIGHT = Float.parseFloat(classbalance.getProperty("DualDaggerVsLight", "1.00"));
				DUALDAGGER_VS_ROBE = Float.parseFloat(classbalance.getProperty("DualDaggerVsRobe", "1.00"));
				DUALDAGGER_VS_HEAVY_AUTOATTACK = Float.parseFloat(classbalance.getProperty("DualDaggerVsHeavyAutoattack", "1.00"));
				DUALDAGGER_VS_LIGHT_AUTOATTACK = Float.parseFloat(classbalance.getProperty("DualDaggerVsLightAutoattack", "1.00"));
				DUALDAGGER_VS_ROBE_AUTOATTACK = Float.parseFloat(classbalance.getProperty("DualDaggerVsRobeAutoattack", "1.00"));
				MAGE_VS_HEAVY = Float.parseFloat(classbalance.getProperty("MageVsHeavy", "1.00"));
				MAGE_VS_LIGHT = Float.parseFloat(classbalance.getProperty("MageVsLight", "1.00"));
				MAGE_VS_ROBE = Float.parseFloat(classbalance.getProperty("MageVsRobe", "1.00"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + CLASS_BALANCE_CONFIG + " File.");
			}
			
			// Load Community Properties file (if exists)
			L2Properties communityServerSettings = new L2Properties();
			final File community = new File(COMMUNITY_CONFIG);
			try (InputStream is = new FileInputStream(community))
			{
				communityServerSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Community Server settings!", e);
			}
			
			ENABLE_COMMUNITY_BOARD = Boolean.parseBoolean(communityServerSettings.getProperty("EnableCommunityBoard", "False"));
			COMMUNITY_SERVER_ADDRESS = communityServerSettings.getProperty("CommunityServerHostname", "localhost");
			COMMUNITY_SERVER_PORT = Integer.parseInt(communityServerSettings.getProperty("CommunityServerPort", "9013"));
			COMMUNITY_SERVER_HEX_ID = new BigInteger(communityServerSettings.getProperty("CommunityServerHexId", "0"), 16).toByteArray();
			
			COMMUNITY_SERVER_SQL_DP_ID = Integer.parseInt(communityServerSettings.getProperty("CommunityServerSqlDpId", "200"));
			// Load VOTE_EVENT_NPC L2Properties file (if exists)
			final File vote3 = new File(VOTE_EVENT_NPC);
			try (InputStream is = new FileInputStream(vote3))
			{
				L2Properties vote3_load = new L2Properties();
				vote3_load.load(is);
				VOTE_LINK_HOPZONE = vote3_load.getProperty("HopzoneUrl", "null");
				VOTE_LINK_TOPZONE = vote3_load.getProperty("TopzoneUrl", "null");
				VOTE_REWARD_ID1 = Integer.parseInt(vote3_load.getProperty("VoteRewardId1", "300"));
				VOTE_REWARD_ID2 = Integer.parseInt(vote3_load.getProperty("VoteRewardId2", "300"));
				VOTE_REWARD_ID3 = Integer.parseInt(vote3_load.getProperty("VoteRewardId3", "300"));
				VOTE_REWARD_ID4 = Integer.parseInt(vote3_load.getProperty("VoteRewardId4", "300"));
				VOTE_REWARD_AMOUNT1 = Integer.parseInt(vote3_load.getProperty("VoteRewardAmount1", "300"));
				VOTE_REWARD_AMOUNT2 = Integer.parseInt(vote3_load.getProperty("VoteRewardAmount2", "300"));
				VOTE_REWARD_AMOUNT3 = Integer.parseInt(vote3_load.getProperty("VoteRewardAmount3", "300"));
				VOTE_REWARD_AMOUNT4 = Integer.parseInt(vote3_load.getProperty("VoteRewardAmount4", "300"));
				SECS_TO_VOTE = Integer.parseInt(vote3_load.getProperty("SecondsToVote", "20"));
				EXTRA_REW_VOTE_AM = Integer.parseInt(vote3_load.getProperty("ExtraRewVoteAm", "20"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + VOTE_EVENT_NPC + " File.");
			}
			
			// Load Feature L2Properties file (if exists)
			L2Properties Feature = new L2Properties();
			final File feature = new File(FEATURE_CONFIG);
			try (InputStream is = new FileInputStream(feature))
			{
				Feature.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Features settings!", e);
			}
			
			CH_TELE_FEE_RATIO = Long.parseLong(Feature.getProperty("ClanHallTeleportFunctionFeeRatio", "604800000"));
			CH_TELE1_FEE = Integer.parseInt(Feature.getProperty("ClanHallTeleportFunctionFeeLvl1", "7000"));
			CH_TELE2_FEE = Integer.parseInt(Feature.getProperty("ClanHallTeleportFunctionFeeLvl2", "14000"));
			CH_SUPPORT_FEE_RATIO = Long.parseLong(Feature.getProperty("ClanHallSupportFunctionFeeRatio", "86400000"));
			CH_SUPPORT1_FEE = Integer.parseInt(Feature.getProperty("ClanHallSupportFeeLvl1", "2500"));
			CH_SUPPORT2_FEE = Integer.parseInt(Feature.getProperty("ClanHallSupportFeeLvl2", "5000"));
			CH_SUPPORT3_FEE = Integer.parseInt(Feature.getProperty("ClanHallSupportFeeLvl3", "7000"));
			CH_SUPPORT4_FEE = Integer.parseInt(Feature.getProperty("ClanHallSupportFeeLvl4", "11000"));
			CH_SUPPORT5_FEE = Integer.parseInt(Feature.getProperty("ClanHallSupportFeeLvl5", "21000"));
			CH_SUPPORT6_FEE = Integer.parseInt(Feature.getProperty("ClanHallSupportFeeLvl6", "36000"));
			CH_SUPPORT7_FEE = Integer.parseInt(Feature.getProperty("ClanHallSupportFeeLvl7", "37000"));
			CH_SUPPORT8_FEE = Integer.parseInt(Feature.getProperty("ClanHallSupportFeeLvl8", "52000"));
			CH_MPREG_FEE_RATIO = Long.parseLong(Feature.getProperty("ClanHallMpRegenerationFunctionFeeRatio", "86400000"));
			CH_MPREG1_FEE = Integer.parseInt(Feature.getProperty("ClanHallMpRegenerationFeeLvl1", "2000"));
			CH_MPREG2_FEE = Integer.parseInt(Feature.getProperty("ClanHallMpRegenerationFeeLvl2", "3750"));
			CH_MPREG3_FEE = Integer.parseInt(Feature.getProperty("ClanHallMpRegenerationFeeLvl3", "6500"));
			CH_MPREG4_FEE = Integer.parseInt(Feature.getProperty("ClanHallMpRegenerationFeeLvl4", "13750"));
			CH_MPREG5_FEE = Integer.parseInt(Feature.getProperty("ClanHallMpRegenerationFeeLvl5", "20000"));
			CH_HPREG_FEE_RATIO = Long.parseLong(Feature.getProperty("ClanHallHpRegenerationFunctionFeeRatio", "86400000"));
			CH_HPREG1_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl1", "700"));
			CH_HPREG2_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl2", "800"));
			CH_HPREG3_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl3", "1000"));
			CH_HPREG4_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl4", "1166"));
			CH_HPREG5_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl5", "1500"));
			CH_HPREG6_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl6", "1750"));
			CH_HPREG7_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl7", "2000"));
			CH_HPREG8_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl8", "2250"));
			CH_HPREG9_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl9", "2500"));
			CH_HPREG10_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl10", "3250"));
			CH_HPREG11_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl11", "3270"));
			CH_HPREG12_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl12", "4250"));
			CH_HPREG13_FEE = Integer.parseInt(Feature.getProperty("ClanHallHpRegenerationFeeLvl13", "5166"));
			CH_EXPREG_FEE_RATIO = Long.parseLong(Feature.getProperty("ClanHallExpRegenerationFunctionFeeRatio", "86400000"));
			CH_EXPREG1_FEE = Integer.parseInt(Feature.getProperty("ClanHallExpRegenerationFeeLvl1", "3000"));
			CH_EXPREG2_FEE = Integer.parseInt(Feature.getProperty("ClanHallExpRegenerationFeeLvl2", "6000"));
			CH_EXPREG3_FEE = Integer.parseInt(Feature.getProperty("ClanHallExpRegenerationFeeLvl3", "9000"));
			CH_EXPREG4_FEE = Integer.parseInt(Feature.getProperty("ClanHallExpRegenerationFeeLvl4", "15000"));
			CH_EXPREG5_FEE = Integer.parseInt(Feature.getProperty("ClanHallExpRegenerationFeeLvl5", "21000"));
			CH_EXPREG6_FEE = Integer.parseInt(Feature.getProperty("ClanHallExpRegenerationFeeLvl6", "23330"));
			CH_EXPREG7_FEE = Integer.parseInt(Feature.getProperty("ClanHallExpRegenerationFeeLvl7", "30000"));
			CH_ITEM_FEE_RATIO = Long.parseLong(Feature.getProperty("ClanHallItemCreationFunctionFeeRatio", "86400000"));
			CH_ITEM1_FEE = Integer.parseInt(Feature.getProperty("ClanHallItemCreationFunctionFeeLvl1", "30000"));
			CH_ITEM2_FEE = Integer.parseInt(Feature.getProperty("ClanHallItemCreationFunctionFeeLvl2", "70000"));
			CH_ITEM3_FEE = Integer.parseInt(Feature.getProperty("ClanHallItemCreationFunctionFeeLvl3", "140000"));
			CH_CURTAIN_FEE_RATIO = Long.parseLong(Feature.getProperty("ClanHallCurtainFunctionFeeRatio", "604800000"));
			CH_CURTAIN1_FEE = Integer.parseInt(Feature.getProperty("ClanHallCurtainFunctionFeeLvl1", "2000"));
			CH_CURTAIN2_FEE = Integer.parseInt(Feature.getProperty("ClanHallCurtainFunctionFeeLvl2", "2500"));
			CH_FRONT_FEE_RATIO = Long.parseLong(Feature.getProperty("ClanHallFrontPlatformFunctionFeeRatio", "259200000"));
			CH_FRONT1_FEE = Integer.parseInt(Feature.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", "1300"));
			CH_FRONT2_FEE = Integer.parseInt(Feature.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", "4000"));
			CH_BUFF_FREE = Boolean.parseBoolean(Feature.getProperty("AltClanHallMpBuffFree", "False"));
			
			CL_SET_SIEGE_TIME_LIST = new ArrayList<>();
			SIEGE_HOUR_LIST_MORNING = new ArrayList<>();
			SIEGE_HOUR_LIST_AFTERNOON = new ArrayList<>();
			String[] sstl = Feature.getProperty("CLSetSiegeTimeList", "").split(",");
			if (sstl.length != 0)
			{
				boolean isHour = false;
				for (String st : sstl)
				{
					if (st.equalsIgnoreCase("day") || st.equalsIgnoreCase("hour") || st.equalsIgnoreCase("minute"))
					{
						if (st.equalsIgnoreCase("hour"))
						{
							isHour = true;
						}
						CL_SET_SIEGE_TIME_LIST.add(st.toLowerCase());
					}
					else
					{
						_log.warning(StringUtil.concat("[CLSetSiegeTimeList]: invalid config property -> CLSetSiegeTimeList \"", st, "\""));
					}
				}
				if (isHour)
				{
					String[] shl = Feature.getProperty("SiegeHourList", "").split(",");
					for (String st : shl)
					{
						if (!st.equalsIgnoreCase(""))
						{
							int val = Integer.parseInt(st);
							if ((val > 23) || (val < 0))
							{
								_log.warning(StringUtil.concat("[SiegeHourList]: invalid config property -> SiegeHourList \"", st, "\""));
							}
							else if (val < 12)
							{
								SIEGE_HOUR_LIST_MORNING.add(val);
							}
							else
							{
								val -= 12;
								SIEGE_HOUR_LIST_AFTERNOON.add(val);
							}
						}
					}
					if (Config.SIEGE_HOUR_LIST_AFTERNOON.isEmpty() && Config.SIEGE_HOUR_LIST_AFTERNOON.isEmpty())
					{
						_log.warning("[SiegeHourList]: invalid config property -> SiegeHourList is empty");
						CL_SET_SIEGE_TIME_LIST.remove("hour");
					}
				}
			}
			CS_TELE_FEE_RATIO = Long.parseLong(Feature.getProperty("CastleTeleportFunctionFeeRatio", "604800000"));
			CS_TELE1_FEE = Integer.parseInt(Feature.getProperty("CastleTeleportFunctionFeeLvl1", "1000"));
			CS_TELE2_FEE = Integer.parseInt(Feature.getProperty("CastleTeleportFunctionFeeLvl2", "10000"));
			CS_SUPPORT_FEE_RATIO = Long.parseLong(Feature.getProperty("CastleSupportFunctionFeeRatio", "604800000"));
			CS_SUPPORT1_FEE = Integer.parseInt(Feature.getProperty("CastleSupportFeeLvl1", "49000"));
			CS_SUPPORT2_FEE = Integer.parseInt(Feature.getProperty("CastleSupportFeeLvl2", "120000"));
			CS_MPREG_FEE_RATIO = Long.parseLong(Feature.getProperty("CastleMpRegenerationFunctionFeeRatio", "604800000"));
			CS_MPREG1_FEE = Integer.parseInt(Feature.getProperty("CastleMpRegenerationFeeLvl1", "45000"));
			CS_MPREG2_FEE = Integer.parseInt(Feature.getProperty("CastleMpRegenerationFeeLvl2", "65000"));
			CS_HPREG_FEE_RATIO = Long.parseLong(Feature.getProperty("CastleHpRegenerationFunctionFeeRatio", "604800000"));
			CS_HPREG1_FEE = Integer.parseInt(Feature.getProperty("CastleHpRegenerationFeeLvl1", "12000"));
			CS_HPREG2_FEE = Integer.parseInt(Feature.getProperty("CastleHpRegenerationFeeLvl2", "20000"));
			CS_EXPREG_FEE_RATIO = Long.parseLong(Feature.getProperty("CastleExpRegenerationFunctionFeeRatio", "604800000"));
			CS_EXPREG1_FEE = Integer.parseInt(Feature.getProperty("CastleExpRegenerationFeeLvl1", "63000"));
			CS_EXPREG2_FEE = Integer.parseInt(Feature.getProperty("CastleExpRegenerationFeeLvl2", "70000"));
			
			OUTER_DOOR_UPGRADE_PRICE2 = Integer.parseInt(Feature.getProperty("OuterDoorUpgradePriceLvl2", "3000000"));
			OUTER_DOOR_UPGRADE_PRICE3 = Integer.parseInt(Feature.getProperty("OuterDoorUpgradePriceLvl3", "4000000"));
			OUTER_DOOR_UPGRADE_PRICE5 = Integer.parseInt(Feature.getProperty("OuterDoorUpgradePriceLvl5", "5000000"));
			INNER_DOOR_UPGRADE_PRICE2 = Integer.parseInt(Feature.getProperty("InnerDoorUpgradePriceLvl2", "750000"));
			INNER_DOOR_UPGRADE_PRICE3 = Integer.parseInt(Feature.getProperty("InnerDoorUpgradePriceLvl3", "900000"));
			INNER_DOOR_UPGRADE_PRICE5 = Integer.parseInt(Feature.getProperty("InnerDoorUpgradePriceLvl5", "1000000"));
			WALL_UPGRADE_PRICE2 = Integer.parseInt(Feature.getProperty("WallUpgradePriceLvl2", "1600000"));
			WALL_UPGRADE_PRICE3 = Integer.parseInt(Feature.getProperty("WallUpgradePriceLvl3", "1800000"));
			WALL_UPGRADE_PRICE5 = Integer.parseInt(Feature.getProperty("WallUpgradePriceLvl5", "2000000"));
			TRAP_UPGRADE_PRICE1 = Integer.parseInt(Feature.getProperty("TrapUpgradePriceLvl1", "3000000"));
			TRAP_UPGRADE_PRICE2 = Integer.parseInt(Feature.getProperty("TrapUpgradePriceLvl2", "4000000"));
			TRAP_UPGRADE_PRICE3 = Integer.parseInt(Feature.getProperty("TrapUpgradePriceLvl3", "5000000"));
			TRAP_UPGRADE_PRICE4 = Integer.parseInt(Feature.getProperty("TrapUpgradePriceLvl4", "6000000"));
			
			FS_TELE_FEE_RATIO = Long.parseLong(Feature.getProperty("FortressTeleportFunctionFeeRatio", "604800000"));
			FS_TELE1_FEE = Integer.parseInt(Feature.getProperty("FortressTeleportFunctionFeeLvl1", "1000"));
			FS_TELE2_FEE = Integer.parseInt(Feature.getProperty("FortressTeleportFunctionFeeLvl2", "10000"));
			FS_SUPPORT_FEE_RATIO = Long.parseLong(Feature.getProperty("FortressSupportFunctionFeeRatio", "86400000"));
			FS_SUPPORT1_FEE = Integer.parseInt(Feature.getProperty("FortressSupportFeeLvl1", "7000"));
			FS_SUPPORT2_FEE = Integer.parseInt(Feature.getProperty("FortressSupportFeeLvl2", "17000"));
			FS_MPREG_FEE_RATIO = Long.parseLong(Feature.getProperty("FortressMpRegenerationFunctionFeeRatio", "86400000"));
			FS_MPREG1_FEE = Integer.parseInt(Feature.getProperty("FortressMpRegenerationFeeLvl1", "6500"));
			FS_MPREG2_FEE = Integer.parseInt(Feature.getProperty("FortressMpRegenerationFeeLvl2", "9300"));
			FS_HPREG_FEE_RATIO = Long.parseLong(Feature.getProperty("FortressHpRegenerationFunctionFeeRatio", "86400000"));
			FS_HPREG1_FEE = Integer.parseInt(Feature.getProperty("FortressHpRegenerationFeeLvl1", "2000"));
			FS_HPREG2_FEE = Integer.parseInt(Feature.getProperty("FortressHpRegenerationFeeLvl2", "3500"));
			FS_EXPREG_FEE_RATIO = Long.parseLong(Feature.getProperty("FortressExpRegenerationFunctionFeeRatio", "86400000"));
			FS_EXPREG1_FEE = Integer.parseInt(Feature.getProperty("FortressExpRegenerationFeeLvl1", "9000"));
			FS_EXPREG2_FEE = Integer.parseInt(Feature.getProperty("FortressExpRegenerationFeeLvl2", "10000"));
			FS_UPDATE_FRQ = Integer.parseInt(Feature.getProperty("FortressPeriodicUpdateFrequency", "360"));
			FS_BLOOD_OATH_COUNT = Integer.parseInt(Feature.getProperty("FortressBloodOathCount", "1"));
			FS_MAX_SUPPLY_LEVEL = Integer.parseInt(Feature.getProperty("FortressMaxSupplyLevel", "6"));
			FS_FEE_FOR_CASTLE = Integer.parseInt(Feature.getProperty("FortressFeeForCastle", "25000"));
			FS_MAX_OWN_TIME = Integer.parseInt(Feature.getProperty("FortressMaximumOwnTime", "168"));
			
			ALT_GAME_CASTLE_DAWN = Boolean.parseBoolean(Feature.getProperty("AltCastleForDawn", "True"));
			ALT_GAME_CASTLE_DUSK = Boolean.parseBoolean(Feature.getProperty("AltCastleForDusk", "True"));
			ALT_GAME_REQUIRE_CLAN_CASTLE = Boolean.parseBoolean(Feature.getProperty("AltRequireClanCastle", "False"));
			ALT_FESTIVAL_MIN_PLAYER = Integer.parseInt(Feature.getProperty("AltFestivalMinPlayer", "5"));
			ALT_MAXIMUM_PLAYER_CONTRIB = Integer.parseInt(Feature.getProperty("AltMaxPlayerContrib", "1000000"));
			ALT_FESTIVAL_MANAGER_START = Long.parseLong(Feature.getProperty("AltFestivalManagerStart", "120000"));
			ALT_FESTIVAL_LENGTH = Long.parseLong(Feature.getProperty("AltFestivalLength", "1080000"));
			ALT_FESTIVAL_CYCLE_LENGTH = Long.parseLong(Feature.getProperty("AltFestivalCycleLength", "2280000"));
			ALT_FESTIVAL_FIRST_SPAWN = Long.parseLong(Feature.getProperty("AltFestivalFirstSpawn", "120000"));
			ALT_FESTIVAL_FIRST_SWARM = Long.parseLong(Feature.getProperty("AltFestivalFirstSwarm", "300000"));
			ALT_FESTIVAL_SECOND_SPAWN = Long.parseLong(Feature.getProperty("AltFestivalSecondSpawn", "540000"));
			ALT_FESTIVAL_SECOND_SWARM = Long.parseLong(Feature.getProperty("AltFestivalSecondSwarm", "720000"));
			ALT_FESTIVAL_CHEST_SPAWN = Long.parseLong(Feature.getProperty("AltFestivalChestSpawn", "900000"));
			ALT_SIEGE_DAWN_GATES_PDEF_MULT = Double.parseDouble(Feature.getProperty("AltDawnGatesPdefMult", "1.1"));
			ALT_SIEGE_DUSK_GATES_PDEF_MULT = Double.parseDouble(Feature.getProperty("AltDuskGatesPdefMult", "0.8"));
			ALT_SIEGE_DAWN_GATES_MDEF_MULT = Double.parseDouble(Feature.getProperty("AltDawnGatesMdefMult", "1.1"));
			ALT_SIEGE_DUSK_GATES_MDEF_MULT = Double.parseDouble(Feature.getProperty("AltDuskGatesMdefMult", "0.8"));
			ALT_STRICT_SEVENSIGNS = Boolean.parseBoolean(Feature.getProperty("StrictSevenSigns", "True"));
			ALT_SEVENSIGNS_LAZY_UPDATE = Boolean.parseBoolean(Feature.getProperty("AltSevenSignsLazyUpdate", "True"));
			
			SSQ_DAWN_TICKET_QUANTITY = Integer.parseInt(Feature.getProperty("SevenSignsDawnTicketQuantity", "300"));
			SSQ_DAWN_TICKET_PRICE = Integer.parseInt(Feature.getProperty("SevenSignsDawnTicketPrice", "1000"));
			SSQ_DAWN_TICKET_BUNDLE = Integer.parseInt(Feature.getProperty("SevenSignsDawnTicketBundle", "10"));
			SSQ_MANORS_AGREEMENT_ID = Integer.parseInt(Feature.getProperty("SevenSignsManorsAgreementId", "6388"));
			SSQ_JOIN_DAWN_ADENA_FEE = Integer.parseInt(Feature.getProperty("SevenSignsJoinDawnFee", "50000"));
			
			TAKE_FORT_POINTS = Integer.parseInt(Feature.getProperty("TakeFortPoints", "200"));
			LOOSE_FORT_POINTS = Integer.parseInt(Feature.getProperty("LooseFortPoints", "0"));
			TAKE_CASTLE_POINTS = Integer.parseInt(Feature.getProperty("TakeCastlePoints", "1500"));
			LOOSE_CASTLE_POINTS = Integer.parseInt(Feature.getProperty("LooseCastlePoints", "3000"));
			CASTLE_DEFENDED_POINTS = Integer.parseInt(Feature.getProperty("CastleDefendedPoints", "750"));
			FESTIVAL_WIN_POINTS = Integer.parseInt(Feature.getProperty("FestivalOfDarknessWin", "200"));
			HERO_POINTS = Integer.parseInt(Feature.getProperty("HeroPoints", "1000"));
			ROYAL_GUARD_COST = Integer.parseInt(Feature.getProperty("CreateRoyalGuardCost", "5000"));
			KNIGHT_UNIT_COST = Integer.parseInt(Feature.getProperty("CreateKnightUnitCost", "10000"));
			KNIGHT_REINFORCE_COST = Integer.parseInt(Feature.getProperty("ReinforceKnightUnitCost", "5000"));
			BALLISTA_POINTS = Integer.parseInt(Feature.getProperty("KillBallistaPoints", "30"));
			BLOODALLIANCE_POINTS = Integer.parseInt(Feature.getProperty("BloodAlliancePoints", "500"));
			BLOODOATH_POINTS = Integer.parseInt(Feature.getProperty("BloodOathPoints", "200"));
			KNIGHTSEPAULETTE_POINTS = Integer.parseInt(Feature.getProperty("KnightsEpaulettePoints", "20"));
			REPUTATION_SCORE_PER_KILL = Integer.parseInt(Feature.getProperty("ReputationScorePerKill", "1"));
			JOIN_ACADEMY_MIN_REP_SCORE = Integer.parseInt(Feature.getProperty("CompleteAcademyMinPoints", "190"));
			JOIN_ACADEMY_MAX_REP_SCORE = Integer.parseInt(Feature.getProperty("CompleteAcademyMaxPoints", "650"));
			RAID_RANKING_1ST = Integer.parseInt(Feature.getProperty("1stRaidRankingPoints", "1250"));
			RAID_RANKING_2ND = Integer.parseInt(Feature.getProperty("2ndRaidRankingPoints", "900"));
			RAID_RANKING_3RD = Integer.parseInt(Feature.getProperty("3rdRaidRankingPoints", "700"));
			RAID_RANKING_4TH = Integer.parseInt(Feature.getProperty("4thRaidRankingPoints", "600"));
			RAID_RANKING_5TH = Integer.parseInt(Feature.getProperty("5thRaidRankingPoints", "450"));
			RAID_RANKING_6TH = Integer.parseInt(Feature.getProperty("6thRaidRankingPoints", "350"));
			RAID_RANKING_7TH = Integer.parseInt(Feature.getProperty("7thRaidRankingPoints", "300"));
			RAID_RANKING_8TH = Integer.parseInt(Feature.getProperty("8thRaidRankingPoints", "200"));
			RAID_RANKING_9TH = Integer.parseInt(Feature.getProperty("9thRaidRankingPoints", "150"));
			RAID_RANKING_10TH = Integer.parseInt(Feature.getProperty("10thRaidRankingPoints", "100"));
			RAID_RANKING_UP_TO_50TH = Integer.parseInt(Feature.getProperty("UpTo50thRaidRankingPoints", "25"));
			RAID_RANKING_UP_TO_100TH = Integer.parseInt(Feature.getProperty("UpTo100thRaidRankingPoints", "12"));
			CLAN_LEVEL_6_COST = Integer.parseInt(Feature.getProperty("ClanLevel6Cost", "5000"));
			CLAN_LEVEL_7_COST = Integer.parseInt(Feature.getProperty("ClanLevel7Cost", "10000"));
			CLAN_LEVEL_8_COST = Integer.parseInt(Feature.getProperty("ClanLevel8Cost", "20000"));
			CLAN_LEVEL_9_COST = Integer.parseInt(Feature.getProperty("ClanLevel9Cost", "40000"));
			CLAN_LEVEL_10_COST = Integer.parseInt(Feature.getProperty("ClanLevel10Cost", "40000"));
			CLAN_LEVEL_11_COST = Integer.parseInt(Feature.getProperty("ClanLevel11Cost", "75000"));
			CLAN_LEVEL_6_REQUIREMENT = Integer.parseInt(Feature.getProperty("ClanLevel6Requirement", "30"));
			CLAN_LEVEL_7_REQUIREMENT = Integer.parseInt(Feature.getProperty("ClanLevel7Requirement", "50"));
			CLAN_LEVEL_8_REQUIREMENT = Integer.parseInt(Feature.getProperty("ClanLevel8Requirement", "80"));
			CLAN_LEVEL_9_REQUIREMENT = Integer.parseInt(Feature.getProperty("ClanLevel9Requirement", "120"));
			CLAN_LEVEL_10_REQUIREMENT = Integer.parseInt(Feature.getProperty("ClanLevel10Requirement", "140"));
			CLAN_LEVEL_11_REQUIREMENT = Integer.parseInt(Feature.getProperty("ClanLevel11Requirement", "170"));
			ALLOW_WYVERN_DURING_SIEGE = Boolean.parseBoolean(Feature.getProperty("AllowRideWyvernDuringSiege", "True"));
			
			// Load TVT L2Properties file (if exists)
			final File tvt = new File(TVT_CONFIG);
			try (InputStream is = new FileInputStream(tvt))
			{
				L2Properties tvtsystem = new L2Properties();
				tvtsystem.load(is);
				TVT_EVENT_ENABLED = Boolean.parseBoolean(tvtsystem.getProperty("TvTEventEnabled", "false"));
				TVT_EVENT_IN_INSTANCE = Boolean.parseBoolean(tvtsystem.getProperty("TvTEventInInstance", "false"));
				TVT_EVENT_INSTANCE_FILE = tvtsystem.getProperty("TvTEventInstanceFile", "coliseum.xml");
				TVT_EVENT_INTERVAL = tvtsystem.getProperty("TvTEventInterval", "20:00").split(",");
				TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(tvtsystem.getProperty("TvTEventParticipationTime", "3600"));
				TVT_EVENT_RUNNING_TIME = Integer.parseInt(tvtsystem.getProperty("TvTEventRunningTime", "1800"));
				TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(tvtsystem.getProperty("TvTEventParticipationNpcId", "0"));
				
				if (TVT_EVENT_PARTICIPATION_NPC_ID == 0)
				{
					TVT_EVENT_ENABLED = false;
					_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcId");
				}
				else
				{
					String[] tvtNpcCoords = tvtsystem.getProperty("TvTEventParticipationNpcCoordinates", "0,0,0").split(",");
					if (tvtNpcCoords.length < 3)
					{
						TVT_EVENT_ENABLED = false;
						_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationNpcCoordinates");
					}
					else
					{
						TVT_EVENT_REWARDS = new ArrayList<>();
						TVT_DOORS_IDS_TO_OPEN = new ArrayList<>();
						TVT_DOORS_IDS_TO_CLOSE = new ArrayList<>();
						TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
						TVT_EVENT_TEAM_1_COORDINATES = new int[3];
						TVT_EVENT_TEAM_2_COORDINATES = new int[3];
						TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
						TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
						TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
						if (tvtNpcCoords.length == 4)
						{
							TVT_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(tvtNpcCoords[3]);
						}
						TVT_EVENT_MIN_PLAYERS_IN_TEAMS = Integer.parseInt(tvtsystem.getProperty("TvTEventMinPlayersInTeams", "1"));
						TVT_EVENT_MAX_PLAYERS_IN_TEAMS = Integer.parseInt(tvtsystem.getProperty("TvTEventMaxPlayersInTeams", "20"));
						TVT_EVENT_MIN_LVL = Byte.parseByte(tvtsystem.getProperty("TvTEventMinPlayerLevel", "1"));
						TVT_EVENT_MAX_LVL = Byte.parseByte(tvtsystem.getProperty("TvTEventMaxPlayerLevel", "80"));
						TVT_EVENT_RESPAWN_TELEPORT_DELAY = Integer.parseInt(tvtsystem.getProperty("TvTEventRespawnTeleportDelay", "20"));
						TVT_EVENT_START_LEAVE_TELEPORT_DELAY = Integer.parseInt(tvtsystem.getProperty("TvTEventStartLeaveTeleportDelay", "20"));
						TVT_EVENT_EFFECTS_REMOVAL = Integer.parseInt(tvtsystem.getProperty("TvTEventEffectsRemoval", "0"));
						TVT_EVENT_MAX_PARTICIPANTS_PER_IP = Integer.parseInt(tvtsystem.getProperty("TvTEventMaxParticipantsPerIP", "0"));
						TVT_ALLOW_VOICED_COMMAND = Boolean.parseBoolean(tvtsystem.getProperty("TvTAllowVoicedInfoCommand", "false"));
						TVT_EVENT_TEAM_1_NAME = tvtsystem.getProperty("TvTEventTeam1Name", "Team1");
						tvtNpcCoords = tvtsystem.getProperty("TvTEventTeam1Coordinates", "0,0,0").split(",");
						if (tvtNpcCoords.length < 3)
						{
							TVT_EVENT_ENABLED = false;
							_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam1Coordinates");
						}
						else
						{
							TVT_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
							TVT_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
							TVT_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
							TVT_EVENT_TEAM_2_NAME = tvtsystem.getProperty("TvTEventTeam2Name", "Team2");
							tvtNpcCoords = tvtsystem.getProperty("TvTEventTeam2Coordinates", "0,0,0").split(",");
							if (tvtNpcCoords.length < 3)
							{
								TVT_EVENT_ENABLED = false;
								_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventTeam2Coordinates");
							}
							else
							{
								TVT_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(tvtNpcCoords[0]);
								TVT_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(tvtNpcCoords[1]);
								TVT_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(tvtNpcCoords[2]);
								tvtNpcCoords = tvtsystem.getProperty("TvTEventParticipationFee", "0,0").split(",");
								try
								{
									TVT_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(tvtNpcCoords[0]);
									TVT_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(tvtNpcCoords[1]);
								}
								catch (NumberFormatException nfe)
								{
									if (tvtNpcCoords.length > 0)
									{
										_log.warning("TvTEventEngine[Config.load()]: invalid config property -> TvTEventParticipationFee");
									}
								}
								tvtNpcCoords = tvtsystem.getProperty("TvTEventReward", "57,100000").split(";");
								for (String reward : tvtNpcCoords)
								{
									String[] rewardSplit = reward.split(",");
									if (rewardSplit.length != 2)
									{
										_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
									}
									else
									{
										try
										{
											TVT_EVENT_REWARDS.add(new int[]
											{
												Integer.parseInt(rewardSplit[0]),
												Integer.parseInt(rewardSplit[1])
											});
										}
										catch (NumberFormatException nfe)
										{
											if (!reward.isEmpty())
											{
												_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventReward \"", reward, "\""));
											}
										}
									}
								}
								
								TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = Boolean.parseBoolean(tvtsystem.getProperty("TvTEventTargetTeamMembersAllowed", "true"));
								TVT_EVENT_SCROLL_ALLOWED = Boolean.parseBoolean(tvtsystem.getProperty("TvTEventScrollsAllowed", "false"));
								TVT_EVENT_POTIONS_ALLOWED = Boolean.parseBoolean(tvtsystem.getProperty("TvTEventPotionsAllowed", "false"));
								TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = Boolean.parseBoolean(tvtsystem.getProperty("TvTEventSummonByItemAllowed", "false"));
								TVT_REWARD_TEAM_TIE = Boolean.parseBoolean(tvtsystem.getProperty("TvTRewardTeamTie", "false"));
								tvtNpcCoords = tvtsystem.getProperty("TvTDoorsToOpen", "").split(";");
								for (String door : tvtNpcCoords)
								{
									try
									{
										TVT_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
									}
									catch (NumberFormatException nfe)
									{
										if (!door.isEmpty())
										{
											_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToOpen \"", door, "\""));
										}
									}
								}
								
								tvtNpcCoords = tvtsystem.getProperty("TvTDoorsToClose", "").split(";");
								for (String door : tvtNpcCoords)
								{
									try
									{
										TVT_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
									}
									catch (NumberFormatException nfe)
									{
										if (!door.isEmpty())
										{
											_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTDoorsToClose \"", door, "\""));
										}
									}
								}
								
								tvtNpcCoords = tvtsystem.getProperty("TvTEventFighterBuffs", "").split(";");
								if (!tvtNpcCoords[0].isEmpty())
								{
									TVT_EVENT_FIGHTER_BUFFS = new HashMap<>(tvtNpcCoords.length);
									for (String skill : tvtNpcCoords)
									{
										String[] skillSplit = skill.split(",");
										if (skillSplit.length != 2)
										{
											_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
										}
										else
										{
											try
											{
												TVT_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
											}
											catch (NumberFormatException nfe)
											{
												if (!skill.isEmpty())
												{
													_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventFighterBuffs \"", skill, "\""));
												}
											}
										}
									}
								}
								
								tvtNpcCoords = tvtsystem.getProperty("TvTEventMageBuffs", "").split(";");
								if (!tvtNpcCoords[0].isEmpty())
								{
									TVT_EVENT_MAGE_BUFFS = new HashMap<>(tvtNpcCoords.length);
									for (String skill : tvtNpcCoords)
									{
										String[] skillSplit = skill.split(",");
										if (skillSplit.length != 2)
										{
											_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
										}
										else
										{
											try
											{
												TVT_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
											}
											catch (NumberFormatException nfe)
											{
												if (!skill.isEmpty())
												{
													_log.warning(StringUtil.concat("TvTEventEngine[Config.load()]: invalid config property -> TvTEventMageBuffs \"", skill, "\""));
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + TVT_CONFIG + " File.");
			}
			
			// Load offline L2Properties file (if exists)
			final File offline = new File(OFFLINE_SHOP_CONFIG);
			try (InputStream is = new FileInputStream(offline))
			{
				L2Properties offline_shop = new L2Properties();
				offline_shop.load(is);
				OFFLINE_TRADE_ENABLE = Boolean.parseBoolean(offline_shop.getProperty("OfflineTradeEnable", "false"));
				OFFLINE_CRAFT_ENABLE = Boolean.parseBoolean(offline_shop.getProperty("OfflineCraftEnable", "false"));
				OFFLINE_MODE_IN_PEACE_ZONE = Boolean.parseBoolean(offline_shop.getProperty("OfflineModeInPaceZone", "False"));
				OFFLINE_MODE_NO_DAMAGE = Boolean.parseBoolean(offline_shop.getProperty("OfflineModeNoDamage", "False"));
				OFFLINE_SET_NAME_COLOR = Boolean.parseBoolean(offline_shop.getProperty("OfflineSetNameColor", "false"));
				OFFLINE_NAME_COLOR = Integer.decode("0x" + offline_shop.getProperty("OfflineNameColor", "808080"));
				OFFLINE_FAME = Boolean.parseBoolean(offline_shop.getProperty("OfflineFame", "true"));
				RESTORE_OFFLINERS = Boolean.parseBoolean(offline_shop.getProperty("RestoreOffliners", "false"));
				OFFLINE_MAX_DAYS = Integer.parseInt(offline_shop.getProperty("OfflineMaxDays", "10"));
				OFFLINE_DISCONNECT_FINISHED = Boolean.parseBoolean(offline_shop.getProperty("OfflineDisconnectFinished", "true"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + OFFLINE_SHOP_CONFIG + " File.");
			}
			
			// Load WEDDING L2Properties file (if exists)
			final File wedding = new File(WEDDING_CONFIG);
			try (InputStream is = new FileInputStream(wedding))
			{
				L2Properties weddingsystem = new L2Properties();
				weddingsystem.load(is);
				ALLOW_WEDDING = Boolean.parseBoolean(weddingsystem.getProperty("AllowWedding", "False"));
				WEDDING_PRICE = Integer.parseInt(weddingsystem.getProperty("WeddingPrice", "250000000"));
				WEDDING_PUNISH_INFIDELITY = Boolean.parseBoolean(weddingsystem.getProperty("WeddingPunishInfidelity", "True"));
				WEDDING_TELEPORT = Boolean.parseBoolean(weddingsystem.getProperty("WeddingTeleport", "True"));
				WEDDING_TELEPORT_PRICE = Integer.parseInt(weddingsystem.getProperty("WeddingTeleportPrice", "50000"));
				WEDDING_TELEPORT_DURATION = Integer.parseInt(weddingsystem.getProperty("WeddingTeleportDuration", "60"));
				WEDDING_SAMESEX = Boolean.parseBoolean(weddingsystem.getProperty("WeddingAllowSameSex", "False"));
				WEDDING_FORMALWEAR = Boolean.parseBoolean(weddingsystem.getProperty("WeddingFormalWear", "True"));
				WEDDING_DIVORCE_COSTS = Integer.parseInt(weddingsystem.getProperty("WeddingDivorceCosts", "20"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + WEDDING_CONFIG + " File.");
			}
			
			// Load TWW L2Properties file (if exists)
			final File tww = new File(TOWNWAR_CONFIG);
			try (InputStream is = new FileInputStream(tww))
			{
				L2Properties twwsys = new L2Properties();
				twwsys.load(is);
				TW_TOWN_ID = Integer.parseInt(twwsys.getProperty("TownWarTownId", "9"));
				TW_TOWN_NAME = twwsys.getProperty("TownWarTownName", "Giran Town");
				TW_ALL_TOWNS = Boolean.parseBoolean(twwsys.getProperty("TownWarAllTowns", "False"));
				TW_AUTO_EVENT = Boolean.parseBoolean(twwsys.getProperty("TownWarAutoEvent", "false"));
				TW_INTERVAL = twwsys.getProperty("TownWarInterval", "20:00").split(",");
				TW_TIME_BEFORE_START = Integer.parseInt(twwsys.getProperty("TownWarTimeBeforeStart", "3600"));
				TW_RUNNING_TIME = Integer.parseInt(twwsys.getProperty("TownWarRunningTime", "1800"));
				TW_ITEM_ID = Integer.parseInt(twwsys.getProperty("TownWarItemId", "57"));
				TW_ITEM_AMOUNT = Integer.parseInt(twwsys.getProperty("TownWarItemAmount", "5000"));
				TW_GIVE_PVP_AND_PK_POINTS = Boolean.parseBoolean(twwsys.getProperty("TownWarGivePvPAndPkPoints", "False"));
				TW_ALLOW_KARMA = Boolean.parseBoolean(twwsys.getProperty("TownWarDisableKarma", "False"));
				TW_DISABLE_GK = Boolean.parseBoolean(twwsys.getProperty("TownWarDisableGK", "True"));
				TW_LOSE_BUFFS_ON_DEATH = Boolean.parseBoolean(twwsys.getProperty("TownWarLoseBuffsOnDeath", "False"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + TOWNWAR_CONFIG + " File.");
			}
			
			// Load CHAMPION_CONFIG L2Properties file (if exists)
			final File easychamp = new File(EASY_CHAMPION_CONFIG);
			try (InputStream is = new FileInputStream(easychamp))
			{
				L2Properties easychampions = new L2Properties();
				easychampions.load(is);
				EASY_CHAMPION_ENABLE = Boolean.parseBoolean(easychampions.getProperty("EasyChampionEnable", "false"));
				EASY_CHAMPION_PASSIVE = Boolean.parseBoolean(easychampions.getProperty("EasyChampionPassive", "false"));
				EASY_CHAMPION_FREQUENCY = Integer.parseInt(easychampions.getProperty("EasyChampionFrequency", "0"));
				EASY_CHAMP_TITLE = easychampions.getProperty("EasyChampionTitle", "EasyChampion");
				EASY_CHAMP_MIN_LVL = Integer.parseInt(easychampions.getProperty("EasyChampionMinLevel", "20"));
				EASY_CHAMP_MAX_LVL = Integer.parseInt(easychampions.getProperty("EasyChampionMaxLevel", "60"));
				EASY_CHAMPION_HP = Integer.parseInt(easychampions.getProperty("EasyChampionHp", "7"));
				EASY_CHAMPION_HP_REGEN = Float.parseFloat(easychampions.getProperty("EasyChampionHpRegen", "1."));
				EASY_CHAMPION_REWARDS = Integer.parseInt(easychampions.getProperty("EasyChampionRewards", "8"));
				EASY_CHAMPION_ADENAS_REWARDS = Float.parseFloat(easychampions.getProperty("EasyChampionAdenasRewards", "1"));
				EASY_CHAMPION_ATK = Float.parseFloat(easychampions.getProperty("EasyChampionAtk", "1."));
				EASY_CHAMPION_SPD_ATK = Float.parseFloat(easychampions.getProperty("EasyChampionSpdAtk", "1."));
				EASY_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE = Integer.parseInt(easychampions.getProperty("EasyChampionRewardLowerLvlItemChance", "0"));
				EASY_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE = Integer.parseInt(easychampions.getProperty("EasyChampionRewardHigherLvlItemChance", "0"));
				EASY_CHAMPION_REWARD_ID = Integer.parseInt(easychampions.getProperty("EasyChampionRewardItemID", "6393"));
				EASY_CHAMPION_REWARD_QTY = Integer.parseInt(easychampions.getProperty("EasyChampionRewardItemQty", "1"));
				EASY_CHAMPION_ENABLE_VITALITY = Boolean.parseBoolean(easychampions.getProperty("EasyChampionEnableVitality", "False"));
				EASY_CHAMPION_ENABLE_IN_INSTANCES = Boolean.parseBoolean(easychampions.getProperty("EasyChampionEnableInInstances", "False"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + EASY_CHAMPION_CONFIG + " File.");
			}
			
			// Load HARD_CHAMPION_CONFIG L2Properties file (if exists)
			final File hardchamp = new File(HARD_CHAMPION_CONFIG);
			try (InputStream is = new FileInputStream(hardchamp))
			{
				L2Properties hardchampions = new L2Properties();
				hardchampions.load(is);
				HARD_CHAMPION_ENABLE = Boolean.parseBoolean(hardchampions.getProperty("HardChampionEnable", "false"));
				HARD_CHAMPION_PASSIVE = Boolean.parseBoolean(hardchampions.getProperty("HardChampionPassive", "false"));
				HARD_CHAMPION_FREQUENCY = Integer.parseInt(hardchampions.getProperty("HardChampionFrequency", "0"));
				HARD_CHAMP_TITLE = hardchampions.getProperty("HardChampionTitle", "HardChampion");
				HARD_CHAMP_MIN_LVL = Integer.parseInt(hardchampions.getProperty("HardChampionMinLevel", "20"));
				HARD_CHAMP_MAX_LVL = Integer.parseInt(hardchampions.getProperty("HardChampionMaxLevel", "60"));
				HARD_CHAMPION_HP = Integer.parseInt(hardchampions.getProperty("HardChampionHp", "7"));
				HARD_CHAMPION_HP_REGEN = Float.parseFloat(hardchampions.getProperty("HardChampionHpRegen", "1."));
				HARD_CHAMPION_REWARDS = Integer.parseInt(hardchampions.getProperty("HardChampionRewards", "8"));
				HARD_CHAMPION_ADENAS_REWARDS = Float.parseFloat(hardchampions.getProperty("HardChampionAdenasRewards", "1"));
				HARD_CHAMPION_ATK = Float.parseFloat(hardchampions.getProperty("HardChampionAtk", "1."));
				HARD_CHAMPION_SPD_ATK = Float.parseFloat(hardchampions.getProperty("HardChampionSpdAtk", "1."));
				HARD_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE = Integer.parseInt(hardchampions.getProperty("HardChampionRewardLowerLvlItemChance", "0"));
				HARD_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE = Integer.parseInt(hardchampions.getProperty("HardChampionRewardHigherLvlItemChance", "0"));
				HARD_CHAMPION_REWARD_ID = Integer.parseInt(hardchampions.getProperty("HardChampionRewardItemID", "6393"));
				HARD_CHAMPION_REWARD_QTY = Integer.parseInt(hardchampions.getProperty("HardChampionRewardItemQty", "1"));
				HARD_CHAMPION_ENABLE_VITALITY = Boolean.parseBoolean(hardchampions.getProperty("HardChampionEnableVitality", "False"));
				HARD_CHAMPION_ENABLE_IN_INSTANCES = Boolean.parseBoolean(hardchampions.getProperty("HardChampionEnableInInstances", "False"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + HARD_CHAMPION_CONFIG + " File.");
			}
			
			// Load Pcbangs L2Properties file (if exists)
			L2Properties Pcbangs = new L2Properties();
			final File pcbang = new File(PC_BANG_CONFIG);
			try (InputStream is = new FileInputStream(pcbang))
			{
				Pcbangs.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Characters settings!", e);
			}
			PC_BANG_ENABLED = Boolean.parseBoolean(Pcbangs.getProperty("Enabled", "false"));
			MAX_PC_BANG_POINTS = Integer.parseInt(Pcbangs.getProperty("MaxPcBangPoints", "200000"));
			if (MAX_PC_BANG_POINTS < 0)
			{
				MAX_PC_BANG_POINTS = 0;
			}
			ENABLE_DOUBLE_PC_BANG_POINTS = Boolean.parseBoolean(Pcbangs.getProperty("DoublingAcquisitionPoints", "false"));
			DOUBLE_PC_BANG_POINTS_CHANCE = Integer.parseInt(Pcbangs.getProperty("DoublingAcquisitionPointsChance", "1"));
			if ((DOUBLE_PC_BANG_POINTS_CHANCE < 0) || (DOUBLE_PC_BANG_POINTS_CHANCE > 100))
			{
				DOUBLE_PC_BANG_POINTS_CHANCE = 1;
			}
			PC_BANG_POINT_RATE = Double.parseDouble(Pcbangs.getProperty("AcquisitionPointsRate", "1.0"));
			if (PC_BANG_POINT_RATE < 0)
			{
				PC_BANG_POINT_RATE = 1;
			}
			PC_BANG_POINT_RATE_BOSS = Double.parseDouble(Pcbangs.getProperty("BossPointsRate", "1.0"));
			if (PC_BANG_POINT_RATE_BOSS < 0)
			{
				PC_BANG_POINT_RATE_BOSS = 1;
			}
			PC_BANG_POINT_RATE_EASY_CHAMPION = Double.parseDouble(Pcbangs.getProperty("EasyChampionPointsRate", "1.0"));
			if (PC_BANG_POINT_RATE_EASY_CHAMPION < 0)
			{
				PC_BANG_POINT_RATE_EASY_CHAMPION = 1;
			}
			PC_BANG_POINT_RATE_HARD_CHAMIPON = Double.parseDouble(Pcbangs.getProperty("HardChampionPointsRate", "1.0"));
			if (PC_BANG_POINT_RATE_HARD_CHAMIPON < 0)
			{
				PC_BANG_POINT_RATE_HARD_CHAMIPON = 1;
			}
			RANDOM_PC_BANG_POINT = Boolean.parseBoolean(Pcbangs.getProperty("AcquisitionPointsRandom", "false"));
			MAGE_LEES_THAN_FIGHTER = Integer.parseInt(Pcbangs.getProperty("MageLeesThanFigter", "2.0"));
			
			// Load Character L2Properties file (if exists)
			L2Properties Character = new L2Properties();
			final File chars = new File(CHARACTER_CONFIG);
			try (InputStream is = new FileInputStream(chars))
			{
				Character.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Characters settings!", e);
			}
			ALT_GAME_DELEVEL = Boolean.parseBoolean(Character.getProperty("Delevel", "true"));
			DECREASE_SKILL_LEVEL = Boolean.parseBoolean(Character.getProperty("DecreaseSkillOnDelevel", "true"));
			ALT_WEIGHT_LIMIT = Double.parseDouble(Character.getProperty("AltWeightLimit", "1"));
			RUN_SPD_BOOST = Integer.parseInt(Character.getProperty("RunSpeedBoost", "0"));
			DEATH_PENALTY_CHANCE = Integer.parseInt(Character.getProperty("DeathPenaltyChance", "20"));
			RESPAWN_RESTORE_CP = Double.parseDouble(Character.getProperty("RespawnRestoreCP", "0")) / 100;
			RESPAWN_RESTORE_HP = Double.parseDouble(Character.getProperty("RespawnRestoreHP", "65")) / 100;
			RESPAWN_RESTORE_MP = Double.parseDouble(Character.getProperty("RespawnRestoreMP", "0")) / 100;
			HP_REGEN_MULTIPLIER = Double.parseDouble(Character.getProperty("HpRegenMultiplier", "100")) / 100;
			MP_REGEN_MULTIPLIER = Double.parseDouble(Character.getProperty("MpRegenMultiplier", "100")) / 100;
			CP_REGEN_MULTIPLIER = Double.parseDouble(Character.getProperty("CpRegenMultiplier", "100")) / 100;
			ALT_GAME_TIREDNESS = Boolean.parseBoolean(Character.getProperty("AltGameTiredness", "false"));
			ENABLE_MODIFY_SKILL_DURATION = Boolean.parseBoolean(Character.getProperty("EnableModifySkillDuration", "false"));
			
			// Create Map only if enabled
			if (ENABLE_MODIFY_SKILL_DURATION)
			{
				String[] propertySplit = Character.getProperty("SkillDurationList", "").split(";");
				SKILL_DURATION_LIST = new HashMap<>(propertySplit.length);
				for (String skill : propertySplit)
				{
					String[] skillSplit = skill.split(",");
					if (skillSplit.length != 2)
					{
						_log.warning(StringUtil.concat("[SkillDurationList]: invalid config property -> SkillDurationList \"", skill, "\""));
					}
					else
					{
						try
						{
							SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!skill.isEmpty())
							{
								_log.warning(StringUtil.concat("[SkillDurationList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
							}
						}
					}
				}
			}
			ENABLE_MODIFY_SKILL_REUSE = Boolean.parseBoolean(Character.getProperty("EnableModifySkillReuse", "false"));
			// Create Map only if enabled
			if (ENABLE_MODIFY_SKILL_REUSE)
			{
				String[] propertySplit = Character.getProperty("SkillReuseList", "").split(";");
				SKILL_REUSE_LIST = new HashMap<>(propertySplit.length);
				for (String skill : propertySplit)
				{
					String[] skillSplit = skill.split(",");
					if (skillSplit.length != 2)
					{
						_log.warning(StringUtil.concat("[SkillReuseList]: invalid config property -> SkillReuseList \"", skill, "\""));
					}
					else
					{
						try
						{
							SKILL_REUSE_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!skill.isEmpty())
							{
								_log.warning(StringUtil.concat("[SkillReuseList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1]));
							}
						}
					}
				}
			}
			
			AUTO_LEARN_SKILLS = Boolean.parseBoolean(Character.getProperty("AutoLearnSkills", "False"));
			AUTO_LEARN_FS_SKILLS = Boolean.parseBoolean(Character.getProperty("AutoLearnForgottenScrollSkills", "False"));
			AUTO_LOOT_HERBS = Boolean.parseBoolean(Character.getProperty("AutoLootHerbs", "false"));
			BUFFS_MAX_AMOUNT = Byte.parseByte(Character.getProperty("MaxBuffAmount", "20"));
			TRIGGERED_BUFFS_MAX_AMOUNT = Byte.parseByte(Character.getProperty("MaxTriggeredBuffAmount", "12"));
			DANCES_MAX_AMOUNT = Byte.parseByte(Character.getProperty("MaxDanceAmount", "12"));
			DANCE_CANCEL_BUFF = Boolean.parseBoolean(Character.getProperty("DanceCancelBuff", "false"));
			DANCE_CONSUME_ADDITIONAL_MP = Boolean.parseBoolean(Character.getProperty("DanceConsumeAdditionalMP", "true"));
			ALT_STORE_DANCES = Boolean.parseBoolean(Character.getProperty("AltStoreDances", "false"));
			AUTO_LEARN_DIVINE_INSPIRATION = Boolean.parseBoolean(Character.getProperty("AutoLearnDivineInspiration", "false"));
			ALT_GAME_CANCEL_BOW = Character.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("bow") || Character.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
			ALT_GAME_CANCEL_CAST = Character.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("cast") || Character.getProperty("AltGameCancelByHit", "Cast").equalsIgnoreCase("all");
			EFFECT_CANCELING = Boolean.parseBoolean(Character.getProperty("CancelLesserEffect", "True"));
			ALT_GAME_MAGICFAILURES = Boolean.parseBoolean(Character.getProperty("MagicFailures", "true"));
			PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(Character.getProperty("PlayerFakeDeathUpProtection", "0"));
			STORE_SKILL_COOLTIME = Boolean.parseBoolean(Character.getProperty("StoreSkillCooltime", "true"));
			SUBCLASS_STORE_SKILL_COOLTIME = Boolean.parseBoolean(Character.getProperty("SubclassStoreSkillCooltime", "false"));
			SUMMON_STORE_SKILL_COOLTIME = Boolean.parseBoolean(Character.getProperty("SummonStoreSkillCooltime", "True"));
			ALT_GAME_SHIELD_BLOCKS = Boolean.parseBoolean(Character.getProperty("AltShieldBlocks", "false"));
			ALT_PERFECT_SHLD_BLOCK = Integer.parseInt(Character.getProperty("AltPerfectShieldBlockRate", "10"));
			ALLOW_CLASS_MASTERS = Boolean.parseBoolean(Character.getProperty("AllowClassMasters", "False"));
			ALLOW_ENTIRE_TREE = Boolean.parseBoolean(Character.getProperty("AllowEntireTree", "False"));
			ALTERNATE_CLASS_MASTER = Boolean.parseBoolean(Character.getProperty("AlternateClassMaster", "False"));
			if (ALLOW_CLASS_MASTERS || ALTERNATE_CLASS_MASTER)
			{
				CLASS_MASTER_SETTINGS = new ClassMasterSettings(Character.getProperty("ConfigClassMaster"));
			}
			LIFE_CRYSTAL_NEEDED = Boolean.parseBoolean(Character.getProperty("LifeCrystalNeeded", "true"));
			ES_SP_BOOK_NEEDED = Boolean.parseBoolean(Character.getProperty("EnchantSkillSpBookNeeded", "true"));
			DIVINE_SP_BOOK_NEEDED = Boolean.parseBoolean(Character.getProperty("DivineInspirationSpBookNeeded", "true"));
			ALT_GAME_SKILL_LEARN = Boolean.parseBoolean(Character.getProperty("AltGameSkillLearn", "false"));
			ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.parseBoolean(Character.getProperty("AltSubClassWithoutQuests", "False"));
			ALT_GAME_SUBCLASS_EVERYWHERE = Boolean.parseBoolean(Character.getProperty("AltSubclassEverywhere", "False"));
			RESTORE_SERVITOR_ON_RECONNECT = Boolean.parseBoolean(Character.getProperty("RestoreServitorOnReconnect", "True"));
			RESTORE_PET_ON_RECONNECT = Boolean.parseBoolean(Character.getProperty("RestorePetOnReconnect", "True"));
			ALLOW_TRANSFORM_WITHOUT_QUEST = Boolean.parseBoolean(Character.getProperty("AltTransformationWithoutQuest", "False"));
			FEE_DELETE_TRANSFER_SKILLS = Integer.parseInt(Character.getProperty("FeeDeleteTransferSkills", "10000000"));
			FEE_DELETE_SUBCLASS_SKILLS = Integer.parseInt(Character.getProperty("FeeDeleteSubClassSkills", "10000000"));
			ENABLE_VITALITY = Boolean.parseBoolean(Character.getProperty("EnableVitality", "True"));
			RECOVER_VITALITY_ON_RECONNECT = Boolean.parseBoolean(Character.getProperty("RecoverVitalityOnReconnect", "True"));
			STARTING_VITALITY_POINTS = Integer.parseInt(Character.getProperty("StartingVitalityPoints", "20000"));
			MAX_BONUS_EXP = Double.parseDouble(Character.getProperty("MaxExpBonus", "3.5"));
			MAX_BONUS_SP = Double.parseDouble(Character.getProperty("MaxSpBonus", "3.5"));
			MAX_RUN_SPEED = Integer.parseInt(Character.getProperty("MaxRunSpeed", "300"));
			MAX_PCRIT_RATE = Integer.parseInt(Character.getProperty("MaxPCritRate", "500"));
			MAX_MCRIT_RATE = Integer.parseInt(Character.getProperty("MaxMCritRate", "200"));
			MAX_PATK_SPEED = Integer.parseInt(Character.getProperty("MaxPAtkSpeed", "1500"));
			MAX_MATK_SPEED = Integer.parseInt(Character.getProperty("MaxMAtkSpeed", "1999"));
			MAX_EVASION = Integer.parseInt(Character.getProperty("MaxEvasion", "250"));
			MIN_ABNORMAL_STATE_SUCCESS_RATE = Integer.parseInt(Character.getProperty("MinAbnormalStateSuccessRate", "10"));
			MAX_ABNORMAL_STATE_SUCCESS_RATE = Integer.parseInt(Character.getProperty("MaxAbnormalStateSuccessRate", "90"));
			MAX_SUBCLASS = Byte.parseByte(Character.getProperty("MaxSubclass", "3"));
			BASE_SUBCLASS_LEVEL = Byte.parseByte(Character.getProperty("BaseSubclassLevel", "40"));
			MAX_SUBCLASS_LEVEL = Byte.parseByte(Character.getProperty("MaxSubclassLevel", "80"));
			MAX_PVTSTORESELL_SLOTS_DWARF = Integer.parseInt(Character.getProperty("MaxPvtStoreSellSlotsDwarf", "4"));
			MAX_PVTSTORESELL_SLOTS_OTHER = Integer.parseInt(Character.getProperty("MaxPvtStoreSellSlotsOther", "3"));
			MAX_PVTSTOREBUY_SLOTS_DWARF = Integer.parseInt(Character.getProperty("MaxPvtStoreBuySlotsDwarf", "5"));
			MAX_PVTSTOREBUY_SLOTS_OTHER = Integer.parseInt(Character.getProperty("MaxPvtStoreBuySlotsOther", "4"));
			INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(Character.getProperty("MaximumSlotsForNoDwarf", "80"));
			INVENTORY_MAXIMUM_DWARF = Integer.parseInt(Character.getProperty("MaximumSlotsForDwarf", "100"));
			INVENTORY_MAXIMUM_GM = Integer.parseInt(Character.getProperty("MaximumSlotsForGMPlayer", "250"));
			INVENTORY_MAXIMUM_QUEST_ITEMS = Integer.parseInt(Character.getProperty("MaximumSlotsForQuestItems", "100"));
			MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, Math.max(INVENTORY_MAXIMUM_DWARF, INVENTORY_MAXIMUM_GM));
			WAREHOUSE_SLOTS_DWARF = Integer.parseInt(Character.getProperty("MaximumWarehouseSlotsForDwarf", "120"));
			WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(Character.getProperty("MaximumWarehouseSlotsForNoDwarf", "100"));
			WAREHOUSE_SLOTS_CLAN = Integer.parseInt(Character.getProperty("MaximumWarehouseSlotsForClan", "150"));
			ALT_FREIGHT_SLOTS = Integer.parseInt(Character.getProperty("MaximumFreightSlots", "200"));
			ALT_FREIGHT_PRICE = Integer.parseInt(Character.getProperty("FreightPrice", "1000"));
			ENCHANT_CHANCE = Double.parseDouble(Character.getProperty("EnchantChance", "66.66"));
			MAX_ENCHANT_LEVEL = Integer.parseInt(Character.getProperty("MaxEnchantLevel", "0"));
			ENCHANT_CHANCE_ELEMENT_STONE = Double.parseDouble(Character.getProperty("EnchantChanceElementStone", "50"));
			ENCHANT_CHANCE_ELEMENT_CRYSTAL = Double.parseDouble(Character.getProperty("EnchantChanceElementCrystal", "30"));
			ENCHANT_CHANCE_ELEMENT_JEWEL = Double.parseDouble(Character.getProperty("EnchantChanceElementJewel", "20"));
			ENCHANT_CHANCE_ELEMENT_ENERGY = Double.parseDouble(Character.getProperty("EnchantChanceElementEnergy", "10"));
			ENCHANT_SAFE_MAX = Integer.parseInt(Character.getProperty("EnchantSafeMax", "3"));
			ENCHANT_SAFE_MAX_FULL = Integer.parseInt(Character.getProperty("EnchantSafeMaxFull", "4"));
			String[] notenchantable = Character.getProperty("EnchantBlackList", "7816,7817,7818,7819,7820,7821,7822,7823,7824,7825,7826,7827,7828,7829,7830,7831,13293,13294,13296").split(",");
			ENCHANT_BLACKLIST = new int[notenchantable.length];
			for (int i = 0; i < notenchantable.length; i++)
			{
				ENCHANT_BLACKLIST[i] = Integer.parseInt(notenchantable[i]);
			}
			Arrays.sort(ENCHANT_BLACKLIST);
			
			AUGMENTATION_NG_SKILL_CHANCE = Integer.parseInt(Character.getProperty("AugmentationNGSkillChance", "15"));
			AUGMENTATION_NG_GLOW_CHANCE = Integer.parseInt(Character.getProperty("AugmentationNGGlowChance", "0"));
			AUGMENTATION_MID_SKILL_CHANCE = Integer.parseInt(Character.getProperty("AugmentationMidSkillChance", "30"));
			AUGMENTATION_MID_GLOW_CHANCE = Integer.parseInt(Character.getProperty("AugmentationMidGlowChance", "40"));
			AUGMENTATION_HIGH_SKILL_CHANCE = Integer.parseInt(Character.getProperty("AugmentationHighSkillChance", "45"));
			AUGMENTATION_HIGH_GLOW_CHANCE = Integer.parseInt(Character.getProperty("AugmentationHighGlowChance", "70"));
			AUGMENTATION_TOP_SKILL_CHANCE = Integer.parseInt(Character.getProperty("AugmentationTopSkillChance", "60"));
			AUGMENTATION_TOP_GLOW_CHANCE = Integer.parseInt(Character.getProperty("AugmentationTopGlowChance", "100"));
			AUGMENTATION_BASESTAT_CHANCE = Integer.parseInt(Character.getProperty("AugmentationBaseStatChance", "1"));
			AUGMENTATION_ACC_SKILL_CHANCE = Integer.parseInt(Character.getProperty("AugmentationAccSkillChance", "0"));
			
			RETAIL_LIKE_AUGMENTATION = Boolean.parseBoolean(Character.getProperty("RetailLikeAugmentation", "True"));
			String[] array = Character.getProperty("RetailLikeAugmentationNoGradeChance", "55,35,7,3").split(",");
			RETAIL_LIKE_AUGMENTATION_NG_CHANCE = new int[array.length];
			for (int i = 0; i < 4; i++)
			{
				RETAIL_LIKE_AUGMENTATION_NG_CHANCE[i] = Integer.parseInt(array[i]);
			}
			array = Character.getProperty("RetailLikeAugmentationMidGradeChance", "55,35,7,3").split(",");
			RETAIL_LIKE_AUGMENTATION_MID_CHANCE = new int[array.length];
			for (int i = 0; i < 4; i++)
			{
				RETAIL_LIKE_AUGMENTATION_MID_CHANCE[i] = Integer.parseInt(array[i]);
			}
			array = Character.getProperty("RetailLikeAugmentationHighGradeChance", "55,35,7,3").split(",");
			RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE = new int[array.length];
			for (int i = 0; i < 4; i++)
			{
				RETAIL_LIKE_AUGMENTATION_HIGH_CHANCE[i] = Integer.parseInt(array[i]);
			}
			array = Character.getProperty("RetailLikeAugmentationTopGradeChance", "55,35,7,3").split(",");
			RETAIL_LIKE_AUGMENTATION_TOP_CHANCE = new int[array.length];
			for (int i = 0; i < 4; i++)
			{
				RETAIL_LIKE_AUGMENTATION_TOP_CHANCE[i] = Integer.parseInt(array[i]);
			}
			RETAIL_LIKE_AUGMENTATION_ACCESSORY = Boolean.parseBoolean(Character.getProperty("RetailLikeAugmentationAccessory", "True"));
			
			array = Character.getProperty("AugmentationBlackList", "6656,6657,6658,6659,6660,6661,6662,8191,10170,10314,13740,13741,13742,13743,13744,13745,13746,13747,13748,14592,14593,14594,14595,14596,14597,14598,14599,14600,14664,14665,14666,14667,14668,14669,14670,14671,14672,14801,14802,14803,14804,14805,14806,14807,14808,14809,15282,15283,15284,15285,15286,15287,15288,15289,15290,15291,15292,15293,15294,15295,15296,15297,15298,15299,16025,16026,21712,22173,22174,22175").split(",");
			AUGMENTATION_BLACKLIST = new int[array.length];
			
			for (int i = 0; i < array.length; i++)
			{
				AUGMENTATION_BLACKLIST[i] = Integer.parseInt(array[i]);
			}
			
			Arrays.sort(AUGMENTATION_BLACKLIST);
			ALT_ALLOW_AUGMENT_PVP_ITEMS = Boolean.parseBoolean(Character.getProperty("AltAllowAugmentPvPItems", "false"));
			ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.parseBoolean(Character.getProperty("AltKarmaPlayerCanBeKilledInPeaceZone", "false"));
			ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.parseBoolean(Character.getProperty("AltKarmaPlayerCanShop", "true"));
			ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.parseBoolean(Character.getProperty("AltKarmaPlayerCanTeleport", "true"));
			ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.parseBoolean(Character.getProperty("AltKarmaPlayerCanUseGK", "false"));
			ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.parseBoolean(Character.getProperty("AltKarmaPlayerCanTrade", "true"));
			ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.parseBoolean(Character.getProperty("AltKarmaPlayerCanUseWareHouse", "true"));
			MAX_PERSONAL_FAME_POINTS = Integer.parseInt(Character.getProperty("MaxPersonalFamePoints", "100000"));
			FORTRESS_ZONE_FAME_TASK_FREQUENCY = Integer.parseInt(Character.getProperty("FortressZoneFameTaskFrequency", "300"));
			FORTRESS_ZONE_FAME_AQUIRE_POINTS = Integer.parseInt(Character.getProperty("FortressZoneFameAquirePoints", "31"));
			CASTLE_ZONE_FAME_TASK_FREQUENCY = Integer.parseInt(Character.getProperty("CastleZoneFameTaskFrequency", "300"));
			CASTLE_ZONE_FAME_AQUIRE_POINTS = Integer.parseInt(Character.getProperty("CastleZoneFameAquirePoints", "125"));
			FAME_FOR_DEAD_PLAYERS = Boolean.parseBoolean(Character.getProperty("FameForDeadPlayers", "true"));
			IS_CRAFTING_ENABLED = Boolean.parseBoolean(Character.getProperty("CraftingEnabled", "true"));
			CRAFT_MASTERWORK = Boolean.parseBoolean(Character.getProperty("CraftMasterwork", "True"));
			DWARF_RECIPE_LIMIT = Integer.parseInt(Character.getProperty("DwarfRecipeLimit", "50"));
			COMMON_RECIPE_LIMIT = Integer.parseInt(Character.getProperty("CommonRecipeLimit", "50"));
			ALT_GAME_CREATION = Boolean.parseBoolean(Character.getProperty("AltGameCreation", "false"));
			ALT_GAME_CREATION_SPEED = Double.parseDouble(Character.getProperty("AltGameCreationSpeed", "1"));
			ALT_GAME_CREATION_XP_RATE = Double.parseDouble(Character.getProperty("AltGameCreationXpRate", "1"));
			ALT_GAME_CREATION_SP_RATE = Double.parseDouble(Character.getProperty("AltGameCreationSpRate", "1"));
			ALT_GAME_CREATION_RARE_XPSP_RATE = Double.parseDouble(Character.getProperty("AltGameCreationRareXpSpRate", "2"));
			ALT_BLACKSMITH_USE_RECIPES = Boolean.parseBoolean(Character.getProperty("AltBlacksmithUseRecipes", "true"));
			ALT_CLAN_LEADER_DATE_CHANGE = Integer.parseInt(Character.getProperty("AltClanLeaderDateChange", "3"));
			if ((ALT_CLAN_LEADER_DATE_CHANGE < 1) || (ALT_CLAN_LEADER_DATE_CHANGE > 7))
			{
				_log.log(Level.WARNING, "Wrong value specified for AltClanLeaderDateChange: " + ALT_CLAN_LEADER_DATE_CHANGE);
				ALT_CLAN_LEADER_DATE_CHANGE = 3;
			}
			ALT_CLAN_LEADER_HOUR_CHANGE = Character.getProperty("AltClanLeaderHourChange", "00:00:00");
			ALT_CLAN_LEADER_INSTANT_ACTIVATION = Boolean.parseBoolean(Character.getProperty("AltClanLeaderInstantActivation", "false"));
			ALT_CLAN_JOIN_DAYS = Integer.parseInt(Character.getProperty("DaysBeforeJoinAClan", "1"));
			ALT_CLAN_CREATE_DAYS = Integer.parseInt(Character.getProperty("DaysBeforeCreateAClan", "10"));
			ALT_CLAN_DISSOLVE_DAYS = Integer.parseInt(Character.getProperty("DaysToPassToDissolveAClan", "7"));
			ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = Integer.parseInt(Character.getProperty("DaysBeforeJoinAllyWhenLeaved", "1"));
			ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = Integer.parseInt(Character.getProperty("DaysBeforeJoinAllyWhenDismissed", "1"));
			ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = Integer.parseInt(Character.getProperty("DaysBeforeAcceptNewClanWhenDismissed", "1"));
			ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = Integer.parseInt(Character.getProperty("DaysBeforeCreateNewAllyWhenDissolved", "1"));
			ALT_MAX_NUM_OF_CLANS_IN_ALLY = Integer.parseInt(Character.getProperty("AltMaxNumOfClansInAlly", "3"));
			ALT_CLAN_MEMBERS_FOR_WAR = Integer.parseInt(Character.getProperty("AltClanMembersForWar", "15"));
			ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.parseBoolean(Character.getProperty("AltMembersCanWithdrawFromClanWH", "false"));
			REMOVE_CASTLE_CIRCLETS = Boolean.parseBoolean(Character.getProperty("RemoveCastleCirclets", "true"));
			ALT_PARTY_RANGE = Integer.parseInt(Character.getProperty("AltPartyRange", "1600"));
			ALT_PARTY_RANGE2 = Integer.parseInt(Character.getProperty("AltPartyRange2", "1400"));
			ALT_LEAVE_PARTY_LEADER = Boolean.parseBoolean(Character.getProperty("AltLeavePartyLeader", "False"));
			INITIAL_EQUIPMENT_EVENT = Boolean.parseBoolean(Character.getProperty("InitialEquipmentEvent", "False"));
			STARTING_ADENA = Long.parseLong(Character.getProperty("StartingAdena", "0"));
			STARTING_LEVEL = Byte.parseByte(Character.getProperty("StartingLevel", "1"));
			STARTING_SP = Integer.parseInt(Character.getProperty("StartingSP", "0"));
			MAX_ADENA = Long.parseLong(Character.getProperty("MaxAdena", "99900000000"));
			if (MAX_ADENA < 0)
			{
				MAX_ADENA = Long.MAX_VALUE;
			}
			AUTO_LOOT = Boolean.parseBoolean(Character.getProperty("AutoLoot", "false"));
			AUTO_LOOT_RAIDS = Boolean.parseBoolean(Character.getProperty("AutoLootRaids", "false"));
			LOOT_RAIDS_PRIVILEGE_INTERVAL = Integer.parseInt(Character.getProperty("RaidLootRightsInterval", "900")) * 1000;
			LOOT_RAIDS_PRIVILEGE_CC_SIZE = Integer.parseInt(Character.getProperty("RaidLootRightsCCSize", "45"));
			UNSTUCK_INTERVAL = Integer.parseInt(Character.getProperty("UnstuckInterval", "300"));
			TELEPORT_WATCHDOG_TIMEOUT = Integer.parseInt(Character.getProperty("TeleportWatchdogTimeout", "0"));
			PLAYER_SPAWN_PROTECTION = Integer.parseInt(Character.getProperty("PlayerSpawnProtection", "0"));
			String[] items = Character.getProperty("PlayerSpawnProtectionAllowedItems", "0").split(",");
			SPAWN_PROTECTION_ALLOWED_ITEMS = new ArrayList<>(items.length);
			for (String item : items)
			{
				Integer itm = 0;
				try
				{
					itm = Integer.parseInt(item);
				}
				catch (NumberFormatException nfe)
				{
					_log.warning("Player Spawn Protection: Wrong ItemId passed: " + item);
					_log.warning(nfe.getMessage());
				}
				if (itm != 0)
				{
					SPAWN_PROTECTION_ALLOWED_ITEMS.add(itm);
				}
			}
			PLAYER_TELEPORT_PROTECTION = Integer.parseInt(Character.getProperty("PlayerTeleportProtection", "0"));
			RANDOM_RESPAWN_IN_TOWN_ENABLED = Boolean.parseBoolean(Character.getProperty("RandomRespawnInTownEnabled", "True"));
			OFFSET_ON_TELEPORT_ENABLED = Boolean.parseBoolean(Character.getProperty("OffsetOnTeleportEnabled", "True"));
			MAX_OFFSET_ON_TELEPORT = Integer.parseInt(Character.getProperty("MaxOffsetOnTeleport", "50"));
			RESTORE_PLAYER_INSTANCE = Boolean.parseBoolean(Character.getProperty("RestorePlayerInstance", "False"));
			ALLOW_SUMMON_TO_INSTANCE = Boolean.parseBoolean(Character.getProperty("AllowSummonToInstance", "True"));
			EJECT_DEAD_PLAYER_TIME = 1000 * Integer.parseInt(Character.getProperty("EjectDeadPlayerTime", "60"));
			PETITIONING_ALLOWED = Boolean.parseBoolean(Character.getProperty("PetitioningAllowed", "True"));
			MAX_PETITIONS_PER_PLAYER = Integer.parseInt(Character.getProperty("MaxPetitionsPerPlayer", "5"));
			MAX_PETITIONS_PENDING = Integer.parseInt(Character.getProperty("MaxPetitionsPending", "25"));
			ALT_GAME_FREE_TELEPORT = Boolean.parseBoolean(Character.getProperty("AltFreeTeleporting", "False"));
			DELETE_DAYS = Integer.parseInt(Character.getProperty("DeleteCharAfterDays", "7"));
			ALT_GAME_EXPONENT_XP = Float.parseFloat(Character.getProperty("AltGameExponentXp", "0."));
			ALT_GAME_EXPONENT_SP = Float.parseFloat(Character.getProperty("AltGameExponentSp", "0."));
			PARTY_XP_CUTOFF_METHOD = Character.getProperty("PartyXpCutoffMethod", "highfive");
			PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(Character.getProperty("PartyXpCutoffPercent", "3."));
			PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(Character.getProperty("PartyXpCutoffLevel", "20"));
			final String[] gaps = Character.getProperty("PartyXpCutoffGaps", "0,9;10,14;15,99").split(";");
			PARTY_XP_CUTOFF_GAPS = new int[gaps.length][2];
			for (int i = 0; i < gaps.length; i++)
			{
				PARTY_XP_CUTOFF_GAPS[i] = new int[]
				{
					Integer.parseInt(gaps[i].split(",")[0]),
					Integer.parseInt(gaps[i].split(",")[1])
				};
			}
			final String[] percents = Character.getProperty("PartyXpCutoffGapPercent", "100;30;0").split(";");
			PARTY_XP_CUTOFF_GAP_PERCENTS = new int[percents.length];
			for (int i = 0; i < percents.length; i++)
			{
				PARTY_XP_CUTOFF_GAP_PERCENTS[i] = Integer.parseInt(percents[i]);
			}
			DISABLE_TUTORIAL = Boolean.parseBoolean(Character.getProperty("DisableTutorial", "False"));
			EXPERTISE_PENALTY = Boolean.parseBoolean(Character.getProperty("ExpertisePenalty", "True"));
			STORE_RECIPE_SHOPLIST = Boolean.parseBoolean(Character.getProperty("StoreRecipeShopList", "False"));
			STORE_UI_SETTINGS = Boolean.parseBoolean(Character.getProperty("StoreCharUiSettings", "False"));
			FORBIDDEN_NAMES = Character.getProperty("ForbiddenNames", "").split(",");
			SILENCE_MODE_EXCLUDE = Boolean.parseBoolean(Character.getProperty("SilenceModeExclude", "False"));
			ALT_VALIDATE_TRIGGER_SKILLS = Boolean.parseBoolean(Character.getProperty("AltValidateTriggerSkills", "False"));
			PLAYER_MOVEMENT_BLOCK_TIME = Integer.parseInt(Character.getProperty("NpcTalkBlockingTime", "0")) * 1000;
			
			// Load Telnet L2Properties file (if exists)
			L2Properties telnetSettings = new L2Properties();
			final File telnet = new File(TELNET_CONFIG);
			try (InputStream is = new FileInputStream(telnet))
			{
				telnetSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Telnet settings!", e);
			}
			
			IS_TELNET_ENABLED = Boolean.parseBoolean(telnetSettings.getProperty("EnableTelnet", "false"));
			
			// MMO
			L2Properties mmoSettings = new L2Properties();
			final File mmo = new File(MMO_CONFIG);
			try (InputStream is = new FileInputStream(mmo))
			{
				mmoSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading MMO settings!", e);
			}
			
			MMO_SELECTOR_SLEEP_TIME = Integer.parseInt(mmoSettings.getProperty("SleepTime", "20"));
			MMO_MAX_SEND_PER_PASS = Integer.parseInt(mmoSettings.getProperty("MaxSendPerPass", "12"));
			MMO_MAX_READ_PER_PASS = Integer.parseInt(mmoSettings.getProperty("MaxReadPerPass", "12"));
			MMO_HELPER_BUFFER_COUNT = Integer.parseInt(mmoSettings.getProperty("HelperBufferCount", "20"));
			MMO_TCP_NODELAY = Boolean.parseBoolean(mmoSettings.getProperty("TcpNoDelay", "False"));
			
			// Load IdFactory L2Properties file (if exists)
			L2Properties IdFactory = new L2Properties();
			final File Id = new File(ID_FACTORY_CONFIG);
			try (InputStream is = new FileInputStream(Id))
			{
				IdFactory.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading IdFactory settings!", e);
			}
			
			IDFACTORY_TYPE = IdFactoryType.valueOf(IdFactory.getProperty("IDFactory", "BitSet"));
			BAD_ID_CHECKING = Boolean.parseBoolean(IdFactory.getProperty("BadIdChecking", "True"));
			
			// Load General L2Properties file (if exists)
			L2Properties General = new L2Properties();
			final File general = new File(GENERAL_CONFIG);
			try (InputStream is = new FileInputStream(general))
			{
				General.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading General settings!", e);
			}
			
			DEBUG = Boolean.parseBoolean(General.getProperty("Debug", "false"));
			PACKET_HANDLER_DEBUG = Boolean.parseBoolean(General.getProperty("PacketHandlerDebug", "false"));
			DEVELOPER = Boolean.parseBoolean(General.getProperty("Developer", "false"));
			ACCEPT_GEOEDITOR_CONN = Boolean.parseBoolean(General.getProperty("AcceptGeoeditorConn", "false"));
			ALT_DEV_NO_HANDLERS = Boolean.parseBoolean(General.getProperty("AltDevNoHandlers", "False"));
			ALT_DEV_NO_QUESTS = Boolean.parseBoolean(General.getProperty("AltDevNoQuests", "False"));
			ALT_DEV_NO_SPAWNS = Boolean.parseBoolean(General.getProperty("AltDevNoSpawns", "False"));
			
			WORLD_X_MIN = Integer.parseInt(General.getProperty("WorldXMin", "10"));
			WORLD_X_MAX = Integer.parseInt(General.getProperty("WorldXMax", "26"));
			WORLD_Y_MIN = Integer.parseInt(General.getProperty("WorldYMin", "10"));
			WORLD_Y_MAX = Integer.parseInt(General.getProperty("WorldYMax", "26"));
			
			String str = General.getProperty("EnableFallingDamage", "auto");
			ENABLE_FALLING_DAMAGE = "auto".equalsIgnoreCase(str) ? GEODATA > 0 : Boolean.parseBoolean(str);
			
			PEACE_ZONE_MODE = Integer.parseInt(General.getProperty("PeaceZoneMode", "0"));
			DEFAULT_GLOBAL_CHAT = General.getProperty("GlobalChat", "ON");
			DEFAULT_TRADE_CHAT = General.getProperty("TradeChat", "ON");
			ALLOW_WAREHOUSE = Boolean.parseBoolean(General.getProperty("AllowWarehouse", "True"));
			WAREHOUSE_CACHE = Boolean.parseBoolean(General.getProperty("WarehouseCache", "False"));
			WAREHOUSE_CACHE_TIME = Integer.parseInt(General.getProperty("WarehouseCacheTime", "15"));
			ALLOW_REFUND = Boolean.parseBoolean(General.getProperty("AllowRefund", "True"));
			ALLOW_MAIL = Boolean.parseBoolean(General.getProperty("AllowMail", "True"));
			ALLOW_ATTACHMENTS = Boolean.parseBoolean(General.getProperty("AllowAttachments", "True"));
			ALLOW_WEAR = Boolean.parseBoolean(General.getProperty("AllowWear", "True"));
			WEAR_DELAY = Integer.parseInt(General.getProperty("WearDelay", "5"));
			WEAR_PRICE = Integer.parseInt(General.getProperty("WearPrice", "10"));
			ALLOW_LOTTERY = Boolean.parseBoolean(General.getProperty("AllowLottery", "True"));
			ALLOW_RACE = Boolean.parseBoolean(General.getProperty("AllowRace", "True"));
			ALLOW_WATER = Boolean.parseBoolean(General.getProperty("AllowWater", "True"));
			ALLOW_RENTPET = Boolean.parseBoolean(General.getProperty("AllowRentPet", "False"));
			ALLOWFISHING = Boolean.parseBoolean(General.getProperty("AllowFishing", "True"));
			ALLOW_MANOR = Boolean.parseBoolean(General.getProperty("AllowManor", "True"));
			ALLOW_BOAT = Boolean.parseBoolean(General.getProperty("AllowBoat", "True"));
			BOAT_BROADCAST_RADIUS = Integer.parseInt(General.getProperty("BoatBroadcastRadius", "20000"));
			ALLOW_CURSED_WEAPONS = Boolean.parseBoolean(General.getProperty("AllowCursedWeapons", "True"));
			ALLOW_PET_WALKERS = Boolean.parseBoolean(General.getProperty("AllowPetWalkers", "True"));
			SERVER_NEWS = Boolean.parseBoolean(General.getProperty("ShowServerNews", "False"));
			COMMUNITY_TYPE = Integer.parseInt(General.getProperty("CommunityType", "2"));
			BBS_SHOW_PLAYERLIST = Boolean.parseBoolean(General.getProperty("BBSShowPlayerList", "false"));
			BBS_DEFAULT = General.getProperty("BBSDefault", "_bbshome");
			SHOW_LEVEL_COMMUNITYBOARD = Boolean.parseBoolean(General.getProperty("ShowLevelOnCommunityBoard", "False"));
			SHOW_STATUS_COMMUNITYBOARD = Boolean.parseBoolean(General.getProperty("ShowStatusOnCommunityBoard", "False"));
			NAME_PAGE_SIZE_COMMUNITYBOARD = Integer.parseInt(General.getProperty("NamePageSizeOnCommunityBoard", "50"));
			NAME_PER_ROW_COMMUNITYBOARD = Integer.parseInt(General.getProperty("NamePerRowOnCommunityBoard", "5"));
			USE_SAY_FILTER = Boolean.parseBoolean(General.getProperty("UseChatFilter", "false"));
			CHAT_FILTER_CHARS = General.getProperty("ChatFilterChars", "^_^");
			String[] propertySplit4 = General.getProperty("BanChatChannels", "0;1;8;17").trim().split(";");
			BAN_CHAT_CHANNELS = new int[propertySplit4.length];
			try
			{
				int i = 0;
				for (String chatId : propertySplit4)
				{
					BAN_CHAT_CHANNELS[i++] = Integer.parseInt(chatId);
				}
			}
			catch (NumberFormatException nfe)
			{
				_log.log(Level.WARNING, nfe.getMessage(), nfe);
			}
			ALT_MANOR_REFRESH_TIME = Integer.parseInt(General.getProperty("AltManorRefreshTime", "20"));
			ALT_MANOR_REFRESH_MIN = Integer.parseInt(General.getProperty("AltManorRefreshMin", "00"));
			ALT_MANOR_APPROVE_TIME = Integer.parseInt(General.getProperty("AltManorApproveTime", "6"));
			ALT_MANOR_APPROVE_MIN = Integer.parseInt(General.getProperty("AltManorApproveMin", "00"));
			ALT_MANOR_MAINTENANCE_PERIOD = Integer.parseInt(General.getProperty("AltManorMaintenancePeriod", "360000"));
			ALT_MANOR_SAVE_ALL_ACTIONS = Boolean.parseBoolean(General.getProperty("AltManorSaveAllActions", "false"));
			ALT_MANOR_SAVE_PERIOD_RATE = Integer.parseInt(General.getProperty("AltManorSavePeriodRate", "2"));
			ALT_LOTTERY_PRIZE = Long.parseLong(General.getProperty("AltLotteryPrize", "50000"));
			ALT_LOTTERY_TICKET_PRICE = Long.parseLong(General.getProperty("AltLotteryTicketPrice", "2000"));
			ALT_LOTTERY_5_NUMBER_RATE = Float.parseFloat(General.getProperty("AltLottery5NumberRate", "0.6"));
			ALT_LOTTERY_4_NUMBER_RATE = Float.parseFloat(General.getProperty("AltLottery4NumberRate", "0.2"));
			ALT_LOTTERY_3_NUMBER_RATE = Float.parseFloat(General.getProperty("AltLottery3NumberRate", "0.2"));
			ALT_LOTTERY_2_AND_1_NUMBER_PRIZE = Long.parseLong(General.getProperty("AltLottery2and1NumberPrize", "200"));
			ALT_ITEM_AUCTION_ENABLED = Boolean.parseBoolean(General.getProperty("AltItemAuctionEnabled", "True"));
			ALT_ITEM_AUCTION_EXPIRED_AFTER = Integer.parseInt(General.getProperty("AltItemAuctionExpiredAfter", "14"));
			ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID = 1000 * (long) Integer.parseInt(General.getProperty("AltItemAuctionTimeExtendsOnBid", "0"));
			FS_TIME_ATTACK = Integer.parseInt(General.getProperty("TimeOfAttack", "50"));
			FS_TIME_COOLDOWN = Integer.parseInt(General.getProperty("TimeOfCoolDown", "5"));
			FS_TIME_ENTRY = Integer.parseInt(General.getProperty("TimeOfEntry", "3"));
			FS_TIME_WARMUP = Integer.parseInt(General.getProperty("TimeOfWarmUp", "2"));
			FS_PARTY_MEMBER_COUNT = Integer.parseInt(General.getProperty("NumberOfNecessaryPartyMembers", "4"));
			if (FS_TIME_ATTACK <= 0)
			{
				FS_TIME_ATTACK = 50;
			}
			if (FS_TIME_COOLDOWN <= 0)
			{
				FS_TIME_COOLDOWN = 5;
			}
			if (FS_TIME_ENTRY <= 0)
			{
				FS_TIME_ENTRY = 3;
			}
			if (FS_TIME_ENTRY <= 0)
			{
				FS_TIME_ENTRY = 3;
			}
			if (FS_TIME_ENTRY <= 0)
			{
				FS_TIME_ENTRY = 3;
			}
			RIFT_MIN_PARTY_SIZE = Integer.parseInt(General.getProperty("RiftMinPartySize", "5"));
			RIFT_MAX_JUMPS = Integer.parseInt(General.getProperty("MaxRiftJumps", "4"));
			RIFT_SPAWN_DELAY = Integer.parseInt(General.getProperty("RiftSpawnDelay", "10000"));
			RIFT_AUTO_JUMPS_TIME_MIN = Integer.parseInt(General.getProperty("AutoJumpsDelayMin", "480"));
			RIFT_AUTO_JUMPS_TIME_MAX = Integer.parseInt(General.getProperty("AutoJumpsDelayMax", "600"));
			RIFT_BOSS_ROOM_TIME_MUTIPLY = Float.parseFloat(General.getProperty("BossRoomTimeMultiply", "1.5"));
			RIFT_ENTER_COST_RECRUIT = Integer.parseInt(General.getProperty("RecruitCost", "18"));
			RIFT_ENTER_COST_SOLDIER = Integer.parseInt(General.getProperty("SoldierCost", "21"));
			RIFT_ENTER_COST_OFFICER = Integer.parseInt(General.getProperty("OfficerCost", "24"));
			RIFT_ENTER_COST_CAPTAIN = Integer.parseInt(General.getProperty("CaptainCost", "27"));
			RIFT_ENTER_COST_COMMANDER = Integer.parseInt(General.getProperty("CommanderCost", "30"));
			RIFT_ENTER_COST_HERO = Integer.parseInt(General.getProperty("HeroCost", "33"));
			DEFAULT_PUNISH = Integer.parseInt(General.getProperty("DefaultPunish", "2"));
			DEFAULT_PUNISH_PARAM = Integer.parseInt(General.getProperty("DefaultPunishParam", "0"));
			ONLY_GM_ITEMS_FREE = Boolean.parseBoolean(General.getProperty("OnlyGMItemsFree", "True"));
			JAIL_IS_PVP = Boolean.parseBoolean(General.getProperty("JailIsPvp", "False"));
			JAIL_DISABLE_CHAT = Boolean.parseBoolean(General.getProperty("JailDisableChat", "True"));
			JAIL_DISABLE_TRANSACTION = Boolean.parseBoolean(General.getProperty("JailDisableTransaction", "False"));
			CUSTOM_SPAWNLIST_TABLE = Boolean.parseBoolean(General.getProperty("CustomSpawnlistTable", "false"));
			SAVE_GMSPAWN_ON_CUSTOM = Boolean.parseBoolean(General.getProperty("SaveGmSpawnOnCustom", "false"));
			CUSTOM_NPC_TABLE = Boolean.parseBoolean(General.getProperty("CustomNpcTable", "false"));
			CUSTOM_NPC_SKILLS_TABLE = Boolean.parseBoolean(General.getProperty("CustomNpcSkillsTable", "false"));
			CUSTOM_TELEPORT_TABLE = Boolean.parseBoolean(General.getProperty("CustomTeleportTable", "false"));
			CUSTOM_DROPLIST_TABLE = Boolean.parseBoolean(General.getProperty("CustomDroplistTable", "false"));
			CUSTOM_MERCHANT_TABLES = Boolean.parseBoolean(General.getProperty("CustomMerchantTables", "false"));
			CUSTOM_NPCBUFFER_TABLES = Boolean.parseBoolean(General.getProperty("CustomNpcBufferTables", "false"));
			CUSTOM_SKILLS_LOAD = Boolean.parseBoolean(General.getProperty("CustomSkillsLoad", "false"));
			CUSTOM_ITEMS_LOAD = Boolean.parseBoolean(General.getProperty("CustomItemsLoad", "false"));
			CUSTOM_MULTISELL_LOAD = Boolean.parseBoolean(General.getProperty("CustomMultisellLoad", "false"));
			ALT_BIRTHDAY_GIFT = Integer.parseInt(General.getProperty("AltBirthdayGift", "22187"));
			ALT_BIRTHDAY_MAIL_SUBJECT = General.getProperty("AltBirthdayMailSubject", "Happy Birthday!");
			ALT_BIRTHDAY_MAIL_TEXT = General.getProperty("AltBirthdayMailText", "Hello Adventurer!! Seeing as you're one year older now, I thought I would send you some birthday cheer :) Please find your birthday pack attached. May these gifts bring you joy and happiness on this very special day." + EOL + EOL + "Sincerely, Alegria");
			ENABLE_BLOCK_CHECKER_EVENT = Boolean.parseBoolean(General.getProperty("EnableBlockCheckerEvent", "false"));
			MIN_BLOCK_CHECKER_TEAM_MEMBERS = Integer.parseInt(General.getProperty("BlockCheckerMinTeamMembers", "2"));
			if (MIN_BLOCK_CHECKER_TEAM_MEMBERS < 1)
			{
				MIN_BLOCK_CHECKER_TEAM_MEMBERS = 1;
			}
			else if (MIN_BLOCK_CHECKER_TEAM_MEMBERS > 6)
			{
				MIN_BLOCK_CHECKER_TEAM_MEMBERS = 6;
			}
			HBCE_FAIR_PLAY = Boolean.parseBoolean(General.getProperty("HBCEFairPlay", "false"));
			HELLBOUND_WITHOUT_QUEST = Boolean.parseBoolean(General.getProperty("HellboundWithoutQuest", "false"));
			CLEAR_CREST_CACHE = Boolean.parseBoolean(General.getProperty("ClearClanCache", "false"));
			
			NORMAL_ENCHANT_COST_MULTIPLIER = Integer.parseInt(General.getProperty("NormalEnchantCostMultipiler", "1"));
			SAFE_ENCHANT_COST_MULTIPLIER = Integer.parseInt(General.getProperty("SafeEnchantCostMultipiler", "5"));
			
			// Load FloodProtector L2Properties file
			L2Properties FloodProtectors = new L2Properties();
			final File flood = new File(FLOOD_PROTECTOR_CONFIG);
			try (InputStream is = new FileInputStream(flood))
			{
				FloodProtectors.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Flood Protector settings!", e);
			}
			loadFloodProtectorConfigs(FloodProtectors);
			
			// Load NPC L2Properties file (if exists)
			L2Properties NPC = new L2Properties();
			final File npc = new File(NPC_CONFIG);
			try (InputStream is = new FileInputStream(npc))
			{
				NPC.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading NPC settings!", e);
			}
			
			ANNOUNCE_MAMMON_SPAWN = Boolean.parseBoolean(NPC.getProperty("AnnounceMammonSpawn", "False"));
			ALT_MOB_AGRO_IN_PEACEZONE = Boolean.parseBoolean(NPC.getProperty("AltMobAgroInPeaceZone", "True"));
			ALT_ATTACKABLE_NPCS = Boolean.parseBoolean(NPC.getProperty("AltAttackableNpcs", "True"));
			ALT_GAME_VIEWNPC = Boolean.parseBoolean(NPC.getProperty("AltGameViewNpc", "False"));
			MAX_DRIFT_RANGE = Integer.parseInt(NPC.getProperty("MaxDriftRange", "300"));
			DEEPBLUE_DROP_RULES = Boolean.parseBoolean(NPC.getProperty("UseDeepBlueDropRules", "True"));
			DEEPBLUE_DROP_RULES_RAID = Boolean.parseBoolean(NPC.getProperty("UseDeepBlueDropRulesRaid", "True"));
			SHOW_NPC_LVL = Boolean.parseBoolean(NPC.getProperty("ShowNpcLevel", "False"));
			SHOW_CREST_WITHOUT_QUEST = Boolean.parseBoolean(NPC.getProperty("ShowCrestWithoutQuest", "False"));
			ENABLE_RANDOM_ENCHANT_EFFECT = Boolean.parseBoolean(NPC.getProperty("EnableRandomEnchantEffect", "False"));
			MIN_NPC_LVL_DMG_PENALTY = Integer.parseInt(NPC.getProperty("MinNPCLevelForDmgPenalty", "78"));
			NPC_DMG_PENALTY = parseConfigLine(NPC.getProperty("DmgPenaltyForLvLDifferences", "0.7, 0.6, 0.6, 0.55"));
			NPC_CRIT_DMG_PENALTY = parseConfigLine(NPC.getProperty("CritDmgPenaltyForLvLDifferences", "0.75, 0.65, 0.6, 0.58"));
			NPC_SKILL_DMG_PENALTY = parseConfigLine(NPC.getProperty("SkillDmgPenaltyForLvLDifferences", "0.8, 0.7, 0.65, 0.62"));
			MIN_NPC_LVL_MAGIC_PENALTY = Integer.parseInt(NPC.getProperty("MinNPCLevelForMagicPenalty", "78"));
			NPC_SKILL_CHANCE_PENALTY = parseConfigLine(NPC.getProperty("SkillChancePenaltyForLvLDifferences", "2.5, 3.0, 3.25, 3.5"));
			DECAY_TIME_TASK = Integer.parseInt(NPC.getProperty("DecayTimeTask", "5000"));
			NPC_DECAY_TIME = Integer.parseInt(NPC.getProperty("NpcDecayTime", "8500"));
			RAID_BOSS_DECAY_TIME = Integer.parseInt(NPC.getProperty("RaidBossDecayTime", "30000"));
			SPOILED_DECAY_TIME = Integer.parseInt(NPC.getProperty("SpoiledDecayTime", "18500"));
			MAX_SWEEPER_TIME = Integer.parseInt(NPC.getProperty("MaxSweeperTime", "15000"));
			ENABLE_DROP_VITALITY_HERBS = Boolean.parseBoolean(NPC.getProperty("EnableVitalityHerbs", "True"));
			GUARD_ATTACK_AGGRO_MOB = Boolean.parseBoolean(NPC.getProperty("GuardAttackAggroMob", "False"));
			ALLOW_WYVERN_UPGRADER = Boolean.parseBoolean(NPC.getProperty("AllowWyvernUpgrader", "False"));
			String[] listPetRentNpc = NPC.getProperty("ListPetRentNpc", "30827").split(",");
			LIST_PET_RENT_NPC = new ArrayList<>(listPetRentNpc.length);
			for (String id : listPetRentNpc)
			{
				LIST_PET_RENT_NPC.add(Integer.valueOf(id));
			}
			RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(NPC.getProperty("RaidHpRegenMultiplier", "100")) / 100;
			RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(NPC.getProperty("RaidMpRegenMultiplier", "100")) / 100;
			RAID_PDEFENCE_MULTIPLIER = Double.parseDouble(NPC.getProperty("RaidPDefenceMultiplier", "100")) / 100;
			RAID_MDEFENCE_MULTIPLIER = Double.parseDouble(NPC.getProperty("RaidMDefenceMultiplier", "100")) / 100;
			RAID_PATTACK_MULTIPLIER = Double.parseDouble(NPC.getProperty("RaidPAttackMultiplier", "100")) / 100;
			RAID_MATTACK_MULTIPLIER = Double.parseDouble(NPC.getProperty("RaidMAttackMultiplier", "100")) / 100;
			RAID_MIN_RESPAWN_MULTIPLIER = Float.parseFloat(NPC.getProperty("RaidMinRespawnMultiplier", "1.0"));
			RAID_MAX_RESPAWN_MULTIPLIER = Float.parseFloat(NPC.getProperty("RaidMaxRespawnMultiplier", "1.0"));
			RAID_MINION_RESPAWN_TIMER = Integer.parseInt(NPC.getProperty("RaidMinionRespawnTime", "300000"));
			String[] propertySplit = NPC.getProperty("CustomMinionsRespawnTime", "").split(";");
			MINIONS_RESPAWN_TIME = new HashMap<>(propertySplit.length);
			for (String prop : propertySplit)
			{
				String[] propSplit = prop.split(",");
				if (propSplit.length != 2)
				{
					_log.warning(StringUtil.concat("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime \"", prop, "\""));
				}
				
				try
				{
					MINIONS_RESPAWN_TIME.put(Integer.valueOf(propSplit[0]), Integer.valueOf(propSplit[1]));
				}
				catch (NumberFormatException nfe)
				{
					if (!prop.isEmpty())
					{
						_log.warning(StringUtil.concat("[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime \"", propSplit[0], "\"", propSplit[1]));
					}
				}
			}
			
			RAID_DISABLE_CURSE = Boolean.parseBoolean(NPC.getProperty("DisableRaidCurse", "False"));
			RAID_CHAOS_TIME = Integer.parseInt(NPC.getProperty("RaidChaosTime", "10"));
			GRAND_CHAOS_TIME = Integer.parseInt(NPC.getProperty("GrandChaosTime", "10"));
			MINION_CHAOS_TIME = Integer.parseInt(NPC.getProperty("MinionChaosTime", "10"));
			INVENTORY_MAXIMUM_PET = Integer.parseInt(NPC.getProperty("MaximumSlotsForPet", "12"));
			PET_HP_REGEN_MULTIPLIER = Double.parseDouble(NPC.getProperty("PetHpRegenMultiplier", "100")) / 100;
			PET_MP_REGEN_MULTIPLIER = Double.parseDouble(NPC.getProperty("PetMpRegenMultiplier", "100")) / 100;
			// PremiumSystemOptions
			L2Properties PremiumSystemOptions = new L2Properties();
			final File services = new File(PREMIUM_SYSTEM_CONFIG);
			try (InputStream is = new FileInputStream(services))
			{
				PremiumSystemOptions.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading PremiumSystemOptions settings!", e);
			}
			USE_PREMIUMSERVICE = Boolean.parseBoolean(PremiumSystemOptions.getProperty("UsePremiumServices", "False"));
			DISABLED_FOR_NON_VIP = Boolean.parseBoolean(PremiumSystemOptions.getProperty("DisabledForNonVip", "False"));
			PREMIUM_RATE_XP = Float.parseFloat(PremiumSystemOptions.getProperty("PremiumRateXp", "2"));
			PREMIUM_RATE_SP = Float.parseFloat(PremiumSystemOptions.getProperty("PremiumRateSp", "2"));
			PREMIUM_RATE_DROP_SPOIL = Float.parseFloat(PremiumSystemOptions.getProperty("PremiumRateDropSpoil", "2"));
			PREMIUM_RATE_DROP_ITEMS = Float.parseFloat(PremiumSystemOptions.getProperty("PremiumRateDropItems", "2"));
			PREMIUM_RATE_DROP_ITEMS_BY_RAID = Float.parseFloat(PremiumSystemOptions.getProperty("PremiumRateRaidDropItems", "2"));
			String[] PremiumrateDropItemsById = PremiumSystemOptions.getProperty("PremiumRateDropItemsById", "").split(";");
			PREMIUM_RATE_DROP_ITEMS_ID = new HashMap<>(PremiumrateDropItemsById.length);
			if (!PremiumrateDropItemsById[0].isEmpty())
			{
				for (String item : PremiumrateDropItemsById)
				{
					String[] itemSplit = item.split(",");
					if (itemSplit.length != 2)
					{
						_log.warning(StringUtil.concat("Config.load(): invalid config property -> PremiumRateDropItemsById \"", item, "\""));
					}
					else
					{
						try
						{
							PREMIUM_RATE_DROP_ITEMS_ID.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!item.isEmpty())
							{
								_log.warning(StringUtil.concat("Config.load(): invalid config property -> PremiumRateDropItemsById \"", item, "\""));
							}
						}
					}
				}
			}
			if (!PREMIUM_RATE_DROP_ITEMS_ID.containsKey(PcInventory.ADENA_ID))
			{
				PREMIUM_RATE_DROP_ITEMS_ID.put(PcInventory.ADENA_ID, PREMIUM_RATE_DROP_ITEMS); // for Adena rate if not defined
			}
			// Load GEODATA_CONFIG L2Properties file (if exists)
			final File geo = new File(GEODATA_CONFIG);
			try (InputStream is = new FileInputStream(geo))
			{
				L2Properties geo_load = new L2Properties();
				geo_load.load(is);
				GEODATA = Integer.parseInt(geo_load.getProperty("GeoData", "0"));
				try
				{
					GEODATA_DIR = new File(geo_load.getProperty("GeodataDirectory", "data/geodata").replaceAll("\\\\", "/")).getCanonicalFile();
				}
				catch (IOException e)
				{
					_log.log(Level.WARNING, "Error setting geodata directory!", e);
					GEODATA_DIR = new File("data/geodata");
				}
				try
				{
					PATHNODE_DIR = new File(geo_load.getProperty("PathnodeDirectory", "data/pathnode").replaceAll("\\\\", "/")).getCanonicalFile();
				}
				catch (IOException e)
				{
					_log.log(Level.WARNING, "Error setting pathnode directory!", e);
					PATHNODE_DIR = new File("data/pathnode");
				}
				GEODATA_CELLFINDING = Boolean.parseBoolean(geo_load.getProperty("CellPathFinding", "False"));
				PATHFIND_BUFFERS = geo_load.getProperty("PathFindBuffers", "100x6;128x6;192x6;256x4;320x4;384x4;500x2");
				LOW_WEIGHT = Float.parseFloat(geo_load.getProperty("LowWeight", "0.5"));
				MEDIUM_WEIGHT = Float.parseFloat(geo_load.getProperty("MediumWeight", "2"));
				HIGH_WEIGHT = Float.parseFloat(geo_load.getProperty("HighWeight", "3"));
				ADVANCED_DIAGONAL_STRATEGY = Boolean.parseBoolean(geo_load.getProperty("AdvancedDiagonalStrategy", "True"));
				DIAGONAL_WEIGHT = Float.parseFloat(geo_load.getProperty("DiagonalWeight", "0.707"));
				MAX_POSTFILTER_PASSES = Integer.parseInt(geo_load.getProperty("MaxPostfilterPasses", "3"));
				DEBUG_PATH = Boolean.parseBoolean(geo_load.getProperty("DebugPath", "False"));
				FORCE_GEODATA = Boolean.parseBoolean(geo_load.getProperty("ForceGeodata", "True"));
				COORD_SYNCHRONIZE = Integer.parseInt(geo_load.getProperty("CoordSynchronize", "-1"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + GEODATA_CONFIG + " File.");
			}
			
			// Load OPTIMIZATION_CONFIG L2Properties file (if exists)
			final File opti = new File(OPTIMIZATION_CONFIG);
			try (InputStream is = new FileInputStream(opti))
			{
				L2Properties optimization_load = new L2Properties();
				optimization_load.load(is);
				ALLOW_DISCARDITEM = Boolean.parseBoolean(optimization_load.getProperty("AllowDiscardItem", "True"));
				AUTODESTROY_ITEM_AFTER = Integer.parseInt(optimization_load.getProperty("AutoDestroyDroppedItemAfter", "600"));
				HERB_AUTO_DESTROY_TIME = Integer.parseInt(optimization_load.getProperty("AutoDestroyHerbTime", "60")) * 1000;
				String[] split = optimization_load.getProperty("ListOfProtectedItems", "0").split(",");
				LIST_PROTECTED_ITEMS = new ArrayList<>(split.length);
				for (String id : split)
				{
					LIST_PROTECTED_ITEMS.add(Integer.parseInt(id));
				}
				DATABASE_CLEAN_UP = Boolean.parseBoolean(optimization_load.getProperty("DatabaseCleanUp", "true"));
				CONNECTION_CLOSE_TIME = Long.parseLong(optimization_load.getProperty("ConnectionCloseTime", "60000"));
				CHAR_STORE_INTERVAL = Integer.parseInt(optimization_load.getProperty("CharacterDataStoreInterval", "15"));
				LAZY_ITEMS_UPDATE = Boolean.parseBoolean(optimization_load.getProperty("LazyItemsUpdate", "false"));
				UPDATE_ITEMS_ON_CHAR_STORE = Boolean.parseBoolean(optimization_load.getProperty("UpdateItemsOnCharStore", "false"));
				DESTROY_DROPPED_PLAYER_ITEM = Boolean.parseBoolean(optimization_load.getProperty("DestroyPlayerDroppedItem", "false"));
				DESTROY_EQUIPABLE_PLAYER_ITEM = Boolean.parseBoolean(optimization_load.getProperty("DestroyEquipableItem", "false"));
				SAVE_DROPPED_ITEM = Boolean.parseBoolean(optimization_load.getProperty("SaveDroppedItem", "false"));
				EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.parseBoolean(optimization_load.getProperty("EmptyDroppedItemTableAfterLoad", "false"));
				SAVE_DROPPED_ITEM_INTERVAL = Integer.parseInt(optimization_load.getProperty("SaveDroppedItemInterval", "60")) * 60000;
				CLEAR_DROPPED_ITEM_TABLE = Boolean.parseBoolean(optimization_load.getProperty("ClearDroppedItemTable", "false"));
				AUTODELETE_INVALID_QUEST_DATA = Boolean.parseBoolean(optimization_load.getProperty("AutoDeleteInvalidQuestData", "False"));
				PRECISE_DROP_CALCULATION = Boolean.parseBoolean(optimization_load.getProperty("PreciseDropCalculation", "True"));
				MULTIPLE_ITEM_DROP = Boolean.parseBoolean(optimization_load.getProperty("MultipleItemDrop", "True"));
				FORCE_INVENTORY_UPDATE = Boolean.parseBoolean(optimization_load.getProperty("ForceInventoryUpdate", "False"));
				LAZY_CACHE = Boolean.parseBoolean(optimization_load.getProperty("LazyCache", "True"));
				CACHE_CHAR_NAMES = Boolean.parseBoolean(optimization_load.getProperty("CacheCharNames", "True"));
				MIN_NPC_ANIMATION = Integer.parseInt(optimization_load.getProperty("MinNPCAnimation", "10"));
				MAX_NPC_ANIMATION = Integer.parseInt(optimization_load.getProperty("MaxNPCAnimation", "20"));
				MIN_MONSTER_ANIMATION = Integer.parseInt(optimization_load.getProperty("MinMonsterAnimation", "5"));
				MAX_MONSTER_ANIMATION = Integer.parseInt(optimization_load.getProperty("MaxMonsterAnimation", "20"));
				MOVE_BASED_KNOWNLIST = Boolean.parseBoolean(optimization_load.getProperty("MoveBasedKnownlist", "False"));
				KNOWNLIST_UPDATE_INTERVAL = Long.parseLong(optimization_load.getProperty("KnownListUpdateInterval", "1250"));
				GRIDS_ALWAYS_ON = Boolean.parseBoolean(optimization_load.getProperty("GridsAlwaysOn", "False"));
				GRID_NEIGHBOR_TURNON_TIME = Integer.parseInt(optimization_load.getProperty("GridNeighborTurnOnTime", "1"));
				GRID_NEIGHBOR_TURNOFF_TIME = Integer.parseInt(optimization_load.getProperty("GridNeighborTurnOffTime", "90"));
				
				split = optimization_load.getProperty("NonTalkingNpcs", "18684,18685,18686,18687,18688,18689,18690,19691,18692,31202,31203,31204,31205,31206,31207,31208,31209,31266,31557,31593,31606,31671,31672,31673,31674,31758,31955,32026,32030,32031,32032,32306,32619,32620,32621").split(",");
				NON_TALKING_NPCS = new ArrayList<>(split.length);
				for (String npcId : split)
				{
					try
					{
						NON_TALKING_NPCS.add(Integer.parseInt(npcId));
					}
					catch (NumberFormatException nfe)
					{
						if (!npcId.isEmpty())
						{
							_log.warning("Could not parse " + npcId + " id for NonTalkingNpcs. Please check that all values are digits and coma separated.");
						}
					}
				}
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + OPTIMIZATION_CONFIG + " File.");
			}
			
			// Load CLIENT_PACKETS_CONFIG L2Properties file (if exists)
			final File client = new File(CLIENT_PACKETS_CONFIG);
			try (InputStream is = new FileInputStream(client))
			{
				L2Properties client_load = new L2Properties();
				client_load.load(is);
				CLIENT_PACKET_QUEUE_SIZE = Integer.parseInt(client_load.getProperty("ClientPacketQueueSize", "0"));
				if (CLIENT_PACKET_QUEUE_SIZE == 0)
				{
					CLIENT_PACKET_QUEUE_SIZE = MMO_MAX_READ_PER_PASS + 2;
				}
				CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = Integer.parseInt(client_load.getProperty("ClientPacketQueueMaxBurstSize", "0"));
				if (CLIENT_PACKET_QUEUE_MAX_BURST_SIZE == 0)
				{
					CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMO_MAX_READ_PER_PASS + 1;
				}
				CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = Integer.parseInt(client_load.getProperty("ClientPacketQueueMaxPacketsPerSecond", "80"));
				CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = Integer.parseInt(client_load.getProperty("ClientPacketQueueMeasureInterval", "5"));
				CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = Integer.parseInt(client_load.getProperty("ClientPacketQueueMaxAveragePacketsPerSecond", "40"));
				CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = Integer.parseInt(client_load.getProperty("ClientPacketQueueMaxFloodsPerMin", "2"));
				CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = Integer.parseInt(client_load.getProperty("ClientPacketQueueMaxOverflowsPerMin", "1"));
				CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = Integer.parseInt(client_load.getProperty("ClientPacketQueueMaxUnderflowsPerMin", "1"));
				CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = Integer.parseInt(client_load.getProperty("ClientPacketQueueMaxUnknownPerMin", "5"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + CLIENT_PACKETS_CONFIG + " File.");
			}
			
			// Load THREAD_CONFIG L2Properties file (if exists)
			final File thread = new File(THREAD_CONFIG);
			try (InputStream is = new FileInputStream(thread))
			{
				L2Properties thread_load = new L2Properties();
				thread_load.load(is);
				THREAD_P_EFFECTS = Integer.parseInt(thread_load.getProperty("ThreadPoolSizeEffects", "10"));
				THREAD_P_GENERAL = Integer.parseInt(thread_load.getProperty("ThreadPoolSizeGeneral", "13"));
				IO_PACKET_THREAD_CORE_SIZE = Integer.parseInt(thread_load.getProperty("UrgentPacketThreadCoreSize", "2"));
				GENERAL_PACKET_THREAD_CORE_SIZE = Integer.parseInt(thread_load.getProperty("GeneralPacketThreadCoreSize", "4"));
				GENERAL_THREAD_CORE_SIZE = Integer.parseInt(thread_load.getProperty("GeneralThreadCoreSize", "4"));
				AI_MAX_THREAD = Integer.parseInt(thread_load.getProperty("AiMaxThread", "6"));
				DEADLOCK_DETECTOR = Boolean.parseBoolean(thread_load.getProperty("DeadLockDetector", "True"));
				DEADLOCK_CHECK_INTERVAL = Integer.parseInt(thread_load.getProperty("DeadLockCheckInterval", "20"));
				RESTART_ON_DEADLOCK = Boolean.parseBoolean(thread_load.getProperty("RestartOnDeadlock", "False"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + THREAD_CONFIG + " File.");
			}
			
			// Load SECURITY_CONFIG L2Properties file (if exists)
			final File sec = new File(SECURITY_CONFIG);
			try (InputStream is = new FileInputStream(sec))
			{
				L2Properties sec_load = new L2Properties();
				sec_load.load(is);
				BYPASS_VALIDATION = Boolean.parseBoolean(sec_load.getProperty("BypassValidation", "True"));
				GAMEGUARD_ENFORCE = Boolean.parseBoolean(sec_load.getProperty("GameGuardEnforce", "False"));
				GAMEGUARD_PROHIBITACTION = Boolean.parseBoolean(sec_load.getProperty("GameGuardProhibitAction", "False"));
				LOG_CHAT = Boolean.parseBoolean(sec_load.getProperty("LogChat", "false"));
				LOG_AUTO_ANNOUNCEMENTS = Boolean.parseBoolean(sec_load.getProperty("LogAutoAnnouncements", "False"));
				LOG_ITEMS = Boolean.parseBoolean(sec_load.getProperty("LogItems", "false"));
				LOG_ITEMS_SMALL_LOG = Boolean.parseBoolean(sec_load.getProperty("LogItemsSmallLog", "false"));
				LOG_ITEM_ENCHANTS = Boolean.parseBoolean(sec_load.getProperty("LogItemEnchants", "false"));
				LOG_SKILL_ENCHANTS = Boolean.parseBoolean(sec_load.getProperty("LogSkillEnchants", "false"));
				GMAUDIT = Boolean.parseBoolean(sec_load.getProperty("GMAudit", "False"));
				LOG_GAME_DAMAGE = Boolean.parseBoolean(sec_load.getProperty("LogGameDamage", "False"));
				LOG_GAME_DAMAGE_THRESHOLD = Integer.parseInt(sec_load.getProperty("LogGameDamageThreshold", "5000"));
				SKILL_CHECK_ENABLE = Boolean.parseBoolean(sec_load.getProperty("SkillCheckEnable", "False"));
				SKILL_CHECK_REMOVE = Boolean.parseBoolean(sec_load.getProperty("SkillCheckRemove", "False"));
				SKILL_CHECK_GM = Boolean.parseBoolean(sec_load.getProperty("SkillCheckGM", "True"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + SECURITY_CONFIG + " File.");
			}
			
			// Load ANTI_BOT L2Properties file (if exists)
			final File ab = new File(ANTI_BOT_CONFIG);
			try (InputStream is = new FileInputStream(ab))
			{
				L2Properties ab_load = new L2Properties();
				ab_load.load(is);
				START_CAPTCHA = Integer.parseInt(ab_load.getProperty("StartAntibot", "0"));
				GET_KILLS = Integer.parseInt(ab_load.getProperty("NextCheckOn", "50"));
				GET_TIME_TO_FILL = Integer.parseInt(ab_load.getProperty("TimeToFill", "60000"));
				
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + ANTI_BOT_CONFIG + " File.");
			}
			
			// Load ADMIN_CONFIG L2Properties file (if exists)
			final File admin = new File(ADMIN_CONFIG);
			try (InputStream is = new FileInputStream(admin))
			{
				L2Properties admin_load = new L2Properties();
				admin_load.load(is);
				EVERYBODY_HAS_ADMIN_RIGHTS = Boolean.parseBoolean(admin_load.getProperty("EverybodyHasAdminRights", "false"));
				SERVER_LIST_BRACKET = Boolean.parseBoolean(admin_load.getProperty("ServerListBrackets", "false"));
				SERVER_LIST_TYPE = getServerTypeId(admin_load.getProperty("ServerListType", "Normal").split(","));
				SERVER_LIST_AGE = Integer.parseInt(admin_load.getProperty("ServerListAge", "0"));
				SERVER_GMONLY = Boolean.parseBoolean(admin_load.getProperty("ServerGMOnly", "false"));
				GM_HERO_AURA = Boolean.parseBoolean(admin_load.getProperty("GMHeroAura", "False"));
				GM_STARTUP_INVULNERABLE = Boolean.parseBoolean(admin_load.getProperty("GMStartupInvulnerable", "False"));
				GM_STARTUP_INVISIBLE = Boolean.parseBoolean(admin_load.getProperty("GMStartupInvisible", "False"));
				GM_STARTUP_SILENCE = Boolean.parseBoolean(admin_load.getProperty("GMStartupSilence", "False"));
				GM_STARTUP_AUTO_LIST = Boolean.parseBoolean(admin_load.getProperty("GMStartupAutoList", "False"));
				GM_STARTUP_DIET_MODE = Boolean.parseBoolean(admin_load.getProperty("GMStartupDietMode", "False"));
				GM_ADMIN_MENU_STYLE = admin_load.getProperty("GMAdminMenuStyle", "modern");
				GM_ITEM_RESTRICTION = Boolean.parseBoolean(admin_load.getProperty("GMItemRestriction", "True"));
				GM_SKILL_RESTRICTION = Boolean.parseBoolean(admin_load.getProperty("GMSkillRestriction", "True"));
				GM_TRADE_RESTRICTED_ITEMS = Boolean.parseBoolean(admin_load.getProperty("GMTradeRestrictedItems", "False"));
				GM_RESTART_FIGHTING = Boolean.parseBoolean(admin_load.getProperty("GMRestartFighting", "True"));
				GM_ANNOUNCER_NAME = Boolean.parseBoolean(admin_load.getProperty("GMShowAnnouncerName", "False"));
				GM_CRITANNOUNCER_NAME = Boolean.parseBoolean(admin_load.getProperty("GMShowCritAnnouncerName", "False"));
				GM_GIVE_SPECIAL_SKILLS = Boolean.parseBoolean(admin_load.getProperty("GMGiveSpecialSkills", "False"));
				GM_GIVE_SPECIAL_AURA_SKILLS = Boolean.parseBoolean(admin_load.getProperty("GMGiveSpecialAuraSkills", "False"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + ADMIN_CONFIG + " File.");
			}
			
			// TODO:
			// Load ANTHARAS_CONFIG L2Properties file (if exists)
			final File antharas = new File(ANTHARAS_CONFIG);
			try (InputStream is = new FileInputStream(antharas))
			{
				L2Properties antharas_load = new L2Properties();
				antharas_load.load(is);
				ANTHARAS_WAIT_TIME = Integer.parseInt(antharas_load.getProperty("AntharasWaitTime", "30"));
				ANTHARAS_SPAWN_INTERVAL = Integer.parseInt(antharas_load.getProperty("IntervalOfAntharasSpawn", "264"));
				ANTHARAS_SPAWN_RANDOM = Integer.parseInt(antharas_load.getProperty("RandomOfAntharasSpawn", "72"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + ANTHARAS_CONFIG + " File.");
			}
			// Load VALAKAS_CONFIG L2Properties file (if exists)
			final File valakas = new File(VALAKAS_CONFIG);
			try (InputStream is = new FileInputStream(valakas))
			{
				L2Properties valakas_load = new L2Properties();
				valakas_load.load(is);
				VALAKAS_WAIT_TIME = Integer.parseInt(valakas_load.getProperty("ValakasWaitTime", "30"));
				VALAKAS_SPAWN_INTERVAL = Integer.parseInt(valakas_load.getProperty("IntervalOfValakasSpawn", "264"));
				VALAKAS_SPAWN_RANDOM = Integer.parseInt(valakas_load.getProperty("RandomOfValakasSpawn", "72"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + VALAKAS_CONFIG + " File.");
			}
			// Load BAIUM_CONFIG L2Properties file (if exists)
			final File baium = new File(BAIUM_CONFIG);
			try (InputStream is = new FileInputStream(baium))
			{
				L2Properties baium_load = new L2Properties();
				baium_load.load(is);
				BAIUM_SPAWN_INTERVAL = Integer.parseInt(baium_load.getProperty("IntervalOfBaiumSpawn", "168"));
				BAIUM_SPAWN_RANDOM = Integer.parseInt(baium_load.getProperty("RandomOfBaiumSpawn", "48"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + BAIUM_CONFIG + " File.");
			}
			// Load CORE_CONFIG L2Properties file (if exists)
			final File core = new File(CORE_CONFIG);
			try (InputStream is = new FileInputStream(core))
			{
				L2Properties core_load = new L2Properties();
				core_load.load(is);
				CORE_SPAWN_INTERVAL = Integer.parseInt(core_load.getProperty("IntervalOfCoreSpawn", "60"));
				CORE_SPAWN_RANDOM = Integer.parseInt(core_load.getProperty("RandomOfCoreSpawn", "24"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + CORE_CONFIG + " File.");
			}
			
			// Load FREYA_EASY_CONFIG L2Properties file (if exists)
			final File freyaeasy = new File(FREYA_EASY_CONFIG);
			try (InputStream is = new FileInputStream(freyaeasy))
			{
				L2Properties freyaeasy_load = new L2Properties();
				freyaeasy_load.load(is);
				MIN_PLAYER_LEVEL_TO_EASY = Integer.parseInt(freyaeasy_load.getProperty("MinLevel", "78"));
				MIN_PLAYERS_TO_EASY = Integer.parseInt(freyaeasy_load.getProperty("MinPlayers", "18"));
				MAX_PLAYERS_TO_EASY = Integer.parseInt(freyaeasy_load.getProperty("MaxPlayers", "27"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + FREYA_EASY_CONFIG + " File.");
			}
			// Load FREYA_HARDCORE_CONFIG L2Properties file (if exists)
			final File freyahardcore = new File(FREYA_HARDCORE_CONFIG);
			try (InputStream is = new FileInputStream(freyahardcore))
			{
				L2Properties freyahardcore_load = new L2Properties();
				freyahardcore_load.load(is);
				MIN_PLAYER_LEVEL_TO_HARD = Integer.parseInt(freyahardcore_load.getProperty("MinLevel", "82"));
				MIN_PLAYERS_TO_HARD = Integer.parseInt(freyahardcore_load.getProperty("MinPlayers", "36"));
				MAX_PLAYERS_TO_HARD = Integer.parseInt(freyahardcore_load.getProperty("MaxPlayers", "45"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + FREYA_HARDCORE_CONFIG + " File.");
			}
			// Load ORFEN_CONFIG L2Properties file (if exists)
			final File orfen = new File(ORFEN_CONFIG);
			try (InputStream is = new FileInputStream(orfen))
			{
				L2Properties orfen_load = new L2Properties();
				orfen_load.load(is);
				ORFEN_SPAWN_INTERVAL = Integer.parseInt(orfen_load.getProperty("IntervalOfOrfenSpawn", "48"));
				ORFEN_SPAWN_RANDOM = Integer.parseInt(orfen_load.getProperty("RandomOfOrfenSpawn", "20"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + ORFEN_CONFIG + " File.");
			}
			// Load QUEEN_ANT_CONFIG L2Properties file (if exists)
			final File queen = new File(QUEEN_ANT_CONFIG);
			try (InputStream is = new FileInputStream(queen))
			{
				L2Properties queen_load = new L2Properties();
				queen_load.load(is);
				QUEEN_ANT_SPAWN_INTERVAL = Integer.parseInt(queen_load.getProperty("IntervalOfQueenAntSpawn", "36"));
				QUEEN_ANT_SPAWN_RANDOM = Integer.parseInt(queen_load.getProperty("RandomOfQueenAntSpawn", "17"));
				MAX_LEVEL_FOR_AQ_ZONE = Integer.parseInt(queen_load.getProperty("MaxLevelToEnter", "50"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + QUEEN_ANT_CONFIG + " File.");
			}
			// Load BELETH_CONFIG L2Properties file (if exists)
			final File beleth = new File(BELETH_CONFIG);
			try (InputStream is = new FileInputStream(beleth))
			{
				L2Properties beleth_load = new L2Properties();
				beleth_load.load(is);
				BELETH_SPAWN_INTERVAL = Integer.parseInt(beleth_load.getProperty("IntervalOfBelethSpawn", "192"));
				BELETH_SPAWN_RANDOM = Integer.parseInt(beleth_load.getProperty("RandomOfBelethSpawn", "148"));
				BELETH_MIN_PLAYERS = Integer.parseInt(beleth_load.getProperty("BelethMinPlayers", "36"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + BELETH_CONFIG + " File.");
			}
			// Load FRINTEZZA_CONFIG L2Properties file (if exists)
			final File frintezza = new File(FRINTEZZA_CONFIG);
			try (InputStream is = new FileInputStream(frintezza))
			{
				L2Properties frintezza_load = new L2Properties();
				frintezza_load.load(is);
				MIN_PLAYER_TO_FE = Integer.parseInt(frintezza_load.getProperty("MinPlayers", "36"));
				MAX_PLAYER_TO_FE = Integer.parseInt(frintezza_load.getProperty("MaxPlayers", "45"));
				MIN_LEVEL_TO_FE = Integer.parseInt(frintezza_load.getProperty("MinLevel", "80"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + FRINTEZZA_CONFIG + " File.");
			}
			// Load SAILREN_CONFIG L2Properties file (if exists)
			final File sailren = new File(SAILREN_CONFIG);
			try (InputStream is = new FileInputStream(sailren))
			{
				L2Properties sailren_load = new L2Properties();
				sailren_load.load(is);
				SAILREN_SPAWN_INTERVAL = Integer.parseInt(sailren_load.getProperty("IntervalOfSailrenSpawn", "12"));
				SAILREN_SPAWN_RANDOM = Integer.parseInt(sailren_load.getProperty("RandomOfSailrenSpawn", "24"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + SAILREN_CONFIG + " File.");
			}
			// Load ZAKEN_CONFIG L2Properties file (if exists)
			final File zaken = new File(ZAKEN_CONFIG);
			try (InputStream is = new FileInputStream(zaken))
			{
				L2Properties zaken_load = new L2Properties();
				zaken_load.load(is);
				MIN_ZAKEN_DAY_PLAYERS = Integer.parseInt(zaken_load.getProperty("MinZakenDayPlayers", "9"));
				MAX_ZAKEN_DAY_PLAYERS = Integer.parseInt(zaken_load.getProperty("MaxZakenDayPlayers", "27"));
				MIN_ZAKEN_NIGHT_PLAYERS = Integer.parseInt(zaken_load.getProperty("MinZakenNightPlayers", "72"));
				MAX_ZAKEN_NIGHT_PLAYERS = Integer.parseInt(zaken_load.getProperty("MaxZakenNightPlayers", "450"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + ZAKEN_CONFIG + " File.");
			}
			
			// Load DESTRUCTION_BOSSES_CONFIG L2Properties file (if exists)
			final File destr = new File(DESTRUCTION_BOSSES_CONFIG);
			try (InputStream is = new FileInputStream(destr))
			{
				L2Properties destr_load = new L2Properties();
				destr_load.load(is);
				CHANGE_STATUS = Integer.parseInt(destr_load.getProperty("ChangeStatus", "30"));
				CHANCE_SPAWN = Integer.parseInt(destr_load.getProperty("ChanceSpawn", "50"));
				RESPAWN_TIME = Integer.parseInt(destr_load.getProperty("RespawnTime", "720"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + DESTRUCTION_BOSSES_CONFIG + " File.");
			}
			
			// Load TIAT_CONFIG L2Properties file (if exists)
			final File sod = new File(TIAT_CONFIG);
			try (InputStream is = new FileInputStream(sod))
			{
				L2Properties sod_load = new L2Properties();
				sod_load.load(is);
				SOD_TIAT_KILL_COUNT = Integer.parseInt(sod_load.getProperty("TiatKillCountForNextState", "10"));
				SOD_STAGE_2_LENGTH = Long.parseLong(sod_load.getProperty("Stage2Length", "720")) * 60000;
				MIN_TIAT_PLAYERS = Integer.parseInt(sod_load.getProperty("MinTiatPlayers", "36"));
				MAX_TIAT_PLAYERS = Integer.parseInt(sod_load.getProperty("MaxTiatPlayers", "45"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + TIAT_CONFIG + " File.");
			}
			// Load EKIMUS_CONFIG L2Properties file (if exists)
			final File soi = new File(EKIMUS_CONFIG);
			try (InputStream is = new FileInputStream(soi))
			{
				L2Properties soi_load = new L2Properties();
				soi_load.load(is);
				SOI_EKIMUS_KILL_COUNT = Integer.parseInt(soi_load.getProperty("EkimusKillCount", "5"));
				EROSION_ATTACK_MIN_PLAYERS = Integer.parseInt(soi_load.getProperty("MinEroAttPlayers", "18"));
				EROSION_ATTACK_MAX_PLAYERS = Integer.parseInt(soi_load.getProperty("MaxEroAttPlayers", "27"));
				EROSION_DEFENCE_MIN_PLAYERS = Integer.parseInt(soi_load.getProperty("MinEroDefPlayers", "18"));
				EROSION_DEFENCE_MAX_PLAYERS = Integer.parseInt(soi_load.getProperty("MaxEroDefPlayers", "27"));
				HEART_ATTACK_MIN_PLAYERS = Integer.parseInt(soi_load.getProperty("MinHeaAttPlayers", "18"));
				HEART_ATTACK_MAX_PLAYERS = Integer.parseInt(soi_load.getProperty("MaxHeaAttPlayers", "27"));
				HEART_DEFENCE_MIN_PLAYERS = Integer.parseInt(soi_load.getProperty("MinHeaDefPlayers", "18"));
				HEART_DEFENCE_MAX_PLAYERS = Integer.parseInt(soi_load.getProperty("MaxHeaDefPlayers", "27"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + EKIMUS_CONFIG + " File.");
			}
			
			// Load Rates L2Properties file (if exists)
			final File rates = new File(RATES_CONFIG);
			L2Properties RatesSettings = new L2Properties();
			try (InputStream is = new FileInputStream(rates))
			{
				RatesSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Rates settings!", e);
			}
			RATE_XP = Float.parseFloat(RatesSettings.getProperty("RateXp", "1."));
			RATE_SP = Float.parseFloat(RatesSettings.getProperty("RateSp", "1."));
			RATE_PARTY_XP = Float.parseFloat(RatesSettings.getProperty("RatePartyXp", "1."));
			RATE_PARTY_SP = Float.parseFloat(RatesSettings.getProperty("RatePartySp", "1."));
			RATE_CONSUMABLE_COST = Float.parseFloat(RatesSettings.getProperty("RateConsumableCost", "1."));
			RATE_EXTRACTABLE = Float.parseFloat(RatesSettings.getProperty("RateExtractable", "1."));
			RATE_DROP_ITEMS = Float.parseFloat(RatesSettings.getProperty("RateDropItems", "1."));
			RATE_DROP_ITEMS_BY_RAID = Float.parseFloat(RatesSettings.getProperty("RateRaidDropItems", "1."));
			RATE_DROP_SPOIL = Float.parseFloat(RatesSettings.getProperty("RateDropSpoil", "1."));
			RATE_DROP_MANOR = Integer.parseInt(RatesSettings.getProperty("RateDropManor", "1"));
			RATE_QUEST_DROP = Float.parseFloat(RatesSettings.getProperty("RateQuestDrop", "1."));
			RATE_QUEST_REWARD = Float.parseFloat(RatesSettings.getProperty("RateQuestReward", "1."));
			RATE_QUEST_REWARD_XP = Float.parseFloat(RatesSettings.getProperty("RateQuestRewardXP", "1."));
			RATE_QUEST_REWARD_SP = Float.parseFloat(RatesSettings.getProperty("RateQuestRewardSP", "1."));
			RATE_QUEST_REWARD_ADENA = Float.parseFloat(RatesSettings.getProperty("RateQuestRewardAdena", "1."));
			RATE_QUEST_REWARD_USE_MULTIPLIERS = Boolean.parseBoolean(RatesSettings.getProperty("UseQuestRewardMultipliers", "False"));
			RATE_QUEST_REWARD_POTION = Float.parseFloat(RatesSettings.getProperty("RateQuestRewardPotion", "1."));
			RATE_QUEST_REWARD_SCROLL = Float.parseFloat(RatesSettings.getProperty("RateQuestRewardScroll", "1."));
			RATE_QUEST_REWARD_RECIPE = Float.parseFloat(RatesSettings.getProperty("RateQuestRewardRecipe", "1."));
			RATE_QUEST_REWARD_MATERIAL = Float.parseFloat(RatesSettings.getProperty("RateQuestRewardMaterial", "1."));
			RATE_HB_TRUST_INCREASE = Float.parseFloat(RatesSettings.getProperty("RateHellboundTrustIncrease", "1."));
			RATE_HB_TRUST_DECREASE = Float.parseFloat(RatesSettings.getProperty("RateHellboundTrustDecrease", "1."));
			
			RATE_VITALITY_LEVEL_1 = Float.parseFloat(RatesSettings.getProperty("RateVitalityLevel1", "1.5"));
			RATE_VITALITY_LEVEL_2 = Float.parseFloat(RatesSettings.getProperty("RateVitalityLevel2", "2."));
			RATE_VITALITY_LEVEL_3 = Float.parseFloat(RatesSettings.getProperty("RateVitalityLevel3", "2.5"));
			RATE_VITALITY_LEVEL_4 = Float.parseFloat(RatesSettings.getProperty("RateVitalityLevel4", "3."));
			RATE_RECOVERY_VITALITY_PEACE_ZONE = Float.parseFloat(RatesSettings.getProperty("RateRecoveryPeaceZone", "1."));
			RATE_VITALITY_LOST = Float.parseFloat(RatesSettings.getProperty("RateVitalityLost", "1."));
			RATE_VITALITY_GAIN = Float.parseFloat(RatesSettings.getProperty("RateVitalityGain", "1."));
			RATE_RECOVERY_ON_RECONNECT = Float.parseFloat(RatesSettings.getProperty("RateRecoveryOnReconnect", "4."));
			RATE_KARMA_EXP_LOST = Float.parseFloat(RatesSettings.getProperty("RateKarmaExpLost", "1."));
			RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(RatesSettings.getProperty("RateSiegeGuardsPrice", "1."));
			RATE_DROP_COMMON_HERBS = Float.parseFloat(RatesSettings.getProperty("RateCommonHerbs", "1."));
			RATE_DROP_HP_HERBS = Float.parseFloat(RatesSettings.getProperty("RateHpHerbs", "1."));
			RATE_DROP_MP_HERBS = Float.parseFloat(RatesSettings.getProperty("RateMpHerbs", "1."));
			RATE_DROP_SPECIAL_HERBS = Float.parseFloat(RatesSettings.getProperty("RateSpecialHerbs", "1."));
			RATE_DROP_VITALITY_HERBS = Float.parseFloat(RatesSettings.getProperty("RateVitalityHerbs", "1."));
			PLAYER_DROP_LIMIT = Integer.parseInt(RatesSettings.getProperty("PlayerDropLimit", "3"));
			PLAYER_RATE_DROP = Integer.parseInt(RatesSettings.getProperty("PlayerRateDrop", "5"));
			PLAYER_RATE_DROP_ITEM = Integer.parseInt(RatesSettings.getProperty("PlayerRateDropItem", "70"));
			PLAYER_RATE_DROP_EQUIP = Integer.parseInt(RatesSettings.getProperty("PlayerRateDropEquip", "25"));
			PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(RatesSettings.getProperty("PlayerRateDropEquipWeapon", "5"));
			PET_XP_RATE = Float.parseFloat(RatesSettings.getProperty("PetXpRate", "1."));
			PET_FOOD_RATE = Integer.parseInt(RatesSettings.getProperty("PetFoodRate", "1"));
			SINEATER_XP_RATE = Float.parseFloat(RatesSettings.getProperty("SinEaterXpRate", "1."));
			KARMA_DROP_LIMIT = Integer.parseInt(RatesSettings.getProperty("KarmaDropLimit", "10"));
			KARMA_RATE_DROP = Integer.parseInt(RatesSettings.getProperty("KarmaRateDrop", "70"));
			KARMA_RATE_DROP_ITEM = Integer.parseInt(RatesSettings.getProperty("KarmaRateDropItem", "50"));
			KARMA_RATE_DROP_EQUIP = Integer.parseInt(RatesSettings.getProperty("KarmaRateDropEquip", "40"));
			KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(RatesSettings.getProperty("KarmaRateDropEquipWeapon", "10"));
			
			// Initializing table
			PLAYER_XP_PERCENT_LOST = new double[Byte.MAX_VALUE + 1];
			
			// Default value
			for (int i = 0; i <= Byte.MAX_VALUE; i++)
			{
				PLAYER_XP_PERCENT_LOST[i] = 1.;
			}
			
			// Now loading into table parsed values
			try
			{
				String[] values = RatesSettings.getProperty("PlayerXPPercentLost", "0,39-7.0;40,75-4.0;76,76-2.5;77,77-2.0;78,78-1.5").split(";");
				
				for (String s : values)
				{
					int min;
					int max;
					double val;
					
					String[] vals = s.split("-");
					String[] mM = vals[0].split(",");
					
					min = Integer.parseInt(mM[0]);
					max = Integer.parseInt(mM[1]);
					val = Double.parseDouble(vals[1]);
					
					for (int i = min; i <= max; i++)
					{
						PLAYER_XP_PERCENT_LOST[i] = val;
					}
				}
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Error while loading Player XP percent lost!", e);
			}
			
			String[] rateDropItemsById = RatesSettings.getProperty("RateDropItemsById", "").split(";");
			RATE_DROP_ITEMS_ID = new HashMap<>(rateDropItemsById.length);
			if (!rateDropItemsById[0].isEmpty())
			{
				for (String item : rateDropItemsById)
				{
					String[] itemSplit = item.split(",");
					if (itemSplit.length != 2)
					{
						_log.warning(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
					}
					else
					{
						try
						{
							RATE_DROP_ITEMS_ID.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
						}
						catch (NumberFormatException nfe)
						{
							if (!item.isEmpty())
							{
								_log.warning(StringUtil.concat("Config.load(): invalid config property -> RateDropItemsById \"", item, "\""));
							}
						}
					}
				}
			}
			if (!RATE_DROP_ITEMS_ID.containsKey(PcInventory.ADENA_ID))
			{
				RATE_DROP_ITEMS_ID.put(PcInventory.ADENA_ID, RATE_DROP_ITEMS); // for Adena rate if not defined
			}
			
			// Load L2JMod L2Properties file (if exists)
			L2Properties L2PSModSettings = new L2Properties();
			final File l2psmods = new File(L2PSMODS_CONFIG);
			try (InputStream is = new FileInputStream(l2psmods))
			{
				L2PSModSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading L2JMod settings!", e);
			}
			SPAWN_CHAR = Boolean.parseBoolean(L2PSModSettings.getProperty("CustomSpawn", "false"));
			SPAWN_X = Integer.parseInt(L2PSModSettings.getProperty("SpawnX", ""));
			SPAWN_Y = Integer.parseInt(L2PSModSettings.getProperty("SpawnY", ""));
			SPAWN_Z = Integer.parseInt(L2PSModSettings.getProperty("SpawnZ", ""));
			
			CHAR_TITLE = Boolean.parseBoolean(L2PSModSettings.getProperty("CharTitle", "true"));
			ADD_CHAR_TITLE = L2PSModSettings.getProperty("CharAddTitle", "L][ Mineral");
			
			ANNOUNCE_HERO_LOGIN = Boolean.parseBoolean(L2PSModSettings.getProperty("AnnounceHero", "false"));
			ANNOUNCE_CASTLE_LORDS = Boolean.parseBoolean(L2PSModSettings.getProperty("AnnounceCastleLord", "false"));
			ANNOUNCE_GM_LOGIN = Boolean.parseBoolean(L2PSModSettings.getProperty("AnnounceGM", "False"));
			
			ALLOW_ANNOUNCE_ONLINE_PLAYERS = Boolean.parseBoolean(L2PSModSettings.getProperty("AnnouncePlayers", "False"));
			FAKE_PLAYERS = Integer.parseInt(L2PSModSettings.getProperty("FakePlayers", "324"));
			ANNOUNCE_ONLINE_PLAYERS_DELAY = Integer.parseInt(L2PSModSettings.getProperty("OnlineInterval", "600"));
			CRITICAL_ONLINE_ANNOUNCE = Boolean.parseBoolean(L2PSModSettings.getProperty("CriticalAnnounce", "False"));
			
			ANNOUNCE_BOSS_SPAWN = Boolean.parseBoolean(L2PSModSettings.getProperty("AnnounceBossSpawn", "false"));
			ANNOUNCE_BOSS_SPAWN_CRIT = Boolean.parseBoolean(L2PSModSettings.getProperty("CriticalAnnounce", "false"));
			ANNOUNCE_BOSS_MSG = L2PSModSettings.getProperty("AnnounceBossMsg", "[Info] Raidboss %raidboss% has been spawned!");
			
			ENABLE_WAREHOUSESORTING_CLAN = Boolean.parseBoolean(L2PSModSettings.getProperty("EnableWarehouseSortingClan", "False"));
			ENABLE_WAREHOUSESORTING_PRIVATE = Boolean.parseBoolean(L2PSModSettings.getProperty("EnableWarehouseSortingPrivate", "False"));
			
			BANKING_SYSTEM_ENABLED = Boolean.parseBoolean(L2PSModSettings.getProperty("BankingEnabled", "false"));
			BANKING_SYSTEM_GOLDBARS = Integer.parseInt(L2PSModSettings.getProperty("BankingGoldbarCount", "1"));
			BANKING_SYSTEM_ADENA = Integer.parseInt(L2PSModSettings.getProperty("BankingAdenaCount", "500000000"));
			
			ENABLE_MANA_POTIONS_SUPPORT = Boolean.parseBoolean(L2PSModSettings.getProperty("EnableManaPotionSupport", "false"));
			
			DISPLAY_SERVER_TIME = Boolean.parseBoolean(L2PSModSettings.getProperty("DisplayServerTime", "false"));
			
			WELCOME_MESSAGE_ENABLED = Boolean.parseBoolean(L2PSModSettings.getProperty("ScreenWelcomeMessageEnable", "false"));
			WELCOME_MESSAGE_TEXT = L2PSModSettings.getProperty("ScreenWelcomeMessageText", "Welcome to L2J server!");
			WELCOME_MESSAGE_TIME = Integer.parseInt(L2PSModSettings.getProperty("ScreenWelcomeMessageTime", "10")) * 1000;
			
			ANTIFEED_ENABLE = Boolean.parseBoolean(L2PSModSettings.getProperty("AntiFeedEnable", "false"));
			ANTIFEED_DUALBOX = Boolean.parseBoolean(L2PSModSettings.getProperty("AntiFeedDualbox", "true"));
			ANTIFEED_DISCONNECTED_AS_DUALBOX = Boolean.parseBoolean(L2PSModSettings.getProperty("AntiFeedDisconnectedAsDualbox", "true"));
			ANTIFEED_INTERVAL = 1000 * Integer.parseInt(L2PSModSettings.getProperty("AntiFeedInterval", "120"));
			ANNOUNCE_PK_PVP = Boolean.parseBoolean(L2PSModSettings.getProperty("AnnouncePkPvP", "False"));
			ANNOUNCE_PK_PVP_NORMAL_MESSAGE = Boolean.parseBoolean(L2PSModSettings.getProperty("AnnouncePkPvPNormalMessage", "True"));
			ANNOUNCE_PK_MSG = L2PSModSettings.getProperty("AnnouncePkMsg", "$killer has slaughtered $target");
			ANNOUNCE_PVP_MSG = L2PSModSettings.getProperty("AnnouncePvpMsg", "$killer has defeated $target");
			
			CHAT_ADMIN = Boolean.parseBoolean(L2PSModSettings.getProperty("ChatAdmin", "false"));
			
			MULTILANG_DEFAULT = L2PSModSettings.getProperty("MultiLangDefault", "en");
			MULTILANG_ENABLE = Boolean.parseBoolean(L2PSModSettings.getProperty("MultiLangEnable", "false"));
			String[] allowed = L2PSModSettings.getProperty("MultiLangAllowed", MULTILANG_DEFAULT).split(";");
			MULTILANG_ALLOWED = new ArrayList<>(allowed.length);
			for (String lang : allowed)
			{
				MULTILANG_ALLOWED.add(lang);
			}
			
			if (!MULTILANG_ALLOWED.contains(MULTILANG_DEFAULT))
			{
				_log.warning("MultiLang[Config.load()]: default language: " + MULTILANG_DEFAULT + " is not in allowed list !");
			}
			
			HELLBOUND_STATUS = Boolean.parseBoolean(L2PSModSettings.getProperty("HellboundStatus", "False"));
			MULTILANG_VOICED_ALLOW = Boolean.parseBoolean(L2PSModSettings.getProperty("MultiLangVoiceCommand", "True"));
			MULTILANG_SM_ENABLE = Boolean.parseBoolean(L2PSModSettings.getProperty("MultiLangSystemMessageEnable", "False"));
			allowed = L2PSModSettings.getProperty("MultiLangSystemMessageAllowed", "").split(";");
			MULTILANG_SM_ALLOWED = new ArrayList<>(allowed.length);
			for (String lang : allowed)
			{
				if (!lang.isEmpty())
				{
					MULTILANG_SM_ALLOWED.add(lang);
				}
			}
			MULTILANG_NS_ENABLE = Boolean.parseBoolean(L2PSModSettings.getProperty("MultiLangNpcStringEnable", "False"));
			allowed = L2PSModSettings.getProperty("MultiLangNpcStringAllowed", "").split(";");
			MULTILANG_NS_ALLOWED = new ArrayList<>(allowed.length);
			for (String lang : allowed)
			{
				if (!lang.isEmpty())
				{
					MULTILANG_NS_ALLOWED.add(lang);
				}
			}
			
			L2WALKER_PROTECTION = Boolean.parseBoolean(L2PSModSettings.getProperty("L2WalkerProtection", "False"));
			
			DUALBOX_CHECK_MAX_PLAYERS_PER_IP = Integer.parseInt(L2PSModSettings.getProperty("DualboxCheckMaxPlayersPerIP", "0"));
			DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP = Integer.parseInt(L2PSModSettings.getProperty("DualboxCheckMaxOlympiadParticipantsPerIP", "0"));
			DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP = Integer.parseInt(L2PSModSettings.getProperty("DualboxCheckMaxL2EventParticipantsPerIP", "0"));
			String[] dualboxCheckWhiteList = L2PSModSettings.getProperty("DualboxCheckWhitelist", "127.0.0.1,0").split(";");
			DUALBOX_CHECK_WHITELIST = new HashMap<>(dualboxCheckWhiteList.length);
			for (String entry : dualboxCheckWhiteList)
			{
				String[] entrySplit = entry.split(",");
				if (entrySplit.length != 2)
				{
					_log.warning(StringUtil.concat("DualboxCheck[Config.load()]: invalid config property -> DualboxCheckWhitelist \"", entry, "\""));
				}
				else
				{
					try
					{
						int num = Integer.parseInt(entrySplit[1]);
						num = num == 0 ? -1 : num;
						DUALBOX_CHECK_WHITELIST.put(InetAddress.getByName(entrySplit[0]).hashCode(), num);
					}
					catch (UnknownHostException e)
					{
						_log.warning(StringUtil.concat("DualboxCheck[Config.load()]: invalid address -> DualboxCheckWhitelist \"", entrySplit[0], "\""));
					}
					catch (NumberFormatException e)
					{
						_log.warning(StringUtil.concat("DualboxCheck[Config.load()]: invalid number -> DualboxCheckWhitelist \"", entrySplit[1], "\""));
					}
				}
			}
			ALLOW_CHANGE_PASSWORD = Boolean.parseBoolean(L2PSModSettings.getProperty("AllowChangePassword", "False"));
			
			// Load PvP L2Properties file (if exists)
			final File pvp = new File(PVP_CONFIG);
			L2Properties PVPSettings = new L2Properties();
			try (InputStream is = new FileInputStream(pvp))
			{
				PVPSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading PVP settings!", e);
			}
			
			KARMA_MIN_KARMA = Integer.parseInt(PVPSettings.getProperty("MinKarma", "240"));
			KARMA_MAX_KARMA = Integer.parseInt(PVPSettings.getProperty("MaxKarma", "10000"));
			KARMA_XP_DIVIDER = Integer.parseInt(PVPSettings.getProperty("XPDivider", "260"));
			KARMA_LOST_BASE = Integer.parseInt(PVPSettings.getProperty("BaseKarmaLost", "0"));
			KARMA_DROP_GM = Boolean.parseBoolean(PVPSettings.getProperty("CanGMDropEquipment", "false"));
			KARMA_AWARD_PK_KILL = Boolean.parseBoolean(PVPSettings.getProperty("AwardPKKillPVPPoint", "true"));
			KARMA_PK_LIMIT = Integer.parseInt(PVPSettings.getProperty("MinimumPKRequiredToDrop", "5"));
			KARMA_NONDROPPABLE_PET_ITEMS = PVPSettings.getProperty("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650,9882");
			KARMA_NONDROPPABLE_ITEMS = PVPSettings.getProperty("ListOfNonDroppableItems", "57,1147,425,1146,461,10,2368,7,6,2370,2369,6842,6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,7694,8181,5575,7694,9388,9389,9390");
			
			String[] karma = KARMA_NONDROPPABLE_PET_ITEMS.split(",");
			KARMA_LIST_NONDROPPABLE_PET_ITEMS = new int[karma.length];
			
			for (int i = 0; i < karma.length; i++)
			{
				KARMA_LIST_NONDROPPABLE_PET_ITEMS[i] = Integer.parseInt(karma[i]);
			}
			
			karma = KARMA_NONDROPPABLE_ITEMS.split(",");
			KARMA_LIST_NONDROPPABLE_ITEMS = new int[karma.length];
			
			for (int i = 0; i < karma.length; i++)
			{
				KARMA_LIST_NONDROPPABLE_ITEMS[i] = Integer.parseInt(karma[i]);
			}
			
			// sorting so binarySearch can be used later
			Arrays.sort(KARMA_LIST_NONDROPPABLE_PET_ITEMS);
			Arrays.sort(KARMA_LIST_NONDROPPABLE_ITEMS);
			
			PVP_NORMAL_TIME = Integer.parseInt(PVPSettings.getProperty("PvPVsNormalTime", "120000"));
			PVP_PVP_TIME = Integer.parseInt(PVPSettings.getProperty("PvPVsPvPTime", "60000"));
			
			// Load Olympiad L2Properties file (if exists)
			final File oly = new File(OLYMPIAD_CONFIG);
			L2Properties Olympiad = new L2Properties();
			try (InputStream is = new FileInputStream(oly))
			{
				Olympiad.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Olympiad settings!", e);
			}
			OLY_VISUAL_RESTRICTION = Boolean.parseBoolean(Olympiad.getProperty("EnableOlyRestriction", "false"));
			ALT_OLY_START_TIME = Integer.parseInt(Olympiad.getProperty("AltOlyStartTime", "18"));
			ALT_OLY_MIN = Integer.parseInt(Olympiad.getProperty("AltOlyMin", "00"));
			ALT_OLY_CPERIOD = Long.parseLong(Olympiad.getProperty("AltOlyCPeriod", "21600000"));
			ALT_OLY_BATTLE = Long.parseLong(Olympiad.getProperty("AltOlyBattle", "300000"));
			ALT_OLY_WPERIOD = Long.parseLong(Olympiad.getProperty("AltOlyWPeriod", "604800000"));
			ALT_OLY_VPERIOD = Long.parseLong(Olympiad.getProperty("AltOlyVPeriod", "86400000"));
			ENABLE_CUSTOM_PERIOD = Boolean.parseBoolean(Olympiad.getProperty("EnableCustomPeriod", "false"));
			propertySplit = Olympiad.getProperty("AltOlyEndDate", "1").split(",");
			ALT_OLY_END_DATE = new int[propertySplit.length];
			for (int i = 0; i < propertySplit.length; i++)
			{
				ALT_OLY_END_DATE[i] = Integer.parseInt(propertySplit[i]);
			}
			propertySplit = Olympiad.getProperty("AltOlyEndHour", "12:00:00").split(":");
			for (int i = 0; i < 3; i++)
			{
				ALT_OLY_END_HOUR[i] = Integer.parseInt(propertySplit[i]);
			}
			ALT_OLY_START_POINTS = Integer.parseInt(Olympiad.getProperty("AltOlyStartPoints", "10"));
			ALT_OLY_WEEKLY_POINTS = Integer.parseInt(Olympiad.getProperty("AltOlyWeeklyPoints", "10"));
			ALT_OLY_CLASSED = Integer.parseInt(Olympiad.getProperty("AltOlyClassedParticipants", "11"));
			ALT_OLY_NONCLASSED = Integer.parseInt(Olympiad.getProperty("AltOlyNonClassedParticipants", "11"));
			ALT_OLY_TEAMS = Integer.parseInt(Olympiad.getProperty("AltOlyTeamsParticipants", "6"));
			ALT_OLY_REG_DISPLAY = Integer.parseInt(Olympiad.getProperty("AltOlyRegistrationDisplayNumber", "100"));
			ALT_OLY_CLASSED_REWARD = parseItemsList(Olympiad.getProperty("AltOlyClassedReward", "13722,50"));
			ALT_OLY_NONCLASSED_REWARD = parseItemsList(Olympiad.getProperty("AltOlyNonClassedReward", "13722,40"));
			ALT_OLY_TEAM_REWARD = parseItemsList(Olympiad.getProperty("AltOlyTeamReward", "13722,85"));
			ALT_OLY_COMP_RITEM = Integer.parseInt(Olympiad.getProperty("AltOlyCompRewItem", "13722"));
			ALT_OLY_MIN_MATCHES = Integer.parseInt(Olympiad.getProperty("AltOlyMinMatchesForPoints", "15"));
			ALT_OLY_GP_PER_POINT = Integer.parseInt(Olympiad.getProperty("AltOlyGPPerPoint", "1000"));
			ALT_OLY_HERO_POINTS = Integer.parseInt(Olympiad.getProperty("AltOlyHeroPoints", "200"));
			ALT_OLY_RANK1_POINTS = Integer.parseInt(Olympiad.getProperty("AltOlyRank1Points", "100"));
			ALT_OLY_RANK2_POINTS = Integer.parseInt(Olympiad.getProperty("AltOlyRank2Points", "75"));
			ALT_OLY_RANK3_POINTS = Integer.parseInt(Olympiad.getProperty("AltOlyRank3Points", "55"));
			ALT_OLY_RANK4_POINTS = Integer.parseInt(Olympiad.getProperty("AltOlyRank4Points", "40"));
			ALT_OLY_RANK5_POINTS = Integer.parseInt(Olympiad.getProperty("AltOlyRank5Points", "30"));
			ALT_OLY_MAX_POINTS = Integer.parseInt(Olympiad.getProperty("AltOlyMaxPoints", "10"));
			ALT_OLY_DIVIDER_CLASSED = Integer.parseInt(Olympiad.getProperty("AltOlyDividerClassed", "5"));
			ALT_OLY_DIVIDER_NON_CLASSED = Integer.parseInt(Olympiad.getProperty("AltOlyDividerNonClassed", "5"));
			ALT_OLY_MAX_WEEKLY_MATCHES = Integer.parseInt(Olympiad.getProperty("AltOlyMaxWeeklyMatches", "70"));
			ALT_OLY_MAX_WEEKLY_MATCHES_NON_CLASSED = Integer.parseInt(Olympiad.getProperty("AltOlyMaxWeeklyMatchesNonClassed", "60"));
			ALT_OLY_MAX_WEEKLY_MATCHES_CLASSED = Integer.parseInt(Olympiad.getProperty("AltOlyMaxWeeklyMatchesClassed", "30"));
			ALT_OLY_MAX_WEEKLY_MATCHES_TEAM = Integer.parseInt(Olympiad.getProperty("AltOlyMaxWeeklyMatchesTeam", "10"));
			ALT_OLY_LOG_FIGHTS = Boolean.parseBoolean(Olympiad.getProperty("AltOlyLogFights", "false"));
			ALT_OLY_SHOW_MONTHLY_WINNERS = Boolean.parseBoolean(Olympiad.getProperty("AltOlyShowMonthlyWinners", "true"));
			ALT_OLY_ANNOUNCE_GAMES = Boolean.parseBoolean(Olympiad.getProperty("AltOlyAnnounceGames", "true"));
			String[] olyRestrictedItems = Olympiad.getProperty("AltOlyRestrictedItems", "6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,9388,9389,9390,17049,17050,17051,17052,17053,17054,17055,17056,17057,17058,17059,17060,17061,20759,20775,20776,20777,20778,14774").split(",");
			LIST_OLY_RESTRICTED_ITEMS = new ArrayList<>(olyRestrictedItems.length);
			for (String id : olyRestrictedItems)
			{
				LIST_OLY_RESTRICTED_ITEMS.add(Integer.parseInt(id));
			}
			ALT_OLY_ENCHANT_LIMIT = Integer.parseInt(Olympiad.getProperty("AltOlyEnchantLimit", "-1"));
			ALT_OLY_WAIT_TIME = Integer.parseInt(Olympiad.getProperty("AltOlyWaitTime", "120"));
			
			final File hex = new File(HEXID_FILE);
			try (InputStream is = new FileInputStream(hex))
			{
				L2Properties Settings = new L2Properties();
				Settings.load(is);
				SERVER_ID = Integer.parseInt(Settings.getProperty("ServerID"));
				HEX_ID = new BigInteger(Settings.getProperty("HexID"), 16).toByteArray();
			}
			catch (Exception e)
			{
				_log.warning("Could not load HexID file (" + HEXID_FILE + "). Hopefully login will give us one.");
			}
			
			// Load Event_Engine L2Properties file (if exists)
			final File eng = new File(EVENT_ENGINE_CONFIG);
			try (InputStream is = new FileInputStream(eng))
			{
				L2Properties event_engine = new L2Properties();
				event_engine.load(is);
				ENABLE_EVENT_ENGINE = Boolean.parseBoolean(event_engine.getProperty("EnableEventEngine", "False"));
			}
			catch (Exception e)
			{
				_log.warning("Config: " + e.getMessage());
				throw new Error("Failed to Load " + EVENT_ENGINE_CONFIG + " File.");
			}
			
			final File chat_filter = new File(CHAT_FILTER_FILE);
			try (FileReader fr = new FileReader(chat_filter);
				BufferedReader br = new BufferedReader(fr);
				LineNumberReader lnr = new LineNumberReader(br))
			{
				FILTER_LIST = new ArrayList<>();
				
				String line = null;
				while ((line = lnr.readLine()) != null)
				{
					if (line.trim().isEmpty() || (line.charAt(0) == '#'))
					{
						continue;
					}
					
					FILTER_LIST.add(line.trim());
				}
				_log.info("Loaded " + FILTER_LIST.size() + " Filter Words.");
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Error while loading chat filter words!", e);
			}
			
			// Security
			L2Properties SecuritySettings = new L2Properties();
			final File security = new File(SECURITY_CONFIG);
			try (InputStream is = new FileInputStream(security))
			{
				SecuritySettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Security settings!", e);
			}
			
			// Second Auth Settings
			SECOND_AUTH_ENABLED = Boolean.parseBoolean(SecuritySettings.getProperty("SecondAuthEnabled", "false"));
			SECOND_AUTH_MAX_ATTEMPTS = Integer.parseInt(SecuritySettings.getProperty("SecondAuthMaxAttempts", "5"));
			SECOND_AUTH_BAN_TIME = Integer.parseInt(SecuritySettings.getProperty("SecondAuthBanTime", "480"));
			SECOND_AUTH_REC_LINK = SecuritySettings.getProperty("SecondAuthRecoveryLink", "5");
			
			L2Properties ClanHallSiege = new L2Properties();
			final File ch_siege = new File(CH_SIEGE_CONFIG);
			try (InputStream is = new FileInputStream(ch_siege))
			{
				ClanHallSiege.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Clan Hall Siege settings!", e);
			}
			
			CHS_MAX_ATTACKERS = Integer.parseInt(ClanHallSiege.getProperty("MaxAttackers", "500"));
			CHS_CLAN_MINLEVEL = Integer.parseInt(ClanHallSiege.getProperty("MinClanLevel", "4"));
			CHS_MAX_FLAGS_PER_CLAN = Integer.parseInt(ClanHallSiege.getProperty("MaxFlagsPerClan", "1"));
			CHS_ENABLE_FAME = Boolean.parseBoolean(ClanHallSiege.getProperty("EnableFame", "false"));
			CHS_FAME_AMOUNT = Integer.parseInt(ClanHallSiege.getProperty("FameAmount", "0"));
			CHS_FAME_FREQUENCY = Integer.parseInt(ClanHallSiege.getProperty("FameFrequency", "0"));
		}
		else if (Server.serverMode == Server.MODE_LOGINSERVER)
		{
			L2Properties ServerSettings = new L2Properties();
			final File login = new File(LOGIN_CONFIG);
			try (InputStream is = new FileInputStream(login))
			{
				ServerSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Login Server settings!", e);
			}
			
			GAME_SERVER_LOGIN_HOST = ServerSettings.getProperty("LoginHostname", "127.0.0.1");
			GAME_SERVER_LOGIN_PORT = Integer.parseInt(ServerSettings.getProperty("LoginPort", "9013"));
			
			LOGIN_BIND_ADDRESS = ServerSettings.getProperty("LoginserverHostname", "*");
			PORT_LOGIN = Integer.parseInt(ServerSettings.getProperty("LoginserverPort", "2106"));
			
			try
			{
				DATAPACK_ROOT = new File(ServerSettings.getProperty("DatapackRoot", ".").replaceAll("\\\\", "/")).getCanonicalFile();
			}
			catch (IOException e)
			{
				_log.log(Level.WARNING, "Error setting datapack root!", e);
				DATAPACK_ROOT = new File(".");
			}
			
			DEBUG = Boolean.parseBoolean(ServerSettings.getProperty("Debug", "false"));
			
			ACCEPT_NEW_GAMESERVER = Boolean.parseBoolean(ServerSettings.getProperty("AcceptNewGameServer", "True"));
			
			LOGIN_TRY_BEFORE_BAN = Integer.parseInt(ServerSettings.getProperty("LoginTryBeforeBan", "5"));
			LOGIN_BLOCK_AFTER_BAN = Integer.parseInt(ServerSettings.getProperty("LoginBlockAfterBan", "900"));
			
			LOG_LOGIN_CONTROLLER = Boolean.parseBoolean(ServerSettings.getProperty("LogLoginController", "true"));
			
			LOGIN_SERVER_SCHEDULE_RESTART = Boolean.parseBoolean(ServerSettings.getProperty("LoginRestartSchedule", "False"));
			LOGIN_SERVER_SCHEDULE_RESTART_TIME = Long.parseLong(ServerSettings.getProperty("LoginRestartTime", "24"));
			
			DATABASE_DRIVER = ServerSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
			DATABASE_URL = ServerSettings.getProperty("URL", "jdbc:mysql://localhost/l2jls");
			DATABASE_LOGIN = ServerSettings.getProperty("Login", "root");
			DATABASE_PASSWORD = ServerSettings.getProperty("Password", "");
			DATABASE_MAX_CONNECTIONS = Integer.parseInt(ServerSettings.getProperty("MaximumDbConnections", "10"));
			DATABASE_MAX_IDLE_TIME = Integer.parseInt(ServerSettings.getProperty("MaximumDbIdleTime", "0"));
			
			SHOW_LICENCE = Boolean.parseBoolean(ServerSettings.getProperty("ShowLicence", "true"));
			
			AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(ServerSettings.getProperty("AutoCreateAccounts", "True"));
			
			FLOOD_PROTECTION = Boolean.parseBoolean(ServerSettings.getProperty("EnableFloodProtection", "True"));
			FAST_CONNECTION_LIMIT = Integer.parseInt(ServerSettings.getProperty("FastConnectionLimit", "15"));
			NORMAL_CONNECTION_TIME = Integer.parseInt(ServerSettings.getProperty("NormalConnectionTime", "700"));
			FAST_CONNECTION_TIME = Integer.parseInt(ServerSettings.getProperty("FastConnectionTime", "350"));
			MAX_CONNECTION_PER_IP = Integer.parseInt(ServerSettings.getProperty("MaxConnectionPerIP", "50"));
			
			// MMO
			final File mmo = new File(MMO_CONFIG);
			L2Properties mmoSettings = new L2Properties();
			try (InputStream is = new FileInputStream(mmo))
			{
				mmoSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading MMO settings!", e);
			}
			
			MMO_SELECTOR_SLEEP_TIME = Integer.parseInt(mmoSettings.getProperty("SleepTime", "20"));
			MMO_MAX_SEND_PER_PASS = Integer.parseInt(mmoSettings.getProperty("MaxSendPerPass", "12"));
			MMO_MAX_READ_PER_PASS = Integer.parseInt(mmoSettings.getProperty("MaxReadPerPass", "12"));
			MMO_HELPER_BUFFER_COUNT = Integer.parseInt(mmoSettings.getProperty("HelperBufferCount", "20"));
			MMO_TCP_NODELAY = Boolean.parseBoolean(mmoSettings.getProperty("TcpNoDelay", "False"));
			
			// Load Telnet L2Properties file (if exists)
			L2Properties telnetSettings = new L2Properties();
			final File telnet = new File(TELNET_CONFIG);
			try (InputStream is = new FileInputStream(telnet))
			{
				telnetSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Telnet settings!", e);
			}
			
			IS_TELNET_ENABLED = Boolean.parseBoolean(telnetSettings.getProperty("EnableTelnet", "false"));
			
			// Email
			L2Properties emailSettings = new L2Properties();
			final File email = new File(EMAIL_CONFIG);
			try (InputStream is = new FileInputStream(email))
			{
				emailSettings.load(is);
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Error while loading Email settings!", e);
			}
			
			EMAIL_SERVERINFO_NAME = emailSettings.getProperty("ServerInfoName", "Unconfigured L2J Server");
			EMAIL_SERVERINFO_ADDRESS = emailSettings.getProperty("ServerInfoAddress", "info@myl2jserver.com");
			
			EMAIL_SYS_ENABLED = Boolean.parseBoolean(emailSettings.getProperty("EmailSystemEnabled", "false"));
			EMAIL_SYS_HOST = emailSettings.getProperty("SmtpServerHost", "smtp.gmail.com");
			EMAIL_SYS_PORT = Integer.parseInt(emailSettings.getProperty("SmtpServerPort", "465"));
			EMAIL_SYS_SMTP_AUTH = Boolean.parseBoolean(emailSettings.getProperty("SmtpAuthRequired", "true"));
			EMAIL_SYS_FACTORY = emailSettings.getProperty("SmtpFactory", "javax.net.ssl.SSLSocketFactory");
			EMAIL_SYS_FACTORY_CALLBACK = Boolean.parseBoolean(emailSettings.getProperty("SmtpFactoryCallback", "false"));
			EMAIL_SYS_USERNAME = emailSettings.getProperty("SmtpUsername", "user@gmail.com");
			EMAIL_SYS_PASSWORD = emailSettings.getProperty("SmtpPassword", "password");
			EMAIL_SYS_ADDRESS = emailSettings.getProperty("EmailSystemAddress", "noreply@myl2jserver.com");
			EMAIL_SYS_SELECTQUERY = emailSettings.getProperty("EmailDBSelectQuery", "SELECT value FROM account_data WHERE account_name=? AND var='email_addr'");
			EMAIL_SYS_DBFIELD = emailSettings.getProperty("EmailDBField", "value");
		}
		else
		{
			_log.severe("Could not Load Config: server mode was not set!");
		}
	}
	
	/**
	 * Set a new value to a config parameter.
	 * @param pName the name of the parameter whose value to change
	 * @param pValue the new value of the parameter
	 * @return {@code true} if the value of the parameter was changed, {@code false} otherwise
	 */
	public static boolean setParameterValue(String pName, String pValue)
	{
		switch (pName.trim().toLowerCase())
		{
			case "AnnouncePlayers":
				ALLOW_ANNOUNCE_ONLINE_PLAYERS = Boolean.parseBoolean(pValue);
				break;
			case "FakePlayers":
				FAKE_PLAYERS = Integer.parseInt(pValue);
				break;
			case "OnlineInterval":
				ANNOUNCE_ONLINE_PLAYERS_DELAY = Integer.parseInt(pValue);
				break;
			case "PremiumRateXp":
				PREMIUM_RATE_XP = Float.parseFloat(pValue);
				break;
			case "PremiumRateSp":
				PREMIUM_RATE_SP = Float.parseFloat(pValue);
				break;
			case "PremiumRateDropSpoil":
				PREMIUM_RATE_DROP_SPOIL = Float.parseFloat(pValue);
				break;
			case "PremiumRateDropItems":
				PREMIUM_RATE_DROP_ITEMS = Float.parseFloat(pValue);
				break;
			case "PremiumRateDropAdena":
				PREMIUM_RATE_DROP_ITEMS_ID.put(PcInventory.ADENA_ID, Float.parseFloat(pValue));
				break;
			case "PremiumRateRaidDropItems":
				PREMIUM_RATE_DROP_ITEMS_BY_RAID = Float.parseFloat(pValue);
				break;
			case "RateXp":
				RATE_XP = Float.parseFloat(pValue);
				break;
			
			case "ratexp":
				RATE_XP = Float.parseFloat(pValue);
				break;
			case "ratesp":
				RATE_SP = Float.parseFloat(pValue);
				break;
			case "ratepartyxp":
				RATE_PARTY_XP = Float.parseFloat(pValue);
				break;
			case "rateconsumablecost":
				RATE_CONSUMABLE_COST = Float.parseFloat(pValue);
				break;
			case "rateextractable":
				RATE_EXTRACTABLE = Float.parseFloat(pValue);
				break;
			case "ratedropitems":
				RATE_DROP_ITEMS = Float.parseFloat(pValue);
				break;
			case "ratedropadena":
				RATE_DROP_ITEMS_ID.put(PcInventory.ADENA_ID, Float.parseFloat(pValue));
				break;
			case "rateraiddropitems":
				RATE_DROP_ITEMS_BY_RAID = Float.parseFloat(pValue);
				break;
			case "ratedropspoil":
				RATE_DROP_SPOIL = Float.parseFloat(pValue);
				break;
			case "ratedropmanor":
				RATE_DROP_MANOR = Integer.parseInt(pValue);
				break;
			case "ratequestdrop":
				RATE_QUEST_DROP = Float.parseFloat(pValue);
				break;
			case "ratequestreward":
				RATE_QUEST_REWARD = Float.parseFloat(pValue);
				break;
			case "ratequestrewardxp":
				RATE_QUEST_REWARD_XP = Float.parseFloat(pValue);
				break;
			case "ratequestrewardsp":
				RATE_QUEST_REWARD_SP = Float.parseFloat(pValue);
				break;
			case "ratequestrewardadena":
				RATE_QUEST_REWARD_ADENA = Float.parseFloat(pValue);
				break;
			case "usequestrewardmultipliers":
				RATE_QUEST_REWARD_USE_MULTIPLIERS = Boolean.parseBoolean(pValue);
				break;
			case "ratequestrewardpotion":
				RATE_QUEST_REWARD_POTION = Float.parseFloat(pValue);
				break;
			case "ratequestrewardscroll":
				RATE_QUEST_REWARD_SCROLL = Float.parseFloat(pValue);
				break;
			case "ratequestrewardrecipe":
				RATE_QUEST_REWARD_RECIPE = Float.parseFloat(pValue);
				break;
			case "ratequestrewardmaterial":
				RATE_QUEST_REWARD_MATERIAL = Float.parseFloat(pValue);
				break;
			case "ratehellboundtrustincrease":
				RATE_HB_TRUST_INCREASE = Float.parseFloat(pValue);
				break;
			case "ratehellboundtrustdecrease":
				RATE_HB_TRUST_DECREASE = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel1":
				RATE_VITALITY_LEVEL_1 = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel2":
				RATE_VITALITY_LEVEL_2 = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel3":
				RATE_VITALITY_LEVEL_3 = Float.parseFloat(pValue);
				break;
			case "ratevitalitylevel4":
				RATE_VITALITY_LEVEL_4 = Float.parseFloat(pValue);
				break;
			case "raterecoverypeacezone":
				RATE_RECOVERY_VITALITY_PEACE_ZONE = Float.parseFloat(pValue);
				break;
			case "ratevitalitylost":
				RATE_VITALITY_LOST = Float.parseFloat(pValue);
				break;
			case "ratevitalitygain":
				RATE_VITALITY_GAIN = Float.parseFloat(pValue);
				break;
			case "raterecoveryonreconnect":
				RATE_RECOVERY_ON_RECONNECT = Float.parseFloat(pValue);
				break;
			case "ratekarmaexplost":
				RATE_KARMA_EXP_LOST = Float.parseFloat(pValue);
				break;
			case "ratesiegeguardsprice":
				RATE_SIEGE_GUARDS_PRICE = Float.parseFloat(pValue);
				break;
			case "ratecommonherbs":
				RATE_DROP_COMMON_HERBS = Float.parseFloat(pValue);
				break;
			case "ratehpherbs":
				RATE_DROP_HP_HERBS = Float.parseFloat(pValue);
				break;
			case "ratempherbs":
				RATE_DROP_MP_HERBS = Float.parseFloat(pValue);
				break;
			case "ratespecialherbs":
				RATE_DROP_SPECIAL_HERBS = Float.parseFloat(pValue);
				break;
			case "ratevitalityherbs":
				RATE_DROP_VITALITY_HERBS = Float.parseFloat(pValue);
				break;
			case "playerdroplimit":
				PLAYER_DROP_LIMIT = Integer.parseInt(pValue);
				break;
			case "playerratedrop":
				PLAYER_RATE_DROP = Integer.parseInt(pValue);
				break;
			case "playerratedropitem":
				PLAYER_RATE_DROP_ITEM = Integer.parseInt(pValue);
				break;
			case "playerratedropequip":
				PLAYER_RATE_DROP_EQUIP = Integer.parseInt(pValue);
				break;
			case "playerratedropequipweapon":
				PLAYER_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
				break;
			case "petxprate":
				PET_XP_RATE = Float.parseFloat(pValue);
				break;
			case "petfoodrate":
				PET_FOOD_RATE = Integer.parseInt(pValue);
				break;
			case "sineaterxprate":
				SINEATER_XP_RATE = Float.parseFloat(pValue);
				break;
			case "karmadroplimit":
				KARMA_DROP_LIMIT = Integer.parseInt(pValue);
				break;
			case "karmaratedrop":
				KARMA_RATE_DROP = Integer.parseInt(pValue);
				break;
			case "karmaratedropitem":
				KARMA_RATE_DROP_ITEM = Integer.parseInt(pValue);
				break;
			case "karmaratedropequip":
				KARMA_RATE_DROP_EQUIP = Integer.parseInt(pValue);
				break;
			case "karmaratedropequipweapon":
				KARMA_RATE_DROP_EQUIP_WEAPON = Integer.parseInt(pValue);
				break;
			case "autodestroydroppeditemafter":
				AUTODESTROY_ITEM_AFTER = Integer.parseInt(pValue);
				break;
			case "destroyplayerdroppeditem":
				DESTROY_DROPPED_PLAYER_ITEM = Boolean.parseBoolean(pValue);
				break;
			case "destroyequipableitem":
				DESTROY_EQUIPABLE_PLAYER_ITEM = Boolean.parseBoolean(pValue);
				break;
			case "savedroppeditem":
				SAVE_DROPPED_ITEM = Boolean.parseBoolean(pValue);
				break;
			case "emptydroppeditemtableafterload":
				EMPTY_DROPPED_ITEM_TABLE_AFTER_LOAD = Boolean.parseBoolean(pValue);
				break;
			case "savedroppediteminterval":
				SAVE_DROPPED_ITEM_INTERVAL = Integer.parseInt(pValue);
				break;
			case "cleardroppeditemtable":
				CLEAR_DROPPED_ITEM_TABLE = Boolean.parseBoolean(pValue);
				break;
			case "precisedropcalculation":
				PRECISE_DROP_CALCULATION = Boolean.parseBoolean(pValue);
				break;
			case "multipleitemdrop":
				MULTIPLE_ITEM_DROP = Boolean.parseBoolean(pValue);
				break;
			case "lowweight":
				LOW_WEIGHT = Float.parseFloat(pValue);
				break;
			case "mediumweight":
				MEDIUM_WEIGHT = Float.parseFloat(pValue);
				break;
			case "highweight":
				HIGH_WEIGHT = Float.parseFloat(pValue);
				break;
			case "advanceddiagonalstrategy":
				ADVANCED_DIAGONAL_STRATEGY = Boolean.parseBoolean(pValue);
				break;
			case "diagonalweight":
				DIAGONAL_WEIGHT = Float.parseFloat(pValue);
				break;
			case "maxpostfilterpasses":
				MAX_POSTFILTER_PASSES = Integer.parseInt(pValue);
				break;
			case "coordsynchronize":
				COORD_SYNCHRONIZE = Integer.parseInt(pValue);
				break;
			case "deletecharafterdays":
				DELETE_DAYS = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuesize":
				CLIENT_PACKET_QUEUE_SIZE = Integer.parseInt(pValue);
				if (CLIENT_PACKET_QUEUE_SIZE == 0)
				{
					CLIENT_PACKET_QUEUE_SIZE = MMO_MAX_READ_PER_PASS + 1;
				}
				break;
			case "clientpacketqueuemaxburstsize":
				CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = Integer.parseInt(pValue);
				if (CLIENT_PACKET_QUEUE_MAX_BURST_SIZE == 0)
				{
					CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMO_MAX_READ_PER_PASS;
				}
				break;
			case "clientpacketqueuemaxpacketspersecond":
				CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemeasureinterval":
				CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxaveragepacketspersecond":
				CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxfloodspermin":
				CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxoverflowspermin":
				CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxunderflowspermin":
				CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = Integer.parseInt(pValue);
				break;
			case "clientpacketqueuemaxunknownpermin":
				CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = Integer.parseInt(pValue);
				break;
			case "allowdiscarditem":
				ALLOW_DISCARDITEM = Boolean.parseBoolean(pValue);
				break;
			case "allowrefund":
				ALLOW_REFUND = Boolean.parseBoolean(pValue);
				break;
			case "allowwarehouse":
				ALLOW_WAREHOUSE = Boolean.parseBoolean(pValue);
				break;
			case "allowwear":
				ALLOW_WEAR = Boolean.parseBoolean(pValue);
				break;
			case "weardelay":
				WEAR_DELAY = Integer.parseInt(pValue);
				break;
			case "wearprice":
				WEAR_PRICE = Integer.parseInt(pValue);
				break;
			case "allowwater":
				ALLOW_WATER = Boolean.parseBoolean(pValue);
				break;
			case "allowrentpet":
				ALLOW_RENTPET = Boolean.parseBoolean(pValue);
				break;
			case "boatbroadcastradius":
				BOAT_BROADCAST_RADIUS = Integer.parseInt(pValue);
				break;
			case "allowcursedweapons":
				ALLOW_CURSED_WEAPONS = Boolean.parseBoolean(pValue);
				break;
			case "allowmanor":
				ALLOW_MANOR = Boolean.parseBoolean(pValue);
				break;
			case "allowpetwalkers":
				ALLOW_PET_WALKERS = Boolean.parseBoolean(pValue);
				break;
			case "bypassvalidation":
				BYPASS_VALIDATION = Boolean.parseBoolean(pValue);
				break;
			case "communitytype":
				COMMUNITY_TYPE = Integer.parseInt(pValue);
				break;
			case "bbsshowplayerlist":
				BBS_SHOW_PLAYERLIST = Boolean.parseBoolean(pValue);
				break;
			case "bbsdefault":
				BBS_DEFAULT = pValue;
				break;
			case "showleveloncommunityboard":
				SHOW_LEVEL_COMMUNITYBOARD = Boolean.parseBoolean(pValue);
				break;
			case "showstatusoncommunityboard":
				SHOW_STATUS_COMMUNITYBOARD = Boolean.parseBoolean(pValue);
				break;
			case "namepagesizeoncommunityboard":
				NAME_PAGE_SIZE_COMMUNITYBOARD = Integer.parseInt(pValue);
				break;
			case "nameperrowoncommunityboard":
				NAME_PER_ROW_COMMUNITYBOARD = Integer.parseInt(pValue);
				break;
			case "showservernews":
				SERVER_NEWS = Boolean.parseBoolean(pValue);
				break;
			case "shownpclevel":
				SHOW_NPC_LVL = Boolean.parseBoolean(pValue);
				break;
			case "showcrestwithoutquest":
				SHOW_CREST_WITHOUT_QUEST = Boolean.parseBoolean(pValue);
				break;
			case "forceinventoryupdate":
				FORCE_INVENTORY_UPDATE = Boolean.parseBoolean(pValue);
				break;
			case "autodeleteinvalidquestdata":
				AUTODELETE_INVALID_QUEST_DATA = Boolean.parseBoolean(pValue);
				break;
			case "maximumonlineusers":
				MAXIMUM_ONLINE_USERS = Integer.parseInt(pValue);
				break;
			case "peacezonemode":
				PEACE_ZONE_MODE = Integer.parseInt(pValue);
				break;
			case "checkknownlist":
				CHECK_KNOWN = Boolean.parseBoolean(pValue);
				break;
			case "maxdriftrange":
				MAX_DRIFT_RANGE = Integer.parseInt(pValue);
				break;
			case "usedeepbluedroprules":
				DEEPBLUE_DROP_RULES = Boolean.parseBoolean(pValue);
				break;
			case "usedeepbluedroprulesraid":
				DEEPBLUE_DROP_RULES_RAID = Boolean.parseBoolean(pValue);
				break;
			case "guardattackaggromob":
				GUARD_ATTACK_AGGRO_MOB = Boolean.parseBoolean(pValue);
				break;
			case "cancellessereffect":
				EFFECT_CANCELING = Boolean.parseBoolean(pValue);
				break;
			case "maximumslotsfornodwarf":
				INVENTORY_MAXIMUM_NO_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumslotsfordwarf":
				INVENTORY_MAXIMUM_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumslotsforgmplayer":
				INVENTORY_MAXIMUM_GM = Integer.parseInt(pValue);
				break;
			case "maximumslotsforquestitems":
				INVENTORY_MAXIMUM_QUEST_ITEMS = Integer.parseInt(pValue);
				break;
			case "maximumwarehouseslotsfornodwarf":
				WAREHOUSE_SLOTS_NO_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumwarehouseslotsfordwarf":
				WAREHOUSE_SLOTS_DWARF = Integer.parseInt(pValue);
				break;
			case "maximumwarehouseslotsforclan":
				WAREHOUSE_SLOTS_CLAN = Integer.parseInt(pValue);
				break;
			case "enchantchanceelementstone":
				ENCHANT_CHANCE_ELEMENT_STONE = Double.parseDouble(pValue);
				break;
			case "enchantchanceelementcrystal":
				ENCHANT_CHANCE_ELEMENT_CRYSTAL = Double.parseDouble(pValue);
				break;
			case "enchantchanceelementjewel":
				ENCHANT_CHANCE_ELEMENT_JEWEL = Double.parseDouble(pValue);
				break;
			case "enchantchanceelementenergy":
				ENCHANT_CHANCE_ELEMENT_ENERGY = Double.parseDouble(pValue);
				break;
			case "enchantsafemax":
				ENCHANT_SAFE_MAX = Integer.parseInt(pValue);
				break;
			case "enchantsafemaxfull":
				ENCHANT_SAFE_MAX_FULL = Integer.parseInt(pValue);
				break;
			case "augmentationngskillchance":
				AUGMENTATION_NG_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationngglowchance":
				AUGMENTATION_NG_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationmidskillchance":
				AUGMENTATION_MID_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationmidglowchance":
				AUGMENTATION_MID_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationhighskillchance":
				AUGMENTATION_HIGH_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationhighglowchance":
				AUGMENTATION_HIGH_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationtopskillchance":
				AUGMENTATION_TOP_SKILL_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationtopglowchance":
				AUGMENTATION_TOP_GLOW_CHANCE = Integer.parseInt(pValue);
				break;
			case "augmentationbasestatchance":
				AUGMENTATION_BASESTAT_CHANCE = Integer.parseInt(pValue);
				break;
			case "hpregenmultiplier":
				HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "mpregenmultiplier":
				MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "cpregenmultiplier":
				CP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "raidhpregenmultiplier":
				RAID_HP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "raidmpregenmultiplier":
				RAID_MP_REGEN_MULTIPLIER = Double.parseDouble(pValue);
				break;
			case "raidpdefencemultiplier":
				RAID_PDEFENCE_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidmdefencemultiplier":
				RAID_MDEFENCE_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidpattackmultiplier":
				RAID_PATTACK_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidmattackmultiplier":
				RAID_MATTACK_MULTIPLIER = Double.parseDouble(pValue) / 100;
				break;
			case "raidminionrespawntime":
				RAID_MINION_RESPAWN_TIMER = Integer.parseInt(pValue);
				break;
			case "raidchaostime":
				RAID_CHAOS_TIME = Integer.parseInt(pValue);
				break;
			case "grandchaostime":
				GRAND_CHAOS_TIME = Integer.parseInt(pValue);
				break;
			case "minionchaostime":
				MINION_CHAOS_TIME = Integer.parseInt(pValue);
				break;
			case "startingadena":
				STARTING_ADENA = Long.parseLong(pValue);
				break;
			case "startinglevel":
				STARTING_LEVEL = Byte.parseByte(pValue);
				break;
			case "startingsp":
				STARTING_SP = Integer.parseInt(pValue);
				break;
			case "unstuckinterval":
				UNSTUCK_INTERVAL = Integer.parseInt(pValue);
				break;
			case "teleportwatchdogtimeout":
				TELEPORT_WATCHDOG_TIMEOUT = Integer.parseInt(pValue);
				break;
			case "playerspawnprotection":
				PLAYER_SPAWN_PROTECTION = Integer.parseInt(pValue);
				break;
			case "playerfakedeathupprotection":
				PLAYER_FAKEDEATH_UP_PROTECTION = Integer.parseInt(pValue);
				break;
			case "restoreplayerinstance":
				RESTORE_PLAYER_INSTANCE = Boolean.parseBoolean(pValue);
				break;
			case "allowsummontoinstance":
				ALLOW_SUMMON_TO_INSTANCE = Boolean.parseBoolean(pValue);
				break;
			case "partyxpcutoffmethod":
				PARTY_XP_CUTOFF_METHOD = pValue;
				break;
			case "partyxpcutoffpercent":
				PARTY_XP_CUTOFF_PERCENT = Double.parseDouble(pValue);
				break;
			case "partyxpcutofflevel":
				PARTY_XP_CUTOFF_LEVEL = Integer.parseInt(pValue);
				break;
			case "respawnrestorecp":
				RESPAWN_RESTORE_CP = Double.parseDouble(pValue) / 100;
				break;
			case "respawnrestorehp":
				RESPAWN_RESTORE_HP = Double.parseDouble(pValue) / 100;
				break;
			case "respawnrestoremp":
				RESPAWN_RESTORE_MP = Double.parseDouble(pValue) / 100;
				break;
			case "maxpvtstoresellslotsdwarf":
				MAX_PVTSTORESELL_SLOTS_DWARF = Integer.parseInt(pValue);
				break;
			case "maxpvtstoresellslotsother":
				MAX_PVTSTORESELL_SLOTS_OTHER = Integer.parseInt(pValue);
				break;
			case "maxpvtstorebuyslotsdwarf":
				MAX_PVTSTOREBUY_SLOTS_DWARF = Integer.parseInt(pValue);
				break;
			case "maxpvtstorebuyslotsother":
				MAX_PVTSTOREBUY_SLOTS_OTHER = Integer.parseInt(pValue);
				break;
			case "storeskillcooltime":
				STORE_SKILL_COOLTIME = Boolean.parseBoolean(pValue);
				break;
			case "subclassstoreskillcooltime":
				SUBCLASS_STORE_SKILL_COOLTIME = Boolean.parseBoolean(pValue);
				break;
			case "announcemammonspawn":
				ANNOUNCE_MAMMON_SPAWN = Boolean.parseBoolean(pValue);
				break;
			case "altgametiredness":
				ALT_GAME_TIREDNESS = Boolean.parseBoolean(pValue);
				break;
			case "enablefallingdamage":
				ENABLE_FALLING_DAMAGE = Boolean.parseBoolean(pValue);
				break;
			case "altgamecreation":
				ALT_GAME_CREATION = Boolean.parseBoolean(pValue);
				break;
			case "altgamecreationspeed":
				ALT_GAME_CREATION_SPEED = Double.parseDouble(pValue);
				break;
			case "altgamecreationxprate":
				ALT_GAME_CREATION_XP_RATE = Double.parseDouble(pValue);
				break;
			case "altgamecreationrarexpsprate":
				ALT_GAME_CREATION_RARE_XPSP_RATE = Double.parseDouble(pValue);
				break;
			case "altgamecreationsprate":
				ALT_GAME_CREATION_SP_RATE = Double.parseDouble(pValue);
				break;
			case "altweightlimit":
				ALT_WEIGHT_LIMIT = Double.parseDouble(pValue);
				break;
			case "altblacksmithuserecipes":
				ALT_BLACKSMITH_USE_RECIPES = Boolean.parseBoolean(pValue);
				break;
			case "altgameskilllearn":
				ALT_GAME_SKILL_LEARN = Boolean.parseBoolean(pValue);
				break;
			case "removecastlecirclets":
				REMOVE_CASTLE_CIRCLETS = Boolean.parseBoolean(pValue);
				break;
			case "reputationscoreperkill":
				REPUTATION_SCORE_PER_KILL = Integer.parseInt(pValue);
				break;
			case "altgamecancelbyhit":
				ALT_GAME_CANCEL_BOW = pValue.equalsIgnoreCase("bow") || pValue.equalsIgnoreCase("all");
				ALT_GAME_CANCEL_CAST = pValue.equalsIgnoreCase("cast") || pValue.equalsIgnoreCase("all");
				break;
			case "altshieldblocks":
				ALT_GAME_SHIELD_BLOCKS = Boolean.parseBoolean(pValue);
				break;
			case "altperfectshieldblockrate":
				ALT_PERFECT_SHLD_BLOCK = Integer.parseInt(pValue);
				break;
			case "delevel":
				ALT_GAME_DELEVEL = Boolean.parseBoolean(pValue);
				break;
			case "magicfailures":
				ALT_GAME_MAGICFAILURES = Boolean.parseBoolean(pValue);
				break;
			case "altmobagroinpeacezone":
				ALT_MOB_AGRO_IN_PEACEZONE = Boolean.parseBoolean(pValue);
				break;
			case "altgameexponentxp":
				ALT_GAME_EXPONENT_XP = Float.parseFloat(pValue);
				break;
			case "altgameexponentsp":
				ALT_GAME_EXPONENT_SP = Float.parseFloat(pValue);
				break;
			case "allowclassmasters":
				ALLOW_CLASS_MASTERS = Boolean.parseBoolean(pValue);
				break;
			case "allowentiretree":
				ALLOW_ENTIRE_TREE = Boolean.parseBoolean(pValue);
				break;
			case "alternateclassmaster":
				ALTERNATE_CLASS_MASTER = Boolean.parseBoolean(pValue);
				break;
			case "altpartyrange":
				ALT_PARTY_RANGE = Integer.parseInt(pValue);
				break;
			case "altpartyrange2":
				ALT_PARTY_RANGE2 = Integer.parseInt(pValue);
				break;
			case "altleavepartyleader":
				ALT_LEAVE_PARTY_LEADER = Boolean.parseBoolean(pValue);
				break;
			case "craftingenabled":
				IS_CRAFTING_ENABLED = Boolean.parseBoolean(pValue);
				break;
			case "craftmasterwork":
				CRAFT_MASTERWORK = Boolean.parseBoolean(pValue);
				break;
			case "lifecrystalneeded":
				LIFE_CRYSTAL_NEEDED = Boolean.parseBoolean(pValue);
				break;
			case "autoloot":
				AUTO_LOOT = Boolean.parseBoolean(pValue);
				break;
			case "autolootraids":
				AUTO_LOOT_RAIDS = Boolean.parseBoolean(pValue);
				break;
			case "autolootherbs":
				AUTO_LOOT_HERBS = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanbekilledinpeacezone":
				ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanshop":
				ALT_GAME_KARMA_PLAYER_CAN_SHOP = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanusegk":
				ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanteleport":
				ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercantrade":
				ALT_GAME_KARMA_PLAYER_CAN_TRADE = Boolean.parseBoolean(pValue);
				break;
			case "altkarmaplayercanusewarehouse":
				ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Boolean.parseBoolean(pValue);
				break;
			case "maxpersonalfamepoints":
				MAX_PERSONAL_FAME_POINTS = Integer.parseInt(pValue);
				break;
			case "fortresszonefametaskfrequency":
				FORTRESS_ZONE_FAME_TASK_FREQUENCY = Integer.parseInt(pValue);
				break;
			case "fortresszonefameaquirepoints":
				FORTRESS_ZONE_FAME_AQUIRE_POINTS = Integer.parseInt(pValue);
				break;
			case "castlezonefametaskfrequency":
				CASTLE_ZONE_FAME_TASK_FREQUENCY = Integer.parseInt(pValue);
				break;
			case "castlezonefameaquirepoints":
				CASTLE_ZONE_FAME_AQUIRE_POINTS = Integer.parseInt(pValue);
				break;
			case "altcastlefordawn":
				ALT_GAME_CASTLE_DAWN = Boolean.parseBoolean(pValue);
				break;
			case "altcastlefordusk":
				ALT_GAME_CASTLE_DUSK = Boolean.parseBoolean(pValue);
				break;
			case "altrequireclancastle":
				ALT_GAME_REQUIRE_CLAN_CASTLE = Boolean.parseBoolean(pValue);
				break;
			case "altfreeteleporting":
				ALT_GAME_FREE_TELEPORT = Boolean.parseBoolean(pValue);
				break;
			case "altsubclasswithoutquests":
				ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Boolean.parseBoolean(pValue);
				break;
			case "altsubclasseverywhere":
				ALT_GAME_SUBCLASS_EVERYWHERE = Boolean.parseBoolean(pValue);
				break;
			case "altmemberscanwithdrawfromclanwh":
				ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Boolean.parseBoolean(pValue);
				break;
			case "dwarfrecipelimit":
				DWARF_RECIPE_LIMIT = Integer.parseInt(pValue);
				break;
			case "commonrecipelimit":
				COMMON_RECIPE_LIMIT = Integer.parseInt(pValue);
				break;
			case "Easychampionenable":
				EASY_CHAMPION_ENABLE = Boolean.parseBoolean(pValue);
				break;
			case "Easychampionfrequency":
				EASY_CHAMPION_FREQUENCY = Integer.parseInt(pValue);
				break;
			case "Easychampionminlevel":
				EASY_CHAMP_MIN_LVL = Integer.parseInt(pValue);
				break;
			case "Easychampionmaxlevel":
				EASY_CHAMP_MAX_LVL = Integer.parseInt(pValue);
				break;
			case "Easychampionhp":
				EASY_CHAMPION_HP = Integer.parseInt(pValue);
				break;
			case "Easychampionhpregen":
				EASY_CHAMPION_HP_REGEN = Float.parseFloat(pValue);
				break;
			case "Easychampionrewards":
				EASY_CHAMPION_REWARDS = Integer.parseInt(pValue);
				break;
			case "Easychampionadenasrewards":
				EASY_CHAMPION_ADENAS_REWARDS = Float.parseFloat(pValue);
				break;
			case "Easychampionatk":
				EASY_CHAMPION_ATK = Float.parseFloat(pValue);
				break;
			case "Easychampionspdatk":
				EASY_CHAMPION_SPD_ATK = Float.parseFloat(pValue);
				break;
			case "Easychampionrewardlowerlvlitemchance":
				EASY_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE = Integer.parseInt(pValue);
				break;
			case "Easychampionrewardhigherlvlitemchance":
				EASY_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE = Integer.parseInt(pValue);
				break;
			case "Easychampionrewarditemid":
				EASY_CHAMPION_REWARD_ID = Integer.parseInt(pValue);
				break;
			case "Easychampionrewarditemqty":
				EASY_CHAMPION_REWARD_QTY = Integer.parseInt(pValue);
				break;
			case "Easychampionenableininstances":
				EASY_CHAMPION_ENABLE_IN_INSTANCES = Boolean.parseBoolean(pValue);
				break;
			case "Hardchampionenable":
				HARD_CHAMPION_ENABLE = Boolean.parseBoolean(pValue);
				break;
			case "Hardchampionfrequency":
				HARD_CHAMPION_FREQUENCY = Integer.parseInt(pValue);
				break;
			case "Hardchampionminlevel":
				HARD_CHAMP_MIN_LVL = Integer.parseInt(pValue);
				break;
			case "Hardchampionmaxlevel":
				HARD_CHAMP_MAX_LVL = Integer.parseInt(pValue);
				break;
			case "Hardchampionhp":
				HARD_CHAMPION_HP = Integer.parseInt(pValue);
				break;
			case "Hardchampionhpregen":
				HARD_CHAMPION_HP_REGEN = Float.parseFloat(pValue);
				break;
			case "Hardchampionrewards":
				HARD_CHAMPION_REWARDS = Integer.parseInt(pValue);
				break;
			case "Hardchampionadenasrewards":
				HARD_CHAMPION_ADENAS_REWARDS = Float.parseFloat(pValue);
				break;
			case "Hardchampionatk":
				HARD_CHAMPION_ATK = Float.parseFloat(pValue);
				break;
			case "Hardchampionspdatk":
				HARD_CHAMPION_SPD_ATK = Float.parseFloat(pValue);
				break;
			case "Hardchampionrewardlowerlvlitemchance":
				HARD_CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE = Integer.parseInt(pValue);
				break;
			case "Hardchampionrewardhigherlvlitemchance":
				HARD_CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE = Integer.parseInt(pValue);
				break;
			case "Hardchampionrewarditemid":
				HARD_CHAMPION_REWARD_ID = Integer.parseInt(pValue);
				break;
			case "Hardchampionrewarditemqty":
				HARD_CHAMPION_REWARD_QTY = Integer.parseInt(pValue);
				break;
			case "Hardchampionenableininstances":
				HARD_CHAMPION_ENABLE_IN_INSTANCES = Boolean.parseBoolean(pValue);
				break;
			case "allowwedding":
				ALLOW_WEDDING = Boolean.parseBoolean(pValue);
				break;
			case "weddingprice":
				WEDDING_PRICE = Integer.parseInt(pValue);
				break;
			case "weddingpunishinfidelity":
				WEDDING_PUNISH_INFIDELITY = Boolean.parseBoolean(pValue);
				break;
			case "weddingteleport":
				WEDDING_TELEPORT = Boolean.parseBoolean(pValue);
				break;
			case "weddingteleportprice":
				WEDDING_TELEPORT_PRICE = Integer.parseInt(pValue);
				break;
			case "weddingteleportduration":
				WEDDING_TELEPORT_DURATION = Integer.parseInt(pValue);
				break;
			case "weddingallowsamesex":
				WEDDING_SAMESEX = Boolean.parseBoolean(pValue);
				break;
			case "weddingformalwear":
				WEDDING_FORMALWEAR = Boolean.parseBoolean(pValue);
				break;
			case "weddingdivorcecosts":
				WEDDING_DIVORCE_COSTS = Integer.parseInt(pValue);
				break;
			case "tvteventenabled":
				TVT_EVENT_ENABLED = Boolean.parseBoolean(pValue);
				break;
			case "tvteventinterval":
				TVT_EVENT_INTERVAL = pValue.split(",");
				break;
			case "tvteventparticipationtime":
				TVT_EVENT_PARTICIPATION_TIME = Integer.parseInt(pValue);
				break;
			case "tvteventrunningtime":
				TVT_EVENT_RUNNING_TIME = Integer.parseInt(pValue);
				break;
			case "tvteventparticipationnpcid":
				TVT_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(pValue);
				break;
			case "enablewarehousesortingclan":
				ENABLE_WAREHOUSESORTING_CLAN = Boolean.parseBoolean(pValue);
				break;
			case "enablewarehousesortingprivate":
				ENABLE_WAREHOUSESORTING_PRIVATE = Boolean.parseBoolean(pValue);
				break;
			case "enablemanapotionsupport":
				ENABLE_MANA_POTIONS_SUPPORT = Boolean.parseBoolean(pValue);
				break;
			case "displayservertime":
				DISPLAY_SERVER_TIME = Boolean.parseBoolean(pValue);
				break;
			case "antifeedenable":
				ANTIFEED_ENABLE = Boolean.parseBoolean(pValue);
				break;
			case "antifeeddualbox":
				ANTIFEED_DUALBOX = Boolean.parseBoolean(pValue);
				break;
			case "antifeeddisconnectedasdualbox":
				ANTIFEED_DISCONNECTED_AS_DUALBOX = Boolean.parseBoolean(pValue);
				break;
			case "antifeedinterval":
				ANTIFEED_INTERVAL = 1000 * Integer.parseInt(pValue);
				break;
			case "minkarma":
				KARMA_MIN_KARMA = Integer.parseInt(pValue);
				break;
			case "maxkarma":
				KARMA_MAX_KARMA = Integer.parseInt(pValue);
				break;
			case "xpdivider":
				KARMA_XP_DIVIDER = Integer.parseInt(pValue);
				break;
			case "basekarmalost":
				KARMA_LOST_BASE = Integer.parseInt(pValue);
				break;
			case "cangmdropequipment":
				KARMA_DROP_GM = Boolean.parseBoolean(pValue);
				break;
			case "awardpkkillpvppoint":
				KARMA_AWARD_PK_KILL = Boolean.parseBoolean(pValue);
				break;
			case "minimumpkrequiredtodrop":
				KARMA_PK_LIMIT = Integer.parseInt(pValue);
				break;
			case "pvpvsnormaltime":
				PVP_NORMAL_TIME = Integer.parseInt(pValue);
				break;
			case "pvpvspvptime":
				PVP_PVP_TIME = Integer.parseInt(pValue);
				break;
			case "globalchat":
				DEFAULT_GLOBAL_CHAT = pValue;
				break;
			case "tradechat":
				DEFAULT_TRADE_CHAT = pValue;
				break;
			case "gmadminmenustyle":
				GM_ADMIN_MENU_STYLE = pValue;
				break;
			default:
				try
				{
					// TODO: stupid GB configs...
					if (!pName.startsWith("Interval_") && !pName.startsWith("Random_"))
					{
						pName = pName.toUpperCase();
					}
					Field clazField = Config.class.getField(pName);
					int modifiers = clazField.getModifiers();
					// just in case :)
					if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers) || Modifier.isFinal(modifiers))
					{
						throw new SecurityException("Cannot modify non public, non static or final config!");
					}
					
					if (clazField.getType() == int.class)
					{
						clazField.setInt(clazField, Integer.parseInt(pValue));
					}
					else if (clazField.getType() == short.class)
					{
						clazField.setShort(clazField, Short.parseShort(pValue));
					}
					else if (clazField.getType() == byte.class)
					{
						clazField.setByte(clazField, Byte.parseByte(pValue));
					}
					else if (clazField.getType() == long.class)
					{
						clazField.setLong(clazField, Long.parseLong(pValue));
					}
					else if (clazField.getType() == float.class)
					{
						clazField.setFloat(clazField, Float.parseFloat(pValue));
					}
					else if (clazField.getType() == double.class)
					{
						clazField.setDouble(clazField, Double.parseDouble(pValue));
					}
					else if (clazField.getType() == boolean.class)
					{
						clazField.setBoolean(clazField, Boolean.parseBoolean(pValue));
					}
					else if (clazField.getType() == String.class)
					{
						clazField.set(clazField, pValue);
					}
					else
					{
						return false;
					}
				}
				catch (NoSuchFieldException e)
				{
					return false;
				}
				catch (Exception e)
				{
					_log.log(Level.WARNING, "", e);
					return false;
				}
		}
		return true;
	}
	
	/**
	 * Save hexadecimal ID of the server in the L2Properties file.<br>
	 * Check {@link #HEXID_FILE}.
	 * @param serverId the ID of the server whose hexId to save
	 * @param hexId the hexadecimal ID to store
	 */
	public static void saveHexid(int serverId, String hexId)
	{
		Config.saveHexid(serverId, hexId, HEXID_FILE);
	}
	
	/**
	 * Save hexadecimal ID of the server in the L2Properties file.
	 * @param serverId the ID of the server whose hexId to save
	 * @param hexId the hexadecimal ID to store
	 * @param fileName name of the L2Properties file
	 */
	public static void saveHexid(int serverId, String hexId, String fileName)
	{
		try
		{
			L2Properties hexSetting = new L2Properties();
			File file = new File(fileName);
			// Create a new empty file only if it doesn't exist
			file.createNewFile();
			try (OutputStream out = new FileOutputStream(file))
			{
				hexSetting.setProperty("ServerID", String.valueOf(serverId));
				hexSetting.setProperty("HexID", hexId);
				hexSetting.store(out, "the hexID to auth into login");
			}
		}
		catch (Exception e)
		{
			_log.warning(StringUtil.concat("Failed to save hex id to ", fileName, " File."));
			_log.warning("Config: " + e.getMessage());
		}
	}
	
	/**
	 * Loads flood protector configurations.
	 * @param properties the properties object containing the actual values of the flood protector configs
	 */
	private static void loadFloodProtectorConfigs(final L2Properties properties)
	{
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_USE_ITEM, "UseItem", "4");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ROLL_DICE, "RollDice", "42");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_FIREWORK, "Firework", "42");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ITEM_PET_SUMMON, "ItemPetSummon", "16");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_HERO_VOICE, "HeroVoice", "100");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_GLOBAL_CHAT, "GlobalChat", "5");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SUBCLASS, "Subclass", "20");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_DROP_ITEM, "DropItem", "10");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SERVER_BYPASS, "ServerBypass", "5");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MULTISELL, "MultiSell", "1");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_TRANSACTION, "Transaction", "10");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANUFACTURE, "Manufacture", "3");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_MANOR, "Manor", "30");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_SENDMAIL, "SendMail", "100");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_CHARACTER_SELECT, "CharacterSelect", "30");
		loadFloodProtectorConfig(properties, FLOOD_PROTECTOR_ITEM_AUCTION, "ItemAuction", "9");
	}
	
	/**
	 * Loads single flood protector configuration.
	 * @param properties properties file reader
	 * @param config flood protector configuration instance
	 * @param configString flood protector configuration string that determines for which flood protector configuration should be read
	 * @param defaultInterval default flood protector interval
	 */
	private static void loadFloodProtectorConfig(final L2Properties properties, final FloodProtectorConfig config, final String configString, final String defaultInterval)
	{
		config.FLOOD_PROTECTION_INTERVAL = Integer.parseInt(properties.getProperty(StringUtil.concat("FloodProtector", configString, "Interval"), defaultInterval));
		config.LOG_FLOODING = Boolean.parseBoolean(properties.getProperty(StringUtil.concat("FloodProtector", configString, "LogFlooding"), "False"));
		config.PUNISHMENT_LIMIT = Integer.parseInt(properties.getProperty(StringUtil.concat("FloodProtector", configString, "PunishmentLimit"), "0"));
		config.PUNISHMENT_TYPE = properties.getProperty(StringUtil.concat("FloodProtector", configString, "PunishmentType"), "none");
		config.PUNISHMENT_TIME = Integer.parseInt(properties.getProperty(StringUtil.concat("FloodProtector", configString, "PunishmentTime"), "0"));
	}
	
	public static int getServerTypeId(String[] serverTypes)
	{
		int tType = 0;
		for (String cType : serverTypes)
		{
			switch (cType.trim().toLowerCase())
			{
				case "Normal":
					tType |= 0x01;
					break;
				case "Relax":
					tType |= 0x02;
					break;
				case "Test":
					tType |= 0x04;
					break;
				case "NoLabel":
					tType |= 0x08;
					break;
				case "Restricted":
					tType |= 0x10;
					break;
				case "Event":
					tType |= 0x20;
					break;
				case "Free":
					tType |= 0x40;
					break;
				default:
					break;
			}
		}
		return tType;
	}
	
	public static class ClassMasterSettings
	{
		private final Map<Integer, Map<Integer, Integer>> _claimItems;
		private final Map<Integer, Map<Integer, Integer>> _rewardItems;
		private final Map<Integer, Boolean> _allowedClassChange;
		
		public ClassMasterSettings(String _configLine)
		{
			_claimItems = new HashMap<>(3);
			_rewardItems = new HashMap<>(3);
			_allowedClassChange = new HashMap<>(3);
			if (_configLine != null)
			{
				parseConfigLine(_configLine.trim());
			}
		}
		
		private void parseConfigLine(String _configLine)
		{
			StringTokenizer st = new StringTokenizer(_configLine, ";");
			
			while (st.hasMoreTokens())
			{
				// get allowed class change
				int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				Map<Integer, Integer> _items = new HashMap<>();
				// parse items needed for class change
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						int _itemId = Integer.parseInt(st3.nextToken());
						int _quantity = Integer.parseInt(st3.nextToken());
						_items.put(_itemId, _quantity);
					}
				}
				
				_claimItems.put(job, _items);
				
				_items = new HashMap<>();
				// parse gifts after class change
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						int _itemId = Integer.parseInt(st3.nextToken());
						int _quantity = Integer.parseInt(st3.nextToken());
						_items.put(_itemId, _quantity);
					}
				}
				
				_rewardItems.put(job, _items);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if (_allowedClassChange == null)
			{
				return false;
			}
			if (_allowedClassChange.containsKey(job))
			{
				return _allowedClassChange.get(job);
			}
			
			return false;
		}
		
		public Map<Integer, Integer> getRewardItems(int job)
		{
			if (_rewardItems.containsKey(job))
			{
				return _rewardItems.get(job);
			}
			
			return null;
		}
		
		public Map<Integer, Integer> getRequireItems(int job)
		{
			if (_claimItems.containsKey(job))
			{
				return _claimItems.get(job);
			}
			
			return null;
		}
	}
	
	/**
	 * @param line the string line to parse
	 * @return a parsed float map
	 */
	private static Map<Integer, Float> parseConfigLine(String line)
	{
		String[] propertySplit = line.split(",");
		Map<Integer, Float> ret = new HashMap<>(propertySplit.length);
		int i = 0;
		for (String value : propertySplit)
		{
			ret.put(i++, Float.parseFloat(value));
		}
		return ret;
	}
	
	/**
	 * Parse a config value from its string representation to a two-dimensional int array.<br>
	 * The format of the value to be parsed should be as follows: "item1Id,item1Amount;item2Id,item2Amount;...itemNId,itemNAmount".
	 * @param line the value of the parameter to parse
	 * @return the parsed list or {@code null} if nothing was parsed
	 */
	private static int[][] parseItemsList(String line)
	{
		final String[] propertySplit = line.split(";");
		if (propertySplit.length == 0)
		{
			// nothing to do here
			return null;
		}
		
		int i = 0;
		String[] valueSplit;
		final int[][] result = new int[propertySplit.length][];
		int[] tmp;
		for (String value : propertySplit)
		{
			valueSplit = value.split(",");
			if (valueSplit.length != 2)
			{
				_log.warning(StringUtil.concat("parseItemsList[Config.load()]: invalid entry -> \"", valueSplit[0], "\", should be itemId,itemNumber. Skipping to the next entry in the list."));
				continue;
			}
			
			tmp = new int[2];
			try
			{
				tmp[0] = Integer.parseInt(valueSplit[0]);
			}
			catch (NumberFormatException e)
			{
				_log.warning(StringUtil.concat("parseItemsList[Config.load()]: invalid itemId -> \"", valueSplit[0], "\", value must be an integer. Skipping to the next entry in the list."));
				continue;
			}
			try
			{
				tmp[1] = Integer.parseInt(valueSplit[1]);
			}
			catch (NumberFormatException e)
			{
				_log.warning(StringUtil.concat("parseItemsList[Config.load()]: invalid item number -> \"", valueSplit[1], "\", value must be an integer. Skipping to the next entry in the list."));
				continue;
			}
			result[i++] = tmp;
		}
		return result;
	}
	
	private static class IPConfigData extends DocumentParser
	{
		private static final List<String> _subnets = new ArrayList<>(5);
		private static final List<String> _hosts = new ArrayList<>(5);
		
		public IPConfigData()
		{
			load();
		}
		
		@Override
		public void load()
		{
			File f = new File(IP_CONFIG);
			if (f.exists())
			{
				_log.log(Level.INFO, "Network Config: ipconfig.xml exists using manual configuration...");
				parseFile(new File(IP_CONFIG));
			}
			else
			// Auto configuration...
			{
				_log.log(Level.INFO, "Network Config: ipconfig.xml doesn't exists using automatic configuration...");
				autoIpConfig();
			}
		}
		
		@Override
		protected void parseDocument()
		{
			NamedNodeMap attrs;
			for (Node n = getCurrentDocument().getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("gameserver".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
					{
						if ("define".equalsIgnoreCase(d.getNodeName()))
						{
							attrs = d.getAttributes();
							_subnets.add(attrs.getNamedItem("subnet").getNodeValue());
							_hosts.add(attrs.getNamedItem("address").getNodeValue());
							
							if (_hosts.size() != _subnets.size())
							{
								_log.log(Level.WARNING, "Failed to Load " + IP_CONFIG + " File - subnets does not match server addresses.");
							}
						}
					}
					
					Node att = n.getAttributes().getNamedItem("address");
					if (att == null)
					{
						_log.log(Level.WARNING, "Failed to load " + IP_CONFIG + " file - default server address is missing.");
						_hosts.add("127.0.0.1");
					}
					else
					{
						_hosts.add(att.getNodeValue());
					}
					_subnets.add("0.0.0.0/0");
				}
			}
		}
		
		protected void autoIpConfig()
		{
			String externalIp = "127.0.0.1";
			try
			{
				URL autoIp = new URL("http://api.externalip.net/ip/");
				try (BufferedReader in = new BufferedReader(new InputStreamReader(autoIp.openStream())))
				{
					externalIp = in.readLine();
				}
			}
			catch (IOException e)
			{
				_log.log(Level.INFO, "Network Config: Failed to connect to api.externalip.net please check your internet connection using 127.0.0.1!");
				externalIp = "127.0.0.1";
			}
			
			try
			{
				Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
				
				Subnet sub = new Subnet();
				while (niList.hasMoreElements())
				{
					NetworkInterface ni = niList.nextElement();
					
					if (!ni.isUp() || ni.isVirtual())
					{
						continue;
					}
					
					if (!ni.isLoopback() && ((ni.getHardwareAddress() == null) || (ni.getHardwareAddress().length != 6)))
					{
						continue;
					}
					
					for (InterfaceAddress ia : ni.getInterfaceAddresses())
					{
						if (ia.getAddress() instanceof Inet6Address)
						{
							continue;
						}
						
						sub.setIPAddress(ia.getAddress().getHostAddress());
						sub.setMaskedBits(ia.getNetworkPrefixLength());
						String subnet = sub.getSubnetAddress() + '/' + sub.getMaskedBits();
						if (!_subnets.contains(subnet) && !subnet.equals("0.0.0.0/0"))
						{
							_subnets.add(subnet);
							_hosts.add(sub.getIPAddress());
							_log.log(Level.INFO, "Network Config: Adding new subnet: " + subnet + " address: " + sub.getIPAddress());
						}
					}
				}
				
				// External host and subnet
				_hosts.add(externalIp);
				_subnets.add("0.0.0.0/0");
				_log.log(Level.INFO, "Network Config: Adding new subnet: 0.0.0.0/0 address: " + externalIp);
			}
			catch (SocketException e)
			{
				_log.log(Level.INFO, "Network Config: Configuration failed please configure manually using ipconfig.xml", e);
				System.exit(0);
			}
		}
		
		protected List<String> getSubnets()
		{
			if (_subnets.isEmpty())
			{
				return Arrays.asList("0.0.0.0/0");
			}
			return _subnets;
		}
		
		protected List<String> getHosts()
		{
			if (_hosts.isEmpty())
			{
				return Arrays.asList("127.0.0.1");
			}
			return _hosts;
		}
	}
}
