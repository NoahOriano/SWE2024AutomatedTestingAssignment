echo "Compiling code..."
javac -d ./bin -cp ./src ./src/ltu/Main.java ./src/ltu/CalendarImpl.java
echo "Compiling tests..."
javac -d ./bin -cp ./lib/org.junit4-4.3.1.jar:./src ./src/ltu/PaymentTest.java