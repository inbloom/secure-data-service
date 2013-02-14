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

package org.slc.sli.api.init;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class for bootstrapping the initial SLI realm that must always exist into mongo.
 *
 */
public class AppDeveloperRealmInitializer extends RealmInitializer{

    @Value("${bootstrap.developer.realm.name}")
    private String devRealmName;

    @Value("${bootstrap.developer.realm.uniqueId}")
    private String devUniqueId;

    @Value("${bootstrap.developer.realm.idpId}")
    private String devIdpId;

    @Value("${bootstrap.developer.realm.redirectEndpoint}")
    private String devRedirectEndpoint;

    @PostConstruct
    public void bootstrap() {
        // boostrap the developer realm
        Map<String, Object> bootstrapDeveloperRealmBody = createDeveloperRealmBody();
        createOrUpdateRealm(devUniqueId, bootstrapDeveloperRealmBody);
    }

    protected Map<String, Object> createDeveloperRealmBody() {
        Map<String, Object> body = createRealmBody(devUniqueId, devRealmName, "", null, false, true, devIdpId,
                devRedirectEndpoint);

        return insertSaml(body, false, true);
    }
}
