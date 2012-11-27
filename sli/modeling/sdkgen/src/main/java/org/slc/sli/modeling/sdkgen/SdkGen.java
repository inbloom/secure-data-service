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

import static java.util.Arrays.asList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.apache.commons.io.IOUtils;
import org.apache.ws.commons.schema.XmlSchema;

import org.slc.sli.modeling.jgen.JavaGenConfig;
import org.slc.sli.modeling.jgen.JavaGenConfigBuilder;
import org.slc.sli.modeling.jgen.JavaOutputFactory;
import org.slc.sli.modeling.jgen.JavaStreamWriter;
import org.slc.sli.modeling.rest.Application;
import org.slc.sli.modeling.rest.Grammars;
import org.slc.sli.modeling.rest.Include;
import org.slc.sli.modeling.sdkgen.grammars.SdkGenGrammars;
import org.slc.sli.modeling.sdkgen.grammars.xsd.SdkGenGrammarsWrapper;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.wadl.helpers.WadlWalker;
import org.slc.sli.modeling.wadl.reader.WadlReader;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.slc.sli.modeling.xsd.XsdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command Line Interface for generating a Java SDK.
 */
public final class SdkGen {

    private static final Logger LOG = LoggerFactory.getLogger(SdkGen.class);

    private static final List<String> ARGUMENT_HELP = asList("h", "?");
    // private static final String ARGUMENT_CLASS = "class";
    private static final String ARGUMENT_PACKAGE = "package";
    private static final String ARGUMENT_WADL = "wadlFile";
    private static final String ARGUMENT_XMI = "xmiFile";
    private static final String ARGUMENT_OUT_FOLDER = "outFolder";

    /**
     * The name we give to the Level 2 Client class.
     */
    private static final String LEVEL_2_CLIENT = "Level2Client";

    /**
     * The name we give to the Level 3 Client class.
     */
    private static final String LEVEL_3_CLIENT = "Level3Client";

    @SuppressWarnings("unused")
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

    public static void main(final String[] args) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<?> helpSpec = parser.acceptsAll(ARGUMENT_HELP, "Show help");
        // final OptionSpec<String> classSpec = optionSpec(parser, ARGUMENT_CLASS, "Class",
        // String.class);
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
                    throw new SdkGenRuntimeException(e);
                }
            } else {
                try {
                    final File wadlFile = options.valueOf(wadlFileSpec);
                    final Application wadlApp = WadlReader.readApplication(wadlFile);
                    final JavaGenConfig config = new JavaGenConfigBuilder().build();

                    final File xmiFile = options.valueOf(xmiFileSpec);
                    final ModelIndex model = new DefaultModelIndex(XmiReader.readModel(xmiFile));
                    final File outFolder = options.valueOf(outFolderSpec);
                    // The package name is currently the same.
                    final String packageName = options.valueOf(packageSpec);

                    final boolean writeLevel2 = true;
                    if (writeLevel2) {
                        final List<String> interfaces = new LinkedList<String>();
                        // Interface
                        if (writeLevel2) {
                            final String className = LEVEL_2_CLIENT;
                            interfaces.add(className);
                            final File interfaceFile = new File(outFolder, className.concat(".java"));
                            writeLevel2ClientInterface(new QName(packageName, className), new Wadl<File>(wadlApp,
                                    wadlFile), model, interfaceFile, config);
                        }
                        // Implementation
                        if (writeLevel2) {
                            final String className = "Standard".concat(LEVEL_2_CLIENT);
                            final File clazzFile = new File(outFolder, className.concat(".java"));
                            writeLevel2ClientImplementation(new QName(packageName, className), interfaces,
                                    new Wadl<File>(wadlApp, wadlFile), model, clazzFile, config);
                        }
                    }
                    // POJOs
                    final boolean writeLevel3 = true;
                    if (writeLevel3) {
                        final List<String> interfaces = new LinkedList<String>();
                        if (writeLevel3) {
                            // POJOs
                            final File dir = new File(outFolder, "/pojo");
                            Level3ClientPojoGenerator.doModel(model, dir, packageName.concat(".pojo"), config);

                            // Interface
                            final String className = LEVEL_3_CLIENT;
                            interfaces.add(className);
                            final File interfaceFile = new File(outFolder, className.concat(".java"));
                            writeLevel3ClientInterface(new QName(packageName, className), new Wadl<File>(wadlApp,
                                    wadlFile), model, interfaceFile, config);
                        }
                        // Implementation
                        if (writeLevel3) {
                            final String className = "Standard".concat(LEVEL_3_CLIENT);
                            final File clazzFile = new File(outFolder, className.concat(".java"));
                            writeLevel3ClientImplementation(new QName(packageName, className), interfaces,
                                    new Wadl<File>(wadlApp, wadlFile), model, clazzFile, config);
                        }
                    }
                } catch (final FileNotFoundException e) {
                    throw new SdkGenRuntimeException(e);
                }
            }
        } catch (final OptionException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }

    private static final void writeLevel2ClientImplementation(final QName name, final List<String> interfaces,
            final Wadl<File> wadl, final ModelIndex model, final File file, final JavaGenConfig config) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeLevel2ClientImplementation(name, interfaces, wadl, model, outstream, config);
            } finally {
                IOUtils.closeQuietly(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static final void writeLevel3ClientImplementation(final QName name, final List<String> interfaces,
            final Wadl<File> wadl, final ModelIndex model, final File file, final JavaGenConfig config) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeLevel3ClientImplementation(name, interfaces, wadl, model, outstream, config);
            } finally {
                IOUtils.closeQuietly(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static final void writeLevel2ClientImplementation(final QName name, final List<String> interfaces,
            final Wadl<File> wadl, final ModelIndex model, final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                new WadlWalker(new Level2ClientImplementationWriter(name.getNamespaceURI(), name.getLocalPart(),
                        interfaces, wadl.getSource(), jsw)).walk(wadl.getApplication());
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }

    private static final void writeLevel3ClientImplementation(final QName name, final List<String> interfaces,
            final Wadl<File> wadl, final ModelIndex model, final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                new WadlWalker(new Level3ClientImplementationWriter(name.getNamespaceURI(), name.getLocalPart(),
                        interfaces, wadl.getSource(), jsw)).walk(wadl.getApplication());
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }

    private static final void writeLevel2ClientInterface(final QName name, final Wadl<File> wadl,
            final ModelIndex model, final File file, final JavaGenConfig config) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeLevel2ClientInterface(name, wadl, model, outstream, config);
            } finally {
                IOUtils.closeQuietly(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static final void writeLevel2ClientInterface(final QName name, final Wadl<File> wadl,
            final ModelIndex unused, final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                new WadlWalker(new Level2ClientInterfaceWriter(name.getNamespaceURI(), name.getLocalPart(),
                        wadl.getSource(), jsw)).walk(wadl.getApplication());
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }

    private static final void writeLevel3ClientInterface(final QName name, final Wadl<File> wadl,
            final ModelIndex model, final File file, final JavaGenConfig config) {
        try {
            final OutputStream outstream = new BufferedOutputStream(new FileOutputStream(file));
            try {
                writeLevel3ClientInterface(name, wadl, model, outstream, config);
            } finally {
                IOUtils.closeQuietly(outstream);
            }
        } catch (final FileNotFoundException e) {
            LOG.warn(e.getMessage());
        }
    }

    private static final void writeLevel3ClientInterface(final QName name, final Wadl<File> wadl,
            final ModelIndex unused, final OutputStream outstream, final JavaGenConfig config) {
        final JavaOutputFactory jof = JavaOutputFactory.newInstance();
        try {
            final JavaStreamWriter jsw = jof.createJavaStreamWriter(outstream, "UTF-8", config);
            try {
                new WadlWalker(new Level3ClientInterfaceWriter(name.getNamespaceURI(), name.getLocalPart(),
                        wadl.getSource(), jsw)).walk(wadl.getApplication());
            } finally {
                jsw.flush();
            }
        } catch (final IOException e) {
            throw new SdkGenRuntimeException(e);
        }
    }
}
