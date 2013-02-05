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
import javax.validation.Valid;

import org.slc.sli.dashboard.web.entity.SafeUUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Layout controller for all types of requests.
 * NOTE: This controller was introduced after the student list and app selector controllers.
 * 
 * @author dwu
 */
@Controller
public class LayoutController extends GenericLayoutController {
    private static final String TABBED_ONE_COL = "tabbed_one_col";
    private static final String HOME_ID = "home";
    
    /**
     * Generic layout component that takes component id and uid of the data entity
     * 
     * @param componentId
     * @param id
     * @param request
     * @return
     */
    // The path variable validation for id is simplified since spring doesn't seem to support exact
    // length regex
    @RequestMapping(value = { "/s/l/{componentId:[a-zA-Z0-9]+}/{id:[A-Za-z0-9-]+(?:_id)?}" }, method = RequestMethod.GET)
    public ModelAndView handleWithId(@PathVariable String componentId, @PathVariable SafeUUID id,
            HttpServletRequest request) {
        return getModelAndView(componentId, id, request);
    }
    
    /**
     * Generic layout handler
     * 
     * @param id
     * @param request
     * @return
     */
    @RequestMapping(value = { "/s/l/{componentId:[a-zA-Z0-9]+}" }, method = RequestMethod.GET)
    public ModelAndView handle(@PathVariable String componentId, @Valid SafeUUID id, HttpServletRequest request) {
        return getModelAndView(componentId, id, request);
    }
    
    /**
     * Handles the "/" url by redirecting to list of students
     * 
     * @param request
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView handleLos(HttpServletRequest request) {
        return getModelAndView(HOME_ID, null, request);
    }
    
    private ModelAndView getModelAndView(String componentId, SafeUUID id, HttpServletRequest request) {
        // if id is not null, override lazy for data
        return getModelView(
                TABBED_ONE_COL,
                getPopulatedModel(componentId, id == null ? null : id.getId(), request,
                        (id != null && id.getId() != null)));
    }
}
