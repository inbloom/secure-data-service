package org.slc.sli.modeling.uml2Xsd;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.HasName;
import org.slc.sli.modeling.uml.Occurs;

/**
 * Used to synthesize a name from an association end that does not have a name.
 */
final class Uml2XsdSyntheticHasName implements HasName {
    
    private final AssociationEnd end;
    
    public Uml2XsdSyntheticHasName(final AssociationEnd end) {
        this.end = end;
    }
    
    @Override
    public QName getName() {
        final Occurs upperBound = end.getMultiplicity().getRange().getUpper();
        final boolean plural = Occurs.UNBOUNDED.equals(upperBound);
        return adjustPlurality(Uml2XsdTools.camelCase(end.getType().getName()), plural);
    }
    
    private static final QName adjustPlurality(final QName name, final boolean plural) {
        if (plural) {
            final String text = name.getLocalPart();
            return new QName(text.concat("s"));
        } else {
            return name;
        }
    }
}
