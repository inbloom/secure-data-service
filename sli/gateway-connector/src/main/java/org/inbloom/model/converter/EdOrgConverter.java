package org.inbloom.model.converter;

import com.mongodb.DBObject;
import org.inbloom.model.EducationOrganization;
import org.springframework.core.convert.converter.Converter;

/**
 * @author ben morgan
 */
public class EdOrgConverter extends BaseConverter implements Converter<DBObject, EducationOrganization> {

    @Override
    public EducationOrganization convert(DBObject dbObj) {
        EducationOrganization edOrg = new EducationOrganization();

        edOrg.setId(getString(dbObj, "_id"));

        edOrg.setNameOfInstitution(getString(dbObj, "body.nameOfInstitution"));
        edOrg.setStateEdOrgId(getString(dbObj, "body.stateOrganizationId"));

        edOrg.setParentEdOrgReferences(getStringList(dbObj, "body.parentEducationAgencyReference"));
        edOrg.setGradesOffered(getStringList(dbObj, "body.gradesOffered"));
        edOrg.setSchoolCategories(getStringList(dbObj, "body.schoolCategories"));

        return edOrg;
    }
}
