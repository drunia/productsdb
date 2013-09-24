@echo off

rem Build jar-app script
rem 23.09.2013 drunia

setlocal enabledelayedexpansion

cls
cd /d "%~dp0"

set classes=sqlite-jdbc-3.7.2.jar
set sources=src
set classout=bin
set target=productsdb.jar
set mainclass=src/ua/drunia/prodsdb/ProductsDB.java
set errors=0

rem compile sources
if not exist %classout% mkdir %classout%
javac -sourcepath %sources% -classpath %classes% -d %classout% %mainclass%
set /a errors="%errors% + %ERRORLEVEL%"
if %errors% EQU 0 echo Compile OK.

rem build jar
jar -cmvf manifest.mf %target% -C ./bin ua >nul
set /a errors="%errors% + %ERRORLEVEL%"
if %errors% EQU 0 echo Build OK.

echo.
if %errors% EQU 0 (
	color a
	set /p answ="Run %target%? [y/n]:"
	if "!answ!"=="y" (
		color
		java -jar %target%
	)
) else (
	color c
	echo Build filed... ((
	pause >nul
)

color
exit /b %errors%