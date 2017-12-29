package l2f.gameserver.model.instances;

import l2f.gameserver.model.Player;
import l2f.gameserver.templates.npc.NpcTemplate;

@Deprecated
public class NoActionNpcInstance extends NpcInstance
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7909283397423330086L;

	public NoActionNpcInstance(final int objectID, final NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void onAction(final Player player, final boolean dontMove)
	{
		player.sendActionFailed();
	}
}
