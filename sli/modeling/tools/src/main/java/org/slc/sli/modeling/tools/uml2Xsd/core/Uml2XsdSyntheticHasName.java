package org.slc.sli.modeling.tools.uml2Xsd.core;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.HasName;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Used to synthesize a name from an association end that does not have a name.
 */
public final class Uml2XsdSyntheticHasName implements HasName {

    private final AssociationEnd end;
    private final ModelIndex lookup;

    public Uml2XsdSyntheticHasName(final AssociationEnd end, final ModelIndex lookup) {
        this.end = end;
        this.lookup = lookup;
    }

    @Override
    public String getName() {
        final Occurs upperBound = end.getMultiplicity().getRange().getUpper();
        final boolean plural = Occurs.UNBOUNDED.equals(upperBound);
        return adjustPlurality(Uml2XsdTools.camelCase(lookup.getType(end.getType()).getName()), plural);
    }

    private static final String adjustPlurality(final String name, final boolean plural) {
        if (plural) {
            return name.concat("s");
        } else {
            return name;
        }
    }
}
