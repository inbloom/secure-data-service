package org.slc.sli.test.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;
import static org.slc.sli.test.utils.InterchangeStatisticsWriterUtils.*;

/**
 * Interchange writer
 *
 * @author bsuzuki
 *
 */
public class InterchangeWriter<T> {

    private static final int XML_WRITER_BUFFER_SIZE = 104857600; // 100 MB
    private static final boolean FORMAT_INTERCHANGE_XML = true;
    private static final boolean SINGLE_LINE_MARSHALLING = true;

    public static final String REPORT_INDENTATION = "  ";
    private static final String STATISTICS_FILE = "./data/statistics.json";
    
    private static PrintWriter statsWriter = null;
    
    private String interchangeName = null;
    private String xmlFilePath = null;

    private XMLStreamWriter writer = null;
    private XMLStreamWriter defaultWriter = null;
    private IndentingXMLStreamWriter indentingWriter = null;
    
    private Marshaller streamMarshaller = null;
    
    private long interchangeStartTime;
    private long marshaledCount;
    
    public InterchangeWriter(Class<T> interchange) {
        
        interchangeStartTime = System.currentTimeMillis();
        interchangeName = interchange.getSimpleName();
        xmlFilePath = StateEdFiXmlGenerator.rootOutputPath + "/" + interchangeName + ".xml";

        writeInterchangeStatisticStart(interchangeName);
        
        try {
            JAXBContext context = JAXBContext.newInstance(interchange);
            streamMarshaller = context.createMarshaller();
            // Doesn't work for XMLStreamWriter
//            streamMarshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            streamMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        } catch (JAXBException e1) {
            e1.printStackTrace();
            System.exit(1);  // fail fast for now
        }

        try {
            FileOutputStream xmlFos = new FileOutputStream(xmlFilePath);
            OutputStream xmlBos = new BufferedOutputStream(xmlFos, XML_WRITER_BUFFER_SIZE);
            defaultWriter = XMLOutputFactory.newFactory().createXMLStreamWriter(xmlBos, "UTF-8");
            if (FORMAT_INTERCHANGE_XML) {
                indentingWriter = new IndentingXMLStreamWriter(defaultWriter);
                indentingWriter.setIndentStep("    ");
                writer = indentingWriter;
            } else {
                writer = defaultWriter;
            }
            writer.writeStartDocument("UTF-8", "1.0");

            // remove unwanted population of namespace attributes
            writer.setNamespaceContext(new NamespaceContext() {
                public Iterator<String> getPrefixes(String namespaceURI) {
                    return null;
                }

                public String getPrefix(String namespaceURI) {
                    return "";
                }

                public String getNamespaceURI(String prefix) {
                    return null;
                }
            });
            
            writer.writeStartElement(interchangeName);
            writer.writeNamespace(null, "http://ed-fi.org/0100");
            
        } catch (XMLStreamException e) {
            e.printStackTrace();
            System.exit(1);  // fail fast for now
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
            System.exit(1);  // fail fast for now
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);  // fail fast for now
        }
    }

    public void close() {
        
        try {
            if (FORMAT_INTERCHANGE_XML && SINGLE_LINE_MARSHALLING) {
                defaultWriter.writeCharacters("\n");
            }
//            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
            System.exit(1);  // fail fast for now
        }
        
        writeInterchangeStatisticEnd(marshaledCount, System.currentTimeMillis() - interchangeStartTime);

        streamMarshaller = null;
    }

    /**
     * Marshal the provided object using the XMLStreamWriter specified
     *
     * @param objectToMarshal
     * @param outputStream
     * @throws JAXBException
     */
    @SuppressWarnings("unused")
    public void marshal(Object objectToMarshal) {
        if (objectToMarshal != null) {

            try {                
                if (FORMAT_INTERCHANGE_XML && SINGLE_LINE_MARSHALLING) {
                    defaultWriter.writeCharacters("\n");
                    streamMarshaller.marshal(objectToMarshal, defaultWriter);
                } else {
                    streamMarshaller.marshal(objectToMarshal, writer);
                }
                marshaledCount++;
            } catch (JAXBException e) {
                e.printStackTrace();
                System.exit(1);  // fail fast for now
            } catch (XMLStreamException e) {
                e.printStackTrace();
                System.exit(1);  // fail fast for now
            }

        } else {
            throw new IllegalArgumentException("Cannot marshal null object");
        }
    }

    private static void initStatisticsWriter() {
        if (statsWriter != null) return;
        
        try {
            statsWriter = new PrintWriter(new BufferedWriter(new FileWriter(STATISTICS_FILE)));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
//    private void writeInterchangeStatisticStart() {
//        if (statsWriter == null) {
//            initStatisticsWriter();
//        }
//        
//        StringBuffer statInfo = new StringBuffer();
//        statInfo.append("{ \"" + interchangeName + "\" : " + "\n");
//        statInfo.append(REPORT_INDENTATION + "{");
//        statsWriter.println(statInfo);
//        
//        System.out.println("{ \"" + interchangeName + "\" : ");
//        System.out.println(REPORT_INDENTATION + "{");
//    }
//    
//    private void writeInterchangeStatisticEnd() {
//        if (statsWriter == null) {
//            initStatisticsWriter();
//        }
//
//        StringBuffer statInfo = new StringBuffer();
//        statInfo.append(REPORT_INDENTATION + REPORT_INDENTATION + "\"Count\" : " + marshaledCount + ",\n");
//        statInfo.append(REPORT_INDENTATION + REPORT_INDENTATION + "\"ElapsedTime\" : " + (System.currentTimeMillis() - interchangeStartTime) +"\n");
//        statInfo.append(REPORT_INDENTATION + "}\n");
//        statInfo.append("}");
//        statsWriter.println(statInfo);
//        statsWriter.flush();
//        
//        System.out.println(REPORT_INDENTATION + REPORT_INDENTATION + "\"Count\" : " + marshaledCount + ",");
//        System.out.println(REPORT_INDENTATION + REPORT_INDENTATION + "\"ElapsedTime\" : " + (System.currentTimeMillis() - interchangeStartTime));
//        System.out.println(REPORT_INDENTATION + "}");
//        System.out.println("}");
//    }
//
//    public static void writeInterchangeEntityStatistic(String entityName, long entityCount, long elapsedTime) {
//        if (statsWriter == null) {
//            initStatisticsWriter();
//        }
//
//        StringBuffer statInfo = new StringBuffer();
//        statInfo.append(REPORT_INDENTATION + REPORT_INDENTATION + "{ \"" + entityName 
//                + "\" : { \"count\" : " + entityCount + ", \"elapsedTime\" : " + elapsedTime + " } }");
//        statsWriter.println(statInfo);
//        
//        System.out.println(REPORT_INDENTATION + REPORT_INDENTATION + "{ \"" + entityName 
//                + "\" : { \"count\" : " + entityCount + ", \"elapsedTime\" : " + elapsedTime + " } }");
//    }
    
    public String getXmlFilePath() {
        return xmlFilePath;
    }

    
}
