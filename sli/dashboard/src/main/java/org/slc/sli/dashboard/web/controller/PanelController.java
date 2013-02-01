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

package org.slc.sli.dashboard.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slc.sli.dashboard.entity.ModelAndViewConfig;
import org.slc.sli.dashboard.manager.component.CustomizationAssemblyFactory;
import org.slc.sli.dashboard.web.entity.SafeUUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Data controller returns JSON data. No freemarker templates are called.
 * 
 * @author dwu
 */
@Controller
@RequestMapping(value = { "/service/component", "/s/c" })
public class PanelController {
    
    private CustomizationAssemblyFactory customizationAssemblyFactory;
    
    /**
     * Controller for client side panel config & data pulls
     * s - is for service and c for component
     * 
     */
    @RequestMapping(value = "/{componentId:[a-zA-Z0-9]+}/{id:[A-Za-z0-9-]*(?:_id)?}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndViewConfig handle(@PathVariable final String componentId, @PathVariable final SafeUUID id,
            final HttpServletRequest request) {
        return customizationAssemblyFactory.getModelAndViewConfig(componentId, id.getId(), true);
    }
    
    /**
     * Controller for client side data pulls without id
     * 
     */
    @RequestMapping(value = "/{componentId:[a-zA-Z0-9]+}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndViewConfig handleWithoutId(@PathVariable final String componentId, final HttpServletRequest request) {
        return customizationAssemblyFactory.getModelAndViewConfig(componentId, null, false);
    }
    
    @Autowired
    public void setCustomizedDataFactory(CustomizationAssemblyFactory customizedDataFactory) {
        this.customizationAssemblyFactory = customizedDataFactory;
    }
}
