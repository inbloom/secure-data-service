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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
 * JUnit for validating section
 *
 * @author nbrown
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SectionValidationTest {

    @Autowired
    private EntityValidator validator;

    @Autowired
    @Qualifier("validationRepo")
    private DummyEntityRepository repo;

    @Before
    public void init() {
        repo.clean();
        repo.addEntity("educationOrganization", "42",
                ValidationTestUtils.makeDummyEntity("educationOrganization", "42"));
        repo.addEntity("session", "MySessionId", ValidationTestUtils.makeDummyEntity("session", "MySessionId"));
        repo.addEntity("program", "program1", ValidationTestUtils.makeDummyEntity("program", "program1"));
        repo.addEntity("program", "program2", ValidationTestUtils.makeDummyEntity("program", "program2"));
        repo.addEntity("courseOffering", "MyCourseOfferingId",
                ValidationTestUtils.makeDummyEntity("courseOffering", "MyCourseOfferingId"));
    }

    private Entity goodSection() {
        final Map<String, Object> goodSection = new HashMap<String, Object>();
        goodSection.put("uniqueSectionCode", "Math101");
        goodSection.put("sequenceOfCourse", 1);
        goodSection.put("educationalEnvironment", "Classroom");
        goodSection.put("mediumOfInstruction", "Face-to-face instruction");
        goodSection.put("populationServed", "Regular Students");
        Map<String, Object> credit = new HashMap<String, Object>();
        credit.put("credit", 1.5);
        credit.put("creditType", "Semester hour credit");
        credit.put("creditConversion", 2.5);
        goodSection.put("availableCredit", credit);
        goodSection.put("schoolId", "42");
        goodSection.put("courseOfferingId", "MyCourseOfferingId");
        goodSection.put("sessionId", "MySessionId");
        List<String> programs = new ArrayList<String>();
        programs.add("program1");
        programs.add("program2");
        goodSection.put("programReference", programs);

        return new Entity() {

            @Override
            public String getType() {
                return "section";
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
    public void testSectionValidation() {
        Entity goodSection = goodSection();
        try {
            assertTrue(validator.validate(goodSection));
        } catch (EntityValidationException ex) {
            for (ValidationError err : ex.getValidationErrors()) {
                System.err.println(err);
            }
            throw ex;
        }
    }

    @Test
    @ExpectedException(value = EntityValidationException.class)
    public void testBadSectionValidation() {
        Entity goodSection = goodSection();
        goodSection.getBody().put("schoolId", "INVALID");
        goodSection.getBody().put("sessionId", "INVALID");
        goodSection.getBody().put("courseId", "INVALID");
        validator.validate(goodSection);
    }

    @Test
    public void testMinimumSection() {
        Entity minSection = goodSection();
        minSection.getBody().remove("educationalEnvironment");
        minSection.getBody().remove("mediumOfInstruction");
        minSection.getBody().remove("populationServed");
        minSection.getBody().remove("availableCredit");
        assertTrue(validator.validate(minSection));
    }

    @Test
    public void testMissingRequiredFields() {
        Entity missingSectionCode = goodSection();
        missingSectionCode.getBody().remove("uniqueSectionCode");
        try {
            assertFalse(validator.validate(missingSectionCode));
        } catch (EntityValidationException e) {
            List<ValidationError> errors = e.getValidationErrors();
            ValidationError error = errors.get(0);
            assertEquals(ValidationError.ErrorType.REQUIRED_FIELD_MISSING, error.getType());
        }

    }
}
