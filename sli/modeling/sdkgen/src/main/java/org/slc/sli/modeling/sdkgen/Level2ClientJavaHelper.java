package org.slc.sli.modeling.sdkgen;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;

public final class Level2ClientJavaHelper {

    public static final List<JavaParam> computeJavaRequestParams(final List<Param> params) {
        final List<JavaParam> javaParams = new LinkedList<JavaParam>();
        // The token parameter is a standard parameter.
        javaParams.add(new JavaParam("token", "String", true));
        for (final Param param : params) {
            if (param.getStyle() == ParamStyle.TEMPLATE) {
                final QName type = param.getType();
                final String javaType = GenericJavaHelper.getJavaType(type);
                javaParams.add(new JavaParam(param.getName(), javaType, true));
            }
        }
        return Collections.unmodifiableList(javaParams);
    }
}
