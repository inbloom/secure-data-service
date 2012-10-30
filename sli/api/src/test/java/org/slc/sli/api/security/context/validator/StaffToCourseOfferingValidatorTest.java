package org.slc.sli.api.security.context.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class StaffToCourseOfferingValidatorTest {

	@Resource
	private StaffToCourseOfferingValidator val;

	@Resource
	private SecurityContextInjector injector;

	@Resource
	private ValidatorTestHelper helper;

	@Before
	public void init() {
		String user = "fake Staff";
		String fullName = "Fake Staff";
		List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

		Entity entity = Mockito.mock(Entity.class);
		Mockito.when(entity.getType()).thenReturn("staff");
		Mockito.when(entity.getEntityId()).thenReturn(helper.STAFF_ID);
		injector.setCustomContext(user, fullName, "MERPREALM", roles, entity, helper.ED_ORG_ID);
	}

	@Test
	public void testCanValidate() {
		Assert.assertTrue("Must be able to validate",val.canValidate(EntityNames.COURSE_OFFERING, false));
		Assert.assertFalse("Must not be able to validate",val.canValidate(EntityNames.ADMIN_DELEGATION, false));
	}
	
	@Test
	public void testValidation() {
		Assert.assertFalse(val.validate(EntityNames.COURSE_OFFERING, new HashSet<String>(Arrays.asList("lamb"))));
	}
}
