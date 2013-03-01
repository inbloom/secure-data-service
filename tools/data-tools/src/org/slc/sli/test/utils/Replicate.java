package org.slc.sli.test.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


//http://docs.oracle.com/javaee/1.4/tutorial/doc/JAXPSAX3.html
public class Replicate {

    static SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMyyyy_HHmmss");
    
    public static void main(String[] argv) throws Exception{
        if (argv.length != 2) {
            System.err.println("Usage: cmd inputXmlDir  copyCount");
            System.exit(1);
        }

        String dateStr = dateFormatter.format(new Date());
        String outputDirName = argv[0] + "/" + dateStr;
        if(!(new File(outputDirName)).mkdir()) {
            System.out.println("Could not create [" + outputDirName + "]. Exiting!");
            System.exit(1);
        } else { System.out.println("Output will be saved to [" + outputDirName + "].");}


        for(int i = 0; i < Integer.parseInt(argv[1]); i++) {

            String iCopyDirName = argv[0] + "/" + dateStr + "/" + i;

            if(!(new File(iCopyDirName)).mkdir()) {
                System.out.println("Could not create [" + iCopyDirName + "]. Exiting!");
                System.exit(1);
            }

            for (File file : new File(argv[0]).listFiles()) {
                if (file.isFile()) {
                    String fname = file.getName();
                    if(fname.endsWith(".xml")) {
                        String outputFileName = outputDirName + "/" + i + "/" + fname;
                        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outputFileName), "UTF8");
                        DefaultHandler handler = new Raptor(out, String.valueOf(10 + i));
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser saxParser = factory.newSAXParser();
                        String fullPath = file.getAbsolutePath();
                        saxParser.parse(new File(fullPath), handler);
                        DataUtils.writeControlFile(outputDirName + "/" + i + "/MainControlFile.ctl", getInterchangeFromName(fname), outputFileName);
                    }
                }
            }
            ValidateSchema.check(outputDirName + "/" + i);
            EdfiStats.generateStats(outputDirName + "/" + i);
        }
    }
    
    static String getInterchangeFromName(String fname) {
        if(fname.contains("AssessmentMetadata.xml"))       return "AssessmentMetadata";
        if(fname.contains("EducationOrgCalendar.xml"))     return "EducationOrgCalendar";
        if(fname.contains("EducationOrganization.xml"))    return "EducationOrganization";
        if(fname.contains("MasterSchedule.xml"))           return "MasterSchedule";
        if(fname.contains("StaffAssociation.xml"))         return "StaffAssociation";
        if(fname.contains("StudentAssessment.xml"))        return "StudentAssessment";
        if(fname.contains("StudentCohort.xml"))            return "StudentCohort";
        if(fname.contains("StudentDiscipline.xml"))        return "StudentDiscipline";
        if(fname.contains("StudentEnrollment.xml"))        return "StudentEnrollment";
        if(fname.contains("StudentGrade.xml"))             return "StudentGrades";
        if(fname.contains("StudentParent.xml"))            return "StudentParent";
        if(fname.contains("StudentProgram.xml"))           return "StudentProgram";
        return null;
    }
}

final class  Raptor extends DefaultHandler {
    private Writer out;
    private StringBuffer textBuffer;
    private String MOD;
    private Stack<String> elementStack = new Stack<String>();
    
    private boolean lastCallbackForStart;    
   
    Map<String,String> dnmElements = new HashMap<String, String>();
    
    Raptor( Writer out, String dataPrefix ) {
        this.out = out;
        MOD = dataPrefix;
        dnmElements.put("NumberOfParts",    "NumberOfParts");
        dnmElements.put("CountyFIPSCode",   "CountyFIPSCode");
        dnmElements.put("SequenceOfCourse", "SequenceOfCourse");
    }
        
    public void startDocument() throws SAXException {
        emit("<?xml version='1.0' encoding='UTF-8'?>");
        nl();
    }

    public void endDocument() throws SAXException {
        try {
            nl();
            out.flush();
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }

    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        echoText();
        String eName = sName;
        if ("".equals(eName)) {
            eName = qName; 
        }
        elementStack.push(eName);
        emit("<" + eName);
        if (attrs != null) {
            for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); 

                if ("".equals(aName)) {
                    aName = attrs.getQName(i);
                }
                emit(" ");
                
                String p = (aName.equals("xmlns"))?"": (doNotModify(elementStack, aName, attrs.getValue(i))?"":MOD ); 
                emit(aName + "=\"" + attrs.getValue(i) + p + "\"");
            }
        }
        emit(">");
        lastCallbackForStart = true;
    }

    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
        String eName = sName;
        if ("".equals(eName)) {
            eName = qName;
        }        
        if(lastCallbackForStart && textBuffer != null && !doNotModify(elementStack, eName, textBuffer.toString())) {
            emit(MOD);
        }
        echoText();        
        emit("</" + eName + ">");
        lastCallbackForStart = false;
        elementStack.pop();
    }

    public void characters(char[] buf, int offset, int len)
        throws SAXException {
        String s = new String(buf, offset, len);
        if (textBuffer == null) {
            textBuffer = new StringBuffer(s);
        } else {
            textBuffer.append(s);
        }
    }

    private void echoText() throws SAXException {
        if (textBuffer == null) {
            return;
        }
        String s = "" + textBuffer;
        emit(s);
        textBuffer = null;
    }

    private void emit(String s) throws SAXException {
        try {
            out.write(s);
            out.flush();
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }

    private void nl() throws SAXException {
        String lineEnd = System.getProperty("line.separator");
        try {
            out.write(lineEnd);
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }
    
    private boolean doNotModify(Stack<String> path, String name, String value) {
        if (EnumeratedValues.enumeratedValues.containsKey(value)) {
            return true;
        } else if(value.matches(".*\\d\\d\\d\\d-\\d\\d-\\d\\d.*")) {//do not modify dates
            return true;
        } else if(value.matches(".*\\d\\d:\\d\\d:\\d\\d.*")) {//do not modify times 
            return true;
        } else if(dnmElements.containsKey(name)) {
            return true;
        } else {
                if(path.contains("StateEducationAgency") || path.contains("StateEducationAgencyReference")){
                    return true;
                }
        }  
        return false;
    }
}