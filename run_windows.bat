set JDK_DIR=
set JAVAFX_DIR=

set  JAVA_HOME=lib\%JDK_DIR%
set PATH_TO_FX=lib\%JAVAFX_DIR%\lib

dir /s /b src\*.java > sources.txt & %JAVA_HOME%\bin\javac.exe --module-path %PATH_TO_FX% --add-modules javafx.controls -d out @sources.txt & del sources.txt
%JAVA_HOME%\bin\java.exe --module-path %PATH_TO_FX% --add-modules javafx.controls --class-path out spidersolitaire.App