package org.slc.sli.dal.adapter.transform.impl;

import org.springframework.stereotype.Component;


/**
 * Sample transform
 *
 * @author srupasinghe
 */
@Component
public class StudentSectionAssociationTransform extends SuperDocumentTransform {

    public StudentSectionAssociationTransform() {
        super("studentSectionAssociation", "student", "studentId", "sections");
    }

    @Override
    public boolean isTransformable(String type, int fromVersion, int toVersion) {
        if (type.equals("studentSectionAssociation")) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isTransformable(String type) {
        if (type.equals("studentSectionAssociation")) {
            return true;
        }

        return false;
    }
}
