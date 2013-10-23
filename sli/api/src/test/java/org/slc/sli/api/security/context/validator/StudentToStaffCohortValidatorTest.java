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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * Junit for student to staff cohort association validation
 *
 * @author nbrown
 *
 */
@SuppressWarnings("unchecked")
public class StudentToStaffCohortValidatorTest {

    private StudentToStaffAssociationAbstractValidator underTest = new StudentToStaffCohortValidator();
    private PagingRepositoryDelegate<Entity> repo = mock(PagingRepositoryDelegate.class);
    private SecurityContextInjector inj = new SecurityContextInjector();

    @Before
    public void setup() {
        underTest.setRepo(repo);
        underTest.setDateHelper(new DateHelper());
        inj.setRepo(repo);
    }

    @Test
    public void testValidate() {
        List<Entity> scas = Arrays.asList(makeEntity("sca1"), makeEntity("sca2"));
        when(repo.findEach(eq("staffCohortAssociation"), argThat(new BaseMatcher<Query>() {

            @Override
            public boolean matches(Object arg0) {
                Query q = (Query) arg0;
                Map<String, Object> cohortIdClause = (Map<String, Object>) q.getQueryObject().get("body.cohortId");
                List<String> inClause = (List<String>) cohortIdClause.get("$in");
                Collections.sort(inClause);
                if(!inClause.equals(Arrays.asList("s1", "s2"))) {
                    //3 has expired, should be ruled out ahead of time
                    return false;
                }
                return true;
            }

            @Override
            public void describeTo(Description arg0) {
                arg0.appendText("Query matching requirements");
            }
        }))).thenReturn(scas.iterator());
        Entity me = mock(Entity.class);
        Map<String, List<Entity>> superDocs = new HashMap<String, List<Entity>>();
        superDocs.put("studentCohortAssociation", Arrays.asList(
                makeStudentCohort("s1", DateTime.now().plusMonths(2).toString(DateHelper.getDateTimeFormat())),
                makeStudentCohort("s2", null),
                makeStudentCohort("s3", DateTime.now().minusMonths(2).toString(DateHelper.getDateTimeFormat()))));
        when(me.getEmbeddedData()).thenReturn(superDocs);
        SLIPrincipal principal = mock(SLIPrincipal.class);
        when(principal.getOwnedStudentEntities()).thenReturn(new HashSet<Entity>(Arrays.asList(me)));
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Set<String> idsToValidate = new HashSet<String>(Arrays.asList("sca1", "sca2"));
        assertTrue(underTest.doValidate(idsToValidate, EntityNames.STAFF_COHORT_ASSOCIATION).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList("sca1", "sca2", "sca3"));
        assertFalse(underTest.doValidate(idsToValidate, EntityNames.STAFF_COHORT_ASSOCIATION).containsAll(idsToValidate));
    }

    private Entity makeEntity(String id) {
        Entity e = mock(Entity.class);
        when(e.getEntityId()).thenReturn(id);
        return e;
    }

    private Entity makeStudentCohort(String cohortId, String endDate) {
        Entity e = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("cohortId", cohortId);
        body.put("endDate", endDate);
        when(e.getBody()).thenReturn(body);
        return e;
    }
}
