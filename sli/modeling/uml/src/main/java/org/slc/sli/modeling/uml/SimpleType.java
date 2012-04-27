package org.slc.sli.modeling.uml;

import java.util.List;

/**
 * A simple type is a super-type of either a data-type or an enumeration.
 */
public interface SimpleType extends Type {

    List<EnumLiteral> getLiterals();

}
