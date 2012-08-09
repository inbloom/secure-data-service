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
        
        /*
        tempList = new ArrayList<Map<String, Object>>();
        temp = new HashMap<String, Object>();
        temp.put("endDate", "2010-03-04");
        temp.put("beginDate", "2010-03-04");
        temp.put("characteristic", "Foster Care");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("endDate", "2011-03-04");
        temp.put("beginDate", "2011-03-04");
        temp.put("characteristic", "Foster Care");
        tempList.add(temp);
        record.put("studentCharacteristics", tempList);

        tempList = new ArrayList<HashMap<String, Object>>();
        temp = new HashMap<String, Object>();
        temp.put("disability", "Developmental Delay");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("disability", "Developmental Delay");
        tempList.add(temp);
        record.put("disabilities", tempList);
        
        tempList = new ArrayList<HashMap<String, Object>>();
        temp = new HashMap<String, Object>();
        temp.put("schoolYear", "2011-2012");
        temp.put("cohortYearType", "Eleventh grade");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("schoolYear", "2010-2011");
        temp.put("cohortYearType", "Tenth grade");
        tempList.add(temp);
        record.put("cohortYears", tempList);

        tempListString = new ArrayList<String>();
        tempListString.add("White");
        tempListString.add("Black - African American");
        record.put("race", tempListString);
        
        tempList = new ArrayList<HashMap<String, Object>>();
        temp = new HashMap<String, Object>();
        temp.put("program", "Athletics");
        temp.put("endDate", "2012-03-04");
        temp.put("beginDate", "2011-03-04");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("program", "Athletics");
        temp.put("endDate", "2012=1-03-04");
        temp.put("beginDate", "2010-03-04");
        tempList.add(temp);
        record.put("programParticipations", tempList);
        
        tempListString = new ArrayList<String>();
        tempListString.add("English");
        tempListString.add("Russian");
        record.put("languages", tempListString);
        
        tempList = new ArrayList<HashMap<String, Object>>();
        temp = new HashMap<String, Object>();
        temp.put("middleName", "Middle");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("verification", "Yes");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("lastSurname", "Last");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("personalTitlePrefix", "");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("firstName", "First");
        tempList.add(temp);
        record.put("name", tempList);
        
        tempList = new ArrayList<HashMap<String, Object>>();
        temp = new HashMap<String, Object>();
        temp.put("birthDate", "1977-01-01");
        tempList.add(temp);
        record.put("birthData", tempList);
        
        tempList = new ArrayList<HashMap<String, Object>>();
        temp = new HashMap<String, Object>();
        temp.put("middleName", "Middle");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("otherNameType", "Secondary");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("lastSurname", "Last");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("personalTitlePrefix", "");
        tempList.add(temp);
        temp = new HashMap<String, Object>();
        temp.put("firstName", "First");
        tempList.add(temp);
        tempListOther = new ArrayList<ArrayList<HashMap<String, Object>>>();
        tempListOther.add(tempList);
        record.put("otherName", tempListOther);
        
        
        record.put("studentIndicators", "");
        record.put("homeLanguages", "");
        record.put("learningStyles", "");
        record.put("limitedEnglishProficiency", "NotLimited");
        record.put("studentIdentificationCode", "");
        record.put("address", "");
        record.put("schoolFoodServicesEligibility", "Full price");
        record.put("electronicMail", "");
        record.put("displacementStatus", "Military Deployment");
        record.put("telephone", "");
        */
        
        return record;
    }
	
}
