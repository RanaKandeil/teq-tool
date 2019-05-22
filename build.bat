@ECHO OFF

SET MAVEN_CMD=mvn clean install
@echo CMD=%MAVEN_CMD%

call %MAVEN_CMD%
GOTO END

:ERROREND
cd %~dp0

:END

