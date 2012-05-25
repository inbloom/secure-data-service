package org.slc.sli.modeling.tools.uml2Xsd.cmdline;

import java.util.Collections;
import java.util.Map;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.tools.uml2Xsd.core.Uml2XsdPlugin;
import org.slc.sli.modeling.tools.uml2Xsd.core.Uml2XsdPluginWriter;
import org.slc.sli.modeling.tools.uml2Xsd.core.Uml2XsdSyntheticHasName;
import org.slc.sli.modeling.tools.uml2Xsd.core.Uml2XsdTools;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xsd.WxsNamespace;

final class PluginForREST implements Uml2XsdPlugin {

    /**
     * Not all association ends have names so we synthesize a name based upon the type.
     */
    private static final String getName(final AssociationEnd element, final ModelIndex lookup) {
        if (!element.getName().trim().isEmpty()) {
            return element.getName();
        } else {
            // Name using the element type. Could be more sophisticated here.
            return new Uml2XsdSyntheticHasName(element, lookup).getName();
        }
    }

    private static final QName getQName(final Type type, final ModelIndex lookup) {
        final Identifier id = type.getId();
        for (final ModelElement whereUsed : lookup.whereUsed(id)) {
            if (whereUsed instanceof UmlPackage) {
                final UmlPackage pkg = (UmlPackage) whereUsed;
                return new QName(pkg.getName(), type.getName());
            }
        }
        return new QName(type.getName());
    }

    @Override
    public Map<String, String> declarePrefixMappings() {
        return Collections.emptyMap();
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
    public QName getPluralTopLevelElementName(final PsmDocument<Type> classType) {
        return new QName(classType.getPluralResourceName().getName());
    }

    @Override
    public QName getSingularTopLevelElementName(final PsmDocument<Type> classType) {
        return new QName(classType.getSingularResourceName().getName());
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
        // Ignore
    }

    public void writeReference(final ClassType complexType, final AssociationEnd end, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        xsw.element();
        try {
            xsw.name(new QName(PluginHelpers.getMongoName(end, model)));
            xsw.type(WxsNamespace.STRING);
            xsw.minOccurs(end.getMultiplicity().getRange().getLower());
            xsw.maxOccurs(end.getMultiplicity().getRange().getUpper());
            {
                xsw.annotation();
                try {
                    PluginHelpers.writeDocumentation(end, model, xsw);
                } finally {
                    xsw.end();
                }
            }
        } finally {
            xsw.end();
        }
    }

    public void writeEmbedded(final ClassType complexType, final AssociationEnd end, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        xsw.element();
        try {
            final String name = getName(end, model);
            final Type type = model.getType(end.getType());
            xsw.name(new QName(name));
            xsw.type(getTypeName(type.getName()));
            xsw.minOccurs(end.getMultiplicity().getRange().getLower());
            xsw.maxOccurs(end.getMultiplicity().getRange().getUpper());
            {
                xsw.annotation();
                try {
                    PluginHelpers.writeDocumentation(end, model, xsw);
                } finally {
                    xsw.end();
                }
            }
        } finally {
            xsw.end();
        }
    }

    @Override
    public void writeAssociation(final ClassType complexType, final AssociationEnd end, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        // The existence of this feature depends on whether the association is logically navigable.
        if (end.isNavigable()) {
            if (PluginHelpers.hasMongoName(end, model)) {
                xsw.choice();
                try {
                    writeReference(complexType, end, model, xsw);
                    writeEmbedded(complexType, end, model, xsw);
                } finally {
                    xsw.end();
                }
            } else {
                writeEmbedded(complexType, end, model, xsw);
            }
        }
    }

    @Override
    public void writeTopLevelElement(final PsmDocument<Type> classType, final ModelIndex model,
            final Uml2XsdPluginWriter xsw) {
        xsw.element();
        try {
            xsw.name(getPluralTopLevelElementName(classType));
            final Type elementType = classType.getType();
            xsw.annotation();
            try {
                PluginHelpers.writeDocumentation(elementType, model, xsw);
            } finally {
                xsw.end();
            }
            xsw.complexType();
            try {
                xsw.sequence();
                try {
                    xsw.element();
                    try {
                        xsw.name(getSingularTopLevelElementName(classType));
                        xsw.type(getQName(elementType, model));
                        xsw.minOccurs(Occurs.ZERO);
                        xsw.maxOccurs(Occurs.UNBOUNDED);
                    } finally {
                        xsw.end();
                    }
                } finally {
                    xsw.end();
                }
            } finally {
                xsw.end();
            }
        } finally {
            xsw.end();
        }
    }
}
