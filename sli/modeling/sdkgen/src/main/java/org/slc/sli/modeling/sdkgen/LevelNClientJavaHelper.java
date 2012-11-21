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

package org.slc.sli.modeling.sdkgen;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaElement;

import org.slc.sli.modeling.jgen.JavaCollectionKind;
import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenGrammars;

public final class LevelNClientJavaHelper {

    private LevelNClientJavaHelper() {
        throw new SdkGenRuntimeException("Not implemented");
    }

    /**
     * The (reserved) name given to the custom property in the SLI database.
     */
    private static final QName CUSTOM_ELEMENT_NAME = new QName("http://www.slcedu.org/api/v1", "custom");

    private static final QName CALCULATED_VALUES_ELEMENT_NAME = new QName("http://www.slcedu.org/api/v1", "calculatedValuesList");

    private static final QName AGGREGATIONS_ELEMENT_NAME = new QName("http://www.slcedu.org/api/v1", "aggregationsList");
    /**
     * This is the default type used for all JSON objects.
     */
    protected static final JavaType JT_MAP_STRING_TO_OBJECT = JavaType.mapType(JavaType.JT_STRING, JavaType.JT_OBJECT);
    /**
     * The domain neutral entity type.
     */
    protected static final JavaType JT_ENTITY = JavaType.simpleType("Entity", JavaType.JT_OBJECT);
    /**
     * A list of the domain neutral entity type.
     */
    protected static final JavaType JT_LIST_OF_ENTITY = JavaType.collectionType(JavaCollectionKind.LIST, JT_ENTITY);

    public static final List<JavaParam> computeParams(final JavaParam token, final List<Param> params,
            final JavaParam requestParam) {
        final List<JavaParam> javaParams = new LinkedList<JavaParam>();
        javaParams.add(token);
        for (final Param param : params) {
            if (param.getStyle() == ParamStyle.TEMPLATE) {
                final QName type = param.getType();
                final JavaType javaType = GenericJavaHelper.getJavaType(type);
                javaParams.add(new JavaParam(param.getName(), javaType, true));
            }
        }
        javaParams.add(requestParam);
        return Collections.unmodifiableList(javaParams);
    }

    public static JavaType getResponseJavaType(final Method method, final SdkGenGrammars grammars,
            final boolean quietMode) {
        final List<Response> responses = method.getResponses();
        for (final Response response : responses) {
           
                final List<Representation> representations = response.getRepresentations();
                for (final Representation representation : representations) {
                    representation.getMediaType();
                    final QName elementName = representation.getElementName();
                    if (elementName != null) {
                        XmlSchemaElement element = grammars.getElement(elementName);
                        if (element != null) {
                            final Stack<QName> elementNames = new Stack<QName>();
                            return Level3ClientJavaHelper.toJavaTypeFromSchemaElement(element, elementNames, grammars);
                        } else {
                            if (CUSTOM_ELEMENT_NAME.equals(elementName)) {
                                return JT_MAP_STRING_TO_OBJECT;
                            } else if (CALCULATED_VALUES_ELEMENT_NAME.equals(elementName) ||
                                    AGGREGATIONS_ELEMENT_NAME.equals(elementName)) {
                                return JT_LIST_OF_ENTITY;
                            } else {
                                if (quietMode) {
                                    return JavaType.JT_OBJECT;
                                } else {
                                    throw new SdkGenRuntimeException("Unknown element: " + elementName);
                                }
                            }
                        }
                    } else {
                        if (quietMode) {
                            return JavaType.JT_OBJECT;
                        } else {
                            throw new SdkGenRuntimeException("Representation is missing element name specification.");
                        }
                    }
                
            } 
        }
        return JavaType.JT_OBJECT;
    }

    /**
     * Converts the specific type extracted from the WADL and schema into a more generic type.
     */
    public static final JavaType computeGenericType(final JavaType type, final boolean quietMode) {
        final JavaCollectionKind collectionKind = type.getCollectionKind();
        switch (collectionKind) {
        case LIST: {
            return JT_LIST_OF_ENTITY;
        }
        case MAP: {
            return JT_MAP_STRING_TO_OBJECT;
        }
        case NONE: {
            if (type.getSimpleName().equalsIgnoreCase("home")) {
                return JT_ENTITY;
            }
            if (quietMode) {
                return JT_LIST_OF_ENTITY;
            } else {
                throw new SdkGenRuntimeException("type : " + type);
            }
        }
        default: {
            throw new AssertionError(collectionKind);
        }
        }
    }

    public static final boolean isMapStringToObject(final JavaType type) {
        final JavaCollectionKind collectionKind = type.getCollectionKind();
        switch (collectionKind) {
        case LIST: {
            return false;
        }
        case MAP: {
            return true;
        }
        case NONE: {
            return false;
        }
        default: {
            throw new AssertionError(collectionKind);
        }
        }
    }

    public static final boolean isEntityList(final JavaType type) {
        final JavaCollectionKind collectionKind = type.getCollectionKind();
        switch (collectionKind) {
            case LIST: {
                if (type.primeType().equals(JT_ENTITY)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            case MAP: {
                return false;
            }
            case NONE: {
                return false;
            }
            default: {
                throw new AssertionError(collectionKind);
            }
        }
    }

    public static final boolean isSingleton(final JavaType type) {
        return type.getCollectionKind() == JavaCollectionKind.NONE;
    }
}
