@echo off

SET RUNPATH=$INSTALL_PATH
cd %RUNPATH%
java -jar Evolvo.jar %1 %2 %3 %4 %5 %6 %7 %8 %9

@echo on
