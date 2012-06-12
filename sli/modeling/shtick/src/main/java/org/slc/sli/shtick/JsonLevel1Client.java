package org.slc.sli.shtick;

import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author jstokes
 */
public final class JsonLevel1Client extends AbstractLevel1Client implements Level1Client {

    public JsonLevel1Client() {
        super(new StandardLevel0Client(), new ObjectMapper());
    }

    @Override
    protected String getMediaType() {
        return MediaType.APPLICATION_JSON;
    }
}
