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