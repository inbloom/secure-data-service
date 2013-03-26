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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.bulk.extract.TestUtils;
import org.slc.sli.bulk.extract.files.ArchivedExtractFile;
import org.slc.sli.bulk.extract.files.DataExtractFile;
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

    @Autowired
    private  EntityExtractor extractor;

    private MongoEntityRepository mongoEntityRepository;

    private ArchivedExtractFile archiveFile;

    /**
     * Runs before JUnit test and does the initiation work.
     * @throws IOException
     *          if an I/O error occurred
     */
    @Before
    public void init() throws IOException {
        mongoEntityRepository = Mockito.mock(MongoEntityRepository.class);
        archiveFile = Mockito.mock(ArchivedExtractFile.class);
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

        File studentExtractFile = TestUtils.createTempFile("student", ".json");
        OutputStream outputStream = new FileOutputStream(studentExtractFile);

        DataExtractFile student = Mockito.mock(DataExtractFile.class);
        Mockito.when(student.getOutputStream()).thenReturn(outputStream);

        Mockito.when(archiveFile.getDataFileEntry(Matchers.anyString())).thenReturn(student);

        Iterator<Entity> cursor = Mockito.mock(Iterator.class);
        List<Entity> students = TestUtils.createStudents();
        Mockito.when(cursor.hasNext()).thenReturn(true, true, true, true, false, false);
        Mockito.when(cursor.next()).thenReturn(students.get(0), students.get(1));
        Mockito.when(mongoEntityRepository.findEach(Matchers.eq(testEntity), Matchers.any(Query.class))).thenReturn(cursor);

        extractor.extractEntity(testTenant, archiveFile, testEntity);

        IOUtils.closeQuietly(outputStream);

        String fileContent = FileUtils.readFileToString(studentExtractFile);

        ObjectMapper mapper = new ObjectMapper();

        Entity studentEntity1 = students.get(0);
        studentEntity1.getBody().put("entityType", "student");
        String student1 = mapper.writeValueAsString(studentEntity1.getBody());

        Entity studentEntity2 = students.get(0);
        studentEntity2.getBody().put("entityType", "student");
        String student2 = mapper.writeValueAsString(studentEntity2.getBody());

        Assert.assertTrue("Failed to find contents of first student", fileContent.contains(student1));
        Assert.assertTrue("Failed to find contents of second student", fileContent.contains(student2));

    }

}
