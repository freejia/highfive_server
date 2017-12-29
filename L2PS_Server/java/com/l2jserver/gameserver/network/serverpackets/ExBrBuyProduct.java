package com.l2jserver.gameserver.network.serverpackets;


public class ExBrBuyProduct extends L2GameServerPacket
{
	private final int _reply;
	
	public ExBrBuyProduct(int reply)
	{
		this._reply = reply;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(254);
		writeH(216);
		writeD(this._reply);
	}
	
	public String getType()
	{
		return "[S] FE:D8 ExBRBuyProduct";
	}
}