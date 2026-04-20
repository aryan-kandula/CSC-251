#!/bin/bash
echo "============================================================"
echo "  Concrete Pad & Chain-Link Fence Estimator"
echo "  Author: Aryan Kandula  |  CSC-251  |  Module 5"
echo "============================================================"

mkdir -p out estimates

echo ""
echo "Compiling Java source files..."
javac -d out -sourcepath src src/Main.java

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Compilation failed."
    echo "        Make sure Java JDK 17+ is installed."
    exit 1
fi

echo ""
echo "[OK] Compilation complete!"
echo ""
echo "Starting application..."
echo "============================================================"
echo ""

java -cp out Main
