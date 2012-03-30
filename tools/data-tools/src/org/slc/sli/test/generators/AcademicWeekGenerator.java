package org.slc.sli.test.generators;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.AcademicWeek;

public class AcademicWeekGenerator {
	private static final Logger log = Logger.getLogger(GradingPeriodGenerator.class);

	private Calendar beginDate = null;
	private Calendar endDate = null;

	Random generator = new Random();

	public AcademicWeek getAcademicWeek () {
		AcademicWeek aw = new AcademicWeek();

		beginDate = GregorianCalendar.getInstance();
		beginDate.roll(Calendar.YEAR, -1);
		endDate = GregorianCalendar.getInstance();
		endDate.roll(Calendar.YEAR, 1);

		aw.setWeekIdentifier("AdcademicWeek");
		aw.setBeginDate(beginDate);
		aw.setEndDate(endDate);

		int roll = 45 + (int) (Math.random() * (150 - 45));
		aw.setTotalInstructionalDays(roll);

		return aw;
	}

	public static void main(String args[]) {
		AcademicWeekGenerator awg = new AcademicWeekGenerator ();

		for (int i = 0; i<10; i++) {
			AcademicWeek aw = awg.getAcademicWeek();
			log.info("AcademicWeek = " + aw.getWeekIdentifier() + ",\n"  + "beginDate = " + aw.getBeginDate().getTime() + ",\n" +
					"endDate = " + aw.getEndDate().getTime() + ",\n" + "totalInstructionalDays = " + aw.getTotalInstructionalDays() + ",\n"
					);
		}

	}

}
