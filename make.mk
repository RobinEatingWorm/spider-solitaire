# Directories
SRC_DIR = src
LIB_DIR = lib
OUT_DIR = out
BUILD_DIR = build
RUNTIME_DIR = runtime

# Java home
ifeq ($(OS), Windows)
JAVA_HOME = $(LIB_DIR)/$(JDK_DIR)
else ifeq ($(OS), macOS)
JAVA_HOME = $(LIB_DIR)/$(JDK_DIR)/Contents/Home
else ifeq ($(OS), Linux)
JAVA_HOME = $(LIB_DIR)/$(JDK_DIR)
endif

# Java tools
ifeq ($(OS), Windows)
JAR = $(JAVA_HOME)/bin/jar.exe
JAVA = $(JAVA_HOME)/bin/java.exe
JAVAC = $(JAVA_HOME)/bin/javac.exe
JLINK = $(JAVA_HOME)/bin/jlink.exe
JPACKAGE = $(JAVA_HOME)/bin/jpackage.exe
else ifeq ($(OS), macOS)
JAR = $(JAVA_HOME)/bin/jar
JAVA = $(JAVA_HOME)/bin/java
JAVAC = $(JAVA_HOME)/bin/javac
JLINK = $(JAVA_HOME)/bin/jlink
JPACKAGE = $(JAVA_HOME)/bin/jpackage
else ifeq ($(OS), Linux)
JAR = $(JAVA_HOME)/bin/jar
JAVA = $(JAVA_HOME)/bin/java
JAVAC = $(JAVA_HOME)/bin/javac
JLINK = $(JAVA_HOME)/bin/jlink
JPACKAGE = $(JAVA_HOME)/bin/jpackage
endif

# Paths to JavaFX SDK and jmods
FX_SDK_PATH = $(LIB_DIR)/$(FX_SDK_DIR)/lib
FX_JMODS_PATH = $(LIB_DIR)/$(FX_JMODS_DIR)

# Modules to add
JAVA_MODULES = java.base
FX_MODULES = javafx.base,javafx.controls,javafx.graphics

# Targets
TARGET = spidersolitaire
TARGET_JAR = $(TARGET).jar
TARGET_CLASS = $(TARGET).App

# Java source files
SOURCES = $(shell find $(SRC_DIR) -name "*.java")

# Java manifest file
MANIFEST = META-INF/MANIFEST.MF

# Application type
ifeq ($(OS), Windows)
TARGET_APP = $(TARGET)
TARGET_TYPE = app-image
else ifeq ($(OS), macOS)
TARGET_APP = $(TARGET).app
TARGET_TYPE = app-image
else ifeq ($(OS), Linux)
TARGET_APP = $(TARGET)
TARGET_TYPE = app-image
endif

# Main rule
all: $(TARGET_APP)

# Application package
$(TARGET_APP): $(BUILD_DIR)/$(TARGET_JAR) $(RUNTIME_DIR)
	$(JPACKAGE) --name $(TARGET) --input $(BUILD_DIR) --main-jar $(TARGET_JAR) --main-class $(TARGET_CLASS) -t $(TARGET_TYPE) --runtime-image $(RUNTIME_DIR)

# Runtime image
$(RUNTIME_DIR):
	$(JLINK) --output $@ --module-path $(JAVA_HOME)/jmods --module-path $(FX_JMODS_PATH) --add-modules $(JAVA_MODULES),$(FX_MODULES)

# JAR archive
$(BUILD_DIR)/$(TARGET_JAR): $(OUT_DIR) $(MANIFEST)
	$(JAR) -c -f $@ -m $(MANIFEST) -C $(OUT_DIR) .

# Compilation to class files
$(OUT_DIR): $(SOURCES)
	$(JAVAC) --module-path $(FX_SDK_PATH) --add-modules $(FX_MODULES) -d $@ $^

# Test without full build
test: $(OUT_DIR)
	$(JAVA) --module-path $(FX_SDK_PATH) --add-modules $(FX_MODULES) --class-path $< $(TARGET_CLASS)

# Remove build artifacts
clean:
	rm -r -f $(OUT_DIR) $(BUILD_DIR) $(RUNTIME_DIR) $(TARGET_APP)

.PHONY: all test clean
