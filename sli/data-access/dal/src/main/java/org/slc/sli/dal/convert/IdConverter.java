package org.slc.sli.dal.convert;

/**
 * Provides an interface to convert database IDs into Strings that the Entity instances can use.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
public interface IdConverter {
    
    /**
     * Converts the given String ID into an ID suitable for persisting in the datastore.
     * @param id
     * @return
     */
    public Object toDatabaseId(String id);
    
    /**
     * Converts the given ID into a String.
     * @param id
     * @return
     */
    public String fromDatabaseId(Object id);
}
