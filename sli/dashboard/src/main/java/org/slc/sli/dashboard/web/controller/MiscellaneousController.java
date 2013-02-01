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

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.slc.sli.dashboard.manager.PortalWSManager;
import org.slc.sli.dashboard.util.SecurityUtil;

/**
 * This controller handles miscellaneous services such as getting the header and footer, etc.
 *
 */
@Controller
public class MiscellaneousController {

    private PortalWSManager portalWSManager;

    @Autowired
    public void setPortalWSManager(PortalWSManager portalWSManager) {
        this.portalWSManager = portalWSManager;
    }

    @RequestMapping(value = "/s/m/header", method = RequestMethod.GET)
    @ResponseBody
    public String getHeader(HttpServletResponse response) {
        boolean isAdmin = SecurityUtil.isAdmin();
        String header = portalWSManager.getHeader(isAdmin);
        if (header != null) {
            header = header.replace("[$USER_NAME$]", SecurityUtil.getUsername());
        }
        response.setContentType("text/html");
        return header;
    }

    @RequestMapping(value = "/s/m/footer", method = RequestMethod.GET)
    @ResponseBody
    public String getFooter(HttpServletResponse response) {
        boolean isAdmin = SecurityUtil.isAdmin();
        response.setContentType("text/html");
        return portalWSManager.getFooter(isAdmin);
    }

}
