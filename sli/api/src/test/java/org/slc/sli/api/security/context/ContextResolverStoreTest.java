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


package org.slc.sli.api.security.context;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.resolver.AllowAllEntityContextResolver;
import org.slc.sli.api.security.context.resolver.DenyAllContextResolver;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;

/**
 * Tests for ContextResolver
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ContextResolverStoreTest {

    @Autowired
    private ContextResolverStore contextResolverStore;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    /**
     * Ensures that there's only one resolver to resolve certain entities.
     */
    @Test
    public void findAmbiguousResolvers() throws Exception {
        List<String> entityNames = new ArrayList<String>();
        Field[] fields = EntityNames.class.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.getType() == String.class && Modifier.isStatic(field.getModifiers())) {
                entityNames.add((String) field.get(null));
            }
        }
        
        Collection<EntityContextResolver> resolvers =  applicationContext.getBeansOfType(EntityContextResolver.class).values();
        for (String entityName1 : Arrays.asList(EntityNames.STAFF, EntityNames.TEACHER)) {
            
            for (String entityName2 : entityNames) {
                List<EntityContextResolver> resolving = new ArrayList<EntityContextResolver>();
                for (EntityContextResolver resolver : resolvers) {
                    if (resolver.canResolve(entityName1, entityName2)) {
                        resolving.add(resolver);
                    }
                }
                Assert.assertTrue("Multiple resolvers resolve " + entityName1 + " to " + entityName2 + ": " + resolving, resolving.size() <= 1);
            }
            
        }
    }

    @Test
    public void testTeacherTeacherResolver() {
        EntityContextResolver resolver = this.contextResolverStore.findResolver("teacher", "teacher");

        Assert.assertTrue(resolver.getClass() != AllowAllEntityContextResolver.class);
    }

    @Test
    public void testTeacherStudentResolver() {
        EntityContextResolver resolver = this.contextResolverStore.findResolver("teacher", "student");

        Assert.assertTrue(resolver.getClass() != AllowAllEntityContextResolver.class);
    }

    @Test
    public void testTeacherSchoolResolver() {
        EntityContextResolver resolver = this.contextResolverStore.findResolver("teacher", "school");

        Assert.assertTrue(resolver.getClass() != AllowAllEntityContextResolver.class);
    }

    @Test
    public void testTeacherSectionResolver() {
        EntityContextResolver resolver = this.contextResolverStore.findResolver("teacher", "section");

        Assert.assertTrue(resolver.getClass() != AllowAllEntityContextResolver.class);
    }

    @Test
    public void testTeacherSessionResolver() {
        EntityContextResolver resolver = this.contextResolverStore.findResolver("teacher", "session");

        Assert.assertTrue(resolver.getClass() != AllowAllEntityContextResolver.class);
    }

    @Test
    public void testTeacherAttendanceResolver() {
        EntityContextResolver resolver = this.contextResolverStore.findResolver("teacher", "attendance");

        Assert.assertTrue(resolver.getClass() != AllowAllEntityContextResolver.class);
    }


    @Test
    public void testNotFoundResolver() {
        EntityContextResolver resolver = this.contextResolverStore.findResolver("hobbit", "mordor");

        Assert.assertTrue(resolver.getClass() == DenyAllContextResolver.class);
    }

}
