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


package org.slc.sli.modeling.docgen;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Command Line Interface for generating the intermediate data file for documentation.
 */
public final class DocGen {
    private static final Logger LOG = LoggerFactory.getLogger(DocGen.class);

    public DocGen() {
        throw new UnsupportedOperationException();
    }

    private static final List<String> ARGUMENT_HELP = asList("h", "?");
    private static final String ARGUMENT_DOMAIN_FILE = "domainFile";
    private static final String ARGUMENT_XMI = "xmiFile";
    private static final String ARGUMENT_OUT_FILE = "outFile";
    private static final String ARGUMENT_OUT_FOLDER = "outFolder";

    private static final List<String> ALL_OPTIONS = new ArrayList<String>();

    static {
        ALL_OPTIONS.addAll(ARGUMENT_HELP);
        ALL_OPTIONS.add(ARGUMENT_DOMAIN_FILE);
        ALL_OPTIONS.add(ARGUMENT_XMI);
        ALL_OPTIONS.add(ARGUMENT_OUT_FILE);
        ALL_OPTIONS.add(ARGUMENT_OUT_FOLDER);
    }

    private static final OptionParser HELP_PARSER = new OptionParser();

    static {
        HELP_PARSER.acceptsAll(ALL_OPTIONS);
    }

    private static final OptionSpec<?> HELP_SPEC = HELP_PARSER.acceptsAll(ARGUMENT_HELP, "Show help");


    public static void main(final String[] args) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<File> domainFileSpec = optionSpec(parser, ARGUMENT_DOMAIN_FILE, "Domain file", File.class);
        final OptionSpec<File> xmiFileSpec = optionSpec(parser, ARGUMENT_XMI, "XMI file", File.class);
        final OptionSpec<String> outFileSpec = optionSpec(parser, ARGUMENT_OUT_FILE, "Output file", String.class);
        final OptionSpec<File> outFolderSpec = optionSpec(parser, ARGUMENT_OUT_FOLDER, "Output folder", File.class);
        try {

            if (HELP_PARSER.parse(args).has(HELP_SPEC)) {
                try {
                    parser.printHelpOn(System.out);
                } catch (final IOException e) {
                    throw new DocumentGeneratorRuntimeException(e);
                }
            } else {

                OptionSet options = parser.parse(args);
                try {
                    final File xmiFile = options.valueOf(xmiFileSpec);
                    final ModelIndex model = new DefaultModelIndex(XmiReader.readModel(xmiFile));
                    final File domainFile = options.valueOf(domainFileSpec);
                    final Documentation<Type> domains = DocumentationReader.readDocumentation(domainFile, model);
                    final File outFolder = options.valueOf(outFolderSpec);
                    final String outFile = options.valueOf(outFileSpec);
                    final File outLocation = new File(outFolder, outFile);
                    DocumentationWriter.writeDocument(domains, model, outLocation);
                } catch (final FileNotFoundException e) {
                    throw new DocumentGeneratorRuntimeException(e);
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
}
