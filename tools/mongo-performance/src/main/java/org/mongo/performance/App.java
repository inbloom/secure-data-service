package org.mongo.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

public class App {
	public static boolean inputFromJsonFlag; 
	public static String entityType;
	public static final String INDEX_PATH="src\\main\\resources\\indexes\\index.properties";
	public static final String JSON_PATH="src\\main\\resources\\JsonFiles\\";
	private static String inputFile;
	
	@SuppressWarnings("unchecked")
	public static void main( String[] args ) {
		System.out.println("Bootstrapping Mongo Performance");
		
		if (args.length <7 || args.length >8 ) {
		    System.out.println("INVALID NUMBER OF INPUTS");
		    System.out.println("1. MODE (SAFE / NONE / NORMAL)");
		    System.out.println("2. NUMBER OF CONCURRENT PROCESSORS");
		    System.out.println("3. NUMBER OF TOTAL RECORDS OPERATED ON BY EACH TYPE OF OPERATION");
		    System.out.println("4. CHUNK SIZE (FOR READS / WRITES)");
		    System.out.println("5. RECORD TYPE PERSISTED (SHORT / FLAT / SHORTKEYS / NORMAL)");
		    System.out.println("6. TYPE OF OPERATIONS (W - WRITE VIA SPRING TEMPLATE / B - BATCHED WRITE VIA SPRING TEMPLATE / D - BATCHED WRITE VIA DRIVER / R - READ / T - BATCHED READ");
		    System.out.println("7. DROP COLLECTION (profiledCollection) PRIOR TO RUN (Y / N).");
		    System.out.println("8. PREFIX OF INPUT JSON FILE NAME.(PLEASE PUT THE JSON FILE UNDER DIR 'resources/JsonFiles/'. ALSO CONFIG THE index.properties FILE UNDER 'resources/indexes/')");
		    System.exit(0);
		}
		
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
        
        int recordType;
        if ("SHORT".equals(args[4])) {
            recordType = 1;
        } else if ("FLAT".equals(args[4])) {
            recordType = 2;
        } else if ("SHORTKEYS".equals(args[4])) {
            recordType = 3;
        } else {
            recordType = 4;
        }
        
        String concurrentOperationsEnabled = args[5];
        
        String dropCollectionFlag = args[6];
        
        if(args.length ==8)
        {
        	inputFile = args[7];
        	inputFromJsonFlag=true;
        }
        else 
        {
        	inputFromJsonFlag=false;
        }
        
        System.out.println("NUMBER OF PROCESSORS = " + numberOfProcessors);
        System.out.println("NUMBER OF RECORDS = " + numberOfRecords);
        System.out.println("CHUNK SIZE = " + chunkSize);
        System.out.println("RECORD TYPE = " + recordType);
        System.out.println("TYPES OF CONCURRENT OPERATIONS ENABLED = " + concurrentOperationsEnabled);
        System.out.println("COLLECTION DROP FLAG = " + dropCollectionFlag);
        
    	long startTime = System.currentTimeMillis();
    	
        if(inputFromJsonFlag)
        {
			MongoProcessor<DBObject> mongoProcessor = context.getBean(MongoProcessor.class);
        	if (recordType == 1) {
        		mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize, generateShortRecordfromJson(), concurrentOperationsEnabled, dropCollectionFlag);
        	} else if (recordType == 2) {
        		mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize, generateFlatRecordfromJson(), concurrentOperationsEnabled, dropCollectionFlag);
        	} else if (recordType == 3) {
            	mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize, generateRecordShortKeysfromJson(), concurrentOperationsEnabled, dropCollectionFlag);
        	} else {
        		mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize, generateRecordfromJson(), concurrentOperationsEnabled, dropCollectionFlag); 
        	}
        	mongoProcessor.writeStatistics();
        }
        else
        {
        	MongoProcessor<HashMap<String, Object>> mongoProcessor = context.getBean(MongoProcessor.class);
        	if (recordType == 1) {
                mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize, generateShortRecord(), concurrentOperationsEnabled, dropCollectionFlag);
            } else if (recordType == 2) {
                mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize, generateFlatRecord(), concurrentOperationsEnabled, dropCollectionFlag);
            } else if (recordType == 3) {
                mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize, generateRecordShortKeys(), concurrentOperationsEnabled, dropCollectionFlag);
            } else {
                mongoProcessor.run(numberOfProcessors, da, numberOfRecords / numberOfProcessors, chunkSize, generateRecord(), concurrentOperationsEnabled, dropCollectionFlag); 
            }
        	mongoProcessor.writeStatistics();
        }
        
    	long endTime = System.currentTimeMillis();
    	long elapsed = endTime - startTime;
    	
    	System.out.println();
    	System.out.println("START TIME = "+startTime+"           END TIME = "+endTime+"           ELAPSED TIME MS = "+elapsed);
    	System.out.println("-------------");
    	
        System.exit(0);
        
	}


private static DBObject generateRecordfromJson() {
	DBObject dbObject = null;
	File file = new File(JSON_PATH+inputFile+"-full.json");
	FileReader fr;
	try {
		fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String curLine;
		curLine = br.readLine();
		dbObject = (DBObject) JSON.parse(curLine);

	} catch (FileNotFoundException e) {
		System.out.println("The specified "+inputFile+"-full.json file is not found.");
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return dbObject;
	}


private static DBObject generateRecordShortKeysfromJson() {

		DBObject dbObject = null;
		File file = new File(JSON_PATH+inputFile+"-shortKeys.json");
		FileReader fr;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String curLine;
			curLine = br.readLine();
			dbObject = (DBObject) JSON.parse(curLine);

		} catch (FileNotFoundException e) {
			System.out.println("The specified "+inputFile+"-shortKeys.json file is not found.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dbObject;
      
	}


private static DBObject generateFlatRecordfromJson() {
	DBObject dbObject = null;
	File file = new File(JSON_PATH+inputFile+"-full.json");
	FileReader fr;
	try {
		fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String curLine;
		curLine = br.readLine();
		dbObject = (DBObject) JSON.parse(curLine);

	} catch (FileNotFoundException e) {
		System.out.println("The specified "+inputFile+"-full.json file is not found.");
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return dbObject;
	}


private static DBObject generateShortRecordfromJson() {
	DBObject dbObject = null;
//	File file = new File(JSON_PATH+"StudentAssessmentAssociation-short.json");
	File file = new File(JSON_PATH+inputFile+"-short.json");
	FileReader fr;
	try {
		fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String curLine;
		curLine = br.readLine();
		dbObject = (DBObject) JSON.parse(curLine);

	} catch (FileNotFoundException e) {
		System.out.println("The specified "+inputFile+"-short.json file is not found.");
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return dbObject;
	}	
	
	private static  HashMap<String, Object> generateShortRecord() {
        BasicDBObject record = new BasicDBObject();
        
        record.put("loginId", "1234567890");
        record.put("sex", "M");
        record.put("oldEthnicity", "White, Not Of Hispanic Origin");
        record.put("economicDisadvantaged", "false");
        record.put("hispanicLatinoEthnicity", "false");
        record.put("studentUniqueStateId", "0");
        record.put("profileThumbnail", "1000000000000000");
        
        return record;
	}
	
	private static  HashMap<String, Object> generateFlatRecord() {
	    BasicDBObject record = new BasicDBObject();
        
        record.put("loginId", "1234567890");
        record.put("sex", "M");
        record.put("oldEthnicity", "White, Not Of Hispanic Origin");
        record.put("economicDisadvantaged", "false");
        record.put("hispanicLatinoEthnicity", "false");
        record.put("studentUniqueStateId", "0");
        record.put("profileThumbnail", "1000000000000000");

        record.put("section504Disabilities-0", "Medical Condition");

        record.put("studentCharacteristics-0-endDate", "2010-03-04");
        record.put("studentCharacteristics-0-beginDate", "2010-03-04");
        record.put("studentCharacteristics-0-characteristic", "Foster Care");
        record.put("studentCharacteristics-1-endDate", "2011-03-04");
        record.put("studentCharacteristics-1-beginDate", "2011-03-04");
        record.put("studentCharacteristics-1-characteristic", "Foster Care");

        record.put("disabilities-0-disability", "Developmental Delay");
        record.put("disabilities-1-disability", "Developmental Delay");
        
        record.put("cohortYears-0-schoolYear", "2011-2012");
        record.put("cohortYears-0-cohortYearType", "Eleventh grade");
        record.put("cohortYears-1-schoolYear", "2010-2011");
        record.put("cohortYears-1-cohortYearType", "Tenth grade");

        record.put("race-0", "White");
        record.put("race-1", "Black - African American");
        
        record.put("programParticipations-0-program", "Athletics");
        record.put("programParticipations-0-endDate", "2012-03-04");
        record.put("programParticipations-0-beginDate", "2011-03-04");
        record.put("programParticipations-1-program", "Athletics");
        record.put("programParticipations-1-endDate", "2012=1-03-04");
        record.put("programParticipations-1-beginDate", "2010-03-04");

        record.put("languages-0", "English");
        record.put("languages-1", "Russian");
        
        record.put("name-0-middleName", "Middle");
        record.put("name-0-verification", "Yes");
        record.put("name-0-lastSurname", "Last");
        record.put("name-0-personalTitlePrefix", "");
        record.put("name-0-firstName", "First");
        
        record.put("birthData-0-birthDate", "1977-01-01");

        record.put("otherName-0-middleName", "Middle");
        record.put("otherName-0-otherNameType", "Secondary");
        record.put("otherName-0-lastSurname", "Last");
        record.put("otherName-0-personalTitlePrefix", "");
        record.put("otherName-0-firstName", "First");
        
        record.put("studentIndicators-0-indicator", "This is a student indicator");
        record.put("studentIndicators-0-indicatorName", "IndicatorName");
        record.put("studentIndicators-0-endDate", "2012-03-04");
        record.put("studentIndicators-0-beginDate", "2011-03-04");
        record.put("studentIndicators-1-indicator", "This is a student indicator");
        record.put("studentIndicators-1-indicatorName", "IndicatorName");
        record.put("studentIndicators-1-endDate", "2011-03-04");
        record.put("studentIndicators-1-beginDate", "2010-03-04");

        record.put("homeLanguages-0", "English");
        record.put("homeLanguages-1", "Russian");

        record.put("learningStyles-0-visualLearning", "50");
        record.put("learningStyles-0-tactileLearning", "25");
        record.put("learningStyles-0-auditoryLearning", "25");
        
        record.put("limitedEnglishProficiency", "NotLimited");
        record.put("schoolFoodServicesEligibility", "Full price");
        record.put("displacementStatus", "Military Deployment");
        
        record.put("studentIdentificationCode-0", "");

        record.put("telephone-0-telephoneNumber", "212-555-1111");
        record.put("telephone-0-primaryTelephoneNumberIndicator", "1");
        record.put("telephone-0-telephoneNumberType", "Home");
        record.put("telephone-1-telephoneNumber", "212-555-2222");
        record.put("telephone-1-primaryTelephoneNumberIndicator", "2");
        record.put("telephone-1-telephoneNumberType", "Cell");

        record.put("electronicMail-0-emailAddress", "test@test.com");
        record.put("electronicMail-0-emailAddressType", "Primary");
        record.put("electronicMail-1-emailAddress", "sample@test.com");
        record.put("electronicMail-1-emailAddressType", "Secondary");

        record.put("address-0-nameOfCounty", "USA");
        record.put("address-0-countyFIPSCode", "1");
        record.put("address-0-apartmentRoomSuiteNumber", "100");
        record.put("address-0-postalCode", "10003");
        record.put("address-0-streetNumberName", "Wall St.");
        record.put("address-0-stateAbbreviation", "St.");
        record.put("address-0-buildingSiteNumber", "100");
        record.put("address-0-longitude", "40.7142");
        record.put("address-0-latitude", "74-0064");
        record.put("address-0-addressType", "Home");
        record.put("address-0-city", "New York City");
        
        return record;
	}
	
	
	private static HashMap<String, Object> generateRecordShortKeys() {
        BasicDBObject record = new BasicDBObject();
        HashMap<String, Object> temp;
        DBObject tempList;
        
        record.put("1", "1234567890");
        record.put("2", "M");
        record.put("3", "White, Not Of Hispanic Origin");
        record.put("4", "false");
        record.put("5", "false");
        record.put("studentUniqueStateId", "0");
        record.put("6", "1000000000000000");

        tempList = new BasicDBList();
        tempList.put("0", "Medical Condition");
        record.put("7", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "2010-03-04");
        temp.put("1", "2010-03-04");
        temp.put("2", "Foster Care");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("0", "2011-03-04");
        temp.put("1", "2011-03-04");
        temp.put("2", "Foster Care");
        tempList.put("1", temp);
        record.put("8", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "Developmental Delay");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("0", "Developmental Delay");
        tempList.put("1", temp);
        record.put("9", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "2011-2012");
        temp.put("1", "Eleventh grade");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("0", "2010-2011");
        temp.put("1", "Tenth grade");
        tempList.put("1", temp);
        record.put("10", tempList);

        tempList = new BasicDBList();
        tempList.put("0", "White");
        tempList.put("1", "Black - African American");
        record.put("11", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "Athletics");
        temp.put("1", "2012-03-04");
        temp.put("2", "2011-03-04");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("0", "Athletics");
        temp.put("1", "2012=1-03-04");
        temp.put("2", "2010-03-04");
        tempList.put("1", temp);
        record.put("12", tempList);

        tempList = new BasicDBList();
        tempList.put("0", "English");
        tempList.put("1", "Russian");
        record.put("13", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "Middle");
        temp.put("1", "Yes");
        temp.put("2", "Last");
        temp.put("3", "");
        temp.put("4", "First");
        tempList.put("0", temp);
        record.put("14", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "1977-01-01");
        tempList.put("0", temp);
        record.put("15", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "Middle");
        temp.put("1", "Secondary");
        temp.put("2", "Last");
        temp.put("3", "");
        temp.put("4", "First");
        tempList.put("0", temp);
        record.put("16", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "This is a student indicator");
        temp.put("1", "IndicatorName");
        temp.put("2", "2012-03-04");
        temp.put("3", "2011-03-04");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("0", "This is a student indicator");
        temp.put("1", "IndicatorName");
        temp.put("2", "2011-03-04");
        temp.put("3", "2010-03-04");
        tempList.put("1", temp);
        record.put("17", tempList);

        tempList = new BasicDBList();
        tempList.put("0", "English");
        tempList.put("1", "Russian");
        record.put("18", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "50");
        temp.put("1", "25");
        temp.put("2", "25");
        tempList.put("0", temp);
        record.put("19", tempList);
        
        record.put("20", "NotLimited");
        record.put("21", "Full price");
        record.put("22", "Military Deployment");
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        tempList.put("0", temp);
        record.put("23", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "212-555-1111");
        temp.put("1", "1");
        temp.put("2", "Home");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("0", "212-555-2222");
        temp.put("1", "2");
        temp.put("2", "Cell");
        tempList.put("1", temp);
        record.put("24", tempList);
        
        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "test@test.com");
        temp.put("1", "Primary");
        tempList.put("0", temp);
        temp = new HashMap<String, Object>();
        temp.put("0", "sample@test.com");
        temp.put("1", "Secondary");
        tempList.put("1", temp);
        record.put("25", tempList);

        tempList = new BasicDBList();
        temp = new HashMap<String, Object>();
        temp.put("0", "USA");
        temp.put("1", "1");
        temp.put("2", "100");
        temp.put("3", "10003");
        temp.put("4", "Wall St.");
        temp.put("5", "St.");
        temp.put("6", "100");
        temp.put("7", "40.7142");
        temp.put("8", "74.0064");
        temp.put("9", "Home");
        temp.put("10", "New York City");
        tempList.put("0", temp);
        record.put("26", tempList);
        
        return record;
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
