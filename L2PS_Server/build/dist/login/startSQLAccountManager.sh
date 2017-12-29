#!/bin/sh
java -Djava.util.logging.config.file=console.cfg -cp ./../libs/*:L2PS_Connect.jar com.l2jserver.tools.accountmanager.SQLAccountManager
