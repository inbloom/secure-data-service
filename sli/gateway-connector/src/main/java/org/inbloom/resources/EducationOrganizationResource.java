package org.inbloom.resources;


import org.inbloom.model.EducationOrganization;
import org.inbloom.model.Tenant;
import org.inbloom.repository.EducationOrganizationRepository;
import org.inbloom.repository.TenantRepository;
import org.inbloom.repository.TenantRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Endpoint for educationOrganizations
 * @author ben morgan
 */
@Controller
public class EducationOrganizationResource {

    @Autowired
    TenantRepositoryFactory educationOrganizationRepositoryFactory;

    @Autowired
    MongoRepositoryFactory operatorRepositoryFactory;

    @RequestMapping(value = "/educationOrganizations", method = RequestMethod.GET, produces = "application/json")

    public @ResponseBody List<EducationOrganization> get() {

        //find all tenants
        TenantRepository tenantRepo = operatorRepositoryFactory.getRepository(TenantRepository.class);
        List<Tenant>  tenants = tenantRepo.findAll();

        List<EducationOrganization> edOrgs = new ArrayList<EducationOrganization>();
        //find edOrgs for each tenant
        for(Tenant tenant : tenants)
        {
            EducationOrganizationRepository edOrgRepo = educationOrganizationRepositoryFactory.getEdOrgRepository(tenant.getDbName());
            if(edOrgRepo != null)
            {
                edOrgs.addAll(edOrgRepo.findAll());
            }

        }

        return edOrgs;
    }
}
