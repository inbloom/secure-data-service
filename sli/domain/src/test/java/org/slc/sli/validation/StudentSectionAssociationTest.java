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


package org.slc.sli.validation;

import static org.junit.Assert.assertTrue;
import static org.slc.sli.validation.ValidationTestUtils.makeDummyEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;

/**
 * Test for validation of Student Section associations
 *
 * @author nbrown
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StudentSectionAssociationTest {

    @Autowired
    private EntityValidator validator;

    @Autowired
    @Qualifier("validationRepo")
    private DummyEntityRepository repo;

    @Before
    public void init() {
        repo.clean();
        repo.addEntity("student", "Calvin", makeDummyEntity("student", "Calvin"));
        repo.addEntity("section", "Math Class", makeDummyEntity("section", "Math Class"));
    }

    private Entity goodAssociation() {
        final Map<String, Object> goodSection = new HashMap<String, Object>();
        goodSection.put("studentId", "Calvin");
        goodSection.put("sectionId", "Math Class");
        goodSection.put("beginDate", "1985-11-18");
        goodSection.put("endDate", "1995-12-31");
        goodSection.put("homeroomIndicator", true);
        goodSection.put("repeatIdentifier", "Repeated, not counted in grade point average");

        return new Entity() {

            @Override
            public String getType() {
                return "studentSectionAssociation";
            }

            @Override
            public String getEntityId() {
                return "id";
            }

            @Override
            public Map<String, Object> getBody() {
                return goodSection;
            }

            @Override
            public Map<String, Object> getMetaData() {
                return new HashMap<String, Object>();
            }

            @Override
            public CalculatedData<String> getCalculatedValues() {
                return null;
            }

            @Override
            public CalculatedData<Map<String, Integer>> getAggregates() {
                return null;
            }

            @Override
            public Map<String, List<Entity>> getEmbeddedData() {
                return null;
            }

            @Override
            public Map<String, List<Map<String, Object>>> getDenormalizedData() {
                return null;
            }

            @Override
            public String getStagedEntityId() {
                return null;
            }
       };
    }

    @Test
    public void testGoodAssociation() {
        Entity goodAssociation = goodAssociation();
        assertTrue(validator.validate(goodAssociation));
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testInvalidStudentSectionAssociation() {
        Entity goodAssociation = goodAssociation();
        goodAssociation.getBody().put("studentId", "INVALID");
        goodAssociation.getBody().put("sectionId", "INVALID");
        validator.validate(goodAssociation);
    }

}
