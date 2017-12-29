package l2f.gameserver.model.entity.CCPHelpers;

import l2f.gameserver.data.htm.HtmCache;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.loginservercon.AuthServerCommunication;
import l2f.gameserver.network.loginservercon.gspackets.ChangeSecondaryPassword;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class CCPSecondaryPassword
{
	private static final Logger _log = LoggerFactory.getLogger(CCPSecondaryPassword.class);

	public static void startSecondaryPasswordSetup(Player player, String text)
	{
		StringTokenizer st = new StringTokenizer(text, "|");
		String[] args = new String[st.countTokens()];
		for(int i = 0; i < args.length; i++)
			args[i] = st.nextToken().trim();

		String pageIndex = args[0].substring(args[0].length() - 1);

		if(pageIndex.equals("F"))
		{
			if(hasPassword(player))
				sendHtml(player, HtmCache.getInstance().getNotNull("command/cfgSPSecondaryChange.htm", player));
			else
				sendHtml(player, HtmCache.getInstance().getNotNull("command/cfgSPSecondarySet.htm", player));
			return;
		}
		if(args.length < 2)
		{
			player.sendMessage("Incorrect values!");
			return;
		}

		switch(pageIndex)
		{
			case "C":
				String currentPass = args[1];
				String newPass = args.length > 2 ? args[2] : "";

				if(getSecondaryPass(player).equals(currentPass))
				{
					setSecondaryPassword(player, player.getAccountName(), newPass);
				}
				else
				{
					player.kick();
				}
				break;
			case "S":
				try
				{
					check(player, args[1], args[2]);
				}
				catch(Exception e)
				{
					player.sendMessage("Set new password!");
				}
				break;
		}

	}

	public static void check(Player player, String first, String second)
	{
		if(!first.equals(second))
		{
			player.sendMessage("Password is not equals!");
			return; ///here
		}

		setSecondaryPassword(player, player.getAccountName(), first);
	}

	public static void setSecondaryPassword(Player changer, String accountName, String password)
	{
		if(!CCPPasswordRecover.checkInvalidChars(password, false))
		{
			changer.sendMessage("Invalid characters in Password!");
			return;
		}

		AuthServerCommunication.getInstance().sendPacket(new ChangeSecondaryPassword(accountName, password));
		changer.sendMessage("Password Changed!");
	}

	public static boolean tryPass(Player player, String pass)
	{
		String correctPass = getSecondaryPass(player);
		if(pass.equalsIgnoreCase(correctPass))
			return true;
		return false;
	}

	public static boolean hasPassword(Player player)
	{
		String pass = getSecondaryPass(player);
		if(pass != null && pass.length() > 0)
			return true;
		return false;
	}

	private static void sendHtml(Player player, String html)
	{
		html = html.replace("%online%", CCPSmallCommands.showOnlineCount());
		NpcHtmlMessage msg = new NpcHtmlMessage(0);
		msg.setHtml(html);
		player.sendPacket(msg);
	}

	private static String getSecondaryPass(Player player)
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("SELECT secondaryPassword FROM accounts WHERE login='" + player.getAccountName() + "'");
				ResultSet rset = statement.executeQuery())
		{
			while(rset.next())
			{
				return rset.getString("secondaryPassword");
			}
		}
		catch(SQLException e)
		{
			_log.error("Error in getSecondaryPass ", e);
		}
		return null;
	}
}
