package l2f.gameserver.vote;


public class MonthlyResetTask
{
	public static void getInstance()
	{
		/*ThreadPoolManager.getInstance().schedule(new Runnable()
		{
			public void run()
			{
				try
				{
					Connection con = DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("UPDATE characters SET monthlyVotes=?");

					statement.setInt(1, 0);
					statement.execute();
					statement.close();
				}
				catch (SQLException e)
				{
					LOG.error("Error while Updating Monthly Votes", e);
				}
			}
		}, getValidationTime());*/
	}

//	private static long getValidationTime()
//	{
//		Calendar cld = Calendar.getInstance();
//		cld.set(5, 1);
//		long time = cld.getTimeInMillis();
//		if (System.currentTimeMillis() - time <= 0L)
//		{
//			return cld.getTimeInMillis() - System.currentTimeMillis();
//		}
//		return 0L;
//	}
}