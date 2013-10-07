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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.date.SimpleDateRetriever;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {

        ex = Mockito.mock(EntityExtractor.class);

        Map<String, ExtractFile> map = new HashMap<String, ExtractFile>();
        ExtractFileMap leaMap = new ExtractFileMap(map);

        //Mock student2LEA cache
        EntityToEdOrgDateCache studentCache = new EntityToEdOrgDateCache();
        studentCache.addEntry(STUDENTS.get(1), LEA, DateTime.parse("2001-01-01", DateHelper.getDateTimeFormat()));
        studentCache.addEntry(STUDENTS.get(1), LEA2, DateTime.parse("2010-02-12", DateHelper.getDateTimeFormat()));

        //Mock edOrgCache
        EntityToEdOrgCache edorgCache = new EntityToEdOrgCache();
        edorgCache.addEntry(EDORGS.get(0), LEA);
        edorgCache.addEntry(EDORGS.get(0), LEA);

        //Mock Repository
        Repository<Entity> repo = Mockito.mock(Repository.class);
        //return 1 disciplineAction
        List<Entity> list = Arrays.asList(createDisciplineAction("2009-01-01"));
        Mockito.when(repo.findEach(Mockito.eq("disciplineAction"), Mockito.any(NeutralQuery.class))).thenReturn(list.listIterator(0));

        //return 2 disciplineIncidents
        Entity e = createDisciplineIncident("2000-02-01");
        Entity e2 = createDisciplineIncident("2011-02-01");
        Mockito.when(repo.findEach(Mockito.eq("disciplineIncident"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(e, e2).listIterator(0));

        disc = new DisciplineExtractor(ex, leaMap, repo, studentCache, edorgCache);
    }

    @Test
    public void testExtractDisciplineIncident() {

        EntityToEdOrgDateCache diCache = new EntityToEdOrgDateCache();
        diCache.addEntry("marker", LEA2, DateTime.parse("2010-02-12", DateHelper.getDateTimeFormat()));
        diCache.addEntry(DI_ID, LEA2, DateTime.parse("2010-02-12", DateHelper.getDateTimeFormat()));
        disc.extractEntities(diCache);
        Mockito.verify(ex, Mockito.times(2)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.eq("disciplineIncident"));
        Mockito.verify(ex, Mockito.times(1)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.eq("disciplineAction"));
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
