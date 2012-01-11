package org.slc.sli.config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;

/** 
 * Utilities for Config info. 
 * Performs Java->XML conversion with JAXB.
 * 
 * @author dwu
 *
 */
public class ConfigUtil {

    private static JAXBContext jc;
    
    static {
        try {
            jc = JAXBContext.newInstance((new ViewConfigSet()).getClass().getPackage().getName());
        } catch (JAXBException e) {
            System.out.println("ERROR creating JAXBContext");
        }
    }
    
    /**
     * Transforms a view config set into its XML representation
     * 
     * @param configSet
     * @return an XML string
     * @throws Exception
     */
    public static String toXMLString(ViewConfigSet configSet) throws Exception {
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(configSet, os);
        return os.toString("UTF-8");
    }

    /**
     * Transforms view config XML into its Java object representation
     * 
     * @param configStr - the configuration in XML format
     * @return ViewConfigSet
     * @throws Exception
     */
    public static ViewConfigSet fromXMLString(String configStr) throws Exception {
        
        InputStream is = new ByteArrayInputStream(configStr.getBytes("UTF-8"));
        Unmarshaller u = jc.createUnmarshaller();
        ViewConfigSet configSet = (ViewConfigSet) (u.unmarshal(is));
        return configSet;
    }
    
    /**
     * Given a list of display sets, returns a list of all fields of a certain type
     * 
     * @param config
     * @param fieldType (i.e. "studentInfo", "assessment", etc)
     * @return a list of Field objects
     */
    public static List<Field> getDataFields(ViewConfig config, String fieldType) {
    
        return getDataFields(config.getDisplaySet(), fieldType);
    }
    
    /**
     * Given a list of display sets, returns a list of all fields of a certain type
     * 
     * @param displaySets - a list of DisplaySets, taken from a ViewConfig
     * @param fieldType
     * @return a list of Field objects
     */
    public static List<Field> getDataFields(List<DisplaySet> displaySets, String fieldType) {
        
        List<Field> dataFields = new ArrayList<Field>();
        
        for (DisplaySet displaySet : displaySets) {

            // recursive call for child display sets 
            dataFields.addAll(getDataFields(displaySet.getDisplaySet(), fieldType));
            
            // add fields
            for (Field field : displaySet.getField()) {
                if (field.getType().equals(fieldType)) {
                    dataFields.add(field);
                }
            }
        }
        return dataFields;
    }
    
}
