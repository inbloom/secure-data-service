package org.slc.sli.api.security.context.resolver;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.resources.security.DelegationUtil;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.enums.Right;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
public class SecurityEventContextResolverTest {

	static final List<String> ROLES_SEA_OR_REALM_ADMIN = Arrays.asList(
			RoleInitializer.SEA_ADMINISTRATOR,
			RoleInitializer.REALM_ADMINISTRATOR);
	static final List<String> ROLES_LEA_ADMIN = Arrays
			.asList(RoleInitializer.LEA_ADMINISTRATOR);

	@Autowired
	EdOrgHelper helper;

	@Autowired
	private DelegationUtil delegationUtil;

	@Autowired
	private EdOrgHelper edOrgHelper;

	@Autowired
	private SecurityEventContextResolver resolver;

	private static final String RESOURCE_NAME = "securityEvent";

	@Test
	public void testFindAccessibleForSEAAdministrator() {
		setAuth("SEA Administrator","IL",false);
		PagingRepositoryDelegate<Entity> repository = Mockito
				.mock(PagingRepositoryDelegate.class);
		List<String> homeEdOrgs = new ArrayList<String>();
		homeEdOrgs.add("IL");
		NeutralQuery or = createFilter(homeEdOrgs, ROLES_SEA_OR_REALM_ADMIN);
		NeutralQuery query = new NeutralQuery();
		query.addOrQuery(or);
		List<String> result = createSEAResult();
		Mockito.when(repository.findAllIds(RESOURCE_NAME, query)).thenReturn(
				result);
		resolver.setRepository(repository);
		Entity entity = null;

		List<String> returnResult = resolver.findAccessible(entity);
		Mockito.verify(repository, times(1)).findAllIds(any(String.class), any(NeutralQuery.class));
		Assert.assertEquals(result, returnResult);
	}

	private NeutralQuery createFilter(List<String> homeEdOrgs, List<String> roles) {
		NeutralQuery or = new NeutralQuery();
		or.addCriteria(new NeutralCriteria("targetEdOrgList",
				NeutralCriteria.CRITERIA_IN, homeEdOrgs));
		or.addCriteria(new NeutralCriteria("roles",
				NeutralCriteria.CRITERIA_IN, roles));
		return or;
	}

	@Test
	public void testFindAccessibleForSEAAdministratorWithDelegation() {
		setAuth("SEA Administrator","IL",true);
		PagingRepositoryDelegate<Entity> repository = Mockito
				.mock(PagingRepositoryDelegate.class);
		List<String> delegatedEdOrgs = new ArrayList<String>();
		delegatedEdOrgs.add("IL-SUNSET");
		List<String> homeEdOrgs = new ArrayList<String>();
		homeEdOrgs.add("IL");
		NeutralQuery or = createFilter(homeEdOrgs,ROLES_SEA_OR_REALM_ADMIN);
        NeutralQuery delegateOr = createFilter(delegatedEdOrgs,ROLES_LEA_ADMIN);
		NeutralQuery query = new NeutralQuery();
		query.addOrQuery(or);
		query.addOrQuery(delegateOr);
		List<String> result = createFullResult();
		Mockito.when(repository.findAllIds(RESOURCE_NAME, query)).thenReturn(
				result);
		DelegationUtil delegationUtil = Mockito.mock(DelegationUtil.class);
		List<String> delegatedLEAStateIds = new ArrayList<String>();
		delegatedLEAStateIds.add("IL-SUNSET");
		Mockito.when(delegationUtil.getSecurityEventDelegateStateIds()).thenReturn(delegatedLEAStateIds);
		resolver.setRepository(repository);
		resolver.setDelegationUtil(delegationUtil);
		Entity entity = null;

		List<String> returnResult = resolver.findAccessible(entity);
		Mockito.verify(repository, times(1)).findAllIds(any(String.class), any(NeutralQuery.class));
		Assert.assertEquals(result, returnResult);
	}


	@Test
	public void testFindAccessibleForOperator() {
		setAuth("SLC Operator",null,false);
		PagingRepositoryDelegate<Entity> repository = Mockito
				.mock(PagingRepositoryDelegate.class);
		NeutralQuery or = new NeutralQuery();
		NeutralQuery query = new NeutralQuery();
		query.addOrQuery(or);
		List<String> result = createFullResult();

		Mockito.when(repository.findAllIds(RESOURCE_NAME, query)).thenReturn(
				result);
		resolver.setRepository(repository);
		Entity entity = null;
		List<String> returnResult = resolver.findAccessible(entity);
		Mockito.verify(repository, times(1)).findAllIds(any(String.class), any(NeutralQuery.class));
		Assert.assertEquals(result, returnResult);
	}

	private List<String> createFullResult() {
		List<String> result = new ArrayList<String>();
		result.add("d0bea921-3ee5-487d-b4db-cb126a705a29");
		result.add("f93b2fe8-321b-4ab8-a0de-bbb7f2ddae50");
		result.add("d0bea921-3ee5-487d-b4db-cb126a705a30");
		result.add("d0bea921-3ee5-487d-b4db-cb126a705a31");
		result.add("268406bf-83b5-4d43-9b56-c428e0998cb4");
		result.add("268406bf-83b5-4d43-9b56-c428e0998cb3");
		result.add("07623f03-126e-427d-9ed4-29562388cdcc");
		result.add("268406bf-83b5-4d43-9b56-c428e0998cb2");
		return result;
	}

	@Test
	public void testFindAccessibleForLEAAdministrator() {
		setAuth("LEA Administrator","IL-SUNSET",false);
		PagingRepositoryDelegate<Entity> repository = Mockito
				.mock(PagingRepositoryDelegate.class);
		List<String> homeEdOrgs = new ArrayList<String>();
		homeEdOrgs.add("IL-SUNSET");
		NeutralQuery or = createFilter(homeEdOrgs,ROLES_LEA_ADMIN);
		NeutralQuery query = new NeutralQuery();
		query.addOrQuery(or);
		List<String> result = createLEAResult();
		Mockito.when(repository.findAllIds(RESOURCE_NAME, query)).thenReturn(
				result);
		resolver.setRepository(repository);
		Entity entity = null;

		List<String> returnResult = resolver.findAccessible(entity);
		Mockito.verify(repository, times(1)).findAllIds(any(String.class), any(NeutralQuery.class));
		Assert.assertEquals(result, returnResult);
	}

	private List<String> createLEAResult() {
		List<String> result = new ArrayList<String>();
		result.add("268406bf-83b5-4d43-9b56-c428e0998cb4");
		result.add("268406bf-83b5-4d43-9b56-c428e0998cb3");
		result.add("07623f03-126e-427d-9ed4-29562388cdcc");
		result.add("268406bf-83b5-4d43-9b56-c428e0998cb2");
		return result;
	}

	private List<String> createSEAResult() {
		List<String> result = new ArrayList<String>();
		result.add("d0bea921-3ee5-487d-b4db-cb126a705a29");
		result.add("f93b2fe8-321b-4ab8-a0de-bbb7f2ddae50");
		result.add("d0bea921-3ee5-487d-b4db-cb126a705a30");
		result.add("d0bea921-3ee5-487d-b4db-cb126a705a31");
		return result;
	}

	private void setAuth(String role, String edOrg, Boolean enabled) {
		SLIPrincipal principal = new SLIPrincipal();
		principal.setEdOrg(edOrg);
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(role);
		principal.setRoles(roles);

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		if (enabled) {
			authorities.add(Right.EDORG_DELEGATE);
		}
		Authentication auth = new PreAuthenticatedAuthenticationToken(
				principal, null, authorities);

		SecurityContextHolder.getContext().setAuthentication(auth);
	}

}
