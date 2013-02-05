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

package org.slc.sli.api.translator;

import com.sun.jersey.spi.container.ContainerRequest;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** 
* URITranslator Tester. 
* 
* @author <Authors name> 
* @since <pre>Nov 13, 2012</pre> 
* @version 1.0 
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class URITranslatorTest {

    private URITranslator translator;

    private PagingRepositoryDelegate<Entity> repository;
    private Entity learningObjective;
    private ContainerRequest request;
    private URI uri;

    @Before
    public void before() throws Exception {
    
        repository = mock(PagingRepositoryDelegate.class);
    
        translator = new URITranslator();
        translator.setRepository(repository);
    } 
    
    /** 
    * 
    * Method: translate(String resource) 
    * 
    */ 
    @Test
    public void testTranslate() throws Exception {
        List<Entity> learningObjectiveList = new ArrayList<Entity>();
        Entity loEntity = mock(Entity.class);
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("parentLearningObjective", "456");
        when(loEntity.getEntityId()).thenReturn("123");
        when(loEntity.getBody()).thenReturn(body);
        learningObjectiveList.add(loEntity);
        when(repository.findAll(anyString(), any(NeutralQuery.class))).thenReturn(learningObjectiveList);
        String newPath = translator.getTranslator("parentLearningObjective").translate("v1/learningObjectives/123/parentLearningObjectives");
        assertTrue("Should match new uri", "learningObjectives/456".equals(newPath));
    
    } 


} 
