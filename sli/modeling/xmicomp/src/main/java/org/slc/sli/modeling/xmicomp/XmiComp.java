package org.slc.sli.modeling.xmicomp;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A quick-n-dirty utility for converting W3C XML Schemas to XMI (with
 * limitations).
 * <p>
 * This is a command line utility. A typical invocation is as follows:
 * <code>--xsdFile SLI.xsd --xmiFile SLI.xmi --xmiFolder . --plugInName Xsd2UmlPlugInForSLI</code>
 * </p>
 */
public final class XmiComp {

	private static final List<String> ARG_HELP = asList("h", "?");
	private static final String ARG_MAP_FILE = "mapFile";
	private static final String ARG_LHS_XMI_FILE = "lhsFile";
	private static final String ARG_RHS_XMI_FILE = "rhsFile";

	/**
	 * This is the entry point for the command line interface.
	 */
	public static void main(final String[] args) {
		final OptionParser parser = new OptionParser();
		final OptionSpec<?> helpSpec = parser.acceptsAll(ARG_HELP, "Show help");
		final OptionSpec<File> mappingSpec = optionSpec(parser, ARG_MAP_FILE,
				"Mapping (input) file", File.class);
		final OptionSpec<File> lhsFileSpec = optionSpec(parser,
				ARG_LHS_XMI_FILE, "LHS XMI (input) file", File.class);
		final OptionSpec<File> rhsFileSpec = optionSpec(parser,
				ARG_RHS_XMI_FILE, "RHS XMI (input) file", File.class);
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
					final File mappingFile = options.valueOf(mappingSpec);
					final File lhsFile = options.valueOf(lhsFileSpec);
					final File rhsFile = options.valueOf(rhsFileSpec);

					final XmiMappingDocument mapping = XmiMappingReader
							.readDocument(mappingFile);
					final ModelIndex lhsModel = new DefaultModelIndex(
							XmiReader.readModel(lhsFile));
					final ModelIndex rhsModel = new DefaultModelIndex(
							XmiReader.readModel(rhsFile));

					checkMappingDocument(mapping, lhsModel, rhsModel);
				} catch (final FileNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		} catch (final OptionException e) {
			// Caused by illegal arguments.
			System.err.println(e.getMessage());
		}
	}

	private static final <T> OptionSpec<T> optionSpec(
			final OptionParser parser, final String option,
			final String description, final Class<T> argumentType) {
		return parser.accepts(option, description).withRequiredArg()
				.ofType(argumentType).required();
	}

	private static final void checkMappingDocument(
			final XmiMappingDocument mappingDocument,
			final ModelIndex lhsModel, final ModelIndex rhsModel) {
		final List<XmiMapping> mappings = mappingDocument.getMappings();
		for (final XmiMapping mapping : mappings) {
			checkMapping(mappingDocument, mapping, lhsModel, rhsModel);
		}
	}

	private static final void checkMapping(
			final XmiMappingDocument mappingDocument, final XmiMapping mapping,
			final ModelIndex lhsModel, final ModelIndex rhsModel) {
		if (mapping == null) {
			throw new NullPointerException("mapping");
		}
		final XmiMappingStatus status = mapping.getStatus();
		final XmiFeature lhsFeature = mapping.getLhsFeature();
		if (lhsFeature != null) {
			checkFeature(mappingDocument.getLhsModel(), lhsFeature, lhsModel,
					status);
		}
		final XmiFeature rhsFeature = mapping.getRhsFeature();
		if (rhsFeature != null) {
			checkFeature(mappingDocument.getRhsModel(), rhsFeature, rhsModel,
					status);
		}
	}

	private static final void checkFeature(final XmiMappingModel context,
			final XmiFeature feature, final ModelIndex model,
			final XmiMappingStatus status) {
		final QName name = feature.getName();
		final QName type = feature.getType();
		if (model.getClassTypes().containsKey(type)) {

		} else {
			System.err.println(type + "." + name + " in " + context.getName()
					+ " is not a recognized type. status : " + status);
		}
	}
}
