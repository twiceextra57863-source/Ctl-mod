@echo off
@rem Gradle start script for Windows

set APP_HOME=%~dp0gradle\wrapper
set CLASSPATH=%APP_HOME%\gradle-wrapper.jar

java -cp "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
