package org.slc.sli.validation.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaContent;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentExtension;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeList;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.slc.sli.validation.ListSchema;
import org.slc.sli.validation.NeutralSchema;
import org.slc.sli.validation.NeutralSchemaFactory;
import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.TokenSchema;
import org.springframework.util.ResourceUtils;
import org.xml.sax.InputSource;

/**
 * Generation tool used to convert XSD to SLI Neutral Schema.
 * This class leverages the prior art/work by Ryan Farris to convert XSD to Avro style schemas.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
public class XsdToNeutralSchema {
    
    // Logging
    private static final Log LOG = LogFactory.getLog(XsdToNeutralSchema.class);
    
    // Constants
    public static final String PARAM_INPUT_PATH = "-i";
    public static final String PARAM_OUTPUT_PATH = "-o";
    public static final String PARAM_REPRESENTATION = "-r";
    public static final String DEFAULT_INPUT_XSD_PATH = "xsd";
    public static final String DEFAULT_OUTPUT_SCHEMA_PATH = "neutral-schemas/";
    public static final String XSD = "xsd";
    public static final String JSON = "json";
    public static final String XML = "xml";
    public static final String DEFAULT_REPRESENTATION = JSON;
    public static final boolean GENERATE_COMBINED_FILES = false;
    
    // XSD to SLI Neutral Schema Generation Tool
    public static void main(String[] args) throws IOException {
        Map<String, String> argumentsMap = parseArguments(args);
        if (argumentsMap == null) {
            System.out
                    .println("Usage: XsdToNeutralSchema [-i xmlInputDirectory] [-o schemaOutputDirectory] [-r schemaRepresentation [json | xml]]");
            System.out.println("Defaults: XsdToNeutralSchema [-i " + DEFAULT_INPUT_XSD_PATH + "] [-o "
                    + DEFAULT_OUTPUT_SCHEMA_PATH + "] [-r " + DEFAULT_REPRESENTATION + " [json | xml]]");
        } else {
            XsdToNeutralSchema tool = new XsdToNeutralSchema();
            if (argumentsMap.containsKey(PARAM_INPUT_PATH)) {
                tool.setXsdPath(argumentsMap.get(PARAM_INPUT_PATH));
            }
            if (argumentsMap.containsKey(PARAM_OUTPUT_PATH)) {
                tool.setSchemaPath(argumentsMap.get(PARAM_OUTPUT_PATH));
            }
            if (argumentsMap.containsKey(PARAM_REPRESENTATION)) {
                tool.setRepresentation(argumentsMap.get(PARAM_REPRESENTATION));
            }
            tool.generateSchemas();
        }
    }
    
    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> argumentsMap = new HashMap<String, String>();
        
        int index = 0;
        while (index < args.length) {
            if (((index % 1) == 0) && (args[index].startsWith("-")) && ((index + 1) < args.length)) {
                String key = args[index++];
                String value = args[index];
                if (value != null) {
                    argumentsMap.put(key, value);
                } else {
                    return null;
                }
            } else {
                return null;
            }
            index++;
        }
        
        return argumentsMap;
    }
    
    // Attributes
    private String xsdPath;
    private String schemaPath;
    private String representation;
    private XmlSchema xmlSchema;
    private NeutralSchemaFactory schemaFactory;
    
    // Constructors
    public XsdToNeutralSchema() {
        this.setXsdPath(DEFAULT_INPUT_XSD_PATH);
        this.setSchemaPath(DEFAULT_OUTPUT_SCHEMA_PATH);
        this.setRepresentation(DEFAULT_REPRESENTATION);
        this.setSchemaFactory(new NeutralSchemaFactory());
    }
    
    // Methods
    public void setXsdPath(String xsdPath) {
        this.xsdPath = xsdPath;
    }
    
    public String getXsdPath() {
        return this.xsdPath;
    }
    
    public void setSchemaPath(String schemaPath) {
        this.schemaPath = schemaPath;
        if (!this.schemaPath.endsWith("/")) {
            this.schemaPath += "/";
        }
    }
    
    public String getSchemaPath() {
        return this.schemaPath;
    }
    
    public void setRepresentation(String representation) {
        this.representation = representation;
    }
    
    public String getRepresentation() {
        return this.representation;
    }
    
    public void setXmlSchema(XmlSchema xmlSchema) {
        this.xmlSchema = xmlSchema;
    }
    
    public XmlSchema getXmlSchema() {
        return this.xmlSchema;
    }
    
    public void setSchemaFactory(NeutralSchemaFactory schemaFactory) {
        this.schemaFactory = schemaFactory;
    }
    
    public NeutralSchemaFactory getSchemaFactory() {
        return this.schemaFactory;
    }
    
    public void generateSchemas() throws IOException {
        
        LOG.info("Starting XSD -> NeutralSchema Generator...");
        LOG.info("Using XML Schema Directory Path: " + this.getXsdPath());
        LOG.info("Using SLI Schema Directory Path: " + this.getSchemaPath());
        LOG.info("Generating Schema Representation: " + this.getRepresentation());
        
        // Scan XML Schemas on path
        List<XmlSchema> xmlSchemas = this.parseXmlSchemas(this.getXsdPath(), XSD);
        
        long totalSchemaCount = 0;
        List primitiveList = new ArrayList();
        List simpleList = new ArrayList();
        List complexList = new ArrayList();
        
        try {
            
            String combinedDirectory = this.getSchemaPath() + "combined/";
            String primitiveDirectory = this.getSchemaPath() + "primitive/";
            String simpleDirectory = this.getSchemaPath() + "simple/";
            String complexDirectory = this.getSchemaPath() + "complex/";
            
            (new File(primitiveDirectory)).mkdirs();
            (new File(simpleDirectory)).mkdirs();
            (new File(complexDirectory)).mkdirs();
            
            // Iterate XML Schemas
            for (XmlSchema schema : xmlSchemas) {
                
                // Set Current XML Schema
                this.setXmlSchema(schema);
                XmlSchemaObjectCollection schemaItems = schema.getItems();
                
                // Iterate XML Schema items
                for (int i = 0; i < schemaItems.getCount(); i++) {
                    XmlSchemaObject schemaObject = schemaItems.getItem(i);
                    
                    String currentSchemaDirectory = "";
                    if (schemaObject instanceof XmlSchemaType) {
                        NeutralSchema neutralSchema = this.parse((XmlSchemaType) schemaObject);
                        if (neutralSchema != null) {
                            if (neutralSchema.isPrimitive()) {
                                currentSchemaDirectory = primitiveDirectory;
                                primitiveList.add(neutralSchema);
                            } else if (neutralSchema.isSimple()) {
                                currentSchemaDirectory = simpleDirectory;
                                simpleList.add(neutralSchema);
                            } else if (!(neutralSchema.isPrimitive() || neutralSchema.isSimple())) {
                                currentSchemaDirectory = complexDirectory;
                                complexList.add(neutralSchema);
                            }
                            
                            // Create individual Schema file
                            this.getSchemaFactory().toFile(currentSchemaDirectory, neutralSchema, representation, true);
                            totalSchemaCount++;
                        }
                    } else if (schemaObject instanceof XmlSchemaElement) {
                        NeutralSchema neutralSchema = parseElement((XmlSchemaElement) schemaObject);
                        // copy and paste. sweeeet.
                        if (neutralSchema.isPrimitive()) {
                            currentSchemaDirectory = primitiveDirectory;
                            primitiveList.add(neutralSchema);
                        } else if (neutralSchema.isSimple()) {
                            currentSchemaDirectory = simpleDirectory;
                            simpleList.add(neutralSchema);
                        } else if (!(neutralSchema.isPrimitive() || neutralSchema.isSimple())) {
                            currentSchemaDirectory = complexDirectory;
                            complexList.add(neutralSchema);
                        }
                        
                        // Create individual Schema file
                        this.getSchemaFactory().toFile(currentSchemaDirectory, neutralSchema, representation, true);
                        totalSchemaCount++;
                    }
                }
            }
            
            if (GENERATE_COMBINED_FILES) {
                this.getSchemaFactory().toFile(combinedDirectory, "combinedPrimitive" + "." + this.getRepresentation(),
                        primitiveList, this.getRepresentation(), true);
                this.getSchemaFactory().toFile(combinedDirectory, "combinedSimple" + "." + this.getRepresentation(),
                        simpleList, this.getRepresentation(), true);
                this.getSchemaFactory().toFile(combinedDirectory, "combinedComplex" + "." + this.getRepresentation(),
                        complexList, this.getRepresentation(), true);
            }
            
        } catch (Exception exception) {
            LOG.error(exception);
        }
        
        LOG.info("Statistics:");
        LOG.info("Xml Total Schema Files Parsed: " + xmlSchemas.size());
        LOG.info("Xml Total Schema Count: " + totalSchemaCount);
        LOG.info("Xml Primitive Schema Count: " + primitiveList.size());
        LOG.info("Xml Simple Schema Count: " + simpleList.size());
        LOG.info("Xml Complex Schema Count: " + complexList.size());
        
        LOG.info("Finished.");
    }
    
    private List<XmlSchema> parseXmlSchemas(String xsdPath, String schemaRepresentation) {
        
        List<XmlSchema> xmlSchemas = new ArrayList<XmlSchema>();
        
        try {
            String xsdClassPath = xsdPath;
            if (!xsdClassPath.startsWith("classpath:")) {
                xsdClassPath = "classpath:" + xsdClassPath;
            }
            URL schemaResourcesUrl = ResourceUtils.getURL(xsdClassPath);
            String protocol = schemaResourcesUrl.getProtocol();
            
            // Process XML schema files found on the file system
            if (protocol.equals("file")) {
                File schemaResourcesDir = FileUtils.toFile(schemaResourcesUrl);
                List<File> schemaFiles = new ArrayList<File>(FileUtils.listFiles(schemaResourcesDir,
                        new String[] { schemaRepresentation }, true));
                for (File schemaFile : schemaFiles) {
                    
                    // Parse XML schema file
                    String schemaResourcePath = "/" + xsdPath;
                    if (!schemaResourcePath.endsWith("/")) {
                        schemaResourcePath += "/";
                    }
                    schemaResourcePath += schemaFile.getName();
                    
                    LOG.info("Parsing Xml Schema: " + schemaResourcePath);
                    
                    XmlSchema schema = this.parseXmlSchema(xsdPath, schemaResourcePath);
                    
                    // Accumulate XML schemas
                    xmlSchemas.add(schema);
                }
            } else {
                LOG.error("Unsupported schema protocol: " + protocol);
            }
            
        } catch (IOException ioException) {
            LOG.error("Unable to parse XML schema resources: " + xsdPath + ": " + ioException.getMessage());
        }
        
        return xmlSchemas;
    }
    
    private XmlSchema parseXmlSchema(final String xsdPath, String resourcePath) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(XsdToNeutralSchema.class.getResourceAsStream(resourcePath));
            XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
            schemaCollection.setSchemaResolver(new URIResolver() {
                @Override
                public InputSource resolveEntity(String targetNamespace, String schemaLocation, String baseUri) {
                    return new InputSource(XsdToNeutralSchema.class.getResourceAsStream("/" + xsdPath + "/"
                            + schemaLocation));
                }
            });
            return schemaCollection.read(reader, null);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioException) {
                    throw new RuntimeException(ioException);
                }
            }
        }
    }
    
    private NeutralSchema parse(XmlSchemaType type) {
        return parse(type, type.getName());
    }
    
    private NeutralSchema parse(XmlSchemaType type, String name) {
        if (type instanceof XmlSchemaComplexType) {
            NeutralSchema complexSchema = this.getSchemaFactory().createSchema(name);
            return parseComplexType((XmlSchemaComplexType) type, complexSchema);
        } else if (type instanceof XmlSchemaSimpleType) {
            return parseSimpleType((XmlSchemaSimpleType) type);
        } else {
            throw new RuntimeException("Unsupported schema type: " + type.getClass().getCanonicalName());
        }
    }
    
    private NeutralSchema parseSimpleType(XmlSchemaSimpleType schemaSimpleType) {
        NeutralSchema simpleSchema = null;
        
        String simpleTypeName = schemaSimpleType.getName();
        
        if (NeutralSchemaType.isPrimitive(schemaSimpleType.getQName())) {
            simpleSchema = this.getSchemaFactory().createSchema(schemaSimpleType.getQName());
        } else if (NeutralSchemaType.exists(schemaSimpleType.getBaseSchemaTypeName())) {
            if (NeutralSchemaType.isPrimitive(schemaSimpleType.getBaseSchemaTypeName())) {
                simpleSchema = this.getSchemaFactory().createSchema(schemaSimpleType.getBaseSchemaTypeName());
            } else {
                XmlSchemaSimpleType simpleBaseType = getSimpleBaseType(schemaSimpleType.getBaseSchemaTypeName());
                if (simpleBaseType != null) {
                    if (simpleTypeName == null) {
                        simpleTypeName = simpleBaseType.getName();
                    }
                    simpleSchema = this.getSchemaFactory().createSchema(simpleTypeName);
                }
            }
        } else if (schemaSimpleType.getContent() != null
                && schemaSimpleType.getContent() instanceof XmlSchemaSimpleTypeList) {
            ListSchema listSchema = (ListSchema) this.getSchemaFactory().createSchema("list");
            
            XmlSchemaSimpleTypeList content = (XmlSchemaSimpleTypeList) schemaSimpleType.getContent();
            NeutralSchema listContentSchema = null;
            if (content.getItemType() != null) {
                listContentSchema = parseSimpleType(content.getItemType());
            } else {
                QName itemTypeName = content.getItemTypeName();
                listContentSchema = this.getSchemaFactory().createSchema(itemTypeName.getLocalPart());
            }
            listSchema.getList().add(listContentSchema);
            return listSchema;
            
        } else if (getSimpleContentTypeName(schemaSimpleType) != null) {
            if (NeutralSchemaType.isPrimitive(getSimpleContentTypeName(schemaSimpleType))) {
                simpleSchema = this.getSchemaFactory().createSchema(getSimpleContentTypeName(schemaSimpleType));
            } else {
                XmlSchemaSimpleType simpleBaseType = getSimpleBaseType(getSimpleContentTypeName(schemaSimpleType));
                if (simpleBaseType != null) {
                    if (simpleTypeName == null) {
                        simpleTypeName = simpleBaseType.getName();
                    }
                    simpleSchema = this.getSchemaFactory().createSchema(simpleTypeName);
                }
            }
        }
        
        if (schemaSimpleType.getContent() != null
                && schemaSimpleType.getContent() instanceof XmlSchemaSimpleTypeRestriction && simpleSchema != null) {
            
            XmlSchemaSimpleTypeRestriction simpleRestrictedContent = (XmlSchemaSimpleTypeRestriction) schemaSimpleType
                    .getContent();
            XmlSchemaObjectCollection facets = simpleRestrictedContent.getFacets();
            List<String> tokens = new ArrayList<String>();
            for (int i = 0; i < facets.getCount(); i++) {
                XmlSchemaObject facetObject = facets.getItem(i);
                
                if (facetObject instanceof XmlSchemaEnumerationFacet) {
                    XmlSchemaEnumerationFacet enumerationFacet = (XmlSchemaEnumerationFacet) facetObject;
                    tokens.add(enumerationFacet.getValue().toString());
                } else if (facetObject instanceof XmlSchemaFacet) {
                    XmlSchemaFacet facet = (XmlSchemaFacet) facetObject;
                    String facetPropertyName = NeutralSchemaType.lookupPropertyName(facet);
                    simpleSchema.getProperties().put(facetPropertyName, facet.getValue().toString());
                }
            }
            
            if (tokens.size() > 0) {
                
                // Token Schema
                simpleSchema.getProperties().put(TokenSchema.TOKENS, tokens);
            } else if ((simpleSchema.getProperties() != null) && (simpleSchema.getProperties().size() > 0)) {
                
                // Restricted Schema
                NeutralSchema restrictedSchema = this.getSchemaFactory().createSchema(
                        NeutralSchemaType.RESTRICTED.getName());
                restrictedSchema.setProperties(simpleSchema.getProperties());
                simpleSchema = restrictedSchema;
            }
        }
        
        if ((simpleSchema != null) && (simpleTypeName != null)) {
            simpleSchema.setType(simpleTypeName);
        }
        
        return simpleSchema;
    }
    
    private XmlSchemaSimpleType getSimpleBaseType(QName simpleBaseTypeName) {
        XmlSchemaSimpleType simpleBaseType = null;
        if (simpleBaseTypeName != null) {
            XmlSchemaType baseType = this.getXmlSchema().getTypeByName(simpleBaseTypeName);
            if (baseType != null) {
                if (baseType instanceof XmlSchemaSimpleType) {
                    simpleBaseType = (XmlSchemaSimpleType) baseType;
                } else {
                    throw new RuntimeException("Unsupported simple base type: "
                            + baseType.getClass().getCanonicalName());
                }
            } else {
                LOG.error("Schema simple base type not found: " + simpleBaseTypeName);
            }
        }
        return simpleBaseType;
    }
    
    private QName getSimpleContentTypeName(XmlSchemaSimpleType schemaSimpleType) {
        QName simpleContentTypeName = null;
        if (schemaSimpleType.getContent() != null
                && schemaSimpleType.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
            XmlSchemaSimpleTypeRestriction simpleContent = (XmlSchemaSimpleTypeRestriction) schemaSimpleType
                    .getContent();
            simpleContentTypeName = simpleContent.getBaseTypeName();
        } else {
            LOG.error("Unsupported simple content model: "
                    + schemaSimpleType.getContent().getClass().getCanonicalName());
        }
        return simpleContentTypeName;
    }
    
    private NeutralSchema parseComplexType(XmlSchemaComplexType schemaComplexType, NeutralSchema complexSchema) {
        if (schemaComplexType.getContentModel() != null && schemaComplexType.getContentModel().getContent() != null) {
            XmlSchemaContent content = schemaComplexType.getContentModel().getContent();
            if (content instanceof XmlSchemaComplexContentExtension) {
                XmlSchemaComplexContentExtension schemaComplexContent = (XmlSchemaComplexContentExtension) content;
                XmlSchemaComplexType complexBaseType = this.getComplexBaseType(schemaComplexContent);
                if (complexBaseType != null) {
                    complexSchema = this.parseComplexType(complexBaseType, complexSchema);
                }
                this.parseFields(schemaComplexContent, complexSchema);
                
            } else if (content instanceof XmlSchemaSimpleContentExtension) {
                QName baseTypeName = ((XmlSchemaSimpleContentExtension) content).getBaseTypeName();
                NeutralSchema simpleContentSchema = this.schemaFactory.createSchema(baseTypeName);
                complexSchema.getFields().put(complexSchema.getType(), simpleContentSchema);
                
                parseAttributes(((XmlSchemaSimpleContentExtension) content).getAttributes(), complexSchema);
            }
        }
        
        this.parseFields(schemaComplexType, complexSchema);
        
        return complexSchema;
    }
    
    private XmlSchemaComplexContentExtension getComplexContent(XmlSchemaComplexType schemaComplexType) {
        XmlSchemaComplexContentExtension schemaComplexContentExtension = null;
        if (schemaComplexType.getContentModel() != null && schemaComplexType.getContentModel().getContent() != null) {
            if (schemaComplexType.getContentModel().getContent() instanceof XmlSchemaComplexContentExtension) {
                schemaComplexContentExtension = (XmlSchemaComplexContentExtension) schemaComplexType.getContentModel()
                        .getContent();
            } else {
                LOG.error("Unsupported complex content model: "
                        + schemaComplexType.getContentModel().getContent().getClass().getCanonicalName());
            }
        }
        return schemaComplexContentExtension;
    }
    
    private XmlSchemaComplexType getComplexBaseType(XmlSchemaComplexContentExtension schemaComplexContent) {
        XmlSchemaComplexType complexBaseType = null;
        QName baseTypeName = schemaComplexContent.getBaseTypeName();
        XmlSchemaType baseType = this.getXmlSchema().getTypeByName(baseTypeName);
        if (baseType != null) {
            if (baseType instanceof XmlSchemaComplexType) {
                complexBaseType = (XmlSchemaComplexType) baseType;
            } else {
                throw new RuntimeException("Unsupported complex base type: " + baseType.getClass().getCanonicalName());
            }
        } else {
            LOG.error("Schema complex base type not found: " + baseTypeName);
        }
        return complexBaseType;
    }
    
    private void parseFields(XmlSchemaComplexType schemaComplexType, NeutralSchema complexSchema) {
        this.parseAttributes(schemaComplexType.getAttributes(), complexSchema);
        this.parseParticle(schemaComplexType.getParticle(), complexSchema);
    }
    
    private void parseFields(XmlSchemaComplexContentExtension schemaComplexContentExtension, NeutralSchema complexSchema) {
        this.parseAttributes(schemaComplexContentExtension.getAttributes(), complexSchema);
        this.parseParticle(schemaComplexContentExtension.getParticle(), complexSchema);
    }
    
    private void parseAttributes(XmlSchemaObjectCollection attributes, NeutralSchema complexSchema) {
        
        if (attributes != null) {
            for (int i = 0; i < attributes.getCount(); i++) {
                XmlSchemaAttribute attribute = (XmlSchemaAttribute) attributes.getItem(i);
                QName attributeTypeName = attribute.getSchemaTypeName();
                
                XmlSchemaType attributeSchemaType = attribute.getSchemaType();
                
                if (attribute.getName() != null) {
                    
                    String attributeName = attribute.getName();
                    
                    // Optional Attributes
                    if (!(attribute.getUse() != null && "required".equalsIgnoreCase(attribute.getUse().getValue()))) {
                        attributeName = "*" + attributeName;
                    }
                    
                    // Derive Attribute Schema
                    NeutralSchema attributeSchema = null;
                    if (attributeSchemaType != null) {
                        attributeSchema = this.parse(attributeSchemaType);
                    } else if (attributeTypeName != null) {
                        attributeSchema = this.getSchemaFactory().createSchema(attributeTypeName);
                    }
                    
                    // Update Neutral Schema Field
                    if (attributeSchema != null) {
                        complexSchema.getFields().put(attributeName, attributeSchema);
                    }
                }
            }
        }
    }
    
    private NeutralSchema parseElement(XmlSchemaElement element) {
        QName elementTypeName = element.getSchemaTypeName();
        
        XmlSchemaType elementSchemaType = element.getSchemaType();
        
        String elementName = element.getName();
        
        // Optional Elements
        if (element.isNillable() || (element.getMinOccurs() <= 0)) {
            elementName = "*" + elementName;
        }
        
        // Derive Element Schema
        NeutralSchema elementSchema = null;
        if (elementSchemaType != null) {
            if (elementSchemaType.getName() != null) {
                elementSchema = this.parse(elementSchemaType);
            } else {
                elementSchema = this.parse(elementSchemaType, elementName);
            }
        } else if (elementTypeName != null) {
            elementSchema = this.getSchemaFactory().createSchema(elementTypeName);
        }
        
        if (elementSchema != null) {
            
            // List Schema
            if (element.getMaxOccurs() > 1) {
                ListSchema listSchema = (ListSchema) this.getSchemaFactory().createSchema("list");
                listSchema.getList().add(elementSchema);
                elementSchema = listSchema;
            }
        }
        
        return elementSchema;
    }
    
    private void parseParticle(XmlSchemaParticle particle, NeutralSchema complexSchema) {
        
        if (particle != null) {
            if (particle instanceof XmlSchemaElement) {
                XmlSchemaElement element = (XmlSchemaElement) particle;
                
                NeutralSchema elementSchema = parseElement(element);
                
                // Update Neutral Schema Field
                complexSchema.getFields().put(element.getName(), elementSchema);
                
            } else if (particle instanceof XmlSchemaSequence) {
                XmlSchemaSequence schemaSequence = (XmlSchemaSequence) particle;
                for (int i = 0; i < schemaSequence.getItems().getCount(); i++) {
                    XmlSchemaObject item = schemaSequence.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        parseParticle((XmlSchemaParticle) item, complexSchema);
                    } else {
                        throw new RuntimeException("Unsupported XmlSchemaSequence item: "
                                + item.getClass().getCanonicalName());
                    }
                }
            } else if (particle instanceof XmlSchemaChoice) {
                LOG.error("Unhandled XmlSchemaChoice element: " + particle + " " + complexSchema.getType());
                
            } else {
                LOG.error("Unsupported XmlSchemaParticle item: " + particle.getClass().getCanonicalName());
            }
        }
    }
}
