package ltu;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import junit.framework.Assert;
import org.junit.Test;

public class PaymentTest
{
    //Test the system for the spring-term of 2016 (2016-01-01 to 2016-06-30).
    //The system should be able to handle the following requirements:

    //Age requirements
    //[ID: 101] The student must be at least 20 years old to receive subsidiary and student loans.
    //[ID: 102] The student may receive subsidiary until the year they turn 56.
    //[ID: 103] The student may not receive any student loans from the year they turn 47.

    //Study pace requirements
    //[ID: 201] The student must be studying at least half time to receive any subsidiary.
    //[ID: 202] A student studying less than full time is entitled to 50% subsidiary.
    //[ID: 203] A student studying full time is entitled to 100% subsidiary.

    //Income while studying requirements
    //[ID: 301] A student who is studying full time or more is permitted to earn a maximum of 85 813SEK per year in order to receive any subsidiary or student loans.
    //[ID: 302] A student who is studying less than full time is allowed to earn a maximum of 128 722SEK per year in order to receive any subsidiary or student loans.

    //Completion ratio requirement
    //[ID: 401] A student must have completed at least 50% of previous studies in order to receive any subsidiary or student loans.
    //When and amount paid requirements

    //Full time students are entitled to:
    //[ID: 501] Student loan: 7088 SEK / month
    //[ID: 502] Subsidiary: 2816 SEK / month

    //Less than full time students are entitled to:
    //[ID: 503] Student loan: 3564 SEK / month
    //[ID: 504] Subsidiary: 1396 SEK / month
    //[ID: 505] A person who is entitled to receive a student loan will always receive the full amount.
    //[ID: 506] Student loans and subsidiary is paid on the last weekday (Monday to Friday) every month.

    public static final int loan100 = 7088;
    public static final int loan50 = 3564;
    public static final int subsidy100 = 2816;
    public static final int subsidy50 = 1396;
    public static final int maxIncomeFullTime = 85813;
    public static final int maxIncomePartTime = 128722;
    public static final int testYear = 2016;
    // Sut class for consistent testing, each test will have a fresh instance of this class
    public class Sut{
        public Sut(){
            mockCalender = new ICalendar(){
                private Date mockedDate = new Date(116, Calendar.JANUARY,1); // Year 116 is 2016
                public Date getDate() {
                    return this.mockedDate;
                }

                private void setDate(Date date) {
                    this.mockedDate = date;
                }
            };
            try {
                paymentService = new PaymentImpl(mockCalender);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        public ICalendar mockCalender;
        public PaymentImpl paymentService;
    }

    // Person class for consistent testing, used to store test people
    public class Person{
        public String personId;
        public int income;
        public int studyRate;
        public int completionRatio;
        public Person(String personId, int income, int studyRate, int completionRatio){
            this.personId = personId;
            this.income = income;
            this.studyRate = studyRate;
            this.completionRatio = completionRatio;
        }
    }

    @Test
    public void testSilly()
    {
        Sut sut = new Sut();
        assertNotNull(sut.mockCalender.getDate());
        assertNotNull(sut.paymentService);
        assertEquals(1,1);
    }

    @Test
    public void studentMustBe20(){
        // Example person number = YYYYMMDD-nnnC, where C is a control digit, e.g. 20001219-3421
        Sut sut = new Sut();
        // Four test people, one who is 17, one who is 19, one who is 20, one who is 21
        // The term is spring-term of 2016 (2016-01-01 to 2016-06-30)
        Person person1TooYoung  = new Person("19990918-1234", 0, 100, 51);
        Person person2TooYoung  = new Person("19970101-1234", 0, 100, 51);
        Person person3OldEnough = new Person("19961118-1234", 0, 100, 51);
        Person person4OldEnough = new Person("19951118-1234", 0, 100, 51);

        // Test each person
        System.out.println(sut.mockCalender.getDate());
        System.out.println(person1TooYoung.personId.substring(0, 4));
        assertEquals(0, sut.paymentService.getMonthlyAmount(person1TooYoung.personId, person1TooYoung.income,
                person1TooYoung.studyRate, person1TooYoung.completionRatio));
        assertEquals(0, sut.paymentService.getMonthlyAmount(person2TooYoung.personId, person2TooYoung.income,
                person2TooYoung.studyRate, person2TooYoung.completionRatio));
        assertTrue(sut.paymentService.getMonthlyAmount(person3OldEnough.personId, person3OldEnough.income,
                person3OldEnough.studyRate, person3OldEnough.completionRatio) > 0);
        assertTrue(sut.paymentService.getMonthlyAmount(person4OldEnough.personId, person4OldEnough.income,
                person4OldEnough.studyRate, person4OldEnough.completionRatio) > 0);
    }

    @Test
    public void studentMustBeUnder56(){
        // Example person number = YYYYMMDD-nnnC, where C is a control digit, e.g. 20001219-3421
        Sut sut = new Sut();
        // Four test people, one who is 63, one who is just too old, one who is just under 56, one who is 54
        // The term is spring-term of 2016 (2016-01-01 to 2016-06-30)
        Person person1TooOld  = new Person("19570918-1234", 0, 100, 51);
        Person person2TooOld  = new Person("19591229-1234", 0, 100, 51);
        Person person3YoungEnough = new Person("19601502-1234", 0, 100, 51);
        Person person4YoungEnough = new Person("19631118-1234", 0, 100, 51);

        // Test each person
        assertEquals(0, sut.paymentService.getMonthlyAmount(person1TooOld.personId, person1TooOld.income,
                person1TooOld.studyRate, person1TooOld.completionRatio));
        assertEquals(0, sut.paymentService.getMonthlyAmount(person2TooOld.personId, person2TooOld.income,
                person2TooOld.studyRate, person2TooOld.completionRatio));
        assertTrue(sut.paymentService.getMonthlyAmount(person3YoungEnough.personId, person3YoungEnough.income,
                person3YoungEnough.studyRate, person3YoungEnough.completionRatio) > 0);
        assertTrue(sut.paymentService.getMonthlyAmount(person4YoungEnough.personId, person4YoungEnough.income,
                person4YoungEnough.studyRate, person4YoungEnough.completionRatio) > 0);
    }

    @Test
    public void studentMustBeUnder47ForSubsidiary(){
        // Example person number = YYYYMMDD-nnnC, where C is a control digit, e.g. 20001219-3421
        Sut sut = new Sut();
        // Four test people, one who is older, one who is just too old, one who is just under 47, one who is 45
        // The term is spring-term of 2016 (2016-01-01 to 2016-06-30)
        Person person1TooOld  = new Person("19650918-1234", 0, 100, 51);
        Person person2TooOld  = new Person("19681229-1234", 0, 100, 51);
        Person person3YoungEnough = new Person("19700201-1234", 0, 100, 51);
        Person person4YoungEnough = new Person("19720118-1234", 0, 100, 51);

        // Test each person
        assertEquals(subsidy100, sut.paymentService.getMonthlyAmount(person1TooOld.personId, person1TooOld.income,
                person1TooOld.studyRate, person1TooOld.completionRatio));
        assertEquals(subsidy100, sut.paymentService.getMonthlyAmount(person2TooOld.personId, person2TooOld.income,
                person2TooOld.studyRate, person2TooOld.completionRatio));
        assertEquals(subsidy100 + loan100, sut.paymentService.getMonthlyAmount(person3YoungEnough.personId, person3YoungEnough.income,
                person3YoungEnough.studyRate, person3YoungEnough.completionRatio));
        assertEquals(subsidy100 + loan100, sut.paymentService.getMonthlyAmount(person4YoungEnough.personId, person4YoungEnough.income,
                person4YoungEnough.studyRate, person4YoungEnough.completionRatio));
    }

    @Test
    public void studentMustHaveCompletedHalfOfStudies(){
        // Example person number = YYYYMMDD-nnnC, where C is a control digit, e.g. 20001219-3421
        Sut sut = new Sut();
        // Four test people, one who has completed 49%, one who has completed 50%, one who has completed 51%, one who has completed 100%
        // Each person is 26 and qualifies for subsidiary and student loans at 100%
        // The term is spring-term of 2016 (2016-01-01 to 2016-06-30)
        Person person1TooLittle  = new Person("19900918-1234", 0, 100, 49);
        Person person2JustEnough  = new Person("19901229-1234", 0, 100, 50);
        Person person3Enough = new Person("19900201-1234", 0, 100, 51);
        Person person4AllDone = new Person("19900118-1234", 0, 100, 100);

        // Test each person
        assertEquals(0, sut.paymentService.getMonthlyAmount(person1TooLittle.personId, person1TooLittle.income,
                person1TooLittle.studyRate, person1TooLittle.completionRatio));
        assertEquals(subsidy100 + loan100, sut.paymentService.getMonthlyAmount(person2JustEnough.personId, person2JustEnough.income,
                person2JustEnough.studyRate, person2JustEnough.completionRatio));
        assertEquals(subsidy100 + loan100, sut.paymentService.getMonthlyAmount(person3Enough.personId, person3Enough.income,
                person3Enough.studyRate, person3Enough.completionRatio));
        assertEquals(subsidy100 + loan100, sut.paymentService.getMonthlyAmount(person4AllDone.personId, person4AllDone.income,
                person4AllDone.studyRate, person4AllDone.completionRatio));
    }

    @Test
    public void fullTimeStudentMustHaveCertainIncome(){
        // Example person number = YYYYMMDD-nnnC, where C is a control digit, e.g. 20001219-3421
        Sut sut = new Sut();
        // Four test people, one who earns way too much, one who earns slightly too much, one who is just under limit, one who is way under limit
        // Each person is 26 and qualifies for subsidiary and student loans at 100%
        // The term is spring-term of 2016 (2016-01-01 to 2016-06-30)
        Person person1TooMuch  = new Person("19900918-1234", 100000, 100, 51);
        Person person2SlightlyTooMuch  = new Person("19900918-1234", 85814, 100, 51);
        Person person3JustUnder = new Person("19900918-1234", 85813, 100, 51);
        Person person4UnderLimit = new Person("19900918-1234", 500, 100, 51);

        // Test each person
        assertEquals(0, sut.paymentService.getMonthlyAmount(person1TooMuch.personId, person1TooMuch.income,
                person1TooMuch.studyRate, person1TooMuch.completionRatio));
        assertEquals(0, sut.paymentService.getMonthlyAmount(person2SlightlyTooMuch.personId, person2SlightlyTooMuch.income,
                person2SlightlyTooMuch.studyRate, person2SlightlyTooMuch.completionRatio));
        assertEquals(subsidy100 + loan100, sut.paymentService.getMonthlyAmount(person3JustUnder.personId, person3JustUnder.income,
                person3JustUnder.studyRate, person3JustUnder.completionRatio));
        assertEquals(subsidy100 + loan100, sut.paymentService.getMonthlyAmount(person4UnderLimit.personId, person4UnderLimit.income,
                person4UnderLimit.studyRate, person4UnderLimit.completionRatio));
    }

    @Test
    public void partTimeStudentMustHaveCertainIncome(){
        // Example person number = YYYYMMDD-nnnC, where C is a control digit, e.g. 20001219-3421
        Sut sut = new Sut();
        // Four test people, one who earns way too much, one who earns slightly too much, one who is just under limit, one who is way under limit
        // Each person is 26 and qualifies for subsidiary and student loans at 50%
        // The term is spring-term of 2016 (2016-01-01 to 2016-06-30)
        Person person1TooMuch  = new Person("19900918-1234", 130000, 50, 51);
        Person person2SlightlyTooMuch  = new Person("19900918-1234", 128723, 50, 51);
        Person person3JustUnder = new Person("19900918-1234", 128722, 50, 51);
        Person person4UnderLimit = new Person("19900918-1234", 500, 50, 51);

        // Test each person
        assertEquals(0, sut.paymentService.getMonthlyAmount(person1TooMuch.personId, person1TooMuch.income,
                person1TooMuch.studyRate, person1TooMuch.completionRatio));
        assertEquals(0, sut.paymentService.getMonthlyAmount(person2SlightlyTooMuch.personId, person2SlightlyTooMuch.income,
                person2SlightlyTooMuch.studyRate, person2SlightlyTooMuch.completionRatio));
        assertEquals(subsidy50 + loan50, sut.paymentService.getMonthlyAmount(person3JustUnder.personId, person3JustUnder.income,
                person3JustUnder.studyRate, person3JustUnder.completionRatio));
        assertEquals(subsidy50 + loan50, sut.paymentService.getMonthlyAmount(person4UnderLimit.personId, person4UnderLimit.income,
                person4UnderLimit.studyRate, person4UnderLimit.completionRatio));
    }

    @Test
    public void subsidiaryAndLoanModifiedByPace(){
        // Example person number = YYYYMMDD-nnnC, where C is a control digit, e.g. 20001219-3421
        Sut sut = new Sut();
        // Three test people, one who is full time, one who is half time, one who is less than half time
        // Each person is 26 and qualifies for subsidiary and student loans
        // The term is spring-term of 2016 (2016-01-01 to 2016-06-30)
        Person person1FullTime  = new Person("19900918-1234", 0, 100, 51);
        Person person2HalfTime  = new Person("19900918-1234", 0, 50, 51);
        Person person3LessThanHalfTime = new Person("19900918-1234", 0, 49, 51);

        // Test each person
        assertEquals(subsidy100 + loan100, sut.paymentService.getMonthlyAmount(person1FullTime.personId, person1FullTime.income,
                person1FullTime.studyRate, person1FullTime.completionRatio));
        assertEquals(subsidy50 + loan50, sut.paymentService.getMonthlyAmount(person2HalfTime.personId, person2HalfTime.income,
                person2HalfTime.studyRate, person2HalfTime.completionRatio));
        assertEquals(0, sut.paymentService.getMonthlyAmount(person3LessThanHalfTime.personId, person3LessThanHalfTime.income,
                person3LessThanHalfTime.studyRate, person3LessThanHalfTime.completionRatio));
    }


}
