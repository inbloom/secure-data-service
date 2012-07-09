package org.slc.sli.modeling.xmicomp;

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
    
    private static Set<FName> commonFeatures(final Set<FName> lhs, final Set<FName> rhs) {
        final Set<FName> result = new HashSet<FName>(lhs);
        result.retainAll(rhs);
        return Collections.unmodifiableSet(result);
    }
    
    private static XmiFeature computeMissingFeature(final Map<FName, XmiFeature> declaredFeatures,
            final ClassType classType, final Feature feature, final XmiDefinition which) {
        final FName name = makeKey(classType, feature);
        if (!declaredFeatures.containsKey(name)) {
            final String className = classType.getName();
            final String featureName = feature.getName();
            return new XmiFeature(featureName, className);
        } else {
            return null;
        }
    }
    
    private static final FName makeKey(final ClassType classType, final Feature feature) {
        if (classType == null) {
            throw new NullPointerException("classType");
        }
        if (feature == null) {
            throw new NullPointerException("feature");
        }
        return new FName(classType.getName(), feature.getName());
    }
    
    private static final FName makeKey(final XmiFeature feature) {
        if (feature == null) {
            throw new NullPointerException("feature");
        }
        return new FName(feature.getType(), feature.getName());
    }
    
    private static final Map<FName, Integer> makeView(final List<XmiMapping> mappings, final boolean isLHS) {
        final Map<FName, Integer> view = new HashMap<FName, Integer>();
        for (int index = 0; index < mappings.size(); index++) {
            final XmiMapping mapping = mappings.get(index);
            final XmiFeature feature = isLHS ? mapping.getLhsFeature() : mapping.getRhsFeature();
            if (feature != null) {
                view.put(new FName(feature.getType(), feature.getName()), index);
            }
        }
        return view;
    }
    
    public static final void mergeCommonFeatures(final Map<FName, XmiFeature> lhsMissing,
            final Map<FName, XmiFeature> rhsMissing, final List<XmiMapping> mappings, final String mergeComment) {
        final Set<FName> common = commonFeatures(lhsMissing.keySet(), rhsMissing.keySet());
        for (final FName name : common) {
            mappings.add(new XmiMapping(lhsMissing.get(name), rhsMissing.get(name), XmiMappingStatus.MATCH,
                    mergeComment));
            lhsMissing.remove(name);
            rhsMissing.remove(name);
        }
    }
    
    public static final void mergeMissingFeatures(final Map<FName, XmiFeature> missing,
            final List<XmiMapping> mappings, final boolean isLHS, final String mergeComment) {
        final Map<FName, Integer> view = XmiMappingHelper.makeView(mappings, !isLHS);
        final Set<FName> missingNames = new HashSet<FName>(missing.keySet());
        for (final FName missingName : missingNames) {
            if (view.containsKey(missingName)) {
                final int index = view.get(missingName);
                final XmiMapping oldMapping = mappings.get(index);
                final XmiFeature lhs = isLHS ? missing.get(missingName) : oldMapping.getLhsFeature();
                final XmiFeature rhs = isLHS ? oldMapping.getRhsFeature() : missing.get(missingName);
                final XmiMapping newMapping = new XmiMapping(lhs, rhs, XmiMappingStatus.MATCH, mergeComment);
                mappings.set(index, newMapping);
                missing.remove(missingName);
            }
        }
    }
    
    public static void mergeRemaining(final Map<FName, XmiFeature> features, final List<XmiMapping> mappings,
            final boolean isLHS, final String comment) {
        if (comment == null) {
            throw new NullPointerException("comment");
        }
        final boolean isRHS = !isLHS;
        final Set<FName> names = new HashSet<FName>(features.keySet());
        for (final FName name : names) {
            final XmiFeature lhsFeature = isLHS ? features.get(name) : null;
            final XmiFeature rhsFeature = isRHS ? features.get(name) : null;
            final XmiMapping mapping = new XmiMapping(lhsFeature, rhsFeature, XmiMappingStatus.UNKNOWN, comment);
            mappings.add(mapping);
            features.remove(name);
        }
        
    }
    
    public static final Map<FName, XmiFeature> missingFeatures(final List<XmiMapping> mappings,
            final XmiDefinition which, final ModelIndex model, final boolean isLHS) {
        
        final Map<FName, XmiFeature> missingFeatures = new HashMap<FName, XmiFeature>();
        
        final Map<FName, XmiFeature> declaredFeatures = new HashMap<FName, XmiFeature>();
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
    
    private XmiMappingHelper() {
        
    }
}
