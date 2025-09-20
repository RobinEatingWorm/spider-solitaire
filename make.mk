# Assert valid OS
ifeq ($(or $(filter Windows, $(OS)), $(filter macOS, $(OS)), $(filter Linux, $(OS))), )
$(error OS must be one of Windows, macOS, or Linux)
endif

# Directories
SRC_DIR = src
LIB_DIR = lib
OUT_DIR = out
BUILD_DIR = build
RUNTIME_DIR = runtime

# Java home
ifneq ($(or $(filter Windows, $(OS)), $(filter Linux, $(OS))), )
JAVA_HOME = $(LIB_DIR)/$(JDK_DIR)
else ifeq ($(OS), macOS)
JAVA_HOME = $(LIB_DIR)/$(JDK_DIR)/Contents/Home
endif

# Java tools
ifeq ($(OS), Windows)
JAR = $(JAVA_HOME)/bin/jar.exe
JAVA = $(JAVA_HOME)/bin/java.exe
JAVAC = $(JAVA_HOME)/bin/javac.exe
JLINK = $(JAVA_HOME)/bin/jlink.exe
JPACKAGE = $(JAVA_HOME)/bin/jpackage.exe
else ifneq ($(or $(filter macOS, $(OS)), $(filter Linux, $(OS))), )
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
FX_MODULES = javafx.controls

# Targets
TARGET = spidersolitaire
TARGET_JAR = $(TARGET).jar
TARGET_CLASS = $(TARGET).App

# Java source files
SOURCES = $(shell find $(SRC_DIR) -name "*.java")

# Java manifest file
MANIFEST = META-INF/MANIFEST.MF

# Target application name
ifneq ($(or $(filter Windows, $(OS)), $(filter Linux, $(OS))), )
TARGET_APP = $(TARGET)
else ifeq ($(OS), macOS)
TARGET_APP = $(TARGET).app
endif

# Main rule
all: $(TARGET_APP)

# Application package
$(TARGET_APP): $(BUILD_DIR)/$(TARGET_JAR) $(RUNTIME_DIR)
	$(JPACKAGE) --name $(TARGET) --input $(BUILD_DIR) --main-jar $(TARGET_JAR) --main-class $(TARGET_CLASS) -t app-image --runtime-image $(RUNTIME_DIR)

# Runtime image
ifeq ($(OS), Windows)
$(RUNTIME_DIR):
	$(JLINK) --output $@ --module-path "$(JAVA_HOME)/jmods;$(FX_JMODS_PATH)" --add-modules $(FX_MODULES)
else ifneq ($(or $(filter macOS, $(OS)), $(filter Linux, $(OS))), )
$(RUNTIME_DIR):
	$(JLINK) --output $@ --module-path $(JAVA_HOME)/jmods:$(FX_JMODS_PATH) --add-modules $(FX_MODULES)
endif

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
