package org.slc.sli.api.security.context.traversal.graph;


/**
 * Basic wrapper class for fields that a connection has
 * 
 * @author rlatta
 * 
 */
public class SecurityNodeConnection {
    private String fieldName = "";
    private String connectionTo = "";
    private String associationNode = "";
    
    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * @param fieldName
     *            the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    /**
     * @return the connectionTo
     */
    public String getConnectionTo() {
        return connectionTo;
    }
    
    /**
     * @param connectionTo
     *            the connectionTo to set
     */
    public void setConnectionTo(String connectionTo) {
        this.connectionTo = connectionTo;
    }
    
    /**
     * @return the associationNode
     */
    public String getAssociationNode() {
        return associationNode;
    }
    
    /**
     * @param associationNode
     *            the associationNode to set
     */
    public void setAssociationNode(String associationNode) {
        this.associationNode = associationNode;
    }
    public SecurityNodeConnection() {
    }
    
    public SecurityNodeConnection(String connectionTo, String fieldName, String associationNode) {
        this.connectionTo = connectionTo;
        this.fieldName = fieldName;
        this.associationNode = associationNode;
    }
}