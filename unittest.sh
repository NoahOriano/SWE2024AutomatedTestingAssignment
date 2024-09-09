echo "Running unittests..."
java -javaagent:./lib/jacocoagent.jar -cp ./lib/org.junit4-4.3.1.jar:./bin org.junit.runner.JUnitCore ltu.PaymentTest
echo "Generating report..."
java -jar ./lib/jacococli.jar report ./jacoco.exec --classfiles ./bin --html ./coveragereport --name CodeCoverageReport --sourcefiles ./src