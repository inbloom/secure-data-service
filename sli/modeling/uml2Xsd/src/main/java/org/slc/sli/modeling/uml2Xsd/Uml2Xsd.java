package org.slc.sli.modeling.uml2Xsd;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.modeling.psm.PsmClassType;
import org.slc.sli.modeling.psm.PsmCollection;
import org.slc.sli.modeling.psm.PsmConfig;
import org.slc.sli.modeling.psm.PsmResource;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultMapper;
import org.slc.sli.modeling.uml.index.Mapper;
import org.slc.sli.modeling.xmi.reader.XmiReader;

/**
 * A utility for converting UML to Documentation.
 */
public final class Uml2Xsd {

	public static void main(final String[] args) {
		try {
			final Mapper model = new DefaultMapper(
					XmiReader.readModel("../data/SLI.xmi"));
			final PsmConfig<Type> psmConfig = extractConfig(model);
			// final PsmConfig<Identifier> temp =
			// PsmConfigReader.readConfig("../data/sli-psm-cfg.xml");
			// final PsmConfig<Type> psmConfig = PsmConfigExpander.expand(temp,
			// model);
			Uml2XsdWriter.writeDocument(psmConfig.getClassTypes(), model,
					new PluginForJson(), "sli-api-json.xsd");
		} catch (final FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private Uml2Xsd() {
		// Prevent instantiation, even through reflection.
		throw new RuntimeException();
	}

	private static final PsmConfig<Type> extractConfig(final Mapper mapper) {
		final List<PsmClassType<Type>> classTypes = new LinkedList<PsmClassType<Type>>();
		for (final ClassType classType : mapper.getClassTypes()) {
			final List<TaggedValue> taggedValues = classType.getTaggedValues();
			for (final PsmResource resource : getResources(taggedValues, mapper)) {
				// The collection doesn't matter for this exercise.
				final PsmCollection collection = new PsmCollection("!@#$%^&*()");
				classTypes.add(new PsmClassType<Type>(classType, resource,
						collection));
			}
		}
		return new PsmConfig<Type>(classTypes);
	}

	private static final List<PsmResource> getResources(
			final List<TaggedValue> taggedValues, final Mapper mapper) {
		final List<PsmResource> resources = new LinkedList<PsmResource>();
		for (final TaggedValue taggedValue : taggedValues) {
			final Identifier tagDefinitionId = taggedValue.getTagDefinition();
			final TagDefinition tagDefinition = mapper
					.getTagDefinition(tagDefinitionId);
			if (tagDefinition.getName().equals("resource")) {
				final String value = taggedValue.getValue();
				resources.add(new PsmResource(value));
			}
		}
		return resources;
	}
}
