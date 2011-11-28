package controllers;

import play.mvc.*;
import play.Logger;
import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        List<District> districts = District.findAll();
        List<IdProvider> idps = IdProvider.findAll();
        render(districts, idps);
    }

    public static void redirect(long districtId) {

        try {
            District district = District.findById(districtId);
            IdProvider idp = district.idp;
            int timeout = 5;
            String path = "idpProxy";

            render(district, idp, timeout, path);
        }
        catch (NullPointerException ex) {
            Logger.error(ex, "Lookup failed?");
            index();
        }

    }

    public static void idpProxy(String idpParam) {
        render(idpParam);
    }

}