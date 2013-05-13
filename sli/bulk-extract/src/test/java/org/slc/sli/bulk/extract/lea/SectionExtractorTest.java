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

import com.google.common.base.Function;
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
public class SectionExtractorTest {

    private static final List<String> accessible = Arrays.asList("nature", "chaos", "sorcery");
    private static final List<String> students = Arrays.asList("Mitsubishi", "Kawasaki");
    private static final String LEA = "HUZZAH";
    private SectionExtractor se;
    private EntityExtractor ex;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {

        Repository<Entity> repo = Mockito.mock(Repository.class);

        ex = Mockito.mock(EntityExtractor.class);
        List<Entity> list = Arrays.asList(AccessibleVia.EDORG.generate(), AccessibleVia.EDORG.generate(), AccessibleVia.EDORG.generate());
        Mockito.when(repo.findEach(Mockito.eq("section"), Mockito.any(NeutralQuery.class))).thenReturn(list.iterator());

        Map<String, ExtractFile> map = new HashMap<String, ExtractFile>();
        // init map
        LEAExtractFileMap leaMap = new LEAExtractFileMap(map);

        EntityToLeaCache studentCache = new EntityToLeaCache();
        studentCache.addEntry(students.get(1), LEA);

        EntityToLeaCache edorgCache = new EntityToLeaCache();
        edorgCache.addEntry(LEA, accessible.get(0));

        se = new SectionExtractor(ex, leaMap, repo, studentCache, edorgCache);
    }

    @Test
    public void testExtractEntities() throws Exception {
        se.extractEntities(null);
        Mockito.verify(ex, Mockito.times(3)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.any(String.class));
    }

    private static enum AccessibleVia {
        NONE(new Function<Entity, Entity>() {

            @Override
            public Entity apply(Entity input) {
                return input;
            }
        }),
        EDORG(new Function<Entity, Entity>() {

            @Override
            public Entity apply(Entity input) {
                input.getBody().put("schoolId", accessible.get(0));
                return input;
            }
        }),
        STUDENT(new Function<Entity, Entity>() {

            @Override
            public Entity apply(Entity input) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Map<String, Map<String, Object>> entity = new HashMap<String, Map<String, Object>>();
                entity.put("body", new HashMap<String, Object>());
                entity.get("body").put("studentId", students.get(1));
                Mockito.when(input.getDenormalizedData().get("studentSectionAssociation")).thenReturn(list);
                return input;
            }
        }),
        BOTH(new Function<Entity, Entity>() {

            @Override
            public Entity apply(Entity input) {
                throw new UnsupportedOperationException("I give up");
            }
        });
        private final Function<Entity, Entity> closure;

        private AccessibleVia(Function<Entity, Entity> cl) {
            this.closure = cl;

        }

        public Entity generate() {
            Entity e = Mockito.mock(Entity.class);
            Map<String, Object> map = new HashMap<String, Object>();
            Mockito.when(e.getBody()).thenReturn(map);
            return this.closure.apply(e);
        }
    }
}
