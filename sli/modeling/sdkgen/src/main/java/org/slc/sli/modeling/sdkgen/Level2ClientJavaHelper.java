package org.slc.sli.modeling.sdkgen;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.slc.sli.modeling.jgen.JavaParam;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.ParamStyle;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.wadl.helpers.WadlHelper;

public final class Level2ClientJavaHelper {

    public static final List<JavaParam> computeJavaRequestParams(final Method method, final Resource resource,
            final Resources resources, final Application application, final Stack<Resource> ancestors) {
        final List<JavaParam> javaParams = new LinkedList<JavaParam>();
        javaParams.add(new JavaParam("token", "String", true));

        final List<Resource> rightToLeftResources = new LinkedList<Resource>();
        rightToLeftResources.addAll(WadlHelper.reverse(ancestors));
        rightToLeftResources.add(resource);

        for (final Resource rightToLeftResource : rightToLeftResources) {
            System.out.println("rightToLeft: " + rightToLeftResource.getPath());
            for (final Param param : rightToLeftResource.getParams()) {
                if (param.getStyle() == ParamStyle.TEMPLATE) {
                    final QName type = param.getType();
                    final String javaType = GenericJavaHelper.getJavaType(type);
                    javaParams.add(new JavaParam(param.getName(), javaType, true));
                    System.out.println("name=" + param.getName() + ", type=" + type + ", style=" + param.getStyle());
                }
            }
        }
        for (final Param param : WadlHelper.getRequestParams(method)) {
            // System.out.println("name=" + param.getName() + ", type=" + param.getType());
        }

        return Collections.unmodifiableList(javaParams);
    }
}
