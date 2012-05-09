package org.slc.sli.ingestion.referenceresolution;

import java.util.Map;

import org.milyn.Smooks;
import org.milyn.payload.StringResult;
import org.milyn.payload.StringSource;

/**
 *
 * @author tke
 *
 */
public class SmooksExtendedReferenceResolver extends ExtendedReferenceResolver {
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
     * @param interchange : Name of interchnage
     * @param element  : name of element
     * @param reference : name of the reference
     * @param content : the content of the referenced element in XML format
     * @return : the resolved content in XML format
     */
    public String resolve(String interchange, String element, String reference, String content) {
        String key = interchange + element + reference;
        Smooks smooks = idRefConfigs.get(key);
        StringResult result = new StringResult();
        StringSource source = new StringSource(content);
        smooks.filterSource(source, result);

        return result.toString();
    }
}
