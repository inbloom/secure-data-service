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

package org.slc.sli.api.criteriaGenerator;

import com.sun.jersey.api.spring.Autowire;
import com.sun.jersey.spi.container.ContainerRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 1/22/13
 * Time: 1:50 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DateFilterCriteriaGeneratorTest {
    @InjectMocks
    private DateFilterCriteriaGenerator dateFilterCriteriaGenerator = new DateFilterCriteriaGenerator();

    @Mock
   private EntityIdentifier entityIdentifier;

    @Mock
    private SessionRangeCalculator sessionRangeCalculator;

    @Mock
    private GranularAccessFilterProvider granularAccessFilterProvider;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerate() throws Exception {
        ContainerRequest request = mock(ContainerRequest.class);
        MultivaluedMap<String,String> parameters = mock(MultivaluedMap.class);
        List<String> schoolYears = new ArrayList<String>();
        schoolYears.add("begin");
        schoolYears.add("end");
        SessionDateInfo sessionDateInfo = new SessionDateInfo("01-01-2010","01-31-2012",
                new HashSet<String>());
        EntityFilterInfo entityFilterInfo =  new EntityFilterInfo();
        entityFilterInfo.setEntityName("testEntity");
        entityFilterInfo.setBeginDateAttribute("beginDate");
        entityFilterInfo.setEndDateAttribute("endDate");

        Mockito.when(request.getQueryParameters()).thenReturn(parameters);
        Mockito.when(parameters.get(ParameterConstants.SCHOOL_YEARS)).thenReturn(schoolYears);
        Mockito.when(sessionRangeCalculator.findDateRange(anyString())).thenReturn(sessionDateInfo);
        Mockito.when(entityIdentifier.findEntity(anyString())).thenReturn(entityFilterInfo);

        dateFilterCriteriaGenerator.generate(request);

    }
}
