package org.mongo.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

public class App {
	public static void main( String[] args ) {
		System.out.println("Bootstrapping Mongo Performance");

		ConfigurableApplicationContext context = null;
        context = new ClassPathXmlApplicationContext("META-INF/spring/bootstrap.xml");

        context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
        
        DataAccessWrapper da = context.getBean(DataAccessWrapper.class);
        
        if ("SAFE".equals(args[0])) {
            da.mongoTemplate.setWriteConcern(WriteConcern.SAFE);
            System.out.println("WRITE CONCERN = SAFE");
        } else if ("NONE".equals(args[0])) {
            da.mongoTemplate.setWriteConcern(WriteConcern.NONE);
            System.out.println("WRITE CONCERN = NONE");
        } else {
            da.mongoTemplate.setWriteConcern(WriteConcern.NORMAL);
            System.out.println("WRITE CONCERN = NORMAL");
        }
        
        int numberOfProcessors = new Integer(args[1]).intValue();
        int numberOfRecords = new Integer(args[2]).intValue();
        int chunkSize = new Integer(args[3]).intValue();
        
        System.out.println("NUMBER OF PROCESSORS = " + numberOfProcessors);
        System.out.println("NUMBER OF RECORDS = " + numberOfRecords);
        System.out.println("CHUNK SIZE = " + chunkSize);

        MongoProcessor mongoProcessor = context.getBean(MongoProcessor.class);
        mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize, generateRecord());
        mongoProcessor.writeStatistics();
        
	}
	
	private static HashMap<String, Object> generateRecord() {
        
	    BasicDBObject record = new BasicDBObject();
        HashMap<String, Object> temp;
        DBObject tempList;
        
        record.put("loginId", "1234567890");
        record.put("sex", "M");
        record.put("oldEthnicity", "White, Not Of Hispanic Origin");
        record.put("economicDisadvantaged", "false");
        record.put("hispanicLatinoEthnicity", "false");
        record.put("studentUniqueStateId", "0");
        record.put("profileThumbnail", "1000000000000000");


        tempList = new BasicDBList();
        tempList.put("0", "Medical Condition");
        record.put("section504Disabilities", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("endDate", "2010-03-04");
        temp.put("beginDate", "2010-03-04");
        temp.put("characteristic", "Foster Care");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("endDate", "2011-03-04");
        temp.put("beginDate", "2011-03-04");
        temp.put("characteristic", "Foster Care");
        tempList.put("1", temp);
        record.put("studentCharacteristics", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("disability", "Developmental Delay");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("disability", "Developmental Delay");
        tempList.put("1", temp);
        record.put("disabilities", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("schoolYear", "2011-2012");
        temp.put("cohortYearType", "Eleventh grade");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("schoolYear", "2010-2011");
        temp.put("cohortYearType", "Tenth grade");
        tempList.put("1", temp);
        record.put("cohortYears", tempList);

        tempList = new BasicDBList();
        tempList.put("0", "White");
        tempList.put("1", "Black - African American");
        record.put("race", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("program", "Athletics");
        temp.put("endDate", "2012-03-04");
        temp.put("beginDate", "2011-03-04");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("program", "Athletics");
        temp.put("endDate", "2012=1-03-04");
        temp.put("beginDate", "2010-03-04");
        tempList.put("1", temp);
        record.put("programParticipations", tempList);

        tempList = new BasicDBList();
        tempList.put("0", "English");
        tempList.put("1", "Russian");
        record.put("languages", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("middleName", "Middle");
        temp.put("verification", "Yes");
        temp.put("lastSurname", "Last");
        temp.put("personalTitlePrefix", "");
        temp.put("firstName", "First");
        tempList.put("0", temp);
        record.put("name", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("birthDate", "1977-01-01");
        tempList.put("0", temp);
        record.put("birthData", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("middleName", "Middle");
        temp.put("otherNameType", "Secondary");
        temp.put("lastSurname", "Last");
        temp.put("personalTitlePrefix", "");
        temp.put("firstName", "First");
        tempList.put("0", temp);
        record.put("otherName", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("indicator", "This is a student indicator");
        temp.put("indicatorName", "IndicatorName");
        temp.put("endDate", "2012-03-04");
        temp.put("beginDate", "2011-03-04");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("indicator", "This is a student indicator");
        temp.put("indicatorName", "IndicatorName");
        temp.put("endDate", "2011-03-04");
        temp.put("beginDate", "2010-03-04");
        tempList.put("1", temp);
        record.put("studentIndicators", tempList);

        tempList = new BasicDBList();
        tempList.put("0", "English");
        tempList.put("1", "Russian");
        record.put("homeLanguages", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("visualLearning", "50");
        temp.put("tactileLearning", "25");
        temp.put("auditoryLearning", "25");
        tempList.put("0", temp);
        record.put("learningStyles", tempList);
        
        record.put("limitedEnglishProficiency", "NotLimited");
        record.put("schoolFoodServicesEligibility", "Full price");
        record.put("displacementStatus", "Military Deployment");
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        tempList.put("0", temp);
        record.put("studentIdentificationCode", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("telephoneNumber", "212-555-1111");
        temp.put("primaryTelephoneNumberIndicator", "1");
        temp.put("telephoneNumberType", "Home");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("telephoneNumber", "212-555-2222");
        temp.put("primaryTelephoneNumberIndicator", "2");
        temp.put("telephoneNumberType", "Cell");
        tempList.put("1", temp);
        record.put("telephone", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("emailAddress", "test@test.com");
        temp.put("emailAddressType", "Primary");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("emailAddress", "sample@test.com");
        temp.put("emailAddressType", "Secondary");
        tempList.put("1", temp);
        record.put("electronicMail", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("nameOfCounty", "USA");
        temp.put("countyFIPSCode", "1");
        temp.put("apartmentRoomSuiteNumber", "100");
        temp.put("postalCode", "10003");
        temp.put("streetNumberName", "Wall St.");
        temp.put("stateAbbreviation", "St.");
        temp.put("buildingSiteNumber", "100");
        temp.put("longitude", "40.7142");
        temp.put("latitude", "74.0064");
        temp.put("addressType", "Home");
        temp.put("city", "New York City");
        tempList.put("0", temp);
        record.put("address", tempList);
        
        return record;
    }
	
}
