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

import org.junit.Test;

import java.util.Calendar;

import static junit.framework.Assert.assertEquals;

/**
 * Unit Tests
 *
 */
public class AssociativeContextHelperTest {

    private AssociativeContextHelper helper = new AssociativeContextHelper(); //class under test

    @Test
    public void testGetFilterDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 4, 3);

        assertEquals("Should match", "2012-02-03", helper.getFilterDate("90", calendar));
        calendar.clear();
        calendar.set(2012, 4, 3);
        assertEquals("Should match", "2012-05-03", helper.getFilterDate("", calendar));
        calendar.clear();
        calendar.set(2012, 1, 3);
        assertEquals("Should match", "2012-02-03", helper.getFilterDate(null, calendar));
    }
}
