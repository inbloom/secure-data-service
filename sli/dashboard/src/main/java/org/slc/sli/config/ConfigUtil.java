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
            jc = JAXBContext.newInstance("org.slc.sli.config");
        } catch (JAXBException e) {
            System.out.println("ERROR creating JAXBContext");
        }
    }
    
    /*
     * Transforms a view config set into its XML representation
     */
    public static String toXMLString(ViewConfigSet configSet) throws Exception {
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(configSet, os);
        return os.toString("UTF-8");
    }

    /*
     * Transforms view config XML into its Java object representation
     */
    public static ViewConfigSet fromXMLString(String configStr) throws Exception {
        
        InputStream is = new ByteArrayInputStream(configStr.getBytes("UTF-8"));
        Unmarshaller u = jc.createUnmarshaller();
        ViewConfigSet configSet = (ViewConfigSet) (u.unmarshal(is));
        return configSet;
    }
    
    /*
     * Given a view config, returns a list of all data set elements
     */
    /*
    public static List<DataSet> getDataSets(ViewConfig config, String dataSetType) {
        List<DataSet> dataSets = new ArrayList<DataSet>();
        for (DataSet dataSet : config.getDataSet()) {
            if (dataSet.getType().equals(dataSetType)) {
                dataSets.add(dataSet);
            }
        }
        return dataSets;
    }
    */
    
    
    /*
     * Given a list of display sets, returns a list of all fields of a certain type
     */
    public static List<Field> getDataFields(ViewConfig config, String fieldType) {
    
        return getDataFields(config.getDisplaySet(), fieldType);
    }
    
    /*
     * Given a list of display sets, returns a list of all fields of a certain type
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
