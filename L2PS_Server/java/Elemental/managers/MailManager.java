/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package Elemental.managers;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2f.gameserver.model.Player;

/**
 * Manager para mas que todo controlar los mails que se envian, los reusos, penalties, etc
 *
 * @author Synerge
 */
public class MailManager
{
	private static final int MAIL_SPAM_PENALTY = 24 * 60 * 60 * 1000; // 1 dia
	private static final int MAIL_SEND_REUSE = 5 * 60 * 1000; // 5 minutos

	private final Map<Integer, ArrayList<Long>> _mailsSent = new ConcurrentHashMap<>();

	protected MailManager()
	{
	}

	/**
	 * Adds a new mail sent to the selected player
	 *
	 * @param player
	 */
	public void addNewMailSent(Player player)
	{
		// Los gms no poseen penalties
		if (player.getAccessLevel() > 0)
			return;

		if (!_mailsSent.containsKey(player.getObjectId()))
		{
			_mailsSent.put(player.getObjectId(), new ArrayList<Long>());

			// Penalty normal de 5 minutos para enviar otro mail
			_mailsSent.get(player.getObjectId()).add(System.currentTimeMillis() + MAIL_SEND_REUSE);
		}
		else
		{
			// Antes de agregar el nuevo mail chequeamos los otros tiempos, si posee 5 mails enviados en 1 hora, el pj recivira un reuso de 24 horas
			final int oneHour = 60 * 60 * 1000;
			final long currentTime = System.currentTimeMillis();
			int mailCount = 1;

			for (long mailTime : _mailsSent.get(player.getObjectId()))
			{
				if (currentTime - mailTime < oneHour)
					mailCount++;
			}

			if (mailCount >= 5)
				_mailsSent.get(player.getObjectId()).add(System.currentTimeMillis() + MAIL_SPAM_PENALTY);
			else
				_mailsSent.get(player.getObjectId()).add(System.currentTimeMillis() + MAIL_SEND_REUSE);
		}
	}

	/**
	 * @param player
	 * @return Returns true if the player can send the mail or is under penalty
	 */
	public boolean canPlayerSendMail(Player player)
	{
		// Los gms no poseen penalties
		if (player.getAccessLevel() > 0)
			return true;

		if (!_mailsSent.containsKey(player.getObjectId()))
			return true;

		final ArrayList<Long> mails = _mailsSent.get(player.getObjectId());
		if (mails.isEmpty())
			return true;

		return System.currentTimeMillis() > mails.get(_mailsSent.size() - 1);
	}

	public static MailManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final MailManager _instance = new MailManager();
	}
}
