package org.slc.sli.validation;

import org.slc.sli.domain.Entity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.slc.sli.validation.ValidationTestUtils.makeDummyEntity;

/**
 * Test for validation of Student Section associations
 *
 * @author nbrown
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SchoolSessionAssociationTest {

    @Autowired
    private EntityValidator validator;

    @Autowired
    private DummyEntityRepository repo;

    @Before
    public void init() {
        repo.clean();
        repo.addEntity("educationOrganization", "A school", makeDummyEntity("educationOrganization", "A school"));
        repo.addEntity("session", "A session", makeDummyEntity("session", "A session"));
    }

    private Entity goodAssociation() {
        final Map<String, Object> goodAssociation = new HashMap<String, Object>();
        goodAssociation.put("schoolId", "A school");
        goodAssociation.put("sessionId", "A session");

        final Map<String, Object> gradingPeriod = new HashMap<String, Object>();

        gradingPeriod.put("gradingPeriod", "Third Nine Weeks");
        gradingPeriod.put("beginDate", "2011-09-01");
        gradingPeriod.put("endDate", "2011-10-31");
        gradingPeriod.put("totalInstructionalDays", 45);

        return new Entity() {

            @Override
            public String getType() {
                return "schoolSessionAssociation";
            }

            @Override
            public String getEntityId() {
                return "id";
            }

            @Override
            public Map<String, Object> getBody() {
                return goodAssociation;
            }

            @Override
            public Map<String, Object> getMetaData() {
                return new HashMap<String, Object>();
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
        goodAssociation.getBody().put("schoolId", "INVALID");
        goodAssociation.getBody().put("sessionId", "INVALID");
        validator.validate(goodAssociation);
    }

}
