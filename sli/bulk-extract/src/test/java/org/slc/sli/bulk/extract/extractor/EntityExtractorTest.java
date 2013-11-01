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
package org.slc.sli.bulk.extract.extractor;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.bulk.extract.SecondaryReadRepository;
import org.slc.sli.bulk.extract.TestUtils;
import org.slc.sli.bulk.extract.files.EntityWriterManager;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.files.writer.JsonFileWriter;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test bulk extraction into zip files.
 *
 * @author tshewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class EntityExtractorTest {

    @InjectMocks
    @Autowired
    private EntityExtractor extractor;

    @Mock
    private SecondaryReadRepository mongoEntityRepository;

    @Mock
    private EntityWriterManager writer;

    private ExtractFile archiveFile;

    /**
     * Runs before JUnit test and does the initiation work.
     * @throws IOException
     *          if an I/O error occurred
     */
    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
        mongoEntityRepository = Mockito.mock(SecondaryReadRepository.class);
        archiveFile = Mockito.mock(ExtractFile.class);
        JsonFileWriter json = Mockito.mock(JsonFileWriter.class);
        Mockito.when(archiveFile.getDataFileEntry(Mockito.anyString())).thenReturn(json);
        extractor.setEntityRepository(mongoEntityRepository);
    }

    /**
     *JUnit test to test the extract entity method.
     * @throws IOException
     *          if an I/O error occurred
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExtractEntities() throws IOException {
        String testTenant = "Midgar";
        String testEntity = "student";

        Iterator<Entity> cursor = Mockito.mock(Iterator.class);
        List<Entity> students = TestUtils.createStudents();
        Mockito.when(cursor.hasNext()).thenReturn(true, true, true, false);
        Mockito.when(cursor.next()).thenReturn(students.get(0), students.get(1));
        Mockito.when(mongoEntityRepository.findEach(Matchers.eq(testEntity), Matchers.any(NeutralQuery.class))).thenReturn(cursor);

        extractor.extractEntities(archiveFile, testEntity, null);

        Mockito.verify(mongoEntityRepository, Mockito.times(1)).findEach("student", new NeutralQuery());
        Mockito.verify(writer, Mockito.times(2)).write(Mockito.any(Entity.class), Mockito.any(ExtractFile.class));

    }
    
    @Test
    public void testExtractEntity() throws IOException {
        extractor.extractEntity(Mockito.mock(Entity.class), Mockito.mock(ExtractFile.class), "BLOOP");
        Mockito.verify(writer, Mockito.times(1)).write(Mockito.any(Entity.class), Mockito.any(ExtractFile.class));
    }

}
