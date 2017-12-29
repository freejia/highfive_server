package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExBrGamePoint;

public final class RequestBrGamePoint extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if (player != null)
		{
			player.sendPacket(new ExBrGamePoint(player));
		}
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:89 RequestBRGamePoint";
	}
}
