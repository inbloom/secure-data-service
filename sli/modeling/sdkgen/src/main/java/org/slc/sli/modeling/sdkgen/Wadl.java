package org.slc.sli.modeling.sdkgen;

import org.slc.sli.modeling.rest.Application;

/**
 * Maintains the relationship between a model and where it was located in order to resolve
 * references.
 */
public final class Wadl<T> {

    private final Application application;
    private final T source;

    public Wadl(final Application application, final T source) {
        if (application == null) {
            throw new NullPointerException("application");
        }
        if (source == null) {
            throw new NullPointerException("source");
        }
        this.application = application;
        this.source = source;

    }

    public Application getApplication() {
        return application;
    }

    public T getSource() {
        return source;
    }
}
