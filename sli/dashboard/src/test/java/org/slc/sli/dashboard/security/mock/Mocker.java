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


package org.slc.sli.dashboard.security.mock;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * TODO: Add javadocs
 *
 *
 */
public class Mocker {

    public static final String MOCK_URL = "mock";
    public static final String VALID_TOKEN = "valid_token";
    public static final String INVALID_TOKEN = "invalid_token";


    public static final String PAYLOAD       = "userdetails.token.id=AQIC5wM2LY4Sfczq7rqe5T89hyJzxvtfmlagB2VFZEFxE2I.*AAJTSQACMDE.*\r\n" + "userdetails.role=id=student,ou=group,dc=opensso,dc=java,dc=net\r\n"
            + "userdetails.role=id=parent,ou=group,dc=opensso,dc=java,dc=net\r\n" + "userdetails.role=id=teacher,ou=group,dc=opensso,dc=java,dc=net\r\n" + "userdetails.attribute.name=uid\r\n"
            + "userdetails.attribute.value=demo\r\n" + "userdetails.attribute.name=userpassword\r\n" + "userdetails.attribute.value={SSHA}lDuocwqvaaTzkN5Bi+beD2BJBP4sI/dtzD/BfQ==\r\n"
            + "userdetails.attribute.name=sn\r\n" + "userdetails.attribute.value=demo\r\n" + "userdetails.attribute.name=cn\r\n" + "userdetails.attribute.value=demo demo\r\n"
            + "userdetails.attribute.name=givenname\r\n" + "userdetails.attribute.value=demo\r\n" + "userdetails.attribute.name=inetuserstatus\r\n" + "userdetails.attribute.value=Active\r\n"
            + "userdetails.attribute.name=dn\r\n" + "userdetails.attribute.value=uid=demo,ou=people,dc=slidev,dc=net\r\n" + "userdetails.attribute.name=objectclass\r\n"
            + "userdetails.attribute.value=person\r\n" + "userdetails.attribute.value=sunIdentityServerLibertyPPService\r\n" + "userdetails.attribute.value=inetorgperson\r\n"
            + "userdetails.attribute.value=sunFederationManagerDataStore\r\n" + "userdetails.attribute.value=iPlanetPreferences\r\n"
            + "userdetails.attribute.value=iplanet-am-auth-configuration-service\r\n" + "userdetails.attribute.value=organizationalperson\r\n"
            + "userdetails.attribute.value=sunFMSAML2NameIdentifier\r\n" + "userdetails.attribute.value=inetuser\r\n" + "userdetails.attribute.value=iplanet-am-managed-person\r\n"
            + "userdetails.attribute.value=iplanet-am-user-service\r\n" + "userdetails.attribute.value=sunAMAuthAccountLockout\r\n" + "userdetails.attribute.value=top";


    public static RestTemplate mockRest() {
        RestTemplate rest = mock(RestTemplate.class);

        ResponseEntity<String> validationOK = new ResponseEntity<String>("boolean=true", HttpStatus.OK);
        ResponseEntity<String> validationFail = new ResponseEntity<String>("boolean=true", HttpStatus.UNAUTHORIZED);
        ResponseEntity<String> attributesOK = new ResponseEntity<String>(PAYLOAD, HttpStatus.OK);

        when(rest.getForEntity(MOCK_URL + "/identity/isTokenValid?tokenid=" + VALID_TOKEN, String.class, Collections.<String, Object>emptyMap())).thenReturn(validationOK);
        when(rest.getForEntity(MOCK_URL + "/identity/isTokenValid?tokenid=" + INVALID_TOKEN, String.class, Collections.<String, Object>emptyMap())).thenReturn(validationFail);
        when(rest.getForEntity(MOCK_URL + "/identity/attributes?subjectid=" + VALID_TOKEN, String.class, Collections.<String, Object>emptyMap())).thenReturn(attributesOK);

        return rest;

    }


}
