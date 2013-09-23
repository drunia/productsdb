@echo off

rem Build productsdb.jar
rem 23.09.2013 drunia

setlocal enabledelayedexpansion

cd /d "%~dp0"

set classes=sqlite-jdbc-3.7.2.jar
set sources=src
set classout=bin
set target=productsdb.jar
set errors=0

rem compile sources
javac -sourcepath %sources% -classpath %classes% -d %classout% src/ua/drunia/prodsdb/ProductsDB.java
set /a errors="%errors% + %ERRORLEVEL%"
if %errors% EQU 0 echo Compile OK.

rem build jar
jar -cmvf manifest.mf %target% -C ./bin ua >nul
set /a errors="%errors% + %ERRORLEVEL%"
if %errors% EQU 0 echo Build OK.

echo.
if %errors% EQU 0 (
	set /p answ="Run %target%? [y/n]:"
	if "!answ!"=="y" java -jar %target%
) else (
	echo Build filed... ((
)

exit /b %errors%