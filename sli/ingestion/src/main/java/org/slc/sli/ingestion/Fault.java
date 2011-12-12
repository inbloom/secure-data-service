package org.slc.sli.ingestion;

public class Fault {

    protected static final int TYPE_WARNING = 1;
    protected static final int TYPE_ERROR = 2;

    protected String message;
    protected int type;

    protected Fault(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public static Fault createWarning(String message) {
        return new Fault(TYPE_WARNING, message);
    }

    public static Fault createError(String message) {
        return new Fault(TYPE_ERROR, message);
    }

    public String getMessage() {
        return message;
    }

    public boolean isWarning() {
        return this.type == TYPE_WARNING;
    }

    public boolean isError() {
        return this.type == TYPE_ERROR;
    }

    @Override
    public String toString() {
        String typeString = (isWarning() ? "WARNING" : "ERROR");
        return typeString + ": " + getMessage();
    }
}