package org.slc.sli.shtick;

import com.fasterxml.jackson.xml.XmlMapper;

import javax.ws.rs.core.MediaType;

/**
 * @author jstokes
 */
public class XmlLevel1Client extends AbstractLevel1Client implements Level1Client {

    public XmlLevel1Client() {
        super(new StandardLevel0Client(), new XmlMapper());
    }

    @Override
    protected String getMediaType() {
        return MediaType.APPLICATION_XML;
    }
}
