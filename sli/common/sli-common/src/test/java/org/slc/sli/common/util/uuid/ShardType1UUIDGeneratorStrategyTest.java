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


package org.slc.sli.common.util.uuid;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author wscott
 */
public class ShardType1UUIDGeneratorStrategyTest {
    @Test
    public void TestShardType1UUIDGenerator() {
        ShardType1UUIDGeneratorStrategy uuidGen = new ShardType1UUIDGeneratorStrategy();
        String uuid = uuidGen.randomUUID();
        assertNotNull(uuid);
        assertEquals('1', uuid.charAt(22)); //make sure we generated a type1 uuid
        assertEquals(43, uuid.length()); // 7 chars for 'yyyyrr-', 36 chars for type 1 uuid
    }
}
