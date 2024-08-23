@echo off
REM CLASSIFICATION: UNCLASSIFIED

set MSPCCS_DATA=..\..\data
set path=..\..\CCS\win;.;%path%

javaw -Xss1024k -jar MSPCCS.jar
