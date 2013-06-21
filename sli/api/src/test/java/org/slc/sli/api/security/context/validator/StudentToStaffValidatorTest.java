/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;

/**
 *
 *
 * @author nbrown
 *
 */
public class StudentToStaffValidatorTest {

    private TransitiveStudentToStaffValidator underTest = new TransitiveStudentToStaffValidator();

    private Entity e = mock(Entity.class);

    @SuppressWarnings("unchecked")
    private PagingRepositoryDelegate<Entity> repo = mock(PagingRepositoryDelegate.class);

    @Before
    public void setup() {
        Map<String, List<Entity>> subDocs = new HashMap<String, List<Entity>>();
        subDocs.put("studentProgramAssociation",
                Arrays.asList(makeStudentProgram("program1"), makeStudentProgram("program2")));
        when(e.getEmbeddedData()).thenReturn(subDocs);
        underTest.setRepo(repo);
    }

    @Test
    public void testFilterConnectedViaProgram() {
        List<String> staffIds = Arrays.asList("staff1", "staff2", "staff3", "staff4");
        Iterator<Entity> staffProgramList = Arrays.asList(makeStaffProgram("staff1", "program1"),
                makeStaffProgram("staff1", "program2"), makeStaffProgram("staff2", "program1"),
                makeStaffProgram("staff3", "program3")).iterator();
        when(repo.findEach(eq("staffProgramAssociation"), any(Query.class))).thenReturn(staffProgramList);
        assertEquals(new HashSet<String>(Arrays.asList("staff1", "staff2", "staff3")),
                underTest.filterConnectedViaProgram(new HashSet<String>(staffIds), e));
    }

    private Entity makeStaffProgram(String staffId, String programId) {
        Entity spa = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("programId", programId);
        body.put("staffId", staffId);
        when(spa.getBody()).thenReturn(body);
        return spa;

    }

    private Entity makeStudentProgram(String id) {
        Entity spa = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("programId", id);
        when(spa.getBody()).thenReturn(body);
        return spa;
    }

}
