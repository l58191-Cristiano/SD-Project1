# === Paths ===
SRC_DIR = src
OUT_DIR = out
LIB_DIR = resources

# === Libraries ===
LIBS = $(wildcard $(LIB_DIR)/*.jar)

# === Java Tools ===
JAVAC = javac
JAVA = java

# === Compilation Flags ===
JFLAGS = -d $(OUT_DIR) -cp "$(LIB_DIR)/*"

# === All .java source files ===
SOURCES = $(shell find $(SRC_DIR) -name "*.java")

# === Main classes to run ===
MAINS = ClienteAdm ClienteGeral ServidorConnc

# === Default target ===
all: compile

# === Compile all .java files ===
compile:
	$(JAVAC) $(JFLAGS) $(SOURCES)

# === Run all main classes ===
run-server:
	$(JAVA) -cp "$(OUT_DIR):$(LIB_DIR)/*" ServidorConnc

run-clientAdm:
	$(JAVA) -cp "$(OUT_DIR):$(LIB_DIR)/*" ClienteAdm

run-clientGeral:
	$(JAVA) -cp "$(OUT_DIR):$(LIB_DIR)/*" ClienteGeral

# === Clean compiled classes ===
clean:
	@echo "🧹 Cleaning build..."
	rm -rf $(OUT_DIR)/*
	@echo "✅ Done."

