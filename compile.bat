@echo off
set JDK_HOME="C:\Program Files\Java SDK\OpenJDK-21"
@echo "Compiling code ..."
@mkdir bin
%JDK_HOME%/bin/javac.exe -d bin -cp src src/ltu/Main.java src/ltu/CalendarImpl.java
@echo "Compiling tests ..."
set JUNIT=lib\org.junit4-4.3.1.jar
%JDK_HOME%/bin/javac.exe -d bin -cp %JUNIT%;src src/ltu/PaymentTest.java