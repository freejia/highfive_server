package com.l2jserver.gameserver.network.clientpackets;


public final class RequestBrRecentProductList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:8D RequestBRRecentProductList";
	}
}