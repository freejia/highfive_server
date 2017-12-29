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
 * this program. If not, see <http://l2jpsproject.eu/>.
 */
package com.l2jserver.gameserver.communitybbs.Managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.l2jserver.Config;
import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.effects.L2Effect;
import com.l2jserver.gameserver.model.skills.L2Skill;

/**
 * @RobikBobik
 */
public class BuffBBSManager extends BaseBBSManager
{
	private static BuffBBSManager _instance = new BuffBBSManager();
	
	public int allskillid_1[][];
	
	public BuffBBSManager()
	{
		Load();
	}
	
	public static BuffBBSManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new BuffBBSManager();
		}
		return _instance;
	}
	
	public void Load()
	{
		
		Connection connn = null;
		try
		{
			connn = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement count = connn.prepareStatement("SELECT COUNT(*) FROM communitybuff");
			ResultSet countt = count.executeQuery();
			countt.next();
			allskillid_1 = new int[countt.getInt(1)][4];
			PreparedStatement table = connn.prepareStatement("SELECT * FROM communitybuff");
			ResultSet skills = table.executeQuery();
			for (int i = 0; i < allskillid_1.length; i++)
			{
				skills.next();
				allskillid_1[i][0] = skills.getInt(2);
				allskillid_1[i][1] = skills.getInt(3);
				allskillid_1[i][2] = skills.getInt(4);
				allskillid_1[i][3] = skills.getInt(5);
			}
			
			count.close();
			countt.close();
			skills.close();
			table.close();
			connn.close();
		}
		catch (Exception ignored)
		{
		}
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		String[] parts = command.split("_");
		boolean petbuff = false;
		
		if (!(parts[2].startsWith("buff")))
		{
			return;
		}
		
		if ((parts[4] != null) && parts[4].startsWith(" Player"))
		{
			petbuff = false;
		}
		if ((parts[4] != null) && parts[4].startsWith(" Pet"))
		{
			petbuff = true;
		}
		
		if (parts[3].startsWith("FIGHERLIST"))
		{
			FIGHERLIST(activeChar, petbuff);
		}
		
		if (parts[3].startsWith("MAGELIST"))
		{
			MAGELIST(activeChar, petbuff);
		}
		
		if (parts[3].startsWith("SAVE"))
		{
			SAVE(activeChar, petbuff);
		}
		
		if (parts[3].startsWith("BUFF"))
		{
			BUFF(activeChar, petbuff);
		}
		
		if (parts[3].startsWith("CANCEL"))
		{
			CANCEL(activeChar, petbuff);
		}
		
		if (parts[3].startsWith("REGMP"))
		{
			REGMP(activeChar, petbuff);
		}
		
		if (parts[3].startsWith("REGHP"))
		{
			REGHP(activeChar, petbuff);
		}
		
		if (parts[3].startsWith("REGCP"))
		{
			REGCP(activeChar, petbuff);
		}
		
		for (int key = 0; key < allskillid_1.length; key++)
		{
			L2Skill skill;
			int skilllevel;
			
			skilllevel = SkillTable.getInstance().getMaxLevel(allskillid_1[key][0]);
			skill = SkillTable.getInstance().getInfo(allskillid_1[key][0], skilllevel);
			if (parts[3].startsWith(skill.getName()))
			{
				SKILL(activeChar, petbuff, key, skill);
			}
		}
	}
	
	private void FIGHERLIST(L2PcInstance activeChar, boolean petbuff)
	{
		int arr$[][] = allskillid_1;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			int aSkillid[] = arr$[i$];
			if ((aSkillid[1] != 1) && (aSkillid[1] != 3))
			{
				continue;
			}
			L2Skill skill;
			int skilllevel;
			if (Config.BUFF_COST)
			{
				if (activeChar.destroyItemByItemId(null, aSkillid[3], aSkillid[2], activeChar, true))
				{
					skilllevel = SkillTable.getInstance().getMaxLevel(aSkillid[0]);
					skill = SkillTable.getInstance().getInfo(aSkillid[0], skilllevel);
					if (!petbuff)
					{
						skill.getEffects(activeChar, activeChar);
					}
					else
					{
						skill.getEffects(activeChar.getSummon(), activeChar.getSummon());
					}
				}
				else
				{
					activeChar.sendMessage("You don't have adena for using service.");
				}
				continue;
			}
			skilllevel = SkillTable.getInstance().getMaxLevel(aSkillid[0]);
			skill = SkillTable.getInstance().getInfo(aSkillid[0], skilllevel);
			if (!petbuff)
			{
				skill.getEffects(activeChar, activeChar);
			}
			else
			{
				skill.getEffects(activeChar.getSummon(), activeChar.getSummon());
			}
		}
		
	}
	
	private void MAGELIST(L2PcInstance activeChar, boolean petbuff)
	{
		int arr$[][] = allskillid_1;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++)
		{
			int aSkillid[] = arr$[i$];
			if ((aSkillid[1] != 2) && (aSkillid[1] != 3))
			{
				continue;
			}
			L2Skill skill;
			int skilllevel;
			if (Config.BUFF_COST)
			{
				if (activeChar.destroyItemByItemId(null, aSkillid[3], aSkillid[2], activeChar, true))
				{
					skilllevel = SkillTable.getInstance().getMaxLevel(aSkillid[0]);
					skill = SkillTable.getInstance().getInfo(aSkillid[0], skilllevel);
					if (!petbuff)
					{
						skill.getEffects(activeChar, activeChar);
					}
					else
					{
						skill.getEffects(activeChar.getSummon(), activeChar.getSummon());
					}
				}
				else
				{
					activeChar.sendMessage("You don't have adena for using service.");
				}
				continue;
			}
			skilllevel = SkillTable.getInstance().getMaxLevel(aSkillid[0]);
			skill = SkillTable.getInstance().getInfo(aSkillid[0], skilllevel);
			if (!petbuff)
			{
				skill.getEffects(activeChar, activeChar);
			}
			else
			{
				skill.getEffects(activeChar.getSummon(), activeChar.getSummon());
			}
		}
		
	}
	
	private void BUFF(L2PcInstance activeChar, boolean petbuff)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM community_skillsave WHERE charId=?;");
			statement.setInt(1, activeChar.getObjectId());
			ResultSet rcln = statement.executeQuery();
			rcln.next();
			if (!petbuff)
			{
				char allskills[] = rcln.getString(2).toCharArray();
				if (allskills.length == allskillid_1.length)
				{
					for (int i = 0; i < allskillid_1.length; i++)
					{
						if (allskills[i] == '1')
						{
							if (Config.BUFF_COST)
							{
								if (activeChar.destroyItemByItemId(null, allskillid_1[i][3], allskillid_1[i][2], activeChar, true))
								{
									int skilllevel = SkillTable.getInstance().getMaxLevel(allskillid_1[i][0]);
									L2Skill skill = SkillTable.getInstance().getInfo(allskillid_1[i][0], skilllevel);
									skill.getEffects(activeChar, activeChar);
									activeChar.getLevel();
								}
								else
								{
									activeChar.sendMessage("You don't have adena for using service.");
								}
							}
							else
							{
								int skilllevel = SkillTable.getInstance().getMaxLevel(allskillid_1[i][0]);
								L2Skill skill = SkillTable.getInstance().getInfo(allskillid_1[i][0], skilllevel);
								skill.getEffects(activeChar, activeChar);
							}
						}
					}
					
				}
			}
			else
			{
				char petskills[] = rcln.getString(3).toCharArray();
				if (petskills.length == allskillid_1.length)
				{
					for (int i = 0; i < allskillid_1.length; i++)
					{
						if (petskills[i] != '1')
						{
							continue;
						}
						if (Config.BUFF_COST)
						{
							if (activeChar.destroyItemByItemId(null, allskillid_1[i][3], allskillid_1[i][2], activeChar, true))
							{
								int skilllevel = SkillTable.getInstance().getMaxLevel(allskillid_1[i][0]);
								L2Skill skill = SkillTable.getInstance().getInfo(allskillid_1[i][0], skilllevel);
								skill.getEffects(activeChar.getSummon(), activeChar.getSummon());
							}
							else
							{
								activeChar.sendMessage("You don't have adena for using service.");
							}
						}
						else
						{
							int skilllevel = SkillTable.getInstance().getMaxLevel(allskillid_1[i][0]);
							L2Skill skill = SkillTable.getInstance().getInfo(allskillid_1[i][0], skilllevel);
							skill.getEffects(activeChar.getSummon(), activeChar.getSummon());
						}
					}
					
				}
			}
			rcln.close();
			statement.close();
		}
		catch (Exception ignored)
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			
		}
		try
		{
			if (con != null)
			{
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void CANCEL(L2PcInstance activeChar, boolean petbuff)
	{
		if (!petbuff)
		{
			activeChar.stopAllEffects();
		}
		else
		{
			activeChar.getSummon().stopAllEffects();
		}
	}
	
	private void REGMP(L2PcInstance activeChar, boolean petbuff)
	{
		if (!petbuff)
		{
			activeChar.setCurrentMp(activeChar.getMaxMp());
		}
		else
		{
			activeChar.getSummon().setCurrentMp(activeChar.getSummon().getMaxMp());
		}
	}
	
	private void REGHP(L2PcInstance activeChar, boolean petbuff)
	{
		if (!petbuff)
		{
			activeChar.setCurrentHp(activeChar.getMaxHp());
		}
		else
		{
			activeChar.getSummon().setCurrentHp(activeChar.getSummon().getMaxHp());
		}
	}
	
	private void REGCP(L2PcInstance activeChar, boolean petbuff)
	{
		if (!petbuff)
		{
			activeChar.setCurrentCp(activeChar.getMaxCp());
		}
		else
		{
			activeChar.getSummon().setCurrentCp(activeChar.getSummon().getMaxCp());
		}
	}
	
	private void SKILL(L2PcInstance activeChar, boolean petbuff, int key, L2Skill skill)
	{
		if (Config.BUFF_COST)
		{
			if (activeChar.destroyItemByItemId(null, allskillid_1[key][3], allskillid_1[key][2], activeChar, true))
			{
				if (!petbuff)
				{
					skill.getEffects(activeChar, activeChar);
				}
				else
				{
					skill.getEffects(activeChar.getSummon(), activeChar.getSummon());
				}
			}
			else
			{
				activeChar.sendMessage("You don't have adena for using service.");
			}
		}
		else if (!petbuff)
		{
			skill.getEffects(activeChar, activeChar);
		}
		else
		{
			skill.getEffects(activeChar.getSummon(), activeChar.getSummon());
		}
	}
	
	private void SAVE(L2PcInstance activeChar, boolean petbuff)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stat = con.prepareStatement("SELECT COUNT(*) FROM community_skillsave WHERE charId=?;");
			stat.setInt(1, activeChar.getObjectId());
			ResultSet rset = stat.executeQuery();
			rset.next();
			String allbuff = "";
			if (!petbuff)
			{
				L2Effect skill[] = activeChar.getAllEffectsCB();
				boolean flag = true;
				int arr$[][] = allskillid_1;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					int aSkillid[] = arr$[i$];
					for (int j = 0; j < skill.length; j++)
					{
						if (aSkillid[0] == skill[j].getId())
						{
							allbuff = (new StringBuilder()).append(allbuff).append(1).toString();
							flag = false;
						}
						if ((j == (skill.length - 1)) && flag)
						{
							allbuff = (new StringBuilder()).append(allbuff).append(0).toString();
						}
					}
					flag = true;
				}
				
				if (rset.getInt(1) == 0)
				{
					PreparedStatement statement1 = con.prepareStatement("INSERT INTO community_skillsave (charId,skills) values (?,?)");
					statement1.setInt(1, activeChar.getObjectId());
					statement1.setString(2, allbuff);
					statement1.execute();
					statement1.close();
				}
				else
				{
					PreparedStatement statement = con.prepareStatement("UPDATE community_skillsave SET skills=? WHERE charId=?;");
					statement.setString(1, allbuff);
					statement.setInt(2, activeChar.getObjectId());
					statement.execute();
					statement.close();
				}
			}
			else
			{
				L2Effect skill[] = activeChar.getSummon().getAllEffectsCB();
				boolean flag = true;
				int arr$[][] = allskillid_1;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++)
				{
					int aSkillid[] = arr$[i$];
					for (int j = 0; j < skill.length; j++)
					{
						if (aSkillid[0] == skill[j].getId())
						{
							allbuff = (new StringBuilder()).append(allbuff).append(1).toString();
							flag = false;
						}
						if ((j == (skill.length - 1)) && flag)
						{
							allbuff = (new StringBuilder()).append(allbuff).append(0).toString();
						}
					}
					flag = true;
				}
				
				if (rset.getInt(1) == 0)
				{
					PreparedStatement statement1 = con.prepareStatement("INSERT INTO community_skillsave (charId,pet) values (?,?)");
					statement1.setInt(1, activeChar.getObjectId());
					statement1.setString(2, allbuff);
					statement1.execute();
					statement1.close();
				}
				else
				{
					PreparedStatement statement = con.prepareStatement("UPDATE community_skillsave SET pet=? WHERE charId=?;");
					statement.setString(1, allbuff);
					statement.setInt(2, activeChar.getObjectId());
					statement.execute();
					statement.close();
				}
			}
			rset.close();
			stat.close();
		}
		catch (Exception ignored)
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			if (con != null)
			{
				con.close();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void parsewrite(String s, String s1, String s2, String s3, String s4, L2PcInstance l2pcinstance)
	{
	}
}