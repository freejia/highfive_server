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
 * this program. If not, see <http://L2J.EternityWorld.ru/>.
 */
package com.l2jserver;

import java.util.logging.Logger;

public class ServerInfo
{
	private static final Logger _log = Logger.getLogger(ServerInfo.class.getName());
	
	public static void info()
	{
		_log.info("--------------------------STOPPED LOADING SERVER-------------------------------");
		_log.info("-------------------------------------------------------------------------------");
		_log.info("                          Developed by L2PS Team                               ");
		_log.info("                           Client: High Five                                   ");
		_log.info("               Copyright 2012-2013 (hi5) L2PS Developer Team                   ");
		_log.info("-------------------------------------------------------------------------------");
		_log.info("                                 Website                                       ");
		_log.info("                          http://l2jpsproject.eu                               ");
		_log.info("-------------------------------------------------------------------------------");
		_log.info("                          Please Support on forum :P                           ");
		_log.info("-------------------------------------------------------------------------------");
		_log.info("--------------------------CONTINUE LOADING SERVER------------------------------");
	}
}