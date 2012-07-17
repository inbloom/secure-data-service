/*
* Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.sandbox.idp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;


@Controller
public class Change {

    /*
     * Form for force password change
     */
    @RequestMapping(value = "/forcePasswordChange", method = RequestMethod.GET)
    public ModelAndView forcePasswordChange(
            HttpServletRequest request) {
    	//place holder for force password change
    	ModelAndView mav = new ModelAndView("forcePasswordChange");
        return mav;
    }
    
    /*
     * PUT Method for change password
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    public ModelAndView changePassword(){
    	return new ModelAndView("put");
    }
}
