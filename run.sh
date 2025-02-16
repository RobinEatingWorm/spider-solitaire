#!/bin/bash

export JAVA_HOME=./lib/jdk-21.0.5.jdk/Contents/Home
PATH_TO_FX=./lib/javafx-sdk-23.0.1/lib

echo Compiling...
javac --module-path $PATH_TO_FX --add-modules javafx.controls -d out $(find src -name "*.java")
cp -r ./src/main/resources/* ./out
echo Running...
java --module-path $PATH_TO_FX --add-modules javafx.controls -cp out spidersolitaire.App
