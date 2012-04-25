/**
 *
 */
package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;


/**
 * @author lchen
 *
 */
public class StudentParentAssociationMeta {

    public final String id;
    public final String studentIds;
    public final String parentIds;
    public final String simpleId;
    public final boolean isMale;
    public StudentParentAssociationMeta (String id, StudentMeta studentMeta, ParentMeta parentMeta) {
        this.id = id;

        this.studentIds = studentMeta.id;
        this.parentIds = parentMeta.id;
        this.isMale = parentMeta.isMale;

        this.simpleId = id;

    }

    @Override
    public String toString() {
        return "StudentParentAssociationMeta [id=" + id +  ", + studentId=" + studentIds  +  ", + parentMeta=" + parentIds + ", + simpleId=" + simpleId + "]";
    }

}
