package org.slc.sli.modeling.sdkgen;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.jgen.JavaType;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;

public class LevelNClientJavaHelper {

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

}
