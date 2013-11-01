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

import static org.mockito.Matchers.argThat;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import org.slc.sli.bulk.extract.SecondaryReadRepository;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * @author: tke
 */
public class PathExtractVerifierTest {

    private Repository<Entity> repo;

    private PathExtractVerifier pathExtractVerifier = new PathExtractVerifier();

    @Before
    public void setup() {
        PathExtractVerifier realPathExtractVerifier = new PathExtractVerifier();
        repo = Mockito.mock(SecondaryReadRepository.class);
        realPathExtractVerifier.setRepo(repo);

        pathExtractVerifier = Mockito.spy(realPathExtractVerifier);
    }

    @Test
    public void testMakeQuery() {

        String diId = "disciplineIncidentId1234";

        Map <String, Object> diBody = new HashMap<String, Object>();
        diBody.put(ParameterConstants.INCIDENT_DATE, "2010-05-23");
        final Entity di = Mockito.mock(Entity.class);
        Mockito.when(di.getEntityId()).thenReturn(diId);
        Mockito.when(di.getType()).thenReturn(EntityNames.DISCIPLINE_INCIDENT);
        Mockito.when(di.getBody()).thenReturn(diBody);

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

        Mockito.doReturn(new SimpleExtractVerifier()).when(pathExtractVerifier).getExtractVerifer(Mockito.eq(EntityNames.DISCIPLINE_INCIDENT));

        Map <String, Object> sdiaBody = new HashMap<String, Object>();
        sdiaBody.put(ParameterConstants.DISCIPLINE_INCIDENT_ID, diId);
        Entity studentDisciplineIncidentAssociation = Mockito.mock(Entity.class);
        Mockito.when(studentDisciplineIncidentAssociation.getBody()).thenReturn(sdiaBody);
        Mockito.when(studentDisciplineIncidentAssociation.getType()).thenReturn(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION);

        Assert.assertTrue(pathExtractVerifier.shouldExtract(studentDisciplineIncidentAssociation, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat())));
        Assert.assertFalse(pathExtractVerifier.shouldExtract(studentDisciplineIncidentAssociation, DateTime.parse("2009-05-23", DateHelper.getDateTimeFormat())));

        Mockito.doReturn(null).when(pathExtractVerifier).getPathEntity(studentDisciplineIncidentAssociation);

        Assert.assertFalse(pathExtractVerifier.shouldExtract(studentDisciplineIncidentAssociation, DateTime.parse("2011-05-23", DateHelper.getDateTimeFormat())));
    }

}
