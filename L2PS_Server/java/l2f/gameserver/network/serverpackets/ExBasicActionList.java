package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;

public class ExBasicActionList extends L2GameServerPacket
{
	private static final int[] BasicActions =
	{
		0, // ​​Switch Entry / Exit. (/ Sit, / stand)
		1, // Toggle Run / Walk. (/ Walk, / run)
		2, // ​​Attack the selected goal (s). Click the button while holding Ctrl, to force the attack. (/ Attack, / attackforce)
		3, // Request for trade with the selected player. (/ Trade)
		4, // Select the next target for attack. (/ Targetnext)
		5, // ​​pick up objects placed nearby. (/ Pickup)
		6, // Switch on the target selected player. (/ Assist)
		7, // Invite a selected player in your group. (/ Invite)
		8, // Leave group. (/ Leave)
		9, // If you are the leader of the group, delete the selected player (s) of the group. (/ Dismiss)
		10, // Set up a personal shop to sell items. (/ Vendor)
		11, // Display the window "Selection Group" to find a group or a member of your group. (/ Partymatching)
		12, // Emotion: greet others. (/ Socialhello)
		13, // Emotion: Show that you or someone else won! (/ Socialvictory)
		14, // Emotion: Inspire your allies (/ socialcharge)
		15, // Your pet or follows you, or left in place.
		16, // Attack the target.
		17, // End the current action.
		18, // Find the nearby objects.
		19, // Removes pet in your inventory.
		20, // use a special ability.
		21, // Your Minions either follow you or stay in place.
		22, // Attack the target.
		23, // End the current action.
		24, // Emotion: Reply in the affirmative. (/ Socialyes)
		25, // Emotion: Reply negatively. (/ Socialno)
		26, // Emotion: A bow, as a sign of respect. (/ Socialbow)
		27, // use a special ability.
		28, // Set up a personal shop to purchase items. (/ Buy)
		29, // Emotion: I do not understand what's going on. (/ Socialunaware)
		30, // Emotion: I'm waiting ... (/ Socialwaiting)
		31, // Emotion: From a good laugh. (/ Sociallaugh)
		32, // ​​Toggle between attack / movement.
		33, // Emotion: Applause. (/ Socialapplause)
		34, // Emotion: Show us your best dance. (/ Socialdance)
		35, // Emotion: I am sad. (/ Socialsad)
		36, // Poison Gas Attack.
		37, // Set the personal studio to create objects using recipes Dwarves for a fee. (/ Dwarvenmanufacture)
		38, // Switch to ride / dismount when you are near a pet that you can ride. (/ Mount, / dismount, / mountdismount)
		39, // Friendly exploding corpses.
		40, // Increases score goal (/ evaluate)
		41, // Attack the gates of the castle, the walls or the staffs shot out of a cannon.
		42, // Returns the damage back to the enemy.
		43, // Attack the enemy, creating a swirling vortex.
		44, // Attack the enemy with a powerful explosion.
		45, // ​​Restores MP summoner.
		46, // Attack the enemy, calling the destructive storm.
		47, // At the same time damages the enemy and heals the servant.
		48, // Attack the enemy shot out of a cannon.
		49, // Attack in a fit of rage.
		50, // The selected member of the group becomes the leader. (/ Changepartyleader)
		51, // Create the object, using the usual recipe for a fee. (/ Generalmanufacture)
		52, // ​​Removes ties with EP and releases it.
		53, // Move to the target.
		54, // Move to the target.
		55, // switch recording and stop recording repeats. (/ Start_videorecording, / end_videorecording, / startend_videorecording)
		56, // Invite a selected target in the channel team. (/ Channelinvite)
		57, // ​​Displays a message of personal shopping, and personal workshop that contain the search word. (/ Findprivatestore)
		58, // Call another player to a duel. (/ Duel)
		59, // ​​Cancel the duel's loss. (/ Withdraw)
		60, // ​​Call another group to a duel. (/ Partyduel)
		61, // Opens personal store to sell packages (/ packagesale)
		62, // Charming posture (/ charm)
		63, // ​​Starts a fun and simple mini-game that you can play at any time. (Command: / minigame)
		64, // Opens a free teleport, which allows you to freely move between locations with teleporters. (Command: / teleportbookmark)
		65, // ​​Report suspicious behavior of an object whose actions suggest the use of bot programs.
		66, // Pose "Confusion" (command: / shyness)
		67, // Management of ship
		68, // ​​Stop control of the ship
		69, // ​​Departure ship
		70, // Descent from the ship
		71, // Bow
		72, // ​​Give Five
		73, // Dance Together
		1000, // Attack the gates of the castle walls and the headquarters of a powerful blow.
		1001, // Reckless, but powerful attack, use it with care.
		1002, // provoke others to attack you.
		1003, // An unexpected attack that deals damage and stuns the opponent.
		1004, // Instant significantly increases P. Def. Def. and Mag. Def. Use this skill can not move.
		1005, // Magic Attack
		1006, // Restores HP pet.
		1007, // In the case of a successful application to temporarily increase attack power group, and a chance for a critical hit.
		1008, // Increases P. Def. Attack. and the accuracy of your group.
		1009, // There is a chance to remove the curse from the group.
		1010, // Increases MP regeneration of your group.
		1011, // Decreases the cooldown of your team.
		1012, // Removes the curse from your group.
		1013, // Taunt opponent and hit, curse, reducing Phys. Def. and Mag. Def.
		1014, // Provokes to attack a lot of enemies and hit with a curse, lowering their Phys. Def. and Mag. Def.
		1015, // Sacrifices HP to regenerate HP selected target.
		1016, // ​​Down on the opponent powerful critical attack.
		1017, // Stunning explosion, dealing damage and stunning enemy.
		1018, // Overlay deadly curse Absorbs the enemy's HP.
		1019, // skill number 2, used Cat
		1020, // skill number 2 used Meow
		1021, // skill number 2 used Kai
		1022, // skill number 2 used Jupiter
		1023, // skill number 2 used Mirage
		1024, // skill number 2 used Bekarev
		1025, // skill number 2 used Shadow
		1026, // skill number one used by Shadow
		1027, // skill number 2 used Hecate
		1028, // skill number 1 used resurrections
		1029, // skill number 2 used resurrections
		1030, // skill number 2 used vicious
		1031, // The King of Cats: The powerful cutting attack. Maximum damage.
		1032, // The King of Cats: Cuts nearby enemies while rotating in the air. Maximum damage.
		1033, // The King of Cats: Freezes enemies standing close
		1034, // Magnus: Slam hind legs, striking and stunning the enemy. Maximum damage.
		1035, // Magnus: strike the multiple objectives huge mass of water.
		1036, // Shadow Lord: BURST corpse, striking nearby enemies.
		1037, // Shadow Lord: The blades in each hand inflict devastating damage. Maximum damage.
		1038, // Curse of nearby enemies, reducing their toxic and speed. Attack.
		1039, // Swoop Cannon: Fires a missile at a short distance. Consumes 4 units. Sparkling powder.
		1040, // Swoop Cannon: Fires a missile at a long distance. Consumes 5 units. Sparkling powder.
		1041, // Horrible bite the enemy
		1042, // Scratch the enemy with both paws. Causes bleeding.
		1043, // Suppress the enemy with a powerful roar
		1044, // awakens secret power
		1045, // Decreases P. Def. Spd. / Def. Attack. have standing nearby enemies.
		1046, // ​​speed decreases. Spd. / Speed. Mag. have standing nearby enemies.
		1047, // Horrible bite the enemy
		1048, // Brings double damage and stuns the enemy at the same time.
		1049, // Breathes fire in your direction.
		1050, // Suppress the enemies' powerful roar.
		1051, // Increases max. number of HP.
		1052, // Increases max. the number of MP.
		1053, // Increases the speed. Attack.
		1054, // Increases spell casting speed.
		1055, // Decreases the MP cost of the selected target. Consumes Runestones.
		1056, // Increases M. Def. Attack.
		1057, // Rank Increases critical strike force and magical attacks
		1058, // Increases the critical attack.
		1059, // Increases the critical strike chance
		1060, // Increases Accuracy
		1061, // A strong attack from ambush. Can only be used in the application of skill "Awakening".
		1062, // Quick double attack
		1063, // Strong twisting attack does not just damage, but also stuns the enemy.
		1064, // Falling from the sky stones do damage to enemies.
		1065, // prints out the latent state
		1066, // Friendly thunderous forces
		1067, // Quick magic attack enemies in sight
		1068, // Attacks multiple enemies Force Lightning
		1069, // hit out of the ambush. Can only be used in the application of skill "Awakening".
		1070, // Can not impose positive effects on the wearer. There are 5 minutes.
		1071, // Attacks on the project
		1072, // Powerful penetrating attack on the facility
		1073, // Attack the enemy and scattered their ranks like a tornado hit
		1074, // Attack the enemy standing in front of a powerful throw spears
		1075, // Victory cry, increasing their own skills
		1076, // Attacks on the project
		1077, // Attack the enemy standing in front of the internal energy
		1078, // Attack standing in front of the enemy with electricity
		1079, // The loud cry, increasing their own skills
		1080, // Fast closer to the enemy and inflicts damage
		1081, // Removes negative effects from the facility
		1082, // Throws flame
		1083, // The powerful bite, inflicting damage to the enemy
		1084, // Toggle between the attacking / defensive mode
		1086, // Limit the number of positive effects to one
		1087, // Increases dark side to 25
		1088, // Trims important skills
		1089, // Attack standing in front of the enemy by the tail.
		1090, // Horrible bite the enemy
		1091, // about to throw the opponent to the horror and makes escape from the battlefield.
		1092, // Increases movement speed.
		5000, // You can pat Rudolph. Fills the scale of loyalty by 25%. Can not be used during the transformation!
		5001, // Increases Max. HP, Max. MP and movement speed by 20%, resistance to de-buff by 10%. Reuse time: 10 min. When using the skill spent 3 Essences Rose. Can not be used with the Beyond Temptation. Duration: 5 min.
		5002, // Increases Max. HP / MP / CP, Phys. Def. and Mag. Def. by 30%, movement speed by 20%, P. Def. Attack. by 10% and M. Def. Attack. by 20% and decreases MP consumption by 15%. Reuse time: 40 min. When using the skill consumes 10 Essences Rose. Duration: 20 min.
		5003, // to strike the enemies of the power of thunder.
		5004, // to strike the enemies standing near a lightning magic attack.
		5005, // to strike the enemies' power of thunder.
		5006, // Do not allow the owner to apply any effects. Duration: 5 min.
		5007, // Pet of piercing the enemy in deadly attacks.
		5008, // Attacks nearby enemies.
		5009, // thrust the sword into the ranks of the enemies vperedistoyaschego.
		5010, // Enhances your skills.
		5011, // Attacks the enemy with a powerful blow.
		5012, // Brings down the accumulated energy in the body at the rows vperedistoyaschego enemies.
		5013, // Explodes shock wave at vperedistoyaschego enemy.
		5014, // significantly enhances their skills.
		5015, // Change the attacker / auxiliary state pet.
	};
	
	private static final int[] TransformationActions =
	{
		1, // Toggle Run / Walk. (/ Walk, / run)
		2, // ​​Attack the selected goal (s). Click the button while holding Ctrl, to force the attack. (/ Attack, / attackforce)
		3, // Request for trade with the selected player. (/ Trade)
		4, // Select the next target for attack. (/ Targetnext)
		5, // ​​pick up objects placed nearby. (/ Pickup)
		6, // Switch on the target selected player. (/ Assist)
		7, // Invite a selected player in your group. (/ Invite)
		8, // Leave group. (/ Leave)
		9, // If you are the leader of the group, delete the selected player (s) of the group. (/Dismiss)
		11, // Display the window "Selection Group" to find a group or a member of your group. (/Partymatching)
		15, // Your pet or follows you, or left in place.
		16, // Attack the target.
		17, // End the current action.
		18, // Find the nearby objects.
		19, // Removes pet in your inventory.
		21, // Your Minions either follow you or stay in place.
		22, // Attack the target.
		23, // End the current action.
		40, // Increases score goal (/ evaluate)
		50, // The selected member of the group becomes the leader. (/ Changepartyleader)
		52, // ​​Removes ties with EP and releases it.
		53, // Move to the target.
		54, // Move to the target.
		55, // switch recording and stop recording repeats. (/ Start_videorecording, / end_videorecording, / startend_videorecording)
		56, // Invite a selected target in the channel team. (/ Channelinvite)
		57, // ​​Displays a message of personal shopping, and personal workshop that contain the search word. (/ Findprivatestore)
		63, // ​​Starts a fun and simple mini-game that you can play at any time. (Command: / minigame)
		64, // Opens a free teleport, which allows you to freely move between locations with teleporters. (Command: / freeteleport)
		65, // report suspicious behavior of an object whose actions suggest the use of BOT-program.
		67, // Management of ship
		68, // ​​Stop control of the ship
		69, // ​​Departure ship
		70, // Descent from the ship
		1000, // Attack the gates of the castle walls and the headquarters of a powerful blow.
		1001, // Reckless, but powerful attack, use it with care.
		1002, // provoke others to attack you.
		1003, // An unexpected attack that deals damage and stuns the opponent.
		1004, // Instant significantly increases P. Def. Def. and Mag. Def. Use this skill can not move.
		1005, // Magic Attack
		1006, // Restores HP pet.
		1007, // In the case of a successful application to temporarily increase attack power group, and a chance for a critical hit.
		1008, // Increases P. Def. Attack. and the accuracy of your group.
		1009, // There is a chance to remove the curse from the group.
		1010, // Increases MP regeneration of your group.
		1011, // Decreases the cooldown of your team.
		1012, // Removes the curse from your group.
		1013, // Taunt opponent and hit, curse, reducing Phys. Def. and Mag. Def.
		1014, // Provokes to attack a lot of enemies and hit with a curse, lowering their Phys. Def. and Mag. Def.
		1015, // Sacrifices HP to regenerate HP selected target.
		1016, // to strike the opponent's powerful critical attack.
		1017, // Stunning explosion, dealing damage and stunning enemy.
		1018, // Overlay deadly curse Absorbs the enemy's HP.
		1019, // skill number 2, used Cat
		1020, // skill number 2 used Meow
		1021, // skill number 2 used Kai
		1022, // skill number 2 used Jupiter
		1023, // skill number 2 used Mirage
		1024, // skill number 2 used Bekarev
		1025, // skill number 2 used Shadow
		1026, // skill number one used by Shadow
		1027, // skill number 2 used Hecate
		1028, // skill number 1 used resurrections
		1029, // skill number 2 used resurrections
		1030, // skill number 2 used vicious
		1031, // The King of Cats: The powerful cutting attack. Maximum damage.
		1032, // The King of Cats: Cuts nearby enemies while rotating in the air. Maximum damage.
		1033, // The King of Cats: Freezes enemies standing close
		1034, // Magnus: Slam hind legs, striking and stunning the enemy. Maximum damage.
		1035, // Magnus: strike the multiple objectives huge mass of water.
		1036, // Shadow Lord: BURST corpse, striking nearby enemies.
		1037, // Shadow Lord: The blades in each hand inflict devastating damage. Maximum damage.
		1038, // Curse of nearby enemies, reducing their toxic and speed. Attack.
		1039, // Swoop Cannon: Fires a missile at a short distance. Consumes 4 units. Sparkling powder.
		1040, // Swoop Cannon: Fires a missile at a long distance. Consumes 5 units. Sparkling powder.
		1041, // Horrible bite the enemy
		1042, // Scratch the enemy with both paws. Causes bleeding.
		1043, // Suppress the enemy with a powerful roar
		1044, // awakens secret power
		1045, // Decreases P. Def. Spd. / Def. Attack. have standing nearby enemies.
		1046, // ​​speed decreases. Spd. / Speed. Mag. have standing nearby enemies.
		1047, // Horrible bite the enemy
		1048, // Brings double damage and stuns the enemy at the same time.
		1049, // Breathes fire in your direction.
		1050, // Suppress the enemies' powerful roar.
		1051, // Increases max. number of HP.
		1052, // Increases max. the number of MP.
		1053, // Increases the speed. Attack.
		1054, // Increases spell casting speed.
		1055, // Decreases the MP cost of the selected target. Consumes Runestones.
		1056, // Increases M. Def. Attack.
		1057, // Rank Increases critical strike force and magical attacks
		1058, // Increases the critical attack.
		1059, // Increases the critical strike chance
		1060, // Increases Accuracy
		1061, // A strong attack from ambush. Can only be used in the application of skill "Awakening".
		1062, // Quick double attack
		1063, // Strong twisting attack does not just damage, but also stuns the enemy.
		1064, // Falling from the sky stones do damage to enemies.
		1065, // prints out the latent state
		1066, // Friendly thunderous forces
		1067, // Quick magic attack enemies in sight
		1068, // Attacks multiple enemies Force Lightning
		1069, // hit out of the ambush. Can only be used in the application of skill "Awakening".
		1070, // Can not impose positive effects on the wearer. There are 5 minutes.
		1071, // Attacks on the project
		1072, // Powerful penetrating attack on the facility
		1073, // Attack the enemy and scattered their ranks like a tornado hit
		1074, // Attack the enemy standing in front of a powerful throw spears
		1075, // Victory cry, increasing their own skills
		1076, // Attacks on the project
		1077, // Attack the enemy standing in front of the internal energy
		1078, // Attack standing in front of the enemy with electricity
		1079, // The loud cry, increasing their own skills
		1080, // Fast closer to the enemy and inflicts damage
		1081, // Removes negative effects from the facility
		1082, // Throws flame
		1083, // The powerful bite, inflicting damage to the enemy
		1084, // Toggle between the attacking / defensive mode
		1086, // Limit the number of positive effects to one
		1087, // Increases dark side to 25
		1088, // Trims important skills
		1089, // Attack standing in front of the enemy by the tail.
		1090, // Horrible bite the enemy
		1091, // about to throw the opponent to the horror and makes escape from the battlefield.
		1092, // Increases movement speed.
		5000, // You can pat Rudolph. Fills the scale of loyalty by 25%. Can not be used during the transformation!
		5001, // Increases Max. HP, Max. MP and movement speed by 20%, resistance to de-buff by 10%. Reuse time: 10 min. When using the skill spent 3 Essences Rose. Can not be used with the Beyond Temptation. Duration: 5 min.
		5002, // Increases Max. HP / MP / CP, Phys. Def. and Mag. Def. by 30%, movement speed by 20%, P. Def. Attack. by 10% and M. Def. Attack. by 20% and decreases MP consumption by 15%. Reuse time: 40 min. When using the skill consumes 10 Essences Rose. Duration: 20 min.
		5003, // to strike the enemies of the power of thunder.
		5004, // to strike the enemies standing near a lightning magic attack.
		5005, // to strike the enemies' power of thunder.
		5006, // Do not allow the owner to apply any effects. Duration: 5 min.
		5007, // Pet of piercing the enemy in deadly attacks.
		5008, // Attacks nearby enemies.
		5009, // thrust the sword into the ranks of the enemies vperedistoyaschego.
		5010, // Enhances your skills.
		5011, // Attacks the enemy with a powerful blow.
		5012, // Brings down the accumulated energy in the body at the rows vperedistoyaschego enemies.
		5013, // Explodes shock wave at vperedistoyaschego enemy.
		5014, // significantly enhances their skills.
		5015, // Change the attacker / auxiliary state pet ..
	};
	
	private final int[] actions;
	
	public ExBasicActionList(Player activeChar)
	{
		actions = activeChar.getTransformation() == 0 ? BasicActions : TransformationActions;
	}
	
	@Override
	protected void writeImpl()
	{
		writeEx(0x5f);
		writeDD(actions, true);
	}
}