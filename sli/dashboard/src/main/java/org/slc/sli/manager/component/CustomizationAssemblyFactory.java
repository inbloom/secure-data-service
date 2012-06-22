package org.slc.sli.manager.component;

import java.util.Collection;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.GenericEntity;
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
     * @return view config
     */
    ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey);

    /**
     * Get data for the declared entity reference
     * @param componentId - component to get data for
     * @param entityKey - entity key for the component
     * @return entity
     */
    GenericEntity getDataComponent(String componentId, Object entityKey);

    /**
     * Get data for the declared entity reference overriding lazy
     * @param componentId - component to get data for
     * @param entityKey - entity key for the component
     * @param lazyOverride - override lazy?
     * @return view config
     */
    ModelAndViewConfig getModelAndViewConfig(String componentId, Object entityKey, boolean lazyOverride);

    /**
     * Get widget configs
     * @return collection of configs
     */
    Collection<Config> getWidgetConfigs();

}
