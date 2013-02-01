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

package org.slc.sli.modeling.xmi.comp;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A quick-n-dirty utility for converting W3C XML Schemas to XMI (with
 * limitations).
 * <p>
 * This is a command line utility. A typical invocation is as follows:
 * <code>--xsdFile SLI.xsd --xmiFile SLI.xmi --xmiFolder . --plugInName Xsd2UmlPlugInForSLI</code>
 * </p>
 */
public final class XmiComp {

    private static final Logger LOG = LoggerFactory.getLogger(XmiComp.class);

    /**
     * Class name suffix that means class is used for establishing identity.
     */
    private static final String OWNER_NAME_SUFFIX_IDENTITY_TYPE = "IdentityType";
    /**
     * Command Line Arguments.
     */
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
                    throw new XmiCompRuntimeException(e);
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
                    try {
                        final List<XmiMapping> mappings = mergeMappingDocument(original, lhsModel, rhsModel,
                                mergeComment);
                        final XmiDefinition lhsDef = original.getLhsDef();
                        final XmiDefinition rhsDef = original.getRhsDef();
                        final XmiComparison revised = new XmiComparison(lhsDef, rhsDef, mappings);
                        // Write out the revised mapping document.
                        XmiMappingWriter.writeMappingDocument(revised, outFile);
                    } catch (final XmiMappingException e) {
                        LOG.warn(e.getMessage());
                    }
                } catch (final FileNotFoundException e) {
                    throw new XmiCompRuntimeException(e);
                }
            }
        } catch (final OptionException e) {
            // Caused by illegal arguments.
            LOG.warn(e.getMessage());
        }
    }
    
    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }
    
    private static final List<XmiMapping> mergeMappingDocument(final XmiComparison doc, final ModelIndex lhsModel,
            final ModelIndex rhsModel, final String mergeComment) throws XmiMappingException {
        
        final List<XmiMapping> existing = checkDeclaredMappingConsistency(doc, lhsModel, rhsModel);
        
        final Map<CaseInsensitiveQName, XmiFeature> lhsMissed = XmiMappingHelper.missingFeatures(existing,
                doc.getLhsDef(), lhsModel, true);
        final Map<CaseInsensitiveQName, XmiFeature> rhsMissed = XmiMappingHelper.missingFeatures(existing,
                doc.getRhsDef(), rhsModel, false);
        
        return merge(lhsMissed, rhsMissed, existing, mergeComment);
    }
    
    private static final List<XmiMapping> merge(final Map<CaseInsensitiveQName, XmiFeature> lhsMissing,
            final Map<CaseInsensitiveQName, XmiFeature> rhsMissing, final List<XmiMapping> mappings,
            final String mergeComment) throws XmiMappingException {
        
        final List<XmiMapping> existingCopy = new LinkedList<XmiMapping>(mappings);
        
        final Map<CaseInsensitiveQName, XmiFeature> lhsCopy = new HashMap<CaseInsensitiveQName, XmiFeature>(lhsMissing);
        final Map<CaseInsensitiveQName, XmiFeature> rhsCopy = new HashMap<CaseInsensitiveQName, XmiFeature>(rhsMissing);
        
        // Make a copy of the original mappings so that it can be mutated.
        // Merge missing features from each model into the new mappings.
        XmiMappingHelper.mergeMissingFeatures(lhsCopy, existingCopy, true, mergeComment);
        XmiMappingHelper.mergeMissingFeatures(rhsCopy, existingCopy, false, mergeComment);
        // What is left are the features that are not already in the existing mappings.
        // Some of these may be common and can therefore be matched.
        
        XmiMappingHelper.commonMappings(existingCopy, lhsCopy, rhsCopy, mergeComment);
        
        XmiMappingHelper.appendRemaining(lhsCopy, existingCopy, true, mergeComment);
        XmiMappingHelper.appendRemaining(rhsCopy, existingCopy, false, mergeComment);
        
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
        final Map<CaseInsensitiveString, ClassType> lhsClassTypes = XmiMappingHelper.toCaseInsensitiveKey(lhsModel
                .getClassTypes());
        final Map<CaseInsensitiveString, ClassType> rhsClassTypes = XmiMappingHelper.toCaseInsensitiveKey(rhsModel
                .getClassTypes());
        final List<XmiMapping> mappings = mappingDocument.getMappings();
        final List<XmiMapping> revised = new ArrayList<XmiMapping>(mappings.size());
        for (final XmiMapping mapping : mappings) {
            final XmiMapping revisedMapping = checkMapping(mappingDocument, mapping, lhsModel, rhsModel, lhsClassTypes,
                    rhsClassTypes);
            if (revisedMapping != null) {
                revised.add(revisedMapping);
            } else {
                throw new AssertionError();
            }
        }
        return Collections.unmodifiableList(revised);
    }
    
    private static final XmiMapping checkMapping(final XmiComparison mappingDocument, final XmiMapping mapping,
            final ModelIndex lhsModel, final ModelIndex rhsModel,
            final Map<CaseInsensitiveString, ClassType> lhsClassTypes,
            final Map<CaseInsensitiveString, ClassType> rhsClassTypes) {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping");
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
        
        final XmiMappingStatus checkedStatus = checkStatus(mapping);
        
        return new XmiMapping(lhsFeature, rhsFeature, checkedStatus, mapping.getTracking(), mapping.getComment());
    }
    
    private static final Map<CaseInsensitiveString, Feature> computeFeatures(final ClassType classType,
            final ModelIndex model) {
        if (classType == null) {
            throw new IllegalArgumentException("classType");
        }
        final Map<CaseInsensitiveString, Feature> features = new HashMap<CaseInsensitiveString, Feature>();
        for (final Attribute attribute : classType.getAttributes()) {
            features.put(new CaseInsensitiveString(attribute.getName()), attribute);
        }
        for (final AssociationEnd associationEnd : model.getAssociationEnds(classType.getId())) {
            features.put(new CaseInsensitiveString(associationEnd.getName()), associationEnd);
        }
        return features;
    }
    
    /**
     * Verify that every feature referenced in the mapping file actually exists
     * in the XMI model.
     */
    private static final XmiFeature checkFeature(final XmiDefinition context, final XmiFeature mapFeature,
            final ModelIndex model, final XmiMappingStatus status,
            final Map<CaseInsensitiveString, ClassType> classTypes) {
        
        final String mapClassName = mapFeature.getOwnerName();
        final String mapFeatureName = mapFeature.getName();
        
        final CaseInsensitiveString type = new CaseInsensitiveString(mapClassName);
        if (classTypes.containsKey(type)) {
            final ClassType classType = classTypes.get(type);
            final String umlClassName = classType.getName();
            // Horribly inefficient to recalculate this.
            final Map<CaseInsensitiveString, Feature> features = computeFeatures(classType, model);
            final CaseInsensitiveString name = new CaseInsensitiveString(mapFeatureName);
            if (features.containsKey(name)) {
                final Feature umlFeature = features.get(name);
                final String umlFeatureName = umlFeature.getName();
                return new XmiFeature(umlFeatureName, true, umlClassName, true);
            } else {
                return new XmiFeature(mapFeatureName, false, umlClassName, true);
            }
        } else {
            return new XmiFeature(mapFeatureName, false, mapClassName, false);
        }
    }
    
    private static final XmiMappingStatus checkStatus(final XmiMapping mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping");
        }
        final XmiMappingStatus status = mapping.getStatus();
        switch (status) {
            case MATCH: {
                if (mapping.getLhsFeature() == null) {
                    LOG.warn("Inconsistent status for mapping : " + mapping);
                }
                if (mapping.getRhsFeature() == null) {
                    LOG.warn("Inconsistent status for mapping : " + mapping);
                }
                return status;
            }
            case UNKNOWN: {
                if (mapping.getLhsFeature() != null) {
                    final XmiFeature lhsFeature = mapping.getLhsFeature();
                    if (lhsFeature.getOwnerName().endsWith(OWNER_NAME_SUFFIX_IDENTITY_TYPE)) {
                        return XmiMappingStatus.IGNORABLE;
                    }
                }
                if (mapping.getRhsFeature() != null) {
                    final XmiFeature rhsFeature = mapping.getRhsFeature();
                    if (rhsFeature.getOwnerName().endsWith(OWNER_NAME_SUFFIX_IDENTITY_TYPE)) {
                        return XmiMappingStatus.IGNORABLE;
                    }
                }
                return status;
            }
            default: {
                return status;
            }
        }
    }
}
