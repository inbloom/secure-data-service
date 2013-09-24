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

package org.slc.sli.bulk.extract.lea;

import java.util.*;

import com.google.common.collect.HashMultimap;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.data.mongodb.core.query.Query;

/*
 * The grading period extractor doesn't have that much logic.  It's pretty much entirely 
 * dependent on the cache that's built in the session extractor.  For that reason I choose 
 * to test both of those extractors in one test class.
 */
public class SessionAndGradingPeriodExtractorTest {
    @Mock
    private Repository<Entity> mockRepo;
    @Mock
    private ExtractFileMap mockMap;
    @Mock
    private EntityExtractor mockExtractor;
    @Mock
    private EntityToEdOrgCache mockCache;
    @Mock 
    private ExtractorHelper mockHelper;

    @Mock
    private EdOrgExtractHelper mockEdOrgExtractHelper;
    
    private SessionExtractor sessionExtractor;
    private GradingPeriodExtractor gradingPeriodExtractor;
    
    @Before
    public void setUp() {
    	MockitoAnnotations.initMocks(this);
    	sessionExtractor =  new SessionExtractor(mockExtractor, mockMap, mockRepo, mockHelper, new EntityToEdOrgCache(), mockEdOrgExtractHelper);
    	gradingPeriodExtractor = new GradingPeriodExtractor(mockExtractor, mockMap, mockRepo, mockEdOrgExtractHelper);
    }
    
    @Test
    public void testWriteOneSessionAndTwoGradingPeriods() {
    	//Setup
    	Entity mockSession = Mockito.mock(Entity.class);
    	Map<String, Object> mockBody = new HashMap<String, Object>();
    	mockBody.put("schoolId", "school-1");
    	List<String> gradingPeriods = Arrays.asList("gp-1", "gp-2");
    	mockBody.put("gradingPeriodReference", gradingPeriods);
    	
    	Entity mockGradingPeriod1 = Mockito.mock(Entity.class);
    	Mockito.when(mockGradingPeriod1.getEntityId()).thenReturn("gp-1");
    	Entity mockGradingPeriod2 = Mockito.mock(Entity.class);
    	Mockito.when(mockGradingPeriod2.getEntityId()).thenReturn("gp-2");

        HashMultimap<String, String> map = HashMultimap.create();
    	map.put("school-1", "lea-1");
        Map<String, Collection<String>> schoolToLeaMap = map.asMap();
    	Mockito.when(mockHelper.buildSubToParentEdOrgCache(Mockito.any(EntityToEdOrgCache.class))).thenReturn(schoolToLeaMap);
    	Mockito.when(mockSession.getBody()).thenReturn(mockBody);
    	Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.SESSION), Mockito.eq(new Query()))).
    		thenReturn(Arrays.asList(mockSession).iterator());
    	Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.GRADING_PERIOD), Mockito.eq(new Query()))).
    		thenReturn(Arrays.asList(mockGradingPeriod1, mockGradingPeriod2).iterator());

    	//Session extractor testing
    	sessionExtractor.extractEntities(mockCache);
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockSession), Mockito.any(ExtractFile.class),
                Mockito.eq(EntityNames.SESSION));
        Assert.assertEquals(new HashSet<String>(Arrays.asList("lea-1")), sessionExtractor.getEntityToLeaCache().getEntriesById("gp-1"));
        Assert.assertEquals(new HashSet<String>(Arrays.asList("lea-1")), sessionExtractor.getEntityToLeaCache().getEntriesById("gp-2"));

        //Grading Period extractor testing
        gradingPeriodExtractor.extractEntities(sessionExtractor.getEntityToLeaCache());
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockGradingPeriod1), Mockito.any(ExtractFile.class),
        		Mockito.eq(EntityNames.GRADING_PERIOD));
        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockGradingPeriod2), Mockito.any(ExtractFile.class),
        		Mockito.eq(EntityNames.GRADING_PERIOD));

    }
    
    
    @Test
    public void testWriteThreeSessionsAndFiveGradingPeriods() {
    	//Setup
    	Entity mockSession1 = Mockito.mock(Entity.class);
    	Entity mockSession2 = Mockito.mock(Entity.class);
    	Entity mockSession3 = Mockito.mock(Entity.class);

    	Map<String, Object> mockBody1 = new HashMap<String, Object>();
    	mockBody1.put("schoolId", "school-1");
    	mockBody1.put("gradingPeriodReference", Arrays.asList("gp-1", "gp-2"));
    	Mockito.when(mockSession1.getBody()).thenReturn(mockBody1);
    	
    	Map<String, Object> mockBody2 = new HashMap<String, Object>();
    	mockBody2.put("schoolId", "school-2");
    	mockBody2.put("gradingPeriodReference", Arrays.asList("gp-1", "gp-3"));
    	Mockito.when(mockSession2.getBody()).thenReturn(mockBody2);


    	Map<String, Object> mockBody3 = new HashMap<String, Object>();
    	mockBody3.put("schoolId", "school-3");
    	mockBody3.put("gradingPeriodReference", Arrays.asList("gp-4", "gp-5"));
    	Mockito.when(mockSession3.getBody()).thenReturn(mockBody3);
    	
    	Entity mockGradingPeriod1 = Mockito.mock(Entity.class);
    	Mockito.when(mockGradingPeriod1.getEntityId()).thenReturn("gp-1");
    	Entity mockGradingPeriod2 = Mockito.mock(Entity.class);
    	Mockito.when(mockGradingPeriod2.getEntityId()).thenReturn("gp-2");
    	Entity mockGradingPeriod3 = Mockito.mock(Entity.class);
    	Mockito.when(mockGradingPeriod3.getEntityId()).thenReturn("gp-3");
    	Entity mockGradingPeriod4 = Mockito.mock(Entity.class);
    	Mockito.when(mockGradingPeriod4.getEntityId()).thenReturn("gp-4");
    	Entity mockGradingPeriod5 = Mockito.mock(Entity.class);
    	Mockito.when(mockGradingPeriod5.getEntityId()).thenReturn("gp-5");

    	Map<String, String> schoolToLeaMap = new HashMap<String, String>();
    	schoolToLeaMap.put("school-1", "lea-1");
    	schoolToLeaMap.put("school-2", "lea-2");
    	schoolToLeaMap.put("school-3", "lea-3");
    	
//    	Mockito.when(mockHelper.buildSubToParentEdOrgCache(Mockito.any(EntityToLeaCache.class))).thenReturn(schoolToLeaMap);
//    	Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.SESSION), Mockito.any(Query.class))).
//    		thenReturn(Arrays.asList(mockSession1, mockSession2, mockSession3).iterator());
//    	Mockito.when(mockRepo.findEach(Mockito.eq(EntityNames.GRADING_PERIOD), Mockito.eq(new Query()))).
//    		thenReturn(Arrays.asList(mockGradingPeriod1, mockGradingPeriod2, mockGradingPeriod3, mockGradingPeriod4, mockGradingPeriod5).iterator());
//
//    	//Session extractor testing
//    	sessionExtractor.extractEntities(mockCache);
//        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockSession1), Mockito.any(ExtractFile.class),
//                Mockito.eq(EntityNames.SESSION));
//        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockSession2), Mockito.any(ExtractFile.class),
//                Mockito.eq(EntityNames.SESSION));
//        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockSession3), Mockito.any(ExtractFile.class),
//                Mockito.eq(EntityNames.SESSION));
//
//        Assert.assertEquals(new HashSet<String>(Arrays.asList("lea-1", "lea-2")), sessionExtractor.getEntityToLeaCache().getEntriesById("gp-1"));
//        Assert.assertEquals(new HashSet<String>(Arrays.asList("lea-1")), sessionExtractor.getEntityToLeaCache().getEntriesById("gp-2"));
//        Assert.assertEquals(new HashSet<String>(Arrays.asList("lea-2")), sessionExtractor.getEntityToLeaCache().getEntriesById("gp-3"));
//        Assert.assertEquals(new HashSet<String>(Arrays.asList("lea-3")), sessionExtractor.getEntityToLeaCache().getEntriesById("gp-4"));
//        Assert.assertEquals(new HashSet<String>(Arrays.asList("lea-3")), sessionExtractor.getEntityToLeaCache().getEntriesById("gp-5"));
//
//        //Grading period extractor testing
//        gradingPeriodExtractor.extractEntities(sessionExtractor.getEntityToLeaCache());
//        Mockito.verify(mockExtractor, Mockito.times(2)).extractEntity(Mockito.eq(mockGradingPeriod1), Mockito.any(ExtractFile.class),
//        		Mockito.eq(EntityNames.GRADING_PERIOD));
//        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockGradingPeriod2), Mockito.any(ExtractFile.class),
//        		Mockito.eq(EntityNames.GRADING_PERIOD));
//        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockGradingPeriod3), Mockito.any(ExtractFile.class),
//        		Mockito.eq(EntityNames.GRADING_PERIOD));
//        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockGradingPeriod4), Mockito.any(ExtractFile.class),
//        		Mockito.eq(EntityNames.GRADING_PERIOD));
//        Mockito.verify(mockExtractor).extractEntity(Mockito.eq(mockGradingPeriod5), Mockito.any(ExtractFile.class),
//        		Mockito.eq(EntityNames.GRADING_PERIOD));
    }

}
