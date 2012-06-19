package org.slc.sli.shtick;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.shtick.pojo.Student;
import org.slc.sli.shtick.pojomanual.StudentManual;

public final class StandardLevel3ClientManual implements Level3ClientManual {

    private final Level2Client inner;

    public StandardLevel3ClientManual(final String baseUrl) {
        this(baseUrl, new StandardLevel2Client(baseUrl));
    }

    public StandardLevel3ClientManual(final String baseUrl, final Level2Client inner) {
        if (baseUrl == null) {
            throw new NullPointerException("baseUrl");
        }
        if (inner == null) {
            throw new NullPointerException("inner");
        }
        this.inner = inner;
    }

    @Override
    public List<StudentManual> getStudents(final String token, final Map<String, Object> queryArgs) throws IOException,
            StatusCodeException {
        final List<Entity> entities = inner.getStudents(token, queryArgs);
        final List<StudentManual> students = new ArrayList<StudentManual>(entities.size());
        for (final Entity entity : entities) {
            students.add(new StudentManual(entity.getData()));
        }
        return students;
    }

    @Override
    public List<StudentManual> getStudentsById(final String token, final List<String> studentIds,
            final Map<String, Object> queryArgs) throws IOException, StatusCodeException {
        final List<Entity> entities = inner.getStudentsById(token, studentIds, queryArgs);
        final List<StudentManual> students = new ArrayList<StudentManual>(entities.size());
        for (final Entity entity : entities) {
            students.add(new StudentManual(entity.getData()));
        }
        return students;
    }

    @Override
    public String postStudent(final String token, final StudentManual student) throws IOException, StatusCodeException {
        return inner.postStudents(token, new Entity("student", student.getUnderlying()));
    }

    @Override
    public void putStudent(final String token, final StudentManual student) throws IOException, StatusCodeException {
        inner.putStudentsById(token, student.getId(), new Entity("student", student.getUnderlying()));
    }

    @Override
    public void deleteStudent(final String token, final StudentManual student) throws IOException, StatusCodeException {
        inner.deleteStudentsById(token, student.getId());
    }
}
