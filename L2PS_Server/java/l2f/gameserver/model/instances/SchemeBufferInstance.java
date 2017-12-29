package l2f.gameserver.model.instances;

import gnu.trove.list.array.TIntArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.instancemanager.ReflectionManager;
import l2f.gameserver.model.Playable;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Summon;
import l2f.gameserver.model.entity.events.impl.AbstractFightClub;
import l2f.gameserver.model.entity.olympiad.Olympiad;
import l2f.gameserver.network.serverpackets.PlaySound;
import l2f.gameserver.network.serverpackets.ShowBoard;
import l2f.gameserver.network.serverpackets.SystemMessage;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.skills.effects.EffectCubic;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.templates.npc.NpcTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemeBufferInstance extends NpcInstance
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1884810446468483368L;

	private static final Logger _log = LoggerFactory.getLogger(SchemeBufferInstance.class);

	private static final boolean DEBUG = false;
	private static final String TITLE_NAME = "Scheme Buffer";
	private static final boolean ENABLE_VIP_BUFFER = Config.NpcBuffer_VIP;
	private static final int VIP_ACCESS_LEVEL = Config.NpcBuffer_VIP_ALV;
	private static final boolean ENABLE_BUFF_SECTION = Config.NpcBuffer_EnableBuff;
	private static final boolean ENABLE_SCHEME_SYSTEM = Config.NpcBuffer_EnableScheme;
	private static final boolean ENABLE_HEAL = Config.NpcBuffer_EnableHeal;
	private static final boolean ENABLE_BUFFS = Config.NpcBuffer_EnableBuffs;
	private static final boolean ENABLE_RESIST = Config.NpcBuffer_EnableResist;
	private static final boolean ENABLE_SONGS = Config.NpcBuffer_EnableSong;
	private static final boolean ENABLE_DANCES = Config.NpcBuffer_EnableDance;
	private static final boolean ENABLE_CHANTS = Config.NpcBuffer_EnableChant;
	private static final boolean ENABLE_OTHERS = Config.NpcBuffer_EnableOther;
	private static final boolean ENABLE_SPECIAL = Config.NpcBuffer_EnableSpecial;
	private static final boolean ENABLE_CUBIC = Config.NpcBuffer_EnableCubic;
	private static final boolean ENABLE_BUFF_REMOVE = Config.NpcBuffer_EnableCancel;
	private static final boolean ENABLE_BUFF_SET = Config.NpcBuffer_EnableBuffSet;
	private static final boolean BUFF_WITH_KARMA = Config.NpcBuffer_EnableBuffPK;
	private static final boolean FREE_BUFFS = Config.NpcBuffer_EnableFreeBuffs;
	private static final int MIN_LEVEL = Config.NpcBuffer_MinLevel;
	private static final int BUFF_REMOVE_PRICE = Config.NpcBuffer_PriceCancel;
	private static final int HEAL_PRICE = Config.NpcBuffer_PriceHeal;
	private static final int BUFF_PRICE = Config.NpcBuffer_PriceBuffs;
	private static final int RESIST_PRICE = Config.NpcBuffer_PriceResist;
	private static final int SONG_PRICE = Config.NpcBuffer_PriceSong;
	private static final int DANCE_PRICE = Config.NpcBuffer_PriceDance;
	private static final int CHANT_PRICE = Config.NpcBuffer_PriceChant;
	private static final int OTHERS_PRICE = Config.NpcBuffer_PriceOther;
	private static final int SPECIAL_PRICE = Config.NpcBuffer_PriceSpecial;
	private static final int CUBIC_PRICE = Config.NpcBuffer_PriceCubic;
	private static final int BUFF_SET_PRICE = Config.NpcBuffer_PriceSet;
	private static final int SCHEME_BUFF_PRICE = Config.NpcBuffer_PriceScheme;
	private static final int SCHEMES_PER_PLAYER = Config.NpcBuffer_MaxScheme;
	private static final int MAX_SCHEME_BUFFS = Config.ALT_BUFF_LIMIT;
	private static final int MAX_SCHEME_DANCES = Config.ALT_MUSIC_LIMIT;
	private static final int CONSUMABLE_ID = 57;

	private static final String SET_FIGHTER = "Fighter";
	private static final String SET_MAGE = "Mage";
	private static final String SET_ALL = "All";
	private static final String SET_NONE = "None";

	private static boolean singleBuffsLoaded = false;
	private static List<SingleBuff> allSingleBuffs = null;

	public SchemeBufferInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);

		if (!singleBuffsLoaded)
		{
			singleBuffsLoaded = true;
			loadSingleBuffs();
		}
	}

	private static void loadSingleBuffs()
	{
		allSingleBuffs = new LinkedList<>();
		try (Connection con = DatabaseFactory.getInstance().getConnection();
		    PreparedStatement statement = con.prepareStatement("SELECT * FROM npcbuffer_buff_list WHERE canUse = 1 ORDER BY Buff_Class ASC, id");
		    ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				int id = rset.getInt("id");
				int buffClass = rset.getInt("buff_class");
				String buffType = rset.getString("buffType");
				int buffId = rset.getInt("buffId");
				int buffLevel = rset.getInt("buffLevel");
				int forClass = rset.getInt("forClass");

				allSingleBuffs.add(new SingleBuff(id, buffClass, buffType, buffId, buffLevel, forClass));
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading Single Buffs", e);
		}
	}

	private static class SingleBuff
	{
		public final int id;
		public final int buffClass;
		public final String buffType;
		public final int buffId;
		public final int buffLevel;
		public final int forClass;

		private SingleBuff(int id, int buffClass, String buffType, int buffId, int buffLevel, int forClass)
		{
			this.id = id;
			this.buffClass = buffClass;
			this.buffType = buffType;
			this.buffId = buffId;
			this.buffLevel = buffLevel;
			this.forClass = forClass;
		}
	}

	public static class PlayerScheme
	{
		public final int schemeId;
		public final String schemeName;
		public final List<SchemeBuff> schemeBuffs;

		private PlayerScheme(int schemeId, String schemeName)
		{
			this.schemeId = schemeId;
			this.schemeName = schemeName;
			schemeBuffs = new LinkedList<>();
		}
	}

	private static class SchemeBuff
	{
		public final int skillId;
		public final int skillLevel;
		public final int forClass;

		private SchemeBuff(int skillId, int skillLevel, int forClass)
		{
			this.skillId = skillId;
			this.skillLevel = skillLevel;
			this.forClass = forClass;
		}
	}

	public static void loadSchemes(Player player, Connection con)
	{
		//Loading Scheme Templates
		try (PreparedStatement statement = con.prepareStatement("SELECT id, scheme_name FROM npcbuffer_scheme_list WHERE player_id=?"))
		{
			statement.setInt(1, player.getObjectId());
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
				{
					int schemeId = rset.getInt("id");
					String schemeName = rset.getString("scheme_name");
					player.getBuffSchemes().add(new PlayerScheme(schemeId, schemeName));
				}
			}
		}
		catch (SQLException e)
		{
			_log.error("Error while loading Scheme Content of the Player", e);
		}

		//Loading Scheme Contents
		for (PlayerScheme scheme : player.getBuffSchemes())
		{
			try (PreparedStatement statement = con.prepareStatement("SELECT skill_id, skill_level, buff_class FROM npcbuffer_scheme_contents WHERE scheme_id=? ORDER BY Buff_Class ASC, id"))
			{
				statement.setInt(1, scheme.schemeId);
				try (ResultSet rset = statement.executeQuery())
				{
					while (rset.next())
					{
						int skillId = rset.getInt("skill_id");
						int skillLevel = rset.getInt("skill_level");
						int forClass = rset.getInt("buff_class");
						scheme.schemeBuffs.add(new SchemeBuff(skillId, skillLevel, forClass));
					}
				}
			}
			catch (SQLException e)
			{
				_log.error("Error while loading Scheme Content of the Player", e);
			}
		}
	}

	private static void setPetBuff(Player player, String eventParam1)
	{
		player.addQuickVar("SchemeBufferPet", Integer.valueOf(eventParam1));
	}

	private static boolean isPetBuff(Player player)
	{
		int value = player.getQuickVarI("SchemeBufferPet");
		return value > 0;
	}

	public static void showWindow(Player player)
	{
		if (!checkConditions(player))
			return;

		String msg;

		if (!ENABLE_VIP_BUFFER || (ENABLE_VIP_BUFFER && (player.getAccessLevel() == VIP_ACCESS_LEVEL)))
		{
			msg = main(player);
		}
		else
		{
			msg = showText(player, "Sorry", "This buffer is only for VIP's!<br>Contact the administrator for more " +
					"info!", false, "Return", "main");
		}
		showCommunity(player, msg);
	}

	private static boolean checkConditions(Player player)
	{
		String msg = null;
		int playerReflectionId = player.getReflection().getInstancedZoneId();
		if (playerReflectionId != ReflectionManager.DEFAULT.getId() && playerReflectionId != ReflectionManager.FIGHT_CLUB_REFLECTION_ID)
		{
			msg = showText(player, "Info", "You cannot buff outside default Instance!", false, "Return", "main");
		}
		else if (player.isInOlympiadMode() || Olympiad.isRegistered(player))
		{
			msg = showText(player, "Info", "You cannot buff while being registered in Olympiad Competition!", false, "Return", "main");
		}
		else if (player.getPvpFlag() > 0 && !player.isInPeaceZone() || player.isInCombat())
		{
			msg = showText(player, "Info", "You cannot buff while being in combat!", false, "Return", "main");
		}
		else if (!BUFF_WITH_KARMA && (player.getKarma() > 0))
		{
			msg = showText(player, "Info", "You have too much <font color=FF0000>karma!</font><br>Come back,<br>when you don't have any karma!", false, "Return", "main");
		}
		else if (Olympiad.isRegistered(player))
		{
			msg = showText(player, "Info", "You can not buff while you are in <font color=FF0000>Olympiad!</font><br>Come back,<br>when you are out of the Olympiad.", false, "Return", "main");
		}
		else if (player.getLevel() < MIN_LEVEL)
		{
			msg = showText(player, "Info", "Your level is too low!<br>You have to be at least level <font color=LEVEL>" + MIN_LEVEL + "</font>,<br>to use my services!", false, "Return", "main");
		}
		else if ((player.getPvpFlag() > 0) && !Config.SCHEME_ALLOW_FLAG)
		{
			msg = showText(player, "Info", "You can't buff while you are <font color=800080>flagged!</font><br>Wait some time and try again!", false, "Return", "main");
		}
		else if (player.isInCombat() && !Config.SCHEME_ALLOW_FLAG)
		{
			msg = showText(player, "Info", "You can't buff while you are attacking!<br>Stop your fight and try again!", false, "Return", "main");
		}
		// Synerge - Block time that the player cannot use the community buffer
		else if (player.getResurrectionBuffBlockedTime() > System.currentTimeMillis())
		{
			msg = showText(player, "Info", "You must wait 10 seconds after being resurrected to use the buffer!", false, "Return", "main");
		}

		if (msg == null)
		{
			return true;
		}
		else
		{
			showCommunity(player, msg);
			return false;
		}
	}

	private static String main(final Player player)
	{
		StringBuilder mainBuilder = new StringBuilder();
		StringBuilder builder = new StringBuilder();

		mainBuilder.append("<html><head><title>").append(TITLE_NAME).append("</title></head><body><br><center><font name=\"hs12\" color=ae9977>Target to Buff</font>");

		int td = 0;
		String[] TRS =
				{
						"<tr><td height=25>",
						"</td>",
						"<td height=25>",
						"</td></tr>"
				};

		final String bottonA, bottonB, bottonC;
		if (isPetBuff(player))
		{
			bottonA = "Auto Buff Pet";
			bottonB = "Heal My Pet";
			bottonC = "Remove Pet Buffs";
			mainBuilder.append("<button value=\"Buffing Pet\" action=\"bypass _bbsbufferbypass_buffpet 0 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Fight3None_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Fight3None\">");
		}
		else
		{
			bottonA = "Auto Buff";
			bottonB = "Heal";
			bottonC = "Remove Buffs";
			mainBuilder.append("<button value=\"Buffing Player\" action=\"bypass _bbsbufferbypass_buffpet 1 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Apply_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Apply\">");
		}

		if (ENABLE_BUFF_SECTION)
		{
			if (ENABLE_BUFFS)
			{
				if (td > 2)
				{
					td = 0;
				}
				builder.append(TRS[td]).append("<button value=\"Buffs\" action=\"bypass _bbsbufferbypass_redirect view_buffs 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
				td += 2;
			}
			if (ENABLE_RESIST)
			{
				if (td > 2)
				{
					td = 0;
				}
				builder.append(TRS[td]).append("<button value=\"Resist\" action=\"bypass _bbsbufferbypass_redirect view_resists 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
				td += 2;
			}
			if (ENABLE_SONGS)
			{
				if (td > 2)
				{
					td = 0;
				}
				builder.append(TRS[td]).append("<button value=\"Songs\" action=\"bypass _bbsbufferbypass_redirect view_songs 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
				td += 2;
			}
			if (ENABLE_DANCES)
			{
				if (td > 2)
				{
					td = 0;
				}
				builder.append(TRS[td]).append("<button value=\"Dances\" action=\"bypass _bbsbufferbypass_redirect view_dances 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
				td += 2;
			}
			if (ENABLE_CHANTS)
			{
				if (td > 2)
				{
					td = 0;
				}
				builder.append(TRS[td]).append("<button value=\"Chants\" action=\"bypass _bbsbufferbypass_redirect view_chants 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
				td += 2;
			}
			if (ENABLE_SPECIAL)
			{
				if (td > 2)
				{
					td = 0;
				}
				builder.append(TRS[td]).append("<button value=\"Special\" action=\"bypass _bbsbufferbypass_redirect view_special 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
				td += 2;
			}
			if (ENABLE_OTHERS)
			{
				if (td > 2)
				{
					td = 0;
				}
				builder.append(TRS[td]).append("<button value=\"Others\" action=\"bypass _bbsbufferbypass_redirect view_others 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
				td += 2;
			}
		}

		if (ENABLE_CUBIC)
		{
			if (td > 2)
			{
				td = 0;
			}
			builder.append(TRS[td]).append("<button value=\"Cubics\" action=\"bypass _bbsbufferbypass_redirect view_cubic 0 0\" width=130 height=25 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
			td += 2;
		}

		if (builder.length() > 0)
		{
			mainBuilder.append("<BR1><br><font name=\"hs12\" color=ae9977>Single Buffs</font><BR1><table cellspacing=0 cellpadding=0>").append(builder.toString()).append("</table>");
			builder = new StringBuilder();
			td = 0;
		}

		if (ENABLE_BUFF_SET)
		{
			if (td > 2)
			{
				td = 0;
			}
			builder.append(TRS[td]).append("<button value=\"").append(bottonA).append("\" action=\"bypass _bbsbufferbypass_castBuffSet 0 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
			td += 2;
		}

		if (ENABLE_HEAL)
		{
			if (td > 2)
			{
				td = 0;
			}
			builder.append(TRS[td]).append("<button value=\"").append(bottonB).append("\" action=\"bypass _bbsbufferbypass_heal 0 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
			td += 2;
		}

		if (ENABLE_BUFF_REMOVE)
		{
			if (td > 2)
			{
				td = 0;
			}
			builder.append(TRS[td]).append("<button value=\"").append(bottonC).append("\" action=\"bypass _bbsbufferbypass_removeBuffs 0 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
			td += 2;
		}

		if (builder.length() > 0)
		{
			mainBuilder.append("<BR1><br><font name=\"hs12\" color=ae9977>Ready Options</font><BR1><table " +
					"cellspacing=0 cellpadding=0>").append(builder.toString()).append("</table>");
		}

		if (ENABLE_SCHEME_SYSTEM)
		{
			mainBuilder.append(generateScheme(player));
		}

		if (player.isGM())
		{
			mainBuilder.append("<br><button value=\"GM Manage Buffs\" action=\"bypass _bbsbufferbypass_redirect manage_buffs 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		mainBuilder.append("</center></html>");
		return mainBuilder.toString();
	}

	private static String viewAllSchemeBuffs(Player player, String scheme, String page, String action)
	{
		int schemeId = Integer.parseInt(scheme);
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>").append(TITLE_NAME).append("</title></head><body><br><center><br>");

		String[] eventSplit = viewAllSchemeBuffs$getBuffCount(player, schemeId).split(" ");
		int TOTAL_BUFF = Integer.parseInt(eventSplit[0]);
		int BUFF_COUNT = Integer.parseInt(eventSplit[1]);
		int DANCE_SONG = Integer.parseInt(eventSplit[2]);

		List<Integer> takenBuffs = new ArrayList<>();
		List<String> playerBuffList = new ArrayList<>();

		if (action.equals("add"))
		{
			builder.append("<font name=\"hs12\" color=ae9977>You can add <font color=LEVEL name=\"hs12\">").append(MAX_SCHEME_BUFFS - BUFF_COUNT).append("</font> Buffs and <font color=LEVEL name=\"hs12\">").append(MAX_SCHEME_DANCES - DANCE_SONG).append("</font> Dances more!</font>");

			for (SingleBuff singleBuff : allSingleBuffs)
			{
				String name = SkillTable.getInstance().getInfo(singleBuff.buffId, singleBuff.buffLevel).getName();
				name = name.replace(" ", "+");
				playerBuffList.add(name + "_" + singleBuff.buffId + "_" + singleBuff.buffLevel + "_" + singleBuff.buffType);
			}
			playerBuffList = removeNotNeededBuffs(playerBuffList, DANCE_SONG, MAX_SCHEME_DANCES, true);
			playerBuffList = removeNotNeededBuffs(playerBuffList, BUFF_COUNT, MAX_SCHEME_BUFFS, false);
		}

		if (action.equals("add"))
		{
			for (SchemeBuff buff : player.getBuffSchemeById(schemeId).schemeBuffs)
				takenBuffs.add(buff.skillId);
		}
		else if (action.equals("remove"))
		{
			builder.append("<font name=\"hs12\" color=ae9977>You have <font color=LEVEL>").append(BUFF_COUNT).append("</font> Buffs and <font color=LEVEL>").append(DANCE_SONG).append("</font> Dances</font>");

			for (SchemeBuff buff : player.getBuffSchemeById(schemeId).schemeBuffs)
			{
				String name = SkillTable.getInstance().getInfo(buff.skillId, buff.skillLevel).getName();
				name = name.replace(" ", "+");
				playerBuffList.add(name + "_" + buff.skillId + "_" + buff.skillLevel);
			}
		}

		builder.append("<BR1><table border=0><tr>");
		final int buffsPerPage = 20;
		final String width, pageName;
		int pc = ((playerBuffList.size() - 1) / buffsPerPage) + 1;
		if (pc > 5)
		{
			width = "25";
			pageName = "P";
		}
		else
		{
			width = "50";
			pageName = "Page ";
		}
		for (int ii = 1; ii <= pc; ++ii)
		{
			if (ii == Integer.parseInt(page))
			{
				builder.append("<td width=").append(width).append(" align=center><font color=LEVEL>").append(pageName).append(ii).append("</font></td>");
			}
			else if (action.equals("add"))
			{
				builder.append("<td width=").append(width).append("><button value=\"").append(pageName).append(ii).append("\" action=\"bypass _bbsbufferbypass_manage_scheme_1 ").append(scheme).append(" ").append(ii).append(" x\" width=").append(width).append(" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			}
			else if (action.equals("remove"))
			{
				builder.append("<td width=").append(width).append("><button value=\"").append(pageName).append(ii).append("\" action=\"bypass _bbsbufferbypass_manage_scheme_2 ").append(scheme).append(" ").append(ii).append(" x\" width=").append(width).append(" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			}
		}
		builder.append("</tr></table>");

		int limit = buffsPerPage * Integer.parseInt(page);
		int start = limit - buffsPerPage;
		int end = Math.min(limit, playerBuffList.size());
		int k = 0;
		for (int i = start; i < end; ++i)
		{
			String value = playerBuffList.get(i);
			value = value.replace("_", " ");
			String[] extr = value.split(" ");
			String name = extr[0];
			name = name.replace("+", " ");
			int id = Integer.parseInt(extr[1]);
			int level = Integer.parseInt(extr[2]);

			/*--String page = extr[3];--*/
			if (action.equals("add"))
			{
				if (!takenBuffs.contains(id))
				{
					if ((k % 2) != 0)
					{
						builder.append("<BR1><table border=0 bgcolor=333333>");
					}
					else
					{
						builder.append("<BR1><table border=0 bgcolor=292929>");
					}
					builder.append("<tr><td width=35>").append(getSkillIconHtml(id, level)).append("</td><td fixwidth=250>").append(name).append("</td><td><button value=\"Add\" action=\"bypass _bbsbufferbypass_add_buff ").append(scheme).append("_").append(id).append("_").append(level).append(" ").append(page).append(" ").append(TOTAL_BUFF).append("\" width=100 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table>");
					k += 1;
				}
			}
			else if (action.equals("remove"))
			{
				if ((k % 2) != 0)
				{
					builder.append("<BR1><table border=0 bgcolor=333333>");
				}
				else
				{
					builder.append("<BR1><table border=0 bgcolor=292929>");
				}
				builder.append("<tr><td width=35>").append(getSkillIconHtml(id, level)).append("</td><td fixwidth=170>").append(name).append("</td><td><button value=\"Remove\" action=\"bypass _bbsbufferbypass_remove_buff ").append(scheme).append("_").append(id).append("_").append(level).append(" ").append(page).append(" ").append(TOTAL_BUFF).append("\" width=65 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></table>");
				k += 1;
			}
		}
		builder.append("<br><br><button value=\"Back\" action=\"bypass _bbsbufferbypass_manage_scheme_select ").append(scheme).append(" x x\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"><button value=\"Home\" action=\"bypass _bbsbufferbypass_redirect main 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"></center>");
		return builder.toString();
	} // viewAllSchemeBuffs

	private static List<String> removeNotNeededBuffs(List<String> playerBuffList, int current, int max, boolean danceSong)
	{
		if (current >= max)
		{
			List<String> toDelete = new ArrayList<>();
			for (String buff : playerBuffList)
			{
				String[] changed = buff.split("_");
				boolean toRemove = false;
				if (changed[3].equals("dance") || changed[3].equals("song"))
				{
					if (danceSong)
						toRemove = true;
				}
				else
				{
					if (!danceSong)
						toRemove = true;
				}

				if (toRemove)
				{
					toDelete.add(buff);
				}
			}
			for (String buff : toDelete)
				playerBuffList.remove(buff);
		}
		return playerBuffList;
	}

	private static boolean canHeal(Player player)
	{
		if (player.isInFightClub() && player.getFightClubEvent().getState() != AbstractFightClub.EVENT_STATE.PREPARATION)
			return false;
		if (!player.isInFightClub() && (!checkConditions(player) || !player.isInPeaceZone()))
			return false;
		return true;
	}

	private static void heal(Player player, boolean isPet)
	{
		if (!canHeal(player))
			return;
		if (!isPet)
		{
			player.setCurrentHp(player.getMaxHp(), false);
			player.setCurrentMp(player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());
		}
		else if (player.getPet() != null)
		{
			Summon pet = player.getPet();
			pet.setCurrentHp(pet.getMaxHp(), false);
			pet.setCurrentMp(pet.getMaxMp());
			pet.setCurrentCp(pet.getMaxCp());
		}
	}

	private static String getSkillIconHtml(int id, int level)
	{
		String iconNumber = getSkillIconNumber(id, level);
		return "<img width=32 height=32 src=\"Icon.skill" + iconNumber + "\">";
	}

	private static String getSkillIconNumber(int id, int level)
	{
		String formato;
		if (id == 4)
		{
			formato = "0004";
		}
		else if ((id > 9) && (id < 100))
		{
			formato = "00" + id;
		}
		else if ((id > 99) && (id < 1000))
		{
			formato = "0" + id;
		}
		else if (id == 1517)
		{
			formato = "1536";
		}
		else if (id == 1518)
		{
			formato = "1537";
		}
		else if (id == 1547)
		{
			formato = "0065";
		}
		else if (id == 2076)
		{
			formato = "0195";
		}
		else if ((id > 4550) && (id < 4555))
		{
			formato = "5739";
		}
		else if ((id > 4698) && (id < 4701))
		{
			formato = "1331";
		}
		else if ((id > 4701) && (id < 4704))
		{
			formato = "1332";
		}
		else if (id == 6049)
		{
			formato = "0094";
		}
		else
		{
			formato = String.valueOf(id);
		}
		return formato;
	}

	private static String getDeleteSchemePage(Player player)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>").append(TITLE_NAME).append("</title></head><body><br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><font name=\"hs12\" color=ae9977>Available schemes:</font><br><br>");

		for (PlayerScheme scheme : player.getBuffSchemes())
		{
			builder.append("<button value=\"").append(scheme.schemeName).append("\" action=\"bypass _bbsbufferbypass_delete_c ").append(scheme.schemeId).append(" ").append(scheme.schemeName).append(" x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}

		builder.append("<br><button value=\"Back\" action=\"bypass _bbsbufferbypass_redirect main 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"></center>");
		return builder.toString();
	}

	private static String getItemNameHtml(Player st, int itemval)
	{
		return "&#" + itemval + ";";
	}

	private static String showText(Player st, String type, String text, boolean buttonEnabled, String buttonName, String location)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>").append(TITLE_NAME).append("</title></head><body><br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
		builder.append("<font color=LEVEL>").append(type).append("</font><br>").append(text).append("<br>");
		if (buttonEnabled)
		{
			builder.append("<button value=\"").append(buttonName).append("\" action=\"bypass _bbsbufferbypass_redirect ").append(location).append(" 0 0\" width=100 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		builder.append("</center></html>");
		st.sendPacket(new PlaySound("ItemSound3.sys_shortage"));
		return builder.toString();
	}

	private static String buildHtml(String buffType)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>").append(TITLE_NAME).append("</title></head><body><br><center><br>");

		Collection<String> availableBuffs = new ArrayList<>();

		for (SingleBuff buff : allSingleBuffs)
		{
			if (buff.buffType.equals(buffType))
			{
				String bName = SkillTable.getInstance().getInfo(buff.buffId, buff.buffLevel).getName();
				bName = bName.replace(" ", "+");
				availableBuffs.add(bName + "_" + buff.buffId + "_" + buff.buffLevel);
			}
		}

		if (availableBuffs.isEmpty())
		{
			builder.append("No buffs are available at this moment!");
		}
		else
		{
			builder.append("<font name=\"hs12\" color=ae9977>Buffer</font>");
			builder.append("<BR1><table width=650>");
			int index = 0;
			for (String buff : availableBuffs)
			{
				if (index % 2 == 0)
				{
					if (index > 0)
						builder.append("</tr>");
					builder.append("<tr>");
				}
				buff = buff.replace("_", " ");
				String[] buffSplit = buff.split(" ");
				String name = buffSplit[0];
				int id = Integer.parseInt(buffSplit[1]);
				int level = Integer.parseInt(buffSplit[2]);
				name = name.replace("+", " ");
				builder.append("<td><center><table><tr><td align=right>").append(getSkillIconHtml(id, level)).append("</td><td><button value=\"").append(name).append("\" action=\"bypass _bbsbufferbypass_giveBuffs ").append(id).append(" ").append(level).append(" ").append(buffType).append("\" width=190 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td align=left>").append(getSkillIconHtml(id, level)).append("</td></tr></table></td>");
				index++;
			}
			builder.append("</table>");
		}

		builder.append("<br><center><button value=\"Back\" action=\"bypass _bbsbufferbypass_redirect main 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"></center>");
		return builder.toString();
	}

	private static String getEditSchemePage(Player player)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>").append(TITLE_NAME).append("</title></head><body><br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><font name=\"hs12\" color=ae9977>Select a scheme that you would like to manage:</font><br><br>");

		for (PlayerScheme scheme : player.getBuffSchemes())
		{
			builder.append("<button value=\"").append(scheme.schemeName).append("\" action=\"bypass _bbsbufferbypass_manage_scheme_select ").append(scheme.schemeId).append(" x x\" width=200 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}

		builder.append("<br><button value=\"Back\" action=\"bypass _bbsbufferbypass_redirect main 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"></center>");
		return builder.toString();
	}

	private static String viewAllSchemeBuffs$getBuffCount(Player player, int schemeId)
	{
		int count = 0;
		int D_S_Count = 0;
		int B_Count = 0;

		for (SchemeBuff buff : player.getBuffSchemeById(schemeId).schemeBuffs)
		{
			++count;
			int val = buff.forClass;
			if ((val == 1) || (val == 2))
			{
				++D_S_Count;
			}
			else
			{
				++B_Count;
			}
		}

		return count + " " + B_Count + " " + D_S_Count;
	}

	private static int getBuffCount(Player player, int schemeId)
	{
		PlayerScheme scheme = player.getBuffSchemeById(schemeId);
		if (scheme != null)
			return scheme.schemeBuffs.size();

		return 0;
	}

	private static String getOptionList(Player player, int schemeId)
	{
		int bcount = getBuffCount(player, schemeId);
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>").append(TITLE_NAME).append("</title></head><body><br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><font name=\"hs12\" color=ae9977>There are <font color=LEVEL name=\"hs12\">").append(bcount).append("</font> buffs in current scheme!</font><br><br>");
		if (bcount < (MAX_SCHEME_BUFFS + MAX_SCHEME_DANCES))
		{
			builder.append("<button value=\"Add buffs\" action=\"bypass _bbsbufferbypass_manage_scheme_1 ").append(schemeId).append(" 1 x\" width=200 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		if (bcount > 0)
		{
			builder.append("<button value=\"Remove buffs\" action=\"bypass _bbsbufferbypass_manage_scheme_2 ").append(schemeId).append(" 1 x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		builder.append("<br><button value=\"Back\" action=\"bypass _bbsbufferbypass_edit_1 0 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"><button value=\"Home\" action=\"bypass _bbsbufferbypass_redirect main 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"></center>");
		return builder.toString();
	}

	private static String viewAllBuffTypes()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>").append(TITLE_NAME).append("</title></head><body><br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");
		builder.append("<font color=LEVEL>[Buff management]</font><br>");
		if (ENABLE_BUFFS)
		{
			builder.append("<button value=\"Buffs\" action=\"bypass _bbsbufferbypass_edit_buff_list buff Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		if (ENABLE_RESIST)
		{
			builder.append("<button value=\"Resist Buffs\" action=\"bypass _bbsbufferbypass_edit_buff_list resist Resists 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		if (ENABLE_SONGS)
		{
			builder.append("<button value=\"Songs\" action=\"bypass _bbsbufferbypass_edit_buff_list song Songs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		if (ENABLE_DANCES)
		{
			builder.append("<button value=\"Dances\" action=\"bypass _bbsbufferbypass_edit_buff_list dance Dances 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		if (ENABLE_CHANTS)
		{
			builder.append("<button value=\"Chants\" action=\"bypass _bbsbufferbypass_edit_buff_list chant Chants 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		if (ENABLE_SPECIAL)
		{
			builder.append("<button value=\"Special Buffs\" action=\"bypass _bbsbufferbypass_edit_buff_list special Special_Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		if (ENABLE_OTHERS)
		{
			builder.append("<button value=\"Others Buffs\" action=\"bypass _bbsbufferbypass_edit_buff_list others Others_Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		if (ENABLE_CUBIC)
		{
			builder.append("<button value=\"Cubics\" action=\"bypass _bbsbufferbypass_edit_buff_list cubic cubic_Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		}
		if (ENABLE_BUFF_SET)
		{
			builder.append("<button value=\"Buff Sets\" action=\"bypass _bbsbufferbypass_edit_buff_list set Buff_Sets 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>");
		}
		builder.append("<button value=\"Back\" action=\"bypass _bbsbufferbypass_redirect main 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"></center>");
		return builder.toString();
	}

	private static String createScheme()
	{
		return "<html><head><title>" + TITLE_NAME + "</title></head><body><br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br><font name=\"hs12\" color=ae9977>You MUST separate new words with a dot (.)<br><br>Scheme name: <edit var=\"name\" width=100><br><br><button value=\"Create Scheme\" action=\"bypass _bbsbufferbypass_create $name no_name x x\" width=200 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br><button value=\"Back\" action=\"bypass _bbsbufferbypass_redirect main 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"></center>";
	}

	private static Collection<String> generateQuery(int case1, int case2)
	{
		Collection<String> buffTypes = new ArrayList<>();
		if (ENABLE_BUFFS)
		{
			if (case1 < MAX_SCHEME_BUFFS)
			{
				buffTypes.add("buff");
			}
		}
		if (ENABLE_RESIST)
		{
			if (case1 < MAX_SCHEME_BUFFS)
			{
				buffTypes.add("resist");
			}
		}
		if (ENABLE_SONGS)
		{
			if (case2 < MAX_SCHEME_DANCES)
			{
				buffTypes.add("song");
			}
		}
		if (ENABLE_DANCES)
		{
			if (case2 < MAX_SCHEME_DANCES)
			{
				buffTypes.add("dance");
			}
		}
		if (ENABLE_CHANTS)
		{
			if (case1 < MAX_SCHEME_BUFFS)
			{
				buffTypes.add("chant");
			}
		}
		if (ENABLE_OTHERS)
		{
			if (case1 < MAX_SCHEME_BUFFS)
			{
				buffTypes.add("others");
			}
		}
		if (ENABLE_SPECIAL)
		{
			if (case1 < MAX_SCHEME_BUFFS)
			{
				buffTypes.add("special");
			}
		}
		return buffTypes;
	}

	private static String generateScheme(Player player)
	{
		List<PlayerScheme> playerSchemes = player.getBuffSchemes();
		StringBuilder mainBuilder = new StringBuilder();

		mainBuilder.append("<BR1><br><font name=\"hs12\" color=ae9977>Scheme Buffs</font><BR1><table cellspacing=0 cellpadding=0>");
		if (playerSchemes.size() > 0)
		{
			StringBuilder builder = new StringBuilder();
			int td = 0;
			String[] TRS =
					{
							"<tr><td>",
							"</td>",
							"<td>",
							"</td></tr>"
					};
			for (int i = 0; i < playerSchemes.size(); ++i)
			{
				if (td > 2)
				{
					td = 0;
				}
				PlayerScheme scheme = playerSchemes.get(i);
				builder.append(TRS[td]).append("<button value=\"").append(scheme.schemeName).append("\" action=\"bypass _bbsbufferbypass_cast ").append(scheme.schemeId).append(" x x\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">").append(TRS[td + 1]);
				td += 2;
			}

			if (builder.length() > 0)
			{
				mainBuilder.append("<table>").append(builder.toString()).append("</table>");
			}
		}

		if (playerSchemes.size() < SCHEMES_PER_PLAYER)
		{
			mainBuilder.append("<BR1><table><tr><td><button value=\"Create\" action=\"bypass _bbsbufferbypass_create_1 x x x\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Fight1None_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Fight1None\"></td>");
		}
		else
		{
			mainBuilder.append("<BR1><table width=100><tr>");
		}

		if (playerSchemes.size() > 0)
		{
			mainBuilder.append("<td><button value=\"Edit\" action=\"bypass _bbsbufferbypass_edit_1 x x x\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Fight3None_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Fight3None\"></td><td><button value=\"Delete\" action=\"bypass _bbsbufferbypass_delete_1 x x x\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Info_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Info\"></td></tr></table>");
		}
		else
		{
			mainBuilder.append("</tr></table>");
		}
		return mainBuilder.toString();
	}

	private static String getBuffType(int id)
	{
		for (SingleBuff singleBuff : allSingleBuffs)
		{
			if (singleBuff.buffId == id)
			{
				return singleBuff.buffType;
			}
		}
		return "none";
	}

	private static boolean isEnabled(int id, int level)
	{
		for (SingleBuff singleBuff : allSingleBuffs)
		{
			if (singleBuff.buffId == id && singleBuff.buffLevel == level)
			{
				return true;
			}
		}

		return false;
	}

	private static int getClassBuff(int id)
	{
		for (SingleBuff singleBuff : allSingleBuffs)
		{
			if (singleBuff.buffId == id)
				return singleBuff.buffClass;
		}

		return 0;
	}

	private static String viewAllBuffs(String type, String typeName, String page)
	{
		List<String> buffList = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		builder.append("<html><head><title>").append(TITLE_NAME).append("</title></head><body><br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>");

		typeName = typeName.replace("_", " ");

		Collection<String> types;
		if (type.equals("set"))
			types = generateQuery(0, 0);
		else
		{
			types = new ArrayList<>();
			types.add(type);
		}


		for (SingleBuff singleBuff : allSingleBuffs)
		{
			if (types.contains(singleBuff.buffType))
			{
				String name = SkillTable.getInstance().getInfo(singleBuff.buffId, singleBuff.buffLevel).getName();
				name = name.replace(" ", "+");
				buffList.add(name + "_" + singleBuff.forClass + "_" + page + "_1_" + singleBuff.buffId + "_" + singleBuff.buffLevel);
			}
		}

		Collections.sort(buffList);
		builder.append("<font color=LEVEL>[Buff management - ").append(typeName).append(" - Page ").append(page).append("]</font><br><table border=0><tr>");
		final int buffsPerPage;
		if (type.equals("set"))
		{
			buffsPerPage = 12;
		}
		else
		{
			buffsPerPage = 20;
		}
		final String width, pageName;
		int pc = ((buffList.size() - 1) / buffsPerPage) + 1;
		if (pc > 5)
		{
			width = "25";
			pageName = "P";
		}
		else
		{
			width = "50";
			pageName = "Page ";
		}
		typeName = typeName.replace(" ", "_");
		for (int ii = 1; ii <= pc; ++ii)
		{
			if (ii == Integer.parseInt(page))
			{
				builder.append("<td width=").append(width).append(" align=center><font color=LEVEL>").append(pageName).append(ii).append("</font></td>");
			}
			else
			{
				builder.append("<td width=").append(width).append("><button value=\"").append(pageName).append(ii).append("\" action=\"bypass _bbsbufferbypass_edit_buff_list ").append(type).append(" ").append(typeName).append(" ").append(ii).append("\" width=").append(width).append(" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
			}
		}
		builder.append("</tr></table><br>");

		int limit = buffsPerPage * Integer.parseInt(page);
		int start = limit - buffsPerPage;
		int end = Math.min(limit, buffList.size());
		for (int i = start; i < end; ++i)
		{
			String value = buffList.get(i);
			value = value.replace("_", " ");
			String[] extr = value.split(" ");
			String name = extr[0];
			name = name.replace("+", " ");
			int forClass = Integer.parseInt(extr[1]);
			/* page = extr[2]; */
			int usable = Integer.parseInt(extr[3]);
			String skillPos = extr[4] + "_" + extr[5];
			if ((i % 2) != 0)
			{
				builder.append("<BR1><table border=0 bgcolor=333333>");
			}
			else
			{
				builder.append("<BR1><table border=0 bgcolor=292929>");
			}
			if (type.equals("set"))
			{
				String listOrder = null;
				if (forClass == 0)
				{
					listOrder = "List=\"" + SET_FIGHTER + ";" + SET_MAGE + ";" + SET_ALL + ";" + SET_NONE + ";\"";
				}
				else if (forClass == 1)
				{
					listOrder = "List=\"" + SET_MAGE + ";" + SET_FIGHTER + ";" + SET_ALL + ";" + SET_NONE + ";\"";
				}
				else if (forClass == 2)
				{
					listOrder = "List=\"" + SET_ALL + ";" + SET_FIGHTER + ";" + SET_MAGE + ";" + SET_NONE + ";\"";
				}
				else if (forClass == 3)
				{
					listOrder = "List=\"" + SET_NONE + ";" + SET_FIGHTER + ";" + SET_MAGE + ";" + SET_ALL + ";\"";
				}
				builder.append("<tr><td fixwidth=145>").append(name).append("</td><td width=70><combobox var=\"newSet").append(i).append("\" width=70 ").append(listOrder).append("></td><td width=50><button value=\"Update\" action=\"bypass _bbsbufferbypass_changeBuffSet ").append(skillPos).append(" $newSet").append(i).append(" ").append(page).append("\" width=50 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
			}
			else
			{
				builder.append("<tr><td fixwidth=170>").append(name).append("</td><td width=80>");
				if (usable == 1)
				{
					builder.append("<button value=\"Disable\" action=\"bypass _bbsbufferbypass_editSelectedBuff ").append(skillPos).append(" 0-").append(page).append(" ").append(type).append("\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
				}
				else if (usable == 0)
				{
					builder.append("<button value=\"Enable\" action=\"bypass _bbsbufferbypass_editSelectedBuff ").append(skillPos).append(" 1-").append(page).append(" ").append(type).append("\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
				}
			}
			builder.append("</table>");
		}
		builder.append("<br><br><button value=\"Back\" action=\"bypass _bbsbufferbypass_redirect manage_buffs 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"><button value=\"Home\" action=\"bypass _bbsbufferbypass_redirect main 0 0\" width=200 height=30 back=\"L2UI_ct1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Back\"></center>");
		return builder.toString();
	}

	public static void onBypass(final Player player, String command)
	{
		if (!checkConditions(player))
			return;

		String msg = null;

		String[] eventSplit = command.split(" ", 4);
		if (eventSplit.length < 4)
			return;

		// If buffs were not loaded, load them now
		if (!singleBuffsLoaded)
		{
			singleBuffsLoaded = true;
			loadSingleBuffs();
		}

		String eventParam0 = eventSplit[0];
		String eventParam1 = eventSplit[1];
		String eventParam2 = eventSplit[2];
		String eventParam3 = eventSplit[3];

		if (!eventParam0.equals("heal") && !canHeal(player) && !player.containsQuickVar("BackHpOn"))
		{
			player.addQuickVar("BackHpOn", true);
			Playable target = isPetBuff(player) ? player.getPet() : player;
			if (!isPetBuff(player))
				ThreadPoolManager.getInstance().schedule(new BackHp(target, target.getCurrentHp(), target.getCurrentMp(), target.getCurrentCp()), 250);
			if (player.getPet() != null)
				ThreadPoolManager.getInstance().schedule(new BackHp(player.getPet(), target.getCurrentHp(), target.getCurrentMp(), target.getCurrentCp()), 250);
		}

		if (!FREE_BUFFS)
		{
			if (player.getAdena() < SCHEME_BUFF_PRICE)
			{
				showCommunity(player, showText(player, "Sorry", "You don't have the enough adena, " +
						"you need at least " + SCHEME_BUFF_PRICE + "!", false, "0", "0"));
				return;
			}
		}

		if (eventParam0.equalsIgnoreCase("buffpet"))
		{
			setPetBuff(player, eventParam1);
			msg = main(player);
		}
		else if (eventParam0.equals("redirect"))
		{
			if (eventParam1.equals("main"))
			{
				msg = main(player);
			}
			else if (eventParam1.equals("manage_buffs"))
			{
				msg = viewAllBuffTypes();
			}
			else if (eventParam1.equals("view_buffs"))
			{
				msg = buildHtml("buff");
			}
			else if (eventParam1.equals("view_resists"))
			{
				msg = buildHtml("resist");
			}
			else if (eventParam1.equals("view_songs"))
			{
				msg = buildHtml("song");
			}
			else if (eventParam1.equals("view_dances"))
			{
				msg = buildHtml("dance");
			}
			else if (eventParam1.equals("view_chants"))
			{
				msg = buildHtml("chant");
			}
			else if (eventParam1.equals("view_others"))
			{
				msg = buildHtml("others");
			}
			else if (eventParam1.equals("view_special"))
			{
				msg = buildHtml("special");
			}
			else if (eventParam1.equals("view_cubic"))
			{
				msg = buildHtml("cubic");
			}
			else if (DEBUG)
			{
				throw new RuntimeException();
			}
		}
		else if (eventParam0.equalsIgnoreCase("edit_buff_list"))
		{
			msg = viewAllBuffs(eventParam1, eventParam2, eventParam3);
		}
		else if (eventParam0.equalsIgnoreCase("changeBuffSet"))
		{
			if (eventParam2.equals(SET_FIGHTER))
			{
				eventParam2 = "0";
			}
			else if (eventParam2.equals(SET_MAGE))
			{
				eventParam2 = "1";
			}
			else if (eventParam2.equals(SET_ALL))
			{
				eventParam2 = "2";
			}
			else if (eventParam2.equals(SET_NONE))
			{
				eventParam2 = "3";
			}
			else if (DEBUG)
			{
				throw new RuntimeException();
			}
		}
		else if (eventParam0.equalsIgnoreCase("editSelectedBuff"))
		{
			eventParam2 = eventParam2.replace("-", " ");
			String[] split = eventParam2.split(" ");
			String action = split[0];
			String page = split[1];
			String typeName = "";

			if (eventParam3.equalsIgnoreCase("buff"))
			{
				typeName = "Buffs";
			}
			else if (eventParam3.equalsIgnoreCase("resist"))
			{
				typeName = "Resists";
			}
			else if (eventParam3.equalsIgnoreCase("song"))
			{
				typeName = "Songs";
			}
			else if (eventParam3.equalsIgnoreCase("dance"))
			{
				typeName = "Dances";
			}
			else if (eventParam3.equalsIgnoreCase("chant"))
			{
				typeName = "Chants";
			}
			else if (eventParam3.equalsIgnoreCase("others"))
			{
				typeName = "Others_Buffs";
			}
			else if (eventParam3.equalsIgnoreCase("special"))
			{
				typeName = "Special_Buffs";
			}
			else if (eventParam3.equalsIgnoreCase("cubic"))
			{
				typeName = "Cubics";
			}
			else if (DEBUG)
			{
				throw new RuntimeException();
			}

			msg = viewAllBuffs(eventParam3, typeName, page);
		}
		else if (eventParam0.equalsIgnoreCase("giveBuffs"))
		{
			int cost = 0;
			if (eventParam3.equalsIgnoreCase("special"))
			{
				cost = BUFF_PRICE;
			}
			else if (eventParam3.equalsIgnoreCase("resist"))
			{
				cost = RESIST_PRICE;
			}
			else if (eventParam3.equalsIgnoreCase("song"))
			{
				cost = SONG_PRICE;
			}
			else if (eventParam3.equalsIgnoreCase("dance"))
			{
				cost = DANCE_PRICE;
			}
			else if (eventParam3.equalsIgnoreCase("chant"))
			{
				cost = CHANT_PRICE;
			}
			else if (eventParam3.equalsIgnoreCase("others"))
			{
				cost = OTHERS_PRICE;
			}
			else if (eventParam3.equalsIgnoreCase("others"))
			{
				cost = OTHERS_PRICE;
			}
			else if (eventParam3.equalsIgnoreCase("special"))
			{
				cost = SPECIAL_PRICE;
			}
			else if (eventParam3.equalsIgnoreCase("cubic"))
			{
				cost = CUBIC_PRICE;
			}
			else if (DEBUG)
			{
				throw new RuntimeException();
			}

			if (!FREE_BUFFS)
			{
				if (Functions.getItemCount(player, CONSUMABLE_ID) < cost)
				{
					msg = showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + cost + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0");
					return;
				}
			}
			if (!isEnabled(Integer.parseInt(eventParam1), Integer.parseInt(eventParam2)) || player.isBlocked())
				return;

			final boolean getpetbuff = isPetBuff(player);
			if (!getpetbuff)
			{
				if (eventParam3.equals("cubic"))
				{
					if (player.getCubics() != null)
					{
						for (EffectCubic cubic : player.getCubics())
						{
							cubic.exit();
							player.getCubic(cubic.getId()).exit();
						}
					}
					player.onMagicUseTimer(player, SkillTable.getInstance().getInfo(Integer.parseInt(eventParam1), Integer.parseInt(eventParam2)), false);
				}
				else
				{
					SkillTable.getInstance().getInfo(Integer.parseInt(eventParam1), Integer.parseInt(eventParam2)).getEffects(player, player, false, false);
				}
			}
			else
			{
				if (eventParam3.equals("cubic"))
				{
					if (player.getCubics() != null)
					{
						for (EffectCubic cubic : player.getCubics())
						{
							cubic.exit();
							player.getCubic(cubic.getId()).exit();
						}
					}
					player.onMagicUseTimer(player, SkillTable.getInstance().getInfo(Integer.parseInt(eventParam1), Integer.parseInt(eventParam2)), false);
				}
				else
				{
					if (player.getPet() != null)
					{
						SkillTable.getInstance().getInfo(Integer.parseInt(eventParam1), Integer.parseInt(eventParam2)).getEffects(player.getPet(), player.getPet(), false, false);
					}
					else
					{
						msg = showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main");
					}
				}
			}
			Functions.removeItem(player, CONSUMABLE_ID, cost, "Scheme Buffer");

			msg = buildHtml(eventParam3);
		}
		else if (eventParam0.equalsIgnoreCase("castBuffSet"))
		{
			if (player.isBlocked())
				return;
			if (!FREE_BUFFS)
			{
				if (Functions.getItemCount(player, CONSUMABLE_ID) < BUFF_SET_PRICE)
				{
					msg = showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + BUFF_SET_PRICE + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0");
					return;
				}
			}
			final List<int[]> buff_sets = new ArrayList<int[]>();
			final int player_class;
			if (player.isMageClass())
			{
				player_class = 1;
			}
			else
			{
				player_class = 0;
			}
			final boolean getpetbuff = isPetBuff(player);
			if (!getpetbuff)
			{
				int max = Math.max(2, player_class);
				int min = Math.min(2, player_class);
				for (SingleBuff buff : allSingleBuffs)
				{
					if (buff.forClass == player_class || buff.forClass == 2)
					{
						buff_sets.add(new int[] { buff.buffId, buff.buffLevel });
					}
				}

				ThreadPoolManager.getInstance().execute(new Runnable()
				{
					@Override
					public void run()
					{
						for (int[] i : buff_sets)
						{
							SkillTable.getInstance().getInfo(i[0], i[1]).getEffects(player, player, false, false);
							try
							{
								Thread.sleep(5);
								if (player.isBlocked())
									return;
							}
							catch (InterruptedException e)
							{}
						}
					}
				});
			}
			else
			{
				if (player.getPet() != null)
				{
					for (SingleBuff buff : allSingleBuffs)
					{
						if (buff.forClass == 0 || buff.forClass == 2)
						{
							buff_sets.add(new int[] { buff.buffId, buff.buffLevel });
						}
					}

					ThreadPoolManager.getInstance().execute(new Runnable()
					{
						@Override
						public void run()
						{
							for (int[] i : buff_sets)
							{
								SkillTable.getInstance().getInfo(i[0], i[1]).getEffects(player.getPet(), player.getPet(), false, false);
								try
								{
									Thread.sleep(5);
									if (player.isBlocked())
										return;
								}
								catch (InterruptedException e)
								{}
							}
						}
					});
				}
				else
				{
					msg = showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main");
				}
			}
			Functions.removeItem(player, CONSUMABLE_ID, BUFF_SET_PRICE, "Scheme Buffer");

			msg = main(player);
		}
		else if (eventParam0.equalsIgnoreCase("heal"))
		{
			if (Functions.getItemCount(player, CONSUMABLE_ID) < HEAL_PRICE)
			{
				msg = showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + HEAL_PRICE + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0");
				return;
			}
			final boolean getpetbuff = isPetBuff(player);
			if (getpetbuff)
			{
				if (player.getPet() != null)
				{
					heal(player, getpetbuff);
				}
				else
				{
					msg = showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main");
				}
			}
			else
			{
				heal(player, getpetbuff);
			}
			Functions.removeItem(player, CONSUMABLE_ID, HEAL_PRICE, "Scheme buffer");

			msg = main(player);
		}
		else if (eventParam0.equalsIgnoreCase("removeBuffs"))
		{
			if (Functions.getItemCount(player, CONSUMABLE_ID) < BUFF_REMOVE_PRICE)
			{
				msg = showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + BUFF_REMOVE_PRICE + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0");
				return;
			}
			final boolean getpetbuff = isPetBuff(player);
			if (getpetbuff)
			{
				if (player.getPet() != null)
				{
					player.getPet().getEffectList().stopAllEffects();
				}
				else
				{
					msg = showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main");
				}
			}
			else
			{
				player.getEffectList().stopAllEffects();
				if (player.getCubics() != null)
				{
					for (EffectCubic cubic : player.getCubics())
					{
						cubic.exit();
						player.getCubic(cubic.getId()).exit();
					}
				}
			}
			Functions.removeItem(player, CONSUMABLE_ID, BUFF_REMOVE_PRICE, "Scheme Buffer");

			msg = main(player);
		}
		else if (eventParam0.equalsIgnoreCase("cast"))
		{
			if (!FREE_BUFFS)
			{
				if (Functions.getItemCount(player, CONSUMABLE_ID) < SCHEME_BUFF_PRICE)
				{
					msg = showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + SCHEME_BUFF_PRICE + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0");
					return;
				}
			}

			final TIntArrayList buffs = new TIntArrayList();
			final TIntArrayList levels = new TIntArrayList();

			for (SchemeBuff buff : player.getBuffSchemeById(Integer.parseInt(eventParam1)).schemeBuffs)
			{
				int id = buff.skillId;
				int level = buff.skillLevel;
				switch (getBuffType(id))
				{
					case "buff":
						if (ENABLE_BUFFS)
						{
							if (isEnabled(id, level))
							{
								buffs.add(id);
								levels.add(level);
							}
						}
						break;
					case "resist":
						if (ENABLE_RESIST)
						{
							if (isEnabled(id, level))
							{
								buffs.add(id);
								levels.add(level);
							}
						}
						break;
					case "song":
						if (ENABLE_SONGS)
						{
							if (isEnabled(id, level))
							{
								buffs.add(id);
								levels.add(level);
							}
						}
						break;
					case "dance":
						if (ENABLE_DANCES)
						{
							if (isEnabled(id, level))
							{
								buffs.add(id);
								levels.add(level);
							}
						}
						break;
					case "chant":
						if (ENABLE_CHANTS)
						{
							if (isEnabled(id, level))
							{
								buffs.add(id);
								levels.add(level);
							}
						}
						break;
					case "others":
						if (ENABLE_OTHERS)
						{
							if (isEnabled(id, level))
							{
								buffs.add(id);
								levels.add(level);
							}
						}
						break;
					case "special":
						if (ENABLE_SPECIAL)
						{
							if (isEnabled(id, level))
							{
								buffs.add(id);
								levels.add(level);
							}
						}
						break;
				}
			}

			final boolean getpetbuff = isPetBuff(player);

			if (player.isBlocked())
				return;

			if (buffs.size() == 0)
			{
				msg = viewAllSchemeBuffs(player, eventParam1, "1", "add");
			}
			else if (getpetbuff && player.getPet() == null)
			{
				msg = showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main");
			}
			else
			{
				ThreadPoolManager.getInstance().execute(new Runnable() {
					@Override
					public void run()
					{
						for (int i = 0; i < buffs.size(); ++i)
						{
							if (!getpetbuff)
							{
								SkillTable.getInstance().getInfo(buffs.get(i), levels.get(i)).getEffects(player, player, false, false);
							}
							else
							{
								SkillTable.getInstance().getInfo(buffs.get(i), levels.get(i)).getEffects(player.getPet(), player.getPet(), false, false);
							}
							try
							{
								Thread.sleep(5);
								if (player.isBlocked())
									return;
							}
							catch (InterruptedException e)
							{}

						}
					}
				});
			}
			Functions.removeItem(player, CONSUMABLE_ID, SCHEME_BUFF_PRICE, "Scheme Buffer");

			msg = main(player);
		}
		else if (eventParam0.equalsIgnoreCase("manage_scheme_1"))
		{
			msg = viewAllSchemeBuffs(player, eventParam1, eventParam2, "add");
		}
		else if (eventParam0.equalsIgnoreCase("manage_scheme_2"))
		{
			msg = viewAllSchemeBuffs(player, eventParam1, eventParam2, "remove");
		}
		else if (eventParam0.equalsIgnoreCase("remove_buff"))
		{
			String[] split = eventParam1.split("_");
			String scheme = split[0];
			String skill = split[1];
			String level = split[2];

			try (Connection con = DatabaseFactory.getInstance().getConnection();
			    PreparedStatement statement = con.prepareStatement("DELETE FROM npcbuffer_scheme_contents WHERE scheme_id=? AND skill_id=? AND skill_level=? LIMIT 1"))
			{
				statement.setString(1, scheme);
				statement.setString(2, skill);
				statement.setString(3, level);
				statement.executeUpdate();
			}
			catch (SQLException e)
			{
				_log.error("Error while deleting Scheme Content", e);
			}
			int skillId = Integer.parseInt(skill);
			for (SchemeBuff buff : player.getBuffSchemeById(Integer.parseInt(scheme)).schemeBuffs)
				if (buff.skillId == skillId)
				{
					player.getBuffSchemeById(Integer.parseInt(scheme)).schemeBuffs.remove(buff);
					break;
				}

			int temp = Integer.parseInt(eventParam3) - 1;
			final String HTML;
			if (temp <= 0)
			{
				HTML = getOptionList(player, Integer.parseInt(scheme));
			}
			else
			{
				HTML = viewAllSchemeBuffs(player, scheme, eventParam2, "remove");
			}
			msg = HTML;
		}
		else if (eventParam0.equalsIgnoreCase("add_buff"))
		{
			String[] split = eventParam1.split("_");
			String scheme = split[0];
			String skill = split[1];
			String level = split[2];
			if (!isEnabled(Integer.parseInt(skill), Integer.parseInt(level)))
				return;
			int idbuffclass = getClassBuff(Integer.parseInt(skill));

			try (Connection con = DatabaseFactory.getInstance().getConnection();
			    PreparedStatement statement = con.prepareStatement("INSERT INTO npcbuffer_scheme_contents (scheme_id,skill_id,skill_level,buff_class) VALUES (?,?,?,?)"))
			{
				statement.setString(1, scheme);
				statement.setString(2, skill);
				statement.setString(3, level);
				statement.setInt(4, idbuffclass);
				statement.executeUpdate();
			}
			catch (SQLException e)
			{
				_log.error("Error while deleting Scheme Content", e);
			}

			player.getBuffSchemeById(Integer.parseInt(scheme)).schemeBuffs.add(new SchemeBuff(Integer.parseInt(skill), Integer.parseInt(level), idbuffclass));

			int temp = Integer.parseInt(eventParam3) + 1;
			final String HTML;
			if (temp >= (MAX_SCHEME_BUFFS + MAX_SCHEME_DANCES))
			{
				HTML = getOptionList(player, Integer.parseInt(scheme));
			}
			else
			{
				HTML = viewAllSchemeBuffs(player, scheme, eventParam2, "add");
			}
			msg = HTML;
		}
		else if (eventParam0.equalsIgnoreCase("create"))
		{
			String param = getCorrectName(eventParam1);
			if (param.isEmpty() || param.equals("no_name"))
			{
				player.sendPacket(new SystemMessage(SystemMsg.INCORRECT_NAME));
				msg = showText(player, "Info", "Please, enter the scheme name!", true, "Return", "main");
				showCommunity(player, msg);
				return;
			}

			try (Connection con = DatabaseFactory.getInstance().getConnection();
			    PreparedStatement statement = con.prepareStatement("INSERT INTO npcbuffer_scheme_list (player_id,scheme_name) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS))
			{
				statement.setInt(1, player.getObjectId());
				statement.setString(2, param);
				statement.executeUpdate();
				try (ResultSet rset = statement.getGeneratedKeys())
				{
					if (rset.next())
					{
						int id = rset.getInt(1);
						player.getBuffSchemes().add(new PlayerScheme(id, param));
					}
					else
					{
						_log.error("Couldn't get Generated Key while creating scheme!");
					}
				}
			}
			catch (SQLException e)
			{
				_log.error("Error while inserting Scheme List", e);
			}

			msg = main(player);
		}
		else if (eventParam0.equalsIgnoreCase("delete"))
		{

			try (Connection con = DatabaseFactory.getInstance().getConnection())
			{

				try (PreparedStatement statement = con.prepareStatement("DELETE FROM npcbuffer_scheme_list WHERE id=? LIMIT 1"))
				{
					statement.setString(1, eventParam1);
					statement.executeUpdate();
				}
				try (PreparedStatement statement = con.prepareStatement("DELETE FROM npcbuffer_scheme_contents WHERE scheme_id=?"))
				{
					statement.setString(1, eventParam1);
					statement.executeUpdate();
				}
			}
			catch (SQLException e)
			{
				_log.error("Error while deleting Scheme Content", e);
			}

			int realId = Integer.parseInt(eventParam1);
			for (PlayerScheme scheme : player.getBuffSchemes())
			{
				if (scheme.schemeId == realId)
				{
					player.getBuffSchemes().remove(scheme);
					break;
				}
			}

			msg = main(player);
		}
		else if (eventParam0.equalsIgnoreCase("delete_c"))
		{
			msg = "<html><head><title>" + TITLE_NAME + "</title></head><body><br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><font name=\"hs12\" color=ae9977>Do you really want to delete '" + eventParam2 + "' scheme?</font><br><br><button value=\"Yes\" action=\"bypass _bbsbufferbypass_delete " + eventParam1 + " x x\" width=50 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">" + "<button value=\"No\" action=\"bypass _bbsbufferbypass_delete_1 x x x\" width=50 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></body></html>";
		}
		else if (eventParam0.equalsIgnoreCase("create_1"))
		{
			msg = createScheme();
		}
		else if (eventParam0.equalsIgnoreCase("edit_1"))
		{
			msg = getEditSchemePage(player);
		}
		else if (eventParam0.equalsIgnoreCase("delete_1"))
		{
			msg = getDeleteSchemePage(player);
		}
		else if (eventParam0.equalsIgnoreCase("manage_scheme_select"))
		{
			msg = getOptionList(player, Integer.parseInt(eventParam1));
		}

		showCommunity(player, msg);
	}

	private static class BackHp implements Runnable
	{
		private final Playable playable;
		private final double hp;
		private final double mp;
		private final double cp;
		private BackHp(Playable playable, double hp, double mp, double cp)
		{
			this.playable = playable;
			this.hp = hp;
			this.mp = mp;
			this.cp = cp;
		}

		@Override
		public void run()
		{
			playable.getPlayer().deleteQuickVar("BackHpOn");
			playable.setCurrentHp(hp, false);
			playable.setCurrentMp(mp);
			playable.setCurrentCp(cp);
		}
	}

	private static void showCommunity(Player player, String text)
	{
		if (text != null)
			ShowBoard.separateAndSend(text, player);
	}

	private static String getCorrectName(String currentName)
	{
		StringBuilder newNameBuilder = new StringBuilder();
		char[] chars = currentName.toCharArray();
		for (char c : chars)
			if (isCharFine(c))
				newNameBuilder.append(c);
		return newNameBuilder.toString();
	}

	private static final char[] FINE_CHARS = {'1','2','3','4','5','6','7','8','9','0','q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l',
	'z','x','c','v','b','n','m','Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M',' '};

	private static boolean isCharFine(char c)
	{
		for (char fineChar : FINE_CHARS)
			if (fineChar == c)
				return true;
		return false;
	}
}
