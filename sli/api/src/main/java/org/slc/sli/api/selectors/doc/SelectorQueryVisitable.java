package org.slc.sli.api.selectors.doc;

/**
 * Visitable interface for building queries
 *
 * @author srupasinghe
 *
 */
public interface SelectorQueryVisitable {

    public SelectorQuery accept(SelectorQueryVisitor selectorQueryVisitor);

}
