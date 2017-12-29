package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.datatables.PrimeShopTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public final class RequestBrBuyProduct extends L2GameClientPacket
{
	private int _brId;
	private int _count;
	
	@Override
	protected void readImpl()
	{
		this._brId = readD();
		this._count = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player != null)
		{
			PrimeShopTable.getInstance().buyItem(player, this._brId, this._count);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:8C RequestBRBuyProduct";
	}
}
