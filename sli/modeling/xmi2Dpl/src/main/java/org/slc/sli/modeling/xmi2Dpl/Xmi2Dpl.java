package org.slc.sli.modeling.xmi2Dpl;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;

import org.slc.sli.modeling.uml.Association;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for converting XMI to Diagrammatic Predicate Logic (or maybe just documentation :).
 */
public final class Xmi2Dpl {
    
    public static void main(final String[] args) {
        try {
            final Model model = XmiReader.readModel("../data/SLI.xmi");
            final boolean showClassTypes = true;
            final boolean showDataTypes = true;
            final boolean showEnumTypes = true;
            final boolean showAssociations = true;
            final boolean showGeneralizations = true;
            final boolean showTagDefinitions = true;
            if (showClassTypes) {
                final Map<Identifier, ClassType> classTypeMap = model.getClassTypeMap();
                final Collection<ClassType> classTypes = classTypeMap.values();
                for (final ClassType classType : classTypes) {
                    System.out.println(classType);
                }
            }
            if (showDataTypes) {
                final Map<Identifier, DataType> dataTypeMap = model.getDataTypeMap();
                final Collection<DataType> dataTypes = dataTypeMap.values();
                for (final DataType dataType : dataTypes) {
                    System.out.println(dataType);
                }
            }
            if (showEnumTypes) {
                final Map<Identifier, EnumType> enumTypeMap = model.getEnumTypeMap();
                final Collection<EnumType> enumTypes = enumTypeMap.values();
                for (final EnumType enumType : enumTypes) {
                    System.out.println(enumType);
                }
            }
            if (showAssociations) {
                final Map<Identifier, Association> associationMap = model.getAssociationMap();
                final Collection<Association> associations = associationMap.values();
                for (final Association association : associations) {
                    System.out.println(association);
                }
            }
            if (showGeneralizations) {
                final Map<Identifier, Generalization> generalizationMap = model.getGeneralizationMap();
                final Collection<Generalization> generalizations = generalizationMap.values();
                for (final Generalization generalization : generalizations) {
                    System.out.println(generalization);
                }
            }
            if (showTagDefinitions) {
                final Map<Identifier, TagDefinition> tagDefinitionMap = model.getTagDefinitionMap();
                final Collection<TagDefinition> tagDefinitions = tagDefinitionMap.values();
                for (final TagDefinition tagDefinition : tagDefinitions) {
                    System.out.println(tagDefinition);
                }
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private Xmi2Dpl() {
        // Prevent instantiation, even through reflection.
        throw new RuntimeException();
    }
}
