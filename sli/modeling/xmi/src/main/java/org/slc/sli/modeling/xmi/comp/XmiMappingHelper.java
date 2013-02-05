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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Feature;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * Utilities for manipulating structures used in establishing mappings.
 */
public final class XmiMappingHelper {
    
    private static Set<CaseInsensitiveQName> commonFeatures(final Set<CaseInsensitiveQName> lhs,
            final Set<CaseInsensitiveQName> rhs) {
        final Set<CaseInsensitiveQName> result = new HashSet<CaseInsensitiveQName>(lhs);
        result.retainAll(rhs);
        return Collections.unmodifiableSet(result);
    }
    
    private static XmiFeature computeMissingFeature(final Map<CaseInsensitiveQName, XmiFeature> declaredFeatures,
            final ClassType classType, final Feature feature, final XmiDefinition which) {
        final CaseInsensitiveQName name = makeKey(classType, feature);
        if (!declaredFeatures.containsKey(name)) {
            return new XmiFeature(feature.getName(), true, classType.getName(), true);
        } else {
            return null;
        }
    }
    
    private static final CaseInsensitiveQName makeKey(final ClassType classType, final Feature feature) {
        if (classType == null) {
            throw new IllegalArgumentException("classType");
        }
        if (feature == null) {
            throw new IllegalArgumentException("feature");
        }
        return new CaseInsensitiveQName(classType.getName(), feature.getName());
    }
    
    private static final CaseInsensitiveQName makeKey(final XmiFeature feature) {
        if (feature == null) {
            throw new IllegalArgumentException("feature");
        }
        return new CaseInsensitiveQName(feature.getOwnerName(), feature.getName());
    }
    
    private static final Map<CaseInsensitiveQName, Integer> makeCaseInsensitiveNameToIndexMapping(
            final List<XmiMapping> mappings, final boolean isLHS) throws XmiMappingException {
        final Map<CaseInsensitiveQName, Integer> view = new HashMap<CaseInsensitiveQName, Integer>();
        for (int index = 0; index < mappings.size(); index++) {
            final XmiMapping mapping = mappings.get(index);
            final XmiFeature feature = isLHS ? mapping.getLhsFeature() : mapping.getRhsFeature();
            if (feature != null) {
                final CaseInsensitiveQName key = new CaseInsensitiveQName(feature.getOwnerName(), feature.getName());
                if (view.containsKey(key)) {
                    throw new XmiMappingException("feature : " + feature.getOwnerName() + "." + feature.getName()
                            + " already exists.");
                } else {
                    view.put(key, index);
                }
            }
        }
        return view;
    }
    
    /**
     * Determines the features that are common in the two input lists.
     * 
     * @param lhsMissing
     *            The features that are missing in the LHS.
     * @param rhsMissing
     *            The features that are missing in the RHS.
     * @param mergeComment
     *            A comment to use with the new mapping.
     */
    public static final void commonMappings(final List<XmiMapping> mappings,
            final Map<CaseInsensitiveQName, XmiFeature> lhsMissing,
            final Map<CaseInsensitiveQName, XmiFeature> rhsMissing, final String mergeComment) {
        
        final Set<CaseInsensitiveQName> common = commonFeatures(lhsMissing.keySet(), rhsMissing.keySet());
        
        for (final CaseInsensitiveQName name : common) {
            mappings.add(new XmiMapping(lhsMissing.get(name), rhsMissing.get(name), XmiMappingStatus.MATCH, "",
                    mergeComment));
            lhsMissing.remove(name);
            rhsMissing.remove(name);
        }
    }
    
    /**
     * We have identified some features that are missing on either the LHS or the RHS. Merge these
     * features, where possible, with existing mappings. This function uses side effects.
     * 
     * @param features
     *            The missing features.
     * @param mappings
     *            The current mappings (assumed to be mutable).
     * @param isLHS
     *            The side of the missing features.
     * @param mergeComment
     *            The comment to put on merged records.
     */
    public static final void mergeMissingFeatures(final Map<CaseInsensitiveQName, XmiFeature> features,
            final List<XmiMapping> mappings, final boolean isLHS, final String mergeComment) throws XmiMappingException {
        
        // Just to be explicit, and to show the symmetry, we'll create the complementary variable.
        final boolean isRHS = !isLHS;
        
        final Map<CaseInsensitiveQName, Integer> view = XmiMappingHelper.makeCaseInsensitiveNameToIndexMapping(
                mappings, isRHS);
        
        final Set<CaseInsensitiveQName> missingNames = new HashSet<CaseInsensitiveQName>(features.keySet());
        for (final CaseInsensitiveQName missingName : missingNames) {
            if (view.containsKey(missingName)) {
                final int index = view.get(missingName);
                final XmiMapping oldMapping = mappings.get(index);
                final XmiFeature lhs = isLHS ? features.get(missingName) : oldMapping.getLhsFeature();
                final XmiFeature rhs = isRHS ? features.get(missingName) : oldMapping.getRhsFeature();
                final XmiMapping newMapping = new XmiMapping(lhs, rhs, XmiMappingStatus.MATCH, "", mergeComment);
                mappings.set(index, newMapping);
                features.remove(missingName);
            }
        }
    }
    
    public static void appendRemaining(final Map<CaseInsensitiveQName, XmiFeature> features,
            final List<XmiMapping> mappings, final boolean isLHS, final String comment) {
        if (comment == null) {
            throw new IllegalArgumentException("comment");
        }
        final boolean isRHS = !isLHS;
        final Set<CaseInsensitiveQName> names = new HashSet<CaseInsensitiveQName>(features.keySet());
        for (final CaseInsensitiveQName name : names) {
            final XmiFeature lhsFeature = isLHS ? features.get(name) : null;
            final XmiFeature rhsFeature = isRHS ? features.get(name) : null;
            final XmiMapping mapping = new XmiMapping(lhsFeature, rhsFeature, XmiMappingStatus.UNKNOWN, "", comment);
            mappings.add(mapping);
            features.remove(name);
        }
        
    }
    
    public static final Map<CaseInsensitiveQName, XmiFeature> missingFeatures(final List<XmiMapping> mappings,
            final XmiDefinition which, final ModelIndex model, final boolean isLHS) {
        
        final Map<CaseInsensitiveQName, XmiFeature> missingFeatures = new HashMap<CaseInsensitiveQName, XmiFeature>();
        
        final Map<CaseInsensitiveQName, XmiFeature> declaredFeatures = new HashMap<CaseInsensitiveQName, XmiFeature>();
        for (final XmiMapping mapping : mappings) {
            final XmiFeature feature = isLHS ? mapping.getLhsFeature() : mapping.getRhsFeature();
            if (feature != null) {
                declaredFeatures.put(makeKey(feature), feature);
            }
        }
        for (final ClassType classType : model.getClassTypes().values()) {
            for (final Attribute attribute : classType.getAttributes()) {
                final XmiFeature missing = computeMissingFeature(declaredFeatures, classType, attribute, which);
                if (missing != null) {
                    missingFeatures.put(makeKey(missing), missing);
                }
            }
            for (final AssociationEnd associationEnd : model.getAssociationEnds(classType.getId())) {
                final XmiFeature missing = computeMissingFeature(declaredFeatures, classType, associationEnd, which);
                if (missing != null) {
                    missingFeatures.put(makeKey(missing), missing);
                }
            }
        }
        return Collections.unmodifiableMap(missingFeatures);
    }
    
    public static final <T> Map<CaseInsensitiveString, T> toCaseInsensitiveKey(final Map<String, T> map) {
        final Map<CaseInsensitiveString, T> caseInsensitiveMap = new HashMap<CaseInsensitiveString, T>(map.size());
        for (final Map.Entry<String, T> entry : map.entrySet()) {
            caseInsensitiveMap.put(new CaseInsensitiveString(entry.getKey()), entry.getValue());
        }
        return Collections.unmodifiableMap(caseInsensitiveMap);
    }
    
    private XmiMappingHelper() {
        
    }
}
