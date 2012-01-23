package org.slc.sli.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * 
 * SLI Schema Factory which creates Schema instances based upon Ed-Fi type.
 * File persistence methods are also provided.
 * 
 * @author Robert Bloh <rbloh@wgen.net>
 * 
 */
@Component
public class NeutralSchemaFactory {
    
    // Logging
    private static final Log LOG = LogFactory.getLog(NeutralSchemaFactory.class);
    
    // Constants
    public static final String JSON = "json";
    public static final String XML = "xml";
    
    // Jackson Mapper
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
    // Static Methods
    public NeutralSchema createSchema(QName qName) {
        return createSchema(qName.getLocalPart());
    }
    
    public NeutralSchema createSchema(String xsd) {
        NeutralSchemaType schemaType = NeutralSchemaType.findByName(xsd);
        if (schemaType != null) {
            switch (schemaType) {
            case BOOLEAN:
                return new BooleanSchema(schemaType.getName());
            case INT:
                return new IntegerSchema(schemaType.getName());
            case INTEGER:
                return new IntegerSchema(schemaType.getName());
            case LONG:
                return new LongSchema(schemaType.getName());
            case FLOAT:
                return new FloatSchema(schemaType.getName());
            case DOUBLE:
                return new DoubleSchema(schemaType.getName());
            case DECIMAL:
                return new DecimalSchema(schemaType.getName());
            case DATE:
                return new DateSchema(schemaType.getName());
            case TIME:
                return new TimeSchema(schemaType.getName());
            case DATETIME:
                return new DateTimeSchema(schemaType.getName());
            case DURATION:
                return new DurationSchema(schemaType.getName());
            case STRING:
                return new StringSchema(schemaType.getName());
            case ID:
                return new StringSchema(schemaType.getName());
            case IDREF:
                return new StringSchema(schemaType.getName());
            case RESTRICTED:
                return new RestrictedSchema(schemaType.getName());
            case TOKEN:
                return new TokenSchema(schemaType.getName());
            case LIST:
                return new ListSchema(schemaType.getName());
            case COMPLEX:
                return new ComplexSchema(schemaType.getName());
            default:
                return null;
            }
        } else {
            return new ComplexSchema(xsd);
        }
    }
    
    public NeutralSchema createSpecificSchema(NeutralSchema neutralSchema) {
        NeutralSchema specificSchema = createSchema(neutralSchema.getType());
        
        specificSchema.setVersion(neutralSchema.getVersion());
        
        specificSchema.setProperties(neutralSchema.getProperties());
        
        for (String fieldName : neutralSchema.getFields().keySet()) {
            Object fieldValue = neutralSchema.getFields().get(fieldName);
            specificSchema.getFields().put(fieldName, fieldValue);
        }
        
        specificSchema.setDocProperties(neutralSchema.getDocProperties());
        
        specificSchema.setCustomProperties(neutralSchema.getCustomProperties());
        
        for (String fieldName : neutralSchema.getCustomFields().keySet()) {
            Object fieldValue = neutralSchema.getCustomFields().get(fieldName);
            specificSchema.getCustomFields().put(fieldName, fieldValue);
        }
        
        specificSchema.setReadConverter(neutralSchema.getReadConverter());
        
        specificSchema.setWriteConverter(neutralSchema.getWriteConverter());
        
        return specificSchema;
    }
    
    public void toFile(String directoryPath, String fileName, List<NeutralSchema> schemaList, String representation,
            boolean overwrite) {
        StringBuffer contents = new StringBuffer();
        
        if (representation.equalsIgnoreCase(XML)) {
            String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
            contents.append(xmlHeader);
            contents.append("<xmlSchema>" + "\n");
        }
        
        for (NeutralSchema schema : schemaList) {
            if (representation.equalsIgnoreCase(JSON)) {
                contents.append(schema.toJson());
            } else if (representation.equalsIgnoreCase(XML)) {
                contents.append(schema.toXml(true));
            }
        }
        
        if (representation.equalsIgnoreCase(XML)) {
            contents.append("</xmlSchema>" + "\n");
        }
        
        // Create combined Schema of type file
        toFile(directoryPath, fileName, contents.toString(), overwrite);
    }
    
    public void toFile(String directoryPath, NeutralSchema schema, String representation, boolean overwrite) {
        String fileName = schema.getType() + "." + representation;
        String fileContents = "";
        if (representation.equalsIgnoreCase(JSON)) {
            fileContents += schema.toJson();
        } else if (representation.equalsIgnoreCase(XML)) {
            String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
            fileContents += xmlHeader;
            fileContents += "<xmlSchema>" + "\n";
            fileContents += schema.toXml(true);
            fileContents += "</xmlSchema>" + "\n";
        }
        toFile(directoryPath, fileName, fileContents, overwrite);
    }
    
    public void toFile(String directoryPath, String fileName, String fileContents, boolean overwrite) {
        
        try {
            File directory = new File(directoryPath);
            directory.mkdirs();
            File file = new File(directory, fileName);
            boolean fileExists = !file.createNewFile();
            if (!fileExists || overwrite) {
                if (fileExists) {
                    LOG.warn("Replacing existing file: " + file.getAbsolutePath());
                }
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(fileContents);
                fileWriter.close();
            }
        } catch (IOException ioException) {
            LOG.error(ioException);
        }
    }
    
    public NeutralSchema fromFile(String directoryPath, String schemaType, String representation, Class schemaClass) {
        return fromFile(directoryPath, schemaType + "." + representation, schemaClass);
    }
    
    public NeutralSchema fromFile(String directoryPath, String fileName, Class schemaClass) {
        
        File directory = new File(directoryPath);
        File file = new File(directory, fileName);
        
        return fromFile(file, schemaClass);
    }
    
    public NeutralSchema fromFile(File file, Class schemaClass) {
        NeutralSchema neutralSchema = null;
        
        BufferedReader fileReader = null;
        try {
            fileReader = new BufferedReader(new FileReader(file));
            StringBuffer fileContents = new StringBuffer();
            String line;
            while ((line = fileReader.readLine()) != null) {
                fileContents.append(line);
            }
            neutralSchema = (NeutralSchema) MAPPER.readValue(fileContents.toString(), schemaClass);
        } catch (IOException ioException) {
            LOG.error(ioException);
        } catch (Exception exception) {
            LOG.error(exception);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException ioException) {
                    LOG.error(ioException);
                }
            }
        }
        
        return neutralSchema;
    }
    
    public NeutralSchema fromResource(String resourcePath, Class schemaClass) {
        NeutralSchema neutralSchema = null;
        
        BufferedReader bufferedReader = null;
        try {
            Reader reader = new InputStreamReader(NeutralSchemaFactory.class.getResourceAsStream(resourcePath));
            bufferedReader = new BufferedReader(reader);
            StringBuffer fileContents = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileContents.append(line);
            }
            neutralSchema = (NeutralSchema) MAPPER.readValue(fileContents.toString(), schemaClass);
        } catch (IOException ioException) {
            LOG.error(ioException);
        } catch (Exception exception) {
            LOG.error(exception);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ioException) {
                    LOG.error(ioException);
                }
            }
        }
        
        return neutralSchema;
    }
    
    public NeutralSchema fromStringBuffer(StringBuffer schema, Class schemaClass) {
        NeutralSchema neutralSchema = null;
        
        try {
            neutralSchema = (NeutralSchema) MAPPER.readValue(schema.toString(), schemaClass);
        } catch (Exception exception) {
            LOG.error(exception);
        }
        
        return neutralSchema;
    }
    
}
