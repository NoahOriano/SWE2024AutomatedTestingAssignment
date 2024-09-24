@echo off
set JDK_HOME=c:\PROGRA~1\Java\jdk1.8.0_45
set REPORT=lib\jacococli.jar
set AGENT=lib\jacocoagent.jar
set JUNIT=lib\org.junit4-4.3.1.jar
@echo "Running unittests ..."
%JDK_HOME%/bin/java.exe -javaagent:%AGENT% -cp %JUNIT%;bin org.junit.runner.JUnitCore ltu.PaymentTest
@echo "Generating report ..."
%JDK_HOME%/bin/java.exe -jar %REPORT% report ./jacoco.exec --classfiles ./bin --html ./coveragereport --name CodeCoverageReport --sourcefiles ./src