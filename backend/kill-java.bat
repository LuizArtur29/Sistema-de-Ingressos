@echo off
echo Matando processos Java que podem estar usando a porta...
taskkill /f /im java.exe 2>nul
taskkill /f /im javaw.exe 2>nul
echo Processos Java finalizados.
pause