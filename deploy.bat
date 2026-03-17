@echo off
setlocal enabledelayedexpansion

set SRC=src\main\java
set BUILD=build
set TOMCAT_LIB=D:\logiciel\apache-tomcat-10.1.28\lib
set TOMCAT_PATH=D:\logiciel\apache-tomcat-10.1.28
set WEBAPP_SOURCE=src\main\webapp
set APP_LIB=%WEBAPP_SOURCE%\WEB-INF\lib
set FRAMEWORK_JAR=%APP_LIB%\framework.jar
set WAR_NAME=reservation.war

echo Nettoyage...
if exist %BUILD% rmdir /s /q %BUILD% >nul 2>&1
mkdir %BUILD%\WEB-INF\classes >nul 2>&1
mkdir %BUILD%\WEB-INF\lib >nul 2>&1

echo Compilation javac...
setlocal enabledelayedexpansion
set "FILES="
for /r %SRC% %%F in (*.java) do (
    set "FILES=!FILES! "%%F""
)
javac -encoding UTF-8 -classpath "%TOMCAT_LIB%\servlet-api.jar;%TOMCAT_LIB%\el-api.jar;%FRAMEWORK_JAR%;%APP_LIB%\*" -parameters -d %BUILD%\WEB-INF\classes !FILES!
if errorlevel 1 (
    echo Erreur compilation!
    pause
    exit /b 1
)

echo Copie libs...
copy /y %FRAMEWORK_JAR% %BUILD%\WEB-INF\lib\ >nul 2>&1
xcopy /s /y /i "%APP_LIB%\*.jar" "%BUILD%\WEB-INF\lib\" >nul 2>&1

echo Copie WEB-INF...
xcopy /s /y /i "%WEBAPP_SOURCE%\WEB-INF\*" "%BUILD%\WEB-INF\" >nul 2>&1

echo Copie resources...
xcopy /s /y /i "%WEBAPP_SOURCE%\css" "%BUILD%\css\" >nul 2>&1
xcopy /s /y /i "%WEBAPP_SOURCE%\js" "%BUILD%\js\" >nul 2>&1
if exist "%WEBAPP_SOURCE%\*.jsp" copy /y "%WEBAPP_SOURCE%\*.jsp" %BUILD%\ >nul 2>&1

echo Creation WAR...
cd %BUILD%
jar cf ..\%WAR_NAME% . >nul 2>&1
cd ..

echo Deploiement...
copy /y %WAR_NAME% "%TOMCAT_PATH%\webapps\" >nul 2>&1
echo OK

