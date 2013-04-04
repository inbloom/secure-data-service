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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.bulk.extract.TestUtils;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.files.JsonExtractFile;
import org.slc.sli.bulk.extract.files.writer.EntityWriter;
import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;

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
    private  EntityExtractor extractor;

    @Mock
    private MongoEntityRepository mongoEntityRepository;

    @Mock
    private EntityWriter writer;

    private ExtractFile archiveFile;

    /**
     * Runs before JUnit test and does the initiation work.
     * @throws IOException
     *          if an I/O error occurred
     */
    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
        mongoEntityRepository = Mockito.mock(MongoEntityRepository.class);
        archiveFile = Mockito.mock(ExtractFile.class);
        JsonExtractFile json = Mockito.mock(JsonExtractFile.class);
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
    public void testExtractEntity() throws IOException{
        String testTenant = "Midgar";
        String testEntity = "student";

        Iterator<Entity> cursor = Mockito.mock(Iterator.class);
        List<Entity> students = TestUtils.createStudents();
        Mockito.when(cursor.hasNext()).thenReturn(true, true, true, false);
        Mockito.when(cursor.next()).thenReturn(students.get(0), students.get(1));
        Mockito.when(mongoEntityRepository.findEach(Matchers.eq(testEntity), Matchers.any(Query.class))).thenReturn(cursor);

        extractor.extractEntities(testTenant, archiveFile, testEntity);

        Mockito.verify(mongoEntityRepository, Mockito.times(1)).findEach("student", new Query());
        Mockito.verify(writer, Mockito.times(2)).write(Mockito.any(Entity.class), Mockito.any(ExtractFile.class));

    }

}
