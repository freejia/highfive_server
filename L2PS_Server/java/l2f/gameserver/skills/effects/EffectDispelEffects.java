package l2f.gameserver.skills.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import l2f.commons.util.Rnd;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Effect;
import l2f.gameserver.model.Skill;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.stats.Env;
import l2f.gameserver.stats.Stats;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Cancellation, Touch of Death, Insane Crusher, Banes, Infinity Spear effect
 */
public class EffectDispelEffects extends Effect
{
	private static final Pattern STACK_TYPE_SEPARATOR = Pattern.compile(";");

	private final String dispelType;
	private final int cancelRate;
	private final String[] stackTypes;
	private final int negateMin;
	private final int negateMax;

	/*
	 * cancelRate is skill dependant constant:
	 * Cancel - 25
	 * Touch of Death/Insane Crusher - 25
	 * Mage/Warrior Bane - 80
	 * Mass Mage/Warrior Bane - 40
	 * Infinity Spear - 10
	 */
	public EffectDispelEffects(Env env, EffectTemplate template)
	{
		super(env, template);
		dispelType = template.getParam().getString("dispelType", "");
		cancelRate = template.getParam().getInteger("cancelRate", 0);
		negateMin = template.getParam().getInteger("negateMin", 1);
		negateMax = template.getParam().getInteger("negateCount", 5);
		stackTypes = STACK_TYPE_SEPARATOR.split(template.getParam().getString("negateStackTypes", ""));
	}

	@Override
	public void onStart()
	{
		List<Effect> effectList = createEffectList();
		if (effectList.isEmpty())
			return;

		final boolean calcEachChance = dispelType.equalsIgnoreCase("cancellation");
		if (!calcEachChance && !Rnd.chance(cancelRate))
			return;

		effectList = effectList.subList(0, Math.min(effectList.size(), negateMax*2));

		int count = 0;
		for (Effect effect : effectList)
		{
			if (effect == null)
				continue;

			// Calculamos el exito del cancel sobre ese efecto
			if (calcEachChance && !calcCancelSuccess(_effector, _effected, effect, getSkill(), cancelRate))
				continue;

			effect.exit();
			_effected.sendPacket(new SystemMessage2(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(effect.getSkill().getId(), effect.getSkill().getLevel()));
			count++;

			if (negateMax > 0 && count >= negateMax)
				break;
		}
	}

	private List<Effect> createEffectList()
	{
		final List<Effect> musicList = new ArrayList<>();
		final List<Effect> buffList = new ArrayList<>();

		for (Effect e : _effected.getEffectList().getAllEffects())
		{
			switch (dispelType)
			{
				case "cancellation":
					if (!e.isOffensive() && !e.getSkill().isToggle() && e.isCancelable())
					{
						if (e.getSkill().isMusic())
							musicList.add(e);
						else
							buffList.add(e);
					}
					break;
				case "bane":
					if (!e.isOffensive() && (ArrayUtils.contains(stackTypes, e.getStackType()) || ArrayUtils.contains(stackTypes, e.getStackType2())) && e.isCancelable())
					{
						buffList.add(e);
					}
					break;
				case "cleanse":
					if (e.isOffensive() && e.isCancelable())
					{
						buffList.add(e);
					}
					break;
			}
		}

		List<Effect> effectList = new ArrayList<>();
		Collections.reverse(musicList);
		effectList.addAll(musicList);
		Collections.reverse(buffList);
		effectList.addAll(buffList);

		return effectList;
	}

	public static final boolean calcCancelSuccess(Creature activeChar, Creature target, Effect buff, Skill cancelSk, double dispelRate)
	{
		final int cancelLvl = cancelSk.getMagicLevel();
		double rate = dispelRate;
		double res = -target.calcStat(Stats.CANCEL_RESIST, 0, activeChar, cancelSk);

		if (res > 100)
			res = 100;
		else if (res < -100)
			res = -100;

		double resMod = 0;
		if (res != 0)
		{
			resMod = (res / 100) * dispelRate;
			rate += resMod;
		}

		rate += 2 * (cancelLvl - (buff.getSkill().getMagicLevel() + target.getLevel()) / 2);
		//rate -= buff.getDuration() / 1000 / 180;

		if (rate > 99)
			rate = 99;
		else if (rate < 1)
			rate = 1;

		return Rnd.chance(rate);
	}

	@Override
	protected boolean onActionTime()
	{
		return false;
	}
}
