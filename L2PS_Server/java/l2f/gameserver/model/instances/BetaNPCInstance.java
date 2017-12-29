package l2f.gameserver.model.instances;

import l2f.gameserver.model.Player;
import l2f.gameserver.templates.npc.NpcTemplate;

public class BetaNPCInstance extends MerchantInstance
{
	private static final long serialVersionUID = 7009993910138614514L;

	public BetaNPCInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		super.onBypassFeedback(player, command);
	}
	
	@Override
	public boolean isNpc()
	{
		return true;
	}
	
}