package l2f.loginserver.gameservercon.gspackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import l2f.loginserver.database.L2DatabaseFactory;
import l2f.loginserver.gameservercon.ReceivablePacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeSecondaryPassword extends ReceivablePacket
{
	private static final Logger log = LoggerFactory.getLogger(ChangeSecondaryPassword.class);

	private String accountName;
	private String newPass;

	@Override
	protected void readImpl()
	{
		accountName = readS();
		newPass = readS();
	}

	@Override
	protected void runImpl()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("UPDATE accounts SET secondaryPassword=? WHERE login=?"))
		{
			statement.setString(1, newPass);
			statement.setString(2, accountName);
			statement.execute();
		}
		catch(SQLException e)
		{
			log.warn("Error ChangeSecondaryPassword ", e);
		}
	}
}