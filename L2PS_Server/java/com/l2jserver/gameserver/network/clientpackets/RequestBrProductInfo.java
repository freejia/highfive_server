package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.datatables.PrimeShopTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public final class RequestBrProductInfo extends L2GameClientPacket
{
	private int _brId;
	
	@Override
	protected void readImpl()
	{
		this._brId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player != null)
		{
			PrimeShopTable.getInstance().showProductInfo(player, this._brId);
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:8B RequestBRProductInfo";
	}
}