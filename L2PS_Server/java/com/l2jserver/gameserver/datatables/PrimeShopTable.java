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
package com.l2jserver.gameserver.datatables;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import javolution.util.FastMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.serverpackets.ExBrBuyProduct;
import com.l2jserver.gameserver.network.serverpackets.ExBrProductInfo;
import com.l2jserver.gameserver.util.Util;

public class PrimeShopTable
{
	private static final Logger _log = Logger.getLogger(PrimeShopTable.class.getName());
	
	@SuppressWarnings(
	{
		"unchecked",
		"rawtypes"
	})
	private final Map<Integer, PrimeShopItem> _primeItems = new FastMap();
	
	public final int BR_BUY_SUCCESS = 1;
	public final int BR_BUY_LACK_OF_POINT = -1;
	public final int BR_BUY_INVALID_PRODUCT = -2;
	public final int BR_BUY_USER_CANCEL = -3;
	public final int BR_BUY_INVENTROY_OVERFLOW = -4;
	public final int BR_BUY_CLOSED_PRODUCT = -5;
	public final int BR_BUY_SERVER_ERROR = -6;
	public final int BR_BUY_BEFORE_SALE_DATE = -7;
	public final int BR_BUY_AFTER_SALE_DATE = -8;
	public final int BR_BUY_INVALID_USER = -9;
	public final int BR_BUY_INVALID_ITEM = -10;
	public final int BR_BUY_INVALID_USER_STATE = -11;
	public final int BR_BUY_NOT_DAY_OF_WEEK = -12;
	public final int BR_BUY_NOT_TIME_OF_DAY = -13;
	public final int BR_BUY_SOLD_OUT = -14;
	
	public PrimeShopTable()
	{
		load();
	}
	
	private void load()
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File file = new File(Config.DATAPACK_ROOT, "data/xml/PrimeShop/PrimeShop.xml");
		Document doc = null;
		if (file.exists())
		{
			try
			{
				doc = factory.newDocumentBuilder().parse(file);
				if (doc == null)
				{
					_log.log(Level.WARNING, "Could not load PrimeShop.xml");
					return;
				}
				
				for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
				{
					if (!"primelist".equalsIgnoreCase(n.getNodeName()))
					{
						continue;
					}
					NamedNodeMap at = n.getAttributes();
					Node attribute = at.getNamedItem("enabled");
					if ((attribute != null) && (Boolean.parseBoolean(attribute.getNodeValue())))
					{
						for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
						{
							if (!"item".equalsIgnoreCase(d.getNodeName()))
							{
								continue;
							}
							NamedNodeMap attrs = d.getAttributes();
							
							int type = 0;
							
							Node att = attrs.getNamedItem("brId");
							if (att == null)
							{
								_log.severe("[PrimeShop] Missing brId, skipping");
							}
							else
							{
								int brId = Integer.parseInt(att.getNodeValue());
								
								att = attrs.getNamedItem("itemId");
								if (att == null)
								{
									_log.severe("[PrimeShop] Missing itemId, skipping");
								}
								else
								{
									int itemId = Integer.parseInt(att.getNodeValue());
									
									att = attrs.getNamedItem("cat");
									if (att == null)
									{
										_log.severe("[PrimeShop] Missing category, skipping");
									}
									else
									{
										int cat = Integer.parseInt(att.getNodeValue());
										
										att = attrs.getNamedItem("price");
										if (att == null)
										{
											_log.severe("[PrimeShop] Missing price, skipping");
										}
										else
										{
											int price = Integer.parseInt(att.getNodeValue());
											
											att = attrs.getNamedItem("count");
											if (att == null)
											{
												_log.severe("[PrimeShop] Missing count, skipping");
											}
											else
											{
												int count = Integer.parseInt(att.getNodeValue());
												if (count == 0)
												{
													_log.severe("[PrimeShop] Item with count=0, skipping");
												}
												else
												{
													count = Integer.parseInt(att.getNodeValue());
													
													att = attrs.getNamedItem("event");
													if (att == null)
													{
														_log.severe("[PrimeShop] Missing event, skipping");
													}
													else
													{
														boolean event = Boolean.parseBoolean(att.getNodeValue());
														
														att = attrs.getNamedItem("best");
														if (att == null)
														{
															_log.severe("[PrimeShop] Missing best, skipping");
														}
														else
														{
															boolean best = Boolean.parseBoolean(att.getNodeValue());
															if (event)
															{
																type++;
															}
															if (best)
															{
																type += 2;
															}
															L2Item item = ItemTable.getInstance().getTemplate(itemId);
															if (item == null)
															{
																_log.severe("[PrimeShop] Item template null");
															}
															else
															{
																this._primeItems.put(Integer.valueOf(brId), new PrimeShopItem(itemId, cat, price, count, item.getWeight(), item.isTradeable(), type));
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
					}
					else
					{
						_log.info("PrimeShop: disabled");
					}
				}
				
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Could not parse PrimeShop.xml file: " + e.getMessage(), e);
			}
			
			if (this._primeItems.size() > 0)
			{
				_log.info("PrimeShop: Loaded " + this._primeItems.size() + " items");
			}
		}
		else
		{
			_log.info("Failed loading PrimeShop.xml");
		}
	}
	
	public void buyItem(L2PcInstance player, int brId, int count)
	{
		if ((count >= 1) && (count <= 99))
		{
			if (this._primeItems.containsKey(Integer.valueOf(brId)))
			{
				PrimeShopItem item = this._primeItems.get(Integer.valueOf(brId));
				if (player.getprime_points() >= (item.getPrimeItemPrice() * count))
				{
					L2ItemInstance dummy = ItemTable.getInstance().createDummyItem(item.getPrimeItemId());
					if (dummy != null)
					{
						int weight = item.getPrimeItemCount() * item.getPrimeWeight() * count;
						
						if (player.getInventory().validateWeight(weight))
						{
							int slots = 0;
							if (dummy.isStackable())
							{
								slots = 1;
							}
							else
							{
								slots = item.getPrimeItemCount() * count;
							}
							if (player.getInventory().validateCapacity(slots))
							{
								if (player.addItem("PrimeShop", item.getPrimeItemId(), count, player, true) != null)
								{
									player.sendPacket(new ExBrBuyProduct(1));
									player.setprime_points(player.getprime_points() - (item.getPrimeItemPrice() * count));
								}
								else
								{
									player.sendPacket(new ExBrBuyProduct(-6));
								}
							}
							else
							{
								player.sendPacket(new ExBrBuyProduct(-4));
							}
						}
						else
						{
							player.sendPacket(new ExBrBuyProduct(-4));
						}
					}
					else
					{
						player.sendPacket(new ExBrBuyProduct(-6));
					}
				}
				else
				{
					player.sendPacket(new ExBrBuyProduct(-1));
				}
			}
			else
			{
				player.sendPacket(new ExBrBuyProduct(-2));
				Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid brId from Prime", Config.DEFAULT_PUNISH);
			}
		}
		else
		{
			Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to buy invalid itemcount [" + count + "] from Prime", Config.DEFAULT_PUNISH);
		}
	}
	
	public void showProductInfo(L2PcInstance player, int brId)
	{
		if ((player == null) || (brId == 0))
		{
			return;
		}
		if (this._primeItems.containsKey(Integer.valueOf(brId)))
		{
			PrimeShopItem item = this._primeItems.get(Integer.valueOf(brId));
			
			player.sendPacket(new ExBrProductInfo(brId, item));
		}
	}
	
	public Map<Integer, PrimeShopItem> getPrimeItems()
	{
		return this._primeItems;
	}
	
	public static PrimeShopTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PrimeShopTable _instance = new PrimeShopTable();
	}
	
	public class PrimeShopItem
	{
		int _itemId;
		int _category;
		int _price;
		int _count;
		int _weight;
		int _tradable;
		int _type;
		
		public PrimeShopItem(int itemId, int category, int price, int count, int weight, boolean tradable, int type)
		{
			this._itemId = itemId;
			this._category = category;
			this._price = price;
			this._weight = weight;
			this._count = count;
			this._tradable = (tradable ? 1 : 0);
			this._type = type;
		}
		
		public int getPrimeItemId()
		{
			return this._itemId;
		}
		
		public int getPrimeItemCat()
		{
			return this._category;
		}
		
		public int getPrimeItemPrice()
		{
			return this._price;
		}
		
		public int getPrimeItemCount()
		{
			return this._count;
		}
		
		public int getPrimeWeight()
		{
			return this._weight;
		}
		
		public int getPrimeTradable()
		{
			return this._tradable;
		}
		
		public int getPrimeType()
		{
			return this._type;
		}
	}
}