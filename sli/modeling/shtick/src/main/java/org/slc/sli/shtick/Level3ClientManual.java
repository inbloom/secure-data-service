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
import java.util.List;
import java.util.Map;

import org.slc.sli.shtick.pojo.Student;

public interface Level3ClientManual {
    
    List<Student> getStudents(String token, Map<String, Object> queryArgs) throws IOException, StatusCodeException;
    
    List<Student> getStudentsById(String token, List<String> studentIds, Map<String, Object> queryArgs)
            throws IOException, StatusCodeException;
    
    String postStudent(String token, Student student) throws IOException, StatusCodeException;
    
    void putStudent(String token, Student student) throws IOException, StatusCodeException;
    
    void deleteStudent(String token, Student student) throws IOException, StatusCodeException;
    
    Map<String, Object> getCustomForStudentsById(String token, List<String> studentIds, Map<String, Object> queryArgs)
            throws IOException, StatusCodeException;
    
    void postCustomForStudentsById(String token, String id, Entity entity) throws IOException, StatusCodeException;
}
