package org.slc.sli.api.security.context.traversal.graph;


import java.util.ArrayList;

/**
 * Basic wrapper class for fields that a connection has
 *
 * @author rlatta
 */
public class SecurityNodeConnection {
    private String fieldName = "";
    private String connectionTo = "";
    private String associationNode = "";
    private boolean isReferenceInSelf = false;
    private ArrayList<NodeFilter> filter;

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName the fieldName to set
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
     * @param connectionTo the connectionTo to set
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


    public ArrayList<NodeFilter> getFilter() {
        return this.filter;
    }

    public boolean isReferenceInSelf() {
        return isReferenceInSelf;
    }

    /**
     * @param associationNode the associationNode to set
     */
    public void setAssociationNode(String associationNode) {
        this.associationNode = associationNode;
    }

    public SecurityNodeConnection(String toEntity, String withField, String associationNode, NodeFilter filter) {
        this.connectionTo = toEntity;
        this.fieldName = withField;
        this.associationNode = associationNode;
        this.filter=new ArrayList<NodeFilter>();
        this.filter.add(filter);
    }
    public SecurityNodeConnection(String toEntity, String withField, String associationNode, ArrayList<NodeFilter> filter) {
        this.connectionTo = toEntity;
        this.fieldName = withField;
        this.associationNode = associationNode;
        this.filter=filter;
    }

    public SecurityNodeConnection(String connectionTo, String fieldName, String associationNode) {
        this.connectionTo = connectionTo;
        this.fieldName = fieldName;
        this.associationNode = associationNode;
        this.filter=null;
    }

    public SecurityNodeConnection(String connectionTo, String fieldName) {
        this.connectionTo = connectionTo;
        this.fieldName = fieldName;
    }

    public SecurityNodeConnection(String connectionTo, String fieldName, boolean isReferenceInSelf) {
        this.connectionTo = connectionTo;
        this.fieldName = fieldName;
        this.isReferenceInSelf = isReferenceInSelf;
    }

}