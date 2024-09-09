package ltu;

import static java.lang.Integer.parseInt;
import static ltu.CalendarFactory.getCalendar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{

    public static void main(String[] args) throws IOException
    {


       if (args.length < 1 || args.length > 2)
        {
            System.out.println("Usage ltu.Main <inputfile> [optional: calendarClass]");
            System.exit(-1);
        }


        PaymentImpl payment;
        if(args.length > 1) {
                payment = new PaymentImpl(getCalendar(args[1]));
        } else {
                payment = new PaymentImpl(getCalendar());
        }

        FileInputStream fis = new FileInputStream(args[0]);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(fis)));
        String line = null;

        while ((line = reader.readLine()) != null)
        {
            if (line.startsWith("#"))
            {
                continue;
            }
            String[] fields = line.split(";");
            if (fields.length != 4)
            {
                System.out.println("Corrupt input file");
                System.exit(-1);
            }
            String personId = fields[0];
            int income = parseInt(fields[1]);
            int studyRate = parseInt(fields[2]);
            int completionRatio = parseInt(fields[3]);
            int amount = payment.getMonthlyAmount(personId, income, studyRate, completionRatio);
            String paymentDate = payment.getNextPaymentDay();
            System.out.println(line + " => " + paymentDate + ": " + personId + ": " + amount);
        }
        reader.close();
    }

}
