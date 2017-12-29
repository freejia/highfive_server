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
package com.l2jserver.gameserver.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.LoginServerThread;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.serverpackets.Ex2ndPasswordAck;
import com.l2jserver.gameserver.network.serverpackets.Ex2ndPasswordCheck;
import com.l2jserver.gameserver.network.serverpackets.Ex2ndPasswordVerify;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Base64;

/**
 * @author mrTJO
 */
public class SecondaryPasswordAuth
{
	private final Logger _log = Logger.getLogger(SecondaryPasswordAuth.class.getName());
	private final L2GameClient _activeClient;
	
	private String _password;
	private int _wrongAttempts;
	private boolean _authed;
	
	private static final String VAR_PWD = "secauth_pwd";
	private static final String VAR_WTE = "secauth_wte";
	
	private static final String SELECT_PASSWORD = "SELECT var, value FROM account_gsdata WHERE account_name=? AND var LIKE 'secauth_%'";
	private static final String INSERT_PASSWORD = "INSERT INTO account_gsdata VALUES (?, ?, ?)";
	private static final String UPDATE_PASSWORD = "UPDATE account_gsdata SET value=? WHERE account_name=? AND var=?";
	
	private static final String INSERT_ATTEMPT = "INSERT INTO account_gsdata VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value=?";
	
	private static final Set<String> forbiddenPasswords = new HashSet<>();
	
	static
	{
		// client checks for these should not be removed if you want more verification you must only add more not remove!
		forbiddenPasswords.add("000000");
		forbiddenPasswords.add("111111");
		forbiddenPasswords.add("222222");
		forbiddenPasswords.add("333333");
		forbiddenPasswords.add("444444");
		forbiddenPasswords.add("555555");
		forbiddenPasswords.add("666666");
		forbiddenPasswords.add("777777");
		forbiddenPasswords.add("888888");
		forbiddenPasswords.add("999999");
		forbiddenPasswords.add("123456");
		forbiddenPasswords.add("234567");
		forbiddenPasswords.add("345678");
		forbiddenPasswords.add("456789");
		forbiddenPasswords.add("567890");
		forbiddenPasswords.add("012345");
		forbiddenPasswords.add("098765");
		forbiddenPasswords.add("987654");
		forbiddenPasswords.add("876543");
		forbiddenPasswords.add("765432");
		forbiddenPasswords.add("543210");
		forbiddenPasswords.add("010101");
		forbiddenPasswords.add("020202");
		forbiddenPasswords.add("030303");
		forbiddenPasswords.add("040404");
		forbiddenPasswords.add("050505");
		forbiddenPasswords.add("060606");
		forbiddenPasswords.add("070707");
		forbiddenPasswords.add("080808");
		forbiddenPasswords.add("090909");
		forbiddenPasswords.add("121212");
		forbiddenPasswords.add("131313");
		forbiddenPasswords.add("141414");
		forbiddenPasswords.add("151515");
		forbiddenPasswords.add("161616");
		forbiddenPasswords.add("171717");
		forbiddenPasswords.add("181818");
		forbiddenPasswords.add("191919");
		forbiddenPasswords.add("101010");
		forbiddenPasswords.add("212121");
		forbiddenPasswords.add("232323");
		forbiddenPasswords.add("242424");
		forbiddenPasswords.add("252525");
		forbiddenPasswords.add("262626");
		forbiddenPasswords.add("272727");
		forbiddenPasswords.add("282828");
		forbiddenPasswords.add("292929");
		forbiddenPasswords.add("202020");
		forbiddenPasswords.add("313131");
		forbiddenPasswords.add("323232");
		forbiddenPasswords.add("343434");
		forbiddenPasswords.add("353535");
		forbiddenPasswords.add("363636");
		forbiddenPasswords.add("373737");
		forbiddenPasswords.add("383838");
		forbiddenPasswords.add("393939");
		forbiddenPasswords.add("303030");
		forbiddenPasswords.add("404040");
		forbiddenPasswords.add("414141");
		forbiddenPasswords.add("424242");
		forbiddenPasswords.add("434343");
		forbiddenPasswords.add("454545");
		forbiddenPasswords.add("464646");
		forbiddenPasswords.add("474747");
		forbiddenPasswords.add("484848");
		forbiddenPasswords.add("494949");
		forbiddenPasswords.add("505050");
		forbiddenPasswords.add("515151");
		forbiddenPasswords.add("525252");
		forbiddenPasswords.add("535353");
		forbiddenPasswords.add("545454");
		forbiddenPasswords.add("565656");
		forbiddenPasswords.add("575757");
		forbiddenPasswords.add("585858");
		forbiddenPasswords.add("595959");
		forbiddenPasswords.add("606060");
		forbiddenPasswords.add("616161");
		forbiddenPasswords.add("626262");
		forbiddenPasswords.add("636363");
		forbiddenPasswords.add("646464");
		forbiddenPasswords.add("656565");
		forbiddenPasswords.add("676767");
		forbiddenPasswords.add("686868");
		forbiddenPasswords.add("696969");
		forbiddenPasswords.add("707070");
		forbiddenPasswords.add("717171");
		forbiddenPasswords.add("727272");
		forbiddenPasswords.add("737373");
		forbiddenPasswords.add("747474");
		forbiddenPasswords.add("757575");
		forbiddenPasswords.add("767676");
		forbiddenPasswords.add("787878");
		forbiddenPasswords.add("797979");
		forbiddenPasswords.add("808080");
		forbiddenPasswords.add("818181");
		forbiddenPasswords.add("828282");
		forbiddenPasswords.add("838383");
		forbiddenPasswords.add("848484");
		forbiddenPasswords.add("858585");
		forbiddenPasswords.add("868686");
		forbiddenPasswords.add("878787");
		forbiddenPasswords.add("898989");
		forbiddenPasswords.add("909090");
		forbiddenPasswords.add("919191");
		forbiddenPasswords.add("929292");
		forbiddenPasswords.add("939393");
		forbiddenPasswords.add("949494");
		forbiddenPasswords.add("959595");
		forbiddenPasswords.add("969696");
		forbiddenPasswords.add("979797");
		forbiddenPasswords.add("989898");
		forbiddenPasswords.add("0000000");
		forbiddenPasswords.add("1111111");
		forbiddenPasswords.add("2222222");
		forbiddenPasswords.add("3333333");
		forbiddenPasswords.add("4444444");
		forbiddenPasswords.add("5555555");
		forbiddenPasswords.add("6666666");
		forbiddenPasswords.add("7777777");
		forbiddenPasswords.add("8888888");
		forbiddenPasswords.add("9999999");
		forbiddenPasswords.add("0123456");
		forbiddenPasswords.add("1234567");
		forbiddenPasswords.add("2345678");
		forbiddenPasswords.add("3456789");
		forbiddenPasswords.add("4567890");
		forbiddenPasswords.add("0987654");
		forbiddenPasswords.add("9876543");
		forbiddenPasswords.add("8765432");
		forbiddenPasswords.add("7654321");
		forbiddenPasswords.add("6543210");
		forbiddenPasswords.add("0101010");
		forbiddenPasswords.add("0202020");
		forbiddenPasswords.add("0303030");
		forbiddenPasswords.add("0404040");
		forbiddenPasswords.add("0505050");
		forbiddenPasswords.add("0606060");
		forbiddenPasswords.add("0707070");
		forbiddenPasswords.add("0808080");
		forbiddenPasswords.add("0909090");
		forbiddenPasswords.add("1212121");
		forbiddenPasswords.add("1313131");
		forbiddenPasswords.add("1414141");
		forbiddenPasswords.add("1515151");
		forbiddenPasswords.add("1616161");
		forbiddenPasswords.add("1717171");
		forbiddenPasswords.add("1818181");
		forbiddenPasswords.add("1919191");
		forbiddenPasswords.add("1010101");
		forbiddenPasswords.add("2020202");
		forbiddenPasswords.add("2121212");
		forbiddenPasswords.add("2323232");
		forbiddenPasswords.add("2424242");
		forbiddenPasswords.add("2525252");
		forbiddenPasswords.add("2626262");
		forbiddenPasswords.add("2727272");
		forbiddenPasswords.add("2828282");
		forbiddenPasswords.add("2929292");
		forbiddenPasswords.add("3030303");
		forbiddenPasswords.add("3131313");
		forbiddenPasswords.add("3232323");
		forbiddenPasswords.add("3434343");
		forbiddenPasswords.add("3535353");
		forbiddenPasswords.add("3636363");
		forbiddenPasswords.add("3737373");
		forbiddenPasswords.add("3838383");
		forbiddenPasswords.add("3939393");
		forbiddenPasswords.add("4040404");
		forbiddenPasswords.add("4141414");
		forbiddenPasswords.add("4242424");
		forbiddenPasswords.add("4343434");
		forbiddenPasswords.add("4545454");
		forbiddenPasswords.add("4646464");
		forbiddenPasswords.add("4747474");
		forbiddenPasswords.add("4848484");
		forbiddenPasswords.add("4949494");
		forbiddenPasswords.add("5050505");
		forbiddenPasswords.add("5151515");
		forbiddenPasswords.add("5252525");
		forbiddenPasswords.add("5353535");
		forbiddenPasswords.add("5454545");
		forbiddenPasswords.add("5656565");
		forbiddenPasswords.add("5757575");
		forbiddenPasswords.add("5858585");
		forbiddenPasswords.add("5959595");
		forbiddenPasswords.add("6060606");
		forbiddenPasswords.add("6161616");
		forbiddenPasswords.add("6262626");
		forbiddenPasswords.add("6363636");
		forbiddenPasswords.add("6464646");
		forbiddenPasswords.add("6565656");
		forbiddenPasswords.add("6767676");
		forbiddenPasswords.add("6868686");
		forbiddenPasswords.add("6969696");
		forbiddenPasswords.add("7070707");
		forbiddenPasswords.add("7171717");
		forbiddenPasswords.add("7272727");
		forbiddenPasswords.add("7373737");
		forbiddenPasswords.add("7474747");
		forbiddenPasswords.add("7575757");
		forbiddenPasswords.add("7676767");
		forbiddenPasswords.add("7878787");
		forbiddenPasswords.add("7979797");
		forbiddenPasswords.add("8080808");
		forbiddenPasswords.add("8181818");
		forbiddenPasswords.add("8282828");
		forbiddenPasswords.add("8383838");
		forbiddenPasswords.add("8484848");
		forbiddenPasswords.add("8585858");
		forbiddenPasswords.add("8686868");
		forbiddenPasswords.add("8787878");
		forbiddenPasswords.add("8989898");
		forbiddenPasswords.add("9090909");
		forbiddenPasswords.add("9191919");
		forbiddenPasswords.add("9292929");
		forbiddenPasswords.add("9393939");
		forbiddenPasswords.add("9494949");
		forbiddenPasswords.add("9595959");
		forbiddenPasswords.add("9696969");
		forbiddenPasswords.add("9797979");
		forbiddenPasswords.add("9898989");
		forbiddenPasswords.add("00000000");
		forbiddenPasswords.add("11111111");
		forbiddenPasswords.add("22222222");
		forbiddenPasswords.add("33333333");
		forbiddenPasswords.add("44444444");
		forbiddenPasswords.add("55555555");
		forbiddenPasswords.add("66666666");
		forbiddenPasswords.add("77777777");
		forbiddenPasswords.add("88888888");
		forbiddenPasswords.add("99999999");
		forbiddenPasswords.add("12345678");
		forbiddenPasswords.add("23456789");
		forbiddenPasswords.add("34567890");
		forbiddenPasswords.add("01234567");
		forbiddenPasswords.add("98765432");
		forbiddenPasswords.add("87654321");
		forbiddenPasswords.add("76543210");
		forbiddenPasswords.add("01010101");
		forbiddenPasswords.add("02020202");
		forbiddenPasswords.add("03030303");
		forbiddenPasswords.add("04040404");
		forbiddenPasswords.add("05050505");
		forbiddenPasswords.add("06060606");
		forbiddenPasswords.add("07070707");
		forbiddenPasswords.add("08080808");
		forbiddenPasswords.add("09090909");
		forbiddenPasswords.add("10101010");
		forbiddenPasswords.add("12121212");
		forbiddenPasswords.add("13131313");
		forbiddenPasswords.add("14141414");
		forbiddenPasswords.add("15151515");
		forbiddenPasswords.add("16161616");
		forbiddenPasswords.add("17171717");
		forbiddenPasswords.add("18181818");
		forbiddenPasswords.add("19191919");
		forbiddenPasswords.add("20202020");
		forbiddenPasswords.add("21212121");
		forbiddenPasswords.add("23232323");
		forbiddenPasswords.add("24242424");
		forbiddenPasswords.add("25252525");
		forbiddenPasswords.add("26262626");
		forbiddenPasswords.add("27272727");
		forbiddenPasswords.add("28282828");
		forbiddenPasswords.add("29292929");
		forbiddenPasswords.add("30303030");
		forbiddenPasswords.add("31313131");
		forbiddenPasswords.add("32323232");
		forbiddenPasswords.add("34343434");
		forbiddenPasswords.add("35353535");
		forbiddenPasswords.add("36363636");
		forbiddenPasswords.add("37373737");
		forbiddenPasswords.add("38383838");
		forbiddenPasswords.add("39393939");
		forbiddenPasswords.add("40404040");
		forbiddenPasswords.add("41414141");
		forbiddenPasswords.add("42424242");
		forbiddenPasswords.add("43434343");
		forbiddenPasswords.add("45454545");
		forbiddenPasswords.add("46464646");
		forbiddenPasswords.add("47474747");
		forbiddenPasswords.add("48484848");
		forbiddenPasswords.add("49494949");
		forbiddenPasswords.add("50505050");
		forbiddenPasswords.add("51515151");
		forbiddenPasswords.add("52525252");
		forbiddenPasswords.add("53535353");
		forbiddenPasswords.add("54545454");
		forbiddenPasswords.add("56565656");
		forbiddenPasswords.add("57575757");
		forbiddenPasswords.add("58585858");
		forbiddenPasswords.add("59595959");
		forbiddenPasswords.add("60606060");
		forbiddenPasswords.add("61616161");
		forbiddenPasswords.add("62626262");
		forbiddenPasswords.add("63636363");
		forbiddenPasswords.add("64646464");
		forbiddenPasswords.add("65656565");
		forbiddenPasswords.add("67676767");
		forbiddenPasswords.add("68686868");
		forbiddenPasswords.add("69696969");
		forbiddenPasswords.add("70707070");
		forbiddenPasswords.add("71717171");
		forbiddenPasswords.add("72727272");
		forbiddenPasswords.add("73737373");
		forbiddenPasswords.add("74747474");
		forbiddenPasswords.add("75757575");
		forbiddenPasswords.add("76767676");
		forbiddenPasswords.add("78787878");
		forbiddenPasswords.add("79797979");
		forbiddenPasswords.add("80808080");
		forbiddenPasswords.add("81818181");
		forbiddenPasswords.add("82828282");
		forbiddenPasswords.add("83838383");
		forbiddenPasswords.add("84848484");
		forbiddenPasswords.add("85858585");
		forbiddenPasswords.add("86868686");
		forbiddenPasswords.add("87878787");
		forbiddenPasswords.add("89898989");
		forbiddenPasswords.add("90909090");
		forbiddenPasswords.add("91919191");
		forbiddenPasswords.add("92929292");
		forbiddenPasswords.add("93939393");
		forbiddenPasswords.add("94949494");
		forbiddenPasswords.add("95959595");
		forbiddenPasswords.add("96969696");
		forbiddenPasswords.add("97979797");
		forbiddenPasswords.add("98989898");
	}
	
	/**
	 * @param activeClient
	 */
	public SecondaryPasswordAuth(L2GameClient activeClient)
	{
		_activeClient = activeClient;
		_password = null;
		_wrongAttempts = 0;
		_authed = false;
		loadPassword();
	}
	
	private void loadPassword()
	{
		String var, value = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(SELECT_PASSWORD))
		{
			statement.setString(1, _activeClient.getAccountName());
			try (ResultSet rs = statement.executeQuery())
			{
				while (rs.next())
				{
					var = rs.getString("var");
					value = rs.getString("value");
					
					if (var.equals(VAR_PWD))
					{
						_password = value;
					}
					else if (var.equals(VAR_WTE))
					{
						_wrongAttempts = Integer.parseInt(value);
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Error while reading password.", e);
		}
	}
	
	public boolean savePassword(String password)
	{
		if (passwordExist())
		{
			_log.warning("[SecondaryPasswordAuth]" + _activeClient.getAccountName() + " forced savePassword");
			_activeClient.closeNow();
			return false;
		}
		
		if (!validatePassword(password))
		{
			_activeClient.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAck.WRONG_PATTERN));
			return false;
		}
		
		password = cryptPassword(password);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_PASSWORD))
		{
			statement.setString(1, _activeClient.getAccountName());
			statement.setString(2, VAR_PWD);
			statement.setString(3, password);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Error while writing password.", e);
			return false;
		}
		_password = password;
		return true;
	}
	
	public boolean insertWrongAttempt(int attempts)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_ATTEMPT))
		{
			statement.setString(1, _activeClient.getAccountName());
			statement.setString(2, VAR_WTE);
			statement.setString(3, Integer.toString(attempts));
			statement.setString(4, Integer.toString(attempts));
			statement.execute();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Error while writing wrong attempts.", e);
			return false;
		}
		return true;
	}
	
	public boolean changePassword(String oldPassword, String newPassword)
	{
		if (!passwordExist())
		{
			_log.warning("[SecondaryPasswordAuth]" + _activeClient.getAccountName() + " forced changePassword");
			_activeClient.closeNow();
			return false;
		}
		
		if (!checkPassword(oldPassword, true))
		{
			return false;
		}
		
		if (!validatePassword(newPassword))
		{
			_activeClient.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAck.WRONG_PATTERN));
			return false;
		}
		
		newPassword = cryptPassword(newPassword);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_PASSWORD))
		{
			statement.setString(1, newPassword);
			statement.setString(2, _activeClient.getAccountName());
			statement.setString(3, VAR_PWD);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "Error while reading password.", e);
			return false;
		}
		
		_password = newPassword;
		_authed = false;
		return true;
	}
	
	public boolean checkPassword(String password, boolean skipAuth)
	{
		password = cryptPassword(password);
		
		if (!password.equals(_password))
		{
			_wrongAttempts++;
			if (_wrongAttempts < Config.SECOND_AUTH_MAX_ATTEMPTS)
			{
				_activeClient.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_WRONG, _wrongAttempts));
				insertWrongAttempt(_wrongAttempts);
			}
			else
			{
				LoginServerThread.getInstance().sendTempBan(_activeClient.getAccountName(), _activeClient.getConnectionAddress().getHostAddress(), Config.SECOND_AUTH_BAN_TIME);
				LoginServerThread.getInstance().sendMail(_activeClient.getAccountName(), "SATempBan", _activeClient.getConnectionAddress().getHostAddress(), Integer.toString(Config.SECOND_AUTH_MAX_ATTEMPTS), Long.toString(Config.SECOND_AUTH_BAN_TIME), Config.SECOND_AUTH_REC_LINK);
				_log.warning(_activeClient.getAccountName() + " - (" + _activeClient.getConnectionAddress().getHostAddress() + ") has inputted the wrong password " + _wrongAttempts + " times in row.");
				insertWrongAttempt(0);
				_activeClient.close(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_BAN, Config.SECOND_AUTH_MAX_ATTEMPTS));
			}
			return false;
		}
		if (!skipAuth)
		{
			_authed = true;
			_activeClient.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_OK, _wrongAttempts));
		}
		insertWrongAttempt(0);
		return true;
	}
	
	public boolean passwordExist()
	{
		return _password == null ? false : true;
	}
	
	public void openDialog()
	{
		if (passwordExist())
		{
			_activeClient.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_PROMPT));
		}
		else
		{
			_activeClient.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_NEW));
		}
	}
	
	public boolean isAuthed()
	{
		return _authed;
	}
	
	private String cryptPassword(String password)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] raw = password.getBytes("UTF-8");
			byte[] hash = md.digest(raw);
			return Base64.encodeBytes(hash);
		}
		catch (NoSuchAlgorithmException e)
		{
			_log.severe("[SecondaryPasswordAuth]Unsupported Algorythm");
		}
		catch (UnsupportedEncodingException e)
		{
			_log.severe("[SecondaryPasswordAuth]Unsupported Encoding");
		}
		return null;
	}
	
	private boolean validatePassword(String password)
	{
		if (!Util.isDigit(password))
		{
			return false;
		}
		
		if ((password.length() < 6) || (password.length() > 8))
		{
			return false;
		}
		
		if (forbiddenPasswords.contains(password))
		{
			return false;
		}
		return true;
	}
}