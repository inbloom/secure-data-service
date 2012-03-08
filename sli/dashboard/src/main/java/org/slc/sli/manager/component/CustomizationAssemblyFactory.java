package org.slc.sli.manager.component;

import org.slc.sli.entity.GenericEntity;

/**
 * Factory responsible for assembly of the required data for each component according to 
 * the component's configuration.
 * @author agrebneva
 *
 */
public interface CustomizationAssemblyFactory {
    /**
     * Get required customized component data by component id
     * @param componentId - a string id of the component
     * @param params - required parameters, such as entityId
     * @return required data for a defined component
     */
    GenericEntity getDataComponent(String componentId, Object params);
    
    /**
     * Get required display metadata for a component
     * @param componentId - a string id of the component
     * @param params - required parameters, such as entityId
     * @return display metadata for a defined component
     */
    GenericEntity getDislplayComponent(String componentId, Object params);
}
