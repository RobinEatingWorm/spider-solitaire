#!/bin/bash

JDK_DIR=
JAVAFX_DIR=

JAVA_HOME=lib/$JDK_DIR/Contents/Home
PATH_TO_FX=lib/$JAVAFX_DIR/lib

javac --module-path $PATH_TO_FX --add-modules javafx.controls -d out $(find src -name "*.java")
java --module-path $PATH_TO_FX --add-modules javafx.controls --class-path out spidersolitaire.App
