package l2f.gameserver.model.instances;

import l2f.gameserver.templates.npc.NpcTemplate;

public class NpcNotSayInstance extends NpcInstance
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7800444525821818993L;

	public NpcNotSayInstance(final int objectID, final NpcTemplate template)
	{
		super(objectID, template);
		setHasChatWindow(false);
	}
}
