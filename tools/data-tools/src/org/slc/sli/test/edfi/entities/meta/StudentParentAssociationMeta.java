/**
 *
 */
package org.slc.sli.test.edfi.entities.meta;



/**
 * @author lchen
 *
 */
public class StudentParentAssociationMeta {

    public final String id;
    public final String studentIds;
    public final String parentIds;
    public final boolean isMale;

    public StudentParentAssociationMeta (String id, StudentMeta studentMeta, ParentMeta parentMeta) {
        this.id = id;

        this.studentIds = studentMeta.id;
        this.parentIds = parentMeta.id;
        this.isMale = parentMeta.isMale;

    }

    @Override
    public String toString() {
        return "StudentParentAssociationMeta [id=" + id +  ", + studentId=" + studentIds  +  ", + parentMeta=" + parentIds + "]";
    }

}
