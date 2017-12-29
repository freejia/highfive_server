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

package com.l2jserver.gameserver.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.instancemanager.Fakes.FakePc;

/**
 * @author RobíkBobík
 */
public class FakePcsTable
{
	private static Logger _LOGGER = Logger.getLogger(FakePcsTable.class.getName());
	
	private final FastMap<Integer, FakePc> _fakePcs = new FastMap<>();
	
	private FakePcsTable()
	{
		try
		{
			loadData();
		}
		catch (SQLException sqlex)
		{
			_LOGGER.log(Level.WARNING, "Failed to load fake pcs table!", sqlex);
		}
	}
	
	private void loadData() throws SQLException
	{
		_fakePcs.clear();
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM fake_pcs");
			ResultSet rset = stmt.executeQuery();
			
			FakePc fpc = null;
			
			while (rset.next())
			{
				fpc = new FakePc();
				
				int npcId = rset.getInt("npc_id");
				
				fpc.name = rset.getString("name");
				fpc.race = rset.getInt("race");
				fpc.sex = rset.getInt("sex");
				fpc.clazz = rset.getInt("class");
				fpc.pdUnder = rset.getInt("pd_under");
				fpc.pdHead = rset.getInt("pd_head");
				fpc.pdRHand = rset.getInt("pd_rhand");
				fpc.pdLHand = rset.getInt("pd_lhand");
				fpc.pdGloves = rset.getInt("pd_gloves");
				fpc.pdChest = rset.getInt("pd_chest");
				fpc.pdLegs = rset.getInt("pd_legs");
				fpc.pdFeet = rset.getInt("pd_feet");
				fpc.pdCloak = rset.getInt("pd_cloak");
				fpc.pdHair = rset.getInt("pd_hair");
				fpc.pdHair2 = rset.getInt("pd_hair2");
				fpc.pdRBracelet = rset.getInt("pd_rbracelet");
				fpc.pdLBracelet = rset.getInt("pd_lbracelet");
				fpc.pdDeco1 = rset.getInt("pd_deco1");
				fpc.pdDeco2 = rset.getInt("pd_deco2");
				fpc.pdDeco3 = rset.getInt("pd_deco3");
				fpc.pdDeco4 = rset.getInt("pd_deco4");
				fpc.pdDeco5 = rset.getInt("pd_deco5");
				fpc.pdDeco6 = rset.getInt("pd_deco6");
				fpc.pdBelt = rset.getInt("pd_belt");
				fpc.pdUnderAug = rset.getInt("pd_under_aug");
				fpc.pdHeadAug = rset.getInt("pd_head_aug");
				fpc.pdRHandAug = rset.getInt("pd_rhand_aug");
				fpc.pdLHandAug = rset.getInt("pd_lhand_aug");
				fpc.pdGlovesAug = rset.getInt("pd_gloves_aug");
				fpc.pdChestAug = rset.getInt("pd_chest_aug");
				fpc.pdLegsAug = rset.getInt("pd_legs_aug");
				fpc.pdFeetAug = rset.getInt("pd_feet_aug");
				fpc.pdCloakAug = rset.getInt("pd_cloak_aug");
				fpc.pdHairAug = rset.getInt("pd_hair_aug");
				fpc.pdHair2Aug = rset.getInt("pd_hair2_aug");
				fpc.pdRBraceletAug = rset.getInt("pd_rbracelet_aug");
				fpc.pdLBraceletAug = rset.getInt("pd_lbracelet_aug");
				fpc.pdDeco1Aug = rset.getInt("pd_deco1_aug");
				fpc.pdDeco2Aug = rset.getInt("pd_deco2_aug");
				fpc.pdDeco3Aug = rset.getInt("pd_deco3_aug");
				fpc.pdDeco4Aug = rset.getInt("pd_deco4_aug");
				fpc.pdDeco5Aug = rset.getInt("pd_deco5_aug");
				fpc.pdDeco6Aug = rset.getInt("pd_deco6_aug");
				fpc.pdBeltAug = rset.getInt("pd_belt_aug");
				fpc.pvpFlag = rset.getBoolean("pvp_flag");
				fpc.karma = rset.getInt("karma");
				fpc.hairStyle = rset.getInt("hair_style");
				fpc.hairColor = rset.getInt("hair_color");
				fpc.face = rset.getInt("face");
				fpc.title = rset.getString("title");
				fpc.clanId = rset.getInt("clan_id");
				fpc.clanCrestId = rset.getInt("clan_crest_id");
				fpc.allyId = rset.getInt("ally_id");
				fpc.allyCrestId = rset.getInt("ally_crest_id");
				fpc.sitWhileIdle = rset.getBoolean("sit_while_idle");
				fpc.invisible = rset.getBoolean("invisible");
				fpc.mount = rset.getByte("mount");
				fpc.recomHave = rset.getShort("recom_have");
				fpc.mountNpcId = rset.getInt("mount_npc_id");
				fpc.team = rset.getByte("team");
				fpc.clanCrestLargeId = rset.getInt("clan_crest_large_id");
				fpc.hero = rset.getBoolean("hero");
				fpc.fishing = rset.getBoolean("fishing");
				fpc.fishingX = rset.getInt("fishing_x");
				fpc.fishingY = rset.getInt("fishing_y");
				fpc.fishingZ = rset.getInt("fishing_z");
				fpc.nameColor = Integer.decode("0x" + rset.getString("name_color"));
				fpc.pledgeClass = rset.getInt("pledge_class");
				fpc.pledgeType = rset.getInt("pledge_type");
				fpc.titleColor = Integer.decode("0x" + rset.getString("title_color"));
				fpc.reputationScore = rset.getInt("reputation_score");
				fpc.transformId = rset.getInt("transform_id");
				fpc.agathionId = rset.getInt("agathion_id");
				
				_fakePcs.put(npcId, fpc);
			}
			
			rset.close();
			stmt.close();
		}
	}
	
	public void reloadData() throws SQLException
	{
		loadData();
	}
	
	public FakePc getFakePc(int npcId)
	{
		return _fakePcs.get(npcId);
	}
	
	public static FakePcsTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final FakePcsTable _instance = new FakePcsTable();
	}
}