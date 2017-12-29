package l2f.gameserver.model.entity.events.objects;

import l2f.gameserver.model.pledge.Clan;

public class AuctionSiegeClanObject extends SiegeClanObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8643534285816128794L;
	private long _bid;

	public AuctionSiegeClanObject(String type, Clan clan, long param)
	{
		this(type, clan, param, System.currentTimeMillis());
	}

	public AuctionSiegeClanObject(String type, Clan clan, long param, long date)
	{
		super(type, clan, param, date);
		_bid = param;
	}

	@Override
	public long getParam()
	{
		return _bid;
	}

	public void setParam(long param)
	{
		_bid = param;
	}
}
