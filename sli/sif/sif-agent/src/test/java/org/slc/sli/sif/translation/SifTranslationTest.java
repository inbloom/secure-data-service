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

package org.slc.sli.sif.translation;

import java.util.List;

import junit.framework.Assert;
import openadk.library.hrfin.EmployeePersonal;
import openadk.library.hrfin.EmploymentRecord;
import openadk.library.student.LEAInfo;
import openadk.library.student.OperationalStatus;
import openadk.library.student.SchoolInfo;
import openadk.library.student.StaffPersonal;
import openadk.library.student.StudentPersonal;
import openadk.library.student.Title1Status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.slcinterface.SifIdResolverImplDummy;
import org.slc.sli.sif.slcinterface.SimpleEntity;

/**
 * Integration test for the configured SIF->SLI translation.
 *
 * @author jtully
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:/spring/test-applicationContext.xml" })
public class SifTranslationTest extends AdkTest {

    @Autowired
    private SifTranslationManager translationManager;

    @Autowired
    private SifIdResolverImplDummy idResolver;

    private static final String ZONE_ID = "TestZone";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        idResolver.reset();
    }

    @Test
    public void shouldTranslateSchoolInfoToSchool() {
        SchoolInfo info = createSchoolInfo();
        List<SliEntity> entities = translationManager.translate(info, ZONE_ID);

        Assert.assertEquals("Should create a single SLI school entity", 1, entities.size());
        Assert.assertNotNull("NULL sli entity", entities.get(0));
        Assert.assertEquals("Mapped SLI entitiy should be of type educationOrganization", "school", entities.get(0)
                .entityType());
    }

    @Test
    public void shouldTranslateLEAInfoInfoToLEA() {
        LEAInfo info = createLEAInfo();
        List<SliEntity> entities = translationManager.translate(info, ZONE_ID);

        Assert.assertEquals("Should create a single SLI LEA entity", 1, entities.size());
        Assert.assertNotNull("NULL sli entity", entities.get(0));
        Assert.assertEquals("Mapped SLI entitiy should be of type educationOrganization", "educationOrganization",
                entities.get(0).entityType());
    }

    @Test
    public void shouldTranslateStudentPersonalToStudent() {
        StudentPersonal info = new StudentPersonal();
        List<SliEntity> entities = translationManager.translate(info, ZONE_ID);

        Assert.assertEquals("Should create a single SLI student entity", 1, entities.size());
        Assert.assertNotNull("NULL sli entity", entities.get(0));
        Assert.assertEquals("Mapped SLI entitiy should be of type student", "student", entities.get(0).entityType());
    }

    @Test
    public void shouldTranslateStaffPersonalToStaff() {
        StaffPersonal info = new StaffPersonal();
        List<SliEntity> entities = translationManager.translate(info, ZONE_ID);

        Assert.assertEquals("Should create a single SLI staff entity", 1, entities.size());
        Assert.assertNotNull("NULL sli entity", entities.get(0));
        Assert.assertEquals("Mapped SLI entitiy should be of type staff", "staff", entities.get(0).entityType());
    }

    @Test
    public void shouldTranslateEmployeePersonalToStaff() {
        EmployeePersonal info = new EmployeePersonal();
        List<SliEntity> entities = translationManager.translate(info, ZONE_ID);

        Assert.assertEquals("Should create a single SLI staff entity", 1, entities.size());
        Assert.assertNotNull("NULL sli entity", entities.get(0));
        Assert.assertEquals("Mapped SLI entitiy should be of type staff", "staff", entities.get(0).entityType());
    }

    @Test
    public void shouldTranslateEmploymentRecordToStaffEdoOrgAssoc() {

        // set up id resolution
        idResolver.putEntity("sifStaffId", new SimpleEntity("staff"));
        idResolver.putEntity("sifEdOrgId", new SimpleEntity("educationOrganization"));

        EmploymentRecord info = new EmploymentRecord();
        info.setLEAInfoRefId("sifEdOrgId");
        info.setSIF_RefId("sifStaffId");

        List<SliEntity> entities = translationManager.translate(info, ZONE_ID);

        Assert.assertEquals("Should create a single SLI entity", 1, entities.size());
        Assert.assertNotNull("NULL sli entity", entities.get(0));
        Assert.assertEquals("Mapped SLI entitiy should be of type staffEducationOrganizationAssociation",
                "staffEducationOrganizationAssociation", entities.get(0).entityType());

    }


    private SchoolInfo createSchoolInfo() {
        SchoolInfo info = new SchoolInfo();

        info.setStateProvinceId("stateOrgId");
        info.setSchoolName("schoolName");
        info.setSchoolURL("schoolUrl");
        info.setTitle1Status(Title1Status.SCHOOLWIDE);

        return info;
    }

    private LEAInfo createLEAInfo() {
        LEAInfo info = new LEAInfo();

        info.setStateProvinceId("stateOrgId");
        info.setLEAName("LEAName");
        info.setLEAURL("LEAURL");
        info.setOperationalStatus(OperationalStatus.AGENCY_CLOSED);

        return info;
    }
}
