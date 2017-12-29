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

import javolution.text.TextBuilder;
import javolution.util.FastMap;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Masterio
 */
public class RankPvpSystemDeathMgr
{
	// private static Logger _log = Logger.getLogger(CustomPvpSystemDeathMgr.class.getName());
	
	private L2PcInstance _killer = null;
	private L2PcInstance _victim = null;
	
	// store killer data, (anti-change target protection).
	// data updating on every kill.
	private int _killerLevel = 0;
	private int _killerClassId = 0;
	private int _killerCurrentCP = 0;
	private int _killerCurrentHP = 0;
	private int _killerCurrentMP = 0;
	
	private int _killerMaxCP = 0;
	private int _killerMaxHP = 0;
	private int _killerMaxMP = 0;
	
	/** [slot_id, killer_item] */
	private FastMap<Integer, KillerItem> _killerItems = new FastMap<>();
	
	/**
	 * Always use this constructor as default!
	 * @param killer
	 * @param victim
	 */
	public RankPvpSystemDeathMgr(L2PcInstance killer, L2PcInstance victim)
	{
		
		_killer = killer;
		_victim = victim;
		
		_killerLevel = killer.getLevel();
		_killerClassId = killer.getClassId().getId();
		_killerCurrentCP = (int) killer.getCurrentCp();
		_killerCurrentHP = (int) killer.getCurrentHp();
		_killerCurrentMP = (int) killer.getCurrentMp();
		
		_killerMaxCP = killer.getMaxCp();
		_killerMaxHP = killer.getMaxHp();
		_killerMaxMP = killer.getMaxMp();
		
		// load item killer list:
		if ((_killer != null) && RankPvpSystemConfig.DEATH_MANAGER_SHOW_ITEMS_ENABLED)
		{
			_killerItems = getKillerItems();
		}
		
	}
	
	/**
	 * Send HTML response to dead player (victim).
	 */
	public void sendVictimResponse()
	{
		NpcHtmlMessage n = new NpcHtmlMessage(0);
		n.setHtml(victimHtmlResponse().toString());
		_victim.sendPacket(n);
	}
	
	private TextBuilder victimHtmlResponse()
	{
		
		TextBuilder tb = new TextBuilder();
		TextBuilder tb_weapon = new TextBuilder();
		TextBuilder tb_armor = new TextBuilder();
		TextBuilder tb_jewel = new TextBuilder();
		TextBuilder tb_other = new TextBuilder();
		
		tb.append("<html><title>" + _killer.getName() + " Equipment informations</title><body><center>");
		
		tb.append("<table width=270 border=0 cellspacing=0 cellpadding=2 bgcolor=000000>");
		tb.append("<tr><td width=270 height=18 align=center><font color=ae9977>Killer Name (lvl):</font> " + getKiller().getName() + " (" + _killerLevel + ")</td></tr>");
		tb.append("<tr><td width=270 height=18 align=center><font color=ae9977>Killer Class:</font> " + RankPvpSystemUtil.getClassName(_killerClassId) + "</td></tr>");
		tb.append("<tr><td FIXWIDTH=270 HEIGHT=4><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
		tb.append("</table>");
		
		tb.append("<table width=270 border=0 cellspacing=0 cellpadding=2 bgcolor=000000>");
		tb.append("<tr><td width=270 height=18 align=center><font color=ae9977>CP:</font> <font color=FFF000>" + _killerCurrentCP + " / " + _killerMaxCP + "</font></td></tr>");
		tb.append("<tr><td width=270 height=18 align=center><font color=ae9977>HP:</font> <font color=FF0000>" + _killerCurrentHP + " / " + _killerMaxHP + "</font><font color=ae9977>, MP:</font> <font color=2080D0>" + _killerCurrentMP + "/" + _killerMaxMP + "</font></td></tr>");
		tb.append("<tr><td FIXWIDTH=270 HEIGHT=4><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
		tb.append("<tr><td width=270 height=12></td></tr>");
		tb.append("</table>");
		
		// show item list:
		if (RankPvpSystemConfig.DEATH_MANAGER_SHOW_ITEMS_ENABLED)
		{
			
			tb.append("<table width=270 border=0 cellspacing=0 cellpadding=2 bgcolor=000000>");
			
			if (getKiller() != null)
			{
				
				// create groups headers:
				tb_weapon.append("<tr><td width=270 height=18 align=center><font color=2080D0>Weapon / Shield</font></td></tr>");
				tb_armor.append("<tr><td width=270 height=18 align=center><font color=2080D0>Armor</font></td></tr>");
				tb_jewel.append("<tr><td width=270 height=18 align=center><font color=2080D0>Jewellery</font></td></tr>");
				tb_other.append("<tr><td width=270 height=18 align=center><font color=2080D0>Other</font></td></tr>");
				
				// create group separator:
				tb_weapon.append("<tr><td FIXWIDTH=270 HEIGHT=3><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
				tb_armor.append("<tr><td FIXWIDTH=270 HEIGHT=3><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
				tb_jewel.append("<tr><td FIXWIDTH=270 HEIGHT=3><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
				tb_other.append("<tr><td FIXWIDTH=270 HEIGHT=3><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
				
				// add items to groups:
				for (FastMap.Entry<Integer, KillerItem> e = _killerItems.head(), end = _killerItems.tail(); (e = e.getNext()) != end;)
				{
					
					if (e.getValue()._group == 1)
					{
						
						tb_weapon.append("<tr><td width=270 height=16 align=center><font color=808080>" + e.getValue()._itemName + " (</font><font color=FF8000>+" + e.getValue()._itemEnchantLevel + "</font><font color=808080>)</font></td></tr>");
						tb_weapon.append("<tr><td width=270 HEIGHT=3><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
						
					}
					else if (e.getValue()._group == 2)
					{
						
						tb_armor.append("<tr><td width=270 height=16 align=center><font color=808080>" + e.getValue()._itemName + " (</font><font color=FF8000>+" + e.getValue()._itemEnchantLevel + "</font><font color=808080>)</font></td></tr>");
						tb_armor.append("<tr><td width=270 HEIGHT=3><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
						
					}
					else if (e.getValue()._group == 3)
					{
						
						tb_jewel.append("<tr><td width=270 height=16 align=center><font color=808080>" + e.getValue()._itemName + " (</font><font color=FF8000>+" + e.getValue()._itemEnchantLevel + "</font><font color=808080>)</font></td></tr>");
						tb_jewel.append("<tr><td width=270 HEIGHT=3><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
						
					}
					else
					{ // group 4
					
						tb_other.append("<tr><td width=270 height=16 align=center><font color=808080>" + e.getValue()._itemName + " (</font><font color=FF8000>+" + e.getValue()._itemEnchantLevel + "</font><font color=808080>)</font></td></tr>");
						tb_other.append("<tr><td width=270 HEIGHT=3><img src=\"L2UI.Squaregray\" width=\"270\" height=\"1\"></td></tr>");
						
					}
					
				}
				
				// add to head TB generated TB's:
				tb.append(tb_weapon);
				tb.append(tb_armor);
				tb.append(tb_jewel);
				tb.append(tb_other);
				
			}
			else
			{
				tb.append("<tr><td>I can't load Killer Data!</td></tr>");
			}
			
			tb.append("</table>");
		}
		
		// footer and back button:
		tb.append("<table border=0 cellspacing=0 cellpadding=0>");
		tb.append("<tr><td width=270 height=12 align=center><font color=808080>- killer state in kill moment -</font></td></tr>");
		tb.append("<tr><td width=270 height=12></td></tr>");
		tb.append("<tr><td width=270 align=center><button value=\"Back\" action=\"bypass -h _rps_info\"  width=" + RankPvpSystemConfig.BUTTON_W + " height=" + RankPvpSystemConfig.BUTTON_H + " back=\"" + RankPvpSystemConfig.BUTTON_DOWN + "\" fore=\"" + RankPvpSystemConfig.BUTTON_UP + "\"></td></tr>");
		tb.append("</table>");
		
		tb.append("</center></body></html>");
		return tb;
	}
	
	/**
	 * @return the _killer
	 */
	public L2PcInstance getKiller()
	{
		return _killer;
	}
	
	/**
	 * @param killer the killer to set
	 */
	public void setKiller(L2PcInstance killer)
	{
		this._killer = killer;
	}
	
	/**
	 * Returns killer items.
	 * @return
	 */
	private FastMap<Integer, KillerItem> getKillerItems()
	{
		
		FastMap<Integer, KillerItem> items = new FastMap<>();
		
		for (int j = 0; j < Inventory.PAPERDOLL_TOTALSLOTS; j++)
		{
			L2ItemInstance item = getKiller().getInventory().getPaperdollItem(j);
			
			if ((item != null) && ((j == Inventory.PAPERDOLL_LHAND) || (j == Inventory.PAPERDOLL_RHAND)))
			{
				items.put(j, new KillerItem(item, KillerItem.GROUP_WEAPON));
			}
			else if ((item != null) && ((j == Inventory.PAPERDOLL_HEAD) || (j == Inventory.PAPERDOLL_CHEST) || (j == Inventory.PAPERDOLL_GLOVES) || (j == Inventory.PAPERDOLL_LEGS) || (j == Inventory.PAPERDOLL_FEET)))
			{
				items.put(j, new KillerItem(item, KillerItem.GROUP_ARMOR));
			}
			else if ((item != null) && ((j == Inventory.PAPERDOLL_NECK) || (j == Inventory.PAPERDOLL_REAR) || (j == Inventory.PAPERDOLL_LEAR) || (j == Inventory.PAPERDOLL_RFINGER) || (j == Inventory.PAPERDOLL_LFINGER)))
			{
				items.put(j, new KillerItem(item, KillerItem.GROUP_JEWEL));
			}
			else if (item != null)
			{
				items.put(j, new KillerItem(item, KillerItem.GROUP_OTHER));
			}
			
		}
		
		return items;
		
	}
	
	class KillerItem
	{
		int _itemObjId = 0;
		String _itemName = null;
		int _itemEnchantLevel = 0;
		int _group = GROUP_OTHER;
		
		/* item groups */
		public static final int GROUP_WEAPON = 1;
		public static final int GROUP_ARMOR = 2;
		public static final int GROUP_JEWEL = 3;
		public static final int GROUP_OTHER = 4;
		
		public KillerItem(L2ItemInstance itemInstance, int group)
		{
			_itemObjId = itemInstance.getObjectId();
			_itemName = itemInstance.getItemName();
			_itemEnchantLevel = itemInstance.getEnchantLevel();
			_group = group;
		}
		
	}
}