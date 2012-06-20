package org.slc.sli.modeling.sdkgen;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;

public final class Level2ClientJavaHelper {

    private static final JavaType JT_MAP_STRING_TO_OBJECT = JavaType.mapType(JavaType.JT_STRING, JavaType.JT_OBJECT);

    public static final List<JavaParam> computeJavaGETParams(final List<Param> params) {
        final List<JavaParam> javaParams = new LinkedList<JavaParam>();
        // The token parameter is a standard parameter.
        javaParams.add(new JavaParam("token", JavaType.JT_STRING, true));
        for (final Param param : params) {
            if (param.getStyle() == ParamStyle.TEMPLATE) {
                final QName type = param.getType();
                // We're going to assume that the Java type of every template parameter is a list of
                // strings (for a GET method).
                // We could make this driven from the WADL if we had a foreign name-space element
                // "maxOccurs", like XML Schema.
                final JavaType javaPrimeType = GenericJavaHelper.getJavaType(type);
                final JavaType javaType = JavaType.listType(javaPrimeType);
                javaParams.add(new JavaParam(param.getName(), javaType, true));
            }
        }
        javaParams.add(new JavaParam("queryArgs", JT_MAP_STRING_TO_OBJECT, true));
        return Collections.unmodifiableList(javaParams);
    }
}
