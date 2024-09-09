package ltu;

public class CalendarFactory
{

    private static ICalendar calendar = null;

    public static ICalendar getCalendar()
    {
        String className = System.getProperty("calendar", "ltu.CalendarImpl");
        return initCalendar(className);
    }

    public static ICalendar getCalendar(String className) {
        return initCalendar(className);
    }

    private static ICalendar initCalendar(String className) {
        try
        {
            calendar = (ICalendar) Class.forName(className).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return calendar;
    }
}