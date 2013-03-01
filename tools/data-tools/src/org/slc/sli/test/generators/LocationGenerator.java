/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.Location;

public class LocationGenerator {
    private static final Logger log = Logger.getLogger(LocationGenerator.class);
    private Random rand = new Random(31);

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

    public Location getClassroomLocation(String id)
    {
        Location location = new Location();
        location.setId(id);
        location.setClassroomIdentificationCode("MainHall" + id + "Code");
        int maxSeats = getRand() % 50;
        location.setMaximumNumberOfSeats(maxSeats);
        location.setOptimalNumberOfSeats(maxSeats<10?maxSeats:maxSeats-10);
        return location;
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
