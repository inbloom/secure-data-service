package org.slc.sli.shtick;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.slc.sli.api.client.Entity;

/**
 * @author jstokes
 */
public class StandardLevel2ClientManual implements Level2ClientManual {

    private Level1ClientManual level1ClientManual;
    public StandardLevel2ClientManual(Level1ClientManual level1ClientManual) {
        this.level1ClientManual = level1ClientManual;
    }

    @Override
    public List<Entity> getStudentsByStudentId(String token, List<String> studentIds) throws IOException, SLIDataStoreException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Entity> getStudents(String token) throws IOException, SLIDataStoreException {
        try {
            return level1ClientManual.getRequest(token, new URL("TODO"));
        } catch (URISyntaxException e) {
            throw new AssertionError(e);
        }
    }
}
