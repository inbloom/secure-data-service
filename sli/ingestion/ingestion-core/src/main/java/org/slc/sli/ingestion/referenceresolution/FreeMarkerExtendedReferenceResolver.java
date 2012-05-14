package org.slc.sli.ingestion.referenceresolution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.milyn.Smooks;
import org.milyn.cdr.SmooksResourceConfiguration;
import org.milyn.templating.TemplatingConfiguration;
import org.milyn.templating.freemarker.FreeMarkerTemplateProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.xml.idref.IdRefResolutionHandler;

/**
 *
 * @author tke
 *
 */

public class FreeMarkerExtendedReferenceResolver implements ReferenceResolutionStrategy {
    public static final Logger LOG = LoggerFactory.getLogger(IdRefResolutionHandler.class);

    private static final String DOCUMENT = "#document";

    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    private final SmooksExtendedReferenceResolver smooksResolver = new SmooksExtendedReferenceResolver();

    private Map<String, String> idRefConfigs;

    public FreeMarkerExtendedReferenceResolver() {
        smooksResolver.setIdRefConfigs(new HashMap<String, Smooks>());
    }

    /**
     * resolve the reference
     *
     * @param xPath
     *            XPath-like element identifier
     * @param content
     *            the content of the referenced element in XML format
     * @return Resolved content in XML format. Null if the reference is not supported yet.
     */
    @Override
    public File resolve(String xPath, File content) {
        if (!reassureSmooksResolver(xPath)) {
            return null;
        }

        return smooksResolver.resolve(xPath, content);
    }

    private boolean reassureSmooksResolver(String xPath) {
        String ftlFile = idRefConfigs.get(xPath);

        if (ftlFile == null) {
            return false;
        }

        Smooks smooks = null;

        rwl.readLock().lock();
        try {
            smooks = smooksResolver.getIdRefConfigs().get(xPath);
        } finally {
            rwl.readLock().unlock();
        }

        if (smooks == null) {
            smooks = new Smooks();

            smooks.addConfiguration(new SmooksResourceConfiguration(DOCUMENT, "org.milyn.delivery.DomModelCreator"));

            FreeMarkerTemplateProcessor visitor = new FreeMarkerTemplateProcessor(new TemplatingConfiguration(ftlFile));

            smooks.addVisitor(visitor, DOCUMENT);

            rwl.writeLock().lock();
            try {
                if (!smooksResolver.getIdRefConfigs().containsKey(xPath)) {
                    smooksResolver.getIdRefConfigs().put(xPath, smooks);
                }
            } finally {
                rwl.writeLock().unlock();
            }

        }

        return true;
    }

    public Map<String, String> getIdRefConfigs() {
        return idRefConfigs;
    }

    public void setIdRefConfigs(Map<String, String> idRefConfigs) {
        this.idRefConfigs = idRefConfigs;
    }
}
