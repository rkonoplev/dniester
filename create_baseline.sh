#!/bin/bash
echo "Starting analysis to create baseline..."
# Set compatible Docker API version
export DOCKER_API_VERSION=1.43
qodana scan --linter jetbrains/qodana-jvm-community:2025.1 --results-dir ./temp-results
if [ -f ./temp-results/qodana.sarif.json ]; then
    echo "Copying results as baseline..."
    cp ./temp-results/qodana.sarif.json ./backend/qodana/baseline.xml
    echo "Baseline created!"
else
    echo "Error: results file not found!"
fi
rm -rf ./temp-results