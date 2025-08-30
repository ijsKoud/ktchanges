#! /bin/bash

# This script runs all the tests for the project.

echo "Navigating to project directory..."
cd ../plugin || exit 1

echo "Running tests..."
./gradlew test