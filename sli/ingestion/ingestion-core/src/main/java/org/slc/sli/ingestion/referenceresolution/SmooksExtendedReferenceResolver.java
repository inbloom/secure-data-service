package org.slc.sli.ingestion.referenceresolution;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.xml.idref.IdRefResolutionHandler;

/**
 *
 * @author tke
 *
 */

public class SmooksExtendedReferenceResolver implements ReferenceResolutionStrategy {
    public static final Logger LOG = LoggerFactory.getLogger(IdRefResolutionHandler.class);

    private Map<String, Smooks> idRefConfigs;

    public Map<String, Smooks> getIdRefConfigs() {
        return idRefConfigs;
    }

    public void setIdRefConfigs(Map<String, Smooks> idRefConfigs) {
        this.idRefConfigs = idRefConfigs;
    }

    /**
     * resolve the reference
     *
     * @param interchange : Name of interchange
     * @param element  : name of element
     * @param reference : name of the reference
     * @param content : the content of the referenced element in XML format
     * @return : the resolved content in XML format. Null if the reference is not supported yet.
     */
    @Override
    public File resolve(String xPath, File content) {
        Smooks smooks = idRefConfigs.get(xPath);

        if (smooks == null) {
            return null;
        }

        File convertedContent;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            StreamSource source = new StreamSource(new BufferedInputStream(new FileInputStream(content)));

            convertedContent = File.createTempFile("smooks", ".xml", content.getParentFile());

            StreamResult result = new StreamResult(new BufferedOutputStream(new FileOutputStream(convertedContent)));

            smooks.filterSource(source, result);
        } catch (IOException e) {
            convertedContent = null;
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }


        return convertedContent;
    }
}
