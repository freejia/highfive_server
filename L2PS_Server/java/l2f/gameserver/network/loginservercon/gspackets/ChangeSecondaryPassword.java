package l2f.gameserver.network.loginservercon.gspackets;

import l2f.gameserver.network.loginservercon.SendablePacket;

public class ChangeSecondaryPassword extends SendablePacket
{
	private final String account;
	private final String newPass;

	public ChangeSecondaryPassword(String account, String newPass)
	{
		this.account = account;
		this.newPass = newPass;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x13);
		writeS(account);
		writeS(newPass);
	}
}