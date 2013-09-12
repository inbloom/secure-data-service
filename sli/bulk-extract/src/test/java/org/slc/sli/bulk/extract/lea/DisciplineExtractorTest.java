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

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * User: dkornishev
 */
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
        LEAExtractFileMap leaMap = new LEAExtractFileMap(map);

        //Mock student2LEA cache
        EntityToLeaCache studentCache = new EntityToLeaCache();
        studentCache.addEntry(STUDENTS.get(1), LEA);
        studentCache.addEntry(STUDENTS.get(1), LEA2);

        //Mock edOrgCache
        EntityToLeaCache edorgCache = new EntityToLeaCache();
        //edorgCache.addEntry(EDORGS.get(0), LEA);
        edorgCache.addEntry(EDORGS.get(0), LEA);

        //Mock Repository
        Repository<Entity> repo = Mockito.mock(Repository.class);
        //return 1 disciplineAction
        List<Entity> list = Arrays.asList(createDisciplineAction());
        Mockito.when(repo.findEach(Mockito.eq("disciplineAction"), Mockito.any(NeutralQuery.class))).thenReturn(list.listIterator(0));

        //return 2 disciplineIncidents
        Entity e = createDisciplineIncident();
        Entity e2 = createDisciplineIncident();
        Mockito.when(repo.findEach(Mockito.eq("disciplineIncident"), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(e, e2).listIterator(0));

        disc = new DisciplineExtractor(ex, leaMap, repo, studentCache, edorgCache);
    }

    @Test
    public void testExtractDisciplineIncident() {

        EntityToLeaCache diCache = new EntityToLeaCache();
        diCache.addEntry("marker", LEA2);
        diCache.addEntry(DI_ID, LEA2);
        disc.extractEntities(diCache);
        Mockito.verify(ex, Mockito.times(2)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.eq("disciplineIncident"));
        Mockito.verify(ex, Mockito.times(2)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.eq("disciplineAction"));
    }


    private Entity createDisciplineIncident() {
        Entity e = Mockito.mock(Entity.class);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("schoolId", LEA);
        Mockito.when(e.getBody()).thenReturn(body);
        return e;
    }

    private Entity createDisciplineAction() {
        Entity e = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", STUDENTS);
        Mockito.when(e.getBody()).thenReturn(body);
        Mockito.when(e.getEntityId()).thenReturn(DI_ID);

        return e;
    }
}
