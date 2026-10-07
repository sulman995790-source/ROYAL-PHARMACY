#!/bin/sh
DIR="$(cd "$(dirname "$0")" && pwd)"
exec java -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew -classpath "$DIR/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
