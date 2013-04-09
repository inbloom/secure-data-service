package org.slc.sli.domain;

public class CascadeResultError {

    public static enum ErrorType {
        ACCESS_DENIED,
        DELETE_ERROR,
        UPDATE_ERROR,
        PATCH_ERROR,
        DATABASE_ERROR,
        CHILD_DATA_EXISTS,
        MAX_DEPTH_EXCEEDED
    }

    int depth;
    ErrorType errorType;
    private String message;          // Status message e.g. error text - null on SUCCESS

    // Used to identify the error object for ACCESS_DENIED and CHILD_DATA_EXISTS status
    private String objectType;
    private String objectId;

    public CascadeResultError(int depth, String message, ErrorType errorType, String objectType, String objectId) {
        this.depth = depth;
        this.message = message;
        this.errorType = errorType;
        this.objectType = objectType;
        this.objectId = objectId;
    }

    public int getDepth() {
        return depth;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getMessage() {
        return message;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    /**
     * Intended only for debugging.
     *
     * <P>Here, the contents of every field are placed into the result, with
     * one field per line.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
        result.append(" depth      : " + depth + NEW_LINE);
        result.append(" errorType  : " + errorType + NEW_LINE);
        result.append(" objectType : " + objectType + NEW_LINE );
        result.append(" objectid   : " + objectId + NEW_LINE);
        result.append(" message    : " + message + NEW_LINE);
        result.append("}");

        return result.toString();
    }

}