package l2f.gameserver.network.clientpackets;

import l2f.gameserver.dao.MailDAO;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.mail.Mail;
import l2f.gameserver.network.serverpackets.ExReplySentPost;
import l2f.gameserver.network.serverpackets.ExShowSentPostList;

/**
 * Π—Π°ΠΏΡ€ΠΎΡ� ΠΈΠ½Ρ„ΠΎΡ€ΠΌΠ°Ρ†ΠΈΠΈ ΠΎΠ± ΠΎΡ‚ΠΏΡ€Π°Π²Π»ΠµΠ½Π½ΠΎΠΌ ΠΏΠΈΡ�Ρ�ΠΌΠµ. Π�ΠΎΡ�Π²Π»Ρ�ΠµΡ‚Ρ�Ρ� ΠΏΡ€ΠΈ Π½Π°Π¶Π°Ρ‚ΠΈΠΈ Π½Π° ΠΏΠΈΡ�Ρ�ΠΌΠΎ ΠΈΠ· Ρ�ΠΏΠΈΡ�ΠΊΠ° {@link ExShowSentPostList}.
 * Π’ ΠΎΡ‚Π²ΠµΡ‚ Ρ�Π»ΠµΡ‚Ρ�Ρ� {@link ExReplySentPost}.
 * @see RequestExRequestReceivedPost
 */
public class RequestExRequestSentPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		postId = readD(); // id ΠΏΠΈΡ�Ρ�ΠΌΠ°
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), postId);
		if(mail != null)
		{
			activeChar.sendPacket(new ExReplySentPost(mail));
			return;
		}

		activeChar.sendPacket(new ExShowSentPostList(activeChar));
	}
}