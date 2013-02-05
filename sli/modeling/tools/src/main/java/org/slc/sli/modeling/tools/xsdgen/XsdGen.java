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


package org.slc.sli.modeling.tools.xsdgen;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.tools.xmi2Psm.PsmConfigReader;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command Line Interface for generating the intermediate data file for documentation.
 */
public final class XsdGen {

    private static final Logger LOG = LoggerFactory.getLogger(XsdGen.class);

    private static final List<String> ARGUMENT_HELP = asList("h", "?");
    private static final String ARGUMENT_DOCUMENT_FILE = "documentFile";
    private static final String ARGUMENT_XMI = "xmiFile";
    private static final String ARGUMENT_OUT_FILE = "outFile";
    private static final String ARGUMENT_OUT_FOLDER = "outFolder";
    private static final String ARGUMENT_PLUGIN_NAME = "plugInName";

    public static void main(final String[] args) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<?> helpSpec = parser.acceptsAll(ARGUMENT_HELP, "Show help");
        final OptionSpec<File> documentFileSpec = optionSpec(parser, ARGUMENT_DOCUMENT_FILE, "Domain file", File.class);
        final OptionSpec<File> xmiFileSpec = optionSpec(parser, ARGUMENT_XMI, "XMI file", File.class);
        final OptionSpec<String> outFileSpec = optionSpec(parser, ARGUMENT_OUT_FILE, "Output file", String.class);
        final OptionSpec<File> outFolderSpec = optionSpec(parser, ARGUMENT_OUT_FOLDER, "Output folder", File.class);
        final OptionSpec<String> plugInNameSpec = optionSpec(parser, ARGUMENT_PLUGIN_NAME, "PlugIn name", String.class);
        try {
            final OptionSet options = parser.parse(args);
            if (options.hasArgument(helpSpec)) {
                try {
                    parser.printHelpOn(System.out);
                } catch (final IOException e) {
                    throw new XsdGenRuntimeException(e);
                }
            } else {
                try {
                    final File xmiFile = options.valueOf(xmiFileSpec);
                    // The UML model provides the types for the logical model.
                    final ModelIndex model = new DefaultModelIndex(XmiReader.readModel(xmiFile));

                    // The document file provides the top-level elements.
                    final File documentFile = options.valueOf(documentFileSpec);
                    final PsmConfig<Type> psmConfig = PsmConfigReader.readConfig(documentFile, model);

                    // The platform-specific model provides the implementation mappings.
                    final String plugInName = options.valueOf(plugInNameSpec);
                    final Uml2XsdPlugin plugIn = loadPlugIn(plugInName);

                    // Write the XML Schema file to the location and file specified.
                    final File outFolder = options.valueOf(outFolderSpec);
                    final String outFile = options.valueOf(outFileSpec);
                    final File outLocation = new File(outFolder, outFile);
                    Uml2XsdWriter.writeSchema(psmConfig.getDocuments(), model, plugIn, outLocation);
                } catch (final FileNotFoundException e) {
                    throw new XsdGenRuntimeException(e);
                }
            }
        } catch (final OptionException e) {
            // Caused by illegal arguments.
            LOG.error(e.getMessage());
        } catch (final ClassNotFoundException e) {
            // Caused by not being able to load the plug-in.
            LOG.warn("Unable to load plugin specified in " + ARGUMENT_PLUGIN_NAME + " argument: "
                    + e.getMessage());
        }

    }

    private static final Uml2XsdPlugin loadPlugIn(final String name) throws ClassNotFoundException {
        final Class<?> clazz = Class.forName(name);
        final Class<? extends Uml2XsdPlugin> factory = clazz.asSubclass(Uml2XsdPlugin.class);
        try {
            return factory.newInstance();
        } catch (final InstantiationException e) {
            throw new XsdGenRuntimeException(name, e);
        } catch (final IllegalAccessException e) {
            throw new XsdGenRuntimeException(name, e);
        }
    }

    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }
}
