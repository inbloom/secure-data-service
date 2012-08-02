package org.slc.sli.api.resources.config;

import com.sun.jersey.api.core.DefaultResourceConfig;
import org.slc.sli.api.resources.generic.GenericResource;


/**
 * Created with IntelliJ IDEA.
 * User: srupasinghe
 * Date: 8/1/12
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceRegisterConfig extends DefaultResourceConfig {

    public ResourceRegisterConfig() {
        super();

        init();
    }

    private void init() {
        getExplicitRootResources().put("generic/students", GenericResource.class);
        getExplicitRootResources().put("generic/sections", GenericResource.class);
        getExplicitRootResources().put("generic/schools", GenericResource.class);
    }
}
