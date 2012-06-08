package org.slc.sli.modeling.sdkgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.wadl.helpers.WadlHandler;

public final class SdkImplementationWriter implements WadlHandler {

    private final String packageName;
    private final String className;
    private final List<String> interfaces;
    private final JavaStreamWriter jsw;

    public SdkImplementationWriter(final String packageName, final String className, final List<String> interfaces,
            final JavaStreamWriter jsw) {
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
        this.interfaces = Collections.unmodifiableList(new ArrayList<String>(interfaces));
        this.jsw = jsw;
    }

    @Override
    public void beginApplication(final Application application) {
        try {
            jsw.writePackage(packageName);
            jsw.writeImport("java.io.IOException");
            jsw.writeImport("java.net.URL");
            jsw.writeImport("java.util.ArrayList");
            jsw.writeImport("java.util.Collections");
            jsw.writeImport("java.util.List");
            jsw.beginClass(className, interfaces);
            // Attributes
            //jsw.writeAttribute("sliClient", "SLIClient");
            // Write Initializer
            jsw.write("public "
                    + className
                    + "(final URL apiServerUrl, final String clientId, final String clientSecret, final URL callbackURL)");
            jsw.beginBlock();
//            jsw.beginStmt().write("sliClient = new BasicClient(apiServerUrl, clientId, clientSecret, callbackURL)")
//                    .endStmt();
            jsw.endBlock();
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
                jsw.writeOverride();
                jsw.beginStmt();
                jsw.write("public List<SLIEntity> " + method.getId() + "() throws IOException, SLIDataStoreException");
                jsw.beginBlock();
                jsw.beginStmt().write("final List<SLIEntity> entities = new ArrayList<SLIEntity>()").endStmt();
                jsw.beginStmt().write("return Collections.unmodifiableList(entities)").endStmt();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endMethod(Method method, Resource resource, Resources resources, Application app,
            Stack<Resource> ancestors) {
        try {
            if (Method.NAME_HTTP_GET.equals(method.getName())) {
                jsw.endBlock();
                jsw.endStmt();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
