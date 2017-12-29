package l2f.gameserver.model.entity.olympiad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Doing Weekly Task(giving points and new fight to all Noble Players)
 */
public class WeeklyTask implements Runnable
{
	private static final Logger _log = LoggerFactory.getLogger(WeeklyTask.class);

	@Override
	public void run()
	{
		Olympiad.doWeekTasks();
		_log.info("Olympiad System: Added weekly points to nobles");
	}
}