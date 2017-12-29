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
package com.l2jserver.gameserver.instancemanager.rank_system.imageconverter;

import java.io.File;

import javolution.text.TextBuilder;
import javolution.util.FastMap;
import javolution.util.FastMap.Entry;

import com.l2jserver.Config;
import com.l2jserver.gameserver.instancemanager.rank_system.rankpvpsystem.Rank;
import com.l2jserver.gameserver.instancemanager.rank_system.rankpvpsystem.RankPvpSystemConfig;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.PledgeCrest;

/**
 * @author Masterio
 */
public class ServerSideImage
{
	
	private static ServerSideImage _instance = null;
	
	/** <rank_id, File> */
	private FastMap<Integer, File> _iconImages = new FastMap<>();
	private FastMap<Integer, File> _nameImages = new FastMap<>();
	private FastMap<Integer, File> _expImages = new FastMap<>();
	
	public final int IMG_PREFIX = RankPvpSystemConfig.IMAGE_PREFIX;
	
	private ServerSideImage()
	{
		
		load();
		
		System.out.println(" - IconImages loaded " + (getIconImages().size()) + " objects.");
		System.out.println(" - NameImages loaded " + (getNameImages().size()) + " objects.");
		System.out.println(" - ExpImages loaded " + (getExpImages().size()) + " objects.");
		
	}
	
	public static ServerSideImage getInstance()
	{
		if (_instance == null)
		{
			_instance = new ServerSideImage();
		}
		
		return _instance;
	}
	
	public void load()
	{
		
		for (Entry<Integer, Rank> e = RankPvpSystemConfig.RANKS.head(), end = RankPvpSystemConfig.RANKS.tail(); (e = e.getNext()) != end;)
		{
			
			String src = null;
			File image = null;
			
			try
			{
				
				// set icon images:
				src = "rank_" + e.getValue().getId();
				image = new File("data/images/" + src + ".png");
				getIconImages().put(e.getValue().getId(), image);
				
				// set name images:
				src = "rank_name_" + e.getValue().getId();
				image = new File("data/images/" + src + ".png");
				getNameImages().put(e.getValue().getId(), image);
				
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				return;
			}
			
		}
		
		for (int i = 0; i <= 100; i++)
		{
			
			String src = null;
			File image = null;
			
			try
			{
				
				// set exp images:
				src = "exp_" + i;
				image = new File("data/images/" + src + ".png");
				getExpImages().put(i, image);
				
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				return;
			}
		}
		
	}
	
	public TextBuilder getExpImageHtmlTag(L2PcInstance player, int expId, int width, int height)
	{
		
		TextBuilder tb = new TextBuilder();
		try
		{
			
			int id = 400000000 + expId + (IMG_PREFIX * 200);
			PledgeCrest packet = new PledgeCrest(id, DDSConverter.convertToDDS(getExpImages().get(expId)).array());
			player.sendPacket(packet);
			
			tb.append("<img src=\"Crest.crest_" + Config.SERVER_ID + "_" + id + "\" width=" + width + " height=" + height + ">");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return tb;
		
	}
	
	public TextBuilder getRankIconImageHtmlTag(L2PcInstance player, int rankId, int width, int height)
	{
		
		TextBuilder tb = new TextBuilder();
		try
		{
			
			int id = 500000000 + rankId + (IMG_PREFIX * 200);
			PledgeCrest packet = new PledgeCrest(id, DDSConverter.convertToDDS(getIconImages().get(rankId)).array());
			player.sendPacket(packet);
			
			tb.append("<img src=\"Crest.crest_" + Config.SERVER_ID + "_" + id + "\" width=" + width + " height=" + height + ">");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return tb;
		
	}
	
	public TextBuilder getRankNameImageHtmlTag(L2PcInstance player, int rankId, int width, int height)
	{
		
		TextBuilder tb = new TextBuilder();
		try
		{
			
			int id = 600000000 + rankId + (IMG_PREFIX * 200);
			PledgeCrest packet = new PledgeCrest(id, DDSConverter.convertToDDS(getNameImages().get(rankId)).array());
			player.sendPacket(packet);
			
			tb.append("<img src=\"Crest.crest_" + Config.SERVER_ID + "_" + id + "\" width=" + width + " height=" + height + ">");
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return tb;
		
	}
	
	/**
	 * @return the _iconImages
	 */
	public FastMap<Integer, File> getIconImages()
	{
		return _iconImages;
	}
	
	/**
	 * @param _iconImages the _iconImages to set
	 */
	public void setIconImages(FastMap<Integer, File> _iconImages)
	{
		this._iconImages = _iconImages;
	}
	
	/**
	 * @return the _nameImages
	 */
	public FastMap<Integer, File> getNameImages()
	{
		return _nameImages;
	}
	
	/**
	 * @param _nameImages the _nameImages to set
	 */
	public void setNameImages(FastMap<Integer, File> _nameImages)
	{
		this._nameImages = _nameImages;
	}
	
	/**
	 * @return the _expImages
	 */
	public FastMap<Integer, File> getExpImages()
	{
		return _expImages;
	}
	
	/**
	 * @param _expImages the _expImages to set
	 */
	public void setExpImages(FastMap<Integer, File> _expImages)
	{
		this._expImages = _expImages;
	}
	
}
