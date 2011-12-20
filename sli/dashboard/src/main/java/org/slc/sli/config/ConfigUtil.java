package org.slc.sli.config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
//import java.io.UnsupportedEncodingException;

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
    
    public static String toXMLString(ViewConfig config) throws Exception {
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(config, os);
        return os.toString("UTF-8");
    }

    public static ViewConfig fromXMLString(String configStr) throws Exception {
        
        InputStream is = new ByteArrayInputStream(configStr.getBytes("UTF-8"));
        Unmarshaller u = jc.createUnmarshaller();
        ViewConfig config = (ViewConfig) (u.unmarshal(is));
        return config;
    }
}
