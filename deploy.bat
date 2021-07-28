echo off
@echo Deploiment de la nouvelle version en cours. Cela supprimera le conteneur Docker actuel et creera un nouveau contenant la nouvelle version de l'application.
@echo ATTENTION ! Pour que ce script fonctionne il faut que le fichier deploy.bat soit place dans la racine du dossier contenant l'application.
@set /p demarr= Voulez-vous continuer ? Oui (o) Non (n) : 
if %demarr% == n ( goto:EOF )

@rem Arrêt et suppression de l'ancien conteneur
@echo Arret et suppression de l'ancien conteneur ... 
@cd "\Applications BRP\BRP_editeur_app\Scripts Docker\"
call "Supprimer Conteneur.bat"

@cd "\Applications BRP\BRP_editeur_app\"

@echo Supression des fichiers locaux ...
:choixSuppression
@echo.
@set /p choix= Veuillez choisir une option. Supprimer les projets (s). Garder les projets (g) : 
if %choix% == s ( goto:suppressionComplete )
if %choix% == g ( goto:suppressionSelective ) else ( goto:mauvaisChoix )

:mauvaisChoix
@echo Mauvais choix. Veuilllez reesayer.
goto:choixSuppression

@rem Suppression des anciens fichiers
:suppressionComplete
@rd /S /Q "BRP_editeur_rapports"
@echo Suppression des projets dans la base de donnees ...
@mysql -u root -proot --port=3307 bd_brp -e "TRUNCATE TABLE BD_BRP.PROJET"
goto:copie

:suppressionSelective
@cd "BRP_editeur_rapports"
@rd /S /Q "fileSaver"
@rd /S /Q "META-INF"
@rd /S /Q "script"
@rd /S /Q "style"
@rd /S /Q "stylesheet"
@rd /S /Q "WEB-INF"
@del "*.html"
goto:copie

:copie
@rem Copie du contenu du fichier war
@mkdir  "\Applications BRP\war"
@rem On se remet dans le dossier où est localisé le script
@cd /d %~dp0
@copy "code\BRP_front_end\target\BRP_front_end-1.0-SNAPSHOT.war" "\Applications BRP\war\"
@cd "\Applications BRP\war"
@7z x "BRP_front_end-1.0-SNAPSHOT.war" -aoa
@xcopy "fileSaver" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\fileSaver\" /E /H
@xcopy "META-INF" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\META-INF\" /E /H
@xcopy "script" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\script\" /E /H
@xcopy "style" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\style\" /E /H
@xcopy "stylesheet" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\stylesheet\" /E /H
@xcopy "WEB-INF" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\WEB-INF\" /E /H
@copy "Dashboard.html" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\"
@copy "Inscription.html" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\"
@copy "index.html" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\"
@rem Les fichiers suivants ne seront copiés que dans le cas d'une suppression complète, où les projets ne sont pas gardés
if %choix% == g ( goto:suppressionFichiersTemporaires )
@xcopy "export_files" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\export_files\" /E /H
@xcopy "import_files" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\import_files\" /E /H
@xcopy "XMLFiles" "\Applications BRP\BRP_editeur_app\BRP_editeur_rapports\XMLFiles\" /E /H

:suppressionFichiersTemporaires
@rem Suppression du dossier war temporaire et son contenu
@cd "\"
@rd /S /Q "\Applications BRP\war"

@rem Appel aux fonctions de création du conteneur docker
@cd "\Applications BRP\BRP_editeur_app\Scripts Docker\"
call "Creer Conteneur.bat"
@echo Application deployee
@pause
exit

