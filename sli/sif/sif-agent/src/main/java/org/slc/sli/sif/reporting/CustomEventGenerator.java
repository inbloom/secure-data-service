package org.slc.sli.sif.reporting;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import openadk.library.ElementDef;
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.SIFDataObject;
import openadk.library.SIFDataObjectXML;
import openadk.library.SIFElement;
import openadk.library.SIFParser;

public class CustomEventGenerator implements EventGenerator {

    @Override
    public Event generateEvent(Properties eventProps) {
        String messageFile = eventProps.getProperty(CustomEventGenerator.MESSAGE_FILE);
        FileReader in = null;
        SIFDataObject dataObject = null;
        try {
            // Construct a SIFParser instance that can be used for parsing
            SIFParser p = SIFParser.newInstance();
            // Open a FileReader to read the text file
            in = new FileReader(messageFile);
            // Read the file into a StringBuffer in 4K chunks
            StringBuffer xml = new StringBuffer();
            int bufSize = 4096;
            char[] buf = new char[bufSize];
            while(in.ready()) {
                bufSize = in.read(buf, 0, buf.length);
                xml.append(buf, 0, bufSize);
            }
            // Parse it
            SIFElement element = p.parse(xml.toString(), null /* zone */ );
            System.out.println(element.toString());

            // Convert it
            ElementDef elementDef = element.getElementDef();
            dataObject = new SIFDataObjectXML(elementDef, xml.toString());

        } catch (Exception e) {
            // eat it
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        Event event = new Event(dataObject, EventAction.ADD);
        return event;
    }

}
