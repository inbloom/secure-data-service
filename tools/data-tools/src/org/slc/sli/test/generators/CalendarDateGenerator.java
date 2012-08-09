/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.test.generators;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.slc.sli.test.edfi.entities.CalendarDate;
import org.slc.sli.test.edfi.entities.CalendarEventType;



public class CalendarDateGenerator {
    static Random generator = new Random();

    public static CalendarDate getCalendarDate(String id, int dateCount) {
        CalendarDate cd = new CalendarDate();
        
        cd.setId(id);

//        cd.setDate("2011-03-04");
        int day = ((dateCount/8) % 28) + 1;
        int month = (((dateCount/8) / 28) % 12) + 1;
        int year = (((dateCount/8) / 28) / 12) + 2011;

        String dayString = "";
        if (day < 10) {
        	dayString = "0" + day;
        } else {
        	dayString = "" + day;
        }
        
        String monthString = "";
        if (month < 10) {
        	monthString = "0" + month;
        } else {
        	monthString = "" + month;
        }
        
        String yearString = "" + year;
        
        cd.setDate(yearString+"-"+monthString+"-"+dayString);
        
//        cd.setCalendarEvent(getCalendarEventType());
        cd.setCalendarEvent(getCalendarEventType(dateCount%8+1));

        return cd;
    }

    

    public static CalendarEventType getCalendarEventType(int roll) {
//        int roll = generator.nextInt(8) + 1;
        switch (roll) {
            case 1: return CalendarEventType.EMERGENCY_DAY;
            case 2: return CalendarEventType.HOLIDAY;
            case 3: return CalendarEventType.INSTRUCTIONAL_DAY;
            case 4: return CalendarEventType.MAKE_UP_DAY;
            case 5: return CalendarEventType.OTHER;
            case 6: return CalendarEventType.STRIKE;
            case 7: return CalendarEventType.STUDENT_LATE_ARRIVAL_EARLY_DISMISSAL;
            case 8: return CalendarEventType.TEACHER_ONLY_DAY;
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
