package org.slc.sli.modeling.sdkgen;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
        throw new RuntimeException();
    }

    /**
     * The (reserved) name given to the custom property in the SLI database.
     */
    private static final QName CUSTOM_ELEMENT_NAME = new QName("http://www.slcedu.org/api/v1", "custom");
    /**
     * This is the default type used for all JSON objects.
     */
    private static final JavaType JT_MAP_STRING_TO_OBJECT = JavaType.mapType(JavaType.JT_STRING, JavaType.JT_OBJECT);
    /**
     * The domain neutral entity type.
     */
    protected static final JavaType JT_ENTITY = JavaType.simpleType("Entity", JavaType.JT_OBJECT);
    /**
     * A list of the domain neutral entity type.
     */
    private static final JavaType JT_LIST_OF_ENTITY = JavaType.collectionType(JavaCollectionKind.LIST, JT_ENTITY);

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
            try {
                final List<Representation> representations = response.getRepresentations();
                for (final Representation representation : representations) {
                    representation.getMediaType();
                    final QName elementName = representation.getElementName();
                    if (elementName != null) {
                        final XmlSchemaElement element = grammars.getElement(elementName);
                        if (element != null) {
                            final Stack<QName> elementNames = new Stack<QName>();
                            return Level3ClientJavaHelper.toJavaTypeFromSchemaElement(element, elementNames, grammars);
                        } else {
                            if (CUSTOM_ELEMENT_NAME.equals(elementName)) {
                                return JT_MAP_STRING_TO_OBJECT;
                            } else {
                                if (quietMode) {
                                    return JavaType.JT_OBJECT;
                                } else {
                                    throw new RuntimeException("Unknown element: " + elementName);
                                }
                            }
                        }
                    } else {
                        if (quietMode) {
                            return JavaType.JT_OBJECT;
                        } else {
                            throw new RuntimeException("Representation is missing element name specification.");
                        }
                    }
                }
            } finally {
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
            if (quietMode) {
                return JT_LIST_OF_ENTITY;
            } else {
                throw new RuntimeException("type : " + type);
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
            // FIXME: This should check the key type and bvalue type as well.
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
}
