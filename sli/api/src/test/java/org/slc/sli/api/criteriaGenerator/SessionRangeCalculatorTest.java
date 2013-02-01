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

package org.slc.sli.api.criteriaGenerator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.QueryParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author: sashton
 */
public class SessionRangeCalculatorTest {

    @InjectMocks
    private SessionRangeCalculator calc = new SessionRangeCalculator();

    @Mock
    private EdOrgHelper mockEdOrgHelper;

    @Mock
    private PagingRepositoryDelegate<Entity> mockRepo;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        Set<String> edOrgIds = Collections.EMPTY_SET;
        Mockito.when(mockEdOrgHelper.getDirectEdorgs()).thenReturn(edOrgIds);
    }


    @Test
    public void shouldCatchInvalidDateRange() {

        List<String> invalidRanges = Arrays.asList("123-1234", "1234-123", "12345-1234", "1234-12345", "123A-1234",
                "1234-123A", "12341234", "1234--1234", "2001-2001", "2001-2000");

        for (String range : invalidRanges) {
            try {
                initRepo();
                calc.findDateRange(range);
                Assert.fail("Did not catch an invalid range: " + range);

            } catch (QueryParseException e) {
                // expected to get here
            }
        }

    }

    @Test
    public void shouldAllowValidDateRange() {

        initRepo();
        SessionDateInfo result = calc.findDateRange("2009-2010");

        Assert.assertEquals("Should match", "2006-08-14", result.getStartDate());
        Assert.assertEquals("Should match", "2009-05-22", result.getEndDate());
        Assert.assertEquals("Should match", 4, result.getSessionIds().size());
    }

    @Test
    public void shouldReturnEmptyRanges() {

        initRepo(Collections.EMPTY_LIST);
        SessionDateInfo result = calc.findDateRange("2009-2010");

        Assert.assertEquals("Should match", "", result.getStartDate());
        Assert.assertEquals("Should match", "", result.getEndDate());
        Assert.assertEquals("Should match", 0, result.getSessionIds().size());
    }

    private void initRepo() {

        List<Pair<String, String>> dates = new ArrayList<Pair<String, String>>();
        dates.add(new ImmutablePair<String, String>("2006-08-14", "2006-12-11"));
        dates.add(new ImmutablePair<String, String>("2007-01-11", "2007-05-21"));
        dates.add(new ImmutablePair<String, String>("2007-08-15", "2007-12-12"));
        dates.add(new ImmutablePair<String, String>("2009-01-12", "2009-05-22"));

        initRepo(dates);
    }


    private void initRepo(List<Pair<String, String>> sessionDates) {
        List<Entity> sessions = new ArrayList<Entity>();

        int id = 1000;

        for (Pair<String, String> pair : sessionDates) {
            String entityId = id + "";
            id += 1;
            Entity e = new MongoEntity("someEntity", entityId, new HashMap<String, Object>(), null);
            e.getBody().put("beginDate", pair.getLeft());
            e.getBody().put("endDate", pair.getRight());
            sessions.add(e);
        }

        Mockito.when(mockRepo.findAll(Mockito.eq(EntityNames.SESSION), Mockito.any(NeutralQuery.class))).thenReturn(sessions);
    }


}
