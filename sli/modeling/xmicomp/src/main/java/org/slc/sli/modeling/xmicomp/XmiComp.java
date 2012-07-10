package org.slc.sli.modeling.xmicomp;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A quick-n-dirty utility for converting W3C XML Schemas to XMI (with
 * limitations).
 * <p>
 * This is a command line utility. A typical invocation is as follows:
 * <code>--xsdFile SLI.xsd --xmiFile SLI.xmi --xmiFolder . --plugInName Xsd2UmlPlugInForSLI</code>
 * </p>
 */
public final class XmiComp {
    
    private static final List<String> ARG_HELP = asList("h", "?");
    private static final String ARG_MAP_FILE = "mapFile";
    private static final String ARG_LHS_XMI_FILE = "lhsFile";
    private static final String ARG_RHS_XMI_FILE = "rhsFile";
    private static final String ARG_OUT_FILE = "outFile";
    
    /**
     * This is the entry point for the command line interface.
     */
    public static void main(final String[] args) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<?> helpSpec = parser.acceptsAll(ARG_HELP, "Show help");
        final OptionSpec<File> mapFileSpec = optionSpec(parser, ARG_MAP_FILE, "Mapping (input) file", File.class);
        final OptionSpec<File> lhsFileSpec = optionSpec(parser, ARG_LHS_XMI_FILE, "LHS XMI (input) file", File.class);
        final OptionSpec<File> rhsFileSpec = optionSpec(parser, ARG_RHS_XMI_FILE, "RHS XMI (input) file", File.class);
        final OptionSpec<File> outFileSpec = optionSpec(parser, ARG_OUT_FILE, "Mapping (output) file", File.class);
        try {
            final OptionSet options = parser.parse(args);
            if (options.hasArgument(helpSpec)) {
                try {
                    parser.printHelpOn(System.out);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    final File mapFile = options.valueOf(mapFileSpec);
                    final File lhsFile = options.valueOf(lhsFileSpec);
                    final File rhsFile = options.valueOf(rhsFileSpec);
                    final File outFile = options.valueOf(outFileSpec);
                    
                    final XmiComparison original = XmiMappingReader.readDocument(mapFile);
                    final ModelIndex lhsModel = new DefaultModelIndex(XmiReader.readModel(lhsFile));
                    final ModelIndex rhsModel = new DefaultModelIndex(XmiReader.readModel(rhsFile));
                    
                    final String mergeComment = "";
                    final List<XmiMapping> mappings = mergeMappingDocument(original, lhsModel, rhsModel, mergeComment);
                    final XmiDefinition lhsDef = original.getLhsDef();
                    final XmiDefinition rhsDef = original.getRhsDef();
                    final XmiComparison revised = new XmiComparison(lhsDef, rhsDef, mappings);
                    // Write out the revised mapping document.
                    XmiMappingWriter.writeMappingDocument(revised, outFile);
                } catch (final FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (final OptionException e) {
            // Caused by illegal arguments.
            System.err.println(e.getMessage());
        }
    }
    
    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }
    
    private static final List<XmiMapping> mergeMappingDocument(final XmiComparison doc, final ModelIndex lhsModel,
            final ModelIndex rhsModel, final String mergeComment) {
        
        final List<XmiMapping> existing = checkDeclaredMappingConsistency(doc, lhsModel, rhsModel);
        
        final Map<CaseInsensitiveQName, XmiFeature> lhsMissed = XmiMappingHelper.missingFeatures(existing,
                doc.getLhsDef(), lhsModel, true);
        final Map<CaseInsensitiveQName, XmiFeature> rhsMissed = XmiMappingHelper.missingFeatures(existing,
                doc.getRhsDef(), rhsModel, false);
        
        return merge(lhsMissed, rhsMissed, existing, mergeComment);
    }
    
    private static final List<XmiMapping> merge(final Map<CaseInsensitiveQName, XmiFeature> lhsMissing,
            final Map<CaseInsensitiveQName, XmiFeature> rhsMissing, final List<XmiMapping> existing,
            final String mergeComment) {
        
        final Map<CaseInsensitiveQName, XmiFeature> lhsCopy = new HashMap<CaseInsensitiveQName, XmiFeature>(lhsMissing);
        final Map<CaseInsensitiveQName, XmiFeature> rhsCopy = new HashMap<CaseInsensitiveQName, XmiFeature>(rhsMissing);
        
        // Make a copy of the original mappings so that it can be mutated.
        final List<XmiMapping> existingCopy = new LinkedList<XmiMapping>(existing);
        // Merge missing features from each model into the new mappings.
        XmiMappingHelper.mergeMissingFeatures(lhsCopy, existingCopy, true, mergeComment);
        XmiMappingHelper.mergeMissingFeatures(rhsCopy, existingCopy, false, mergeComment);
        // What is left are the features that are not already in the existing mappings.
        // Some of these may be common and can therefore be matched.
        
        XmiMappingHelper.mergeCommonFeatures(lhsCopy, rhsCopy, existingCopy, mergeComment);
        
        XmiMappingHelper.mergeRemaining(lhsCopy, existingCopy, true, mergeComment);
        XmiMappingHelper.mergeRemaining(rhsCopy, existingCopy, false, mergeComment);
        
        // Verify that we accounted for everything.
        if (lhsCopy.size() > 0) {
            throw new IllegalStateException();
        }
        if (rhsCopy.size() > 0) {
            throw new IllegalStateException();
        }
        
        return Collections.unmodifiableList(existingCopy);
    }
    
    /**
     * Verify that the declared mappings are consistent with the UML models.
     */
    private static final List<XmiMapping> checkDeclaredMappingConsistency(final XmiComparison mappingDocument,
            final ModelIndex lhsModel, final ModelIndex rhsModel) {
        // Prepare case-insensitive lookup tables so that we don't have to be concerned about
        // case sensitivity for existence checking.
        final Map<CaseInsensitiveName, ClassType> lhsClassTypes = XmiMappingHelper.makeLowerCaseKey(lhsModel
                .getClassTypes());
        final Map<CaseInsensitiveName, ClassType> rhsClassTypes = XmiMappingHelper.makeLowerCaseKey(rhsModel
                .getClassTypes());
        final List<XmiMapping> mappings = mappingDocument.getMappings();
        final List<XmiMapping> revised = new ArrayList<XmiMapping>(mappings.size());
        for (final XmiMapping mapping : mappings) {
            checkMapping(mappingDocument, mapping, lhsModel, rhsModel, lhsClassTypes, rhsClassTypes);
        }
        return Collections.unmodifiableList(revised);
    }
    
    private static final XmiMapping checkMapping(final XmiComparison mappingDocument, final XmiMapping mapping,
            final ModelIndex lhsModel, final ModelIndex rhsModel,
            final Map<CaseInsensitiveName, ClassType> lhsClassTypes,
            final Map<CaseInsensitiveName, ClassType> rhsClassTypes) {
        if (mapping == null) {
            throw new NullPointerException("mapping");
        }
        final XmiMappingStatus status = mapping.getStatus();
        XmiFeature lhsFeature = mapping.getLhsFeature();
        if (lhsFeature != null) {
            lhsFeature = checkFeature(mappingDocument.getLhsDef(), lhsFeature, lhsModel, status, lhsClassTypes);
        }
        XmiFeature rhsFeature = mapping.getRhsFeature();
        if (rhsFeature != null) {
            rhsFeature = checkFeature(mappingDocument.getRhsDef(), rhsFeature, rhsModel, status, rhsClassTypes);
        }
        
        checkStatus(mapping);
        
        return new XmiMapping(lhsFeature, rhsFeature, mapping.getStatus(), mapping.getComment());
    }
    
    private static final Map<CaseInsensitiveName, Feature> computeFeatures(final ClassType classType,
            final ModelIndex model) {
        if (classType == null) {
            throw new NullPointerException("classType");
        }
        final Map<CaseInsensitiveName, Feature> features = new HashMap<CaseInsensitiveName, Feature>();
        for (final Attribute attribute : classType.getAttributes()) {
            features.put(new CaseInsensitiveName(attribute.getName()), attribute);
        }
        for (final AssociationEnd associationEnd : model.getAssociationEnds(classType.getId())) {
            features.put(new CaseInsensitiveName(associationEnd.getName()), associationEnd);
        }
        return features;
    }
    
    /**
     * Verify that every feature referenced in the mapping file actually exists
     * in the XMI model.
     */
    private static final XmiFeature checkFeature(final XmiDefinition context, final XmiFeature feature,
            final ModelIndex model, final XmiMappingStatus status, final Map<CaseInsensitiveName, ClassType> classTypes) {
        final CaseInsensitiveName type = new CaseInsensitiveName(feature.getType());
        if (classTypes.containsKey(type)) {
            final ClassType classType = classTypes.get(type);
            
            // FIXME: Horribly inefficient to recalculate this.
            final Map<CaseInsensitiveName, Feature> features = computeFeatures(classType, model);
            final CaseInsensitiveName name = new CaseInsensitiveName(feature.getName());
            if (features.containsKey(name)) {
                return new XmiFeature(features.get(name).getName(), classType.getName(), true);
            } else {
                System.err.println(feature.getType() + "." + feature.getName() + " in " + context.getName()
                        + " is not a recognized feature.");
                return new XmiFeature(feature.getName(), classType.getName(), true);
            }
        } else {
            System.err.println(feature.getType() + " in " + context.getName() + " is not a recognized class type.");
            return new XmiFeature(feature.getName(), feature.getType(), false);
        }
    }
    
    private static final void checkStatus(final XmiMapping mapping) {
        if (mapping == null) {
            throw new NullPointerException("mapping");
        }
        switch (mapping.getStatus()) {
            case MATCH: {
                if (mapping.getLhsFeature() == null) {
                    System.err.println("Inconsistent status for mapping : " + mapping);
                }
                if (mapping.getRhsFeature() == null) {
                    System.err.println("Inconsistent status for mapping : " + mapping);
                }
            }
        }
        
    }
}
