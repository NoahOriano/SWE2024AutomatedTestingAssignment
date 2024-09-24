package ltu;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

// This class exists for debugging purposes and for testing the PaymentImpl class more fully
// Rather than a single requirement approach, this class uses an iterative approach to test the PaymentImpl class
// This allows it to test the PaymentImpl class with a wide range of cases, and compare the expected output
// Each failure is logged and printed to the console, and the test fails if any failures are found
public class PaymentIterativeTest {
    public static final int LOAN_100 = 7088;
    public static final int LOAN_50 = 3564;
    public static final int SUBSIDY_100 = 2816;
    public static final int SUBSIDY_50 = 1396;
    public static final int MAX_INCOME_FULL_TIME = 85813;
    public static final int MAX_INCOME_PART_TIME = 128722;
    // This test is used to test the payment amounts returned by the PaymentImpl class
    @Test
    public void testPaymentAmounts(){
        // The identifiers used for test values are Error, None, Full, Part for determining payment rate.
        // Error, None, Subsidy, Full are used for determining payment amount.
        int[] testAges = {0, 19, 20, 21, 26, 46, 47, 56, 57, 61, 100};
        int[] testIncomes = {-1, 0, MAX_INCOME_FULL_TIME, MAX_INCOME_FULL_TIME+1, MAX_INCOME_PART_TIME, MAX_INCOME_PART_TIME+1, 200000};
        int[] testStudyRates = {-1, 0, 49, 50, 99, 100};
        int[] testCompletionRatios = {-1, 0, 49, 50,99, 100};
        ArrayList<String> failures = new ArrayList<String>();
        PaymentTest.Sut sut = new PaymentTest.Sut();
        for(int age : testAges){
            for(int income : testIncomes){
                for(int studyRate : testStudyRates){
                    for(int completionRatio : testCompletionRatios){
                        boolean shouldError = income <= -1 || studyRate <= -1 || completionRatio <= -1;
                        if(shouldError) {
                            try{
                                sut.paymentService.getMonthlyAmount(2016-age + "0101-5544", income, studyRate, completionRatio);
                                failures.add("Age: " + age + ", Income: " + income + ", StudyRate: " + studyRate + ", CompletionRatio: " + completionRatio + " => No exception thrown");
                            } catch(IllegalArgumentException e){
                                // Expected
                            }
                            continue;
                        }
                        int expectedAmount = 0;
                        boolean fullTime = studyRate >= 100;
                        boolean partTime = studyRate < 100 && studyRate >= 50;
                        boolean loan = age >= 20 && age < 47 && completionRatio >= 50;
                        boolean subsidy = age >= 20 && age <= 56 && completionRatio >= 50;
                        if(fullTime && income <= MAX_INCOME_FULL_TIME){
                            if(loan){
                                expectedAmount += LOAN_100;
                            }
                            if(subsidy){
                                expectedAmount += SUBSIDY_100;
                            }
                        } else if(partTime && income <= MAX_INCOME_PART_TIME){
                            if(loan){
                                expectedAmount += LOAN_50;
                            }
                            if(subsidy){
                                expectedAmount += SUBSIDY_50;
                            }
                        }
                        int actualAmount = sut.paymentService.getMonthlyAmount(2016-age + "0101-5544", income, studyRate, completionRatio);
                        if(actualAmount != expectedAmount){
                            failures.add("Age: " + age + ", Income: " + income + ", StudyRate: " + studyRate + ", CompletionRatio: " + completionRatio + " => " + actualAmount + " != " + expectedAmount);
                        }
                    }
                }
            }
        }
        if(failures.size() > 0){
            System.out.println("Failures: " + failures.size());
            System.out.println("Failures are in format: Age, Income, StudyRate, CompletionRatio => Actual != Expected");
            for(String failure : failures){
                System.out.println(failure);
            }
            fail("Test failed");
        }
    }

    // This test is used to test the payment dates returned by the PaymentImpl class
    @Test
    public void testPaymentDates(){
        class TestMonth{
            public int month;
            public int days;
            public int lastWeekday;
            public TestMonth(int month, int days, int lastWeekday){
                this.month = month;
                this.days = days;
                this.lastWeekday = lastWeekday;
            }
        }
        // The last weekday is the expected pay date for the month
        TestMonth[] testMonths = {
                new TestMonth(Calendar.JANUARY, 31, 29), // 31st is a sunday
                new TestMonth(Calendar.FEBRUARY, 29, 29),
                new TestMonth(Calendar.MARCH, 31, 31),
                new TestMonth(Calendar.APRIL, 30, 29), // 30th is a saturday
                new TestMonth(Calendar.MAY, 31, 31),
                new TestMonth(Calendar.JUNE, 30, 30),
                new TestMonth(Calendar.JULY, 31, 29),
        };
        PaymentTest.Sut sut = new PaymentTest.Sut();
        boolean failure = false;
        for(TestMonth testMonth : testMonths){
            sut.mockCalender.setDate(new Date(116, testMonth.month, 1)); // year 116 is 2016
            String expectedDate = 2016+"0"+(testMonth.month+1)+""+testMonth.lastWeekday; // 0 is added to month to make it 2 digits
            String actualDate = sut.paymentService.getNextPaymentDay();
            if(!expectedDate.equals(actualDate)){
                failure = true;
                System.out.println("Expected: " + expectedDate + ", Actual: " + actualDate);
            }
        }
        if(failure){
            fail("Test failed");
        }
    }
}
