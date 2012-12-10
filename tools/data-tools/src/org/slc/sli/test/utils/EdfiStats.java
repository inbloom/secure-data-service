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

import org.slc.sli.common.domain.EmbeddedDocumentRelations;

/*
 * Based on org.slc.sli.test.utils.IngestionDataParser
 */
public class EdfiStats {
    private static final String README_FILE = "README";

    private static final String EXPECTED_JS = "jsExpected.js";
    
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
        
        // Derive counts for transformed entities
        Map<String, Long> sliCounts = IngestionDataParser.mapEntityCounts(edfiCounts);
        if (sliCounts.get("attendance") == 0) {
            sliCounts.remove("attendance");
        } else {
           sliCounts.put("attendance", (long)uniqueStudentSessions.size());
        }
        
        // Derive counts for subdocs or superdocs
        Map<String, Map<String, Long>> superDocCounts = deriveSuperDocCounts(sliCounts);
        
        // Remove counts that should not be directly checked
        sliCounts.remove("assessmentItem"                       );
        sliCounts.remove("assessmentFamily"                     );
        sliCounts.remove("assessmentPeriodDescriptor"           );
        sliCounts.remove("objectiveAssessment"                  );
        sliCounts.remove("performanceLevelDescriptor"           );
        sliCounts.remove("serviceDescriptor"                    );
        sliCounts.remove("studentAssessmentItem"                );
        sliCounts.remove("studentObjectiveAssessment"           );
        sliCounts.remove("studentAssessmentAssociation"         );

        PrintWriter js = new PrintWriter(
                new FileOutputStream(dataDirectory + File.separator + EXPECTED_JS));
        js.println("//mongo localhost:27017/<database_name> " + EXPECTED_JS);
        //http://stackoverflow.com/questions/2159678/paste-a-multi-line-java-string-in-eclipse
        js.println("var entities ={");
        for(String entity: new TreeSet<String>(sliCounts.keySet())) {
            js.println("              '" + entity + "'" +  ":" + sliCounts.get(entity) + ",");
        }
        js.println("};");

        js.println("var superdocs = {");
        // Write out superdoc entities
        for(String superEntity: new TreeSet<String>(superDocCounts.keySet())) {
            js.println("    '" + superEntity + "':{");
            
            // Write out subdoc counts
            for(String subEntity: new TreeSet<String>(superDocCounts.get(superEntity).keySet())) {
                js.println("        '" + subEntity + "'" +  ":" + superDocCounts.get(superEntity).get(subEntity) + ",");
            }
            
            js.println("    },");
        }
        js.println("}");

        js.println(
                "for(var entity in  entities){\n" + 
                "    var expectedCount = entities[entity];\n" + 
                "    var mongoCount = db[entity].find().count();\n" + 
                "    if(expectedCount != mongoCount) {\n" + 
                "        print (\"                                  >\"  + entity + \"[\" + expectedCount + \"/\" + mongoCount + \"] Mismatch *\"); \n" + 
                "    } else {\n" + 
                "        print (entity + \"[\" + expectedCount + \"/\" + mongoCount + \"] Match *\"); \n" + 
                "    }\n" + 
                "}\n" + 
                "print(\"\\nChecking sub-document counts:\");\n" + 
                "for (var superdoc in superdocs) {\n" + 
                "    var fields = superdocs[superdoc];\n" + 
                "    var expectedCounts = {};\n" + 
                "    var mongoCounts = {};\n" + 
                "    for (var field in fields) {\n" + 
                "        expectedCounts[field] = fields[field];\n" + 
                "        mongoCounts[field] = 0;\n" + 
                "    }\n" + 
                "    db[superdoc].find().forEach( function(x){\n" + 
                "        for (var field in fields) {\n" + 
                "            if (field in x) {\n" + 
                "                mongoCounts[field] += x[field].length;\n" + 
                "            }\n" + 
                "        }\n" + 
                "    })\n" + 
                "    for (var field in fields) {\n" + 
                "        var s = superdoc + \".\" + field + \"[\" + expectedCounts[field] + \"/\" + mongoCounts[field] + \"]\";\n" + 
                "        if (expectedCounts[field] != mongoCounts[field]) {\n" + 
                "            print (\"                                  >\"  + s + \"Mismatch *\"); \n" + 
                "        } else {\n" + 
                "            print (\"  \" + s + \" Match *\"); \n" + 
                "        }\n" + 
                "    }\n" + 
                "}\n" + 
                "");
        js.close();
        
        PrintWriter readme = new PrintWriter(
                new FileOutputStream(dataDirectory + File.separator + README_FILE));
        long totalCount = 0;
        for(String entity: new TreeSet<String>(edfiCounts.keySet())) {
            Long count = edfiCounts.get(entity);
            readme.println(String.format("%-50s%10d", entity, count));
            totalCount += count;
        }
        readme.println("------------------------------------------------------------");
        readme.println(String.format("%50s:%10d", "Total", totalCount));
        System.out.println(String.format("%30s:%-10d", "Total Entities", totalCount));
        System.out.println("see " + dataDirectory + File.separator + EXPECTED_JS);
        System.out.println("see " + dataDirectory + File.separator + README_FILE);
        readme.close();
    }
 
    private static Map<String, Map<String, Long>> deriveSuperDocCounts(Map<String, Long> sliCounts) {
        Map<String, Map<String, Long>> superDocCounts = new HashMap<String, Map<String, Long>>();
        
//        // Add count information for DENORMALIZATIONs
//        for(String element: EmbeddedDocumentRelations.getDenormalizedDocuments()){
//            // Associate the subdoc count to the destination entity and field
//            
//            // HACK remove the subdoc count from the sliCounts to prevent it being checked directly
//            Long count = sliCounts.remove(element);
//            
//            if (count != null) {
//                String superDoc = EmbeddedDocumentRelations.getDenormalizeToEntity(element);
//                String subDoc = EmbeddedDocumentRelations.getDenormalizedToField(element);
//                Map<String, Long> subDocCounts = superDocCounts.get(superDoc);
//                if (subDocCounts == null) {
//                    // Create the super doc entry if necessary
//                    subDocCounts = new HashMap<String, Long>();
//                    superDocCounts.put(superDoc, subDocCounts);
//                }           
//                subDocCounts.put(subDoc, count);
//            }
//        }
        
        // Add count information for sub-documents
        for(String element: EmbeddedDocumentRelations.getSubDocuments()){
            
            // HACK remove the subdoc count from the sliCounts to prevent it being checked directly
            Long count = sliCounts.remove(element);
            
            if (count != null) {
                String superDoc = EmbeddedDocumentRelations.getParentEntityType(element);
                Map<String, Long> subDocCounts = superDocCounts.get(superDoc);
                if (subDocCounts == null) {
                    // Create the super doc entry if necessary
                    subDocCounts = new HashMap<String, Long>();
                    superDocCounts.put(superDoc, subDocCounts);
                }
                if (subDocCounts.containsKey(element)) {
                    count += subDocCounts.get(element);
                }
                subDocCounts.put(element, count);
            }
        }
        return superDocCounts;
    }

    public static void main(String argv[]) throws Exception{
        String dataDirectory  = "C:\\Users\\ldalgado\\Desktop\\TestPlan\\GalvatonSDS\\Medium";
        if (argv.length > 0) {
            dataDirectory = argv[0];
        }
        System.out.println("Generating expected counts for xml files at " + dataDirectory);
        EdfiStats.generateStats(dataDirectory);        
    }
}