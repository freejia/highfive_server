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
package com.l2jserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import com.l2jserver.gameserver.model.quest.Quest;

public class FunEvents extends Quest
{
	private static final String qn = "EventsConfig";
	
	/*
	 * Master of Enchanting date
	 */
	public static boolean MY_STARTED = getEvent("MasterOfEnchanting");
	
	/*
	 * The Valentine Event date
	 */
	public static boolean TVE_STARTED = getEvent("TheValentineEvent");
	public static boolean TVE_ACTIVE_DROP = getEvent("TheValentineEvent");
	
	/*
	 * Holly Cow Event date
	 */
	public static boolean HC_STARTED = getEvent("HollyCow");
	
	/*
	 * April Fool's Day date
	 */
	public static boolean AP_STARTED = getEvent("AprilFools");
	
	/*
	 * Ninja Adventures Event date
	 */
	public static boolean NA_STARTED = getEvent("NinjaAdventures");
	
	/*
	 * Super Star Event date
	 */
	public static boolean SS_STARTED = getEvent("SuperStar");
	
	/*
	 * Squish and Squash Event date
	 */
	public static boolean SQUASH_STARTED = getEvent("SquashEvent");
	public static boolean SQUASH_DROP_ACTIVE = getEvent("SquashEvent");
	
	/*
	 * L2Day Event date
	 */
	public static boolean L2DAY_STARTED = getEvent("L2Day");
	public static boolean L2DAY_ACTIVE_DROP = getEvent("L2Day");
	
	/*
	 * SchoolDays date
	 */
	public static boolean SD_STARTED = getEvent("SchoolDays");
	public static boolean SD_ACTIVE_DROP = getEvent("SchoolDays");
	
	/*
	 * Trick or Transmutation Event date
	 */
	public static boolean TOT_STARTED = getEvent("TrickorTransmutation");
	public static boolean TOT_ACTIVE_DROP = getEvent("TrickorTransmutation");
	
	/*
	 * Hallowed You Event date
	 */
	public static boolean HY_STARTED = getEvent("HallowedYou");
	public static boolean HY_ACTIVE_DROP = getEvent("HallowedYou");
	
	/*
	 * Christmas Is Here Event date
	 */
	public static boolean CH_STARTED = getEvent("ChristmasIsHere");
	public static boolean CH_ACTIVE_DROP = getEvent("ChristmasIsHere");
	
	/**
	 * Get Event data from sql
	 * @param eventName
	 * @return
	 */
	private static boolean getEvent(String eventName)
	{
		boolean state = false;
		int eventMonth = 0;
		int eventStartDay = 0;
		int eventEndDay = 0;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();)
		{
			PreparedStatement statement = con.prepareStatement("SELECT * FROM events WHERE name=?");
			statement.setString(1, eventName);
			ResultSet result = statement.executeQuery();
			if (result.next())
			{
				eventMonth = result.getInt("month");
				eventStartDay = result.getInt("start_day");
				eventEndDay = result.getInt("end_day");
			}
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Events Table Couldn't read:" + e);
		}
		int Month = Integer.valueOf(Calendar.getInstance().get(Calendar.MONTH)) + 1;
		int Day = Integer.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		try
		{
			if ((eventMonth == Month) && (Day >= eventStartDay) && (Day <= eventEndDay))
			{
				return true;
			}
		}
		catch (Exception e)
		{
		}
		return state;
	}
	
	/*
	 * How to change a event? 1. Change Boolean value of event 2. Use command //quest_reload EventsConfig 3. Use command //quest_reload EventName 4. Now you can use event.
	 */
	
	/**
	 * Character Birthday
	 */
	public static final int ADVENTURER_HAT = 10250;
	public static final int BIRTHDAY_CAKE_SKILL = 5950;
	/**
	 * L2Marks
	 */
	public static final int L2MARKS_VITAMIN_LEVEL = 77;
	public static final int L2MARKS_COIN = 13419;
	/**
	 * Heavy Medal
	 */
	public static final int HM_MEDAL = 6392;
	public static final int HM_GLITTERING_MEDAL = 6393;
	/**
	 * Holly Cow
	 */
	public static final int HC_ADENA = 57;
	public static final int HC_MILK = 14739;
	public static final int HC_MILK_COW_SCROLL = 14724;
	public static final int HC_HEAD_MILK_COW_SCROLL = 14725;
	public static final int HC_GLOOM_MILK_COW_SCROLL = 14726;
	public static final int HC_GLOOM_HEAD_MILK_COW_SCROLL = 14727;
	/**
	 * L2 Day
	 */
	public static final int L2DAY_A = 3875;
	public static final int L2DAY_C = 3876;
	public static final int L2DAY_E = 3877;
	public static final int L2DAY_F = 3878;
	public static final int L2DAY_G = 3879;
	public static final int L2DAY_H = 3880;
	public static final int L2DAY_I = 3881;
	public static final int L2DAY_L = 3882;
	public static final int L2DAY_N = 3883;
	public static final int L2DAY_O = 3884;
	public static final int L2DAY_R = 3885;
	public static final int L2DAY_S = 3886;
	public static final int L2DAY_T = 3887;
	public static final int L2DAY_II = 3888;
	public static final int L2DAY_Y = 13417;
	public static final int L2DAY_5 = 13418;
	public static final int L2DAY_GUIDANCE = 3926;
	public static final int L2DAY_DEATH_WHISPER = 3927;
	public static final int L2DAY_FOCUS = 3928;
	public static final int L2DAY_GREATER_ACUMEN = 3929;
	public static final int L2DAY_HASTE = 3930;
	public static final int L2DAY_AGILITY = 3931;
	public static final int L2DAY_MYSTIC_EMPOWER = 3932;
	public static final int L2DAY_MIGHT = 3933;
	public static final int L2DAY_WINDWALK = 3934;
	public static final int L2DAY_SHIELD = 3935;
	public static final int L2DAY_BSOE = 1538;
	public static final int L2DAY_BSOR = 3936;
	public static final int L2DAY_MANA_REGENERATION = 4218;
	public static final int L2DAY_ADENA = 57;
	public static final int L2DAY_ANCIENT_ADENA = 5575;
	public static final int L2DAY_MAGE_COCKTAIL = 20394;
	public static final int L2DAY_FIGHTER_COCKTAIL = 20393;
	/**
	 * Master Of Enchanting
	 */
	public static final int MY_STAFF = 13539;
	public static final int MY_SCROLL = 13540;
	public static final int MY_ADENA = 57;
	public static final int MY_STAFF_PRICE = 1000;
	public static final int MY_SCROLL_24_PRICE = 6000;
	public static final int MY_SCROLL_24_TIME = 6;
	public static final int MY_SCROLL_1_PRICE = 77777;
	public static final int MY_SCROLL_10_PRICE = 777770;
	public static final int MY_HAND_SLOT = 16;
	public static final int[] MY_HAT_SHADOW_REWARD =
	{
		13074,
		13075,
		13076
	};
	public static final int[] MY_HAT_EVENT_REWARD =
	{
		13518,
		13519,
		13522
	};
	public static final int[] MY_CRYSTAL_REWARD =
	{
		9570,
		9571,
		9572
	};
	/**
	 * Ninja Adventures
	 */
	public static final int NA_HAIRBAND = 7060;
	public static final int NA_ADENA = 57;
	public static final int NA_ANCIENT_ADENA = 5575;
	/**
	 * The Valentine Event
	 */
	public static final int TVE_RECIPE = 20191;
	public static final int TVE_REAGENT1 = 20194;
	public static final int TVE_REAGENT2 = 20192;
	public static final int TVE_REAGENT3 = 20193;
	public static final int TVE_CAKE1 = 20195;
	public static final int TVE_CAKE2 = 20196;
	public static final int TVE_CAKE3 = 20197;
	public static final int TVE_CAKE4 = 20198;
	/**
	 * Trick or Transmutation
	 */
	public static final int TOT_KEY = 9205;
	public static final int TOT_RED_STONE = 9162;
	public static final int TOT_BLUE_STONE = 9163;
	public static final int TOT_ORANGE_STONE = 9164;
	public static final int TOT_BLACK_STONE = 9165;
	public static final int TOT_WHITE_STONE = 9166;
	public static final int TOT_GREEN_STONE = 9167;
	public static final int TOT_STONE_ORE = 9168;
	public static final int TOT_STONE_FORMULA = 9169;
	public static final int TOT_MAGIC_REAGENTS = 9170;
	/**
	 * HallowedYou
	 */
	public static final int HALLOWEEN_CANDY = 15430;
	/**
	 * ChristmasIsHere
	 */
	public static final int CH_CHRISTMAS_SOCK = 14612;
	/**
	 * SchoolDays
	 */
	
	/**
	 * Super Star
	 */
	public static final int SS_BACKUP_STONE_WEP_D = 12362;
	public static final int SS_BACKUP_STONE_WEP_C = 12363;
	public static final int SS_BACKUP_STONE_WEP_B = 12364;
	public static final int SS_BACKUP_STONE_WEP_A = 12365;
	public static final int SS_BACKUP_STONE_WEP_S = 12366;
	public static final int SS_BACKUP_STONE_ARM_D = 12367;
	public static final int SS_BACKUP_STONE_ARM_C = 12368;
	public static final int SS_BACKUP_STONE_ARM_B = 12369;
	public static final int SS_BACKUP_STONE_ARM_A = 12370;
	public static final int SS_BACKUP_STONE_ARM_S = 12371;
	
	/**
	 * Text - this event is disabled
	 */
	public static final String EVENT_DISABLED = "<html><title>Event Message</title><body><center><img src=\"L2UI_CH3.onscrmsg_pattern01_1\" width=300 height=32 align=left><br>" + "<br><br><br><br><br><br><b><font color=LEVEL>THIS EVENT<br>IS<br>NOW<br>DISABLED</font></b><br><br><br><br><br><br><br><br><br><br><br><br>" + "<br><img src=\"L2UI_CH3.onscrmsg_pattern01_2\" width=300 height=32 align=left></center></body></html>";
	
	public static void setValue(boolean value, boolean status)
	{
		value = status;
	}
	
	public FunEvents(int questId, String name, String descr)
	{
		super(questId, name, descr);
	}
	
	public static void main(String[] args)
	{
		new FunEvents(-1, qn, "events");
	}
}