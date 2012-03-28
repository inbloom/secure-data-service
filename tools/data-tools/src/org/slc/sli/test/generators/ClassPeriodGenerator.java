package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.List;

import org.slc.sli.test.edfi.entities.ClassPeriod;

public class ClassPeriodGenerator {

	public List<ClassPeriod> getMultipleClassPeriods(int n)
	{
		List<ClassPeriod> periods = new ArrayList<ClassPeriod>();
		for(int i =0; i < n; i++)
		{
			ClassPeriod period = new ClassPeriod();
			period.setClassPeriodName("Period " + i);
			period.setId(String.valueOf(i));
			
			periods.add(period);
		}
		return periods;
	}
	
	public ClassPeriod getClassPeriod(String id)
	{
		ClassPeriod period = new ClassPeriod();
		period.setClassPeriodName("Period " + id);
		period.setId(String.valueOf(id));
		return period;
	}
}
