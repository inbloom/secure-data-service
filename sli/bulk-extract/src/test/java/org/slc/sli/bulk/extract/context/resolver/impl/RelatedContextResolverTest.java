package org.slc.sli.bulk.extract.context.resolver.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;

public class RelatedContextResolverTest {

    private RelatedContextResolver resolver;

    private ReferrableResolver staffTeacherResolver;
    private static final String REFERRED_ID = "ekrabapple";
    private Map<String, Object> body;
    private Entity mockEntity;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        resolver = Mockito.mock(StaffTeacherDirectRelatedContextResolver.class);

        mockEntity = Mockito.mock(Entity.class);
        staffTeacherResolver = Mockito.mock(ReferrableResolver.class);

        Mockito.when(resolver.findGoverningEdOrgs(Mockito.any(Entity.class))).thenCallRealMethod();
        Mockito.when(resolver.getReferredId(Mockito.anyString(), Mockito.anyMap())).thenCallRealMethod();
        Mockito.when(resolver.getReferredResolver()).thenReturn(staffTeacherResolver);
        Mockito.when(resolver.getReferenceProperty(EntityNames.TEACHER_SCHOOL_ASSOCIATION)).thenReturn(ParameterConstants.TEACHER_ID);

        body = new HashMap<String, Object>();
        body.put(ParameterConstants.TEACHER_ID, REFERRED_ID);
    }

    @Test
    public void testfindGoverningEdOrgs() {
        Mockito.when(mockEntity.getType()).thenReturn(EntityNames.TEACHER_SCHOOL_ASSOCIATION);
        Mockito.when(mockEntity.getBody()).thenReturn(body);

        Set<String> edOrgs = new HashSet<String>(Arrays.asList("springfieldElementary", "springfieldHigh"));
        Mockito.when(staffTeacherResolver.findGoverningEdOrgs(Mockito.eq(REFERRED_ID), Mockito.eq(mockEntity))).thenReturn(edOrgs);

        assertTrue(resolver.findGoverningEdOrgs(mockEntity).equals(edOrgs));
    }

    @Test
    public void testgetReferredId() {
        assertTrue(resolver.getReferredId(EntityNames.TEACHER_SCHOOL_ASSOCIATION, body).equals(REFERRED_ID));
    }

}
