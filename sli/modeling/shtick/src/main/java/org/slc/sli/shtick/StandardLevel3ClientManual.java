/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.shtick;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.shtick.pojo.Student;

public final class StandardLevel3ClientManual implements Level3ClientManual {
    
    private final Level2Client inner;
    
    public StandardLevel3ClientManual(final String baseUrl) {
        this(baseUrl, new StandardLevel2Client(baseUrl));
    }
    
    public StandardLevel3ClientManual(final String baseUrl, final Level2Client inner) {
        if (baseUrl == null) {
            throw new IllegalArgumentException("baseUrl");
        }
        if (inner == null) {
            throw new IllegalArgumentException("inner");
        }
        this.inner = inner;
    }
    
    @Override
    public List<Student> getStudents(final String token, final Map<String, Object> queryArgs) throws IOException,
            StatusCodeException {
        final List<Entity> entities = inner.getStudents(token, queryArgs);
        final List<Student> students = new ArrayList<Student>(entities.size());
        for (final Entity entity : entities) {
            students.add(new Student(entity.getData()));
        }
        return students;
    }
    
    @Override
    public List<Student> getStudentsById(final String token, final List<String> studentIds,
            final Map<String, Object> queryArgs) throws IOException, StatusCodeException {
        final List<Entity> entities = inner.getStudentsById(token, studentIds, queryArgs);
        final List<Student> students = new ArrayList<Student>(entities.size());
        for (final Entity entity : entities) {
            students.add(new Student(entity.getData()));
        }
        return students;
    }
    
    @Override
    public String postStudent(final String token, final Student student) throws IOException, StatusCodeException {
        return inner.postStudents(token, new Entity("student", student.toMap()));
    }
    
    @Override
    public void putStudent(final String token, final Student student) throws IOException, StatusCodeException {
        inner.putStudentsById(token, student.getId(), new Entity("student", student.toMap()));
    }
    
    @Override
    public void deleteStudent(final String token, final Student student) throws IOException, StatusCodeException {
        inner.deleteStudentsById(token, student.getId());
    }
    
    @Override
    public Map<String, Object> getCustomForStudentsById(String token, List<String> studentIds,
            Map<String, Object> queryArgs) throws IOException, StatusCodeException {
        return inner.getCustomForStudentsById(token, studentIds, queryArgs);
    }
    
    @Override
    public void postCustomForStudentsById(String token, String id, Entity entity) throws IOException,
            StatusCodeException {
        inner.postCustomForStudentsById(token, id, entity);
    }
}
