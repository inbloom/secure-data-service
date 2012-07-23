package org.slc.sli.api.selectors.model;

import org.slc.sli.api.selectors.model.ModelElementNotFoundException;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides information from the model about Associations and Attributes
 * @author jstokes
 */

@Component
public final class ModelProvider {
    private final ModelIndex modelIndex;
    private final static String DEFAULT_XMI_LOC = "/sliModel/SLI.xmi";

    public ModelProvider(final String XMI_LOC) {
        final Model model = XmiReader.readModel(getClass().getResourceAsStream(XMI_LOC));
        modelIndex = new DefaultModelIndex(model);
    }

    public ModelProvider() {
        this(DEFAULT_XMI_LOC);
    }

    public ModelProvider(final ModelIndex modelIndex) {
        this.modelIndex = modelIndex;
    }

    public List<AssociationEnd> getAssociationEnds(final Identifier type) {
        return modelIndex.getAssociationEnds(type);
    }

    public Set<ModelElement> lookupByName(final QName qName) {
        return modelIndex.lookupByName(qName);
    }

    public Map<String, ClassType> getClassTypes() {
        return modelIndex.getClassTypes();
    }

    public TagDefinition getTagDefinition(final Identifier id) {
        return modelIndex.getTagDefinition(id);
    }

    public Type getType(final Identifier id) {
        return modelIndex.getType(id);
    }

    public ClassType lookupSingleModelElement(final QName qName)
            throws ModelElementNotFoundException {
        final Set<ModelElement> elementSet = lookupByName(qName);
        if (elementSet.size() == 1) {
            final ModelElement rVal = elementSet.iterator().next();
            if (rVal instanceof ClassType) return (ClassType) rVal;
            else throw new AssertionError();
        } else {
            error("Element {} was not found", qName);
            throw new ModelElementNotFoundException();
        }
    }
}
