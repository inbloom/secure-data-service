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

import java.util.Random;

import org.slc.sli.test.edfi.entities.BehaviorDescriptorType;
import org.slc.sli.test.edfi.entities.SLCDisciplineIncident;
import org.slc.sli.test.edfi.entities.SLCDisciplineIncidentIdentityType;
import org.slc.sli.test.edfi.entities.SLCDisciplineIncidentReferenceType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.IncidentLocationType;
import org.slc.sli.test.edfi.entities.ObjectFactory;

public class DisciplineGenerator {

    public static SLCDisciplineIncidentReferenceType getDisciplineIncidentReferenceType(String discId,
            String stateOrEdOrgId) {
        SLCDisciplineIncidentIdentityType diit = new SLCDisciplineIncidentIdentityType();
        diit.setIncidentIdentifier(discId);

        SLCEducationalOrgIdentityType edOrgIdentity = new SLCEducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(stateOrEdOrgId);
        SLCEducationalOrgReferenceType edOrgRef = new SLCEducationalOrgReferenceType();
        edOrgRef.setEducationalOrgIdentity(edOrgIdentity);

        diit.setEducationalOrgReference(edOrgRef);
        SLCDisciplineIncidentReferenceType dirt = new SLCDisciplineIncidentReferenceType();
        dirt.setDisciplineIncidentIdentity(diit);
        return dirt;
    }

    public SLCDisciplineIncident generate(String incidentId, String delimiter) {
        Random random = new Random(31);

        SLCDisciplineIncident discIncident = new SLCDisciplineIncident();

        // Do required elements first.

        // Incident Identifier
        discIncident.setIncidentIdentifier(incidentId.split(delimiter)[1]);

        // Incident Date (Hard-coded at this time)
        discIncident.setIncidentDate("2012-04-14");

        // Incident Time (Hard-coded at this time)
        discIncident.setIncidentTime("15:18:42");

        // Incident Location
        IncidentLocationType IncLocType = null;
        int randomInt22 = random.nextInt(22);
        if (randomInt22 == 0)
            IncLocType = IncidentLocationType.ADMINISTRATIVE_OFFICES_AREA;
        else if (randomInt22 == 1)
            IncLocType = IncidentLocationType.ATHLETIC_FIELD_OR_PLAYGROUND;
        else if (randomInt22 == 2)
            IncLocType = IncidentLocationType.AUDITORIUM;
        else if (randomInt22 == 3)
            IncLocType = IncidentLocationType.BUS_STOP;
        else if (randomInt22 == 4)
            IncLocType = IncidentLocationType.CAFETERIA_AREA;
        else if (randomInt22 == 5)
            IncLocType = IncidentLocationType.CLASSROOM;
        else if (randomInt22 == 6)
            IncLocType = IncidentLocationType.COMPUTER_LAB;
        else if (randomInt22 == 7)
            IncLocType = IncidentLocationType.HALLWAY_OR_STAIRS;
        else if (randomInt22 == 8)
            IncLocType = IncidentLocationType.LIBRARY_MEDIA_CENTER;
        else if (randomInt22 == 9)
            IncLocType = IncidentLocationType.LOCKER_ROOM_OR_GYM_AREAS;
        else if (randomInt22 == 10)
            IncLocType = IncidentLocationType.OFF_SCHOOL;
        else if (randomInt22 == 11)
            IncLocType = IncidentLocationType.OFF_SCHOOL_AT_OTHER_SCHOOL;
        else if (randomInt22 == 12)
            IncLocType = IncidentLocationType.OFF_SCHOOL_AT_OTHER_SCHOOL_DISTRICT_FACILITY;
        else if (randomInt22 == 13)
            IncLocType = IncidentLocationType.ON_SCHOOL;
        else if (randomInt22 == 14)
            IncLocType = IncidentLocationType.ON_SCHOOL_OTHER_INSIDE_AREA;
        else if (randomInt22 == 15)
            IncLocType = IncidentLocationType.ON_SCHOOL_OTHER_OUTSIDE_AREA;
        else if (randomInt22 == 16)
            IncLocType = IncidentLocationType.ONLINE;
        else if (randomInt22 == 17)
            IncLocType = IncidentLocationType.PARKING_LOT;
        else if (randomInt22 == 18)
            IncLocType = IncidentLocationType.RESTROOM;
        else if (randomInt22 == 19)
            IncLocType = IncidentLocationType.STADIUM;
        else if (randomInt22 == 20)
            IncLocType = IncidentLocationType.SCHOOL_BUS;
        else if (randomInt22 == 21)
            IncLocType = IncidentLocationType.WALKING_TO_OR_FROM_SCHOOL;
        else if (randomInt22 == 22)
            IncLocType = IncidentLocationType.UNKNOWN;
        discIncident.setIncidentLocation(IncLocType);

        // Behavior (1 behavior at this time)
        BehaviorDescriptorType bdtType = new BehaviorDescriptorType();
        ObjectFactory fact = new ObjectFactory();
        // JAXBElement<String> str = fact.createBehaviorDescriptorTypeDescription("Bullying");
        // bdtType.getCodeValueOrShortDescriptionOrDescription().add(str);
        bdtType.setCodeValue("Bullying");
        bdtType.setShortDescription("Bullying");
        bdtType.setShortDescription("Bullying");
        discIncident.getBehaviors().add(bdtType);

        // School Reference
        String schoolId = incidentId.split(delimiter)[0];
        SLCEducationalOrgReferenceType eor = SchoolGenerator.getEducationalOrgReferenceType(schoolId);
        discIncident.setSchoolReference(eor);

        return discIncident;
    }

}
