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

package org.slc.sli.shtick.samples;

import org.junit.Test;
import org.slc.sli.shtick.Level3Client;
import org.slc.sli.shtick.StandardLevel3Client;
import org.slc.sli.shtick.StatusCodeException;
import org.slc.sli.shtick.pojo.Section;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.slc.sli.shtick.TestingConstants.BASE_URL;
import static org.slc.sli.shtick.TestingConstants.ROGERS_TOKEN;

/**
 * @author jstokes
 */
public class IdTest {
    private final Level3Client client = new StandardLevel3Client(BASE_URL);
    private static final Map<String, Object> EMPTY_QUERY_ARGS = Collections.emptyMap();

    @Test
    public void testSection() throws IOException, StatusCodeException {
        final List<Section> testSection = client.getSectionsById(ROGERS_TOKEN,
                Arrays.asList("01777045-266c-4e64-8f71-7bcb27b89e5c"), EMPTY_QUERY_ARGS);

        assertEquals(1, testSection.size());
        final Section section = testSection.get(0);

        assertNotNull(section.toMap().get("schoolId"));
        assertEquals("a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb", section.toMap().get("schoolId"));

        assertNotNull(section.getSchoolId());
        assertEquals("a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb", section.getSchoolId());

        assertNotNull(section.getProgramReference());
        assertEquals(1, section.getProgramReference().size());
    }
}
