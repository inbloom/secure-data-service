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

package org.slc.sli.api.security.context.validator;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.*;

/**
 * User: dkornishev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext-test.xml"})
public class TransitiveStudentToParentValidatorTest {

    private static final String STUDENT_ID = "Scipio Afrikanus";
    private static final String PARENT1_ID = "Gaius Aurelius";
    private static final String PARENT2_ID = "Gaius Julius Caesar";

    @Resource
    private SecurityContextInjector inj;

    @Resource
    private ValidatorTestHelper helper;

    @Resource
    private TransitiveStudentToParentValidator val;

    @Resource
    private StudentToParentValidator val2;

    @Before
    public void setup() {
        Entity student = Mockito.mock(Entity.class);
        Mockito.when(student.getType()).thenReturn("student");
        Mockito.when(student.getEntityId()).thenReturn(STUDENT_ID);
        Map<String, List<Entity>> map = new HashMap<String, List<Entity>>();
        map.put("studentParentAssociation", Arrays.asList(helper.generateStudentParentAssoc(STUDENT_ID, PARENT1_ID), helper.generateStudentParentAssoc(STUDENT_ID, PARENT2_ID)));
        Mockito.when(student.getEmbeddedData()).thenReturn(map);

        inj.setStudentContext(student);
    }

    @Test
    public void testCanValidate() throws Exception {
        Assert.assertTrue("", val.canValidate(EntityNames.PARENT, true));
        Assert.assertFalse("", val.canValidate(EntityNames.PARENT, false));
        Assert.assertFalse("", val.canValidate(EntityNames.STUDENT, true));

    }

    @Test
    public void testCanValidateIntransitive() throws Exception {
        Assert.assertTrue("", val2.canValidate(EntityNames.PARENT, false));
        Assert.assertFalse("", val2.canValidate(EntityNames.PARENT, true));
        Assert.assertFalse("", val2.canValidate(EntityNames.STUDENT, true));

    }

    @Test
    public void testValidate() throws Exception {
        Set<String> idsToValidate = new HashSet<String>(Arrays.asList("Jadwiga Jagellonska"));
        Assert.assertFalse("Must NOT be able to access", val.validate(EntityNames.PARENT, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList("Jan III Sobieski Piast"));
        Assert.assertFalse("Must NOT be able to access", val.validate(EntityNames.PARENT, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList(PARENT1_ID));
        Assert.assertTrue("Must be able to access", val.validate(EntityNames.PARENT, idsToValidate).containsAll(idsToValidate));

        idsToValidate = new HashSet<String>(Arrays.asList(PARENT2_ID));
        Assert.assertTrue("Must be able to access", val.validate(EntityNames.PARENT, idsToValidate).containsAll(idsToValidate));
    }
}
