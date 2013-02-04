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
package org.slc.sli.api.init;

import org.slc.sli.common.util.scope.CustomThreadScope;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * This is a {@link ApplicationListener} that gives us the opportunity to
 * clean up anything that's hanging around when the app is being restarted or shutdown.
 * 
 * The biggest thing this helps with are some ThreadLocals belonging to the main
 * web container thread that otherwise would never be cleaned up.
 * 
 * For example, anything that does a mongo query during app startup causes a
 * TenantContext to be created with ThreadLocals on the main thread.
 *
 */
@Component
public class ContextCleanupListener implements ApplicationListener<ContextRefreshedEvent>  {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        TenantContext.cleanup();
        CustomThreadScope.cleanup();
    }



}
