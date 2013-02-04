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


package org.slc.sli.sample.transform;

import junit.framework.TestCase;

public class CCSEnglishCSV2XMLTransformerTest extends TestCase {
    public void testGradeLevelMapper() {
        CCSEnglishCSV2XMLTransformer.EnglishGradeLevelMapper englishGradeLevelMapper = new CCSEnglishCSV2XMLTransformer.EnglishGradeLevelMapper();
        assertEquals(9, englishGradeLevelMapper.getGradeLevel("9-10.blah"));
        assertEquals(0, englishGradeLevelMapper.getGradeLevel("K.something"));
    }
}
