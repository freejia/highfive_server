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
 * this program. If not, see <http://l2jpsproject.eu/>.
 */
package com.l2jserver.gameserver.communitybbs.Managers;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import javolution.text.TextBuilder;

import com.l2jserver.Config;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @RobikBobik
 */
public class EnchantBBSManager extends BaseBBSManager
{
	private static Logger _log = Logger.getLogger(EnchantBBSManager.class.getName());
	
	private static EnchantBBSManager _Instance = null;
	
	public static EnchantBBSManager getInstance()
	{
		if (_Instance == null)
		{
			_Instance = new EnchantBBSManager();
		}
		return _Instance;
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		if (command.equals("_bbsechant"))
		{
			String name = "None Name";
			name = ItemTable.getInstance().getTemplate(Config.ENCHANT_ITEM).getName();
			TextBuilder sb = new TextBuilder();
			sb.append("<table width=350>");
			L2ItemInstance arr[] = activeChar.getInventory().getItems();
			int len = arr.length;
			for (int i = 0; i < len; i++)
			{
				
				L2ItemInstance _item = arr[i];
				
				if ((_item == null) || (_item.getItem() instanceof L2EtcItem) || !_item.isEquipped() || _item.isHeroItem() || (_item.getItem().getCrystalType() == L2Item.CRYSTAL_NONE) || ((_item.getItemId() >= 7816) && (_item.getItemId() <= 7831)) || _item.isShadowItem() || _item.isCommonItem() || _item.isWear() || (_item.getEnchantLevel() >= 26))
				{
					continue;
				}
				sb.append((new StringBuilder()).append("<tr><td><img src=icon.").append(ItemTableBBSManager.getIcon(_item.getItem().getItemId())).append(" width=32 height=32></td><td>").toString());
				sb.append((new StringBuilder()).append("<font color=\"LEVEL\">").append(_item.getItem().getName()).append(" ").append(_item.getEnchantLevel() <= 0 ? "" : (new StringBuilder()).append("</font><font color=3293F3>Current: +").append(_item.getEnchantLevel()).toString()).append("</font><br1>").toString());
				
				sb.append((new StringBuilder()).append("Consume: <font color=\"LEVEL\">").append(name).append("</font>").toString());
				sb.append("<img src=\"l2ui.squaregray\" width=\"170\" height=\"1\">");
				sb.append("</td><td>");
				sb.append((new StringBuilder()).append("<button value=\"Enchant\" action=\"bypass -h _bbsechant;enchlistpage;").append(_item.getObjectId()).append("\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">").toString());
				sb.append("</td></tr>");
				sb.append("<td>");
				sb.append((new StringBuilder()).append("<button value=\"Attribute\" action=\"bypass -h _bbsechant;enchlistpageAtrChus;").append(_item.getObjectId()).append("\" width=75 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">").toString());
				sb.append("</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(activeChar.getLang(), "data/html/CommunityBoard/36.htm");
			adminReply.replace("%enchanter%", sb.toString());
			separateAndSend(adminReply.getHtm(), activeChar);
		}
		if (command.startsWith("_bbsechant;enchlistpage;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int ItemForEchantObjID = Integer.parseInt(st.nextToken());
			int price = 0;
			String name = "None Name";
			name = ItemTable.getInstance().getTemplate(Config.ENCHANT_ITEM).getName();
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(ItemForEchantObjID);
			if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_D)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 2;
				}
				else
				{
					price = 1;
				}
			}
			else if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_C)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 2;
				}
				else
				{
					price = 1;
				}
			}
			else if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_B)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 2;
				}
				else
				{
					price = 1;
				}
			}
			else if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_A)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 2;
				}
				else
				{
					price = 1;
				}
			}
			else if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_S)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 2;
				}
				else
				{
					price = 1;
				}
			}
			else if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_S80)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 2;
				}
				else
				{
					price = 1;
				}
			}
			else if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_S84)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 2;
				}
				else
				{
					price = 1;
				}
			}
			TextBuilder sb = new TextBuilder();
			sb.append("IDK11:<br1><table width=400>");
			sb.append((new StringBuilder()).append("<tr><td width=32><img src=icon.").append(ItemTableBBSManager.getIcon(EhchantItem.getItem().getItemId())).append(" width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td><td width=236><center>").toString());
			sb.append((new StringBuilder()).append("<font color=\"LEVEL\">").append(EhchantItem.getItem().getName()).append(" ").append(EhchantItem.getEnchantLevel() <= 0 ? "" : (new StringBuilder()).append("</font><font color=3293F3>Current: +").append(EhchantItem.getEnchantLevel()).toString()).append("</font><br1>").toString());
			sb.append((new StringBuilder()).append("Consume: <font color=\"LEVEL\">").append(name).append("</font>").toString());
			sb.append("<img src=\"l2ui.squaregray\" width=\"236\" height=\"1\"><center></td>");
			sb.append((new StringBuilder()).append("<td width=32><img src=icon.").append(ItemTableBBSManager.getIcon(EhchantItem.getItem().getItemId())).append(" width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td>").toString());
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br1>");
			sb.append("<br1>");
			sb.append("<table border=0 width=400><tr><td width=200>");
			sb.append("<button value=\"next +5 (Price: " + (price * (price + 1)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;5;" + (price * (price + 1)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +6 (Price: " + (price * (price + 2)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;6;" + (price * (price + 2)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +7 (Price: " + (price * (price + 3)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;7;" + (price * (price + 3)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +8 (Price: " + (price * (price + 4)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;8;" + (price * (price + 4)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +9 (Price: " + (price * (price + 5)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;9;" + (price * (price + 5)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +10 (Price: " + (price * (price + 6)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;10;" + (price * (price + 6)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +11 (Price: " + (price * (price + 7)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;11;" + (price * (price + 7)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +12 (Price: " + (price * (price + 8)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;12;" + (price * (price + 8)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +13 (Price: " + (price * (price + 9)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;13;" + (price * (price + 9)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +14 (Price: " + (price * (price + 10)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;14;" + (price * (price + 10)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +15 (Price: " + (price * (price + 11)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;15;" + (price * (price + 11)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("</td><td width=200>");
			sb.append("<button value=\"next +16 (Price: " + (price * (price + 12)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;16;" + (price * (price + 12)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +17 (Price: " + (price * (price + 13)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;17;" + (price * (price + 13)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +18 (Price: " + (price * (price + 14)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;18;" + (price * (price + 14)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +19 (Price: " + (price * (price + 15)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;19;" + (price * (price + 15)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +20 (Price: " + (price * (price + 16)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;20;" + (price * (price + 16)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +21 (Price: " + (price * (price + 17)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;21;" + (price * (price + 17)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +22 (Price: " + (price * (price + 18)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;22;" + (price * (price + 18)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +23 (Price: " + (price * (price + 19)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;23;" + (price * (price + 19)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +24 (Price: " + (price * (price + 20)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;24;" + (price * (price + 20)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +25 (Price: " + (price * (price + 21)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;25;" + (price * (price + 21)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1>");
			sb.append("<button value=\"next +26 (Price: " + (price * (price + 22)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgo;26;" + (price * (price + 22)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("</td></tr></table><br1><button value=\"Back\" action=\"bypass -h _bbsechant\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(activeChar.getLang(), "data/html/CommunityBoard/36.htm");
			adminReply.replace("%enchanter%", sb.toString());
			separateAndSend(adminReply.getHtm(), activeChar);
		}
		if (command.startsWith("_bbsechant;enchlistpageAtrChus;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int ItemForEchantObjID = Integer.parseInt(st.nextToken());
			String name = "None Name";
			name = ItemTable.getInstance().getTemplate(Config.ENCHANT_ITEM).getName();
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(ItemForEchantObjID);
			
			TextBuilder sb = new TextBuilder();
			sb.append("Attributing:<br1><table width=300>");
			sb.append((new StringBuilder()).append("<tr><td width=32><img src=icon.").append(ItemTableBBSManager.getIcon(EhchantItem.getItem().getItemId())).append(" width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td><td width=236><center>").toString());
			sb.append((new StringBuilder()).append("<font color=\"LEVEL\">").append(EhchantItem.getItem().getName()).append(" ").append(EhchantItem.getEnchantLevel() <= 0 ? "" : (new StringBuilder()).append("</font><br1><font color=3293F3>Current: +").append(EhchantItem.getEnchantLevel()).toString()).append("</font><br1>").toString());
			
			sb.append((new StringBuilder()).append("Consume: <font color=\"LEVEL\">").append(name).append("</font>").toString());
			sb.append("<img src=\"l2ui.squaregray\" width=\"236\" height=\"1\"><center></td>");
			sb.append((new StringBuilder()).append("<td width=32><img src=icon.").append(ItemTableBBSManager.getIcon(EhchantItem.getItem().getItemId())).append(" width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td>").toString());
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br1>");
			sb.append("<br1>");
			sb.append("<table border=0 width=400><tr><td width=200>");
			sb.append("<center><img src=icon.etc_wind_stone_i00 width=32 height=32></center><br1>");
			sb.append("<button value=\"Wind \" action=\"bypass -h _bbsechant;enchlistpageAtr;2;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1><center><img src=icon.etc_earth_stone_i00 width=32 height=32></center><br1>");
			sb.append("<button value=\"Earth \" action=\"bypass -h _bbsechant;enchlistpageAtr;3;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1><center><img src=icon.etc_fire_stone_i00 width=32 height=32></center><br1>");
			sb.append("<button value=\"Fire \" action=\"bypass -h _bbsechant;enchlistpageAtr;0;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("</td><td width=200>");
			sb.append("<center><img src=icon.etc_water_stone_i00 width=32 height=32></center><br1>");
			sb.append("<button value=\"Water \" action=\"bypass -h _bbsechant;enchlistpageAtr;1;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1><center><img src=icon.etc_holy_stone_i00 width=32 height=32></center><br1>");
			sb.append("<button value=\"Divine \" action=\"bypass -h _bbsechant;enchlistpageAtr;4;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("<br1><center><img src=icon.etc_unholy_stone_i00 width=32 height=32></center><br1>");
			sb.append("<button value=\"Dark \" action=\"bypass -h _bbsechant;enchlistpageAtr;5;" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
			sb.append("</td></tr></table><br1><button value=\"Back\" action=\"bypass -h _bbsechant\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(activeChar.getLang(), "data/html/CommunityBoard/36.htm");
			adminReply.replace("%enchanter%", sb.toString());
			separateAndSend(adminReply.getHtm(), activeChar);
		}
		if (command.startsWith("_bbsechant;enchlistpageAtr;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int AtributType = Integer.parseInt(st.nextToken());
			int ItemForEchantObjID = Integer.parseInt(st.nextToken());
			int price = 0;
			String ElementName = "";
			
			if (AtributType == 0)
			{
				ElementName = "Fire";
			}
			else if (AtributType == 1)
			{
				ElementName = "Water";
			}
			else if (AtributType == 2)
			{
				ElementName = "Wind";
			}
			else if (AtributType == 3)
			{
				ElementName = "Earth";
			}
			else if (AtributType == 4)
			{
				ElementName = "Divine";
			}
			else if (AtributType == 5)
			{
				ElementName = "Dark";
			}
			
			String name = "None Name";
			name = ItemTable.getInstance().getTemplate(Config.ENCHANT_ITEM).getName();
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(ItemForEchantObjID);
			if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_S)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 2;
				}
				else
				{
					price = 1;
				}
			}
			else if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_S80)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 4;
				}
				else
				{
					price = 2;
				}
			}
			else if (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_S84)
			{
				if (EhchantItem.getItem().getType2() == L2Item.TYPE2_WEAPON)
				{
					price = 6;
				}
				else
				{
					price = 4;
				}
			}
			TextBuilder sb = new TextBuilder();
			sb.append("IDK: <font color=\"LEVEL\">" + ElementName + "</font><br1> IDK2:<br1><table width=300>");
			sb.append((new StringBuilder()).append("<tr><td width=32><img src=icon.").append(ItemTableBBSManager.getIcon(EhchantItem.getItem().getItemId())).append(" width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td><td width=236><center>").toString());
			sb.append((new StringBuilder()).append("<font color=\"LEVEL\">").append(EhchantItem.getItem().getName()).append(" ").append(EhchantItem.getEnchantLevel() <= 0 ? "" : (new StringBuilder()).append("</font><br1><font color=3293F3>IDK4: +").append(EhchantItem.getEnchantLevel()).toString()).append("</font><br1>").toString());
			
			sb.append((new StringBuilder()).append("IDK3: <font color=\"LEVEL\">").append(name).append("</font>").toString());
			sb.append("<img src=\"l2ui.squaregray\" width=\"236\" height=\"1\"><center></td>");
			sb.append((new StringBuilder()).append("<td width=32><img src=icon.").append(ItemTableBBSManager.getIcon(EhchantItem.getItem().getItemId())).append(" width=32 height=32> <img src=\"l2ui.squaregray\" width=\"32\" height=\"1\"></td>").toString());
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<br1>");
			sb.append("<br1>");
			if ((EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_S) || (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_S80) || (EhchantItem.getItem().getCrystalType() == L2Item.CRYSTAL_S84))
			{
				sb.append("<table border=0 width=400><tr><td width=200>");
				sb.append("<button value=\"next +25 (Price: " + (price * (price + 1)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;25;" + AtributType + ";" + (price * (price + 1)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("<br1>");
				sb.append("<button value=\"next +50 (Price: " + (price * (price + 2)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;50;" + AtributType + ";" + (price * (price + 2)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("<br1>");
				sb.append("<button value=\"next +75 (Price: " + (price * (price + 3)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;75;" + AtributType + ";" + (price * (price + 3)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("<br1>");
				sb.append("<button value=\"next +100 (Price: " + (price * (price + 4)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;100;" + AtributType + ";" + (price * (price + 4)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("<br1>");
				sb.append("</td><td width=200>");
				sb.append("<button value=\"next +125 (Price: " + (price * (price + 5)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;125;" + AtributType + ";" + (price * (price + 5)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("<br1>");
				sb.append("<button value=\"next +150 (Price: " + (price * (price + 6)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;150;" + AtributType + ";" + (price * (price + 6)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("<br1>");
				sb.append("<button value=\"next +300 (Price: " + (price * (price + 7)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;300;" + AtributType + ";" + (price * (price + 7)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("<br1>");
				sb.append("<button value=\"next +450 (Price: " + (price * (price + 8)) + " " + name + ")\" action=\"bypass -h _bbsechant;enchantgoAtr;450;" + AtributType + ";" + (price * (price + 8)) + ";" + ItemForEchantObjID + "\" width=200 height=20 back=\"L2UI_CT1.Button_DF\" fore=\"L2UI_CT1.Button_DF\">");
				sb.append("</td></tr></table><br1>");
			}
			else
			{
				sb.append("<table border=0 width=400><tr><td width=200>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<center><font color=\"LEVEL\">idk!</font></center>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("<br1>");
				sb.append("</td></tr></table><br1>");
			}
			sb.append("<button value=\"Enchant\" action=\"bypass -h _bbsechant\" width=70 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(activeChar.getLang(), "data/html/CommunityBoard/36.htm");
			adminReply.replace("%enchanter%", sb.toString());
			separateAndSend(adminReply.getHtm(), activeChar);
		}
		if (command.startsWith("_bbsechant;enchantgo;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int EchantVal = Integer.parseInt(st.nextToken());
			int EchantPrice = Integer.parseInt(st.nextToken());
			int EchantObjID = Integer.parseInt(st.nextToken());
			L2Item item = ItemTable.getInstance().getTemplate(Config.ENCHANT_ITEM);
			L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(EchantObjID);
			
			if ((activeChar.isProcessingTransaction()) || (activeChar.getPrivateStoreType() != 0) || (activeChar.getActiveTradeList() != null))
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_ENCHANT_WHILE_STORE);
				return;
			}
			else if ((pay != null) && (pay.getCount() >= EchantPrice))
			{
				activeChar.destroyItem("Enchanting", pay, EchantPrice, activeChar, true);
				EhchantItem.setEnchantLevel(EchantVal);
				activeChar.getInventory().equipItem(EhchantItem);
				activeChar.broadcastUserInfo();
				activeChar.sendMessage("" + EhchantItem.getItem().getName() + " enchanted " + EchantVal + ".");
				parsecmd("_bbsechant", activeChar);
				
				_log.info("WMZSELLER: Item: " + EhchantItem + " Val: " + EchantVal + " Price: " + EchantPrice + " Player: " + activeChar.getName() + "");
			}
			else
			{
				activeChar.sendMessage("Incorrect items count!");
				return;
			}
			
		}
		if (command.startsWith("_bbsechant;enchantgoAtr;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			int EchantVal = Integer.parseInt(st.nextToken());
			int AtrType = Integer.parseInt(st.nextToken());
			int EchantPrice = Integer.parseInt(st.nextToken());
			int EchantObjID = Integer.parseInt(st.nextToken());
			L2Item item = ItemTable.getInstance().getTemplate(Config.ENCHANT_ITEM);
			L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
			L2ItemInstance EhchantItem = activeChar.getInventory().getItemByObjectId(EchantObjID);
			
			if ((activeChar.isProcessingTransaction()) || (activeChar.getPrivateStoreType() != 0) || (activeChar.getActiveTradeList() != null))
			{
				activeChar.sendPacket(SystemMessageId.CANNOT_ENCHANT_WHILE_STORE);
				return;
			}
			else if ((pay != null) && (pay.getCount() >= EchantPrice))
			{
				activeChar.destroyItem("Enchanting", pay, EchantPrice, activeChar, true);
				EhchantItem.setElementAttr((byte) AtrType, EchantVal);
				activeChar.broadcastUserInfo();
				activeChar.sendMessage("" + EhchantItem.getItem().getName() + ": enchanted - " + EchantVal + ".");
				parsecmd("_bbsechant", activeChar);
				
				_log.info("WMZSELLER: Item: " + EhchantItem + " Val: " + EchantVal + " Price: " + EchantPrice + " Player: " + activeChar.getName() + "");
			}
			else
			{
				activeChar.sendMessage("Incorrect items count!");
				return;
			}
		}
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
	}
}