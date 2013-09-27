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
package org.slc.sli.bulk.extract.date;

import junit.framework.Assert;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.argThat;

/**
 * @author: tke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class PathDateRetriverTest {

    PathDateRetriever pathDateRetriver  = Mockito.mock(PathDateRetriever.class);

    Repository  repo = Mockito.mock(Repository.class);

    @Before
    public void setup() {
        Mockito.doCallRealMethod().when(pathDateRetriver).setRepo(Mockito.any(Repository.class));
        pathDateRetriver.setRepo(repo);

    }

    @Test
    public void testMakeQuery() {

        String diId = "disciplineIncidentId1234";
        Mockito.doCallRealMethod().when(pathDateRetriver).getPathEntity(Mockito.any(Entity.class));

        final Entity di = Mockito.mock(Entity.class);
        Mockito.when(di.getEntityId()).thenReturn(diId);

        Mockito.when(repo.findOne(Matchers.eq(EntityNames.DISCIPLINE_INCIDENT), argThat(new BaseMatcher<NeutralQuery>() {

            @Override
            public boolean matches(Object arg0) {
                NeutralQuery query = (NeutralQuery) arg0;
                return query.getCriteria().contains(
                        new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.OPERATOR_EQUAL, di.getEntityId()));

            }

            @Override
            public void describeTo(Description arg0) {
            }
        }))).thenReturn(di);

        Map <String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.DISCIPLINE_INCIDENT_ID, diId);
        Entity studentDisciplineIncidentAssociation = Mockito.mock(Entity.class);
        Mockito.when(studentDisciplineIncidentAssociation.getBody()).thenReturn(body);
        Mockito.when(studentDisciplineIncidentAssociation.getType()).thenReturn(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION);

        Entity entity = pathDateRetriver.getPathEntity(studentDisciplineIncidentAssociation);
        Assert.assertEquals(di, entity);
    }
}
