@echo off
REM CLASSIFICATION: UNCLASSIFIED

REM set library path and data path
set PATH=.;..\..\CCS\win;%PATH%
set MSPCCS_DATA=..\..\data

REM run the test driver
javaw -jar MSPCCS_SpreadsheetTester.jar