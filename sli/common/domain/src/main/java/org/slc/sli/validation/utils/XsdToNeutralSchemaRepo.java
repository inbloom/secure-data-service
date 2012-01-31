package org.slc.sli.validation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import org.slc.sli.validation.NeutralSchemaType;
import org.slc.sli.validation.SchemaFactory;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.ListSchema;
import org.slc.sli.validation.schema.NeutralSchema;
import org.slc.sli.validation.schema.TokenSchema;

/**
 * Generation tool used to convert XSD to SLI Neutral Schema.
 * This class leverages the prior art/work by Ryan Farris to convert XSD to Avro style schemas.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Component
public class XsdToNeutralSchemaRepo implements SchemaRepository {
    
    // Logging
    private static final Log LOG = LogFactory.getLog(XsdToNeutralSchemaRepo.class);
    
    // Constants
    public static final String DEFAULT_INPUT_XSD_PATH = "xsd";
    public static final String XSD = "xsd";
    private static final String PII_ELEMENT_NAME = "PersonallyIdentifiableInfo";
    private static final String SLI_XSD_NAMESPACE = "http://slc-sli/ed-org/0.1";
    
    // Attributes
    private final String xsdPath;
    private final SchemaFactory schemaFactory;
    
    private Map<String, NeutralSchema> schemas = new HashMap<String, NeutralSchema>();
    
    @Autowired
    public XsdToNeutralSchemaRepo(@Value("classpath:sliXsd") String xsdPath, SchemaFactory schemaFactory)
            throws IOException {
        this.xsdPath = xsdPath;
        this.schemaFactory = schemaFactory;
        generateSchemas();
    }
    
    @Override
    public NeutralSchema getSchema(String type) {
        return schemas.get(type);
    }
    
    private String getXsdPath() {
        return xsdPath;
    }
    
    private SchemaFactory getSchemaFactory() {
        return schemaFactory;
    }
    
    private void generateSchemas() throws IOException {
        
        LOG.info("Starting XSD -> NeutralSchema Generator...");
        LOG.info("Using XML Schema Directory Path: " + getXsdPath());
        
        // Scan XML Schemas on path
        List<XmlSchema> xmlSchemas = parseXmlSchemas(getXsdPath(), XSD);
        
        // Iterate XML Schemas
        for (XmlSchema schema : xmlSchemas) {
            loadSchema(schema);
        }
        
        LOG.info("Statistics:");
        LOG.info("Xml Total Schema Files Parsed: " + xmlSchemas.size());
        LOG.info("Xml Total Schema Count: " + schemas.size());
        
        LOG.info("Finished.");
    }
    
    void loadSchema(XmlSchema schema) {
        XmlSchemaObjectCollection schemaItems = schema.getItems();
        
        // Iterate XML Schema items
        for (int i = 0; i < schemaItems.getCount(); i++) {
            XmlSchemaObject schemaObject = schemaItems.getItem(i);
            
            NeutralSchema neutralSchema;
            if (schemaObject instanceof XmlSchemaType) {
                neutralSchema = parse((XmlSchemaType) schemaObject, schema);
            } else if (schemaObject instanceof XmlSchemaElement) {
                neutralSchema = parseElement((XmlSchemaElement) schemaObject, schema);
            } else {
                continue;
            }
            schemas.put(neutralSchema.getType(), neutralSchema);
        }
    }
    
    private List<XmlSchema> parseXmlSchemas(String xsdPath, String schemaRepresentation) {
        
        List<XmlSchema> xmlSchemas = new ArrayList<XmlSchema>();
        
        try {
            URL schemaResourcesUrl = ResourceUtils.getURL(xsdPath);
            String protocol = schemaResourcesUrl.getProtocol();
            
            // Process XML schema files found on the file system
            if (protocol.equals("file")) {
                File schemaResourcesDir = FileUtils.toFile(schemaResourcesUrl);
                List<File> schemaFiles = new ArrayList<File>(FileUtils.listFiles(schemaResourcesDir,
                        new String[] { schemaRepresentation }, true));
                for (File schemaFile : schemaFiles) {
                    
                    // Parse XML schema file
                    String schemaResourcePath = xsdPath + (xsdPath.endsWith("/") ? "" : "/") + schemaFile.getName();
                    
                    LOG.info("Parsing Xml Schema: " + schemaResourcePath);
                    
                    XmlSchema schema = parseXmlSchema(xsdPath, new FileInputStream(schemaResourcePath));
                    
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
    
    XmlSchema parseXmlSchema(final String xsdPath, final InputStream schema) {
        Reader reader = null;
        try {
            reader = new InputStreamReader(schema);
            XmlSchemaCollection schemaCollection = new XmlSchemaCollection();
            schemaCollection.setSchemaResolver(new URIResolver() {
                @Override
                public InputSource resolveEntity(String targetNamespace, String schemaLocation, String baseUri) {
                    return new InputSource(xsdPath + "/" + schemaLocation);
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
    
    private NeutralSchema parse(XmlSchemaType type, XmlSchema schema) {
        return parse(type, type.getName(), schema);
    }
    
    private NeutralSchema parse(XmlSchemaType type, String name, XmlSchema schema) {
        if (type instanceof XmlSchemaComplexType) {
            NeutralSchema complexSchema = getSchemaFactory().createSchema(name);
            return parseComplexType((XmlSchemaComplexType) type, complexSchema, schema);
        } else if (type instanceof XmlSchemaSimpleType) {
            return parseSimpleType((XmlSchemaSimpleType) type, schema, name);
        } else {
            throw new RuntimeException("Unsupported schema type: " + type.getClass().getCanonicalName());
        }
    }
    
    private NeutralSchema parseSimpleType(XmlSchemaSimpleType schemaSimpleType, XmlSchema schema, String name) {
        NeutralSchema simpleSchema = null;
        
        String simpleTypeName = schemaSimpleType.getName();
        
        if (NeutralSchemaType.isPrimitive(schemaSimpleType.getQName())) {
            simpleSchema = getSchemaFactory().createSchema(schemaSimpleType.getQName());
        } else if (NeutralSchemaType.exists(schemaSimpleType.getBaseSchemaTypeName())) {
            if (NeutralSchemaType.isPrimitive(schemaSimpleType.getBaseSchemaTypeName())) {
                simpleSchema = getSchemaFactory().createSchema(schemaSimpleType.getBaseSchemaTypeName());
            } else {
                XmlSchemaSimpleType simpleBaseType = getSimpleBaseType(schemaSimpleType.getBaseSchemaTypeName(), schema);
                if (simpleBaseType != null) {
                    if (simpleTypeName == null) {
                        simpleTypeName = simpleBaseType.getName();
                    }
                    simpleSchema = getSchemaFactory().createSchema(simpleTypeName);
                }
            }
        } else if (schemaSimpleType.getContent() != null
                && schemaSimpleType.getContent() instanceof XmlSchemaSimpleTypeList) {
            ListSchema listSchema = (ListSchema) getSchemaFactory().createSchema("list");
            
            XmlSchemaSimpleTypeList content = (XmlSchemaSimpleTypeList) schemaSimpleType.getContent();
            NeutralSchema listContentSchema = null;
            if (content.getItemType() != null) {
                listContentSchema = parseSimpleType(content.getItemType(), schema, null);
            } else {
                QName itemTypeName = content.getItemTypeName();
                listContentSchema = getSchemaFactory().createSchema(itemTypeName.getLocalPart());
            }
            listSchema.getList().add(listContentSchema);
            return listSchema;
            
        } else if (getSimpleContentTypeName(schemaSimpleType) != null) {
            if (NeutralSchemaType.isPrimitive(getSimpleContentTypeName(schemaSimpleType))) {
                simpleSchema = getSchemaFactory().createSchema(getSimpleContentTypeName(schemaSimpleType));
            } else {
                XmlSchemaSimpleType simpleBaseType = getSimpleBaseType(getSimpleContentTypeName(schemaSimpleType),
                        schema);
                if (simpleBaseType != null) {
                    if (simpleTypeName == null) {
                        simpleTypeName = simpleBaseType.getName();
                    }
                    simpleSchema = getSchemaFactory().createSchema(simpleTypeName);
                }
            }
        }
        
        if (simpleSchema != null && schemaSimpleType.getContent() != null) {
            
            if (schemaSimpleType.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
                
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
                }
            }
        }
        
        // See if this element contains personally identifiable information (PII)
        if (simpleSchema != null && schemaSimpleType.getAnnotation() != null) {
            XmlSchemaObjectCollection annotations = schemaSimpleType.getAnnotation().getItems();
            
            // The PII flag is set in AppInfo, which is contained within an Annotation element.
            // There may be multiple annotations on this object, so iterate over them.
            for (int annotationIdx = 0; annotationIdx < annotations.getCount(); ++annotationIdx) {
                XmlSchemaObject annotation = annotations.getItem(annotationIdx);
                
                if (annotation instanceof XmlSchemaAppInfo) {
                    XmlSchemaAppInfo info = (XmlSchemaAppInfo) annotation;
                    
                    NodeList appInfoNodes = info.getMarkup();
                    for (int appInfoNodeIdx = 0; annotationIdx < appInfoNodes.getLength(); ++appInfoNodeIdx) {
                        
                        if (appInfoNodes.item(appInfoNodeIdx) instanceof Element) {
                            
                            Element e = (Element) appInfoNodes.item(appInfoNodeIdx);
                            NodeList sli = e.getElementsByTagNameNS(SLI_XSD_NAMESPACE, PII_ELEMENT_NAME);
                            if (sli.getLength() > 1) {
                                
                                // Note: multiple PII annotations on the same type should fail
                                // XSD validation so we don't check for this situation here. If
                                // there are duplicate PII elements, the last one parsed wins.
                                String piiValue = sli.item(0).getNodeValue();
                                simpleSchema.isPersonallyIdentifiableInfo(Boolean.parseBoolean(piiValue));
                            }
                        }
                    }
                }
            }
        }
        
        if ((simpleSchema != null) && (simpleTypeName != null)) {
            simpleSchema.setType(simpleTypeName);
        } else if (simpleSchema != null && simpleTypeName == null && name != null
                && simpleSchema.getProperties().size() > 0) {
            /*
             * If we hit this conditional block, it means we need to create a new NeutralSchema to
             * represent this XML element that is defined in-line.
             */
            simpleSchema.setType(name);
        }
        
        return simpleSchema;
    }
    
    private XmlSchemaSimpleType getSimpleBaseType(QName simpleBaseTypeName, XmlSchema schema) {
        XmlSchemaSimpleType simpleBaseType = null;
        if (simpleBaseTypeName != null) {
            XmlSchemaType baseType = schema.getTypeByName(simpleBaseTypeName);
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
    
    private NeutralSchema parseComplexType(XmlSchemaComplexType schemaComplexType, NeutralSchema complexSchema,
            XmlSchema schema) {
        if (schemaComplexType.getContentModel() != null && schemaComplexType.getContentModel().getContent() != null) {
            XmlSchemaContent content = schemaComplexType.getContentModel().getContent();
            if (content instanceof XmlSchemaComplexContentExtension) {
                XmlSchemaComplexContentExtension schemaComplexContent = (XmlSchemaComplexContentExtension) content;
                XmlSchemaComplexType complexBaseType = getComplexBaseType(schemaComplexContent, schema);
                if (complexBaseType != null) {
                    complexSchema = parseComplexType(complexBaseType, complexSchema, schema);
                }
                this.parseFields(schemaComplexContent, complexSchema, schema);
                
            } else if (content instanceof XmlSchemaSimpleContentExtension) {
                QName baseTypeName = ((XmlSchemaSimpleContentExtension) content).getBaseTypeName();
                NeutralSchema simpleContentSchema = schemaFactory.createSchema(baseTypeName);
                complexSchema.getFields().put(complexSchema.getType(), simpleContentSchema);
                
                parseAttributes(((XmlSchemaSimpleContentExtension) content).getAttributes(), complexSchema, schema);
            }
        }
        
        this.parseFields(schemaComplexType, complexSchema, schema);
        
        return complexSchema;
    }
    
    private XmlSchemaComplexType getComplexBaseType(XmlSchemaComplexContentExtension schemaComplexContent,
            XmlSchema schema) {
        XmlSchemaComplexType complexBaseType = null;
        QName baseTypeName = schemaComplexContent.getBaseTypeName();
        XmlSchemaType baseType = schema.getTypeByName(baseTypeName);
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
    
    private void parseFields(XmlSchemaComplexType schemaComplexType, NeutralSchema complexSchema, XmlSchema schema) {
        parseAttributes(schemaComplexType.getAttributes(), complexSchema, schema);
        parseParticle(schemaComplexType.getParticle(), complexSchema, schema);
    }
    
    private void parseFields(XmlSchemaComplexContentExtension schemaComplexContentExtension,
            NeutralSchema complexSchema, XmlSchema schema) {
        parseAttributes(schemaComplexContentExtension.getAttributes(), complexSchema, schema);
        parseParticle(schemaComplexContentExtension.getParticle(), complexSchema, schema);
    }
    
    private void parseAttributes(XmlSchemaObjectCollection attributes, NeutralSchema complexSchema, XmlSchema schema) {
        
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
                        attributeSchema = parse(attributeSchemaType, schema);
                    } else if (attributeTypeName != null) {
                        attributeSchema = getSchemaFactory().createSchema(attributeTypeName);
                    }
                    
                    // Update Neutral Schema Field
                    if (attributeSchema != null) {
                        complexSchema.getFields().put(attributeName, attributeSchema);
                    }
                }
            }
        }
    }
    
    private NeutralSchema parseElement(XmlSchemaElement element, XmlSchema schema) {
        QName elementTypeName = element.getSchemaTypeName();
        
        XmlSchemaType elementSchemaType = element.getSchemaType();
        
        // Derive Element Schema
        NeutralSchema elementSchema = null;
        if (elementSchemaType != null) {
            if (elementSchemaType.getName() != null) {
                elementSchema = this.parse(elementSchemaType, schema);
            } else {
                elementSchema = this.parse(elementSchemaType, element.getName(), schema);
            }
        } else if (elementTypeName != null) {
            elementSchema = getSchemaFactory().createSchema(elementTypeName);
        }
        
        if (elementSchema != null) {
            
            // List Schema
            if (element.getMaxOccurs() > 1) {
                ListSchema listSchema = (ListSchema) getSchemaFactory().createSchema("list");
                listSchema.getList().add(elementSchema);
                elementSchema = listSchema;
            }
        }
        
        return elementSchema;
    }
    
    private void parseParticle(XmlSchemaParticle particle, NeutralSchema complexSchema, XmlSchema schema) {
        
        if (particle != null) {
            if (particle instanceof XmlSchemaElement) {
                XmlSchemaElement element = (XmlSchemaElement) particle;
                
                NeutralSchema elementSchema = parseElement(element, schema);
                
                String elementName = element.getName();
                
                // Optional Elements
                if (element.isNillable() || (element.getMinOccurs() <= 0)) {
                    elementName = "*" + elementName;
                }
                
                // Update Neutral Schema Field
                complexSchema.getFields().put(elementName, elementSchema);
                
            } else if (particle instanceof XmlSchemaSequence) {
                XmlSchemaSequence schemaSequence = (XmlSchemaSequence) particle;
                for (int i = 0; i < schemaSequence.getItems().getCount(); i++) {
                    XmlSchemaObject item = schemaSequence.getItems().getItem(i);
                    if (item instanceof XmlSchemaParticle) {
                        parseParticle((XmlSchemaParticle) item, complexSchema, schema);
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
