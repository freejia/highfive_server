package l2f.gameserver.model.instances;

import l2f.gameserver.templates.npc.NpcTemplate;

public class SpecialMonsterInstance extends MonsterInstance
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6472112622662881599L;

	public SpecialMonsterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public boolean canChampion()
	{
		return false;
	}
}