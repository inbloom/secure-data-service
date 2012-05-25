package org.slc.sli.modeling.sdkgen;

import static java.util.Arrays.asList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Stack;

import javax.xml.namespace.QName;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.slc.sli.modeling.jgen.JavaGenConfig;
import org.slc.sli.modeling.jgen.JavaGenConfigBuilder;
import org.slc.sli.modeling.jgen.JavaOutputFactory;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Resources;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.xmi.reader.XmiReader;

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
                final Application wadl = WadlReader.readApplication(wadlFile);
                final JavaGenConfig config = new JavaGenConfigBuilder().build();

                final File xmiFile = options.valueOf(xmiFileSpec);
                final ModelIndex model = new DefaultModelIndex(XmiReader.readModel(xmiFile));
                final File outFolder = options.valueOf(outFolderSpec);
                {
                    final String className = options.valueOf(classSpec);
                    final String packageName = options.valueOf(packageSpec);
                    final String fileName = className.concat(".java");
                    final File file = new File(outFolder, fileName);
                    writeProxyClass(new QName(packageName, className), wadl, model, file, config);
                }
            } catch (final FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }

    private static final void writeProxyClass(final QName name, final Application wadl, final ModelIndex model,
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

    private static final void writeProxyClass(final QName name, final Application wadl, final ModelIndex model,
            final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                jsw.writePackage(name.getNamespaceURI());
                jsw.beginClass(name.getLocalPart(), /* extends */null);
                try {
                    final Resources resources = wadl.getResources();
                    final Stack<Resource> ancestors = new Stack<Resource>();
                    for (final Resource resource : resources.getResources()) {
                        writeResource(resource, wadl, ancestors, jsw);
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

    private static final void writeResource(final Resource resource, final Application wadl,
            final Stack<Resource> ancestors, final JavaStreamWriter jsw) throws IOException {
        for (final Method method : resource.getMethods()) {
            writeMethod(method, resource, ancestors, jsw);
        }
        ancestors.push(resource);
        try {
            for (final Resource childResource : resource.getResources()) {
                writeResource(childResource, wadl, ancestors, jsw);
            }
        } finally {
            ancestors.pop();
        }
    }

    private static final void writeMethod(final Method method, final Resource resource,
            final Stack<Resource> ancestors, final JavaStreamWriter jsw) throws IOException {
        jsw.writeComment(method.getId());
        jsw.beginStmt();
        jsw.write("void " + method.getId() + "()");
        jsw.beginBlock();
        jsw.endBlock();
        jsw.endStmt();
    }
}
