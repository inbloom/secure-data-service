/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.modeling.tools.xsdgen;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.HasName;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Used to synthesize a name from an association end that does not have a name.
 */
final class Uml2XsdSyntheticHasName implements HasName {

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
