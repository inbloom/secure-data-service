package org.slc.sli.modeling.docgen;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * Command Line Interface for generating the intermediate data file for documentation.
 */
public final class DocGen {

    private static final List<String> ARGUMENT_HELP = asList("h", "?");
    private static final String ARGUMENT_DOMAIN_FILE = "domainFile";
    private static final String ARGUMENT_XMI = "xmiFile";
    private static final String ARGUMENT_OUT_FILE = "outFile";
    private static final String ARGUMENT_OUT_FOLDER = "outFolder";

    public static void main(final String[] args) {
        final OptionParser parser = new OptionParser();
        final OptionSpec<?> helpSpec = parser.acceptsAll(ARGUMENT_HELP, "Show help");
        final OptionSpec<File> domainFileSpec = optionSpec(parser, ARGUMENT_DOMAIN_FILE, "Domain file", File.class);
        final OptionSpec<File> xmiFileSpec = optionSpec(parser, ARGUMENT_XMI, "XMI file", File.class);
        final OptionSpec<String> outFileSpec = optionSpec(parser, ARGUMENT_OUT_FILE, "Output file", String.class);
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
                final File xmiFile = options.valueOf(xmiFileSpec);
                final ModelIndex model = new DefaultModelIndex(XmiReader.readModel(xmiFile));
                final File domainFile = options.valueOf(domainFileSpec);
                final Documentation<Type> domains = DocumentationReader.readDocumentation(domainFile, model);
                final File outFolder = options.valueOf(outFolderSpec);
                final String outFile = options.valueOf(outFileSpec);
                final File outLocation = new File(outFolder, outFile);
                DocumentationWriter.writeDocument(domains, model, outLocation);
            } catch (final FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final <T> OptionSpec<T> optionSpec(final OptionParser parser, final String option,
            final String description, final Class<T> argumentType) {
        return parser.accepts(option, description).withRequiredArg().ofType(argumentType).required();
    }
}
