package org.slc.sli.dal.convert;



/**
 * Holds common logic around superdoc IDs.
 * 
 */
public class SuperDocIdUtility {
    
    /**
     * Figures out the parent superdoc ID from the subdoc's id.
     * This should be injected, but for some reason all the superdoc stuff is not spring managed.
     */
    public static String getParentId(String embededId) {
        String parentId = embededId;
        if (embededId.split("_id").length == 2) {
            parentId = embededId.split("_id")[0] + "_id";
        }
        return parentId;
    }
    
}
