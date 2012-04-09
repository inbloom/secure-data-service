package org.slc.sli.manager.component;

import java.util.Collection;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.ModelAndViewConfig;

/**
 * Factory responsible for assembly of the required data for each component according to 
 * the component's configuration.
 * @author agrebneva
 *
 */
public interface CustomizationAssemblyFactory {
    
    /**
     * Get required data and display metadata for a component
     * @param componentId
     * @param params
     * @return
     */
    ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey);

    Collection<Config> getWidgetConfigs();
}
