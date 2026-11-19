@echo off
cd /d "%~dp0"
echo You are here:
echo %CD%
echo.
echo --- Terminal Java ---
where java
java -version
echo.
echo --- Trying to run your app ---
java -jar TextGround.jar
pause