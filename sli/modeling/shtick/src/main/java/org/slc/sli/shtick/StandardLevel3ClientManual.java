package org.slc.sli.shtick;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.client.Entity;
import org.slc.sli.shtick.pojo.Student;

public final class StandardLevel3ClientManual implements Level3ClientManual {

    private final Level2Client inner;

    public StandardLevel3ClientManual(final String baseUrl) {
        if (baseUrl == null) {
            throw new NullPointerException("baseUrl");
        }
        inner = new StandardLevel2Client(baseUrl);
    }

    @Override
    public List<Student> getStudents(String token, Map<String, Object> queryArgs) throws IOException, HttpRestException {
        final List<Entity> entities = inner.getStudents(token, queryArgs);
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Student> getStudentsById(List<String> studentIds) throws IOException, HttpRestException {
        // TODO Auto-generated method stub
        return null;
    }

}
