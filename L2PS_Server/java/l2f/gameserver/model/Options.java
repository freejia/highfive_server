/*
 * Copyright (C) 2004-2013 L2J Server
 *
 * This file is part of L2J Server.
 *
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.model;

import l2f.gameserver.stats.triggers.TriggerInfo;
import l2f.gameserver.stats.triggers.TriggerType;

/**
 * Holder para guardar cada opcion de augmentation
 *
 * @author Synerge
 */
public class Options
{
	private final int _id;
	private final String _augType;
	private final TriggerInfo _trigger;

	public enum AugmentationFilter
	{
		NONE,
		ACTIVE_SKILL,
		PASSIVE_SKILL,
		CHANCE_SKILL,
		STATS
	}

	public Options(int augId, String augType, int skillId, int skillLevel, TriggerType triggerType, double triggerChance)
	{
		_id = augId;
		_augType = augType;
		_trigger = new TriggerInfo(skillId, skillLevel, triggerType, triggerChance);
	}

	public int getAugmentationId()
	{
		return _id;
	}

	public String getAugType()
	{
		return _augType;
	}

	public TriggerInfo getTrigger()
	{
		return _trigger;
	}
}
