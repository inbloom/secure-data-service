package org.slc.sli.ingestion.smooks;

import java.util.Collections;
import java.util.List;

/**
 * Holds all configuration information to create an instance of Smooks for SLI ingestion
 *
 * @author dduran
 *
 */
public final class SliSmooksConfig {

    private final String configFileName;

    private final List<String> targetSelectors;

    public SliSmooksConfig(String configFileName, List<String> targetSelectors) {
        this.configFileName = configFileName;
        this.targetSelectors = targetSelectors;
    }

    public String getConfigFileName() {
        return configFileName;
    }

    public List<String> getTargetSelectors() {
        return Collections.unmodifiableList(targetSelectors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("configFileName: ");
        sb.append(this.configFileName);
        sb.append("; targetSelectors: [");
        for (String targetSelector : this.targetSelectors) {
            sb.append(targetSelector + ", ");
        }
        sb.append("]");
        return sb.toString();
    }

}
