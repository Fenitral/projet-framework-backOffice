@echo off
echo ========================================
echo   Compilation et deploiement du projet
echo ========================================

REM Variables de configuration
set TOMCAT_PATH=D:\apache-tomcat-10.1.28
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PROJECT_NAME=reservation-system

REM Verification de l'existence de Tomcat
if not exist "%TOMCAT_PATH%" (
    echo ERREUR: Tomcat n'est pas trouve a %TOMCAT_PATH%
    echo Veuillez modifier TOMCAT_PATH dans ce fichier
    pause
    exit /b 1
)

REM Verification de Java
if not exist "%JAVA_HOME%" (
    echo ERREUR: Java n'est pas trouve a %JAVA_HOME%
    echo Veuillez modifier JAVA_HOME dans ce fichier
    pause
    exit /b 1
)

echo.
echo 0. Arret de Tomcat...
set "CATALINA_HOME=%TOMCAT_PATH%"
call "%TOMCAT_PATH%\bin\shutdown.bat" 2>nul
timeout /t 3 /nobreak >nul

echo.
echo 1. Nettoyage des anciens fichiers...
if exist "build" rmdir /s /q build
if exist "%TOMCAT_PATH%\webapps\%PROJECT_NAME%" rmdir /s /q "%TOMCAT_PATH%\webapps\%PROJECT_NAME%"
if exist "%TOMCAT_PATH%\webapps\%PROJECT_NAME%.war" del "%TOMCAT_PATH%\webapps\%PROJECT_NAME%.war"
if exist "%TOMCAT_PATH%\work\Catalina\localhost\%PROJECT_NAME%" rmdir /s /q "%TOMCAT_PATH%\work\Catalina\localhost\%PROJECT_NAME%"

echo.
echo 2. Creation des repertoires de build...
mkdir build\WEB-INF\classes
mkdir build\WEB-INF\lib

echo.
echo 3. Compilation de tous les fichiers Java...
set "SRC=backend\framework\src"
set "CP=backend\framework\lib\servlet-api.jar;backend\framework\lib\postgresql-42.7.2.jar"

"%JAVA_HOME%\bin\javac" -cp "%CP%" -d build\WEB-INF\classes -sourcepath "%SRC%" "%SRC%\com\annotations\Api.java" "%SRC%\com\annotations\Authorized.java" "%SRC%\com\annotations\ControllerAnnotation.java" "%SRC%\com\annotations\GetMapping.java" "%SRC%\com\annotations\HandleUrl.java" "%SRC%\com\annotations\Param.java" "%SRC%\com\annotations\PostMapping.java" "%SRC%\com\annotations\Role.java" "%SRC%\com\annotations\Session.java" "%SRC%\com\classes\ModelView.java" "%SRC%\com\models\Hotel.java" "%SRC%\com\models\Reservation.java" "%SRC%\com\exceptions\HttpException.java" "%SRC%\com\exceptions\BadRequestException.java" "%SRC%\com\exceptions\InternalServerErrorException.java" "%SRC%\com\exceptions\NotFoundException.java" "%SRC%\com\interfaces\SessionUserProvider.java" "%SRC%\com\utils\ApiResponse.java" "%SRC%\com\utils\PropertiesUtil.java" "%SRC%\com\utils\DatabaseConnection.java" "%SRC%\com\utils\JsonSerializer.java" "%SRC%\com\utils\UrlPattern.java" "%SRC%\com\utils\SessionMap.java" "%SRC%\com\utils\MappingHandler.java" "%SRC%\com\utils\ParametersHandler.java" "%SRC%\com\utils\ObjectBinder.java" "%SRC%\com\utils\AuthManager.java" "%SRC%\com\utils\ErrorHandler.java" "%SRC%\com\utils\FileStorage.java" "%SRC%\com\utils\ScanningUrl.java" "%SRC%\com\utils\MappingExecutor.java" "%SRC%\com\controllers\ReservationController.java" "%SRC%\com\framework\FrontServlet.java"

if errorlevel 1 (
    echo ERREUR: Echec de la compilation
    pause
    exit /b 1
)

echo.
echo 4. Copie des ressources...
copy backend\framework\src\framework.properties build\WEB-INF\classes\
copy src\main\webapp\*.jsp build\
copy src\main\webapp\WEB-INF\web.xml build\WEB-INF\

REM Copie des librairies necessaires
copy backend\framework\lib\servlet-api.jar build\WEB-INF\lib\
copy backend\framework\lib\postgresql-42.7.2.jar build\WEB-INF\lib\

echo.
echo 5. Creation du fichier WAR...
cd build
"%JAVA_HOME%\bin\jar" -cvf ..\%PROJECT_NAME%.war .
cd ..

echo.
echo 6. Deploiement sur Tomcat...
copy %PROJECT_NAME%.war "%TOMCAT_PATH%\webapps\"

echo.
echo ========================================
echo   Deploiement termine !
echo ========================================
echo.
echo Le WAR a ete copie dans Tomcat.
echo.
echo Demarrez Tomcat manuellement si besoin:
echo   %TOMCAT_PATH%\bin\startup.bat
echo.
echo Puis accedez a:
echo   http://localhost:8081/%PROJECT_NAME%
echo.
pause