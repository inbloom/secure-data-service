package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.Location;

public class LocationGenerator {
	private static final Logger log = Logger.getLogger(LocationGenerator.class);
	private Random rand = new Random(); 

	private int getRand()
	{
		int num = rand.nextInt();
		return num < 0? -1 * num:num;
	}

	public List<Location> getMultipleClassroomLocations(int n)
	{
		List<Location> locations = new ArrayList<Location>();
		for(int i = 0; i < n; i++)
		{
			Location location = new Location();
			location.setId("MainHall" + i);
			location.setClassroomIdentificationCode("MainHall" + i + "Code");
			int maxSeats = getRand() % 50;
			location.setMaximumNumberOfSeats(maxSeats);
			location.setOptimalNumberOfSeats(maxSeats<10?maxSeats:maxSeats-10);
			locations.add(location);
		}
		return locations;
	}

	public static void main(String [] args )
	{
		LocationGenerator locationsGen = new LocationGenerator();
		List<Location> locations = locationsGen.getMultipleClassroomLocations(10);
		for(Location loc:locations)
		{
			String locDesc = 
					"Id: " + loc.getId() + ",\n" + 
							"ClassroomIdentificationCode: " + loc.getClassroomIdentificationCode() + ",\n" + 
							"MaximumNumberOfSeats: " + loc.getMaximumNumberOfSeats() + ",\n" + 
							"OptimalNumberOfSeats: " + loc.getOptimalNumberOfSeats();
			log.info(locDesc);
			System.out.println(locDesc);
		}
	}
}
