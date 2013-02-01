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


package controllers;

import play.mvc.*;
import play.Logger;
import java.util.*;

import models.*;

/**
 * Controllers for discovery service.
 * Details controller for realm selection and redirecting to an identity provider.
 */
public class DiscoveryService extends Controller {

    public static void index() {
        List<District> districts = District.findAll();
        List<IdProvider> idps = IdProvider.findAll();
        render(districts, idps);
    }

    public static void redirect(long districtId) {

        District district = District.findById(districtId);

        if( district == null || district.idp == null ) {
            index();
        } else {
            IdProvider idp = district.idp;
            int timeout = 5;
            String path = "idpProxy";
            render(district, idp, timeout, path);
        }

    }

    public static void idpProxy(String idpParam) {
        render(idpParam);
    }

}
