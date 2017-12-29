package com.l2jserver.gameserver.network.serverpackets;

import com.l2jserver.gameserver.datatables.PrimeShopTable;

public class ExBrProductInfo extends L2GameServerPacket
{
	private final int _brId;
	private final int _price;
	private final int _cat;
	private final int _itemId;
	private final int _count;
	private final int _weight;
	private final int _tradable;
	
	public ExBrProductInfo(int brId, PrimeShopTable.PrimeShopItem item)
	{
		this._brId = brId;
		this._price = item.getPrimeItemPrice();
		this._cat = item.getPrimeItemCat();
		this._itemId = item.getPrimeItemId();
		this._count = item.getPrimeItemCount();
		this._weight = item.getPrimeWeight();
		this._tradable = item.getPrimeTradable();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(254);
		writeH(215);
		writeD(this._brId);
		writeD(this._price);
		writeD(this._cat);
		writeD(this._itemId);
		writeD(this._count);
		writeD(this._weight);
		writeD(this._tradable);
	}
	
	public String getType()
	{
		return "[S] FE:D7 ExBRProductInfo";
	}
}