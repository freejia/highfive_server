package l2f.gameserver.model.entity.events.objects;

public class CastleDamageZoneObject extends ZoneObject
{
	private static final long serialVersionUID = 4096875528408541341L;
	private final long _price;

	public CastleDamageZoneObject(String name, long price)
	{
		super(name);
		_price = price;
	}

	public long getPrice()
	{
		return _price;
	}
}
