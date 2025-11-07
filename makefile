#Make File
#Variáveis
SRC_DIR = src
BUILD_DIR = out/production/SD-Project1
CLASSPATH = $(BUILD_DIR)
JAVA_FILES = $(shell find $(SRC_DIR) -name "*.java")
#Targets
all: compile run-nameserver
compile:
	javac -d $(BUILD_DIR) -classpath $(CLASSPATH) $(JAVA_FILES)
run-nameserver:
	rmiregistry -J-classpath -J$(CLASSPATH) 1099
