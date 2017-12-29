package l2f.gameserver.network.clientpackets;

import l2f.gameserver.dao.MailDAO;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.World;
import l2f.gameserver.model.mail.Mail;
import l2f.gameserver.network.serverpackets.ExNoticePostArrived;
import l2f.gameserver.network.serverpackets.ExReplyReceivedPost;
import l2f.gameserver.network.serverpackets.ExShowReceivedPostList;
import l2f.gameserver.network.serverpackets.components.SystemMsg;

/**
 * Π¨Π»ΠµΡ‚Ρ�Ρ� ΠΊΠ»ΠΈΠµΠ½Ρ‚ΠΎΠΌ ΠΊΠ°ΠΊ Π·Π°ΠΏΡ€ΠΎΡ� Π½Π° ΠΎΡ‚ΠΊΠ°Π· ΠΏΡ€ΠΈΠ½Ρ�Ρ‚Ρ� ΠΏΠΈΡ�Ρ�ΠΌΠΎ ΠΈΠ· {@link ExReplyReceivedPost}. Π•Ρ�Π»ΠΈ ΠΊ ΠΏΠΈΡ�Ρ�ΠΌΡƒ ΠΏΡ€ΠΈΠ»ΠΎΠ¶ΠµΠ½Ρ‹ Π²ΠµΡ‰ΠΈ Ρ‚ΠΎ ΠΈΡ… Π½Π°Π΄ΠΎ Π²ΠµΡ€Π½ΡƒΡ‚Ρ� ΠΎΡ‚ΠΏΡ€Π°Π²ΠΈΡ‚ΠµΠ»Ρ�.
 */
public class RequestExRejectPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		postId = readD();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;
		
		if(activeChar.isActionsDisabled())
		{
			activeChar.sendActionFailed();
			return;
		}
		
		if(activeChar.isInStoreMode())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS);
			return;
		}

		if(activeChar.isInTrade())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE);
			return;
		}

		if(activeChar.getEnchantScroll() != null)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT);
			return;
		}

		if(!activeChar.isInPeaceZone())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_CANCEL_IN_A_NONPEACE_ZONE_LOCATION);
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
			return;
		}
		
		Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), postId);
		if(mail != null)
		{
			if(mail.getType() != Mail.SenderType.NORMAL || mail.getAttachments().isEmpty())
			{
				activeChar.sendActionFailed();
				return;
			}

			int expireTime = 360 * 3600 + (int) (System.currentTimeMillis() / 1000L); //TODO [G1ta0] хардкод времени актуальности почты

			Mail reject = mail.reject();
			mail.delete();
			reject.setExpireTime(expireTime);
			reject.save();

			Player sender = World.getPlayer(reject.getReceiverId());
			if(sender != null)
				sender.sendPacket(ExNoticePostArrived.STATIC_TRUE);
		}

		activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
	}
}