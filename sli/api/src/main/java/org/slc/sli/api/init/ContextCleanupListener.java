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
