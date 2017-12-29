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

import javolution.text.TextBuilder;

import com.l2jserver.Config;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;

/**
 * @RobikBobik
 */
public class ClassBBSManager extends BaseBBSManager
{
	private static ClassBBSManager _Instance = null;
	
	public static ClassBBSManager getInstance()
	{
		if (_Instance == null)
		{
			_Instance = new ClassBBSManager();
		}
		return _Instance;
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		ClassId classId = activeChar.getClassId();
		int jobLevel = classId.level();
		int level = activeChar.getLevel();
		TextBuilder html = new TextBuilder("");
		html.append("<br>");
		html.append("<center>");
		if (Config.ALLOW_CLASS_MASTERS_LISTCB.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LISTCB.contains(jobLevel))
		{
			jobLevel = 3;
		}
		if ((((level >= 20) && (jobLevel == 0)) || ((level >= 40) && (jobLevel == 1)) || ((level >= 76) && (jobLevel == 2))) && Config.ALLOW_CLASS_MASTERS_LISTCB.contains(jobLevel))
		{
			L2Item item = ItemTable.getInstance().getTemplate(Config.CLASS_MASTERS_PRICE_ITEMCB);
			html.append("Price for class is: <font color=\"LEVEL\">");
			html.append(Util.formatAdena(Config.CLASS_MASTERS_PRICE_LISTCB[jobLevel])).append("</font> <font color=\"LEVEL\">").append(item.getName()).append("</font> Really want one click class?<br>");
			for (ClassId cid : ClassId.values())
			{
				if (cid == ClassId.inspector)
				{
					continue;
				}
				if (cid.childOf(classId) && (cid.level() == (classId.level() + 1)))
				{
					html.append("<br><center><button value=\"").append(cid.name()).append("\" action=\"bypass -h _bbsclass;change_class;").append(cid.getId()).append(";").append(Config.CLASS_MASTERS_PRICE_LISTCB[jobLevel]).append("\" width=250 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
				}
			}
			html.append("</center>");
		}
		else
		{
			switch (jobLevel)
			{
				case 0:
					html.append("Hello " + activeChar.getName() + "! Your class is <font color=F2C202>" + activeChar.getClassId().name() + "</font>.<br>");
					html.append("Class level at: <font color=F2C202>20 level.</font><br>");
					html.append("Class level at: <font color=F2C202>40 level.</font><br>");
					html.append("Class level at:  <font color=F2C202>76 level.</font><br>");
					break;
				case 1:
					html.append("Congratulation " + activeChar.getName() + "! you are <font color=F2C202>" + activeChar.getClassId().name() + "</font>now.<br>");
					break;
				case 2:
					html.append("Hello " + activeChar.getName() + "! Your class is <font color=F2C202>" + activeChar.getClassId().name() + "</font>.<br>");
					html.append("Class level at: <font color=F2C202>20 level.</font><br>");
					html.append("Class level at:<font color=F2C202>40 level.</font><br>");
					html.append("Class level at: <font color=F2C202>76 level.</font><br>");
					break;
				case 3:
					html.append("Hello " + activeChar.getName() + "! Your class is <font color=F2C202>" + activeChar.getClassId().name() + "</font>.<br>");
					html.append("Congratulation !.<br>");
					if (level >= 76)
					{
						html.append("Your level is <font color=F2C202>76</font>! and Higher.<br>");
					}
					break;
			}
		}
		NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
		adminReply.setFile(activeChar.getLang(), "data/html/CommunityBoard/5.htm");
		adminReply.replace("%classmaster%", html.toString());
		separateAndSend(adminReply.getHtm(), activeChar);
		
		if (command.startsWith("_bbsclass;change_class;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			st.nextToken();
			short val = Short.parseShort(st.nextToken());
			int price = Integer.parseInt(st.nextToken());
			L2Item item = ItemTable.getInstance().getTemplate(Config.CLASS_MASTERS_PRICE_ITEMCB);
			L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getItemId());
			if ((pay != null) && (pay.getCount() >= price))
			{
				activeChar.destroyItem("ClassMaster", pay, price, activeChar, true);
				changeClass(activeChar, val);
				parsecmd("_bbsclass;", activeChar);
			}
			else if (Config.CLASS_MASTERS_PRICE_ITEMCB == 57)
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			}
			else
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
			}
		}
	}
	
	private void changeClass(L2PcInstance activeChar, short val)
	{
		if (activeChar.getClassId().level() == ClassId.values()[val].level())
		{
			return;
		}
		
		if (activeChar.getClassId().level() == 3)
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THIRD_CLASS_TRANSFER));
		}
		else
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLASS_TRANSFER));
		}
		activeChar.setClassId(val);
		
		if (activeChar.getClassId().level() == 2)
		{
			activeChar.getInventory().addItem("bbsClassManager", 6622, 1L, activeChar, null);
		}
		
		if (activeChar.isSubClassActive())
		{
			activeChar.getSubClasses().get(Integer.valueOf(activeChar.getClassIndex())).setClassId(activeChar.getActiveClass());
		}
		else
		{
			if (activeChar.getClassId().level() == 0)
			{
				activeChar.getInventory().addItem("bbsClassManager", 8869, 15L, activeChar, null);
			}
			else if (activeChar.getClassId().level() == 1)
			{
				activeChar.getInventory().addItem("bbsClassManager", 8870, 15L, activeChar, null);
			}
			
			activeChar.setBaseClass(activeChar.getActiveClass());
		}
		
		if (activeChar.getClassId().getId() == 97)
		{
			activeChar.getInventory().addItem("bbsClassManager", 15307, 1L, activeChar, null);
		}
		else if (activeChar.getClassId().getId() == 105)
		{
			activeChar.getInventory().addItem("bbsClassManager", 15308, 1L, activeChar, null);
		}
		else if (activeChar.getClassId().getId() == 112)
		{
			activeChar.getInventory().addItem("bbsClassManager", 15309, 4L, activeChar, null);
		}
		
		activeChar.broadcastUserInfo();
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
	}
}