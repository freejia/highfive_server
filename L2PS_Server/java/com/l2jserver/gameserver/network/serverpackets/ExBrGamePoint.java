package com.l2jserver.gameserver.network.serverpackets;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class ExBrGamePoint extends L2GameServerPacket
{
	private final int _charId;
	private final int _charPoints;
	
	public ExBrGamePoint(L2PcInstance player)
	{
		this._charId = player.getObjectId();
		this._charPoints = (int) player.getprime_points();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(254);
		writeH(213);
		writeD(this._charId);
		writeQ(this._charPoints);
		writeD(0);
	}
	
	public String getType()
	{
		return "[S] FE:D5 ExBRGamePoint";
	}
}