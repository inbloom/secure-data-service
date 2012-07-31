package org.slc.sli.api.selectors.doc;


import org.slc.sli.api.selectors.model.BooleanSelectorElement;
import org.slc.sli.api.selectors.model.ComplexSelectorElement;
import org.slc.sli.api.selectors.model.IncludeAllSelectorElement;
import org.slc.sli.api.selectors.model.SemanticSelector;

/**
 * Visitor for building queries
 *
 * @author srupasinghe
 *
 */
public interface SelectorQueryVisitor {

    public SelectorQuery visit(SemanticSelector semanticSelector);

    public SelectorQuery visit(BooleanSelectorElement booleanSelectorElement);

    public SelectorQuery visit(ComplexSelectorElement complexSelectorElement);

    public SelectorQuery visit(IncludeAllSelectorElement includeAllSelectorElement);

}
