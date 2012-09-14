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

package org.slc.sli.modeling.jgen;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * JUnit for JavaGenConfigBuilder
 * @author chung
 */
public class JavaGenConfigBuilderTest {

    @Test
    public void testCreateObject() {
        @SuppressWarnings("unused")
        JavaGenConfigBuilder javaConfigBuilder = new JavaGenConfigBuilder();
    }

    @Test
    public void testBuild() {
        JavaGenConfigBuilder javaConfigBuilder = new JavaGenConfigBuilder();
        javaConfigBuilder.useDataTypeBase(true);
        JavaGenConfig javaGenConfig = javaConfigBuilder.build();
        assertTrue(javaGenConfig.useDataTypeBase());
    }

}
