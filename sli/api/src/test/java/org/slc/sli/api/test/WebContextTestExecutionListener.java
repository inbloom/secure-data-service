package org.slc.sli.api.test;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Provides a test execution listener for fetching request scoped beans from the
 * application context.
 * 
 * @author smelody
 * 
 */

public class WebContextTestExecutionListener extends AbstractTestExecutionListener {
    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        
        if (testContext.getApplicationContext() instanceof GenericApplicationContext) {
            GenericApplicationContext context = (GenericApplicationContext) testContext.getApplicationContext();
            ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
            Scope requestScope = new SimpleThreadScope();
            beanFactory.registerScope("request", requestScope);
            Scope sessionScope = new SimpleThreadScope();
            beanFactory.registerScope("session", sessionScope);
        }
    }
}
