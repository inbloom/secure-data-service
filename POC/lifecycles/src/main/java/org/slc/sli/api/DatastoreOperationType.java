package org.slc.sli.api;


/**
 * Datastore operation types
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 */
public enum DatastoreOperationType {
    
    CREATE("Create"),
    READ("Read"),
    UPDATE("Update"),
    DELETE("Delete"),
    CREATE_LIST("CreateList"),
    READ_LIST("ReadList"),
    UPDATE_LIST("UpdateList"),
    DELETE_LIST("DeleteList");
    
    private String value;
    
    private DatastoreOperationType(String value) {
        this.value = value;
    }
    
    public String value() {
        return this.value;
    }
    
}
