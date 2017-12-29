package l2f.gameserver.model.entity.olympiad;


import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.Announcements;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.entity.Hero;
import l2f.gameserver.network.serverpackets.SystemMessage2;
import l2f.gameserver.network.serverpackets.components.SystemMsg;
import l2f.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OlympiadEndTask extends RunnableImpl
{
	private static final Logger _log = LoggerFactory.getLogger(OlympiadEndTask.class);

	@Override
	public void runImpl() throws Exception
	{
		if (Olympiad._inCompPeriod) // Если бои еще не закончились, откладываем окончание олимпиады на минуту
		{
			ThreadPoolManager.getInstance().schedule(new OlympiadEndTask(), 60000);
			return;
		}

		Announcements.getInstance().announceToAll(new SystemMessage2(SystemMsg.OLYMPIAD_PERIOD_S1_HAS_ENDED).addInteger(Olympiad._currentCycle));
		Announcements.getInstance().announceToAll("Olympiad Validation Period has began");

		Olympiad._isOlympiadEnd = true;
		if (Olympiad._scheduledManagerTask != null)
			Olympiad._scheduledManagerTask.cancel(false);
		if (Olympiad._scheduledWeeklyTask != null)
			Olympiad._scheduledWeeklyTask.cancel(false);

		Olympiad._validationEnd = System.currentTimeMillis() + Olympiad._olympiadEnd + Config.ALT_OLY_VPERIOD;

		OlympiadDatabase.saveNobleData();
		Olympiad._period = 1;
		Hero.getInstance().clearHeroes();

		try
		{
			OlympiadDatabase.save();
		}
		catch (RuntimeException e)
		{
			_log.error("Olympiad System: Failed to save Olympiad configuration!", e);
		}

		_log.info("Olympiad System: Starting Validation period. Time to end validation:" + Olympiad.getMillisToValidationEnd() / TimeUtils.MINUTE_IN_MILLIS);

		if (Olympiad._scheduledValdationTask != null)
			Olympiad._scheduledValdationTask.cancel(false);
		Olympiad._scheduledValdationTask = ThreadPoolManager.getInstance().schedule(new ValidationTask(), Olympiad.getMillisToValidationEnd());
	}
}