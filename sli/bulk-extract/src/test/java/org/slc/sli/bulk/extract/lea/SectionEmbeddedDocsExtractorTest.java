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
import com.google.common.base.Predicate;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * User: dkornishev
 */
@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SectionEmbeddedDocsExtractorTest {

    private static final List<String> EDORGS = Arrays.asList("nature", "chaos", "sorcery");
    private static final List<String> STUDENTS = Arrays.asList("Mitsubishi", "Kawasaki");
    private static final List<String> TEACHERS = Arrays.asList("LaLaurie", "Laveau");
    private static final List<String> COURSE_OFFERINGS = Arrays.asList("witchcraft", "demonology", "escatronics");
    private static final List<String> SSA = Arrays.asList("phenomenology");
    private static final String LEA = "HUZZAH";
    private SectionEmbeddedDocsExtractor se;
    private EntityExtractor ex;
    private Repository<Entity> repo;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {

        repo = Mockito.mock(Repository.class);

        ex = Mockito.mock(EntityExtractor.class);

        Map<String, ExtractFile> map = new HashMap<String, ExtractFile>();
        // init map
        ExtractFileMap leaMap = new ExtractFileMap(map);

        EntityToEdOrgDateCache studentCache = new EntityToEdOrgDateCache();
        studentCache.addEntry(STUDENTS.get(1), LEA,  DateTime.parse("2011-05-01", DateHelper.getDateTimeFormat()));
        EntityToEdOrgCache edorgCache = new EntityToEdOrgCache();
        edorgCache.addEntry(LEA, EDORGS.get(0));

        EntityToEdOrgDateCache staffCache = new EntityToEdOrgDateCache();
        staffCache.addEntry(TEACHERS.get(0), LEA, DateTime.parse("2011-05-01", DateHelper.getDateTimeFormat()));

        se = new SectionEmbeddedDocsExtractor(ex, leaMap, repo, studentCache, staffCache);
    }

    @Test
    public void testExtractValidTSA() throws Exception {
        List<Entity> list = Arrays.asList(AccessibleVia.VALIDTSA.generate(), AccessibleVia.VALIDTSA.generate(), AccessibleVia.VALIDTSA.generate(), AccessibleVia.NONE.generate());
        Mockito.when(repo.findEach(Mockito.eq(EntityNames.SECTION), Mockito.any(NeutralQuery.class))).thenReturn(list.iterator());

        se.extractEntities(null);
        Mockito.verify(ex, Mockito.times(3)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION));
    }

    @Test
    public void testExtractInvalidTSAs() throws Exception {
        List<Entity> list = Arrays.asList(AccessibleVia.INVALIDTSA.generate(), AccessibleVia.INVALIDTSA.generate(), AccessibleVia.INVALIDTSA.generate());
        Mockito.when(repo.findEach(Mockito.eq(EntityNames.SECTION), Mockito.any(NeutralQuery.class))).thenReturn(list.iterator());

        se.extractEntities(null);
        Mockito.verify(ex, Mockito.never()).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION));
    }

    @Test
    public void testExtractValidSSAs() throws Exception {
        List<Entity> list = Arrays.asList(AccessibleVia.VALIDSSA.generate(), AccessibleVia.VALIDSSA.generate(), AccessibleVia.VALIDSSA.generate(), AccessibleVia.NONE.generate());
        Mockito.when(repo.findEach(Mockito.eq(EntityNames.SECTION), Mockito.any(NeutralQuery.class))).thenReturn(list.iterator());

        se.extractEntities(null);
        Mockito.verify(ex, Mockito.times(3)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION));
    }

    @Test
    public void testExtractInvalidSSAs() throws Exception {
        List<Entity> list = Arrays.asList(AccessibleVia.INVALIDSSA.generate(), AccessibleVia.INVALIDSSA.generate(), AccessibleVia.INVALIDSSA.generate());
        Mockito.when(repo.findEach(Mockito.eq(EntityNames.SECTION), Mockito.any(NeutralQuery.class))).thenReturn(list.iterator());

        se.extractEntities(null);
        Mockito.verify(ex, Mockito.never()).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.any(String.class));
    }

    @Test
    public void testMixedExtract() throws Exception {
        List<Entity> list = Arrays.asList(AccessibleVia.VALIDSSA.generate(), AccessibleVia.VALIDSSA.generate(), AccessibleVia.VALIDSSA.generate(), AccessibleVia.NONE.generate(),
                AccessibleVia.NONE.generate(), AccessibleVia.VALIDTSA.generate(), AccessibleVia.VALIDTSA.generate());
        Mockito.when(repo.findEach(Mockito.eq(EntityNames.SECTION), Mockito.any(NeutralQuery.class))).thenReturn(list.iterator());

        se.extractEntities(null);
        Mockito.verify(ex, Mockito.times(2)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.eq(EntityNames.TEACHER_SECTION_ASSOCIATION));
        Mockito.verify(ex, Mockito.times(3)).extractEntity(Mockito.any(Entity.class), Mockito.any(ExtractFile.class), Mockito.eq(EntityNames.STUDENT_SECTION_ASSOCIATION));

        Assert.assertTrue("StudentSectionAssociations must contain expected id", se.getStudentSectionAssociationDateCache().getEntityIds().contains(SSA.get(0)));
    }


    private static enum AccessibleVia {
        NONE(new Function<Entity, Entity>() {

            @Override
            public Entity apply(Entity input) {
                return input;
            }
        }),
        VALIDSSA(new Function<Entity, Entity>() {

            @Override
            @SuppressWarnings("unchecked")
            public Entity apply(Entity input) {
                List<Entity> list = new ArrayList<Entity>();
                Entity ssaEntity = Mockito.mock(Entity.class);
                Mockito.when(ssaEntity.getEntityId()).thenReturn(SSA.get(0));
                Mockito.when(ssaEntity.getType()).thenReturn(EntityNames.STUDENT_SECTION_ASSOCIATION);
                Map<String, Object> body = new HashMap<String, Object>();
                body.put("studentId", STUDENTS.get(1));
                body.put(ParameterConstants.BEGIN_DATE, "2010-10-10");
                Mockito.when(ssaEntity.getBody()).thenReturn(body);
                list.add(ssaEntity);

                Map<String, List<Entity>> map = new HashMap<String, List<Entity>>();
                map.put(EntityNames.STUDENT_SECTION_ASSOCIATION, list);
                Mockito.when(input.getEmbeddedData()).thenReturn(map);

                return input;
            }
        }),
        INVALIDSSA(new Function<Entity, Entity>() {

            @Override
            @SuppressWarnings("unchecked")
            public Entity apply(Entity input) {
                List<Entity> list = new ArrayList<Entity>();
                Entity ssaEntity = Mockito.mock(Entity.class);
                Mockito.when(ssaEntity.getEntityId()).thenReturn(SSA.get(0));
                Mockito.when(ssaEntity.getType()).thenReturn(EntityNames.STUDENT_SECTION_ASSOCIATION);
                Map<String, Object> body = new HashMap<String, Object>();
                body.put("studentId", STUDENTS.get(1));
                body.put(ParameterConstants.BEGIN_DATE, "2012-10-10");
                Mockito.when(ssaEntity.getBody()).thenReturn(body);
                list.add(ssaEntity);

                Map<String, List<Entity>> map = new HashMap<String, List<Entity>>();
                map.put(EntityNames.STUDENT_SECTION_ASSOCIATION, list);
                Mockito.when(input.getEmbeddedData()).thenReturn(map);

                return input;
            }
        }),
        VALIDTSA(new Function<Entity, Entity>() {

            @Override
            @SuppressWarnings("unchecked")
            public Entity apply(Entity input) {
                List<Entity> list = new ArrayList<Entity>();
                Entity tsaEntity = Mockito.mock(Entity.class);
                //Mockito.when(ssaEntity.getEntityId()).thenReturn(SSA.get(0));
                Mockito.when(tsaEntity.getType()).thenReturn(EntityNames.TEACHER_SECTION_ASSOCIATION);
                Map<String, Object> body = new HashMap<String, Object>();
                body.put(ParameterConstants.TEACHER_ID, TEACHERS.get(0));
                body.put(ParameterConstants.BEGIN_DATE, "2010-10-10");
                Mockito.when(tsaEntity.getBody()).thenReturn(body);
                list.add(tsaEntity);

                Map<String, List<Entity>> map = new HashMap<String, List<Entity>>();
                map.put(EntityNames.TEACHER_SECTION_ASSOCIATION, list);
                Mockito.when(input.getEmbeddedData()).thenReturn(map);

                return input;
            }
        }),
        INVALIDTSA(new Function<Entity, Entity>() {

            @Override
            @SuppressWarnings("unchecked")
            public Entity apply(Entity input) {
                List<Entity> list = new ArrayList<Entity>();
                Entity tsaEntity = Mockito.mock(Entity.class);
                //Mockito.when(ssaEntity.getEntityId()).thenReturn(SSA.get(0));
                Mockito.when(tsaEntity.getType()).thenReturn(EntityNames.TEACHER_SECTION_ASSOCIATION);
                Map<String, Object> body = new HashMap<String, Object>();
                body.put(ParameterConstants.TEACHER_ID, TEACHERS.get(0));
                body.put(ParameterConstants.BEGIN_DATE, "2012-10-10");
                Mockito.when(tsaEntity.getBody()).thenReturn(body);
                list.add(tsaEntity);

                Map<String, List<Entity>> map = new HashMap<String, List<Entity>>();
                map.put(EntityNames.TEACHER_SECTION_ASSOCIATION, list);
                Mockito.when(input.getEmbeddedData()).thenReturn(map);

                return input;
            }
        });
        private final Function<Entity, Entity> closure;

        private AccessibleVia(Function<Entity, Entity> cl) {
            this.closure = cl;

        }

        public Entity generate() {
            Entity e = Mockito.mock(Entity.class);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("courseOfferingId",COURSE_OFFERINGS.get(2));
            Mockito.when(e.getBody()).thenReturn(map);
            return this.closure.apply(e);
        }
    }
}
