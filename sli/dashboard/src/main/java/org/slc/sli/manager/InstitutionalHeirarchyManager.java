package org.slc.sli.manager;

import com.google.gson.Gson;

import org.slc.sli.entity.School;
import org.slc.sli.entity.EducationalOrganization;
import org.slc.sli.entity.EducationalOrganizationAssociation;
import org.slc.sli.entity.SchoolEducationalOrganizationAssociation;
import org.slc.sli.util.SecurityUtil;

/**
 * Retrieves and applies necessary business logic to obtain institution data
 * 
 * @author dwu
 *
 */
public class InstitutionalHeirarchyManager extends Manager {

    public School[] getSchools() {
        return apiClient.getSchools(SecurityUtil.getToken());
    }
    public EducationalOrganization[] getEducationalOrganizations() {
        return apiClient.getEducationalOrganizations(SecurityUtil.getToken());
    }
    public SchoolEducationalOrganizationAssociation[] getSchoolEducationalOrganizationAssociation() {
        return apiClient.getSchoolEducationalOrganizationAssociations(SecurityUtil.getToken());
    }
    public EducationalOrganizationAssociation[] getEducationalOrganizationAssociations() {
        return apiClient.getEducationalOrganizationAssociations(SecurityUtil.getToken());
    }

    /**
     * Returns the institutional heirarchy visible to the current user as a JSON string
     * with the ed-org level flattened  
     * @return 
     */
    public String getInstHeirarchyJSON() {
        // Okay, this function should arguably be placed in a view package, but if the school JSONs 
        // are already being constructed in the *API Client* level (WTF was that about anyway?!?),
        // constructing inst heirarchy as JSON here isn't making things worse than they already are. 
        // @@@
        Gson gson = new Gson();

        School[] schools = getSchools();
        EducationalOrganization[] edorgs = getEducationalOrganizations();
        EducationalOrganizationAssociation[] edorgAss = getEducationalOrganizationAssociations();

        return "[]";
    }


}
