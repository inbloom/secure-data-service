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

package org.slc.sli.modeling.tools.xmigen;

/**
 * A utility class that assists in the XSD to UML transformation process.
 * 
 * @author kmyers
 *
 */
public final class Xsd2UmlHelper {
    
    public static final String pluralize(final String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName");
        }
        if (typeName.endsWith("y")) {
            return typeName.substring(0, typeName.length() - 1).concat("ies");
        } else if (typeName.endsWith("s")) {
            return typeName;
        } else if (typeName.equalsIgnoreCase("staff")) {
            return typeName;
        } else {
            return typeName.concat("s");
        }
    }
    
    public static String replaceAllIgnoreCase(final String strval, final String target, final String replacement) {
        return strval.replaceAll("(?i)".concat(target), replacement);
    }
    
    private static final String[] ACRONYMS = { "CIP", "CTE", "GPA", "ID", "IEP", "LEA", "URI", "URL", "URN" };
    
    public static final String camelCase(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("text");
        }
        for (final String acronym : ACRONYMS) {
            if (acronym.equals(text)) {
                return text;
            }
        }
        for (final String acronym : ACRONYMS) {
            if (text.startsWith(acronym)) {
                return acronym.toLowerCase().concat(text.substring(acronym.length()));
            }
        }
        return text.substring(0, 1).toLowerCase().concat(text.substring(1));
    }
    
    public static final String titleCase(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("text");
        }
        return text.substring(0, 1).toUpperCase().concat(text.substring(1));
    }
    
    /**
     * Compute a sensible name for the reverse (physical) navigation direction.
     */
    public static final String makeAssociationEndName(final String sourceTypeName, final String sourceEndName,
            final int degeneracy, final String targetTypeName) {
        if (degeneracy > 1) {
            // It's rather crude to use a type name.
            final String targetName = Xsd2UmlHelper.pluralize(targetTypeName);
            // There is more than one pathway so make sure that the name is unique.
            return sourceEndName.concat(Xsd2UmlHelper.titleCase(targetName));
        } else {
            if (targetTypeName.equals(sourceTypeName)) {
                // Reference to self. Avoid absurdity. See AssessmentFamily.
                if ("parent".concat(sourceTypeName).equals(sourceEndName)) {
                    return "child".concat(Xsd2UmlHelper.pluralize(targetTypeName));
                } else {
                    // It's rather crude to use a type name.
                    final String targetName = Xsd2UmlHelper.pluralize(targetTypeName);
                    return Xsd2UmlHelper.camelCase(targetName);
                }
            } else {
                // It's rather crude to use a type name.
                final String targetName = Xsd2UmlHelper.pluralize(targetTypeName);
                return Xsd2UmlHelper.camelCase(targetName);
            }
        }
    }
    
}
