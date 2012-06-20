package org.slc.sli.ingestion.referenceresolution;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.milyn.Smooks;
import org.milyn.SmooksException;
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

    private static ThreadLocal<Map<String, Smooks>> threadLocalIdRefConfigs = new ThreadLocal<Map<String, Smooks>>() {
        @Override
        protected Map<String, Smooks> initialValue() {
            Map<String, Smooks> idRefConfigs = new HashMap<String, Smooks>();
            return idRefConfigs;
        }
    };

    /**
     * resolve the reference
     *
     * @param interchange
     *            : Name of interchange
     * @param element
     *            : name of element
     * @param reference
     *            : name of the reference
     * @param content
     *            : the content of the referenced element in XML format
     * @return : the resolved content in XML format. Null if the reference is not supported yet.
     */
    @Override
    public String resolve(String xPath, String content) {
        Smooks smooks = getIdRefConfigs().get(xPath);

        if (smooks == null) {
            return null;
        }

        String convertedContent = null;

        try {

            StreamSource source = new StreamSource(new StringReader(content));

            StringWriter stringWriter = new StringWriter();
            StreamResult result = new StreamResult(stringWriter);

            smooks.filterSource(source, result);

            convertedContent = stringWriter.toString();

        } catch (SmooksException se) {
            LOG.error("Exception filtering idref xml through smooks", se);
        }
        return convertedContent;
    }

    public Map<String, Smooks> getIdRefConfigs() {
        return threadLocalIdRefConfigs.get();
    }
}
