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

import javolution.util.FastMap;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Masterio
 */
public final class RankPvpSystemBBSHtm
{
	
	public static final String prepareHtmResponse(L2PcInstance activeChar, int page)
	{
		
		String file = null;
		
		file = getBody();
		
		if (file == null)
		{
			file = "<html><body><br><br><center>404 :File Not found!<br> (check file: data/html/CommunityBoard/rankpvpsystem/body.htm) </center></body></html>";
		}
		else
		{
			file = prepareHeaderName(page, file);
			
			if (RankPvpSystemConfig.RANKS_ENABLED)
			{
				file = file.replace("%button_1%", getNextButton(page));
				file = file.replace("%button_2%", getPreviousButton(page));
			}
			else
			{
				file = file.replace("%button_1%", "&nbsp;");
				file = file.replace("%button_2%", "&nbsp;");
			}
			
			file = prepareTopList(activeChar, page, file);
			
			file = file.replace("%refresh_time%", getRefreshTime());
			
		}
		
		return file;
	}
	
	private static final String prepareHeaderName(int page, String file)
	{
		
		if (!TopTable.getInstance().isUpdating())
		{
			if (page == 1)
			{
				return file.replace("%header%", "TOP 10 Rank Point Gatherers");
			}
			
			return file.replace("%header%", "TOP 10 Killers");
		}
		
		return file.replace("%header%", "TOP 10");
	}
	
	private static final String prepareTopList(L2PcInstance activeChar, int page, String file)
	{
		
		String list = "";
		
		if (!TopTable.getInstance().isUpdating())
		{
			
			boolean playerInfo = false;
			
			int pos = 0;
			
			FastMap<Integer, TopField> topTable = null;
			
			if (page == 1)
			{
				topTable = TopTable.getInstance().getTopGatherersTable();
			}
			else
			{
				topTable = TopTable.getInstance().getTopKillsTable();
			}
			
			if (topTable == null)
			{
				return file;
			}
			
			for (FastMap.Entry<Integer, TopField> e = topTable.head(), end = topTable.tail(); (e = e.getNext()) != end;)
			{
				pos++;
				
				if (activeChar.getObjectId() == e.getValue().getCharacterId())
				{
					if (pos <= 10)
					{
						// add row to the top 10 list for current player who is watching the list:
						list += prepareListItem(pos, e.getValue().getCharacterName(), e.getValue().getCharacterLevel(), RankPvpSystemUtil.getClassName(e.getValue().getCharacterBaseClassId()), e.getValue().getCharacterPoints(), "2080D0");
					}
					else
					{
						// add row under the top 10 list for current player who is watching the list:
						list += "<br>" + prepareListItem(pos, e.getValue().getCharacterName(), e.getValue().getCharacterLevel(), RankPvpSystemUtil.getClassName(e.getValue().getCharacterBaseClassId()), e.getValue().getCharacterPoints(), "2080D0");
					}
					
					playerInfo = true;
					
				}
				else if (pos <= 10)
				{
					// add row to list with player data:
					list += prepareListItem(pos, e.getValue().getCharacterName(), e.getValue().getCharacterLevel(), RankPvpSystemUtil.getClassName(e.getValue().getCharacterBaseClassId()), e.getValue().getCharacterPoints(), null);
				}
				
				if ((pos > 10) && playerInfo)
				{
					// if list complete:
					break;
				}
			}
			
			if (!playerInfo)
			{
				if (RankPvpSystemConfig.COMMUNITY_BOARD_TOP_LIST_IGNORE_TIME_LIMIT > 0)
				{
					file = file.replace("%message%", "You're out of 500, or you did not kill anyone or even killed more than " + Math.round((double) RankPvpSystemConfig.COMMUNITY_BOARD_TOP_LIST_IGNORE_TIME_LIMIT / (double) 86400000) + " days ago.");
				}
				else
				{
					file = file.replace("%message%", "You're out of 500, or you did not kill anyone.");
				}
				
			}
			else
			{
				file = file.replace("%message%", "&nbsp;");
			}
			
			if (list.equals(""))
			{
				list += "No one on TOP 10 list yet";
			}
			
			// add list header before item list:
			list = prepareListHead(page) + list;
			
		}
		else
		{ // if is updating:
		
			list = "<font color=FF8000>Updating... try again for few seconds</font><br><br>";
			
			if (page == 1)
			{
				list += "<button value=\"Refresh\" action=\"bypass _bbsrps:1\" width=" + RankPvpSystemConfig.BUTTON_W + " height=" + RankPvpSystemConfig.BUTTON_H + " back=\"" + RankPvpSystemConfig.BUTTON_DOWN + "\" fore=\"" + RankPvpSystemConfig.BUTTON_UP + "\">";
			}
			else
			{
				list += "<button value=\"Refresh\" action=\"bypass _bbsrps:0\" width=" + RankPvpSystemConfig.BUTTON_W + " height=" + RankPvpSystemConfig.BUTTON_H + " back=\"" + RankPvpSystemConfig.BUTTON_DOWN + "\" fore=\"" + RankPvpSystemConfig.BUTTON_UP + "\">";
			}
			
		}
		
		return file.replace("%list%", list);
		
	}
	
	private static final String prepareListHead(int page)
	{
		String item = getListHead();
		
		if (page == 1)
		{
			item = item.replace("%col5_name%", "Rank Point's");
		}
		else
		{
			item = item.replace("%col5_name%", "PvP Kill's");
		}
		
		return item;
	}
	
	/**
	 * If fontColor == null then function not use any colors.
	 * @param position
	 * @param playerName
	 * @param playerLevel
	 * @param playerClass
	 * @param col5Value
	 * @param fontColor - Example: "FF0000", "red", "FFFFFF", ...
	 * @return
	 */
	private static final String prepareListItem(int position, String playerName, int playerLevel, String playerClass, long col5Value, String fontColor)
	{
		
		String item = "";
		
		if (fontColor != null)
		{
			item += "<font color=" + fontColor + ">";
		}
		
		item += getListItem();
		
		item = item.replace("%position%", position + "");
		item = item.replace("%player_name%", playerName);
		item = item.replace("%player_level%", playerLevel + "");
		item = item.replace("%player_class%", playerClass);
		item = item.replace("%col5_value%", col5Value + "");
		
		if (fontColor != null)
		{
			item += "</font>";
		}
		
		return item;
		
	}
	
	private static final String getNextButton(int page)
	{
		if (!TopTable.getInstance().isUpdating())
		{
			if (page == 0)
			{
				return "<button value=\">>\" action=\"bypass _bbsrps:1\" width=" + RankPvpSystemConfig.BUTTON_W + " height=" + RankPvpSystemConfig.BUTTON_H + " back=\"" + RankPvpSystemConfig.BUTTON_DOWN + "\" fore=\"" + RankPvpSystemConfig.BUTTON_UP + "\">";
			}
			
			return "&nbsp;";
		}
		
		return "&nbsp;";
	}
	
	private static final String getPreviousButton(int page)
	{
		if (!TopTable.getInstance().isUpdating())
		{
			if (page == 1)
			{
				return "<button value=\"<<\" action=\"bypass _bbsrps:0\" width=" + RankPvpSystemConfig.BUTTON_W + " height=" + RankPvpSystemConfig.BUTTON_H + " back=\"" + RankPvpSystemConfig.BUTTON_DOWN + "\" fore=\"" + RankPvpSystemConfig.BUTTON_UP + "\">";
			}
			
			return "&nbsp;";
		}
		
		return "&nbsp;";
	}
	
	private static final String getRefreshTime()
	{
		
		long time = RankPvpSystemConfig.TOP_TABLE_UPDATE_INTERVAL;
		
		long timeHours = time / (60 * 60 * 1000);
		long timeMinutes = (time % (60 * 60 * 1000)) / (60 * 1000);
		
		String H = Long.toString(timeHours);
		String M = Long.toString(timeMinutes);
		
		if ((timeHours > 0) && (timeMinutes > 0))
		{
			return H + " " + (timeHours == 1 ? "hour" : "hours") + " and " + M + " " + (timeMinutes == 1 ? "minute" : "minutes");
		}
		else if ((timeHours == 0) && (timeMinutes > 0))
		{
			return M + " " + (timeMinutes == 1 ? "minute" : "minutes");
		}
		else if ((timeHours > 0) && (timeMinutes == 0))
		{
			return H + " " + (timeHours == 1 ? "hour" : "hours");
		}
		
		return "less than 1 minute";
	}
	
	private static final String getBody()
	{
		return HtmCache.getInstance().getHtm("AH", "data/html/CommunityBoard/rankpvpsystem/body.htm");
	}
	
	private static final String getListHead()
	{
		return HtmCache.getInstance().getHtm("AH", "data/html/CommunityBoard/rankpvpsystem/list_head.htm");
	}
	
	private static final String getListItem()
	{
		return HtmCache.getInstance().getHtm("AH", "data/html/CommunityBoard/rankpvpsystem/list_item.htm");
	}
	
}
