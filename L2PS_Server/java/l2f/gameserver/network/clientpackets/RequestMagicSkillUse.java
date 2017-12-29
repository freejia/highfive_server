package l2f.gameserver.network.clientpackets;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.items.attachment.FlagItemAttachment;
import l2f.gameserver.network.serverpackets.ActionFail;
import l2f.gameserver.tables.SkillTable;

public class RequestMagicSkillUse extends L2GameClientPacket
{
	private Integer _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
 
	protected void readImpl()
	{
		_magicId = readD();
		_ctrlPressed = readD() != 0;
		_shiftPressed = readC() != 0;
	}
 
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
 
		if (activeChar == null)
		{
	        getClient().sendPacket(ActionFail.STATIC);
			return;
		}
 
		activeChar.setActive();
 
     if (activeChar.isOutOfControl())
     {
       activeChar.setMacroSkill(null);
       activeChar.sendActionFailed();
       return;
     }
 
     if (activeChar.getMacroSkill() != null) {
       this._magicId = Integer.valueOf(activeChar.getMacroSkill().getId());
     }
     Skill skill = SkillTable.getInstance().getInfo(this._magicId.intValue(), activeChar.getSkillLevel(this._magicId));
 
     if (activeChar.isPendingOlyEnd())
     {
       if ((skill != null) && (skill.isOffensive()))
       {
         activeChar.setMacroSkill(null);
         activeChar.sendActionFailed();
         return;
       }
     }
     if (skill != null)
     {
       if ((!skill.isActive()) && (!skill.isToggle()))
       {
         activeChar.setMacroSkill(null);
         return;
       }
 
       FlagItemAttachment attachment = activeChar.getActiveWeaponFlagAttachment();
       if ((attachment != null) && (!attachment.canCast(activeChar, skill)))
       {
         activeChar.setMacroSkill(null);
         activeChar.sendActionFailed();
         return;
       }
 
       if ((activeChar.getTransformation() != 0) && (!activeChar.getAllSkills().contains(skill)))
       {
         activeChar.setMacroSkill(null);
         return;
       }
 
       if ((skill.isToggle()) && 
         (activeChar.getEffectList().getEffectsBySkill(skill) != null))
       {
         activeChar.setMacroSkill(null);
         activeChar.getEffectList().stopEffect(skill.getId());
         activeChar.sendActionFailed();
         return;
       }
 
       Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());
 
       activeChar.setGroundSkillLoc(null);
 
       if (activeChar.getMacroSkill() != null)
       {
         if (skill.getReuseDelay(activeChar) < 9000L)
         {
           activeChar.setReuseDelay(skill.getReuseDelay(activeChar) - 3000L);
           activeChar.setMacroSkill(null);
         }
       }
       activeChar.getAI().Cast(skill, target, _ctrlPressed, _shiftPressed);
     }
     else
     {
       activeChar.setMacroSkill(null);
       activeChar.sendActionFailed();
     }
   }
 }