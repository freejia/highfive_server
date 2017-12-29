package l2f.gameserver.handler.voicecommands;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.handler.voicecommands.impl.*;
import l2f.gameserver.handler.voicecommands.impl.BotReport.ReportCommand;

import java.util.HashMap;
import java.util.Map;

public class VoicedCommandHandler extends AbstractHolder
{
	private static final VoicedCommandHandler _instance = new VoicedCommandHandler();

	public static VoicedCommandHandler getInstance()
	{
		return _instance;
	}

	private final Map<String, IVoicedCommandHandler> _datatable = new HashMap<String, IVoicedCommandHandler>();

	private VoicedCommandHandler()
	{
		registerVoicedCommandHandler(new Atod());
		registerVoicedCommandHandler(new AntiGrief());
		registerVoicedCommandHandler(new CombineTalismans());
		registerVoicedCommandHandler(new Cfg());
		registerVoicedCommandHandler(new Help());
		registerVoicedCommandHandler(new Online());
		registerVoicedCommandHandler(new Hellbound());
		registerVoicedCommandHandler(new Teleport());
		registerVoicedCommandHandler(new PollCommand());
		registerVoicedCommandHandler(new CWHPrivileges());
		registerVoicedCommandHandler(new Offline());
		registerVoicedCommandHandler(new Password());
		registerVoicedCommandHandler(new Relocate());
		registerVoicedCommandHandler(new ReportCommand());
		registerVoicedCommandHandler(new Repair());
		registerVoicedCommandHandler(new ServerInfo());
		registerVoicedCommandHandler(new Wedding());
		registerVoicedCommandHandler(new WhoAmI());
		registerVoicedCommandHandler(new Debug());
		registerVoicedCommandHandler(new Security());
		registerVoicedCommandHandler(new ReportBot());
		registerVoicedCommandHandler(new res());
		registerVoicedCommandHandler(new Ping());
		registerVoicedCommandHandler(new CommandSiege());
		registerVoicedCommandHandler(new LockPc());

		// Synerge
		registerVoicedCommandHandler(new BuffStoreVoiced());
		registerVoicedCommandHandler(new VoiceGmEvent());
	}

	public void registerVoicedCommandHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		for (String element : ids)
		{
			_datatable.put(element, handler);
		}
	}

	public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand)
	{
		String command = voicedCommand;
		if (voicedCommand.indexOf(" ") != -1)
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}

		return _datatable.get(command);
	}

	@Override
	public int size()
	{
		return _datatable.size();
	}

	@Override
	public void clear()
	{
		_datatable.clear();
	}
}
