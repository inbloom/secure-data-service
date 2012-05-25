package org.slc.sli.modeling.tools.uml2Xsd.cmdline;

import static org.slc.sli.modeling.xml.XmlTools.collapseWhitespace;

import org.slc.sli.modeling.psm.helpers.TagName;
import org.slc.sli.modeling.tools.uml2Xsd.core.Uml2XsdPluginWriter;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.helpers.TaggedValueHelper;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Common functions for the WXS generation plug-ins.
 */
public final class PluginHelpers {

    /**
     * Writes the <code>documentation</code> element for a tagged UML model element.
     */
    public static final void writeDocumentation(final Taggable taggable, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        for (final TaggedValue taggedValue : taggable.getTaggedValues()) {
            final TagDefinition tagDefinition = model.getTagDefinition(taggedValue.getTagDefinition());
            if (TagName.DOCUMENTATION.equals(tagDefinition.getName())) {
                xsw.documentation();
                try {
                    xsw.characters(collapseWhitespace(taggedValue.getValue()));
                } finally {
                    xsw.end();
                }
            }
        }
    }

    /**
     * Determines whether the association is navigable at the database level. A navigable
     * association
     * end at the database level has a field. Note that an association may be logically navigable
     * even if there is no corresponding database field.
     */
    public static final boolean isMongoNavigable(final AssociationEnd associationEnd, final ModelIndex model) {
        return TaggedValueHelper.getBooleanTag(TagName.MONGO_NAVIGABLE, associationEnd, model, false);
    }

    /**
     * Determines whether the {@link Feature} has a database name override.
     */
    public static final boolean hasMongoName(final Feature feature, final ModelIndex model) {
        return TaggedValueHelper.hasTag(TagName.MONGO_NAME, feature, model);
    }

    /**
     * Returns the database name override for the specified {@link Feature}.
     */
    public static final String getMongoName(final Feature feature, final ModelIndex model) {
        return TaggedValueHelper.getStringTag(TagName.MONGO_NAME, feature, model, null);
    }

}
