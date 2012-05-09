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
public class SmooksExtendedReferenceResolver implements ReferenceResolutionStrategy {
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
    public String resolve(String interchange, String element, String reference, String content) {
        String key = interchange + element + reference;
        if (!idRefConfigs.containsKey(key)) {
            return null;
        }
        Smooks smooks = idRefConfigs.get(key);
        StringResult result = new StringResult();
        StringSource source = new StringSource(content);
        smooks.filterSource(source, result);

        return result.toString();
    }
}
