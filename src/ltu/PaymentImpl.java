package ltu;

import static java.lang.Integer.parseInt;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class PaymentImpl implements IPayment
{

    private static final String DEFAULT_RULES = "student100loan=7088\nstudent100subsidy=2816\nstudent50loan=3564\nstudent50subsidy=1396\nstudent0loan=0\nstudent0subsidy=0\nfulltimeIncome=85813\nparttimeIncome=128722\n";
    private final int FULL_LOAN;
    private final int HALF_LOAN;
    private final int ZERO_LOAN;
    private final int FULL_SUBSIDY;
    private final int HALF_SUBSIDY;
    private final int ZERO_SUBSIDY;
    private final int FULLTIME_INCOME;
    private final int PARTTIME_INCOME;
    private final ICalendar calendar;
    private final Properties props;

    public PaymentImpl(ICalendar calendar) throws IOException
    {
        this(calendar, DEFAULT_RULES);
    }

    public PaymentImpl(ICalendar cal, String rules) throws IOException
    {
        calendar = cal;
        props = new Properties();
        try
        {
            props.load(new StringReader(rules));
        } catch (IOException e)
        {
            e.printStackTrace();
            throw e;
        }
        FULL_LOAN = parseInt((String) props.get("student100loan"));
        HALF_LOAN = parseInt((String) props.get("student50loan"));
        ZERO_LOAN = parseInt((String) props.get("student0loan"));
        FULL_SUBSIDY = parseInt((String) props.get("student100subsidy"));
        HALF_SUBSIDY = parseInt((String) props.get("student50subsidy"));
        ZERO_SUBSIDY = parseInt((String) props.get("student0subsidy"));
        FULLTIME_INCOME = parseInt((String) props.get("fulltimeIncome"));
        PARTTIME_INCOME = parseInt((String) props.get("parttimeIncome"));
    }

    @Override
    public String getNextPaymentDay()
    {
    	DateFormat format = new SimpleDateFormat("yyyyMMdd");
    	Calendar cal = Calendar.getInstance();
        cal.setTime(calendar.getDate());
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        if (weekDay == Calendar.SUNDAY)
        {
            cal.add(Calendar.DATE, -2);

        } else if (weekDay == Calendar.SATURDAY)
        {
            cal.add(Calendar.DATE, -1);

        }
        return format.format(cal.getTime());
    }

    @Override
    public int getMonthlyAmount(String personId, int income, int studyRate, int completionRatio)
            throws IllegalArgumentException
    {
        if (personId == null || income < 0 || studyRate < 0 || completionRatio < 0)
        {
            throw new IllegalArgumentException("Invalid input.");
        }
        int age = getAge(personId);
        int amount = getLoan(age, income, studyRate, completionRatio);
        amount += getSubsidy(age, income, studyRate, completionRatio);
        return amount;
    }

    private int getAge(String personId)
    {
        if (personId == null || personId.length() != 13)
        {
            throw new IllegalArgumentException("Invalid personId: " + personId);
        }
        int personYear = parseInt(personId.substring(0, 4));
        int paymentYear = parseInt(new SimpleDateFormat("yyyy").format(calendar.getDate()));
        int age = paymentYear - personYear;
        return age;
    }

    private int getLoan(int age, int income, int studyRate, int completionRatio)
    {
        if (studyRate < 50 || age < 20 || age >= 47 || completionRatio < 50)
        {
            return ZERO_LOAN;
        }
        if (studyRate >= 100 && income > FULLTIME_INCOME)
        {
            return ZERO_LOAN;
        }
        if (studyRate < 100 && income > PARTTIME_INCOME)
        {
            return ZERO_LOAN;
        }
        if (studyRate < 100)
        {
            return HALF_LOAN;
        }
        return FULL_LOAN;
    }

    private int getSubsidy(int age, int income, int studyRate, int completionRatio)
    {
        if (studyRate < 50 || age < 20 || age > 56 || completionRatio < 50)
        {
            return ZERO_SUBSIDY;
        }
        if (studyRate >= 100 && income > FULLTIME_INCOME)
        {
            return ZERO_SUBSIDY;
        }
        if (studyRate < 100 && income > PARTTIME_INCOME)
        {
            return ZERO_SUBSIDY;
        }
        if (studyRate < 100)
        {
            return HALF_SUBSIDY;
        }
        return FULL_SUBSIDY;
    }

}
