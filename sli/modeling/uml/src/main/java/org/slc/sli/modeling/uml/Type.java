package org.slc.sli.modeling.uml;

/**
 * A type in the system. This could be a class, a data-type or an enumeration.
 */
public interface Type extends HasIdentity, HasName, HasModifiers, Taggable {

    boolean isClassType();

    boolean isEnumType();

    boolean isDataType();
}
