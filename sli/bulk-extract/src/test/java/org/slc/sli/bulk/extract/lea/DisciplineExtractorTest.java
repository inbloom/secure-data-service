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

import java.util.*;


/**
 * User: dkornishev
 */
public class DisciplineExtractorTest {
    private static final List<String> EDORGS = Arrays.asList("nature", "chaos", "sorcery");
    private static final List<String> STUDENTS = Arrays.asList("Mitsubishi", "Kawasaki");
    private static final List<String> SSA = Arrays.asList("phenomenology");
    private static final String LEA = "HUZZAH";
    private static final String LEA2 = "PIPEC";
    private EntityExtractor ex;
    private Repository<Entity> repo;
    private DisciplineExtractor disc;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {

        repo = Mockito.mock(Repository.class);

        ex = Mockito.mock(EntityExtractor.class);

        Map<String, ExtractFile> map = new HashMap<String, ExtractFile>();

        LEAExtractFileMap leaMap = new LEAExtractFileMap(map);

        EntityToLeaCache studentCache = new EntityToLeaCache();
        studentCache.addEntry(STUDENTS.get(1), LEA);
        studentCache.addEntry(STUDENTS.get(1), LEA2);

        EntityToLeaCache edorgCache = new EntityToLeaCache();
        edorgCache.addEntry(LEA, EDORGS.get(0));

        disc = new DisciplineExtractor(ex, leaMap, repo, studentCache, edorgCache);

        Mockito.when(repo.findEach(Mockito.eq("disciplineIncident"), Mockito.any(NeutralQuery.class))).thenReturn(Collections.<Entity>emptyListIterator());
    }

    @Test
    public void testExtractEntities() throws Exception {
        List<Entity> list = Arrays.asList(createDisciplineAction());
        Mockito.when(repo.findEach(Mockito.eq("disciplineAction"), Mockito.any(NeutralQuery.class))).thenReturn(list.iterator());

        disc.extractEntities(null);
        Mockito.verify(ex,Mockito.times(3)).extractEntity(Mockito.any(Entity.class),Mockito.any(ExtractFile.class),Mockito.eq("disciplineAction"));
    }

    private Entity createDisciplineAction() {
        Entity e = Mockito.mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", STUDENTS);
        Mockito.when(e.getBody()).thenReturn(body);

        return e;
    }
}
