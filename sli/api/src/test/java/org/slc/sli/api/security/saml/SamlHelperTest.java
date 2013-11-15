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
package org.slc.sli.api.security.saml;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.security.context.APIAccessDeniedException;
import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ablum
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SamlHelperTest {
    @InjectMocks
    @Autowired
    SamlHelper samlHelper;

    @Mock
    RealmHelper realmHelper;

    private Entity realm = Mockito.mock(Entity.class);

    private static final String REALM_ID = "REALM_ID";
    private static final String ARTIFACT = "AAQAAjh3bwgbBZ+LiIx3/RVwDGy0aRUu+xxuNtTZVbFofgZZVCKJQwQNQ7Q=";
    private static final String ARTIFACT_RESOLUTION_ENDPOINT = "https://example/artifactResolution";

    private static final String VALID_SOURCEID = "38776f081b059f8b888c77fd15700c6cb469152e";
    private static final String INCORRECT_SOURCEID = "59886f081b059f8b888c77fd15700c6cb469152e";
    private static final String NOT_HEX_SOURCEID = "49887f091b059f8b888c77fd15700c6vf469152f";


    @SuppressWarnings("unchecked")
    @Before
    public void init() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(realmHelper.findRealmById(Mockito.anyString())).thenReturn(realm);
        Mockito.when(realm.getEntityId()).thenReturn(REALM_ID);

    }

    @Test
    public void testGetArtifactUrl() {
        setRealm(VALID_SOURCEID);

        String result = samlHelper.getArtifactUrl(REALM_ID, ARTIFACT);
        Assert.assertEquals(ARTIFACT_RESOLUTION_ENDPOINT, result);

    }

    @Test(expected = APIAccessDeniedException.class)
    public void testGetArtifactUrlIncorrectSourceId() {
        setRealm(INCORRECT_SOURCEID);

        String result = samlHelper.getArtifactUrl(REALM_ID, ARTIFACT);
        Assert.assertEquals(ARTIFACT_RESOLUTION_ENDPOINT, result);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArtifactUrlInvalidSourceIdFormat() {
        setRealm(NOT_HEX_SOURCEID);

        String result = samlHelper.getArtifactUrl(REALM_ID, ARTIFACT);
        Assert.assertEquals(ARTIFACT_RESOLUTION_ENDPOINT, result);

    }

    private void setRealm(String sourceId) {
        Map<String, Object> realmBody = new HashMap<String, Object>();
        realmBody.put("edOrg", "My School");
        Map<String, List<Map<String, String>>> saml = new HashMap<String, List<Map<String, String>>>();
        saml.put("field", new ArrayList<Map<String, String>>());
        realmBody.put("saml", saml);
        realmBody.put("tenantId", "My Tenant");
        realmBody.put("admin", true);
        realmBody.put("developer", Boolean.FALSE);
        Map<String, Object> idp = new HashMap<String, Object>();
        idp.put("artifactResolutionEndpoint", ARTIFACT_RESOLUTION_ENDPOINT);
        idp.put("sourceId", sourceId);
        realmBody.put("idp", idp);
        Mockito.when(realm.getBody()).thenReturn(realmBody);
    }

}
