# Caminhos
SRC_DIR = src
OUT_DIR = out
LIB_DIR = resources

# Livrarias/Classes Externas
LIBS = $(wildcard $(LIB_DIR)/*.jar)

# Flags para Compile
JFLAGS = -d $(OUT_DIR) -cp "$(LIB_DIR)/*"

# Seleciona todos os ficheiros .java na source
SOURCES = $(shell find $(SRC_DIR) -name "*.java")

# Default - Compile e abre o servidor
all: compile run-server

# Compilar todas as classes
compile:
	javac $(JFLAGS) $(SOURCES)

# Run das Classes Principais
run-server:
	java -cp "$(OUT_DIR):$(LIB_DIR)/*" ServidorConnc

run-clientAdm:
	java -cp "$(OUT_DIR):$(LIB_DIR)/*" ClienteAdm

run-clientGeral:
	java -cp "$(OUT_DIR):$(LIB_DIR)/*" ClienteGeral

