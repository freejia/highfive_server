package com.l2jserver.gameserver.instancemanager.event_engine;

public class Interface
{
	public static final String version = "2.0";
	
	// ------------------------------------------------------
	// System methods
	// ------------------------------------------------------
	
	public static boolean areTeammates(Integer player, Integer target)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		
		return (Boolean) invokeMethod("io.In", "areTeammates", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			player,
			target
		});
	}
	
	public static void bypass(Integer player, String command)
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		invokeMethod("io.In", "bypass", new Class<?>[]
		{
			Integer.class,
			String.class
		}, new Object[]
		{
			player,
			command
		});
	}
	
	public static boolean canAttack(Integer player, Integer target)
	{
		if (!Main.isLoaded())
		{
			return true;
		}
		
		return (Boolean) invokeMethod("io.In", "canAttack", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			player,
			target
		});
		
	}
	
	public static boolean canTargetPlayer(Integer target, Integer self)
	{
		if (!Main.isLoaded())
		{
			return true;
		}
		
		return (Boolean) invokeMethod("io.In", "canTargetPlayer", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			target,
			self
		});
	}
	
	public static boolean canUseSkill(Integer player, Integer skill)
	{
		if (!Main.isLoaded())
		{
			return true;
		}
		
		return (Boolean) invokeMethod("io.In", "canUseSkill", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			player,
			skill
		});
	}
	
	// ------------------------------------------------------
	// Player Event / Combat checks
	// ------------------------------------------------------
	
	public static boolean doAttack(Integer self, Integer target)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		
		return (Boolean) invokeMethod("io.In", "doAttack", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			self,
			target
		});
	}
	
	public static void eventOnLogout(Integer player)
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		invokeMethod("io.In", "eventOnLogout", new Class<?>[]
		{
			Integer.class
		}, new Object[]
		{
			player
		});
		
		// mini events onLogout are already handled
	}
	
	public static boolean getBoolean(String propName, Integer player)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		
		return (Boolean) (invokeMethod("io.In", "getBoolean", new Class<?>[]
		{
			String.class,
			Integer.class
		}, new Object[]
		{
			propName,
			player
		}));
	}
	
	private static Class<?> getClass(String name)
	{
		Class<?> c = null;
		try
		{
			c = Class.forName("com.l2jserver.gameserver.instancemanager.event_engine." + name);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return c;
	}
	
	// ------------------------------------------------------
	// Player action notifiers
	// ------------------------------------------------------
	
	public static int getInt(String propName, Integer player)
	{
		if (!Main.isLoaded())
		{
			return 0;
		}
		
		return (Integer) (invokeMethod("io.In", "getInt", new Class<?>[]
		{
			String.class,
			Integer.class
		}, new Object[]
		{
			propName,
			player
		}));
	}
	
	private static Object invokeMethod(String className, String methodName)
	{
		try
		{
			return getClass(className).getMethod(methodName).invoke(getClass(className).getMethod("getInstance").invoke(null), (Object[]) null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private static Object invokeMethod(String className, String methodName, Class<?>[] paramTypes, Object[] args)
	{
		try
		{
			return getClass(className).getMethod(methodName, paramTypes).invoke(getClass(className).getMethod("getInstance").invoke(null), args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static boolean isParticipating(Integer player)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		
		if (player != null)
		{
			return ((Boolean) (invokeMethod("io.In", "isParticipating", new Class<?>[]
			{
				Integer.class
			}, new Object[]
			{
				player
			})));
		}
		else
		{
			return false;
		}
	}
	
	public static boolean isRegistered(Integer player)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		
		return (Boolean) invokeMethod("io.In", "isRegistered", new Class<?>[]
		{
			Integer.class
		}, new Object[]
		{
			player
		});
	}
	
	public static boolean logout(Integer player)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		
		return (Boolean) invokeMethod("io.In", "logout", new Class<?>[]
		{
			Integer.class
		}, new Object[]
		{
			player
		});
	}
	
	// ------------------------------------------------------
	// Player's actions - registrations, voting, buffer, stats
	// ------------------------------------------------------
	
	public static void onDie(Integer victim, Integer killer)
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		invokeMethod("io.In", "onDie", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			victim,
			killer
		});
		
	}
	
	public static void onHit(Integer actor, Integer target)
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		invokeMethod("io.In", "onHit", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			actor,
			target
		});
		
	}
	
	public static void onKill(Integer victim, Integer killer)
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		invokeMethod("io.In", "onKill", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			victim,
			killer
		});
		
	}
	
	public static void onLogout(Integer player)
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		invokeMethod("io.In", "onLogout", new Class<?>[]
		{
			Integer.class
		}, new Object[]
		{
			player
		});
	}
	
	public static void onSay(int type, Integer player, String text)
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		invokeMethod("io.In", "onSay", new Class<?>[]
		{
			int.class,
			Integer.class,
			String.class
		}, new Object[]
		{
			type,
			player,
			text
		});
	}
	
	public static boolean onTalkNpc(Integer npc, Integer player)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		
		return (Boolean) invokeMethod("io.In", "onTalkNpc", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			npc,
			player
		});
		
	}
	
	public static boolean onUseItem(Integer player, Integer item, Integer objectId)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		
		return (Boolean) invokeMethod("io.In", "onUseItem", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			player,
			item
		});
		
	}
	
	public static boolean onUseMagic(Integer player, Integer skill)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		return (Boolean) invokeMethod("io.In", "onUseMagic", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			player,
			skill
		});
	}
	
	public static void showFirstHtml(Integer player, int obj)
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		invokeMethod("io.In", "showFirstHtml", new Class<?>[]
		{
			Integer.class,
			int.class
		}, new Object[]
		{
			player,
			obj
		});
	}
	
	public static void shutdown()
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		invokeMethod("io.In", "shutdown");
	}
	
	public static void start()
	{
		if (!Main.isLoaded())
		{
			return;
		}
		
		try
		{
			getClass("Phoenix").getMethod("phoenixStart").invoke(null);
			// getClass("Stats").getMethod("getInstance", (Class[]) null).invoke(
			// null);
			if ((Boolean) (invokeMethod("io.In", "getBoolean", new Class<?>[]
			{
				String.class,
				Integer.class
			}, new Object[]
			{
				"eventBufferEnabled",
				0
			})))
			{
				getClass("function.Buffer").getMethod("getInstance").invoke(null);
			}
			
			// getClass("SystemMessages").getMethod("getInstance",(Class[])null).invoke(null);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean talkNpc(Integer player, Integer npc)
	{
		if (!Main.isLoaded())
		{
			return false;
		}
		
		return (Boolean) invokeMethod("io.In", "talkNpc", new Class<?>[]
		{
			Integer.class,
			Integer.class
		}, new Object[]
		{
			player,
			npc
		});
	}
	
}
