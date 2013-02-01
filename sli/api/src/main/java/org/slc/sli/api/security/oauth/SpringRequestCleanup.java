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
package org.slc.sli.api.security.oauth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slc.sli.api.jersey.PostProcessFilter;
import org.slc.sli.api.resources.security.SamlFederationResource;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Requests handled by Spring's {@link RequestMapping} don't get processed through
 * the {@link PostProcessFilter}, so anything that needs to get cleaned up on a
 * per-request basis for the Spring endpoints can be handled here.
 * 
 * Such endpoints are implemented in {@link AuthController} and {@link SamlFederationResource}.
 */
@Component
public class SpringRequestCleanup extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        TenantContext.cleanup();
    }

}
