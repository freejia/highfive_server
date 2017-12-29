package com.l2jserver.gameserver.network.clientpackets;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExBrProductList;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;

public final class RequestBrProductList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance player = getClient().getActiveChar();
		if ((Config.DISABLED_FOR_NON_VIP) && (player.getPremiumService() == 0))
		{
			player.sendPacket(new ExShowScreenMessage("You are not vip, you cannot to use Prime Shop", 5000));
			return;
		}
		player.sendPacket(new ExBrProductList());
		_log.info("Vip player getting infor for prime shop");
	}
	
	@Override
	public String getType()
	{
		return "[C] D0:8A RequestBRProductList";
	}
}