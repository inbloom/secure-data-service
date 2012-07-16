package org.slc.sli.api.resources.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import org.slc.sli.api.init.RoleInitializer;
import org.slc.sli.api.resources.security.UserResource.RightToGroupMapper;
import org.slc.sli.domain.enums.Right;

/**
 * Unit tests for user resource.
 * @author joechung
 *
 */
public class UserResourceTest {

    private static final GrantedAuthority[] EMPTY_RIGHT = {};
    private static final GrantedAuthority[] NO_ADMIN_RIGHT = {Right.INGEST_DATA, Right.ANONYMOUS_ACCESS};
    private static final GrantedAuthority[] ONE_ADMIN_RIGHT_ONLY = {Right.CRUD_SEA_ADMIN};
    private static final GrantedAuthority[] ONE_ADMIN_RIGHT_WITH_OTHERS = {Right.CRUD_SLC_OPERATOR, Right.AGGREGATE_READ};
    private static final GrantedAuthority[] TWO_ADMIN_RIGHTS_ONLY = {Right.CRUD_SLC_OPERATOR, Right.CRUD_SEA_ADMIN};
    private static final GrantedAuthority[] TWO_ADMIN_RIGHTS_WITH_OTHERS = {Right.EDORG_APP_AUTHZ, Right.CRUD_LEA_ADMIN, Right.CRUD_SANDBOX_ADMIN};

    @Test
    public void testValidateAdminRights() {
        assertNotNull(UserResource.validAdminRights(Arrays.asList(EMPTY_RIGHT)));
        assertNotNull(UserResource.validAdminRights(Arrays.asList(NO_ADMIN_RIGHT)));
        assertNull(UserResource.validAdminRights(Arrays.asList(ONE_ADMIN_RIGHT_ONLY)));
        assertNull(UserResource.validAdminRights(Arrays.asList(ONE_ADMIN_RIGHT_WITH_OTHERS)));
        assertNull(UserResource.validAdminRights(Arrays.asList(TWO_ADMIN_RIGHTS_ONLY)));
        assertNull(UserResource.validAdminRights(Arrays.asList(TWO_ADMIN_RIGHTS_WITH_OTHERS)));
    }

    @Test
    public void testRightToGroupMapper() {
        RightToGroupMapper rightToGroupMapper = RightToGroupMapper.getInstance();
        assertEquals(0, rightToGroupMapper.getGroups(Arrays.asList(new GrantedAuthority[] {})).size());

        Collection<String> leaAdminGroups = rightToGroupMapper.getGroups(Arrays.asList(new GrantedAuthority[] {Right.CRUD_LEA_ADMIN}));
        assertCollectionEquals(Arrays.asList(new String[] {RoleInitializer.LEA_ADMINISTRATOR, RoleInitializer.REALM_ADMINISTRATOR, RoleInitializer.INGESTION_USER}), leaAdminGroups);

        Collection<String> seaAdminGroups = rightToGroupMapper.getGroups(Arrays.asList(new GrantedAuthority[] {Right.CRUD_SEA_ADMIN, Right.CRUD_LEA_ADMIN}));
        assertCollectionEquals(Arrays.asList(new String[] {RoleInitializer.SEA_ADMINISTRATOR, RoleInitializer.LEA_ADMINISTRATOR, RoleInitializer.REALM_ADMINISTRATOR, RoleInitializer.INGESTION_USER}), seaAdminGroups);

        Collection<String> slcOperatorGroups = rightToGroupMapper.getGroups(Arrays.asList(new GrantedAuthority[] {Right.CRUD_SLC_OPERATOR, Right.CRUD_SEA_ADMIN, Right.CRUD_LEA_ADMIN}));
        assertCollectionEquals(Arrays.asList(new String[] {RoleInitializer.SLC_OPERATOR, RoleInitializer.SEA_ADMINISTRATOR, RoleInitializer.LEA_ADMINISTRATOR, RoleInitializer.REALM_ADMINISTRATOR, RoleInitializer.INGESTION_USER}), slcOperatorGroups);

        Collection<String> sandboxAdminGroups = rightToGroupMapper.getGroups(Arrays.asList(new GrantedAuthority[] {Right.CRUD_SANDBOX_ADMIN}));
        assertCollectionEquals(Arrays.asList(new String[] {RoleInitializer.SANDBOX_ADMINISTRATOR, RoleInitializer.APP_DEVELOPER, RoleInitializer.INGESTION_USER}), sandboxAdminGroups);

        Collection<String> slcOperatorGroups2 = rightToGroupMapper.getGroups(Arrays.asList(new GrantedAuthority[] {Right.CRUD_SLC_OPERATOR}));
        assertCollectionEquals(Arrays.asList(new String[] {RoleInitializer.SLC_OPERATOR, RoleInitializer.REALM_ADMINISTRATOR, RoleInitializer.INGESTION_USER}), slcOperatorGroups2);
    }



    private void assertCollectionEquals(final Collection<String> expected, final Collection<String> actual) {
        assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
    }
}
