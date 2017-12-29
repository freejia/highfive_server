package l2f.gameserver.network.clientpackets;

import l2f.gameserver.cache.ItemInfoCache;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.items.ItemInfo;
import l2f.gameserver.network.serverpackets.ActionFail;
import l2f.gameserver.network.serverpackets.ExRpItemLink;

public class RequestExRqItemLink extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		ItemInfo item;
		if ((item = ItemInfoCache.getInstance().get(_objectId)) == null)
		{
			if (_objectId >= 5000000 && _objectId < 6000000 && getClient().getActiveChar() != null)
			{
				Player player = getClient().getActiveChar();
				String varName = "DisabledAnnounce" + _objectId;
				if (!player.containsQuickVar(varName))
				{
					player.addQuickVar(varName, "true");
					player.sendMessage("Announcement Disabled!");
				}
			}
			sendPacket(ActionFail.STATIC);
		}
		else
			sendPacket(new ExRpItemLink(item));
	}
}