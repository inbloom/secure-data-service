package org.slc.sli.ingestion.referenceresolution;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * Class to resolve and create extended references from simple references within ingested XML files.
 *
 * @author tshewchuk
 *
 */
public class ExtendedReferenceResolver implements ReferenceResolutionStrategy {

    private class ConfigurationManager {
        Map<String, Object> getReferenceTemplate(String interchangeName, String callingEntityName, String referenceName) {
            return null;
        }
    }

    /**
     * Main method of the extended reference resolver.
     *
     * @param referenceName
     *            Simple reference to be expanded to an extended reference.
     * @param referenceId
     *            Simple reference ID to target entity.
     * @param enclosingEntityName
     *            Entity containing simple reference.
     * @param xmlFile
     *            Input XML file to be parsed.
     * @param interchangeName
     *            Interchange containing reference/target pair.
     *
     * @return String
     *         Extended reference XML text body, or null, if unresolved.
     *
     */
    @Override
    public String resolveReference(String referenceName, String referenceId, String enclosingEntityName, File xmlFile,
            String interchangeName) {
        // Create an extended reference to replace the simple reference.
        String referenceBody = null;
        String entityBody = null;
        InputStream input = null;
        XMLEventReader reader = null;
        ConfigurationManager configurationManager = new ConfigurationManager();
        try {
            // Iterate through the input XML file until we find the reference target entity.
            URL url = new URL(xmlFile.getPath());
            input = url.openStream();
            XMLInputFactory factory = XMLInputFactory.newInstance();
            reader = factory.createXMLEventReader(xmlFile.getPath(), input);
            while (reader.hasNext()) {
                XMLEvent event = reader.peek();
                if (event.isStartElement()) {
                    // Check if this is the target ID.
                    StartElement start = event.asStartElement();
                    Attribute id = start.getAttributeByName(new QName("id"));
                    if ((id != null) && (id.getValue() == referenceId)) {
                        // Get the target entity body and exit the while loop.
                        entityBody = reader.getElementText();
                        break;
                    }
                }
                reader.nextEvent();
            }

            // If the target was found, call the Configuration Manager to get the extended reference
            // template.
            if (entityBody != null) {
                // Get the extended reference template from the Configuration Manager.
                Map<String, Object> template = configurationManager.getReferenceTemplate(interchangeName,
                        enclosingEntityName, referenceName);

                // Construct the extended reference body from the template.
                referenceBody = convert(entityBody, template);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                input.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Return the extended reference body, or null if not resolved.
        return referenceBody;

    }

    private String convert(String entityBody, Map<String, Object> template) {
        // TODO Implement actual conversion.
        return entityBody;
    }

}
