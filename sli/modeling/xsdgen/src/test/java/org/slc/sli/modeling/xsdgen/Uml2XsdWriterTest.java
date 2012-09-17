package org.slc.sli.modeling.xsdgen;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.psm.PsmCollection;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.psm.PsmResource;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.Visitor;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xsd.XsdReader;

public class Uml2XsdWriterTest {
	
	private final static String TARGET_NAMESPACE = "namespace";
	private final static Multiplicity MULTIPLICITY = new Multiplicity(new Range(Occurs.UNBOUNDED, Occurs.UNBOUNDED));

	//
	private List<PsmDocument<Type>> documents;
	private ModelIndex model;
	private Uml2XsdPlugin plugin;
	private String filename;
	

	@Test
	public void testByFile() throws FileNotFoundException {
		
		File file = new File(filename);
		
		//write schema
		Uml2XsdWriter.writeSchema(documents, model, plugin, file);
		
		try {
			//read schema
			XsdReader.readSchema(file, null);
		} catch (AssertionError e) {
			//if the XSD cannot be read then the tested class produces bogus data
			fail();
		} finally {
			file.delete();
		}
		
	}

	@Test
	public void testByString() throws FileNotFoundException {
		
		
		//write schema
		Uml2XsdWriter.writeSchema(documents, model, plugin, filename);
		
		try {
			//read schema
			XsdReader.readSchema(filename, null);
		} catch (AssertionError e) {
			//if the XSD cannot be read then the tested class produces bogus data
			fail();
		} finally {
			File file = new File(filename);
			file.delete();
		}
		
	}
	
	@Test (expected = UnsupportedOperationException.class)
	public void testUninstantiatable() {
		new Uml2XsdWriter();
	}
	
	
	@Before
	public void before() {
		List<TaggedValue> enumTaggedValues = new ArrayList<TaggedValue>();
		Map<Identifier, TagDefinition> tagDefinitionRepo = new HashMap<Identifier, TagDefinition>();
		
		
		this.setupTag("documentation", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("maxLength", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("minLength", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("maxExclusive", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("minExclusive", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("maxInclusive", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("minInclusive", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("totalDigits", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("fractionDigits", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("length", enumTaggedValues, tagDefinitionRepo);
		this.setupTag("pattern", enumTaggedValues, tagDefinitionRepo);

		
		Identifier identifier = Identifier.random();
		String enumName = "enumName";
		Identifier identifier2 = Identifier.random();
		String enumName2 = "enumName2";
		
		List<EnumType> enumTypes = new ArrayList<EnumType>();
		enumTypes.add(new EnumType(identifier, enumName, new ArrayList<EnumLiteral>(), enumTaggedValues));
		enumTypes.add(new EnumType(identifier2, enumName2, new ArrayList<EnumLiteral>(), enumTaggedValues));
		
		AssociationEnd lhs = new AssociationEnd(MULTIPLICITY, "lhs", false, Identifier.random(), "lhs");
		AssociationEnd rhs = new AssociationEnd(MULTIPLICITY, "rhs", false, Identifier.random(), "rhs");
		Identifier myClassTypeIdentifier = Identifier.random();
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new Attribute(Identifier.random(), "myAttribute", Identifier.random(), MULTIPLICITY, new ArrayList<TaggedValue>()));
		
		ClassType myClassType = new ClassType(myClassTypeIdentifier, "name", false, attributes, lhs, rhs, new ArrayList<TaggedValue>());
		
		Map<String, ClassType> classTypes = new HashMap<String, ClassType>();
		classTypes.put("myClassType", myClassType);
		
		List<AssociationEnd> associationEnds = new ArrayList<AssociationEnd>();
		associationEnds.add(lhs);
		associationEnds.add(rhs);
		
		Map<Identifier, List<AssociationEnd>> allAssociationEnds = new HashMap<Identifier, List<AssociationEnd>>();
		allAssociationEnds.put(myClassTypeIdentifier, associationEnds);
		
		
		List<Generalization> generalizations = new ArrayList<Generalization>();
		generalizations.add(new Generalization(Identifier.random(), Identifier.random()));
		
		Map<Identifier, List<Generalization>> allGeneralizations = new HashMap<Identifier, List<Generalization>>();
		allGeneralizations.put(identifier, generalizations);
		allGeneralizations.put(identifier2, new ArrayList<Generalization>());
		
		this.documents = new ArrayList<PsmDocument<Type>>();
		this.documents.add(new PsmDocument<Type>(mock(Type.class), new PsmResource("PsmResource"), new PsmCollection("PsmCollection")));
		this.model = this.createModelIndex(enumTypes, tagDefinitionRepo, classTypes, allAssociationEnds, allGeneralizations);
		this.plugin = this.createUml2XsdPlugin();
		this.filename = "test.xsd";
		
	}
	
	private Uml2XsdPlugin createUml2XsdPlugin() {
		return new Uml2XsdPlugin() {

			@Override
			public Map<String, String> declarePrefixMappings() {
				return new HashMap<String, String>();
			}

			@Override
			public QName getElementName(String name, boolean isAssociation) {
				return new QName(TARGET_NAMESPACE, name, "prefix");
			}

			@Override
			public QName getElementType(String name, boolean isAssociation) {
				return new QName(TARGET_NAMESPACE, name, "prefix");
			}

			@Override
			public QName getGraphAssociationEndName(PsmDocument<Type> classType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public QName getElementName(PsmDocument<Type> classType) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getTargetNamespace() {
				return TARGET_NAMESPACE;
			}

			@Override
			public QName getTypeName(String name) {
				return new QName(TARGET_NAMESPACE, name, "prefix");
			}

			@Override
			public boolean isAttributeFormDefaultQualified() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isElementFormDefaultQualified() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isEnabled(QName name) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void writeAppInfo(TaggedValue taggedValue,
					ModelIndex lookup, Uml2XsdPluginWriter xsw) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void writeAssociation(ClassType complexType,
					AssociationEnd element, ModelIndex lookup,
					Uml2XsdPluginWriter xsw) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void writeTopLevelElement(PsmDocument<Type> classType,
					ModelIndex model, Uml2XsdPluginWriter xsw) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	private void setupTag(String name, List<TaggedValue> enumTaggedValues, Map<Identifier, TagDefinition> tagDefinitionRepo) {
		Identifier tagIdentifier = Identifier.random();
		TagDefinition tagDefinition = new TagDefinition(tagIdentifier, name, MULTIPLICITY);
		TaggedValue taggedValue = new TaggedValue(name, tagIdentifier);
		
		enumTaggedValues.add(taggedValue);
		tagDefinitionRepo.put(tagIdentifier, tagDefinition);
	}
	
	private ModelIndex createModelIndex(final List<EnumType> enumTypes, 
			final Map<Identifier, TagDefinition> tagDefinitionRepo, 
			final Map<String, ClassType> classTypes,
			final Map<Identifier, List<AssociationEnd>> allAssociationEnds,
			final Map<Identifier, List<Generalization>> allGeneralizations) {
		return new ModelIndex() {

			@Override
			public List<AssociationEnd> getAssociationEnds(Identifier type) {
				return allAssociationEnds.get(type);
			}

			@Override
			public Map<String, ClassType> getClassTypes() {
				return classTypes;
			}

			@Override
			public Map<QName, DataType> getDataTypes() {
				return new HashMap<QName, DataType>();
			}

			@Override
			public Iterable<EnumType> getEnumTypes() {
				return enumTypes;
			}

			@Override
			public List<Generalization> getGeneralizationBase(Identifier derived) {
				List<Generalization> generalizations = allGeneralizations.get(derived);
				
				if (generalizations != null) {
					return generalizations;
				} else {
					return new ArrayList<Generalization>();
				}
			}

			@Override
			public List<Generalization> getGeneralizationDerived(Identifier base) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getNamespaceURI(Type type) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public TagDefinition getTagDefinition(Identifier reference) {
				return tagDefinitionRepo.get(reference);
			}

			@Override
			public TagDefinition getTagDefinition(QName name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Type getType(Identifier reference) {
				return new Type() {

					@Override
					public Identifier getId() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getName() {
						return "TypeName";
					}

					@Override
					public boolean isAbstract() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public List<TaggedValue> getTaggedValues() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean isClassType() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean isEnumType() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean isDataType() {
						// TODO Auto-generated method stub
						return false;
					}
					
				};
			}

			@Override
			public void lookup(Identifier id, Visitor visitor) {
				// TODO Auto-generated method stub
				
			}

			@Override
			@Deprecated
			public
			Set<ModelElement> lookupByName(QName name) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set<ModelElement> whereUsed(Identifier id) {
				return new HashSet<ModelElement>();
			}
			
		};
	}
	
	
}
