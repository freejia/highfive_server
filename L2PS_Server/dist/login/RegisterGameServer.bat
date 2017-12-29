@echo off
title L2PS - Register Game Server
color 17
java -Djava.util.logging.config.file=console.cfg -cp ./../libs/*;L2PS_Connect.jar com.l2jserver.tools.gsregistering.BaseGameServerRegister -c
pause