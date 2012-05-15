package org.slc.sli.ingestion.referenceresolution;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
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


        File convertedContent = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        boolean failure = true;

        try {
            convertedContent = File.createTempFile("smooks", ".xml", content.getParentFile());

            in = new BufferedInputStream(new FileInputStream(content));
            out = new BufferedOutputStream(new FileOutputStream(convertedContent));

            StreamSource source = new StreamSource(in);
            StreamResult result = new StreamResult(out);

            smooks.filterSource(source, result);

            out.flush();

            // If the file is empty, the configuration could not use to resolve the input
            failure = (convertedContent.length() == 0);
        } catch (Exception e) {
            failure = true;
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);

            if (failure) {
                LOG.warn("Failed to resolve input with configuration :" + xPath);
                FileUtils.deleteQuietly(convertedContent);
                convertedContent = null;
            }
        }

        return convertedContent;
    }
}
