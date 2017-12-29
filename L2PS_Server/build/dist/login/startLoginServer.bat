@echo off
title L2PS Login Server Console

:start
echo Starting L2PS Login Server.
echo.

java -Xms128m -Xmx128m -cp ./../libs/*;L2PS_Connect.jar com.l2jserver.loginserver.L2LoginServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end

:restart
echo.
echo Admin Restarted Login Server.
echo.
goto start

:error
echo.
echo Login Server terminated abnormally!
echo.

:end
echo.
echo Login Server Terminated.
echo.
pause