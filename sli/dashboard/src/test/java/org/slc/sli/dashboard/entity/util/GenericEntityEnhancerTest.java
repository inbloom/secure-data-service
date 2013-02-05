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


/**
 *
 */
package org.slc.sli.dashboard.entity.util;

import org.junit.Assert;
import org.junit.Test;

import org.slc.sli.dashboard.entity.GenericEntity;

/**
 * @author tosako
 *
 */
public class GenericEntityEnhancerTest {

    /**
     * Test method for
     * {@link org.slc.sli.dashboard.entity.util.GenericEntityEnhancer#enhanceStudent(org.slc.sli.dashboard.entity.GenericEntity)}
     * .
     */
    @Test
    public void testEnhanceStudent() {
        GenericEntity entity = new GenericEntity();
        entity.put("gradeLevel", "Adult Education");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "AE");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Early Education");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "EE");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Eighth grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "8");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Eleventh grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "11");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Fifth grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "5");

        entity = new GenericEntity();
        entity.put("gradeLevel", "First grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "1");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Fourth grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "4");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Grade 13");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "13");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Infant/toddler");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "IN");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Kindergarten");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "K");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Ninth grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "9");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Other");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "O");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Postsecondary");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "+S");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Preschool/Prekindergarten");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "PK");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Second grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "2");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Seventh grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "7");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Sixth grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "6");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Tenth grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "10");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Third grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "3");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Transitional Kindergarten");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "TK");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Twelfth grade");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "12");

        entity = new GenericEntity();
        entity.put("gradeLevel", "Ungraded");
        entity = GenericEntityEnhancer.enhanceStudent(entity);
        Assert.assertEquals(entity.get("gradeLevelCode"), "UG");

        entity = new GenericEntity();
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertNull(entity.get("gradeLevelCode"));
    }

    /**
     * Test method for
     * {@link org.slc.sli.dashboard.entity.util.GenericEntityEnhancer#enhanceStudentSchoolAssociation(org.slc.sli.dashboard.entity.GenericEntity)}
     * .
     */
    @Test
    public void testEnhanceStudentSchoolAssociation() {
        GenericEntity entity = new GenericEntity();
        entity.put("entryGradeLevel", "Adult Education");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "AE");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Early Education");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "EE");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Eighth grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "8");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Eleventh grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "11");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Fifth grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "5");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "First grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "1");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Fourth grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "4");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Grade 13");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "13");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Infant/toddler");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "IN");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Kindergarten");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "K");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Ninth grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "9");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Other");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "O");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Postsecondary");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "+S");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Preschool/Prekindergarten");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "PK");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Second grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "2");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Seventh grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "7");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Sixth grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "6");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Tenth grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "10");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Third grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "3");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Transitional Kindergarten");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "TK");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Twelfth grade");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "12");

        entity = new GenericEntity();
        entity.put("entryGradeLevel", "Ungraded");
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertEquals(entity.get("entryGradeLevelCode"), "UG");

        entity = new GenericEntity();
        entity = GenericEntityEnhancer.enhanceStudentSchoolAssociation(entity);
        Assert.assertNull(entity.get("entryGradeLevelCode"));

    }

}
