package org.slc.sli.modeling.xsdgen;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.psm.PsmConfigReader;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * Command Line Interface for generating the intermediate data file for documentation.
 */
public final class XsdGen {

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
        final OptionSet options = parser.parse(args);
        if (options.hasArgument(helpSpec)) {
            try {
                parser.printHelpOn(System.out);
            } catch (final IOException e) {
                throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }
        }
    }

    private static final Uml2XsdPlugin loadPlugIn(final String name) {
        try {
            final Class<?> clazz = Class.forName(name);
            final Class<? extends Uml2XsdPlugin> factory = clazz.asSubclass(Uml2XsdPlugin.class);
            try {
                return factory.newInstance();
            } catch (final InstantiationException e) {
                throw new RuntimeException(name, e);
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(name, e);
            }
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(name, e);
        }

    }

    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }
}
