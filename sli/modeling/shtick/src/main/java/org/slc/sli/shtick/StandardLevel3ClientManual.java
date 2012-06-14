package org.slc.sli.shtick;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.shtick.pojo.Name;
import org.slc.sli.shtick.pojo.Student;

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
    public List<Student> getStudents(final String token, final Map<String, Object> queryArgs) throws IOException,
            RestException {
        final List<RestEntity> entities = inner.getStudents(token, queryArgs);
        final List<Student> students = new ArrayList<Student>(entities.size());
        for (final RestEntity entity : entities) {
            students.add(convertToStudent(entity.getData()));
        }
        return students;
    }

    @Override
    public List<Student> getStudentsById(final String token, final String studentId, final Map<String, Object> queryArgs)
            throws IOException, RestException {
        final List<RestEntity> entities = inner.getStudentsById(token, studentId, queryArgs);
        final List<Student> students = new ArrayList<Student>(entities.size());
        for (final RestEntity entity : entities) {
            students.add(convertToStudent(entity.getData()));
        }
        return students;
    }

    private Student convertToStudent(final Map<String, Object> data) {
        final String id = (String) data.get("id");
        @SuppressWarnings("unchecked")
        final Name name = convertToName((Map<String, Object>) data.get("name"));
        final String sex = (String) data.get("sex");
        final Boolean economicDisadvantaged = (Boolean) data.get("economicDisadvantaged");
        final String studentUniqueStateId = (String) data.get("studentUniqueStateId");
        return new Student(id, name, sex, economicDisadvantaged, studentUniqueStateId);
    }

    private Name convertToName(final Map<String, Object> data) {
        final String firstName = (String) data.get("firstName");
        final String lastSurname = (String) data.get("lastSurname");
        return new Name(firstName, lastSurname);
    }
}
