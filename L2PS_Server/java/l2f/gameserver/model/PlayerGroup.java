package l2f.gameserver.model;

import java.util.Iterator;

import l2f.commons.collections.EmptyIterator;
import l2f.gameserver.network.serverpackets.components.IStaticPacket;

public interface PlayerGroup extends Iterable<Player>
{
	public static final PlayerGroup EMPTY = new PlayerGroup()
	{
		@Override
		public void broadCast(IStaticPacket... packet)
		{

		}

		@Override
		public Iterator<Player> iterator()
		{
			return EmptyIterator.getInstance();
		}
	};

	void broadCast(IStaticPacket... packet);
}
