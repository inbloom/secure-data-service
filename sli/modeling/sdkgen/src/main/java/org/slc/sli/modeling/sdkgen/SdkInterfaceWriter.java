package org.slc.sli.modeling.sdkgen;

import java.io.IOException;
import java.util.Stack;

import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;

public final class SdkInterfaceWriter implements WadlHandler {

    private final String packageName;
    private final String className;
    private final JavaStreamWriter jsw;

    public SdkInterfaceWriter(final String packageName, final String className, final JavaStreamWriter jsw) {
        if (packageName == null) {
            throw new NullPointerException("packageName");
        }
        if (className == null) {
            throw new NullPointerException("className");
        }
        if (jsw == null) {
            throw new NullPointerException("jsw");
        }
        this.packageName = packageName;
        this.className = className;
        this.jsw = jsw;
    }

    @Override
    public void beginApplication(final Application application) {
        try {
            jsw.writePackage(packageName);
            jsw.writeImport("java.io.IOException");
            jsw.writeImport("java.util.List");
            jsw.writeImport("org.slc.sli.api.client.Entity");
            jsw.beginInterface(className);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endApplication(final Application application) {
        try {
            jsw.endClass();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beginResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // TODO Auto-generated method stub

    }

    @Override
    public void endResource(Resource resource, Resources resources, Application app, Stack<Resource> ancestors) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beginMethod(final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors) {
        try {
            if (Method.NAME_HTTP_GET.equals(method.getName())) {
                jsw.writeComment(method.getId());
                jsw.beginStmt();
                jsw.write("List<Entity> " + method.getId() + "() throws IOException, SLIDataStoreException");
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endMethod(final Method method, final Resource resource, final Resources resources,
            final Application app, final Stack<Resource> ancestors) {
        try {
            if (Method.NAME_HTTP_GET.equals(method.getName())) {
                jsw.endStmt();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
