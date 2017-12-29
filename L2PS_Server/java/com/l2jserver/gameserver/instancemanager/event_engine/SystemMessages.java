package com.l2jserver.gameserver.instancemanager.event_engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import javolution.util.FastMap;

public class SystemMessages
{
	private static class SingletonHolder
	{
		protected static final SystemMessages _instance = new SystemMessages();
	}
	
	public static SystemMessages getInstance()
	{
		return SingletonHolder._instance;
	}
	
	FastMap<Integer, String> messages;
	
	public SystemMessages()
	{
		messages = new FastMap<>();
		loadMessages();
	}
	
	public String getMsg(Integer msgId, Object[] params)
	{
		String message = messages.get(msgId);
		if (message != null)
		{
			if (params == null)
			{
				return message;
			}
			int i = 0;
			while (message.indexOf("#" + i) != -1)
			{
				message = message.replaceAll("#" + i, params[i].toString());
				i++;
			}
			return message;
		}
		
		return "MSG-ERROR";
	}
	
	private void loadMessages()
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader("config/EventMessages.txt"));
			String str;
			while ((str = in.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(str, "|");
				Integer id = Integer.valueOf(st.nextToken());
				String message = st.nextToken();
				messages.put(id, message);
			}
			
			in.close();
		}
		catch (IOException e)
		{
		}
	}
}
