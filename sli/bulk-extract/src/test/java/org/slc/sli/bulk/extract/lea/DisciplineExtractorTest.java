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

package org.slc.sli.bulk.extract.lea;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;


/**
 * User: dkornishev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class DisciplineExtractorTest {

    private static final List<String> EDORGS = Arrays.asList("nature", "chaos", "sorcery");
    private static final List<String> STUDENTS = Arrays.asList("Mitsubishi", "Kawasaki");
    private static final List<String> SSA = Arrays.asList("phenomenology");
    private static final String LEA = "HUZZAH";
    private static final String LEA2 = "PIPEC";
    private static final String DI_ID = "ALLE";
    private EntityExtractor ex;
    private DisciplineExtractor disc;
    private Repository<Entity> repo;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {

        ex = Mockito.mock(EntityExtractor.class);

        Map<String, ExtractFile> map = new HashMap<String, ExtractFile>();
        ExtractFileMap leaMap = new ExtractFileMap(map);

        //  Mock student2LEA cache.
        EntityToEdOrgDateCache studentCache = new EntityToEdOrgDateCache();
        studentCache.addEntry(STUDENTS.get(1), LEA, DateTime.parse("2001-01-01", DateHelper.getDateTimeFormat()));
        studentCache.addEntry(STUDENTS.get(1), LEA2, DateTime.parse("2010-02-12", DateHelper.getDateTimeFormat()));

        //Mock Repository
        repo = Mockito.mock(Repository.class);

        disc = new DisciplineExtractor(ex, leaMap, repo, studentCache);
    }

    @Test
    public void testExtractDisciplineIncidentAndAction() {

        // Return 2 disciplineActions (one good, one bad).
        Entity da1 = createDisciplineAction("2009-01-01");
        Entity da2 = createDisciplineAction("2010-02-13");
        Mockito.when(repo.findEach(Mockito.eq("disciplineAction"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(da1, da2).listIterator(0));

        // Return 2 disciplineIncidents (one good, one bad).
        Entity di1 = createDisciplineIncident("2000-02-01");
        Entity di2 = createDisciplineIncident("2011-02-01");
        Mockito.when(repo.findEach(Mockito.eq("disciplineIncident"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(di1, di2).listIterator(0));

        EntityToEdOrgDateCache diCache = new EntityToEdOrgDateCache();
        diCache.addEntry("marker", LEA2, DateTime.parse("2010-02-12", DateHelper.getDateTimeFormat()));
        diCache.addEntry(DI_ID, LEA2, DateTime.parse("2010-02-12", DateHelper.getDateTimeFormat()));

        disc.extractEntities(diCache);

        Mockito.verify(ex, Mockito.times(1)).extractEntity(Mockito.eq(da1), Mockito.any(ExtractFile.class), Mockito.eq("disciplineAction"));
        Mockito.verify(ex, Mockito.never()).extractEntity(Mockito.eq(da2), Mockito.any(ExtractFile.class), Mockito.eq("disciplineAction"));
        Mockito.verify(ex, Mockito.times(1)).extractEntity(Mockito.eq(di1), Mockito.any(ExtractFile.class), Mockito.eq("disciplineIncident"));
        Mockito.verify(ex, Mockito.never()).extractEntity(Mockito.eq(di2), Mockito.any(ExtractFile.class), Mockito.eq("disciplineIncident"));
    }


    private Entity createDisciplineIncident(String date) {
        Entity e = Mockito.mock(Entity.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("schoolId", LEA);
        body.put(ParameterConstants.INCIDENT_DATE, date);
        Mockito.when(e.getBody()).thenReturn(body);
        Mockito.when(e.getEntityId()).thenReturn(DI_ID);
        Mockito.when(e.getType()).thenReturn(EntityNames.DISCIPLINE_INCIDENT);
        return e;
    }

    private Entity createDisciplineAction(String date) {
        Entity e = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", STUDENTS);
        body.put(ParameterConstants.DISCIPLINE_DATE, date);
        Mockito.when(e.getBody()).thenReturn(body);
        Mockito.when(e.getEntityId()).thenReturn(DI_ID);
        Mockito.when(e.getType()).thenReturn(EntityNames.DISCIPLINE_ACTION);

        return e;
    }
}
