/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.dashboard.manager.component;

import java.util.Collection;

import org.slc.sli.dashboard.entity.Config;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.ModelAndViewConfig;

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
