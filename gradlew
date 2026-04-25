#!/bin/sh
# Gradle wrapper script
# Run: chmod +x gradlew && ./gradlew build

GRADLE_VERSION="8.8"
GRADLE_DIST_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

exec gradle "$@" 2>/dev/null || \
  echo "Please install Gradle or use './gradlew' after setting up Gradle wrapper."
