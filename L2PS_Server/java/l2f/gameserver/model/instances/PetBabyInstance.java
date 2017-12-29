package l2f.gameserver.model.instances;

import l2f.commons.threading.RunnableImpl;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.*;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.tables.PetDataTable;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public final class PetBabyInstance extends PetInstance
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 603852276024480069L;

	private static final Logger _log = LoggerFactory.getLogger(PetBabyInstance.class);

	private Future<?> _actionTask;
	private boolean _buffEnabled = true;

	public PetBabyInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control, int _currentLevel, long exp)
	{
		super(objectId, template, owner, control, _currentLevel, exp);
	}

	public PetBabyInstance(int objectId, NpcTemplate template, Player owner, ItemInstance control)
	{
		super(objectId, template, owner, control);
	}

	// heal
	private static final int HealTrick = 4717;
	private static final int GreaterHealTrick = 4718;
	private static final int GreaterHeal = 5195;
	private static final int BattleHeal = 5590;
	private static final int Recharge = 5200;

	class ActionTask extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			Skill skill = onActionTask();
			_actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), skill == null ? 1000 : skill.getHitTime() * 333 / Math.max(getMAtkSpd(), 1) - 100);
		}
	}

	public Skill[] getBuffs()
	{
		switch (getNpcId())
		{
			case PetDataTable.IMPROVED_BABY_COUGAR_ID:
				return COUGAR_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_BABY_BUFFALO_ID:
				return BUFFALO_BUFFS[getBuffLevel()];
			case PetDataTable.IMPROVED_BABY_KOOKABURRA_ID:
				return KOOKABURRA_BUFFS[getBuffLevel()];
			case PetDataTable.FAIRY_PRINCESS_ID://Princess Fay
				return FAIRY_PRINCESS_BUFFS[getBuffLevel()];
			case PetDataTable.SPIRIT_SHAMAN_ID://Spirit of the Mage
				return FAIRY_PRINCESS_BUFFS[getBuffLevel()];
			case PetDataTable.TOY_KNIGHT_ID://toy Knight
				return TOY_KNIGHT_BUFFS[getBuffLevel()];
			case PetDataTable.TURTLE_ASCETIC_ID://turtle
				return TURTLE_ASCETIC_BUFFS[getBuffLevel()];
			case PetDataTable.WHITE_WEASEL_ID://white Weasel
				return WHITE_WEASEL_BUFFS[getBuffLevel()];
			default:
				return Skill.EMPTY_ARRAY;
		}
	}

	public Skill onActionTask()
	{
		try
		{
			Player owner = getPlayer();
			if (!owner.isDead() && !owner.isInvul() && !isCastingNow())
			{
				if (getEffectList().getEffectsCountForSkill(5753) > 0) // Awakening
					return null;

				if (getEffectList().getEffectsCountForSkill(5771) > 0) // Buff Control
					return null;

				boolean improved = PetDataTable.isImprovedBabyPet(getNpcId());
				Skill skill = null;

				if (!Config.ALT_PET_HEAL_BATTLE_ONLY || owner.isInCombat())
				{
					// check treatment
					double curHp = owner.getCurrentHpPercents();
					if (curHp < 90 && Rnd.chance((100 - curHp) / 3))
						if (curHp < 33) // emergency situation, a strong healing
							skill = SkillTable.getInstance().getInfo(improved ? BattleHeal : GreaterHealTrick, getHealLevel());
						else if (getNpcId() != PetDataTable.IMPROVED_BABY_KOOKABURRA_ID || getNpcId() != PetDataTable.FAIRY_PRINCESS_ID 
								|| getNpcId() != PetDataTable.SPIRIT_SHAMAN_ID)
							skill = SkillTable.getInstance().getInfo(improved ? GreaterHeal : HealTrick, getHealLevel());

					// check RECHARGER
					if (skill == null && getNpcId() == PetDataTable.IMPROVED_BABY_KOOKABURRA_ID || getNpcId() != PetDataTable.FAIRY_PRINCESS_ID 
								|| getNpcId() != PetDataTable.SPIRIT_SHAMAN_ID) // RECHARGER only Kookaburra in combat mode
					{
						double curMp = owner.getCurrentMpPercents();
						if (curMp < 66 && Rnd.chance((100 - curMp) / 2))
							skill = SkillTable.getInstance().getInfo(Recharge, getRechargeLevel());
					}

					if (skill != null && skill.checkCondition(PetBabyInstance.this, owner, false, !isFollowMode(), true))
					{
						setTarget(owner);
						getAI().Cast(skill, owner, false, !isFollowMode());
						return skill;
					}
				}

				if (!improved || owner.isInOfflineMode() || owner.getEffectList().getEffectsCountForSkill(5771) > 0)
					return null;

				outer: for(Skill buff : getBuffs())
				{
					if (getCurrentMp() < buff.getMpConsume2())
						continue;

					for (Effect ef : owner.getEffectList().getAllEffects())
						if (checkEffect(ef, buff))
							continue outer;

					if (buff.checkCondition(this, owner, false, !isFollowMode(), true))
					{
						setTarget(owner);
						getAI().Cast(buff, owner, false, !isFollowMode());
						return buff;
					}
					return null;
				}
			}
		}
		catch (Throwable e)
		{
			_log.warn("Pet [#" + getNpcId() + "] a buff task error has occurred", e);
		}
		return null;
	}

	/**
	 * Возвращает true если эффект для скилла уже есть и заново накладывать не надо
	 */
	private boolean checkEffect(Effect ef, Skill skill)
	{
		if (ef == null || !ef.isInUse() || !EffectList.checkStackType(ef.getTemplate(), skill.getEffectTemplates()[0])) // there is no such skill
			return false;
		if (ef.getStackOrder() < skill.getEffectTemplates()[0]._stackOrder) // old weaker
			return false;
		if (ef.getTimeLeft() > 10) // old and not weaker still not the end - waiting
			return true;
		if (ef.getNext() != null) // old is not weaker but ends - check that there is recursion zasheduleno
			return checkEffect(ef.getNext(), skill);
		return false;
	}

	public synchronized void stopBuffTask()
	{
		if (_actionTask != null)
		{
			_actionTask.cancel(false);
			_actionTask = null;
		}
	}

	public synchronized void startBuffTask()
	{
		if (_actionTask != null)
			stopBuffTask();

		if (_actionTask == null && !isDead())
			_actionTask = ThreadPoolManager.getInstance().schedule(new ActionTask(), 5000);
	}

	public boolean isBuffEnabled()
	{
		return _buffEnabled;
	}

	public void triggerBuff()
	{
		_buffEnabled = !_buffEnabled;
	}

	@Override
	protected void onDeath(Creature killer)
	{
		stopBuffTask();
		super.onDeath(killer);
	}

	@Override
	public void doRevive()
	{
		super.doRevive();
		startBuffTask();
	}

	@Override
	public void unSummon()
	{
		stopBuffTask();
		super.unSummon();
	}

	public int getHealLevel()
	{
		return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 12), 1), 12);
	}

	public int getRechargeLevel()
	{
		return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 8), 1), 8);
	}

	public int getBuffLevel()
	{
		if (getNpcId() == PetDataTable.FAIRY_PRINCESS_ID)
			return Math.min(Math.max((getLevel() - getMinLevel()) / ((80 - getMinLevel()) / 3), 0), 3);
		return Math.min(Math.max((getLevel() - 55) / 5, 0), 3);
	}

	@Override
	public int getSoulshotConsumeCount()
	{
		return 1;
	}

	@Override
	public int getSpiritshotConsumeCount()
	{
		return 1;
	}

	private static final int Pet_Haste = 5186; // 1-2
	private static final int Pet_Vampiric_Rage = 5187; // 1-4
	@SuppressWarnings("unused")
	private static final int Pet_Regeneration = 5188; // 1-3
	private static final int Pet_Blessed_Body = 5189; // 1-6
	private static final int Pet_Blessed_Soul = 5190; // 1-6
	private static final int Pet_Guidance = 5191; // 1-3
	private static final int Pet_Wind_Walk = 5192; // 1-2
	private static final int Pet_Acumen = 5193; // 1-3
	private static final int Pet_Empower = 5194; // 1-3
	private static final int Pet_Concentration = 5201; // 1-3
	private static final int Pet_Might = 5586; // 1-3
	private static final int Pet_Shield = 5587; // 1-3
	private static final int Pet_Focus = 5588; // 1-3
	private static final int Pet_Death_Wisper = 5589; // 1-3
	private static final int Pet_Armor_Maintenance = 5988; // 1
	private static final int Pet_Weapon_Maintenance = 5987; // 1

	// debuff (unused)
	@SuppressWarnings("unused")
	private static final int WindShackle = 5196, Hex = 5197, Slow = 5198, CurseGloom = 5199;

	private static final Skill[][] TOY_KNIGHT_BUFFS = {
		{ SkillTable.getInstance().getInfo(Pet_Focus, 3), 
			SkillTable.getInstance().getInfo(Pet_Death_Wisper, 3) },
		{
			SkillTable.getInstance().getInfo(Pet_Focus, 3),
			SkillTable.getInstance().getInfo(Pet_Death_Wisper, 3),
			SkillTable.getInstance().getInfo(Pet_Shield, 3),
			SkillTable.getInstance().getInfo(Pet_Wind_Walk, 4) },
			{
				SkillTable.getInstance().getInfo(Pet_Focus, 3),
				SkillTable.getInstance().getInfo(Pet_Death_Wisper, 3),
				SkillTable.getInstance().getInfo(Pet_Shield, 3),
				SkillTable.getInstance().getInfo(Pet_Wind_Walk, 4),
				SkillTable.getInstance().getInfo(Pet_Vampiric_Rage, 4),
				SkillTable.getInstance().getInfo(Pet_Haste, 2) },
				{
					SkillTable.getInstance().getInfo(Pet_Focus, 3),
					SkillTable.getInstance().getInfo(Pet_Death_Wisper, 3),
					SkillTable.getInstance().getInfo(Pet_Shield, 3),
					SkillTable.getInstance().getInfo(Pet_Wind_Walk, 4),
					SkillTable.getInstance().getInfo(Pet_Vampiric_Rage, 4),
					SkillTable.getInstance().getInfo(Pet_Haste, 2),
					SkillTable.getInstance().getInfo(Pet_Might, 3),
					SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6) } };

	private static final Skill[][] WHITE_WEASEL_BUFFS = {
		{ SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6), 
			SkillTable.getInstance().getInfo(Pet_Wind_Walk, 2) },
		{
			SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
			SkillTable.getInstance().getInfo(Pet_Wind_Walk, 2),
			SkillTable.getInstance().getInfo(Pet_Death_Wisper, 3),
			SkillTable.getInstance().getInfo(Pet_Shield, 3) },
			{
				SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
				SkillTable.getInstance().getInfo(Pet_Wind_Walk, 2),
				SkillTable.getInstance().getInfo(Pet_Death_Wisper, 3),
				SkillTable.getInstance().getInfo(Pet_Shield, 3),
				SkillTable.getInstance().getInfo(Pet_Vampiric_Rage, 4),
				SkillTable.getInstance().getInfo(Pet_Focus, 3) },
				{
					SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
					SkillTable.getInstance().getInfo(Pet_Wind_Walk, 2),
					SkillTable.getInstance().getInfo(Pet_Death_Wisper, 3),
					SkillTable.getInstance().getInfo(Pet_Shield, 3),
					SkillTable.getInstance().getInfo(Pet_Vampiric_Rage, 4),
					SkillTable.getInstance().getInfo(Pet_Focus, 3),
					SkillTable.getInstance().getInfo(Pet_Haste, 2) } };

	private static final Skill[][] TURTLE_ASCETIC_BUFFS = {
		{ SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6), 
			SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6) },
		{
			SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
			SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6),
			SkillTable.getInstance().getInfo(Pet_Shield, 3),
			SkillTable.getInstance().getInfo(Pet_Wind_Walk, 2) },
			{
				SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
				SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6),
				SkillTable.getInstance().getInfo(Pet_Shield, 3),
				SkillTable.getInstance().getInfo(Pet_Wind_Walk, 2),
				SkillTable.getInstance().getInfo(Pet_Armor_Maintenance, 1),
				SkillTable.getInstance().getInfo(Pet_Weapon_Maintenance, 2) } };

	private static final Skill[][] COUGAR_BUFFS = {
		{ SkillTable.getInstance().getInfo(Pet_Empower, 3), SkillTable.getInstance().getInfo(Pet_Might, 3) },
		{
			SkillTable.getInstance().getInfo(Pet_Empower, 3),
			SkillTable.getInstance().getInfo(Pet_Might, 3),
			SkillTable.getInstance().getInfo(Pet_Shield, 3),
			SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6) },
			{
				SkillTable.getInstance().getInfo(Pet_Empower, 3),
				SkillTable.getInstance().getInfo(Pet_Might, 3),
				SkillTable.getInstance().getInfo(Pet_Shield, 3),
				SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
				SkillTable.getInstance().getInfo(Pet_Acumen, 3),
				SkillTable.getInstance().getInfo(Pet_Haste, 2) },
				{
					SkillTable.getInstance().getInfo(Pet_Empower, 3),
					SkillTable.getInstance().getInfo(Pet_Might, 3),
					SkillTable.getInstance().getInfo(Pet_Shield, 3),
					SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
					SkillTable.getInstance().getInfo(Pet_Acumen, 3),
					SkillTable.getInstance().getInfo(Pet_Haste, 2),
					SkillTable.getInstance().getInfo(Pet_Vampiric_Rage, 4),
					SkillTable.getInstance().getInfo(Pet_Focus, 3) } };

	private static final Skill[][] BUFFALO_BUFFS = {
		{ SkillTable.getInstance().getInfo(Pet_Might, 3), SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6) },
		{
			SkillTable.getInstance().getInfo(Pet_Might, 3),
			SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
			SkillTable.getInstance().getInfo(Pet_Shield, 3),
			SkillTable.getInstance().getInfo(Pet_Guidance, 3), },
			{
				SkillTable.getInstance().getInfo(Pet_Might, 3),
				SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
				SkillTable.getInstance().getInfo(Pet_Shield, 3),
				SkillTable.getInstance().getInfo(Pet_Guidance, 3),
				SkillTable.getInstance().getInfo(Pet_Vampiric_Rage, 4),
				SkillTable.getInstance().getInfo(Pet_Haste, 2) },
				{
					SkillTable.getInstance().getInfo(Pet_Might, 3),
					SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
					SkillTable.getInstance().getInfo(Pet_Shield, 3),
					SkillTable.getInstance().getInfo(Pet_Guidance, 3),
					SkillTable.getInstance().getInfo(Pet_Vampiric_Rage, 4),
					SkillTable.getInstance().getInfo(Pet_Haste, 2),
					SkillTable.getInstance().getInfo(Pet_Focus, 3),
					SkillTable.getInstance().getInfo(Pet_Death_Wisper, 3) } };

	private static final Skill[][] KOOKABURRA_BUFFS = {
		{ SkillTable.getInstance().getInfo(Pet_Empower, 3), SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6) },
		{
			SkillTable.getInstance().getInfo(Pet_Empower, 3),
			SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6),
			SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
			SkillTable.getInstance().getInfo(Pet_Shield, 3) },
			{
				SkillTable.getInstance().getInfo(Pet_Empower, 3),
				SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6),
				SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
				SkillTable.getInstance().getInfo(Pet_Shield, 3),
				SkillTable.getInstance().getInfo(Pet_Acumen, 3),
				SkillTable.getInstance().getInfo(Pet_Concentration, 6) },
				{
					SkillTable.getInstance().getInfo(Pet_Empower, 3),
					SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6),
					SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
					SkillTable.getInstance().getInfo(Pet_Shield, 3),
					SkillTable.getInstance().getInfo(Pet_Acumen, 3),
					SkillTable.getInstance().getInfo(Pet_Concentration, 6) } };

	private static final Skill[][] FAIRY_PRINCESS_BUFFS = {
		{ SkillTable.getInstance().getInfo(Pet_Empower, 3), SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6) },
		{
			SkillTable.getInstance().getInfo(Pet_Empower, 3),
			SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6),
			SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
			SkillTable.getInstance().getInfo(Pet_Shield, 3) },
			{
				SkillTable.getInstance().getInfo(Pet_Empower, 3),
				SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6),
				SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
				SkillTable.getInstance().getInfo(Pet_Shield, 3),
				SkillTable.getInstance().getInfo(Pet_Acumen, 3),
				SkillTable.getInstance().getInfo(Pet_Concentration, 6) },
				{
					SkillTable.getInstance().getInfo(Pet_Empower, 3),
					SkillTable.getInstance().getInfo(Pet_Blessed_Soul, 6),
					SkillTable.getInstance().getInfo(Pet_Blessed_Body, 6),
					SkillTable.getInstance().getInfo(Pet_Shield, 3),
					SkillTable.getInstance().getInfo(Pet_Acumen, 3),
					SkillTable.getInstance().getInfo(Pet_Concentration, 6) } };
}