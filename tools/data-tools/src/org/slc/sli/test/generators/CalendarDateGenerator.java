package org.slc.sli.test.generators;

import java.util.Calendar;
import java.util.Random;

import org.slc.sli.test.edfi.entities.CalendarDate;
import org.slc.sli.test.edfi.entities.CalendarEventType;



public class CalendarDateGenerator {
    static Random generator = new Random();

    public static CalendarDate getCalendarDate(String id) {
        CalendarDate cd = new CalendarDate();
        
        cd.setId(id);

        cd.setDate("2011-03-04");

        cd.setCalendarEvent(getCalendarEventType());

        return cd;
    }

    

    public static CalendarEventType getCalendarEventType() {
        int roll = generator.nextInt(8) + 1;
        switch (roll) {
            case 1:  return CalendarEventType.EMERGENCY_DAY;

            case 2: return CalendarEventType.HOLIDAY;
            case 3: return CalendarEventType.INSTRUCTIONAL_DAY;
            case 4: return CalendarEventType.INSTRUCTIONAL_DAY;
            case 5: return CalendarEventType.MAKE_UP_DAY;
            case 6: return CalendarEventType.OTHER;
            case 7: return CalendarEventType.STRIKE;
            case 8: return CalendarEventType.STUDENT_LATE_ARRIVAL_EARLY_DISMISSAL;
            case 9: return CalendarEventType.TEACHER_ONLY_DAY;
            default: return CalendarEventType.WEATHER_DAY;
        }
    }


    public static void main (String args[]) {
//        CalendarDateGenerator cdg = new CalendarDateGenerator ();
//        for (int i = 0; i < 10; i++) {
//            CalendarDate cd = cdg.getCalendarDate();
//            System.out.println("Date = " + cd.getDate());
//            System.out.println("CalendarEventType = " + cd.getCalendarEvent());
//        }
    }


	public static Calendar generateLowFi() {
		// TODO Auto-generated method stub
		return null;
	}

}
