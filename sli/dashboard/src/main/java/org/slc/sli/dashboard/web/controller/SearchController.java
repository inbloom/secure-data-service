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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.slc.sli.dashboard.web.entity.StudentSearch;

/**
 * Layout controller for search.
 * NOTE: This controller was introduced after the student list and app selector controllers.
 *
 * @author dwu
 */
@Controller
@RequestMapping(value = { "/s/l" })
public class SearchController extends LayoutController {
    private static final String SEARCH_RESULTS = "search_results";

    /**
     * Controller for student search
    */
    @RequestMapping(value = "/studentSearch", method = RequestMethod.GET)
    public ModelAndView handle(
            @Valid StudentSearch studentSearch,
            HttpServletRequest request) {
        return getModelView(SEARCH_RESULTS, getPopulatedModel("studentSearch", studentSearch.get(), request));
    }
}
