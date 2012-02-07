package org.slc.sli.entity;

/**
 *
 * @author svankina
 *TODO: javadoc
 */
public class ResponseObject {

    String id;
    InnerResponse[] links;

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public InnerResponse[] getLinks() {
        return links;
    }


    public void setLinks(InnerResponse[] links) {
        this.links = links;
    }





//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getHref() {
//        return link.getHref();
//    }
//
//    public String getRel() {
//        return link.getRel();
//    }
public static void main(String[] args) {
    String str = "https://devapp1.slidev.org/api/rest/teachers/00000000-0000-0000-0000-000000000600";
    int index = str.lastIndexOf("/");

    System.out.println(str.substring(index + 1));
}
}
