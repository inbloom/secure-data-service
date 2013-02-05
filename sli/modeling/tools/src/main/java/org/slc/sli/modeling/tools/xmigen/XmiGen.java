/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.modeling.tools.xmigen;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.apache.ws.commons.schema.XmlSchema;

import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.writer.XmiWriter;
import org.slc.sli.modeling.xsd.XsdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A quick-n-dirty utility for converting W3C XML Schemas to XMI (with limitations).
 * <p>
 * This is a command line utility. A typical invocation is as follows:
 * <code>--xsdFile SLI.xsd --xmiFile SLI.xmi --xmiFolder . --plugInName Xsd2UmlPlugInForSLI</code>
 * </p>
 */
public final class XmiGen {

    private static final Logger LOG = LoggerFactory.getLogger(XmiGen.class);

    private static final List<String> ARGUMENT_HELP = asList("h", "?");
    private static final String ARGUMENT_XSD = "xsdFile";
    private static final String ARGUMENT_XMI_FILE = "xmiFile";
    private static final String ARGUMENT_XMI_FOLDER = "xmiFolder";
    private static final String ARGUMENT_PLUGIN_NAME = "plugInName";

    /**
     * This is the entry point for the command line interface.
     */
    public static void main(final String[] args) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<?> helpSpec = parser.acceptsAll(ARGUMENT_HELP, "Show help");
        final OptionSpec<File> xsdFileSpec = optionSpec(parser, ARGUMENT_XSD, "XSD (input) file", File.class);
        final OptionSpec<String> outFileSpec = optionSpec(parser, ARGUMENT_XMI_FILE, "XMI (output) file", String.class);
        final OptionSpec<File> outFolderSpec = optionSpec(parser, ARGUMENT_XMI_FOLDER, "XMI (output) folder",
                File.class);
        final OptionSpec<String> plugInNameSpec = optionSpec(parser, ARGUMENT_PLUGIN_NAME, "PlugIn name", String.class);
        try {
            final OptionSet options = parser.parse(args);
            if (options.hasArgument(helpSpec)) {
                try {
                    parser.printHelpOn(System.out);
                } catch (final IOException e) {
                    throw new XmiGenRuntimeException(e);
                }
            } else {
                try {
                    final File xsdFile = options.valueOf(xsdFileSpec);

                    final File outFolder = options.valueOf(outFolderSpec);
                    final String outFile = options.valueOf(outFileSpec);
                    final File outLocation = new File(outFolder, outFile);
                    // The platform-specific model provides the implementation mappings.
                    final String plugInName = options.valueOf(plugInNameSpec);
                    final Xsd2UmlHostedPlugin plugIn = loadPlugIn(plugInName);
                    final XmlSchema schema = XsdReader.readSchema(xsdFile,
                            new ParentFileURIResolver(xsdFile.getParentFile()));

                    final Model model = Xsd2Uml.transform("", schema, plugIn);

                    final ModelIndex modelIndex = new DefaultModelIndex(model);

                    XmiWriter.writeDocument(model, modelIndex, outLocation);
                } catch (final FileNotFoundException e) {
                    throw new XmiGenRuntimeException(e);
                }
            }
        } catch (final OptionException e) {
            // Caused by illegal arguments.
            LOG.warn(e.getMessage());
        } catch (final ClassNotFoundException e) {
            // Caused by not being able to load the plug-in.
            LOG.warn("Unable to load plugin specified in " + ARGUMENT_PLUGIN_NAME + " argument: "
                    + e.getMessage());
        }
    }

    private static final Xsd2UmlHostedPlugin loadPlugIn(final String name) throws ClassNotFoundException {
        final Class<?> clazz = Class.forName(name);
        final Class<? extends Xsd2UmlHostedPlugin> factory = clazz.asSubclass(Xsd2UmlHostedPlugin.class);
        try {
            return factory.newInstance();
        } catch (final InstantiationException e) {
            throw new XmiGenRuntimeException(name, e);
        } catch (final IllegalAccessException e) {
            throw new XmiGenRuntimeException(name, e);
        }
    }

    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }
}
