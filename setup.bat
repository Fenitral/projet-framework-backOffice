@echo off
echo ========================================
echo     INSTALLATION DES PREREQUIS
echo ========================================

echo.
echo Ce script vous aide a configurer l'environnement pour tester le projet.
echo.

echo 1. POSTGRESQL
echo -------------
echo - Telechargez PostgreSQL depuis : https://www.postgresql.org/download/windows/
echo - Installez avec les parametres par defaut (port 5432)
echo - Retenez le mot de passe de l'utilisateur 'postgres'
echo.

echo 2. APACHE TOMCAT
echo ----------------
echo - Telechargez Tomcat 10.x depuis : https://tomcat.apache.org/download-10.cgi
echo - Extraire dans D:\apache-tomcat-10.1.28
echo - Ou modifier le chemin dans deploy.bat
echo.

echo 3. JDK
echo ------
echo - Telechargez JDK 17+ depuis : https://www.oracle.com/java/technologies/downloads/
echo - Installez dans C:\Program Files\Java\jdk-17
echo - Ou modifier le chemin dans deploy.bat
echo.

echo 4. DRIVER POSTGRESQL
echo --------------------
echo - Telechargez le driver JDBC PostgreSQL depuis :
echo   https://jdbc.postgresql.org/download/postgresql-42.7.2.jar
echo - Placez le fichier dans le dossier 'lib\' de ce projet
echo.

echo 5. CONFIGURATION BASE DE DONNEES
echo ---------------------------------
echo - Executez PostgreSQL
echo - Lancez pgAdmin ou psql
echo - Executez le fichier script.sql pour creer la base et les tables
echo.

echo 6. LANCEMENT DU PROJET
echo ----------------------
echo - Executez deploy.bat
echo - Le navigateur s'ouvrira automatiquement
echo.

echo ========================================

echo.
echo Voulez-vous continuer avec le deploiement ? (O/N)
set /p choice=Votre choix : 

if /i "%choice%"=="O" (
    call deploy.bat
) else (
    echo Configuration terminee. Lancez deploy.bat quand vous serez pret.
)

pause