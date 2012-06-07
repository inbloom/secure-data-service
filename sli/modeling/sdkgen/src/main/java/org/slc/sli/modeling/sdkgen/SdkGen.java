package org.slc.sli.modeling.sdkgen;

import static java.util.Arrays.asList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.apache.ws.commons.schema.XmlSchema;

import org.slc.sli.modeling.jgen.JavaGenConfig;
import org.slc.sli.modeling.jgen.JavaGenConfigBuilder;
import org.slc.sli.modeling.jgen.JavaOutputFactory;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Grammars;
import org.slc.sli.modeling.rest.Include;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Representation;
import org.slc.sli.modeling.rest.Request;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenElement;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenGrammars;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenType;
import org.slc.sli.modeling.sdkgen.grammars.xsd.SdkGenGrammarsWrapper;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.wadl.helpers.WadlHelper;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.slc.sli.modeling.xsd.XsdReader;

/**
 * Command Line Interface for generating a Java SDK.
 */
public final class SdkGen {

    private static final List<String> ARGUMENT_HELP = asList("h", "?");
    private static final String ARGUMENT_CLASS = "class";
    private static final String ARGUMENT_PACKAGE = "package";
    private static final String ARGUMENT_WADL = "wadlFile";
    private static final String ARGUMENT_XMI = "xmiFile";
    private static final String ARGUMENT_OUT_FOLDER = "outFolder";

    public static void main(final String[] args) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<?> helpSpec = parser.acceptsAll(ARGUMENT_HELP, "Show help");
        final OptionSpec<String> classSpec = optionSpec(parser, ARGUMENT_CLASS, "Class", String.class);
        final OptionSpec<String> packageSpec = optionSpec(parser, ARGUMENT_PACKAGE, "Package", String.class);
        final OptionSpec<File> wadlFileSpec = optionSpec(parser, ARGUMENT_WADL, "WADL file", File.class);
        final OptionSpec<File> xmiFileSpec = optionSpec(parser, ARGUMENT_XMI, "XMI file", File.class);
        final OptionSpec<File> outFolderSpec = optionSpec(parser, ARGUMENT_OUT_FOLDER, "Output folder", File.class);
        try {
            final OptionSet options = parser.parse(args);
            if (options.hasArgument(helpSpec)) {
                try {
                    parser.printHelpOn(System.out);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    final File wadlFile = options.valueOf(wadlFileSpec);
                    final Application wadlApp = WadlReader.readApplication(wadlFile);
                    final JavaGenConfig config = new JavaGenConfigBuilder().build();

                    final File xmiFile = options.valueOf(xmiFileSpec);
                    final ModelIndex model = new DefaultModelIndex(XmiReader.readModel(xmiFile));
                    final File outFolder = options.valueOf(outFolderSpec);
                    final String className = options.valueOf(classSpec);
                    final String packageName = options.valueOf(packageSpec);
                    final String fileName = className.concat(".java");
                    final File javaFile = new File(outFolder, fileName);
                    writeProxyClass(new QName(packageName, className), new Wadl<File>(wadlApp, wadlFile), model,
                            javaFile, config);
                } catch (final FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (final OptionException e) {
            System.err.println(e.getMessage());
        }
    }

    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }

    private static final void writeProxyClass(final QName name, final Wadl<File> wadl, final ModelIndex model,
            final File file, final JavaGenConfig config) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeProxyClass(name, wadl, model, outstream, config);
            } finally {
                try {
                    outstream.close();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final SdkGenGrammars getSchema(final Grammars grammars, final File wadlFile)
            throws FileNotFoundException {
        final List<XmlSchema> xmlSchemas = new LinkedList<XmlSchema>();
        for (final Include include : grammars.getIncludes()) {
            final String href = include.getHref();
            final File schemaFile = new File(wadlFile.getParentFile(), href);
            final XmlSchema xmlSchema = XsdReader.readSchema(schemaFile, new SdkGenURIResolver());
            xmlSchemas.add(xmlSchema);
        }
        return new SdkGenGrammarsWrapper(xmlSchemas);
    }

    private static final void writeProxyClass(final QName name, final Wadl<File> wadl, final ModelIndex model,
            final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                jsw.writePackage(name.getNamespaceURI());
                jsw.beginClass(name.getLocalPart(), /* extends */null);
                try {
                    final Application app = wadl.getApplication();
                    final SdkGenGrammars grammars = getSchema(app.getGrammars(), wadl.getSource());
                    final Resources resources = app.getResources();
                    final Stack<Resource> ancestors = new Stack<Resource>();
                    for (final Resource resource : resources.getResources()) {
                        writeResource(resource, resources, app, ancestors, grammars, jsw);
                    }
                } finally {
                    jsw.endClass();
                }
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final void writeResource(final Resource resource, final Resources resources, final Application wadl,
            final Stack<Resource> ancestors, final SdkGenGrammars grammars, final JavaStreamWriter jsw)
            throws IOException {
        for (final Method method : resource.getMethods()) {
            writeMethod(method, resource, resources, wadl, ancestors, grammars, jsw);
        }
        ancestors.push(resource);
        try {
            for (final Resource childResource : resource.getResources()) {
                writeResource(childResource, resources, wadl, ancestors, grammars, jsw);
            }
        } finally {
            ancestors.pop();
        }
    }

    private static final void writeMethod(final Method method, final Resource resource, final Resources resources,
            final Application wadl, final Stack<Resource> ancestors, final SdkGenGrammars grammars,
            final JavaStreamWriter jsw) throws IOException {
        @SuppressWarnings("unused")
        // Perhaps modify this method to generate a different naming scheme?
        final String id = WadlHelper.computeId(method, resource, resources, wadl, ancestors);

        @SuppressWarnings("unused")
        final Request request = method.getRequest();

        final List<Response> responses = method.getResponses();
        for (final Response response : responses) {
            final List<Representation> representations = response.getRepresentations();
            for (final Representation representation : representations) {
                final QName elementName = representation.getElement();
                final SdkGenElement element = grammars.getElement(elementName);
                if (elementName != null && !elementName.getLocalPart().equals("custom")
                        && !elementName.getLocalPart().equals("home")) {

                    System.out.println(method.getName() + " " + elementName + " => " + element);
                    if (element != null) {
                        final SdkGenType type = element.getType();
                        System.out.println(method.getName() + " " + elementName + " => " + type);
                    }
                }
            }
        }

        jsw.writeComment(method.getId());
        jsw.beginStmt();
        jsw.write("void " + method.getId() + "()");
        jsw.beginBlock();
        jsw.endBlock();
        jsw.endStmt();
    }
}
