package org.slc.sli.dal.lifecycle;

/**
 * Model exception.
 *
 * @author Robert Bloh <rbloh@wgen.net>
 *
 */

public class ModelException extends Exception {

    private static final long serialVersionUID = -4807783627215371132L;

    // Constructors
    public ModelException() {
    }

    public ModelException(String message) {
        super(message);
    }

    public ModelException(Throwable cause) {
        super(cause);
    }

    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }

}
