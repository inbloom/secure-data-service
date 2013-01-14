/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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


package org.slc.sli.validation.schema;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.ws.commons.schema.constants.Constants.BlockConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.slc.sli.domain.enums.Right;

/**
 * SLI appinfo annotation support. This class holds SLI-specific appinfo elements as
 * defined in the SLI XSD files.
 *
 * @author asaarela
 *
 */
public class AppInfo extends Annotation {

    protected static final String SLI_XSD_NAMESPACE = "http://slc-sli/ed-org/0.1";
    protected static final String PII_ELEMENT_NAME = "PersonallyIdentifiableInfo";
    protected static final String READ_ENFORCEMENT_ELEMENT_NAME = "ReadEnforcement";
    protected static final String WRITE_ENFORCEMENT_ELEMENT_NAME = "WriteEnforcement";
    protected static final String ALLOWED_BY_ELEMENT_NAME = "allowedBy";
    protected static final String RELAXEDBLACKLIST_ELEMENT_NAME = "RelaxedBlacklist";
    protected static final String REFERENCE_TYPE_ELEMENT_NAME = "ReferenceType";
    protected static final String COLLECTION_TYPE_ELEMENT_NAME = "CollectionType";
    protected static final String SECURITY_SPHERE = "SecuritySphere";
    protected static final String RESTRICTED_ELEMENT_NAME = "RestrictedForLogging";
    protected static final String NATURAL_KEY = "naturalKey";
    protected static final String APPLY_NATURAL_KEYS = "applyNaturalKeys";
    protected static final String SELF_REFERENCE = "SelfReference";
    public static final String SCHEMA_VERSION = "schemaVersion";

    public static final int NOT_VERSIONED = -1;

    protected static final int DEFAULT_SCHEMA_VERSION = NOT_VERSIONED;

    private final Map<String, Object> values = new LinkedHashMap<String, Object>();

    /**
     * Construct an AppInfo instance from a list of AppInfo DOM nodes.
     *
     * @param nodes
     */
    public AppInfo(NodeList nodes) {

        if (nodes == null) {
            return;
        }

        for (int appInfoNodeIdx = 0; appInfoNodeIdx < nodes.getLength(); ++appInfoNodeIdx) {
            Node node = nodes.item(appInfoNodeIdx);
            if (node instanceof Element) {
                Element e = (Element) node;

                // ignore nodes not in the sli namespace
                if (!e.getNamespaceURI().equals(SLI_XSD_NAMESPACE)) {
                    continue;
                }

                String key = node.getLocalName().trim();
                if ((key.equals(READ_ENFORCEMENT_ELEMENT_NAME) || key.equals(WRITE_ENFORCEMENT_ELEMENT_NAME)) && e.hasChildNodes()) {
                    Set<String> rights = new HashSet<String>();
                    NodeList allAllowedBy = e.getChildNodes();
                    for (int i = 0; i < allAllowedBy.getLength(); i++) {
                        Node allowedBy = allAllowedBy.item(i);
                        if (allowedBy.hasChildNodes()) {
                            NodeList rightsNodes = allowedBy.getChildNodes();
                            for (int j = 0; j < rightsNodes.getLength(); j++) {
                                rights.add(rightsNodes.item(j).getNodeValue().trim());
                            }
                            values.put(key, rights);
                        }
                    }
                } else {
                    String value = node.getFirstChild().getNodeValue().trim();
                    values.put(key, value);
                }
            }
        }
    }

    public void put(String key, Object value) {
        values.put(key, value);
    }

    @Override
    public Annotation.AnnotationType getType() {
        return Annotation.AnnotationType.APPINFO;
    }


    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        int idx = 0;
        int len = values.size();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            builder.append("\"" + entry.getKey() + "\":");
            builder.append("\"" + entry.getValue() + "\"");

            if (++idx < len) {
                builder.append(", ");
            }

        }

        return builder.toString();
    }

    /**
     * Helper functions
     */

    public boolean isPersonallyIdentifiableInfo() {

        boolean rval = false;
        if (values.containsKey(PII_ELEMENT_NAME)) {
            rval = Boolean.parseBoolean((String) values.get(PII_ELEMENT_NAME));
        }

        return rval;
    }

    public boolean isRestrictedFieldForLogging() {
        boolean rval = false;
        if (values.containsKey(RESTRICTED_ELEMENT_NAME)) {
            rval = Boolean.parseBoolean((String) values.get(RESTRICTED_ELEMENT_NAME));
        }

        return rval;
    }

    public boolean isRelaxedBlacklisted() {
        boolean rval = false;
        if (values.containsKey(RELAXEDBLACKLIST_ELEMENT_NAME)) {
            rval = Boolean.parseBoolean((String) values.get(RELAXEDBLACKLIST_ELEMENT_NAME));
        }

        return rval;

    }

    public Set<Right> getReadAuthorities() {
        Right rval = Right.READ_GENERAL;

        if (values.containsKey(READ_ENFORCEMENT_ELEMENT_NAME)) {
            Set<String> rightStrings = (Set<String>) values.get(READ_ENFORCEMENT_ELEMENT_NAME);
            Set<Right> rights = new HashSet<Right>();
            for (String string : rightStrings) {
                rights.add(Right.valueOf(string));
            }
            return rights;
        }

        return new HashSet<Right>(Arrays.asList(rval));
    }

    public Set<Right> getWriteAuthorities() {
        Right rval = Right.WRITE_GENERAL;

        if (values.containsKey(WRITE_ENFORCEMENT_ELEMENT_NAME)) {
            Set<String> rightStrings = (Set<String>) values.get(WRITE_ENFORCEMENT_ELEMENT_NAME);
            Set<Right> rights = new HashSet<Right>();
            for (String string : rightStrings) {
                rights.add(Right.valueOf(string));
            }
            return rights;
        }

        return new HashSet<Right>(Arrays.asList(rval));
    }

    public String getSecuritySphere() {
        String rval = "CDM";

        if (values.containsKey(SECURITY_SPHERE)) {
            rval = (String) values.get(SECURITY_SPHERE);
        }

        return rval;
    }

    public String getReferenceType() {
        if (values.containsKey(REFERENCE_TYPE_ELEMENT_NAME)) {
            return (String) values.get(REFERENCE_TYPE_ELEMENT_NAME);
        }

        return null;
    }

    public String getCollectionType() {
        if (values.containsKey(COLLECTION_TYPE_ELEMENT_NAME)) {
            return (String) values.get(COLLECTION_TYPE_ELEMENT_NAME);
        }

        return null;
    }

    public int getSchemaVersion() {
        if (values.containsKey(SCHEMA_VERSION)) {
            return Integer.parseInt((String) values.get(SCHEMA_VERSION));
        }

        return NOT_VERSIONED;
    }

    public String getValue(String key) {
        return (String) values.get(key);
    }

    /**
     * Inherit more restrictive annotations from a parent container.
     *
     * @param parentInfo
     */
    public void inherit(AppInfo parentInfo) {
        if (parentInfo.isPersonallyIdentifiableInfo()) {
            values.put(PII_ELEMENT_NAME, "true");
        }

        if (parentInfo.isRelaxedBlacklisted()) {
            values.put(RELAXEDBLACKLIST_ELEMENT_NAME, "true");
        }

        for (Right right : parentInfo.getReadAuthorities()) {
            if (right.equals(Right.FULL_ACCESS)) {
                values.put(READ_ENFORCEMENT_ELEMENT_NAME, toSet(Right.FULL_ACCESS.toString()));
            } else if (right.equals(Right.ADMIN_ACCESS)) {
                if (!getReadAuthorities().contains(Right.FULL_ACCESS)) {
                    values.put(READ_ENFORCEMENT_ELEMENT_NAME, toSet(Right.ADMIN_ACCESS.toString()));
                }
            } else if (right.equals(Right.READ_RESTRICTED)) {
                if (!getReadAuthorities().contains(Right.FULL_ACCESS) && !getReadAuthorities().contains(Right.ADMIN_ACCESS)) {
                    values.put(READ_ENFORCEMENT_ELEMENT_NAME, toSet(Right.READ_RESTRICTED.toString()));
                }
            } else if (right.equals(Right.READ_GENERAL)) {
                if (getReadAuthorities().contains(Right.ANONYMOUS_ACCESS)) {
                    values.put(READ_ENFORCEMENT_ELEMENT_NAME, toSet(Right.READ_GENERAL.toString()));
                }
            }
        }

        for (Right right : parentInfo.getWriteAuthorities()) {
            if (right.equals(Right.FULL_ACCESS)) {
                values.put(WRITE_ENFORCEMENT_ELEMENT_NAME, toSet(Right.FULL_ACCESS.toString()));
            } else if (right.equals(Right.ADMIN_ACCESS)) {
                if (!getWriteAuthorities().contains(Right.FULL_ACCESS)) {
                    values.put(WRITE_ENFORCEMENT_ELEMENT_NAME, toSet(Right.ADMIN_ACCESS.toString()));
                }
            } else if (right.equals(Right.WRITE_RESTRICTED)) {
                if (!getWriteAuthorities().contains(Right.FULL_ACCESS) && !getReadAuthorities().contains(Right.ADMIN_ACCESS)) {
                    values.put(WRITE_ENFORCEMENT_ELEMENT_NAME, toSet(Right.WRITE_RESTRICTED.toString()));
                }
            } else if (right.equals(Right.WRITE_GENERAL)) {
                if (getWriteAuthorities().contains(Right.ANONYMOUS_ACCESS)) {
                    values.put(WRITE_ENFORCEMENT_ELEMENT_NAME, toSet(Right.WRITE_GENERAL.toString()));
                }
            }
        }

        if (parentInfo.getSecuritySphere() != null) {
            values.put(SECURITY_SPHERE, parentInfo.getSecuritySphere());
        }
    }

    private static Set<String> toSet(String ... elements) {
        HashSet<String> toReturn = new HashSet<String>(elements.length);
        for (String element : elements) {
            toReturn.add(element);
        }
        return toReturn;
    }

    public boolean isRequired() {
        String tmp = (String) values.get(BlockConstants.REQUIRED);
        if (tmp != null) {
            return Boolean.parseBoolean(tmp);
        }
        return false;
    }


    public boolean isNaturalKey() {
        boolean rval = false;
        if (values.containsKey(NATURAL_KEY)) {
            rval = Boolean.parseBoolean((String) values.get(NATURAL_KEY));
        }

        return rval;
    }

    public boolean applyNaturalKeys() {
        boolean rval = false;
        if (values.containsKey(APPLY_NATURAL_KEYS)) {
            rval = Boolean.parseBoolean((String) values.get(APPLY_NATURAL_KEYS));
        }

        return rval;
    }

    public boolean isSelfReference() {
        boolean rval = false;
        if (values.containsKey(SELF_REFERENCE)) {
            rval = Boolean.parseBoolean((String) values.get(SELF_REFERENCE));
        }

        return rval;
    }

}
