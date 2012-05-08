package org.slc.sli.modeling.uml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The collection of all entities making up the unified model
 */
public final class Model {
    
    private final Map<Identifier, ClassType> classTypes;
    private final Map<Identifier, DataType> dataTypes;
    private final Map<Identifier, EnumType> enumTypes;
    private final Map<Identifier, Association> associations;
    private final Map<Identifier, TagDefinition> tagDefinitions;
    
    public Model(final Map<Identifier, ClassType> classTypes, final Map<Identifier, DataType> dataTypes,
            final Map<Identifier, EnumType> enumTypes, final Map<Identifier, Association> associations,
            final Map<Identifier, TagDefinition> tagDefinitions) {
        if (classTypes == null) {
            throw new NullPointerException("classTypes");
        }
        if (dataTypes == null) {
            throw new NullPointerException("dataTypes");
        }
        if (enumTypes == null) {
            throw new NullPointerException("enumTypes");
        }
        if (associations == null) {
            throw new NullPointerException("associations");
        }
        if (tagDefinitions == null) {
            throw new NullPointerException("tagDefinitions");
        }
        this.classTypes = Collections.unmodifiableMap(new HashMap<Identifier, ClassType>(classTypes));
        this.dataTypes = Collections.unmodifiableMap(new HashMap<Identifier, DataType>(dataTypes));
        this.enumTypes = Collections.unmodifiableMap(new HashMap<Identifier, EnumType>(enumTypes));
        this.associations = Collections.unmodifiableMap(new HashMap<Identifier, Association>(associations));
        this.tagDefinitions = Collections.unmodifiableMap(new HashMap<Identifier, TagDefinition>(tagDefinitions));
    }
    
    public Map<Identifier, ClassType> getClassTypeMap() {
        return classTypes;
    }
    
    public Map<Identifier, DataType> getDataTypeMap() {
        return dataTypes;
    }
    
    public Map<Identifier, EnumType> getEnumTypeMap() {
        return enumTypes;
    }
    
    public Map<Identifier, Association> getAssociationMap() {
        return associations;
    }
    
    public Map<Identifier, TagDefinition> getTagDefinitionMap() {
        return tagDefinitions;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof Model) {
            final Model that = (Model) other;
            return (this.classTypes.equals(that.classTypes));
        } else {
            return false;
        }
    }
}
