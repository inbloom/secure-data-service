package org.slc.sli.modeling.tools.uml2Xsd.cmdline;

import static org.slc.sli.modeling.xml.XmlTools.collapseWhitespace;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.tools.SliMongoConstants;
import org.slc.sli.modeling.tools.SliUmlConstants;
import org.slc.sli.modeling.tools.TagName;
import org.slc.sli.modeling.tools.uml2Xsd.core.Uml2XsdPlugin;
import org.slc.sli.modeling.tools.uml2Xsd.core.Uml2XsdPluginWriter;
import org.slc.sli.modeling.tools.uml2Xsd.core.Uml2XsdTools;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.helpers.TaggedValueHelper;
import org.slc.sli.modeling.uml.index.ModelIndex;

final class PluginForMongo implements Uml2XsdPlugin {

    /**
     * This is the type that is used whenever we have just a reference.
     */
    private static final String MONGO_REFERENCE_TYPE = "Reference";

    @SuppressWarnings("unused")
    private static final String camelCase(final String text) {
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }

    @SuppressWarnings("unused")
    private static final String titleCase(final String text) {
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }

    /**
     * Not all association ends have names so we synthesize a name based upon the type.
     */
    private static final String getName(final AssociationEnd end, final ModelIndex lookup) {
        if (!end.getName().trim().isEmpty()) {
            // If a name has been specified explicitly, use it without change.
            return end.getName();
        } else {
            // Synthesize the name using the and accounting for naive plurality.
            final Occurs maxOccurs = end.getMultiplicity().getRange().getUpper();
            final boolean unbounded = isUnbounded(maxOccurs);
            final String name = Uml2XsdTools.camelCase(lookup.getType(end.getType()).getName());
            return name.concat(unbounded ? "Ids" : "Id");
        }
    }

    private static final boolean isUnbounded(final Occurs occurs) {
        return occurs.equals(Occurs.UNBOUNDED);
    }

    @Override
    public Map<String, String> declarePrefixMappings() {
        final Map<String, String> pms = new HashMap<String, String>();
        pms.put("sli", SliMongoConstants.NAMESPACE_SLI);
        return Collections.unmodifiableMap(pms);
    }

    @Override
    public QName getElementName(final PsmDocument<Type> classType) {
        return new QName(classType.getResource().getName());
    }

    @Override
    public QName getElementName(final String name, final boolean isReference) {
        // camel case is the convention for JSON.
        return new QName(Uml2XsdTools.camelCase(name));
    }

    @Override
    public QName getElementType(final String name, final boolean isAssociation) {
        return getTypeName(name);
    }

    @Override
    public QName getTypeName(final String name) {
        return new QName(name);
    }

    @Override
    public boolean isEnabled(final QName name) {
        return false;
    }

    @Override
    public void writeAppInfo(final TaggedValue taggedValue, final ModelIndex lookup, final Uml2XsdPluginWriter xsw) {
        final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
        xsw.appinfo();
        {
            if (SliUmlConstants.TAGDEF_NATURAL_KEY.equals(tagDefinition.getName())) {
                xsw.begin("sli", SliMongoConstants.SLI_NATURAL_KEY.getLocalPart(),
                        SliMongoConstants.SLI_NATURAL_KEY.getNamespaceURI());
                xsw.end();
            } else if (SliUmlConstants.TAGDEF_PII.equals(tagDefinition.getName())) {
                xsw.begin("sli", SliMongoConstants.SLI_PII.getLocalPart(), SliMongoConstants.SLI_PII.getNamespaceURI());
                xsw.characters(taggedValue.getValue());
                xsw.end();
            } else if (SliUmlConstants.TAGDEF_ENFORCE_READ.equals(tagDefinition.getName())) {
                xsw.begin("sli", SliMongoConstants.SLI_READ_ENFORCEMENT.getLocalPart(),
                        SliMongoConstants.SLI_READ_ENFORCEMENT.getNamespaceURI());
                xsw.characters(taggedValue.getValue());
                xsw.end();
            } else if (SliUmlConstants.TAGDEF_ENFORCE_WRITE.equals(tagDefinition.getName())) {
                xsw.begin("sli", SliMongoConstants.SLI_WRITE_ENFORCEMENT.getLocalPart(),
                        SliMongoConstants.SLI_WRITE_ENFORCEMENT.getNamespaceURI());
                xsw.characters(taggedValue.getValue());
                xsw.end();
            } else if (SliUmlConstants.TAGDEF_RELAXED_BLACKLIST.equals(tagDefinition.getName())) {
                xsw.begin("sli", SliMongoConstants.SLI_RELAXEDBLACKLIST.getLocalPart(),
                        SliMongoConstants.SLI_RELAXEDBLACKLIST.getNamespaceURI());
                xsw.characters(taggedValue.getValue());
                xsw.end();
            } else if (SliUmlConstants.TAGDEF_SECURITY_SPHERE.equals(tagDefinition.getName())) {
                xsw.begin("sli", SliMongoConstants.SLI_SECURITY_SPHERE.getLocalPart(),
                        SliMongoConstants.SLI_SECURITY_SPHERE.getNamespaceURI());
                xsw.characters(taggedValue.getValue());
                xsw.end();
            } else {
                throw new AssertionError(tagDefinition.getName());
            }
        }
        xsw.end();
    }

    private static final boolean isMongoNavigable(final AssociationEnd element, final ModelIndex lookup) {
        return TaggedValueHelper.getBooleanTag(TagName.MONGO_NAVIGABLE, element, lookup, false);
    }

    private static final boolean hasMongoName(final Feature feature, final ModelIndex lookup) {
        return TaggedValueHelper.hasTag(TagName.MONGO_NAME, feature, lookup);
    }

    private static final String getMongoName(final Feature feature, final ModelIndex lookup) {
        return TaggedValueHelper.getStringTag(TagName.MONGO_NAME, feature, lookup, null);
    }

    @Override
    public void writeAssociation(final ClassType complexType, final AssociationEnd end, final ModelIndex lookup,
            final Uml2XsdPluginWriter xsw) {
        if (isMongoNavigable(end, lookup)) {
            final Type type = lookup.getType(end.getType());
            final Occurs minOccurs = end.getMultiplicity().getRange().getLower();
            final Occurs maxOccurs = end.getMultiplicity().getRange().getUpper();
            xsw.element();
            if (hasMongoName(end, lookup)) {
                xsw.name(new QName(getMongoName(end, lookup)));
            } else {
                xsw.name(new QName(getName(end, lookup)));
            }
            xsw.type(getTypeName(MONGO_REFERENCE_TYPE));
            // xsw.type(getTypeName(type.getName()));
            xsw.minOccurs(minOccurs);
            xsw.maxOccurs(maxOccurs);
            {
                xsw.annotation();
                {
                    for (final TaggedValue taggedValue : end.getTaggedValues()) {
                        final TagDefinition tagDefinition = lookup.getTagDefinition(taggedValue.getTagDefinition());
                        if (TagName.DOCUMENTATION.equals(tagDefinition.getName())) {
                            xsw.documentation();
                            xsw.characters(collapseWhitespace(taggedValue.getValue()));
                            xsw.end();
                        } else if (SliUmlConstants.TAGDEF_REFERENCE.equals(tagDefinition.getName())) {
                            // Ignore, or should we used the code below?
                        } else if (TagName.MONGO_NAME.equals(tagDefinition.getName())) {
                            // Ignore.
                        } else if (TagName.MONGO_NAVIGABLE.equals(tagDefinition.getName())) {
                            // Ignore.
                        } else {
                            throw new AssertionError(tagDefinition.getName());
                        }
                    }
                    xsw.appinfo();
                    {
                        xsw.begin("sli", SliMongoConstants.SLI_REFERENCE_TYPE.getLocalPart(),
                                SliMongoConstants.SLI_REFERENCE_TYPE.getNamespaceURI());
                        xsw.characters(getTypeName(type.getName()).getLocalPart());
                        xsw.end();
                    }
                    xsw.end();
                }
                xsw.end();
            }
            xsw.end();
        }
    }
}
