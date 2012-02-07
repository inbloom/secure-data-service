package org.slc.sli.entity;

/**
 *
 * @author svankina
 *         TODO: javadoc
 */
public class AssociationResponseObject {

    String id;
    InnerResponse link;

    public InnerResponse getLink() {
        return link;
    }

    public void setLink(InnerResponse link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
