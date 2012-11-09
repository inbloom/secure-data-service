package org.slc.sli.test.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 * Based on org.slc.sli.test.utils.IngestionDataParser
 */
public class EdfiStats {
    private SAXParserFactory factory = SAXParserFactory.newInstance();
    private SAXParser saxParser = null;
        
    public EdfiStats() throws Exception{
        saxParser = factory.newSAXParser();
    }

    static final class Counter {
        long count = 0;
        
        public void plusOne(){
            count++;
        }
    }
    
    static final class  SAXHandler extends DefaultHandler {

        private int depth                                 = 0;
        private Map<String, Counter> elementCounts        = new HashMap<String, Counter>();
        private String interchange                        = null;
        //private String elementName                        = null;

        private char[] tagValue                           = new char[1000];
        private int tagValueLength                        = 0;
        private Set<String> uniqueStudentSessions         = new HashSet<String>(10000);
        
        private String studentId                          = null;
        private String schoolId                           = null;
        private boolean interchangeStudentEnrollment      = false;
        
        public void startElement(String uri, String lName,String name, Attributes a) 
                throws SAXException {
            depth++;
            //elementName = name;
            if(depth > 2) {/*do nothing*/}
            else if(depth == 2) {inc(name);}
            else if(depth == 1) {
                interchange = name; 
                if(interchange.equals("InterchangeStudentEnrollment")) 
                    interchangeStudentEnrollment = true;
                else
                    interchangeStudentEnrollment = false;
            }
        }

        public void characters(char ch[], int start, int length) 
                throws SAXException {
            if(interchangeStudentEnrollment && depth == 5) {
                tagValueLength = length;
                System.arraycopy(ch, start, tagValue, 0, length);
            }
        }

        public void endElement(String uri, String lName, String name) 
                throws SAXException {            
            if(interchangeStudentEnrollment)
                   if(depth == 5 && name.equals("StudentUniqueStateId")) {
                       studentId = new String(tagValue, 0, tagValueLength);                
            } else if(depth == 5 && name.equals("StateOrganizationId")) {
                       schoolId = new String(tagValue, 0, tagValueLength); 
            } else if(depth == 2 && name.equals("StudentSchoolAssociation")) {
                       //System.out.println("[" + studentId + "][" + schoolId + "]");
                       if(!uniqueStudentSessions.add(studentId + "," + schoolId)){
                       //    System.out.println("Duplicate StudentSessionAssociation["
                       //+ studentId + "," + schoolId + "]");
                       }
            }
            depth--;
        }
        
        private void inc(String name){
            Counter count = elementCounts.get(name);
            if(count == null) { count = new Counter(); elementCounts.put(name, count);}
            count.plusOne();
        }
        
        public Map<String, Long> getElementCounts() {
            Map<String, Long> counts = new HashMap<String, Long>();
            for(String element: elementCounts.keySet()){
                Counter count = elementCounts.get(element);
                counts.put(element, count.count);
            }
            return counts;
        }

        public Set<String> getUniqueStudentSessions() {
            return uniqueStudentSessions;
        }
        
    }
        
    private List<Object> getCountsForDirectory(String dir) throws Exception{
        System.out.println("Starting EdFi/SLI Stats Collection");
        long begin = System.currentTimeMillis();
        SAXHandler handler = new SAXHandler();
        for (File file : new File(dir).listFiles()) {
            if (file.isFile() && file.getName().endsWith(".xml")) {
                System.out.println("Processing [" + file.getName() + "]");
                saxParser.parse(file, handler);
            } else {
                //System.out.println("Ignoring [" + file.getName() + "].");
            }
        }
        Map<String, Long> elementCounts    = handler.getElementCounts();
        Set<String> uniqueStudentSessions  = handler.getUniqueStudentSessions();
        long end = System.currentTimeMillis();
        System.out.println("Finished in " + ((end -begin)/(1000f)) + " seconds");
        return Arrays.asList(elementCounts, uniqueStudentSessions);
    }
    
    public static void generateStats(String dataDirectory) throws Exception{
        EdfiStats stats = new EdfiStats();
        
        List<Object> counts = stats.getCountsForDirectory(dataDirectory);        
        Map<String, Long> edfiCounts = (Map<String, Long>)counts.get(0);
        Set<String> uniqueStudentSessions = (Set<String>)counts.get(1);
        
        Map<String, Long> sliCounts = IngestionDataParser.mapEntityCounts(edfiCounts);
        if (sliCounts.get("attendance") == 0) {
            sliCounts.remove("attendance");
        } else {
           sliCounts.put("attendance", (long)uniqueStudentSessions.size());
        }
        sliCounts.remove("assessmentItem"               );
        sliCounts.remove("assessmentFamily"             );
        sliCounts.remove("assessmentPeriodDescriptor"   );
        sliCounts.remove("courseOffering"               );
        sliCounts.remove("objectiveAssessment"          );
        sliCounts.remove("performanceLevelDescriptor"   );
        sliCounts.remove("serviceDescriptor"            );
        sliCounts.remove("studentAssessmentItem"        );
        sliCounts.remove("studentObjectiveAssessment"   );
        sliCounts.remove("studentAssessmentAssociation" );
        sliCounts.remove("studentSectionAssociation"    );
        sliCounts.remove("teacherSectionAssociation"    );
        sliCounts.remove("studentProgramAssociation"    );

        PrintWriter js = new PrintWriter(
                new FileOutputStream(dataDirectory + "/" + "jsExpected.js"));
        js.println("//mongo localhost:27017/sli --eval 'var tenant=\"MegatronIL\";' jsExpected.js");
        //http://stackoverflow.com/questions/2159678/paste-a-multi-line-java-string-in-eclipse
        js.println("var criteria = {};\r\n" + 
                "if( typeof tenant  != 'undefined' \r\n" + 
                "       &&  tenant  != null \r\n" + 
                "       &&  tenant.length > 0) \r\n" + 
                "    criteria['metaData.tenantId']=tenant;");
        js.println("var entities ={");
        for(String entity: new TreeSet<String>(sliCounts.keySet())) {
            js.println("              '" + entity + "'" +  ":" + sliCounts.get(entity) + ",");
        }
        js.println("};");

        js.println("for(var entity in  entities){\r\n" + 
        		"    var expectedCount = entities[entity];\r\n" + 
        		"    var mongoCount = db[entity].find(criteria).count();\r\n" + 
        		"    if(expectedCount != mongoCount)\r\n" + 
        		"        print (\"                                  >\"  + entity + \"[\" + expectedCount + \"/\" + mongoCount + \"] Mismatch *\"); \r\n" + 
        		"    else\r\n" + 
        		"        print (entity + \"[\" + expectedCount + \"/\" + mongoCount + \"] Match *\"); \r\n" + 
        		"}");
        js.close();
        
        PrintWriter readme = new PrintWriter(
                new FileOutputStream(dataDirectory + "/" + "README"));
        long totalCount = 0;
        for(String entity: new TreeSet<String>(edfiCounts.keySet())) {
            Long count = edfiCounts.get(entity);
            readme.println(String.format("%-50s%10d", entity, count));
            totalCount += count;
        }
        readme.println("------------------------------------------------------------");
        readme.println(String.format("%50s:%10d", "Total", totalCount));
        System.out.println(String.format("%30s:%-10d", "Total Entities", totalCount));
        System.out.println("see " + dataDirectory + "/" + "expected.js");
        System.out.println("see " + dataDirectory + "/" + "README");
        readme.close();
    }
 
    public static void main(String argv[]) throws Exception{
        String dataDirectory  = "C:\\Users\\ldalgado\\Desktop\\TestPlan\\GalvatonSDS\\Medium";
        EdfiStats.generateStats(dataDirectory);        
    }
}